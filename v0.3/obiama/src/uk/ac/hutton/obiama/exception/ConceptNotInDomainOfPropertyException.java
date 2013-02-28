/*
 * uk.ac.hutton.obiama.exception: ConceptNotInDomainOfPropertyException.java
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
 * <!-- ConceptNotInDomainOfPropertyException -->
 * 
 * An exception caused when a Var is assigned to a Concept by an action that is
 * not in its domain in the model structure ontology.
 * 
 * @author Gary Polhill
 */
public class ConceptNotInDomainOfPropertyException extends IntegrationInconsistencyException {
  /**
   * Serialisation number
   */
  private static final long serialVersionUID = 4612607947386840392L;

  /**
   * Concept being assigned a Var
   */
  URI concept;

  /**
   * Var property assumed to have Concept in its domain
   */
  URI property;

  /**
   * @param action The action causing the exception
   * @param concept The concept
   * @param property The property expected by the action to have the concept in
   *          its domain
   */
  public ConceptNotInDomainOfPropertyException(Process action, URI concept, URI property) {
    super(action);
    this.concept = concept;
    this.property = property;
  }

  /**
   * <!-- getErrorMessage -->
   *
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  protected String getErrorMessage() {
    return "Property " + property + " expected to have concept " + concept + " in its domain, but it doesn't";
  }

}
