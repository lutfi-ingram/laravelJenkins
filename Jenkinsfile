pipeline {
    agent any
    triggers {
        pollSCM '*/1 * * * *'
    }
    // environment {
    //     APPEXIST = 'no'
    // }
    stages {
        stage('Checkout') {
            steps {
                checkout scm: [
                    $class: 'GitSCM',
                    userRemoteConfigs: [[url: "${params.REPOSITORY_URL}"]],
                    branches: [[name: 'refs/heads/master']]
                ], poll: true
            }
        }
        stage('Create App') {
            steps {
                script {
                    openshift.withCluster() {
                        try {
                            openshift.newApp( 'openshift/template.json', "-p", "NAME=${params.APPLICATION}","-p","SOURCE_REPOSITORY_URL=${params.REPOSITORY_URL}"  )
                            sh 'echo no > variable2022_0001.txt'
                        } catch (Exception ex) {
                            println(ex.getMessage())
                            sh 'echo yes > variable2022_0001.txt'
                        }
                    }
                }
            }
        }
        stage('Build') {
            steps {
                script {
                    openshift.withCluster() {
                        def APPEXIST = readFile('variable2022_0001.txt').trim()
                        println(APPEXIST)
                        if (APPEXIST == 'yes'){
                            def bc = openshift.selector( "bc/${params.APPLICATION}" )
                            def result = bc.startBuild()
                            timeout(10) {
                                result.logs('-f')
                            }
                        }
                    }
                }
            }
        }
        stage('Rollout') {
            steps {
                script {
                    def APPEXIST = readFile('variable2022_0001.txt').trim()
                    if (APPEXIST == 'yes'){
                        openshift.withCluster() {
                            def dc = openshift.selector( "dc/${params.APPLICATION}" )
                            try {
                                def rollout = dc.rollout()
                                rollout.latest()
                            } catch (Exception ex) {
                                println(ex.getMessage())
                            }
                        }
                    }
                }
            }
        }
    }
}
