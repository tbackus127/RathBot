
package com.rath.rathbot.cmd.faq;

import java.util.Set;

import com.rath.rathbot.RathBot;
import com.rath.rathbot.cmd.RBCommand;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class FAQEditCmd extends RBCommand {
  
  protected FAQEditCmd(String name, String descr) {
    super(name, true, descr);
    // TODO Auto-generated constructor stub
  }
  
  @Override
  public Set<RBCommand> getSubcommands() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public void executeCommand(RathBot rb, IUser author, IChannel channel, String[] tokens) {
    // TODO Auto-generated method stub
    
  }
  
}
