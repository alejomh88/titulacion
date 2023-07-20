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
                    sh 'docker build -t alejo88/devops-integration .'
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
