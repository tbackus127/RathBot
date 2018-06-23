
package com.rath.rathbot.task.util.remindme;

import java.time.Instant;
import java.util.TreeMap;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.task.RBTask;
import com.rath.rathbot.task.time.RelativeTimeConfiguration;
import com.rath.rathbot.task.time.TimeConfiguration;

/**
 * This task will direct message the command issuer a message of their choice on a specific date, or in a specific
 * period of time.
 * 
 * @author Tim Backus tbackus127@gmail.com
 */
public class RemindmeTask extends RBTask {
  
  /** The name of the task for registration. */
  private static final String TASK_NAME = "remindme";
  
  private static final TimeConfiguration TIME_CONFIG = new RelativeTimeConfiguration("every 1m");
  
  /**
   * Default constructor.
   */
  public RemindmeTask() {
    super(TASK_NAME, TIME_CONFIG);
  }
  
  @Override
  public void performTask() {
    
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
  
  @Override
  public boolean isResourceIntensive() {
    return false;
  }
  
}
