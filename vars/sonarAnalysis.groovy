def call(Map config = [:]) {
    // Obtener los parámetros pasados desde el pipeline
    def abortPipeline = config.get('abortPipeline', false)
    def branchName = config.get('branchName', env.BRANCH_NAME ?: 'unknown')

    // Verificar si la variable de entorno BRANCH_NAME está disponible
    if (branchName == 'unknown') {
        error "El nombre de la rama no se pudo capturar."
    }

    echo "Ejecución de las pruebas de calidad de código en la rama: ${branchName}"

    withSonarQubeEnv('SonarQube') {
        // Ejecutar el análisis real con SonarQube
        sh 'sonar-scanner'

        // Esperar el resultado del Quality Gate con timeout de 5 minutos
        timeout(time: 5, unit: 'MINUTES') {
            def qualityGate = waitForQualityGate()
            if (qualityGate.status != 'OK') {
                echo "Quality Gate status: ${qualityGate.status}"

                // Evaluar si debe abortar el pipeline según la heurística
                if (abortPipeline || shouldAbort(branchName)) {
                    error "Abortando el pipeline debido a la falla en el Quality Gate en la rama: ${branchName}"
                } else {
                    echo "Continuando con el pipeline a pesar de la falla."
                }
            } else {
                echo "Quality Gate aprobado"
            }
        }
    }
}

// Función auxiliar para decidir si abortar el pipeline según el nombre de la rama
def shouldAbort(String branchName) {
    if (branchName == 'master' || branchName.startsWith('hotfix')) {
        return true
    }
    return false
}
