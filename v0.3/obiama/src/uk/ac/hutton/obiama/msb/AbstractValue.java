/*
 * uk.ac.hutton.obiama.msb: AbstractValue.java
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

import org.semanticweb.owl.model.OWLIndividual;

import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.action.Process;

/**
 * AbstractValue
 * 
 * An abstract class for data values. This adds the update method, which is only
 * callable from within this package, to handle updating the ontology at the
 * right time.
 * 
 * @author Gary Polhill
 */
public abstract class AbstractValue<T> implements Value<T> {
  /**
   * Individual having the value
   */
  OWLIndividual individual;

  Process process;

  /**
   * <!-- manifest -->
   * 
   * Return the result of registering a value with the MSB.
   * 
   * @param msb
   * @param value
   * @return
   * @throws IntegrationInconsistencyException
   */
  protected static <U> AbstractValue<U> manifest(AbstractModelStateBroker msb, AbstractValue<U> value)
      throws IntegrationInconsistencyException {
    return msb.registerValue(value);
  }

  /**
   * Constructor initialising the individual from the URI. The individual must
   * exist in the ontology. This value must be registered with the model state
   * broker so that references to it are kept after the action has finished
   * working with it, whereupon it can be called to instigate any updates to the
   * ontology.
   * 
   * @param individual
   * @param msb
   * @param process
   * @throws IntegrationInconsistencyException
   */
  protected AbstractValue(URI individual, AbstractModelStateBroker msb, Process process)
      throws IntegrationInconsistencyException {
    this.process = process;
    this.individual = msb.getIndividual(process, individual);
  }

  /**
   * <!-- getVar -->
   * 
   * Return the variable
   * 
   * @see uk.ac.hutton.obiama.msb.Value#getVar()
   */
  public abstract Var getVar();

  /**
   * <!-- getIndividual -->
   * 
   * Return the individual
   * 
   * @see uk.ac.hutton.obiama.msb.Value#getIndividual()
   */
  public URI getIndividual() {
    return individual.getURI();
  }

  /**
   * <!-- readOnly -->
   * 
   * @return <code>true</code> iff this value is read-only (i.e. cannot be
   *         changed)
   */
  public abstract boolean readOnly();

  /**
   * <!-- update -->
   * 
   * This is called from the model state broker when it is time for any changes
   * in this value to be implemented in the ontology
   * 
   * @param msb
   * @throws IntegrationInconsistencyException
   */
  abstract void update(AbstractModelStateBroker msb) throws IntegrationInconsistencyException;

  /**
   * <!-- getAbstractVar -->
   *
   * @return The {@link AbstractVar} associated with this AbstractValue.
   */
   abstract AbstractVar getAbstractVar();
   
   /**
   * <!-- getProcess -->
   *
   * @return The {@link Process} associated with this AbstractValue.
   */
  Process getProcess() {
     return process;
   }
}
