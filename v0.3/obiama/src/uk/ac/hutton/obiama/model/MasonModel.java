/* uk.ac.hutton.obiama.model: MasonModel.java
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
package uk.ac.hutton.obiama.model;

import java.util.Map;

import ec.util.MersenneTwisterFast;
import sim.engine.Schedule;
import sim.engine.SimState;
import uk.ac.hutton.obiama.exception.UsageException;
import uk.ac.hutton.obiama.msb.ObiamaSetUp;

/**
 * MasonModel
 *
 * 
 *
 * @author Gary Polhill
 */
public class MasonModel extends SimState {

  /**
   * @param seed
   */
  public MasonModel(long seed) {
    super(seed);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param random
   */
  public MasonModel(MersenneTwisterFast random) {
    super(random);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param random
   * @param schedule
   */
  public MasonModel(MersenneTwisterFast random, Schedule schedule) {
    super(random, schedule);
    // TODO Auto-generated constructor stub
  }

  public void start() {
    super.start();
    // build objects
    // build schedule
  }
  
  public void finish() {
    super.finish();
  }
  
  /**
   * <!-- main -->
   *
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    try {
      Map<String, String> parsedArgs = ObiamaSetUp.getObiamaOptions(MasonModel.class.getName(), args);
    }
    catch(UsageException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
