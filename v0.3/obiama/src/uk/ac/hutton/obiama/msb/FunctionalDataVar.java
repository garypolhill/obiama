/*
 * uk.ac.hutton.obiama.msb: FunctionalDataVar.java
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
import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.InconsistentRangeException;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.Panic;
import uk.ac.hutton.util.FloatingPointComparison;

/**
 * FunctionalDataVar
 * 
 * A variable representing a functional data property
 * 
 * @author Gary Polhill
 */
public class FunctionalDataVar extends AbstractDataVar implements Var {
  private FloatingPointComparison fcmp;

  /**
   * Constructor
   * 
   * @param process The action requesting the variable
   * @param msb The model state broker
   * @param property The property this variable represents
   * @param range XSD type of the range of this property
   * @throws IntegrationInconsistencyException 
   */
  FunctionalDataVar(Process process, AbstractModelStateBroker msb, OWLDataProperty property, XSDVocabulary range) throws IntegrationInconsistencyException {
    super(process, msb, property, range);
    fcmp = process.getFCmp(this);
    if(range != null &&(range.equals(XSDVocabulary.FLOAT) || range.equals(XSDVocabulary.DOUBLE))) {
      if(fcmp == null) fcmp = ObiamaSetUp.getFCmp();
    }
    else if(fcmp != null) {
      ErrorHandler.warn(new InconsistentRangeException(property.getURI(), getType().getURI(), XSDVocabulary.DOUBLE
          .getURI(), process), "getting value for data property " + property.getURI(),
          "the requested floating point comparison for this property will be ignored");
      fcmp = null;
    }
  }

  /**
   * <!-- getValueFor -->
   * 
   * Return the value of this variable for an individual, without stipulating
   * whether or not it should exist
   * 
   * @see uk.ac.hutton.obiama.msb.Var#getValueFor(java.net.URI)
   * @param individual
   * @return
   * @throws IntegrationInconsistencyException
   */
  public <T> Value<T> getValueFor(URI individual) throws IntegrationInconsistencyException {
    if(fcmp != null) return FunctionalDataValue.manifest(individual, this, fcmp);
    else
      return FunctionalDataValue.manifest(individual, this);
  }

  /**
   * <!-- getExistingValueFor -->
   * 
   * Return the value of this variable for an individual, which is expected to
   * exist already
   * 
   * @see uk.ac.hutton.obiama.msb.Var#getValueFor(java.net.URI)
   */
  public <T> Value<T> getExistingValueFor(URI individual) throws IntegrationInconsistencyException {
    if(fcmp != null) return FunctionalDataValue.manifest(individual, this, true, fcmp);
    else
      return FunctionalDataValue.manifest(individual, this, true);
  }

  /**
   * <!-- getNewValueFor -->
   * 
   * Return the value of this variable for an individual to initialise
   * 
   * @see uk.ac.hutton.obiama.msb.Var#getNewValueFor(java.net.URI)
   */
  public <T> Value<T> getNewValueFor(URI individual) throws IntegrationInconsistencyException {
    if(fcmp != null) return FunctionalDataValue.manifest(individual, this, false, fcmp);
    else
      return FunctionalDataValue.manifest(individual, this, false);
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
      Value<Object> value0 = getValueFor(arg0.getURI());
      Value<Object> value1 = getValueFor(arg1.getURI());
      return value0.compareTo(value1);
    }
    catch(IntegrationInconsistencyException e) {
      ErrorHandler.fatal(e, "comparing " + property.getURI() + " of individuals " + arg0.getURI() + " and "
        + arg1.getURI() + " in action " + process);
      throw new Panic();
    }
  }

}
