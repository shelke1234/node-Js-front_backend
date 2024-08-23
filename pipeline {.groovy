pipeline {
    agent any
    
    tools {
        jdk 'jdk17'
        maven 'maven3'
    }
  environment {
        SONAR_HOME = tool "sonar-scanner" 
  }
    stages {
        stage('Git checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/shelke1234/Boardgame.git'
            }
        }
        stage('Compile') {
            steps {
                sh 'mvn compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'    
            }
        }
        stage('File System scan') {
            steps {
                sh "trivy fs --format table -o trivy-fs-report.html ."
            }
        }
        stage('SonarQube Analsyis') {
            steps {
                withSonarQubeEnv('sonar-scanner') {
                   sh "$SONAR_HOME/bin/sonar-scanner -Dsonar.projectName=BoardGame -Dsonar.projectKey=BoardGame -X"
                }
            }
        }
        stage('Quality Gate') {
            steps {
                script {
                  waitForQualityGate abortPipeline: false, credentialsId: 'sonar-token' 
                }
            }
        }
        stage('Build') {
            steps {
                sh "mvn package"
            }
        }
        stage('Publish To Nexus') {
            steps {
                withMaven(globalMavenSettingsConfig: 'global-settings', jdk: 'jdk17', maven: 'maven3', mavenSettingsConfig: '', traceability: true) {
                    sh "mvn deploy"
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                        sh "docker build -t dhananjayshelke/boardshack:latest ."
                    }
                }
            }
        }
        stage('Docker Image scan') {
            steps {
                sh "trivy image --format table -o trivy-image-report.html dhananjayshelke/boardshack:latest "
            }
        }
        stage('Push Docker Image') {
            steps {
                script {
                    withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                        sh "docker push dhananjayshelke/boardshack:latest"
                    }
                }
            }
        }
    }
} 
