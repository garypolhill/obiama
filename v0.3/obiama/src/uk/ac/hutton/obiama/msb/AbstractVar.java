/*
 * uk.ac.hutton.obiama.msb: AbstractVar.java
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
package uk.ac.hutton.obiama.msb;

import java.net.URI;

import org.semanticweb.owl.model.OWLProperty;

import uk.ac.hutton.obiama.action.Process;
import uk.ac.hutton.obiama.action.Action;

/**
 * AbstractVar
 * 
 * Abstract class for all variables. This provides methods that must be
 * implemented within the package for communication among objects, but are not
 * required outside the package.
 * 
 * @author Gary Polhill
 */
public abstract class AbstractVar implements Var {
  /**
   * The action requesting the variable
   */
  Process process;
  
  /**
   * The model state broker
   */
  AbstractModelStateBroker msb;
    
  /**
   * Whether or not any values for this variable can be written to
   */
  boolean readOnly;

  /**
   * Constructor populating the ivars
   * 
   * @param process The process this var belongs to
   * @param msb The model state broker
   * @param property The property
   */
  AbstractVar(Process process, AbstractModelStateBroker msb) {
    this.process = process;
    this.msb = msb;
    readOnly = process instanceof Action ? false : true;
  }
    
  /**
   * <!-- getProcess -->
   *
   * @return the process actually responsible for this var
   */
  Process getProcess() {
    return process;
  }
  
  /**
   * <!-- getModelStateBroker -->
   * 
   * @return the model state broker
   */
  AbstractModelStateBroker getModelStateBroker() {
    return msb;
  }

  /**
   * <!-- getProperty -->
   * 
   * @return The OWLProperty of this var
   */
  abstract OWLProperty<?, ?> getProperty();
  
  /**
   * <!-- getURI -->
   *
   * @see uk.ac.hutton.obiama.msb.Var#getURI()
   * @return The URI of this var
   */
  public abstract URI getURI();
  
  /**
   * <!-- readOnly -->
   *
   * @see uk.ac.hutton.obiama.msb.Var#readOnly()
   * @return <code>true</code> if this var will only provide read-only values
   */
  public boolean readOnly() {
    return readOnly;
  }
}
