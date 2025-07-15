package team8.comp47360_team8_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import team8.comp47360_team8_backend.dto.POIBusynessDistanceRecommendationDTO;
import team8.comp47360_team8_backend.dto.RecommendationInputDTO;
import team8.comp47360_team8_backend.exception.POITypeNotFoundException;
import team8.comp47360_team8_backend.model.POI;
import team8.comp47360_team8_backend.model.POIType;
import team8.comp47360_team8_backend.model.UserPlan;
import team8.comp47360_team8_backend.repository.POITypeRepository;
import team8.comp47360_team8_backend.service.POIService;
import team8.comp47360_team8_backend.service.ZoneService;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @Author : Ze Li
 * @Date : 02/06/2025 22:06
 * @Version : V1.0
 * @Description :
 */

@Service
public class POIServiceImpl implements POIService {
    @Autowired
    private POITypeRepository poiTypeRepository;
    @Autowired
    private ZoneService zoneService;

    // Adjust busyness levels to numeric values
    public static final Map<String, Integer> BUSYNESS_MAP = Map.of(
            "low", 10,
            "medium", 5,
            "high", 1
    );
    public static final double CLOSEST_DISTANCE_SCORE = 10;

    private class TimeGap {
        private int startIndex;
        private int endIndex;
        private long minutes;

        public TimeGap(int startIndex, int endIndex, long minutes) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.minutes = minutes;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public long getMinutes() {
            return minutes;
        }
    }

    @Override
    public Set<POI> getPOIsByPOITypeName(String poiTypeName) {
        POIType poiType = poiTypeRepository.getByPoiTypeName(poiTypeName).orElseThrow(() -> new POITypeNotFoundException(poiTypeName));
        return poiType.getPOIs();
    }

    @Override
    public List<POIBusynessDistanceRecommendationDTO> assignBusynessDistanceForPOIs(String poiTypeName, POI lastPOI, HashMap<Long, String> zoneBusynessMap, String transitType, int limit) {
        Set<POI> pois = getPOIsByPOITypeName(poiTypeName);
        PriorityQueue<POIBusynessDistanceRecommendationDTO> poiBusynessDistanceRecommendationDTOHeap =
                new PriorityQueue<>(limit, Comparator.comparingDouble(POIBusynessDistanceRecommendationDTO::getRecommendation));

        // distance = 1, math.exp(-1/2)=0.61
        final double distanceScoreDecayFactor;
        if (transitType == null) {
            distanceScoreDecayFactor = 2;
        } else if (transitType.equals("cycle") || transitType.equals("bus")) {
            distanceScoreDecayFactor = 4;
        } else if (transitType.equals("car")) {
            distanceScoreDecayFactor = 8;
        } else {
            // walk as default
            distanceScoreDecayFactor = 2;
        }

        for (POI poi : pois) {
            String busyness = zoneBusynessMap.get(poi.getZone().getZoneId());
            double distance = calculateDistance(lastPOI, poi);
            POIBusynessDistanceRecommendationDTO poiBusynessDistanceRecommendationDTO = new POIBusynessDistanceRecommendationDTO(poi, busyness, distance, calculateRecommendation(busyness, distance, distanceScoreDecayFactor));
            poiBusynessDistanceRecommendationDTOHeap.add(poiBusynessDistanceRecommendationDTO);
            if (poiBusynessDistanceRecommendationDTOHeap.size() > limit) {
                // remove the lowest recommendation
                poiBusynessDistanceRecommendationDTOHeap.poll();
            }
        }

        List<POIBusynessDistanceRecommendationDTO> result = new ArrayList<>(limit);
        while (!poiBusynessDistanceRecommendationDTOHeap.isEmpty()) {
            result.add(poiBusynessDistanceRecommendationDTOHeap.poll());
        }
        Collections.reverse(result);
        return result;
    }

    private boolean isFixedPoi(RecommendationInputDTO recommendationInputDTO) {
        return recommendationInputDTO.getPoiName() != null && recommendationInputDTO.getZoneId() != null
                && recommendationInputDTO.getLatitude() != null && recommendationInputDTO.getLongitude() != null;
    }

    int getIndexByBinarySearch(List<TimeGap> gaps, TimeGap newGap, Comparator<TimeGap> gapComparator) {
        int index = Collections.binarySearch(gaps, newGap, gapComparator);
        if (index < 0) {
            index = -index - 1;
        }
        return index;
    }

    @Override
    public List<UserPlan> getListOfRecommendations(List<RecommendationInputDTO> recommendationInputDTOS) {
        if (recommendationInputDTOS.size() <= 1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No recommendation input");
        if (!isFixedPoi(recommendationInputDTOS.get(0)) || recommendationInputDTOS.get(0).getTime() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start location is not valid");

        final int minutesPerKilometer;
        String transitType = recommendationInputDTOS.get(0).getTransitType();
        if (transitType == null) {
            minutesPerKilometer = 12;
        } else if (transitType.equals("cycle") || transitType.equals("bus")) {
            minutesPerKilometer = 6;
        } else if (transitType.equals("car")) {
            minutesPerKilometer = 3;
        } else {
            // walk as default
            minutesPerKilometer = 12;
        }

        List<RecommendationInputDTO> fixedPoiFixedTime = new ArrayList<>();
        List<RecommendationInputDTO> fixedPoiOnly      = new ArrayList<>();
        List<RecommendationInputDTO> fixedTimeOnly     = new ArrayList<>();
        List<RecommendationInputDTO> fullyFlexible     = new ArrayList<>();

        // Classify the entries
        for (RecommendationInputDTO e : recommendationInputDTOS) {
            boolean hasPoi = isFixedPoi(e);
            boolean hasTime = e.getTime() != null;
            if (e.getStayMinutes() == null) e.setStayMinutes(15);

            if (hasPoi && hasTime) fixedPoiFixedTime.add(e);
            else if (hasPoi) fixedPoiOnly.add(e);
            else if (e.getPoiTypeName() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "POI type name is not valid for unfixed POI");
            else if (hasTime) fixedTimeOnly.add(e);
            else fullyFlexible.add(e);
        }
        ArrayList<UserPlan> userPlans = new ArrayList<>(recommendationInputDTOS.size());

        // Build your anchors list as DTOs, not PlanEntry
        List<RecommendationInputDTO> anchors = new ArrayList<>();
        anchors.addAll(fixedPoiFixedTime);
        anchors.addAll(fixedTimeOnly);

        // Sort by the DTO’s time
        anchors.sort(Comparator.comparing(RecommendationInputDTO::getTime));

        // Handle ending location
        ZonedDateTime lastAnchorTime = anchors.get(anchors.size() - 1).getTime();
        ZonedDateTime endTime = lastAnchorTime.withHour(21).withMinute(0).withSecond(0).withNano(0);
        if (lastAnchorTime.isAfter(endTime)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End location time is not valid, only support before 21:00");
        // add default ending time if not specified
        if (recommendationInputDTOS.get(recommendationInputDTOS.size() - 1).getTime() == null) {
            anchors.add(new RecommendationInputDTO(null, null, null, null, endTime, null, null, null));
        }

        // compute busyness for fixed POIs with fixed time
        Map<RecommendationInputDTO, String> busynessForAnchor = new HashMap<>();
        for (RecommendationInputDTO anchor : anchors) {
            if (isFixedPoi(anchor)) {
                String busy = zoneService
                        .predictZoneBusyness(
                                Collections.singletonList(anchor.getTime()),
                                anchor.getZoneId())
                        .get(0);
                busynessForAnchor.put(anchor, busy);
            }
        }

        // build gaps (using departure)
        List<TimeGap> gaps = new ArrayList<>();
        for (int i = 1; i < anchors.size(); i++) {
            RecommendationInputDTO prev = anchors.get(i - 1);
            RecommendationInputDTO next = anchors.get(i);
            ZonedDateTime departPrev = prev.getTime().plusMinutes(prev.getStayMinutes());
            long gapMin = Duration.between(departPrev, next.getTime()).toMinutes();
            gaps.add(new TimeGap(i-1, i, gapMin));
        }

        // compute busyness for fixed Time only POIs
        for (int i = 1; i < anchors.size() - 1; i++) {
            RecommendationInputDTO anchor = anchors.get(i);
            // only fill slots that need recommendation
            if (!isFixedPoi(anchor)) {
                // neighbors
                RecommendationInputDTO prev = anchors.get(i - 1);
                RecommendationInputDTO next = anchors.get(i + 1);

                // decide which anchor to use as context (lastPOI)
                POI lastPOIEntity = new POI();
                if (!isFixedPoi(next) || gaps.get(i - 1).getMinutes() < gaps.get(i).getMinutes()) {
                    lastPOIEntity.setLatitude(prev.getLatitude());
                    lastPOIEntity.setLongitude(prev.getLongitude());
                } else {
                    lastPOIEntity.setLatitude(next.getLatitude());
                    lastPOIEntity.setLongitude(next.getLongitude());
                }

                // 1) pick up the busyness map for this anchor’s time
                HashMap<Long,String> zoneBusynessMap
                        = zoneService.predictZoneBusyness(anchor.getTime());

                // use the single top result to fill the anchor
                POIBusynessDistanceRecommendationDTO best =
                        assignBusynessDistanceForPOIs(
                                anchor.getPoiTypeName(),
                                lastPOIEntity,
                                zoneBusynessMap,
                                transitType,    // transitType
                                1        // limit
                        ).get(0);

                POI chosen = best.getPoi();
                anchor.setPoiName(chosen.getPoiName());
                anchor.setLatitude(chosen.getLatitude());
                anchor.setLongitude(chosen.getLongitude());
                anchor.setZoneId(chosen.getZone().getZoneId());
                busynessForAnchor.put(anchor, best.getBusyness());
            }
        }

        // end location has fixed time but unfixed location
        RecommendationInputDTO end = anchors.get(anchors.size() - 1);
        if (!isFixedPoi(end) && end.getPoiTypeName() != null) {
            RecommendationInputDTO secondEnd = anchors.get(anchors.size() - 2);
            HashMap<Long,String> zoneBusynessMap
                    = zoneService.predictZoneBusyness(end.getTime());
            // use the single top result to fill the anchor
            POIBusynessDistanceRecommendationDTO best =
                    assignBusynessDistanceForPOIs(
                            end.getPoiTypeName(),
                            new POI(secondEnd.getLatitude(), secondEnd.getLongitude()),
                            zoneBusynessMap,
                            transitType,    // transitType
                            1        // limit
                    ).get(0);
            end.setPoiName(best.getPoi().getPoiName());
            end.setLatitude(best.getPoi().getLatitude());
            end.setLongitude(best.getPoi().getLongitude());
            end.setZoneId(best.getPoi().getZone().getZoneId());
            busynessForAnchor.put(end, best.getBusyness());
        }

        // sort gaps
        Comparator<TimeGap> gapComparator = (a, b) ->
                Long.compare(b.getMinutes(), a.getMinutes());
        gaps.sort(gapComparator);  // largest first
        // 2) insert each fixed-POI-only into the biggest feasible gap
        for (RecommendationInputDTO poiOnly : fixedPoiOnly) {
            boolean placed = false;
            // compute a POI stub for distance calculations
            POI thisPoi = new POI(poiOnly.getLatitude(), poiOnly.getLongitude());

            // already sorted gaps
            for (int i = 0; i < gaps.size(); i++) {
                TimeGap gap = gaps.get(i);
                int startIndex = gap.getStartIndex();
                int endIndex = gap.getEndIndex(); // the index of the end anchor of the gap
                RecommendationInputDTO prev = anchors.get(startIndex);
                RecommendationInputDTO next = anchors.get(endIndex);

                // 2a) distance‐based travel times
                double distPrevKm = calculateDistance(
                        new POI(prev.getLatitude(), prev.getLongitude()),
                        thisPoi
                );
                // next anchor could be added fake POI with 21:00, so distance set to 0
                double distNextKm = !isFixedPoi(next) ? 0 : calculateDistance(
                        thisPoi,
                        new POI(next.getLatitude(), next.getLongitude())
                );
                double preTravelTime = distPrevKm * minutesPerKilometer;
                double travelMin = preTravelTime + distNextKm * minutesPerKilometer + poiOnly.getStayMinutes();

                // 2b) can we fit?
                if (travelMin <= gap.minutes) {
                    ZonedDateTime mid = prev.getTime().plusMinutes((long) (prev.getStayMinutes() + preTravelTime + (gap.getMinutes() - travelMin) / 2));
                    poiOnly.setTime(mid);

                    // 3) compute its busyness and store it
                    String busy = zoneService
                            .predictZoneBusyness(
                                    Collections.singletonList(poiOnly.getTime()),
                                    poiOnly.getZoneId()
                            )
                            .get(0);
                    busynessForAnchor.put(poiOnly, busy);

                    anchors.add(poiOnly);
                    gaps.remove(i);
                    TimeGap newGap = new TimeGap(startIndex, anchors.size()-1, Duration.between(prev.getTime().plusMinutes(prev.getStayMinutes()), poiOnly.getTime()).toMinutes());
                    int index = getIndexByBinarySearch(gaps, newGap, gapComparator);
                    gaps.add(index, newGap);

                    TimeGap newGap2 = new TimeGap(anchors.size()-1, endIndex, Duration.between(poiOnly.getTime().plusMinutes(poiOnly.getStayMinutes()), next.getTime()).toMinutes());
                    index = getIndexByBinarySearch(gaps, newGap2, gapComparator);
                    gaps.add(index, newGap2);
                    placed = true;
                    break;
                }
            }
            // 2c) fallback: if none of the gaps could fit it, stick it in the very largest gap at its midpoint
            if (!placed) {
                // the largest gap is at index 0
                TimeGap lg = gaps.get(0);
                int startIndex = lg.getStartIndex();
                int endIndex = lg.getEndIndex();

                RecommendationInputDTO prev = anchors.get(startIndex);

                // midpoint between departPrev and arriveNext
                ZonedDateTime mid = prev.getTime().plusMinutes(prev.getStayMinutes()).plusMinutes(lg.getMinutes() / 2);
                // assign that fallback time
                poiOnly.setTime(mid);
                // compute its busyness too
                String busy = zoneService
                        .predictZoneBusyness(
                                Collections.singletonList(poiOnly.getTime()),
                                poiOnly.getZoneId()
                        )
                        .get(0);
                busynessForAnchor.put(poiOnly, busy);

                // finally insert it into your anchors list
                anchors.add(poiOnly);
                gaps.remove(0);
                TimeGap newGap = new TimeGap(startIndex, anchors.size()-1, 0);
                int index = getIndexByBinarySearch(gaps, newGap, gapComparator);
                gaps.add(index, newGap);

                TimeGap newGap2 = new TimeGap(anchors.size()-1, endIndex, 0);
                index = getIndexByBinarySearch(gaps, newGap2, gapComparator);
                gaps.add(index, newGap2);
            }
        }

        // 3) place each fully-flexible entry
        for (RecommendationInputDTO flex : fullyFlexible) {
            boolean placed = false;

            // try each of the two largest gaps
            for (int i = 0; i < 2; i++) {
                TimeGap lg = gaps.get(i);
                int startIndex = lg.getStartIndex();
                int endIndex = lg.getEndIndex();
                RecommendationInputDTO prev = anchors.get(startIndex);
                RecommendationInputDTO next = anchors.get(endIndex);

                // build context POI from prev
                POI lastPOI = new POI(prev.getLatitude(), prev.getLongitude());

                // get a big candidate list
                ZonedDateTime mid = prev.getTime().plusMinutes(prev.getStayMinutes()).plusMinutes(lg.getMinutes() / 2);
                HashMap<Long,String> zoneMap =
                        zoneService.predictZoneBusyness(mid);
                List<POIBusynessDistanceRecommendationDTO> candidates =
                        assignBusynessDistanceForPOIs(
                                flex.getPoiTypeName(),
                                lastPOI,
                                zoneMap,
                                transitType,
                                100
                        );

                // total gap in minutes
                long gapMin = lg.getMinutes();
                double halfGapMin = gapMin / 2.0;
                // next anchor could be added fake POI with 21:00, so distance set to 0
                POI nextPOI = !isFixedPoi(next) ? null : new POI(next.getLatitude(), next.getLongitude());
                // scan candidates until one fits
                for (var cand : candidates) {
                    POI cPoi = cand.getPoi();
                    // travel minutes A→Z and Z→B
                    double tAZ = calculateDistance(lastPOI, cPoi) * minutesPerKilometer;
                    double tZB = nextPOI == null ? 0 : calculateDistance(cPoi, nextPOI) * minutesPerKilometer;

                    // include the stay at Z
                    int stayMin = flex.getStayMinutes();
                    double requiredMin = tAZ + stayMin + tZB;

                    // must fit in the gap
                    if (requiredMin <= gapMin) {
                        mid = prev.getTime().plusMinutes((long) (prev.getStayMinutes() + tAZ + (gapMin-requiredMin)/2));
                        // now fill flex
                        flex.setPoiName(cPoi.getPoiName());
                        flex.setLatitude(cPoi.getLatitude());
                        flex.setLongitude(cPoi.getLongitude());
                        flex.setZoneId(cPoi.getZone().getZoneId());
                        flex.setTime(mid);
                        busynessForAnchor.put(flex, cand.getBusyness());

                        // insert and mark placed
                        anchors.add(flex);
                        gaps.remove(i);
                        TimeGap newGap = new TimeGap(startIndex, anchors.size()-1, Duration.between(prev.getTime().plusMinutes(prev.getStayMinutes()), flex.getTime()).toMinutes());
                        int index = getIndexByBinarySearch(gaps, newGap, gapComparator);
                        gaps.add(index, newGap);

                        TimeGap newGap2 = new TimeGap(anchors.size()-1, endIndex, Duration.between(flex.getTime().plusMinutes(flex.getStayMinutes()), next.getTime()).toMinutes());
                        index = getIndexByBinarySearch(gaps, newGap2, gapComparator);
                        gaps.add(index, newGap2);
                        placed = true;
                        break;
                    }
                }
                if (placed) break;
            }
            // if still not placed, fall back to midpoint of largest gap
            if (!placed) {
                TimeGap lg = gaps.get(0);
                int startIndex = lg.getStartIndex();
                int endIndex = lg.getEndIndex();
                RecommendationInputDTO prev = anchors.get(startIndex);

                ZonedDateTime mid = prev.getTime().plusMinutes(prev.getStayMinutes() + (lg.getMinutes()) / 2);

                // pick top-1 candidate now
                POI lastPOI = new POI(prev.getLatitude(), prev.getLongitude());
                var best = assignBusynessDistanceForPOIs(
                        flex.getPoiTypeName(),
                        lastPOI,
                        zoneService.predictZoneBusyness(mid),
                        transitType,
                        1
                ).get(0);
                POI cPoi = best.getPoi();
                flex.setPoiName(cPoi.getPoiName());
                flex.setLatitude(cPoi.getLatitude());
                flex.setLongitude(cPoi.getLongitude());
                flex.setZoneId(cPoi.getZone().getZoneId());
                flex.setTime(mid);
                busynessForAnchor.put(flex, best.getBusyness());

                anchors.add(flex);
                gaps.remove(0);
                TimeGap newGap = new TimeGap(startIndex, anchors.size()-1, 0);
                int index = getIndexByBinarySearch(gaps, newGap, gapComparator);
                gaps.add(index, newGap);

                TimeGap newGap2 = new TimeGap(anchors.size()-1, endIndex, 0);
                index = getIndexByBinarySearch(gaps, newGap2, gapComparator);
                gaps.add(index, newGap2);
            }
        }

        anchors.sort(Comparator.comparing(RecommendationInputDTO::getTime));
        for (RecommendationInputDTO dto : anchors) {
            if (!isFixedPoi(dto)) continue;
            UserPlan plan = new UserPlan();
            plan.setPoiName(dto.getPoiName());
            plan.setTime(dto.getTime());
            plan.setBusyness(busynessForAnchor.get(dto));
            plan.setLatitude(dto.getLatitude());
            plan.setLongitude(dto.getLongitude());
            userPlans.add(plan);
        }
        return userPlans;
    }


    public double calculateRecommendation(String busyness, double distance, double distanceScoreDecayFactor) {
        double busynessScore = BUSYNESS_MAP.get(busyness);
        // Exponential decay formula for distanceScore
        double distanceScore = CLOSEST_DISTANCE_SCORE * Math.exp(-distance / distanceScoreDecayFactor);
        return busynessScore/2 + distanceScore/2;
    }

    public double calculateDistance(POI poi1, POI poi2) {
        // Radius of the Earth in kilometers
        final double R = 6371.0;
        double lat1 = Math.toRadians(poi1.getLatitude());
        double lon1 = Math.toRadians(poi1.getLongitude());
        double lat2 = Math.toRadians(poi2.getLatitude());
        double lon2 = Math.toRadians(poi2.getLongitude());
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}