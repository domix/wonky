package com.domingosuarez.wonky.web

import static org.springframework.web.bind.annotation.RequestMethod.GET

import com.domingosuarez.wonky.service.SlackService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Created by domix on 19/07/15.
 */
@Controller
@RequestMapping('/')
class WonkyController {
  @Value('${slack.token:}')
  String slackToken

  @Value('${slack.host:}')
  String slackHost

  @Autowired
  SlackService slackService

  @RequestMapping(method = GET)
  String index(ModelMap model) {
    Map data = slackService.publicData(slackToken, slackHost)
    model.addAttribute('org', data)

    'index'
  }
}
