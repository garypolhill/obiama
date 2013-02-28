/*
 * uk.ac.hutton.obiama.model: RandomOrderActionForEach.java
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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.action.Creator;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ScheduleException;
import uk.ac.hutton.obiama.msb.Concept;
import uk.ac.hutton.obiama.msb.Instance;
import uk.ac.hutton.obiama.msb.ModelStateBroker;
import uk.ac.hutton.obiama.random.RNGFactory;

/**
 * RandomOrderActionForEach
 * 
 * Actions performed by a group of agents in random order.
 * 
 * @author Gary Polhill
 */
public class RandomOrderActionForEach extends AbstractScheduledAction {
  /**
   * The concept in the model structure ontology determining the set of agents
   * performing the action
   */
  protected Concept concept;

  /**
   * The action the agents are to perform
   */
  protected Action action;

  /**
   * Constructor, which checks that the class exists in the model structure
   * ontology
   * 
   * @param agent The agent performing the action
   * @param action The action
   * @param msb The Model State Broker
   * @param actionURI The URI of the action in the schedule ontology
   * @param assertedNonTimed <code>true</code> if the action is asserted
   *          non-timed
   * @throws IntegrationInconsistencyException
   */
  RandomOrderActionForEach(URI agentClass, Action action, ModelStateBroker msb, URI actionURI, boolean assertedNonTimed)
      throws IntegrationInconsistencyException {
    super(msb, actionURI, assertedNonTimed);
    init(agentClass, action);
  }

  /**
   * Constructor for timed actions
   * 
   * @param agentClass the class of agents performing the action
   * @param action the action they are to perform
   * @param msb the model state broker
   * @param actionURI the URI of the action in the schedule ontology
   * @param time the time at which the action is to start
   * @throws ScheduleException
   */
  public RandomOrderActionForEach(URI agentClass, Action action, ModelStateBroker msb, URI actionURI, double time)
      throws IntegrationInconsistencyException, ScheduleException {
    super(msb, actionURI, time);
    init(agentClass, action);
  }

  /**
   * <!-- init -->
   * 
   * Initialise the action
   * 
   * @param agentClass the class of agents performing the action
   * @param action the action they are to perform
   * @throws IntegrationInconsistencyException
   */
  private void init(URI agentClass, Action action) throws IntegrationInconsistencyException {
    this.action = action;
    this.concept = msb.getConcept(agentClass, action, null, null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see uk.ac.hutton.obiama.model.AbstractScheduledActivity#step()
   */
  @Override
  public void stepImpl() throws IntegrationInconsistencyException {
    Set<Instance> agents = concept.getInstances();
    LinkedList<Instance> agentList = new LinkedList<Instance>(agents);
    RNGFactory.getRNG().shuffle(agentList);

    for(Instance agent: agentList) {
      action.step(agent.getURI());
      msb.update();
    }
  }

  private void stepAlternative() throws IntegrationInconsistencyException {
    Set<Instance> agents = concept.getInstances();
    LinkedList<Instance> agentList = new LinkedList<Instance>(agents);
    Collections.shuffle(agentList);
    Set<Instance> stepped = new HashSet<Instance>();
    while(agentList.size() > 0) {
      Instance agent = agentList.removeFirst();
      action.step(agent.getURI());
      msb.update();
      stepped.add(agent);
      agents = concept.getInstances();
      agents.removeAll(stepped);
      agentList = new LinkedList<Instance>(agents);
      Collections.shuffle(agentList);
    }
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
