/*
 * uk.ac.hutton.obiama.exception: QueryInvokationException.java Copyright (C)
 * 2013 The James Hutton Institute
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

import java.lang.reflect.Method;
import java.util.Map;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.action.Process;
import uk.ac.hutton.obiama.action.Query;

/**
 * QueryInvokationException
 * 
 * An exception caused when an action attempts to invoke a query wrongly.
 * 
 * @author Gary Polhill
 */
public class QueryInvokationException extends IntegrationInconsistencyException {
  /**
   * Serialisation number
   */
  private static final long serialVersionUID = -2796445854386245339L;

  /**
   * The query being invoked
   */
  Query<?> query;

  /**
   * Any details associated with the invokation. If this has zero size, or is
   * null, then no matching method for the query could be found.
   */
  Map<Method, Throwable> details;

  /**
   * @param originator The action invoking the query
   * @param query The query being invoked
   * @param errors Details associated with any attempts.
   */
  public QueryInvokationException(Process originator, Query<?> query, Map<Method, Throwable> errors) {
    super(originator);
    this.query = query;
    this.details = errors;
  }

  /**
   * <!-- getErrorMessage -->
   * 
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  protected String getErrorMessage() {
    if(details == null || details.size() == 0)
      return "Query " + query.getURI() + " does not have a method matching the invokation of it by the action";
    StringBuffer buf = new StringBuffer();
    int i = 1;
    for(Method method: details.keySet()) {
      buf.append("  " + i + ": Invokation of " + method.getName() + "(");
      Class<?> types[] = method.getParameterTypes();
      for(int j = 0; j < types.length; j++) {
        if(j > 0) buf.append(", ");
        buf.append(types[i].getName());
      }
      buf.append(") returning " + method.getReturnType().getName());
      Throwable e = details.get(method);
      buf.append(" caused exception " + e.getClass().getName() + ": \"" + e.getMessage() + "\"\n");
    }
    return "Invokation of query " + query.getURI() + " by the action failed. Tried the following:\n" + buf;
  }
}
