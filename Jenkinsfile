pipeline {
    agent any

    environment {
        APP_NAME    = 'cirt-devops'
        IMAGE_NAME  = 'cirt-devops'
        IMAGE_TAG   = "${BUILD_NUMBER}"
        REGISTRY    = 'cirt-registry.local:5000'
    }

    options {
        timeout(time: 1, unit: 'HOURS')
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source repository...'
                checkout scm
            }
        }

        stage('Build & Unit Tests') {
            steps {
                echo 'Running Maven compile and unit tests...'
                script {
                    if (isUnix()) {
                        sh 'mvn clean test -B'
                    } else {
                        bat 'mvn clean test -B'
                    }
                }
            }
        }

        stage('Package Artifact') {
            steps {
                echo 'Packaging application fat-JAR...'
                script {
                    if (isUnix()) {
                        sh 'mvn package -DskipTests -B'
                    } else {
                        bat 'mvn package -DskipTests -B'
                    }
                }
            }
        }

        stage('Container Image Build') {
            steps {
                echo "Building Docker image: ${IMAGE_NAME}:${IMAGE_TAG}..."
                script {
                    if (isUnix()) {
                        sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -t ${IMAGE_NAME}:latest ."
                    } else {
                        bat "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -t ${IMAGE_NAME}:latest ."
                    }
                }
            }
        }

        stage('Integration & Selenium E2E Tests') {
            steps {
                echo 'Running Selenium End-to-End Integration Tests...'
                script {
                    if (isUnix()) {
                        sh 'mvn verify -Pselenium -Dtest=false -B'
                    } else {
                        bat 'mvn verify -Pselenium -Dtest=false -B'
                    }
                }
            }
        }

        stage('Security & Vulnerability Scan') {
            steps {
                echo 'Performing container image security scan using Trivy...'
                script {
                    if (isUnix()) {
                        sh "docker run --rm aquasec/trivy:latest image --severity HIGH,CRITICAL ${IMAGE_NAME}:${IMAGE_TAG} || true"
                    } else {
                        bat "docker run --rm aquasec/trivy:latest image --severity HIGH,CRITICAL ${IMAGE_NAME}:${IMAGE_TAG} || true"
                    }
                }
            }
        }

        stage('Deploy to Staging Environment') {
            steps {
                echo 'Deploying application to Staging environment using Docker Compose...'
                script {
                    if (isUnix()) {
                        sh 'docker compose down || true'
                        sh 'docker compose up -d'
                    } else {
                        bat 'docker compose down || true'
                        bat 'docker compose up -d'
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Archiving test results and build artifacts...'
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
        }
        success {
            echo "Pipeline SUCCESSFUL for ${APP_NAME} build #${BUILD_NUMBER}!"
        }
        failure {
            echo "Pipeline FAILED for ${APP_NAME} build #${BUILD_NUMBER}!"
        }
    }
}
