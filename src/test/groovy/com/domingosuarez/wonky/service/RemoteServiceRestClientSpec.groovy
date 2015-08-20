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

import static com.github.kristofa.test.http.MediaType.APPLICATION_JSON_UTF8
import static com.github.kristofa.test.http.Method.GET
import static java.util.Collections.emptyMap

import com.github.kristofa.test.http.MockHttpServer
import com.github.kristofa.test.http.SimpleHttpResponseProvider
import spock.lang.Specification

/**
 * Created by domix on 20/08/15.
 */
class RemoteServiceRestClientSpec extends Specification {

  def foo() {
    when:
      RemoteServiceRestClient client = new RemoteServiceRestClient()

      def responseProvider = new SimpleHttpResponseProvider()
      responseProvider.expect(GET, '/').respondWith(200, APPLICATION_JSON_UTF8.value, '{}')

      def port = 3321
      def server = new MockHttpServer(port, responseProvider)
      server.start()

      def url = "http://localhost:$port/"
      def response = client.get(url, emptyMap())
      server.stop()
    then:
      response == emptyMap()
      /*when:
        responseProvider = new SimpleHttpResponseProvider()
        responseProvider.expect(POST, '/').respondWith(200, APPLICATION_JSON_UTF8.value, '{}')
        server = new MockHttpServer(port, responseProvider)
        server.start()
        response = client.post(url) {
          charset 'UTF-8'
          urlenc token: 'sd'
        }
        server.stop()
      then:
        response == emptyMap()*/
  }
}
