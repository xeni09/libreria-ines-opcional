def call(Map config = [:]) {
    def abortPipeline = config.get('abortPipeline', false)

    echo "Ejecución de las pruebas de calidad de código en la rama: ${env.BRANCH_NAME}"

    withSonarQubeEnv('Sonar Local') {
        try {
            // Define las propiedades de Sonar con la configuración conocida
            sh '''
                sonar-scanner \
                -Dsonar.projectKey=threepoints_devops_webserver_ines \
                -Dsonar.projectName="DevOps Web Server Ines" \
                -Dsonar.sources=./src \
                -Dsonar.host.url=http://localhost:9000 \
                -Dsonar.login=YOUR_SONAR_TOKEN
            '''

            // Esperar el resultado del Quality Gate con timeout
            timeout(time: 5, unit: 'MINUTES') {
                def qualityGate = waitForQualityGate()
                if (qualityGate.status != 'OK') {
                    echo "Quality Gate status: ${qualityGate.status}"
                    if (abortPipeline) {
                        error "Abortando el pipeline debido a la falla en el Quality Gate"
                    } else {
                        echo "Continuando con el pipeline a pesar de la falla."
                    }
                } else {
                    echo "Quality Gate aprobado"
                }
            }
        } catch (Exception e) {
            echo "Error durante la ejecución del análisis de SonarQube: ${e.getMessage()}"
            error "Error en el análisis de SonarQube."
        }
    }
}
