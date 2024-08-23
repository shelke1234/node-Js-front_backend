pipeline {
    agent any
  environment {
        SONAR_HOME = tool "Sonar"
        AWS_REGION = 'ap-south-1'  
        ECR_REPO_NAME = "${env.ECR_REPO_NAME}"  
        IMAGE_TAG = "${env.BUILD_ID}"  
        AWS_ACCOUNT_ID = credentials('awslogin')  
  }

    // parameters {
    //     booleanParam(name: 'skip_test', defaultValue: false, description: 'Set to true to skip the test stage')
    // }
    stages{
        stage("code") {
            steps{
                git branch: 'master', credentialsId: 'dhananjay-github', url: 'https://github.com/shelke1234/Configuring-CI-CD-on-Kubernetes-with-Jenkins.git'
                echo "Code Cloned Successfully"
            }
        }
        stage("SonarQube
                withSonarQubeEnv("Sonar") {
                    sh "$SONAR_HOME/bin/sonar-scanner -Dsonar.projectName=django-notes-app -Dsonar.projectKey=django-notes-app -X"
                }
            }
        }
        
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
        stage("Docker remove Image") {
            steps {
                sh "docker rmi ${env.ECR_REPO_NAME}:${IMAGE_TAG}"
            }
        }
        stage("Apply kubernetes Files"){
            steps {
                withkubeConfig([credentialsId: 'kubeconfig']) {
                    sh 'cat deployment.yml | sed "s/{{IMAGE_TAG}/{{IMAGE_TAG}}/g" | kubectl apply -f -'
                    sh 'kubectl apply -f service.yaml'
                }
            }
        }
        stage("Deploy"){
            steps{
                echo "Deploy to"
            }
        }
    }

}