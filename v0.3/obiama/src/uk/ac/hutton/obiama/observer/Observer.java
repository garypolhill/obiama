/*
 * uk.ac.hutton.obiama.observer: Observer.java
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
package uk.ac.hutton.obiama.observer;

import java.net.URI;

import uk.ac.hutton.obiama.action.Process;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.model.ObiamaOntology;

/**
 * <!-- Observer -->
 * 
 * An observer is a process to gather data from the simulation.
 * 
 * @author Gary Polhill
 */
public interface Observer extends Process {
  public static final URI ONTOLOGY_URI = URI.create(ObiamaOntology.ONTOLOGY_PATH + "/built-in/observer.owl");
  
  /**
   * <!-- saveData -->
   * 
   * Save the data gathered to the stated destination
   * 
   * @param destination
   */
  public void saveData(String destination);

  /**
   * <!-- step -->
   * 
   * Gather the data
   * @throws IntegrationInconsistencyException 
   */
  public void step() throws IntegrationInconsistencyException;

  /**
   * <!-- addVisualiser -->
   * 
   * Add the visualiser to those this observer uses
   *
   * @param visualiser
   */
  public void addVisualiser(Visualiser visualiser);
}
