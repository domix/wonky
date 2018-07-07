package wonky.api

import io.micronaut.context.ApplicationContext
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.RxStreamingHttpClient
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import wonky.model.Organization

import static io.micronaut.http.HttpRequest.GET
import static io.micronaut.http.HttpStatus.OK

class ApiControllerSpec extends Specification {
  @Shared
  @AutoCleanup
  EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer)

  @Shared
  @AutoCleanup
  RxStreamingHttpClient client = embeddedServer.applicationContext.createBean(RxStreamingHttpClient, embeddedServer.getURL())

  @Ignore
  def "Getting the organization info"() {
    when:
      HttpRequest request = GET('/v1/organizations/_self').header("Host", "localhost")

      HttpResponse<Organization> rsp = client.toBlocking().exchange(request, Argument.of(Organization))

    then: 'the endpoint can be accessed'
      rsp.status == OK
      rsp.body()

    when:
      def body = rsp.body()

    then:
      body.team.name
  }
}
