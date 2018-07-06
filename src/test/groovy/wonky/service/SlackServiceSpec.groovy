package wonky.service

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Ignore
import spock.lang.Specification
import wonky.json.JacksonUtil

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

  @Ignore
  def foo2() {
    given:
      def token = System.getenv("TOKEN")
      assert token
      def jacksonUtil = new JacksonUtil(objectMapper: new ObjectMapper())
      def service = new SlackService(tenantsFile: './src/test/resources/foo.yaml', jacksonUtil: jacksonUtil)
      def information = service.tenantSlackInformation(token)
    expect:
      information
      information.getIcon().getImageOriginal()
      println information.getIcon().getImageOriginal()
  }
}
