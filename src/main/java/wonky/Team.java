package wonky;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Team {
  private String domain;
  @JsonProperty("email_domain")
  private String emailDomain;
  private Icon icon;
  private String id;
  private String name;

  @Override
  public String toString() {
    return "Team{" +
      "domain='" + domain + '\'' +
      ", emailDomain='" + emailDomain + '\'' +
      ", icon=" + icon +
      ", id='" + id + '\'' +
      ", name='" + name + '\'' +
      '}';
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getEmailDomain() {
    return emailDomain;
  }

  public void setEmailDomain(String emailDomain) {
    this.emailDomain = emailDomain;
  }

  public Icon getIcon() {
    return icon;
  }

  public void setIcon(Icon icon) {
    this.icon = icon;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
