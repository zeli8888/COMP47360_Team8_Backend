pipeline{

  agent any
  environment {
    version = '1.2'
  }

  stages{

    stage('Start Database'){
      steps{
        sh 'docker-compose -p planhattan -f mysql.yaml up -d'
      }
    }

    stage('Test'){
      steps{
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
      }
    }

    stage('Run Docker Container'){
      steps{
        sh "export version=${version} && docker-compose -p planhattan -f planhattan-api.yaml up --force-recreate -d"
      }
    }
  }
}
