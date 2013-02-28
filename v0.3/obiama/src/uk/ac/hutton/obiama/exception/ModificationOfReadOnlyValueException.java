/*
 * uk.ac.hutton.obiama.exception: ModificationOfReadOnlyValueException.java
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
 * <!-- ModificationOfReadOnlyValueException -->
 * 
 * An exception caused when an attempt is made to modify a read-only value.
 * 
 * @author Gary Polhill
 */
public class ModificationOfReadOnlyValueException extends IntegrationInconsistencyException {
  /**
   * Serialisation number
   */
  private static final long serialVersionUID = 8590266107510310602L;

  /**
   * The process attempting to modify the read-only value
   */
  Process process;

  /**
   * The individual with the property attempting to be modified
   */
  URI individual;

  /**
   * The property attempting to be modified
   */
  URI property;

  /**
   * @param process The process attempting to modify the read-only value
   * @param process2 
   * @param action The action ultimately responsible for running this process
   * @param individual The individual that is the subject of the property
   * @param property The property being modified
   */
  public ModificationOfReadOnlyValueException(Process process, URI individual, URI property) {
    super(process);
    this.process = process;
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
    return "Attempt to modify read-only value " + property + " of individual " + individual;
  }

}
