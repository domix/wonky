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

import static java.util.Optional.of
import static org.springframework.context.i18n.LocaleContextHolder.locale

import com.domingosuarez.boot.autoconfigure.jade4j.JadeHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource

/**
 * Created by domix on 26/08/15.
 */
@JadeHelper('message')
class MessageService {
  @Autowired
  MessageSource messageSource

  String get(String code, Object[] params) {
    messageSource.getMessage(code, params, code, locale)
  }

  String get(String code) {
    get(code, null)
  }

  String inflect(String code, Integer count) {

    String messagecode = of(count).filter { it != 0 }.map {
      "${code}s"
    }.orElse(code)

    get(messagecode)
  }
}
