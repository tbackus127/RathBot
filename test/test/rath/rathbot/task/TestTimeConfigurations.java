
package test.rath.rathbot.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;

import com.rath.rathbot.task.time.AbsoluteTimeConfiguration;
import com.rath.rathbot.task.time.BadTimeConfigException;
import com.rath.rathbot.task.time.InternalTimeConfigException;
import com.rath.rathbot.task.time.RelativeTimeConfiguration;

public class TestTimeConfigurations {
  
  public static final ZoneId TEST_ZONE_ID = ZoneId.of("America/New_York");
  
  public static final int SPAN_FLAG_MINUTE = 1;
  public static final int SPAN_FLAG_HOUR = 2;
  public static final int SPAN_FLAG_DAY = 4;
  public static final int SPAN_FLAG_WEEK = 8;
  public static final int SPAN_FLAG_MONTH = 16;
  public static final int SPAN_FLAG_YEAR = 32;
  
  private static final int MAX_WILDCARD_ITERATIONS = 200;
  private static final int RELATIVE_TEST_SIZE = 10;
  
  private static final long RELATIVE_EPOCH_SECOND_ERR = 3;
  
  static final ZonedDateTime ZDT_NOW = ZonedDateTime.now(TEST_ZONE_ID);
  static final ZonedDateTime ZDT_START_OF_TODAY = ZDT_NOW.truncatedTo(ChronoUnit.DAYS);
  static final ZonedDateTime ZDT_START_OF_TOMORROW = ZDT_START_OF_TODAY.plusDays(1);
  
  static final ZonedDateTime ZDT_START_OF_THIS_YEAR = ZDT_START_OF_TODAY.withDayOfYear(1);
  static final ZonedDateTime ZDT_START_OF_NEXT_YEAR = ZDT_START_OF_THIS_YEAR.plusYears(1);
  
  static final int THIS_YEAR = ZDT_START_OF_TODAY.getYear();
  static final int NEXT_YEAR = THIS_YEAR + 1;
  
  //@formatter:off
  // Bad format; regex will not parse/split
  private static final String[] BAD_FORMAT_ABS_CONFIGS = { 
      "", " ", "\n", ",", "an", "at", "on", "to", "at ", "\nat", "at m", "at mon", "on 6", "at []", "*", "at *", "on *",
      "on dec 6pm", "at dec on 3:40", "at 4:00 on 3PM", "on dec 25 2019 at", "on 7 * at", "at 7pm on 4 5 [2018,]",
      "on 4:40PM on dec 13 2023",  "on mar 16 * at *", "on 1//3", "on /12/9/18", "at 9am on [] * *",
      "on [1,4] [6,12,18] []", "on [1,7,14,21,28] * [*]", "on [", "on jan]", "at 6:00ap"
  };
  
  // Correct format, but out-of-range dates/times (months:1-12, days:1-31, years:2018-2030)
  private static final String[] BAD_SINGLE_ABS_CONFIGS = {
      "on 13 2 2018", "on 18/18/18", "at 13am", "at 21pm", "at 2400", "at 9:00 AM on mar 32", "at 0 on 0 0 0", "at 0000 on 1 0 2020",
      "on jan 31 2017", "on 6/6/12", "at 0700 on apr 14 2031"
  };

  // Lists that contain invalid dates/times
  private static final String[] BAD_MULTI_ABS_CONFIGS = {
      "on [1,2,3,4,13] 1 2020", "on [1,4,7,10] [0,14,28] 2020"
  };
  
  private static final String[] BAD_FORMAT_REL_CONFIGS = {
      "in", "in ", "\nin", "in\n", "in, 4", "in 3", "in 111", "every", "every ", "\nevery", "every\n", "every 9", "ever",
      "every 12foo", "every 16o", "in 6p", "in 3L"
  };
  
  @SuppressWarnings("serial")
  private static final AbsoluteTimeTestEntry[] GOOD_SINGLE_ABS_CONFIGS = {
      
      new AbsoluteTimeTestEntry("at 6pm", new ArrayList<Long>() {
        { 
          add( (ZDT_START_OF_TODAY.plusHours(18).compareTo(ZDT_NOW) > 0 ) ?
              (ZDT_START_OF_TODAY.plusHours(18)).toEpochSecond() :
              (ZDT_START_OF_TOMORROW.plusHours(18)).toEpochSecond()
          );
        }
      }),
      
      new AbsoluteTimeTestEntry("at 2100", new ArrayList<Long>() {
        { 
          add( (ZDT_START_OF_TODAY.plusHours(21).compareTo(ZDT_NOW) > 0 ) ?
              (ZDT_START_OF_TODAY.plusHours(21)).toEpochSecond() :
              (ZDT_START_OF_TOMORROW.plusHours(21)).toEpochSecond()
          );
        }
      }),
      
      new AbsoluteTimeTestEntry("at 2AM", new ArrayList<Long>() {
        { 
          add( (ZDT_START_OF_TODAY.plusHours(2).compareTo(ZDT_NOW) > 0 ) ?
              (ZDT_START_OF_TODAY.plusHours(2)).toEpochSecond() :
              (ZDT_START_OF_TOMORROW.plusHours(2)).toEpochSecond()
          );
        }
      }),
      
      new AbsoluteTimeTestEntry("at 7 AM", new ArrayList<Long>() {
        { 
          add( (ZDT_START_OF_TODAY.plusHours(7).compareTo(ZDT_NOW) > 0 ) ?
              (ZDT_START_OF_TODAY.plusHours(7)).toEpochSecond() :
              (ZDT_START_OF_TOMORROW.plusHours(7)).toEpochSecond()
          );
        }
      }),
      
      new AbsoluteTimeTestEntry("on 4 1", new ArrayList<Long>() {
        { 
          add( (ZDT_START_OF_THIS_YEAR.plusMonths(3).compareTo(ZDT_NOW) > 0 ) ?
              (ZDT_START_OF_THIS_YEAR.plusMonths(3).toEpochSecond()) :
              (ZDT_START_OF_NEXT_YEAR.plusMonths(3).toEpochSecond())
          );
        }
      }),
      
      new AbsoluteTimeTestEntry("on 6 28 2029", new ArrayList<Long>() {
        { 
          add(ZonedDateTime.of(2029, 6, 28, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
        }
      }),
      
      new AbsoluteTimeTestEntry("on 7/7/2019", new ArrayList<Long>() {
        {
          add(ZonedDateTime.of(2019, 7, 7, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
        }
      }),
      
      new AbsoluteTimeTestEntry("on 8/11/19", new ArrayList<Long>() {
        {
          add(ZonedDateTime.of(2019, 8, 11, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
        }
      }),
      
      new AbsoluteTimeTestEntry("on feb 14", new ArrayList<Long>() {
        {
          add( (ZDT_START_OF_THIS_YEAR.plusMonths(1).plusDays(13).compareTo(ZDT_NOW) > 0 ) ?
              (ZDT_START_OF_THIS_YEAR.plusMonths(1).plusDays(13).toEpochSecond()) :
              (ZDT_START_OF_NEXT_YEAR.plusMonths(1).plusDays(13).toEpochSecond())
          );
        }
      }),
      
      new AbsoluteTimeTestEntry("on may 10 2020", new ArrayList<Long>() {
        {
          add(ZonedDateTime.of(2020, 5, 10, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
        }
      }),
      
      new AbsoluteTimeTestEntry("on jun 14 at 4pm", new ArrayList<Long>() {
        {
          add( (ZDT_START_OF_THIS_YEAR.plusMonths(5).plusDays(13).plusHours(16).compareTo(ZDT_NOW) > 0 ) ?
              (ZDT_START_OF_THIS_YEAR.plusMonths(5).plusDays(13).plusHours(16).toEpochSecond()) :
              (ZDT_START_OF_NEXT_YEAR.plusMonths(5).plusDays(13).plusHours(16).toEpochSecond())
          );
        }
      }),
      
      new AbsoluteTimeTestEntry("on oct 21 at 6:00 PM", new ArrayList<Long>() {
        {
          add( (ZDT_START_OF_THIS_YEAR.plusMonths(9).plusDays(20).plusHours(18).compareTo(ZDT_NOW) > 0 ) ?
              (ZDT_START_OF_THIS_YEAR.plusMonths(9).plusDays(20).plusHours(18).toEpochSecond()) :
              (ZDT_START_OF_NEXT_YEAR.plusMonths(9).plusDays(20).plusHours(18).toEpochSecond())
          );
        }
      }),
      
      new AbsoluteTimeTestEntry("on nov 26 at 2200", new ArrayList<Long>() {
        { 
          add( (ZDT_START_OF_THIS_YEAR.plusMonths(10).plusDays(25).plusHours(22).compareTo(ZDT_NOW) > 0 ) ?
              (ZDT_START_OF_THIS_YEAR.plusMonths(10).plusDays(25).plusHours(22).toEpochSecond()) :
              (ZDT_START_OF_NEXT_YEAR.plusMonths(10).plusDays(25).plusHours(22).toEpochSecond())
          );
        }
      }),
      
      new AbsoluteTimeTestEntry("on feb/14/2024", new ArrayList<Long>() {
        {
          add(ZonedDateTime.of(2024, 2, 14, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
        }
      }),
      
      new AbsoluteTimeTestEntry("at 4:53 pm on dec 31 2021", new ArrayList<Long>() {
        {
          add(ZonedDateTime.of(2021, 12, 31, 16, 53, 0, 0, TEST_ZONE_ID).toEpochSecond());
        }
      }),
  };
  
  @SuppressWarnings("serial")
  private static final AbsoluteTimeTestEntry[] GOOD_MULTI_ABS_CONFIGS = {
      
      new AbsoluteTimeTestEntry("on [1,4, 5] 1 2021", new ArrayList<Long>() {
        {
          add(ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2021, 4, 1, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2021, 5, 1, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
        }
      }),
      
      new AbsoluteTimeTestEntry("on feb [2,10,18,24] 2023", new ArrayList<Long>() {
        {
          add(ZonedDateTime.of(2023, 2, 2, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2023, 2, 10, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2023, 2, 18, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2023, 2, 24, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
        }
      }),
      
      new AbsoluteTimeTestEntry("on [mar, may, aug, dec] [1] 2025", new ArrayList<Long>() {
        {
          add(ZonedDateTime.of(2025, 3, 1, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2025, 5, 1, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2025, 8, 1, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2025, 12, 1, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
        }
      }),
      
      new AbsoluteTimeTestEntry("on [jan] [1,7,28] [2023, 2024]", new ArrayList<Long>() {
        {
          add(ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2023, 1, 7, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2023, 1, 28, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2024, 1, 7, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2024, 1, 28, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
        }
      }),
      
      new AbsoluteTimeTestEntry("on [may, sep, oct, dec] [1,15,30] [2024]", new ArrayList<Long>() {
        {
          add(ZonedDateTime.of(2024, 5, 1, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2024, 5, 15, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2024, 5, 30, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2024, 9, 1, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2024, 9, 15, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2024, 9, 30, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2024, 10, 1, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2024, 10, 15, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2024, 10, 30, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2024, 12, 1, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2024, 12, 15, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2024, 12, 30, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
        }
      }),
      
      new AbsoluteTimeTestEntry("on dec 25 [2021,2022, 2023, 2024, 2025]", new ArrayList<Long>() {
        {
          add(ZonedDateTime.of(2021, 12, 25, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2022, 12, 25, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2023, 12, 25, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2024, 12, 25, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2025, 12, 25, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
        }
      }),
      
      new AbsoluteTimeTestEntry("on * * 2026", generateAll2026()),
      
      new AbsoluteTimeTestEntry("on [5,7]/14/2020", new ArrayList<Long>() {
        {
          add(ZonedDateTime.of(2020, 5, 14, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2020, 7, 14, 0, 0, 0, 0, TEST_ZONE_ID).toEpochSecond());
        }
      }),
      
      new AbsoluteTimeTestEntry("at 10:05 AM on jan/[1, 27]/[2023]", new ArrayList<Long>() {
        {
          add(ZonedDateTime.of(2023, 1, 1, 10, 5, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2023, 1, 27, 10, 5, 0, 0, TEST_ZONE_ID).toEpochSecond());
        }
      }),
      new AbsoluteTimeTestEntry("at 1700 on */[1,8,20]/2024", generate1820()),
      
      new AbsoluteTimeTestEntry("on */*/* at 1400", generateAll14()),
      
      new AbsoluteTimeTestEntry("on [ jan , dec ] [ 30 , 31 ] [ 2020, 2022] at 21:30", new ArrayList<Long>() {
        {
          add(ZonedDateTime.of(2020, 1, 30, 21, 30, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2020, 1, 31, 21, 30, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2020, 12, 30, 21, 30, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2020, 12, 31, 21, 30, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2022, 1, 30, 21, 30, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2022, 1, 31, 21, 30, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2022, 12, 30, 21, 30, 0, 0, TEST_ZONE_ID).toEpochSecond());
          add(ZonedDateTime.of(2022, 12, 31, 21, 30, 0, 0, TEST_ZONE_ID).toEpochSecond());
        }
      }),
      
      new AbsoluteTimeTestEntry("on [may, jun,jul ] [15, 30 ]", new ArrayList<Long>() {
        {
          add( (ZDT_START_OF_THIS_YEAR.plusMonths(4).plusDays(14).compareTo(ZDT_NOW) > 0 ) ?
              (ZDT_START_OF_THIS_YEAR.plusMonths(4).plusDays(14).toEpochSecond()) :
              (ZDT_START_OF_NEXT_YEAR.plusMonths(4).plusDays(14).toEpochSecond())
          );
          add( (ZDT_START_OF_THIS_YEAR.plusMonths(4).plusDays(29).compareTo(ZDT_NOW) > 0 ) ?
              (ZDT_START_OF_THIS_YEAR.plusMonths(4).plusDays(29).toEpochSecond()) :
              (ZDT_START_OF_NEXT_YEAR.plusMonths(4).plusDays(29).toEpochSecond())
          );
          add( (ZDT_START_OF_THIS_YEAR.plusMonths(5).plusDays(14).compareTo(ZDT_NOW) > 0 ) ?
              (ZDT_START_OF_THIS_YEAR.plusMonths(5).plusDays(14).toEpochSecond()) :
              (ZDT_START_OF_NEXT_YEAR.plusMonths(5).plusDays(14).toEpochSecond())
          );
          add( (ZDT_START_OF_THIS_YEAR.plusMonths(5).plusDays(29).compareTo(ZDT_NOW) > 0 ) ?
              (ZDT_START_OF_THIS_YEAR.plusMonths(5).plusDays(29).toEpochSecond()) :
              (ZDT_START_OF_NEXT_YEAR.plusMonths(5).plusDays(29).toEpochSecond())
          );
          add( (ZDT_START_OF_THIS_YEAR.plusMonths(6).plusDays(14).compareTo(ZDT_NOW) > 0 ) ?
              (ZDT_START_OF_THIS_YEAR.plusMonths(6).plusDays(14).toEpochSecond()) :
              (ZDT_START_OF_NEXT_YEAR.plusMonths(6).plusDays(14).toEpochSecond())
          );
          add( (ZDT_START_OF_THIS_YEAR.plusMonths(6).plusDays(29).compareTo(ZDT_NOW) > 0 ) ?
              (ZDT_START_OF_THIS_YEAR.plusMonths(6).plusDays(29).toEpochSecond()) :
              (ZDT_START_OF_NEXT_YEAR.plusMonths(6).plusDays(29).toEpochSecond())
          );
        }
      }),
  };
  
  @SuppressWarnings("serial")
  private static final RelativeTimeTestEntry[] GOOD_RELATIVE_CONFIGS = {
      new RelativeTimeTestEntry("1m", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(1);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.MINUTES);
        }
      })),
      new RelativeTimeTestEntry("7m", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(7);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.MINUTES);
        }
      })),
      new RelativeTimeTestEntry("40m", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(40);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.MINUTES);
        }
      })),
      new RelativeTimeTestEntry("177m", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(177);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.MINUTES);
        }
      })),
      new RelativeTimeTestEntry("1h", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(1);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.HOURS);
        }
      })),
      new RelativeTimeTestEntry("9h", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(9);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.HOURS);
        }
      })),
      new RelativeTimeTestEntry("23h", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(23);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.HOURS);
        }
      })),
      new RelativeTimeTestEntry("76h", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(76);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.HOURS);
        }
      })),
      new RelativeTimeTestEntry("1d", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(1);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.DAYS);
        }
      })),
      new RelativeTimeTestEntry("6d", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(6);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.DAYS);
        }
      })),
      new RelativeTimeTestEntry("28d", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(28);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.DAYS);
        }
      })),
      new RelativeTimeTestEntry("117d", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(117);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.DAYS);
        }
      })),
      new RelativeTimeTestEntry("1w", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(1);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.WEEKS);
        }
      })),
      new RelativeTimeTestEntry("4w", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(4);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.WEEKS);
        }
      })),
      new RelativeTimeTestEntry("33w", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(33);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.WEEKS);
        }
      })),
      new RelativeTimeTestEntry("201w", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(201);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.WEEKS);
        }
      })),
      new RelativeTimeTestEntry("1M", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(1);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.MONTHS);
        }
      })),
      new RelativeTimeTestEntry("4M", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(4);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.MONTHS);
        }
      })),
      new RelativeTimeTestEntry("11M", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(11);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.MONTHS);
        }
      })),
      new RelativeTimeTestEntry("35M", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(35);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.MONTHS);
        }
      })),
      new RelativeTimeTestEntry("1y", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(1);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.YEARS);
        }
      })),
      new RelativeTimeTestEntry("3y", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(3);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.YEARS);
        }
      })),
      new RelativeTimeTestEntry("2h3m", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(2);
          add(3);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.HOURS);
          add(ChronoUnit.MINUTES);
        }
      })),
      new RelativeTimeTestEntry("8d9h13m", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(8);
          add(9);
          add(13);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.DAYS);
          add(ChronoUnit.HOURS);
          add(ChronoUnit.MINUTES);
        }
      })),
      new RelativeTimeTestEntry("3w4d30m", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(3);
          add(4);
          add(30);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.WEEKS);
          add(ChronoUnit.DAYS);
          add(ChronoUnit.MINUTES);
        }
      })),
      new RelativeTimeTestEntry("2M3d", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(2);
          add(3);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.MONTHS);
          add(ChronoUnit.DAYS);
        }
      })),
      new RelativeTimeTestEntry("1y4M7m", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(1);
          add(4);
          add(7);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.YEARS);
          add(ChronoUnit.MONTHS);
          add(ChronoUnit.MINUTES);
        }
      })),
      new RelativeTimeTestEntry("2y9M3w1d7h50m", generateRelativeMultiples(new ArrayList<Integer>() {
        {
          add(2);
          add(9);
          add(3);
          add(1);
          add(7);
          add(50);
        }
      }, new ArrayList<ChronoUnit>() {
        {
          add(ChronoUnit.YEARS);
          add(ChronoUnit.MONTHS);
          add(ChronoUnit.WEEKS);
          add(ChronoUnit.DAYS);
          add(ChronoUnit.HOURS);
          add(ChronoUnit.MINUTES);
        }
      }))
  };
  //@formatter:on
  
  // at */*/* at 1400
  private static final ArrayList<Long> generateAll14() {
    
    final ZonedDateTime currZdt = ZonedDateTime.now(TEST_ZONE_ID);
    final ArrayList<Long> result = new ArrayList<Long>();
    int iterations = 0;
    
    for (int year = THIS_YEAR; year <= 2026; year++) {
      
      for (int month = 1; month <= 12; month++) {
        
        for (int day = 1; day <= 31; day++) {
          
          // Only allow a certain amount of wildcard iterations to prevent infinite loops
          if (iterations >= MAX_WILDCARD_ITERATIONS) {
            break;
          }
          
          // Try constructing the current date, and if invalid, try another
          LocalDate d;
          try {
            d = LocalDate.of(year, month, day);
          } catch (@SuppressWarnings("unused") DateTimeException dte) {
            continue;
          }
          
          final ZonedDateTime genZdt = ZonedDateTime.of(d, LocalTime.of(14, 0), TEST_ZONE_ID);
          if (genZdt.compareTo(currZdt) <= 0) {
            continue;
          }
          
          result.add(genZdt.toEpochSecond());
          iterations++;
          
        }
      }
    }
    
    return result;
  }
  
  // on */[1,8,20]/2024
  private static final ArrayList<Long> generate1820() {
    
    final ArrayList<Long> result = new ArrayList<Long>();
    
    final int[] days = { 1, 8, 20 };
    
    for (int month = 1; month <= 12; month++) {
      
      for (int i = 0; i < days.length; i++) {
        final int day = days[i];
        
        ZonedDateTime zdt;
        try {
          zdt = ZonedDateTime.of(2024, month, day, 17, 0, 0, 0, TEST_ZONE_ID);
        } catch (@SuppressWarnings("unused") DateTimeException dte) {
          continue;
        }
        
        result.add(zdt.toEpochSecond());
        
      }
      
    }
    
    return result;
  }
  
  // on * * 2026
  private static final ArrayList<Long> generateAll2026() {
    
    final ArrayList<Long> result = new ArrayList<Long>();
    
    int iterations = 0;
    for (int month = 1; month <= 12; month++) {
      
      for (int day = 1; day <= 31; day++) {
        
        if (iterations >= MAX_WILDCARD_ITERATIONS) {
          break;
        }
        
        ZonedDateTime zdt;
        try {
          zdt = ZonedDateTime.of(2026, month, day, 0, 0, 0, 0, TEST_ZONE_ID);
        } catch (@SuppressWarnings("unused") DateTimeException dte) {
          continue;
        }
        
        result.add(zdt.toEpochSecond());
        iterations++;
      }
      
    }
    
    return result;
  }
  
  private static final ArrayList<Long> generateRelativeMultiples(final ArrayList<Integer> intervals,
      final ArrayList<ChronoUnit> units) {
    
    final ArrayList<Long> result = new ArrayList<Long>();
    
    for (int i = 1; i <= RELATIVE_TEST_SIZE; i++) {
      
      ZonedDateTime zdt = ZDT_NOW;
      for (int j = 0; j < intervals.size(); j++) {
        
        zdt = zdt.plus(intervals.get(j) * i, units.get(j));
        
      }
      
      result.add(zdt.toEpochSecond());
      
    }
    
    return result;
  }
  
  @Test
  @SuppressWarnings("static-method")
  public void testBadFormatAbsConfigs() {
    
    System.out.println("=== Testing bad absolute time configs ===");
    
    int numErrors = 0;
    for (int i = 0; i < BAD_FORMAT_ABS_CONFIGS.length; i++) {
      
      final String config = BAD_FORMAT_ABS_CONFIGS[i];
      System.out.print("Testing config: \"" + config + "\":");
      
      boolean caughtExc = false;
      
      try {
        new AbsoluteTimeConfiguration(config, TEST_ZONE_ID);
      } catch (@SuppressWarnings("unused") BadTimeConfigException btc) {
        caughtExc = true;
      } catch (@SuppressWarnings("unused") final InternalTimeConfigException itc) {
        System.out.println(" INTERNAL EXCEPTION");
        numErrors++;
        continue;
      }
      
      if (caughtExc) {
        numErrors++;
        System.out.println(" OK");
      } else {
        System.out.println(" NOEXCEPT");
      }
    }
    
    System.out.println();
    
    assertEquals(numErrors, BAD_FORMAT_ABS_CONFIGS.length);
    
  }
  
  @Test
  @SuppressWarnings("static-method")
  public void testBadSingleAbsConfigs() {
    
    System.out.println("=== Testing bad single absolute time configs ===");
    
    int numErrors = 0;
    for (int i = 0; i < BAD_SINGLE_ABS_CONFIGS.length; i++) {
      
      final String config = BAD_SINGLE_ABS_CONFIGS[i];
      System.out.print("Testing config: \"" + config + "\":");
      
      boolean caughtExc = false;
      
      try {
        new AbsoluteTimeConfiguration(config, TEST_ZONE_ID);
      } catch (@SuppressWarnings("unused") BadTimeConfigException btc) {
        caughtExc = true;
      } catch (@SuppressWarnings("unused") final InternalTimeConfigException itc) {
        System.out.println(" INTERNAL EXCEPTION");
        numErrors++;
        continue;
      }
      
      if (caughtExc) {
        numErrors++;
        System.out.println(" OK");
      } else {
        System.out.println(" NOEXCEPT");
      }
    }
    
    System.out.println();
    
    assertEquals(numErrors, BAD_SINGLE_ABS_CONFIGS.length);
    
  }
  
  @Test
  @SuppressWarnings("static-method")
  public void testBadMultiAbsConfigs() {
    
    System.out.println("=== Testing bad multi absolute time configs ===");
    
    int numErrors = 0;
    for (int i = 0; i < BAD_MULTI_ABS_CONFIGS.length; i++) {
      
      final String config = BAD_MULTI_ABS_CONFIGS[i];
      System.out.print("Testing config: \"" + config + "\":");
      
      boolean caughtExc = false;
      
      try {
        new AbsoluteTimeConfiguration(config, TEST_ZONE_ID);
      } catch (@SuppressWarnings("unused") BadTimeConfigException btc) {
        caughtExc = true;
      }
      
      if (caughtExc) {
        numErrors++;
        System.out.println(" OK");
      } else {
        System.out.println(" NOEXCEPT");
      }
    }
    
    System.out.println();
    
    assertEquals(numErrors, BAD_MULTI_ABS_CONFIGS.length);
    
  }
  
  @Test
  @SuppressWarnings("static-method")
  public void testGoodSingleAbsConfigs() {
    
    System.out.println("=== Testing good single absolute time configs ===");
    
    int failCount = 0;
    for (int i = 0; i < GOOD_SINGLE_ABS_CONFIGS.length; i++) {
      
      final AbsoluteTimeTestEntry entry = GOOD_SINGLE_ABS_CONFIGS[i];
      System.out.print("Testing config: \"" + entry.getConfigString() + "\":");
      
      AbsoluteTimeConfiguration atc = null;
      try {
        atc = new AbsoluteTimeConfiguration(entry.getConfigString(), TEST_ZONE_ID);
      } catch (@SuppressWarnings("unused") BadTimeConfigException btc) {
        System.out.println(" EXCEPTION");
        failCount++;
        continue;
      } catch (@SuppressWarnings("unused") final InternalTimeConfigException itc) {
        System.out.println(" INTERNAL EXCEPTION");
        failCount++;
        continue;
      }
      
      final Long expectedVal = entry.getExpected().get(0);
      final Iterator<Long> itr = atc.iterator();
      
      if (!itr.hasNext()) {
        System.out.println(" EMPTY");
        failCount++;
        continue;
      }
      
      final Long actualVal = itr.next();
      if (actualVal == null) {
        System.out.println(" NULL");
        failCount++;
        continue;
      }
      
      String resultStr = " OK";
      if (!actualVal.equals(expectedVal)) {
        resultStr = " Expected " + expectedVal + ", but got " + actualVal + "!";
        failCount++;
      }
      
      System.out.println(resultStr);
      
    }
    
    System.out.println();
    
    assertEquals(failCount, 0);
    
  }
  
  @Test
  @SuppressWarnings("static-method")
  public void testGoodMultiAbsConfigs() {
    
    System.out.println("=== Testing good single absolute time configs ===");
    
    int failCount = 0;
    for (int i = 0; i < GOOD_MULTI_ABS_CONFIGS.length; i++) {
      
      final AbsoluteTimeTestEntry entry = GOOD_MULTI_ABS_CONFIGS[i];
      System.out.println("Testing config \"" + entry.getConfigString() + "\":");
      
      AbsoluteTimeConfiguration atc = null;
      try {
        atc = new AbsoluteTimeConfiguration(entry.getConfigString(), TEST_ZONE_ID);
      } catch (@SuppressWarnings("unused") final BadTimeConfigException btc) {
        System.out.println(" EXCEPTION");
        failCount++;
        continue;
      } catch (@SuppressWarnings("unused") final InternalTimeConfigException itc) {
        System.out.println(" INTERNAL EXCEPTION");
        failCount++;
        continue;
      }
      
      final Iterator<Long> itr = atc.iterator();
      
      // for (int j = 0; j < 50 && j < entry.getExpected().size(); j++) {
      for (int j = 0; j < entry.getExpected().size(); j++) {
        
        final Long expectedVal = entry.getExpected().get(j);
        System.out.print("  Timepoint " + j + " (" + expectedVal + "):");
        
        if (!itr.hasNext()) {
          System.out.println(" EMPTY");
          failCount++;
          continue;
        }
        
        final Long actualVal = itr.next();
        if (actualVal == null) {
          System.out.println(" NULL");
          failCount++;
          continue;
        }
        
        String resultStr = " OK";
        if (!actualVal.equals(expectedVal)) {
          resultStr = " Expected " + expectedVal + ", but got " + actualVal + "!";
          failCount++;
        }
        
        System.out.println(resultStr);
        
      }
      
    }
    
    System.out.println();
    
    assertEquals(failCount, 0);
    
  }
  
  @Test
  @SuppressWarnings("static-method")
  public void testBadRelConfigs() {
    
    System.out.println("=== Testing bad relative time configs ===");
    
    int numErrors = 0;
    for (int i = 0; i < BAD_FORMAT_REL_CONFIGS.length; i++) {
      
      final String config = BAD_FORMAT_REL_CONFIGS[i];
      System.out.print("Testing config: \"" + config + "\":");
      
      boolean caughtExc = false;
      
      try {
        new RelativeTimeConfiguration(config, TEST_ZONE_ID);
      } catch (@SuppressWarnings("unused") BadTimeConfigException btc) {
        caughtExc = true;
      } catch (final InternalTimeConfigException itc) {
        itc.printStackTrace();
        caughtExc = true;
        continue;
      }
      
      if (caughtExc) {
        numErrors++;
        System.out.println(" OK");
      } else {
        System.out.println(" NOEXCEPT");
      }
    }
    
    System.out.println();
    
    assertEquals(numErrors, BAD_FORMAT_REL_CONFIGS.length);
    
  }
  
  @Test
  @SuppressWarnings("static-method")
  public void testGoodRelConfigs() {
    
    System.out.println("=== Testing good relative time configs ===");
    
    int failCount = 0;
    for (int i = 0; i < GOOD_RELATIVE_CONFIGS.length; i++) {
      
      final RelativeTimeTestEntry currEntry = GOOD_RELATIVE_CONFIGS[i];
      final String configString = currEntry.getConfigString();
      
      System.out.println("Testing config \"" + configString + "\".");
      
      RelativeTimeConfiguration rtc = null;
      
      // "in" test
      System.out.print("  \"in\" construction:");
      try {
        rtc = new RelativeTimeConfiguration("in " + configString, TEST_ZONE_ID);
      } catch (@SuppressWarnings("unused") final BadTimeConfigException btc) {
        System.out.println("   EXCEPTION");
        failCount++;
        continue;
      } catch (@SuppressWarnings("unused") final InternalTimeConfigException itc) {
        System.out.println("   INTERNAL EXCEPTION");
        failCount++;
        continue;
      }
      
      System.out.println(" build OK");
      
      Iterator<Long> itr = rtc.iterator();
      ArrayList<Long> expectedTimes = currEntry.getExpectedTimes();
      if (expectedTimes == null || expectedTimes.size() <= 0) {
        fail("Expected relative times is empty.");
        return;
      }
      long exp = expectedTimes.get(0);
      
      System.out.print("  Expected=" + exp + ", Result: ");
      if (!itr.hasNext()) {
        failCount++;
        System.out.println("ERROR, Empty");
        continue;
      }
      
      // 23 21
      
      // TODO: Add in error here
      long val = itr.next();
      if (Math.abs(exp - val) > RELATIVE_EPOCH_SECOND_ERR) {
        failCount++;
        System.out.println("ERROR, got " + val);
        continue;
      }
      
      System.out.println("OK");
      
      // ------------------------------------------------------------------------------------------
      
      // "every" test
      System.out.print("  \"every\" construction:");
      try {
        rtc = new RelativeTimeConfiguration("in " + configString, TEST_ZONE_ID);
      } catch (@SuppressWarnings("unused") final BadTimeConfigException btc) {
        System.out.println("   EXCEPTION");
        failCount++;
        continue;
      } catch (@SuppressWarnings("unused") final InternalTimeConfigException itc) {
        System.out.println("   INTERNAL EXCEPTION");
        failCount++;
        continue;
      }
      
      System.out.println(" build OK");
      
      itr = rtc.iterator();
      expectedTimes = currEntry.getExpectedTimes();
      for (int j = 0; j < expectedTimes.size(); j++) {
        
        exp = expectedTimes.get(j);
        System.out.print("    #" + (j + 1) + ": Expected=" + exp + ", Result: ");
        
        if (!itr.hasNext()) {
          failCount++;
          System.out.println("ERROR, Empty");
          continue;
        }
        
        // TODO: Add in error here
        val = itr.next();
        if (Math.abs(exp - val) > RELATIVE_EPOCH_SECOND_ERR) {
          failCount++;
          System.out.println("ERROR, got " + val);
          continue;
        }
        
        System.out.println("OK");
        
      }
      
    }
    
    System.out.println();
    assertEquals(failCount, 0);
  }
  
}
