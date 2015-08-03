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
