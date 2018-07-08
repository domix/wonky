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
