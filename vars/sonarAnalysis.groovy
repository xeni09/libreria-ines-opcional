def call(Map config = [:]) {
    def abortPipeline = config.get('abortPipeline', false)
    def abortOnQualityGateFail = config.get('abortOnQualityGateFail', false)

    // Invocar SonarQube análisis
    echo "Ejecución de las pruebas de calidad de código"

    // Realizar el análisis de SonarQube
    withSonarQubeEnv('Sonar Local') {
        // Simulación del análisis o reemplazo por comando real de SonarQube
        sh 'echo "Realizando análisis de SonarQube..."'
        
        // Agregar un retardo para asegurar que el análisis sea procesado antes de verificar el Quality Gate
        sh 'sleep 10'

        // Timeout para el Quality Gate
        timeout(time: 5, unit: 'MINUTES') {
            def qg = waitForQualityGate()

            // Evaluar el resultado del Quality Gate
            if (qg.status != 'OK') {
                echo "Quality Gate status: ${qg.status}"
                if (abortPipeline || abortOnQualityGateFail) {
                    error "Abortando el pipeline debido a la falla en Quality Gate"
                } else {
                    echo "Continuando con el pipeline a pesar de la falla en Quality Gate"
                }
            } else {
                echo "Quality Gate aprobado"
            }
        }
    }
}
