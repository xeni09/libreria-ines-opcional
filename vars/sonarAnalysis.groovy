def call(Map config = [:]) {
    // Parámetros con valores predeterminados
    def abortPipeline = config.get('abortPipeline', false)
    def abortOnQualityGateFail = config.get('abortOnQualityGateFail', false)

    echo "Ejecución de las pruebas de calidad de código"

    // Simular el análisis estático de código
    sh 'echo "Ejecución de las pruebas de calidad de código"'

    // Esperar el resultado del Quality Gate con un timeout de 5 minutos
    timeout(time: 5, unit: 'MINUTES') {
        // Simular el resultado del Quality Gate
        def qg = [status: 'ERROR'] // Cambia a 'OK' para simular éxito
        echo "Quality Gate status: ${qg.status}"

        // Evaluar el resultado del Quality Gate y los parámetros booleanos
        if (qg.status != 'OK') {
            if (abortPipeline || abortOnQualityGateFail) {
                error "Abortando el pipeline debido al error en el Quality Gate"
            } else {
                echo "Continuando con el pipeline a pesar del error en el Quality Gate"
            }
        } else {
            echo "Quality Gate aprobado"
        }
    }
}
