/*
 * uk.ac.hutton.obiama.exception: IndividualDoesNotHavePropertyException.java
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
 * IndividualDoesNotHavePropertyException
 * 
 * 
 * 
 * @author Gary Polhill
 */
public class IndividualDoesNotHavePropertyException extends IntegrationInconsistencyException {
  /**
   * Serial number
   */
  private static final long serialVersionUID = -9178582088900703397L;

  /**
   * The individual that does not have the expected property
   */
  URI individual;

  /**
   * The property they are expected to have
   */
  URI property;

  /**
   * Constructor allowing relevant information to be passed in
   * 
   * @param process The action expecting an individual to have a property
   * @param individual The individual the action expects to have a property
   * @param property The property the action expects the individual to have
   */
  public IndividualDoesNotHavePropertyException(Process process, URI individual, URI property) {
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
    return "Individual " + individual + " does not have a value for property " + property
      + ", but is expected to by the action";
  }
}
