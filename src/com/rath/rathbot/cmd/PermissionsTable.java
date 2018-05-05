
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
  
  /** Whether or not to actually save the table to disk (disable for testing purposes). */
  private static boolean saveToDisk = true;
  
  /**
   * Disables saving the table to disk.
   */
  public static final void disableSaveToDisk() {
    saveToDisk = false;
  }
  
  /**
   * Initializes a user with default permissions.
   * 
   * @param userID the ID of the member to initialize.
   * @return false if there was an error.
   */
  public static final boolean initUser(final long userID) {
    boolean ret = updateUser(userID, DEFAULT_PERM_LEVEL);
    savePerms();
    return ret;
  }
  
  /**
   * Updates the permissions table for a given user.
   * 
   * @param userID the ID of the member to update.
   * @param permLevel the new permissions level this user should receive.
   * @return false if there was an error.
   */
  public static final boolean updateUser(final long userID, final int permLevel) {
    
    if (permMap == null) {
      System.err.println("Perm map is null!");
      return false;
    }
    
    permMap.put(userID, permLevel);
    System.out.println("Updated " + userID + " to " + permLevel + ".");
    System.out.println(userID + " is now " + permMap.get(userID));
    savePerms();
    return true;
  }
  
  /**
   * Removes the entry for a given user (if they leave, are banned, or purged).
   * 
   * @param userID the ID of the member to remove.
   * @return false if there was an error.
   */
  public static final boolean removeUser(final long userID) {
    
    if (permMap == null) {
      System.err.println("Perm map is null!");
      return false;
    }
    
    permMap.remove(userID);
    savePerms();
    return true;
  }
  
  /**
   * Gets the permission level for the given user.
   * 
   * @param userID the ID of the member we're getting the permission level for.
   * @return the permission level as an int, defined in RBCommand. If there is an error, -1 will be returned.
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
   * @return true if the user exists; false if not. If the table is null, false will be returned.
   */
  public static final boolean hasUser(final long userID) {
    
    if (permMap == null) {
      return false;
    }
    
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
   * Clears the permissions table, but does not save it to disk. USE WITH CAUTION.
   */
  public static final void clearTable() {
    permMap = new TreeMap<Long, Integer>();
  }
  
  /**
   * Saves the permission table to the hard disk.
   */
  public static final void savePerms() {
    
    if (!saveToDisk) {
      return;
    }
    
    System.out.println("Saving permissions map to file.");
    
    try (FileOutputStream fos = new FileOutputStream(PERM_DATA_PATH);
        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
      
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
    
    // Create the file if it doesn't exist.
    if (!PERM_FILE.exists()) {
      try {
        System.out.println("Permissions file doesn't exist. Creating new.");
        PERM_FILE.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    Object obj = null;
    try (FileInputStream fis = new FileInputStream(PERM_DATA_PATH);
        ObjectInputStream oin = new ObjectInputStream(fis);) {
      
      if (fis.available() > 0) {
        
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
    
    // Cast the read object to a TreeMap
    if (obj instanceof TreeMap) {
      permMap = (TreeMap<Long, Integer>) obj;
    } else {
      System.err.println("Error with loading. Creating new table.");
      permMap = new TreeMap<Long, Integer>();
    }
    
  }
  
}
