def call(params) {
    pipeline {
        agent any
        parameters {
            choice(name: 'APP_NAME', choices: ['dashboard-app'], description: 'Which frontend app to deploy')
            string(name: 'BRANCH', defaultValue: 'main', description: 'Branch to deploy')
            choice(name: 'ENVIRONMENT', choices: ['dev', 'staging', 'prod'], description: 'Target environment')
        }
        environment {
            AWS_ACCOUNT_ID = "123456789012"
            AWS_REGION = "ap-southeast-1"
            IMAGE_TAG = "${env.BUILD_NUMBER}"
        }
        stages {
            stage('Checkout') {
                steps {
                    git branch: "${params.BRANCH}", url: "git@github.com:dwikipr/${params.APP_NAME}.git"
                }
            }

            stage('Build & Push Image') {
                steps {
                    sh """
                    aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com
                    docker build -t $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/${params.APP_NAME}:$IMAGE_TAG .
                    docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/${params.APP_NAME}:$IMAGE_TAG
                    """
                }
            }

            stage('Deploy') {
                steps {
                    withKubeConfig([credentialsId: 'eks-kubeconfig']) {
                        sh """
                        helm upgrade --install ${params.APP_NAME} helm-charts/${params.APP_NAME} \
                          --namespace ${params.ENVIRONMENT} \
                          --set image.repository=$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/${params.APP_NAME} \
                          --set image.tag=$IMAGE_TAG
                        """
                    }
                }
            }
        }
    }
}
