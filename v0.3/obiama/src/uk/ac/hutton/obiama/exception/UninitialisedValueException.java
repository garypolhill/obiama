/*
 * uk.ac.hutton.obiama.exception: UninitialisedValueException.java
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
package uk.ac.hutton.obiama.exception;

import java.net.URI;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.action.Process;

/**
 * UninitialisedValueException
 * 
 * An exception caused when an action fails to give an initial value to a
 * property.
 * 
 * @author Gary Polhill
 */
public class UninitialisedValueException extends IntegrationInconsistencyException {
  /**
   * Serialisation number
   */
  private static final long serialVersionUID = 6174064648388856789L;

  /**
   * The individual supposed to have the property
   */
  URI individual;

  /**
   * The property the action should have initialised for the individual
   */
  URI property;

  /**
   * Constructor
   * 
   * @param process The action causing the exception
   * @param individual The individual supposed to be given a value for the
   *          property
   * @param property The property the action should have given a value for
   */
  public UninitialisedValueException(Process process, URI individual, URI property) {
    super(process);
    this.individual = individual;
    this.property = property;
  }

  /**
   * <!-- getErrorMessage -->
   * 
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  protected String getErrorMessage() {
    return "Individual " + individual + " has no initial value for property " + property;
  }

}
