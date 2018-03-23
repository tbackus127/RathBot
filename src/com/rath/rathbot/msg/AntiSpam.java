
package com.rath.rathbot.msg;

import java.util.ArrayList;
import java.util.TreeMap;

import com.rath.rathbot.disc.InfractionEntry;
import com.rath.rathbot.disc.Infractions;

import sx.blah.discord.handle.obj.IMessage;

/**
 * This class contains anti-spam checks for the bot.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class AntiSpam {
  
  /** Permissions level required to bypass spam filtering. */
  public static final int PERM_LVL_IGNORE = 9;
  
  /** Whether or not to ignore messages containing only a single image (for dumps). */
  public static final boolean IGNORE_IMAGE_POSTS = true;
  
  /** Cooldown between spam warnings, in seconds. */
  private static final int WARN_COOLDOWN_SEC = 8;
  
  /** Mute durations, in seconds. 10s, 5m, 1hr, 24hr */
  public static final int[] MUTE_DURATIONS = { 10, 300, 3600, 86400 };
  // TODO: This. When muted, their messages are immediately deleted.
  
  /** How many messages a user needs to send while muted to be kicked. */
  private static final int MUTE_KICK_THRESHOLD = 10;
  // TODO: ^ If they keep spamming, they get kicked.
  
  /** The reason that will be entered in infraction entries when the message rate is abused. */
  private static final String REASON_MSG_RATE_ABUSE = "RB rate abuse";
  
  /** The reason that will be entered in infraction entries when repeat messages are abused. */
  private static final String REASON_REPEAT_MSG_ABUSE = "RB repeat abuse";
  
  /** If messages begin with these characters, do not count them in repeat message flagging. */
  private static final String[] REPEAT_PREFIX_EXCEPTIONS = { ".", "rb!", "=", ">", "t!", "+", ";;" };
  
  /**
   * How many messages trigger spam protection for the amount of time indicated by the same index of
   * TRIGGER_RATE_TIMEOUT_SECS.
   */
  private static final int[] TRIGGER_RATE_MSG_COUNTS = { 15, 12 };
  
  /**
   * The amount of time that anti-spam can trigger for the counts defined in the same index of TRIGGER_RATE_MSG_COUNTS.
   */
  private static final int[] TRIGGER_RATE_TIMEOUT_SECS = { 3, 5 };
  
  /** The number of sequential, duplicate messages required to trigger repeat message abuse. */
  private static final int TRIGGER_DUPLICATE_MSG_COUNT = 5;
  
  /**
   * The maximum number of messages to keep in a user's history. This should be the maximum value in
   * TRIGGER_RATE_MSG_COUNTS.
   */
  private static final int MAX_HISTORY_LENGTH = 15;
  
  /** A set of all tracked users. */
  private static final TreeMap<Long, ArrayList<MessageTrackerEntry>> trackerEntries = new TreeMap<Long, ArrayList<MessageTrackerEntry>>();
  
  /**
   * Adds a message and timestamp for the given user.
   * 
   * @param msg the IMessage container to add an entry from.
   */
  public static final void addEntry(final IMessage msg) {
    
    // Create a new entry if the author doesn't exist
    final long authorID = msg.getAuthor().getLongID();
    if (!trackerEntries.containsKey(authorID)) {
      // System.out.println("Creating new entry for " + authorID);
      trackerEntries.put(authorID, new ArrayList<MessageTrackerEntry>());
    }
    
    // Add the message to the user's list
    final ArrayList<MessageTrackerEntry> messageHistory = trackerEntries.get(authorID);
    final int historySize = messageHistory.size();
    // System.out.println("History size: " + historySize);
    
    // Remove the last message in the list if it has too many entries
    if (historySize >= MAX_HISTORY_LENGTH) {
      // System.out.println("Purged last entry in history.");
      messageHistory.remove(historySize - 1);
    }
    
    // Insert the new message at the beginning of the list
    messageHistory.add(0, new MessageTrackerEntry(msg.getContent(), msg.getTimestamp().getEpochSecond()));
    
  }
  
  /**
   * Checks if a message rate threshold has been passed.
   * 
   * @param msg Discord IMessage object from the EventHandler.
   * @return true if the anti-spam was triggered.
   */
  public static final boolean checkMessageRate(final IMessage msg) {
    
    // Unpack message data
    final long authorID = msg.getAuthor().getLongID();
    final ArrayList<InfractionEntry> infrHistory = Infractions.getInfractionHistory(authorID);
    final long msgTime = msg.getTimestamp().getEpochSecond();
    
    // If the member has an infraction history
    if (infrHistory.size() >= 1) {
      
      // If the last infraction was for message rate abuse, check if the cooldown has passed
      final InfractionEntry entry = infrHistory.get(0);
      if ((entry.getReason().equals(REASON_MSG_RATE_ABUSE) && (msgTime - entry.getTimestamp() < WARN_COOLDOWN_SEC))) {
        return false;
      }
      
    }
    
    // Iterate through their history
    final ArrayList<MessageTrackerEntry> messageHistory = trackerEntries.get(authorID);
    for (int i = 0; i < TRIGGER_RATE_MSG_COUNTS.length; i++) {
      
      // Check if there are enough messages to check the rate thresholds
      final int historySize = messageHistory.size();
      if (historySize >= TRIGGER_RATE_MSG_COUNTS[i]) {
        
        // Compute number of seconds between message counts for each trigger
        final long start = messageHistory.get(TRIGGER_RATE_MSG_COUNTS[i] - 1).getTime();
        final long end = messageHistory.get(0).getTime();
        
        // If the delta is greater than the threshold, trigger the spam protection
        if ((end - start) < TRIGGER_RATE_TIMEOUT_SECS[i]) {
          
          // Add an infraction as message rate abuse to the user's infraction data
          Infractions.warnUser(authorID, msgTime, REASON_MSG_RATE_ABUSE);
          return true;
        }
      }
    }
    
    // Protection did not trigger
    return false;
  }
  
  /**
   * Checks if the author's last few messages are duplicates.
   * 
   * @param msg Discord IMessage object from the EventHandler.
   * @return true if the anti-spam was triggered.
   */
  public static final boolean checkRepeatMessages(final IMessage msg) {
    
    System.out.println("Checking for repeats.");
    
    // Ignore bot commands
    final String message = msg.getContent();
    for (int i = 0; i < REPEAT_PREFIX_EXCEPTIONS.length; i++) {
      if (message.startsWith(REPEAT_PREFIX_EXCEPTIONS[i])) {
        return false;
      }
    }
    
    // Unpack infraction data
    final long uid = msg.getAuthor().getLongID();
    ArrayList<InfractionEntry> infrHistory = Infractions.getInfractionHistory(uid);
    final long msgTime = msg.getTimestamp().getEpochSecond();
    
    // If the member has an infraction history
    if (infrHistory.size() >= 1) {
      
      System.out.println("  Has infraction history.");
      
      // If the last infraction was for repeat message abuse, check if the cooldown has passed
      final InfractionEntry entry = infrHistory.get(0);
      if ((entry.getReason().equals(REASON_REPEAT_MSG_ABUSE) && (msgTime - entry.getTimestamp() < WARN_COOLDOWN_SEC))) {
        System.out.println("  On cooldown.");
        return false;
      }
      
    }
    
    // Iterate through their history
    final ArrayList<MessageTrackerEntry> messageHistory = trackerEntries.get(uid);
    
    // Check if there are enough messages to check the rate thresholds
    final int historySize = messageHistory.size();
    if (historySize >= TRIGGER_DUPLICATE_MSG_COUNT) {
      
      System.out.println("  Checking message history.");
      
      // Count the number of duplicate messages
      int count = 0;
      String currMsg = msg.getContent();
      for (int i = 0; i < TRIGGER_DUPLICATE_MSG_COUNT; i++) {
        
        final MessageTrackerEntry entry = messageHistory.get(i);
        final String entryMsg = entry.getMessage();
        
        // If we encounter a message that isn't a duplicate, stop searching and return false
        if (!entryMsg.equals(currMsg)) {
          System.out.println("  Different message found, aborting.");
          return false;
        }
        
        System.out.println("  Duplicate message detected.");
        currMsg = entryMsg;
        count++;
        
      }
      
      // Trigger if the count meets the threshold
      if (count >= TRIGGER_DUPLICATE_MSG_COUNT) {
        messageHistory.clear();
        return true;
      }
      
    } else {
      System.out.println("  History not large enough yet.");
    }
    
    // Protection did not trigger
    return false;
    
  }
  
}
