package wonky.service;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SlackOrganization {
  private String teamDomain;
  private String token;
  private String wonkyDomain;
}
