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
}
