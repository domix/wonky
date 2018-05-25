package wonky.api;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

/**
 * Created by domix on 01/06/18.
 */
@Controller("/hello")
public class ApiController {
  @Get("/")
  public String index() {
    return "Hello World";
  }
}
