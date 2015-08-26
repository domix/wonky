/*
 *
 * Copyright (C) 2014-2015 the original author or authors.
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
package com.domingosuarez.wonky.service

import org.springframework.context.MessageSource
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by domix on 26/08/15.
 */
class MessageServiceSpec extends Specification {
  @Unroll
  def 'inflect message should works when code is #code, count: #count with result #result'() {
    when:
      MessageSource messageSource = Stub(MessageSource)
      messageSource.getMessage(_, _, _, _) >> result
      MessageService service = new MessageService(messageSource: messageSource)
      def get = service.inflect(code, count)
    then:
      get
      get == result
    where:
      code     | count || result
      'foo'    | 0     || 'foos'
      'demo'   | 1     || 'demo'
      'minion' | 2     || 'minions'
      'minion' | 67    || 'minions'
  }
}
