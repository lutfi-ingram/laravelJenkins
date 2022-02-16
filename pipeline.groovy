pipeline {
    agent any

    stages {
        stage('Hello') {
            steps {
                echo 'Hello World'
            }
        }
    }
    stages {
        stage('Deploy'){
            steps {
                openshift.withCluster( 'devocpcluster' ) {
                    echo "Hello from ${openshift.cluster()}'s default project: ${openshift.project()}"
                
                    // But we can easily change project contexts
                    openshift.withProject( 'another-project' ) {
                        echo "Hello from a non-default project: ${openshift.project()}"
                    }
                
                    // And even scope operations to other clusters within the same script
                    openshift.withCluster( 'myothercluster' ) {
                        echo "Hello from ${openshift.cluster()}'s default project: ${openshift.project()}"
                    }
                }
            }
        }
    }
}
