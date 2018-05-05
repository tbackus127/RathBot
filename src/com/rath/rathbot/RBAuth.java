
package com.rath.rathbot;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * This class handles reading authentication tokens for Discord and other APIs.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class RBAuth {
  
  /**
   * Handles reading the authentication token from a file.
   * 
   * @param confPath the path to the config file as a String.
   * @return the token itself as a String.
   */
  public static final String readToken(final String confPath) {
    
    final File confFile = new File(confPath);
    
    // Check file existence
    if (!confFile.exists()) {
      System.err.println("Config file \"" + confFile.getAbsolutePath() + "\" does not exist!");
      return null;
    }
    
    // Check if it's actually a file and we can read from it
    if (!confFile.isFile() || !confFile.canRead()) {
      System.err.println("Config file is unreadable!");
      return null;
    }
    
    // Open a scanner on the configuration file
    Scanner fscan = null;
    try {
      fscan = new Scanner(confFile);
      return readToken(fscan);
    } catch (IOException ioe) {
      ioe.printStackTrace(System.err);
    } finally {
      if (fscan != null) {
        fscan.close();
      }
    }
    return null;
  }
  
  /**
   * Reads the Discord authentication token from the file.
   * 
   * @param confScan a Scanner already open on the config file.
   * @return the token as a String.
   */
  public static final String readToken(final Scanner confScan) {
    
    if (confScan.hasNextLine()) {
      return confScan.nextLine().split(":")[1];
    } else {
      System.err.println("Scanner couldn't get token from config file.");
      return null;
    }
  }
  
}
