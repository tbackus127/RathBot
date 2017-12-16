
package com.rath.rathbot.exceptions;

public class FAQNotFoundException extends Exception {
  
  /** Default serial version UID. */
  private static final long serialVersionUID = 1L;
  
  /** The name of the FAQ that caused the exception. */
  private final String faqName;
  
  /**
   * Default constructor.
   * 
   * @param faqName the name of the FAQ.
   */
  public FAQNotFoundException(final String faqName) {
    super();
    this.faqName = faqName;
  }
  
  @Override
  public String getMessage() {
    return "Error with FAQ ID=\"" + this.faqName + "\"!";
  }
  
}
