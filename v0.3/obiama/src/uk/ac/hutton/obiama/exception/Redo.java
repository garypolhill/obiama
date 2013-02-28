/*
 * uk.ac.hutton.obiama.exception: Redo.java
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

/**
 * Redo
 * 
 * A Redo is an exception that applies in GUI mode only. Its provision enables
 * an application to give the opportunity for the user to restart with some
 * different settings, preventing the application from crashing at every little
 * thing. In non GUI mode, this exception will have to cause the program to
 * terminate.
 * 
 * @author Gary Polhill
 * 
 */
public class Redo extends RuntimeException {

  /**
   * Serial number for serialization
   */
  private static final long serialVersionUID = -3187476441616155444L;

}
