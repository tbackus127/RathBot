
package com.rath.rathbot;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import com.rath.rathbot.cmd.HelpCmd;
import com.rath.rathbot.cmd.PermissionsTable;
import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.cmd.admin.UIDCmd;
import com.rath.rathbot.cmd.faq.FAQCmd;
import com.rath.rathbot.cmd.userpunishments.MuteCmd;
import com.rath.rathbot.cmd.userpunishments.UnmuteCmd;
import com.rath.rathbot.disc.Infractions;
import com.rath.rathbot.log.MessageLogger;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;

/**
 * Main class for RathBot.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class RathBot {
  
  /** The name of the data directory. */
  public static final String DIR_DATA = "dat/";
  
  /** Relative path to the bot's config file containing the authentication key. */
  private static final String CONFIG_FILE_PATH = "rathbot.conf";
  
  /** The default Playing text under the bot's username. */
  private static final String DEFAULT_PLAYING_TEXT = "\u2606I\u2606MA\u2606SU\u2606GU\u2606";
  
  // TODO: Add more here as they become available.
  /** A list of commands to initialize. */
  private static final RBCommand[] commandList = { new FAQCmd(), new UIDCmd(), new MuteCmd(), new UnmuteCmd() };
  
  /** Reference to the client. */
  private static IDiscordClient client = null;
  
  /** A map from channel name to ID. */
  private static TreeMap<String, IChannel> channelMap;
  
  /** The set of commands this bot responds to. */
  private final TreeMap<String, RBCommand> commandMap;
  
  /**
   * Default constructor.
   * 
   * @param client an instance of the IDiscordClient object, already built.
   */
  public RathBot(final IDiscordClient client) {
    
    // Log in and build the channel map
    RathBot.client = client;
    RathBot.channelMap = buildChannelMap(client);
    
    // Load and initialize everything
    // TODO: Add more stuff here
    MessageLogger.initPrintStreamMap(channelMap);
    PermissionsTable.loadPerms();
    Infractions.loadFromFile();
    
    // Build the command set and initialize the commands
    this.commandMap = new TreeMap<String, RBCommand>();
    for (int i = 0; i < commandList.length; i++) {
      addAndInitializeCommand(commandList[i]);
    }
    
    // Build the help command (other commands must have been built first!)
    addAndInitializeCommand(buildHelpCommand());
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
   * Sends a private message to the specified user.
   * 
   * @param user the user to send the PM to.
   * @param msg the message contents.
   */
  public final void sendDirectMessage(final IUser user, final String msg) {
    
    user.getOrCreatePMChannel().sendMessage(msg);
    
  }
  
  /**
   * Sets the bot's Now Playing message.
   * 
   * @param status the status to set the bot's NP to.
   */
  public final void setPlaying(final String status) {
    
    client.changePresence(StatusType.ONLINE, ActivityType.PLAYING, status);
    
  }
  
  /**
   * Gets the commands that are registered for the bot.
   * 
   * @return a Set of RBCommands.
   */
  public TreeMap<String, RBCommand> getCommandMap() {
    
    return this.commandMap;
  }
  
  /**
   * Has the bot log in to the server.
   */
  public final void login() {
    client.login();
  }
  
  /**
   * Has the bot log out from the server.
   */
  public final void logout() {
    
    System.out.println("Logging out...");
    client.logout();
    MessageLogger.closeStreams();
  }
  
  /**
   * Gets the reference to the Discord client.
   * 
   * @return the reference to the Discord client.
   */
  public static final IDiscordClient getClient() {
    return client;
  }
  
  /**
   * Gets the reference to the channel map.
   * 
   * @return a TreeMap from String -> IChannel.
   */
  public static final TreeMap<String, IChannel> getChannelMap() {
    return channelMap;
  }
  
  /**
   * Builds a map of Channel Name -> Channel ID.
   * 
   * @param client the logged-in client instance.
   * @return a HashMap of type String -> Long.
   */
  private final TreeMap<String, IChannel> buildChannelMap(final IDiscordClient client) {
    
    // Log in and wait until ready to receive commands
    System.out.println("Logging in... ");
    login();
    while (!client.isReady()) {}
    System.out.println("Successfully logged in.");
    
    // Change the playing text to the default
    client.changePresence(StatusType.ONLINE, ActivityType.PLAYING, DEFAULT_PLAYING_TEXT);
    
    // Initialize channel structures
    System.out.println("Building channel map...");
    final List<IChannel> channels = client.getChannels();
    final TreeMap<String, IChannel> result = new TreeMap<String, IChannel>();
    
    // For each channel, add a mapping from its name to its ID
    for (final IChannel c : channels) {
      final String name = c.getName();
      System.out.println("  Added channel #" + name + " -> " + c.getLongID() + ".");
      result.put(name, c);
    }
    System.out.println("Channel map successfully built.");
    return result;
  }
  
  /**
   * Builds the help command's entries after all other commands have been registered.
   * 
   * @return the built HelpCmd.
   */
  private final HelpCmd buildHelpCommand() {
    
    final HelpCmd result = new HelpCmd();
    for (String cmdName : commandMap.keySet()) {
      result.addCommandEntry(cmdName, commandMap.get(cmdName));
    }
    return result;
  }
  
  /**
   * Adds a command to the bot's list of available commands.
   * 
   * @param cmd the RBCommand to add.
   */
  private void addAndInitializeCommand(final RBCommand cmd) {
    System.out.print("Initializing command " + cmd.getCommandName() + "... ");
    cmd.setupCommand();
    this.commandMap.put(cmd.getCommandName(), cmd);
    System.out.println("DONE");
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
   * Reads the Discord authentication token from the file.
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
    System.out.print("Creating client... ");
    final IDiscordClient client = new ClientBuilder().withPingTimeout(5).withToken(token).build();
    final RathBot bot = new RathBot(client);
    client.getDispatcher().registerListener(new EventHandler(bot));
    System.out.println("Startup complete!");
    
    // Get commands from terminal
    Scanner cin = new Scanner(System.in);
    ConsoleHandler.getConsoleCommands(bot, cin);
    
    // Clean everything up
    cin.close();
    if (RathBot.client.isLoggedIn()) {
      bot.logout();
    }
  }
  
}
