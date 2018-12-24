package wonky.http;

import io.micronaut.http.client.annotation.Client;

@Client("https://slack.com")
public interface SlackClient {
}
