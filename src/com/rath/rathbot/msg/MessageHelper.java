
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
      result += s + " - " + commands.get(s).getCommandDescription() + "\n";
    }
    return result;
  }
  
  /**
   * Squishes a String array's contents to a single String starting at and including the given starting point.
   * 
   * @param tokens the String array to squish.
   * @param startIndex the index to start squishing at.
   * @return a single String.
   */
  public static String concatenateTokens(String[] tokens, int startIndex) {
    
    if (tokens.length <= startIndex) {
      System.err.println("concatenateTokens(): Token length out of bounds.");
    }
    
    String result = tokens[startIndex];
    for (int i = startIndex + 1; i < tokens.length; i++) {
      result += " " + tokens[i];
    }
    
    return result;
  }
}
