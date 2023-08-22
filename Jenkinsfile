pipeline {
    agent any
    environment {
        AWS_ACCESS_KEY_ID = 'ASIAQMZCW2O6WIVRA67Y'
        AWS_SECRET_ACCESS_KEY = 'pSQb7dzBLDMpkXkitwBaZW+wAu453R8UN3OHhhWr'
        AWS_SESSION_TOKEN = 'FwoGZXIvYXdzEDsaDCNqbigweym5MSdfiiK2AXuLaE1lKUzNnE9+7FF9syVSbcCndJAIJeW1Ce6fIsGzCZGaNNWx5lRAxXMZvM88mUyCUeeYLaLj+KkwqI8DfIopW1/B8PP1/Sa4ZfhUsuRJUmTldSZAxUDvrOjMV0n05kqrSsNV+XK0PM3TTdmCCOX/P/DCF/woePpUCCY8LEeVet8jYkF4IvabgT/k5ceaqL2n+CT8DcG3OU4wSyE+hc3lEuJ0Gmq7UwiFcSRp+PhYdBYygd9OKM6skKcGMi31JX/xApA9afnpYMp0CKMaHyvNQYkJaxRRNOBcKBr8oUO1DHJYxmQ8Qd2KbP4='
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
