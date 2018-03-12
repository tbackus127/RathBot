
package com.rath.rathbot.cmd.faq;

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
import com.rath.rathbot.data.DataLoader;
import com.rath.rathbot.exceptions.FAQNotFoundException;

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
  
  /** A map from FAQ name to its contents. */
  private static TreeMap<String, String> faqMap = null;
  
  /** The command String for this command. */
  private static final String FAQ_CMD = "faq";
  
  /** The command description. */
  private static final String FAQ_DESCR = "Allows the storing and recalling of text blocks.";
  
  /** The handler for saving/loading this class' data. */
  private static final DataLoader<TreeMap<String, String>> loader = new DataLoader<TreeMap<String, String>>(
      TreeMap.class, faqMap, FAQ_DATA_PATH);
  
  /**
   * Initializes the FAQ map.
   * 
   * @return a TreeMap mapping FAQ ID to its contents.
   */
  private static final TreeMap<String, String> initFAQ() {
    
    System.out.println("faqMap init");
    
    final File fdat = new File(FAQ_DATA_PATH);
    
    // If the file doesn't exist, create it
    if (!fdat.exists()) {
      System.out.println("  File " + fdat.getAbsolutePath() + " doesn't exist, creating.");
      try {
        fdat.createNewFile();
        return new TreeMap<String, String>();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      
      // If the file is empty, create a new TreeMap for it
      if (fdat.length() <= 0) {
        System.out.println("  Map data empty. Creating new table.");
        return new TreeMap<String, String>();
      }
    }
    
    return loadFAQMap(fdat);
    
  }
  
  /**
   * Loads the FAQ map from file.
   * 
   * @return the previously-serialized HashMap.
   */
  private static final TreeMap<String, String> loadFAQMap(final File file) {
    
    System.out.println("Loading FAQ map from file.");
    return loader.loadFromDisk();
  }
  
  /**
   * Saves the FAQ map to a file.
   */
  private static final void saveFAQMap() {
    
    loader.saveToDisk();
  }
  
  /**
   * Posts the list of FAQs that are currently set.
   * 
   * @param channel the channel to send the list to. Returns null if the map is empty.
   */
  public static final String getFaqList() {
    
    System.out.println("getFaqList");
    
    if (faqMap == null) {
      System.out.println("faqMap is null!");
      return null;
    }
    
    // Check that the map has entries in it
    if (faqMap.size() <= 0) {
      return null;
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
  public boolean executeCommand(final RathBot rb, final IUser author, final IChannel channel, final String[] tokens,
      final int tokenDepth) {
    
    // If there are no subcommands for this command
    if (!super.executeCommand(rb, author, channel, tokens, tokenDepth)) {
      
      // TODO: Do normal "rb! faq" stuff (post help message)
      System.out.println("Executing faq");
      
    }
    
    return true;
    
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
  public int permissionLevelRequired() {
    return RBCommand.PERM_STANDARD;
  }
  
  @Override
  public void setupCommand() {
    faqMap = initFAQ();
    saveFAQMap();
  }
  
}
