/*
 * uk.ac.hutton.obiama.exception: BrokenUniqueNameAssumptionException.java
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

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.model.OWLIndividual;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.action.Process;

/**
 * BrokenUniqueNameAssumptionException
 * 
 * OBIAMA makes the Unique Name Assumption, which OWL does not make. This
 * exception is raised when it becomes clear that a number of individuals have
 * been asserted or inferred to be the same.
 * 
 * @author Gary Polhill
 */
public class BrokenUniqueNameAssumptionException extends IntegrationInconsistencyException {
  /**
   * Serial number
   */
  private static final long serialVersionUID = 1L;

  /**
   * The set of URIs that have been asserted or inferred to be the same
   * individual
   */
  Set<URI> individuals;

  /**
   * Constructor allowing the action and set of individuals to be passed in
   * 
   * @param process
   * @param individuals
   */
  public BrokenUniqueNameAssumptionException(Process process, Set<OWLIndividual> owlIndividuals) {
    super(process);
    this.individuals = new HashSet<URI>();
    for(OWLIndividual individual: owlIndividuals) {
      this.individuals.add(individual.getURI());
    }
  }

  /**
   * <!-- getErrorMessage -->
   * 
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  protected String getErrorMessage() {
    return "Unique name assumption (for individuals) broken. Can prove the following are the same individual: "
      + individuals;
  }

}
