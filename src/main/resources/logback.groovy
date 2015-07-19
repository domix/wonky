scan '30 seconds'

String encoderPattern = '%-12date{HH:mm:ss} %-5level %logger{35} - %msg%n'
String logDir = "build/logs"
String logFileName = 'wonky'
jmxConfigurator()

appender('CONSOLE', ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = encoderPattern
  }
}

appender('FILE', RollingFileAppender) {
  file = "${logDir}/${logFileName}.log"
  append = true
  rollingPolicy(TimeBasedRollingPolicy) {
    fileNamePattern = "${logDir}/${logFileName}-%d{yyyy-MM-dd_HH}.log"
    maxHistory = 7
  }
  encoder(PatternLayoutEncoder) {
    pattern = encoderPattern
  }
}

root ERROR, ['CONSOLE', 'FILE']
//root loggingLevel, appenders

logger 'com.domingosuarez.wonky', DEBUG
logger 'org.apache.tomcat', ERROR
logger 'org.apache.coyote', ERROR
logger 'org.apache.catalina', ERROR
logger 'org.apache.jasper', ERROR

