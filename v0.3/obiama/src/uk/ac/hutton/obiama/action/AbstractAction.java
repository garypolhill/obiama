/*
 * uk.ac.hutton.obiama.action: AbstractAction.java
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
import java.util.Map;

import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ScheduleException;
import uk.ac.hutton.obiama.msb.ModelStateBroker;
import uk.ac.hutton.util.FloatingPointComparison;

/**
 * AbstractAction
 * 
 * An optional class for actions to extend, which contains useful common
 * functionality
 * 
 * @author Gary Polhill
 */
public abstract class AbstractAction extends AbstractProcess implements Action {

  /**
   * <!-- getAction -->
   * 
   * The action ultimately responsible for this action is this action...
   * 
   * @see uk.ac.hutton.obiama.action.Process#getAction()
   * @return <code>this</code>
   */
  public Action getAction() {
    return this;
  }

  public void initialiseLocal() throws IntegrationInconsistencyException {
    initialise();
  }

  /**
   * <!-- initialise -->
   * 
   * Method for subclasses to implement to complete the initialisation.
   * 
   * @throws IntegrationInconsistencyException
   */
  protected abstract void initialise() throws IntegrationInconsistencyException;

}
