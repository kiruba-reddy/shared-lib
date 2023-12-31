def call(){
      node{
        environment{
        SCANNER_HOME= tool 'sonar-scanner'
        }
        stage('SCM') {
            checkout scm
        }
        stage('SonarQube Analysis') {
            def mvn = tool 'maven';
            withSonarQubeEnv('sonarQube-server') {
                sh "${mvn}/bin/mvn install -Dmaven.test.skip=true"
                sh "${mvn}/bin/mvn sonar:sonar -Dsonar.projectKey=kiruba-reddy_UserDetails_AYon97PSL8-qgnsNu7aQ"
            }
        }
        stage("Quality Gate"){
          timeout(time: 1, unit: 'HOURS') {
              def qg = waitForQualityGate(webhookSecretId: 'sonar-token')
              print "the quality gate ${qg}"
              if (qg.status != 'OK') {
                  error "Pipeline aborted due to quality gate failure: ${qg.status}"
              }
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