/*
 * uk.ac.hutton.obiama.exception: IndividualDoesNotHaveQueryException.java
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
import uk.ac.hutton.obiama.action.Query;

/**
 * <!-- IndividualDoesNotHaveQueryException -->
 * 
 * An exception caused when an individual is asked a query by a requester, but
 * has not been set up by the action to have that query.
 * 
 * @author Gary Polhill
 */
public class IndividualDoesNotHaveQueryException extends IntegrationInconsistencyException {
  /**
   * Serialisation number
   */
  private static final long serialVersionUID = 1516186268544272653L;

  /**
   * The query the individual is expected to have
   */
  Query<?> query;
  
  /**
   * The individual requesting the query
   */
  URI requester;
  
  /**
   * The individual that doesn't have the query, so cannot respond
   */
  URI individual;

  /**
   * @param originator The action during which the exception is caused
   * @param query The query expected to exist
   * @param requester The requester expecting the query to exist
   * @param individual The individual expected to be able to answer the query
   */
  public IndividualDoesNotHaveQueryException(Process originator, Query<?> query, URI requester, URI individual) {
    super(originator);
    this.query = query;
    this.requester = requester;
    this.individual = individual;
  }

  /**
   * <!-- getErrorMessage -->
   *
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  protected String getErrorMessage() {
    return "Individual " + individual + " does not have query " + query.getURI() + ", requested by " + requester;
  }

}
