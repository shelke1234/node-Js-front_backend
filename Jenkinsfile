pipeline {
    agent any
    
    environment {
        SONAR_HOME = tool "Sonar"
        AWS_REGION = 'ap-south-1' // Set your AWS region
        ECR_REPO_NAME = 'devopsprojectpratices' // Set your ECR repository name
        IMAGE_TAG = "${env.BUILD_ID}" // Use the build ID as the image tag
        DOCKER_IMAGE = "${ECR_REPO_NAME}:${IMAGE_TAG}"
        AWS_ACCOUNT_ID = credentials('awslogin') // Assuming you've stored your AWS account ID as a Jenkins credential
        AWS_ACCESS_KEY_ID = credentials('awslogin')
        AWS_SECRET_ACCESS_KEY = credentials('awslogin')
    }
    
    stages {
        stage("Code"){
            steps{
                git url: "https://github.com/shelke1234/node-Js-front_backend.git", branch: "Dhananjay"
                echo "Code Cloned Successfully"
            }
        }
        
        stage("SonarQube Analysis"){
            steps{
                withSonarQubeEnv("Sonar"){
                    sh "$SONAR_HOME/bin/sonar-scanner -Dsonar.projectName=node-Js-front_backend -Dsonar.projectKey=node-Js-front_backend -X"
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    // Build Docker image from Dockerfile
                    dockerImage = docker.build("${ECR_REPO_NAME}:${IMAGE_TAG}", "./")
                }
            }
        }
        
        stage('Login to ECR') {
            steps {
                script {
                    // Retrieve ECR login password
                    sh "aws ecr get-login-password --region ${AWS_REGION}"
                    echo 'show me'

                    
                    // Login to ECR
                    sh "docker login --username AWS --password-stdin ${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com <<< ${ecrLogin}"
                    echo 'login done'
                }
            }
        }
        
        stage("Push to ECR repo"){
            steps{
                // Tag the Docker image with ECR repository URL
                sh "docker tag ${ECR_REPO_NAME}:${IMAGE_TAG} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO_NAME}:${IMAGE_TAG}"
                
                // Push Docker image to ECR
                sh "docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO_NAME}:${IMAGE_TAG}"
            }
        }

        stage("Deploy"){
            steps{
                sh "docker-compose down && docker-compose up -d"
                echo "App Deployed Successfully"
            }
        }
    }
}
