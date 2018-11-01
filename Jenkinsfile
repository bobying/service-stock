#!/usr/bin/env groovy

node {
    stage('checkout') {
        checkout scm
    }

    stage('check java') {
        sh "java -version"
    }

    stage('clean') {
        sh "chmod +x mvnw"
        sh "./mvnw clean"
    }

    stage('packaging') {
        sh "./mvnw verify -Pprod -DskipTests"
        archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
    }

    def dockerImage
    stage('build docker') {
        sh "cp -R src/main/docker target/"
        sh "cp target/*.war target/docker/"
        dockerImage = docker.build('cloud/stock', 'target/docker')
    }

    stage('publish docker') {
    
        withCredentials([usernamePassword( credentialsId: 'docker-login', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
	        docker.withRegistry('https://docker.eyun.online:9082', 'docker-login') {
	            sh "docker login -u ${USERNAME} -p ${PASSWORD} https://docker.eyun.online:9082"
	            dockerImage.push 'latest'
	        }
        }
    }

}
