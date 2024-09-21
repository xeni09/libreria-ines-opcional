def call(Map config = [:]) {
    // Parámetros con valores predeterminados
    def abortPipeline = config.get('abortPipeline', false)
    def branchName = 'main'  // Hardcoded branch name

    echo "Ejecución de las pruebas de calidad de código en la rama: ${branchName}"

    // Usar el entorno de SonarQube configurado en Jenkins
    withSonarQubeEnv('Sonar Local') {
        try {
            // Ejecutar el análisis real con SonarQube
            echo "Ejecutando sonar-scanner..."
            sh 'sonar-scanner'

            // Añadir sleep para dar tiempo al análisis
            sh 'sleep 10'  // Sleep por 10 segundos (ajustable según las necesidades)

            // Esperar el resultado del Quality Gate dentro del mismo entorno de SonarQube
            timeout(time: 5, unit: 'MINUTES') {
                def qualityGate = waitForQualityGate()
                if (qualityGate.status != 'OK') {
                    echo "Quality Gate status: ${qualityGate.status}"

                    // Evaluar si debe abortar el pipeline según la heurística
                    if (abortPipeline) {
                        error "Abortando el pipeline debido a la falla en el Quality Gate en la rama: ${branchName}"
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
