
package com.rath.rathbot.action;

import java.time.Instant;

import sx.blah.discord.handle.obj.IUser;

public class ActionExecCommand extends RBIssuedAction {
  
  private final String cmdName;
  
  protected ActionExecCommand(final Instant timestamp, final IUser issuer, final String cmdName) {
    super(timestamp, issuer);
    this.cmdName = cmdName;
  }
  
  @Override
  public String getActionMessage() {
    return this.timestamp + ": Executed command \'" + this.cmdName + "\'";
  }
  
}
