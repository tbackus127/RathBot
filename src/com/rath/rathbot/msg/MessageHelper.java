
package com.rath.rathbot.msg;

import java.util.Map;

import com.rath.rathbot.cmd.RBCommand;

/**
 * This class contains frequently-used message formatting methods.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class MessageHelper {

  /**
   * Builds a message with commands and their descriptions.
   * 
   * @param header the line before the commands list.
   * @param commands a map of Strings to RBCommands.
   * @return the built message as a String.
   */
  public static final String buildCmdDescrMsg(final String header, final Map<String, RBCommand> commands) {

    String result = header + "\n";
    for (final String s : commands.keySet()) {
      result += s + " - " + commands.get(s) + "\n";
    }
    return result;
  }
}
