pipeline {
    agent any
    options { timestamps () }

    stages {

    stage('Clean') {
        steps {
            echo 'Cleaning..'
        }
    }
        stage('Build') {
            steps {
                echo 'Building..'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
        when {
        branch 'jenkins-dev'
        }
            steps {
            withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'backmanity-conditioner-aws', variable: 'AWS_ACCESS_KEY_ID']]) {
                   echo 'Deploying....'
               }
            }
        }
    }
}
