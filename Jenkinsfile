pipeline {
    agent any

    tools {
        maven: 'my-maven'
    }


    environment {
//        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'
    }

    stages {


        stage('Build and Test') {
            steps {
                script {
                    sh 'mvn -version'
                    sh 'java -version'
                    sh 'mvn clean package'
                }
            }
        }


        stage('Packaging/Pushing Docker Image') {
            steps {
                withDockerRegistry([credentialsId: 'dockerhub', url: 'https://index.docker.io/v1/']) {
                    sh 'docker build -t cucarot123/data-service .'
                    sh 'docker push cucarot123/data-service'
                }
            }
        }

        stage('Deploy redis') {
            steps {
                script {
                    echo 'Deploying and cleaning up Redis'
                    sh 'docker image pull redis:7.4.0'
                    sh 'docker network create data-service-network || echo "this network already exists"'
                    sh 'docker container stop redis || echo "this container does not exist"'
                    sh 'echo y | docker container prune'
                    sh 'docker volume rm redis-data || echo "this volume does not exist"'

                    sh 'docker run --name redis  --rm --network data-service-network -v redis-data:/var/lib/redis  redis:7.4.0'
                    sh 'sleep 10'
                }
            }
        }

        stage('Deploy data-service') {
            steps {
                script {

                    echo 'Deploying and cleaning up data-service'

                    echo 'docker image pull cucarot123/data-service'
                    sh 'docker container stop data-service || echo "this container does not exist"'
                    sh 'docker network create data-service-network || echo "this network already exists"'
                    sh 'echo y | docker container prune'
                    sh 'docker run --name data-service --rm --network data-service-network -p 8080:8080 cucarot123/data-service'
                }
            }
        }
    }

    post {
        // If the pipeline is successful, send a Slack notification.
//        success {
//            slackSend channel: '#general',
//                    color: 'good',
//                    message: "The pipeline ${currentBuild.fullDisplayName} has succeeded."
//        }

        // Cleanup workspace after the build is done
        always {
            cleanWs()
        }
    }
}