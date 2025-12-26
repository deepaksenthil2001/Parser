pipeline {
    agent any

    tools {
        jdk 'JDK17'
        nodejs 'Node18'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/deepaksenthil2001/Parser.git'
            }
        }

        stage('Backend Build') {
            steps {
                dir('Backend') {
                    bat 'mvn clean install'
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
            echo '✅ Build Successful'
        }
        failure {
            echo '❌ Build Failed'
        }
    }
}
