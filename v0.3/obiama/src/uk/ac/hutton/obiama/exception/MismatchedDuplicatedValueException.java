/*
 * uk.ac.hutton.obiama.exception: MismatchedDuplicatedValueException.java
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

import uk.ac.hutton.obiama.action.Process;

/**
 * <!-- MismatchedDuplicatedValueException -->
 * 
 * An exception caused by a mismatched type in a duplicated access to a
 * {@link uk.ac.hutton.obiama.msb.Value}.
 * 
 * @author Gary Polhill
 */
public class MismatchedDuplicatedValueException extends IntegrationInconsistencyException {

  /**
   * Seralisation number
   */
  private static final long serialVersionUID = 3620107634018497172L;
  
  /**
   * Process requesting the value
   */
  Process process;
  
  /**
   * Individual the value is requested for
   */
  URI individualURI;
  
  /**
   * Property of that individual being accessed
   */
  URI propertyURI;

  /**
   * @param process Process requesting the duplicate value
   * @param individualURI Individual the value is requested for
   * @param propertyURI Property of the individual requested
   */
  public MismatchedDuplicatedValueException(Process process, URI individualURI, URI propertyURI) {
    super(process);
    this.process = process;
    this.individualURI = individualURI;
    this.propertyURI = propertyURI;
  }

  /**
   * <!-- getErrorMessage -->
   *
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  protected String getErrorMessage() {
    return "Type mismatch in duplicate access to " + propertyURI + " of individual " + individualURI;
  }

}
