package com.rath.rathbot.task.time;


public class BadTimeConfigException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  
  private final TimeExceptionReason reason;
  
  public BadTimeConfigException(final TimeExceptionReason reason) {
    this.reason = reason;
  }
  
  public String getReason() {
    return this.reason.getReasonString();
  }
  
}
