/*
 * uk.ac.hutton.obiama.exception: NonClassAccessedAsClassException.java
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
 * <!-- NonClassAccessedAsClassException -->
 * 
 * An integration inconsistency exception arising from an action requesting a
 * class by its URI, when the URI instead refers to some other kind of entity.
 * 
 * @author Gary Polhill
 */
public class NonClassAccessedAsClassException extends IntegrationInconsistencyException {

  /**
   * Serialisation number
   */
  private static final long serialVersionUID = 2045913499510195468L;

  /**
   * URI of entity causing the problem
   */
  URI entityURI;

  /**
   * @param entityURI URI of entity in model structure ontology expected to be a
   *          class but isn't
   * @param action Action during which the error occurred
   */
  public NonClassAccessedAsClassException(URI entityURI, Process action) {
    super(action);
    this.entityURI = entityURI;
  }

  /**
   * <!-- getErrorMessage -->
   * 
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  @Override
  protected String getErrorMessage() {
    return "entity " + entityURI
      + " accessed as a class, but is not asserted or inferred as such in the model structure ontology";
  }

}
