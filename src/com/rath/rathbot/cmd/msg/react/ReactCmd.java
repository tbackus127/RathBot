
package com.rath.rathbot.cmd.msg.react;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

/**
 * This commmand handles posting images as reactions, but supports images larger than emojis.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class ReactCmd extends RBCommand {
  
  /** The reaction images directory. */
  public static final String DIR_REACT_IMGS = RathBot.DIR_IMG + "react/";
  
  /** Supported image extensions. */
  private static final String[] IMG_EXTENSIONS = { ".jpg", ".png", ".gif" };
  
  @Override
  public String getCommandName() {
    return "react";
  }
  
  @Override
  public String getCommandDescription() {
    return "Posts the specified reaction image.";
  }
  
  @Override
  public String getCommandUsage() {
    return "rb! react <imageName>";
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
  public Set<RBCommand> getSubcommands() {
    
    final Set<RBCommand> result = new HashSet<RBCommand>();
    result.add(new ReactListCmd());
    
    return result;
  }
  
  @Override
  public boolean executeCommand(final IMessage msg, final String[] tokens, final int tokDepth) {
    if (!super.executeCommand(msg, tokens, tokDepth)) {
      
      // Ensure proper argument length (ignore extra args)
      final IChannel channel = msg.getChannel();
      if (tokens.length < 3) {
        RathBot.sendMessage(channel, "Syntax error. Syntax:\n  " + getCommandUsage());
      }
      
      // Find the image file
      final String filename = tokens[2];
      File imgFile = null;
      for (int i = 0; i < IMG_EXTENSIONS.length; i++) {
        
        imgFile = new File(DIR_REACT_IMGS + filename + IMG_EXTENSIONS[i]);
        System.out.println("Checking against file: \"" + imgFile.getAbsolutePath() + "\".");
        
        // If we've found it, stop searching and do the thing
        if (imgFile.exists()) {
          try {
            
            RathBot.sendFile(channel, imgFile);
            return RBCommand.STOP_CMD_SEARCH;
            
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          }
        }
      }
      
      RathBot.sendMessage(channel, "That react image is not available.");
      msg.delete();
    }
    
    return RBCommand.STOP_CMD_SEARCH;
  }
  
}
