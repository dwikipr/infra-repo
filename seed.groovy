// seed.groovy — generates two pipeline jobs from groovy files

pipelineJob('backend-deployment') {
    definition {
        cps {
            script(readFileFromWorkspace('jenkins/backend-pipeline.groovy'))
            sandbox()
        }
    }
}

pipelineJob('frontend-deployment') {
    definition {
        cps {
            script(readFileFromWorkspace('jenkins/frontend-pipeline.groovy'))
            sandbox()
        }
    }
}
