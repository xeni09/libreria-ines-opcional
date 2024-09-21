def call(Map config = [:]) {
    // Parámetros con valores predeterminados
    def abortPipeline = config.get('abortPipeline', false)
    def abortOnQualityGateFail = config.get('abortOnQualityGateFail', false)

    // Usar el entorno de SonarQube configurado en Jenkins
    withSonarQubeEnv('Sonar Local') {
        withCredentials([string(credentialsId: 'NuevoToken', variable: 'SONAR_TOKEN')]) {
            try {
                // Ejecutar el análisis real con SonarQube
                echo "Ejecutando sonar-scanner..."
                sh '''sonar-scanner \
                    -Dsonar.projectKey=threepoints_devops_webserver_ines \
                    -Dsonar.projectName="DevOps Web Server Ines" \
                    -Dsonar.sources=./src \
                    -Dsonar.host.url=http://localhost:9000 \
                    -Dsonar.login=${SONAR_TOKEN}'''
                
                // Añadir un pequeño delay antes de esperar el Quality Gate
                echo "Durmiendo 5 segundos antes de esperar el Quality Gate..."
                sleep(time: 5, unit: 'SECONDS')

                // Esperar el resultado del Quality Gate con timeout de 5 minutos
                timeout(time: 5, unit: 'MINUTES') {
                    def qualityGate = waitForQualityGate()
                    if (qualityGate.status != 'OK') {
                        echo "Quality Gate status: ${qualityGate.status}"

                        // Evaluar si debe abortar el pipeline según los parámetros booleanos
                        if (abortPipeline || abortOnQualityGateFail) {
                            error "Abortando el pipeline debido a la falla en el Quality Gate"
                        } else {
                            echo "Continuando con el pipeline a pesar de la falla en el Quality Gate"
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
}