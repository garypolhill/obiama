/*
 * uk.ac.hutton.obiama.action: UniformRandomInstanceChooser.java
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
import uk.ac.hutton.obiama.msb.Value;
import uk.ac.hutton.obiama.msb.Var;
import uk.ac.hutton.obiama.random.RNG;
import uk.ac.hutton.obiama.random.RNGFactory;

/**
 * <!-- UniformRandomInstanceChooser -->
 * 
 * @author Gary Polhill
 */
public class UniformRandomInstanceChooser extends AbstractAction {

  public static final URI CHOICE_URI = URI.create(Action.BUILT_IN_ACTION_PATH
    + "UniformRandomInstanceChooser.owl#choice");

  public static final URI CHOSEN_THING_URI = URI.create(Action.BUILT_IN_ACTION_PATH
    + "UniformRandomInstanceChooser.owl#ChosenThing");

  protected Concept chosenThing;

  protected Var choice;

  protected RNG rng;

  /**
   * <!-- initialise -->
   * 
   * @see uk.ac.hutton.obiama.action.AbstractAction#initialise()
   * @throws IntegrationInconsistencyException
   */
  @Override
  protected void initialise() throws IntegrationInconsistencyException {
    choice = getVar(CHOICE_URI);
    
    chosenThing = getConcept(CHOSEN_THING_URI);
    
    rng = RNGFactory.getRNG();
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
    Value<URI> choiceValue = choice.getValueFor(individual);
    
    Set<Instance> instances = chosenThing.getInstances();
    
    Instance[] allInstances = instances.toArray(new Instance[0]);
    
    choiceValue.set(allInstances[rng.sampleUniform(0, allInstances.length - 1)]);
  }
}
