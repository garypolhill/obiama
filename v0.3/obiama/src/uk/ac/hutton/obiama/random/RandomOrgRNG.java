/*
 * uk.ac.hutton.obiama.random: RandomOrgRNG.java
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.util.Bug;
import uk.ac.hutton.util.Panic;

/**
 * <!-- RandomOrgRNG -->
 * 
 * <a href="http://www.random.org/">random.org</a> is a website offering a true
 * random number generation service. A limited number of random bits can be
 * accessed from this website from a single IP address for free each day. Larger
 * quotas can be purchased. This class provides access to these.
 * 
 * @author Gary Polhill
 */
public class RandomOrgRNG extends AbstractRecordedRNG {

  /**
   * Serialisation number
   */
  private static final long serialVersionUID = -2441124745568368212L;

  /**
   * URL to use to check the quota on random.org
   */
  public static final String CHECK_QUOTA_URL = "http://www.random.org/quota/?format=plain";

  /**
   * random.org needs a range to work with; this is the maximum of the range for
   * one (unsigned) byte
   */
  private static final int SAMPLE_MAX = (1 << Byte.SIZE) - 1;

  /**
   * First part of the URL to use to access random numbers
   */
  public static final String GET_INTEGER_URL = "http://www.random.org/integers/?num=";

  /**
   * Second part of the URL to use to access random numbers, telling it to
   * return a new set of numbers in the range 0 to {@link #SAMPLE_MAX} in a
   * plain, single column hexadecimal format
   */
  public static final String GET_INTEGER_PARAMETERS =
    "&min=0&max=" + SAMPLE_MAX + "&col=1&base=16&format=plain&rnd=new";

  /**
   * Default number of bytes to get from random.org at one time. The number of
   * integers will be this number divided by
   * <code>Integer.SIZE / Byte.SIZE</code>
   */
  public static final int DEFAULT_CHUNK_SIZE = 12500;

  /**
   * Array to store the downloaded integers
   */
  private int[] data;

  /**
   * Actual number of bytes to get from random.org at one time.
   */
  private int chunkSize;

  /**
   * Position in the {@link #data} array of the next number to read
   */
  private int pos;

  /**
   * Number of integers downloaded most recently from random.org
   */
  private int maxPos;

  /**
   * Default constructor using the default chunk size
   */
  public RandomOrgRNG() {
    this(DEFAULT_CHUNK_SIZE);
  }

  /**
   * User-defined chunk size
   * 
   * @param chunkSize Number of bytes to get from random.org at one time
   */
  public RandomOrgRNG(int chunkSize) {
    super();
    init(chunkSize);
  }
  
  /**
   * @param file File to save the data to
   * @throws FileNotFoundException
   */
  public RandomOrgRNG(String file) throws FileNotFoundException {
    this(DEFAULT_CHUNK_SIZE, file);
  }
  
  /**
   * @param chunkSize Number of bytes to get from random.org at one time
   * @param file File to save the data to
   * @throws FileNotFoundException
   */
  public RandomOrgRNG(int chunkSize, String file) throws FileNotFoundException {
    super(file);
    init(chunkSize);
  }
  
  /**
   * <!-- init -->
   * 
   * Initialise the object. Constructors end up calling this.
   *
   * @param chunkSize Number of bytes to get from random.org at one time
   */
  private void init(int chunkSize) {
    this.chunkSize = chunkSize;
    data = new int[chunkSize / (Integer.SIZE / Byte.SIZE)];
    pos = -1;
    maxPos = 0;    
  }

  /**
   * <!-- quotaOK -->
   * 
   * @return <code>true</code> if random.org should let us download at least one
   *         more chunk of data
   */
  public boolean quotaOK() {
    Long quota = quotaBytes();
    return quota != null && quota > 0;
  }

  /**
   * <!-- quotaBytes -->
   * 
   * @return The quota of bytes remaining to download from random.org
   */
  public Long quotaBytes() {
    // Check the quota
    URL quotaCheck;
    try {
      quotaCheck = new URL(CHECK_QUOTA_URL);
      BufferedReader fp = new BufferedReader(new InputStreamReader(quotaCheck.openStream()));
      String quotaLine = fp.readLine();
      fp.close();
      if(quotaLine != null) return Long.parseLong(quotaLine) / Byte.SIZE;
      return null;
    }
    catch(MalformedURLException e) {
      throw new Bug();
    }
    catch(IOException e) {
      ErrorHandler.redo(e, "checking quota on random.org");
      throw new Panic();
    }

  }

  /**
   * <!-- generateInt -->
   * 
   * Get the next integer, downloading another chunk of data if need be
   *
   * @see uk.ac.hutton.obiama.random.AbstractRecordedRNG#generateInt()
   * @return The next integer
   */
  protected int generateInt() {
    if(pos < 0 || pos >= maxPos) {
      try {
        if(!quotaOK()) {
          ErrorHandler.redo(new Exception("Run out of quota at random.org"),
              "downloading random numbers from random.org");
        }

        // Download some more random numbers. The URL is formatted to ask for
        // one byte at a time.
        URL random = new URL(GET_INTEGER_URL + chunkSize + GET_INTEGER_PARAMETERS);
        BufferedReader fp = new BufferedReader(new InputStreamReader(random.openStream()));
        int num = 0;
        int counter = 0;
        int i = 0;
        for(String line = fp.readLine(); line != null && i < data.length; line = fp.readLine()) {
          int byt = Integer.parseInt(line, 16);
          byt <<= counter * Byte.SIZE;
          num |= byt;
          counter++;
          if(counter == Integer.SIZE / Byte.SIZE) {
            counter = 0;
            data[i] = num;
            i++;
            num = 0;
          }
        }
        maxPos = i;
        fp.close();
      }
      catch(MalformedURLException e) {
        throw new Bug();
      }
      catch(IOException e) {
        ErrorHandler.redo(e, "downloading random numbers from random.org");
        throw new Panic();
      }
      pos = 0;
    }
    return data[pos++];
  }

  /**
   * <!-- main -->
   * 
   * Run this to check the program works
   * 
   * @param args No arguments are processed
   */
  public static void main(String[] args) {
    RandomOrgRNG rng = new RandomOrgRNG(128);
    for(int i = 0; i <= rng.data.length; i++) {
      System.out.println("The " + (i + 1) + "th number is " + Integer.toHexString(rng.nextInt()));
    }
  }

}
