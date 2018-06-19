
package com.rath.rathbot;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * This class handles reading authentication tokens for Discord and other APIs.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class RBConfig {
  
  /** Whether or not the config file has been loaded. */
  private static boolean hasLoaded = false;
  
  /** The map of config names to their values as Strings. */
  private static TreeMap<String, String> configMap = null;
  
  /** The order of the expected config name-value pairs. */
  private static final String[] CONFIG_NAMES = { "authKey", "timeZone", "guildID", "reportChannelID",
      "reportChannelName" };
  
  /**
   * Loads the config values from file and populates the config map.
   * 
   * @param configFileName the relative path to the config file.
   * @return true if there were problems parsing the config file; false if not.
   */
  public static final boolean loadConfigMap(final String configPath) {
    
    final File confFile = new File(configPath);
    
    // Check file existence
    if (!confFile.exists()) {
      System.err.println("Config file \"" + confFile.getAbsolutePath() + "\" does not exist!");
      return false;
    }
    
    // Check if it's actually a file and we can read from it
    if (!confFile.isFile() || !confFile.canRead()) {
      System.err.println("Config file is unreadable!");
      return false;
    }
    
    try (Scanner fScan = new Scanner(confFile)) {
      
      if (!parseLines(fScan)) {
        System.err.println("Error reading config file!");
        return false;
      }
      
    } catch (IOException ioe) {
      System.err.println("Error reading config file!");
      ioe.printStackTrace();
      return false;
    }
    
    hasLoaded = true;
    return true;
  }
  
  /**
   * Gets the Discord authentication token for the bot.
   * 
   * @return the authentication token as a String, or null if something goes wrong.
   */
  public static final String getAuthToken() {
    
    if (ensureInit()) {
      return (configMap.get("authKey"));
    }
    
    return null;
  }
  
  /**
   * Gets the unique guild ID the bot will be logging in to. Find it by enabling developer mode on Discord, right
   * clicking the guild icon, and clicking &quot;Copy ID&quot;.
   * 
   * @return the guild ID as a long, or -1 if something went wrong.
   */
  public static final long getGuildID() {
    
    if (!ensureInit()) {
      return -1L;
    }
    
    // Get the value from the map and parse it as a long
    final String gidStr = configMap.get("guildID").trim();
    long result = -1L;
    try {
      result = Long.parseLong(gidStr);
    } catch (NumberFormatException nfe) {
      System.err.println("Could not parse guildID from config map!");
      nfe.printStackTrace();
    }
    
    return result;
    
  }
  
  /**
   * Gets the unique channel ID for the report audit channel of the guild. Find it by enabling developer mode on
   * Discord, right clicking the report channel, and clicking &quot;Copy ID&quot;.
   * 
   * @return
   */
  public static final long getReportChannelID() {
    
    if (!ensureInit()) {
      return -1L;
    }
    
    // Get the value from the map and parse it as a long
    final String rcStr = configMap.get("reportChannelID").trim();
    long result = -1L;
    try {
      result = Long.parseLong(rcStr);
    } catch (NumberFormatException nfe) {
      System.err.println("Could not parse reportChannelID from config map!");
      nfe.printStackTrace();
    }
    
    return result;
    
  }
  
  /**
   * Gets the report channel's name.
   * 
   * @return the name of the report channel (without the &quot;&#35;&quot;).
   */
  public static final String getReportChannelName() {
    
    if (ensureInit()) {
      return (configMap.get("reportChannelName"));
    }
    
    return null;
    
  }
  
  public static final ZoneId getTimeZone() {
    return ZoneId.of(configMap.get("timeZone"));
  }
  
  /**
   * Ensures that everything was initialized before fetching a config value.
   * 
   * @return true if Everything's Alright; false if not.
   */
  private static final boolean ensureInit() {
    
    // Check if the file has been loaded before attempting to get anything
    if (!hasLoaded) {
      System.err.println("The config file has not been loaded!");
      return false;
    }
    
    // Make sure the config map isn't null
    if (configMap == null) {
      System.err.println("Config map is null!");
      return false;
    }
    
    return true;
  }
  
  /**
   * Parses the config file line-by-line for name-value pairs.
   * 
   * @param sc the Scanner already open on the config file.
   * @return true if everything went well; false if not.
   */
  private static final boolean parseLines(final Scanner sc) {
    
    int curLine = 0;
    configMap = new TreeMap<String, String>();
    
    while (sc.hasNextLine()) {
      
      final String line = sc.nextLine();
      
      // If the line doesn't contain at least three characters (1 for name, 1 for colon, 1 for value), it's invalid
      if (line.length() <= 3) {
        System.err.println("Config file line format invalid! (At line " + (curLine + 1) + ").");
        return false;
      }
      
      // If the line isn't empty and there isn't a colon, the config line is invalid
      if ((line.indexOf(':') == -1)) {
        System.err.println("Config file line format invalid! (At line " + (curLine + 1) + ").");
        return false;
      }
      
      // Split the line into tokens delimited by a colon, only split at the first instance
      final String[] tokens = line.split(":", 2);
      if (tokens.length != 2) {
        System.err.println("Config file line format invalid! (At line " + (curLine + 1) + ").");
        return false;
      }
      
      // Ensure that we're parsing the expected key
      if (!tokens[0].equals(CONFIG_NAMES[curLine])) {
        System.err.println("Expected key \"" + CONFIG_NAMES[curLine] + "\", but parsed \"" + tokens[0] + "\"!");
        return false;
      }
      
      // We've found a valid key-value pair, so add it to the map
      configMap.put(tokens[0], tokens[1]);
      System.out.println("Added config value \"" + tokens[0] + "\" -> \"" + tokens[1] + "\".");
      curLine++;
    }
    
    return true;
    
  }
  
}
