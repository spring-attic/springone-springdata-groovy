package org.springframework.data.groovy

import org.springframework.context.ApplicationEvent

class MongoEvent extends ApplicationEvent {

  public static enum Type {
    CREATE, RETRIEVE, UPDATE, DELETE
  }

  private Type type;

  public MongoEvent(Type type, Object source) {
    super(source);
    this.type = type;
  }

  def getType() {
    type
  }
}
