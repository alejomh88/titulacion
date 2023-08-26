pipeline {
    agent any

    environment {
        IMAGE_NAME = 'titulacion'
        IMAGE_TAG = '001'
        DOCKER_HUB_USER = 'alejo88'
        ENVIRONMENT = 'prod'
        AWS_ACCESS_KEY_ID = 'ASIAQMZCW2O6W2AIRKPW'
        AWS_SECRET_ACCESS_KEY = 'dOs2lEPSnVbnofDzqsxN4pUhRPlmFpWtHbyPxfkG'
        AWS_SESSION_TOKEN = 'FwoGZXIvYXdzEKz//////////wEaDOil73Hc1v4IvhgTESK2AcdhWGQbWw21uE2sFq2Gzih5NUJmc3vCwHzBYLhU7niGBSEtnsQLRB/fAXi3xNhcG+U4vmOyOOU7QGJ4Co1pI+MD5YV0i+M8pxwnoQt2tLKz19JulGk2yJwwusKtfC1ppLZ/3xIOxGC26Ce7/OTsljskmu2ZNDqmPTPJikfrd3G1OThKbrgeCPw1BJ2hW4f0Zr4SP4UG1VJ7/u7z2Jip34b0KeNsLQ+qMi5FeG5oi0EXt+hVAr5mKLqSqacGMi3MnMQDLLnNDVR+Y7d+0JMrXQolxByrO7KGMG8lqBrWItTgBb7Sbmt7gjS/fno='
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
