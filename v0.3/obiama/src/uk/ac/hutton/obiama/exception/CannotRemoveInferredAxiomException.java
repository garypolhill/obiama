/*
 * uk.ac.hutton.obiama.exception: CannotRemoveInferredAxiomException.java
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
package uk.ac.hutton.obiama.exception;

import uk.ac.hutton.obiama.action.Action;

/**
 * <!-- CannotRemoveInferredAxiomException -->
 * 
 * An exception caused when the Model State Broker tries to remove an inferred
 * axiom, but is unable to do so in any trivial manner. More specifically,
 * suppose an action has a property B that it operates on, and the model
 * structure ontology contains a property A asserted equivalent to B. The model
 * state ontology contains an assertion that A = X for individual J, and it is
 * thus inferred by the reasoner that J has B = X. The action causes a change
 * such that J does not have B = X. Provided the model state ontology says that
 * J has A = X and the model structure ontology that A and B are equivalent, the
 * required change can be implemented by removing the assertion J has A = X.
 * However, if any other set of assertions lead to the inference that J has B =
 * X, then this exception will be thrown.
 * 
 * @author Gary Polhill
 */
public class CannotRemoveInferredAxiomException extends IntegrationInconsistencyException {
  /**
   * Serialisation number
   */
  private static final long serialVersionUID = 4854993409174911321L;

  /**
   * String representation of axiom that could not be removed
   */
  String axiom;

  /**
   * @param originator Action attempting to remove the axiom
   * @param axiom String representation of the axiom that could not be removed
   */
  public CannotRemoveInferredAxiomException(Action originator, String axiom) {
    super(originator);
    this.axiom = axiom;
  }

  /**
   * <!-- getErrorMessage -->
   * 
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return Error message for this exception
   */
  @Override
  protected String getErrorMessage() {
    return "No trivial way to modify asserted state so as to prevent inference of " + axiom;
  }

}
