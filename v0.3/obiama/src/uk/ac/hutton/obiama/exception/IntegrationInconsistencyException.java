/*
 * uk.ac.hutton.obiama.exception: IntegrationInconsistencyException.java
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

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.action.Creator;
import uk.ac.hutton.obiama.action.Process;
import uk.ac.hutton.obiama.action.Query;

/**
 * IntegrationInconsistencyException
 * 
 * An exception caused by an inconsistency arising from an attempted integration
 * of actions.
 * 
 * @author Gary Polhill
 */
public abstract class IntegrationInconsistencyException extends Exception {
  /**
   * Number for serialisation
   */
  private static final long serialVersionUID = -451596411953531130L;

  /**
   * Originating action of the exception
   */
  Process originator;

  /**
   * Constructor allowing originator to be passed in
   * 
   * @param process the originating action
   */
  IntegrationInconsistencyException(Process process) {
    this.originator = process;
  }

  public final String getMessage() {
    if(originator == null) {
      return "Integration inconsistency exception (" + getClass().getName() + ") during unknown action: "
        + getErrorMessage();
      // Yes, this is a bug...
    }
    Process stack = originator;
    StringBuffer buffer = new StringBuffer(getTypeInfo(originator));
    do {
      Process oldStack = stack;
      stack = stack.getOriginator();
      if(stack != null && stack != oldStack) {
        buffer.append(", called from " + getTypeInfo(stack));
      }
      else {
        break;
      }
    } while(true);
    return "Integration inconsistency exception (" + getClass().getName() + ") during " + buffer + ": "
      + getErrorMessage();
  }

  protected abstract String getErrorMessage();

  private String getTypeInfo(Process p) {
    String string = p.getURI() + " (" + p.getClass().getCanonicalName() + ")";
    if(p instanceof Creator) {
      return "creator " + string;
    }
    else if(p instanceof Action) {
      return "action " + string;
    }
    else if(p instanceof Query) {
      return "query " + string;
    }
    else {
      return "process " + string;
    }
  }
}