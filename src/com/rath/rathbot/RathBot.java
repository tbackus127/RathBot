
package com.rath.rathbot;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.rath.rathbot.cmd.HelpCmd;
import com.rath.rathbot.cmd.RBCommand;
import com.rath.rathbot.cmd.admin.ReportCmd;
import com.rath.rathbot.cmd.faq.FAQCmd;
import com.rath.rathbot.data.MessageLogger;
import com.rath.rathbot.data.PermissionsTable;

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
  
  /** A map from channel name to ID. */
  public static HashMap<String, IChannel> channelMap;
  
  /** Reference to the client. */
  private static IDiscordClient client = null;
  
  /** The set of commands this bot responds to. */
  private final Set<RBCommand> commandSet;
  
  /** A list of commands to initialize. */
  private static final RBCommand[] commandList = { new FAQCmd(), new ReportCmd() };
  // TODO: Add more here as they become available.
  
  /**
   * Default constructor.
   * 
   * @param client an instance of the IDiscordClient object, already built.
   */
  public RathBot(final IDiscordClient client) {
    
    RathBot.client = client;
    
    // Log in and build the channel map
    RathBot.channelMap = buildChannelMap(client);
    
    // Load and initialize everything
    MessageLogger.initPrintStreamMap(channelMap);
    PermissionsTable.loadPerms();
    
    // Build the command set and initialize the commands
    this.commandSet = new HashSet<RBCommand>();
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
   * Gets the commands that are registered for the bot.
   * 
   * @return a Set of RBCommands.
   */
  public Set<RBCommand> getCommandSet() {
    
    return this.commandSet;
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
   * Builds a map of Channel Name -> Channel ID.
   * 
   * @param client the logged-in client instance.
   * @return a HashMap of type String -> Long.
   */
  private final HashMap<String, IChannel> buildChannelMap(final IDiscordClient client) {
    
    // Log in and wait until ready to receive commands
    System.out.print("Logging in... ");
    login();
    while (!client.isReady()) {}
    System.out.println("DONE");
    
    // Change the playing text to the default
    client.changePresence(StatusType.ONLINE, ActivityType.PLAYING, DEFAULT_PLAYING_TEXT);
    
    // For each channel, add a mapping from its name to its ID
    final List<IChannel> channels = client.getChannels();
    final HashMap<String, IChannel> result = new HashMap<String, IChannel>();
    for (IChannel c : channels) {
      final String name = c.getName();
      System.out.println("Added channel #" + name + " -> " + c.getLongID() + ".");
      result.put(name, c);
    }
    return result;
  }
  
  /**
   * Builds the help command's entries after all other commands have been registered.
   * 
   * @return the built HelpCmd.
   */
  private HelpCmd buildHelpCommand() {
    
    final HelpCmd result = new HelpCmd();
    for (RBCommand cmd : commandSet) {
      result.addCommandEntry(cmd.getCommandName(), cmd);
    }
    return result;
  }
  
  /**
   * Adds a command to the bot's list of available commands.
   * 
   * @param cmd the RBCommand to add.
   */
  private void addAndInitializeCommand(final RBCommand cmd) {
    cmd.setupCommand();
    commandSet.add(cmd);
  }
  
  /**
   * Starts the command line interpreter. Only supported on the local machine this bot is running on.
   * 
   * @param bot reference to the RathBot object.
   * @param cin reference to System.in.
   */
  private static final void getConsoleCommands(final RathBot bot, final Scanner cin) {
    
    // Command interface
    while (cin.hasNextLine()) {
      
      // Split command into tokens
      final String line = cin.nextLine();
      final String[] tokens = line.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
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
            bot.sendMessage(RathBot.channelMap.get(channel), npMessage);
          }
        break;
      
        // Get userIDs for users that match the next argument
        case "uid":
          if (tokens.length == 2) {
            final List<IUser> users = RathBot.client.getUsersByName(tokens[1], true);
            for (final IUser u : users) {
              System.out.println(u.getName() + ": " + u.getLongID());
            }
          }
        break;
      
        // Manually set permissions for commands
        case "perm":
          if (tokens.length == 3) {
            
            long id = -1;
            try {
              id = Long.parseLong(tokens[1]);
            } catch (NumberFormatException nfe) {
              nfe.printStackTrace();
            }
            
            int lvl = -1;
            try {
              lvl = Integer.parseInt(tokens[2]);
            } catch (NumberFormatException nfe) {
              nfe.printStackTrace();
            }
            
            if (id > 0 && lvl > 0) PermissionsTable.updateUser(id, lvl);
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
    System.out.println("DONE");
    
    // Get commands from terminal
    Scanner cin = new Scanner(System.in);
    getConsoleCommands(bot, cin);
    
    // Clean everything up
    cin.close();
    if (RathBot.client.isLoggedIn()) {
      bot.logout();
    }
  }
  
}
