
package com.rath.rathbot.data;

import java.util.TreeMap;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;

/**
 * This class holds each user's permission level. Certain commands require a specific level to execute.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class PermissionsTable {
  
  /** The filename for the permission data. */
  private static final String PERM_DATA_PATH = RathBot.DIR_DATA + "perms.dat";
  
  /** The default permissions level new users are added with. */
  private static final int DEFAULT_PERM_LEVEL = RBCommand.PERM_STANDARD;
  
  /** The permissions table. */
  private static TreeMap<Long, Integer> permMap = null;
  
  /** The handler for saving/loading this class' data. */
  private static final DataLoader<TreeMap<Long, Integer>> loader = new DataLoader<TreeMap<Long, Integer>>(TreeMap.class,
      permMap, PERM_DATA_PATH);
  
  /**
   * Initializes a user with default permissions.
   * 
   * @param userID the ID of the member to initialize.
   */
  public static final void initUser(final long userID) {
    updateUser(userID, DEFAULT_PERM_LEVEL);
    savePerms();
  }
  
  /**
   * Updates the permissions table for a given user.
   * 
   * @param userID the ID of the member to update.
   * @param permLevel the new permissions level this user should receive.
   */
  public static final void updateUser(final long userID, final int permLevel) {
    
    if (permMap == null) {
      System.err.println("Perm map is null!");
      return;
    }
    
    permMap.put(userID, permLevel);
    System.out.println("Updated " + userID + " to " + permLevel + ".");
    System.out.println(userID + " is now " + permMap.get(userID));
    savePerms();
  }
  
  /**
   * Removes the entry for a given user (if they leave, are banned, or purged).
   * 
   * @param userID the ID of the member to remove.
   */
  public static final void removeUser(final long userID) {
    
    if (permMap == null) {
      System.err.println("Perm map is null!");
      return;
    }
    
    permMap.remove(userID);
    savePerms();
  }
  
  /**
   * Gets the permission level for the given user.
   * 
   * @param userID the ID of the member we're getting the permission level for.
   * @return the permission level as an int, defined in RBCommand.
   */
  public static final int getLevel(final long userID) {
    System.out.println("Getting perms for id=" + userID);
    
    if (permMap == null) {
      System.err.println("Perm map is null!");
      return -1;
    }
    
    final Integer lvl = permMap.get(userID);
    if (lvl == null) {
      return -1;
    }
    return lvl;
  }
  
  /**
   * Saves the permission table to the hard disk.
   */
  public static final void savePerms() {
    System.out.println("Saving permissions map to file.");
    loader.saveToDisk();
  }
  
  /**
   * Loads the permission table from the hard disk.
   */
  public static final void loadPerms() {
    System.out.println("Loading permissions map from file.");
    permMap = loader.loadFromDisk();
    savePerms();
  }
  
}
