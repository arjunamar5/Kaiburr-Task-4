pipeline {
    agent any
    environment {
        DOCKER_IMAGE = "arjunamar5/poc-app-task"
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        DOCKER_USER = 'arjunamar5@gmail.com'
        DOCKER_PASS = 'ARJUN@DOCKER'
        PATH = "/opt/homebrew/bin:/usr/local/bin:${env.PATH}"
    }
    stages {
        stage('Building jar file') {
            steps {
                sh '''
                    echo "Building application..."
                    mvn clean package -DskipTests
                '''
            }
        }
        stage('Docker Build') {
            steps {
                sh '''
                    echo "Building Docker image..."
                    docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                '''
            }
        }
        stage('Docker Push') {
            steps { {
                    sh '''
                        echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                        docker push ${DOCKER_IMAGE}:${DOCKER_TAG}
                        docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest
                        docker push ${DOCKER_IMAGE}:latest
                    '''
                }
            }
        }
    }
    post {
        always {
            sh 'docker logout'
        }
    }
}