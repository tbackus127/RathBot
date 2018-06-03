
package com.rath.rathbot.task.util.remindme;

import java.time.Instant;
import java.util.TreeMap;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.task.RBTask;

/**
 * This task will direct message the command issuer a message of their choice on a specific date, or in a specific
 * period of time.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class RemindmeTask extends RBTask {
  
  /** The name of the task for registration. */
  private static final String TASK_NAME = "remindme";
  
  /** The frequency that reminders will be checked (default: 60s (1min)). */
  private static final long TASK_FREQ = 60;
  
  /**
   * Default constructor.
   */
  public RemindmeTask() {
    super(TASK_NAME, TASK_FREQ, 0, false);
  }
  
  @Override
  public void run() {
    
    // Iterate through the reminder table's users
    for (final Long uid : Reminders.getReminderTable().keySet()) {
      
      // Iterate through the user's reminders
      userLoop: {
        final TreeMap<String, ReminderEntry> userTable = Reminders.getUserTable(uid);
        for (final String label : userTable.keySet()) {
          
          // If this entry passed the current time, send the reminder message
          final ReminderEntry entry = userTable.get(label);
          if (entry.getRemEpochSecond() > Instant.now().getEpochSecond()) {
            
            // Send them the reminder and remove the entry
            RathBot.sendDirectMessage(entry.getAuthor(), entry.getMessage());
            Reminders.remove(uid, label);
            
          } else {
            
            // Otherwise, stop for this user because reminders are sorted by time
            break userLoop;
          }
        }
      }
      
    }
    
  }
  
}
