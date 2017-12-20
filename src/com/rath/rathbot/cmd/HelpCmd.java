
package com.rath.rathbot.cmd;

import java.util.Set;

import com.rath.rathbot.RathBot;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

/**
 * This command provides a manual for the other commands.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class HelpCmd extends RBCommand {

  /** The name of this command. */
  private static final String CMD_NAME = "help";

  /** The description of this command. */
  private static final String CMD_DESCR = "Provides information on how to use various commands.";

  @Override
  public String getCommandName() {

    return CMD_NAME;
  }

  @Override
  public String getCommandDescription() {

    return CMD_DESCR;
  }

  @Override
  public boolean requiresModStatus() {

    return false;
  }

  @Override
  public Set<RBCommand> getSubcommands() {

    return null;
  }

  @Override
  public void executeCommand(final RathBot rb, final IUser author, final IChannel channel, final String[] tokens,
      final int tokenDepth) {

    // TODO Auto-generated method stub

  }

  public void addCommandEntry(RBCommand cmd) {

    // TODO Auto-generated method stub

  }

}
