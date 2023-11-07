pipeline {
    agent any

    environment {
        IMAGE_NAME = 'titulacion'
        IMAGE_TAG = '001'
        DOCKER_HUB_USER = 'alejo88'
        ENVIRONMENT = 'prod'
        AWS_DEFAULT_REGION = "us-east-1"
	THE_BUTLER_SAYS_SO = credentials('764071613828')
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
	stage('Sonarqube Scan') {
            steps {
                withSonarQubeEnv(installationName: 'sonarqubetest') {
                    sh 'mvn clean package sonar:sonar'
                }
            }
        }
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
                        //sh 'kubectl delete configmap hostname-config'
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
