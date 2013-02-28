/*
 * uk.ac.hutton.obiama.model: ConcurrentActionForEach.java
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
import java.util.Collections;
import java.util.Set;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.action.Creator;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ScheduleException;
import uk.ac.hutton.obiama.msb.Concept;
import uk.ac.hutton.obiama.msb.Instance;
import uk.ac.hutton.obiama.msb.ModelStateBroker;

/**
 * ConcurrentActionForEach
 * 
 * A model action performed by a class of agents, running concurrently. This
 * does not mean multi-threading, but is an ontological issue in that actions
 * for each agent should not interfere with each other.
 * 
 * @author Gary Polhill
 */
public class ConcurrentActionForEach extends AbstractNoUpdateAction {
  /**
   * The OWL class that agents performing this action all belong to
   */
  protected Concept concept;

  /**
   * The action the agents each perform
   */
  protected Action action;

  /**
   * Constructor, which checks the class of agents exists
   * 
   * @param agentClass the class agents belong to
   * @param action the action they perform
   * @param msb the Model State Broker
   * @param actionURI the URI of the action in the schedule ontology
   * @param assertedNonTimed <code>true</code> if the action is asserted
   *          non-timed
   * @throws IntegrationInconsistencyException
   */
  public ConcurrentActionForEach(URI agentClass, Action action, ModelStateBroker msb, URI actionURI,
      boolean assertedNonTimed) throws IntegrationInconsistencyException {
    super(msb, actionURI, assertedNonTimed);
    init(agentClass, action);
  }

  /**
   * Constructor for timed actions
   * 
   * @param agentClass the class agents belong to
   * @param action the action they perform
   * @param msb the model state broker
   * @param actionURI the URI of the action in the schedule ontology
   * @param time the time at which the action is to start
   * @throws IntegrationInconsistencyException
   * @throws ScheduleException
   */
  public ConcurrentActionForEach(URI agentClass, Action action, ModelStateBroker msb, URI actionURI, double time)
      throws IntegrationInconsistencyException, ScheduleException {
    super(msb, actionURI, time);
    init(agentClass, action);
  }

  /**
   * <!-- init -->
   * 
   * Initialise the action
   * 
   * @param agentClass the class agents belong to
   * @param action the action they perform
   * @throws IntegrationInconsistencyException
   */
  private void init(URI agentClass, Action action) throws IntegrationInconsistencyException {
    this.concept = msb.getConcept(agentClass, action, null, null);
    this.action = action;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * uk.ac.hutton.obiama.model.AbstractScheduledNoUpdateActivity#stepNoUpdate
   * ()
   */
  @Override
  public void stepNoUpdate() throws IntegrationInconsistencyException {
    Set<Instance> agents = concept.getInstances();
    for(Instance agent: agents) {
      action.step(agent.getURI());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see uk.ac.hutton.obiama.model.AbstractScheduledActivity#step()
   */
  @Override
  public void stepImpl() throws IntegrationInconsistencyException {
    stepNoUpdate();
    if(action instanceof Creator) msb.updateCreators();
    else
      msb.update();
  }

  /**
   * <!-- getActionSet -->
   * 
   * Return the action as a singleton set
   * 
   * @see uk.ac.hutton.obiama.model.AbstractScheduledAction#getActionSet()
   */
  @Override
  public Set<Action> getActionSet() {
    return Collections.singleton(action);
  }

  /**
   * <!-- allCreators -->
   *
   * @see uk.ac.hutton.obiama.model.AbstractScheduledAction#allCreators()
   * @return
   */
  @Override
  boolean allCreators() {
    return action instanceof Creator;
  }
}
