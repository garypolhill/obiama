/*
 * uk.ac.hutton.obiama.exception:
 * NeedFunctionalGotNonFunctionalPropertyException.java
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
import uk.ac.hutton.util.StringTools;

/**
 * NeedFunctionalGotNonFunctionalPropertyException
 * 
 * An exception thrown when an action expects a functional property, but in the
 * ontology the property is non-functional
 * 
 * @author Gary Polhill
 */
public class NeedFunctionalGotNonFunctionalPropertyException extends IntegrationInconsistencyException {
  /**
   * Serial number
   */
  private static final long serialVersionUID = -6976615513300726206L;

  /**
   * URI of the property expected to be functional
   */
  URI property;

  String[] values;

  /**
   * Constructor passing in the action and the property
   * 
   * @param process the action expecting the property to be functional
   * @param property the non-functional property
   * @param str 
   * @param valueStr 
   */
  public NeedFunctionalGotNonFunctionalPropertyException(Process process, URI property) {
    super(process);
    this.property = property;
    values = null;
  }

  /**
   * @param action
   * @param property
   * @param values
   */
  public NeedFunctionalGotNonFunctionalPropertyException(Process process, URI property, String... values) {
    this(process, property);
    this.values = values;
  }

  /**
   * <!-- getErrorMessage -->
   * 
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  protected String getErrorMessage() {
    if(values == null) return "Non-functional property " + property + " expected by action to be functional";
    return "Property " + property + " expected by action to be functional has more than one different value: "
      + StringTools.join(",", (Object[])values);
  }
}
