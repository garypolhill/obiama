/*
 * uk.ac.hutton.obiama.exception: IndividualAlreadyHasPropertyException.java
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
 * IndividualAlreadyHasPropertyException
 * 
 * An exception caused when an Action is giving a value to a property of an
 * individual, expecting that individual not to already have a value (for
 * example, during initialisation).
 * 
 * @author Gary Polhill
 */
public class IndividualAlreadyHasPropertyException extends IntegrationInconsistencyException {
  /**
   * Serialisation number
   */
  private static final long serialVersionUID = 7212945589554550194L;

  /**
   * The individual expected not to have a property
   */
  URI individual;

  /**
   * The property the individual is expected not to have
   */
  URI property;

  /**
   * (One of) the value(s) that property in fact already has
   */
  String value;

  /**
   * Constructor
   * 
   * @param process Action causing the exception
   * @param individual Individual expected not to have a property
   * @param property Property the individual is expected not to have
   * @param subject (One of) the value(s) the property does in fact have
   */
  public IndividualAlreadyHasPropertyException(Process process, URI individual, URI property, Object subject) {
    super(process);
    this.individual = individual;
    this.property = property;
    this.value = subject.toString();
  }

  /**
   * <!-- getErrorMessage -->
   * 
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  protected String getErrorMessage() {
    return "Individual " + individual + " already has a value (" + value + ") for property " + property
      + ", but is not expected to by the action";
  }

}
