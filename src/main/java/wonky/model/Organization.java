package wonky.model;

import wonky.slack.Team;

public class Organization {
  private Team team;
  //TODO: User info
  //TODO: channels info

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }
}
