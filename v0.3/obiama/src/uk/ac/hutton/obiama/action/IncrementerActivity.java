/*
 * uk.ac.hutton.obiama.action: IncrementerActivity.java
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

import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.msb.Value;
import uk.ac.hutton.obiama.msb.Var;

/**
 * <!-- IncrementerActivity -->
 * 
 * An action to increment a variable that takes no parameters. The variable to
 * be incremented is configured through the model structure ontology and/or
 * through using the base and extension URIs of the action.
 * 
 * @author Gary Polhill
 */
public class IncrementerActivity extends AbstractAction {
  protected Var incrementedVar;
  public static final URI INCREMENTED_PROPERTY_URI =
    URI.create(Action.BUILT_IN_ACTION_PATH + "Incrementer.owl#incrementedProperty");
  public static final URI INCREMENTED_PROPERTY_DOMAIN_URI =
    URI.create(Action.BUILT_IN_ACTION_PATH + "Incrementer.owl#IncrementedThing");

  /**
   * <!-- initialise -->
   * 
   * Get the incremented variable
   * 
   * @see uk.ac.hutton.obiama.action.AbstractAction#initialise()
   * @throws IntegrationInconsistencyException
   */
  @Override
  protected void initialise() throws IntegrationInconsistencyException {
    incrementedVar =
      msb.getVariableName(getURIFor(INCREMENTED_PROPERTY_URI), getURIFor(INCREMENTED_PROPERTY_DOMAIN_URI),
          XSDVocabulary.INT, this);
  }

  /**
   * <!-- step -->
   * 
   * Increment the variable
   * 
   * @see uk.ac.hutton.obiama.action.Action#step(java.net.URI)
   * @param individual
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void step(URI individual) throws IntegrationInconsistencyException {
    Value<Integer> value = incrementedVar.getExistingValueFor(individual);
    value.set(value.get() + 1);
  }

}
