pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/deepaksenthil2001/Parser.git'
            }
        }

        stage('Check Versions') {
            steps {
                bat 'java -version'
                bat 'node -v'
                bat 'npm -v'
                bat 'mvn -v'
            }
        }

        stage('Backend Build (Skip Tests)') {
            steps {
                dir('Backend') {
                    bat 'mvn clean install -DskipTests'
                }
            }
        }

        stage('Frontend Build') {
            steps {
                dir('Frontend') {
                    bat 'npm install'
                    bat 'npm run build'
                }
            }
        }
    }

    post {
        success {
            echo '✅ CI Pipeline Completed Successfully'
        }
        failure {
            echo '❌ CI Pipeline Failed'
        }
    }
}
