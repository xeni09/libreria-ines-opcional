def call(Map config = [:]) {
    // Obtener los parámetros pasados desde el pipeline
    def abortPipeline = config.get('abortPipeline', false)
    def abortOnQualityGateFail = config.get('abortOnQualityGateFail', false)
    def branchName = config.get('branchName', env.BRANCH_NAME ?: 'unknown')

    // Verificar si la variable de entorno BRANCH_NAME está disponible
    if (branchName == 'unknown') {
        error "El nombre de la rama no se pudo capturar."
    }

    echo "Ejecución de las pruebas de calidad de código con SonarQube en la rama: ${branchName}"

    withSonarQubeEnv('Sonar Local') {
        // Ejecutar el análisis con sonar-scanner (puedes cambiar a sonar-scanner real si lo deseas)
        sh 'echo "Ejecución de las pruebas de calidad de código"'

        // Esperar el resultado del Quality Gate con un timeout de 5 minutos
        timeout(time: 5, unit: 'MINUTES') {
            // Simular el resultado del Quality Gate (puedes cambiar a waitForQualityGate() si es necesario)
            def qg = [status: 'ERROR'] // Cambia a 'OK' para simular un fallo

            if (qg.status != 'OK') {
                echo "Quality Gate status: ${qg.status}"

                // Evaluar si debe abortar el pipeline según la heurística
                if (abortPipeline || abortOnQualityGateFail || shouldAbort(branchName)) {
                    error "Abortando el pipeline debido a la falla en el Quality Gate en la rama: ${branchName}"
                } else {
                    echo "Continuando con el pipeline a pesar de la falla en el Quality Gate en la rama: ${branchName}"
                }
            } else {
                echo "Quality Gate aprobado"
            }
        }
    }
}

// Función auxiliar para decidir si abortar el pipeline según el nombre de la rama
def shouldAbort(String branchName) {
    // Si la rama es 'master' o empieza con 'hotfix', abortar el pipeline
    if (branchName == 'master' || branchName.startsWith('hotfix')) {
        return true
    }
    // Para cualquier otra rama, no abortar
    return false
}
