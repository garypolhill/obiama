/*
 * uk.ac.hutton.obiama.exception: IndividualNotInstanceOfConceptException.java
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
 * <!-- IndividualNotInstanceOfConceptException -->
 * 
 * An exception caused when an action expects an individual to be a member of an
 * OWL class, but it is not.
 * 
 * @author Gary Polhill
 */
public class IndividualNotInstanceOfConceptException extends IntegrationInconsistencyException {
  /**
   * Serialisation ID
   */
  private static final long serialVersionUID = -2982045539827003751L;

  /**
   * URI of the individual concerned
   */
  URI individual;

  /**
   * URI of the concept the action expects the individual to belong to
   */
  URI concept;

  /**
   * @param individual URI of the individual
   * @param concept URI of the concept the action expects the individual to
   *          belong to
   * @param originator Action the execution of which has caused the exception
   */
  public IndividualNotInstanceOfConceptException(URI individual, URI concept, Process originator) {
    super(originator);
    this.individual = individual;
    this.concept = concept;
  }

  /**
   * <!-- getErrorMessage -->
   *
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  protected String getErrorMessage() {
    return "Individual " + individual + " expected to belong to concept " + concept + ", but doesn't";
  }
}
