/*
 * uk.ac.hutton.obiama.exception: NoSuchDataTypeException.java
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
 * NoSuchDataTypeException
 * 
 * Caused when an action requests a datatype that cannot be found in the
 * ontology
 * 
 * @author Gary Polhill
 */
public class NoSuchDataTypeException extends IntegrationInconsistencyException {
  /**
   * Serialisation number
   */
  private static final long serialVersionUID = -6276622086009977951L;

  /**
   * The offending datatype
   */
  URI datatype;

  /**
   * Constructor allowing action and datatype to be passed in
   * 
   * @param action the action requesting the datatype
   * @param datatype the datatype that cannot be found
   */
  public NoSuchDataTypeException(Process action, URI datatype) {
    super(action);
    this.datatype = datatype;
  }

  /**
   * <!-- getErrorMessage -->
   * 
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  protected String getErrorMessage() {
    return "Datatype " + datatype
      + " does not appear in the model structure ontology, but is expected to be there by the action";
  }

}
