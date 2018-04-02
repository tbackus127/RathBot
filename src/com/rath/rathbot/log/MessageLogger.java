
package com.rath.rathbot.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.TreeMap;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

/**
 * This class handles logging message history.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class MessageLogger {
  
  /** The directory name that will contain the logs. */
  private static final String PATH_LOGS = "logs/";
  
  /** The prefix for all log files. Channel names will be between these. */
  private static final String LOG_PREFIX = "log_";
  
  /** The suffix for all log files. */
  private static final String LOG_SUFFIX = ".txt";
  
  /**
   * The channel label for private message logging. Discord channels cannot contain spaces, so it should be impossible
   * to accidentally log here.
   */
  private static final String PM_LOG_FILEPATH = PATH_LOGS + "_PM History" + LOG_SUFFIX;
  
  /** Maps a channel to its PrintStream. */
  private static HashMap<IChannel, PrintStream> printStreamMap = null;
  
  /** The direct message PrintStream. */
  private static PrintStream pmStream = null;
  
  /** If the logger is initialized yet. */
  private static boolean isLoggerReady = false;
  
  /**
   * Populates the PrintStream map.
   * 
   * @param channelMap a HashMap from IChannels to their respective PrintStreams.
   */
  public static final void initPrintStreamMap(final TreeMap<String, IChannel> channelMap) {
    
    final HashMap<IChannel, PrintStream> result = new HashMap<IChannel, PrintStream>();
    
    // For every entry in the channel map
    for (final String s : channelMap.keySet()) {
      
      // Build log file path
      final String filePath = PATH_LOGS + LOG_PREFIX + s + LOG_SUFFIX;
      try {
        
        // Create a new PrintStream with its correct channel name and map it from its IChannel
        final File file = new File(filePath);
        if (!file.exists()) {
          System.out.println("Creating new file " + filePath);
          file.createNewFile();
        }
        
        // Check if the log file is unwritable
        if (!file.canWrite()) {
          System.err.println("Cannot write to file!");
          return;
        }
        
        final PrintStream ps = new PrintStream(new FileOutputStream(file, true));
        result.put(channelMap.get(s), ps);
        
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    printStreamMap = result;
    
    // Set up the PM logging PrintStream
    final File pmLog = new File(PM_LOG_FILEPATH);
    try {
      pmLog.createNewFile();
      pmStream = new PrintStream(pmLog);
    } catch (IOException e) {
      System.err.println("Error initializing PM log file! Disabling logging.");
      return;
    }
    
    isLoggerReady = true;
  }
  
  /**
   * Logs a message to the correct log file.
   * 
   * @param msg the IMessage event caught by the EventHandler.
   */
  public static final void logMessage(final IMessage msg) {
    
    // Do not log if it's not ready
    if (!isLoggerReady) {
      return;
    }
    
    // Unpack IMessage object
    final IChannel channel = msg.getChannel();
    final String author = msg.getAuthor().getName();
    final String messageString = msg.getContent();
    final String timestamp = msg.getTimestamp().toString();
    
    // Build and append the string to the log file
    PrintStream ps = printStreamMap.get(channel);
    final String logString = author + " @ " + timestamp + ": " + messageString;
    
    // If it was a direct message, log it with the PM logger
    if (ps == null) {
      ps = pmStream;
    }
    
    ps.println(logString);
    
  }
  
  /**
   * Closes up the open PrintStreams.
   */
  public static final void closeStreams() {
    
    // Check if the map is null
    if (printStreamMap == null) {
      System.err.println("PrintStream map is null.");
      return;
    }
    
    System.out.print("Closing PrintStreams... ");
    for (final IChannel ch : printStreamMap.keySet()) {
      printStreamMap.get(ch).close();
    }
    System.out.println("DONE");
  }
}
