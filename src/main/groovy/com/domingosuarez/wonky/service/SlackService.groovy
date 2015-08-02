package com.domingosuarez.wonky.service

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import wslite.rest.RESTClient
import wslite.rest.Response

/**
 * Created by domix on 19/07/15.
 */
@Service
@Slf4j
class SlackService {
  Map slack(String token, String host) {
    RESTClient client = new RESTClient("https://${host}.slack.com/api")
    Response response = client.get(path: '/rtm.start', query: [token: token])

    new JsonSlurper().parseText(response.contentAsString)
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
