/*
 * uk.ac.hutton.obiama.exception: AmbiguousConceptException.java
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

import java.net.URI;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.action.Process;

/**
 * AmbiguousConceptException
 * 
 * An exception caused when a single concept is required, but many have been found.
 * 
 * @author Gary Polhill
 */
public class AmbiguousConceptException extends IntegrationInconsistencyException {
  /**
   * Serialisation number
   */
  private static final long serialVersionUID = -1050277996185858769L;
  
  /**
   * The property causing the exception
   */
  URI property;
  
  /**
   * Array of ambiguous concepts
   */
  URI[] conceptURIs;

  /**
   * Constructor
   * 
   * @param action The action causing the exception
   * @param property The property with a problem
   * @param uris The concepts that form an ambiguity
   */
  public AmbiguousConceptException(Process action, URI property, URI... uris) {
    super(action);
    this.property = property;
    conceptURIs = uris;
  }

  /**
   * <!-- getErrorMessage -->
   *
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  protected String getErrorMessage() {
    StringBuffer buf = new StringBuffer();
    if(conceptURIs != null && conceptURIs.length > 0) {
      for(int i = 0; i < conceptURIs.length; i++) {
        if(i > 0) buf.append(", ");
        buf.append(conceptURIs[i]);
      }
    }
    else {
      buf.append("(null or empty array of concepts)");
    }
    return "Range of property " + property + " ambiguous. Could be any of ";
  }

}
