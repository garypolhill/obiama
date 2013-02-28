/*
 * uk.ac.hutton.obiama.action:
 * StochasticNormalAutocorrelativeTimeSeriesAction.java
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
import uk.ac.hutton.obiama.random.RNG;
import uk.ac.hutton.obiama.random.RNGFactory;

/**
 * <!-- StochasticNormalAutocorrelativeTimeSeriesAction -->
 * 
 * @author Gary Polhill
 */
public class StochasticNormalAutocorrelativeTimeSeriesAction extends AbstractAction {
  public static URI ONTOLOGY_URI = URI.create(BUILT_IN_ACTION_PATH + "StochasticNormalAutocorrelativeTimeSeries.owl");
  public static URI TIME_SERIES_URI = URI.create(ONTOLOGY_URI + "#timeSeries");

  protected ActionParameter rngClass;
  protected ActionParameter rngParam;
  protected ActionParameter mean;
  protected ActionParameter variance;

  public static String RNG_CLASS_DEFAULT = "#UseGlobal";

  Var timeSeries;

  private RNG rng;

  /**
   * 
   */
  public StochasticNormalAutocorrelativeTimeSeriesAction() {
    rngClass =
      new ActionParameter("RNGclass", String.class, RNG_CLASS_DEFAULT,
          "Class to use for random number generator, or \"" + RNG_CLASS_DEFAULT + "\" to use the global RNG");
    rngParam =
      new ActionParameter("RNGparameters", long.class,
          "Parameters (if any) to use for random number generator (if applicable)");
    mean = new ActionParameter("mean", double.class, "Mean of normal distribution");
    variance = new ActionParameter("variance", double.class, "Variance of normal distribution");
  }

  /**
   * <!-- initialise -->
   * 
   * @see uk.ac.hutton.obiama.action.AbstractAction#initialise()
   * @throws IntegrationInconsistencyException
   */
  @Override
  protected void initialise() throws IntegrationInconsistencyException {
    if(rngClass.getParameter().equals(RNG_CLASS_DEFAULT)) {
      rng = RNGFactory.getRNG();
    }
    else if(rngParam.parameterSet()) {
      rng = RNGFactory.getNewRNG(rngClass.getParameter(), rngParam.getParameter());
    }
    else {
      // TODO throw exception
    }
    timeSeries = msb.getVariableName(TIME_SERIES_URI, XSDVocabulary.DOUBLE, this);
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
    Value<Double> value = timeSeries.getExistingValueFor(individual);
    value.set(value.get() + rng.sampleNormal(mean.getDoubleParameter(), variance.getDoubleParameter()));
  }

}
