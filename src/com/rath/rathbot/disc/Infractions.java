
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
  
  /** Whether or not to actually save the table to disk (for testing). */
  private static boolean saveToDisk = true;
  
  /**
   * Disables saving the table to disk.
   */
  public static final void disableSaveToDisk() {
    saveToDisk = false;
  }
  
  /**
   * Gets whether the table has an entry for the given member.
   * 
   * @param user the Discord unique long ID.
   * @return true if an entry exists; false if the map is null or the member isn't a key.
   */
  public static final boolean hasMember(final long user) {
    
    if (infractionMap == null) {
      return false;
    }
    
    return infractionMap.containsKey(user);
  }
  
  /**
   * Populates the table with a new entry for the given user.
   * 
   * @param user the Discord unique long ID.
   * @return true if there were no errors; false if there were.
   */
  public static final boolean initMember(final long user) {
    
    // Ensure the map isn't null
    if (infractionMap == null) {
      return false;
    }
    
    // Disallow negative UIDs
    if (user < 0) {
      return false;
    }
    
    infractionMap.put(user, new InfractionData());
    return true;
  }
  
  /**
   * Gets the user's infraction history.
   * 
   * @param user the Discord unique long ID.
   * @return an ArrayList of InfractionEntry's. Returns null if either the map itself is null, or the InfractionData
   *         object is null.
   */
  public static final ArrayList<InfractionEntry> getInfractionHistory(final long user) {
    
    if (infractionMap == null || infractionMap.get(user) == null) {
      return null;
    }
    
    return infractionMap.get(user).getHistory();
  }
  
  /**
   * Gets the number of times the user has been warned.
   * 
   * @param user the Discord unique long ID.
   * @return the warn count as an int. Returns -1 if the map is null.
   */
  public static final int getWarnCount(final long user) {
    
    if (infractionMap == null || infractionMap.get(user) == null) {
      return -1;
    }
    
    return infractionMap.get(user).getWarnCount();
  }
  
  /**
   * Sets the user's warn status and increments their warn count.
   * 
   * @param user the Discord unique long ID.
   * @param time the epoch time the user was warned.
   * @param reason the reason the user is being warned.
   * @return true if there were no errors; false if either the map itself is null, or if the user's InfractionData list
   *         is null.
   */
  public static final boolean warnUser(final long user, final long time, final String reason) {
    
    if (infractionMap == null || infractionMap.get(user) == null) {
      return false;
    }
    
    infractionMap.get(user).warn(time, reason);
    saveToFile();
    
    return true;
  }
  
  /**
   * Gets the user's muted status.
   * 
   * @param user the Discord unique long ID.
   * @return true if the user is muted; false if not, or if the table is null.
   */
  public static final boolean isMuted(final long user) {
    
    if (infractionMap == null || infractionMap.get(user) == null) {
      return false;
    }
    
    return infractionMap.get(user).isMuted();
  }
  
  /**
   * Gets how many times the user has been muted.
   * 
   * @param user the Discord unique long ID.
   * @return an int. Returns -1 if the map is null.
   */
  public static final int getMuteCount(final long user) {
    
    if (infractionMap == null || infractionMap.get(user) == null) {
      return -1;
    }
    
    return infractionMap.get(user).getMuteCount();
  }
  
  /**
   * Updates a user's muted status.
   * 
   * @param user the Discord unique long ID.
   * @param b true if muted; false if not.
   * @return true if there were no errors; false if either the map itself was null, or if the user's InfractionData list
   *         is null.
   */
  public static final boolean setMuted(final long user, final boolean b) {
    
    if (infractionMap == null || infractionMap.get(user) == null) {
      return false;
    }
    
    infractionMap.get(user).setMuted(b);
    saveToFile();
    return true;
  }
  
  /**
   * Sets a the user's muted status to true and increments their mute count.
   * 
   * @param user the Discord unique long ID.
   * @param issueTime the epoch time the user was muted.
   * @param muteDuration the amount of time in seconds the user should be muted for.
   * @param reason the reason the user is being muted.
   * @return true if there were no errors; false if either the map itself was null, or if the user's InfractionData list
   *         is null.
   */
  public static final boolean muteUser(final long user, final long issueTime, final int muteDuration,
      final String reason) {
    
    if (infractionMap == null || infractionMap.get(user) == null) {
      return false;
    }
    
    infractionMap.get(user).mute(issueTime, muteDuration, reason);
    saveToFile();
    return true;
  }
  
  /**
   * Gets how many times the user has been kicked.
   * 
   * @param user the Discord unique long ID.
   * @return an int; returns -1 if the map or the user's InfractionData list is null.
   */
  public static final int getKickCount(final long user) {
    
    if (infractionMap == null || infractionMap.get(user) == null) {
      return -1;
    }
    
    return infractionMap.get(user).getKickCount();
  }
  
  /**
   * Setsu the user's kicked status to true and increments their kick count.
   * 
   * @param user the Discord unique long ID.
   * @param time the epoch time the user was kicked.
   * @param reason the reason the user is being kicked.
   * @return true if there were no errors; false if either the map itself was null, or if the user's InfractionData list
   *         is null.
   */
  public static final boolean kickUser(final long user, final long time, final String reason) {
    
    if (infractionMap == null || infractionMap.get(user) == null) {
      return false;
    }
    
    infractionMap.get(user).kick(time, reason);
    saveToFile();
    return true;
  }
  
  /**
   * Gets whether or not the user is banned.
   * 
   * @param user the Discord unique long ID.
   * @return true if the user is banned; false if not or if something is null.
   */
  public static final boolean isBanned(final long user) {
    
    if (infractionMap == null || infractionMap.get(user) == null) {
      return false;
    }
    
    return infractionMap.get(user).isBanned();
  }
  
  /**
   * Gets how many times the user has been banned.
   * 
   * @param user the Discord unique long ID.
   * @return an int; returns -1 if something is null.
   */
  public static final int getBanCount(final long user) {
    
    if (infractionMap == null || infractionMap.get(user) == null) {
      return -1;
    }
    
    return infractionMap.get(user).getBanCount();
  }
  
  /**
   * Sets the user's banned status.
   * 
   * @param user the Discord unique long ID.
   * @param b true if the user is banned; false if not.
   * @return true if there were no errors; false if either the map itself was null, or if the user's InfractionData list
   *         is null.
   */
  public static final boolean setBanned(final long user, final boolean b) {
    
    if (infractionMap == null || infractionMap.get(user) == null) {
      return false;
    }
    
    infractionMap.get(user).setBanned(b);
    saveToFile();
    return true;
  }
  
  /**
   * Sets the user's banned status to true and increments their ban count.
   * 
   * @param user the Discord unique long ID.
   * @param time the epoch time the user was banned.
   * @param reason the reason the user is being banned.
   * @return true if there were no errors; false if either the map itself was null, or if the user's InfractionData list
   *         is null.
   */
  public static final boolean banUser(final long user, final long time, final String reason) {
    
    if (infractionMap == null || infractionMap.get(user) == null) {
      return false;
    }
    
    infractionMap.get(user).ban(time, reason);
    saveToFile();
    return true;
  }
  
  /**
   * Clears the infractions records for the specified user.
   * 
   * @param user the Discord unique long ID.
   * @return true if there were no errors; false if something was null.
   */
  public static final boolean clearInfractions(final long user) {
    
    if (infractionMap == null) {
      return false;
    }
    
    infractionMap.put(user, new InfractionData());
    saveToFile();
    return true;
  }
  
  /**
   * Clears the infractions table, but does not save it to disk. USE WITH CAUTION.
   */
  public static final void initTable() {
    infractionMap = new TreeMap<Long, InfractionData>();
  }
  
  /**
   * Saves the infractions table to disk.
   */
  public static final void saveToFile() {
    
    // Don't actually save if we're just testing
    if (!saveToDisk) {
      return;
    }
    
    System.out.println("Saving infractions map to file.");
    
    try (FileOutputStream fos = new FileOutputStream(INFRACTIONS_DATA_PATH);
        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
      
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
    
    // Create the file if it doesn't exist.
    if (!INFRACTIONS_FILE.exists()) {
      try {
        System.out.println("Infractions file doesn't exist. Creating new.");
        INFRACTIONS_FILE.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    Object obj = null;
    try (FileInputStream fis = new FileInputStream(INFRACTIONS_DATA_PATH);) {
      
      // Build an input stream for deserialization
      if (fis.available() > 0) {
        
        ObjectInputStream oin = new ObjectInputStream(fis);
        
        // Read the object in and close the streams
        obj = oin.readObject();
        oin.close();
        fis.close();
        
        // If something went wrong, return null
        if (obj == null) {
          System.err.println("Infractions map load error.");
          initTable();
          saveToFile();
          return;
        }
        
      } else {
        System.out.println("Infractions map is empty.");
        initTable();
        return;
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
      infractionMap = (TreeMap<Long, InfractionData>) obj;
    } else {
      System.err.println("Error with loading. Creating new table.");
      initTable();
    }
  }
}
