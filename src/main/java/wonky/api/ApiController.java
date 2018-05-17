package wonky.api;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/hello")
public class ApiController {
  @Get("/")
  public String index() {
    return "Hello World";
  }
}
