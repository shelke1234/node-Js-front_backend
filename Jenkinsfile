pipeline {
     agent { label ""}
    
    environment {
        SONAR_HOME = tool "Sonar"
        AWS_REGION = 'ap-south-1'  
        ECR_REPO_NAME = "${env.ECR_REPO_NAME}"  
        IMAGE_TAG = "${env.BUILD_ID}"  
        AWS_ACCOUNT_ID = credentials('awslogin')  
    }
    
    stages {
        stage("Code") {
            steps {
                git branch: 'Dhananjay', credentialsId: 'dhananjay-github', url: 'https://github.com/LondheShubham153/node-todo-cicd.git'
                echo "Code Cloned Successfully"
            }
        }
        
        stage("SonarQube Analysis") {
            steps {
                withSonarQubeEnv("Sonar") {
                    sh "$SONAR_HOME/bin/sonar-scanner -Dsonar.projectName=node-Js-front_backend -Dsonar.projectKey=node-Js-front_backend -X"
                }
            }
        }
        
        // stage("SonarQube Quality Gates") {
        //     steps {
        //         timeout(time: 1, unit: "MINUTES") {
        //             waitForQualityGate abortPipeline: false
        //         }
        //     }
        // }
        
        stage('Build and Push Docker Image') {
            steps {
                script {
                    // Build Docker image
                    def dockerImage = docker.build("${env.ECR_REPO_NAME}:${IMAGE_TAG}", "./")
                    
                    def trivyOutput = sh(script: "trivy image ${env.ECR_REPO_NAME}:${IMAGE_TAG}", returnStdout: true).trim()

                    println trivyOutput

                    // Check if vulnerabilities were found
                    // Uncomment the following block if you want to fail on vulnerabilities found
                    /*
                    if (trivyOutput.contains("Total: 0")) {
                        echo "No vulnerabilities found in the Docker image."
                    } else {
                        error "Vulnerabilities found in the Docker image. Build failed."
                    }
                    */
                    
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
