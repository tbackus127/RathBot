
package com.rath.rathbot;

import java.util.List;

import com.rath.rathbot.cmd.PermissionsTable;
import com.rath.rathbot.disc.Infractions;
import com.rath.rathbot.log.MessageLogger;
import com.rath.rathbot.msg.AntiSpam;
import com.rath.rathbot.msg.SpamTrigger;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IEmbed.IEmbedImage;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * This class handles bot events, such as reading messages and responsing to them.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class EventHandler {
  
  /** The prefix for all commands. */
  private static final String COMMAND_PREFIX = "rb!";
  
  /** A reference to the bot. */
  private final RathBot bot;
  
  /**
   * Default constructor.
   * 
   * @param bot a reference to an already-built RathBot.
   */
  public EventHandler(final RathBot bot) {
    this.bot = bot;
  }
  
  /**
   * Message received handler.
   * 
   * @param event contains event details.
   */
  @EventSubscriber
  public void onMessageReceived(MessageReceivedEvent event) {
    
    // Let's not respond to bots
    final IUser author = event.getAuthor();
    if (author.isBot()) {
      return;
    }
    
    // Unpack and log message
    final IMessage message = event.getMessage();
    MessageLogger.logMessage(message);
    
    // If the user isn't in the infractions table, initialize them
    final long uid = author.getLongID();
    if (!Infractions.hasMember(uid)) {
      Infractions.initMember(uid);
    }
    
    // If the author is an owner, bypass anti-spam measures
    if (PermissionsTable.getLevel(uid) < AntiSpam.PERM_LVL_IGNORE) {
      
      // Immediately delete muted users' messages
      if (Infractions.isMuted(uid)) {
        
        // TODO: If the mute time is over, unmute and let the user post again
        // TODO: Else message.delete();
        // Use AntiSpam.MUTE_DURATIONS[Infractions.getMuteCount(uid) - 1];
        
        message.delete();
        return;
      }
      
      // Spam filtering
      final IChannel channel = event.getChannel();
      final SpamTrigger trig = checkSpamType(message);
      if (trig != null) {
        switch (trig) {
          
          case MESSAGE_RATE:
            
            // TODO: Actually do stuff
            this.bot.sendMessage(channel, "Message rate abuse triggered for " + message.getAuthor().getName()
                + ". This doesn't do anything now, but will in the future.");
            
          break;
          case REPEAT_MESSAGES:
            
            // TODO: Actually do stuff
            this.bot.sendMessage(channel, "Repeat message abuse triggered for " + message.getAuthor().getName()
                + ". This doesn't do anything now, but will in the future.");
            
          break;
          default:
        }
        return;
      }
    }
    
    // Parse commands if it starts with the command prefix
    final String messageString = message.getContent();
    if (messageString.startsWith(COMMAND_PREFIX)) {
      
      // TODO: Spin up a new thread here so we can accept new commands while we process
      CommandParser.parseCommand(this.bot, event.getChannel(), event.getAuthor(), messageString);
    }
    
  }
  
  /**
   * Tests if a message triggers an anti-spam mechanism.
   * 
   * @param msg the IMessage event.
   * @return true if spam; false if not.
   */
  private final SpamTrigger checkSpamType(final IMessage msg) {
    
    AntiSpam.addEntry(msg);
    
    // Check if the post is just an image
    final List<IEmbed> embeds = msg.getEmbeds();
    if (embeds.size() == 1) {
      
      // Get the image, and if it's not null, it's an image post.
      final IEmbedImage img = embeds.get(0).getImage();
      if (img != null) {
        return null;
      }
    }
    
    // Check message triggers
    if (AntiSpam.checkMessageRate(msg)) return SpamTrigger.MESSAGE_RATE;
    if (AntiSpam.checkRepeatMessages(msg)) return SpamTrigger.REPEAT_MESSAGES;
    
    // If nothing triggered, return null
    return null;
  }
  
  /**
   * Prints message details.
   * 
   * @param evt contains event details.
   */
  @SuppressWarnings("unused")
  private final void logMessageDetailsToConsole(final MessageReceivedEvent evt) {
    final String author = evt.getAuthor().getName();
    final String channel = evt.getChannel().getName();
    final String message = evt.getMessage().getContent();
    System.out.println("Received message from " + author + " in #" + channel + ":");
    System.out.println("\"" + message + "\"");
  }
}
