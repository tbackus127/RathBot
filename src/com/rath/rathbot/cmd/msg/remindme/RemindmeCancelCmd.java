
package com.rath.rathbot.cmd.msg.remindme;

import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.handle.obj.IMessage;

public class RemindmeCancelCmd extends RBCommand {
  
  private static final String CMD_NAME = "cancel";
  
  private static final String CMD_DESC = "Cancels a reminder.";
  
  private static final String CMD_USG = "rb! remindme cancel <label>";
  
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
    return RBCommand.PERM_MINIMAL;
  }
  
  @Override
  public boolean requiresDirectMessage() {
    return false;
  }
  
  @Override
  public boolean executeCommand(final IMessage msg, final String[] tokens, final int tokDepth) {
    
    // TODO: RemindmeCancelCommand body
    
    return RBCommand.STOP_CMD_SEARCH;
  }
  
}
