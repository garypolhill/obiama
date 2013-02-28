/*
 * uk.ac.hutton.obiama.action: RandomChooser.java
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
import java.util.Set;

import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.msb.Concept;
import uk.ac.hutton.obiama.msb.Instance;
import uk.ac.hutton.obiama.msb.Var;
import uk.ac.hutton.obiama.random.RNG;
import uk.ac.hutton.obiama.random.RNGFactory;
import uk.ac.hutton.util.SetCreator;

/**
 * <!-- UniformRandomChooser -->
 * 
 * @author Gary Polhill
 */
public class UniformRandomChooser extends AbstractAction {

  public static final URI CHOOSER_URI = URI
      .create(Action.BUILT_IN_ACTION_PATH + "UniformRandomChooser.owl#Chooser");
  public static final URI CHOICE_URI = URI.create(Action.BUILT_IN_ACTION_PATH + "UniformRandomChooser.owl#choice");

  public static final URI CHOOSER_QUERY_URI = URI.create(Action.BUILT_IN_ACTION_PATH
    + "UniformRandomChooser.owl#chooserQuery");

  protected Concept selection;

  protected Var choice;
  
  protected RNG rng;
  
  private Query<Set<?>> query;

  /**
   * <!-- initialise -->
   * 
   * @see uk.ac.hutton.obiama.action.AbstractAction#initialise()
   * @throws IntegrationInconsistencyException
   */
  @SuppressWarnings("unchecked")
  @Override
  protected void initialise() throws IntegrationInconsistencyException {
    choice = getVar(CHOICE_URI);
//    selection = getConcept(CHOOSER_URI, SetCreator.createSet(choice), CHOOSER_QUERY_URI, Query.class);
    selection = getConcept(CHOOSER_URI, CHOOSER_QUERY_URI, Query.class);
    rng = RNGFactory.getRNG();
    query = (Query<Set<?>>)selection.getQuery(CHOOSER_QUERY_URI);
  }

  /**
   * <!-- step -->
   * 
   * @see uk.ac.hutton.obiama.action.Action#step(java.net.URI)
   * @param individual
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void step(URI individual) throws IntegrationInconsistencyException {
    Instance instance = selection.getInstance(individual);

    Set<?> values = instance.ask(query, instance);
    int chooser = rng.sampleUniform(1, values.size()) - 1;
    instance.setProperty(choice, values.toArray()[chooser]);
  }

}
