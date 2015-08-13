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

import com.domingosuarez.wonky.config.SlackOrgs
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by domix on 12/08/15.
 */
class SlackServiceSpec extends Specification {

  @Unroll
  def 'should be #result when search organization for "foo" hostname, with orgs #orgs and token: #token'() {
    when:
      SlackService service = new SlackService(slackOrgs: orgs, slackToken: token)
      Optional<SlackOrganization> org = service.getSlackOrg('foo')
    then:
      org.isPresent() == result
    where:
      orgs      | token || result
      null      | null  || false
      fooOrgs() | null  || true
      null      | 'foo' || true
  }

  def fooOrgs() {
    new SlackOrgs(orgs: [new SlackOrganization(wonkyDomain: 'foo')])
  }
}
