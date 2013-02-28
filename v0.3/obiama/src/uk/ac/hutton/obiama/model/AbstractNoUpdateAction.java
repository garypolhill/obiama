/*
 * uk.ac.hutton.obiama.model: AbstractNoUpdateAction.java
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
package uk.ac.hutton.obiama.model;

import java.net.URI;

import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ScheduleException;
import uk.ac.hutton.obiama.msb.ModelStateBroker;

/**
 * AbstractNoUpdateAction
 * 
 * Wrapper class for actions that can be stepped without updating the model
 * state broker--such functionality is needed, for example when an action is
 * part of a composite and an update will take place only after all members of
 * the composite have registered their changes with the model state broker.
 * 
 * @author Gary Polhill
 */
public abstract class AbstractNoUpdateAction extends AbstractScheduledAction {

  /**
   * Tedious required overriding of superclass constructor.
   * 
   * @param msb the model state broker
   * @param actionURI the URI of the action in the schedule ontology
   * @param assertedNonTimed <code>true</code> if the action is asserted
   *          non-timed
   */
  AbstractNoUpdateAction(ModelStateBroker msb, URI actionURI, boolean assertedNonTimed) {
    super(msb, actionURI, assertedNonTimed);
  }

  /**
   * Ditto
   * 
   * @param msb the model state broker
   * @param actionURI the URI of the action in the schedule ontology
   * @param time the time at which the action is to start
   * @throws ScheduleException
   */
  AbstractNoUpdateAction(ModelStateBroker msb, URI actionURI, double time) throws ScheduleException {
    super(msb, actionURI, time);
  }

  /**
   * <!-- stepNoUpdate -->
   * 
   * Step without updating the model state broker.
   * 
   * @throws IntegrationInconsistencyException
   */
  abstract void stepNoUpdate() throws IntegrationInconsistencyException;

}
