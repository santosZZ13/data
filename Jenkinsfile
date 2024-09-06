pipeline {
    agent any
    environment {
        test_cre = credentials('test-secret')
        account = 'test-250@santossv.iam.gserviceaccount.com'
        host = 'asia-south1-docker.pkg.dev'

        PROJET_ID = 'santossv'
        CLUSTER_NAME = 'santossv-cluster'
        ZONE = 'asia-south1-a'
        K8S_NAMESPACE = 'santossv-namespace'
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
                }
            }
        }


        stage('Deploy to GKE') {
            steps {
                script {

                    sh """
                        sed -e 's|{{DEPLOYMENT_NAME}}|${DATA_SERVICE_DEPLOYMENT_NAME}|g' \
                            -e 's|{{DEPLOYMENT_NAME_LABEL}}|${DEPLOYMENT_NAME_LABEL}|g' \
                            -e 's|{{DATA_SERVICE_PORT}}|${DATA_SERVICE_PORT}|g' \
                            k8s/deployment.yaml > k8s/deployment.yaml

                    """



                    withCredentials([test_cre]) {
                        sh "gcloud auth activate-service-account --key-file=${test_cre}"
                    }
                    sh "gcloud container clusters get-credentials ${CLUSTER_NAME} --zone ${ZONE} --project ${PROJET_ID}"
                    sh "kubectl create namespace ${K8S_NAMESPACE}"
                    sh "kubectl config set-context --current --namespace=${K8S_NAMESPACE}"
                    sh "kubectl apply -f k8s/deployment.yaml"
                }
            }
        }


    }
}
