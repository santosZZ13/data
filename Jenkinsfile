pipeline {
    agent any
    environment {
        test_cre = credentials('test-secret')
        account = 'test-250@santossv.iam.gserviceaccount.com'
        host = 'asia-south1-docker.pkg.dev'
    }
    stages {
        stage('Example') {
            steps {
                sh 'gcloud auth activate-service-account ${account} --key-file=${test_cre}'
                sh 'gcloud auth configure-docker ${host}'
                echo 'AUTHENTICATED SUCCESSFULLY'
            }
        }
        
        stage('Get Branch Name') {
            steps {
                script {
                    def commitHash = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                    env.dockerTag = "dev-commit-${commitHash}-${BUILD_NUMBER}"
                    echo '${env.dockerTag}'
                }
            }
        }
    }
}
