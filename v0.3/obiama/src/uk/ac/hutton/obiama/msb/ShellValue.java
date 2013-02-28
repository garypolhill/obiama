/*
 * uk.ac.hutton.obiama.msb: ShellValue.java
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

import java.util.Collection;
import java.util.Iterator;

import uk.ac.hutton.obiama.action.Process;
import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.MismatchedDuplicatedValueException;
import uk.ac.hutton.obiama.exception.ModificationOfLockedValueException;

/**
 * <!-- ShellValue -->
 * 
 * A {@link Value} to use for Values that are already registered. This class
 * allows read-only access to the registered Value's information.
 * 
 * @author Gary Polhill
 */
public class ShellValue<T> extends AbstractValue<T> implements Value<T> {
  /**
   * The value being shadowed by this ShellValue
   */
  Value<T> shadowValue;

  /**
   * The process using this ShellValue
   */
  Process process;

  AbstractVar var;

  /**
   * @param process The process using this ShellValue
   * @param value The value being shadowed
   */
  @SuppressWarnings("unchecked")
  ShellValue(Process process, AbstractValue<?> value) throws IntegrationInconsistencyException {
    super(value.getIndividual(), value.getAbstractVar().msb, value.process);
    var = value.getAbstractVar();
    this.process = process;
    try {
      this.shadowValue = (Value<T>)value;
    }
    catch(ClassCastException e) {
      throw new MismatchedDuplicatedValueException(value.process, value.getIndividual(), var.getURI());
    }
  }

  /**
   * <!-- update -->
   * 
   * There is a bug if this method is ever called--the MSB should not have a
   * ShellValue as a registered value to update.
   * 
   * @see uk.ac.hutton.obiama.msb.AbstractValue#update(uk.ac.hutton.obiama.msb.AbstractModelStateBroker)
   * @param msb
   * @throws IntegrationInconsistencyException
   */
  @Override
  void update(AbstractModelStateBroker msb) throws IntegrationInconsistencyException {
    throw new Bug();
  }

  /**
   * <!-- add -->
   * 
   * Attempt to modify locked value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#add(java.lang.Object)
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void add(T value) throws IntegrationInconsistencyException {
    throw new ModificationOfLockedValueException(process, individual.getURI(), var.getURI());
  }

  /**
   * <!-- add -->
   * 
   * Attempt to modify locked value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#add(java.lang.Object)
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void add(Instance value) throws IntegrationInconsistencyException {
    throw new ModificationOfLockedValueException(process, individual.getURI(), var.getURI());
  }

  /**
   * <!-- addString -->
   * 
   * Attempt to modify locked value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#addString(java.lang.String)
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void addString(String value) throws IntegrationInconsistencyException {
    throw new ModificationOfLockedValueException(process, individual.getURI(), var.getURI());
  }

  /**
   * <!-- clear -->
   * 
   * Attempt to modify locked value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#clear()
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void clear() throws IntegrationInconsistencyException {
    throw new ModificationOfLockedValueException(process, individual.getURI(), var.getURI());
  }

  /**
   * <!-- get -->
   * 
   * Forward to shadowed value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#get()
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public T get() throws IntegrationInconsistencyException {
    return shadowValue.get();
  }
  
  public String getString() throws IntegrationInconsistencyException {
    return shadowValue.getString();
  }

  /**
   * <!-- getAll -->
   * 
   * Forward to shadowed value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#getAll(java.util.Collection)
   * @param repository
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void getAll(Collection<T> repository) throws IntegrationInconsistencyException {
    shadowValue.getAll(repository);
  }
  
  public void getAllString(Collection<String> repository) throws IntegrationInconsistencyException {
    shadowValue.getAllString(repository);
  }

  /**
   * <!-- has -->
   * 
   * Forward to shadowed value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#has(java.lang.Object)
   * @param value
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public boolean has(T value) throws IntegrationInconsistencyException {
    return shadowValue.has(value);
  }

  /**
   * <!-- has -->
   * 
   * Forward to shadowed value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#has(java.lang.Object)
   * @param value
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public boolean has(Instance value) throws IntegrationInconsistencyException {
    return shadowValue.has(value);
  }

  /**
   * <!-- hasString -->
   * 
   * Forward to shadowed value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#hasString(java.lang.String)
   * @param value
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public boolean hasString(String value) throws IntegrationInconsistencyException {
    return shadowValue.hasString(value);
  }

  /**
   * <!-- nElements -->
   * 
   * Forward to shadowed value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#nElements()
   * @return
   */
  @Override
  public int nElements() {
    return shadowValue.nElements();
  }

  /**
   * <!-- remove -->
   * 
   * Attempt to modify locked value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#remove(java.lang.Object)
   * @param value
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public boolean remove(T value) throws IntegrationInconsistencyException {
    throw new ModificationOfLockedValueException(process, individual.getURI(), var.getURI());
  }

  /**
   * <!-- remove -->
   * 
   * Attempt to modify locked value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#remove(Instance)
   * @param value
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public boolean remove(Instance value) throws IntegrationInconsistencyException {
    throw new ModificationOfLockedValueException(process, individual.getURI(), var.getURI());
  }

  /**
   * <!-- removeString -->
   * 
   * Attempt to modify locked value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#removeString(java.lang.String)
   * @param value
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public boolean removeString(String value) throws IntegrationInconsistencyException {
    throw new ModificationOfLockedValueException(process, individual.getURI(), var.getURI());
  }

  /**
   * <!-- set -->
   * 
   * Attempt to modify locked value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#set(java.lang.Object)
   * @param value
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public T set(T value) throws IntegrationInconsistencyException {
    throw new ModificationOfLockedValueException(process, individual.getURI(), var.getURI());
  }

  /**
   * <!-- set -->
   * 
   * Attempt to modify locked value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#set(java.lang.Object)
   * @param value
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public T set(Instance value) throws IntegrationInconsistencyException {
    throw new ModificationOfLockedValueException(process, individual.getURI(), var.getURI());
  }

  /**
   * <!-- setString -->
   * 
   * Attempt to modify locked value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#setString(java.lang.String)
   * @param value
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public T setString(String value) throws IntegrationInconsistencyException {
    throw new ModificationOfLockedValueException(process, individual.getURI(), var.getURI());
  }

  /**
   * <!-- unset -->
   * 
   * Attempt to modify locked value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#unset()
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void unset() throws IntegrationInconsistencyException {
    throw new ModificationOfLockedValueException(process, individual.getURI(), var.getURI());
  }

  /**
   * <!-- iterator -->
   * 
   * Forward to shadowed value
   * 
   * @see java.lang.Iterable#iterator()
   * @return
   */
  @Override
  public Iterator<T> iterator() {
    return shadowValue.iterator();
  }

  /**
   * <!-- compareTo -->
   * 
   * Forward to shadowed value
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   * @param o
   * @return
   */
  @Override
  public int compareTo(T o) {
    return shadowValue.compareTo(o);
  }
  
  @Override
  public int compareToString(String arg) throws IntegrationInconsistencyException {
    return shadowValue.compareToString(arg);
  }

  /**
   * <!-- readOnly -->
   * 
   * @see uk.ac.hutton.obiama.msb.AbstractValue#readOnly()
   * @return <code>true</code>
   */
  @Override
  public boolean readOnly() {
    return true;
  }

  /**
   * <!-- getAbstractVar -->
   * 
   * @see uk.ac.hutton.obiama.msb.AbstractValue#getAbstractVar()
   * @return
   */
  @Override
  AbstractVar getAbstractVar() {
    return var;
  }

  /**
   * <!-- getVar -->
   * 
   * @see uk.ac.hutton.obiama.msb.AbstractValue#getVar()
   * @return
   */
  @Override
  public Var getVar() {
    return var;
  }
}
