job('seed') {
    scm {
        github('visualphoenix/job-dsl-gradle-example','master')
    }
    triggers {
      hudsonStartupTrigger {
        label('master')
        quietPeriod(null)
        nodeParameterName(null)
      }
    }
    steps {
        gradle 'clean test'
        dsl {
            external 'jobs/**/*Jobs.groovy'
            additionalClasspath 'src/main/groovy'
        }
    }
    publishers {
        archiveJunit 'build/test-results/**/*.xml'
    }
}
