/*
 * uk.ac.hutton.obiama.action: Action.java
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
import java.util.Map;

import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ScheduleException;
import uk.ac.hutton.obiama.model.ObiamaOntology;
import uk.ac.hutton.obiama.msb.ModelStateBroker;
import uk.ac.hutton.util.FloatingPointComparison;

/**
 * Action
 * 
 * Interface that must be implemented by all Actions
 * 
 * @author Gary Polhill
 */
public interface Action extends Process {
  /**
   * Path to use for URIs of OBIAMA built-in action ontological entities.
   */
  public static final String BUILT_IN_ACTION_PATH = ObiamaOntology.ONTOLOGY_PATH + "/built-in/action/";

  /**
   * <!-- initialise -->
   * 
   * Initialise the action, passing in a URI extension and model state broker.
   * The URI extension is to allow the action to be configured for multiple
   * classes of object. The uriBase can also be used to the same effect.
   * 
   * @param actionURI URI of the action in the schedule ontology
   * @param uriBase base URI to use for ontological entities referred to by the
   *          action
   * @param uriExtension extension to URI to use for ontological entities
   *          referred to by the action
   * @param msb the model state broker
   * @param parameters parameters for the action
   * @param fcmps floating point comparison methods for the action
   * @throws IntegrationInconsistencyException
   * @throws ScheduleException
   */
  public void initialise(URI actionURI, String uriBase, String uriExtension, ModelStateBroker msb,
      Map<String, ActionParameter> parameters, Map<URI, FloatingPointComparison> fcmps)
      throws IntegrationInconsistencyException, ScheduleException;

  /**
   * <!-- step -->
   * 
   * Run the action for a particular individual
   * 
   * @param individual the nominal performer of the action
   * @throws IntegrationInconsistencyException
   */
  public void step(URI individual) throws IntegrationInconsistencyException;

}
