def call(){
      node{
        stage('checkout'){
            git branch:'master', url:'https://github.com/kiruba-reddy/UserDetails.git', changelog: false, poll: false
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