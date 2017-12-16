
package com.rath.rathbot.msg;

import com.rath.rathbot.RathBot;

import sx.blah.discord.handle.obj.IChannel;

/**
 * This class contains various error messages the bot can send.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class ErrorMsg {
  
  /**
   * Has the bot post a message saying the command was invalid.
   * 
   * @param rb reference to the bot.
   * @param ch the channel to post the message on.
   * @param cmd the command that was attempted.
   */
  public static final void sendInvalidCmdMsg(final RathBot rb, final IChannel ch, final String cmd) {
    if (cmd == null) {
      rb.sendMessage(ch, "Invalid command.");
    } else {
      rb.sendMessage(ch, "Invalid command: \"" + cmd + "\".");
    }
  }
  
  /**
   * Has the bot post a message saying the user does not have permission to use the attempted command.
   * 
   * @param rb reference to the bot.
   * @param ch the channel to post the message on.
   */
  public static final void sendPermDeniedMsg(final RathBot rb, final IChannel ch) {
    rb.sendMessage(ch, "You do not have permission to use that command.");
  }
  
}
