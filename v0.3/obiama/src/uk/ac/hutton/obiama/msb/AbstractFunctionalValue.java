/*
 * uk.ac.hutton.obiama.msb: AbstractFunctionalValue.java
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
import java.util.Iterator;

import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.NeedNonFunctionalGotFunctionalPropertyException;
import uk.ac.hutton.obiama.exception.NeedNonFunctionalGotFunctionalPropertyRuntimeException;

/**
 * AbstractFunctionalValue
 * 
 * Abstract class for functional values. This essentially implements those
 * methods for non-functional value access by throwing an exception.
 * 
 * @author Gary Polhill
 */
public abstract class AbstractFunctionalValue<T> extends AbstractValue<T> {

  private URI uri;
  /**
   * Constructor.
   * 
   * @param individual The individual the variable is for
   * @param var The variable
   * @throws IntegrationInconsistencyException
   */
  AbstractFunctionalValue(URI individual, AbstractVar var) throws IntegrationInconsistencyException {
    super(individual, var.msb, var.process);
    uri = var.getURI();
  }

  /**
   * <!-- getAll -->
   * 
   * Throw an exception as a non-functional value access method has been called
   * for a functional value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#getAll(java.util.Collection)
   * @param repository
   * @throws IntegrationInconsistencyException
   */
  public void getAll(Collection<T> repository) throws IntegrationInconsistencyException {
    throw new NeedNonFunctionalGotFunctionalPropertyException(process, uri);
  }
  
  public void getAllString(Collection<String> repository) throws IntegrationInconsistencyException {
    throw new NeedNonFunctionalGotFunctionalPropertyException(process, uri);
  }

  /**
   * <!-- add -->
   * 
   * Throw an exception as a non-functional value access method has been called
   * for a functional value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#add(java.lang.Object)
   */
  public void add(T value) throws IntegrationInconsistencyException {
    throw new NeedNonFunctionalGotFunctionalPropertyException(process, uri);
  }
  
  /**
   * <!-- add -->
   *
   * Throw an exception as a non-functional value access method has been called
   * for a functional value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#add(uk.ac.hutton.obiama.msb.Instance)
   * @param value
   * @throws IntegrationInconsistencyException
   */
  public void add(Instance value) throws IntegrationInconsistencyException {
    throw new NeedNonFunctionalGotFunctionalPropertyException(process, uri);
  }

  /**
   * <!-- addString -->
   * 
   * Throw an exception is a non-functional value access method has been called
   * for a functional value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#addString(java.lang.String)
   */
  public void addString(String value) throws IntegrationInconsistencyException {
    throw new NeedNonFunctionalGotFunctionalPropertyException(process, uri);
  }

  /**
   * <!-- has -->
   * 
   * Throw an exception as a non-functional value access method has been called
   * for a functional value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#has(java.lang.Object)
   */
  public boolean has(T value) throws IntegrationInconsistencyException {
    throw new NeedNonFunctionalGotFunctionalPropertyException(process, uri);
  }

  /**
   * <!-- has -->
   * 
   * Throw an exception as a non-functional value access method has been called
   * for a functional value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#has(java.lang.Object)
   */
  public boolean has(Instance value) throws IntegrationInconsistencyException {
    throw new NeedNonFunctionalGotFunctionalPropertyException(process, uri);
  }

  /**
   * <!-- hasString -->
   * 
   * Throw an exception as a non-functional value access method has been called
   * for a functional value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#hasString(java.lang.String)
   */
  public boolean hasString(String value) throws IntegrationInconsistencyException {
    throw new NeedNonFunctionalGotFunctionalPropertyException(process, uri);
  }

  /**
   * <!-- remove -->
   * 
   * Throw an exception as a non-functional value access method has been called
   * for a functional value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#remove(java.lang.Object)
   */
  public boolean remove(T value) throws IntegrationInconsistencyException {
    throw new NeedNonFunctionalGotFunctionalPropertyException(process, uri);
  }
  
  /**
   * <!-- remove -->
   * 
   * Throw an exception as a non-functional value access method has been called
   * for a functional value
   *
   * @see uk.ac.hutton.obiama.msb.Value#remove(uk.ac.hutton.obiama.msb.Instance)
   * @param value
   * @throws IntegrationInconsistencyException
   */
  public boolean remove(Instance value) throws IntegrationInconsistencyException {
    throw new NeedNonFunctionalGotFunctionalPropertyException(process, uri);
  }

  /**
   * <!-- removeString -->
   * 
   * Throw an exception as a non-functional value access method has been called
   * for a functional value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#removeString(java.lang.String)
   */
  public boolean removeString(String value) throws IntegrationInconsistencyException {
    throw new NeedNonFunctionalGotFunctionalPropertyException(process, uri);
  }
  
  /**
   * <!-- clear -->
   * 
   * Throw an exception as a non-functinal value access method has been called
   *
   * @see uk.ac.hutton.obiama.msb.Value#clear()
   * @throws IntegrationInconsistencyException
   */
  public void clear() throws IntegrationInconsistencyException {
    throw new NeedNonFunctionalGotFunctionalPropertyException(process, uri);
  }

  /**
   * <!-- iterator -->
   * 
   * Throw an exception as a non-functional value access method has been called
   * for a functional value. Unfortunately because this is implementing a
   * Java-standard interface that doesn't declare an exception, the exception
   * has to be wrapped in a RuntimeException.
   * 
   * @see java.lang.Iterable#iterator()
   */
  public Iterator<T> iterator() {
    throw new NeedNonFunctionalGotFunctionalPropertyRuntimeException(process, uri);
  }

}
