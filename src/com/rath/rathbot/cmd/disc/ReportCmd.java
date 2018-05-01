
package com.rath.rathbot.cmd.disc;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class ReportCmd extends RBCommand {
  
  private static final long REPORT_CHANNEL_ID = 291441076257161217L;
  private static IDiscordClient client = RathBot.getClient();
  
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
  public boolean executeCommand(final IUser author, final IChannel channel, final String[] tokens,
      final int tokenDepth) {
    
    System.out.println("Executing report");
    
    RathBot.sendDirectMessage(author, "Thank you for your report! We will look into this and take action accordingly.");
    
    // TODO: Log report
    
    // TODO: Post to #report
    IChannel report = ReportCmd.client.getChannelByID(ReportCmd.REPORT_CHANNEL_ID);
    RathBot.sendMessage(report, "User " + author.getName());
    
    return RBCommand.STOP_CMD_SEARCH;
  }
  
  @Override
  public boolean requiresDirectMessage() {
    return true;
  }
}
