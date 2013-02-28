/* uk.ac.hutton.obiama.action: CreateNAgentsAction.java
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
package uk.ac.hutton.obiama.action;

import java.net.URI;

import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.msb.Concept;

/**
 * <!-- CreateNAgentsAction -->
 * 
 * Action to create a specified number of agents.
 *
 * @author Gary Polhill
 */
public class CreateNAgentsAction extends AbstractAction implements Action {
  public static final URI ONTOLOGY_URI = URI.create(Action.BUILT_IN_ACTION_PATH + "CreateNAgents.owl");
  public static final URI CREATED_AGENT_URI = URI.create(ONTOLOGY_URI + "#CreatedThing");
  
  public static final String N_AGENTS_PARAMETER = "nAgents";
  
  /**
   * The class to create agents of
   */
  protected Concept createdAgentClass;
  
  /**
   * Number of agents to create
   */
  ActionParameter nAgents;

  /**
   * Initialise the parameters
   */
  public CreateNAgentsAction() {
    nAgents = new ActionParameter(N_AGENTS_PARAMETER, Integer.class, "Number of agents to create");
  }

  /**
   * <!-- step -->
   * 
   * Create the agents
   *
   * @see uk.ac.hutton.obiama.action.Action#step(java.net.URI)
   * @param individual
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void step(URI individual) throws IntegrationInconsistencyException {
    System.out.println("Creating " + nAgents.getParameter() + " agent(s)");
    for(int i = 1; i <= nAgents.getIntParameter(); i++) {
      createdAgentClass.createInstance();
    }
  }

  /**
   * <!-- initialise -->
   *
   * @see uk.ac.hutton.obiama.action.AbstractAction#initialise()
   * @throws IntegrationInconsistencyException
   */
  @Override
  protected void initialise() throws IntegrationInconsistencyException {
    createdAgentClass = msb.getConcept(getURIFor(CREATED_AGENT_URI), this, null, null);
    addConcepts(createdAgentClass);
  }

}
