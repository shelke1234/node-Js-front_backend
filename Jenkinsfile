pipeline {
    agent any
    
    environment {
        AWS_REGION = 'ap-south-1'  // Set your AWS region
        ECR_REPO_NAME = "${env.ECR_REPO_NAME}"   // Set your ECR repository name
        IMAGE_TAG = "${env.BUILD_ID}"  // Use the build ID as the image tag
        AWS_ACCOUNT_ID = credentials('awslogin')  // Reference AWS Account ID stored in Jenkins credentials
    }
    
    stages {
        stage('Build and Push Docker Image') {
            steps {
                script {
                    // Build Docker image
                    def dockerImage = docker.build("${env.ECR_REPO_NAME}:${IMAGE_TAG}", "./")
                    
                    // Run Trivy to scan the Docker image
                    def trivyOutput = sh(script: "trivy image ${env.ECR_REPO_NAME}:${IMAGE_TAG}", returnStdout: true).trim()

                    // Display Trivy scan results
                    println trivyOutput

                    // Check if vulnerabilities were found
                    // if (trivyOutput.contains("Total: 0")) {
                    //     echo "No vulnerabilities found in the Docker image."
                    // } else {
                    //     error "Vulnerabilities found in the Docker image. Build failed."
                    // }
                    
                    // Retrieve ECR login password
                    def ecrLogin = sh(script: "aws ecr get-login-password --region ${env.AWS_REGION}", returnStdout: true).trim()
                    
                    // Login to AWS ECR
                    sh "echo ${ecrLogin} | docker login --username AWS --password-stdin ${env.ACCOUNT_ID}.dkr.ecr.${env.AWS_REGION}.amazonaws.com"
                    
                    // Tag and push Docker image to ECR
                    sh "docker tag ${env.ECR_REPO_NAME}:${IMAGE_TAG} ${env.ACCOUNT_ID}.dkr.ecr.${env.AWS_REGION}.amazonaws.com/${env.ECR_REPO_NAME}:${IMAGE_TAG}"
                    sh "docker push ${env.ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${env.ECR_REPO_NAME}:${IMAGE_TAG}"
                }
            }
        }
        
        stage('Deploy') {
            steps {
                sh """
                    docker-compose --env-file docker-compose.env down
                    docker-compose --env-file docker-compose.env up -d
                """
                echo "App Deployed Successfully"
            }
        }
    }
}
