
package com.rath.rathbot.disc;

public enum PunishmentType {
  WARN("warned", "in"), MUTE("muted", "in"), KICK("kicked", "from"), BAN("banned", "from");
  
  final String verb;
  final String prep;
  
  private PunishmentType(final String verb, final String prep) {
    this.verb = verb;
    this.prep = prep;
  }
  
  public String getVerb() {
    return this.verb;
  }
  
  public String getPrep() {
    return this.prep;
  }
}
