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

import static java.util.Collections.emptyList
import static java.util.Collections.emptyMap
import static java.util.Optional.of
import static java.util.Optional.ofNullable
import static org.springframework.context.i18n.LocaleContextHolder.locale

import com.domingosuarez.wonky.config.SlackOrgs
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import wslite.rest.RESTClient

/**
 * Created by domix on 19/07/15.
 */
@Service
@Slf4j
class SlackService {
  @Value('${slack.token:}')
  String slackToken

  @Value('${slack.host:}')
  String slackHost

  @Autowired
  MessageSource messageSource

  @Autowired
  SlackOrgs slackOrgs

  Optional<SlackOrganization> getSlackOrg(String hostname) {
    orgs.stream().filter { it.wonkyDomain == hostname }.findFirst().map { of(it) }.orElseGet {
      ofNullable(slackToken).filter { !it.trim().isEmpty() }
        .map { new SlackOrganization(teamDomain: slackHost, token: slackToken) }
    }
  }

  List<SlackOrganization> getOrgs() {
    ofNullable(slackOrgs).map { it.orgs }.orElse(emptyList())
  }

  @Cacheable('slackPublicData')
  Map slack(String hostname) {
    getSlackOrg(hostname)
      .map { publicData(it.token, it.teamDomain) }
      .orElse(emptyMap())
  }

  Map slack(String token, String host) {
    new RESTClient("https://${host}.slack.com/api")
      .get(path: '/rtm.start', query: [token: token])
      .parsedResponseContent.json
  }

  Map invite(String hostname, String email) {
    getSlackOrg(hostname)
      .map { invite(it.token, it.teamDomain, email) }
      .orElse { emptyMap() }
  }

  Map invite(String token, String host, String email) {
    Map response = new RESTClient("https://${host}.slack.com/api/users.admin.invite").post {
      charset 'UTF-8'
      urlenc token: token, email: email
    }.parsedResponseContent.json

    if (response.ok) {
      response.message = messageSource.getMessage('invite.success', [].toArray(), 'WOOT. Check your email!', locale)
    } else {
      response.error = messageSource.getMessage(response.error, [].toArray(), response.error, locale)
    }

    response
  }

  Map publicData(String token, String host) {
    log.info 'Searching public data in Slack for {}', host
    publicData(slack(token, host))
  }

  Map publicData(Map data) {
    int active = data.users.findAll {
      it.id != 'USLACKBOT' && it.presence == 'active'
    }.size()

    [
      name : data.team.name,
      logo : data.team.icon.image_132,
      users: [
        active: active,
        total : data.users.size()
      ]
    ]
  }

}
