/* uk.ac.hutton.obiama.random: JavaRNG.java
 *
 * Copyright (C) 2013 The James Hutton Institute
 *
 * This file is part of obiama-0.3.
 *
 * obiama-0.3 is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 *
 * obiama-0.3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with obiama-0.3. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Contact information:
 *   Gary Polhill
 *   The James Hutton Institute, Craigiebuckler, Aberdeen. AB15 8QH. UK.
 *   gary.polhill@hutton.ac.uk
 */
package uk.ac.hutton.obiama.random;

import java.util.Random;

/**
 * <!-- JavaRNG -->
 * 
 * A random number generator using Java's native functionality.
 *
 * @author Gary Polhill
 */
public final class JavaRNG extends AbstractRNG {

  /**
   * Serialisation number
   */
  private static final long serialVersionUID = 6564061116380807196L;
  
  /**
   * Java native RNG
   */
  Random rng;

  /**
   * @param seed Seed for the RNG
   */
  public JavaRNG(long seed) {
    super(seed);
    rng = new Random(seed);
  }

  /**
   * @param seed Seed for the RNG
   * @param length Number of initial samples to discard
   */
  public JavaRNG(long seed, int length) {
    this(seed);
    read(length);
  }

  /**
   * <!-- setRNGSeed -->
   *
   * @see uk.ac.hutton.obiama.random.AbstractRNG#setRNGSeed(long)
   * @param seed
   * @return <code>true</code>
   */
  protected boolean setRNGSeed(long seed) {
    rng.setSeed(seed);
    return true;
  }

  /**
   * <!-- nextInt -->
   *
   * @see cern.jet.random.engine.RandomEngine#nextInt()
   * @return
   */
  public int nextInt() {
    return rng.nextInt();
  }
  
}
