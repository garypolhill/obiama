/*
 * uk.ac.hutton.obiama.exception:
 * NeedNonFunctionalGotFunctionalPropertyRuntimeException.java
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
 * NeedFunctionalGotNonFunctionalPropertyRuntimeException
 * 
 * Provide a RuntimeException for the
 * NeedFunctionalGotNonFunctionalPropertyException class. This is required in
 * some methods that override language-standard methods and cannot declare that
 * they throw this exception.
 * 
 * @author Gary Polhill
 */
public class NeedFunctionalGotNonFunctionalPropertyRuntimeException extends
    RuntimeException {
  /**
   * Serial number
   */
  private static final long serialVersionUID = -3498781623457364811L;

  /**
   * Wrapped NonFunctionalGotFunctionalPropertyException
   */
  NeedFunctionalGotNonFunctionalPropertyException e;

  /**
   * Constructor passing in the action and property
   * 
   * Pass this to the non-runtime exception it is covering
   * 
   * @param process the action expecting a non-functional property
   * @param property the functional property
   */
  public NeedFunctionalGotNonFunctionalPropertyRuntimeException(Process process,
      URI property) {
    e = new NeedFunctionalGotNonFunctionalPropertyException(process, property);
  }

  /**
   * <!-- getMessage -->
   * 
   * Pass back the message from the exception this is wrapping
   *
   * @see java.lang.Throwable#getMessage()
   */
  public String getMessage() {
    return e.getMessage();
  }

  /**
   * <!-- getLocalizedMessage -->
   * 
   * Pass back the message from the exception this is wrapping
   *
   * @see java.lang.Throwable#getLocalizedMessage()
   */
  public String getLocalizedMessage() {
    return e.getLocalizedMessage();
  }

  /**
   * <!-- toString -->
   * 
   * Pass back the string from the exception this is wrapping
   *
   * @see java.lang.Throwable#toString()
   */
  public String toString() {
    return e.toString();
  }
}
