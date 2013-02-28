/*
 * uk.ac.hutton.obiama.msb: ObiamaOntology.java
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
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.UsageException;

/**
 * ObiamaOntology
 * 
 * Constants for the OBIAMA ontology
 * 
 * @author Gary Polhill
 */
public final class ObiamaOntology extends AbstractOntology {
  public static final URI ONTOLOGY_PATH = URI.create("http://www.obiama.org/ontology");

  public static final URI ONTOLOGY_URI = URI.create(ONTOLOGY_PATH + "/obiama.owl");

  // Classes

  public static final URI QUERY_URI = URI.create(ONTOLOGY_URI + "#Query");
  public static final URI CREATOR_URI = URI.create(ONTOLOGY_URI + "#Creator");
  public static final URI ACTION_URI = URI.create(ONTOLOGY_URI + "#Action");
  public static final URI AGENT_URI = URI.create(ONTOLOGY_URI + "#Agent");
  public static final URI ACTANT_URI = URI.create(ONTOLOGY_URI + "#Actant");
  public static final URI EX_AGENT_URI = URI.create(ONTOLOGY_URI + "#ExAgent");
  public static final URI GC_AGENT_URI = URI.create(ONTOLOGY_URI + "#GCAgent");

  // Annotation properties

  public static final URI HAS_CREATOR_URI = URI.create(ONTOLOGY_URI + "#hasCreator");
  public static final URI IMPLEMENTED_BY_URI = URI.create(ONTOLOGY_URI + "#implementedBy");
  public static final URI HAS_QUERY_URI = URI.create(ONTOLOGY_URI + "#hasQuery");
  public static final URI HAS_ACTION_URI = URI.create(ONTOLOGY_URI + "#hasAction");

  // Object properties

  public static final URI PART_OF_URI = URI.create(ONTOLOGY_URI + "#partOf");
  public static final URI PROPER_PART_OF_URI = URI.create(ONTOLOGY_URI + "#properPartOf");
  public static final URI MOVEMENT_PART_OF_URI = URI.create(ONTOLOGY_URI + "#movementPartOf");
  public static final URI CREATION_PART_OF_URI = URI.create(ONTOLOGY_URI + "#creationPartOf");
  public static final URI DESTRUCTION_PART_OF_URI = URI.create(ONTOLOGY_URI + "#destructionPartOf");
  public static final URI COPYING_PART_OF_URI = URI.create(ONTOLOGY_URI + "#copyingPartOf");
  // ... other functional senses of 'proper part of' could go here
  public static final URI EXCLUSIVE_PROPER_PART_OF_URI = URI.create(ONTOLOGY_URI + "#exclusiveProperPartOf");
  public static final URI HAS_PART_URI = URI.create(ONTOLOGY_URI + "#hasPart");
  public static final URI ASSOCIATED_WITH_URI = URI.create(ONTOLOGY_URI + "#associatedWith");
  public static final URI ACTANT_OBJECT_PROPERTY_URI = URI.create(ONTOLOGY_URI + "#actantObjectProperty");
  public static final URI AGENT_OBJECT_PROPERTY_URI = URI.create(ONTOLOGY_URI + "#agentObjectProperty");

  // Data properties

  public static final URI AGENT_DATA_PROPERTY_URI = URI.create(ONTOLOGY_URI + "#agentDataProperty");
  public static final URI WAS_A_URI = URI.create(ONTOLOGY_URI + "#wasA");

  // Individuals

  public static final URI EXOGENOUS_AGENT_URI = ScheduleOntology.BOOTSTRAPPER_URI;
  public static final URI GLOBAL_AGENT_URI = URI.create(ONTOLOGY_URI + "#globalAgent"); // Should
                                                                                        // this
                                                                                        // be
                                                                                        // globalActant?

  /**
   * @param physical
   * @throws OWLOntologyCreationException
   * @throws UnknownOWLOntologyException
   * @throws OWLOntologyChangeException
   * @throws OWLOntologyStorageException
   */
  public ObiamaOntology(URI physical) throws OWLOntologyCreationException, UnknownOWLOntologyException,
      OWLOntologyChangeException, OWLOntologyStorageException {
    super(ONTOLOGY_URI, physical);
  }

  /*
   * (non-Javadoc)
   * 
   * @see uk.ac.hutton.obiama.model.AbstractOntology#buildOntology()
   */
  @Override
  protected void buildOntology() {
    imports(ScheduleOntology.ONTOLOGY_URI);

    // Main OBIAMA ontology

    // Mereology: 'partOf' is used for any sense of part of, and 'properPartOf'
    // for all senses of part of.
    objectPropertyInverse(PART_OF_URI, HAS_PART_URI);
    objectPropertyReflexive(PART_OF_URI);

    subObjectPropertyOf(PROPER_PART_OF_URI, PART_OF_URI);
    objectPropertyIrreflexive(PROPER_PART_OF_URI);
    objectPropertyAntiSymmetric(PROPER_PART_OF_URI);

    subObjectPropertyOf(MOVEMENT_PART_OF_URI, PROPER_PART_OF_URI);
    subObjectPropertyOf(DESTRUCTION_PART_OF_URI, PROPER_PART_OF_URI);
    subObjectPropertyOf(CREATION_PART_OF_URI, PROPER_PART_OF_URI);
    subObjectPropertyOf(COPYING_PART_OF_URI, PROPER_PART_OF_URI);

    subObjectPropertyOf(EXCLUSIVE_PROPER_PART_OF_URI, MOVEMENT_PART_OF_URI, DESTRUCTION_PART_OF_URI,
        CREATION_PART_OF_URI, COPYING_PART_OF_URI);
    if(spp.isFull()) objectPropertyFunctional(EXCLUSIVE_PROPER_PART_OF_URI);
    objectPropertyTransitive(PART_OF_URI, PROPER_PART_OF_URI, MOVEMENT_PART_OF_URI, DESTRUCTION_PART_OF_URI,
        CREATION_PART_OF_URI, COPYING_PART_OF_URI, EXCLUSIVE_PROPER_PART_OF_URI);

    subClassOf(AGENT_URI, ACTANT_URI);
    disjointClasses(AGENT_URI, EX_AGENT_URI);
    equivalentClasses(
        GC_AGENT_URI,
        objectIntersectionOf(namedClass(EX_AGENT_URI), objectAllRestriction(ASSOCIATED_WITH_URI, EX_AGENT_URI),
            objectComplementOf(objectSomeRestriction(ACTANT_OBJECT_PROPERTY_URI, OWL_THING_URI(spp)))));
    objectPropertySymmetric(ASSOCIATED_WITH_URI, ACTANT_OBJECT_PROPERTY_URI, AGENT_OBJECT_PROPERTY_URI);

    declareDataProperty(AGENT_DATA_PROPERTY_URI);
    dataPropertyRange(WAS_A_URI, XSDVocabulary.ANY_URI);

    declareIndividual(EXOGENOUS_AGENT_URI);
    individualHasClass(EXOGENOUS_AGENT_URI, AGENT_URI);

    // Annotations

    declareObjectProperty(HAS_CREATOR_URI);
    objectPropertyRange(HAS_CREATOR_URI, ScheduleOntology.NON_TIMED_SCHEDULE_URI);
    objectPropertyFunctional(HAS_CREATOR_URI);
  }

  public static void main(String args[]) {
    try {
      createOntology(ObiamaOntology.class, args);
    }
    catch(UsageException e) {
      ErrorHandler.fatal(e, "parsing command line arguments");
    }
  }

}
