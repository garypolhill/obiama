/* uk.ac.hutton.obiama.exception: Panic.java
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
 * Panic
 * @author Gary Polhill
 *
 * A Panic is an exception that "should not" occur.
 */
public final class Panic extends Error {
  /**
   * UID for serialisation
   */
  private static final long serialVersionUID = -1243773617912666153L;

  /**
   * Constructor for a Panic. Creates an exception which can then be used to get
   * the file and line from a stack trace
   */
  public Panic() {
    this(new Exception());
  }
  
  /**
   * Private constructor. Takes the exception created by the default constructor,
   * and uses the stack trace to get the line number and file where the panic
   * occurred.
   * 
   * @param e Exception created by the stack trace
   */
  private Panic(Exception e) {
    super("Panic! File: " + e.getStackTrace()[1].getFileName()
        + ", Line: " + e.getStackTrace()[1].getLineNumber());
  }
}
