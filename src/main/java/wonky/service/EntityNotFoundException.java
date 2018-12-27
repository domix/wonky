package wonky.service;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {
  private final String entity;
  private final String id;

  public EntityNotFoundException(String entity, String id) {
    this("The entity can not be found.", entity, id);
  }

  public EntityNotFoundException(String message, String entity, String id) {
    super(message);

    this.entity = entity;
    this.id = id;
  }
}
