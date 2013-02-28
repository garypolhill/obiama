/*
 * uk.ac.hutton.obiama.exception: DeathOfNonAgentException.java
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

/**
 * <!-- DeathOfNonAgentException -->
 * 
 * An individual that is not an Agent has been asserted to die.
 * 
 * @author Gary Polhill
 */
public class DeathOfNonAgentException extends IntegrationInconsistencyException {
  /**
   * Serialisation number
   */
  private static final long serialVersionUID = 8764613635878127548L;

  /**
   * URI of the individual that isn't an Agent
   */
  URI individualURI;

  /**
   * @param action Action causing the problem
   * @param individualURI URI of the individual that isn't an Agent
   */
  public DeathOfNonAgentException(Action action, URI individualURI) {
    super(action);
    this.individualURI = individualURI;
  }

  /**
   * <!-- getErrorMessage -->
   *
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  protected String getErrorMessage() {
    return "Attempt to run death of non-agent " + individualURI;
  }

}
