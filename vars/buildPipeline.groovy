def call(){
      node{
        tool{
            maven 'maven3.9.4'
        }
        environment{
        SCANNER_HOME= tool 'sonar-scanner'
        }
        stage('SCM') {
            checkout scm
        }
        stage('SonarQube Analysis') {
            def mvn = tool 'Default Maven';
            withSonarQubeEnv('sonarQube-server') {
            sh "${mvn}/bin/mvn clean verify sonar:sonar -Dsonar.projectKey=kiruba-reddy_UserDetails_AYon97PSL8-qgnsNu7aQ"
            }
        }
        stage('build'){
            sh 'docker build -t kirubareddy/user-app:${BUILD_NUMBER} .'
        }
        stage('push'){
            withCredentials([usernamePassword(credentialsId: 'docker-creds', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                sh 'docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD'
                sh 'docker push kirubareddy/user-app:${BUILD_NUMBER}'
            }
        }
    }
}

node {
  stage('SCM') {
    checkout scm
  }
  stage('SonarQube Analysis') {
    def mvn = tool 'Default Maven';
    withSonarQubeEnv() {
      sh "${mvn}/bin/mvn clean verify sonar:sonar -Dsonar.projectKey=kiruba-reddy_UserDetails_AYon97PSL8-qgnsNu7aQ"
    }
  }
}