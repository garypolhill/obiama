/*
 * uk.ac.hutton.obiama.msb: AbstractNonFunctionalValue.java
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
import uk.ac.hutton.obiama.exception.NeedFunctionalGotNonFunctionalPropertyException;
import uk.ac.hutton.obiama.exception.NeedFunctionalGotNonFunctionalPropertyRuntimeException;

/**
 * AbstractNonFunctionalValue
 * 
 * Abstract class for non-functional values. This simply provides
 * implementations of functional value access methods that throw exceptions.
 * 
 * @author Gary Polhill
 */
public abstract class AbstractNonFunctionalValue<T> extends AbstractValue<T> {
  private URI uri;
  /**
   * Constructor
   * 
   * @param individual The individual the value is for
   * @param var The variable the individual is expected to have
   * @throws IntegrationInconsistencyException
   */
  AbstractNonFunctionalValue(URI individual, AbstractVar var) throws IntegrationInconsistencyException {
    super(individual, var.msb, var.process);
    uri = var.getURI();
  }

  /**
   * <!-- get -->
   * 
   * Throw an exception because a functional value access method has been called
   * for a non-functional value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#get()
   */
  public T get() throws IntegrationInconsistencyException {
    throw new NeedFunctionalGotNonFunctionalPropertyException(process, uri);
  }
  
  public String getString() throws IntegrationInconsistencyException {
    throw new NeedFunctionalGotNonFunctionalPropertyException(process, uri);
  }

  /**
   * <!-- getAll -->
   * 
   * Put all the values of the non-functional property in the repository
   * 
   * @see uk.ac.hutton.obiama.msb.Value#getAll(java.util.Collection)
   * @param repository
   */
  public void getAll(Collection<T> repository) {
    for(T obj: this) {
      repository.add(obj);
    }
  }
  
  public void getAllString(Collection<String> repository) {
    for(T obj: this) {
      repository.add(obj.toString());
    }
  }

  /**
   * <!-- set -->
   * 
   * Throw an exception because a functional value access method has been called
   * for a non-functional value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#set(java.lang.Object)
   */
  public T set(T value) throws IntegrationInconsistencyException {
    throw new NeedFunctionalGotNonFunctionalPropertyException(process, uri);
  }

  /**
   * <!-- set -->
   * 
   * Throw an exception because a functional value access method has been called
   * for a non-functional value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#set(java.lang.Object)
   */
  public T set(Instance value) throws IntegrationInconsistencyException {
    throw new NeedFunctionalGotNonFunctionalPropertyException(process, uri);
  }

  /**
   * <!-- setString -->
   * 
   * Throw an exception because a functional value access method has been called
   * for a non-functional value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#setString(java.lang.String)
   */
  public T setString(String value) throws IntegrationInconsistencyException {
    throw new NeedFunctionalGotNonFunctionalPropertyException(process, uri);
  }

  /**
   * <!-- unset -->
   * 
   * Throw an exception as a functional value access method has been called for
   * a non-functional value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#unset()
   * @throws IntegrationInconsistencyException
   */
  public void unset() throws IntegrationInconsistencyException {
    throw new NeedFunctionalGotNonFunctionalPropertyException(process, uri);
  }

  /**
   * <!-- compareTo -->
   * 
   * Throw an exception because non-functional values are not comparable
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(T o) {
    throw new NeedFunctionalGotNonFunctionalPropertyRuntimeException(process, uri);
  }
  
  public int compareToString(String arg) {
    throw new NeedFunctionalGotNonFunctionalPropertyRuntimeException(process, uri);
  }
}
