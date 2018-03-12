
package com.rath.rathbot.cmd.admin;

import java.util.Set;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class ReportCmd extends RBCommand {
  
  @Override
  public String getCommandName() {
    return "report";
  }
  
  @Override
  public String getCommandDescription() {
    return "Reports a user to the moderation team with a reason for the report.";
  }
  
  @Override
  public int permissionLevelRequired() {
    return RBCommand.PERM_BASE;
  }
  
  @Override
  public Set<RBCommand> getSubcommands() {
    return null;
  }
  
  @Override
  public boolean executeCommand(final RathBot rb, final IUser author, final IChannel channel, final String[] tokens,
      final int tokenDepth) {
    
    System.out.println("Executing report");
    
    // TODO: Delete reporter's message
    // TODO: Log report
    // TODO: Post to #report
    
    return true;
  }
  
  @Override
  public void setupCommand() {
    // TODO Auto-generated method stub
    
  }
}
