pipeline {
    agent {
        label 'maven'
    }

    stages {
        stage('Source') {
            steps {
                git 'https://github.com/alejomh88/titulacion.git'
            }
        }
        stage('Build') {
            steps {
                echo 'Building stage!'
		sh 'mvn clean install'
            }
        }
	stage('Build docker image'){
            steps{
                script{
		    docker.withServer('tcp://172.17.0.1:2375') {
  		    def myImage = docker.build('alejo88/devops-integration')
                    //sh 'docker build -t alejo88/devops-integration .'
		    }
                }
            }
        }
	/*
	stage('Scan') {
            steps {
                echo 'Scan!'
                withSonarQubeEnv(installationName: 'sonarqubetest') {
		  sh 'mvn clean package sonar:sonar'
		}
            }
        }
	*/
    }
}
