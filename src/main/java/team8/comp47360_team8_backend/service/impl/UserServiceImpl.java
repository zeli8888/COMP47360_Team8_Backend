package team8.comp47360_team8_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import team8.comp47360_team8_backend.exception.EmailAlreadyExistException;
import team8.comp47360_team8_backend.exception.UserAlreadyExistException;
import team8.comp47360_team8_backend.model.User;
import team8.comp47360_team8_backend.repository.UserRepository;
import team8.comp47360_team8_backend.security.CustomUserDetails;
import team8.comp47360_team8_backend.service.UserService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author : Ze Li
 * @Date : 17/06/2025 18:02
 * @Version : V1.0
 * @Description :
 */
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${user.picture.size}")
    private int userPictureSize;

    private static final String[] ALLOWED_CONTENT_TYPES = {"image/jpeg", "image/png", "image/gif"};

    @Override
    public UserDetails loadUserByUsername(String userName) {
        if (userName == null) throw new UsernameNotFoundException("Please offer user name or email!");
        Optional<User> opt = userRepository.findByUserName(userName);

        if (!opt.isPresent()) {
            opt = userRepository.findByEmail(userName);
        }

        if(!opt.isPresent())
            throw new UsernameNotFoundException("User not exist!");
        else {
            User user = opt.get();
            if (user.getPassword() == null) throw new UsernameNotFoundException("Please login through third-party Account!");
            Set<GrantedAuthority> authorities = new HashSet<>();
            // Add a default role directly without the ROLE_ prefix
            authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // replace "USER" with your actual default role
            return new CustomUserDetails(
                    user.getUserName(),
                    user.getPassword(),
                    user.getId(),
                    authorities
            );
        }
    }

    @Override
    public User createUser(User user) {
        user.setId(null);
        user.setUserPlans(null);
        String googleId = user.getGoogleId();
        if (googleId == null && user.getUserName() == null) throw new UsernameNotFoundException("User name cannot be null");
        // third party account should have no userName and password
        if (googleId != null) {
            user.setUserName(null);
            user.setPassword(null);
        }
        String userName = user.getUserName();
        if (userName != null) {
            validateUsername(userName);
            // normal user register should set email afterward to allow validation.
            user.setEmail(null);
            // normal user register should set profile picture afterward by posting.
            user.setUserPicture(null);
        }
        String email = user.getEmail();
        // third party account register with email
        if (email != null) validateEmail(email);

        String passwd= user.getPassword();
        String encodedPassword;
        if (passwd == null && userName != null){
            // normal user register with no password
            throw new UsernameNotFoundException("User password cannot be null");
        } else if (passwd == null) {
            // new user register with third-party account
            encodedPassword = null;
        } else {
            // normal user register
            encodedPassword = passwordEncoder.encode(passwd);
        }
        user.setPassword(encodedPassword);
        userRepository.save(user);
        // for third-party account, the picture will be remote url
        // for normal account, picture will be null
        return new User(null, null, user.getEmail(), user.getUserName(), user.getUserPicture());
    }

    @Override
    public User updateUser(User user) {
        User storedUser = getUserFromAuthentication();
        if (user.getUserName() != null && !user.getUserName().equals(storedUser.getUserName())) {
            // update user name
            validateUsername(user.getUserName());
            storedUser.setUserName(user.getUserName());
        }
        // we don't offer email update service for now, it should be linked with Google account
        if (user.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            storedUser.setPassword(encodedPassword);
        }
        // update user picture by posting, not here
        userRepository.save(storedUser);

        String pictureUri = storedUser.getUserPicture();
        if (pictureUri != null && !pictureUri.startsWith("http")) {
            // add app context only if it is a local file (not a remote url or null)
            pictureUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/picture/{pictureUrl}").
                    buildAndExpand(pictureUri).toUri().toString();
        }
        return new User(null, null, storedUser.getEmail(), storedUser.getUserName(), pictureUri);
    }

    @Override
    public User getUser() {
        User user = getUserFromAuthentication();
        String pictureUri = user.getUserPicture();
        if (pictureUri != null && !pictureUri.startsWith("http")) {
            // add app context only if it is a local file (not a remote url or null)
            pictureUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/picture/{pictureUrl}").
                    buildAndExpand(pictureUri).toUri().toString();
        }
        return new User(null, null, user.getEmail(), user.getUserName(), pictureUri);
    }

    @Override
    public void deleteUser() {
        User user = getUserFromAuthentication();
        String pictureUri = user.getUserPicture();
        if (pictureUri != null && !pictureUri.startsWith("http")) {
            // delete local file
            Path file = Paths.get(uploadPath).resolve(pictureUri);
            if (Files.exists(file)) {
                try {
                    Files.delete(file);
                } catch (IOException e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete file: " + pictureUri, e);
                }
            }
        }
        userRepository.delete(user);
    }

    @Override
    public String updateUserPicture(MultipartFile file) {
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please select a file to upload.");
        }

        // Check file size
        if (file.getSize() > userPictureSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size must be less than " + userPictureSize/1024 + " KB.");
        }

        // Check file content type
        String contentType = file.getContentType();
        if (!isAllowedContentType(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File content type is not allowed");
        }

        try {
            // Create the directory if it doesn't exist
            Path uploadPathVal = Paths.get(uploadPath);
            if (!Files.exists(uploadPathVal)) {
                Files.createDirectories(uploadPathVal);
            }

            // Save the file to the server
            User user = getUserFromAuthentication();
            String fileName = "user_" + user.getId() + "_" + file.getOriginalFilename();
            Path filePath = uploadPathVal.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Update the user's picture in the database
            String oldPictureName = user.getUserPicture();
            if ( oldPictureName != null && !oldPictureName.startsWith("http") && !oldPictureName.equals(fileName)) {
                Path oldFilePath = uploadPathVal.resolve(oldPictureName);
                if (Files.exists(oldFilePath)) {
                    Files.delete(oldFilePath);
                }
            }
            user.setUserPicture(fileName);
            userRepository.save(user);
            return fileName;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload file.", e);
        }
    }

    @Override
    public Resource getUserPicture(String filename) {
        Path filePath = Paths.get(uploadPath).resolve(filename);
        if (Files.exists(filePath) && Files.isReadable(filePath)) {
            Resource resource;
            try {
                resource = new UrlResource(filePath.toUri());
            } catch (MalformedURLException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create URL resource for file: " + filename, e);
            }
            if (resource.exists()) return resource;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found.");
    }

    private User getUserFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetail = (CustomUserDetails) authentication.getPrincipal();
        return userRepository.findById(userDetail.getUserId()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private void validateEmail(String email) {
        String emailRegex = "^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) throw new UsernameNotFoundException("User email invalid: " + email);
        if (userRepository.findByEmail(email).isPresent()) throw new EmailAlreadyExistException(email);
    }

    private void validateUsername(String userName) {
        // This example allows alphanumeric characters, underscores, and hyphens, with a length between 3 and 16 characters
        String usernameRegex = "^[a-zA-Z0-9_-]{3,16}$";
        Pattern pattern = Pattern.compile(usernameRegex);
        Matcher matcher = pattern.matcher(userName);
        if (!matcher.matches()) throw new UsernameNotFoundException("User name invalid: " + userName);
        if (userRepository.findByUserName(userName).isPresent()) throw new UserAlreadyExistException(userName);
    }

    private boolean isAllowedContentType(String contentType) {
        for (String allowedType : ALLOWED_CONTENT_TYPES) {
            if (allowedType.equals(contentType)) {
                return true;
            }
        }
        return false;
    }
}
