/*
 * uk.ac.hutton.obiama.action: AbstractCreator.java
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
 * <!-- AbstractCreator -->
 * 
 * @author Gary Polhill
 */
public abstract class AbstractCreator extends AbstractAction implements Creator {
  private Instance creation = null;

  public void setCreation(Instance creation) {
    this.creation = creation;
  }

  /**
   * <!-- step -->
   * 
   * Implement the step method; this simply checks that the object being created
   * has been specified, and calls the Creator step method.
   * 
   * @see uk.ac.hutton.obiama.action.Action#step(java.net.URI)
   * @param individual The individual nominally performing the creation (usually
   *          the exogenous agent)
   * @throws IntegrationInconsistencyException
   */
  @Override
  public final void step(URI individual) throws IntegrationInconsistencyException {
    if(creation == null) {
      // TODO throw exception
    }
    step(individual, creation);

    // Ensure that the Creator cannot be rerun until the setCreation() method
    // has been called again
    creation = null;
  }

  /**
   * <!-- stepExogenousCreator -->
   * 
   * A method to implement for Creator actions that are part of an exogenous
   * process (e.g. initialisation or data input).
   * 
   * @throws IntegrationInconsistencyException
   */
  protected abstract void step(URI actor, Instance creation) throws IntegrationInconsistencyException;
}
