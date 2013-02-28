/*
 * uk.ac.hutton.obiama.exception: FileFormatException.java 
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

/**
 * FileFormatException
 * 
 * An exception caused when a file has unexpected contents
 * 
 * @author Gary Polhill
 */
public class FileFormatException extends Exception {
  /**
   * Serial version ID
   */
  private static final long serialVersionUID = 1464461145383277279L;

  /**
   * Name of the file
   */
  private String filename;

  /**
   * Expected contents
   */
  private String expecting;

  /**
   * Actual contents
   */
  private String found;

  /**
   * Where found
   */
  private String where;

  /**
   * Main constructor. If <code>expecting</code> and <code>found</code> are
   * supposed to be the actual contents of the file rather than a description
   * thereof, then they should be put in quotes. <code>where</code> is a generic
   * phrase describing where in the file the problem occurs. The format of the
   * message is: <code>"File format exception in file &lt;filename&gt;:
   * expecting &lt;expecting&gt;, found &lt;found&gt;, &lt;where&gt;"</code>
   * 
   * @param filename Name of the file where the problem occurred
   * @param expecting Expected contents
   * @param found Actual contents
   * @param where Phrase describing where in the file the error was detected
   */
  public FileFormatException(String filename, String expecting, String found, String where) {
    this.filename = filename;
    this.expecting = expecting;
    this.found = found;
    this.where = where;
  }

  /**
   * Convenience constructor in which the <code>where</code> phrase is given by
   * a line and column number as <code>"at line
   * &lt;line&gt;, column &lt;col&gt;"</code>
   * 
   * @param filename Name of the file where the problem occurred
   * @param expecting Expected contents
   * @param found Actual contents
   * @param line Line number
   * @param col Column number
   */
  public FileFormatException(String filename, String expecting, String found, int line, int col) {
    this(filename, expecting, found, "at line " + line + ", column " + col);
  }

  /* (non-Javadoc)
   * @see java.lang.Throwable#getMessage()
   */
  @Override
  public String getMessage() {
    return "File format exception in file " + filename + ": expecting " + expecting + ", found " + found + ", " + where;
  }
}
