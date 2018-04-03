
package com.rath.rathbot.msg;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.disc.PunishmentType;

/**
 * This class contains frequently-used message formatting methods.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class MessageHelper {
  
  public static final String NO_COMMANDS_MSG = "No commands registered.";
  
  /**
   * Builds a message with commands and their descriptions.
   * 
   * @param header the line before the commands list.
   * @param commands a map of Strings to RBCommands.
   * @return the built message as a String.
   */
  public static final String buildCmdDescrMsg(final String header, final Map<String, RBCommand> commands) {
    
    if (commands == null) {
      return NO_COMMANDS_MSG;
    }
    
    String result = "";
    
    // Check if we need to indent because of a header
    boolean doIndent = false;
    if (header != null && header.length() >= 1) {
      doIndent = true;
      result += header + "\n";
    }
    
    final Set<String> keySet = commands.keySet();
    final int ksSize = keySet.size();
    int i = 1;
    for (final String s : keySet) {
      if (doIndent) {
        result += "  ";
      }
      result += s + " - " + commands.get(s).getCommandDescription();
      if (i < ksSize) {
        result += "\n";
        i++;
      }
    }
    return result;
  }
  
  /**
   * Squishes a String array's contents to a single String starting at and including the given starting point, with the
   * contents of each index being separated by a single space.
   * 
   * @param tokens the String array to squish.
   * @param startIndex the index to start squishing at.
   * @return a single String.
   */
  public static final String concatenateTokens(String[] tokens, int startIndex) {
    
    if (tokens.length <= startIndex) {
      System.err.println("concatenateTokens(): Token length out of bounds.");
      return null;
    }
    
    String result = tokens[startIndex];
    for (int i = startIndex + 1; i < tokens.length; i++) {
      result += " " + tokens[i];
    }
    
    return result;
  }
  
  /**
   * Builds a String from a header and list of entries with the option to break lines between each.
   * 
   * @param header the header for the list, includes a line break.
   * @param list the entries as a List of whatevers. The object's toString() method will be called.
   * @param breakLines true if each list entry will be on a separate line; false if they are comma-separated.
   * @return the built String.
   */
  public static final String buildListString(final String header, final List<?> list, final boolean breakLines) {
    String result = header + "\n  ";
    
    // Fencepost start
    if (list.size() >= 1) {
      result += list.get(0).toString();
    } else {
      result += "List is empty.";
    }
    
    // Append separator, then entry
    for (int i = 1; i < list.size(); i++) {
      result += (breakLines) ? "\n  " : ", ";
      result += list.get(i).toString();
    }
    
    return result;
  }
  
  /**
   * Builds a notification message for an infraction, delivered to the user in a direct message.
   * 
   * @param type the type of punishment (warn, ban, etc.).
   * @param time the amount of time the user stays muted; if the user isn't to be muted, this can be any value.
   * @return the message that will be sent as a String.
   */
  public static String buildDiscNotificationMessage(final PunishmentType type, final int time) {
    String msg = "You have been " + type.getVerb() + type.getPrep() + " the osu! University server";
    if (type.equals(PunishmentType.MUTE)) {
      msg += " for ";
      if (time >= 3600) {
        msg += (time / 3600) + " hours";
      } else {
        msg += (time / 60 + " minutes");
      }
    }
    msg += ".\nIf you believe this was an error, contact Rath (Rathuldr#0587) or Kami (Danni293#0911).";
    return msg;
  }
}
