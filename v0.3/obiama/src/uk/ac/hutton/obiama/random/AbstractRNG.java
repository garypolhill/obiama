/*
 * uk.ac.hutton.obiama.random: AbstractRNG.java
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cern.jet.random.AbstractDistribution;
import cern.jet.random.Beta;
import cern.jet.random.Binomial;
import cern.jet.random.BreitWigner;
import cern.jet.random.BreitWignerMeanSquare;
import cern.jet.random.ChiSquare;
import cern.jet.random.Exponential;
import cern.jet.random.ExponentialPower;
import cern.jet.random.Gamma;
import cern.jet.random.HyperGeometric;
import cern.jet.random.Hyperbolic;
import cern.jet.random.Logarithmic;
import cern.jet.random.NegativeBinomial;
import cern.jet.random.Normal;
import cern.jet.random.Poisson;
import cern.jet.random.StudentT;
import cern.jet.random.Uniform;
import cern.jet.random.VonMises;
import cern.jet.random.Zeta;
import cern.jet.random.engine.RandomEngine;

/**
 * <!-- AbstractRNG -->
 * 
 * An abstract RNG for other classes to subclass from, implementing the
 * distribution sampling methods.
 * 
 * @author Gary Polhill
 */
abstract class AbstractRNG extends RandomEngine implements RNG {
  /**
   * Serialization number
   */
  private static final long serialVersionUID = -3125305359852305392L;

  /**
   * Private copy of the seed
   */
  private long seed;

  /**
   * Map of distributions already sampled from (see
   * {@link #makeKey(String,double[])})
   */
  private Map<String, AbstractDistribution> distributions;

  /**
   * Constructor for generators without a seed
   */
  public AbstractRNG() {
    distributions = new HashMap<String, AbstractDistribution>();
  }

  /**
   * @param seed Seed for the generator
   */
  public AbstractRNG(long seed) {
    this();
    this.seed = seed;
  }

  /**
   * Constructor for generators without a seed
   * 
   * @param length Number of initial samples to discard
   */
  public AbstractRNG(int length) {
    this();
    read(length);
  }

  /**
   * @param seed Seed for the generator
   * @param length Number of initial samples to discard
   */
  public AbstractRNG(long seed, int length) {
    this(seed);
    read(length);
  }

  /**
   * <!-- getSeed -->
   * 
   * @see uk.ac.hutton.obiama.random.RNG#getSeed()
   * @return The seed used for this generator (or null if none)
   */
  public Long getSeed() {
    return seed;
  }

  /**
   * <!-- setSeed -->
   * 
   * This method simply keeps track of the seed set for the generator, and then
   * passes on responsibility to {@link #setRNGSeed(long)}
   * 
   * @see uk.ac.hutton.obiama.random.RNG#setSeed(long)
   * @param seed The seed to use for this generator
   */
  public void setSeed(long seed) {
    if(setRNGSeed(seed)) this.seed = seed;
  }

  /**
   * <!-- setRNGSeed -->
   * 
   * Method that actually sets the seed in the RNG itself
   * 
   * @param seed The seed
   * @return <code>true</code> if the seed was changed successfully
   */
  protected abstract boolean setRNGSeed(long seed);

  /**
   * <!-- withProbability -->
   * 
   * Return a boolean that is true with the given probability
   * 
   * @see uk.ac.hutton.obiama.random.RNG#withProbability(double)
   * @param p The probability
   * @return <code>true</code> in proportion p of the times it is called
   */
  public boolean withProbability(double p) {
    return sampleUniform(0.0, 1.0) < p;
  }

  /**
   * <!-- read -->
   * 
   * Read an array of bytes from the distribution
   * 
   * @see uk.ac.hutton.obiama.random.RNG#read(int)
   * @param length The number of bytes to read
   * @return An array of bytes
   */
  public byte[] read(int length) {
    byte[] arr = new byte[length];
    for(int i = 0; i < length / Byte.SIZE + (length % Byte.SIZE > 0 ? 1 : 0); i += Integer.SIZE / Byte.SIZE) {
      int r = nextInt();
      for(int j = 0; j < Integer.SIZE / Byte.SIZE && j + i < length; j++) {
        arr[i + j] = (byte)((r >> Byte.SIZE * j) & 0xFF);
      }
    }
    return arr;
  }

  /**
   * <!-- makeKey -->
   * 
   * The statistical distributions sampled from are stored in
   * {@link #distributions}. This method computes a key to use to allow
   * distributions to be retrieved
   * 
   * @param dist An identifier for the distribution
   * @param params Parameters the distribution takes
   * @return A key to use in the {@link distributions} map
   */
  private String makeKey(String dist, double... params) {
    StringBuffer buff = new StringBuffer(dist);
    for(int i = 0; i < params.length; i++) {
      buff.append(":");
      buff.append(Double.toHexString(params[i]));
    }
    return buff.toString();
  }

  /**
   * <!-- sampleBeta -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#sampleBeta(double, double)
   * @param alpha
   * @param beta
   * @return A sample from a beta distribution
   */
  public double sampleBeta(double alpha, double beta) {
    String key = makeKey("Beta", alpha, beta);
    if(!distributions.containsKey(key)) {
      distributions.put(key, new Beta(alpha, beta, this));
    }
    return distributions.get(key).nextDouble();
  }

  /**
   * <!-- sampleBinomial -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#sampleBinomial(int, double)
   * @param n
   * @param p
   * @return A sample from a binomial distribution
   */
  public int sampleBinomial(int n, double p) {
    String key = makeKey("Binomial", (double)n, p);
    if(!distributions.containsKey(key)) {
      distributions.put(key, new Binomial(n, p, this));
    }
    return distributions.get(key).nextInt();
  }

  /**
   * <!-- sampleBreitWigner -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#sampleBreitWigner(double, double,
   *      double)
   * @param mean
   * @param gamma
   * @param cut
   * @return A sample from a Breit Wigner distribution
   */
  public double sampleBreitWigner(double mean, double gamma, double cut) {
    String key = makeKey("BreitWigner", mean, gamma, cut);
    if(!distributions.containsKey(key)) {
      distributions.put(key, new BreitWigner(mean, gamma, cut, this));
    }
    return distributions.get(key).nextDouble();
  }

  /**
   * <!-- sampleBreitWignerMeanSquare -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#sampleBreitWignerMeanSquare(double,
   *      double, double)
   * @param mean
   * @param gamma
   * @param cut
   * @return A sample from a Breit Wigner mean square distribution
   */
  public double sampleBreitWignerMeanSquare(double mean, double gamma, double cut) {
    String key = makeKey("BreitWignerMeanSquare", mean, gamma, cut);
    if(!distributions.containsKey(key)) {
      distributions.put(key, new BreitWignerMeanSquare(mean, gamma, cut, this));
    }
    return distributions.get(key).nextDouble();
  }

  /**
   * <!-- sampleChiSquared -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#sampleChiSquared(double)
   * @param freedom
   * @return A sample from a Chi squared distribution
   */
  public double sampleChiSquared(double freedom) {
    String key = makeKey("ChiSquared", freedom);
    if(!distributions.containsKey(key)) {
      distributions.put(key, new ChiSquare(freedom, this));
    }
    return distributions.get(key).nextDouble();
  }

  /**
   * <!-- sampleExponential -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#sampleExponential(double)
   * @param lambda
   * @return A sample from an exponential disribution
   */
  public double sampleExponential(double lambda) {
    String key = makeKey("Exponential", lambda);
    if(!distributions.containsKey(key)) {
      distributions.put(key, new Exponential(lambda, this));
    }
    return distributions.get(key).nextDouble();
  }

  /**
   * <!-- sampleExponentialPower -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#sampleExponentialPower(double)
   * @param tau
   * @return A sample from an exponential power distribution
   */
  public double sampleExponentialPower(double tau) {
    String key = makeKey("ExponentialPower", tau);
    if(!distributions.containsKey(key)) {
      distributions.put(key, new ExponentialPower(tau, this));
    }
    return distributions.get(key).nextDouble();
  }

  /**
   * <!-- sampleGamma -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#sampleGamma(double, double)
   * @param alpha
   * @param lambda
   * @return A sample from a gamma distribution
   */
  public double sampleGamma(double alpha, double lambda) {
    String key = makeKey("Gamma", alpha, lambda);
    if(!distributions.containsKey(key)) {
      distributions.put(key, new Gamma(alpha, lambda, this));
    }
    return distributions.get(key).nextDouble();
  }

  /**
   * <!-- sampleHyperGeometric -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#sampleHyperGeometric(int, int,
   *      int)
   * @param N
   * @param s
   * @param n
   * @return A sample from a hypergeometric distribution
   */
  public int sampleHyperGeometric(int N, int s, int n) {
    String key = makeKey("HyperGeometric", N, s, n);
    if(!distributions.containsKey(key)) {
      distributions.put(key, new HyperGeometric(N, s, n, this));
    }
    return distributions.get(key).nextInt();
  }

  /**
   * <!-- sampleHyperbolic -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#sampleHyperbolic(double, double)
   * @param alpha
   * @param beta
   * @return A sample from a hyperbolic distribution
   */
  public double sampleHyperbolic(double alpha, double beta) {
    String key = makeKey("Hyperbolic", alpha, beta);
    if(!distributions.containsKey(key)) {
      distributions.put(key, new Hyperbolic(alpha, beta, this));
    }
    return distributions.get(key).nextDouble();
  }

  /**
   * <!-- sampleLogarithmic -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#sampleLogarithmic(double)
   * @param p
   * @return A sample from a logarithmic distribution
   */
  public double sampleLogarithmic(double p) {
    String key = makeKey("Logarithmic", p);
    if(!distributions.containsKey(key)) {
      distributions.put(key, new Logarithmic(p, this));
    }
    return distributions.get(key).nextDouble();
  }

  /**
   * <!-- sampleNegativeBinomial -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#sampleNegativeBinomial(int,
   *      double)
   * @param n
   * @param p
   * @return A sample from a negative binomial distribution
   */
  public int sampleNegativeBinomial(int n, double p) {
    String key = makeKey("NegativeBinomial", n, p);
    if(!distributions.containsKey(key)) {
      distributions.put(key, new NegativeBinomial(n, p, this));
    }
    return distributions.get(key).nextInt();
  }

  /**
   * <!-- sampleNormal -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#sampleNormal(double, double)
   * @param mean
   * @param var
   * @return A sample from a normal distribution
   */
  public double sampleNormal(double mean, double var) {
    String key = makeKey("Normal", mean, var);
    if(!distributions.containsKey(key)) {
      distributions.put(key, new Normal(mean, var, this));
    }
    return distributions.get(key).nextDouble();
  }

  /**
   * <!-- samplePoisson -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#samplePoisson(double)
   * @param mean
   * @return A sample from a Poisson distribution
   */
  public int samplePoisson(double mean) {
    String key = makeKey("Poisson", mean);
    if(!distributions.containsKey(key)) {
      distributions.put(key, new Poisson(mean, this));
    }
    return distributions.get(key).nextInt();
  }

  /**
   * <!-- sampleStudentT -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#sampleStudentT(double)
   * @param freedom
   * @return A sample from a T distribution
   */
  public double sampleStudentT(double freedom) {
    String key = makeKey("StudentT", freedom);
    if(!distributions.containsKey(key)) {
      distributions.put(key, new StudentT(freedom, this));
    }
    return distributions.get(key).nextDouble();
  }

  /**
   * <!-- sampleUniform -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#sampleUniform(double, double)
   * @param min
   * @param max
   * @return A sample from a uniform (double) distribution
   */
  public double sampleUniform(double min, double max) {
    String key = makeKey("DoubleUniform", min, max);
    if(!distributions.containsKey(key)) {
      distributions.put(key, new Uniform(min, max, this));
    }
    return distributions.get(key).nextDouble();
  }

  /**
   * <!-- sampleUniform -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#sampleUniform(int, int)
   * @param min
   * @param max
   * @return A sample from a uniform (int) distribution
   */
  public int sampleUniform(int min, int max) {
    String key = makeKey("IntUniform");
    if(!distributions.containsKey(key)) {
      distributions.put(key, new Uniform(this));
    }
    return ((Uniform)distributions.get(key)).nextIntFromTo(min, max);
  }

  /**
   * <!-- sampleUniform -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#sampleUniform(long, long)
   * @param min
   * @param max
   * @return A sample from a uniform (long) distribution
   */
  public long sampleUniform(long min, long max) {
    String key = makeKey("LongUniform");
    if(!distributions.containsKey(key)) {
      distributions.put(key, new Uniform(this));
    }
    return ((Uniform)distributions.get(key)).nextLongFromTo(min, max);
  }

  /**
   * <!-- sampleVonMises -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#sampleVonMises(double)
   * @param freedom
   * @return A sample from a uniform von Mises distribution
   */
  public double sampleVonMises(double freedom) {
    String key = makeKey("VonMises", freedom);
    if(!distributions.containsKey(key)) {
      distributions.put(key, new VonMises(freedom, this));
    }
    return distributions.get(key).nextDouble();
  }

  /**
   * <!-- sampleZeta -->
   * 
   * @see uk.ac.hutton.obiama.random.Sampler#sampleZeta(double, double)
   * @param ro
   * @param pk
   * @return A sample from a zeta distribution
   */
  public int sampleZeta(double ro, double pk) {
    String key = makeKey("Zeta", ro, pk);
    if(!distributions.containsKey(key)) {
      distributions.put(key, new Zeta(ro, pk, this));
    }
    return distributions.get(key).nextInt();
  }

  /**
   * <!-- shuffle -->
   * 
   * Implement a shuffle algorithm. This is achieved by allocating a random
   * number to each element in the list, then sorting in order of the random
   * number, which allows the possibility that the list contains duplicates.
   * 
   * @see uk.ac.hutton.obiama.random.RNG#shuffle(java.util.List)
   * @param list
   */
  public <T> void shuffle(List<T> list) {
    ArrayList<Integer> arr = new ArrayList<Integer>();
    final double[] rnd = new double[list.size()];
    Map<Integer, T> randomMap = new HashMap<Integer, T>();

    for(int i = 0; list.size() > 0; i++) {
      arr.add(i);
      rnd[i] = nextDouble();
      randomMap.put(i, list.remove(0));
    }

    Collections.sort(arr, new Comparator<Integer>() {
      public int compare(Integer arg0, Integer arg1) {
        return Double.compare(rnd[arg0], rnd[arg1]);
      }
    });

    for(int i = 0; i < arr.size(); i++) {
      list.add(randomMap.get(arr.get(i)));
    }
  }

}
