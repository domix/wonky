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

import static org.springframework.context.i18n.LocaleContextHolder.locale

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
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
  @Autowired
  MessageSource messageSource

  Map slack(String token, String host) {
    new RESTClient("https://${host}.slack.com/api")
      .get(path: '/rtm.start', query: [token: token])
      .parsedResponseContent.json
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

  @Cacheable('slackPublicData')
  Map publicData(String token, String host) {
    log.debug 'Searching public data in Slack for {}', host
    Map result = [:]

    if (token && host) {
      result = publicData(slack(token, host))
    } else {
      log.warn 'The token or host for Slack are not set up. Please configure Wonky correctly'
    }

    result
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
