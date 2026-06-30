pipeline {
    agent any
    
    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'idev', 'prod'],
            description: 'Select the target environment'
        )
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh "./mvnw clean compile"
            }
        }
        
        stage('Test') {
            steps {
                sh "./mvnw test"
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                sh "./mvnw package -DskipTests"
            }
        }
        
        stage('Build Docker Image') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                script {
                    def imageName = "notification-service:${ENVIRONMENT}-${BUILD_NUMBER}"
                    sh "docker build --build-arg SPRING_PROFILES_ACTIVE=${ENVIRONMENT} -t ${imageName} ."
                    sh "docker tag ${imageName} notification-service:${ENVIRONMENT}-latest"
                }
            }
        }
        
        stage('Deploy to Dev') {
            when {
                expression { params.ENVIRONMENT == 'dev' }
            }
            steps {
                echo "Deploying to Development environment"
                sh """
                    # Add your dev deployment commands here
                    # Example: kubectl apply -f k8s/dev/
                    echo "Deploying to dev cluster"
                """
            }
        }
        
        stage('Deploy to IDev') {
            when {
                expression { params.ENVIRONMENT == 'idev' }
            }
            steps {
                echo "Deploying to Integration Development environment"
                sh """
                    # Add your idev deployment commands here
                    # Example: kubectl apply -f k8s/idev/
                    echo "Deploying to idev cluster"
                """
            }
        }
        
        stage('Deploy to Prod') {
            when {
                expression { params.ENVIRONMENT == 'prod' }
            }
            steps {
                input message: 'Deploy to Production?', ok: 'Deploy'
                echo "Deploying to Production environment"
                sh """
                    # Add your prod deployment commands here
                    # Example: kubectl apply -f k8s/prod/
                    echo "Deploying to prod cluster"
                """
            }
        }
    }
    
    post {
        success {
            echo "Pipeline executed successfully for ${params.ENVIRONMENT} environment"
        }
        failure {
            echo "Pipeline failed for ${params.ENVIRONMENT} environment"
        }
    }
}
