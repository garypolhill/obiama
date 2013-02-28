/*
 * uk.ac.hutton.obiama.random: RNG.java
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

import java.util.List;

import cern.jet.random.engine.RandomGenerator;

/**
 * <!-- RNG -->
 * 
 * Interface for all (pseudo)-random number generators to follow in OBIAMA. This
 * includes methods to sample from various distributions. These methods are
 * implemented in AbstractRNG for those wanting to implement this interface.
 * 
 * @author Gary Polhill
 */
public interface RNG extends RandomGenerator {
  /**
   * <!-- setSeed -->
   * 
   * Set the seed of the generator. Note that some generators do not have seeds
   * (in which case, this method will have no effect)
   * 
   * @param seed Seed to set
   */
  public void setSeed(long seed);

  /**
   * <!-- getSeed -->
   * 
   * @return The seed of the generator, or null if there isn't one
   */
  public Long getSeed();

  /**
   * <!-- read -->
   * 
   * @param length Number of bytes of randomly generated data to return
   * @return An array of the specified size containing randomly generated data
   */
  public byte[] read(int length);

  /**
   * <!-- withProbability -->
   * 
   * <p>
   * Provide a convenience method for undertaking an action with a given
   * probability, allowing the following idiom:
   * </p>
   * 
   * <p>
   * <code>RNG rng;<br>
   * <br>
   * if(rng.withProbability(p)) doSomethingWithProbabilityP();</code>
   * </p>
   * 
   * @param p A probability
   * @return <code>true</code> if a randomly generated number in the range [0,
   *         1] is less than <code>p</code>
   */
  public boolean withProbability(double p);

  /**
   * <!-- sampleBeta -->
   * 
   * @param alpha
   * @param beta
   * @return A sample from a beta distribution with the specified parameters
   */
  public double sampleBeta(double alpha, double beta);

  /**
   * <!-- sampleBreitWigner -->
   * 
   * @param mean
   * @param gamma
   * @param cut
   * @return A sample from a Breit Wigner distribution with the specified
   *         parameters
   */
  public double sampleBreitWigner(double mean, double gamma, double cut);

  /**
   * <!-- sampleBreitWignerMeanSquare -->
   * 
   * @param mean
   * @param gamma
   * @param cut
   * @return A sample from a Breit Wigner mean square distribution with the
   *         specified parameters
   */
  public double sampleBreitWignerMeanSquare(double mean, double gamma, double cut);

  /**
   * <!-- sampleChiSquared -->
   * 
   * @param freedom
   * @return A sample from a <i>X</i><sup>2</sup> distribution with the
   *         specified parameter
   */
  public double sampleChiSquared(double freedom);

  /**
   * <!-- sampleExponential -->
   * 
   * @param lambda
   * @return A sample from an exponential distribution with the specified
   *         parameter
   */
  public double sampleExponential(double lambda);

  /**
   * <!-- sampleExponentialPower -->
   * 
   * @param tau
   * @return A sample from an exponential power distribution with the specified
   *         parameter
   */
  public double sampleExponentialPower(double tau);

  /**
   * <!-- sampleGamma -->
   * 
   * @param alpha
   * @param lambda
   * @return A sample from a gamma distribution with the specified parameters
   */
  public double sampleGamma(double alpha, double lambda);

  /**
   * <!-- sampleHyperbolic -->
   * 
   * @param alpha
   * @param beta
   * @return A sample from a hyperbolic distribution with the specified
   *         parameters
   */
  public double sampleHyperbolic(double alpha, double beta);

  /**
   * <!-- sampleLogarithmic -->
   * 
   * @param p
   * @return A sample from a logarithmic distribution with the specified
   *         parameter
   */
  public double sampleLogarithmic(double p);

  /**
   * <!-- sampleNormal -->
   * 
   * @param mean
   * @param var
   * @return A sample from a normal distribution with the specified parameters
   */
  public double sampleNormal(double mean, double var);

  /**
   * <!-- sampleStudentT -->
   * 
   * @param freedom
   * @return A sample from a T distribution with the specified parameter
   */
  public double sampleStudentT(double freedom);

  /**
   * <!-- sampleUniform -->
   * 
   * @param min
   * @param max
   * @return A sample from a (floating point) uniform distribution with the
   *         specified parameters
   */
  public double sampleUniform(double min, double max);

  /**
   * <!-- sampleVonMises -->
   * 
   * @param freedom
   * @return A sample from a von Mises distribution with the specified parameter
   */
  public double sampleVonMises(double freedom);

  /**
   * <!-- sampleBinomial -->
   * 
   * @param n
   * @param p
   * @return A sample from a binomial distribution with the specified parameters
   */
  public int sampleBinomial(int n, double p);

  /**
   * <!-- sampleHyperGeometric -->
   * 
   * @param N
   * @param s
   * @param n A sample from a hypergeometric distribution with the specified
   *          parameters
   */
  public int sampleHyperGeometric(int N, int s, int n);

  /**
   * <!-- sampleNegativeBinomial -->
   * 
   * @param n
   * @param p
   * @return A sample from a negative binomial distribution with the specified
   *         parameters
   */
  public int sampleNegativeBinomial(int n, double p);

  /**
   * <!-- samplePoisson -->
   * 
   * @param mean
   * @return A sample from a Poisson distribution with the specified parameters
   */
  public int samplePoisson(double mean);

  /**
   * <!-- sampleUniform -->
   * 
   * @param min
   * @param max
   * @return A sample from a (int) uniform distribution with the specified
   *         parameters
   */
  public int sampleUniform(int min, int max);

  /**
   * <!-- sampleUniform -->
   * 
   * @param min
   * @param max
   * @return A sample from a (long) uniform distribution with the specified
   *         parameters
   */
  public long sampleUniform(long min, long max);

  /**
   * <!-- sampleZeta -->
   * 
   * @param ro
   * @param pk
   * @return A sample from a zeta distribution with the specified parameters
   */
  public int sampleZeta(double ro, double pk);

  /**
   * <!-- shuffle -->
   * 
   * Shuffle a list using the random number generator.
   * 
   * @param list List to be shuffled.
   */
  public <T> void shuffle(List<T> list);
}
