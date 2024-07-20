
pipeline {
    
    agent any
    environment{
        SONAR_HOME = tool "Sonar"
    }
    stages {
        
        stage("Code"){
            steps{
                git url: "https://github.com/shelke1234/node-Js-front_backend.git" , branch: "Dhananjay"
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
        // stage("SonarQube Quality Gates"){
        //     steps{
        //        timeout(time: 2, unit: "MINUTES"){
        //            waitForQualityGate abortPipeline: true
        //        }
        //     }
        // }
        // stage("OWASP"){
        //     steps{
        //         dependencyCheck additionalArguments: '--scan ./', odcInstallation: 'OWASP'
        //         dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
        //     }
        // }
        stage("Build & Test"){
            steps{
                sh 'aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin 449368362816.dkr.ecr.ap-south-1.amazonaws.com'
                echo "Code Built Successfully"
            }
        }
        // stage("Trivy"){
        //     steps{
        //         sh "trivy image node-app-batch-6"
        //     }
        // }
        stage("Push to Private Docker Hub Repo"){
            steps{
                sh "docker build -t devopsprojectpratices ."
                sh "docker tag devopsprojectpratices:latest 449368362816.dkr.ecr.ap-south-1.amazonaws.com/devopsprojectpratices:latest"
                sh "docker push 449368362816.dkr.ecr.ap-south-1.amazonaws.com/devopsprojectpratices:latest"
                sh "aws ecr get-login-password ${env.aceruser} -p ${env.acrpassword}"
                sh "docker tag node-app-test-new:latest ${env.aceruser}/node-app-test-new:latest"
                sh "docker push ${env.aceruser}/node-app-test-new:latest"
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
