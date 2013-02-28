/*
 * uk.ac.hutton.obiama.model: ACCSpaceOntology.java
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
import java.util.Set;

import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyStorageException;
import org.semanticweb.owl.model.UnknownOWLOntologyException;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.UsageException;

/**
 * ACCSpaceOntology
 * 
 * @author Gary Polhill
 * 
 */
public class ACCSpaceOntology extends AbstractOntology {

  public static final URI ONTOLOGY_URI = URI.create("http://www.hutton.ac.uk/obiama/ontologies/ACC.owl");

  // Classes

  public static final URI CELL_URI = URI.create(ONTOLOGY_URI + "#Cell");
  public static final URI CELL_0D_URI = URI.create(ONTOLOGY_URI + "#Cell0D");
  public static final URI EUCLIDEAN_CELL_URI = URI.create(ONTOLOGY_URI + "#EuclideanCell");
  public static final URI EUCLIDEAN_CELL_0D_URI = URI.create(ONTOLOGY_URI + "#EuclideanCell0D");
  public static final URI EUCLIDEAN_2D_CELL_0D_URI = URI.create(ONTOLOGY_URI + "#Euclidean2DCell0D");
  public static final URI EUCLIDEAN_3D_CELL_0D_URI = URI.create(ONTOLOGY_URI + "#Euclidean3DCell0D");
  public static final URI CELL_1D_URI = URI.create(ONTOLOGY_URI + "#Cell1D");
  public static final URI EUCLIDEAN_CELL_1D_URI = URI.create(ONTOLOGY_URI + "#EuclideanCell1D");
  public static final URI EUCLIDEAN_2D_CELL_1D_URI = URI.create(ONTOLOGY_URI + "#Euclidean2DCell1D");
  public static final URI EUCLIDEAN_3D_CELL_1D_URI = URI.create(ONTOLOGY_URI + "#Euclidean3DCell1D");
  public static final URI CELL_2D_URI = URI.create(ONTOLOGY_URI + "#Cell2D");
  public static final URI EUCLIDEAN_CELL_2D_URI = URI.create(ONTOLOGY_URI + "#EuclideanCell2D");
  public static final URI EUCLIDEAN_2D_CELL_2D_URI = URI.create(ONTOLOGY_URI + "#Euclidean2DCell2D");
  public static final URI EUCLIDEAN_3D_CELL_2D_URI = URI.create(ONTOLOGY_URI + "#Euclidean3DCell2D");
  public static final URI CELL_3D_URI = URI.create(ONTOLOGY_URI + "#Cell3D");
  public static final URI EUCLIDEAN_CELL_3D_URI = URI.create(ONTOLOGY_URI + "#EuclideanCell3D");
  public static final URI EUCLIDEAN_2D_CELL_3D_URI = URI.create(ONTOLOGY_URI + "#Euclidean2DCell3D");
  public static final URI EUCLIDEAN_3D_CELL_3D_URI = URI.create(ONTOLOGY_URI + "#Euclidean3DCell3D");
  public static final URI EUCLIDEAN_2D_CELL_URI = URI.create(ONTOLOGY_URI + "#Euclidean2DCell");
  public static final URI EUCLIDEAN_3D_CELL_URI = URI.create(ONTOLOGY_URI + "#Euclidean3DCell");
  public static final URI COMPLEX_URI = URI.create(ONTOLOGY_URI + "#Complex");
  public static final URI COMPLEX_0D_URI = URI.create(ONTOLOGY_URI + "#Complex0D");
  public static final URI EUCLIDEAN_COMPLEX_0D_URI = URI.create(ONTOLOGY_URI + "#EuclideanComplex0D");
  public static final URI EUCLIDEAN_2D_COMPLEX_0D_URI = URI.create(ONTOLOGY_URI + "#Euclidean2DComplex0D");
  public static final URI EUCLIDEAN_3D_COMPLEX_0D_URI = URI.create(ONTOLOGY_URI + "#Euclidean3DComplex0D");
  public static final URI COMPLEX_1D_URI = URI.create(ONTOLOGY_URI + "#Complex1D");
  public static final URI EUCLIDEAN_COMPLEX_1D_URI = URI.create(ONTOLOGY_URI + "#EuclideanComplex1D");
  public static final URI EUCLIDEAN_2D_COMPLEX_1D_URI = URI.create(ONTOLOGY_URI + "#Euclidean2DComplex1D");
  public static final URI EUCLIDEAN_3D_COMPLEX_1D_URI = URI.create(ONTOLOGY_URI + "#Euclidean3DComplex1D");
  public static final URI COMPLEX_2D_URI = URI.create(ONTOLOGY_URI + "#Complex2D");
  public static final URI EUCLIDEAN_COMPLEX_2D_URI = URI.create(ONTOLOGY_URI + "#EuclideanComplex2D");
  public static final URI EUCLIDEAN_2D_COMPLEX_2D_URI = URI.create(ONTOLOGY_URI + "#Euclidean2DComplex2D");
  public static final URI EUCLIDEAN_3D_COMPLEX_2D_URI = URI.create(ONTOLOGY_URI + "#Euclidean3DComplex2D");
  public static final URI COMPLEX_3D_URI = URI.create(ONTOLOGY_URI + "#Complex3D");
  public static final URI EUCLIDEAN_COMPLEX_3D_URI = URI.create(ONTOLOGY_URI + "#EuclideanComplex3D");
  public static final URI EUCLIDEAN_2D_COMPLEX_3D_URI = URI.create(ONTOLOGY_URI + "#Euclidean2DComplex3D");
  public static final URI EUCLIDEAN_3D_COMPLEX_3D_URI = URI.create(ONTOLOGY_URI + "#Euclidean3DComplex3D");
  public static final URI EUCLIDEAN_2D_COMPLEX_URI = URI.create(ONTOLOGY_URI + "#Euclidean2DComplex");
  public static final URI EUCLIDEAN_3D_COMPLEX_URI = URI.create(ONTOLOGY_URI + "#Euclidean3DComplex");
  public static final URI COORDINATE_URI = URI.create(ONTOLOGY_URI + "#Coordinate");
  public static final URI COORDINATE_2D_URI = URI.create(ONTOLOGY_URI + "#Coordinate2D");
  public static final URI COORDINATE_3D_URI = URI.create(ONTOLOGY_URI + "#Coordinate3D");
  public static final URI ACC_SPACE_URI = URI.create(ONTOLOGY_URI + "#ACCSpace");
  public static final URI EUCLIDEAN_2D_ACC_SPACE_URI = URI.create(ONTOLOGY_URI + "#Euclidean2DACCSpace");
  public static final URI EUCLIDEAN_3D_ACC_SPACE_URI = URI.create(ONTOLOGY_URI + "#Euclidean3DACCSpace");

  // Object properties

  public static final URI BOUNDED_BY_URI = URI.create(ONTOLOGY_URI + "#boundedBy");
  public static final URI BOUNDARY_OF_URI = URI.create(ONTOLOGY_URI + "#boundaryOf");
  public static final URI COORDINATES_URI = URI.create(ONTOLOGY_URI + "#coordinates");
  public static final URI CONTAINS_URI = URI.create(ONTOLOGY_URI + "#contains");
  public static final URI CONTAINED_IN_URI = URI.create(ONTOLOGY_URI + "#containedIn");
  public static final URI CONTAINS_CELL_URI = URI.create(ONTOLOGY_URI + "#containsCell");

  // Data properties

  public static final URI X_URI = URI.create(ONTOLOGY_URI + "#x");
  public static final URI Y_URI = URI.create(ONTOLOGY_URI + "#y");
  public static final URI Z_URI = URI.create(ONTOLOGY_URI + "#z");

  // Individuals

  public static final URI GET_NEIGHBOURS_URI = URI.create(ONTOLOGY_URI + "#getNeighbours");

  private static boolean test = false;

  /**
   * @param physical
   * @throws OWLOntologyCreationException
   * @throws UnknownOWLOntologyException
   * @throws OWLOntologyChangeException
   * @throws OWLOntologyStorageException
   */
  public ACCSpaceOntology(URI physical) throws OWLOntologyCreationException, UnknownOWLOntologyException,
      OWLOntologyChangeException, OWLOntologyStorageException {
    super(ONTOLOGY_URI, physical, OWLSpecies.OWL_2_DL);
  }

  /**
   * @param physical
   * @param spp
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   * @throws OWLOntologyStorageException
   */
  public ACCSpaceOntology(URI physical, OWLSpecies spp) throws OWLOntologyCreationException,
      OWLOntologyChangeException, OWLOntologyStorageException {
    super(ONTOLOGY_URI, physical, spp);
  }

  /**
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  public ACCSpaceOntology() throws OWLOntologyCreationException, OWLOntologyChangeException {
    super(ONTOLOGY_URI, OWLSpecies.OWL_2_DL);
  }

  /**
   * @param spp
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  public ACCSpaceOntology(OWLSpecies spp) throws OWLOntologyCreationException, OWLOntologyChangeException {
    super(ONTOLOGY_URI, spp);
  }

  /**
   * @param related
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  public ACCSpaceOntology(AbstractOntology related) throws OWLOntologyCreationException, OWLOntologyChangeException {
    super(ONTOLOGY_URI, related);
  }

  /**
   * <!-- buildOntology -->
   * 
   * @see uk.ac.hutton.obiama.model.AbstractOntology#buildOntology()
   */
  @Override
  protected void buildOntology() {
    imports(SpaceOntology.ONTOLOGY_URI);

    // Space

    equivalentClasses(ACC_SPACE_URI, objectIntersectionOf(namedClass(SpaceOntology.SPACE_URI), objectAllRestriction(
        SpaceOntology.CONTAINS_LOCATIONS_URI, COMPLEX_URI), objectSomeRestriction(SpaceOntology.CONTAINS_LOCATIONS_URI,
        COMPLEX_URI)));
    
    objectPropertyDomain(CONTAINS_CELL_URI, ACC_SPACE_URI);
    objectPropertyRange(CONTAINS_CELL_URI, CELL_URI);
    superPropertyOfChain(CONTAINS_CELL_URI, SpaceOntology.CONTAINS_LOCATIONS_URI, CONTAINS_URI);

    // Cells

    subClassOf(CELL_0D_URI, CELL_URI);
    subClassOf(CELL_1D_URI, CELL_URI);
    subClassOf(CELL_2D_URI, CELL_URI);
    subClassOf(CELL_3D_URI, CELL_URI);

    subClassOf(CELL_1D_URI, objectIntersectionOf(objectAllRestriction(BOUNDED_BY_URI, CELL_0D_URI),
        objectSomeRestriction(BOUNDED_BY_URI, CELL_0D_URI)));
    subClassOf(CELL_2D_URI, objectIntersectionOf(objectAllRestriction(BOUNDED_BY_URI, objectUnionOf(CELL_0D_URI,
        CELL_1D_URI)), objectSomeRestriction(BOUNDED_BY_URI, CELL_1D_URI)));
    subClassOf(CELL_3D_URI, objectIntersectionOf(objectAllRestriction(BOUNDED_BY_URI, objectUnionOf(CELL_0D_URI,
        CELL_1D_URI, CELL_2D_URI)), objectSomeRestriction(BOUNDED_BY_URI, CELL_2D_URI)));

    objectPropertyDomain(BOUNDED_BY_URI, CELL_URI);
    objectPropertyRange(BOUNDED_BY_URI, CELL_URI);
    // objectPropertyAntiSymmetric(BOUNDED_BY_URI);
    // objectPropertyIrreflexive(BOUNDED_BY_URI);
    objectPropertyTransitive(BOUNDED_BY_URI);

    objectPropertyInverse(BOUNDED_BY_URI, BOUNDARY_OF_URI);

    // Complexes

    subClassOf(COMPLEX_URI, SpaceOntology.LOCATION_URI);

    objectPropertyDomain(CONTAINS_URI, COMPLEX_URI);
    objectPropertyRange(CONTAINS_URI, CELL_URI);

    objectPropertyInverse(CONTAINS_URI, CONTAINED_IN_URI);

    equivalentClasses(COMPLEX_0D_URI, objectIntersectionOf(namedClass(COMPLEX_URI), objectAllRestriction(CONTAINS_URI,
        CELL_0D_URI), objectSomeRestriction(CONTAINS_URI, CELL_0D_URI)));
    equivalentClasses(COMPLEX_1D_URI, objectIntersectionOf(namedClass(COMPLEX_URI), objectAllRestriction(CONTAINS_URI,
        objectUnionOf(CELL_0D_URI, CELL_1D_URI)), objectSomeRestriction(CONTAINS_URI, CELL_1D_URI)));
    equivalentClasses(COMPLEX_2D_URI, objectIntersectionOf(namedClass(COMPLEX_URI), objectAllRestriction(CONTAINS_URI,
        objectUnionOf(CELL_0D_URI, CELL_1D_URI, CELL_2D_URI)), objectSomeRestriction(CONTAINS_URI, CELL_2D_URI)));
    equivalentClasses(COMPLEX_3D_URI, objectIntersectionOf(namedClass(COMPLEX_URI), objectAllRestriction(CONTAINS_URI,
        objectUnionOf(CELL_0D_URI, CELL_1D_URI, CELL_2D_URI, CELL_3D_URI)), objectSomeRestriction(CONTAINS_URI,
        CELL_3D_URI)));

    // Connected-to and Overlaps

    superPropertyOfChain(SpaceOntology.CONNECTED_TO_URI, CONTAINS_URI, BOUNDED_BY_URI, BOUNDARY_OF_URI,
        CONTAINED_IN_URI);

    superPropertyOfChain(SpaceOntology.OVERLAPS_URI, CONTAINS_URI, CONTAINED_IN_URI);

    // Euclidean Cells

    equivalentClasses(EUCLIDEAN_CELL_URI, objectIntersectionOf(namedClass(CELL_URI), objectAllRestriction(
        BOUNDED_BY_URI, EUCLIDEAN_CELL_URI)));

    subClassOf(EUCLIDEAN_CELL_0D_URI, CELL_0D_URI);

    equivalentClasses(EUCLIDEAN_2D_CELL_0D_URI, objectIntersectionOf(namedClass(EUCLIDEAN_CELL_0D_URI),
        objectAllRestriction(COORDINATES_URI, COORDINATE_2D_URI), objectSomeRestriction(COORDINATES_URI,
            COORDINATE_2D_URI)));
    equivalentClasses(EUCLIDEAN_3D_CELL_0D_URI, objectIntersectionOf(namedClass(EUCLIDEAN_CELL_0D_URI),
        objectAllRestriction(COORDINATES_URI, COORDINATE_3D_URI), objectSomeRestriction(COORDINATES_URI,
            COORDINATE_3D_URI)));

    subClassOf(EUCLIDEAN_CELL_1D_URI, CELL_1D_URI);
    equivalentClasses(EUCLIDEAN_2D_CELL_1D_URI, objectIntersectionOf(namedClass(EUCLIDEAN_CELL_1D_URI),
        objectAllRestriction(BOUNDED_BY_URI, EUCLIDEAN_2D_CELL_0D_URI), objectSomeRestriction(COORDINATES_URI,
            COORDINATE_2D_URI)));
    equivalentClasses(EUCLIDEAN_3D_CELL_0D_URI, objectIntersectionOf(namedClass(EUCLIDEAN_CELL_0D_URI),
        objectAllRestriction(COORDINATES_URI, COORDINATE_3D_URI), objectSomeRestriction(COORDINATES_URI,
            COORDINATE_3D_URI)));

    subClassOf(EUCLIDEAN_CELL_2D_URI, CELL_2D_URI);
    equivalentClasses(EUCLIDEAN_2D_CELL_0D_URI, objectIntersectionOf(namedClass(EUCLIDEAN_CELL_0D_URI),
        objectAllRestriction(COORDINATES_URI, COORDINATE_2D_URI), objectSomeRestriction(COORDINATES_URI,
            COORDINATE_2D_URI)));
    equivalentClasses(EUCLIDEAN_3D_CELL_0D_URI, objectIntersectionOf(namedClass(EUCLIDEAN_CELL_0D_URI),
        objectAllRestriction(COORDINATES_URI, COORDINATE_3D_URI), objectSomeRestriction(COORDINATES_URI,
            COORDINATE_3D_URI)));

    subClassOf(EUCLIDEAN_CELL_3D_URI, CELL_3D_URI);
    equivalentClasses(EUCLIDEAN_2D_CELL_0D_URI, objectIntersectionOf(namedClass(EUCLIDEAN_CELL_0D_URI),
        objectAllRestriction(COORDINATES_URI, COORDINATE_2D_URI), objectSomeRestriction(COORDINATES_URI,
            COORDINATE_2D_URI)));
    equivalentClasses(EUCLIDEAN_3D_CELL_0D_URI, objectIntersectionOf(namedClass(EUCLIDEAN_CELL_0D_URI),
        objectAllRestriction(COORDINATES_URI, COORDINATE_3D_URI), objectSomeRestriction(COORDINATES_URI,
            COORDINATE_3D_URI)));

    equivalentClasses(EUCLIDEAN_2D_CELL_URI, objectUnionOf(EUCLIDEAN_2D_CELL_0D_URI, EUCLIDEAN_2D_CELL_1D_URI,
        EUCLIDEAN_2D_CELL_2D_URI, EUCLIDEAN_2D_CELL_3D_URI));
    equivalentClasses(EUCLIDEAN_3D_CELL_URI, objectUnionOf(EUCLIDEAN_3D_CELL_0D_URI, EUCLIDEAN_3D_CELL_1D_URI,
        EUCLIDEAN_3D_CELL_2D_URI, EUCLIDEAN_3D_CELL_3D_URI));

    // Euclidean complexes

    subClassOf(EUCLIDEAN_COMPLEX_0D_URI, COMPLEX_0D_URI);
    subClassOf(EUCLIDEAN_COMPLEX_1D_URI, COMPLEX_1D_URI);
    subClassOf(EUCLIDEAN_COMPLEX_2D_URI, COMPLEX_2D_URI);
    subClassOf(EUCLIDEAN_COMPLEX_3D_URI, COMPLEX_3D_URI);

    equivalentClasses(EUCLIDEAN_2D_COMPLEX_0D_URI, objectIntersectionOf(namedClass(COMPLEX_URI), objectAllRestriction(
        CONTAINS_URI, EUCLIDEAN_2D_CELL_0D_URI), objectSomeRestriction(CONTAINS_URI, EUCLIDEAN_2D_CELL_0D_URI)));
    equivalentClasses(EUCLIDEAN_2D_COMPLEX_1D_URI, objectIntersectionOf(namedClass(COMPLEX_URI), objectAllRestriction(
        CONTAINS_URI, objectUnionOf(EUCLIDEAN_2D_CELL_0D_URI, EUCLIDEAN_2D_CELL_1D_URI)), objectSomeRestriction(
        CONTAINS_URI, EUCLIDEAN_2D_CELL_1D_URI)));
    equivalentClasses(EUCLIDEAN_2D_COMPLEX_2D_URI, objectIntersectionOf(namedClass(COMPLEX_URI), objectAllRestriction(
        CONTAINS_URI, objectUnionOf(EUCLIDEAN_2D_CELL_0D_URI, EUCLIDEAN_2D_CELL_1D_URI, EUCLIDEAN_2D_CELL_2D_URI)),
        objectSomeRestriction(CONTAINS_URI, EUCLIDEAN_2D_CELL_2D_URI)));
    equivalentClasses(EUCLIDEAN_2D_COMPLEX_3D_URI, objectIntersectionOf(namedClass(COMPLEX_URI), objectAllRestriction(
        CONTAINS_URI, objectUnionOf(EUCLIDEAN_2D_CELL_0D_URI, EUCLIDEAN_2D_CELL_1D_URI, EUCLIDEAN_2D_CELL_2D_URI,
            EUCLIDEAN_2D_CELL_3D_URI)), objectSomeRestriction(CONTAINS_URI, EUCLIDEAN_2D_CELL_3D_URI)));

    equivalentClasses(EUCLIDEAN_3D_COMPLEX_0D_URI, objectIntersectionOf(namedClass(COMPLEX_URI), objectAllRestriction(
        CONTAINS_URI, EUCLIDEAN_3D_CELL_0D_URI), objectSomeRestriction(CONTAINS_URI, EUCLIDEAN_3D_CELL_0D_URI)));
    equivalentClasses(EUCLIDEAN_3D_COMPLEX_1D_URI, objectIntersectionOf(namedClass(COMPLEX_URI), objectAllRestriction(
        CONTAINS_URI, objectUnionOf(EUCLIDEAN_3D_CELL_0D_URI, EUCLIDEAN_3D_CELL_1D_URI)), objectSomeRestriction(
        CONTAINS_URI, EUCLIDEAN_3D_CELL_1D_URI)));
    equivalentClasses(EUCLIDEAN_3D_COMPLEX_2D_URI, objectIntersectionOf(namedClass(COMPLEX_URI), objectAllRestriction(
        CONTAINS_URI, objectUnionOf(EUCLIDEAN_3D_CELL_0D_URI, EUCLIDEAN_3D_CELL_1D_URI, EUCLIDEAN_3D_CELL_2D_URI)),
        objectSomeRestriction(CONTAINS_URI, EUCLIDEAN_3D_CELL_2D_URI)));
    equivalentClasses(EUCLIDEAN_3D_COMPLEX_3D_URI, objectIntersectionOf(namedClass(COMPLEX_URI), objectAllRestriction(
        CONTAINS_URI, objectUnionOf(EUCLIDEAN_3D_CELL_0D_URI, EUCLIDEAN_3D_CELL_1D_URI, EUCLIDEAN_3D_CELL_2D_URI,
            EUCLIDEAN_3D_CELL_3D_URI)), objectSomeRestriction(CONTAINS_URI, EUCLIDEAN_3D_CELL_3D_URI)));

    equivalentClasses(EUCLIDEAN_2D_COMPLEX_URI, objectUnionOf(EUCLIDEAN_2D_COMPLEX_0D_URI, EUCLIDEAN_2D_COMPLEX_1D_URI,
        EUCLIDEAN_2D_COMPLEX_2D_URI, EUCLIDEAN_2D_COMPLEX_3D_URI));
    equivalentClasses(EUCLIDEAN_3D_COMPLEX_URI, objectUnionOf(EUCLIDEAN_3D_COMPLEX_0D_URI, EUCLIDEAN_3D_COMPLEX_1D_URI,
        EUCLIDEAN_3D_COMPLEX_2D_URI, EUCLIDEAN_3D_COMPLEX_3D_URI));

    // Euclidean Spaces

    equivalentClasses(EUCLIDEAN_2D_ACC_SPACE_URI, objectIntersectionOf(namedClass(ACC_SPACE_URI), objectAllRestriction(
        SpaceOntology.CONTAINS_LOCATIONS_URI, EUCLIDEAN_2D_COMPLEX_URI), objectSomeRestriction(
        SpaceOntology.CONTAINS_LOCATIONS_URI, EUCLIDEAN_2D_COMPLEX_URI)));

    equivalentClasses(EUCLIDEAN_3D_ACC_SPACE_URI, objectIntersectionOf(namedClass(ACC_SPACE_URI), objectAllRestriction(
        SpaceOntology.CONTAINS_LOCATIONS_URI, EUCLIDEAN_3D_COMPLEX_URI), objectSomeRestriction(
        SpaceOntology.CONTAINS_LOCATIONS_URI, EUCLIDEAN_3D_COMPLEX_URI)));

    // Co-ordinates

    objectPropertyDomain(COORDINATES_URI, EUCLIDEAN_CELL_0D_URI);
    objectPropertyRange(COORDINATES_URI, COORDINATE_URI);

    subClassOf(COORDINATE_2D_URI, COORDINATE_URI);
    subClassOf(COORDINATE_3D_URI, COORDINATE_URI);
    disjointClasses(COORDINATE_2D_URI, COORDINATE_3D_URI);

    dataPropertyDomain(X_URI, objectUnionOf(COORDINATE_2D_URI, COORDINATE_3D_URI));
    dataPropertyRange(X_URI, XSDVocabulary.DOUBLE);

    dataPropertyDomain(Y_URI, objectUnionOf(COORDINATE_2D_URI, COORDINATE_3D_URI));
    dataPropertyRange(Y_URI, XSDVocabulary.DOUBLE);

    dataPropertyDomain(Z_URI, COORDINATE_3D_URI);
    dataPropertyRange(Z_URI, XSDVocabulary.DOUBLE);

    if(test) {
      URI points[] =
        new URI[] { URI.create(ONTOLOGY_URI + "#bottom-left-point"), URI.create(ONTOLOGY_URI + "#bottom-middle-point"),
          URI.create(ONTOLOGY_URI + "#bottom-right-point"), URI.create(ONTOLOGY_URI + "#middle-left-point"),
          URI.create(ONTOLOGY_URI + "#middle-right-point"), URI.create(ONTOLOGY_URI + "#top-point") };
      for(URI point: points) {
        individualHasClass(point, CELL_0D_URI);
      }
      URI lines[] =
        new URI[] { URI.create(ONTOLOGY_URI + "#bottom-left-to-bottom-middle-line"),
          URI.create(ONTOLOGY_URI + "#bottom-middle-to-bottom-right-line"),
          URI.create(ONTOLOGY_URI + "#bottom-left-to-middle-left-line"),
          URI.create(ONTOLOGY_URI + "#bottom-middle-to-middle-left-line"),
          URI.create(ONTOLOGY_URI + "#bottom-right-to-middle-right-line"),
          URI.create(ONTOLOGY_URI + "#bottom-middle-to-middle-right-line"),
          URI.create(ONTOLOGY_URI + "#middle-left-to-middle-right-line"),
          URI.create(ONTOLOGY_URI + "#middle-left-to-top-line"), URI.create(ONTOLOGY_URI + "#middle-right-to-top-line") };
      for(URI line: lines) {
        individualHasClass(line, CELL_URI);
      }
      individualHasObjectPropertyValue(lines[0], BOUNDED_BY_URI, points[0]);
      individualHasObjectPropertyValue(lines[0], BOUNDED_BY_URI, points[1]);
      individualHasObjectPropertyValue(lines[1], BOUNDED_BY_URI, points[1]);
      individualHasObjectPropertyValue(lines[1], BOUNDED_BY_URI, points[2]);
      individualHasObjectPropertyValue(lines[2], BOUNDED_BY_URI, points[0]);
      individualHasObjectPropertyValue(lines[2], BOUNDED_BY_URI, points[3]);
      individualHasObjectPropertyValue(lines[3], BOUNDED_BY_URI, points[1]);
      individualHasObjectPropertyValue(lines[3], BOUNDED_BY_URI, points[3]);
      individualHasObjectPropertyValue(lines[4], BOUNDED_BY_URI, points[1]);
      individualHasObjectPropertyValue(lines[4], BOUNDED_BY_URI, points[4]);
      individualHasObjectPropertyValue(lines[5], BOUNDED_BY_URI, points[2]);
      individualHasObjectPropertyValue(lines[5], BOUNDED_BY_URI, points[4]);
      individualHasObjectPropertyValue(lines[6], BOUNDED_BY_URI, points[3]);
      individualHasObjectPropertyValue(lines[6], BOUNDED_BY_URI, points[4]);
      individualHasObjectPropertyValue(lines[7], BOUNDED_BY_URI, points[3]);
      individualHasObjectPropertyValue(lines[7], BOUNDED_BY_URI, points[5]);
      individualHasObjectPropertyValue(lines[8], BOUNDED_BY_URI, points[4]);
      individualHasObjectPropertyValue(lines[8], BOUNDED_BY_URI, points[5]);
      URI cells[] =
        new URI[] { URI.create(ONTOLOGY_URI + "#left-cell"), URI.create(ONTOLOGY_URI + "#middle-cell"),
          URI.create(ONTOLOGY_URI + "#right-cell"), URI.create(ONTOLOGY_URI + "#top-cell") };
      individualHasObjectPropertyValue(cells[0], BOUNDED_BY_URI, lines[0]);
      individualHasObjectPropertyValue(cells[0], BOUNDED_BY_URI, lines[2]);
      individualHasObjectPropertyValue(cells[0], BOUNDED_BY_URI, lines[3]);
      individualHasObjectPropertyValue(cells[1], BOUNDED_BY_URI, lines[3]);
      individualHasObjectPropertyValue(cells[1], BOUNDED_BY_URI, lines[4]);
      individualHasObjectPropertyValue(cells[1], BOUNDED_BY_URI, lines[6]);
      individualHasObjectPropertyValue(cells[2], BOUNDED_BY_URI, lines[1]);
      individualHasObjectPropertyValue(cells[2], BOUNDED_BY_URI, lines[4]);
      individualHasObjectPropertyValue(cells[2], BOUNDED_BY_URI, lines[5]);
      individualHasObjectPropertyValue(cells[3], BOUNDED_BY_URI, lines[6]);
      individualHasObjectPropertyValue(cells[3], BOUNDED_BY_URI, lines[7]);
      individualHasObjectPropertyValue(cells[3], BOUNDED_BY_URI, lines[8]);
      for(URI cell: cells) {
        individualHasClass(cell, CELL_URI);
      }
      URI complexes[] =
        new URI[] { URI.create(ONTOLOGY_URI + "#left-triangle"), URI.create(ONTOLOGY_URI + "#middle-triangle"),
          URI.create(ONTOLOGY_URI + "#right-triangle"), URI.create(ONTOLOGY_URI + "#top-triangle"),
          URI.create(ONTOLOGY_URI + "#left-diamond"), URI.create(ONTOLOGY_URI + "#right-diamond"),
          URI.create(ONTOLOGY_URI + "#top-diamond") };
      individualHasObjectPropertyValue(complexes[0], CONTAINS_URI, cells[0]);
      individualHasObjectPropertyValue(complexes[1], CONTAINS_URI, cells[1]);
      individualHasObjectPropertyValue(complexes[2], CONTAINS_URI, cells[2]);
      individualHasObjectPropertyValue(complexes[3], CONTAINS_URI, cells[3]);
      individualHasObjectPropertyValue(complexes[4], CONTAINS_URI, cells[0]);
      individualHasObjectPropertyValue(complexes[4], CONTAINS_URI, cells[1]);
      individualHasObjectPropertyValue(complexes[5], CONTAINS_URI, cells[2]);
      individualHasObjectPropertyValue(complexes[5], CONTAINS_URI, cells[1]);
      individualHasObjectPropertyValue(complexes[6], CONTAINS_URI, cells[3]);
      individualHasObjectPropertyValue(complexes[6], CONTAINS_URI, cells[1]);
      for(URI complex: complexes) {
        individualHasClass(complex, COMPLEX_URI);
      }
    }
  }

  public static void main(String args[]) {
    try {
      test = false;
      ACCSpaceOntology ont = (ACCSpaceOntology)createOntology(ACCSpaceOntology.class, args);
      System.out.println("0D Cells:");
      for(URI ind: ont.getAssertedMembersOf(CELL_0D_URI)) {
        System.out.println(ind.getFragment());
      }
      System.out.println("1D Cells:");
      for(URI ind: ont.getAssertedMembersOf(CELL_1D_URI)) {
        System.out.println(ind.getFragment());
      }
      System.out.println("2D Cells:");
      for(URI ind: ont.getAssertedMembersOf(CELL_2D_URI)) {
        System.out.println(ind.getFragment());
      }
      System.out.println("3D Cells:");
      for(URI ind: ont.getAssertedMembersOf(CELL_3D_URI)) {
        System.out.println(ind.getFragment());
      }

      System.out.println("0D Complexes:");
      for(URI ind: ont.getAssertedMembersOf(COMPLEX_0D_URI)) {
        System.out.println(ind.getFragment());
      }
      System.out.println("1D Complexes:");
      for(URI ind: ont.getAssertedMembersOf(COMPLEX_1D_URI)) {
        System.out.println(ind.getFragment());
      }
      System.out.println("2D Complexes:");
      for(URI ind: ont.getAssertedMembersOf(COMPLEX_2D_URI)) {
        System.out.println(ind.getFragment());
      }
      System.out.println("3D Complexes:");
      for(URI ind: ont.getAssertedMembersOf(COMPLEX_3D_URI)) {
        System.out.println(ind.getFragment());
      }

      System.out.println("Connections:");
      for(URI ind1: ont.getAssertedMembersOf(COMPLEX_URI)) {
        for(URI ind2: ont.getObjectPropertyOf(ind1, SpaceOntology.CONNECTED_TO_URI)) {
          System.out.println(ind1.getFragment() + " " + SpaceOntology.CONNECTED_TO_URI.getFragment() + " "
            + ind2.getFragment());
        }
      }

      System.out.println("Overlaps:");
      for(URI ind1: ont.getAssertedMembersOf(COMPLEX_URI)) {
        for(URI ind2: ont.getObjectPropertyOf(ind1, SpaceOntology.OVERLAPS_URI)) {
          System.out.println(ind1.getFragment() + " " + SpaceOntology.OVERLAPS_URI.getFragment() + " "
            + ind2.getFragment());
        }
      }

      System.out.println("Neighbours:");
      for(URI ind1: ont.getAssertedMembersOf(COMPLEX_URI)) {
        Set<URI> overlaps = ont.getObjectPropertyOf(ind1, SpaceOntology.OVERLAPS_URI);
        for(URI ind2: ont.getObjectPropertyOf(ind1, SpaceOntology.CONNECTED_TO_URI)) {
          if(!overlaps.contains(ind2)) {
            System.out.println("EC(" + ind1.getFragment() + ", " + ind2.getFragment() + ")");
          }
        }
      }

    }
    catch(UsageException e) {
      ErrorHandler.fatal(e, "parsing command line arguments");
    }
  }

}
