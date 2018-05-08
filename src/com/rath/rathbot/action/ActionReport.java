
package com.rath.rathbot.action;

import java.time.Instant;

import sx.blah.discord.handle.obj.IUser;

public class ActionReport extends RBDiscAction {
  
  public ActionReport(Instant timestamp, IUser issuedUser, IUser infringingUser) {
    super(timestamp, issuedUser, infringingUser);
  }
  
  @Override
  public String getActionMessage() {
    return this.timestamp + ": " + this.getIssuer().getName() + " reported " + this.infringingUser.getName() + ".";
  }
}