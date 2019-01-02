/**
 *
 * Copyright (C) 2014-2019 the original author or authors.
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
package wonky.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;
import java.io.IOException;

import static io.micronaut.core.util.StringUtils.isNotEmpty;

/**
 * Created by domix on 05/06/18.
 */
@Singleton
public class JacksonUtil {
  private final ObjectMapper objectMapper;

  public JacksonUtil(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public String toJson(Object d) {
    try {
      return objectMapper.writeValueAsString(d);
    } catch (JsonProcessingException e) {
      //TODO: improve exception handling
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public <T> T readValue(String content, String node, Class<T> valueType) {
    if (isNotEmpty(node)) {
      try {
        JsonNode jsonNode = objectMapper.readTree(content).get(node);
        return objectMapper.treeToValue(jsonNode, valueType);
      } catch (IOException e) {
        //TODO: improve exception handling
        throw new RuntimeException(e.getMessage(), e);
      }
    } else {
      return readValue(content, valueType);
    }
  }

  public <T> T readValue(String content, Class<T> valueType) {
    try {
      return objectMapper.readValue(content, valueType);
    } catch (IOException e) {
      //TODO: improve exception handling
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
