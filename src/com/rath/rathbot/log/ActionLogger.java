
package com.rath.rathbot.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.action.RBAction;

/**
 * This class will log actions taken by the bot.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 */
public class ActionLogger {
  
  /** The file name of the action log. */
  private static final String ACTION_LOG_FILENAME = "actions.txt";
  
  /** The relative path of the action log from the bot's root directory. */
  private static final String ACTION_LOG_PATH = RathBot.DIR_LOGS + ACTION_LOG_FILENAME;
  
  /** The file handle of the action log. */
  private static final File ACTION_LOG_FILE = new File(ACTION_LOG_PATH);
  
  private static PrintStream logWriter = null;
  
  /**
   * Initializes the action logger. Must be called before commands are received!
   */
  public static final void initActionLogger() {
    
    if (!ACTION_LOG_FILE.exists()) {
      System.out.println("Action log file \"" + ACTION_LOG_FILE.getAbsolutePath() + "\" does not exist. Creating.");
      try {
        ACTION_LOG_FILE.createNewFile();
      } catch (IOException e) {
        System.err.println("Failed to create new action logger file.");
        e.printStackTrace();
        return;
      }
    }
    
    if (!ACTION_LOG_FILE.canWrite()) {
      System.err.println("Can't write to action log file!");
      return;
    }
    
    try {
      logWriter = new PrintStream(new FileOutputStream(ACTION_LOG_FILE, true));
    } catch (@SuppressWarnings("unused") FileNotFoundException e) {
      System.err.println("Error initializing action logger!");
    }
  }
  
  /**
   * Closes the PrintStream open on the log file. This should be called during a graceful shutdown.
   */
  // TODO: Implement graceful shutdown for RathBot
  public static final void closePrintStream() {
    logWriter.close();
  }
  
  /**
   * Appends the action to the action log.
   * 
   * @param action a child of the RBAction class.
   */
  public static final void logAction(final RBAction action) {
    
    // Check against the logger being null
    if (logWriter == null) {
      System.err.println("Action logger is null! Was initActionLogger() called?");
    } else {
      logWriter.println(action.getActionMessage());
    }
    
  }
  
}
