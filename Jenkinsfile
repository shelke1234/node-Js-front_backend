pipeline {
    agent any
    
    environment {
        AWS_REGION = 'ap-south-1'  // Set your AWS region
        ECR_REPO_NAME = 'devopsprojectpratices'  // Set your ECR repository name
        IMAGE_TAG = "${env.BUILD_ID}"  // Use the build ID as the image tag
        AWS_ACCOUNT_ID = credentials('AWS_ACCOUNT_ID')  // Reference AWS Account ID stored in Jenkins credentials
        AWS_ACCESS_KEY_ID = credentials('awslogin')  // Reference AWS Access Key ID stored in Jenkins credentials
        AWS_SECRET_ACCESS_KEY = credentials('awslogin')  // Reference AWS Secret Access Key stored in Jenkins credentials
    }
    
    stages {
        stage('Build and Push Docker Image') {
            steps {
                script {
                    // Build Docker image
                    def dockerImage = docker.build("${ECR_REPO_NAME}:${IMAGE_TAG}", "./")
                    
                    // Retrieve ECR login password
                    def ecrLogin = sh(script: "aws ecr get-login-password --region ${AWS_REGION}", returnStdout: true).trim()
                    
                    // Login to AWS ECR
                    sh "echo ${ecrLogin} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
                    
                    // Tag and push Docker image to ECR
                    sh "docker tag ${ECR_REPO_NAME}:${IMAGE_TAG} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO_NAME}:${IMAGE_TAG}"
                    sh "docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO_NAME}:${IMAGE_TAG}"
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
