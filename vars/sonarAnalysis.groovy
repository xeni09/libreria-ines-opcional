def call(Map config = [:]) {
    def abortPipeline = config.get('abortPipeline', false)
    def abortOnQualityGateFail = config.get('abortOnQualityGateFail', false)
    
    // Obtener el nombre de la rama desde las variables de entorno
    def branchName = env.BRANCH_NAME ?: 'unknown'

    echo "Ejecución de las pruebas de calidad de código con SonarQube en la rama: ${branchName}"

    withSonarQubeEnv('Sonar Local') {
        // Ejecutar el análisis con sonar-scanner (o un echo en su lugar)
        sh 'echo "Ejecución de las pruebas de calidad de código"'

        // Esperar el resultado del Quality Gate con un timeout de 5 minutos
        timeout(time: 5, unit: 'MINUTES') {
            // Simular el resultado del Quality Gate
            def qg = [status: 'OK'] // Cambiar a 'ERROR' para simular fallo

            // Verificar el estado del Quality Gate
            if (qg.status != 'OK') {
                echo "Quality Gate status: ${qg.status}"

                // Evaluar heurística para decidir si abortar
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
