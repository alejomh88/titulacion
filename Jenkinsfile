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
	stage('Scan') {
            steps {
                echo 'Scan!'
                withSonarQubeEnv(installationName: 'sonarqubetest')
		  sh './mvnw clean package sonar:sonar'
            }
        }
    }
}
