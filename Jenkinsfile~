pipeline{

  agent any
  environment {
    version = '2.0'
  }

  stages{

    stage('Start Database'){
      steps{
        sh script: 'docker stop planhattan-mysql', returnStatus: true
        sh script: 'docker rm planhattan-mysql', returnStatus: true
        sh 'docker-compose -p planhattan -f mysql.yaml up -d --force-recreate'
      }
    }

    stage('Test'){
      steps{
        sh script: 'docker stop planhattan-api', returnStatus: true
        sh script: 'docker rm planhattan-api || true', returnStatus: true
        sh 'mvn test'
      }
    }

    stage('Build'){
      steps{
        sh 'mvn clean package'
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
        sh "export version=${version} && docker-compose -p planhattan -f planhattan-api.yaml up -d --force-recreate"
      }
    }
  }
}
