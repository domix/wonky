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
package com.domingosuarez.wonky.service

import groovy.util.logging.Slf4j

/**
 * Created by domix on 20/08/15.
 */
@Slf4j
class TestRemoteService implements RemoteService {
  private final Map response

  TestRemoteService(Map response) {

    this.response = response
  }

  @Override
  Map get(String url, Map request) {
    response
  }

  @Override
  Map post(String url, Closure content) {
    Closure c = content.clone()
    c.resolveStrategy = Closure.DELEGATE_FIRST
    c.delegate = this
    c.call()
    response
  }

  def methodMissing(String name, args) {
    log.debug 'method {} with {}', name, args
    args
  }
}
