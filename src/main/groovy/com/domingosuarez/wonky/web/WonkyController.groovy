/*
 *
 * Copyright (C) 2014-2016 the original author or authors.
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
package com.domingosuarez.wonky.web

import static java.util.Optional.of
import static org.springframework.web.bind.annotation.RequestMethod.GET
import static org.springframework.web.bind.annotation.RequestMethod.POST

import com.domingosuarez.wonky.service.SlackService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

import javax.servlet.http.HttpServletRequest

/**
 * Created by domix on 19/07/15.
 */
@Slf4j
@Controller
@RequestMapping('/')
class WonkyController {

  @Autowired
  SlackService slackService

  @RequestMapping(method = GET)
  String index(ModelMap model, HttpServletRequest request) {
    String host = getHostname(request)
    log.info 'wonky for {}', host

    of(slackService.slack(host)).filter { !it.isEmpty() }.map {
      model.addAttribute('org', it)
      'index'
    }.orElse('landing')
  }

  @RequestMapping(method = POST)
  @ResponseBody
  Map invite(@RequestBody Map jsonString, HttpServletRequest request) {
    String host = getHostname(request)
    slackService.invite(host, jsonString.email)
  }

  String getHostname(HttpServletRequest request) {
    new URL(request.requestURL.toString()).host
  }
}
