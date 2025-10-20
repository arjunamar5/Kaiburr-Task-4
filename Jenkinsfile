pipeline {
    agent any
    environment {
        DOCKER_IMAGE = "arjunamar5/poc-app-task"
        DOCKER_TAG = "${BUILD_NUMBER}"
        DOCKER_USER = "arjunamar5"
        DOCKER_PASS = "ARJUN@DOCKER"
    }

    stages {
        stage('Build Jar File') {
            steps {
                bat '''
                    echo Building application...
                    mvn clean package -DskipTests
                '''
            }
        }

        stage('Docker Build') {
            steps {
                bat '''
                    echo Building Docker image...
                    docker build -t %DOCKER_IMAGE%:%DOCKER_TAG% .
                '''
            }
        }

        stage('Docker Push') {
            steps {
                bat '''
                    echo Logging into DockerHub...
                    echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin
                    docker tag %DOCKER_IMAGE%:%DOCKER_TAG% %DOCKER_IMAGE%:latest
                    docker push %DOCKER_IMAGE%:latest
                '''
            }
        }
    }

    post {
        always {
            bat 'docker logout'
        }
    }
}

