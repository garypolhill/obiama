/*
 * uk.ac.hutton.obiama.random: DeviceReaderRNG.java
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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <!-- DeviceReaderRNG -->
 * 
 * <p>
 * A random number generator for using /dev/random and /dev/urandom to get data.
 * This is a pseudo-random number generator device made available on Linux
 * machines. Note that /dev/random can block until entropy has been added,
 * whereas /dev/urandom won't block, but doesn't produce such good quality
 * results. (See {@link DevRandomRNG} and {@link DevURandomRNG}.)
 * </p>
 * 
 * <p>
 * If you want to use a different device, or your Linux configuration uses a
 * non-default path for the equivalent of /dev/random and /dev/urandom, you can
 * use this class instead to build your RNG. It simply reads binary data from
 * the device as per {@link FileRNG} (which essentially this class provides a
 * wrapper around allowing the data to be recorded). If your other device issues
 * some non-random data first before giving the random data, you should use one
 * of the constructors with a <code>length</code> argument.
 * </p>
 * 
 * @author Gary Polhill
 */
public class DeviceReaderRNG extends AbstractRecordedRNG {
  /**
   * Serialisation number
   */
  private static final long serialVersionUID = -840060572706461484L;

  /**
   * {@link FileRNG} that will be used to generate the data.
   */
  private FileRNG rng;

  /**
   * Name of the device to read from
   */
  private String device;

  /**
   * Default constructor
   * 
   * @throws FileNotFoundException
   */
  public DeviceReaderRNG(String device) throws FileNotFoundException {
    super();
    rng = new FileRNG(device);
    this.device = device;
  }

  /**
   * Constructor using non-default retryCount and sleepMS
   * 
   * @param retryCount see {@link FileRNG#retryCount}
   * @param retrySleepMS see {@link FileRNG#retrySleepMS}
   * @throws FileNotFoundException
   */
  public DeviceReaderRNG(String device, int retryCount, int retrySleepMS) throws FileNotFoundException {
    super();
    rng = new FileRNG(device, retryCount, retrySleepMS);
    this.device = device;
  }

  /**
   * Constructor saving data
   * 
   * @param file File to save the generated data to so it can be recovered later
   * @throws FileNotFoundException
   */
  public DeviceReaderRNG(String device, String file) throws FileNotFoundException {
    super(file);
    rng = new FileRNG(device);
    this.device = device;
  }

  /**
   * Constructor saving data and using non-default retryCount and sleepMS
   * 
   * @param file File to save the generated data to so it can be recovered later
   * @param retryCount see {@link FileRNG#retryCount}
   * @param retrySleepMS see {@link FileRNG#retrySleepMS}
   * @throws FileNotFoundException
   */
  public DeviceReaderRNG(String device, String file, int retryCount, int retrySleepMS) throws FileNotFoundException {
    super(file);
    rng = new FileRNG(device, retryCount, retrySleepMS);
    this.device = device;
  }

  /**
   * Simple constructor discarding the first <code>length</code> bytes
   * 
   * @param device
   * @param length
   * @throws FileNotFoundException
   */
  public DeviceReaderRNG(String device, int length) throws FileNotFoundException {
    super();
    rng = new FileRNG(device);
    this.device = device;
    read(length);
  }

  /**
   * Simple constructor saving data to a file and discarding the first
   * <code>length</code> bytes
   * 
   * @param device
   * @param file
   * @param length
   * @throws FileNotFoundException
   */
  public DeviceReaderRNG(String device, String file, int length) throws FileNotFoundException {
    super(file);
    rng = new FileRNG(device);
    this.device = device;
    read(length);
  }

  /**
   * Constructor specifying non-default <code>retryCount</code> and
   * <code>retrySleepMS</code> discarding the first <code>length</code> bytes
   * 
   * @param device
   * @param length
   * @param retryCount
   * @param retrySleepMS
   * @throws FileNotFoundException
   */
  public DeviceReaderRNG(String device, int length, int retryCount, int retrySleepMS) throws FileNotFoundException {
    super();
    rng = new FileRNG(device, retryCount, retrySleepMS);
    this.device = device;
    read(length);
  }

  /**
   * Constructor specifying non-default <code>retryCount</code> and
   * <code>retrySleepMS</code> discarding the first <code>length</code> bytes
   * and saving the data to a file
   * 
   * @param device
   * @param file
   * @param length
   * @param retryCount
   * @param retrySleepMS
   * @throws FileNotFoundException
   */
  public DeviceReaderRNG(String device, String file, int length, int retryCount, int retrySleepMS)
      throws FileNotFoundException {
    super(file);
    rng = new FileRNG(device, retryCount, retrySleepMS);
    this.device = device;
    read(length);
  }

  /**
   * <!-- addEntropy -->
   * 
   * Add entropy to the generator by writing some data to it
   * 
   * @param data Data to use to create the entropy
   * @throws IOException
   */
  public void addEntropy(byte[] data) throws IOException {
    DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(device)));
    stream.write(data);
    stream.close();
  }

  /**
   * <!-- generateInt -->
   * 
   * Use the {@link FileRNG} to get the next integer from /dev/random
   * 
   * @see uk.ac.hutton.obiama.random.AbstractRecordedRNG#generateInt()
   * @return The next integer from /dev/random
   */
  protected int generateInt() {
    return rng.nextInt();
  }

}
