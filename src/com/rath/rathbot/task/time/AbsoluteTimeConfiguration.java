
package com.rath.rathbot.task.time;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * This class handles all time configurations that happen at specific points in time. e.g.: at February 10, 2019 at 6:00
 * PM CST.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class AbsoluteTimeConfiguration extends TimeConfiguration {
  
  /** Whether to spam the console with variables and such. */
  private static final boolean DEBUG_MODE = false;
  
  /** Choose spaces before and after the words "at" and "on". */
  private static final String REGEX_SPLIT_CLAUSES = "((?<=at)\\s+)|(\\s+(?=at))|((?<=on)\\s+)|(\\s+(?=on))";
  
  /** Choose spaces between hours, minutes, and optional AM/PM. */
  private static final String REGEX_SPLIT_AT_CLAUSE = "((?<=\\d\\d)\\s*(?=\\d\\d))|(:(?=\\d\\d))|((?<=1?\\d)\\s*(?=[AaPp][Mm]))";
  
  /** Choose any spaces not between square brackets. */
  private static final String REGEX_SPLIT_ON_CLAUSE = "(\\s+(?![^\\[]*\\]))|(\\s*/\\s*)";
  
  /** Match a sequence of comma-separated numbers inside of square brackets. */
  private static final String REGEX_MATCH_BRACKET_LIST = "\\[\\s*((\\d+)|(\\w{3}))(\\s*,\\s*((\\d+)|(\\w{3}))\\s*)*\\s*\\]";
  
  /** The number of next configurations to try before giving up. */
  private static final int VALID_DATE_SEARCH_MAX_ITERATIONS = 8;
  
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
  
  /** A map of three-character months to their numeric counterparts. */
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
  
  /** The position in the month list that will be used next. */
  private int monthPos = -1;
  
  /** The position in the day list that will be used next. */
  private int dayPos = -1;
  
  /** The position in the year list that will be used next. */
  private int yearPos = -1;
  
  /** The hour this time configuration is set to. */
  private int hour = -1;
  
  /** The minute this time configuration is set to. */
  private int minute = -1;
  
  /** If the constructor has parsed an at-clause. */
  private transient boolean parsedAt = false;
  
  /** If the constructor has parsed an on-clause. */
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
    
    // If an at-clause has been parsed, but an on-clause hasn't
    if (this.parsedAt && !this.parsedOn) {
      
      if (DEBUG_MODE) System.out.println("Only at-clause specified.");
      
      final ZonedDateTime zdtNow = ZonedDateTime.now(this.fromTimeZone);
      
      // Initialize the issuer's desired date/time to the start of today, plus the number of hours and minutes specified
      // in the at-clause
      ZonedDateTime desiredZdt = zdtNow.truncatedTo(ChronoUnit.DAYS);
      desiredZdt = desiredZdt.plusHours(this.hour).plusMinutes(this.minute);
      
      if (DEBUG_MODE) System.out.println("Raw desired ZDT=" + desiredZdt + ".");
      
      // If the desired time has already passed for today, use tomorrow instead
      if (zdtNow.compareTo(desiredZdt) >= 0) {
        desiredZdt = desiredZdt.plusDays(1);
      }
      
      if (DEBUG_MODE) System.out.println("Desired ZDT=" + desiredZdt + ".");
      
      // Set the month list to the current month of the desired date/time
      this.monthList = new ArrayList<Integer>();
      this.monthList.add(desiredZdt.getMonthValue());
      this.monthPos = 0;
      
      // Set the day list to the current day of the desired date/time
      this.dayList = new ArrayList<Integer>();
      this.dayList.add(desiredZdt.getDayOfMonth());
      this.dayPos = 0;
      
      // Set the year list to the current year of the desired date/time
      this.yearList = new ArrayList<Integer>();
      this.yearList.add(desiredZdt.getYear());
      this.yearPos = 0;
      
      // If we've parsed an on-clause but not an at-clause
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
  
  /**
   * Gets the time zone the dates/times are expected to be converted from.
   * 
   * @return the time zone as a {@link ZoneId}.
   */
  public ZoneId getFromTimeZone() {
    return this.fromTimeZone;
  }
  
  /**
   * Gets the current year in the year list.
   * 
   * @return an Integer.
   */
  public final Integer getCurrentYear() {
    return this.yearList.get(this.yearPos);
  }
  
  /**
   * Gets the current month in the month list.
   * 
   * @return an Integer.
   */
  public final Integer getCurrentMonth() {
    return this.monthList.get(this.monthPos);
  }
  
  /**
   * Gets the current year in the year list.
   * 
   * @return an Integer.
   */
  public final Integer getCurrentDay() {
    return this.dayList.get(this.dayPos);
  }
  
  /**
   * Gets the hour of this configuration.
   * 
   * @return an Integer.
   */
  public final Integer getHour() {
    return this.hour;
  }
  
  /**
   * Gets the hour of this configuration.
   * 
   * @return an Integer.
   */
  public final Integer getMinute() {
    return this.minute;
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
        
        // TODO: Make the year wildcard store the current year in its pos field
        
        boolean validDateFound = false;
        ZonedDateTime zdt = null;
        
        for (int i = 0; !validDateFound && i < VALID_DATE_SEARCH_MAX_ITERATIONS; i++) {
          
          try {
            zdt = ZonedDateTime.of(getCurrentYear(), getCurrentMonth(), getCurrentDay(), getHour(), getMinute(), 0, 0,
                getFromTimeZone());
          } catch (@SuppressWarnings("unused") DateTimeException dte) {
            propogateCounters();
            continue;
          }
          validDateFound = true;
          
        }
        
        if (zdt == null) {
          System.err.println("Could not find the next valid date/time!");
          return null;
        }
        
        // System.out.println("\nIssuer's LDT: " + zdt.toString() + ", T=" + zdt.toEpochSecond() + ".");
        
        // TODO: Use RBConfig.getTimeZone() in production (surround the get with a try/catch?)
        final ZonedDateTime myDateTime = zdt.withZoneSameLocal((ZoneId.of("America/New_York")));
        // System.out.println("Bot's LDT: " + myDateTime.toString() + ", T=" + myDateTime.toEpochSecond() + ".");
        
        // Increment day, ripple carry through months/years
        propogateCounters();
        
        return myDateTime.toEpochSecond();
      }
      
    };
  }
  
  /**
   * Ripple-carries the day, month, and year pointers.
   */
  final void propogateCounters() {
    
    if (this.dayPos >= this.dayList.size() - 1) {
      
      this.dayPos = 0;
      
      if (this.monthPos >= this.monthList.size() - 1) {
        
        this.monthPos = 0;
        this.yearPos++;
        
      } else {
        this.monthPos++;
      }
      
    } else {
      this.dayPos++;
    }
    
  }
  
  /**
   * Generates a comma-separated list of numbers enclosed in square brackets.
   * 
   * @param min The first and lowest value, inclusive.
   * @param max The last and highest value, inclusive.
   * @return a {@link String}.
   */
  private static final String generateAsteriskString(final int min, final int max) {
    String result = "[" + min;
    for (int i = min + 1; i <= max; i++) {
      result += "," + i;
    }
    return result + "]";
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
    
    if (DEBUG_MODE) System.out.println("Parsing on-clause.");
    
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
      
      // If the desired time has already passed for today, use tomorrow's date
      if (ZonedDateTime.now(this.fromTimeZone).compareTo(desiredZdt) >= 0) {
        desiredZdt = desiredZdt.plusYears(1);
      }
      
      this.yearList = new ArrayList<Integer>();
      this.yearList.add(desiredZdt.getYear());
      this.yearPos = 0;
    }
    
    this.parsedOn = true;
    if (DEBUG_MODE) System.out.println("On-clause parse finished.");
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
      
      if (DEBUG_MODE) System.out.println("Is not bracket list.");
      
      // Translate the asterisk to its alias
      if (monthsString.equals("*")) {
        
        if (DEBUG_MODE) System.out.println("Is asterisk.");
        monthsString = ASTERISK_MONTHS;
        
      } else {
        
        // Check if the current token is a three-letter month abbreviation
        int m = -1;
        monthsString = monthsString.trim().toLowerCase();
        
        if (MONTH_ALIASES.containsKey(monthsString)) {
          m = MONTH_ALIASES.get(monthsString);
        } else {
          
          if (DEBUG_MODE) System.out.println("Months string: " + monthsString);
          
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
      
    } else {
      if (DEBUG_MODE) System.out.println("Is bracket list.");
    }
    
    // Trim square brackets and split on comma
    monthsString = monthsString.trim();
    monthsString = monthsString.substring(1, monthsString.length() - 1);
    monthTokens = monthsString.split("\\s*,\\s*");
    
    if (DEBUG_MODE) System.out.println("Month tokens: " + Arrays.toString(monthTokens));
    
    // Convert each index to an integer and add it to the months list
    for (String monthTok : monthTokens) {
      
      int m = -1;
      monthTok = monthTok.trim();
      
      if (DEBUG_MODE) System.out.println("Month token: \"" + monthTok + "\".");
      
      // Check if the current token is a three-letter month abbreviation
      if (MONTH_ALIASES.containsKey(monthTok.toLowerCase())) {
        m = MONTH_ALIASES.get(monthTok.toLowerCase());
        if (DEBUG_MODE) System.out.println("Translated to " + m + ".");
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
        daysString = daysString.trim();
        
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
    daysString = daysString.trim();
    daysString = daysString.substring(1, daysString.length() - 1);
    dayTokens = daysString.split("\\s*,\\s*");
    
    // Convert each index to an integer and add it to the days list
    for (String dayTok : dayTokens) {
      
      int d = -1;
      dayTok = dayTok.trim();
      
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
        yearsString = yearsString.trim();
        
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
    yearsString = yearsString.trim();
    yearsString = yearsString.substring(1, yearsString.length() - 1);
    yearTokens = yearsString.split("\\s*,\\s*");
    
    // Convert each index to an integer and add it to the years list
    for (String yearTok : yearTokens) {
      
      int d = -1;
      yearTok = yearTok.trim();
      
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
   * Wrapper for this class' Iterator.hasNext() method to prevent fields from not being private.
   * 
   * @return true if there are still more dates to go; false if not.
   */
  protected final boolean absTimeConfigNextAvailable() {
    return this.yearPos < this.yearList.size() && this.dayPos < this.dayList.size()
        && this.monthPos < this.monthList.size();
  }
  
}

// TODO: ** Don't delete the help message; you need that.
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
