/*
 * uk.ac.hutton.obiama.msb: OWLAPIProvenance.java
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLOntologyStorageException;
import org.semanticweb.owl.model.UnknownOWLOntologyException;
import org.semanticweb.owl.vocab.OWLXMLVocabulary;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.ProvenanceException;
import uk.ac.hutton.util.SetCreator;

/**
 * <!-- OWLAPIProvenance -->
 * 
 * Implementation of provenance for the OWLAPI
 * 
 * @author Gary Polhill
 */
public class OWLAPIProvenance implements Provenance {
  /**
   * OWLOntologyManager
   */
  OWLOntologyManager manager;

  /**
   * OWLDataFactory
   */
  OWLDataFactory factory;

  /**
   * OWLOntology object for the provenance ontology
   */
  OWLOntology provenanceOntology;

  /**
   * OWLOntology object for the OBIAMA provenance ontology
   */
  OWLOntology obiamaProvenanceOntology;

  /**
   * OWLOntology object for the history provenance ontology
   */
  OWLOntology ontology;

  /**
   * Set of axioms to add to the history provenance ontology for each assertion
   */
  Set<Axiom> assertionAxioms;

  /**
   * Set of axioms to add to the history provenance ontology for each retraction
   */
  Set<Axiom> retractionAxioms;

  /**
   * Counters for each kind of entity created in each assertion/retraction
   */
  Map<String, Integer> entityCounters;

  /**
   * URIs for axioms in the model state
   */
  Map<String, URI> axiomURIs;
  
  /**
   * Constructor
   */
  public OWLAPIProvenance() {
    manager = OWLManager.createOWLOntologyManager();
    factory = manager.getOWLDataFactory();
    assertionAxioms = new HashSet<Axiom>();
    retractionAxioms = new HashSet<Axiom>();
    provenanceOntology = null;
    ontology = null;
    entityCounters = new HashMap<String, Integer>();
    axiomURIs = new HashMap<String, URI>();
  }

  /**
   * <!-- nextEntity -->
   * 
   * @param key Label for the entity
   * @return The next counter value for the entity argument
   */
  private int nextEntity(String key) {
    if(!entityCounters.containsKey(key)) {
      entityCounters.put(key, 0);
    }
    Integer curVal = entityCounters.get(key);
    curVal++;
    entityCounters.put(key, curVal);
    return curVal;
  }

  /**
   * <!-- generateURI -->
   * 
   * @param key
   * @return Build a URI for an entity
   */
  private URI generateURI(String key) {
    if(provenanceOntology == null) {
      throw new Bug();
    }
    URI provURI = provenanceOntology.getURI();
    if(provURI.toString().endsWith("#")) return URI.create(provURI + key + "_" + Integer.toString(nextEntity(key)));
    else
      return URI.create(provURI + "#" + key + "_" + Integer.toString(nextEntity(key)));
  }

  /**
   * <!-- setProvenanceOntology -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#setProvenanceOntology(java.net.URI)
   * @param ontologyURI URI of the provenance ontology to be used
   */
  public void setProvenanceOntology(URI ontologyURI) {
    if(provenanceOntology == null) {
      try {
        provenanceOntology = OntologyIOHelper.load(ontologyURI, manager);
        obiamaProvenanceOntology = manager.createOntology(HISTORY_PROVENANCE_URI);
      }
      catch(OWLOntologyCreationException e) {
        ErrorHandler.redo(e, "setting the provenance ontology");
        throw new Bug();
      }
    }
    else {
      throw new Bug("Provenance ontology cannot be set twice");
    }
  }

  /**
   * <!-- setRetractionAxioms -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#setRetractionAxioms(java.util.Set)
   * @param axioms Axioms to be added for each retraction
   */
  public void setRetractionAxioms(Provenance.Axiom... axioms) {
    for(Provenance.Axiom axiom: axioms) {
      retractionAxioms.add(new Axiom(axiom));
    }
  }

  /**
   * <!-- setAssertionAxioms -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#setAssertionAxioms(java.util.Set)
   * @param axioms Axioms to be added for each assertion
   */
  public void setAssertionAxioms(Provenance.Axiom... axioms) {
    for(Provenance.Axiom axiom: axioms) {
      assertionAxioms.add(new Axiom(axiom));
    }
  }

  /**
   * <!-- setObiamaAxioms -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#setObiamaAxioms(uk.ac.hutton.obiama.msb.Provenance.Axiom[])
   * @param axioms Axioms to be added when creating the OBIAMA extensions to the
   *          provenance ontology
   */
  public void setObiamaAxioms(Provenance.Axiom... axioms) {
    for(Provenance.Axiom axiom: axioms) {
      Axiom axiomOWLAPI = new Axiom(axiom);
      try {
        manager.addAxiom(obiamaProvenanceOntology, axiomOWLAPI.getOWLAxiom());
      }
      catch(OWLOntologyChangeException e) {
        ErrorHandler.redo(e, "adding OBIAMA axiom to provenance ontology");
      }
    }
  }

  /**
   * <!-- getAxiom -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#getAxiom(java.net.URI,
   *      java.net.URI, java.net.URI)
   * @param subject
   * @param predicate
   * @param object
   * @return An object property or class assertion axiom
   * @throws ProvenanceException
   */
  public Provenance.Axiom getAxiom(URI subject, URI predicate, URI object) throws ProvenanceException {
    if(predicate.equals(OWLXMLVocabulary.CLASS_ASSERTION.getURI())) {
      if(!provenanceOntology.containsClassReference(object) && !obiamaProvenanceOntology.containsClassReference(object)) {
        throw new ProvenanceException("attempt to make class assertion to nonexistent provenance ontology class "
          + object);
      }
    }
    else if(!provenanceOntology.containsObjectPropertyReference(predicate)
      && !obiamaProvenanceOntology.containsObjectPropertyReference(predicate)
      && !predicate.relativize(OWLXMLVocabulary.ONTOLOGY.getURI()).toString().startsWith("#")) {
      throw new ProvenanceException("no such object property in provenance ontology as " + predicate);
    }
    return new Axiom(subject, predicate, object);
  }

  /**
   * <!-- getDataAxiom -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#getDataAxiom(java.net.URI,
   *      java.net.URI, java.lang.String)
   * @param subject
   * @param predicate
   * @param object
   * @return An untyped data property assertion axiom
   * @throws ProvenanceException
   */
  public Provenance.Axiom getDataAxiom(URI subject, URI predicate, String object) throws ProvenanceException {
    if(!provenanceOntology.containsDataPropertyReference(predicate)
      && !obiamaProvenanceOntology.containsDataPropertyReference(predicate)
      && !predicate.relativize(OWLXMLVocabulary.ONTOLOGY.getURI()).toString().startsWith("#")) {
      throw new ProvenanceException("no such data property in provenance ontology as " + predicate);
    }
    return new Axiom(subject, predicate, object);
  }

  /**
   * <!-- getDataAxiom -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#getDataAxiom(java.net.URI,
   *      java.net.URI, java.lang.String,
   *      org.semanticweb.owl.vocab.XSDVocabulary)
   * @param subject
   * @param predicate
   * @param object
   * @param type
   * @return A typed data property assertion axiom
   * @throws ProvenanceException
   */
  public Provenance.Axiom getDataAxiom(URI subject, URI predicate, String object, XSDVocabulary type)
      throws ProvenanceException {
    if(!provenanceOntology.containsDataPropertyReference(predicate)
      && !obiamaProvenanceOntology.containsDataPropertyReference(predicate)
      && !predicate.relativize(OWLXMLVocabulary.ONTOLOGY.getURI()).toString().startsWith("#")) {
      throw new ProvenanceException("no such data property in provenance ontology as " + predicate);
    }
    return new Axiom(subject, predicate, object, type);
  }

  /**
   * <!-- setHistoryProvenanceOntology -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#setHistoryProvenanceOntology(java.net.URI)
   * @param ontologyURI URI to use for the history provenance ontology
   */
  public void setHistoryProvenanceOntology(URI ontologyURI) {
    if(provenanceOntology == null) {
      throw new Bug("The provenance ontology must be set before the recorded provenance ontology");
    }
    try {
      ontology = manager.createOntology(ontologyURI);
      manager.addAxiom(ontology, factory.getOWLImportsDeclarationAxiom(ontology, provenanceOntology.getURI()));
    }
    catch(OWLOntologyCreationException e) {
      ErrorHandler.redo(e, "creating history provenance ontology");
      throw new Bug();
    }
    catch(OWLOntologyChangeException e) {
      ErrorHandler.redo(e, "creating history provenance ontology");
      throw new Bug();
    }
  }

  /**
   * <!-- addAxiomAxiom -->
   * 
   * Build a URI for an object property or class assertion axiom in the history
   * provenance ontology. This creates axioms that assert the properties of the
   * axiom.
   * 
   * @param subject
   * @param predicate
   * @param object
   * @return URI in the history provenance ontology of the assertion axiom
   * @throws OWLOntologyChangeException
   */
  private URI addAxiomAxiom(URI subject, URI predicate, URI object) throws OWLOntologyChangeException {
    String axiomKey = subject.toString() + " " + predicate.toString() + " " + object.toString();
    if(axiomURIs.containsKey(axiomKey)) return axiomURIs.get(axiomKey);

    URI axiomURI = generateURI("axiom");
    OWLIndividual axiomInd = factory.getOWLIndividual(axiomURI);

    manager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(axiomInd,
        factory.getOWLObjectProperty(Provenance.SUBJECT_URI), factory.getOWLIndividual(subject)));
    manager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(axiomInd,
        factory.getOWLObjectProperty(Provenance.PREDICATE_URI), factory.getOWLIndividual(predicate)));
    manager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(axiomInd,
        factory.getOWLObjectProperty(Provenance.OBJECT_URI), factory.getOWLIndividual(object)));

    axiomURIs.put(axiomKey, axiomURI);
    return axiomURI;
  }

  /**
   * <!-- addAxiomAxiom -->
   * 
   * Build a data property assertion axiom in the history provenance ontology,
   * returning the URI of it
   * 
   * @param subject
   * @param predicate
   * @param data
   * @param type
   * @return URI of data property axiom.
   * @throws OWLOntologyChangeException
   */
  private URI addAxiomAxiom(URI subject, URI predicate, String data, XSDVocabulary type)
      throws OWLOntologyChangeException {
    String axiomKey = subject.toString() + " " + predicate.toString() + " " + type.getURI().toString() + " " + data;
    if(axiomURIs.containsKey(axiomKey)) return axiomURIs.get(axiomKey);

    URI axiomURI = generateURI("axiom");
    OWLIndividual axiomInd = factory.getOWLIndividual(axiomURI);

    manager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(axiomInd,
        factory.getOWLObjectProperty(Provenance.SUBJECT_URI), factory.getOWLIndividual(subject)));
    manager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(axiomInd,
        factory.getOWLObjectProperty(Provenance.PREDICATE_URI), factory.getOWLIndividual(predicate)));
    manager.addAxiom(
        ontology,
        factory.getOWLDataPropertyAssertionAxiom(axiomInd, factory.getOWLDataProperty(Provenance.DATA_URI),
            factory.getOWLTypedConstant(data, factory.getOWLDataType(type.getURI()))));

    axiomURIs.put(axiomKey, axiomURI);
    return axiomURI;
  }

  /**
   * <!-- recordAssertion -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#recordAssertion(uk.ac.hutton.obiama.action.Action,
   *      java.net.URI, java.net.URI, java.lang.String, java.net.URI,
   *      java.net.URI, java.net.URI)
   * @param action
   * @param inOntology
   * @param outOntology
   * @param time
   * @param subject
   * @param predicate
   * @param object
   */
  public void recordAssertion(Action action, URI inOntology, URI outOntology, String time, URI subject, URI predicate,
      URI object) {

    try {
      URI axiomURI = addAxiomAxiom(subject, predicate, object);

      recordAssertion(action, inOntology, outOntology, time, axiomURI);
    }
    catch(OWLOntologyChangeException e) {
      ErrorHandler.redo(e, "recording assertion in history provenance ontology");
      throw new Bug();
    }

  }

  /**
   * <!-- recordAssertion -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#recordAssertion(uk.ac.hutton.obiama.action.Action,
   *      java.net.URI, java.net.URI, java.lang.String, java.net.URI,
   *      java.net.URI, java.lang.String,
   *      org.semanticweb.owl.vocab.XSDVocabulary)
   * @param action
   * @param inOntology
   * @param outOntology
   * @param time
   * @param subject
   * @param predicate
   * @param data
   * @param type
   */
  public void recordAssertion(Action action, URI inOntology, URI outOntology, String time, URI subject, URI predicate,
      String data, XSDVocabulary type) {

    try {
      URI axiomURI = addAxiomAxiom(subject, predicate, data, type);

      recordAssertion(action, inOntology, outOntology, time, axiomURI);
    }
    catch(OWLOntologyChangeException e) {
      ErrorHandler.redo(e, "recording assertion in history provenance ontology");
      throw new Bug();
    }

  }

  /**
   * <!-- recordRetraction -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#recordRetraction(uk.ac.hutton.obiama.action.Action,
   *      java.net.URI, java.net.URI, java.lang.String, java.net.URI,
   *      java.net.URI, java.net.URI)
   * @param action
   * @param inOntology
   * @param outOntology
   * @param time
   * @param subject
   * @param predicate
   * @param object
   */
  public void recordRetraction(Action action, URI inOntology, URI outOntology, String time, URI subject, URI predicate,
      URI object) {

    try {
      URI axiomURI = addAxiomAxiom(subject, predicate, object);

      recordRetraction(action, inOntology, outOntology, time, axiomURI);
    }
    catch(OWLOntologyChangeException e) {
      ErrorHandler.redo(e, "recording retraction in history provenance ontology");
      throw new Bug();
    }

  }

  /**
   * <!-- recordRetraction -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#recordRetraction(uk.ac.hutton.obiama.action.Action,
   *      java.net.URI, java.net.URI, java.lang.String, java.net.URI,
   *      java.net.URI, java.lang.String,
   *      org.semanticweb.owl.vocab.XSDVocabulary)
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
      String data, XSDVocabulary type) {

    try {
      URI axiomURI = addAxiomAxiom(subject, predicate, data, type);

      recordRetraction(action, inOntology, outOntology, time, axiomURI);
    }
    catch(OWLOntologyChangeException e) {
      ErrorHandler.redo(e, "recording retraction in history provenance ontology");
      throw new Bug();
    }

  }

  /**
   * <!-- recordAssertion -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#recordAssertion(uk.ac.hutton.obiama.action.Action,
   *      java.net.URI, java.net.URI, java.lang.String, java.net.URI)
   * @param action
   * @param inOntology
   * @param outOntology
   * @param time
   * @param axiomURI
   */
  @Override
  public void recordAssertion(Action action, URI inOntology, URI outOntology, String time, URI axiomURI) {

    Map<String, URI> substitutions = new HashMap<String, URI>();

    for(Axiom axiom: assertionAxioms) {
      try {
        OWLAxiom owlAxiom = axiom.getOWLAxiom(action, inOntology, outOntology, axiomURI, substitutions);
        if(owlAxiom != null) manager.addAxiom(ontology, owlAxiom);
      }
      catch(OWLOntologyChangeException e) {
        ErrorHandler.redo(e, "recording assertion in history provenance ontology");
        throw new Bug();
      }
    }

  }

  /**
   * <!-- recordRetraction -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#recordRetraction(uk.ac.hutton.obiama.action.Action,
   *      java.net.URI, java.net.URI, java.lang.String, java.net.URI)
   * @param action
   * @param inOntology
   * @param outOntology
   * @param time
   * @param axiomURI
   */
  @Override
  public void recordRetraction(Action action, URI inOntology, URI outOntology, String time, URI axiomURI) {

    Map<String, URI> substitutions = new HashMap<String, URI>();

    for(Axiom axiom: retractionAxioms) {
      try {
        OWLAxiom owlAxiom = axiom.getOWLAxiom(action, inOntology, outOntology, axiomURI, substitutions);
        if(owlAxiom != null) manager.addAxiom(ontology, owlAxiom);
      }
      catch(OWLOntologyChangeException e) {
        ErrorHandler.redo(e, "recording retraction in history provenance ontology");
        throw new Bug();
      }
    }
  }

  /**
   * <!-- saveHistoryProvenanceOntology -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#saveHistoryProvenanceOntology(java.net.URI)
   * @param physicalURI
   */
  public void saveHistoryProvenanceOntology(URI physicalURI) {
    try {
      manager.saveOntology(ontology, physicalURI);
    }
    catch(UnknownOWLOntologyException e) {
      ErrorHandler.redo(e, "saving history provenance ontology to " + physicalURI);
      throw new Bug();
    }
    catch(OWLOntologyStorageException e) {
      ErrorHandler.redo(e, "saving history provenance ontology to " + physicalURI);
      throw new Bug();
    }
  }

  /**
   * <!-- Axiom -->
   * 
   * Implementation of the Provenance.Axiom interface
   * 
   * @author Gary Polhill
   */
  public class Axiom implements Provenance.Axiom {
    /**
     * Subject of the axiom
     */
    private URI subject;

    /**
     * Predicate of the axiom
     */
    private URI predicate;

    /**
     * Object of the axiom (or <code>null</code> if data property)
     */
    private URI object;

    /**
     * Data object of the axiom (or <code>null</code> if object property)
     */
    private String objectStr;

    /**
     * Type of the data (or <code>null</code> if object property or data
     * untyped)
     */
    private XSDVocabulary objectType;

    /**
     * Constructor for object property or class assertions
     * 
     * @param subject
     * @param predicate
     * @param object
     */
    Axiom(URI subject, URI predicate, URI object) {
      this.subject = subject;
      this.predicate = predicate;
      this.object = object;
      objectStr = null;
      objectType = null;
    }

    /**
     * Constructor for untyped data property assertions
     * 
     * @param subject
     * @param predicate
     * @param objectStr
     */
    Axiom(URI subject, URI predicate, String objectStr) {
      this(subject, predicate, objectStr, null);
    }

    /**
     * Constructor for typed data property assertions
     * 
     * @param subject
     * @param predicate
     * @param objectStr
     * @param objectType
     */
    Axiom(URI subject, URI predicate, String objectStr, XSDVocabulary objectType) {
      this.subject = subject;
      this.predicate = predicate;
      this.objectStr = objectStr;
      this.objectType = objectType;
      object = null;
    }

    /**
     * Cloning constructor
     * 
     * @param axiom
     */
    Axiom(Provenance.Axiom axiom) {
      this.subject = axiom.getSubject();
      this.predicate = axiom.getPredicate();
      this.object = axiom.getObject();
      this.objectStr = axiom.getObjectData();
      this.objectType = axiom.getObjectDataType();
    }

    /**
     * <!-- substitute -->
     * 
     * Create a proper URI from those indicating substitutions in the axiom
     * assertion/retraction sets
     * 
     * @param uri
     * @param action
     * @param inOntology
     * @param outOntology
     * @param axiomURI
     * @param substitutions
     * @return 'proper' URI without data to be substituted
     */
    private URI substitute(URI uri, Action action, URI inOntology, URI outOntology, URI axiomURI,
        Map<String, URI> substitutions) {
      if(uri == null) return null;

      String uriStr = uri.toString();

      if(uri.equals(Provenance.Axiom.ACTION_URI)) {
        return action.getURI();
      }
      else if(uri.equals(Provenance.Axiom.IN_ONTOLOGY_URI)) {
        return inOntology;
      }
      else if(uri.equals(Provenance.Axiom.OUT_ONTOLOGY_URI)) {
        return outOntology;
      }
      else if(uri.equals(Provenance.Axiom.AXIOM_ID_URI)) {
        return axiomURI;
      }
      else if(uriStr.startsWith("$") && uriStr.endsWith("$")) {
        if(!substitutions.containsKey(uriStr)) {
          substitutions.put(uriStr, generateURI(uriStr.substring(1, uriStr.length() - 1)));
        }
        return substitutions.get(uriStr);
      }
      else {
        return uri;
      }
    }

    /**
     * <!-- getSubject -->
     * 
     * @see uk.ac.hutton.obiama.msb.Provenance.Axiom#getSubject()
     * @return The subject of the axiom as constructed
     */
    public URI getSubject() {
      return subject;
    }

    /**
     * <!-- getSubject -->
     * 
     * @param action
     * @param inOntology
     * @param outOntology
     * @param axiomURI
     * @param substitutions
     * @return The subject of the axiom with substitutions
     */
    URI getSubject(Action action, URI inOntology, URI outOntology, URI axiomURI, Map<String, URI> substitutions) {
      return substitute(subject, action, inOntology, outOntology, axiomURI, substitutions);
    }

    /**
     * <!-- getPredicate -->
     * 
     * @see uk.ac.hutton.obiama.msb.Provenance.Axiom#getPredicate()
     * @return The predicate of the axiom as constructed
     */
    public URI getPredicate() {
      return predicate;
    }

    /**
     * <!-- getObject -->
     * 
     * @see uk.ac.hutton.obiama.msb.Provenance.Axiom#getObject()
     * @return The object of the axiom as constructed (or <code>null</code> if
     *         it's a data property assertion)
     */
    public URI getObject() {
      return object;
    }

    /**
     * <!-- getObjectData -->
     * 
     * @see uk.ac.hutton.obiama.msb.Provenance.Axiom#getObjectData()
     * @return The data of the axiom as constructed (or <code>null</code> if
     *         it's an object property or class assertion)
     */
    public String getObjectData() {
      return objectStr;
    }

    /**
     * <!-- getObjectDataType -->
     * 
     * @see uk.ac.hutton.obiama.msb.Provenance.Axiom#getObjectDataType()
     * @return The data type of the axiom as constructed (or <code>null</code>
     *         if it's an object property or class assertion, or the data
     *         property assertion is untyped)
     */
    public XSDVocabulary getObjectDataType() {
      return objectType;
    }

    /**
     * <!-- getObject -->
     * 
     * @param action
     * @param inOntology
     * @param outOntology
     * @param axiomURI
     * @param substitutions
     * @return The object of the axiom with substitutions
     */
    URI getObject(Action action, URI inOntology, URI outOntology, URI axiomURI, Map<String, URI> substitutions) {
      return substitute(object, action, inOntology, outOntology, axiomURI, substitutions);
    }

    /**
     * <!-- getOWLAxiom -->
     * 
     * @param action
     * @param inOntology
     * @param outOntology
     * @param axiomURI
     * @param substitutions
     * @return An OWLAxiom object corresponding to this axiom
     */
    OWLAxiom getOWLAxiom(Action action, URI inOntology, URI outOntology, URI axiomURI, Map<String, URI> substitutions) {
      URI subj = getSubject(action, inOntology, outOntology, axiomURI, substitutions);
      URI obj = getObject(action, inOntology, outOntology, axiomURI, substitutions);

      if(subj == null) return null;

      if(predicate.equals(OWLXMLVocabulary.CLASS_ASSERTION.getURI())) {
        if(obj == null) return null;

        return factory.getOWLClassAssertionAxiom(factory.getOWLIndividual(subj), factory.getOWLClass(obj));
      }
      else if(object != null) {
        if(obj == null) return null;

        return factory.getOWLObjectPropertyAssertionAxiom(factory.getOWLIndividual(subj),
            factory.getOWLObjectProperty(predicate), factory.getOWLIndividual(obj));
      }
      else if(objectType != null) {
        return factory.getOWLDataPropertyAssertionAxiom(factory.getOWLIndividual(subj),
            factory.getOWLDataProperty(predicate),
            factory.getOWLTypedConstant(objectStr, factory.getOWLDataType(objectType.getURI())));
      }
      else {
        return factory.getOWLDataPropertyAssertionAxiom(factory.getOWLIndividual(subj),
            factory.getOWLDataProperty(predicate), factory.getOWLUntypedConstant(objectStr));
      }
    }

    OWLAxiom getOWLAxiom() {
      if(predicate.relativize(OWLXMLVocabulary.ONTOLOGY.getURI()).toString().startsWith("#")) {
        // If the relativization (sorry) of the predicate with respect to one of
        // the OWLXMLVocabularies begins with a #, then they must both have the
        // same web address, and hence the predicate is part of OWL.
        for(OWLXMLVocabulary owl: OWLXMLVocabulary.values()) {
          if(owl.getURI().equals(predicate)) {
            switch(owl) {
            case ANTI_SYMMETRIC_OBJECT_PROPERTY:
              return factory.getOWLAntiSymmetricObjectPropertyAxiom(factory.getOWLObjectProperty(subject));
            case CLASS_ASSERTION:
              return factory.getOWLClassAssertionAxiom(factory.getOWLIndividual(subject), factory.getOWLClass(object));
            case DATA_PROPERTY_DOMAIN:
              return factory.getOWLDataPropertyDomainAxiom(factory.getOWLDataProperty(subject),
                  factory.getOWLClass(object));
            case DATA_PROPERTY_RANGE:
              return factory.getOWLDataPropertyRangeAxiom(factory.getOWLDataProperty(subject),
                  factory.getOWLDataType(object));
            case DECLARATION:
              if(object.equals(OWLXMLVocabulary.CLASS.getURI())) {
                return factory.getOWLDeclarationAxiom(factory.getOWLClass(subject));
              }
              else if(object.equals(OWLXMLVocabulary.DATA_PROPERTY.getURI())) {
                return factory.getOWLDeclarationAxiom(factory.getOWLDataProperty(subject));
              }
              else if(object.equals(OWLXMLVocabulary.INDIVIDUAL.getURI())) {
                return factory.getOWLDeclarationAxiom(factory.getOWLIndividual(subject));
              }
              else if(object.equals(OWLXMLVocabulary.OBJECT_PROPERTY.getURI())) {
                return factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(subject));
              }
              else {
                throw new Bug("Declaration axiom doesn't have recognised OWL entity type URI as subject");
              }
            case DIFFERENT_INDIVIDUALS:
              return factory.getOWLDifferentIndividualsAxiom(factory.getOWLIndividual(subject),
                  factory.getOWLIndividual(object));
            case DISJOINT_CLASSES:
              return factory.getOWLDisjointClassesAxiom(factory.getOWLClass(subject), factory.getOWLClass(object));
            case DISJOINT_DATA_PROPERTIES:
              return factory.getOWLDisjointDataPropertiesAxiom(SetCreator.createSet(
                  factory.getOWLDataProperty(subject), factory.getOWLDataProperty(object)));
            case DISJOINT_OBJECT_PROPERTIES:
              return factory.getOWLDisjointObjectPropertiesAxiom(SetCreator.createSet(
                  factory.getOWLObjectProperty(subject), factory.getOWLObjectProperty(object)));
            case EQUIVALENT_CLASSES:
              return factory.getOWLEquivalentClassesAxiom(factory.getOWLClass(subject), factory.getOWLClass(object));
            case EQUIVALENT_DATA_PROPERTIES:
              return factory.getOWLEquivalentDataPropertiesAxiom(SetCreator.createSet(
                  factory.getOWLDataProperty(subject), factory.getOWLDataProperty(object)));
            case EQUIVALENT_OBJECT_PROPERTIES:
              return factory.getOWLEquivalentObjectPropertiesAxiom(SetCreator.createSet(
                  factory.getOWLObjectProperty(subject), factory.getOWLObjectProperty(object)));
            case FUNCTIONAL_DATA_PROPERTY:
              return factory.getOWLFunctionalDataPropertyAxiom(factory.getOWLDataProperty(subject));
            case FUNCTIONAL_OBJECT_PROPERTY:
              return factory.getOWLFunctionalObjectPropertyAxiom(factory.getOWLObjectProperty(object));
            case INVERSE_FUNCTIONAL_OBJECT_PROPERTY:
              return factory.getOWLInverseFunctionalObjectPropertyAxiom(factory.getOWLObjectProperty(object));
            case INVERSE_OBJECT_PROPERTIES:
              return factory.getOWLInverseObjectPropertiesAxiom(factory.getOWLObjectProperty(subject),
                  factory.getOWLObjectProperty(object));
            case IRREFLEXIVE_OBJECT_PROPERTY:
              return factory.getOWLIrreflexiveObjectPropertyAxiom(factory.getOWLObjectProperty(subject));
            case OBJECT_PROPERTY_DOMAIN:
              return factory.getOWLObjectPropertyDomainAxiom(factory.getOWLObjectProperty(subject),
                  factory.getOWLClass(object));
            case OBJECT_PROPERTY_RANGE:
              return factory.getOWLObjectPropertyRangeAxiom(factory.getOWLObjectProperty(subject),
                  factory.getOWLClass(object));
            case REFLEXIVE_OBJECT_PROPERTY:
              return factory.getOWLReflexiveObjectPropertyAxiom(factory.getOWLObjectProperty(subject));
            case SAME_INDIVIDUALS:
              return factory.getOWLSameIndividualsAxiom(SetCreator.createSet(factory.getOWLIndividual(subject),
                  factory.getOWLIndividual(object)));
            case SUB_CLASS_OF:
              return factory.getOWLSubClassAxiom(factory.getOWLClass(subject), factory.getOWLClass(object));
            case SUB_DATA_PROPERTY_OF:
              return factory.getOWLSubDataPropertyAxiom(factory.getOWLDataProperty(subject),
                  factory.getOWLDataProperty(object));
            case SUB_OBJECT_PROPERTY_OF:
              return factory.getOWLSubObjectPropertyAxiom(factory.getOWLObjectProperty(subject),
                  factory.getOWLObjectProperty(subject));
            case SYMMETRIC_OBJECT_PROPERTY:
              return factory.getOWLSymmetricObjectPropertyAxiom(factory.getOWLObjectProperty(subject));
            case TRANSITIVE_OBJECT_PROPERTY:
              return factory.getOWLTransitiveObjectPropertyAxiom(factory.getOWLObjectProperty(subject));
            default:
              throw new Bug("Cannot create an axiom with OWL term " + predicate);
            }
          }
        }
        throw new Bug("Cannot create an axiom with unrecognised OWL term " + predicate);
      }
      else {
        if(object != null) {
          return factory.getOWLObjectPropertyAssertionAxiom(factory.getOWLIndividual(subject),
              factory.getOWLObjectProperty(predicate), factory.getOWLIndividual(object));
        }
        else if(objectType != null) {
          return factory.getOWLDataPropertyAssertionAxiom(factory.getOWLIndividual(subject),
              factory.getOWLDataProperty(predicate),
              factory.getOWLTypedConstant(objectStr, factory.getOWLDataType(objectType.getURI())));
        }
        else {
          return factory.getOWLDataPropertyAssertionAxiom(factory.getOWLIndividual(subject),
              factory.getOWLDataProperty(predicate), factory.getOWLUntypedConstant(objectStr));
        }
      }
    }
  }

}
