/*
 * uk.ac.hutton.obiama.exception: NoSuchActionImplementationException.java
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

import java.net.URI;
import java.util.Map;
import uk.ac.hutton.obiama.action.Process;

/**
 * NoSuchActionImplementationException
 * 
 * Exception covering the case when a schedule ontology mentions an action that
 * does not have any implementation.
 * 
 * @author Gary Polhill
 */
public class NoSuchProcessImplementationException extends Exception {
  /**
   * Serial number
   */
  private static final long serialVersionUID = -6140735318144917508L;
  
  /**
   * Map of classes tried to error message
   */
  Map<String, String> errMap;
  
  /**
   * URI of action without any implementation
   */
  URI processURI;
  
  /**
   * Class of process we are trying to create
   */
  Class<? extends Process> processClass;

  /**
   * @param processURI URI of process lacking a valid implementation
   * @param processClass (Subclass) of process we are trying to create
   * @param errMap Map of classes tried to error message (can be empty or null)
   */
  public NoSuchProcessImplementationException(URI processURI, Class<? extends Process> processClass, Map<String, String> errMap) {
    this.errMap = errMap;
    this.processURI = processURI;
    this.processClass = processClass;
  }


  /* (non-Javadoc)
   * @see java.lang.Throwable#getMessage()
   * 
   * Build an error message
   */
  @Override
  public String getMessage() {
    StringBuffer buf = new StringBuffer(processClass.getSimpleName() + " ");
    buf.append(processURI.toString());
    buf.append(" does not have any valid implementations.");
    if(errMap == null || errMap.size() == 0) {
      buf.append(" None given in schedule ontology.");
    }
    else {
      buf.append(" Tried");
      Boolean first = true;
      for(String key: errMap.keySet()) {
        buf.append(first ? ": " : "; ");
        first = false;
        buf.append(key);
        buf.append("--");
        buf.append(errMap.get(key));
      }
      buf.append(".");
    }
    return buf.toString();
  }

}
