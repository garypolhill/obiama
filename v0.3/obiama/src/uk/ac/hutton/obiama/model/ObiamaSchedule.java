/* uk.ac.hutton.obiama.model: ObiamaSchedule.java
 *
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
package uk.ac.hutton.obiama.model;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.action.Creator;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.NoSuchProcessImplementationException;
import uk.ac.hutton.obiama.exception.OntologyConfigurationException;
import uk.ac.hutton.obiama.exception.ScheduleException;
import uk.ac.hutton.obiama.msb.Instance;
import uk.ac.hutton.obiama.msb.ModelStateBroker;

/**
 * ObiamaSchedule
 *
 * 
 *
 * @author Gary Polhill
 */
public class ObiamaSchedule {
  protected final Map<URI, AbstractScheduledAction> allActions;
  protected final AbstractScheduledAction actionGroup;
  protected final Double stopTime;
  protected final Double clockTick;
  protected final URI scheduleURI;
  protected final ScheduleOntology ontology;
  
  private Set<Action> actionSet;
  
  ObiamaSchedule(URI scheduleURI, ModelStateBroker msb, ScheduleOntology ontology)
      throws NoSuchProcessImplementationException, IntegrationInconsistencyException, ScheduleException,
      OntologyConfigurationException {
    this.scheduleURI = scheduleURI;
    this.ontology = ontology;
    allActions = new HashMap<URI, AbstractScheduledAction>();
    URI actionURI = ontology.getFunctionalObjectPropertyOf(scheduleURI, ScheduleOntology.HAS_ACTION_GROUP_URI);
    if(actionURI == null) {
      throw new OntologyConfigurationException(ontology.getURI(), "Schedule " + scheduleURI + " has no action");
    }
    actionGroup = ontology.getScheduledAction(actionURI, msb, allActions);
    stopTime = ontology.getDoubleFunctionalDataPropertyOf(scheduleURI, ScheduleOntology.STOP_TIME_URI);
    clockTick = ontology.getDoubleFunctionalDataPropertyOf(scheduleURI, ScheduleOntology.CLOCK_TICK_URI);
    actionSet = null;
  }
  
  public AbstractScheduledAction getActionFor(URI actionURI) {
    return allActions.get(actionURI);
  }
  
  public AbstractScheduledAction getActionGroup() {
    return actionGroup;
  }
  
  public Double getStopTime() {
    return stopTime;
  }
  
  public Double getClockTick() {
    return clockTick;
  }
  
  public URI getURI() {
    return scheduleURI;
  }
  
  public boolean isTimed() {
    return actionGroup.isTimed();
  }
  
  public double getStartTime() {
    return actionGroup.getTime();
  }
  
  public LinkedList<AbstractScheduledAction> getActionList() throws ScheduleException {
    return actionGroup.getActionList();
  }
  
  public Set<Action> getActionSet() {
    if(actionSet == null) actionSet = actionGroup.getActionSet();
    return actionSet;
  }
  
  public void run() throws IntegrationInconsistencyException, ScheduleException {
    if(isTimed()) {
      throw new ScheduleException(actionGroup, "Attempt to run this action from timed schedule " + scheduleURI);
    }
    actionGroup.step();
  }
  
  public synchronized void runCreator(Instance individual) throws IntegrationInconsistencyException, ScheduleException {
    for(Action action: getActionSet()) {
      if(action instanceof Creator) {
        ((Creator)action).setCreation(individual);
      }
      else {
        // TODO throw exception
      }
    }
    run();
  }
}
