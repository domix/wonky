package wonky.service;

import io.micronaut.context.annotation.Value;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.lang.String.format;

/**
 * Created by domix on 01/06/18.
 */
@Singleton
@Setter
@Slf4j
public class SlackService {
  @Value("${wonky.tenants.file:/etc/wonky/tenants.yaml}")
  private String tenantsFile;

  private List<SlackOrganization> orgs;

  @PostConstruct
  public void load() {
    Yaml yaml = new Yaml();

    try {
      File file = new File(tenantsFile);
      InputStream ios = new FileInputStream(file);
      orgs = yaml.load(ios);
      String tenantsFileDirectory = file.getParentFile().getName();
      Path path = Paths.get(tenantsFileDirectory);
      ios.close();
    } catch (FileNotFoundException e) {
      //log.error(format("Can't load tenants file. '%s'", tenantsFile), e);
      throw new IllegalStateException(format("Can't load tenants file. '%s'", tenantsFile), e);
    } catch (IOException e) {
      //log.warn(e.getMessage(), e);
    }
  }
}
