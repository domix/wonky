package wonky.service

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

/**
 * Created by domix on 01/06/18.
 */
class SlackServiceSpec extends Specification {

  def foo() {
    given:
      def service = new SlackService(tenantsFile: './src/test/resources/foo.yaml')
      service.load()
    expect:
      service.orgs.size() == 2
  }

  def foo2() {
    given:
      def token = System.getenv("TOKEN")
      def service = new SlackService(tenantsFile: './src/test/resources/foo.yaml', objectMapper: new ObjectMapper())
      def information = service.tenantSlackInformation(token, "")
    expect:
      information
      println information
  }

}
