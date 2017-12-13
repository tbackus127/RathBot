
package com.rath.rathbot;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

/*
 * Console commands:
 *   login - Logs the bot in.
 *   logout - Logs the bot out.
 *   say {channel} {message} - Posts $message in $channel.
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
public class RathBot {
  
  /** Relative path to the bot's config file containing the authentication key. */
  private static final String CONFIG_FILE_PATH = "rathbot.conf";
  
  /** Reference to the client. */
  private final IDiscordClient client;
  
  private final HashMap<String, Long> channelMap;
  
  private final HashMap<String, String> faqMap;
  
  /** osu! player statistics map. */
  private final HashMap<String, PlayerStats> playerMap;
  
  /**
   * Default constructor.
   * 
   * @param client an instance of the IDiscordClient object, already built.
   */
  public RathBot(final IDiscordClient client) {
    this.client = client;
    this.channelMap = buildChannelMap(client);
    this.playerMap = new HashMap<String, PlayerStats>();
    this.faqMap = new HashMap<String, String>();
  }
  
  /**
   * Builds a map of Channel Name -> Channel ID.
   * 
   * @param client the logged-in client instance.
   * @return a HashMap of type String -> Long.
   */
  private final HashMap<String, Long> buildChannelMap(final IDiscordClient client) {
    
    // Log in and wait until ready to receive commands
    client.login();
    while (!client.isReady()) {}
    client.changePlayingText("\u2606I\u2606MA\u2606SU\u2606GU\u2606");
    final List<IChannel> channels = client.getChannels();
    final HashMap<String, Long> result = new HashMap<String, Long>();
    for (IChannel c : channels) {
      final String name = c.getName();
      final long id = c.getLongID();
      System.out.println("Added channel #" + name + " -> " + id + ".");
      result.put(name, id);
    }
    return result;
  }
  
  /**
   * Sends a plain text message in the specified channel.
   * 
   * @param channel the channel to send the message on.
   * @param msg the message as a String.
   */
  public final void sendMessage(final IChannel channel, final String msg) {
    channel.sendMessage(msg);
  }
  
  /**
   * Sets the bot's Now Playing message.
   * 
   * @param status the status to set the bot's NP to.
   */
  public final void setPlaying(final String status) {
    client.changePlayingText(status);
  }
  
  /**
   * Posts the list of FAQs that are currently set.
   * 
   * @param channel the channel to send the list to.
   */
  public void postFaqList(final IChannel channel) {
    
    if (this.faqMap.size() <= 0) {
      sendMessage(channel, "No FAQs available!");
      return;
    }
    
    String message = "FAQ List:\n";
    for (final String s : this.faqMap.keySet()) {
      message += ("  " + s + "\n");
    }
    sendMessage(channel, message);
  }
  
  /**
   * Tests if the FAQ map has a specific ID registered.
   * 
   * @param faq the FAQ ID.
   * @return true if the map's key set contains the ID; false if not.
   */
  public boolean hasFaq(final String faq) {
    return this.faqMap.containsKey(faq);
  }
  
  /**
   * Sends a FAQ's contents to the specific channel.
   * 
   * @param faq
   * @param channel
   */
  public void postFaq(final String faq, final IChannel channel) {
    sendMessage(channel, this.faqMap.get(faq));
  }
  
  /**
   * Adds or replaces a FAQ entry.
   * 
   * @param faqName the FAQ ID.
   * @param message the FAQ's contents.
   */
  public void addFaq(final String faqName, final String message) {
    System.out.println("Adding FAQ: \"" + faqName + "\" -> \"" + message + "\".");
    this.faqMap.put(faqName, message);
  }
  
  /**
   * Removes a FAQ entry.
   * 
   * @param faqName the FAQ ID.
   */
  public void removeFaq(final String faqName) {
    System.out.println("Removing FAQ: \"" + faqName + "\".");
    this.faqMap.remove(faqName);
  }
  
  /**
   * Has the bot log in to the server.
   */
  public final void login() {
    System.out.println("Logging in...");
    client.login();
  }
  
  /**
   * Has the bot log out from the server.
   */
  public final void logout() {
    System.out.println("Logging out...");
    client.logout();
  }
  
  /**
   * Main method.
   * 
   * @param args runtime arguments (ignored).
   */
  public static final void main(String[] args) {
    
    // Get the authentication token
    final String token = readToken(CONFIG_FILE_PATH);
    System.out.println("Creating bot with token " + token + ".");
    if (token == null) {
      System.err.println("Fetching the authentication token went wrong. Exiting.");
      return;
    }
    
    // Create the client and the bot
    System.out.println("Creating client...");
    final IDiscordClient client = new ClientBuilder().withPingTimeout(5).withToken(token).build();
    final RathBot bot = new RathBot(client);
    client.getDispatcher().registerListener(new EventHandler(bot));
    
    // Log in and create the bot
    System.out.println("Logging in...");
    
    // Get commands from terminal
    Scanner cin = new Scanner(System.in);
    getCommands(bot, cin);
    
    // Clean everything up
    cin.close();
    if (bot.client.isLoggedIn()) {
      bot.logout();
    }
  }
  
  /**
   * Starts the command line interpreter.
   * 
   * @param bot reference to the RathBot object.
   * @param cin reference to System.in.
   */
  private static final void getCommands(final RathBot bot, final Scanner cin) {
    
    // Command interface
    while (cin.hasNextLine()) {
      
      // Split command into tokens
      final String line = cin.nextLine();
      final String[] tokens = line.split("\\s+");
      if (tokens.length < 1) continue;
      switch (tokens[0]) {
        
        // Logout
        case "logout":
          System.out.println("Logging out...");
          bot.logout();
          
        break;
        // Change Now Playing status
        case "np":
          if (tokens.length >= 2) {
            String npMessage = tokens[1];
            for (int i = 2; i < tokens.length; i++) {
              npMessage += " " + tokens[i];
            }
            bot.setPlaying(npMessage);
          }
        break;
      
        // Send a message to a specific channel
        case "say":
          if (tokens.length >= 3) {
            final String channel = tokens[1];
            String npMessage = tokens[2];
            for (int i = 3; i < tokens.length; i++) {
              npMessage += " " + tokens[i];
            }
            bot.sendMessage(bot.client.getChannelByID(bot.channelMap.get(channel)), npMessage);
          }
        break;
        default:
          System.out.println("Command not recognized.");
      }
    }
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
      return confScan.nextLine().split(":")[1];
    } else {
      System.err.println("Scanner couldn't get token from config file.");
      return null;
    }
  }
  
}
