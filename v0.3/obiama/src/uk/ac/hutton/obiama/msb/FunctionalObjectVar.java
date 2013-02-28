/* uk.ac.hutton.obiama.msb: FunctionalObjectVar.java
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
import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.Panic;

/**
 * FunctionalObjectVar
 * 
 * A variable representing a functional object property
 * 
 * @author Gary Polhill
 */
public class FunctionalObjectVar extends AbstractObjectVar implements Var {

  /**
   * Constructor
   * 
   * @param process The action requesting the variable
   * @param msb The model state broker
   * @param property The property this variable represents
   */
  FunctionalObjectVar(Process process, AbstractModelStateBroker msb, OWLObjectProperty property) {
    super(process, msb, property);
  }
  
  /**
   * <!-- getValueFor -->
   *
   * Get the value for an individual
   *
   * @see uk.ac.hutton.obiama.msb.Var#getValueFor(java.net.URI)
   * @param individual The individual to get the value for
   * @return The value
   * @throws IntegrationInconsistencyException
   */
  @SuppressWarnings("unchecked")
  public AbstractValue<URI> getValueFor(URI individual) throws IntegrationInconsistencyException {
    return FunctionalObjectValue.manifest(individual, this);
  }

  /**
   * <!-- getExistingValueFor -->
   * 
   * Get an existing value for this variable for an individual
   * 
   * @see uk.ac.hutton.obiama.msb.Var#getValueFor(java.net.URI)
   */
  @SuppressWarnings("unchecked")
  public AbstractValue<URI> getExistingValueFor(URI individual) throws IntegrationInconsistencyException {
    return FunctionalObjectValue.manifest(individual, this, true);
  }

  /**
   * <!-- getNewValueFor -->
   * 
   * Get a value for this variable for an individual to initialise
   * 
   * @see uk.ac.hutton.obiama.msb.Var#getNewValueFor(java.net.URI)
   */
  @SuppressWarnings("unchecked")
  public AbstractValue<URI> getNewValueFor(URI individual) throws IntegrationInconsistencyException {
    return FunctionalObjectValue.manifest(individual, this, false);
  }

  /**
   * <!-- isFunctional -->
   * 
   * Return true: this property is functional
   * 
   * @see uk.ac.hutton.obiama.msb.Var#isFunctional()
   */
  public boolean isFunctional() {
    return true;
  }

  /**
   * <!-- isNonFunctional -->
   * 
   * Return false: this property is functional
   * 
   * @see uk.ac.hutton.obiama.msb.Var#isNonFunctional()
   */
  public boolean isNonFunctional() {
    return false;
  }

  /**
   * <!-- compare -->
   * 
   * Compare two functional datatype properties of individuals
   * 
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  public int compare(Instance arg0, Instance arg1) {
    try {
      AbstractValue<URI> value0 = getValueFor(arg0.getURI());
      AbstractValue<URI> value1 = getValueFor(arg1.getURI());
      return value0.get().compareTo(value1.get());
    }
    catch(IntegrationInconsistencyException e) {
      ErrorHandler.fatal(e, "comparing " + property.getURI() + " of individuals " + arg0.getURI() + " and "
        + arg1.getURI() + " in action " + process);
      throw new Panic();
    }
  }

}
