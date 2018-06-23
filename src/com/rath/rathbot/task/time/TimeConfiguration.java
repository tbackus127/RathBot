
package com.rath.rathbot.task.time;

/**
 * This class handles storing the complex scheduling information of tasks by doing something not complex and storing it
 * as a String, only to be evaluated as needed.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public abstract class TimeConfiguration {
  
  /** The type of time configuration this TimeConfiguration represents (relative or absolute). */
  protected final TimeConfigurationType type;
  
  /** The configuration String describing the timing. */
  protected final String config;
  
  /**
   * Constructs a new TimeConfiguration base class. Cannot be directly constructed.
   * 
   * @param type the type of configuration this is.
   * @param configString the String that describes this configuration.
   * @param doesRepeat true if this time configuration is repeat-enabled; false if not.
   */
  protected TimeConfiguration(final TimeConfigurationType type, final String configString) {
    this.type = type;
    this.config = configString;
  }
  
  /**
   * Gets if this time configuration is repeat-enabled.
   * 
   * @return true if it repeats; false if not.
   */
  public abstract boolean doesRepeat();
  
  /**
   * Gets the next time this task should be executed in milliseconds as of 1/1/1970.
   * 
   * @return a long.
   */
  public abstract long getNextEpochTime();
  
}
