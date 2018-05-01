
package com.rath.rathbot.cmd.disc;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Provides a report command for users to report rule breaking activity, posts the report in server #report channel.
 * 
 * @author Kami lehenbnw@gmail.com
 *
 */

public class ReportCmd extends RBCommand {
  
  /**
   * Bot Development server #report channel ID for testing purposes
   * 
   * TODO: Remove this constant before merge with PROD.
   */
  private static final long REPORT_CHANNEL_ID = 431308950097494028L;
  
  /** IDiscordClient copy from RathBot, used to obtain IChannel object from channel map. */
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
        + "For example, Kami's Discord username is \"Loli no Kami\".";
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
    
    long REPORTED_USER_ID = 0;
    
    // Retrieve UID from arguments.
    try {
      REPORTED_USER_ID = Long.parseLong(tokens[tokenDepth]);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    
    // Create IUser object from REPORTED_USER_ID to reference user in messages.
    IUser reportedUser = client.getUserByID(REPORTED_USER_ID);
    
    // Message user for confirmation that report is being filed successfully.
    System.out.println("Filing report...");
    RathBot.sendDirectMessage(msg.getAuthor(), "Thank you, your report against " + reportedUser.getName()
        + " We will look into this and take action accordingly.");
    
    // TODO: Log report
    
    // TODO: Replace ReportCmd.REPORT_CHANNEL_ID with RathBot.REPORT_CHANNEL_ID before merge into PROD.
    IChannel report = ReportCmd.client.getChannelByID(ReportCmd.REPORT_CHANNEL_ID);
    RathBot.sendMessage(report, "User " + reportedUser.getName() + " was reported by " + msg.getAuthor().getName()
        + " at " + msg.getTimestamp() + ".\n" + "Reason: " + tokens[tokenDepth + 1]);
    
    return RBCommand.STOP_CMD_SEARCH;
  }
  
  @Override
  public boolean requiresDirectMessage() {
    return true;
  }
}
