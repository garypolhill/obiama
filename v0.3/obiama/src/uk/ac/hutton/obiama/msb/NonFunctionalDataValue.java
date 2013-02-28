/*
 * uk.ac.hutton.obiama.msb: NonFunctionalDataValue.java
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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.exception.InconsistentRangeException;
import uk.ac.hutton.obiama.exception.IndividualAlreadyHasPropertyException;
import uk.ac.hutton.obiama.exception.IndividualDoesNotHavePropertyException;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ModificationOfReadOnlyValueException;
import uk.ac.hutton.obiama.exception.NeedObjectGotDataPropertyException;

/**
 * NonFunctionalDataValue
 * 
 * A value storing the values for a non-functional data property that an
 * individual has. Generics are used to handle datatypes.
 * 
 * @author Gary Polhill
 */
public class NonFunctionalDataValue<T> extends AbstractNonFunctionalValue<T> {
  /**
   * Set of values retrieved from the ontology for comparison with those
   * manipulated by the action.
   */
  Set<T> original;

  /**
   * Set of values manipulated by the action
   */
  Set<T> stored;

  /**
   * Overridden var ivar with the appropriate type
   */
  NonFunctionalDataVar var;

  /**
   * <code>true</code> if there were no values initialised in the ontology
   */
  boolean writeOnly;

  static <U> Value<U> manifest(URI individual, NonFunctionalDataVar var) throws IntegrationInconsistencyException {
    return manifest(var.msb, new NonFunctionalDataValue<U>(individual, var));
  }

  static <U> Value<U> manifest(URI individual, NonFunctionalDataVar var, boolean existing)
      throws IntegrationInconsistencyException {
    return manifest(var.msb, new NonFunctionalDataValue<U>(individual, var, existing));
  }

  /**
   * Basic constructor. Uses LinkedHashSet to preserve any ordering in the
   * values returned by the ontology API.
   * 
   * @param individual Individual to get the value for
   * @param var The variable to get the value for
   * @throws IntegrationInconsistencyException
   */
  @SuppressWarnings("unchecked")
  private NonFunctionalDataValue(URI individual, NonFunctionalDataVar var) throws IntegrationInconsistencyException {
    super(individual, var);
    this.var = var;

    Set<String> values = var.msb.getDataPropertyValues(this.individual, var.property);
    // Use LinkedHashSet to preserve any ordering returned by the ontology
    original = new LinkedHashSet<T>();
    stored = new LinkedHashSet<T>();
    if(values == null || values.size() == 0) {
      writeOnly = true;
    }
    else {
      writeOnly = false;
      for(String value: values) {
        try {
          stored.add((T)XSDHelper.instantiate(var.type, value));
          original.add((T)XSDHelper.instantiate(var.type, value));
        }
        catch(ClassCastException e) {
          throw new InconsistentRangeException(var.property.getURI(), var.getType().getURI(), null,
              var.process);
        }
      }
    }

  }

  /**
   * Constructor when the status of the value is known
   * 
   * @param individual
   * @param var
   * @param existing <code>true</code> if the individual is expected to already
   *          have values for this property
   * @throws IntegrationInconsistencyException
   */
  private NonFunctionalDataValue(URI individual, NonFunctionalDataVar var, boolean existing)
      throws IntegrationInconsistencyException {
    this(individual, var);

    if(existing && writeOnly) throw new IndividualDoesNotHavePropertyException(var.process, individual,
        var.property.getURI());
    else if(!existing && !writeOnly)
      throw new IndividualAlreadyHasPropertyException(var.process, individual, var.property.getURI(),
          original.iterator().next().toString());
  }

  /**
   * <!-- update -->
   * 
   * Update the ontology for changes made to the set of values.
   * 
   * @throws IntegrationInconsistencyException
   * 
   * @see uk.ac.hutton.obiama.msb.AbstractValue#update(uk.ac.hutton.obiama.msb.AbstractModelStateBroker)
   */
  @Override
  void update(AbstractModelStateBroker msb) throws IntegrationInconsistencyException {
    if(original.size() == 0 && stored.size() == 0) return;

    // Assemble the changes, preserving ordering if possible
    Set<T> removed = new LinkedHashSet<T>();
    Set<T> added = new LinkedHashSet<T>();
    for(T original_item: original) {
      if(!stored.contains(original_item)) removed.add(original_item);
    }
    for(T current_item: stored) {
      if(!original.contains(current_item)) added.add(current_item);
    }

    if(var.readOnly() && (removed.size() > 0 || added.size() > 0)) {
      throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(),
          var.property.getURI());
    }

    // Make them
    for(T remove: removed) {
      msb.removeDataPropertyAssertionValue((Action)var.process, individual, var.property, remove, var.type);
    }
    for(T add: added) {
      msb.addDataPropertyAssertionValue((Action)var.process, individual, var.property, add, var.type);
    }
  }

  /**
   * <!-- add -->
   * 
   * Pass to stored
   * 
   * @see uk.ac.hutton.obiama.msb.Value#add(java.lang.Object)
   */
  public void add(T value) throws IntegrationInconsistencyException {
    if(var.readOnly()) {
      throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(),
          var.property.getURI());
    }

    stored.add(value);
  }

  /**
   * <!-- add -->
   * 
   * Throw an exception -- this method is only for use by object properties
   * 
   * @see uk.ac.hutton.obiama.msb.Value#add(uk.ac.hutton.obiama.msb.Instance)
   * @param value
   * @throws IntegrationInconsistencyException
   */
  public void add(Instance value) throws IntegrationInconsistencyException {
    throw new NeedObjectGotDataPropertyException(var.process, var.property.getURI());
  }

  /**
   * <!-- addString -->
   * 
   * Convert string to type and pass to stored
   * 
   * @see uk.ac.hutton.obiama.msb.Value#addString(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public void addString(String value) throws IntegrationInconsistencyException {
    if(var.readOnly()) {
      throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(),
          var.property.getURI());
    }

    stored.add((T)XSDHelper.instantiate(var.type, value));
  }

  /**
   * <!-- has -->
   * 
   * Pass to stored
   * 
   * @see uk.ac.hutton.obiama.msb.Value#has(java.lang.Object)
   */
  public boolean has(T value) throws IntegrationInconsistencyException {
    return stored.contains(value);
  }

  /**
   * <!-- has -->
   * 
   * Throw an exception -- this method should only be used for object properties
   * 
   * @see uk.ac.hutton.obiama.msb.Value#has(uk.ac.hutton.obiama.msb.Instance)
   * @param value
   * @throws IntegrationInconsistencyException
   */
  public boolean has(Instance value) throws IntegrationInconsistencyException {
    throw new NeedObjectGotDataPropertyException(var.process, var.property.getURI());
  }

  /**
   * <!-- hasString -->
   * 
   * Convert string to type and pass to stored
   * 
   * @see uk.ac.hutton.obiama.msb.Value#hasString(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public boolean hasString(String value) throws IntegrationInconsistencyException {
    return stored.contains((T)XSDHelper.instantiate(var.type, value));
  }

  /**
   * <!-- remove -->
   * 
   * Pass to stored
   * 
   * @see uk.ac.hutton.obiama.msb.Value#remove(java.lang.Object)
   */
  public boolean remove(T value) throws IntegrationInconsistencyException {
    if(var.readOnly()) {
      throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(),
          var.property.getURI());
    }

    return stored.remove(value);
  }

  /**
   * <!-- remove -->
   * 
   * Throw an exception -- this is just for object properties.
   * 
   * @see uk.ac.hutton.obiama.msb.Value#remove(uk.ac.hutton.obiama.msb.Instance)
   * @param value
   * @throws IntegrationInconsistencyException
   */
  public boolean remove(Instance value) throws IntegrationInconsistencyException {
    throw new NeedObjectGotDataPropertyException(var.process, var.property.getURI());
  }

  /**
   * <!-- removeString -->
   * 
   * Convert string to type and pass to stored
   * 
   * @see uk.ac.hutton.obiama.msb.Value#removeString(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public boolean removeString(String value) throws IntegrationInconsistencyException {
    if(var.readOnly()) {
      throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(),
          var.property.getURI());
    }

    return stored.remove((T)XSDHelper.instantiate(var.type, value));
  }

  /**
   * <!-- clear -->
   * 
   * Remove all entries from the property
   * 
   * @see uk.ac.hutton.obiama.msb.Value#clear()
   * @throws IntegrationInconsistencyException
   */
  public void clear() throws IntegrationInconsistencyException {
    if(var.readOnly()) {
      throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(),
          var.property.getURI());
    }

    stored.clear();
  }

  /**
   * <!-- nElements -->
   * 
   * @see uk.ac.hutton.obiama.msb.Value#nElements()
   * @return The number of elements
   */
  public int nElements() {
    return stored.size();
  }

  /**
   * <!-- iterator -->
   * 
   * Pass to stored
   * 
   * @see java.lang.Iterable#iterator()
   */
  public Iterator<T> iterator() {
    return stored.iterator();
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
