/*
 * uk.ac.hutton.obiama.exception: StateOntologyHasTBoxAxiomsException.java
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

/**
 * <!-- StateOntologyHasTBoxAxiomsException -->
 * 
 * An exception caused when OBIAMA detects a T-box assertion in a state
 * ontology.
 * 
 * @author Gary Polhill
 */
public class StateOntologyHasTBoxAxiomsException extends Exception {
  /**
   * Serialisation number
   */
  private static final long serialVersionUID = -2323373822086281685L;

  /**
   * URI of state ontology
   */
  String stateOntologyStr;

  /**
   * URI of ontology containing Tbox assertion
   */
  URI closureURI;

  /**
   * Text describing axiom in the closureURI that is the Tbox assertion
   */
  String axiomDescription;

  /**
   * @param stateOntologyStr The state ontology being loaded in
   * @param closureURI The ontology in which a T-box assertion has been detected
   * @param axiomDescription A text description of the T-box axiom found
   */
  public StateOntologyHasTBoxAxiomsException(String stateOntologyStr, URI closureURI, String axiomDescription) {
    this.stateOntologyStr = stateOntologyStr;
    this.closureURI = closureURI;
    this.axiomDescription = axiomDescription;
  }

  /**
   * <!-- getMessage -->
   * 
   * Return the error message for this exception
   * 
   * @see java.lang.Throwable#getMessage()
   * @return the error message
   */
  public String getMessage() {
    URI stateOntologyURI = URI.create(stateOntologyStr);
    if(stateOntologyURI.equals(closureURI)) {
      return "T-box axiom \"" + axiomDescription + "\" found in state ontology " + stateOntologyStr;
    }
    return "T-box axiom \"" + axiomDescription + "\" found in non-model structure ontology " + closureURI
      + " in closure of state ontology " + stateOntologyStr;
  }
}
