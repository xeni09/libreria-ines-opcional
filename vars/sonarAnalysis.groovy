def call(Map params = [:]) {
    // Obtener los parámetros con valores predeterminados
    boolean abortOnQualityGateFail = params.get('abortOnQualityGateFail', false)
    boolean abortPipeline = params.get('abortPipeline', false)

    timeout(time: 5, unit: 'MINUTES') {
        // Configuración del entorno de SonarQube
        withSonarQubeEnv('Sonar Local') {
            // Ejecuta el análisis de SonarQube
            sh "sonar-scanner \
                -Dsonar.projectKey=ejercicio2-Ines \
                -Dsonar.sources=./src \
                -Dsonar.host.url=http://localhost:9000 \
                -Dsonar.login=admin \
                -Dsonar.password=threepoints"
            
            // Espera el resultado del QualityGate
            def qg = waitForQualityGate()
            if (qg.status != 'OK') {
                echo "Quality Gate status: ${qg.status}"
                if (abortOnQualityGateFail || abortPipeline) {
                    error "Pipeline abortado debido a que no pasó el Quality Gate"
                }
            }
        }
    }
}
