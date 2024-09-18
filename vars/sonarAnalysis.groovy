def call(Map config = [:]) {
    def abortPipeline = config.get('abortPipeline', false)
    def abortOnQualityGateFail = config.get('abortOnQualityGateFail', false)
    def branchName = env.BRANCH_NAME

    echo "Ejecución de las pruebas de calidad de código con SonarQube en la rama ${branchName}"

    withSonarQubeEnv('Sonar Local') {
        // Ejecutar el análisis con sonar-scanner (o un echo en su lugar)
        sh 'echo "Ejecución de las pruebas de calidad de código"'

        // Esperar el resultado del Quality Gate
        timeout(time: 5, unit: 'MINUTES') {
            // Simular el resultado del Quality Gate
            def qg = [status: 'OK'] // Cambiar a 'ERROR' para simular fallo
            if (qg.status != 'OK') {
                echo "Quality Gate status: ${qg.status}"
                if (abortPipeline || abortOnQualityGateFail || branchName == 'master' || branchName.startsWith('hotfix')) {
                    error "Abortando el pipeline debido a la falla en el Quality Gate"
                } else {
                    echo "Continuando con el pipeline a pesar de la falla en el Quality Gate"
                }
            } else {
                echo "Quality Gate aprobado"
            }
        }
    }
}