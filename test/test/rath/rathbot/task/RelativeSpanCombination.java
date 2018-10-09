
package test.rath.rathbot.task;

public class RelativeSpanCombination {
  
  private final int spanCombo;
  
  public RelativeSpanCombination(final int spanCombo) {
    this.spanCombo = spanCombo;
  }
  
  public final boolean hasYears() {
    return hasSpan(TestTimeConfigurations.SPAN_FLAG_YEAR);
  }
  
  public final boolean hasMonths() {
    return hasSpan(TestTimeConfigurations.SPAN_FLAG_MONTH);
  }
  
  public final boolean hasWeeks() {
    return hasSpan(TestTimeConfigurations.SPAN_FLAG_WEEK);
  }
  
  public final boolean hasDays() {
    return hasSpan(TestTimeConfigurations.SPAN_FLAG_DAY);
  }
  
  public final boolean hasHours() {
    return hasSpan(TestTimeConfigurations.SPAN_FLAG_HOUR);
  }
  
  public final boolean hasMinutes() {
    return hasSpan(TestTimeConfigurations.SPAN_FLAG_MINUTE);
  }
  
  private final boolean hasSpan(final int span) {
    return (this.spanCombo & span) != 0;
  }
  
}
