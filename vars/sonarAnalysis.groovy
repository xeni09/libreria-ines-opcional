def call(Map config = [:]) {
    def abortPipeline = config.get('abortPipeline', false)
    def abortOnQualityGateFail = config.get('abortOnQualityGateFail', false)
    def branchName = env.BRANCH_NAME

    echo "Ejecución de las pruebas de calidad de código con SonarQube"

    withSonarQubeEnv('Sonar Local') {
        // Ejecutar el análisis con sonar-scanner
        sh '''
        export PATH=$PATH:/opt/homebrew/bin && sonar-scanner \
          -Dsonar.projectKey=threepoints_devops_webserver_ines \
          -Dsonar.projectName="DevOps Web Server Ines" \
          -Dsonar.sources=./src \
          -Dsonar.host.url=http://localhost:9000
        '''

        // Añadir un tiempo de espera para asegurar que el servidor procese el análisis
        sh 'sleep 30'

        // Esperar el resultado del Quality Gate
        timeout(time: 10, unit: 'MINUTES') {
            def qg = waitForQualityGate()
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