/*
 * uk.ac.hutton.obiama.model: OrderedActionForEach.java
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
import java.util.LinkedList;
import java.util.Set;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.NeedDataGotObjectPropertyException;
import uk.ac.hutton.obiama.exception.NeedFunctionalGotNonFunctionalPropertyException;
import uk.ac.hutton.obiama.exception.ScheduleException;
import uk.ac.hutton.obiama.msb.Instance;
import uk.ac.hutton.obiama.msb.ModelStateBroker;
import uk.ac.hutton.obiama.msb.Var;

/**
 * OrderedActionForEach
 * 
 * An action performed by each member of a class of agents, in an order
 * determined by a functional data property
 * 
 * @author Gary Polhill
 */
public class OrderedActionForEach extends RandomOrderActionForEach {
  /**
   * Whether to sort agents in ascending order of the property
   */
  protected boolean sortAscending;

  /**
   * Var corresponding to the property the agents are to be sorted on
   */
  protected Var var;

  /**
   * Constructor, which will check that the concept exists in the ontology, that
   * it is in the domain of the property, and that the property is a functional
   * data property.
   * 
   * @param agentClass Class of agents performing the action
   * @param action The action they are to perform
   * @param msb The Model State Broker
   * @param actionURI The URI of the action in the schedule ontology
   * @param assertedNonTimed <code>true</code> if the action is asserted
   *          non-timed
   * @param sortProperty The property determining the order the agents perform
   *          the action
   * @param sortAscending Whether the agents run in ascending (<code>true</code>
   *          ) or descending (<code>false</code>) order of the sortProperty.
   * @throws IntegrationInconsistencyException
   */
  OrderedActionForEach(URI agentClass, Action action, ModelStateBroker msb, URI actionURI, boolean assertedNonTimed,
      URI sortProperty, boolean sortAscending) throws IntegrationInconsistencyException {
    super(agentClass, action, msb, actionURI, assertedNonTimed);
    init(agentClass, action, sortProperty, sortAscending);
  }

  /**
   * Constructor for timed events
   * 
   * @param agentClass The class of agents performing the action
   * @param action The action they are to perform
   * @param msb The Model State Broker
   * @param actionURI The URI of the action in the schedule ontology
   * @param sortProperty The property determining the order the agents perform
   *          the action
   * @param sortAscending Whether the agents run in ascending (<code>true</code>
   *          ) or descending (<code>false</code>) order of the sortProperty.
   * @param time The time at which the action is to start
   * @throws IntegrationInconsistencyException
   * @throws ScheduleException
   */
  OrderedActionForEach(URI agentClass, Action action, ModelStateBroker msb, URI actionURI, URI sortProperty,
      boolean sortAscending, double time) throws IntegrationInconsistencyException, ScheduleException {
    super(agentClass, action, msb, actionURI, time);
    init(agentClass, action, sortProperty, sortAscending);
  }

  /**
   * <!-- init -->
   * 
   * Initialise the action
   * 
   * @param agentClass The class of agents performing the action
   * @param action The action they are to perform
   * @param sortProperty The property determining the order the agents perform
   *          the action
   * @param sortAscending Whether the agents run in ascending (<code>true</code>
   *          ) or descending (<code>false</code>) order of the sortProperty
   * @throws IntegrationInconsistencyException
   */
  private void init(URI agentClass, Action action, URI sortProperty, boolean sortAscending)
      throws IntegrationInconsistencyException {
    this.sortAscending = sortAscending;
    var = msb.getVariableName(sortProperty, action);
    if(!var.isFunctional()) {
      throw new NeedFunctionalGotNonFunctionalPropertyException(action, sortProperty);
    }
    if(!var.isDataVar()) {
      throw new NeedDataGotObjectPropertyException(action, sortProperty);
    }
    concept = msb.getConcept(agentClass, action, Collections.singleton(var), null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see uk.ac.hutton.obiama.model.ModelCollectiveAction#step()
   */
  @Override
  public void stepImpl() throws IntegrationInconsistencyException {
    Set<Instance> agents = concept.getInstances();
    LinkedList<Instance> agentList = new LinkedList<Instance>(agents);
    Collections.sort(agentList, var);
    if(!sortAscending) Collections.reverse(agentList);
    for(Instance agent: agentList) {
      action.step(agent.getURI());
      msb.update();
    }
  }

}
