/*
 * uk.ac.hutton.obiama.action: AbstractQuery.java Copyright (C) 2013 The James Hutton
 * Institute
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
package uk.ac.hutton.obiama.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.QueryInvokationException;
import uk.ac.hutton.obiama.exception.ScheduleException;
import uk.ac.hutton.obiama.model.Log;
import uk.ac.hutton.util.FloatingPointComparison;
import uk.ac.hutton.util.Reflection;

/**
 * <!-- AbstractQuery -->
 * 
 * <p>
 * Abstract class providing a partial implementation of the Query<T> interface.
 * A Query provides <i>derived</i> information from the Model State Ontology
 * without changing it. All Queries have an 'agent' answering the query, and
 * another requesting it. A Query should belong to the action where the
 * information is used, and then the information required should be obtained by
 * calling its ask() method. The ask() method has arguments at least specifying
 * the agent and the requester, and possibly other arguments.
 * </p>
 * 
 * <p>
 * To implement a Query by subclassing from this class, do the following:
 * </p>
 * 
 * <ul>
 * <li>Declare Vars, Concepts and ActionParameters as ivars</li>
 * <li>Add definitions of Vars and Concepts in the initialise() method</li>
 * <li>Implement one or more ask() methods</li>
 * </ul>
 * 
 * <p>
 * This class implements the generic ask(URI, URI, Object...) method, by trying
 * to find and successfully invoke the most specific implemention of ask() with
 * a set of arguments matching the types of those as called.
 * </p>
 * 
 * @author Gary Polhill
 * @see Query
 * @see AbstractProcess
 */
public abstract class AbstractQuery<T> extends AbstractProcess implements Query<T>, Comparator<Method> {
  private URI queryID;
  private Process originator;

  public void setQueryID(URI queryID) {
    this.queryID = queryID;
  }

  public URI getQueryID() {
    return queryID;
  }

  @Override
  public Process  getOriginator() {
    return originator;
  }

  public void initialiseLocal() throws IntegrationInconsistencyException {
    initialise();
  }

  /**
   * <!-- initialise -->
   * 
   * This method is for subclasses to implement. It is called after all other
   * initialisation has taken place.
   * 
   * @throws IntegrationInconsistencyException
   */
  protected abstract void initialise() throws IntegrationInconsistencyException;

  /**
   * <!-- ask -->
   * 
   * Implementation of the generic ask() method, which looks for the most
   * specific implementation of ask() it can find matching the arguments that it
   * can successfully call.
   * 
   * @see uk.ac.hutton.obiama.action.Query#ask(java.net.URI, java.net.URI,
   *      java.lang.Object[])
   * @param agent The agent being "asked" for the information
   * @param requester The agent requesting the information
   * @param args Any arguments taken by the information
   * @return The information requested
   * @throws IntegrationInconsistencyException
   */
  @SuppressWarnings("unchecked")
  public synchronized T ask(URI agent, URI requester, Process originator, Object... args)
      throws IntegrationInconsistencyException {

    this.originator = originator;

    // Create a list of ask() methods that can be called with these arguments

    Method methods[] = this.getClass().getMethods();
    LinkedList<Method> asks = new LinkedList<Method>();

    for(int i = 0; i < methods.length; i++) {
      if(methods[i].getName().equals(Query.ASK_METHOD_NAME)) {
        Class<?> argTypes[] = methods[i].getParameterTypes();

        if(argTypes.length != 2 + args.length || !Reflection.subType(URI.class, argTypes[0])
          || !Reflection.subType(URI.class, argTypes[1])) {
          continue;
        }

        for(int j = 0; j < args.length; j++) {
          if(!Reflection.subType(args[j].getClass(), argTypes[j + 2])) {
            continue;
          }
        }

        asks.add(methods[i]);
      }
    }

    // Sort the methods from most to least specific

    Collections.sort(asks, this);

    // Try to invoke the methods, starting at the most specific

    Object invokationArgs[] = new Object[args.length + 2];
    invokationArgs[0] = agent;
    invokationArgs[1] = requester;
    for(int j = 0; j < args.length; j++) {
      invokationArgs[j + 2] = args[j];
    }

    Map<Method, Throwable> errors = new HashMap<Method, Throwable>();

    while(asks.size() > 0) {
      Method ask = asks.removeFirst();
      try {
        T result = (T)ask.invoke(this, invokationArgs);
        Log.query(agent, requester, this.getClass().getCanonicalName(), this.getQueryID(), result, args);
        originator = null;
        return result;
      }
      catch(IllegalArgumentException e) {
        errors.put(ask, e);
      }
      catch(IllegalAccessException e) {
        errors.put(ask, e);
      }
      catch(InvocationTargetException e) {
        errors.put(ask, e.getTargetException());
      }
      catch(ClassCastException e) {
        errors.put(ask, e);
      }
    }
    throw new QueryInvokationException(originator, this, errors);
  }

  /**
   * <!-- compare -->
   * 
   * Compare two methods for their specificity... This method probably belongs
   * somewhere else...
   * 
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   * @param ask1 One method
   * @param ask2 Another method
   * @return 0 if the methods are incomparable or equal, -1 if all arguments to
   *         ask2() are subtypes of or the same type as the corresponding
   *         arguments to ask1(), +1 if all arguments to ask1() are subtypes of
   *         or the same type as the corresponding arguments to ask2()
   */
  public int compare(Method ask1, Method ask2) {
    Class<?> args1[] = ask1.getParameterTypes();
    Class<?> args2[] = ask2.getParameterTypes();

    // the methods are incomparable (this shouldn't happen from calls from the
    // ask() method)
    if(args1.length != args2.length) return 0;

    // dir is null for as long as arguments have the same type
    Integer dir = null;

    for(int i = 0; i < args1.length; i++) {
      // arguments have the same type: no change to dir
      if(args1[i].equals(args2[i])) continue;

      // get the direction of subtype relation, or 0 if unrelated
      int thisDir = Reflection.subType(args1[i], args2[i]) ? 1 : (Reflection.subType(args2[i], args1[i]) ? -1 : 0);

      // methods ask1 and ask2 are unrelated
      if(thisDir == 0) return 0;

      // set dir to direction found
      else if(dir == null) dir = thisDir;

      // thisDir different from previous direction: ask1 and ask2 are unrelated
      else if(dir != thisDir) return 0;
    }

    // dir is null if ask1.equals(ask2)
    return dir == null ? 0 : dir;
  }

}
