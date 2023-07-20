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
		   withDockerServer([uri: "tcp://172.17.0.1:2375"]) {
  			withDockerRegistry([credentialsId: 'dockerhubpwd', url: "https://hub.docker.com/repositories/alejo88"]) {
    			sh '''
      			docker build -t whatever .
      			docker push whatever
	  		'''
			}
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
