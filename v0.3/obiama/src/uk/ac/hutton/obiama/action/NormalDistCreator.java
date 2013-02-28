/*
 * uk.ac.hutton.obiama.action: NormalDistCreator.java
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
import uk.ac.hutton.obiama.msb.Instance;
import uk.ac.hutton.obiama.msb.Var;
import uk.ac.hutton.obiama.random.RNG;
import uk.ac.hutton.obiama.random.RNGFactory;

/**
 * <!-- NormalDistCreator -->
 * 
 * @author Gary Polhill
 */
public class NormalDistCreator extends AbstractCreator implements Creator {
  public static final URI ONTOLOGY_URI = URI.create(BUILT_IN_CREATOR_PATH + "NormalDist.owl");
  public static final URI NORMAL_PROPERTY_URI = URI.create(ONTOLOGY_URI + "#normallyDistributedProperty");

  protected Var normalProperty;

  ActionParameter mean;
  ActionParameter variance;

  RNG rng;

  /**
   * 
   */
  public NormalDistCreator() {
    mean = new ActionParameter("mean", double.class, "mean of the normally distributed data property");
    variance = new ActionParameter("variance", double.class, "variance of the normally distributed data property");
    rng = RNGFactory.getRNG();
  }

  /**
   * <!-- initialise -->
   * 
   * @see uk.ac.hutton.obiama.action.AbstractAction#initialise()
   * @throws IntegrationInconsistencyException
   */
  @Override
  protected void initialise() throws IntegrationInconsistencyException {
    normalProperty = msb.getVariableName(NORMAL_PROPERTY_URI, XSDVocabulary.DOUBLE, this);
    addVars(normalProperty);
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
    creation.setNewProperty(normalProperty, rng.sampleNormal(mean.getDoubleParameter(), variance.getDoubleParameter()));
  }

}
