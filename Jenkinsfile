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
                    userRemoteConfigs: [[url: 'https://github.com/lutfi-ingram/laravelJenkins.git']],
                    branches: [[name: 'refs/heads/master']]
                ], poll: true
            }
        }
        stage('Create App') {
            steps {
                script {
                    openshift.withCluster() {
                        try {
                            def created = openshift.newApp( 'openshift/template.json', "-p", "NAME=${params.APPLICATION}"  )                            
                            sh 'echo no > variable2022_0001.txt'
                        } catch (Exception ex) {
                            println(ex.getMessage())
                            println('writing amazing')
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
                            def bc = openshift.selector( "bc/${params.BC_NAME}" )
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
                    openshift.withCluster() {
                        def dc = openshift.selector( "dc/${params.APPLICATION}" )
                        try {
                            def rollout = dc.rollout()
                            def resultRollout = rollout.latest()
                    } catch (Exception ex) {
                            println(ex.getMessage())
                        }
                    }
                }
            }
        }
    }
}
