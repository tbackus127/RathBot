
package com.rath.rathbot.msg;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.PermissionsTable;
import com.rath.rathbot.disc.InfractionEntry;
import com.rath.rathbot.disc.Infractions;

import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IEmbed.IEmbedImage;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * This class contains anti-spam checks for the bot, which are unnecessary because I didn't know Discord has its own.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class AntiSpam {
  
  /** The reason that will be entered in infraction entries when the message rate is abused. */
  public static final String REASON_MSG_RATE_ABUSE = "RB rate abuse";
  
  /** The reason that will be entered in infraction entries when repeat messages are abused. */
  public static final String REASON_REPEAT_MSG_ABUSE = "RB repeat abuse";
  
  /** Permissions level required to bypass spam filtering. */
  private static final int PERM_LVL_IGNORE = 9;
  // TODO: Change this back to RBCommand.PERM_LVL_SUDO
  
  /** Whether or not to ignore messages containing only a single image (for dumps). */
  private static final boolean IGNORE_IMAGE_POSTS = true;
  
  /** Mute durations, in seconds. 1m, 5m, 30m, 1hr, 24hr */
  private static final int[] MUTE_DURATIONS = { 60, 300, 1800, 3600, 86400 };
  
  /** How many messages a user needs to send while muted to be kicked. */
  @SuppressWarnings("unused")
  private static final int MUTE_KICK_THRESHOLD = 10;
  // TODO: ^ If they keep posting after being muted, they get kicked.
  // TODO: Have the bot PM them with minutes left every N minutes. N depends on muted time left.
  
  /** How many warns it takes for the bot to mute a user. */
  private static final int WARNS_TO_MUTE = 5;
  
  /** How many mutes it takes for the bot to kick a user. */
  private static final int MUTES_TO_KICK = MUTE_DURATIONS.length;
  
  /** How many kicks it takes for the bot to ban a user. */
  private static final int KICKS_TO_BAN = 3;
  
  /** Cooldown between spam warnings, in seconds. */
  private static final int WARN_COOLDOWN_SEC = 8;
  
  /** If messages begin with these characters, do not count them in repeat message flagging. */
  private static final String[] REPEAT_PREFIX_EXCEPTIONS = { ".", "rb!", "=", ">", "t!", "+", ";;" };
  
  /**
   * How many messages trigger spam protection for the amount of time indicated by the same index of
   * TRIGGER_RATE_TIMEOUT_SECS.
   */
  private static final int[] TRIGGER_RATE_MSG_COUNTS = { 5, 20 };
  
  /**
   * The amount of time that anti-spam can trigger for the counts defined in the same index of TRIGGER_RATE_MSG_COUNTS.
   */
  private static final int[] TRIGGER_RATE_TIMEOUT_SECS = { 3, 8 };
  
  /** The number of sequential, duplicate messages required to trigger repeat message abuse. */
  private static final int TRIGGER_DUPLICATE_MSG_COUNT = 7;
  
  /**
   * The maximum number of messages to keep in a user's history. This should be the maximum value in
   * TRIGGER_RATE_MSG_COUNTS.
   */
  private static final int MAX_HISTORY_LENGTH = 18;
  
  /** A table of all tracked users to their chat history. */
  private static final TreeMap<Long, ArrayList<MessageTrackerEntry>> trackerEntries = new TreeMap<Long, ArrayList<MessageTrackerEntry>>();
  
  /**
   * Performs message filtering for spam and muted users.
   * 
   * @param message the IMessage the event handler received.
   * @return true if the spam filtering was flagged; false if not.
   */
  public static final boolean filterMessage(final IMessage message) {
    
    // If the user isn't in the infractions table, initialize them
    final IUser author = message.getAuthor();
    final long uid = author.getLongID();
    if (!Infractions.hasMember(uid)) {
      Infractions.initMember(uid);
    }
    
    // If the author is an owner, bypass anti-spam measures
    if (PermissionsTable.getLevel(uid) < AntiSpam.PERM_LVL_IGNORE) {
      return false;
    }
    
    // If the author's mute time is up, unmute them
    // TODO: ^ This. Use MUTE_DURATIONS, MUTE_KICK_THRESHOLD
    
    // If the author is muted, immediately delete muted users' messages
    if (Infractions.isMuted(uid)) {
      message.delete();
      return false;
    }
    
    // Check for spam and return a type if a flag is raised
    final SpamTrigger trig = checkSpamType(message);
    
    // If spam filtering flagged this message
    if (trig != null) {
      
      // Check if the bot needs to ban the user
      final int kickCount = Infractions.getKickCount(uid);
      final int muteCount = Infractions.getMuteCount(uid);
      final int warnCount = Infractions.getWarnCount(uid);
      if (kickCount >= KICKS_TO_BAN) {
        
        // Issue the ban
        RathBot.banUser(message, trig.getReason());
        
      } else if (muteCount >= MUTES_TO_KICK) {
        
        // Issue the kick
        RathBot.kickUser(message, trig.getReason());
        
      } else if (warnCount >= WARNS_TO_MUTE) {
        
        // Issue the mute
        RathBot.muteUser(message, MUTE_DURATIONS[muteCount], trig.getReason());
        
      } else {
        
        // Issue the warn
        RathBot.warnUser(message, trig.getReason());
        
      }
      
      return true;
    }
    return false;
  }
  
  /**
   * Adds a message and timestamp for the given user.
   * 
   * @param msg the IMessage container to add an entry from.
   */
  public static final void addMessageHistoryEntry(final IMessage msg) {
    
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
   * Tests if a message triggers an anti-spam mechanism.
   * 
   * @param msg the IMessage event.
   * @return true if spam; false if not.
   */
  public static final SpamTrigger checkSpamType(final IMessage msg) {
    
    addMessageHistoryEntry(msg);
    
    // Check if the post is just an image, if enabled
    if (IGNORE_IMAGE_POSTS) {
      final List<IEmbed> embeds = msg.getEmbeds();
      if (embeds.size() == 1) {
        
        // Get the image, and if it's not null, it's an image post.
        final IEmbedImage img = embeds.get(0).getImage();
        if (img != null) {
          return null;
        }
      }
    }
    
    // Check message triggers
    if (AntiSpam.checkMessageRate(msg)) return SpamTrigger.MESSAGE_RATE;
    if (AntiSpam.checkRepeatMessages(msg)) return SpamTrigger.REPEAT_MESSAGES;
    
    // If nothing triggered, return null
    return null;
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
      
      // If the last infraction was for repeat message abuse, check if the cooldown has passed
      final InfractionEntry entry = infrHistory.get(0);
      if ((entry.getReason().equals(REASON_REPEAT_MSG_ABUSE) && (msgTime - entry.getTimestamp() < WARN_COOLDOWN_SEC))) {
        return false;
      }
      
    }
    
    // Iterate through their history
    final ArrayList<MessageTrackerEntry> messageHistory = trackerEntries.get(uid);
    
    // Check if there are enough messages to check the rate thresholds
    final int historySize = messageHistory.size();
    if (historySize >= TRIGGER_DUPLICATE_MSG_COUNT) {
      
      // Count the number of duplicate messages
      int count = 0;
      String currMsg = msg.getContent();
      for (int i = 0; i < TRIGGER_DUPLICATE_MSG_COUNT; i++) {
        
        final MessageTrackerEntry entry = messageHistory.get(i);
        final String entryMsg = entry.getMessage();
        
        // If we encounter a message that isn't a duplicate, stop searching and return false
        if (!entryMsg.equals(currMsg)) {
          return false;
        }
        
        currMsg = entryMsg;
        count++;
        
      }
      
      // Trigger if the count meets the threshold
      if (count >= TRIGGER_DUPLICATE_MSG_COUNT) {
        messageHistory.clear();
        return true;
      }
      
    }
    
    // Protection did not trigger
    return false;
    
  }
  
}
