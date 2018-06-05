package wonky.slack;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by domix on 05/06/18.
 */
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

  @Override
  public String toString() {
    return "Icon{" +
      "image102='" + image102 + '\'' +
      ", image132='" + image132 + '\'' +
      ", image230='" + image230 + '\'' +
      ", image34='" + image34 + '\'' +
      ", image44='" + image44 + '\'' +
      ", image68='" + image68 + '\'' +
      ", image88='" + image88 + '\'' +
      ", imageOriginal='" + imageOriginal + '\'' +
      '}';
  }

  public String getImage102() {
    return image102;
  }

  public void setImage102(String image102) {
    this.image102 = image102;
  }

  public String getImage132() {
    return image132;
  }

  public void setImage132(String image132) {
    this.image132 = image132;
  }

  public String getImage230() {
    return image230;
  }

  public void setImage230(String image230) {
    this.image230 = image230;
  }

  public String getImage34() {
    return image34;
  }

  public void setImage34(String image34) {
    this.image34 = image34;
  }

  public String getImage44() {
    return image44;
  }

  public void setImage44(String image44) {
    this.image44 = image44;
  }

  public String getImage68() {
    return image68;
  }

  public void setImage68(String image68) {
    this.image68 = image68;
  }

  public String getImage88() {
    return image88;
  }

  public void setImage88(String image88) {
    this.image88 = image88;
  }

  public String getImageOriginal() {
    return imageOriginal;
  }

  public void setImageOriginal(String imageOriginal) {
    this.imageOriginal = imageOriginal;
  }
}
