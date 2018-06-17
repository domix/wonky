package wonky.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by domix on 01/06/18.
 */
@Setter
@Getter
@ToString
public class SlackOrganization {
  private String teamDomain;
  private String token;
  private String wonkyDomain;
}
