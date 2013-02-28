/*
 * uk.ac.hutton.obiama.exception: ModificationOfLockedValueException.java
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
 * <!-- ModificationOfLockedValueException -->
 * 
 * An exception caused when two processes try to modify a value for the same
 * individual and property. This can be caused by actions acting in parallel
 * that cannot be made to do so without the clash, or by a single action making
 * use of a process (e.g. a creator) that modifies a value.
 * 
 * @author Gary Polhill
 */
public class ModificationOfLockedValueException extends IntegrationInconsistencyException {

  /**
   * Serialisation number
   */
  private static final long serialVersionUID = -3225396789829836683L;

  /**
   * Individual the value for which is required
   */
  URI individualURI;

  /**
   * Property the value of which is required
   */
  URI propertyURI;

  /**
   * @param process
   * @param individualURI
   * @param propertyURI
   */
  public ModificationOfLockedValueException(Process process, URI individualURI, URI propertyURI) {
    super(process);
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
    return "Attempt to modify property " + propertyURI + " of individual " + individualURI
      + " already being modified by another process";
  }

}
