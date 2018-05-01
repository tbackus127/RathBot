
package com.rath.rathbot.cmd.disc;

import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.handle.obj.IMessage;

public class ReportCmd extends RBCommand {
  
  @Override
  public String getCommandName() {
    return "report";
  }
  
  @Override
  public String getCommandDescription() {
    return "**REQUIRES A DIRECT MESSAGE** Reports a user to the moderation team with a reason for the report. "
        + "You will need the member's internal ID. Get it using the \"uid <Discord username>\" command. "
        + "The uid command also requires a direct message."
        + "Note that this is NOT their nickname for the server, but the username you see when you open their profile."
        + "For example, Kami's Discord username is \"Danni293\".";
  }
  
  @Override
  public String getCommandUsage() {
    return "rb! report <uid> <Reason..>";
  }
  
  @Override
  public int permissionLevelRequired() {
    return RBCommand.PERM_MINIMAL;
  }
  
  @Override
  public boolean executeCommand(final IMessage msg, final String[] tokens, final int tokenDepth) {
    
    System.out.println("Executing report");
    
    // TODO: Reply that the report has been received
    // TODO: Log report
    // TODO: Post to #report
    
    return RBCommand.STOP_CMD_SEARCH;
  }
  
  @Override
  public boolean requiresDirectMessage() {
    return true;
  }
}
