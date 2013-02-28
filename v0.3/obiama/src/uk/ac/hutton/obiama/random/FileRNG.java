/*
 * uk.ac.hutton.obiama.random: FileRNG.java
 * 
 * Copyright (C) 2013 The James Hutton Institute
 * 
 * This file is part of obiama-0.3.
 * 
 * obiama-0.3 is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * obiama-0.3 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with obiama-0.3. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact information: Gary Polhill, The James Hutton Institute, Craigiebuckler,
 * Aberdeen. AB15 8QH. UK. gary.polhill@hutton.ac.uk
 */
package uk.ac.hutton.obiama.random;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.util.Bug;

/**
 * <!-- FileRNG -->
 * 
 * A random number generator that reads in numbers from a file. This is useful
 * for 'playing back' a simulation done with one of the
 * {@link AbstractRecordedRNG}s. The file is expected to be a binary format
 * file, with no particular pre-amble. You should use one of the constructors
 * providing a <code>length</code> argument if you want to use a file that has
 * nonrandom data (e.g. metadata) before the random data.
 * 
 * @author Gary Polhill
 */
public class FileRNG extends AbstractRNG {
  /**
   * Serialisation number
   */
  private static final long serialVersionUID = 661709126209740679L;

  /**
   * Input stream
   */
  private DataInputStream fp;

  /**
   * File name for input stream
   */
  private String file;

  /**
   * Number of times to retry if we can't get the bytes from the file
   */
  private int retryCount;

  /**
   * How long to sleep between retries
   */
  private int retrySleepMS;

  /**
   * Default number of times to retry if we can't get the bytes from the file
   */
  public static int DEFAULT_RETRY_COUNT = 10;

  /**
   * Default number of milliseconds to sleep between retries
   */
  public static int DEFAULT_RETRY_SLEEP_MS = 100;

  /**
   * @param file
   * @throws FileNotFoundException
   */
  public FileRNG(String file) throws FileNotFoundException {
    super();
    fp = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
    this.file = file;
    retryCount = DEFAULT_RETRY_COUNT;
    retrySleepMS = DEFAULT_RETRY_SLEEP_MS;
  }

  /**
   * @param file
   * @param retryCount
   * @param retrySleepMS
   * @throws FileNotFoundException
   */
  public FileRNG(String file, int retryCount, int retrySleepMS) throws FileNotFoundException {
    this(file);
    this.retryCount = retryCount;
    this.retrySleepMS = retrySleepMS;
  }

  /**
   * @param seed
   * @param length
   * @throws FileNotFoundException
   */
  public FileRNG(String file, int length) throws FileNotFoundException {
    this(file);
    read(length);
  }

  /**
   * @param file
   * @param length
   * @param retryCount
   * @param retrySleepMS
   * @throws FileNotFoundException
   */
  public FileRNG(String file, int length, int retryCount, int retrySleepMS) throws FileNotFoundException {
    this(file, length);
    this.retryCount = retryCount;
    this.retrySleepMS = retrySleepMS;
  }

  /**
   * <!-- finalize -->
   * 
   * Tidy up the stream on object destruction
   * 
   * @see java.lang.Object#finalize()
   * @throws Throwable
   */
  protected void finalize() throws Throwable {
    fp.close();
    super.finalize();
  }

  /**
   * <!-- getSeed -->
   *
   * @see uk.ac.hutton.obiama.random.AbstractRNG#getSeed()
   * @return <code>null</code>
   */
  @Override
  public Long getSeed() {
    return null;
  }

  /**
   * <!-- setRNGSeed -->
   * 
   * @see uk.ac.hutton.obiama.random.AbstractRNG#setRNGSeed(long)
   * @param seed
   * @return <code>false</code>
   */
  protected boolean setRNGSeed(long seed) {
    return false;
  }

  /**
   * <!-- getFile -->
   * 
   * @return The file we are currently reading from
   */
  public String getFile() {
    return file;
  }

  /**
   * <!-- setFile -->
   * 
   * Read from a new file
   * 
   * @param file The file to read from
   * @throws IOException If there is a problem closing the old file or opening
   *           the new one
   */
  public void setFile(String file) throws IOException {
    fp.close();
    fp = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
    this.file = file;
  }

  /**
   * <!-- nextInt -->
   * 
   * Retrieve the next integer from the file. If the end of the file has been
   * reached, try to open it again.
   * 
   * @see cern.jet.random.engine.RandomEngine#nextInt()
   * @return The next integer from the file
   */
  public int nextInt() {
    try {
      int counter = 0;
      while(fp.available() < Integer.SIZE / Byte.SIZE && counter < retryCount) {
        try {
          Thread.sleep(retrySleepMS);
          counter++;
        }
        catch(InterruptedException e) {
          // Ignore
        }
      }
      return fp.readInt();
    }
    catch(EOFException e) {
      ErrorHandler.warn(e, "reading from file: " + file, "random numbers will repeat");
      try {
        setFile(file);
        return fp.readInt();
      }
      catch(IOException ioe) {
        ErrorHandler.fatal(ioe, "reading from file: " + file);
        throw new Bug();
      }
    }
    catch(IOException e) {
      ErrorHandler.fatal(e, "reading from file: " + file);
      throw new Bug();
    }
  }

}
