pipeline {
    options { checkoutToSubdirectory('zookeeper-refdb') }
    agent { label 'bazel-debian' }
    stages {
        stage('Formatting') {
            steps {
                sh "find zookeeper-refdb -name '*.java' | xargs /home/jenkins/format/google-java-format-1.7 -i"
                script {
                    def formatOut = sh (script: 'cd zookeeper-refdb && git status --porcelain', returnStdout: true)
                    if (formatOut.trim()) {
                        def files = formatOut.split('\n').collect { it.split(' ').last() }
                        files.each { gerritComment path:it, message: 'Needs reformatting with GJF' }
                        gerritReview labels: [Formatting: -1]
                    } else {
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
                sh 'printenv | sort'
                dir ('gerrit') {
                    sh 'bazelisk build plugins/zookeeper-refdb'
                    sh 'bazelisk test --test_env DOCKER_HOST=$DOCKER_HOST plugins/zookeeper-refdb:zookeeper-refdb_tests'
                }
            }
        }
    }
    post {
      success { script { if (env.GERRIT_CHANGE_NUMBER) { gerritReview labels: [Verified: 1] } } }
        unstable { script { if (env.GERRIT_CHANGE_NUMBER) { gerritReview labels: [Verified: 0], message: "Build is unstable: ${env.BUILD_URL}" } } }
        failure { script { if (env.GERRIT_CHANGE_NUMBER) { gerritReview labels: [Verified: -1], message: "Build failed: ${env.BUILD_URL}" } } }
    }
}
