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
                    }

                    def copyrightOut = sh (script: "cd zookeeper-refdb && find . -type f -name '*.java' -exec awk '!/Copyright \\(C\\) [[:digit:]][[:digit:]][[:digit:]][[:digit:]] The Android Open Source Project/ {print FILENAME} {nextfile}' {} \\;", returnStdout: true)
                    if (copyrightOut.trim()) {
                        def files = copyrightOut.split('\n').collect { it.split(' ').last() }
                        files.each { gerritComment path:it, message: 'Missing Copyright header' }
                    }

                    if (formatOut.trim() || copyrightOut.trim()) {
                        gerritReview labels: [Formatting: -1]
                    } else {
                        gerritReview labels: [Formatting: 1]
                    }
                }
            }
        }
        stage('build') {
            steps {
                gerritReview labels: [Verified: 0], message: "Build started: ${env.BUILD_URL}"
                sh "git clone --recursive -b ${env.GERRIT_BRANCH} https://gerrit.googlesource.com/gerrit"
                sh 'cd gerrit/plugins && ln -sf ../../zookeeper-refdb . && ln -sf zookeeper-refdb/external_plugin_deps.bzl .'
                sh 'cd gerrit && bazelisk build plugins/zookeeper-refdb && bazelisk test plugins/zookeeper-refdb:zookeeper-refdb_tests'
            }
        }
    }
    post {
        success { gerritReview labels: [Verified: 1] }
        unstable { gerritReview labels: [Verified: 0], message: "Build is unstable: ${env.BUILD_URL}" }
        failure { gerritReview labels: [Verified: -1], message: "Build failed: ${env.BUILD_URL}" }
    }
}
