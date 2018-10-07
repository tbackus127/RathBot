
package com.rath.rathbot.task.time;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * This class handles all time configurations that happen at specific points in time. e.g.: at February 10, 2019 at 6:00
 * PM CST.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class AbsoluteTimeConfiguration extends TimeConfiguration {
  
  private static final boolean DEBUG_MODE = false;
  
  /** Choose spaces before and after the words "at" and "on". */
  private static final String REGEX_SPLIT_CLAUSES = "((?<=at)\\s+)|(\\s+(?=at))|((?<=on)\\s+)|(\\s+(?=on))";
  
  /** Choose spaces between hours, minutes, and optional AM/PM. */
  private static final String REGEX_SPLIT_AT_CLAUSE = "((?<=\\d\\d)\\s*(?=\\d\\d))|(:(?=\\d\\d))|((?<=1?\\d)\\s*(?=[AaPp][Mm]))";
  
  /** Choose any spaces not between square brackets. */
  private static final String REGEX_SPLIT_ON_CLAUSE = "(\\s+(?![^\\[]*\\]))|(\\s*/\\s*)";
  
  /** Match a sequence of comma-separated numbers inside of square brackets. */
  private static final String REGEX_MATCH_BRACKET_LIST = "\\[((\\d+)|(\\w{3}))(\\s*,\\s*((\\d+)|(\\w{3})))*\\]";
  
  /** The minimum year that can be specified. */
  private static final int YEAR_MIN = 2018;
  
  /** The maximum year that can be specified. */
  private static final int YEAR_MAX = 2030;
  
  /** The minimum day that can be specified. */
  private static final int DAY_MIN = 1;
  
  /** The maximum day that can be specified. */
  private static final int DAY_MAX = 31;
  
  /** The minimum month that can be specified. */
  private static final int MONTH_MIN = 1;
  
  /** The maximum month that can be specified. */
  private static final int MONTH_MAX = 12;
  
  /** The minimum hour that can be specified for 24-hour time. */
  private static final int HOUR_24_MIN = 0;
  
  /** The maximum hour that can be specified for 24-hour time. */
  private static final int HOUR_24_MAX = 23;
  
  /** The minimum hour that can be specified for 12-hour time. */
  private static final int HOUR_12_MIN = 1;
  
  /** The maximum hour that can be specified for 12-hour time. */
  private static final int HOUR_12_MAX = 12;
  
  /** The minimum minute that can be specified. */
  private static final int MINUTE_MIN = 0;
  
  /** The maximum minute that can be specified. */
  private static final int MINUTE_MAX = 59;
  
  private static final TreeMap<String, Integer> MONTH_ALIASES = new TreeMap<String, Integer>() {
    
    private static final long serialVersionUID = 1L;
    
    {
      put("jan", 1);
      put("feb", 2);
      put("mar", 3);
      put("apr", 4);
      put("may", 5);
      put("jun", 6);
      put("jul", 7);
      put("aug", 8);
      put("sep", 9);
      put("oct", 10);
      put("nov", 11);
      put("dec", 12);
    }
    
  };
  
  /** A {@link String} that is equal to an asterisk character for the months list argument in the on-clause. */
  private static final String ASTERISK_MONTHS = generateAsteriskString(MONTH_MIN, MONTH_MAX);
  
  /** A {@link String} that is equal to an asterisk character for the days list argument in the on-clause. */
  private static final String ASTERISK_DAYS = generateAsteriskString(DAY_MIN, DAY_MAX);
  
  /** A {@link String} that is equal to an asterisk character for the years list argument in the on-clause. */
  private static final String ASTERISK_YEARS = generateAsteriskString(YEAR_MIN, YEAR_MAX);
  
  /** The time zone that the bot's local time will be converted from. */
  private final ZoneId fromTimeZone;
  
  /** The list of month numbers this time configuration is active for. */
  private ArrayList<Integer> monthList = null;
  
  /** The list of day numbers this time configuration is active for. */
  private ArrayList<Integer> dayList = null;
  
  /** The list of year numbers this time configuration is active for. */
  private ArrayList<Integer> yearList = null;
  
  private int monthPos = -1;
  private int dayPos = -1;
  private int yearPos = -1;
  
  private int hour = -1;
  private int minute = -1;
  
  private transient boolean parsedAt = false;
  private transient boolean parsedOn = false;
  
  /**
   * Constructs a new AbsoluteTimeConfiguration object.
   * 
   * @param configString The {@link String} containing the at-clause and/or on-clause (command and to-clause are not
   *        included).
   * @param timeZone The time zone this AbsoluteTimeConfiguration will be converted from as a {@link ZoneId}.
   * 
   * @throws BadTimeConfigException when the configString parameter is invalid.
   */
  public AbsoluteTimeConfiguration(final String configString, final ZoneId timeZone) throws BadTimeConfigException {
    super(TimeConfigurationType.ABSOLUTE, configString);
    
    if (DEBUG_MODE) System.out.println("Entered constructor.");
    
    this.fromTimeZone = timeZone;
    
    // At least 4 tokens are required ("on"/"at", on/at-clause, "to", to-clause)
    final String[] clauseTokens = configString.split(REGEX_SPLIT_CLAUSES);
    if (clauseTokens == null || clauseTokens.length < 2) {
      if (DEBUG_MODE) System.out.println("ERR001.");
      throw new BadTimeConfigException();
    }
    
    // If the clause tokens length is not even, throw an exception
    if (clauseTokens.length % 2 == 1) {
      if (DEBUG_MODE) System.out.println("ERR002.");
      throw new BadTimeConfigException();
    }
    
    // Parse both at/on-clauses
    if (DEBUG_MODE) System.out.println("Parsing clauses.");
    for (int i = 0; i < clauseTokens.length; i += 2) {
      if (clauseTokens[i].equals("at")) {
        parseAtClause(clauseTokens[i + 1]);
      } else if (clauseTokens[i].equals("on")) {
        parseOnClause(clauseTokens[i + 1]);
      }
    }
    
    if (this.parsedAt && !this.parsedOn) {
      
      if (DEBUG_MODE) System.out.println("Only at-clause specified.");
      
      ZonedDateTime desiredZdt = ZonedDateTime.now(this.fromTimeZone).truncatedTo(ChronoUnit.DAYS).with(
          ChronoField.HOUR_OF_DAY, this.hour).with(ChronoField.MINUTE_OF_DAY, this.minute);
      // LocalDateTime desiredLdt =
      // LocalDateTime.now(this.fromTimeZone).truncatedTo(ChronoUnit.DAYS).with(ChronoField.HOUR_OF_DAY,
      // this.hour).with(ChronoField.MINUTE_OF_DAY, this.minute);
      
      // If the desired time has already passed for today, use tomorrow's date
      if (ZonedDateTime.now(this.fromTimeZone).compareTo(desiredZdt) >= 0) {
        desiredZdt = desiredZdt.plusDays(1);
      }
      
      this.monthList = new ArrayList<Integer>();
      this.monthList.add(desiredZdt.getMonthValue());
      this.monthPos = 0;
      this.dayList = new ArrayList<Integer>();
      this.dayList.add(desiredZdt.getDayOfMonth());
      this.dayPos = 0;
      this.yearList = new ArrayList<Integer>();
      this.yearList.add(desiredZdt.getYear());
      this.yearPos = 0;
      
    } else if (this.parsedOn && !this.parsedAt) {
      
      if (DEBUG_MODE) System.out.println("Only on-clause specified.");
      
      this.hour = HOUR_24_MIN;
      this.minute = MINUTE_MIN;
      
    }
    
    // Check if every field was initialized; if not, throw an exception
    if (this.monthPos == -1 || this.dayPos == -1 || this.yearPos == -1 || this.monthList == null || this.dayList == null
        || this.yearList == null) {
      if (DEBUG_MODE) System.out.println("ERR003");
      throw new BadTimeConfigException();
    }
    
    if (DEBUG_MODE) System.out.println("Finished construction.");
    if (DEBUG_MODE) System.out.println("M=" + this.monthList.toString() + " D=" + this.dayList.toString() + " Y="
        + this.yearList.toString() + " H=" + this.hour + " M=" + this.minute);
    if (DEBUG_MODE) System.out.println("Mi=" + this.monthPos + " Di=" + this.dayPos + " Yi=" + this.yearPos);
    
  }
  
  private static final String generateAsteriskString(final int min, final int max) {
    String result = "[" + min;
    for (int i = min + 1; i <= max; i++) {
      result += "," + i;
    }
    return result;
  }
  
  /**
   * Splits the at-clause into tokens and dispatches control to the proper handling method.
   * 
   * @param atClause The at-clause as a String.
   */
  private final void parseAtClause(final String atClause) {
    
    if (DEBUG_MODE) System.out.println("Parsing at-clause.");
    
    // Split the at-clause into tokens and handle each
    final String[] atTokens = atClause.split(REGEX_SPLIT_AT_CLAUSE);
    if (atTokens == null || atTokens.length < 2 || atTokens.length > 3) {
      if (DEBUG_MODE) System.out.println("ERR004");
      throw new BadTimeConfigException();
    }
    
    // Military or hour-only ({19, 00} or {7, PM})
    if (atTokens.length == 2) {
      
      if (DEBUG_MODE) System.out.println("Military or hour-only");
      
      // If the 2nd letter of the 2nd token is an M, it's in hour-only format
      if (atTokens[1].toLowerCase().charAt(1) == 'm') {
        handleHourOnly(atTokens[0], atTokens[1]);
      } else {
        handleMilitary(atTokens[0], atTokens[1]);
      }
      
      // Standard
    } else {
      
      if (DEBUG_MODE) System.out.println("Standard format");
      handleStandard(atTokens[0], atTokens[1], atTokens[2]);
    }
    
    this.parsedAt = true;
    if (DEBUG_MODE) System.out.println("At-clause parse finished.");
  }
  
  /**
   * Handles hour-only time specification for the at-clause. e.g.: "7pm". The minutes field will be set to zero, so the
   * task will take place on the hour.
   * 
   * @param hourString the {@link String} containing the hour.
   * @param amPmString the {@link String} that determines if the hour will be interpreted as AM or PM.
   */
  private final void handleHourOnly(final String hourString, final String amPmString) {
    
    if (DEBUG_MODE) System.out.println("Hour only.");
    
    // Parse the hour string
    int h = -1;
    try {
      h = Integer.parseInt(hourString);
      if (DEBUG_MODE) System.out.println("Hour only: Parsed " + h + ".");
    } catch (@SuppressWarnings("unused") NumberFormatException nfe) {
      if (DEBUG_MODE) System.out.println("ERR005");
      throw new BadTimeConfigException();
    }
    
    // Check out of bounds
    if (h < HOUR_12_MIN || h > HOUR_12_MAX) {
      if (DEBUG_MODE) System.out.println("ERR006");
      throw new BadTimeConfigException();
    }
    
    // Convert to 24-hour time
    if (amPmString.toLowerCase().charAt(0) == 'p') {
      if (DEBUG_MODE) System.out.println("Converted to 24-hour.");
      h = (h + HOUR_12_MAX) % (HOUR_24_MAX + 1);
      if (DEBUG_MODE) System.out.println("Hour is now " + h + ".");
    }
    
    // Set time fields
    this.hour = h;
    this.minute = MINUTE_MIN;
  }
  
  /**
   * Handles military time or 24-hour time for the at-clause. e.g.: "1530".
   * 
   * @param hourString the {@link String} containing the hour.
   * @param minutesString the {@link String} containing the minute.
   */
  private final void handleMilitary(final String hourString, final String minutesString) {
    
    if (DEBUG_MODE) System.out.println("Military format.");
    
    // Parse the hour and minute strings
    int h = -1;
    int m = -1;
    try {
      h = Integer.parseInt(hourString);
      m = Integer.parseInt(minutesString);
    } catch (@SuppressWarnings("unused") NumberFormatException nfe) {
      if (DEBUG_MODE) System.out.println("ERR007");
      throw new BadTimeConfigException();
    }
    
    // Check out of bounds
    if (h < HOUR_24_MIN || h > HOUR_24_MAX || m < MINUTE_MIN || m > MINUTE_MAX) {
      if (DEBUG_MODE) System.out.println("ERR008");
      throw new BadTimeConfigException();
    }
    
    // Set time fields
    this.hour = h;
    this.minute = m;
    
  }
  
  /**
   * Handles standard American time for the at-clause. e.g.: "7:45 PM".
   * 
   * @param hourString the {@link String} containing the hour.
   * @param minutesString the {@link String} containing the minute.
   * @param amPmString the {@link String} that determines if the hour will be interpreted as AM or PM.
   */
  private final void handleStandard(final String hourString, final String minutesString, final String amPmString) {
    
    // Parse the hour and minute strings
    int h = -1;
    int m = -1;
    try {
      h = Integer.parseInt(hourString);
      m = Integer.parseInt(minutesString);
    } catch (@SuppressWarnings("unused") NumberFormatException nfe) {
      if (DEBUG_MODE) System.out.println("ERR009");
      throw new BadTimeConfigException();
    }
    
    // Check out of bounds
    if (h < HOUR_12_MIN || h > HOUR_12_MAX || m < MINUTE_MIN || m > MINUTE_MAX) {
      if (DEBUG_MODE) System.out.println("ERR010");
      throw new BadTimeConfigException();
    }
    
    // Convert to 24-hour time
    if (amPmString.toLowerCase().charAt(0) == 'p') {
      h = (h + HOUR_12_MAX) % (HOUR_24_MAX + 1);
    }
    
    // Set time fields
    this.hour = h;
    this.minute = m;
    
  }
  
  /**
   * Splits the on-clause into its three tokens and calls each token's handling function.
   * 
   * @param onClause the clause containing the date(s) to execute the task on.
   */
  private final void parseOnClause(final String onClause) {
    
    // Split the at-clause into tokens and handle each
    final String[] onTokens = onClause.split(REGEX_SPLIT_ON_CLAUSE);
    if (onTokens == null || onTokens.length < 2 || onTokens.length > 3) {
      if (DEBUG_MODE) System.out.println("ERR011");
      throw new BadTimeConfigException();
    }
    
    handleMonths(onTokens[0].trim());
    handleDays(onTokens[1].trim());
    
    // If the year was specified, handle the token
    if (onTokens.length == 3) {
      handleYears(onTokens[2].trim());
    } else {
      
      // If not, check if there is only one month/day
      if (this.monthList.size() != 1 || this.dayList.size() != 1) {
        if (DEBUG_MODE) System.out.println("ERR012");
        throw new BadTimeConfigException();
      }
      
      ZonedDateTime desiredZdt = ZonedDateTime.now(this.fromTimeZone).withMonth(this.monthList.get(0)).withDayOfMonth(
          this.dayList.get(0));
      // LocalDateTime desiredLdt =
      // LocalDateTime.now(this.fromTimeZone).withMonth(this.monthList.get(0)).withDayOfMonth(this.dayList.get(0));
      
      // If the desired time has already passed for today, use tomorrow's date
      if (ZonedDateTime.now(this.fromTimeZone).compareTo(desiredZdt) >= 0) {
        desiredZdt = desiredZdt.plusYears(1);
      }
      
      this.yearList = new ArrayList<Integer>();
      this.yearList.add(desiredZdt.getYear());
      this.yearPos = 0;
    }
    
    this.parsedOn = true;
  }
  
  /**
   * Handles populating the month list from the months argument of the on-clause.
   * 
   * @param monthsString a {@link String} containing a single month; a square bracket-enclosed, comma-separated list of
   *        month numbers or three-letter abbreviations; or an asterisk.
   */
  private final void handleMonths(String monthsString) {
    
    this.monthList = new ArrayList<Integer>();
    
    // Convert months argument to a square bracket list
    String[] monthTokens = null;
    if (!monthsString.matches(REGEX_MATCH_BRACKET_LIST)) {
      
      // Translate the asterisk to its alias
      if (monthsString.equals("*")) {
        
        monthsString = ASTERISK_MONTHS;
        
      } else {
        
        // Check if the current token is a three-letter month abbreviation
        int m = -1;
        if (MONTH_ALIASES.containsKey(monthsString.toLowerCase())) {
          m = MONTH_ALIASES.get(monthsString);
        } else {
          
          // Otherwise, parse as a single value and surround in square brackets
          try {
            m = Integer.parseInt(monthsString);
          } catch (@SuppressWarnings("unused") NumberFormatException nfe) {
            if (DEBUG_MODE) System.out.println("ERR013");
            throw new BadTimeConfigException();
          }
          
        }
        
        monthsString = "[" + m + "]";
        
      }
      
    }
    
    // Trim square brackets and split on comma
    monthsString = monthsString.substring(1, monthsString.length() - 1);
    monthTokens = monthsString.split("\\s*,\\s*");
    
    // Convert each index to an integer and add it to the months list
    for (final String monthTok : monthTokens) {
      
      int m = -1;
      
      // Check if the current token is a three-letter month abbreviation
      if (MONTH_ALIASES.containsKey(monthTok.toLowerCase())) {
        m = MONTH_ALIASES.get(monthTok);
      } else {
        
        // If not, parse it as a number
        try {
          m = Integer.parseInt(monthTok);
        } catch (@SuppressWarnings("unused") NumberFormatException nfe) {
          if (DEBUG_MODE) System.out.println("ERR014");
          throw new BadTimeConfigException();
        }
        
      }
      
      // Check out of bounds
      if (m < MONTH_MIN || m > MONTH_MAX) {
        if (DEBUG_MODE) System.out.println("ERR015");
        throw new BadTimeConfigException();
      }
      
      this.monthList.add(m);
    }
    this.monthPos = 0;
  }
  
  /**
   * Handles populating the days list from the years argument of the on-clause.
   * 
   * @param daysString a {@link String} containing a single day; a square bracket-enclosed, comma-separated list of day
   *        numbers; or an asterisk.
   */
  private final void handleDays(String daysString) {
    
    this.dayList = new ArrayList<Integer>();
    
    // Convert days argument to a square bracket list
    String[] dayTokens = null;
    if (!daysString.matches(REGEX_MATCH_BRACKET_LIST)) {
      
      // Translate the asterisk to its alias
      if (daysString.equals("*")) {
        
        daysString = ASTERISK_DAYS;
        
      } else {
        
        // Otherwise, parse as a single value and surround in square brackets
        int d = -1;
        try {
          d = Integer.parseInt(daysString);
        } catch (@SuppressWarnings("unused") NumberFormatException nfe) {
          if (DEBUG_MODE) System.out.println("ERR016");
          throw new BadTimeConfigException();
        }
        
        daysString = "[" + d + "]";
        
      }
      
    }
    
    // Trim square brackets and split on comma
    daysString = daysString.substring(1, daysString.length() - 1);
    dayTokens = daysString.split("\\s*,\\s*");
    
    // Convert each index to an integer and add it to the days list
    for (final String dayTok : dayTokens) {
      
      int d = -1;
      
      try {
        d = Integer.parseInt(dayTok);
      } catch (@SuppressWarnings("unused") NumberFormatException nfe) {
        if (DEBUG_MODE) System.out.println("ERR017");
        throw new BadTimeConfigException();
      }
      
      // Check out of bounds
      if (d < DAY_MIN || d > DAY_MAX) {
        if (DEBUG_MODE) System.out.println("ERR018");
        throw new BadTimeConfigException();
      }
      
      this.dayList.add(d);
    }
    this.dayPos = 0;
  }
  
  /**
   * Handles populating the year list from the years argument of the on-clause.
   * 
   * @param yearsString a {@link String} containing a single year; a square bracket-enclosed, comma-separated list of
   *        year numbers; or an asterisk.
   */
  private final void handleYears(String yearsString) {
    
    this.yearList = new ArrayList<Integer>();
    
    // Convert years argument to a square bracket list
    String[] yearTokens = null;
    if (!yearsString.matches(REGEX_MATCH_BRACKET_LIST)) {
      
      // Translate the asterisk to its alias
      if (yearsString.equals("*")) {
        
        yearsString = ASTERISK_YEARS;
        
      } else {
        
        // Otherwise, parse as a single value and surround in square brackets
        int y = -1;
        try {
          y = Integer.parseInt(yearsString);
        } catch (@SuppressWarnings("unused") NumberFormatException nfe) {
          if (DEBUG_MODE) System.out.println("ERR019");
          throw new BadTimeConfigException();
        }
        
        // If the year was specified with two digits, add 2000 to the year
        if (y < 100) {
          y += 2000;
        }
        
        // Check out of bounds
        if (y < YEAR_MIN || y > YEAR_MAX) {
          if (DEBUG_MODE) System.out.println("ERR020");
          throw new BadTimeConfigException();
        }
        
        yearsString = "[" + y + "]";
        
      }
      
    }
    
    // Trim square brackets and split on comma
    yearsString = yearsString.substring(1, yearsString.length() - 1);
    yearTokens = yearsString.split("\\s*,\\s*");
    
    // Convert each index to an integer and add it to the years list
    for (final String yearTok : yearTokens) {
      
      int d = -1;
      
      try {
        d = Integer.parseInt(yearTok);
      } catch (@SuppressWarnings("unused") NumberFormatException nfe) {
        if (DEBUG_MODE) System.out.println("ERR021");
        throw new BadTimeConfigException();
      }
      
      this.yearList.add(d);
    }
    this.yearPos = 0;
  }
  
  /**
   * Gets the time zone the dates/times are expected to be converted from.
   * 
   * @return the time zone as a {@link ZoneId}.
   */
  public ZoneId getFromTimeZone() {
    return this.fromTimeZone;
  }
  
  @Override
  public Iterator<Long> iterator() {
    return new Iterator<Long>() {
      
      @Override
      public boolean hasNext() {
        return absTimeConfigNextAvailable();
      }
      
      @Override
      public Long next() {
        
        // TODO: Implement next() in AbsoluteTimeConfiguration.iterator()
        // TODO: DON'T FORGET TO ACCOUNT FOR NONEXISTENT DATES! (February 30, November 31, etc.)
        // TODO: Make the year wildcard store the current year in its pos field
        // Use ZonedDateTime.now(ZoneId.of(INSERT_ISSUERS_TZ_HERE).withZoneSameInstant(ZoneId.of("America/New_York"));
        // LocalDateTime.atZone(ZoneId)
        
        final ZonedDateTime zdt = ZonedDateTime.of(getAndIncrementYear(), getAndIncrementMonth(), getAndIncrementDay(),
            getHour(), getMinute(), 0, 0, getFromTimeZone());
        System.out.println("\nIssuer's LDT: " + zdt.toString() + ".");
        
        // final ZonedDateTime issuerDateTime = ldt.atZone(getFromTimeZone());
        // System.out.println("Issuer's datetime: " + issuerDateTime.toString() + ".");
        
        // final ZonedDateTime myDateTime = zdt.atZone(ZoneId.of("America/New_York"));
        final ZonedDateTime myDateTime = zdt.withZoneSameLocal((ZoneId.of("America/New_York")));
        System.out.println("Bot's LDT: " + myDateTime.toString() + ".");
        
        // Construct new local datetime with current list pointers
        // Convert from issuer's time zone -> my time zone
        // Increment day, ripple carry through months/years
        // RBConfig.getTimeZone()
        
        return myDateTime.toEpochSecond();
      }
      
    };
  }
  
  /**
   * Wrapper for this class' Iterator.hasNext() method to prevent fields from not being private.
   * 
   * @return true if there are still more dates to go; false if not.
   */
  final protected boolean absTimeConfigNextAvailable() {
    return this.monthPos < this.monthList.size() && this.dayPos < this.dayList.size()
        && this.yearPos < this.yearList.size();
  }
  
  final protected Integer getAndIncrementYear() {
    return this.yearList.get(this.yearPos++);
  }
  
  final protected Integer getAndIncrementMonth() {
    return this.monthList.get(this.monthPos++);
  }
  
  final protected Integer getAndIncrementDay() {
    return this.dayList.get(this.dayPos++);
  }
  
  final protected Integer getHour() {
    return this.hour;
  }
  
  final protected Integer getMinute() {
    return this.minute;
  }
  
}

//@formatter:off
/*

HELP message:
  Time Clause: "at [4-digit military time (":" is optional)] or [ Hours:Minutes (AM or PM) ] or [Hours (am or pm)]
    Examples: "at 6pm", "at 1900", "at 5:45 PM"
    (If no time is specified, 6:00 AM will be used)
  Date Clause: "on ( [Months] [Days] [Years] ) or (Month#/Day#/Year#)"
    Months: Specify the month number (1-12) or the first three letters of the month. List them in []'s with a comma separating them. Replace this
      field with an asterisk (*) to specify all months.
    Days: Specify the day number. Lists and asterisks are supported.
    Years: Specify the 4-digit year. Lists and asterisks are supported.
    Examples: "on 7/21/2020", "on dec 25 *", "on [1, 15] * 2019", "on * * *", "on [1,7] [1,7,14,21,28] [2018,2019,2020]"
    (If no date is specified, today will be used unless the time has expired, then tomorrow will be used)
  Reminder Message
    Must start with the word "to".



:: Ideas ::

Shortcut aliases (on-clause):
* "every month" -> "on * 1 *"
* "every day" -> "on * * *"
* "every week" -> "on * [1,7,14,21,28] *"
("tomorrow", "next week", and "next month" are handled by the relative time configuration)

Shortcuts (at-clause):
* "noon"/"in the afternoon" -> 1200 (today)
* "morning"/"in the morning" -> 0600 (today)
* "evening"/"in the evening" -> 1800 (today)
* "night"/"at night" -> 2200 (today)
* "midnight"/"at midnight" -> 0000 (tomorrow)











 */
