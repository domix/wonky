package wonky.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import wonky.slack.Team;

@Setter
@Getter
@ToString
public class Organization {
  private Team team;
  //TODO: User info
  //TODO: channels info
}
