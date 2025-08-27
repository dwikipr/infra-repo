pipeline {
    agent any
    stages {
        stage('Backend Deployment') {
            steps {
                script { load "jenkins/backend-pipeline.groovy" }
            }
        }
        stage('Frontend Deployment') {
            steps {
                script { load "jenkins/frontend-pipeline.groovy" }
            }
        }
    }
}
