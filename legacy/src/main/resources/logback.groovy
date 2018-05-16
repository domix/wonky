/*
 *
 * Copyright (C) 2014-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

logger 'com.domingosuarez.wonky', WARN
logger 'org.apache.tomcat', ERROR
logger 'org.apache.coyote', ERROR
logger 'org.apache.catalina', ERROR
logger 'org.apache.jasper', ERROR
