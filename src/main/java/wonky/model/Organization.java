package wonky.model;

import lombok.Getter;
import lombok.Setter;
import wonky.slack.Team;

@Setter
@Getter
public class Organization {
  private Team team;
  //TODO: Users info
  //TODO: channels info
}
