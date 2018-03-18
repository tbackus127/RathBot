
package com.rath.rathbot;

import com.rath.rathbot.data.MessageLogger;
import com.rath.rathbot.msg.AntiSpam;
import com.rath.rathbot.msg.SpamTrigger;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

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
    
    final IMessage message = event.getMessage();
    final IChannel channel = event.getChannel();
    MessageLogger.logMessage(message);
    
    // Let's not respond to bots
    if (event.getAuthor().isBot()) {
      return;
    }
    
    // Spam filtering
    final SpamTrigger trig = checkSpamType(message);
    if (trig != null) {
      switch (trig) {
        
        case MESSAGE_RATE:
          
          // TODO: Actually do stuff
          this.bot.sendMessage(channel, "Message rate abuse triggered for " + message.getAuthor().getName()
              + ". This doesn't do anything now, but will in the future.");
          
        break;
        case REPEAT_MESSAGES:
        
        // TODO: Repeat messages warning and punishment
        
        break;
        case JUNK_MESSAGES:
        
        // TODO: Junk messages warning and punishment
        
        break;
        default:
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
    if (AntiSpam.checkMessageRate(msg)) return SpamTrigger.MESSAGE_RATE;
    if (AntiSpam.checkRepeatMessages(msg)) return SpamTrigger.REPEAT_MESSAGES;
    if (AntiSpam.checkJunkMessages(msg)) return SpamTrigger.JUNK_MESSAGES;
    
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
