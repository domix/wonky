package wonky.slack;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by domix on 05/06/18.
 */
@Setter
@Getter
public class Team {
  private String domain;
  @JsonProperty("email_domain")
  private String emailDomain;
  private Icon icon;
  private String id;
  private String name;
}
