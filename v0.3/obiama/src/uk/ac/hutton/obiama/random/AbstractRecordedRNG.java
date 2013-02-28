/*
 * uk.ac.hutton.obiama.random: AbstractRecordedRNG.java
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

import uk.ac.hutton.obiama.exception.ErrorHandler;

/**
 * <!-- AbstractRecordedRNG -->
 * 
 * Superclass for classes generating random numbers that might need to be
 * recorded for run duplication purposes (i.e. because they have no seed)
 * 
 * @author Gary Polhill
 */
public abstract class AbstractRecordedRNG extends AbstractRNG {

  /**
   * Serialisation number
   */
  private static final long serialVersionUID = -641844371201691168L;

  /**
   * Stream to write the data to
   */
  private DataOutputStream fp;

  /**
   * Name of file to write the data to
   */
  private String file;

  /**
   * Constructor that doesn't write anything to a file 
   */
  public AbstractRecordedRNG() {
    super();
    file = null;
    fp = null;
  }

  /**
   * Constructor setting a file location
   * 
   * @param file The name of the file to write to
   * @throws FileNotFoundException
   */
  public AbstractRecordedRNG(String file) throws FileNotFoundException {
    this();
    this.file = file;
    fp = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
  }

  /**
   * <!-- finalize -->
   * 
   * Close the file when the object is destroyed
   *
   * @see java.lang.Object#finalize()
   * @throws Throwable
   */
  protected void finalize() throws Throwable {
    fp.close();
  }
  
  /**
   * <!-- getSeed -->
   * 
   * Subclasses of this class do not have seeds.
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
   * <!-- writing -->
   *
   * @return <code>true</code> if data are being written as they are generated
   */
  public boolean writing() {
    return fp != null;
  }

  /**
   * <!-- file -->
   *
   * @return The file being written to, or <code>null</code> if not writing
   */
  public String file() {
    return file;
  }

  /**
   * <!-- nextInt -->
   * 
   * @see cern.jet.random.engine.RandomEngine#nextInt()
   * @return The next integer, saving it to a file if required
   */
  public int nextInt() {
    int next = generateInt();
    try {
      if(fp != null) fp.write(next);
    }
    catch(IOException e) {
      ErrorHandler.redo(e, "saving generated random numbers");
    }
    return next;
  }

  /**
   * <!-- nextInteger -->
   * 
   * Subclasses implement this to actually generate the data
   *
   * @return The next generated integer
   */
  protected abstract int generateInt();

}
