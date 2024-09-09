pipeline {
    agent any

    tools {
        maven 'my-maven'
    }

    environment {
//        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'

        PROJECT_ID = 'santossv'
        CLUSTER_NAME = "node-kubernetes"
        ZONE_KUBERNETES = "us-central1-a"

        ZONE = "asia-east2"
        DATA_SERVICE_REPO = "santos"
        DEPLOY_FOLDER = "${WORKSPACE}/deploy"

//        GOOGLE_APPLICATION_CREDENTIALS = credentials('gcp-service-account-key')
        CLIENT_EMAIL = "test-250@santossv.iam.gserviceaccount.com"
        GCLOUD_CREDS = credentials('test-secret')

    }

    stages {
        stage('Init Environment') {
            steps {
                script {

                    scmVars = checkout scm
                    env.BRANCH_NAME = scmVars.GIT_BRANCH.replaceAll('^origin/', '').replaceAll('/', '-').toLowerCase()

                    env.DATA_SERVICE_DEPLOYMENT_NAME = "data-service-${env.BRANCH_NAME}"
                    env.DEPLOYMENT_NAME_LABEL = "data-service"
                    env.DATA_SERVICE_PORT = "8080"


                    env.DATA_SERVICE_REGISTRY_PATH = "${ZONE}-docker.pkg.dev/${PROJECT_ID}/${DATA_SERVICE_REPO}/${DATA_SERVICE_DEPLOYMENT_NAME}"
                }
            }
        }


        stage('Build and Test') {
            steps {
                script {
                    sh 'mvn -version'
                    sh 'java -version'
                    sh 'mvn clean package'
                }
            }
        }


        stage('Set up Google Cloud') {
            steps {
                script {

//                    gcloud container clusters get-credentials ${CLUSTER_NAME} --zone ${ZONE}

                    sh '''
                        gcloud auth activate-service-account ${CLIENT_EMAIL} --key-file="${GCLOUD_CREDS}"
                        gcloud config set project ${PROJECT_ID}
                        gcloud auth configure-docker ${ZONE}-docker.pkg.dev
                    '''
                }
            }
        }

        stage('Pushing Docker Image') {
            steps {
                script {
                    sh 'docker build -t ${DATA_SERVICE_REGISTRY_PATH}:latest .'
                    // asia-east2-docker.pkg.dev/santossv/santos/data-service-master:11
                    sh 'docker push ${DATA_SERVICE_REGISTRY_PATH}:latest'
                }
            }
        }

        stage('Deploy to GKE') {
            steps {
                script {
                    dir(DEPLOY_FOLDER) {
                        sh '''
                            sed -e "s|\\\${DATA_SERVICE_DEPLOYMENT_NAME}|${DATA_SERVICE_DEPLOYMENT_NAME}|g" \
                                -e "s|\\\${DEPLOYMENT_NAME_LABEL}|${DEPLOYMENT_NAME_LABEL}|g" \
                                -e "s|\\\${DATA_SERVICE_PORT}|${DATA_SERVICE_PORT}|g" \
                                -e "s|\\\${DATA_SERVICE_REGISTRY_PATH}|${DATA_SERVICE_REGISTRY_PATH}|g" \
                            data-service-deployment.yaml > data-service-deployment-updated.yaml
                            
                            gcloud container clusters get-credentials ${CLUSTER_NAME} --zone ${ZONE_KUBERNETES} --project ${PROJECT_ID}
                            
                            kubectl apply -f data-service-deployment-updated.yaml
                    '''
                    }
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
