
package com.rath.rathbot;

import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import com.rath.rathbot.cmd.PermissionsTable;
import com.rath.rathbot.disc.Infractions;

import sx.blah.discord.handle.obj.IUser;

public class ConsoleHandler {
  
  /**
   * Starts the command line interpreter. Only supported on the local machine this bot is running on.
   * 
   * @param cin reference to System.in.
   */
  public static final void getConsoleCommands(final Scanner cin) {
    
    System.out.println("Now receiving commands.");
    
    // Command interface
    while (cin.hasNextLine()) {
      
      // Split command into tokens
      final String line = cin.nextLine();
      final String[] tokens = line.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
      if (tokens.length < 1) continue;
      switch (tokens[0]) {
        
        // Logout
        case "logout":
          RathBot.logout();
        break;
      
        // Change Now Playing status
        case "np":
          if (tokens.length >= 2) {
            String npMessage = tokens[1];
            for (int i = 2; i < tokens.length; i++) {
              npMessage += " " + tokens[i];
            }
            RathBot.setPlaying(npMessage);
          }
        break;
      
        // Send a message to a specific channel
        case "say":
          if (tokens.length >= 3) {
            final String channel = tokens[1];
            String npMessage = tokens[2];
            for (int i = 3; i < tokens.length; i++) {
              npMessage += " " + tokens[i];
            }
            RathBot.sendMessage(RathBot.getChannelMap().get(channel), npMessage);
          }
        break;
      
        // Get userIDs for users that match the next argument
        case "uid":
          if (tokens.length == 2) {
            final List<IUser> users = RathBot.getClient().getUsersByName(tokens[1], true);
            for (final IUser u : users) {
              System.out.println(u.getName() + ": " + u.getLongID());
            }
          }
        break;
      
        // Manually set permissions for commands
        case "perm":
          if (tokens.length == 3) {
            
            long id = -1;
            try {
              id = Long.parseLong(tokens[1]);
            } catch (NumberFormatException nfe) {
              nfe.printStackTrace();
            }
            
            int lvl = -1;
            try {
              lvl = Integer.parseInt(tokens[2]);
            } catch (NumberFormatException nfe) {
              nfe.printStackTrace();
            }
            
            if (id > 0 && lvl > 0) PermissionsTable.updateUser(id, lvl);
          }
        break;
      
        // List permissions
        case "perms":
          if (tokens.length == 1) {
            final TreeMap<Long, Integer> permMap = PermissionsTable.getPermMap();
            for (final long uid : permMap.keySet()) {
              System.out.println(RathBot.getClient().getUserByID(uid).getName() + ": " + permMap.get(uid));
            }
          }
        break;
      
        // Mute
        case "mute":
          if (tokens.length == 2) {
            final long uid = Long.parseLong(tokens[1]);
            if (!Infractions.hasMember(uid)) {
              Infractions.initMember(uid);
            }
            Infractions.setMuted(uid, true);
          }
        break;
      
        // Unmute
        case "unmute":
          if (tokens.length == 2) {
            final long uid = Long.parseLong(tokens[1]);
            if (!Infractions.hasMember(uid)) {
              Infractions.initMember(uid);
            }
            Infractions.setMuted(uid, false);
          }
        break;
      
        // Reset infractions
        case "ireset":
          if (tokens.length == 2) {
            final long uid = Long.parseLong(tokens[1]);
            if (Infractions.hasMember(uid)) {
              Infractions.clearInfractions(uid);
            }
          }
        break;
      
        default:
          System.out.println("Command not recognized.");
          
      }
    }
  }
  
}
