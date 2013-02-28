/*
 * uk.ac.hutton.obiama.action: SetPropertyValueCreator.java
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

import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.msb.Instance;
import uk.ac.hutton.obiama.msb.Var;

/**
 * <!-- SetPropertyValueCreator -->
 * 
 * @author Gary Polhill
 */
public class SetPropertyValueCreator extends AbstractCreator {

  public static final URI ONTOLOGY_URI = URI.create(Creator.BUILT_IN_CREATOR_PATH + "SetPropertyValueCreator.owl");

  public static final URI PROPERTY_URI = URI.create(ONTOLOGY_URI + "#property");

  protected Var property;

  ActionParameter value;

  public static final String VALUE_PARAMETER_NAME = "value";

  public SetPropertyValueCreator() {
    value = new ActionParameter(VALUE_PARAMETER_NAME, String.class, "The value to set the property to");
  }

  /**
   * <!-- initialise -->
   * 
   * @see uk.ac.hutton.obiama.action.AbstractAction#initialise()
   * @throws IntegrationInconsistencyException
   */
  @Override
  protected void initialise() throws IntegrationInconsistencyException {
    property = getVar(PROPERTY_URI);
  }

  /**
   * <!-- step -->
   * 
   * @see uk.ac.hutton.obiama.action.AbstractCreator#step(java.net.URI,
   *      uk.ac.hutton.obiama.msb.Instance)
   * @param actor
   * @param creation
   * @throws IntegrationInconsistencyException
   */
  @Override
  protected void step(URI actor, Instance creation) throws IntegrationInconsistencyException {
    creation.setNewPropertyString(property, value.getParameter());
  }

}
