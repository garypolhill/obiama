/*
 * uk.ac.hutton.obiama.model: IndividualAction.java
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
import uk.ac.hutton.obiama.msb.ModelStateBroker;

/**
 * ModelAction
 * 
 * This class acts as a wrapper for an OBIAMA action performed by a single
 * agent.
 * 
 * @author Gary Polhill
 */
public class IndividualAction extends AbstractNoUpdateAction {
  /**
   * The agent performing the action
   */
  protected URI agent;

  /**
   * The action the agent is to perform
   */
  protected Action action;

  /**
   * Constructor passing in the agent, action and model state broker
   * 
   * @param agent the agent performing the action
   * @param action the action the agent performs
   * @param msb the model state broker
   * @param actionURI the URI of the action in the schedule ontology
   */
  public IndividualAction(URI agent, Action action, ModelStateBroker msb, URI actionURI, boolean assertedNonTimed) {
    super(msb, actionURI, assertedNonTimed);
    init(agent, action);
  }

  /**
   * Constructor for timed actions
   * 
   * @param agent the agent performing the action
   * @param action the action the agent performs
   * @param msb the model state broker
   * @param actionURI the URI of the action in the schedule ontology
   * @param time the time at which the action is to start
   * @throws ScheduleException
   */
  public IndividualAction(URI agent, Action action, ModelStateBroker msb, URI actionURI, double time)
      throws ScheduleException {
    super(msb, actionURI, time);
    init(agent, action);
  }

  /**
   * <!-- init -->
   * 
   * Initialise the action
   * 
   * @param agent the agent performing the action
   * @param action the action the agent performs
   */
  private void init(URI agent, Action action) {
    this.action = action;
    this.agent = agent;
  }

  /*
   * (non-Javadoc)
   * 
   * @see uk.ac.hutton.obiama.model.AbstractScheduledActivity#step()
   */
  public void stepImpl() throws IntegrationInconsistencyException {
    stepNoUpdate();
    if(allCreators()) msb.updateCreators();
    else
      msb.update();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * uk.ac.hutton.obiama.model.AbstractScheduledNoUpdateActivity#stepNoUpdate
   * ()
   */
  public void stepNoUpdate() throws IntegrationInconsistencyException {
    action.step(agent);
  }

  /**
   * <!-- getActionSet -->
   * 
   * Return the action as a set
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
