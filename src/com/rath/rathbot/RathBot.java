
package com.rath.rathbot;

import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import com.rath.rathbot.cmd.PermissionsTable;
import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.cmd.admin.UIDCmd;
import com.rath.rathbot.cmd.disc.actions.MuteCmd;
import com.rath.rathbot.cmd.disc.actions.UnmuteCmd;
import com.rath.rathbot.cmd.msg.HelpCmd;
import com.rath.rathbot.cmd.msg.faq.FAQCmd;
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
  
  /** The set of commands this bot responds to. */
  private static final TreeMap<String, RBCommand> commandMap = new TreeMap<String, RBCommand>();
  
  /** Reference to the client. */
  private static IDiscordClient discClient = null;
  
  /** A map from channel name to ID. */
  private static TreeMap<String, IChannel> channelMap;
  
  /**
   * Sends a plain text message in the specified channel.
   * 
   * @param channel the channel to send the message on.
   * @param msg the message as a String.
   */
  public static final void sendMessage(final IChannel channel, final String msg) {
    
    channel.sendMessage(msg);
  }
  
  /**
   * Sends a private message to the specified user.
   * 
   * @param user the user to send the PM to.
   * @param msg the message contents.
   */
  public static final void sendDirectMessage(final IUser user, final String msg) {
    
    user.getOrCreatePMChannel().sendMessage(msg);
    
  }
  
  /**
   * Sets the bot's Now Playing message.
   * 
   * @param status the status to set the bot's NP to.
   */
  public static final void setPlaying(final String status) {
    
    discClient.changePresence(StatusType.ONLINE, ActivityType.PLAYING, status);
    
  }
  
  /**
   * Gets the commands that are registered for the bot.
   * 
   * @return a Set of RBCommands.
   */
  public static final TreeMap<String, RBCommand> getCommandMap() {
    
    return commandMap;
  }
  
  /**
   * Has the bot log in to the server.
   */
  public static final void login() {
    discClient.login();
  }
  
  /**
   * Has the bot log out from the server.
   */
  public static final void logout() {
    
    System.out.println("Logging out...");
    discClient.logout();
    MessageLogger.closeStreams();
  }
  
  /**
   * Gets the reference to the Discord client.
   * 
   * @return the reference to the Discord client.
   */
  public static final IDiscordClient getClient() {
    return discClient;
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
   * Creates the Discord client and loads the bot's data structures.
   * 
   * @return the built IDiscordClient object.
   */
  private static final IDiscordClient createClient() {
    
    // Get the authentication token
    final String token = RBAuth.readToken(CONFIG_FILE_PATH);
    System.out.println("Creating bot with token " + token + ".");
    if (token == null) {
      System.err.println("Fetching the authentication token went wrong. Exiting.");
      return null;
    }
    
    // Create the client and the bot
    System.out.print("Creating client... ");
    final IDiscordClient client = new ClientBuilder().withPingTimeout(5).withToken(token).build();
    return client;
  }
  
  /**
   * Builds and loads the various data structures the bot needs.
   * 
   * @param client the Discord client used for pretty much everything.
   */
  private static final void buildAndLoadDataStructures(final IDiscordClient client) {
    
    // Log in and build the channel map
    discClient = client;
    channelMap = buildChannelMap(client);
    
    // Load and initialize everything
    MessageLogger.initPrintStreamMap(channelMap);
    PermissionsTable.loadPerms();
    Infractions.loadFromFile();
    // TODO: Add more tables here when needed
  }
  
  /**
   * Builds a map of Channel Name -> Channel ID.
   * 
   * @param client the logged-in client instance.
   * @return a HashMap of type String -> Long.
   */
  private static final TreeMap<String, IChannel> buildChannelMap(final IDiscordClient client) {
    
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
   * Builds the command map from the command list and initializes each command.
   */
  private static final void buildCommands() {
    
    // Build the command set and initialize the commands
    for (int i = 0; i < commandList.length; i++) {
      addAndInitializeCommand(commandList[i]);
    }
    
    // Build the help command (other commands must have been built first!)
    addAndInitializeCommand(buildHelpCommand());
  }
  
  /**
   * Builds the help command's entries after all other commands have been registered.
   * 
   * @return the built HelpCmd.
   */
  private static final HelpCmd buildHelpCommand() {
    
    // Iterate through every command in the command map
    final HelpCmd result = new HelpCmd();
    for (final String cmdName : commandMap.keySet()) {
      result.addCommandEntry(cmdName, commandMap.get(cmdName));
    }
    return result;
  }
  
  /**
   * Adds a command to the bot's list of available commands.
   * 
   * @param cmd the RBCommand to add.
   */
  private final static void addAndInitializeCommand(final RBCommand cmd) {
    System.out.print("Initializing command " + cmd.getCommandName() + "... ");
    cmd.setupCommand();
    commandMap.put(cmd.getCommandName(), cmd);
    System.out.println("DONE");
  }
  
  /**
   * Main method.
   * 
   * @param args runtime arguments (ignored).
   */
  public static final void main(String[] args) {
    
    // Start the bot up
    final IDiscordClient client = createClient();
    buildAndLoadDataStructures(client);
    buildCommands();
    client.getDispatcher().registerListener(new EventHandler());
    System.out.println("Startup complete!");
    
    // Start accepting commands from the console window
    final Scanner cin = new Scanner(System.in);
    ConsoleHandler.getConsoleCommands(cin);
    
    // Clean everything up
    cin.close();
    if (discClient.isLoggedIn()) {
      logout();
    }
  }
  
}
