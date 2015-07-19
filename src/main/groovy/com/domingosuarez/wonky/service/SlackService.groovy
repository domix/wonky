package com.domingosuarez.wonky.service

import groovy.json.JsonSlurper
import org.springframework.stereotype.Service
import wslite.rest.RESTClient
import wslite.rest.Response

/**
 * Created by domix on 19/07/15.
 */
@Service
class SlackService {
  Map slack(String token, String host) {
    RESTClient client = new RESTClient("https://${host}.slack.com/api")
    Response response = client.get(path: '/rtm.start', query: [token: token])

    new JsonSlurper().parseText(response.contentAsString)
  }

  Map publicData(String token, String host) {
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
