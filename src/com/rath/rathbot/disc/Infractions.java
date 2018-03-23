
package com.rath.rathbot.disc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.TreeMap;

import com.rath.rathbot.RathBot;

public class Infractions {
  
  /** The filename for the permission data. */
  private static final String INFRACTIONS_DATA_FILENAME = "infractions.dat";
  
  /** The path for the permission data. */
  private static final String INFRACTIONS_DATA_PATH = RathBot.DIR_DATA + INFRACTIONS_DATA_FILENAME;
  
  /** The infractions table for each user. */
  private static TreeMap<Long, InfractionData> infractionMap = null;
  
  /** Reference to the infractions map file. */
  private static final File INFRACTIONS_FILE = new File(INFRACTIONS_DATA_PATH);
  
  /**
   * Gets whether the table has an entry for the given member.
   * 
   * @param user the Discord unique long ID.
   * @return true if an entry exists; false if not.
   */
  public static final boolean hasMember(final long user) {
    return infractionMap.containsKey(user);
  }
  
  /**
   * Populates the table with a new entry for the given user.
   * 
   * @param user the Discord unique long ID.
   */
  public static final void initMember(final long user) {
    infractionMap.put(user, new InfractionData());
  }
  
  /**
   * Gets the user's infraction history.
   * 
   * @param user the Discord unique long ID.
   * @return an ArrayList of InfractionEntry's.
   */
  public static final ArrayList<InfractionEntry> getInfractionHistory(final long user) {
    return infractionMap.get(user).getHistory();
  }
  
  /**
   * Gets the number of times the user has been warned.
   * 
   * @param user the Discord unique long ID.
   * @return an int.
   */
  public static final int getWarnCount(final long user) {
    return infractionMap.get(user).getWarnCount();
  }
  
  /**
   * Sets the user's warn status and increments their warn count.
   * 
   * @param user the Discord unique long ID.
   * @param time the epoch time the user was warned.
   * @param reason the reason the user is being warned.
   */
  public static final void warnUser(final long user, final long time, final String reason) {
    infractionMap.get(user).warn(time, reason);
    saveToFile();
  }
  
  /**
   * Gets the user's muted status.
   * 
   * @param user the Discord unique long ID.
   * @return true if the user is muted; false if not.
   */
  public static final boolean isMuted(final long user) {
    return infractionMap.get(user).isMuted();
  }
  
  /**
   * Gets how many times the user has been muted.
   * 
   * @param user the Discord unique long ID.
   * @return an int.
   */
  public static final int getMuteCount(final long user) {
    return infractionMap.get(user).getMuteCount();
  }
  
  /**
   * Updates a user's muted status.
   * 
   * @param user the Discord unique long ID.
   * @param b true if muted; false if not.
   */
  public static final void setMuted(final long user, final boolean b) {
    infractionMap.get(user).setMuted(b);
    saveToFile();
  }
  
  /**
   * Sets a the user's muted status to true and increments their mute count.
   * 
   * @param user the Discord unique long ID.
   * @param time the epoch time the user was muted.
   * @param reason the reason the user is being muted.
   */
  public static final void muteUser(final long user, final long time, final String reason) {
    infractionMap.get(user).mute(time, reason);
    saveToFile();
  }
  
  /**
   * Gets how many times the user has been kicked.
   * 
   * @param user the Discord unique long ID.
   * @return an int.
   */
  public static final int getKickCount(final long user) {
    return infractionMap.get(user).getKickCount();
  }
  
  /**
   * Setsu the user's kicked status to true and increments their kick count.
   * 
   * @param user the Discord unique long ID.
   * @param time the epoch time the user was kicked.
   * @param reason the reason the user is being kicked.
   */
  public static final void kickUser(final long user, final long time, final String reason) {
    infractionMap.get(user).kick(time, reason);
    saveToFile();
  }
  
  /**
   * Gets whether or not the user is banned.
   * 
   * @param user the Discord unique long ID.
   * @return true if the user is banned; false if not.
   */
  public static final boolean isBanned(final long user) {
    return infractionMap.get(user).isBanned();
  }
  
  /**
   * Gets how many times the user has been banned.
   * 
   * @param user the Discord unique long ID.
   * @return an int.
   */
  public static final int getBanCount(final long user) {
    return infractionMap.get(user).getBanCount();
  }
  
  /**
   * Sets the user's banned status.
   * 
   * @param user the Discord unique long ID.
   * @param b true if the user is banned; false if not.
   */
  public static final void setBanned(final long user, final boolean b) {
    infractionMap.get(user).setBanned(b);
    saveToFile();
  }
  
  /**
   * Sets the user's banned status to true and increments their ban count.
   * 
   * @param user the Discord unique long ID.
   * @param time the epoch time the user was banned.
   * @param reason the reason the user is being banned.
   */
  public static final void banUser(final long user, final long time, final String reason) {
    infractionMap.get(user).ban(time, reason);
    saveToFile();
  }
  
  /**
   * Saves the infractions table to disk.
   */
  public static final void saveToFile() {
    
    System.out.println("Saving infractions map to file.");
    
    FileOutputStream fos = null;
    ObjectOutputStream oos = null;
    
    try {
      
      // Build an output stream for serialization
      fos = new FileOutputStream(INFRACTIONS_DATA_PATH);
      oos = new ObjectOutputStream(fos);
      
      // Write the map and close streams
      oos.writeObject(infractionMap);
      oos.close();
      fos.close();
      
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Loads the infractions table from disk.
   */
  @SuppressWarnings("unchecked")
  public static final void loadFromFile() {
    System.out.println("Loading Infractions map from file.");
    
    // Initialize data streams
    FileInputStream fis = null;
    ObjectInputStream oin = null;
    Object obj = null;
    
    // Create the file if it doesn't exist.
    if (!INFRACTIONS_FILE.exists()) {
      try {
        System.out.println("Infractions file doesn't exist. Creating new.");
        INFRACTIONS_FILE.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    try {
      
      // Build an input stream for deserialization
      fis = new FileInputStream(INFRACTIONS_DATA_PATH);
      if (fis.available() > 0) {
        oin = new ObjectInputStream(fis);
        
        // Read the object in and close the streams
        obj = oin.readObject();
        oin.close();
        fis.close();
        
      } else {
        System.err.println("Infractions map is empty.");
      }
      
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    
    // If something went wrong, return null
    if (obj == null || fis == null || oin == null) {
      System.err.println("Infractions map load error.");
      infractionMap = new TreeMap<Long, InfractionData>();
      saveToFile();
      return;
    }
    
    // Cast the read object to a TreeMap
    if (obj instanceof TreeMap) {
      infractionMap = (TreeMap<Long, InfractionData>) obj;
    } else {
      System.err.println("Error with loading. Creating new table.");
      infractionMap = new TreeMap<Long, InfractionData>();
    }
  }
}
