/*
 * uk.ac.hutton.obiama.exception:
 * IgnorableIntegrationInconsistencyException.java
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
package uk.ac.hutton.obiama.exception;

import java.util.LinkedList;
import java.util.HashMap;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.model.Log;

/**
 * <!-- IgnorableIntegrationInconsistencyException -->
 * 
 * <p>
 * An abstract class of integration inconsistency exceptions that can be
 * ignored. Ignoring is handled by throwing an {@link IgnoreException} when the
 * exception is created. This means that throwing these kinds of exception would
 * be handled with code like this:
 * <p>
 * 
 * <p>
 * <code>
 * &nbsp;&nbsp;try {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;throw new <i>AnIgnorableException</i>(originator, <i>args</i>...);<br>
 * &nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;catch(IgnoreException e) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;// do nothing<br>
 * &nbsp;&nbsp;}<br>
 * </code>
 * </p>
 * 
 * <p>
 * In general, it would be bad form not to catch the {@link IgnoreException}.
 * </p>
 * 
 * @author Gary Polhill
 */
public abstract class IgnorableIntegrationInconsistencyException extends IntegrationInconsistencyException {
  /**
   * Serialisation ID
   */
  private static final long serialVersionUID = 1584141984484163308L;

  /**
   * Map of ignored situations
   */
  private static HashMap<Class<? extends IgnorableIntegrationInconsistencyException>, LinkedList<Object[]>> ignored =
    null;

  /**
   * Constructor that all subclasses must call
   * 
   * @param originator Action causing the exception
   * @param args Arguments to the exception--these will be used to decide the
   *          'situation' and whether or not to ignore it
   * @throws IgnoreException
   */
  IgnorableIntegrationInconsistencyException(Action originator, Object... args) throws IgnoreException {
    super(originator);
    if(ignores(args)) throw new IgnoreException();
  }

  /**
   * <!-- ignore -->
   * 
   * Assert that a particular exception class is to be ignored in a given
   * situation.
   * 
   * @param exceptionClass An exception class with an ignorable situation
   * @param args Descriptors for the situation to be ignored. <code>"*"</code>
   *          can be used for a wildcard for a particular descriptor. There
   *          should be a constructor corresponding to the arguments given here
   *          in the exception class.
   */
  public static void ignore(Class<? extends IgnorableIntegrationInconsistencyException> exceptionClass, Object... args) {
    if(ignored == null) {
      ignored = new HashMap<Class<? extends IgnorableIntegrationInconsistencyException>, LinkedList<Object[]>>();
    }
    if(!ignored.containsKey(exceptionClass)) {
      ignored.put(exceptionClass, new LinkedList<Object[]>());
    }
    ignored.get(exceptionClass).add(args);
    Log.ignoring(exceptionClass.getCanonicalName(), args);
  }

  /**
   * <!-- ignores -->
   * 
   * Check whether this class ignores the situation described in the arguments,
   * checking superclasses.
   * 
   * @param args
   * @return <code>true</code> if this exception class or one of its
   *         superclasses ignores the situation described in the arguments
   */
  @SuppressWarnings("unchecked")
  public boolean ignores(Object... args) {
    for(Class<? extends IgnorableIntegrationInconsistencyException> c = this.getClass(); c != IgnorableIntegrationInconsistencyException.class; c =
      (Class<? extends IgnorableIntegrationInconsistencyException>)c.getSuperclass()) {
      if(ignores(c, args)) return true;
    }
    return false;
  }

  /**
   * <!-- ignores -->
   * 
   * Check whether the class in the argument (and only that class, not its
   * superclasses, ignores the situation described in the other arguments.
   * 
   * @param exceptionClass Exception class to check
   * @param args
   * @return <code>true</code> if the class ignores the situation.
   */
  public static boolean ignores(Class<? extends IgnorableIntegrationInconsistencyException> exceptionClass,
      Object... args) {
    if(ignored == null) return false;
    if(!ignored.containsKey(exceptionClass)) return false;
    for(Object[] ignore: ignored.get(exceptionClass)) {
      if(ignore.length != args.length) continue;
      boolean allEqual = true;
      for(int i = 0; i < ignore.length; i++) {
        if(ignore[i].toString().equals("*")) continue;
        if(!ignore[i].equals(args[i])) {
          allEqual = false;
          break;
        }
      }
      if(allEqual) return true;
    }
    return false;
  }
}
