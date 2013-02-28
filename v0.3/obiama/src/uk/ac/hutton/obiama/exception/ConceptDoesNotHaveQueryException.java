/*
 * uk.ac.hutton.obiama.exception: ConceptDoesNotHaveQueryException.java
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
 * <!-- ConceptDoesNotHaveQueryException -->
 * 
 * An exception caused when a {@link uk.ac.hutton.obiama.msb.Concept} is
 * expected by an {@link uk.ac.hutton.obiama.action.Action} to have a
 * {@link uk.ac.hutton.obiama.action.Query}, but this is not asserted in the
 * model structure ontology.
 * 
 * @author Gary Polhill
 */
public class ConceptDoesNotHaveQueryException extends IntegrationInconsistencyException {

  /**
   * Serialisation number
   */
  private static final long serialVersionUID = -6350790475398111883L;

  /**
   * Concept
   */
  URI concept;

  /**
   * Query the concept is expected to have
   */
  URI queryID;

  /**
   * Class the query is expected to have
   */
  String queryClass;

  /**
   * @param originator
   * @param concept
   * @param queryID
   */
  public ConceptDoesNotHaveQueryException(Process originator, URI concept, URI queryID) {
    super(originator);
    this.concept = concept;
    this.queryID = queryID;
    this.queryClass = null;
  }

  /**
   * @param originator
   * @param concept
   * @param queryID
   * @param queryClass
   */
  public ConceptDoesNotHaveQueryException(Process originator, URI concept, URI queryID, String queryClass) {
    this(originator, concept, queryID);
    this.queryClass = queryClass;
  }

  /**
   * <!-- getErrorMessage -->
   * 
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  protected String getErrorMessage() {
    if(queryClass == null) {
      return "Query ID " + queryID + " made of concept " + concept + " not declared to have it";
    }
    else {
      return "Query ID " + queryID + " of concept " + concept + " is not of the expected class " + queryClass;
    }
  }

}
