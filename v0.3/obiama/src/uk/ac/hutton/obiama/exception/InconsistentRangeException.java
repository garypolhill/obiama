/*
 * uk.ac.hutton.obiama.exception: InconsistentRangeException.java
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
 * InconsistentRangeException
 * 
 * An exception caused by an action expecting a range for a property that is
 * inconsistent with a range in the model ontology
 * 
 * @author Gary Polhill
 */
public class InconsistentRangeException extends IntegrationInconsistencyException {
  /**
   * Serial number for serialization
   */
  private static final long serialVersionUID = -7786755141386713190L;

  /**
   * The property causing the exception
   */
  URI property;

  /**
   * The range it is expected to have by the action
   */
  URI range;

  /**
   * The range it is expected to have by the ontology
   */
  URI ontoRange;

  /**
   * Constructor allowing the property, range and action to be passed in
   * 
   * @param property the URI of the property
   * @param ontoRange the URI of the range it has in the ontology (or
   *          <code>null</code> if not known)
   * @param range the URI of the range it is expected to have (or
   *          <code>null</code> if not known)
   * @param process the action causing the problem
   */
  public InconsistentRangeException(URI property, URI ontoRange, URI range, Process process) {
    super(process);
    this.property = property;
    this.ontoRange = ontoRange;
    this.range = range;
  }

  /**
   * <!-- getErrorMessage -->
   * 
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  protected String getErrorMessage() {
    if(ontoRange == null && range == null)
      return "Property " + property + " has inconsistent range with that in the action";
    if(ontoRange == null) return "Property " + property + " expected to have range " + range + ", but it doesn't";
    if(range == null)
      return "Property " + property + " expected to have range inconsistent with " + ontoRange
        + " in the model structure ontology";
    return "Property " + property + " expected to have range " + range
      + ", but in the model structure ontology it has range " + ontoRange;
  }

}
