
package com.rath.rathbot;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;

// TODO: Put console on a separate thread, send messages to the bot
/*
 * Console commands:
 *   login - Logs the bot in.
 *   logout - Logs the bot out.
 *   say {channel} {message} - Posts $message in $channel.
 *   shutdown - Logs out and terminates the program.
 *   lst - Lists tracked users.
 *   rmt {user} - Removes a user from the tracking system.
 */

// TODO: Command dispatcher
// TODO: Command parser
// TODO: rb!help
// TODO: rb!faq
// TODO: rb!setplayer
// TODO: rb!poll

/**
 * Main class for RathBot.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class RathBot implements IListener<MessageReceivedEvent> {
  
  /** Relative path to the bot's config file containing the authentication key. */
  private static final String CONFIG_FILE_PATH = "rathbot.conf";
  
  /** Reference to the client. */
  private final IDiscordClient client;
  
  /**
   * Default constructor.
   * 
   * @param client an instance of the IDiscordClient object, already built.
   */
  public RathBot(final IDiscordClient client) {
    this.client = client;
  }
  
  /**
   * Has the bot log in to the server.
   */
  public void login() {
    System.out.println("Logging in...");
    client.login();
  }
  
  /**
   * Performs message handling.
   * 
   * @param evt the event data from a message.
   */
  @Override
  public void handle(MessageReceivedEvent evt) {
    final IMessage msgObj = evt.getMessage();
    final IChannel channelObj = evt.getChannel();
    final long channelID = channelObj.getLongID();
    System.out.println("Message from \"" + msgObj.getAuthor().getName() + "\":");
    System.out.println(msgObj.getContent());
  }
  
  /**
   * Main method.
   * 
   * @param args runtime arguments (ignored).
   */
  public static void main(String[] args) {
    
    // Get the authentication token
    final String token = readToken(CONFIG_FILE_PATH);
    if (token == null) {
      System.err.println("Fetching the authentication token went wrong. Exiting.");
      return;
    }
    
    // Create the client and the bot
    final IDiscordClient client = createClient(token);
    final RathBot bot = new RathBot(client);
  }
  
  /**
   * Handles reading the authentication token from a file.
   * 
   * @param confPath the path to the config file as a String.
   * @return the token itself as a String.
   */
  private static final String readToken(final String confPath) {
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
      fscan.close();
    }
    return null;
  }
  
  /**
   * Reads the token from the file.
   * 
   * @param confScan a Scanner already open on the config file.
   * @return the token as a String.
   */
  private static final String readToken(final Scanner confScan) {
    if (confScan.hasNextLine()) {
      return confScan.nextLine().trim();
    } else {
      System.err.println("Scanner couldn't get token from config file.");
      return null;
    }
  }
  
  /**
   * Creates a Discord client, but does not log it in.
   * 
   * @param authToken the authentication token from the CONFIG_FILE_PATH file.
   * @return an instance of an IDiscordClient object.
   */
  private static final IDiscordClient createClient(final String authToken) {
    
    final ClientBuilder builder = new ClientBuilder();
    builder.withToken(authToken);
    try {
      return builder.build();
    } catch (DiscordException dce) {
      dce.printStackTrace(System.err);
    }
    
    return null;
  }
}
