/*
 * uk.ac.hutton.obiama.exception: DeathOfDeadAgentException.java
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
 * <!-- DeathOfDeadAgentException -->
 * 
 * @author Gary Polhill
 */
public class DeathOfDeadAgentException extends DeathOfNonAgentException {

  /**
   * Serialisation number
   */
  private static final long serialVersionUID = 2941614440935205352L;

  /**
   * @param action
   * @param individualURI
   */
  public DeathOfDeadAgentException(Action action, URI individualURI) {
    super(action, individualURI);
  }

  @Override
  protected String getErrorMessage() {
    return "Attempt to run death of dead agent " + individualURI;
  }

}
