/*
 * uk.ac.hutton.obiama.random: DevRandomRNG.java
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

import java.io.FileNotFoundException;

/**
 * <!-- DevRandomRNG -->
 * 
 * Use /dev/random to generate numbers. This is available on Linux machines, but
 * can block if entropy is low.
 * 
 * @author Gary Polhill
 */
public final class DevRandomRNG extends DeviceReaderRNG {

  /**
   * Serialisation number
   */
  private static final long serialVersionUID = -922538876176550843L;

  /**
   * Device: /dev/random
   */
  public static final String DEVICE = "/dev/random";

  /**
   * @throws FileNotFoundException
   */
  public DevRandomRNG() throws FileNotFoundException {
    super(DEVICE);
  }

  /**
   * @param retryCount
   * @param retrySleepMS
   * @throws FileNotFoundException
   */
  public DevRandomRNG(int retryCount, int retrySleepMS) throws FileNotFoundException {
    super(DEVICE, retryCount, retrySleepMS);
  }

  /**
   * @param file
   * @throws FileNotFoundException
   */
  public DevRandomRNG(String file) throws FileNotFoundException {
    super(DEVICE, file);
  }

  /**
   * @param file
   * @param retryCount
   * @param retrySleepMS
   * @throws FileNotFoundException
   */
  public DevRandomRNG(String file, int retryCount, int retrySleepMS) throws FileNotFoundException {
    super(DEVICE, file, retryCount, retrySleepMS);
  }

}
