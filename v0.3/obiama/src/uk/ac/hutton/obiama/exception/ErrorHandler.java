/*
 * uk.ac.hutton.obiama.exception: ErrorHandler.java 
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

import java.util.HashSet;
import java.util.Set;

/**
 * ErrorHandler
 * 
 * Class to handle exceptions in a standard way. <!-- For now, it just writes
 * them to stderr. -->
 * 
 * @author Gary Polhill
 */
public class ErrorHandler {
  private static Set<String> requestedDebugTags = new HashSet<String>();
  private static boolean showAll = false;
  public static final String ALL_REQUEST = "all";

  /**
   * <!-- fatal -->
   * 
   * Handle a fatal exception. Display a standard message and then exit.
   * 
   * @param e The fatal exception
   * @param whilst A string summarising what activity was being carried out when
   *          the exception occurred.
   */
  public static void fatal(Exception e, String whilst) {
    System.err.println("The following fatal error has occurred whilst " + whilst + ": " + e.getMessage());
    System.err.println("The program will exit.");
    e.printStackTrace();
    System.exit(1);
  }

  /**
   * <!-- redo -->
   * 
   * Handle a "redo" exception. This is a fatal exception that cannot be
   * addressed without reconfiguring the run. In GUI mode, it might be possible
   * for the user to do this without terminating the program.
   * 
   * @param e
   * @param whilst
   */
  public static void redo(Exception e, String whilst) {
    System.err.println("The following error has occurred whilst " + whilst + ": " + e.getMessage());
    System.err.println("You need to change the setup to run successfully.");
    throw new Redo();
  }

  /**
   * <!-- warn -->
   * 
   * Handle a warning. Display a standard message, and the means by which the
   * computer will proceed.
   * 
   * @param e The exception
   * @param whilst A string summarising what activity was being carried out when
   *          the exception occurred.
   * @param but A string summarising what assumptions are being made to allow
   *          the program to continue.
   */
  public static void warn(Exception e, String whilst, String but) {
    System.err.println("An exception (" + e.getClass().getSimpleName() + ") has occurred whilst " + whilst + ": "
      + e.getMessage());
    System.err.println("The system will proceed, but " + but);
  }

  /**
   * <!-- warn -->
   * 
   * Handle a warning that doesn't pertain to an exception per se.
   * 
   * @param message Message the user needs to be warned about
   * @param whilst What was being carried out when the issue arose
   * @param but What assumptions are being made that allow the program to
   *          continue
   */
  public static void warn(String message, String whilst, String but) {
    System.err.println("The following issue has occurred whilst " + whilst + ": " + message);
    System.err.println("The system will proceed, but " + but);
  }

  /**
   * <!-- note -->
   * 
   * Handle a note. Display the message to be noted by the user. <!-- This isn't
   * really an error situation, so should the class be renamed? -->
   * 
   * @param message The note to display.
   */
  public static void note(String message) {
    System.err.println("Note: " + message);
  }

  /**
   * <!-- debug -->
   * 
   * Handle a debug message.
   * 
   * @param tag A tag with which to identify this kind of message, so they can
   *          be switched off.
   * @param message The message.
   */
  public static void debug(String tag, String message) {
    if(showAll || requestedDebugTags.contains(tag.toLowerCase())) {
      System.err.println("Debug (" + tag + "): " + message);
    }
  }

  /**
   * <!-- show -->
   * 
   * Request a set of debug messages are shown
   * 
   * @param tag The tag the debug messages have
   */
  public static void show(String tag) {
    if(tag.equalsIgnoreCase(ALL_REQUEST)) showAll = true;
    else
      requestedDebugTags.add(tag.toLowerCase());
  }
}
