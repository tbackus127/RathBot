
package com.rath.rathbot.task.time;

public enum TimeExceptionReason {
  // @formatter:off
  // TODO: Add error strings to all of these
  ABS_MISSING_CLAUSES("The time configuration string is missing an on-clause or an at-clause."),
  ABS_MISSING_CLAUSE_ARGS("An on-clause or at-clause is not complete."),
  ABS_BAD_ON_CLAUSE("The specified on-clause has too many or too few components."),
  ABS_BAD_AT_CLAUSE("The specified at-clause has too many or too few components."),
  ABS_YEAR_NAN("An invalid year or year list was entered for the on-clause."),
  ABS_MONTH_NAN("An invalid month or month list was entered for the on-clause."),
  ABS_DAY_NAN("An invalid day-of-month or day-of-month list was entered for the on-clause."),
  ABS_HOUR_NAN("An invalid hour was entered for the at-clause."),
  ABS_MINUTE_NAN("An invalid minute was entered for the at-clause."),
  ABS_YEAR_OOB("The entered year is either not a positive number, before the current year, or too far into the future."),
  ABS_MONTH_OOB("The entered month does not represent a number between 1 and 12 (inclusive)."),
  ABS_DAY_OOB("The entered day-of-month is not a number between 1 and 31 (inclusive)."),
  ABS_12HOUR_OOB("The entered standard time hour is not a number between 1 and 12 (inclusive)."),
  ABS_24HOUR_OOB("The entered military time hour is not a number between 0 and 23 (inclusive)."),
  ABS_MINUTE_OOB("The entered minute is not a number between 0 and 59 (inclusive)."),
  
  // TODO: Let's find a workaround for this one...
  ABS_YEAR_REQUIRED("A year or year list must be specified when providing a month list or day-of-month list."),
  
  REL_MISSING_CLAUSE("The time configuration screen is missing either an in-clause or an every-clause.");
  // @formatter:on
  
  private final String reasonString;
  
  private TimeExceptionReason(final String reasonString) {
    this.reasonString = reasonString;
  }
  
  public final String getReasonString() {
    return this.reasonString;
  }
  
}
