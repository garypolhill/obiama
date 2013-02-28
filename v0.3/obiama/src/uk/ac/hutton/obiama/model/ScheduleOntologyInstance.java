/*
 * uk.ac.hutton.obiama.model: ScheduleOntologyInstance.java
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

import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;

import uk.ac.hutton.obiama.exception.ErrorHandler;

/**
 * <!-- ScheduleOntologyInstance -->
 * 
 * A specific instance of the schedule ontology, consisting of a series of
 * assertions describing a schedule.
 * 
 * @author Gary Polhill
 */
public class ScheduleOntologyInstance extends ScheduleOntology {

  /**
   * Build a schedule ontology instance from the specified ontology URI
   * 
   * @param ontologyURI URI of the ontology to build the schedule from
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  public ScheduleOntologyInstance(URI ontologyURI) throws OWLOntologyCreationException, OWLOntologyChangeException {
    super(ontologyURI, new ScheduleOntology());
  }

  public ScheduleOntologyInstance(URI ontologyURI, OWLOntology importIndividualsFromOntology)
      throws OWLOntologyCreationException, OWLOntologyChangeException {
    super(ontologyURI, importIndividualsFromOntology, new ScheduleOntology());
  }

  /**
   * Build a schedule ontology instance of the specified OWL sublanguage from
   * the given URI
   * 
   * @param ontologyURI URI of the ontology to build the schedule from
   * @param spp OWL sublanguage
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  public ScheduleOntologyInstance(URI ontologyURI, OWLSpecies spp) throws OWLOntologyCreationException,
      OWLOntologyChangeException {
    super(ontologyURI, new ScheduleOntology(spp));
  }

  /**
   * <!-- buildOntology -->
   * 
   * Build the ontology--import the schedule ontology, and then load in all the
   * A-box axioms from the schedule instance ontology.
   * 
   * @see uk.ac.hutton.obiama.model.ScheduleOntology#buildOntology()
   */
  @Override
  protected void buildOntology() {
    imports(ScheduleOntology.ONTOLOGY_URI);
    try {
      importIndividuals(getURI());
    }
    catch(OWLOntologyCreationException e) {
      ErrorHandler.redo(e, "loading schedule ontology " + getURI());
    }
  }

}
