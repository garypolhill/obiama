/* uk.ac.hutton.obiama.exception: UsageException.java
 * Copyright (C) 2013 The James Hutton Institute
 *
 * This file is part of obiama-0.3.
 *
 * obiama-0.3 is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 *
 * obiama-0.3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with obiama-0.3. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Contact information:
 *   Gary Polhill
 *   The James Hutton Institute, Craigiebuckler, Aberdeen. AB15 8QH. UK.
 *   gary.polhill@hutton.ac.uk
 */
package uk.ac.hutton.obiama.exception;

/**
 * UsageException
 * @author Gary Polhill
 *
 * An exception caused by the user giving incorrect command-line arguments
 * to the program.
 */
public class UsageException extends Exception {
  /**
   * UID for serialisation
   */
  private static final long serialVersionUID = 841429731905718330L;

  /**
   * Constructor
   * 
   * @param arg The argument causing the error
   * @param message The error message
   * @param usage A synopsis
   */
  
  public UsageException(String arg, String message, String usage) {
    super("Option " + arg + ": " + message + "\n" + usage);
  }

}
