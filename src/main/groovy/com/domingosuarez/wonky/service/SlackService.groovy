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

import java.util.function.Predicate

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

  @Autowired
  RemoteService remoteService

  Optional<SlackOrganization> findTenant(String hostname) {
    tenants().stream().filter { it.wonkyDomain == hostname }.findFirst().map { of(it) }.orElseGet {
      ofNullable(slackToken).filter { !it.trim().isEmpty() }
        .map { new SlackOrganization(teamDomain: slackHost, token: slackToken) }
    }
  }

  List<SlackOrganization> tenants() {
    ofNullable(slackOrgs).map { it.orgs }.orElse(emptyList())
  }

  @Cacheable('slackPublicData')
  Map slack(String hostname) {
    findTenant(hostname)
      .map { publicData(it.token, it.teamDomain) }
      .orElse(emptyMap())
  }

  Map invite(String hostname, String email) {
    findTenant(hostname)
      .map { invite(it.token, it.teamDomain, email) }
      .orElse(emptyMap())
  }

  Map tenantSlackInformation(String token, String host) {
    Map request = [path: '/rtm.start', query: [token: token]]
    remoteService.get("https://${host}.slack.com/api", request)
  }

  Map invite(String token, String host, String email) {
    Map response = remoteService.post("https://${host}.slack.com/api/users.admin.invite") {
      charset 'UTF-8'
      urlenc token: token, email: email
    }

    response + ofNullable(response.ok).filter { it == true }.map {
      [message: messageSource.getMessage('invite.success', [].toArray(), 'WOOT. Check your email!', locale)]
    }.orElseGet {
      [error: messageSource.getMessage(response.error, [].toArray(), response.error, locale)]
    }
  }

  Map publicData(String token, String host) {
    log.info 'Searching public data in Slack for {}', host

    Map map = tenantSlackInformation(token, host)
    publicData(map)
  }

  private Predicate isSlackBot = { Map map ->
    'USLACKBOT' == map.id
  }

  private Predicate isActive = { Map map ->
    'active' == map.presence
  }

  Boolean isActiveUser(Map map) {
    isSlackBot.negate().and(isActive).test(map)
  }

  Map publicData(Map data) {
    int active = data.users.findAll {
      isActiveUser(it)
    }.size()

    int total = data.users.findAll {
      isSlackBot.negate().test(it)
    }.size()

    [
      name : data.team.name,
      logo : data.team.icon.image_132,
      users: [
        active: active,
        total : total
      ]
    ]
  }

}
