package wonky.http;

import lombok.Getter;

@Getter
public class SlackResponseException extends RuntimeException {
  private final String error;
  private final String slackResponse;

  public SlackResponseException(String error, String slackResponse) {
    this("Slack error response", error, slackResponse);
  }

  public SlackResponseException(String message, String error, String slackResponse) {
    super(message);
    this.error = error;
    this.slackResponse = slackResponse;
  }
}
