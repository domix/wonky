# wonky

Wonky is a port of [slacking](https://github.com/rauchg/slackin/), to the JVM written in [Groovy](http://www.groovy-lang.org) and [SpringBoot](http://projects.spring.io/spring-boot/).

## features

- A landing page you can point users to fill in their emails and receive an invite (`http://slack.yourdomain.com`)

## Build

```bash
./gradlew clean bootRepackage
```

## Run

To run wonky you need a Slack API token. Note that the user you use to generate the token must be an admin. You may want to create a dedicated @slackin-inviter user (or similar) for this.

You can find your API token [here](http://api.slack.com/web)

In order to run `wonky` you need to provide the following settings:

- `slack.token`
- `slack.host`

By default wonky runs on port 3030, as any Spring Boot application you can chance the port as you wish.

### Setting configuration values

You have different options for this:

- ```bash
    java -Dslack.token={your_token_here} -Dslack.host={your_slack_host_here} -jar build/libs/wonky-x.x.x.jar
  ```

- ```bash
    export SLACK_TOKEN={your_token_here}
    export SLACK_HOST={your_slack_host_here}
    java -jar build/libs/wonky-x.x.x.jar
  ```


### Websites using Wonky

- [SpringHispano.org](http://slack.springhispano.org)

[![Stories in Ready](https://badge.waffle.io/domix/wonky.svg?label=ready&title=Ready)](http://waffle.io/domix/wonky)

[![Throughput Graph](https://graphs.waffle.io/domix/wonky/throughput.svg)](https://waffle.io/domix/wonky/metrics))
