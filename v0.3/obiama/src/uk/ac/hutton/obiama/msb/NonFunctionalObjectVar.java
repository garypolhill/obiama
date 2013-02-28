/*
 * uk.ac.hutton.obiama.msb: NonFunctionalObjectVar.java
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
package uk.ac.hutton.obiama.msb;

import java.net.URI;

import org.semanticweb.owl.model.OWLObjectProperty;

import uk.ac.hutton.obiama.action.Process;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.NeedFunctionalGotNonFunctionalPropertyRuntimeException;

/**
 * NonFunctionalObjectVar
 * 
 * A variable to store a non-functional object property
 * 
 * @author Gary Polhill
 */
public class NonFunctionalObjectVar extends AbstractObjectVar implements Var {

  /**
   * Constructor
   * 
   * @param process The action requesting the variable
   * @param msb The model state broker to use
   * @param property The property this var represents
   */
  NonFunctionalObjectVar(Process process, AbstractModelStateBroker msb, OWLObjectProperty property) {
    super(process, msb, property);
  }

  /**
   * <!-- getValueFor -->
   * 
   * Return a value of this property for a particular individual
   * 
   * @see uk.ac.hutton.obiama.msb.Var#getValueFor(java.net.URI)
   */
  @SuppressWarnings("unchecked")
  public Value<URI> getValueFor(URI individual) throws IntegrationInconsistencyException {
    return NonFunctionalObjectValue.manifest(individual, this);
  }

  /**
   * <!-- getExistingValueFor -->
   * 
   * Return an expected existing value of this property for a particular
   * individual
   * 
   * @see uk.ac.hutton.obiama.msb.Var#getValueFor(java.net.URI)
   */
  @SuppressWarnings("unchecked")
  public Value<URI> getExistingValueFor(URI individual) throws IntegrationInconsistencyException {
    return NonFunctionalObjectValue.manifest(individual, this, true);
  }

  /**
   * <!-- getNewValueFor -->
   * 
   * Return a value of this property for a particular individual to initialise
   * 
   * @see uk.ac.hutton.obiama.msb.Var#getNewValueFor(java.net.URI)
   */
  @SuppressWarnings("unchecked")
  public Value<URI> getNewValueFor(URI individual) throws IntegrationInconsistencyException {
    return NonFunctionalObjectValue.manifest(individual, this, false);
  }

  /**
   * <!-- isFunctional -->
   * 
   * Return false: this variable is non-functional
   * 
   * @see uk.ac.hutton.obiama.msb.Var#isFunctional()
   */
  public boolean isFunctional() {
    return false;
  }

  /**
   * <!-- isNonFunctional -->
   * 
   * Return true: this variable is non-functional
   * 
   * @see uk.ac.hutton.obiama.msb.Var#isNonFunctional()
   */
  public boolean isNonFunctional() {
    return true;
  }

  public int compare(Instance o1, Instance o2) {
    throw new NeedFunctionalGotNonFunctionalPropertyRuntimeException(process, property.getURI());
  }

}
