/*
 * uk.ac.hutton.obiama.msb: FunctionalDataValue.java
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.InconsistentRangeException;
import uk.ac.hutton.obiama.exception.IndividualAlreadyHasPropertyException;
import uk.ac.hutton.obiama.exception.IndividualDoesNotHavePropertyException;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ModificationOfReadOnlyValueException;
import uk.ac.hutton.obiama.exception.NeedFunctionalGotNonFunctionalPropertyException;
import uk.ac.hutton.obiama.exception.NeedObjectGotDataPropertyException;
import uk.ac.hutton.obiama.exception.UninitialisedValueException;
import uk.ac.hutton.obiama.model.Log;
import uk.ac.hutton.util.FloatingPointComparison;

/**
 * FunctionalDataValue<T>
 * 
 * Store the value of a functional data property. Types are handled using
 * generics.
 * 
 * @author Gary Polhill
 */
public class FunctionalDataValue<T> extends AbstractFunctionalValue<T> implements Value<T> {
  /**
   * The data value as presented to the caller, which can be changed
   */
  T stored;

  /**
   * The original data value as obtained from the ontology
   */
  T original;

  /**
   * The variable from which the data value was obtained
   */
  FunctionalDataVar var;

  /**
   * Comparator to use for floating point comparisons.
   */
  FloatingPointComparison fcmp;

  boolean writeOnly;

  static <U> Value<U> manifest(URI individual, FunctionalDataVar var) throws IntegrationInconsistencyException {
    return manifest(var.msb, new FunctionalDataValue<U>(individual, var));
  }

  static <U> Value<U> manifest(URI individual, FunctionalDataVar var, boolean existing)
      throws IntegrationInconsistencyException {
    return manifest(var.msb, new FunctionalDataValue<U>(individual, var, existing));
  }

  static <U> Value<U> manifest(URI individual, FunctionalDataVar var, FloatingPointComparison fcmp)
      throws IntegrationInconsistencyException {
    return manifest(var.msb, new FunctionalDataValue<U>(individual, var, fcmp));
  }

  static <U> Value<U> manifest(URI individual, FunctionalDataVar var, boolean existing, FloatingPointComparison fcmp)
      throws IntegrationInconsistencyException {
    return manifest(var.msb, new FunctionalDataValue<U>(individual, var, existing, fcmp));
  }

  /**
   * Basic constructor
   * 
   * @param individual
   * @param var
   * @throws IntegrationInconsistencyException
   */
  private FunctionalDataValue(URI individual, FunctionalDataVar var) throws IntegrationInconsistencyException {
    super(individual, var);
    this.var = var;
    Set<String> literalSet = var.msb.getDataPropertyValues(this.individual, var.property);
    if(literalSet == null || literalSet.size() == 0) {
      writeOnly = true;
      stored = null;
      original = null;
    }
    else {
      writeOnly = false;
      String valueStr = null;
      for(String str: literalSet) {
        if(valueStr == null) {
          valueStr = str;
        }
        else if(!valueStr.equals(str)) {
          throw new NeedFunctionalGotNonFunctionalPropertyException(var.process, var.getURI(), valueStr, str);
        }
      }
      try {
        stored = XSDHelper.instantiate(var.type, valueStr);
        original = XSDHelper.instantiate(var.type, valueStr);
      }
      catch(ClassCastException e) {
        throw new InconsistentRangeException(var.getURI(), var.getType().getURI(), null, var.process);
      }
    }
    fcmp = null;
  }

  /**
   * Constructor to use when it is known whether the value should exist or not
   * 
   * @param individual
   * @param var
   * @param existing <code>true</code> if the individual is expected to already
   *          have a value for the variable
   * @throws IntegrationInconsistencyException
   */
  private FunctionalDataValue(URI individual, FunctionalDataVar var, boolean existing)
      throws IntegrationInconsistencyException {
    this(individual, var);

    if(existing && writeOnly) throw new IndividualDoesNotHavePropertyException(var.process, individual,
        var.property.getURI());
    else if(!existing && !writeOnly)
      throw new IndividualAlreadyHasPropertyException(var.process, individual, var.property.getURI(),
          original.toString());
  }

  /**
   * Constructor for floating-point datatypes, allowing a comparison method to
   * be passed in.
   * 
   * @param individual
   * @param var
   * @param existing
   * @param fcmp
   * @throws IntegrationInconsistencyException
   */
  private FunctionalDataValue(URI individual, FunctionalDataVar var, boolean existing, FloatingPointComparison fcmp)
      throws IntegrationInconsistencyException {
    this(individual, var, existing);
    this.fcmp = fcmp;
  }

  /**
   * @param individual
   * @param functionalDataVar
   * @param fcmp
   * @throws IntegrationInconsistencyException
   */
  private FunctionalDataValue(URI individual, FunctionalDataVar var, FloatingPointComparison fcmp)
      throws IntegrationInconsistencyException {
    this(individual, var);
    this.fcmp = fcmp;
  }

  /**
   * <!-- update -->
   * 
   * Check if there has been a change to the type, and update the ontology if
   * there has
   * 
   * @throws IntegrationInconsistencyException
   * 
   * @see uk.ac.hutton.obiama.msb.AbstractValue#update(uk.ac.hutton.obiama.msb.AbstractModelStateBroker)
   */
  @Override
  void update(AbstractModelStateBroker msb) throws IntegrationInconsistencyException {
    if(original == null && stored == null) return;

    if((original == null && stored != null) || !original.equals(stored)) {
      if(var.readOnly()) {
        System.out.println("Original: " + original + "; stored: " + stored);
        throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(), var.property.getURI());
      }

      if(original != null)
        msb.removeDataPropertyAssertionValue((Action)var.process, individual, var.property, original, var.type);
      if(stored != null)
        msb.addDataPropertyAssertionValue((Action)var.process, individual, var.property, stored, var.type);
    }
  }

  /**
   * <!-- get -->
   * 
   * Return the stored value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#get()
   */
  public T get() throws IntegrationInconsistencyException {
    if(stored == null) {
      throw new UninitialisedValueException(var.process, individual.getURI(), var.property.getURI());
    }
    return stored;
  }

  public String getString() throws IntegrationInconsistencyException {
    return get().toString();
  }

  /**
   * <!-- set -->
   * 
   * Allow the stored value to be set
   * 
   * @see uk.ac.hutton.obiama.msb.Value#set(java.lang.Object)
   */
  public T set(T value) throws IntegrationInconsistencyException {
    if(var.readOnly()) {
      throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(), var.property.getURI());
    }

    T tmp = stored;
    stored = value;
    return tmp;
  }

  public T set(Instance value) throws IntegrationInconsistencyException {
    throw new NeedObjectGotDataPropertyException(var.process, var.property.getURI());
  }

  public T setString(String value) throws IntegrationInconsistencyException {
    if(var.readOnly()) {
      throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(), var.property.getURI());
    }

    T tmp = stored;
    stored = XSDHelper.instantiate(var.type, value);
    return tmp;
  }

  /**
   * <!-- unset -->
   * 
   * Unset the value
   * 
   * @see uk.ac.hutton.obiama.msb.Value#unset()
   * @throws IntegrationInconsistencyException
   */
  public void unset() throws IntegrationInconsistencyException {
    if(var.readOnly()) {
      throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(), var.property.getURI());
    }

    stored = null;
  }

  /**
   * <!-- compareTo -->
   * 
   * Permit comparisons of functional data values. Empty values always sort
   * first.
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public int compareTo(T o) {
    if(stored == null) return o == null ? 0 : -1;
    if(fcmp != null && (stored instanceof Double || stored instanceof Float) && o instanceof Number) {
      int result = fcmp.compare((Number)stored, (Number)o);
      Log.comparison(var, (Number)stored, (Object)o, result);
      return result;
    }
    if(stored instanceof Comparable && o instanceof Comparable) {
      return ((Comparable)stored).compareTo((Comparable)o);
    }
    else {
      return stored.toString().compareTo(o.toString());
    }
  }

  public int compareToString(String arg) throws IntegrationInconsistencyException {
    if(stored == null) {
      throw new UninitialisedValueException(var.process, individual.getURI(), var.property.getURI());
    }
    if(fcmp != null && (stored instanceof Double || stored instanceof Float)) {
      Number o;
      if(stored instanceof Double) {
        o = Double.parseDouble(arg);
      }
      else {
        o = Float.parseFloat(arg);
      }
      int result = fcmp.compare((Number)stored, o);
      Log.comparison(var, (Number)stored, (Object)o, result);
      return result;
    }
    else if(stored instanceof Double) {
      return ((Double)stored).compareTo(Double.parseDouble(arg));
    }
    else if(stored instanceof Float) {
      return ((Float)stored).compareTo(Float.parseFloat(arg));
    }
    else if(stored instanceof Integer) {
      return ((Integer)stored).compareTo(Integer.parseInt(arg));
    }
    else if(stored instanceof Long) {
      return ((Long)stored).compareTo(Long.parseLong(arg));
    }
    else if(stored instanceof Short) {
      return ((Short)stored).compareTo(Short.parseShort(arg));
    }
    else if(stored instanceof Byte) {
      return ((Byte)stored).compareTo(Byte.parseByte(arg));
    }
    else if(stored instanceof String) {
      return ((String)stored).compareTo(arg);
    }
    else if(stored instanceof URI) {
      return ((URI)stored).compareTo(URI.create(arg));
    }
    else if(stored instanceof Boolean) {
      return ((Boolean)stored).compareTo(Boolean.parseBoolean(arg));
    }
    else if(stored instanceof BigDecimal) {
      return ((BigDecimal)stored).compareTo(new BigDecimal(arg));
    }
    else if(stored instanceof BigInteger) {
      return ((BigInteger)stored).compareTo(new BigInteger(arg));
    }
    else if(stored instanceof javax.xml.datatype.XMLGregorianCalendar) {
      try {
        DatatypeFactory factory = DatatypeFactory.newInstance();
        return ((javax.xml.datatype.XMLGregorianCalendar)stored).compare(factory.newXMLGregorianCalendar(arg));
      }
      catch(DatatypeConfigurationException e) {
        throw new Bug(e.getMessage());
      }
    }
    else if(stored instanceof javax.xml.datatype.Duration) {
      try {
        DatatypeFactory factory = DatatypeFactory.newInstance();
        return ((javax.xml.datatype.Duration)stored).compare(factory.newDuration(arg));
      }
      catch(DatatypeConfigurationException e) {
        throw new Bug(e.getMessage());
      }
    }
    else {
      return stored.toString().compareTo(arg);
    }
  }

  /**
   * <!-- nElements -->
   * 
   * @see uk.ac.hutton.obiama.msb.Value#nElements()
   * @return <code>1</code> if the property has a value, <code>0</code>
   *         otherwise
   */
  public int nElements() {
    return stored == null ? 0 : 1;
  }

  public int compareTo(FunctionalDataValue<T> o) {
    return compareTo(o.stored);
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
