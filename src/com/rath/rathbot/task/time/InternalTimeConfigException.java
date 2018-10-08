
package com.rath.rathbot.task.time;

public class InternalTimeConfigException extends RuntimeException {
  
  private static final long serialVersionUID = 1L;
  
  private final String reason;
  
  public InternalTimeConfigException(final String reason) {
    this.reason = reason;
  }
  
  public String getReason() {
    return this.reason;
  }
  
}
