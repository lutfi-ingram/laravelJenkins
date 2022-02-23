pipeline {
    agent any
    triggers {
        pollSCM '*/1 * * * *'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm: [ 
                    $class: 'GitSCM',
                    userRemoteConfigs: [[url: 'https://github.com/lutfi-ingram/cakephp-ex.git']],
                    branches: [[name: 'refs/heads/master']]
                ], poll: true
            }
        }
        stage('Create App') {
            steps {
                script {
                    openshift.withCluster() {
                        try {
                            def created = openshift.newApp( 'openshift/templates.json' )
                        } catch (Exception ex) {
                            println(ex.getMessage())
                        }
                    }
                }
            }
        }
        stage('Build') {
            steps {
                script {
                    openshift.withCluster() {
                        def bc = openshift.selector( "bc/${params.BC_NAME}" )
                        def result = bc.startBuild()
                        timeout(10) {
                            result.logs('-f')
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
                            resultRollout.logs('-f')
                    } catch (Exception ex) {
                            println(ex.getMessage())
                        }
                    }
                }
            }
        }
    }
}
