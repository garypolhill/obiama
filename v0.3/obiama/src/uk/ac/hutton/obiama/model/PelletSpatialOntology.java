/*
 * uk.ac.hutton.obiama.model: PelletSpatialOntology.java 
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
 * PelletSpatialOntology
 * 
 * This is the ontology that comes with PelletSpatial 0.1, available from <a
 * href="http://clarkparsia.com/pellet/spatial">http://clarkparsia.com/pellet/
 * spatial</a> (file spatial.rdf).
 * 
 * @author Gary Polhill
 */
public class PelletSpatialOntology extends AbstractOntology {
  public static final URI ONTOLOGY_URI = URI.create("http://clarkparsia.com/pellet/spatial");

  // Object properties

  public static final URI DISCONNECTED_FROM_URI = URI.create(ONTOLOGY_URI + "#disconnectedFrom");
  public static final URI EQUALS_TO_URI = URI.create(ONTOLOGY_URI + "#equalsTo");
  public static final URI EXTERNALLY_CONNECTED_TO_URI = URI.create(ONTOLOGY_URI + "#externallyConnectedTo");
  public static final URI HAS_NON_TANGENTIAL_PROPER_PART_URI = URI.create(ONTOLOGY_URI + "#hasNonTangentialProperPart");
  public static final URI HAS_TANGENTIAL_PROPER_PART_URI = URI.create(ONTOLOGY_URI + "#hasTangentialPropertPart");
  public static final URI NON_TANGENTIAL_PROPER_PART_OF_URI = URI.create(ONTOLOGY_URI + "#nonTangentialProperPartOf");
  public static final URI PARTIALLY_OVERLAPS_URI = URI.create(ONTOLOGY_URI + "#partiallyOverlaps");
  public static final URI TANGENTIAL_PROPER_PART_OF_URI = URI.create(ONTOLOGY_URI + "#tangentialProperPartOf");

  /**
   * @param physical
   * @throws OWLOntologyCreationException
   * @throws UnknownOWLOntologyException
   * @throws OWLOntologyChangeException
   * @throws OWLOntologyStorageException
   */
  public PelletSpatialOntology(URI physical) throws OWLOntologyCreationException, UnknownOWLOntologyException,
      OWLOntologyChangeException, OWLOntologyStorageException {
    super(ONTOLOGY_URI, physical);
  }

  /**
   * @param physical
   * @param spp
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   * @throws OWLOntologyStorageException
   */
  public PelletSpatialOntology(URI physical, OWLSpecies spp) throws OWLOntologyCreationException,
      OWLOntologyChangeException, OWLOntologyStorageException {
    super(ONTOLOGY_URI, physical, spp);
  }

  /**
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  public PelletSpatialOntology() throws OWLOntologyCreationException, OWLOntologyChangeException {
    super(ONTOLOGY_URI);
  }

  /**
   * @param spp
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  public PelletSpatialOntology(OWLSpecies spp) throws OWLOntologyCreationException, OWLOntologyChangeException {
    super(ONTOLOGY_URI, spp);
  }

  /*
   * (non-Javadoc)
   * 
   * This should reproduce the ontology pretty much exactly as it appears in
   * PelletSpatial 0.1
   * 
   * @see uk.ac.hutton.obiama.model.AbstractOntology#buildOntology()
   */
  @Override
  protected void buildOntology() {
    imports(URI.create("http://sqwrl.stanford.edu/ontologies/built-ins/3.4/sqwrl.owl"), URI
        .create("http://swrl.stanford.edu/ontologies/3.3/swrla.owl"));

    declareObjectProperty(DISCONNECTED_FROM_URI, EQUALS_TO_URI, EXTERNALLY_CONNECTED_TO_URI,
        HAS_NON_TANGENTIAL_PROPER_PART_URI, HAS_TANGENTIAL_PROPER_PART_URI, NON_TANGENTIAL_PROPER_PART_OF_URI,
        PARTIALLY_OVERLAPS_URI, TANGENTIAL_PROPER_PART_OF_URI);

    ontologyComment("An ontology that defines the spatial relations supported by PelletSpatial. "
      + "These terms can be used to specify how two regions relate to each other. "
      + "These spatial relations are based on the relations defined in Region Connection Calculus. "
      + "Note that, these relations are pairwise disjoint so two regions cannot be related to each other "
      + "with more than one of these properties.");

    commentObjectProperty(DISCONNECTED_FROM_URI, "R1 is disconnected from R2 if the regions do not share any point.");
    labelObjectProperty(DISCONNECTED_FROM_URI, "disconnected from");

    commentObjectProperty(EQUALS_TO_URI,
        "R1 is equal to R2 if their interior and boundaries are exactly equal to each other.");
    labelObjectProperty(EQUALS_TO_URI, "equals to");

    commentObjectProperty(EXTERNALLY_CONNECTED_TO_URI,
        "R1 is externally connected to R2 if R1 and R2 share a boundary but their interiors do not share a point.");
    labelObjectProperty(EXTERNALLY_CONNECTED_TO_URI, "externally connected to");

    commentObjectProperty(HAS_NON_TANGENTIAL_PROPER_PART_URI,
        "R2 is non tangential proper part of R1 if all the points of R2 belong to the interior of R1.");
    labelObjectProperty(HAS_NON_TANGENTIAL_PROPER_PART_URI, "has non tangential proper part");

    commentObjectProperty(HAS_TANGENTIAL_PROPER_PART_URI,
        "R2 is tangential proper part of R1 if all the points of R2 is in R1 and their boundaries share a point.");
    labelObjectProperty(HAS_TANGENTIAL_PROPER_PART_URI, "has tangential proper part");

    commentObjectProperty(NON_TANGENTIAL_PROPER_PART_OF_URI,
        "R1 is non tangential proper part of R2 if all the points of R1 belong to the interior of R2.");
    labelObjectProperty(NON_TANGENTIAL_PROPER_PART_OF_URI, "non tangential proper part of");

    commentObjectProperty(PARTIALLY_OVERLAPS_URI, "R1 partially overlaps R2 if their interiors share a point "
      + "and both regions have interior points that are not shared.");
    labelObjectProperty(PARTIALLY_OVERLAPS_URI, "partially overlaps");

    commentObjectProperty(TANGENTIAL_PROPER_PART_OF_URI,
        "R1 is tangential proper part of R2 if all the points of R1 is in R2 and their boundaries share a point.");
    labelObjectProperty(TANGENTIAL_PROPER_PART_OF_URI, "tangential proper part of");
  }
  
  public static void main(String args[]) {
    try {
      createOntology(PelletSpatialOntology.class, args);
    }
    catch(UsageException e) {
      ErrorHandler.fatal(e, "parsing command line arguments");
    }
  }

}
