package wonky.service;

/**
 * Created by domix on 01/06/18.
 */
public class SlackOrganization {
  private String teamDomain;
  private String token;
  private String wonkyDomain;

  public String getTeamDomain() {
    return teamDomain;
  }

  public void setTeamDomain(String teamDomain) {
    this.teamDomain = teamDomain;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getWonkyDomain() {
    return wonkyDomain;
  }

  public void setWonkyDomain(String wonkyDomain) {
    this.wonkyDomain = wonkyDomain;
  }
}
