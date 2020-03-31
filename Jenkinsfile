pipeline {
    agent { label 'bazel-debian' }
    stages {
        stage('Formatting') {
            steps {
                sh "git clone -b refs/changes/${env.BRANCH_NAME} https://gerrit.googlesource.com/plugins/zookeeper-refdb"
                sh "find zookeeper-refdb -name '*.java' | xargs /home/jenkins/format/google-java-format-1.7 -i"
                script {
                    def formatOut = sh (script: 'cd zookeeper-refdb && git status --porcelain', returnStdout: true)
                    if (formatOut.trim()) {
                        def files = formatOut.split('\n').collect { it.split(' ').last() }
                        files.each { gerritComment path:it, message: 'Needs reformatting with GJF' }
                        gerritCheck (checks: ['gerritforge:zookeeper-refdb-format-8b1e7fb8ce34448cc425': 'FAILED'], url: "${env.BUILD_URL}console")
                        gerritReview labels: [Formatting: -1]
                    } else {
                        gerritCheck (checks: ['gerritforge:zookeeper-refdb-format-8b1e7fb8ce34448cc425': 'SUCCESSFUL'], url: "${env.BUILD_URL}console")
                        gerritReview labels: [Formatting: 1]
                    }
                }
            }
        }
        stage('build') {
             environment {
                 DOCKER_HOST = """${sh(
                     returnStdout: true,
                     script: "/sbin/ip route|awk '/default/ {print  \"tcp://\"\$3\":2375\"}'"
                 )}"""
            }
            steps {
                gerritReview labels: [Verified: 0], message: "Build started: ${env.BUILD_URL}"
                sh 'git clone --recursive -b $GERRIT_BRANCH https://gerrit.googlesource.com/gerrit'
                sh 'cd gerrit/plugins && ln -sf ../../zookeeper-refdb . && ln -sf zookeeper-refdb/external_plugin_deps.bzl .'
                dir ('gerrit') {
                    sh 'bazelisk build plugins/zookeeper-refdb'
                    sh 'bazelisk test --test_env DOCKER_HOST=$DOCKER_HOST plugins/zookeeper-refdb:zookeeper-refdb_tests'
                }
            }
        }
    }
    post {
        success {
          gerritReview labels: [Verified: 1]
          gerritCheck (checks: ['gerritforge:zookeeper-refdb-8b1e7fb8ce34448cc425': 'SUCCESSFUL'], url: "${env.BUILD_URL}console")
        }
        unstable {
          gerritReview labels: [Verified: 0], message: "Build is unstable: ${env.BUILD_URL}"
          gerritCheck (checks: ['gerritforge:zookeeper-refdb-8b1e7fb8ce34448cc425': 'FAILED'], url: "${env.BUILD_URL}console")
        }
        failure {
          gerritReview labels: [Verified: -1], message: "Build failed: ${env.BUILD_URL}"
          gerritCheck (checks: ['gerritforge:zookeeper-refdb-8b1e7fb8ce34448cc425': 'FAILED'], url: "${env.BUILD_URL}console")
        }
    }
}
