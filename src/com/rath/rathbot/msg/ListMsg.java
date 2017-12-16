
package com.rath.rathbot.msg;

import java.util.List;
import java.util.Map;

public class ListMsg {
  
  public static final String buildListMsg(final String header, final List<String> contents) {
    String result = header + "\n";
    for (final String s : contents) {
      result += s + "\n";
    }
    return result;
  }
  
  public static final String buildMapMsg(final String header, final Map<String, String> contents) {
    String result = header + "\n";
    for (final String s : contents.keySet()) {
      result += s + " - " + contents.get(s) + "\n";
    }
    return result;
  }
}
