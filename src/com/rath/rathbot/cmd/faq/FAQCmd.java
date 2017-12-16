
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
import com.rath.rathbot.exceptions.FAQNotFoundException;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

/**
 * This class handles the 'faq' command, which provides a way to store and recall any text.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class FAQCmd extends RBCommand {
  
  /** The path to the saved FAQ Strings. */
  private static final String FAQ_DATA_PATH = "dat/faq.dat";
  
  /** A map from FAQ name to its contents. */
  private static final TreeMap<String, String> faqMap = loadFAQMap(FAQ_DATA_PATH);
  
  /** The command String for this command. */
  private static final String FAQ_CMD = "faq";
  
  /** The command description. */
  private static final String FAQ_DESCR = "Allows the storing and recalling of text blocks.";
  
  /**
   * Default constructor.
   * 
   * @param subs the sub-commands this command has in its hierarchy.
   */
  public FAQCmd() {
    super(FAQ_CMD, false, FAQ_DESCR);
  }
  
  /**
   * Loads the FAQ map from file.
   * 
   * @return the previously-serialized HashMap.
   */
  @SuppressWarnings("unchecked")
  private static final TreeMap<String, String> loadFAQMap(final String path) {
    
    // Check if FAQ data exists before attempting to load
    final File f = new File(path);
    if (!f.exists() || !f.canRead()) {
      System.err.println("File " + path + " does not exist or is not readable!");
      return null;
    }
    
    // Initialize data streams
    FileInputStream fis = null;
    ObjectInputStream oin = null;
    TreeMap<String, String> result = null;
    Object obj = null;
    try {
      
      // Build an input stream for deserialization
      fis = new FileInputStream(FAQ_DATA_PATH);
      oin = new ObjectInputStream(fis);
      
      // Read the object in and close the streams
      obj = oin.readObject();
      oin.close();
      fis.close();
      
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    
    // If something went wrong, return null
    if (fis == null || oin == null) {
      return null;
    }
    
    // Cast the read object to a TreeMap
    if (obj instanceof TreeMap) {
      result = (TreeMap<String, String>) obj;
    } else {
      return null;
    }
    
    return result;
  }
  
  /**
   * Saves the FAQ map to a file.
   */
  private static final void saveFAQMap() {
    
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
  
  /**
   * Posts the list of FAQs that are currently set.
   * 
   * @param channel the channel to send the list to. Returns null if the map is empty.
   */
  public static final String getFaqList() {
    
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
    return faqMap.containsKey(faq);
  }
  
  /**
   * Sends a FAQ's contents to the specific channel.
   * 
   * @param faq the FAQ ID to fetch the message of.
   */
  public static final String getFaq(final String faq) {
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
    System.out.println("Adding FAQ: \"" + faqName + "\" -> \"" + message + "\".");
    faqMap.put(faqName, message);
  }
  
  /**
   * Removes a FAQ entry.
   * 
   * @param faqName the FAQ ID.
   * @throws FAQNotFoundException if the FAQ entry does not exist.
   */
  public static final void removeFaq(final String faqName) throws FAQNotFoundException {
    System.out.println("Removing FAQ: \"" + faqName + "\".");
    
    if (faqMap.containsKey(faqName)) {
      faqMap.remove(faqName);
    } else {
      throw new FAQNotFoundException(faqName);
    }
  }
  
  @Override
  public Set<RBCommand> getSubcommands() {
    
    final Set<RBCommand> result = new HashSet<RBCommand>();
    
    // TODO: list
    // TODO: edit
    // TODO: remove
    
    return result;
  }
  
  @Override
  public void executeCommand(RathBot rb, IUser author, IChannel channel, String[] tokens) {
    // TODO Auto-generated method stub
    
  }
  
}
