
package com.rath.rathbot.cmd.msg.remindme;

import java.util.HashSet;

import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.handle.obj.IMessage;

/**
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class RemindmeCmd extends RBCommand {
  
  private static final String CMD_NAME = "remindme";
  
  private static final String CMD_DESC = "Has the bot send you a custom direct message at the specified time & date or in the specified time interval.";
  
  //@formatter:off
  private static final String CMD_USG = "rb! remindme <label> (<timestring> or <datestring>) <Message to receive>\n" +
                                        "  label: A custom name for your reminder (no spaces, must be unique to you)\n" + 
                                        "    You can use this to cancel a reminder.\n" + 
                                        "  timestring: #M#w#d#h#m, where # is a number, and\n" + 
                                        "    M: Months, w: Weeks, d: Days, h: Hours, m: Minutes\n" +
                                        "    Ex: \"2w3d\" will mean 2 weeks and 3 days\n" +
                                        "  datestring: MM/DD/YY@hh:mm. M: Month, D: Day, Y: Year\n" +
                                        "    h: Hour (24-hour), m: Minute. Times are EST (GMT-5)\n" +
                                        "  Timespans longer than one year will be ignored.";
  //@formatter:on
  
  @Override
  public String getCommandName() {
    return CMD_NAME;
  }
  
  @Override
  public String getCommandDescription() {
    return CMD_DESC;
  }
  
  @Override
  public String getCommandUsage() {
    return CMD_USG;
  }
  
  @Override
  public int permissionLevelRequired() {
    return RBCommand.PERM_STANDARD;
  }
  
  @Override
  public boolean requiresDirectMessage() {
    return false;
  }
  
  @Override
  public HashSet<RBCommand> getSubcommands() {
    final HashSet<RBCommand> result = new HashSet<RBCommand>();
    result.add(new RemindmeCancelCmd());
    return result;
  }
  
  @Override
  public boolean executeCommand(final IMessage msg, final String[] tokens, final int tokDepth) {
    if (!super.executeCommand(msg, tokens, tokDepth)) {
      // TODO: rb! remindme <timestring>|<datestring> <Message..>
      // TODO: Give a unique ID when the reminder is created
    }
    return RBCommand.STOP_CMD_SEARCH;
  }
  
}
