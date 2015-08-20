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
package com.domingosuarez.wonky.web

import static groovy.json.JsonOutput.toJson
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

import com.domingosuarez.wonky.service.SlackService
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by domix on 20/08/15.
 */
class WonkyControllerSpec extends Specification {

  @Unroll
  def 'should get the #view page when the model from Slack is #slackData'() {
    when:
      SlackService slackService = Stub(SlackService)
      slackService.slack(_) >> slackData

      def mockMvc = standaloneSetup(new WonkyController(slackService: slackService)).build()
      def perform = mockMvc.perform(get('/'))
    then:
      perform.andExpect(status().isOk()).andExpect(view().name(view))
    where:
      slackData    || view
      [:]          || 'landing'
      [foo: 'bar'] || 'index'
  }

  @Unroll
  def 'should invite a slacker'() {
    when:
      SlackService slackService = Stub(SlackService)
      slackService.invite(_, _) >> Collections.emptyMap()

      def mockMvc = standaloneSetup(new WonkyController(slackService: slackService)).build()
      def perform = mockMvc.perform(post('/')
        .accept(APPLICATION_JSON).contentType(APPLICATION_JSON)
        .content(toJson([email: 'foo@bar'])))
    then:
      perform.andExpect(status().isOk())
  }
}
