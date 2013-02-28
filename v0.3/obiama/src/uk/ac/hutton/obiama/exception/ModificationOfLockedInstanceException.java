/*
 * uk.ac.hutton.obiama.exception: ModificationOfLockedInstanceException.java
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

import uk.ac.hutton.obiama.action.Process;

/**
 * <!-- ModificationOfLockedInstanceException -->
 * 
 * An exception caused when an instance is modified by two processes. This can
 * occur when processes run in parallel that should not, or can be due to more
 * subtle problems arising from a process using a creator or other sub-action
 * that clashes with an earlier attempt in the action to access the instance.
 * 
 * @author Gary Polhill
 */
public class ModificationOfLockedInstanceException extends IntegrationInconsistencyException {

  /**
   * Serialisation number
   */
  private static final long serialVersionUID = 5376824037690626107L;

  /**
   * URI of instance being requested access to
   */
  URI individualURI;

  /**
   * @param process
   * @param individualURI
   */
  public ModificationOfLockedInstanceException(Process process, URI individualURI) {
    super(process);
    this.individualURI = individualURI;
  }

  /**
   * <!-- getErrorMessage -->
   * 
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  protected String getErrorMessage() {
    return "Attempt to modify individual " + individualURI + " already being modified by another action";
  }

}
