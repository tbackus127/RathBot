
package com.rath.rathbot.task.time;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;

import com.rath.rathbot.RBConfig;

/**
 * This class handles configurations that generate date/times relative to the current time.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class RelativeTimeConfiguration extends TimeConfiguration {
  
  /** The time this configuration was constructed. */
  private final ZonedDateTime scheduleTime;
  
  /** The time durations to apply for each iteration. */
  private final ArrayList<ChronoUnit> durations = new ArrayList<ChronoUnit>();
  
  /** The number of time durations to apply for each iteration. */
  private final ArrayList<Integer> intervals = new ArrayList<Integer>();
  
  /** True if the configuration repeats. */
  private boolean doesRepeat = false;
  
  /** The number of times to apply the time interval. */
  private int iterationCount = 0;
  
  /**
   * Constructs a new RelativeTimeConfiguration with the bot's default time zone.
   * 
   * @param timeString The configuration {@link String}.
   */
  public RelativeTimeConfiguration(final String timeString) {
    this(timeString, RBConfig.getTimeZone());
  }
  
  /**
   * Constructs a new RelativeTimeConfiguration with the specified time zone.
   * 
   * @param configString The configuration {@link String}.
   * @param testZoneId The time zone to create this configuration in.
   */
  public RelativeTimeConfiguration(final String configString, final ZoneId testZoneId) {
    super(TimeConfigurationType.RELATIVE, configString);
    this.scheduleTime = ZonedDateTime.now(testZoneId);
    parseConfigString();
  }
  
  @Override
  public final Iterator<Long> iterator() {
    return new Iterator<Long>() {
      
      @Override
      public boolean hasNext() {
        // TODO: Implement hasNext in iterator
        return false;
      }
      
      @Override
      public Long next() {
        // TODO: Implement next in iterator
        return null;
      }
      
    };
  }
  
  /**
   * Gets the time this configuration was created.
   * 
   * @return the creation time as a {@link ZonedDateTime}.
   */
  public final ZonedDateTime getCreateTime() {
    return this.scheduleTime;
  }
  
  /**
   * Determines whether the config string is an in-clause or an every-clause.
   */
  private final void parseConfigString() {
    
    // TODO: regex match this string and parse
    if (this.config.startsWith("every ")) {
      this.doesRepeat = true;
    } else if (!this.config.startsWith("in ")) {
      throw new BadTimeConfigException(TimeExceptionReason.REL_MISSING_CLAUSE);
    }
    
    parseTimeConfig();
  }
  
  /**
   * Sets the intervals and durations for this time configuration from the config string.
   */
  private final void parseTimeConfig() {
    
    // TODO: Implement ParseTimeConfig
    
  }
}