/*
 * uk.ac.hutton.obiama.random: JavaSecureRNG.java
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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import uk.ac.hutton.obiama.exception.ErrorHandler;

/**
 * <!-- JavaSecureRNG -->
 * 
 * An RNG based on Java's {@link SecureRandom} class.
 * 
 * @author Gary Polhill
 */
public class JavaSecureRNG extends AbstractRNG {

  /**
   * Serialisation number
   */
  private static final long serialVersionUID = 1753831968682764161L;

  /**
   * Internal RNG
   */
  private SecureRandom rng;

  /**
   * The algorithm that SecureRandom implements
   */
  private static final String ALGORITHM = "SHA1PRNG";

  /**
   * Constructor with seed
   * 
   * @param seed
   */
  public JavaSecureRNG(long seed) {
    super(seed);
    try {
      rng = SecureRandom.getInstance(ALGORITHM);
      rng.setSeed(seed);
    }
    catch(NoSuchAlgorithmException e) {
      ErrorHandler.redo(e, "building Java SecureRandom generator " + ALGORITHM);
    }
  }

  /**
   * Constructor with seed discarding initial bytes
   * 
   * @param seed
   * @param length
   */
  public JavaSecureRNG(long seed, int length) {
    this(seed);
    read(length);
  }

  /**
   * <!-- setRNGSeed -->
   * 
   * Successive seed settings interfere with each other (see
   * {@link SecureRandom#setSeed(long)}), so it doesn't make sense to allow the
   * seed to be set from here (otherwise the run will not be repeatable).
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
   * @return The next integer from the SecureRandom RNG
   */
  @Override
  public int nextInt() {
    return rng.nextInt();
  }

}
