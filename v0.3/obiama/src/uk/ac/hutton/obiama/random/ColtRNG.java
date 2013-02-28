/*
 * uk.ac.hutton.obiama.random: ColtRNG.java
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

import cern.jet.random.engine.RandomEngine;

/**
 * <!-- ColtRNG -->
 * 
 * Wrapper class for RNGs from the Colt library.
 * 
 * @author Gary Polhill
 */
public abstract class ColtRNG extends AbstractRNG {

  /**
   * Serialisation number
   */
  private static final long serialVersionUID = 8198609254169636644L;

  /**
   * Colt library random number generator
   */
  RandomEngine rng;

  /**
   * Basic constructor
   */
  public ColtRNG(RandomEngine rng) {
    super();
    this.rng = rng;
  }

  /**
   * Constructor passing in a seed
   * 
   * @param seed
   */
  public ColtRNG(RandomEngine rng, long seed) {
    super(seed);
    this.rng = rng;
  }

  /**
   * Constructor discarding some of the first random numbers
   * 
   * @param length
   */
  public ColtRNG(RandomEngine rng, int length) {
    this(rng);
    read(length);
  }

  /**
   * Constructor passing in a seed and discarding some of the first random
   * numbers
   * 
   * @param seed
   * @param length
   */
  public ColtRNG(RandomEngine rng, long seed, int length) {
    this(rng, seed);
    read(length);
  }

  /**
   * <!-- setRNGSeed -->
   * 
   * Colt library RNGs don't allow you to reset the seed
   * 
   * @see uk.ac.hutton.obiama.random.AbstractRNG#setRNGSeed(long)
   * @param seed
   * @return <code>false</code>
   */
  @Override
  protected boolean setRNGSeed(long seed) {
    return false;
  }

  /**
   * <!-- nextInt -->
   * 
   * @see cern.jet.random.engine.RandomEngine#nextInt()
   * @return The next integer from the Colt library
   */
  @Override
  public int nextInt() {
    return rng.nextInt();
  }

}
