/**
 *
 * Copyright (C) 2014-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wonky.slack;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by domix on 05/06/18.
 */
@Setter
@Getter
public class Icon {
  @JsonProperty("image_102")
  private String image102;
  @JsonProperty("image_132")
  private String image132;
  @JsonProperty("image_230")
  private String image230;
  @JsonProperty("image_34")
  private String image34;
  @JsonProperty("image_44")
  private String image44;
  @JsonProperty("image_68")
  private String image68;
  @JsonProperty("image_88")
  private String image88;
  @JsonProperty("image_original")
  private String imageOriginal;
}
