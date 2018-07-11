package wonky.json

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import wonky.slack.Team

class JacksonUtilSpec extends Specification {
  def foo() {
    given:
      def util = new JacksonUtil(new ObjectMapper())
      Team team = new Team(domain: 'dd.com')
      def json = util.toJson(team)
    expect:
      util.readValue(json, Team)
  }

  def bar() {
    when:
      def util = new JacksonUtil(new ObjectMapper())
      def json = util.readValue("ssffs", Team)
    then:
      thrown RuntimeException
  }
}
