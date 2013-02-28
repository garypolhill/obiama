/*
 * uk.ac.hutton.obiama.action: SquareACCCreator.java
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
package uk.ac.hutton.obiama.action;

import java.net.URI;

import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.msb.Instance;

/**
 * <!-- SquareACCCreator -->
 * 
 * Create a square abstract cell complex. Parameters xCells and yCells 
 * determine the number of cells on the x and y axes respectively.
 * Parameters wrapX and wrapY determine which dimensions are wrapped 
 * such that the cells on one side of the space are neighbours of the
 * cells on the other. For a toroid, set both to <code>true</code>,
 * for a plane, set both to <code>false</code>, and for a cylinder,
 * set one to <code>true</code> and the other to <code>false</code>.
 * 
 * @author Gary Polhill
 */
public class SquareACCCreator extends AbstractACCCreator implements Creator {
  /**
   * Number of cells in the space on the x axis
   */
  protected ActionParameter xCells;
  
  /**
   * Number of cells in the space on the y axis
   */
  protected ActionParameter yCells;
  
  /**
   * Whether to wrap the x dimension of the space
   */
  protected ActionParameter wrapX;
  
  /**
   * Whether to wrap the y dimension of the space
   */
  protected ActionParameter wrapY;

  /**
   * Constructor: initialise the parameters
   */
  public SquareACCCreator() {
    xCells = new ActionParameter("xCells", int.class, "number of cells in the space on the x axis");
    yCells = new ActionParameter("xCells", int.class, "number of cells in the space on the y axis");
    wrapX = new ActionParameter("wrapX", boolean.class, Boolean.toString(true), "wrap the x axis");
    wrapY = new ActionParameter("wrapY", boolean.class, Boolean.toString(true), "wrap the y axis");
  }
  
  /**
   * <!-- step -->
   *
   * @see uk.ac.hutton.obiama.action.AbstractCreator#step(java.net.URI, uk.ac.hutton.obiama.msb.Instance)
   * @param actor Agent nominally responsible for creating this creation
   * @param creation Instance being created
   * @throws IntegrationInconsistencyException
   */
  protected void step(URI actor, Instance creation) throws IntegrationInconsistencyException {
    buildSpace(creation, xCells.getIntParameter(), yCells.getIntParameter(), wrapX
        .getBooleanParameter(), wrapY.getBooleanParameter());
  }

}
