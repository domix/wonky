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
