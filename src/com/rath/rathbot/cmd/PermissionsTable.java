
package com.rath.rathbot.cmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.TreeMap;

import com.rath.rathbot.RathBot;

/**
 * This class holds each user's permission level. Certain commands require a specific level to execute.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class PermissionsTable {
  
  /** The filename for the permission data. */
  private static final String PERM_DATA_FILENAME = "perms.dat";
  
  /** The path for the permission data. */
  private static final String PERM_DATA_PATH = RathBot.DIR_DATA + PERM_DATA_FILENAME;
  
  /** The default permissions level new users are added with. */
  private static final int DEFAULT_PERM_LEVEL = RBCommand.PERM_STANDARD;
  
  /** The permissions table file. */
  private static final File PERM_FILE = new File(PERM_DATA_PATH);
  
  /** The permissions table. */
  private static TreeMap<Long, Integer> permMap = null;
  
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
    // System.out.println("Getting perms for id=" + userID);
    
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
   * Whether or not an entry exists for a user.
   * 
   * @param userID the ID of the user to check for.
   * @return true if the user exists; false if not.
   */
  public static final boolean hasUser(final long userID) {
    return permMap.containsKey(userID);
  }
  
  /**
   * Gets the reference to the permissions table.
   * 
   * @return the permissions table as a TreeMap.
   */
  public static final TreeMap<Long, Integer> getPermMap() {
    return permMap;
  }
  
  /**
   * Saves the permission table to the hard disk.
   */
  public static final void savePerms() {
    System.out.println("Saving permissions map to file.");
    
    FileOutputStream fos = null;
    ObjectOutputStream oos = null;
    
    try {
      
      // Build an output stream for serialization
      fos = new FileOutputStream(PERM_DATA_PATH);
      oos = new ObjectOutputStream(fos);
      
      // Write the map and close streams
      oos.writeObject(permMap);
      oos.close();
      fos.close();
      
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Loads the permission table from the hard disk.
   */
  @SuppressWarnings("unchecked")
  public static final void loadPerms() {
    
    System.out.println("Loading permissions map from file.");
    
    // Initialize data streams
    FileInputStream fis = null;
    ObjectInputStream oin = null;
    Object obj = null;
    
    // Create the file if it doesn't exist.
    if (!PERM_FILE.exists()) {
      try {
        System.out.println("Permissions file doesn't exist. Creating new.");
        PERM_FILE.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    try {
      
      // Build an input stream for deserialization
      fis = new FileInputStream(PERM_DATA_PATH);
      if (fis.available() > 0) {
        oin = new ObjectInputStream(fis);
        
        // Read the object in and close the streams
        obj = oin.readObject();
        oin.close();
        fis.close();
        
      } else {
        System.err.println("Permissions map is empty.");
      }
      
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    
    // If something went wrong, return null
    if (fis == null || oin == null) {
      System.err.println("Permissions map load error.");
    }
    
    // Cast the read object to a TreeMap
    if (obj instanceof TreeMap) {
      permMap = (TreeMap<Long, Integer>) obj;
    } else {
      System.err.println("Error with loading. Creating new table.");
      permMap = new TreeMap<Long, Integer>();
    }
    
  }
  
}
