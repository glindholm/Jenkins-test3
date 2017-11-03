pipeline {
    agent { docker 'maven:3.3.3' }
    stages {
        stage('build') {
            steps {
				sh 'echo Test3'
                sh 'mvn --version'
				sh 'echo $PATH'
            }
        }
    }
}