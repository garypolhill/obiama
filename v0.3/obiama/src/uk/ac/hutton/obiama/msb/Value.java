/*
 * uk.ac.hutton.obiama.msb: Value.java
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
import java.util.Collection;

import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;

/**
 * Value
 * 
 * Responsible for handling access to data in the ontology from within Actions.
 * 
 * @author Gary Polhill
 */
public interface Value<T> extends Iterable<T>, Comparable<T> {
  /**
   * <!-- get -->
   * 
   * For <i>functional</i> properties, return a value of an appropriate type (in
   * the case of object properties, this will be URI). For <i>non-functional</i>
   * properties, throw an exception (to be specific, this is not the way to
   * access information about the value for non-functional properties.
   * 
   * @return The value of the (functional) property
   * @throws IntegrationInconsistencyException
   */
  public T get() throws IntegrationInconsistencyException;
  
  public String getString() throws IntegrationInconsistencyException;

  /**
   * <!-- getAll -->
   * 
   * For <i>non-functional</i> properties, fill the repository with all the
   * values (URIs in the case of object properties). For <i>functional</i>
   * properties, throw an exception.
   * 
   * @param repository
   * @throws IntegrationInconsistencyException 
   */
  public void getAll(Collection<T> repository) throws IntegrationInconsistencyException;
  
  public void getAllString(Collection<String> repository) throws IntegrationInconsistencyException;

  /**
   * <!-- set -->
   * 
   * Set the value (functional properties only).
   * 
   * @param value The value to set it to
   * @return The previous value of the variable
   * @throws IntegrationInconsistencyException
   */
  public T set(T value) throws IntegrationInconsistencyException;
  
  public T set(Instance value) throws IntegrationInconsistencyException;

  public T setString(String value) throws IntegrationInconsistencyException;
  
  /**
   * <!-- unset -->
   * 
   * Assert that the individual no longer has the (functional) property.
   *
   * @throws IntegrationInconsistencyException
   */
  public void unset() throws IntegrationInconsistencyException;

  /**
   * <!-- add -->
   * 
   * For <i>non-functional</i> properties, add a value of an appropriate type to
   * the set of literals or objects (when the type should be URI) forming the
   * values of this variable. For <i>functional</i> properties, throw an
   * exception.
   * 
   * @param value The value to add to the range of the non-functional property.
   * @throws IntegrationInconsistencyException
   */
  public void add(T value) throws IntegrationInconsistencyException;
  
  public void add(Instance value) throws IntegrationInconsistencyException;

  public void addString(String value) throws IntegrationInconsistencyException;

  /**
   * <!-- remove -->
   * 
   * Remove a value from the range of a (non-functional) property
   * 
   * @param value The value to remove (URI type for object properties)
   * @return <code>true</code> if the value contains the argument
   * @throws IntegrationInconsistencyException
   */
  public boolean remove(T value) throws IntegrationInconsistencyException;
  
  public boolean remove(Instance value) throws IntegrationInconsistencyException;

  public boolean removeString(String value) throws IntegrationInconsistencyException;
  
  /**
   * <!-- clear -->
   * 
   * Remove all values from the range of a (non-functional) property
   *
   * @throws IntegrationInconsistencyException
   */
  public void clear() throws IntegrationInconsistencyException;

  /**
   * <!-- has -->
   * 
   * Check whether a value is contained in the range of a (non-functional)
   * property
   * 
   * @param value The value to check
   * @return true iff the value is contained in the range of the variable
   * @throws IntegrationInconsistencyException
   */
  public boolean has(T value) throws IntegrationInconsistencyException;
  
  public boolean has(Instance value) throws IntegrationInconsistencyException;

  public boolean hasString(String value) throws IntegrationInconsistencyException;
  
  /**
   * <!-- compareToString -->
   * 
   * Compare the value to a string
   *
   * @param value
   * @return
   * @throws IntegrationInconsistencyException
   */
  public int compareToString(String value) throws IntegrationInconsistencyException;

  /**
   * <!-- getVar -->
   * 
   * @return The variable this value is a value of
   */
  public Var getVar();

  /**
   * <!-- getIndividual -->
   * 
   * @return The individual with the value
   */
  public URI getIndividual();
  
  /**
   * <!-- nElements -->
   *
   * @return The number of elements in the variable (for functional properties: 0 or 1)
   */
  public int nElements();

  /**
   * <!-- readOnly -->
   *
   * @return <code>true</code> iff the value is read-only
   */
  public boolean readOnly();
}
