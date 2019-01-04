/*
 *
 * Copyright (C) 2014-2019 the original author or authors.
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
package wonky.json

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import wonky.slack.Team

class JacksonUtilSpec extends Specification {
  def foo() {
    given:
      def util = new JacksonUtil(new ObjectMapper())
      Team team = new Team(domain: 'dd.com')
      def json = util.toJson(team)
    expect:
      util.readValue(json, null, Team)
  }

  def bar() {
    when:
      def util = new JacksonUtil(new ObjectMapper())
      util.readValue("ssffs", null, Team)
    then:
      thrown RuntimeException
    when:
      util.readValue("ssffs", "ss", Team)
    then:
      thrown RuntimeException
    when:
      util.toJson(new Object())
    then:
      thrown RuntimeException
  }
}
