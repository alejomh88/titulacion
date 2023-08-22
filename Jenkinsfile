pipeline {
    agent any
    environment {
        AWS_ACCESS_KEY_ID = 'ASIAQMZCW2O6WIVRA67Y'
        AWS_SECRET_ACCESS_KEY = 'pSQb7dzBLDMpkXkitwBaZW+wAu453R8UN3OHhhWr'
        AWS_DEFAULT_REGION = "us-east-1"
    }
    stages {
        stage("Create an EKS Cluster") {
            steps {
                script {
                    dir('terraform/test') {
                        sh "terraform init"
                        sh "terraform apply -auto-approve"
                    }
                }
            }
        }
    }
}
