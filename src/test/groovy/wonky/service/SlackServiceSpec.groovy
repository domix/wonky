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
package wonky.service

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Ignore
import spock.lang.Specification
import wonky.http.SlackClient
import wonky.json.JacksonUtil

/**
 * Created by domix on 01/06/18.
 */
class SlackServiceSpec extends Specification {

  def foo() {
    given:
      def service = new SlackService(Mock(SlackClient), './src/test/resources/foo.yaml', 100)
      service.load()
    expect:
      service.orgs.size() == 2
  }

  @Ignore
  def foo2() {
    given:
      def token = System.getenv("TOKEN")
      assert token
      def jacksonUtil = new JacksonUtil(objectMapper: new ObjectMapper())
      def service = new SlackService(tenantsFile: './src/test/resources/foo.yaml', jacksonUtil: jacksonUtil)
      def information = service.tenantSlackInformation(token)
    expect:
      information
      information.icon.imageOriginal
      println information.getIcon().getImageOriginal()
  }

  def bar() {
    when:
      def service = new SlackService(Mock(SlackClient), './src/test/resources/notfile.yaml', 100)
      service.load()
    then:
      thrown IllegalStateException
  }
}
