package com.domingosuarez.wonky.web

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
class WonkyController {
  @Value('${default.token: }')
  String defaultToken

  @Value('${default.host: }')
  String defaultHost

  @Autowired
  SlackService slackService

  @RequestMapping('/')
  String index(ModelMap model) {
    Map data = slackService.publicData(defaultToken, defaultHost)
    model.addAttribute('slack', data)

    'index'
  }
}
