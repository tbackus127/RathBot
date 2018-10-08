
package test.rath.rathbot.task;

public class RelativeTimeTestEntry {
  
  private final String configString;
  private final int plusYears;
  private final int plusMonths;
  private final int plusWeeks;
  private final int plusDays;
  private final int plusHours;
  private final int plusMinutes;
  
  public RelativeTimeTestEntry(final String configString, final int years, final int months, final int weeks,
      final int days, final int hours, final int minutes) {
    this.configString = configString;
    this.plusYears = years;
    this.plusMonths = months;
    this.plusWeeks = weeks;
    this.plusDays = days;
    this.plusHours = hours;
    this.plusMinutes = minutes;
  }
  
  public String getConfigString() {
    return this.configString;
  }
  
  public int getPlusYears() {
    return this.plusYears;
  }
  
  public int getPlusMonths() {
    return this.plusMonths;
  }
  
  public int getPlusWeeks() {
    return this.plusWeeks;
  }
  
  public int getPlusDays() {
    return this.plusDays;
  }
  
  public int getPlusHours() {
    return this.plusHours;
  }
  
  public int getPlusMinutes() {
    return this.plusMinutes;
  }
  
}
