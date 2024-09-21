def call(Map config = [:]) {
    // Parámetros con valores predeterminados
    def abortPipeline = config.get('abortPipeline', false)
    def branchName = config.get('branchName', env.BRANCH_NAME ?: 'main') // Por defecto 'main'

    echo "Ejecución de las pruebas de calidad de código en la rama: ${branchName}"

    // Usar el entorno de SonarQube configurado en Jenkins
    withSonarQubeEnv('Sonar Local') {
        try {
            // Obtener el token de SonarQube desde las credenciales globales de Jenkins
            withCredentials([string(credentialsId: 'SonarQube', variable: 'SONAR_TOKEN')]) {
                // Ejecutar el análisis real con SonarQube
                echo "Ejecutando sonar-scanner..."
                sh """
                    sonar-scanner \
                    -Dsonar.projectKey=threepoints_devops_webserver_ines \
                    -Dsonar.projectName="DevOps Web Server Ines" \
                    -Dsonar.sources=./src \
                    -Dsonar.host.url=http://localhost:9000 \
                    -Dsonar.login=$SONAR_TOKEN
                """
            }

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
        } catch (Exception e) {
            echo "Error durante la ejecución del análisis de SonarQube: ${e.getMessage()}"
            error "Error en el análisis de SonarQube."
        }
    }
}

// Función auxiliar para decidir si abortar el pipeline según el nombre de la rama
def shouldAbort(String branchName) {
    if (branchName == 'main' || branchName.startsWith('hotfix')) {
        return true
    }
    return false
}
