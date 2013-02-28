/*
 * uk.ac.hutton.obiama.msb: Provenance.java
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
package uk.ac.hutton.obiama.msb;

import java.net.URI;

import org.semanticweb.owl.vocab.OWLXMLVocabulary;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.ProvenanceException;
import uk.ac.hutton.obiama.model.ObiamaOntology;
import uk.ac.hutton.util.Panic;

/**
 * <!-- Provenance -->
 * 
 * Interface for creating provenance as the simulation runs. The interface also
 * contains an enclosed interface to record axioms, and an enumeration of
 * implementations.
 * 
 * @author Gary Polhill
 */
public interface Provenance {
  public static final URI PROVENANCE_PATH_URI = URI.create(ObiamaOntology.ONTOLOGY_PATH + "/provenance");

  public static final URI HISTORY_PROVENANCE_URI = URI.create(PROVENANCE_PATH_URI + "/history.owl");

  public static final URI EXECUTION_PROVENANCE_URI = URI.create(PROVENANCE_PATH_URI + "/execution.owl");

  public static final URI ASSERTION_URI = URI.create(HISTORY_PROVENANCE_URI + "#Assertion");

  public static final URI RETRACTION_URI = URI.create(HISTORY_PROVENANCE_URI + "#Retraction");

  public static final URI AXIOM_URI = URI.create(HISTORY_PROVENANCE_URI + "#Axiom");

  public static final URI SUBJECT_URI = URI.create(HISTORY_PROVENANCE_URI + "#subject");

  public static final URI PREDICATE_URI = URI.create(HISTORY_PROVENANCE_URI + "#predicate");

  public static final URI OBJECT_URI = URI.create(HISTORY_PROVENANCE_URI + "#object");

  public static final URI DATA_URI = URI.create(HISTORY_PROVENANCE_URI + "#data");

  /**
   * <!-- setProvenanceOntology -->
   * 
   * Set the provenance ontology to use. This method will be called from the
   * implementation.
   * 
   * @param ontologyURI Logical URI of the provenance ontology
   */
  public void setProvenanceOntology(URI ontologyURI);

  /**
   * <!-- setHistoryProvenanceOntology -->
   * 
   * Set the history provenance ontology to save history provenance to.
   * 
   * @param ontologyURI Logical URI of the history provenance to use (the
   *          physical URI will be specified when you save it)
   */
  public void setHistoryProvenanceOntology(URI ontologyURI);

  /**
   * <!-- setRetractionAxioms -->
   * 
   * Specify the axioms that need to be asserted when recording a retraction in
   * the provenance ontology. Some axioms will require new URIs to be created,
   * or will need to refer to specific entities. This can be done using a URI of
   * <code>URI.create("$<i>id</i>$")</code>, where <code><i>id</i></code> is an
   * identifier to use to refer to the new entity in other axioms in the
   * cluster. See also the {@link #Axiom} interface for URIs to use to be
   * substituted for the action URI, axiom ID URI, in and out ontology URIs and
   * (String) time.
   * 
   * @param axioms Cluster of axioms to assert in the history provenance
   *          ontology when retracting an axiom.
   */
  public void setRetractionAxioms(Provenance.Axiom... axioms);

  /**
   * <!-- setAssertionAxioms -->
   * 
   * Set the axioms to assert. See
   * {@link #setRetractionAxioms(Provenance.Axiom[])}.
   * 
   * @param axioms Cluster of axioms to assert in the history provenance
   *          ontology when asserting an axiom.
   */
  public void setAssertionAxioms(Provenance.Axiom... axioms);

  /**
   * <!-- setObiamaAxioms -->
   * 
   * Set the axioms to assert to create the OBIAMA components of the ontology
   * 
   * @param axioms
   */
  public void setObiamaAxioms(Provenance.Axiom... axioms);

  /**
   * <!-- getAxiom -->
   * 
   * Get an axiom -- this method is mostly for use in
   * {@link #setRetractionAxioms(Provenance.Axiom[])} and {@link
   * #setAssertionAxioms(Provenance.Axiom[]}, which will be called by the
   * {@link #Implementation}.
   * 
   * @param subject Subject of the axiom
   * @param predicate Predicate of the axiom (worth bearing the OWLXMLVocabulary
   *          class (included with the OWLAPI) for this)
   * @param object Object of the axiom
   * @return the axiom
   * @throws ProvenanceException
   */
  public Provenance.Axiom getAxiom(URI subject, URI predicate, URI object) throws ProvenanceException;

  /**
   * <!-- getDataAxiom -->
   * 
   * Get a data property assertion axiom (with no specified type). See
   * {@link #getAxiom(URI, URI, URI)}.
   * 
   * @param subject
   * @param predicate
   * @param object
   * @return the axiom
   * @throws ProvenanceException
   */
  public Provenance.Axiom getDataAxiom(URI subject, URI predicate, String object) throws ProvenanceException;

  /**
   * <!-- getDataAxiom -->
   * 
   * Get a data property assertion axiom with a specified type. See
   * {@link #getAxiom(URI, URI, URI)}.
   * 
   * @param subject
   * @param predicate
   * @param data
   * @param type
   * @return the axiom
   * @throws ProvenanceException
   */
  public Provenance.Axiom getDataAxiom(URI subject, URI predicate, String data, XSDVocabulary type)
      throws ProvenanceException;

  /**
   * <!-- recordAssertion -->
   * 
   * Record an assertion. The arguments to this method contain any relevant
   * information that might be used when doing so. This, however, is not a
   * guarantee that they will be used.
   * 
   * @param action The {@link Action} responsible. (Note that the URI of the
   *          Action will be that in the schedule ontology.)
   * @param inOntology The ontology in which the axiom is to be asserted
   * @param outOntology The ontology with the axiom asserted in it
   * @param time The time when the assertion was made
   * @param subject Subject of the axiom
   * @param predicate Predicate of the axiom
   * @param object Object of the axiom
   */
  public void recordAssertion(Action action, URI inOntology, URI outOntology, String time, URI subject, URI predicate,
      URI object);

  /**
   * <!-- recordAssertion -->
   * 
   * Record a data property assertion. (See
   * {@link #recordAssertion(Action, URI, URI, String, URI, URI, URI)}.)
   * 
   * @param action
   * @param inOntology
   * @param outOntology
   * @param time
   * @param subject
   * @param predicate
   * @param data Data asserted
   * @param type Type of the data
   */
  public void recordAssertion(Action action, URI inOntology, URI outOntology, String time, URI subject, URI predicate,
      String data, XSDVocabulary type);

  /**
   * <!-- recordRetraction -->
   * 
   * Record a retraction. (See
   * {@link #recordAssertion(Action, URI, URI, String, URI, URI, URI)}.)
   * 
   * @param action
   * @param inOntology
   * @param outOntology
   * @param time
   * @param subject
   * @param predicate
   * @param object
   */
  public void recordRetraction(Action action, URI inOntology, URI outOntology, String time, URI subject, URI predicate,
      URI object);

  /**
   * <!-- recordRetraction -->
   * 
   * Record a data property retraction. (See
   * {@link #recordAssertion(Action, URI, URI, String, URI, URI, URI)}.)
   * 
   * @param action
   * @param inOntology
   * @param outOntology
   * @param time
   * @param subject
   * @param predicate
   * @param data
   * @param type
   */
  public void recordRetraction(Action action, URI inOntology, URI outOntology, String time, URI subject, URI predicate,
      String data, XSDVocabulary type);

  /**
   * <!-- recordAssertion -->
   * 
   * Record an assertion -- this to future proof for if/when axioms have
   * identifiers
   * 
   * @param action
   * @param inOntology
   * @param outOntology
   * @param time
   * @param axiomURI
   */
  public void recordAssertion(Action action, URI inOntology, URI outOntology, String time, URI axiomURI);

  /**
   * <!-- recordRetraction -->
   * 
   * Record a retraction -- future proof
   * 
   * @param action
   * @param inOntology
   * @param outOntology
   * @param time
   * @param axiomURI
   */
  public void recordRetraction(Action action, URI inOntology, URI outOntology, String time, URI axiomURI);

  /**
   * <!-- saveHistoryProvenanceOntology -->
   * 
   * Save the history provenance ontology
   * 
   * @param physicalURI Location to save it to
   */
  public void saveHistoryProvenanceOntology(URI physicalURI);

  /**
   * <!-- Axiom -->
   * 
   * Store and access Axiom data; also provide constants to use
   * 
   * @author Gary Polhill
   */
  public interface Axiom {
    /**
     * Standard URI to be substituted with the axiom ID when the provenance is
     * recorded
     */
    public static final URI AXIOM_ID_URI = URI.create("$$axiom$$");

    /**
     * Standard URI to be substituted with the action when the provenance is
     * recorded
     */
    public static final URI ACTION_URI = URI.create("$$action$$");

    /**
     * Standard URI to be substituted with the in ontology when the provenance
     * is recorded
     */
    public static final URI IN_ONTOLOGY_URI = URI.create("$$in$$");

    /**
     * Standard URI to be substituted with the out ontology when the provenance
     * is recorded
     */
    public static final URI OUT_ONTOLOGY_URI = URI.create("$$out$$");

    /**
     * Standard data to be substituted with the model time when the provenance
     * is recorded
     */
    public static final String TIME_STR = "$time$";

    /**
     * <!-- getSubject -->
     * 
     * @return The subject of the axiom
     */
    public URI getSubject();

    /**
     * <!-- getPredicate -->
     * 
     * @return The predicate of the axiom
     */
    public URI getPredicate();

    /**
     * <!-- getObject -->
     * 
     * @return The object of the axiom (could be <code>null</code> if the axiom
     *         is a data property assertion)
     */
    public URI getObject();

    /**
     * <!-- getObjectData -->
     * 
     * @return The object data of the axiom (could be <code>null</code> if the
     *         axiom is not a data property assertion)
     */
    public String getObjectData();

    /**
     * <!-- getObjectDataType -->
     * 
     * @return The type of the object data (could be <code>null</code> if the
     *         axiom is not a data property assertion)
     */
    public XSDVocabulary getObjectDataType();
  }

  /**
   * <!-- Implementation -->
   * 
   * Known implementations of the provenance
   * 
   * @author Gary Polhill
   */
  public enum Implementation {
    OPM, OPMV, POLICYGRID2, PG2, PROVO;

    /**
     * Open Provenance Model full OWL implementation
     */
    public static final URI OPM_URI = URI.create("http://openprovenance.org/model/opmo");

    /**
     * Open Provenance Model light OWL implementation
     */
    public static final URI OPMV_URI = URI.create("http://purl.org/net/opmv/ns");

    /**
     * PolicyGrid II OWL implementation
     */
    public static final URI PG2_URI = URI.create("http://www.policygrid.org/provenance-simulation.owl");

    /**
     * PROV-O OWL implementation
     */
    public static final URI PROVO_URI = URI.create("http://www.w3.org/ns/prov");

    // or should that be http://www.w3.org/TR/prov-o/prov-20120724.owl -- I
    // think not -- see hash() method

    /**
     * <!-- configureRecorder -->
     * 
     * Configure the provenance recorder according to the implementation
     * 
     * @param provenance Provenance recorder to configure
     */
    public void configureRecorder(Provenance provenance) {
      switch(this) {
      case OPM:
        configOPMRecorder(provenance);
        return;
      case OPMV:
        configOPMVRecorder(provenance);
        return;
      case POLICYGRID2:
      case PG2:
        configPG2Recorder(provenance);
        return;
      case PROVO:
        configPROVORecorder(provenance);
        return;
      default:
        throw new Panic();
      }
    }

    /**
     * <!-- getOntologyURI -->
     * 
     * @return The ontology URI associated with this implementation
     */
    public URI getOntologyURI() {
      switch(this) {
      case OPM:
        return OPM_URI;
      case POLICYGRID2:
      case PG2:
        return PG2_URI;
      case PROVO:
        return PROVO_URI;
      default:
        throw new Panic();
      }
    }

    /**
     * <!-- configOPMRecorder -->
     * 
     * @param provenance Provenance recorder to configure for the Open
     *          Provenance Model
     */
    private static void configOPMRecorder(Provenance provenance) {
      provenance.setProvenanceOntology(OPM_URI);

      try {

        provenance.setObiamaAxioms(
            provenance.getAxiom(AXIOM_URI, OWLXMLVocabulary.SUB_CLASS_OF.getURI(), hash(OPM_URI, "Artifact")),
            provenance.getAxiom(SUBJECT_URI, OWLXMLVocabulary.DATA_PROPERTY_DOMAIN.getURI(), AXIOM_URI),
            provenance.getAxiom(PREDICATE_URI, OWLXMLVocabulary.DATA_PROPERTY_DOMAIN.getURI(), AXIOM_URI),
            provenance.getAxiom(OBJECT_URI, OWLXMLVocabulary.DATA_PROPERTY_DOMAIN.getURI(), AXIOM_URI),
            provenance.getAxiom(DATA_URI, OWLXMLVocabulary.DATA_PROPERTY_DOMAIN.getURI(), AXIOM_URI),
            provenance.getAxiom(ASSERTION_URI, OWLXMLVocabulary.SUB_CLASS_OF.getURI(), hash(OPM_URI, "Process")),
            provenance.getAxiom(RETRACTION_URI, OWLXMLVocabulary.SUB_CLASS_OF.getURI(), hash(OPM_URI, "Process")));

        // Assertion -- create an assertion process instance, then say that the
        // assertion process used the in-ontology, generated the axiom and the
        // out-ontology, and was controlled by the action

        provenance
            .setAssertionAxioms(
                provenance.getAxiom(URI.create("$assertion$"), OWLXMLVocabulary.CLASS_ASSERTION.getURI(), ASSERTION_URI),

                provenance.getAxiom(URI.create("$used$"), hash(OPM_URI, "causeUsed"), Axiom.IN_ONTOLOGY_URI),

                provenance.getAxiom(URI.create("$used$"), hash(OPM_URI, "effectUsed"), URI.create("$assertion$")),

                provenance.getAxiom(URI.create("$wgbO$"), hash(OPM_URI, "causeWasGeneratedBy"),
                    URI.create("$assertion$")),
                provenance.getAxiom(URI.create("$wgbO$"), hash(OPM_URI, "effectWasGeneratedBy"), Axiom.OUT_ONTOLOGY_URI),

                provenance.getAxiom(URI.create("$wgbA$"), hash(OPM_URI, "causeWasGeneratedBy"),
                    URI.create("$assertion$")),

                provenance.getAxiom(URI.create("$wgbA$"), hash(OPM_URI, "effectWasGeneratedBy"), Axiom.AXIOM_ID_URI),

                provenance.getAxiom(URI.create("$wcb$"), hash(OPM_URI, "causeWasControlledBy"), Axiom.ACTION_URI),

                provenance.getAxiom(URI.create("$wcb$"), hash(OPM_URI, "effectWasControlledBy"),
                    URI.create("$assertion$")));

        // Retraction -- create a retraction process instance, then say that the
        // retraction process used the in-ontology and the axiom, generated the
        // out-ontology, and was controlled by the action
        provenance
            .setAssertionAxioms(provenance.getAxiom(URI.create("$assertion$"),
                OWLXMLVocabulary.CLASS_ASSERTION.getURI(), RETRACTION_URI),

            provenance.getAxiom(URI.create("$usedO$"), hash(OPM_URI, "causeUsed"), Axiom.IN_ONTOLOGY_URI),

            provenance.getAxiom(URI.create("$usedO$"), hash(OPM_URI, "effectUsed"), URI.create("$assertion$")),

            provenance.getAxiom(URI.create("$usedA$"), hash(OPM_URI, "causeUsed"), Axiom.AXIOM_ID_URI),

            provenance.getAxiom(URI.create("$usedA$"), hash(OPM_URI, "effectUsed"), URI.create("$assertion$")),

            provenance.getAxiom(URI.create("$wgb$"), hash(OPM_URI, "causeWasGeneratedBy"), URI.create("$assertion$")),

            provenance.getAxiom(URI.create("$wgb$"), hash(OPM_URI, "effectWasGeneratedBy"), Axiom.OUT_ONTOLOGY_URI),

            provenance.getAxiom(URI.create("$wcb$"), hash(OPM_URI, "causeWasControlledBy"), Axiom.ACTION_URI),

            provenance.getAxiom(URI.create("$wcb$"), hash(OPM_URI, "effectWasControlledBy"), URI.create("$assertion$")));
      }
      catch(ProvenanceException e) {
        throw new Bug(e.getMessage());
      }

    }

    /**
     * <!-- configOPMVRecorder -->
     * 
     * @param provenance Provenance recorder to configure for the light open
     *          provenance model
     */
    private static void configOPMVRecorder(Provenance provenance) {
      provenance.setProvenanceOntology(OPMV_URI);

      try {
        provenance.setObiamaAxioms(
            provenance.getAxiom(AXIOM_URI, OWLXMLVocabulary.SUB_CLASS_OF.getURI(), hash(OPMV_URI, "Artifact")),
            provenance.getAxiom(SUBJECT_URI, OWLXMLVocabulary.DATA_PROPERTY_DOMAIN.getURI(), AXIOM_URI),
            provenance.getAxiom(PREDICATE_URI, OWLXMLVocabulary.DATA_PROPERTY_DOMAIN.getURI(), AXIOM_URI),
            provenance.getAxiom(OBJECT_URI, OWLXMLVocabulary.DATA_PROPERTY_DOMAIN.getURI(), AXIOM_URI),
            provenance.getAxiom(DATA_URI, OWLXMLVocabulary.DATA_PROPERTY_DOMAIN.getURI(), AXIOM_URI),
            provenance.getAxiom(ASSERTION_URI, OWLXMLVocabulary.SUB_CLASS_OF.getURI(), hash(OPMV_URI, "Process")),
            provenance.getAxiom(RETRACTION_URI, OWLXMLVocabulary.SUB_CLASS_OF.getURI(), hash(OPMV_URI, "Process")));

        // Assertion -- see configOPMRecorder
        provenance.setAssertionAxioms(
            provenance.getAxiom(URI.create("$assertion$"), OWLXMLVocabulary.CLASS_ASSERTION.getURI(), ASSERTION_URI),
            provenance.getAxiom(URI.create("$assertion$"), hash(OPMV_URI, "used"), Axiom.IN_ONTOLOGY_URI),
            provenance.getAxiom(Axiom.OUT_ONTOLOGY_URI, hash(OPMV_URI, "wasGeneratedBy"), URI.create("$assertion$")),
            provenance.getAxiom(Axiom.AXIOM_ID_URI, hash(OPMV_URI, "wasGeneratedBy"), URI.create("$assertion$")),
            provenance.getAxiom(URI.create("$assertion$"), hash(OPMV_URI, "wasControlledBy"), Axiom.ACTION_URI));

        // Retraction -- see configOPMRecorder
        provenance.setRetractionAxioms(
            provenance.getAxiom(URI.create("$retraction$"), OWLXMLVocabulary.CLASS_ASSERTION.getURI(), RETRACTION_URI),
            provenance.getAxiom(URI.create("$retraction$"), hash(OPMV_URI, "used"), Axiom.IN_ONTOLOGY_URI),
            provenance.getAxiom(URI.create("$retraction$"), hash(OPMV_URI, "used"), Axiom.AXIOM_ID_URI),
            provenance.getAxiom(Axiom.OUT_ONTOLOGY_URI, hash(OPMV_URI, "wasGeneratedBy"), URI.create("$retraction$")),
            provenance.getAxiom(URI.create("$retraction$"), hash(OPMV_URI, "wasControlledBy"), Axiom.ACTION_URI));
      }
      catch(ProvenanceException e) {
        throw new Bug(e.getMessage());
      }

    }

    /**
     * <!-- configPG2Recorder -->
     * 
     * @param provenance Provenance recorder to configure for the PolicyGrid II
     *          framework
     */
    private static void configPG2Recorder(Provenance provenance) {
      configOPMRecorder(provenance);
    }

    /**
     * <!-- configPROVORecorder -->
     * 
     * @param provenance Provenance recorder to configure for the PROV-O
     *          ontology
     */
    private static void configPROVORecorder(Provenance provenance) {
      provenance.setProvenanceOntology(PROVO_URI);
      try {
        provenance.setObiamaAxioms(
            provenance.getAxiom(AXIOM_URI, OWLXMLVocabulary.SUB_CLASS_OF.getURI(), hash(PROVO_URI, "Entity")),
            provenance.getAxiom(SUBJECT_URI, OWLXMLVocabulary.DATA_PROPERTY_DOMAIN.getURI(), AXIOM_URI),
            provenance.getAxiom(PREDICATE_URI, OWLXMLVocabulary.DATA_PROPERTY_DOMAIN.getURI(), AXIOM_URI),
            provenance.getAxiom(OBJECT_URI, OWLXMLVocabulary.DATA_PROPERTY_DOMAIN.getURI(), AXIOM_URI),
            provenance.getAxiom(DATA_URI, OWLXMLVocabulary.DATA_PROPERTY_DOMAIN.getURI(), AXIOM_URI),
            provenance.getAxiom(ASSERTION_URI, OWLXMLVocabulary.SUB_CLASS_OF.getURI(), hash(PROVO_URI, "Activity")),
            provenance.getAxiom(RETRACTION_URI, OWLXMLVocabulary.SUB_CLASS_OF.getURI(), hash(PROVO_URI, "Activity")));

        provenance.setAssertionAxioms(
            provenance.getAxiom(URI.create("$assertion$"), OWLXMLVocabulary.CLASS_ASSERTION.getURI(), ASSERTION_URI),
            provenance.getAxiom(URI.create("$assertion$"), hash(PROVO_URI, "used"), Axiom.IN_ONTOLOGY_URI),
            provenance.getAxiom(Axiom.OUT_ONTOLOGY_URI, hash(PROVO_URI, "wasGeneratedBy"), URI.create("$assertion$")),
            provenance.getAxiom(Axiom.AXIOM_ID_URI, hash(PROVO_URI, "wasGeneratedBy"), URI.create("$assertion$")),
            provenance.getAxiom(Axiom.ACTION_URI, OWLXMLVocabulary.CLASS_ASSERTION.getURI(), hash(PROVO_URI, "Agent")),
            // provenance.getAxiom(Axiom.AXIOM_ID_URI, hash(PROVO_URI,
            // "wasAttributedTo"), Axiom.ACTION_URI),
            provenance.getAxiom(URI.create("$assertion$"), hash(PROVO_URI, "wasAssociatedWith"), Axiom.ACTION_URI));

        provenance.setRetractionAxioms(
            provenance.getAxiom(URI.create("$retraction$"), OWLXMLVocabulary.CLASS_ASSERTION.getURI(), RETRACTION_URI),
            provenance.getAxiom(URI.create("$retraction$"), hash(PROVO_URI, "used"), Axiom.IN_ONTOLOGY_URI),
            provenance.getAxiom(URI.create("$retraction$"), hash(PROVO_URI, "used"), Axiom.AXIOM_ID_URI),
            provenance.getAxiom(Axiom.OUT_ONTOLOGY_URI, hash(PROVO_URI, "wasGeneratedBy"), URI.create("$retraction$")),
            provenance.getAxiom(Axiom.ACTION_URI, OWLXMLVocabulary.CLASS_ASSERTION.getURI(), hash(PROVO_URI, "Agent")),
            provenance.getAxiom(URI.create("$retraction$"), hash(PROVO_URI, "wasAssociatedWith"), Axiom.ACTION_URI));
      }
      catch(ProvenanceException e) {
        throw new Bug(e.getMessage());
      }

    }

    /**
     * <!-- hash -->
     * 
     * Utility method to create a URI referring to an entity in an ontology
     * 
     * @param ontologyURI Ontology
     * @param entity Entity to refer to in it
     * @return
     */
    private static URI hash(URI ontologyURI, String entity) {
      if(ontologyURI.toString().endsWith("#")) return URI.create(ontologyURI + entity);
      else
        return URI.create(ontologyURI + "#" + entity);
    }
  }
}
