pipeline{

  agent any
  tools {
  maven 'Maven'
  }
  environment {
    version = '2.0'
    DB_PASSWORD = credentials('DB_PASSWORD')
    PLANHATTAN_MYSQL_VOLUME = credentials('PLANHATTAN_MYSQL_VOLUME')
    PLANHATTAN_UPLOADS = credentials('PLANHATTAN_UPLOADS')
    OPEN_WEATHER_KEY = credentials('OPEN_WEATHER_KEY')
    GOOGLE_OAUTH2_CLIENT_ID = credentials('GOOGLE_OAUTH2_CLIENT_ID')
    GOOGLE_OAUTH2_CLIENT_SECRET = credentials('GOOGLE_OAUTH2_CLIENT_SECRET')
  }

  stages{

    stage('Start Database'){
      steps{
        sh script: 'docker stop planhattan-mysql', returnStatus: true
        sh script: 'docker rm planhattan-mysql', returnStatus: true
        sh 'docker-compose -p planhattan -f mysql.yaml up -d --force-recreate'
      }
    }

    stage('Wait for Database to Start'){
      steps{
        sh '''#!/bin/sh
            max_attempts=60
            attempt=1
            until (docker inspect planhattan-mysql --format '{{.State.Running}}' | grep -q true &&
                   docker exec planhattan-mysql mysqladmin ping -h localhost --silent) || [ $attempt -gt $max_attempts ];
            do
                echo "Attempt $attempt/$max_attempts - Database not ready yet"
                sleep 1
                attempt=$((attempt+1))
            done
            if [ $attempt -gt $max_attempts ]; then
                echo "Database did not start within $max_attempts seconds!"
                exit 1
            fi'''
      }
    }

    stage('Test and Build'){
      steps{
        sh '''
          MAVEN_OPTS="-Xms256m -Xmx512m -Dlog.level=WARN" \
          mvn clean package -Dsurefire.forkCount=1 -Dsurefire.reuseForks=false -T 1
        '''
      }
    }

    stage('Build Docker Image'){
      steps{
        sh "docker build -t planhattan-api:${version} ."
        sh "docker tag planhattan-api:${version} zeli8888/planhattan-api:${version}"
        sh "docker push zeli8888/planhattan-api:${version}"
        sh "docker image prune -f"
      }
    }

    stage('Run Docker Container'){
      steps{
        sh script: 'docker stop planhattan-api', returnStatus: true
        sh script: 'docker rm planhattan-api || true', returnStatus: true
        sh "export version=${version} && docker-compose -p planhattan -f planhattan-api.yaml up -d --force-recreate"
      }
    }
  }
}
