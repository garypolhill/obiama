/*
 * uk.ac.hutton.obiama.msb: NonFunctionalDataVar.java
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

import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.action.Process;
import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.NeedFunctionalGotNonFunctionalPropertyRuntimeException;

/**
 * NonFunctionalDataVar
 * 
 * A variable containing a non-functional data property
 * 
 * @author Gary Polhill
 */
public class NonFunctionalDataVar extends AbstractDataVar implements Var {
  /**
   * Constructor
   * 
   * @param process The action requesting the variable
   * @param msb The model state broker
   * @param property The property this variable represents
   * @param range The XSD type of the range of this property
   * @throws IntegrationInconsistencyException 
   */
  NonFunctionalDataVar(Process process, AbstractModelStateBroker msb, OWLDataProperty property, XSDVocabulary range) throws IntegrationInconsistencyException {
    super(process, msb, property, range);
    if(this.property == null) throw new Bug();
  }

  /**
   * <!-- getValueFor -->
   * 
   * Return a value for the individual
   *
   * @see uk.ac.hutton.obiama.msb.Var#getValueFor(java.net.URI)
   * @param individual
   * @return
   * @throws IntegrationInconsistencyException
   */
  public <T> Value<T> getValueFor(URI individual) throws IntegrationInconsistencyException {
    return NonFunctionalDataValue.manifest(individual, this);
  }

  /**
   * <!-- getExistingValueFor -->
   * 
   * Return a value of this property for an individual, which is expected to be
   * present
   * 
   * @see uk.ac.hutton.obiama.msb.Var#getValueFor(java.net.URI)
   */
  public <T> Value<T> getExistingValueFor(URI individual) throws IntegrationInconsistencyException {
    return NonFunctionalDataValue.manifest(individual, this, true);
  }

  /**
   * <!-- getNewValueFor -->
   * 
   * Return a value of this property for an individual to initialise
   * 
   * @see uk.ac.hutton.obiama.msb.Var#getNewValueFor(java.net.URI)
   */
  public <T> Value<T> getNewValueFor(URI individual) throws IntegrationInconsistencyException {
    return NonFunctionalDataValue.manifest(individual, this, false);
  }

  /**
   * <!-- isFunctional -->
   * 
   * Return false: this property is non-functional
   * 
   * @see uk.ac.hutton.obiama.msb.Var#isFunctional()
   */
  public boolean isFunctional() {
    return false;
  }

  /**
   * <!-- isNonFunctional -->
   * 
   * Return true: this property is functional
   * 
   * @see uk.ac.hutton.obiama.msb.Var#isNonFunctional()
   */
  public boolean isNonFunctional() {
    return true;
  }

  /**
   * <!-- compare -->
   * 
   * Compare two instances against this property--throw an exception as this is
   * not possible
   * 
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   * @param o1
   * @param o2
   * @return (Throws an exception)
   */
  public int compare(Instance o1, Instance o2) {
    throw new NeedFunctionalGotNonFunctionalPropertyRuntimeException(process, property.getURI());
  }

}
