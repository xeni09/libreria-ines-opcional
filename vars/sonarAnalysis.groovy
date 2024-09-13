def call(Map params = [:]) {
    boolean abortOnQualityGateFail = params.get('abortOnQualityGateFail', false)
    boolean abortPipeline = params.get('abortPipeline', false)

    timeout(time: 5, unit: 'MINUTES') {
        // Configuración del entorno de SonarQube
        withSonarQubeEnv('Sonar Local') {
            // Usar el SonarQube Scanner configurado en Jenkins
            def scannerHome = tool 'sonar-scanner'
            withCredentials([usernamePassword(credentialsId: 'Credentials_Threepoints', usernameVariable: 'SONAR_USER', passwordVariable: 'SONAR_PASS')]) {
                sh "${scannerHome}/bin/sonar-scanner \
                    -Dsonar.projectKey=ejercicio2-Ines \
                    -Dsonar.sources=./src \
                    -Dsonar.host.url=http://localhost:9000 \
                    -Dsonar.login=${SONAR_USER} \
                    -Dsonar.password=${SONAR_PASS}"
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
