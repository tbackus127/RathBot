
package com.rath.rathbot.task.time;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TreeMap;

import com.rath.rathbot.RBConfig;

// Use ZonedDateTime.now(ZoneId.of(INSERT_ISSUERS_TZ_HERE).withZoneSameInstant(ZoneId.of("America/New_York"));
// LocalDateTime.atZone(ZoneId)

public class AbsoluteTimeConfiguration extends TimeConfiguration {
  
  private static final int ON_CLAUSE_TOKEN_LENGTH = 3;
  
  private static final int[] ALIAS_ASTERISK_MONTHS = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
  
  private static final TreeMap<String, Integer> MONTH_MAP = new TreeMap<String, Integer>() {
    
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
  
  private final int[][] dateCombinations;
  
  private char itrPosMonth = 0;
  
  private char itrPosDay = 0;
  
  private char itrPosYear = 0;
  
  private final ZoneId timeZone;
  
  private final ZonedDateTime localDateTime;
  
  public AbsoluteTimeConfiguration(final String configString, final ZoneId zoneID) {
    super(TimeConfigurationType.ABSOLUTE, configString);
    
    // Set fields
    this.timeZone = zoneID;
    
    // TODO: Separate into on-clause and at-clause
    final String[] clauses = getOnAtClause(this.config);
    
    this.dateCombinations = calcDateCombinations(clauses[0]);
    final LocalTime atTime = calcTime(clauses[1]);
    
    // TODO: Also set time if given; 06:00 if not. Don't forget to convert from user's TZ -> Config.getTimeZone()
    final int year = this.dateCombinations[2][0];
    final int month = this.dateCombinations[0][0];
    final int day = this.dateCombinations[1][0];
    
    // TODO: Pack hour/minute into a LocalTime object with a method, return 06:00 if at-clause missing
    
    this.localDateTime = ZonedDateTime.of(LocalDate.of(year, month, day), atTime, this.timeZone).withZoneSameInstant(
        RBConfig.getTimeZone());
  }
  
  private LocalTime calcTime(String string) {
    // TODO Auto-generated method stub
    return null;
  }
  
  private String[] getOnAtClause(String config) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public final long getNextEpochTime() {
    
    // TODO: getNextEpochTime() in AbsoluteTimeConfiguration.java
    return -1L;
    
  }
  
  // TODO: JavaDoc this
  @SuppressWarnings("static-method")
  public final long peekNextEpochTime() {
    
    // TODO: peekNextEpochTime() in AbsoluteTimeConfiguration.java
    return -1L;
    
  }
  
  /**
   * Gets the time zone's ID.
   * 
   * @return the ID as a ZoneId.
   */
  public ZoneId getTimeZone() {
    return this.timeZone;
  }
  
  @Override
  public boolean doesRepeat() {
    
    // TODO Auto-generated method stub
    return false;
    
  }
  
  /**
   * Calculate date combinations for the given config string.
   * 
   * @param config the config String.
   * @return the fully-expanded date combination as a length-3 array of int arrays.
   */
  private static final int[][] calcDateCombinations(String config) {
    
    // Make sure this is an actual on-clause
    if (!config.startsWith("on ")) {
      
      // TODO: Throw an exception here, invalid on-clause
      
      return null;
    }
    
    final String[] onClauseTokens = config.substring(3).split("\\s+");
    final int[][] expLists = convertToExplicitLists(onClauseTokens);
    
    if (!listsInOrder(expLists)) {
      // TODO: Throw an exception here, lists aren't in order
    }
    
    return expLists;
    
  }
  
  /**
   * Checks if all date combinations are in order. This is required to ensure getNextEpochTime() returns the next epoch
   * second instead of a far-off one, or a previous one.
   * 
   * @param expOnTokens the expanded on-clause list.
   * @return true if the lists are sorted; false if not.
   */
  private static final boolean listsInOrder(final int[][] expOnTokens) {
    
    // Iterate through months, days, years
    for (int i = 0; i < expOnTokens.length; i++) {
      int lastValue = 0;
      
      // Iterate through each ordinal
      for (int j = 0; j < expOnTokens[i].length; j++) {
        
        // If we find an out-of-order value, the list isn't in order
        final int curr = expOnTokens[i][j];
        if (lastValue >= curr) {
          return false;
        }
        
        // Keep track of the last value checked
        lastValue = curr;
      }
    }
    return true;
  }
  
  // {*, [1,7], 2018} -> {{1,2,3,4,5,6,7,8,9,10,11,12}, {1,7}, {2018}}
  /**
   * Expands on-clause tokens into three integer lists.
   * 
   * @param onClauseTokens tokens from the on-clause of a config string as a String array. Ex: {"*", "[1,7]", "2018"}.
   * @return the expanded and converted on-clause. Ex: {{1,2,3,4,5,6,7,8,9,10,11,12}, {1,7}, {2018}}.
   */
  private static final int[][] convertToExplicitLists(final String[] onClauseTokens) {
    
    // Allocate three variable-length integer lists and iterate through the on-clause tokens
    final int[][] result = new int[ON_CLAUSE_TOKEN_LENGTH][];
    for (int onIdx = 0; onIdx < onClauseTokens.length; onIdx++) {
      
      final String currOnTok = onClauseTokens[onIdx];
      
      // Wildcard handling
      if (currOnTok.equals("*")) {
        
        // Only allow in months token
        if (onIdx != 0) {
          
          // TODO: Throw an exception here, asterisk disallowed in non-month column
          return null;
        }
        result[onIdx] = ALIAS_ASTERISK_MONTHS;
        continue;
      }
      
      // Handle lists
      else if (currOnTok.length() > 2 && currOnTok.startsWith("[") && currOnTok.endsWith("]")) {
        
        // Split the list by commas
        final String[] listTokens = currOnTok.substring(1, currOnTok.length() - 1).split(",");
        
        // Create a new subarray and parse each thing in the list as an int
        result[onIdx] = new int[listTokens.length];
        for (int j = 0; j < listTokens.length; j++) {
          
          final String currListTok = listTokens[j];
          
          // Use the month map for month parsing if we're on the month token
          if (onIdx == 0) {
            result[onIdx][j] = MONTH_MAP.get(currListTok.substring(0, 3).toLowerCase());
            
          } else {
            try {
              result[onIdx][j] = Integer.parseInt(currListTok);
            } catch (NumberFormatException nfe) {
              
              // TODO: Create a new exception that maps onIdx -> column name and throw it
              nfe.printStackTrace();
            }
          }
          
        }
      }
      
      // Handle single elements
      else {
        result[onIdx] = new int[1];
        
        try {
          result[onIdx][0] = Integer.parseInt(currOnTok);
        } catch (NumberFormatException nfe) {
          
          // TODO: Create a new exception that maps onIdx -> column name and throw it
          nfe.printStackTrace();
        }
      }
    }
    
    return result;
  }
  
}
