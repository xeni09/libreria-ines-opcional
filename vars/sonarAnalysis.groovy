def call(Map config = [:]) {
    // Parámetros con valores predeterminados
    def abortPipeline = config.get('abortPipeline', false)

    echo "Ejecución de las pruebas de calidad de código en la rama: ${env.BRANCH_NAME}"

    withSonarQubeEnv('Sonar Local') {
        try {
            // Ejecutar el análisis real con SonarQube
            echo "Ejecutando sonar-scanner..."
            sh "sonar-scanner -Dsonar.projectKey=threepoints_devops_webserver_ines -Dsonar.projectName='DevOps Web Server Ines' -Dsonar.sources=./src -Dsonar.host.url=http://localhost:9000 -Dsonar.login=\${SONAR_TOKEN}"

            // Esperar el resultado del Quality Gate con timeout de 5 minutos
            timeout(time: 5, unit: 'MINUTES') {
                def qualityGate = waitForQualityGate()
                if (qualityGate.status != 'OK') {
                    if (abortPipeline) {
                        error "Abortando el pipeline debido a la falla en el Quality Gate"
                    } else {
                        echo "Quality Gate fallido, pero el pipeline continúa."
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
