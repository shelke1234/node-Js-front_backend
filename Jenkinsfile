pipeline {
    agent any
    
    environment {
        AWS_REGION = 'ap-south-1'  // Set your AWS region
        ECR_REPO_NAME = 'devopsprojectpratices'  // Set your ECR repository name
        IMAGE_TAG = "${env.BUILD_ID}"  // Use the build ID as the image tag
        AWS_ACCESS_KEY_ID = credentials('awslogin')  // Reference AWS Access Key ID stored in Jenkins credentials
        AWS_SECRET_ACCESS_KEY = credentials('awslogin')  // Reference AWS Secret Access Key stored in Jenkins credentials
    }
    
    stages {
        stage('Build and Push Docker Image') {
            steps {
                script {
                    // Build Docker image
                    def dockerImage = docker.build("${env.ECR_REPO_NAME}:${IMAGE_TAG}", "./")
                    
                    // Retrieve ECR login password
                    def ecrLogin = sh(script: "aws ecr get-login-password --region ${env.AWS_REGION}", returnStdout: true).trim()
                    
                    // Login to AWS ECR
                    sh "echo ${ecrLogin} | docker login --username AWS --password-stdin ${env.ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
                    
                    // Tag and push Docker image to ECR
                    sh "docker tag ${env.ECR_REPO_NAME}:${IMAGE_TAG} ${AWS_ACCOUNT_ID}.dkr.ecr.${env.AWS_REGION}.amazonaws.com/${env.ECR_REPO_NAME}:${IMAGE_TAG}"
                    sh "docker push ${env.ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${env.ECR_REPO_NAME}:${IMAGE_TAG}"
                }
            }
        }
        
        stage('Deploy') {
            steps {
                sh "docker-compose down && docker-compose up -d"
                echo "App Deployed Successfully"
            }
        }
    }
}
