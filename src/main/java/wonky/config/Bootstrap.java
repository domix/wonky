/**
 * Copyright (C) 2014-2019 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wonky.config;

import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.scheduling.annotation.Async;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wonky.service.SlackService;

@Slf4j
@Singleton
public class Bootstrap {
  private final SlackService slackService;

  public Bootstrap(SlackService slackService) {
    this.slackService = slackService;
  }

  @EventListener
  @Async
  void onStartup(StartupEvent event) {
    slackService.loadAllOrgs();
  }
}
