def call(Map config = [:]) {
    def abortPipeline = config.get('abortPipeline', false)
    def abortOnQualityGateFail = config.get('abortOnQualityGateFail', false)

    // Invocar SonarQube análisis
    echo "Ejecución de las pruebas de calidad de código"

    withSonarQubeEnv('Sonar Local') {
        // Ejecutar análisis de SonarQube real
        sh '''
        sonar-scanner \
          -Dsonar.projectKey=threepoints_devops_webserver_ines \
          -Dsonar.projectName="DevOps Web Server Ines" \
          -Dsonar.sources=./src \
          -Dsonar.host.url=http://localhost:9000
        '''
        
        // Agregar un retardo si es necesario
        sh 'sleep 10'

        // Esperar el resultado del Quality Gate
        timeout(time: 5, unit: 'MINUTES') {
            def qg = waitForQualityGate()
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
