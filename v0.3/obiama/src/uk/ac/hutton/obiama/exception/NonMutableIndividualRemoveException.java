/* uk.ac.hutton.obiama.exception: NonMutableIndividualRemoveException.java
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

import org.semanticweb.owl.model.OWLIndividual;

import uk.ac.hutton.obiama.action.Action;

/**
 * NonMutableIndividualRemoveException
 * 
 * Exception caused through trying to delete a non-mutable individual
 * 
 * @author Gary Polhill
 */
public class NonMutableIndividualRemoveException extends
    IntegrationInconsistencyException {

  /**
   * Serial number
   */
  private static final long serialVersionUID = 5387362167317958782L;

  URI individual;
  
  /**
   * Constructor
   * 
   * @param originator The action attempting to remove the individual
   * @param individual URI of the non-mutable individual being removed
   */
  public NonMutableIndividualRemoveException(Action originator, URI individual) {
    super(originator);
    this.individual = individual;
  }
  
  /**
   * Convenience constructor
   * 
   * @param originator The action attempting to remove the individual
   * @param individual The non-mutable individual being removed
   */
  public NonMutableIndividualRemoveException(Action originator, OWLIndividual individual) {
    this(originator, individual.getURI());
  }

  /**
   * <!-- getErrorMessage -->
   *
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  protected String getErrorMessage() {
    return "Attempt to delete non-mutable individual " + individual;
  }
}
