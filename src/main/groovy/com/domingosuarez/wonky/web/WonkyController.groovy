package com.domingosuarez.wonky.web

import static org.springframework.web.bind.annotation.RequestMethod.GET
import static org.springframework.web.bind.annotation.RequestMethod.POST

import com.domingosuarez.wonky.service.SlackService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 * Created by domix on 19/07/15.
 */
@Slf4j
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
    model.addAttribute('org', slackService.publicData(slackToken, slackHost))

    'index'
  }

  @RequestMapping(method = POST)
  @ResponseBody
  String invite(@RequestBody Map jsonString) {
    slackService.invite(slackToken, slackHost, jsonString.email)
  }
}
