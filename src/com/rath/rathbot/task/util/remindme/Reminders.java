
package com.rath.rathbot.task.util.remindme;

import java.io.File;
import java.util.TreeMap;

import com.rath.rathbot.RathBot;

/**
 * This class contains the data structure for reminders, used by the remindme command/task.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class Reminders {
  
  /** A table that maps a UID to a table mapping a label to a reminder entry. */
  private static TreeMap<Long, TreeMap<String, ReminderEntry>> reminderTable = null;
  
  /** The filename for the base user table data. */
  private static final String REMINDER_DATA_FILENAME = "reminderTable.dat";
  
  /** Prefix for a user's reminder table. */
  private static final String REMINDER_TABLE_PREFIX = "reminderEntry-";
  
  /** Suffix for a user's reminder table. */
  private static final String REMINDER_TABLE_SUFFIX = ".dat";
  
  /** The path for the permission data. */
  private static final String REMINDER_DATA_PATH = RathBot.DIR_DATA + REMINDER_DATA_FILENAME;
  
  /** A handle to the table as a File object. */
  private static final File REMINDER_TABLE_FILE = new File(REMINDER_DATA_PATH);
  
  /** Whether or not to save the reminder table to the disk on every update. */
  private static boolean saveToDisk = true;
  
  /**
   * 
   */
  public static final void initTable() {
    reminderTable = new TreeMap<Long, TreeMap<String, ReminderEntry>>();
  }
  
  /**
   * 
   * @param uid
   * @return
   */
  public static final boolean initUser(Long uid) {
    
    if (reminderTable == null) {
      System.err.println("Reminder user table is null!");
      return false;
    }
    
    if (reminderTable.put(uid, new TreeMap<String, ReminderEntry>()) == null) {
      return false;
    }
    saveToDisk();
    return true;
  }
  
  /**
   * 
   * @return
   */
  public static final TreeMap<Long, TreeMap<String, ReminderEntry>> getReminderTable() {
    
    return reminderTable;
  }
  
  /**
   * 
   * @param uid
   * @return
   */
  public static final TreeMap<String, ReminderEntry> getUserTable(final Long uid) {
    
    if (reminderTable == null) {
      System.err.println("Reminder user table is null!");
      return null;
    }
    
    final TreeMap<String, ReminderEntry> res = reminderTable.get(uid);
    if (res == null) {
      return null;
    }
    
    saveToDisk();
    return res;
  }
  
  /**
   * 
   * @param uid
   * @param label
   * @param entry
   * @return
   */
  public static final boolean add(final Long uid, final String label, final ReminderEntry entry) {
    
    if (reminderTable == null) {
      System.err.println("Reminder user table is null!");
      return false;
    }
    
    if (!reminderTable.containsKey(uid)) {
      initUser(uid);
    }
    
    reminderTable.get(uid).put(label, entry);
    saveToDisk();
    return true;
  }
  
  /**
   * 
   * @param uid
   * @param label
   * @return
   */
  public static final boolean remove(final Long uid, final String label) {
    
    if (reminderTable == null) {
      System.err.println("Reminder user table is null!");
      return false;
    }
    
    final TreeMap<String, ReminderEntry> remTable = reminderTable.get(uid);
    if (remTable == null) {
      System.err.println("Reminder entry table is null!");
      return false;
    }
    
    if (remTable.remove(label) == null) {
      return false;
    }
    
    saveToDisk();
    return true;
  }
  
  /**
   * 
   */
  public static final void disableSaveToDisk() {
    saveToDisk = false;
  }
  
  /**
   * 
   */
  private static final void saveToDisk() {
    
    if (!saveToDisk) {
      return;
    }
    
    // TODO: Write saveToDisk() for Reminders. Don't forget to save all user's entry tables.
  }
  
  /**
   * 
   */
  private static final void loadFromDisk() {
    
    // TODO: Write loadFromDisk() for Reminders. Don't forget to load all user's entry tables.
  }
  
}
