
package com.rath.rathbot.action;

import java.time.Instant;

import sx.blah.discord.handle.obj.IUser;

/**
 * This class acts as a skeletons for all actions that are issued by a user.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public abstract class RBIssuedAction extends RBAction {
  
  private final IUser issuer;
  
  protected RBIssuedAction(final Instant timestamp, final IUser issuer) {
    super(timestamp);
    this.issuer = issuer;
  }
  
  /**
   * Gets the member who issued the action that the bot performed.
   * 
   * @return an IUser reference.
   */
  public IUser getIssuer() {
    return this.issuer;
  }
  
}
