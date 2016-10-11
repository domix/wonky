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

import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK
import static org.springframework.web.servlet.HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE

import org.springframework.core.io.ClassPathResource
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.webjars.WebJarAssetLocator

import javax.servlet.http.HttpServletRequest

/**
 * Created by domix on 02/08/15.
 */
@Controller
class WebJar {

  @ResponseBody
  @RequestMapping('/webjarslocator/{webjar}/**')
  ResponseEntity locateWebjarAsset(@PathVariable String webjar, HttpServletRequest request) {
    WebJarAssetLocator assetLocator = new WebJarAssetLocator()
    try {
      String mvcPrefix = "/webjarslocator/${webjar}/" // This prefix must match the mapping path!
      String mvcPath = (String) request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)
      String fullPath = assetLocator.getFullPath(webjar, mvcPath[mvcPrefix.length()..-1])
      new ResponseEntity(new ClassPathResource(fullPath), OK)
    } catch (Exception e) {
      new ResponseEntity<>(NOT_FOUND)
    }
  }
}
