/* uk.ac.hutton.obiama.exception: InconsistentDomainException.java
 *
 * Copyright (C) 2013 The James Hutton Institute
 *
 * This file is part of obiama-0.3.
 *
 * obiama-0.3 is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 *
 * obiama-0.3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with obiama-0.3. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Contact information:
 *   Gary Polhill
 *   The James Hutton Institute, Craigiebuckler, Aberdeen. AB15 8QH. UK.
 *   gary.polhill@hutton.ac.uk
 */
package uk.ac.hutton.obiama.exception;

import java.net.URI;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.action.Process;

/**
 * InconsistentDomainException
 *
 * A domain expected by an action is not included in the domain of a property
 *
 * @author Gary Polhill
 */
public class InconsistentDomainException extends
    IntegrationInconsistencyException {
  /**
   * Serial number
   */
  private static final long serialVersionUID = -9041543343063911397L;
  
  /**
   * The property causing the problem
   */
  URI property;
  
  /**
   * The domain the property is expected to have
   */
  URI domain;
  
  /**
   * Constructor allowing property, domain and action to be set
   * 
   * @param property the property causing the exception
   * @param domain the domain an action expects it to have
   * @param process the action expecting the domain
   */
  public InconsistentDomainException(URI property, URI domain, Process process) {
    super(process);
    this.property = property;
    this.domain = domain;
  }

  /**
   * <!-- getErrorMessage -->
   *
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  protected String getErrorMessage() {
    return "Property " + property + " expected to have domain " + domain + " but it doesn't";
  }
}
