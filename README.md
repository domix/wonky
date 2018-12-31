# wonky
[![Build Status](https://travis-ci.org/domix/wonky.svg)](https://travis-ci.org/domix/wonky)
[![codecov.io](http://codecov.io/github/domix/wonky/coverage.svg?branch=master)](http://codecov.io/github/domix/wonky?branch=master)


Wonky is a port of [slacking](https://github.com/rauchg/slackin/), to the JVM written in `Java` and [Micronaut](http://micronaut.io/).

## Features

- A landing page you can point users to fill in their emails and receive an invite (`http://slack.yourdomain.com`)

## Build

### Requirements

[!["JDK"](https://img.shields.io/badge/JDK-8.0+-F30000.svg?style=flat)](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

### Slack token

To build & run wonky you need a `Slack API token`. Note that the user you use to generate the token must be an admin. You may want to create a dedicated @wonky-inviter user (or similar) for this.

You can find your API token [here](http://api.slack.com/web)

Once you have the token, you need to write the `configuration file`.

#### Configuration file

The configuration file is very simple to write, it's a `YAML`.

##### Single Slack organization

```YAML
- !!wonky.service.SlackOrganization
  token: "xoxp-..."
  wonkyDomain: "localhost:8080"
```

*NOTE:* Wonky supports multiple organizations (aka multitenancy), Wonky will use the domain (HOST http header) to select the right token. Consider this.

##### Multiple Slack organizations

```YAML
- !!wonky.service.SlackOrganization
  token: "xoxp-..."
  wonkyDomain: "localhost:8080"
- !!wonky.service.SlackOrganization
  token: "xoxp-..."
  wonkyDomain: "slack.myorganization.com"
``` 

##### Recomendation

We strongly recommend you write and name the config file as `orgs_ignored.yaml` and save it to the root source of wonky, in git is marked as ignored.  

#### Environment variables

In order to run properly the test, you have to provide the following `Environment Variables`;

* WONKY_TENANTS_FILE
* WONKY_TEST_EMAIL_PREFIX

You can configued as follows in the shell:

````bash
$ export WONKY_TENANTS_FILE=./orgs_ignored.yaml
$ export WONKY_TEST_EMAIL_PREFIX=something

````

Now you can build wonky from source :)

## Building from source

```bash
$ ./gradlew clean build
```

## Run

By default wonky runs on port `8080`, as any `Micronaut` application you can chance the port as you wish.


```bash
$ ./gradlew run  
```

Alternatively, you can run Wonky with Docker as a container:


```bash
$ docker run --rm -p 8080:8080 -v `pwd`/orgs_ignored.yaml:/etc/wonky/tenants.yaml  domix/wonky:0.2.4  
```

### Communities using Wonky

- [The Data Pub](http://slack.thedata.pub)
- [JavaMexico.org](http://slack.javamexico.org)
- [SpringHispano.org](http://slack.springhispano.org)
- [Groovyando.org](http://slack.groovyando.org)
- [PeruJUG](http://slack.perujug.org/)
- [Functional Programming Club](http://functionalprogramming.club/)
- [JavaHispano.org](http://slack.javahispano.org)

### Development badges

[![Stories in Ready](https://badge.waffle.io/domix/wonky.svg?label=ready&title=Ready)](http://waffle.io/domix/wonky)

[![Throughput Graph](https://graphs.waffle.io/domix/wonky/throughput.svg)](https://waffle.io/domix/wonky/metrics)

![codecov.io](http://codecov.io/github/domix/wonky/branch.svg?branch=master)
