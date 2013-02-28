/*
 * uk.ac.hutton.obiama.msb: XSDHelper.java
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataComplementOf;
import org.semanticweb.owl.model.OWLDataOneOf;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataRangeRestriction;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLTypedConstant;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.Panic;
import uk.ac.hutton.util.Tree;

/**
 * XSDHelper
 * 
 * Provide assistance for handling XSD types.
 * 
 * @author Gary Polhill
 */
public class XSDHelper {
  /**
   * A public map to allow the XSDVocabulary to be returned from the URI
   */
  public static final Map<URI, XSDVocabulary> xsdTypes = new HashMap<URI, XSDVocabulary>();
  static {
    for(XSDVocabulary type: XSDVocabulary.values()) {
      xsdTypes.put(type.getURI(), type);
    }
  }

  /**
   * A tree reflecting the hierarchy of XSD Vocabulary types
   */
  private static Tree<XSDVocabulary> hierarchy = new Tree<XSDVocabulary>(XSDVocabulary.ANY_TYPE);
  static {
    hierarchy.add(XSDVocabulary.ANY_SIMPLE_TYPE, XSDVocabulary.ANY_TYPE);
    hierarchy.add(XSDVocabulary.ENTITIES, XSDVocabulary.ANY_TYPE);
    hierarchy.add(XSDVocabulary.IDREFS, XSDVocabulary.ANY_TYPE);
    // hierarchy.add(XSDVocabulary.NMTOKENS, XSDVocabulary.ANY_TYPE);
    XSDVocabulary[] simpleTypes =
      { XSDVocabulary.G_MONTH, XSDVocabulary.G_DAY, XSDVocabulary.G_MONTH_DAY, XSDVocabulary.G_YEAR,
        XSDVocabulary.G_YEAR_MONTH, XSDVocabulary.DATE, XSDVocabulary.TIME, XSDVocabulary.DATE_TIME,
        XSDVocabulary.DURATION, XSDVocabulary.STRING, XSDVocabulary.BOOLEAN, XSDVocabulary.BASE_64_BINARY,
        XSDVocabulary.HEX_BINARY, XSDVocabulary.FLOAT, XSDVocabulary.DECIMAL, XSDVocabulary.DOUBLE,
        XSDVocabulary.ANY_URI, XSDVocabulary.Q_NAME, XSDVocabulary.NOTATION };
    HashSet<XSDVocabulary> c = new HashSet<XSDVocabulary>();
    Collections.addAll(c, simpleTypes);
    hierarchy.addAll(c, XSDVocabulary.ANY_SIMPLE_TYPE);

    hierarchy.add(XSDVocabulary.NORMALIZED_STRING, XSDVocabulary.STRING);
    hierarchy.add(XSDVocabulary.TOKEN, XSDVocabulary.NORMALIZED_STRING);
    hierarchy.add(XSDVocabulary.LANGUAGE, XSDVocabulary.TOKEN);
    hierarchy.add(XSDVocabulary.NAME, XSDVocabulary.TOKEN);
    hierarchy.add(XSDVocabulary.NMTOKEN, XSDVocabulary.TOKEN);
    hierarchy.add(XSDVocabulary.NCNAME, XSDVocabulary.NAME);
    hierarchy.add(XSDVocabulary.ID, XSDVocabulary.NCNAME);
    hierarchy.add(XSDVocabulary.IDREF, XSDVocabulary.NCNAME);
    hierarchy.add(XSDVocabulary.ENTITY, XSDVocabulary.NCNAME);

    hierarchy.add(XSDVocabulary.INTEGER, XSDVocabulary.DECIMAL);
    hierarchy.add(XSDVocabulary.NON_POSIITIVE_INTEGER, XSDVocabulary.INTEGER);
    hierarchy.add(XSDVocabulary.LONG, XSDVocabulary.INTEGER);
    hierarchy.add(XSDVocabulary.NON_NEGATIVE_INTEGER, XSDVocabulary.INTEGER);
    hierarchy.add(XSDVocabulary.NEGATIVE_INTEGER, XSDVocabulary.NON_POSIITIVE_INTEGER);
    hierarchy.add(XSDVocabulary.POSITIVE_INTEGER, XSDVocabulary.NON_NEGATIVE_INTEGER);
    hierarchy.add(XSDVocabulary.UNSIGNED_LONG, XSDVocabulary.NON_NEGATIVE_INTEGER);
    hierarchy.add(XSDVocabulary.INT, XSDVocabulary.LONG);
    hierarchy.add(XSDVocabulary.UNSIGNED_INT, XSDVocabulary.UNSIGNED_LONG);
    hierarchy.add(XSDVocabulary.SHORT, XSDVocabulary.INT);
    // hierarchy.add(XSDVocabulary.UNSIGNED_SHORT, XSDVocabulary.UNSIGNED_INT);
    hierarchy.add(XSDVocabulary.BYTE, XSDVocabulary.SHORT);
    // hierarchy.add(XSDVocabulary.UNSIGNED_BYTE, XSDVocabulary.UNSIGNED_SHORT);
  }

  /**
   * Disable constructor
   */
  private XSDHelper() {
  }

  /**
   * <!-- datatypeCompatible -->
   * 
   * Determine whether an object and its type can be stored in a data property
   * with the specified type
   * 
   * @param propertyType the type of the OWL data property in which the object
   *          is to be stored
   * @param value the object to be stored in the OWL data property
   * @param type the type of the object to be stored in the OWL data property
   * @return
   */
  public static boolean datatypeCompatible(XSDVocabulary propertyType, Object value, XSDVocabulary type) {
    // Deal with the trivial case: the property can store the type
    if(datatypeContains(propertyType, type)) return true;

    // Handle various specific cases where the type of the object is more
    // general than the type of the property--here, can the object's value still
    // be stored in the property's type?
    switch(propertyType) {
    case STRING:
      return value.toString() != null;
    case BOOLEAN:
      if(datatypeContains(XSDVocabulary.DECIMAL, type)) {
        return integerValueInRange(value, 0L, 1L);
      }
      else if(value instanceof String) {
        if(integerValueInRange(value, 0L, 1L)) return true;
        return value.equals("true") || value.equals("false");
      }
      else if(datatypeContains(XSDVocabulary.STRING, type)) {
        if(integerValueInRange(value, 0L, 1L)) return true;
        return value.toString().equals("true") || value.toString().equals("false");
      }
      else
        return false;
    case BYTE:
      return integerValueInRange(value, (long)Byte.MIN_VALUE, (long)Byte.MAX_VALUE);
    case SHORT:
      return integerValueInRange(value, (long)Short.MIN_VALUE, (long)Short.MAX_VALUE);
    case INT:
      return integerValueInRange(value, (long)Integer.MIN_VALUE, (long)Integer.MAX_VALUE);
    case LONG:
      return integerValueInRange(value, Long.MIN_VALUE, Long.MAX_VALUE);
    case UNSIGNED_INT:
      return integerValueInRange(value, 0L, (long)Integer.MAX_VALUE);
    case UNSIGNED_LONG:
      return integerValueInRange(value, 0L, Long.MAX_VALUE);
    case POSITIVE_INTEGER:
      return integerValueInRange(value, BigInteger.ONE, null);
    case NEGATIVE_INTEGER:
      return integerValueInRange(value, null, BigInteger.ZERO.subtract(BigInteger.ONE));
    case NON_POSIITIVE_INTEGER:
      return integerValueInRange(value, null, BigInteger.ZERO);
    case NON_NEGATIVE_INTEGER:
      return integerValueInRange(value, BigInteger.ZERO, null);
    case FLOAT:
      if(decimalValueDataLoss(value, 0.0F)) return false;
      return decimalValueInRange(value, (double)Float.MIN_VALUE, (double)Float.MAX_VALUE);
    case DOUBLE:
      if(decimalValueDataLoss(value, 0.0)) return false;
      return decimalValueInRange(value, Double.MIN_VALUE, Double.MAX_VALUE);
    default:
      return false;
    }
  }

  /**
   * <!-- integerValueInRange -->
   * 
   * Return whether or not a value is in the specified range
   * 
   * @param value the value to check
   * @param min the minimum of the range, or null if there is no minimum
   * @param max the maximum of the range, or null if there is no maximum
   * @return true if the value is within the range
   */
  private static boolean integerValueInRange(BigInteger value, BigInteger min, BigInteger max) {
    boolean minok = (min == null) ? true : false;
    boolean maxok = (max == null) ? true : false;
    if(value instanceof BigInteger) {
      if(min != null) {
        minok = ((BigInteger)value).compareTo(min) >= 0;
      }
      if(max != null) {
        maxok = ((BigInteger)value).compareTo(max) <= 0;
      }
    }
    return minok && maxok;
  }

  /**
   * <!-- integerValueInRange -->
   * 
   * Convenience method with long min/max and general value
   * 
   * @param value the value to check
   * @param min the minimum of the range or null if there is no minimum
   * @param max the maximum of the range or null if there is no maximum
   * @return true if the value is within the range
   */
  private static boolean integerValueInRange(final Object value, final Long min, final Long max) {
    return integerValueInRange(value, min == null ? null : BigInteger.valueOf(min), max == null ? null : BigInteger
        .valueOf(max));
  }

  /**
   * <!-- integerValueInRange -->
   * 
   * Convenience method with general value. The latter is converted to a
   * BigInteger according to its type
   * 
   * @param value the value to check
   * @param min the minimum of the range or null if there is no minimum
   * @param max the maximum of the range or null if there is no maximum
   * @return true if the value is within the range
   */
  private static boolean integerValueInRange(final Object value, final BigInteger min, final BigInteger max) {
    if(value instanceof Number) {
      if(value instanceof BigInteger) return integerValueInRange((BigInteger)value, min, max);
      else if(value instanceof BigDecimal) return integerValueInRange((BigDecimal)value, min, max);
      else if(value instanceof Double) return integerValueInRange((Double)value, min, max);
      else if(value instanceof Float) return integerValueInRange((Double)value, min, max);
      else
        return integerValueInRange((Long)value, min, max);
    }
    else if(value instanceof String) {
      if(!Pattern.matches("[+-]?\\d+", (String)value)) return false;
      BigInteger bint = new BigInteger((String)value);
      return integerValueInRange(bint, min, max);
    }
    else
      return integerValueInRange(value.toString(), min, max);
  }

  /**
   * <!-- integerValueInRange -->
   * 
   * Convenience method with long value.
   * 
   * @param value the value to check
   * @param min the minimum of the range or null if there is no minimum
   * @param max the maximum of the range or null of there is no maximum
   * @return true if the value is within the range
   */
  private static boolean integerValueInRange(final Long value, final BigInteger min, final BigInteger max) {
    return integerValueInRange(BigInteger.valueOf(value), min, max);
  }

  /**
   * <!-- integerValueInRange -->
   * 
   * Convenience method with double value
   * 
   * @param value the value to check
   * @param min the minimum of the range or null if there is no minimum
   * @param max the maximum of the range or null if there is no maximum
   * @return true if the value is within the range
   */
  private static boolean integerValueInRange(final Double value, final BigInteger min, final BigInteger max) {
    return integerValueInRange(BigDecimal.valueOf(value), min, max);
  }

  /**
   * <!-- integerValueInRange -->
   * 
   * Convenience method with BigDecimal value. Relies on ArithmeticException to
   * catch the case when the BigDecimal is not storing an integer value.
   * 
   * @param value the value to check
   * @param min the minimum of the range or null if there is no minimum
   * @param max the maximum of the range or null if there is no maximum
   * @return true if the value is within the range
   */
  private static boolean integerValueInRange(final BigDecimal value, final BigInteger min, final BigInteger max) {
    try {
      BigInteger bint = value.toBigIntegerExact();
      return integerValueInRange(bint, min, max);
    }
    catch(ArithmeticException e) {
      return false;
    }
  }

  /**
   * <!-- decimalValueInRange -->
   * 
   * Check if a value lies within a 'real' number range
   * 
   * @param value the value to check
   * @param min the minimum of the range or null if there is no minimum
   * @param max the maximum of the range or null if there is no maximum
   * @return true if the value is within the range
   */
  private static boolean decimalValueInRange(final BigDecimal value, final BigDecimal min, final BigDecimal max) {
    boolean minok = (min == null) ? true : false;
    boolean maxok = (max == null) ? true : false;
    if(min != null) {
      minok = value.compareTo(min) >= 0;
    }
    if(max != null) {
      maxok = value.compareTo(max) <= 0;
    }
    return minok && maxok;
  }

  /**
   * <!-- decimalValueInRange -->
   * 
   * Convenience method with general value and double range
   * 
   * @param value the value to check
   * @param min the minimum of the range or null if there is no minimum
   * @param max the maximum of the range or null if there is no maximum
   * @return true if the value is within the range
   */
  private static boolean decimalValueInRange(final Object value, final Double min, final Double max) {
    return decimalValueInRange(value, min == null ? null : BigDecimal.valueOf(min), max == null ? null : BigDecimal
        .valueOf(max));
  }

  /**
   * <!-- decimalValueInRange -->
   * 
   * Convenience method with general value. The latter is converted to a
   * BigDecimal according to its type.
   * 
   * @param value the value to check
   * @param min the minimum of the range or null if there is no minimum
   * @param max the maximum of the range or null if there is no maximum
   * @return true if the value is within the range
   */
  private static boolean decimalValueInRange(final Object value, final BigDecimal min, final BigDecimal max) {
    if(value instanceof Number) {
      if(value instanceof BigInteger) return decimalValueInRange((BigInteger)value, min, max);
      else if(value instanceof BigDecimal) return decimalValueInRange((BigDecimal)value, min, max);
      else if(value instanceof Double) return decimalValueInRange((Double)value, min, max);
      else if(value instanceof Float) return decimalValueInRange(new Double((Float)value), min, max);
      else
        return decimalValueInRange((Long)value, min, max);
    }
    else if(value instanceof String) {
      try {
        BigDecimal bdec = new BigDecimal((String)value);
        return decimalValueInRange(bdec, min, max);
      }
      catch(ArithmeticException e) {
        return false;
      }
      catch(NumberFormatException e) {
        return false;
      }
    }
    else
      return decimalValueInRange(value.toString(), min, max);
  }

  /**
   * <!-- decimalValueInRange -->
   * 
   * Convenience method with BigInteger value
   * 
   * @param value the value to check
   * @param min the minimum of the range or null if there is no minimum
   * @param max the maximum of the range or null if there is no maximum
   * @return true if the value is within the range
   */
  private static boolean decimalValueInRange(final BigInteger value, final BigDecimal min, final BigDecimal max) {
    return decimalValueInRange(new BigDecimal(value), min, max);
  }

  /**
   * <!-- decimalValueInRange -->
   * 
   * Convenience method with double value
   * 
   * @param value the value to check
   * @param min the minimum of the range or null if there is no minimum
   * @param max the maximum of the range or null if there is no maximum
   * @return true if the value is within the range
   */
  private static boolean decimalValueInRange(final Double value, final BigDecimal min, final BigDecimal max) {
    return decimalValueInRange(BigDecimal.valueOf(value), min, max);
  }

  /**
   * <!-- decimalValueInRange -->
   * 
   * Convenience method with long value
   * 
   * @param value the value to check
   * @param min the minimum of the range or null if there is no minimum
   * @param max the maximum of the range or null if there is no maximum
   * @return true if the value is within the range
   */
  private static boolean decimalValueInRange(final Long value, final BigDecimal min, final BigDecimal max) {
    return decimalValueInRange(BigDecimal.valueOf(value), min, max);
  }

  /**
   * <!-- decimalValueDataLoss -->
   * 
   * Check if a value can be stored in a number without loss of information.
   * 
   * @param value the value to be checked
   * @param store the store in which it is to be put
   * @return <code>true</code> if there is loss of data storing value in store
   */
  private static boolean decimalValueDataLoss(final Object value, final Number store) {
    if(value instanceof Number) {
      if(value instanceof BigInteger) return decimalValueDataLoss((BigInteger)value, store);
      else if(value instanceof BigDecimal) return decimalValueDataLoss((BigDecimal)value, store);
      else if(value instanceof Double) return decimalValueDataLoss((Double)value, store);
      else if(value instanceof Float) return decimalValueDataLoss(new Double((Float)value), store);
      else
        return decimalValueDataLoss((Long)value, store);
    }
    else if(value instanceof String) {
      try {
        BigDecimal bdec = new BigDecimal((String)value);
        return decimalValueDataLoss(bdec, store);
      }
      catch(ArithmeticException e) {
        return false;
      }
      catch(NumberFormatException e) {
        return false;
      }
    }
    else
      return decimalValueDataLoss(value.toString(), store);
  }

  /**
   * <!-- decimalValueDataLoss -->
   * 
   * Check if a float or double store can be used for a BigDecimal value without
   * loss of information. An IllegalArgumentException is thrown if the store is
   * not a double or float.
   * 
   * @param value the BigDecimal value
   * @param store the float or double store
   * @return <code>true</code> if there is loss of data storing value in store
   * @throws IllegalArgumentException
   */
  private static boolean decimalValueDataLoss(final BigDecimal value, final Number store) {
    if(store instanceof Float) {
      float st = (Float)store;
      st = value.floatValue();
      if(Float.isInfinite(st) || Float.isNaN(st)) return true;
      BigDecimal stvalue = BigDecimal.valueOf(st);
      return stvalue.compareTo(value) != 0;
    }
    else if(store instanceof Double) {
      double st = (Double)store;
      st = value.doubleValue();
      if(Double.isInfinite(st) || Double.isNaN(st)) return true;
      BigDecimal stvalue = BigDecimal.valueOf(st);
      return stvalue.compareTo(value) != 0;
    }
    else
      throw new IllegalArgumentException(store.getClass().getName());
  }

  /**
   * <!-- decimalValueDataLoss -->
   * 
   * Convenience method with Double value
   * 
   * @param value the value to check
   * @param store the store in which it is to be put
   * @return <code>true</code> if there is loss of data storing value in store
   */
  private static boolean decimalValueDataLoss(final Double value, final Number store) {
    return decimalValueDataLoss(BigDecimal.valueOf(value), store);
  }

  /**
   * <!-- decimalValueDataLoss -->
   * 
   * Convenience method with Long value
   * 
   * @param value the value to check
   * @param store the store in which it is to be put
   * @return <code>true</code> if there is loss of data storing value in store
   */
  private static boolean decimalValueDataLoss(final Long value, final Number store) {
    return decimalValueDataLoss(BigDecimal.valueOf(value), store);
  }

  /**
   * <!-- datatypeContains -->
   * 
   * Check whether toType is a type that contain fromType (i.e. it is more
   * general than or the same as the fromType)
   * 
   * @param toType the type of a proposed store
   * @param fromType the type of a proposed value
   * @return true if the store type is more general than or equal to the value
   *         type
   */
  public static boolean datatypeContains(XSDVocabulary toType, XSDVocabulary fromType) {
    return toType.equals(fromType) || hierarchy.getSuperNodes(fromType).contains(toType);
  }

  /**
   * <!-- recommendedClassFor -->
   * 
   * Return a recommended class, instances of which are suitable for storing the
   * given XSD type. Throws an IllegalArgumentException if the type is not
   * recognised.
   * 
   * @param type the type for which a recommended class is sought
   * @return a recommended class for the type
   * @throws IllegalArgumentException
   */
  @SuppressWarnings("unchecked")
  public static <T> Class<T> recommendedClassFor(XSDVocabulary type) {
    switch(type) {
    case ANY_SIMPLE_TYPE:
    case ANY_TYPE:
    case BASE_64_BINARY:
    case ENTITIES:
    case ENTITY:
    case HEX_BINARY:
    case ID:
    case IDREF:
    case IDREFS:
    case LANGUAGE:
    case NAME:
    case NCNAME:
    case NMTOKEN:
    case NORMALIZED_STRING:
    case NOTATION:
    case STRING:
    case TOKEN:
      // String has a String constructor
      return (Class<T>)String.class;
    case ANY_URI:
      // URI has a String constructor throwing URISyntaxException
      // URISyntaxException is a subclass of Exception
      return (Class<T>)URI.class;
    case BOOLEAN:
      // Boolean has a String constructor
      return (Class<T>)Boolean.class;
    case BYTE:
      // Byte has a String constructor throwing NumberFormatException
      // NumberFormatException is a subclass of IllegalArgumentException
      return (Class<T>)Byte.class;
    case DATE:
    case DATE_TIME:
    case TIME:
      // To build one of these, you need an instance of DatatypeFactory,
      // with an implementation of the newXMLGregorianCalendar(String)
      // method. Get the instance by calling DatatypeFactory.newInstance()
      return (Class<T>)javax.xml.datatype.XMLGregorianCalendar.class;
    case DECIMAL:
      // Has a String constructor
      return (Class<T>)java.math.BigDecimal.class;
    case DOUBLE:
      // Has a String constructor throwing NumberFormatException
      return (Class<T>)Double.class;
    case DURATION:
      // To build one of these you need an instance of DatatypeFactory,
      // with an implementation of the newDuration(String) method
      return (Class<T>)javax.xml.datatype.Duration.class;
    case FLOAT:
      // Has a String constructor throwing NumberFormatException
      return (Class<T>)Float.class;
    case G_DAY:
    case G_MONTH:
    case G_MONTH_DAY:
    case G_YEAR:
    case G_YEAR_MONTH:
      return (Class<T>)javax.xml.datatype.XMLGregorianCalendar.class;
    case INT:
      // Has a String constructor throwing NumberFormatException
      return (Class<T>)Integer.class;
    case INTEGER:
    case NEGATIVE_INTEGER:
    case NON_NEGATIVE_INTEGER:
    case NON_POSIITIVE_INTEGER:
    case POSITIVE_INTEGER:
    case UNSIGNED_LONG:
      // Has a String constructor
      return (Class<T>)java.math.BigInteger.class;
    case LONG:
    case UNSIGNED_INT:
      // Has a String constructor throwing NumberFormatException
      return (Class<T>)Long.class;
    case Q_NAME:
      // Has a String constructor, expected to be the 'localPart'
      return (Class<T>)javax.xml.namespace.QName.class;
    case SHORT:
      // Has a String constructor throwing NumberFormatException
      return (Class<T>)Short.class;
    default:
      throw new IllegalArgumentException("XSDVocabulary datatype not recognised: " + type.toString());
    }

  }

  public static <T> T instantiate(final OWLDataType type, final String value) {
    return instantiate(type.getURI(), value);
  }
  
  public static <T> T instantiate(final URI type, final String value) {
    return instantiate(xsdTypes.get(type), value);
  }
  
  /**
   * <!-- instantiate -->
   * 
   * Return an object as an instance of the recommended class for type,
   * initialised to the given String value
   * 
   * @param type the type to use as a basis for determining the class of the
   *          instance
   * @param value an initial value for the instance
   * @return the instance
   */
  @SuppressWarnings("unchecked")
  public static <T> T instantiate(final XSDVocabulary type, final String value) {
    Class<T> c = recommendedClassFor(type);
    // Handle classes returned from recommendedClassFor() without a String
    // constructor
    if(c == javax.xml.datatype.XMLGregorianCalendar.class || c == javax.xml.datatype.Duration.class) {
      try {
        DatatypeFactory factory = DatatypeFactory.newInstance();
        return (T)(c == javax.xml.datatype.XMLGregorianCalendar.class ? factory.newXMLGregorianCalendar(value)
                                                                     : factory.newDuration(value));
      }
      catch(DatatypeConfigurationException e) {
        throw new IllegalArgumentException(value, e);
      }
    }
    // Handle classes with a String constructor
    try {
      Constructor<T> cons = c.getConstructor(String.class);
      return cons.newInstance(value);
    }
    // Exceptions throwing a Bug are because the bestClassFor() method must have
    // returned an invalid class, unless otherwise stated
    catch(SecurityException e) {
      throw new Bug();
    }
    catch(NoSuchMethodException e) {
      // The class being created doesn't have a constructor with a String
      // argument
      throw new Bug();
    }
    catch(IllegalArgumentException e) {
      throw new IllegalArgumentException(value, e);
    }
    catch(InstantiationException e) {
      throw new Bug();
    }
    catch(IllegalAccessException e) {
      throw new Bug();
    }
    catch(InvocationTargetException e) {
      throw new Bug();
    }
  }

  /**
   * getTypeFor
   * 
   * @param value
   * @return
   */
  public static XSDVocabulary getTypeFor(final Object value) {
    return getTypeFor(value.getClass());
  }

  public static XSDVocabulary getTypeFor(final Class<?> value) {
    if(value == URI.class) {
      return XSDVocabulary.ANY_URI;
    }
    else if(value == BigDecimal.class) {
      return XSDVocabulary.DECIMAL;
    }
    else if(value == BigInteger.class) {
      return XSDVocabulary.INTEGER;
    }
    else if(value == Long.class) {
      return XSDVocabulary.LONG;
    }
    else if(value == Integer.class) {
      return XSDVocabulary.INT;
    }
    else if(value == Short.class) {
      return XSDVocabulary.SHORT;
    }
    else if(value == Byte.class) {
      return XSDVocabulary.BYTE;
    }
    else if(value == Float.class) {
      return XSDVocabulary.FLOAT;
    }
    else if(value == Double.class) {
      return XSDVocabulary.DOUBLE;
    }
    else if(value == Date.class) {
      return XSDVocabulary.DATE_TIME;
    }
    else if(value == javax.xml.datatype.XMLGregorianCalendar.class) {
      return XSDVocabulary.DATE_TIME;
    }
    else if(value == javax.xml.datatype.Duration.class) {
      return XSDVocabulary.DURATION;
    }
    else if(value == javax.xml.namespace.QName.class) {
      return XSDVocabulary.Q_NAME;
    }
    else if(value == Boolean.class) {
      return XSDVocabulary.BOOLEAN;
    }
    else if(value == String.class) {
      return XSDVocabulary.STRING;
    }
    else
      return XSDVocabulary.ANY_TYPE;
  }

  /**
   * getSuperType
   * 
   * Return the super-type of a type in the XSD hierarchy
   * 
   * @param type the type
   * @return its super-type
   */
  public static XSDVocabulary getSuperType(final XSDVocabulary type) {
    return hierarchy.getSuperNode(type);
  }

  /**
   * <!-- generaliseType -->
   * 
   * Return the most specific generalisation of a type that can contain another
   * type
   * 
   * @param type the type to generalise
   * @param containedType the type to be contained
   * @return the generalisation of type
   */
  public static XSDVocabulary generaliseType(final XSDVocabulary type, final XSDVocabulary containedType) {
    if(type == null) return containedType;
    XSDVocabulary ret_type = type;
    while(!datatypeContains(ret_type, containedType)) {
      if(ret_type.equals(XSDVocabulary.ANY_TYPE)) throw new Bug();
      ret_type = getSuperType(ret_type);
      if(ret_type == null) throw new Panic();
    }
    return ret_type;
  }

  /**
   * <!-- generaliseType -->
   * 
   * Return the most specific generalisation of a type that can contain an
   * OWLDataRange. OWLDataRanges can be quite complex, and this method works by
   * recursing to other methods according to the specific subclass of
   * OWLDataRange that owlType is an instance of.
   * 
   * @param type The type to generalise
   * @param owlType The OWLDataRange that the type must contain
   * @return The most specific generalisation of type that can contain owlType
   */
  public static XSDVocabulary generaliseType(final XSDVocabulary type, final OWLDataRange owlType) {
    if(owlType instanceof OWLDataType) return generaliseType(type, (OWLDataType)owlType);
    else if(owlType instanceof OWLDataRangeRestriction) return generaliseType(type, (OWLDataRangeRestriction)owlType);
    else if(owlType instanceof OWLDataOneOf) return generaliseType(type, (OWLDataOneOf)owlType);
    else if(owlType instanceof OWLDataComplementOf) return generaliseType(type, (OWLDataComplementOf)owlType);
    else
      throw new Panic();
  }

  /**
   * <!-- generaliseType -->
   * 
   * Handle an OWLDataType subclass of OWLDataRange. This is the 'main' method
   * that actually extracts the XSD type from the OWLDataRange using the
   * xsdTypes map from the URI of the OWLDataType to the XSDVocabulary.
   * 
   * @param type The type to generalise
   * @param owlType The OWLDataType to contain
   * @return The most specific generalisation of type that contains owlType
   */
  private static XSDVocabulary generaliseType(final XSDVocabulary type, final OWLDataType owlType) {
    XSDVocabulary otype = xsdTypes.get(owlType.getURI());
    if(type == null) return otype;
    else
      return generaliseType(type, otype);
  }

  /**
   * <!-- generaliseType -->
   * 
   * Handle an OWLDataRangeRestriction subclass of OWLDataRange. Here we are
   * just interested in the data range of the range restriction, so recurse to
   * find out the XSD type of that.
   * 
   * @param type The type to generalise
   * @param owlType The OWLDataRangeRestriction to contain
   * @return The most specific generalisation of the type that contains owlType
   */
  private static XSDVocabulary generaliseType(final XSDVocabulary type, final OWLDataRangeRestriction owlType) {
    return generaliseType(type, owlType.getDataRange());
  }

  /**
   * <!-- generaliseType -->
   * 
   * Handle an OWLDataOneOf subclass of OWLDataRange. Here we need to look at
   * the datatype of each constant in the one-of. If no type is provided, we
   * return ANY_TYPE.
   * 
   * @param type The type to generalise
   * @param owlType The OWLDataOneOf to contain
   * @return The most specific generalisation of the type that contains owlType
   */
  private static XSDVocabulary generaliseType(final XSDVocabulary type, final OWLDataOneOf owlType) {
    XSDVocabulary retType = type;
    for(OWLConstant c: owlType.getValues()) {
      if(c.isTyped()) {
        retType = generaliseType(retType, ((OWLTypedConstant)c).getDataType());
      }
      else
        return XSDVocabulary.ANY_TYPE;
    }
    return retType;
  }

  /**
   * <!-- generaliseType -->
   * 
   * Handle an OWLDataComplementOf. I assume that OWLDataComplementOf is the
   * complement of OWLDataOneOf, or the complement of OWLDataRangeRestriction.
   * So what matters is the underlying type of the thing being complemented.
   * Recurse to get that.
   * 
   * @param type The type to generalise
   * @param owlType The OWLDataComplementOf to contain
   * @return The most specific generalisation of the type that contains owlType
   */
  private static XSDVocabulary generaliseType(final XSDVocabulary type, final OWLDataComplementOf owlType) {
    return generaliseType(type, owlType.getDataRange());
  }

  /**
   * <!-- dataRangeContains -->
   * 
   * Confirm that an OWLDataRange can at least theoretically store a value of
   * the specified type. This is achieved by recursively descending into the
   * OWLDataRange until we have an OWLDataType from which the URI can be
   * extracted. This is expected to be the URI of an XSDVocabulary, and we then
   * use the XSDHelper to compare the two XSD types. By "theoretically", since
   * we are only exploring datatypes, it is not possible to confirm that any
   * specific value of that type will conform to a specified range, complement
   * or one-of: all we want to know is that the base type of the range,
   * complement or one-of is compatible with the requested type. The reasoner
   * will then presumably highlight where values are inconsistent.
   * 
   * @param owlType The OWLDataRange
   * @param type The type the owlType is expected to contain
   * @return true if the owlType can theoretically contain the type
   */
  public static boolean dataRangeContains(OWLDataRange owlType, XSDVocabulary type) {
    if(owlType instanceof OWLDataType) return dataRangeContains((OWLDataType)owlType, type);
    else if(owlType instanceof OWLDataRangeRestriction) return dataRangeContains((OWLDataRangeRestriction)owlType, type);
    else if(owlType instanceof OWLDataOneOf) return dataRangeContains((OWLDataOneOf)owlType, type);
    else if(owlType instanceof OWLDataComplementOf) return dataRangeContains((OWLDataComplementOf)owlType, type);
    else
      throw new Panic();
  }

  /**
   * <!-- dataRangeContains -->
   * 
   * The simple case: the owlType is an OWLDataType. Extract the URI and use the
   * XSDHelper to confirm that it can contain the requested type.
   * 
   * @param owlType The OWLDataType
   * @param type The type the owlType is expected to be able to contain
   * @return true iff the owlType can contain the type
   */
  private static boolean dataRangeContains(OWLDataType owlType, XSDVocabulary type) {
    return XSDHelper.datatypeContains(xsdTypes.get(owlType.getURI()), type);
  }

  /**
   * <!-- dataRangeContains -->
   * 
   * Handle an OWLDataRangeRestriction owlType. Here we cannot confirm the
   * particulars of the range--we just need the base type of the range.
   * 
   * @param owlType The range
   * @param type The type the range is expected to contain
   * @return true iff the base datatype of the owlType can contain the type
   */
  private static boolean dataRangeContains(OWLDataRangeRestriction owlType, XSDVocabulary type) {
    return dataRangeContains(owlType.getDataRange(), type);
  }

  /**
   * <!-- dataRangeContains -->
   * 
   * Handle an OWLDataOneOf owlType. Here we check that at least one of the
   * 'one-ofs' has a base type that can contain the requested type.
   * 
   * @param owlType The one-of
   * @param type The type the one-of is expected to contain
   * @return true iff at least one of the one-ofs has a base datatype that can
   *         contain the expected type
   */
  private static boolean dataRangeContains(OWLDataOneOf owlType, XSDVocabulary type) {
    for(OWLConstant c: owlType.getValues()) {
      if(c.isTyped()) {
        if(dataRangeContains(((OWLTypedConstant)c).getDataType(), type)) {
          return true;
        }
      }
      else
        return true;
    }
    return false;
  }

  /**
   * <!-- dataRangeContains -->
   * 
   * Handle an OWLDataComplementOf owlType. The meaning of OWLDataComplementOf
   * is assumed to be, in the case of a one-of, anything of the same type as the
   * one-ofs except those listed, and in the case of a range, anything outside
   * that range. Thus we need to know that the base type of what is being
   * complemented can contain the type requested.
   * 
   * @param owlType The complement
   * @param type The type the complement is expected to contain
   * @return true iff the base type of the complement can contain the expected
   *         type
   */
  private static boolean dataRangeContains(OWLDataComplementOf owlType, XSDVocabulary type) {
    return dataRangeContains(owlType.getDataRange(), type);
  }

}
