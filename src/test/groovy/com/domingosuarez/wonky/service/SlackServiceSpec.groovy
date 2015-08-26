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

import static java.util.Collections.emptyMap

import com.domingosuarez.wonky.config.SlackOrgs
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by domix on 12/08/15.
 */
class SlackServiceSpec extends Specification {

  static final Map<String, Serializable> SLACK_EXPECTED_DATA = [name: 'foo', logo: 'foo', users: [active: 1, total: 2]]
  static final Map<String, Boolean> INVITE_RESPONSE = [ok: true, message: null]
  static final Map<String, Boolean> OK_SLACK_RESPONSE = [ok: true]

  @Unroll
  def 'should be #result when search organization for "foo" hostname, with orgs #orgs and token: "#token"'() {
    when:
      SlackService service = new SlackService(slackOrgs: orgs, slackToken: token)
      Optional<SlackOrganization> org = service.findTenant('foo')
    then:
      org.isPresent() == result
    where:
      orgs            | token || result
      null            | null  || false
      fooOrgs()       | null  || true
      fooOrgs()       | 'foo' || true
      null            | 'foo' || true
      null            | ' '   || false
      new SlackOrgs() | null  || false
  }

  @Unroll
  def 'should get slack data with #result when the orgs are #orgs and token is "#token"'() {
    when:
      def remoteService = Stub(RemoteService)
      remoteService.get(_, _) >> [
        users: [
          [id: 'USLACKBOT', presence: 'active'],
          [id: 'domix', presence: 'inactive'],
          [id: 'iamedu', presence: 'active'],
        ],
        team : [
          name: 'foo',
          icon: [image_132: 'foo']
        ]
      ]
      SlackService service = new SlackService(slackOrgs: orgs, slackToken: token, remoteService: remoteService)
      def slack = service.slack('foo')
    then:
      slack == result
    where:
      orgs            | token || result
      null            | null  || emptyMap()
      fooOrgs()       | null  || SLACK_EXPECTED_DATA
      fooOrgs()       | 'foo' || SLACK_EXPECTED_DATA
      null            | 'foo' || SLACK_EXPECTED_DATA
      null            | ' '   || emptyMap()
      new SlackOrgs() | null  || emptyMap()
  }

  @Unroll
  def 'should invite a slacker when orgs are #orgs and token is "#token" with result #result '() {
    when:
      def messageSource = Mock(MessageService)

      def remoteService = new TestRemoteService(slackResponse)

      SlackService service = new SlackService(
        slackOrgs: orgs, slackToken: token, remoteService: remoteService, messageSource: messageSource)
      def slack = service.invite('foo', 'domingo.suarez@gmail.com')
    then:
      slack == result
    where:
      orgs            | token | slackResponse            || result
      null            | null  | OK_SLACK_RESPONSE        || emptyMap()
      fooOrgs()       | null  | OK_SLACK_RESPONSE        || INVITE_RESPONSE
      fooOrgs()       | 'foo' | OK_SLACK_RESPONSE        || INVITE_RESPONSE
      null            | 'foo' | OK_SLACK_RESPONSE        || INVITE_RESPONSE
      null            | ' '   | OK_SLACK_RESPONSE        || emptyMap()
      new SlackOrgs() | null  | OK_SLACK_RESPONSE        || emptyMap()
      fooOrgs()       | 'foo' | [error: 'invalid_email'] || [error: null]
  }

  static SlackOrgs fooOrgs() {
    new SlackOrgs(orgs: [new SlackOrganization(wonkyDomain: 'foo')])
  }
}
