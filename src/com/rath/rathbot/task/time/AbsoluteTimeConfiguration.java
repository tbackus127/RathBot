
package com.rath.rathbot.task.time;

import java.time.ZoneId;
import java.util.Iterator;

// Use ZonedDateTime.now(ZoneId.of(INSERT_ISSUERS_TZ_HERE).withZoneSameInstant(ZoneId.of("America/New_York"));
// LocalDateTime.atZone(ZoneId)

public class AbsoluteTimeConfiguration extends TimeConfiguration {
  
  private final ZoneId issuersTimeZone;
  
  private final int[] monthList = { 1 };
  private final int[] dayList = { 1 };
  private final int[] yearList = { 1 };
  
  private int monthPos = -1;
  private int dayPos = -1;
  private int yearPos = -1;
  
  public AbsoluteTimeConfiguration(final String configString, final ZoneId timeZone) {
    super(TimeConfigurationType.ABSOLUTE, configString);
    this.issuersTimeZone = timeZone;
    
    // TODO: Parse config string and set month/day/year lists here.
    
  }
  
  public ZoneId getIssuersTimeZone() {
    return this.issuersTimeZone;
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
        // TODO Auto-generated method stub
        return null;
      }
      
    };
  }
  
  final protected boolean absTimeConfigNextAvailable() {
    return this.monthPos < this.monthList.length - 1 && this.dayPos < this.dayList.length - 1
        && this.yearPos < this.yearList.length - 1;
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



at-clause regex: (at\s+(([01]?\d)|(2[0123]))(:[0-5]\d)?\s*([AaPp][Mm])?)

X month regex: (^([Jj][Aa]|[Jj][Uu][LlNn]|[Ff]|[Aa][PpUu]|[Mm][Aa][RrYy]|[Ss]|[Dd]|[Oo]|[Nn]))|(\*)
  months regex: 
X day regex: (([12]\d)|(3[01])|([1-9]))
  days regex: (\[\s*((\d+\s*\,\s*){0,29}\d+)\s*\])|\d+|\*
X years regex: \d{4}|(\*)

"at 1540 on [jul,aug,oct,nov,dec] * [2019,2020]"; issuer lives in TZ-3

Months: {7, 8, 10, 11, 12}
Days: {1, 2, ..., 31}
Years: {2019, 2020}

Hour: 18
Minute: 40



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