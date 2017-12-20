package com.rath.rathbot.cmd.faq;

import java.util.Set;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

/**
 * This subcommand outputs a list of all saved FAQs.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class FAQListCmd extends RBCommand {

  private static final String CMD_NAME = "list";

  private static final String CMD_DESCR = "Displays a list of all available FAQs.";

  @Override
  public String getCommandName() {

    return CMD_NAME;
  }

  @Override
  public String getCommandDescription() {

    return CMD_DESCR;
  }

  @Override
  public Set<RBCommand> getSubcommands() {

    return null;
  }

  @Override
  public void executeCommand(final RathBot rb, final IUser author, final IChannel channel, final String[] tokens,
      final int tokenDepth) {

    // Check this command's subcommands for a match, and return the matched command
    final RBCommand cmd = super.checkSubcommands(getSubcommands(), tokens, tokenDepth);

    // If a subcommand is not found
    if (cmd == null) {

      // TODO list available FAQs

    } else {
      cmd.executeCommand(rb, author, channel, tokens, tokenDepth + 1);
    }

  }

  @Override
  public boolean requiresModStatus() {

    return false;
  }

}
