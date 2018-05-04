
package com.rath.rathbot.action;

import java.time.Instant;

import sx.blah.discord.handle.obj.IUser;

/**
 * This class acts as a skeleton for disciplinary actions that are performed on a user.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public abstract class RBDiscAction extends RBIssuedAction {
  
  /** The user who committed the infraction. */
  protected final IUser infringingUser;
  
  /**
   * Default constructor.
   * 
   * @param timestamp the Instant that the disciplinary action was issued.
   * @param issuedUser the IUser who issued the punishment.
   * @param infringingUser the IUser who received the punishment.
   */
  protected RBDiscAction(final Instant timestamp, final IUser issuedUser, final IUser infringingUser) {
    super(timestamp, issuedUser);
    this.infringingUser = infringingUser;
  }
  
  /**
   * Gets the member who received the punishment.
   * 
   * @return the IUser handle of the member.
   */
  public final IUser getInfringingUser() {
    return this.infringingUser;
  }
  
}
