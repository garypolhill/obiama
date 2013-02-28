/*
 * uk.ac.hutton.obiama.model: RCCOntology.java 
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
 * RCCOntology
 * 
 * This ontology embeds RCC in OWL, and is the unconstrained option as
 * downloaded from <a
 * href="http://webgis.wsl.ch/rcc-webclient/faces/rcc-client.jspx"
 * >http://webgis.wsl.ch/rcc-webclient/faces/rcc-client.jspx</a> on 11 December
 * 2009, but without individuals representing regions in Switzerland. The link
 * is provided in Grütter, Scharrenbach and Bauer-Messmer's <a href="http://www.wsl.ch/forschung/forschungsprojekte/dnl/Dokumente/Grutter_Scharrenbach_Bauer-Messmer_ISWC_2008.pdf"
 * >paper</a> to <a href="http://iswc2008.semanticweb.org/">ISWC 2008</a>
 * (published in <a
 * href="http://www.springerlink.com/content/978-3-540-88563-4/">LNCS 5318</a>).
 * The ontology itself is discussed in Grütter and Bauer-Messmer's <a
 * href="http://www.webont.org/owled/2007/PapersPDF/submission_8.pdf"
 * >presentation</a> to <a
 * href="http://www.webont.org/owled/2007/Proceedings.html">OWLED 2007</a>.
 * 
 * @author Gary Polhill
 */
public class RCCOntology extends AbstractOntology {

  public static final URI ONTOLOGY_URI = URI.create("http://arcserver.wsl.ch/wiki/example");

  // Classes

  public static final URI REGION_URI = URI.create(ONTOLOGY_URI + "#Region");

  // Object properties

  public static final URI SPATIALLY_RELATED_URI = URI.create(ONTOLOGY_URI + "#spatiallyRelated");
  public static final URI OVERLAPS_URI = URI.create(ONTOLOGY_URI + "#overlaps");
  public static final URI DISCRETE_FROM_URI = URI.create(ONTOLOGY_URI + "#discreteFrom");
  public static final URI CONNECTS_WITH_URI = URI.create(ONTOLOGY_URI + "#connectsWith");
  public static final URI OVERLAPS_NOT_EQUAL_URI = URI.create(ONTOLOGY_URI + "#overlapsNotEqual");
  public static final URI EQUAL_TO_URI = URI.create(ONTOLOGY_URI + "#equalTo");
  public static final URI PROPER_PART_OF_URI = URI.create(ONTOLOGY_URI + "#properPartOf");
  public static final URI INVERSE_PROPER_PART_OF_URI = URI.create(ONTOLOGY_URI + "#inverseProperPartOf");
  public static final URI PARTIALLY_OVERLAPS_URI = URI.create(ONTOLOGY_URI + "#partiallyOverlaps");
  public static final URI TANGENTIAL_PROPER_PART_OF_URI = URI.create(ONTOLOGY_URI + "#tangentialProperPartOf");
  public static final URI INVERSE_TANGENTIAL_PROPER_PART_OF_URI =
    URI.create(ONTOLOGY_URI + "#inverseTangentialProperPartOf");
  public static final URI EXTERNALLY_CONNECTED_TO_URI = URI.create(ONTOLOGY_URI + "#externallyConnectedTo");
  public static final URI NON_TANGENTIAL_PROPER_PART_OF_URI = URI.create(ONTOLOGY_URI + "#nonTangentialProperPartOf");
  public static final URI INVERSE_NON_TANGENTIAL_PROPER_PART_OF_URI =
    URI.create(ONTOLOGY_URI + "#inverseNonTangentialProperPartOf");
  public static final URI DISCONNECTED_FROM_URI = URI.create(ONTOLOGY_URI + "#disconnectedFrom");
  public static final URI PART_OF_URI = URI.create(ONTOLOGY_URI + "#partOf");

  /**
   * @param physical
   * @throws OWLOntologyCreationException
   * @throws UnknownOWLOntologyException
   * @throws OWLOntologyChangeException
   * @throws OWLOntologyStorageException
   */
  public RCCOntology(URI physical) throws OWLOntologyCreationException, UnknownOWLOntologyException,
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
  public RCCOntology(URI physical, OWLSpecies spp) throws OWLOntologyCreationException, OWLOntologyChangeException,
      OWLOntologyStorageException {
    super(ONTOLOGY_URI, physical, spp);
  }

  /**
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  public RCCOntology() throws OWLOntologyCreationException, OWLOntologyChangeException {
    super(ONTOLOGY_URI);
  }

  /**
   * @param spp
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  public RCCOntology(OWLSpecies spp) throws OWLOntologyCreationException, OWLOntologyChangeException {
    super(ONTOLOGY_URI, spp);
  }

  /*
   * (non-Javadoc)
   * 
   * @see uk.ac.hutton.obiama.model.AbstractOntology#buildOntology()
   */
  @Override
  protected void buildOntology() {

    // Class: http://arcserver.wsl.ch/wiki/example#Region

    subClassOf(REGION_URI, AbstractOntology.OWL_THING_URI(spp));

    // Object property:
    // http://arcserver.wsl.ch/wiki/example#inverseTangentialProperPartOf

    subObjectPropertyOf(INVERSE_TANGENTIAL_PROPER_PART_OF_URI, INVERSE_PROPER_PART_OF_URI);
    objectPropertyInverse(INVERSE_TANGENTIAL_PROPER_PART_OF_URI, TANGENTIAL_PROPER_PART_OF_URI);

    // Object property: http://arcserver.wsl.ch/wiki/example#connectsWith

    subObjectPropertyOf(CONNECTS_WITH_URI, SPATIALLY_RELATED_URI);
    objectPropertySymmetric(CONNECTS_WITH_URI);

    // Object property: http://arcserver.wsl.ch/wiki/example#inverseProperPartOf

    subObjectPropertyOf(INVERSE_PROPER_PART_OF_URI, OVERLAPS_NOT_EQUAL_URI);
    objectPropertyInverse(INVERSE_PROPER_PART_OF_URI, PROPER_PART_OF_URI);

    // Object property: http://arcserver.wsl.ch/wiki/example#overlapsNotEqual

    subObjectPropertyOf(OVERLAPS_NOT_EQUAL_URI, OVERLAPS_URI);
    objectPropertySymmetric(OVERLAPS_NOT_EQUAL_URI);

    // Object property: http://arcserver.wsl.ch/wiki/example#overlaps

    subObjectPropertyOf(OVERLAPS_URI, CONNECTS_WITH_URI);
    subObjectPropertyOf(OVERLAPS_URI, SPATIALLY_RELATED_URI);
    objectPropertySymmetric(OVERLAPS_URI);

    // Object property:
    // http://arcserver.wsl.ch/wiki/example#externallyConnectedTo

    subObjectPropertyOf(EXTERNALLY_CONNECTED_TO_URI, DISCRETE_FROM_URI);
    subObjectPropertyOf(EXTERNALLY_CONNECTED_TO_URI, CONNECTS_WITH_URI);
    objectPropertySymmetric(EXTERNALLY_CONNECTED_TO_URI);

    // Object property: http://arcserver.wsl.ch/wiki/example#partiallyOverlaps

    subObjectPropertyOf(PARTIALLY_OVERLAPS_URI, OVERLAPS_NOT_EQUAL_URI);
    objectPropertySymmetric(PARTIALLY_OVERLAPS_URI);

    // Object property: http://arcserver.wsl.ch/wiki/example#spatiallyRelated

    objectPropertySymmetric(SPATIALLY_RELATED_URI);
    objectPropertyDomain(SPATIALLY_RELATED_URI, REGION_URI);
    objectPropertyRange(SPATIALLY_RELATED_URI, REGION_URI);

    // Object property:
    // http://arcserver.wsl.ch/wiki/example#tangentialProperPartOf

    subObjectPropertyOf(TANGENTIAL_PROPER_PART_OF_URI, PROPER_PART_OF_URI);
    objectPropertyInverse(INVERSE_TANGENTIAL_PROPER_PART_OF_URI, TANGENTIAL_PROPER_PART_OF_URI);

    // Object property: http://arcserver.wsl.ch/wiki/example#disconnectedFrom

    subObjectPropertyOf(DISCONNECTED_FROM_URI, DISCRETE_FROM_URI);
    objectPropertySymmetric(DISCONNECTED_FROM_URI);

    // Object property:
    // http://arcserver.wsl.ch/wiki/example#nonTangentialProperPartOf

    subObjectPropertyOf(NON_TANGENTIAL_PROPER_PART_OF_URI, PROPER_PART_OF_URI);
    objectPropertyInverse(INVERSE_NON_TANGENTIAL_PROPER_PART_OF_URI, NON_TANGENTIAL_PROPER_PART_OF_URI);

    // Object property: http://arcserver.wsl.ch/wiki/example#properPartOf

    subObjectPropertyOf(PROPER_PART_OF_URI, OVERLAPS_NOT_EQUAL_URI);
    objectPropertyInverse(INVERSE_PROPER_PART_OF_URI, PROPER_PART_OF_URI);

    // Object property: http://arcserver.wsl.ch/wiki/example#partOf

    subObjectPropertyOf(PART_OF_URI, OVERLAPS_URI);
    objectPropertyIrreflexive(PART_OF_URI);

    // Object property: http://arcserver.wsl.ch/wiki/example#discreteFrom

    subObjectPropertyOf(DISCRETE_FROM_URI, SPATIALLY_RELATED_URI);
    objectPropertySymmetric(DISCRETE_FROM_URI);

    // Object property: http://arcserver.wsl.ch/wiki/example#equalTo

    subObjectPropertyOf(EQUAL_TO_URI, OVERLAPS_URI);
    objectPropertySymmetric(EQUAL_TO_URI);

    // Object property:
    // http://arcserver.wsl.ch/wiki/example#inverseNonTangentialProperPartOf

    subObjectPropertyOf(INVERSE_NON_TANGENTIAL_PROPER_PART_OF_URI, INVERSE_PROPER_PART_OF_URI);
    objectPropertyInverse(INVERSE_NON_TANGENTIAL_PROPER_PART_OF_URI, NON_TANGENTIAL_PROPER_PART_OF_URI);

  }

  public static void main(String args[]) {
    try {
      createOntology(RCCOntology.class, args);
    }
    catch(UsageException e) {
      ErrorHandler.fatal(e, "parsing command line arguments");
    }
  }

}
