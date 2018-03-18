
package com.rath.rathbot.msg;

import java.util.ArrayList;
import java.util.TreeMap;

import sx.blah.discord.handle.obj.IMessage;

/**
 * This class contains anti-spam checks for the bot.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class AntiSpam {
  
  /**
   * How many messages trigger spam protection for the amount of time indicated by the same index of
   * TRIGGER_RATE_TIMEOUT_SECS.
   */
  private static final int[] TRIGGER_RATE_MSG_COUNTS = { 15, 12 }; // 15, 12
  
  /**
   * The amount of time that anti-spam can trigger for the counts defined in the same index of TRIGGER_RATE_MSG_COUNTS.
   */
  private static final int[] TRIGGER_RATE_TIMEOUT_SECS = { 3, 5 }; // 3, 5
  
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
    
    // System.out.println("Checking message rate.");
    
    final long authorID = msg.getAuthor().getLongID();
    final ArrayList<MessageTrackerEntry> messageHistory = trackerEntries.get(authorID);
    
    for (int i = 0; i < TRIGGER_RATE_MSG_COUNTS.length; i++) {
      
      // System.out.println(" For " + TRIGGER_RATE_MSG_COUNTS[i] + "x in " + TRIGGER_RATE_TIMEOUT_SECS[i] + "s");
      
      // Check if there are enough messages to check the rate thresholds
      final int historySize = messageHistory.size();
      if (historySize >= TRIGGER_RATE_MSG_COUNTS[i]) {
        
        // System.out.println(" " + historySize + ":" + TRIGGER_RATE_MSG_COUNTS[i]);
        
        // Compute number of seconds between message counts for each trigger
        final long start = messageHistory.get(TRIGGER_RATE_MSG_COUNTS[i] - 1).getTime();
        final long end = messageHistory.get(0).getTime();
        // System.out.println(" Start = " + start);
        // System.out.println(" End = " + end);
        // System.out.println(" Delta = " + (end - start));
        
        // If the delta is greater than the threshold, trigger the spam protection
        if ((end - start) < TRIGGER_RATE_TIMEOUT_SECS[i]) {
          // System.out.println(" !! Triggered !!");
          return true;
        }
      }
    }
    
    return false;
  }
  
  public static final boolean checkRepeatMessages(final IMessage msg) {
    // TODO Auto-generated method stub
    return false;
  }
  
  public static final boolean checkJunkMessages(final IMessage msg) {
    // TODO Auto-generated method stub
    return false;
  }
  
}
