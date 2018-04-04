
package com.rath.rathbot.cmd.msg.faq;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.exceptions.FAQNotFoundException;
import com.rath.rathbot.msg.MessageHelper;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

/**
 * This class handles the 'faq' command, which provides a way to store and recall any text.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class FAQCmd extends RBCommand {
  
  /** The path to the saved FAQ Strings. */
  private static final String FAQ_DATA_PATH = RathBot.DIR_DATA + "faq.dat";
  
  /** The command String for this command. */
  private static final String FAQ_CMD = "faq";
  
  /** The command description. */
  private static final String FAQ_DESCR = "Allows the storing and recalling of text blocks.";
  
  /** The usage message when the command has a syntactic or semantic error. */
  private static final String FAQ_USAGE = "rb! faq <faqName>";
  
  /** Reference to the faqMap file. */
  private static final File FAQ_FILE = new File(FAQ_DATA_PATH);
  
  /** A map from FAQ name to its contents. */
  private static TreeMap<String, String> faqMap = new TreeMap<String, String>();
  
  /** Whether or not to actually save the FAQ table to disk (for testing purposes). */
  private static boolean saveToDisk = true;
  
  /**
   * Disables saving the FAQ table to disk.
   */
  public static final void disableSaveToDisk() {
    saveToDisk = false;
  }
  
  /**
   * Gets a list of FAQs that are currently set.
   * 
   * @return a String containing a list of FAQs that are currently registered.
   */
  public static final String getFaqList() {
    
    if (faqMap == null) {
      System.out.println("faqMap is null!");
      return null;
    }
    
    // Check that the map has entries in it
    if (faqMap.size() <= 0) {
      return "There are no saved FAQs.";
    }
    // Construct the FAQ list message
    String message = "FAQ List:\n";
    for (final String s : faqMap.keySet()) {
      message += ("  " + s + "\n");
    }
    return message;
  }
  
  /**
   * Tests if the FAQ map has a specific ID registered.
   * 
   * @param faq the FAQ ID.
   * @return true if the map's key set contains the ID; false if not.
   */
  public static final boolean hasFaq(final String faq) {
    
    System.out.println("hasFaq");
    
    if (faqMap == null) {
      System.out.println("faqMap is null!");
      return false;
    }
    return faqMap.containsKey(faq);
  }
  
  /**
   * Sends a FAQ's contents to the specific channel.
   * 
   * @param faq the FAQ ID to fetch the message of.
   * @return the contents of the FAQ specified as a String.
   */
  public static final String getFaq(final String faq) {
    
    System.out.println("getFaq");
    
    if (faqMap == null) {
      System.out.println("faqMap is null!");
      return null;
    }
    
    if (faqMap.containsKey(faq)) {
      return faqMap.get(faq);
    } else {
      return null;
    }
  }
  
  /**
   * Adds or replaces a FAQ entry.
   * 
   * @param faqName the FAQ ID.
   * @param message the FAQ's contents.
   */
  public static final void addFaq(final String faqName, final String message) {
    
    System.out.println("addFaq");
    
    if (faqMap == null) {
      System.out.println("faqMap is null!");
      return;
    }
    
    if (hasFaq(faqName)) {
      System.out.println("Editing FAQ: \"" + faqName + "\" to \"" + message + "\".");
    } else {
      System.out.println("Adding FAQ: \"" + faqName + "\" -> \"" + message + "\".");
    }
    
    faqMap.put(faqName, message);
    saveFAQMap();
  }
  
  /**
   * Removes a FAQ entry.
   * 
   * @param faqName the FAQ ID.
   * @throws FAQNotFoundException if the FAQ entry does not exist.
   */
  public static final void removeFaq(final String faqName) throws FAQNotFoundException {
    
    System.out.println("removeFaq");
    
    // If the map isn't created yet for some reason, do it.
    if (faqMap == null) {
      initFAQ();
    }
    
    System.out.println("Removing FAQ: \"" + faqName + "\".");
    
    // Remove the mapping and save
    if (faqMap.containsKey(faqName)) {
      faqMap.remove(faqName);
      saveFAQMap();
    } else {
      throw new FAQNotFoundException(faqName);
    }
    
  }
  
  /**
   * Removes all mappings from the FAQ map.
   */
  public static final void clearFAQMap() {
    
    System.out.println("clearFaqMap");
    
    if (faqMap == null) {
      System.out.println("faqMap is null!");
      return;
    }
    faqMap.clear();
    saveFAQMap();
  }
  
  @Override
  public Set<RBCommand> getSubcommands() {
    
    final Set<RBCommand> result = new HashSet<RBCommand>();
    
    // All sub-commands of the 'faq' command are registered here.
    result.add(new FAQListCmd());
    result.add(new FAQEditCmd());
    result.add(new FAQRemoveCmd());
    
    return result;
  }
  
  @Override
  public boolean executeCommand(final IUser author, final IChannel channel, final String[] tokens,
      final int tokenDepth) {
    
    // If there are no subcommands for this command
    if (!super.executeCommand(author, channel, tokens, tokenDepth)) {
      
      System.out.println("Executing faq");
      
      // Print the usage info if there aren't any arguments
      if (tokens.length <= 2) {
        RathBot.sendMessage(channel, "Usage: " + getCommandUsage());
        return RBCommand.STOP_CMD_SEARCH;
      }
      
      // Either post the FAQ contents, or that it doesn't exist
      if (faqMap.containsKey(tokens[2])) {
        RathBot.sendMessage(channel, faqMap.get(tokens[2]));
      } else {
        RathBot.sendMessage(channel, "FAQ \"" + tokens[2] + "\" doesn't exist.");
      }
    }
    
    return RBCommand.STOP_CMD_SEARCH;
    
  }
  
  @Override
  public String getCommandName() {
    
    return FAQ_CMD;
  }
  
  @Override
  public String getCommandDescription() {
    
    return FAQ_DESCR;
  }
  
  @Override
  public String getCommandUsage() {
    return FAQ_USAGE;
  }
  
  @Override
  public int permissionLevelRequired() {
    
    return RBCommand.PERM_STANDARD;
  }
  
  @Override
  public void setupCommand() {
    initFAQ();
    saveFAQMap();
  }
  
  @Override
  public boolean requiresDirectMessage() {
    return false;
  }
  
  /**
   * Initializes the FAQ map.
   */
  private static final void initFAQ() {
    
    // If the file doesn't exist, create it
    if (!FAQ_FILE.exists()) {
      System.out.println("  File " + FAQ_FILE.getAbsolutePath() + " doesn't exist, creating.");
      try {
        FAQ_FILE.createNewFile();
        faqMap = new TreeMap<String, String>();
        return;
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      
      // If the file is empty, create a new TreeMap for it
      if (FAQ_FILE.length() <= 0) {
        System.out.println("  Map data empty. Creating new table.");
        faqMap = new TreeMap<String, String>();
        return;
      }
    }
    
    loadFAQMap();
    
  }
  
  /**
   * Loads the FAQ map from file.
   */
  @SuppressWarnings("unchecked")
  private static final void loadFAQMap() {
    
    // Initialize data streams
    FileInputStream fis = null;
    ObjectInputStream oin = null;
    Object obj = null;
    
    try {
      
      // Build an input stream for deserialization
      fis = new FileInputStream(FAQ_DATA_PATH);
      if (fis.available() > 0) {
        oin = new ObjectInputStream(fis);
        
        // Read the object in and close the streams
        obj = oin.readObject();
        oin.close();
        fis.close();
        
      } else {
        System.err.println("FAQ map is empty.");
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
      System.err.println("FAQ map load error.");
    }
    
    // Cast the read object to a TreeMap
    if (obj instanceof TreeMap) {
      faqMap = (TreeMap<String, String>) obj;
    } else {
      System.err.println("Error with loading. Creating new table.");
      faqMap = new TreeMap<String, String>();
    }
  }
  
  /**
   * Saves the FAQ map to a file.
   */
  private static final void saveFAQMap() {
    
    if (!saveToDisk) {
      return;
    }
    
    FileOutputStream fos = null;
    ObjectOutputStream oos = null;
    
    try {
      
      // Build an output stream for serialization
      fos = new FileOutputStream(FAQ_DATA_PATH);
      oos = new ObjectOutputStream(fos);
      
      // Write the map and close streams
      oos.writeObject(faqMap);
      oos.close();
      fos.close();
      
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
}
