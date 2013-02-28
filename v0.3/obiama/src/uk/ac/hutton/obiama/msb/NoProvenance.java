/*
 * uk.ac.hutton.obiama.msb: NoProvenance.java
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

import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.action.Action;

/**
 * <!-- NoProvenance -->
 * 
 * Ghost class to allow a non-null provenance to be used.
 * 
 * @author Gary Polhill
 */
public class NoProvenance implements Provenance {

  /**
   * Constructor
   */
  public NoProvenance() {
    // Do nothing
  }

  /**
   * <!-- setProvenanceOntology -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#setProvenanceOntology(java.net.URI)
   * @param ontologyURI
   */
  public void setProvenanceOntology(URI ontologyURI) {
    // Do nothing
  }

  /**
   * <!-- setHistoryProvenanceOntology -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#setHistoryProvenanceOntology(java.net.URI)
   * @param ontologyURI
   */
  public void setHistoryProvenanceOntology(URI ontologyURI) {
    // Do nothing
  }

  /**
   * <!-- setRetractionAxioms -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#setRetractionAxioms(uk.ac.hutton.obiama.msb.Provenance.Axiom[])
   * @param axioms
   */
  public void setRetractionAxioms(Provenance.Axiom... axioms) {
    // Do nothing
  }

  /**
   * <!-- setAssertionAxioms -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#setAssertionAxioms(uk.ac.hutton.obiama.msb.Provenance.Axiom[])
   * @param axioms
   */
  public void setAssertionAxioms(Provenance.Axiom... axioms) {
    // Do nothing
  }

  /**
   * <!-- setObiamaAxioms -->
   *
   * @see uk.ac.hutton.obiama.msb.Provenance#setObiamaAxioms(uk.ac.hutton.obiama.msb.Provenance.Axiom[])
   * @param axioms
   */
  @Override
  public void setObiamaAxioms(uk.ac.hutton.obiama.msb.Provenance.Axiom... axioms) {
    // Do nothing    
  }

  /**
   * <!-- getAxiom -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#getAxiom(java.net.URI,
   *      java.net.URI, java.net.URI)
   * @param subject
   * @param predicate
   * @param object
   * @return An axiom
   */
  public Provenance.Axiom getAxiom(URI subject, URI predicate, URI object) {
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
   * @return An axiom
   */
  public Provenance.Axiom getDataAxiom(URI subject, URI predicate, String object) {
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
   * @param data
   * @return An axiom
   */
  public Provenance.Axiom getDataAxiom(URI subject, URI predicate, String object, XSDVocabulary data) {
    return new Axiom(subject, predicate, object, data);
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
    // Do nothing
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
    // Do nothing
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
    // Do nothing
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
    // Do nothing
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
  public void recordAssertion(Action action, URI inOntology, URI outOntology, String time, URI axiomURI) {
    // Do nothing
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
  public void recordRetraction(Action action, URI inOntology, URI outOntology, String time, URI axiomURI) {
    // Do nothing
  }

  /**
   * <!-- saveHistoryProvenanceOntology -->
   * 
   * @see uk.ac.hutton.obiama.msb.Provenance#saveHistoryProvenanceOntology(java.net.URI)
   * @param physicalURI
   */
  public void saveHistoryProvenanceOntology(URI physicalURI) {
    // Do nothing
  }

  /**
   * <!-- Axiom -->
   * 
   * This class is an implementation of the Axiom interface that does act as a
   * container for Axiom data.
   * 
   * @author Gary Polhill
   */
  public class Axiom implements Provenance.Axiom {
    /**
     * Subject of the axiom
     */
    URI subject;

    /**
     * Predicate of the axiom
     */
    URI predicate;

    /**
     * Object of the axiom (unless it's a data property assertion)
     */
    URI object;

    /**
     * Data object of the axiom (if it's a data property assertion)
     */
    String data;

    /**
     * Data type of the axiom (if it's a data property assertion). This may be
     * null.
     */
    XSDVocabulary type;

    /**
     * Internal constructor, just for the sake of consistency
     * 
     * @param subject
     * @param predicate
     */
    private Axiom(URI subject, URI predicate) {
      this.subject = subject;
      this.predicate = predicate;
      object = null;
      data = null;
      type = null;
    }

    /**
     * Constructor for non-data properties
     * 
     * @param subject
     * @param predicate
     * @param object
     */
    public Axiom(URI subject, URI predicate, URI object) {
      this(subject, predicate);
      this.object = object;
    }

    /**
     * Constructor for data properties with non-specified type
     * 
     * @param subject
     * @param predicate
     * @param data
     */
    public Axiom(URI subject, URI predicate, String data) {
      this(subject, predicate);
      this.data = data;
    }

    /**
     * Constructor for data properties with specified type
     * 
     * @param subject
     * @param predicate
     * @param data
     * @param type
     */
    public Axiom(URI subject, URI predicate, String data, XSDVocabulary type) {
      this(subject, predicate, data);
      this.type = type;
    }

    /**
     * <!-- getSubject -->
     * 
     * @see uk.ac.hutton.obiama.msb.Provenance.Axiom#getSubject()
     * @return The subject (or null)
     */
    public URI getSubject() {
      return subject;
    }

    /**
     * <!-- getPredicate -->
     * 
     * @see uk.ac.hutton.obiama.msb.Provenance.Axiom#getPredicate()
     * @return The predicate (or null)
     */
    public URI getPredicate() {
      return predicate;
    }

    /**
     * <!-- getObject -->
     * 
     * @see uk.ac.hutton.obiama.msb.Provenance.Axiom#getObject()
     * @return The object (or null)
     */
    public URI getObject() {
      return object;
    }

    /**
     * <!-- getObjectData -->
     * 
     * @see uk.ac.hutton.obiama.msb.Provenance.Axiom#getObjectData()
     * @return The object data (or null)
     */
    public String getObjectData() {
      return data;
    }

    /**
     * <!-- getObjectDataType -->
     * 
     * @see uk.ac.hutton.obiama.msb.Provenance.Axiom#getObjectDataType()
     * @return The object data type (or null)
     */
    public XSDVocabulary getObjectDataType() {
      return type;
    }

  }
}
