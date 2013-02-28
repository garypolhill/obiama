/*
 * uk.ac.hutton.obiama.exception: NonMutableIndividualClassException.java
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

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLIndividual;

import uk.ac.hutton.obiama.action.Action;

/**
 * NonMutableIndividualClassException
 * 
 * An attempt has been made to assert a class for a non-mutable individual
 * 
 * @author Gary Polhill
 */
public class NonMutableIndividualClassException extends
    IntegrationInconsistencyException {

  /**
   * Version ID for serialisation
   */
  private static final long serialVersionUID = -1462190733699413300L;

  /**
   * URI of the class being asserted for a non-mutable individual
   */
  URI cls;

  /**
   * URI of the non-mutable individual
   */
  URI individual;

  /**
   * Constructor
   * 
   * @param originator The action constituting the source of the problem
   * @param cls URI of the class that is being asserted for the individual
   * @param individual URI of the non-mutable individual
   */
  public NonMutableIndividualClassException(Action originator, URI cls,
      URI individual) {
    super(originator);
    this.cls = cls;
    this.individual = individual;
  }

  /**
   * Convenience constructor
   * 
   * @param originator The action constituting the source of the problem
   * @param cls The class being asserted for the individual
   * @param individual The non-mutable individual
   */
  public NonMutableIndividualClassException(Action originator, OWLClass cls,
      OWLIndividual individual) {
    this(originator, cls.getURI(), individual.getURI());
  }

  /**
   * <!-- getErrorMessage -->
   *
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  protected String getErrorMessage() {
    return "Attempt to set class of non-mutable individual " + individual + " to " + cls;
  }

}
