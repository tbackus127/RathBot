
package com.rath.rathbot.cmd.msg.react;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.util.MessageHelper;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

/**
 * This command handles listing all of the available react images.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class ReactListCmd extends RBCommand {
  
  @Override
  public String getCommandName() {
    return "list";
  }
  
  @Override
  public String getCommandDescription() {
    return "Lists all available react images.";
  }
  
  @Override
  public String getCommandUsage() {
    return "rb! react list";
  }
  
  @Override
  public int permissionLevelRequired() {
    return RBCommand.PERM_STANDARD;
  }
  
  @Override
  public boolean requiresDirectMessage() {
    return false;
  }
  
  @Override
  public boolean executeCommand(final IMessage msg, final String[] tokens, final int tokDepth) {
    
    // Check that the react directory exists
    final File reactDir = new File(ReactCmd.DIR_REACT_IMGS);
    if (!reactDir.exists()) {
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    // Get a list of all files in the react directory
    final File[] fileList = reactDir.listFiles();
    final IChannel channel = msg.getChannel();
    if (fileList.length <= 0) {
      RathBot.sendMessage(channel, "There are no reaction images available.");
      return RBCommand.STOP_CMD_SEARCH;
    }
    // Convert to an ArrayList so we can use the MessageHelper
    final ArrayList<String> reactList = new ArrayList<String>();
    for (int i = 0; i < fileList.length; i++) {
      final String filename = fileList[i].getName();
      if (filename.length() > 4) {
        reactList.add(filename.substring(0, filename.length() - 4));
      }
    }
    
    if (reactList.isEmpty()) {
      RathBot.sendMessage(channel, "There are no reaction images available.");
      System.err.println("ArrayList of images shouldn't be empty!");
      return RBCommand.STOP_CMD_SEARCH;
    }
    
    // Build the list string and send it
    final String listMsg = MessageHelper.buildListString("Available images:", reactList, false);
    RathBot.sendMessage(channel, listMsg);
    
    return RBCommand.STOP_CMD_SEARCH;
  }
}
