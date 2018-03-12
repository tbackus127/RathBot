
package com.rath.rathbot.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.TreeMap;

/**
 * This class handles serialization and deserialization of data tables for this bot.
 * 
 * @author Tim Backus tbackus127@gmail.com
 *
 * @param <T> the type of data structure to save/load.
 */
public class DataLoader<T> {
  
  /** Reference to the data structure itself. */
  private final T obj;
  
  /** The path to save/load to/from. */
  private final String path;
  
  /** The Class object of the data structure's class for calling newInstance(). */
  private final Class<T> cls;
  
  /**
   * Default constructor. Construct like so: "new DataLoader&lt;ClassName&gt;(ClassName.class, refToDatStruct,
   * filePath)".
   * 
   * @param c the Class object of the data structure.
   * @param obj the reference to the data structure to save/load.
   * @param path the file path to read/write as a String.
   */
  @SuppressWarnings("unchecked")
  public DataLoader(final Class<?> c, final T obj, final String path) {
    this.obj = obj;
    this.path = path;
    
    // TODO: Check this cast
    this.cls = (Class<T>) c;
  }
  
  /**
   * Saves the data structure to the disk.
   */
  public final void saveToDisk() {
    
    FileOutputStream fos = null;
    ObjectOutputStream oos = null;
    
    try {
      
      // Build an output stream for serialization
      fos = new FileOutputStream(this.path);
      oos = new ObjectOutputStream(fos);
      
      // Write the map and close streams
      oos.writeObject(this.obj);
      oos.close();
      fos.close();
      
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Loads the data structure from the disk.
   * 
   * @return the data structure loaded from the disk. null will be returned if anything goes wrong.
   */
  @SuppressWarnings("unchecked")
  public final T loadFromDisk() {
    
    boolean err = false;
    
    // Initialize data streams
    FileInputStream fis = null;
    ObjectInputStream oin = null;
    T result = null;
    Object obj = null;
    
    // Create the file if it doesn't exist.
    final File file = new File(path);
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
        err = true;
      }
    }
    
    try {
      
      // Build an input stream for deserialization
      fis = new FileInputStream(path);
      if (fis.available() > 0) {
        oin = new ObjectInputStream(fis);
        
        // Read the object in and close the streams
        obj = oin.readObject();
        oin.close();
        fis.close();
        
      } else {
        System.err.println("Data structure is empty.");
      }
      
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      err = true;
    } catch (IOException e) {
      e.printStackTrace();
      err = true;
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      err = true;
    }
    
    // If something went wrong, return null
    if (fis == null || oin == null) {
      System.err.println("Data structure load error.");
      err = true;
    }
    
    // Cast the read object to a TreeMap
    if (obj instanceof TreeMap && !err) {
      result = (T) obj;
      System.out.println("Read successfully.");
    } else {
      System.err.println("Error with loading. Creating new table.");
      try {
        return buildClass();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    
    return result;
  }
  
  /**
   * Creates a new instance of the generic's class. Workaround for the inability to do "new T()"
   * 
   * @return new T();
   * @throws InstantiationException if there's an error instantiating the generic class.
   * @throws IllegalAccessException if there's an error with accessing memory.
   */
  private final T buildClass() throws InstantiationException, IllegalAccessException {
    return this.cls.newInstance();
  }
  
}
