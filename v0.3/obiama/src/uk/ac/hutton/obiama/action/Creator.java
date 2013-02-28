/*
 * uk.ac.hutton.obiama.action: Creator.java
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
package uk.ac.hutton.obiama.action;

import java.net.URI;

import uk.ac.hutton.obiama.model.ObiamaOntology;
import uk.ac.hutton.obiama.msb.Instance;

/**
 * Creator
 * 
 * A creator is an initialiser for instance variables of a class. A Creator is
 * an Action, the difference being that the argument of the step method is a URI
 * to be created, and some assertions made about that URI.
 * 
 * @author Gary Polhill
 */
public interface Creator extends Action {
  /**
   * Path to use for URIs of built-in creator ontological entities
   */
  public static final String BUILT_IN_CREATOR_PATH = ObiamaOntology.ONTOLOGY_PATH + "/built-in/creator/";

  /**
   * <!-- setCreation -->
   * 
   * Set the creation to be built by the next call to {@link Action#step(URI)}
   * 
   * @param individual
   */
  public void setCreation(Instance individual);

}
