pipeline {
    agent any

    environment {
        IMAGE_NAME = 'titulacion'
        IMAGE_TAG = '001'
        DOCKER_HUB_USER = 'alejo88'
        ENVIRONMENT = 'prod'
        AWS_ACCESS_KEY_ID = 'ASIAQMZCW2O65IJOHQ4H'
        AWS_SECRET_ACCESS_KEY = 'y1p2DTE/p1QlJ8viXo51K9FCtDNtHPVK/8fWTuGw'
        AWS_SESSION_TOKEN = 'FwoGZXIvYXdzEIT//////////wEaDKBjjr9ooUdKusVjbiK2AUidd7iSQjKeesaBtNks12Q2AXdz/Vy+QtJ+jSNqt37/RFvGxtAL3xuHbLvG6MZ3kF0wKsByqaBFc1Ke3ctIrXXOX3WanSN3agKXHeTck7Q1658g08T5gAU6BvkHtK3iBspg0ZSHqQAnxuhwFWIesKYJIRHz7Is0qJJVkICuD3BPRqEQdtTkoEaF1LrLXvRFILSFwMbKzogC6p/6YsMkr40U68pfd7qdcdIRqHrSqXDs/Qdzy4KjKKCioKcGMi3gp/oBmSTUcTvIYoRx80XLlMKyuY0K/USVb3YA5RSXjTCrcBZ2wkPZvEkc3l4='
        AWS_DEFAULT_REGION = "us-east-1"
    }
    stages {
        stage('Build and Test') {
            steps {
                sh 'mvn clean install'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
	/*
	stage('Sonarqube Scan') {
            steps {
                withSonarQubeEnv(installationName: 'sonarqubetest') {
                    sh 'mvn clean package sonar:sonar'
                }
            }
        }
	*/
        stage('Docker image build and push') {
            steps {
                script {
                    withDockerServer([uri: 'tcp://172.17.0.1:2375']) {
                        withDockerRegistry(credentialsId: 'dockerHubCredentials', url: 'https://index.docker.io/v1/') {
                            image = docker.build("${DOCKER_HUB_USER}/" + "${IMAGE_NAME}-" + "${ENVIRONMENT}:" + "${IMAGE_TAG}", '.')
                            image.push()
                            image.push('latest')
                        }
                    }
                }
            }
        }
		stage('Deploy to EKS') {
            steps {
                script {
                    dir('kubernetes/prod') {
                        sh 'aws eks update-kubeconfig --name myapp-eks-cluster'
                        sh 'kubectl delete configmap hostname-config'
                        sh 'kubectl create configmap hostname-config --from-literal=postgres_host=$(kubectl get svc postgresdb -o jsonpath="{.spec.clusterIP}")'
                        sh 'kubectl apply -f deployAPP.yml --force'
                    }
                }
            }
        }
        stage('Slack notification') {
            steps {
               slackSend channel: 'EduGPT', message: "Ejecución exitosa Pipeline Prod - Build N. ${BUILD_NUMBER}", 
               teamDomain: 'edugptespacio', tokenCredentialId: 'slack'
            }
        }
    }
    post {
        always {
            cleanWs(cleanWhenNotBuilt: false,
                    deleteDirs: true,
                    disableDeferredWipeout: true,
                    notFailBuild: true,
                    patterns: [[pattern: '.gitignore', type: 'INCLUDE'],
                               [pattern: '.propsfile', type: 'EXCLUDE']])
        }
    }
}
