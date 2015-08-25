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

import org.springframework.stereotype.Service
import wslite.rest.RESTClient

/**
 * Created by domix on 20/08/15.
 */
@Service
class RemoteServiceRestClient implements RemoteService {
  Map get(String url, Map request) {
    new RESTClient(url)
      .get(request)
      .parsedResponseContent.json
  }

  Map post(String url, Closure content) {
    new RESTClient(url)
      .post(content)
      .parsedResponseContent.json
  }
}
