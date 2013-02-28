/*
 * uk.ac.hutton.obiama.model: SpaceOntology.java Copyright (C) 2013 The James Hutton
 * Institute
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
 * SpaceOntology
 * 
 * Class for the main space ontology. This simply defines the Location class,
 * and the locatedAt, connectedTo and overlaps properties.
 * 
 * @author Gary Polhill
 */
public class SpaceOntology extends AbstractOntology {
  public static final URI ONTOLOGY_URI = URI.create("http://www.hutton.ac.uk/obiama/ontologies/space.owl");

  // Classes

  public static final URI LOCATION_URI = URI.create(ONTOLOGY_URI + "#Location");
  public static final URI SPACE_URI = URI.create(ONTOLOGY_URI + "#Space");

  // Object properties

  public static final URI LOCATED_AT_URI = URI.create(ONTOLOGY_URI + "#locatedAt");
  public static final URI LOCATION_OF_URI = URI.create(ONTOLOGY_URI + "#locationOf");
  public static final URI CONNECTED_TO_URI = URI.create(ONTOLOGY_URI + "#connectedTo");
  public static final URI OVERLAPS_URI = URI.create(ONTOLOGY_URI + "#overlaps");
  public static final URI CONTAINS_LOCATIONS_URI = URI.create(ONTOLOGY_URI + "#containsLocations");
  public static final URI SPATIAL_PART_OF_URI = URI.create(ONTOLOGY_URI + "#spatialPartOf");
  public static final URI SPATIAL_PROPER_PART_OF_URI = URI.create(ONTOLOGY_URI + "#spatialProperPartOf");
  public static final URI ALL_LOCATIONS_URI = URI.create(ONTOLOGY_URI + "#allLocations");

  /**
   * @throws OWLOntologyChangeException
   * @throws OWLOntologyCreationException
   */
  public SpaceOntology() throws OWLOntologyCreationException, OWLOntologyChangeException {
    super(ONTOLOGY_URI);
  }

  /**
   * @param physical Location to save the ontology
   * @throws UnknownOWLOntologyException
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   * @throws OWLOntologyStorageException
   */
  public SpaceOntology(URI physical) throws UnknownOWLOntologyException, OWLOntologyCreationException,
      OWLOntologyChangeException, OWLOntologyStorageException {
    super(ONTOLOGY_URI, physical);
  }

  /**
   * @param spp OWL species to use for the ontology
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  public SpaceOntology(OWLSpecies spp) throws OWLOntologyCreationException, OWLOntologyChangeException {
    super(ONTOLOGY_URI, spp);
  }

  /**
   * @param physical Location to save the ontology
   * @param spp OWL species to use for the ontology
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   * @throws OWLOntologyStorageException
   */
  public SpaceOntology(URI physical, OWLSpecies spp) throws OWLOntologyCreationException, OWLOntologyChangeException,
      OWLOntologyStorageException {
    super(ONTOLOGY_URI, physical, spp);
  }

  /**
   * @param related Ontology this one is to be in the same group as
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  public SpaceOntology(AbstractOntology related) throws OWLOntologyCreationException, OWLOntologyChangeException {
    super(ONTOLOGY_URI, related);
  }

  /**
   * <!-- buildOntology -->
   * 
   * Build the ontology
   * 
   * @see uk.ac.hutton.obiama.model.AbstractOntology#buildOntology()
   */
  @Override
  protected void buildOntology() {
    imports(ObiamaOntology.ONTOLOGY_URI);

    // Entity declarations

    declareClass(LOCATION_URI, SPACE_URI);

    declareObjectProperty(LOCATED_AT_URI, LOCATION_OF_URI, CONNECTED_TO_URI, OVERLAPS_URI, CONTAINS_LOCATIONS_URI,
        SPATIAL_PART_OF_URI, SPATIAL_PROPER_PART_OF_URI, ALL_LOCATIONS_URI);

    // Disjoint classes

    disjointClasses(LOCATION_URI, SPACE_URI);

    // #locatedAt

    objectPropertyRange(LOCATED_AT_URI, LOCATION_URI);

    // #locationOf

    objectPropertyDomain(LOCATION_OF_URI, LOCATION_URI);
    objectPropertyInverse(LOCATED_AT_URI, LOCATION_OF_URI);

    // #connectedTo

    objectPropertyDomain(CONNECTED_TO_URI, LOCATION_URI);
    objectPropertyRange(CONNECTED_TO_URI, LOCATION_URI);
    objectPropertySymmetric(CONNECTED_TO_URI);

    // #overlaps

    objectPropertyDomain(OVERLAPS_URI, LOCATION_URI);
    objectPropertyRange(OVERLAPS_URI, LOCATION_URI);

    // #containsLocations

    objectPropertyDomain(CONTAINS_LOCATIONS_URI, SPACE_URI);
    objectPropertyRange(CONTAINS_LOCATIONS_URI, LOCATION_URI);

    // #spatialPartOf

    subObjectPropertyOf(SPATIAL_PART_OF_URI, ObiamaOntology.PART_OF_URI);
    objectPropertyDomain(SPATIAL_PART_OF_URI, LOCATION_URI);
    objectPropertyRange(SPATIAL_PART_OF_URI, LOCATION_URI);
    objectPropertyReflexive(SPATIAL_PART_OF_URI);

    // #spatialProperPartOf

    subObjectPropertyOf(SPATIAL_PROPER_PART_OF_URI, SPATIAL_PART_OF_URI, ObiamaOntology.PROPER_PART_OF_URI);
    // Shouldn't be necessary to state the domain and range of spatial proper
    // part, as these will be picked up by spatial part. Also shouldn't need to
    // say it's irreflexive, because this will be picked up by proper part.
    objectPropertyTransitive(SPATIAL_PROPER_PART_OF_URI, SPATIAL_PART_OF_URI);
    
    // #allLocations
    
    superPropertyOfChain(ALL_LOCATIONS_URI, LOCATED_AT_URI, SPATIAL_PART_OF_URI);
  }

  /**
   * <!-- main -->
   * 
   * Call this class from the command line to create the ontology and save it.
   * 
   * @param args
   */
  public static void main(String[] args) {
    try {
      createOntology(SpaceOntology.class, args);
    }
    catch(UsageException e) {
      ErrorHandler.fatal(e, "parsing command line arguments");
    }
  }

}
