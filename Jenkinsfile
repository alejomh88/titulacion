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
                sh 'make build'
            }
        }
    }
    post {
        always {
		    archiveArtifacts artifacts: 'results/*.xml'
            junit 'results/*_result.xml'
            cleanWs()
        }
         failure {
			// mail bcc: '', body: '<b>Error</b><br>Task: ${env.JOB_NAME} <br>Build Number: ${env.BUILD_NUMBER} <br> URL de build: ${env.BUILD_URL}', 
            //cc: 'paae_mh@hotmail.com', from: '', replyTo: '', subject: 'ERROR CI: Project name -> ${env.JOB_NAME}', to: 'paul.aguayo88@gmail.com'
			echo "Task: ${env.JOB_NAME} , Build Number: ${env.BUILD_NUMBER} , URL de build: ${env.BUILD_URL}"
               } 
    }
}
