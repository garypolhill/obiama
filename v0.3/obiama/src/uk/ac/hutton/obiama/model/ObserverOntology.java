/*
 * uk.ac.hutton.obiama.model: ObserverOntology.java
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

import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyStorageException;
import org.semanticweb.owl.model.UnknownOWLOntologyException;

import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.UsageException;

/**
 * <!-- ObserverOntology -->
 * 
 * @author Gary Polhill
 */
public class ObserverOntology extends AbstractOntology {

  public static final URI ONTOLOGY_URI = URI.create("http://www.obiama.org/ontology/observer.owl");
  
  // Individuals
  
  public static final URI OBSERVER_URI = URI.create(ONTOLOGY_URI + "#observer");

  /**
   * @param physical
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   * @throws OWLOntologyStorageException
   * @throws UnknownOWLOntologyException
   */
  ObserverOntology(URI physical) throws OWLOntologyCreationException, OWLOntologyChangeException,
      UnknownOWLOntologyException, OWLOntologyStorageException {
    super(ONTOLOGY_URI, physical);
  }

  /**
   * <!-- buildOntology -->
   * 
   * @see uk.ac.hutton.obiama.model.AbstractOntology#buildOntology()
   */
  @Override
  protected void buildOntology() {
    imports(ObiamaOntology.ONTOLOGY_URI);
    
    individualHasClass(OBSERVER_URI, ObiamaOntology.AGENT_URI);
  }

  public static void main(String[] args) {
    try {
      createOntology(ObserverOntology.class, args);
    }
    catch(UsageException e) {
      ErrorHandler.fatal(e, "parsing command line arguments");
    }
  }
}
