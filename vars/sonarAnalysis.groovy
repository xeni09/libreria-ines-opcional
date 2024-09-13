def call(Map params = [:]) {
    boolean abortOnQualityGateFail = params.get('abortOnQualityGateFail', false)
    boolean abortPipeline = params.get('abortPipeline', false)

    timeout(time: 5, unit: 'MINUTES') {
        // Configuración del entorno de SonarQube
        withSonarQubeEnv('Sonar Local') {
            def scannerHome = tool 'sonar-scanner'
            // Usar el token almacenado en Jenkins
            withCredentials([string(credentialsId: 'jenkinsToken', variable: 'SONAR_TOKEN')]) {
                sh "${scannerHome}/bin/sonar-scanner \
                    -Dsonar.projectKey=ejercicio2-Ines \
                    -Dsonar.sources=./src \
                    -Dsonar.host.url=http://localhost:9000 \
                    -Dsonar.login=${SONAR_TOKEN}"
            }

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
