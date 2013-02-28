/*
 * uk.ac.hutton.obiama.msb: FunctionalObjectValue.java
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
import java.util.Set;

import org.semanticweb.owl.model.OWLIndividual;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.exception.BrokenUniqueNameAssumptionException;
import uk.ac.hutton.obiama.exception.IndividualAlreadyHasPropertyException;
import uk.ac.hutton.obiama.exception.IndividualDoesNotHavePropertyException;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ModificationOfReadOnlyValueException;
import uk.ac.hutton.obiama.exception.UninitialisedValueException;

/**
 * FunctionalObjectValue
 * 
 * Store a value from a functional object property, and allow it to be changed.
 * 
 * @author Gary Polhill
 */
public class FunctionalObjectValue extends AbstractFunctionalValue<URI> {
  /**
   * The value of the functional object property retrieved from the ontology
   */
  OWLIndividual original;

  /**
   * The value of the functional object property possibly changed by the calling
   * action
   */
  OWLIndividual stored;

  /**
   * Overridden var with appropriate type
   */
  FunctionalObjectVar var;

  /**
   * <code>true</code> if the value was not set from the ontology
   */
  boolean writeOnly;

  static AbstractValue<URI> manifest(URI individual, FunctionalObjectVar var)
      throws IntegrationInconsistencyException {
    return manifest(var.msb, new FunctionalObjectValue(individual, var));
  }

  static AbstractValue<URI> manifest(URI individual, FunctionalObjectVar var, boolean existing)
      throws IntegrationInconsistencyException {
    return manifest(var.msb, new FunctionalObjectValue(individual, var, existing));
  }

  /**
   * Basic constructor
   * 
   * @param individual
   * @param var
   * @throws IntegrationInconsistencyException
   */
  private FunctionalObjectValue(URI individual, FunctionalObjectVar var) throws IntegrationInconsistencyException {
    super(individual, var);
    this.var = var;
    Set<OWLIndividual> individuals = var.msb.getObjectPropertyValues(this.individual, var.property);
    if(individuals == null || individuals.size() == 0) {
      writeOnly = true;
      stored = null;
      original = null;
    }
    else {
      writeOnly = false;
      for(OWLIndividual ind: individuals) {
        original = ind;
      }
      stored = original;
      if(individuals.size() > 1) {
        throw new BrokenUniqueNameAssumptionException(var.process, individuals);
      }
    }

  }

  /**
   * Constructor when the status of the value is known
   * 
   * @param individual The individual
   * @param var The property of the individual to get
   * @param existing <code>true</code> if the individual is expected to have a
   *          value for the property already
   * @throws IntegrationInconsistencyException
   */
  private FunctionalObjectValue(URI individual, FunctionalObjectVar var, boolean existing)
      throws IntegrationInconsistencyException {
    this(individual, var);

    if(existing && writeOnly) throw new IndividualDoesNotHavePropertyException(var.process, individual,
        var.property.getURI());
    else if(!existing && !writeOnly)
      throw new IndividualAlreadyHasPropertyException(var.process, individual, var.property.getURI(),
          original.toString());
  }

  /**
   * <!-- get -->
   * 
   * Return the currently stored value of the object property
   * 
   * @see uk.ac.hutton.obiama.msb.Value#get()
   */
  public URI get() throws IntegrationInconsistencyException {
    if(stored == null)
      throw new UninitialisedValueException(var.process, individual.getURI(), var.property.getURI());
    return stored.getURI();
  }

  public String getString() throws IntegrationInconsistencyException {
    return get().toString();
  }

  /**
   * <!-- set -->
   * 
   * Allow the currently stored value of the object property to be set
   * 
   * @see uk.ac.hutton.obiama.msb.Value#set(java.lang.Object)
   */
  public URI set(URI value) throws IntegrationInconsistencyException {
    if(var.readOnly()) {
      throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(),
          var.property.getURI());
    }

    URI previous = stored == null ? null : stored.getURI();
    stored = var.msb.getIndividual(var.process, value);
    return previous;
  }

  /**
   * <!-- set -->
   * 
   * Allow the currently stored value to be set from the URI of an instance.
   * 
   * @see uk.ac.hutton.obiama.msb.Value#set(uk.ac.hutton.obiama.msb.Instance)
   * @param value
   * @return
   * @throws IntegrationInconsistencyException
   */
  public URI set(Instance value) throws IntegrationInconsistencyException {
    return set(value.getURI());
  }

  /**
   * <!-- setString -->
   * 
   * Allow the currently stored value of the object property to be set from a
   * String
   * 
   * @see uk.ac.hutton.obiama.msb.Value#setString(java.lang.String)
   */
  public URI setString(String value) throws IntegrationInconsistencyException {
    if(var.readOnly()) {
      throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(),
          var.property.getURI());
    }

    URI previous = stored == null ? null : stored.getURI();
    stored = var.msb.getIndividual(var.process, URI.create(value));
    return previous;
  }

  /**
   * <!-- unset -->
   * 
   * Remove the assertion that the individual has the relation
   * 
   * @see uk.ac.hutton.obiama.msb.Value#unset()
   * @throws IntegrationInconsistencyException
   */
  public void unset() throws IntegrationInconsistencyException {
    if(var.readOnly()) {
      throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(),
          var.property.getURI());
    }

    stored = null;
  }

  /**
   * <!-- update -->
   * 
   * If the value of the object property has been changed, then update the
   * ontology
   * 
   * @see uk.ac.hutton.obiama.msb.AbstractValue#update(uk.ac.hutton.obiama.msb.AbstractModelStateBroker)
   */
  @Override
  void update(AbstractModelStateBroker msb) throws IntegrationInconsistencyException {
    if(original == null && stored == null) return;

    if((original == null && stored != null) || !original.equals(stored)) {
      if(var.readOnly()) {
        throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(), var.property.getURI());
      }

      if(original != null) {
        msb.removeObjectPropertyAssertionValue((Action)var.process, individual, var.property, original);
      }

      if(stored != null) {
        msb.addObjectPropertyAssertionValue((Action)var.process, individual, var.property, stored);
      }
    }
  }

  /**
   * <!-- nElements -->
   * 
   * @see uk.ac.hutton.obiama.msb.Value#nElements()
   * @return <code>1</code> if the property has a value, <code>0</code> if not
   */
  public int nElements() {
    return stored == null ? 0 : 1;
  }

  /**
   * <!-- compareTo -->
   * 
   * Comparison is based on name. Empty values sort first.
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(URI o) {
    if(stored == null) return o == null ? 0 : -1;
    return stored.getURI().compareTo(o);
  }

  public int compareTo(FunctionalObjectValue o) {
    if(stored == null) return o.stored == null ? 0 : -1;
    return stored.getURI().compareTo(o.stored.getURI());
  }
  
  public int compareToString(String arg) throws IntegrationInconsistencyException {
    if(stored == null)
      throw new UninitialisedValueException(var.process, individual.getURI(), var.property.getURI());
    return stored.getURI().compareTo(URI.create(arg));
  }

  /**
   * <!-- getVar -->
   * 
   * @see uk.ac.hutton.obiama.msb.AbstractValue#getVar()
   * @return
   */
  public Var getVar() {
    return var;
  }

  AbstractVar getAbstractVar() {
    return var;
  }

  /**
   * <!-- readOnly -->
   * 
   * @see uk.ac.hutton.obiama.msb.AbstractValue#readOnly()
   * @return
   */
  public boolean readOnly() {
    return var.readOnly();
  }
}
