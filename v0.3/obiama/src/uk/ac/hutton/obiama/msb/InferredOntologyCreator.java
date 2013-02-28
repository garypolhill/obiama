/*
 * uk.ac.hutton.obiama.msb: InferredOntologyCreator.java 
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.util.InferredAxiomGenerator;
import org.semanticweb.owl.util.InferredAxiomGeneratorException;
import org.semanticweb.owl.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owl.util.InferredDataPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owl.util.InferredDisjointClassesAxiomGenerator;
import org.semanticweb.owl.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owl.util.InferredEquivalentDataPropertiesAxiomGenerator;
import org.semanticweb.owl.util.InferredEquivalentObjectPropertyAxiomGenerator;
import org.semanticweb.owl.util.InferredInverseObjectPropertiesAxiomGenerator;
import org.semanticweb.owl.util.InferredObjectPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owl.util.InferredOntologyGenerator;
import org.semanticweb.owl.util.InferredPropertyAssertionGenerator;
import org.semanticweb.owl.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owl.util.InferredSubDataPropertyAxiomGenerator;
import org.semanticweb.owl.util.InferredSubObjectPropertyAxiomGenerator;

import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.Panic;
import uk.ac.hutton.obiama.model.OWLSpecies;

/**
 * InferredOntologyCreator
 * 
 * <p>
 * Use the reasoner to generate a complete inferred ontology from a set of
 * ontologies. The class can be used in two ways. Statically, in which case, no
 * performance metrics or other information about the inference can be obtained,
 * and exceptions are handled; or by creating an instance, in which case,
 * reasoning time and inferred axioms can be accessed, and exceptions must be
 * handled by the caller. Reasoning itself requires various arguments, many of
 * which can be given defaults:
 * </p>
 * 
 * <ul>
 * <li><code>manager</code> (no default): A manager to handle all the ontologies
 * </li>
 * <li><code>inferred</code> (<code>DEFAULT_INFERRED_URI</code>): Ontology in
 * which to put all inferred axioms</li>
 * <li><code>reasoner</code> (reasoner class given on the command line):
 * Reasoner to use</li>
 * <li><code>ontologies</code> (set of ontologies managed by
 * <code>manager</code>): Ontologies from which to derive inferred axioms</li>
 * <li><code>spp</code> (OWL-DL): Species/sublanguage/profile of OWL to use for
 * inferred ontology</li>
 * <li><code>transferAxiom</code> (determined by other arguments): Whether to
 * copy all axioms from <code>ontologies</code> into <code>inferred</code>
 * before reasoning</li>
 * <li><code>realiseIndividuals</code> (<code>true</code>/determined by method
 * used): Whether to infer Abox axioms</li>
 * <li><code>classifyTaxonomy</code> (<code>true</code>): Whether to infer Tbox
 * axioms</li>
 * </ul>
 * 
 * <p>
 * Methods are provided for making the inferences using various combinations of
 * default and non-default values. The <code>inferred</code> ontology can be
 * given as a URI (which, effectively also happens if the ontology is not
 * given), in which case, it will be created in the <code>manager</code> and
 * filled with axioms from the <code>ontologies</code>. If given as an
 * OWLOntology, then this will not happen. The reasoner can be given as a class
 * name, class, or OWLReasoner object.
 * </p>
 * 
 * @author Gary Polhill
 * 
 */
public class InferredOntologyCreator {
  /**
   * Default URI to use to store the inferred ontology
   */
  public static final String DEFAULT_INFERRED_URI = "tmpInferred.owl";

  /**
   * Default OWL species/sublanguage/profile to use in the inferred ontology
   */
  public static final OWLSpecies DEFAULT_OWL_SPP = OWLSpecies.OWL_DL;

  /**
   * OWLOntologyManager to use for handling the ontologies
   */
  private OWLOntologyManager manager;

  /**
   * Ontology in which to put inferred axioms
   */
  private OWLOntology inferred;

  /**
   * Reasoner to use
   */
  private OWLReasoner reasoner;

  /**
   * The set of ontologies from which to infer axioms
   */
  private Set<OWLOntology> ontologies;

  /**
   * The set of ontologies found to be inconsistent
   */
  private Set<OWLOntology> inconsistentOntologies;

  /**
   * The set of axioms from the original ontology/ontologies
   */
  private Set<OWLAxiom> originalAxioms;

  /**
   * The set of axioms in the inferred ontology (which may intersect with the
   * set of original axioms)
   */
  private Set<OWLAxiom> inferredAxioms;

  /**
   * The set of classes found to be inconsistent
   */
  private Set<OWLClass> inconsistentClasses;

  /**
   * The species/sublanguage/profile to use for the inferred ontology
   */
  private OWLSpecies spp;

  /**
   * Set this to false to disable generating axioms inferred from individuals.
   * This might be necessary in larger models with too many triples for
   * tractable inference.
   */
  private boolean realiseIndividuals;

  /**
   * Set this to false to disable classifying the taxonomy.
   */
  private boolean classifyTaxonomy;

  /**
   * The time used to do the reasoning
   */
  private long elapsedTime;

  /**
   * Constructor for making inferences from an ontology. Calling a constructor
   * will cause all inferences to be made, the instance can then be used to
   * access more information about the reasoning process and inferences. Various
   * constructors are available, using default or non-default values for the
   * inference--this is the simplest, using all-default values.
   * 
   * @param manager
   * @throws OWLException
   */
  public InferredOntologyCreator(OWLOntologyManager manager) throws OWLException {
    this(manager, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, boolean realiseIndividuals, boolean classifyTaxonomy)
      throws OWLException {
    this(manager, getUnusedURI(manager), realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, Class<OWLReasoner> reasoner) throws OWLException {
    this(manager, reasoner, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, Class<OWLReasoner> reasoner, boolean realiseIndividuals,
      boolean classifyTaxonomy) throws OWLException {
    this(manager, getUnusedURI(manager), reasoner, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, Class<OWLReasoner> reasoner, OWLSpecies spp)
      throws OWLException {
    this(manager, reasoner, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, Class<OWLReasoner> reasoner, OWLSpecies spp,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, getUnusedURI(manager), reasoner, spp, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, Class<OWLReasoner> reasoner, Set<OWLOntology> ontologies)
      throws OWLException {
    this(manager, reasoner, ontologies, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, Class<OWLReasoner> reasoner, Set<OWLOntology> ontologies,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, getUnusedURI(manager), reasoner, ontologies, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, Class<OWLReasoner> reasoner, Set<OWLOntology> ontologies,
      OWLSpecies spp) throws OWLException {
    this(manager, reasoner, ontologies, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, Class<OWLReasoner> reasoner, Set<OWLOntology> ontologies,
      OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, getUnusedURI(manager), reasoner, ontologies, spp, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred) throws OWLException {
    this(manager, inferred, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, boolean realiseIndividuals,
      boolean classifyTaxonomy) throws OWLException {
    this(manager, inferred, ReasonerFactory.getReasonerOrDie(manager), manager.getOntologies(), DEFAULT_OWL_SPP, false,
        realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner)
      throws OWLException {
    this(manager, inferred, reasoner, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, inferred, reasoner, manager.getOntologies(), DEFAULT_OWL_SPP, false, realiseIndividuals,
        classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      OWLSpecies spp) throws OWLException {
    this(manager, inferred, reasoner, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, inferred, reasoner, manager.getOntologies(), spp, false, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies) throws OWLException {
    this(manager, inferred, reasoner, ontologies, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, inferred, reasoner, ontologies, DEFAULT_OWL_SPP, false, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) throws OWLException {
    this(manager, inferred, reasoner, ontologies, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy)
      throws OWLException {
    this(manager, inferred, reasoner, ontologies, spp, false, realiseIndividuals, classifyTaxonomy);
  }

  private InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean transferAxioms, boolean realiseIndividuals,
      boolean classifyTaxonomy) throws OWLException {
    this(manager, inferred, ReasonerFactory.getReasonerOrDie(manager, reasoner), ontologies, spp, transferAxioms,
        realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner)
      throws OWLException {
    this(manager, inferred, reasoner, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, inferred, reasoner, manager.getOntologies(), DEFAULT_OWL_SPP, false, realiseIndividuals,
        classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner, OWLSpecies spp)
      throws OWLException {
    this(manager, inferred, reasoner, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, inferred, reasoner, manager.getOntologies(), spp, false, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      Set<OWLOntology> ontologies) throws OWLException {
    this(manager, inferred, reasoner, ontologies, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, inferred, reasoner, ontologies, DEFAULT_OWL_SPP, false, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) throws OWLException {
    this(manager, inferred, reasoner, ontologies, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy)
      throws OWLException {
    this(manager, inferred, reasoner, ontologies, spp, false, realiseIndividuals, classifyTaxonomy);
  }

  /**
   * Main constructor method--all other constructors will end up calling this
   * one.
   * 
   * @param manager
   * @param inferred
   * @param reasoner
   * @param ontologies
   * @param spp
   * @param transferAxioms
   * @param realiseIndividuals
   * @param classifyTaxonomy
   * @throws OWLException
   */
  private InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean transferAxioms, boolean realiseIndividuals,
      boolean classifyTaxonomy) throws OWLException {
    this.manager = manager;
    this.inferred = inferred;
    this.reasoner = reasoner;
    this.ontologies = new HashSet<OWLOntology>(ontologies);
    if(transferAxioms && this.ontologies.contains(inferred)) this.ontologies.remove(inferred);
    if(transferAxioms) conflateOntologies(inferred, manager, ontologies);
    this.spp = spp;
    this.realiseIndividuals = realiseIndividuals;
    this.classifyTaxonomy = classifyTaxonomy;
    inferMeasured();
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, OWLSpecies spp) throws OWLException {
    this(manager, inferred, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, OWLSpecies spp,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, inferred, ReasonerFactory.getReasonerOrDie(manager), manager.getOntologies(), spp, false,
        realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, Set<OWLOntology> ontologies)
      throws OWLException {
    this(manager, inferred, ontologies, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, Set<OWLOntology> ontologies,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, inferred, ReasonerFactory.getReasonerOrDie(manager), ontologies, DEFAULT_OWL_SPP, false,
        realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, Set<OWLOntology> ontologies,
      OWLSpecies spp) throws OWLException {
    this(manager, inferred, ontologies, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, Set<OWLOntology> ontologies,
      OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, inferred, ReasonerFactory.getReasonerOrDie(manager), ontologies, spp, false, realiseIndividuals,
        classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, String reasoner) throws OWLException {
    this(manager, inferred, reasoner, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, String reasoner,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, inferred, reasoner, manager.getOntologies(), DEFAULT_OWL_SPP, false, realiseIndividuals,
        classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, String reasoner, OWLSpecies spp)
      throws OWLException {
    this(manager, inferred, reasoner, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, String reasoner, OWLSpecies spp,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, inferred, reasoner, manager.getOntologies(), spp, false, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, String reasoner,
      Set<OWLOntology> ontologies) throws OWLException {
    this(manager, inferred, reasoner, ontologies, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, String reasoner,
      Set<OWLOntology> ontologies, boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, inferred, reasoner, ontologies, DEFAULT_OWL_SPP, false, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, String reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) throws OWLException {
    this(manager, inferred, reasoner, ontologies, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, String reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy)
      throws OWLException {
    this(manager, inferred, reasoner, ontologies, spp, false, realiseIndividuals, classifyTaxonomy);
  }

  private InferredOntologyCreator(OWLOntologyManager manager, OWLOntology inferred, String reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean transferAxioms, boolean realiseIndividuals,
      boolean classifyTaxonomy) throws OWLException {
    this(manager, inferred, ReasonerFactory.getReasonerOrDie(manager, reasoner), ontologies, spp, transferAxioms,
        realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLReasoner reasoner) throws OWLException {
    this(manager, reasoner, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLReasoner reasoner, boolean realiseIndividuals,
      boolean classifyTaxonomy) throws OWLException {
    this(manager, getUnusedURI(manager), reasoner, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLReasoner reasoner, OWLSpecies spp) throws OWLException {
    this(manager, reasoner, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLReasoner reasoner, OWLSpecies spp,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, getUnusedURI(manager), reasoner, spp, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLReasoner reasoner, Set<OWLOntology> ontologies)
      throws OWLException {
    this(manager, reasoner, ontologies, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLReasoner reasoner, Set<OWLOntology> ontologies,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, getUnusedURI(manager), reasoner, ontologies, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLReasoner reasoner, Set<OWLOntology> ontologies,
      OWLSpecies spp) throws OWLException {
    this(manager, reasoner, ontologies, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLReasoner reasoner, Set<OWLOntology> ontologies,
      OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, getUnusedURI(manager), reasoner, ontologies, spp, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLSpecies spp) throws OWLException {
    this(manager, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, OWLSpecies spp, boolean realiseIndividuals,
      boolean classifyTaxonomy) throws OWLException {
    this(manager, getUnusedURI(manager), spp, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, Set<OWLOntology> ontologies) throws OWLException {
    this(manager, ontologies, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, Set<OWLOntology> ontologies, boolean realiseIndividuals,
      boolean classifyTaxonomy) throws OWLException {
    this(manager, getUnusedURI(manager), ontologies, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, Set<OWLOntology> ontologies, OWLSpecies spp)
      throws OWLException {
    this(manager, ontologies, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, Set<OWLOntology> ontologies, OWLSpecies spp,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, getUnusedURI(manager), ontologies, spp, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, String reasoner) throws OWLException {
    this(manager, reasoner, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, String reasoner, boolean realiseIndividuals,
      boolean classifyTaxonomy) throws OWLException {
    this(manager, getUnusedURI(manager), reasoner, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, String reasoner, OWLSpecies spp) throws OWLException {
    this(manager, reasoner, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, String reasoner, OWLSpecies spp,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, getUnusedURI(manager), reasoner, spp, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, String reasoner, Set<OWLOntology> ontologies)
      throws OWLException {
    this(manager, reasoner, ontologies, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, String reasoner, Set<OWLOntology> ontologies,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, getUnusedURI(manager), reasoner, ontologies, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, String reasoner, Set<OWLOntology> ontologies,
      OWLSpecies spp) throws OWLException {
    this(manager, reasoner, ontologies, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, String reasoner, Set<OWLOntology> ontologies,
      OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, getUnusedURI(manager), reasoner, ontologies, spp, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI) throws OWLException {
    this(manager, manager.createOntology(inferredURI), true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, boolean realiseIndividuals,
      boolean classifyTaxonomy) throws OWLException {
    this(manager, manager.createOntology(inferredURI), ReasonerFactory.getReasonerOrDie(manager), manager
        .getOntologies(), DEFAULT_OWL_SPP, true, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner)
      throws OWLException {
    this(manager, inferredURI, reasoner, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, manager.createOntology(inferredURI), reasoner, manager.getOntologies(), DEFAULT_OWL_SPP, true,
        realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      OWLSpecies spp) throws OWLException {
    this(manager, inferredURI, reasoner, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, manager.createOntology(inferredURI), reasoner, manager.getOntologies(), spp, true,
        realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies) throws OWLException {
    this(manager, inferredURI, reasoner, ontologies, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, manager.createOntology(inferredURI), reasoner, ontologies, DEFAULT_OWL_SPP, true, realiseIndividuals,
        classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) throws OWLException {
    this(manager, inferredURI, reasoner, ontologies, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy)
      throws OWLException {
    this(manager, manager.createOntology(inferredURI), reasoner, ontologies, spp, true, realiseIndividuals,
        classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner) throws OWLException {
    this(manager, inferredURI, reasoner, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, manager.createOntology(inferredURI), reasoner, manager.getOntologies(), DEFAULT_OWL_SPP, true,
        realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner, OWLSpecies spp)
      throws OWLException {
    this(manager, inferredURI, reasoner, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner, OWLSpecies spp,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, manager.createOntology(inferredURI), reasoner, manager.getOntologies(), spp, true,
        realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner,
      Set<OWLOntology> ontologies) throws OWLException {
    this(manager, inferredURI, reasoner, ontologies, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, manager.createOntology(inferredURI), reasoner, ontologies, DEFAULT_OWL_SPP, true, realiseIndividuals,
        classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) throws OWLException {
    this(manager, inferredURI, reasoner, ontologies, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy)
      throws OWLException {
    this(manager, manager.createOntology(inferredURI), reasoner, ontologies, spp, true, realiseIndividuals,
        classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, OWLSpecies spp) throws OWLException {
    this(manager, manager.createOntology(inferredURI), spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, OWLSpecies spp,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, manager.createOntology(inferredURI), ReasonerFactory.getReasonerOrDie(manager), manager
        .getOntologies(), spp, true, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, Set<OWLOntology> ontologies)
      throws OWLException {
    this(manager, manager.createOntology(inferredURI), ontologies, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, Set<OWLOntology> ontologies,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, manager.createOntology(inferredURI), ReasonerFactory.getReasonerOrDie(manager), ontologies,
        DEFAULT_OWL_SPP, true, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, Set<OWLOntology> ontologies,
      OWLSpecies spp) throws OWLException {
    this(manager, manager.createOntology(inferredURI), ontologies, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, Set<OWLOntology> ontologies,
      OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, manager.createOntology(inferredURI), ReasonerFactory.getReasonerOrDie(manager), ontologies, spp,
        true, realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, String reasoner) throws OWLException {
    this(manager, inferredURI, reasoner, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, String reasoner,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, manager.createOntology(inferredURI), reasoner, manager.getOntologies(), DEFAULT_OWL_SPP, true,
        realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, String reasoner, OWLSpecies spp)
      throws OWLException {
    this(manager, inferredURI, reasoner, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, String reasoner, OWLSpecies spp,
      boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, manager.createOntology(inferredURI), reasoner, manager.getOntologies(), spp, true,
        realiseIndividuals, classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, String reasoner,
      Set<OWLOntology> ontologies) throws OWLException {
    this(manager, inferredURI, reasoner, ontologies, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, String reasoner,
      Set<OWLOntology> ontologies, boolean realiseIndividuals, boolean classifyTaxonomy) throws OWLException {
    this(manager, manager.createOntology(inferredURI), reasoner, ontologies, DEFAULT_OWL_SPP, true, realiseIndividuals,
        classifyTaxonomy);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, String reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) throws OWLException {
    this(manager, inferredURI, reasoner, ontologies, spp, true, true);
  }

  public InferredOntologyCreator(OWLOntologyManager manager, URI inferredURI, String reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy)
      throws OWLException {
    this(manager, manager.createOntology(inferredURI), reasoner, ontologies, spp, true, realiseIndividuals,
        classifyTaxonomy);
  }

  /**
   * <!-- conflateOntologies -->
   * 
   * Copy axioms from the set of <code>ontologies</code> into the
   * <code>conflated</code> ontology.
   * 
   * @param conflated the ontology to contain all the axioms in the
   *          <code>ontologies</code>
   * @param manager the OWLOntologyManager for the <code>conflated</code>
   *          ontology
   * @param ontologies the set of ontologies to copy axioms from
   * @throws OWLOntologyChangeException
   */
  public static void conflateOntologies(OWLOntology conflated, OWLOntologyManager manager, Set<OWLOntology> ontologies)
      throws OWLOntologyChangeException {
    List<AddAxiom> ontologyChanges = new LinkedList<AddAxiom>();
    for(OWLOntology ontology: ontologies) {
      for(OWLAxiom axiom: ontology.getAxioms()) {
        ontologyChanges.add(new AddAxiom(conflated, axiom));
      }
    }
    manager.applyChanges(ontologyChanges);
  }

  /**
   * <!-- getUnusedURI -->
   * 
   * Find a URI based on the DEFAULT_INFERRED_URI that will not clash with
   * existing ontology URIs in the <code>manager</code>
   * 
   * @param manager the ontology manager with ontologies having URIs that must
   *          not clash with the returned URI
   * @return the URI
   */
  private static URI getUnusedURI(OWLOntologyManager manager) {
    Set<URI> ontoURIs = new HashSet<URI>();
    for(OWLOntology ontology: manager.getOntologies()) {
      ontoURIs.add(ontology.getURI());
    }
    URI newURI = URI.create(DEFAULT_INFERRED_URI);
    int i = 0;
    while(ontoURIs.contains(newURI)) {
      i++;
      newURI = URI.create(DEFAULT_INFERRED_URI + "-" + i);
      int lastDot = DEFAULT_INFERRED_URI.lastIndexOf('.');
      if(lastDot > 0) {
        String notSuffix = DEFAULT_INFERRED_URI.substring(0, lastDot);
        String suffix = DEFAULT_INFERRED_URI.substring(lastDot);
        if(suffix.indexOf("/") == -1) newURI = URI.create(notSuffix + "-" + i + suffix);
      }
    }
    return newURI;
  }

  /**
   * <!-- infer -->
   * 
   * Methods for statically building an inferred ontology including both A-box
   * and T-box inferences. Various forms of this method are provided--this is
   * the simplest.
   * 
   * @param manager
   */
  public static void infer(OWLOntologyManager manager) {
    infer(manager, true, true);
  }

  private static void infer(OWLOntologyManager manager, boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, getUnusedURI(manager), realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, Class<OWLReasoner> reasoner) {
    infer(manager, reasoner, true, true);
  }

  private static void infer(OWLOntologyManager manager, Class<OWLReasoner> reasoner, boolean realiseIndividuals,
      boolean classifyTaxonomy) {
    infer(manager, getUnusedURI(manager), reasoner, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, Class<OWLReasoner> reasoner, OWLSpecies spp) {
    infer(manager, reasoner, spp, true, true);
  }

  private static void infer(OWLOntologyManager manager, Class<OWLReasoner> reasoner, OWLSpecies spp,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, getUnusedURI(manager), reasoner, spp, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, Class<OWLReasoner> reasoner, Set<OWLOntology> ontologies) {
    infer(manager, reasoner, ontologies, true, true);
  }

  private static void infer(OWLOntologyManager manager, Class<OWLReasoner> reasoner, Set<OWLOntology> ontologies,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, getUnusedURI(manager), reasoner, ontologies, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, Class<OWLReasoner> reasoner, Set<OWLOntology> ontologies,
      OWLSpecies spp) {
    infer(manager, reasoner, ontologies, spp, true, true);
  }

  private static void infer(OWLOntologyManager manager, Class<OWLReasoner> reasoner, Set<OWLOntology> ontologies,
      OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, getUnusedURI(manager), reasoner, ontologies, spp, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLOntology inferred) {
    infer(manager, inferred, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLOntology inferred, boolean realiseIndividuals,
      boolean classifyTaxonomy) {
    infer(manager, inferred, ReasonerFactory.getReasonerOrDie(manager), manager.getOntologies(), DEFAULT_OWL_SPP,
        false, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner) {
    infer(manager, inferred, reasoner, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, inferred, reasoner, manager.getOntologies(), DEFAULT_OWL_SPP, false, realiseIndividuals,
        classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner, OWLSpecies spp) {
    infer(manager, inferred, reasoner, spp, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, inferred, reasoner, manager.getOntologies(), spp, false, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies) {
    infer(manager, inferred, reasoner, ontologies, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, inferred, reasoner, ontologies, DEFAULT_OWL_SPP, false, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    infer(manager, inferred, reasoner, ontologies, spp, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, inferred, reasoner, ontologies, spp, false, realiseIndividuals, classifyTaxonomy);
  }

  private static void infer(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean transferAxioms, boolean realiseIndividuals,
      boolean classifyTaxonomy) {
    infer(manager, inferred, ReasonerFactory.getReasonerOrDie(manager, reasoner), ontologies, spp, transferAxioms,
        realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner) {
    infer(manager, inferred, reasoner, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, inferred, reasoner, manager.getOntologies(), DEFAULT_OWL_SPP, false, realiseIndividuals,
        classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner, OWLSpecies spp) {
    infer(manager, inferred, reasoner, spp, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner, OWLSpecies spp,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, inferred, reasoner, manager.getOntologies(), spp, false, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      Set<OWLOntology> ontologies) {
    infer(manager, inferred, reasoner, ontologies, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, inferred, reasoner, ontologies, DEFAULT_OWL_SPP, false, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    infer(manager, inferred, reasoner, ontologies, spp, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, inferred, reasoner, ontologies, spp, false, realiseIndividuals, classifyTaxonomy);
  }

  /**
   * <!-- infer -->
   * 
   * Main static method for obtaining an inferred ontology. All other static
   * methods will end up calling this one.
   * 
   * @param manager
   * @param inferred
   * @param reasoner
   * @param ontologies
   * @param spp
   * @param transferAxioms
   * @param realiseIndividuals
   * @param classifyTaxonomy
   */
  private static void infer(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean transferAxioms, boolean realiseIndividuals,
      boolean classifyTaxonomy) {
    Set<OWLOntology> myOntologies = new HashSet<OWLOntology>(ontologies);
    if(transferAxioms && myOntologies.contains(inferred)) myOntologies.remove(inferred);
    StringBuffer buf = new StringBuffer();
    for(OWLOntology ontology: myOntologies) {
      if(buf.length() > 0) buf.append(", ");
      buf.append(ontology.getURI());
    }
    try {
      if(transferAxioms) conflateOntologies(inferred, manager, myOntologies);
      inferWithExceptions(manager, inferred, reasoner, myOntologies, spp, realiseIndividuals, classifyTaxonomy);
    }
    catch(OWLException e) {
      ErrorHandler.fatal(e, "inferring from ontologies " + buf + " using reasoner class "
        + reasoner.getClass().getName());
      throw new Panic();
    }
  }

  public static void infer(OWLOntologyManager manager, OWLOntology inferred, OWLSpecies spp) {
    infer(manager, inferred, spp, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLOntology inferred, OWLSpecies spp,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, inferred, ReasonerFactory.getReasonerOrDie(manager), manager.getOntologies(), spp, false,
        realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLOntology inferred, Set<OWLOntology> ontologies) {
    infer(manager, inferred, ontologies, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLOntology inferred, Set<OWLOntology> ontologies,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, inferred, ReasonerFactory.getReasonerOrDie(manager), ontologies, DEFAULT_OWL_SPP, false,
        realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLOntology inferred, Set<OWLOntology> ontologies, OWLSpecies spp) {
    infer(manager, inferred, ontologies, spp, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLOntology inferred, Set<OWLOntology> ontologies,
      OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, inferred, ReasonerFactory.getReasonerOrDie(manager), ontologies, spp, false, realiseIndividuals,
        classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLOntology inferred, String reasoner) {
    infer(manager, inferred, reasoner, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLOntology inferred, String reasoner,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, inferred, reasoner, manager.getOntologies(), DEFAULT_OWL_SPP, false, realiseIndividuals,
        classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLOntology inferred, String reasoner, OWLSpecies spp) {
    infer(manager, inferred, reasoner, spp, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLOntology inferred, String reasoner, OWLSpecies spp,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, inferred, reasoner, manager.getOntologies(), spp, false, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLOntology inferred, String reasoner,
      Set<OWLOntology> ontologies) {
    infer(manager, inferred, reasoner, ontologies, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLOntology inferred, String reasoner,
      Set<OWLOntology> ontologies, boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, inferred, reasoner, ontologies, DEFAULT_OWL_SPP, false, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLOntology inferred, String reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    infer(manager, inferred, reasoner, ontologies, spp, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLOntology inferred, String reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, inferred, reasoner, ontologies, spp, false, realiseIndividuals, classifyTaxonomy);
  }

  private static void infer(OWLOntologyManager manager, OWLOntology inferred, String reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean transferAxioms, boolean realiseIndividuals,
      boolean classifyTaxonomy) {
    infer(manager, inferred, ReasonerFactory.getReasonerOrDie(manager, reasoner), ontologies, spp, transferAxioms,
        realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLReasoner reasoner) {
    infer(manager, reasoner, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLReasoner reasoner, boolean realiseIndividuals,
      boolean classifyTaxonomy) {
    infer(manager, getUnusedURI(manager), reasoner, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLReasoner reasoner, OWLSpecies spp) {
    infer(manager, reasoner, spp, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLReasoner reasoner, OWLSpecies spp,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, getUnusedURI(manager), reasoner, spp, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLReasoner reasoner, Set<OWLOntology> ontologies) {
    infer(manager, reasoner, ontologies, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLReasoner reasoner, Set<OWLOntology> ontologies,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, getUnusedURI(manager), reasoner, ontologies, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLReasoner reasoner, Set<OWLOntology> ontologies, OWLSpecies spp) {
    infer(manager, reasoner, ontologies, spp, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLReasoner reasoner, Set<OWLOntology> ontologies,
      OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, getUnusedURI(manager), reasoner, ontologies, spp, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, OWLSpecies spp) {
    infer(manager, spp, true, true);
  }

  private static void infer(OWLOntologyManager manager, OWLSpecies spp, boolean realiseIndividuals,
      boolean classifyTaxonomy) {
    infer(manager, getUnusedURI(manager), spp, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, Set<OWLOntology> ontologies) {
    infer(manager, ontologies, true, true);
  }

  private static void infer(OWLOntologyManager manager, Set<OWLOntology> ontologies, boolean realiseIndividuals,
      boolean classifyTaxonomy) {
    infer(manager, getUnusedURI(manager), ontologies, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, Set<OWLOntology> ontologies, OWLSpecies spp) {
    infer(manager, ontologies, spp, true, true);
  }

  private static void infer(OWLOntologyManager manager, Set<OWLOntology> ontologies, OWLSpecies spp,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, getUnusedURI(manager), ontologies, spp, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, String reasoner) {
    infer(manager, reasoner, true, true);
  }

  private static void infer(OWLOntologyManager manager, String reasoner, boolean realiseIndividuals,
      boolean classifyTaxonomy) {
    infer(manager, getUnusedURI(manager), reasoner, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, String reasoner, OWLSpecies spp) {
    infer(manager, reasoner, spp, true, true);
  }

  private static void infer(OWLOntologyManager manager, String reasoner, OWLSpecies spp, boolean realiseIndividuals,
      boolean classifyTaxonomy) {
    infer(manager, getUnusedURI(manager), reasoner, spp, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, String reasoner, Set<OWLOntology> ontologies) {
    infer(manager, reasoner, ontologies, true, true);
  }

  private static void infer(OWLOntologyManager manager, String reasoner, Set<OWLOntology> ontologies,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, getUnusedURI(manager), reasoner, ontologies, realiseIndividuals, classifyTaxonomy);
  }

  public static void infer(OWLOntologyManager manager, String reasoner, Set<OWLOntology> ontologies, OWLSpecies spp) {
    infer(manager, reasoner, ontologies, spp, true, true);
  }

  private static void infer(OWLOntologyManager manager, String reasoner, Set<OWLOntology> ontologies, OWLSpecies spp,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    infer(manager, getUnusedURI(manager), reasoner, ontologies, spp, realiseIndividuals, classifyTaxonomy);
  }

  public static OWLOntology infer(OWLOntologyManager manager, URI inferredURI) {
    return infer(manager, inferredURI, true, true);
  }

  private static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, boolean realiseIndividuals,
      boolean classifyTaxonomy) {
    return infer(manager, inferredURI, ReasonerFactory.getReasonerOrDie(manager), manager.getOntologies(),
        DEFAULT_OWL_SPP, true, realiseIndividuals, classifyTaxonomy);
  }

  public static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner) {
    return infer(manager, inferredURI, reasoner, true, true);
  }

  private static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    return infer(manager, inferredURI, reasoner, manager.getOntologies(), DEFAULT_OWL_SPP, true, realiseIndividuals,
        classifyTaxonomy);
  }

  public static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      OWLSpecies spp) {
    return infer(manager, inferredURI, reasoner, spp, true, true);
  }

  private static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) {
    return infer(manager, inferredURI, reasoner, manager.getOntologies(), spp, true, realiseIndividuals,
        classifyTaxonomy);
  }

  public static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies) {
    return infer(manager, inferredURI, reasoner, ontologies, true, true);
  }

  private static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, boolean realiseIndividuals, boolean classifyTaxonomy) {
    return infer(manager, inferredURI, reasoner, ontologies, DEFAULT_OWL_SPP, true, realiseIndividuals,
        classifyTaxonomy);
  }

  public static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    return infer(manager, inferredURI, reasoner, ontologies, spp, true, true);
  }

  private static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) {
    return infer(manager, inferredURI, reasoner, ontologies, spp, true, realiseIndividuals, classifyTaxonomy);
  }

  private static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean transferAxioms, boolean realiseIndividuals,
      boolean classifyTaxonomy) {
    return infer(manager, inferredURI, ReasonerFactory.getReasonerOrDie(manager, reasoner), ontologies, spp,
        transferAxioms, realiseIndividuals, classifyTaxonomy);
  }

  public static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner) {
    return infer(manager, inferredURI, reasoner, true, true);
  }

  private static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    return infer(manager, inferredURI, reasoner, manager.getOntologies(), DEFAULT_OWL_SPP, true, realiseIndividuals,
        classifyTaxonomy);
  }

  public static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner, OWLSpecies spp) {
    return infer(manager, inferredURI, reasoner, spp, true, true);
  }

  private static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner, OWLSpecies spp,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    return infer(manager, inferredURI, reasoner, manager.getOntologies(), spp, true, realiseIndividuals,
        classifyTaxonomy);
  }

  public static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner,
      Set<OWLOntology> ontologies) {
    return infer(manager, inferredURI, reasoner, ontologies, true, true);
  }

  private static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, boolean realiseIndividuals, boolean classifyTaxonomy) {
    return infer(manager, inferredURI, reasoner, ontologies, DEFAULT_OWL_SPP, true, realiseIndividuals,
        classifyTaxonomy);
  }

  public static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    return infer(manager, inferredURI, reasoner, ontologies, spp, true, true);
  }

  private static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) {
    return infer(manager, inferredURI, reasoner, ontologies, spp, true, realiseIndividuals, classifyTaxonomy);
  }

  private static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean transferAxioms, boolean realiseIndividuals,
      boolean classifyTaxonomy) {
    try {
      OWLOntology inferredOntology = manager.createOntology(inferredURI);
      infer(manager, inferredOntology, reasoner, ontologies, spp, transferAxioms, realiseIndividuals, classifyTaxonomy);
      return inferredOntology;
    }
    catch(OWLOntologyCreationException e) {
      ErrorHandler.fatal(e, "creating inferred ontology " + inferredURI + " for reasoning using "
        + reasoner.getClass().getName());
      throw new Panic();
    }
  }

  public static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, OWLSpecies spp) {
    return infer(manager, inferredURI, spp, true, true);
  }

  private static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, OWLSpecies spp,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    return infer(manager, inferredURI, ReasonerFactory.getReasonerOrDie(manager), manager.getOntologies(), spp, true,
        realiseIndividuals, classifyTaxonomy);
  }

  public static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, Set<OWLOntology> ontologies) {
    return infer(manager, inferredURI, ontologies, true, true);
  }

  private static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, Set<OWLOntology> ontologies,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    return infer(manager, inferredURI, ReasonerFactory.getReasonerOrDie(manager), ontologies, DEFAULT_OWL_SPP, true,
        realiseIndividuals, classifyTaxonomy);
  }

  public static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, Set<OWLOntology> ontologies,
      OWLSpecies spp) {
    return infer(manager, inferredURI, ontologies, spp, true, true);
  }

  private static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, Set<OWLOntology> ontologies,
      OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) {
    return infer(manager, inferredURI, ReasonerFactory.getReasonerOrDie(manager), ontologies, spp, true,
        realiseIndividuals, classifyTaxonomy);
  }

  public static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, String reasoner) {
    return infer(manager, inferredURI, reasoner, true, true);
  }

  private static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, String reasoner,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    return infer(manager, inferredURI, reasoner, manager.getOntologies(), DEFAULT_OWL_SPP, true, realiseIndividuals,
        classifyTaxonomy);
  }

  public static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, String reasoner, OWLSpecies spp) {
    return infer(manager, inferredURI, reasoner, spp, true, true);
  }

  private static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, String reasoner, OWLSpecies spp,
      boolean realiseIndividuals, boolean classifyTaxonomy) {
    return infer(manager, inferredURI, reasoner, manager.getOntologies(), spp, true, realiseIndividuals,
        classifyTaxonomy);
  }

  public static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, String reasoner,
      Set<OWLOntology> ontologies) {
    return infer(manager, inferredURI, reasoner, ontologies, true, true);
  }

  private static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, String reasoner,
      Set<OWLOntology> ontologies, boolean realiseIndividuals, boolean classifyTaxonomy) {
    return infer(manager, inferredURI, reasoner, ontologies, DEFAULT_OWL_SPP, true, realiseIndividuals,
        classifyTaxonomy);
  }

  public static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, String reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    return infer(manager, inferredURI, reasoner, ontologies, spp, true, true);
  }

  private static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, String reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy) {
    return infer(manager, inferredURI, reasoner, ontologies, spp, true, realiseIndividuals, classifyTaxonomy);
  }

  private static OWLOntology infer(OWLOntologyManager manager, URI inferredURI, String reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean transferAxioms, boolean realiseIndividuals,
      boolean classifyTaxonomy) {
    return infer(manager, inferredURI, ReasonerFactory.getReasonerOrDie(manager, reasoner), ontologies, spp,
        transferAxioms, realiseIndividuals, classifyTaxonomy);
  }

  /**
   * <!-- inferIndividuals -->
   * 
   * Static methods for making A-box inferences only. Various forms are
   * provided--see comments on class. This is the simplest, using all default
   * values.
   * 
   * @param manager
   */
  public static void inferIndividuals(OWLOntologyManager manager) {
    infer(manager, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, Class<OWLReasoner> reasoner) {
    infer(manager, reasoner, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, Class<OWLReasoner> reasoner, OWLSpecies spp) {
    infer(manager, reasoner, spp, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies) {
    infer(manager, reasoner, ontologies, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    infer(manager, reasoner, ontologies, spp, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLOntology inferred) {
    infer(manager, inferred, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner) {
    infer(manager, inferred, reasoner, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      OWLSpecies spp) {
    infer(manager, inferred, reasoner, spp, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies) {
    infer(manager, inferred, reasoner, ontologies, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    infer(manager, inferred, reasoner, ontologies, spp, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner) {
    infer(manager, inferred, reasoner, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      OWLSpecies spp) {
    infer(manager, inferred, reasoner, spp, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      Set<OWLOntology> ontologies) {
    infer(manager, inferred, reasoner, ontologies, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    infer(manager, inferred, reasoner, ontologies, spp, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLOntology inferred, OWLSpecies spp) {
    infer(manager, inferred, spp, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLOntology inferred, Set<OWLOntology> ontologies) {
    infer(manager, inferred, ontologies, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLOntology inferred, Set<OWLOntology> ontologies,
      OWLSpecies spp) {
    infer(manager, inferred, ontologies, spp, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLOntology inferred, String reasoner) {
    infer(manager, inferred, reasoner, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLOntology inferred, String reasoner, OWLSpecies spp) {
    infer(manager, inferred, reasoner, spp, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLOntology inferred, String reasoner,
      Set<OWLOntology> ontologies) {
    infer(manager, inferred, reasoner, ontologies, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLOntology inferred, String reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    infer(manager, inferred, reasoner, ontologies, spp, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLReasoner reasoner) {
    infer(manager, reasoner, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLReasoner reasoner, OWLSpecies spp) {
    infer(manager, reasoner, spp, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLReasoner reasoner, Set<OWLOntology> ontologies) {
    infer(manager, reasoner, ontologies, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLReasoner reasoner, Set<OWLOntology> ontologies,
      OWLSpecies spp) {
    infer(manager, reasoner, ontologies, spp, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, OWLSpecies spp) {
    infer(manager, spp, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, Set<OWLOntology> ontologies) {
    infer(manager, ontologies, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, Set<OWLOntology> ontologies, OWLSpecies spp) {
    infer(manager, ontologies, spp, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, String reasoner) {
    infer(manager, reasoner, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, String reasoner, OWLSpecies spp) {
    infer(manager, reasoner, spp, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, String reasoner, Set<OWLOntology> ontologies) {
    infer(manager, reasoner, ontologies, true, false);
  }

  public static void inferIndividuals(OWLOntologyManager manager, String reasoner, Set<OWLOntology> ontologies,
      OWLSpecies spp) {
    infer(manager, reasoner, ontologies, spp, true, false);
  }

  public static OWLOntology inferIndividuals(OWLOntologyManager manager, URI inferredURI) {
    return infer(manager, inferredURI, true, false);
  }

  public static OWLOntology inferIndividuals(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner) {
    return infer(manager, inferredURI, reasoner, true, false);
  }

  public static OWLOntology inferIndividuals(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      OWLSpecies spp) {
    return infer(manager, inferredURI, reasoner, spp, true, false);
  }

  public static OWLOntology inferIndividuals(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies) {
    return infer(manager, inferredURI, reasoner, ontologies, true, false);
  }

  public static OWLOntology inferIndividuals(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    return infer(manager, inferredURI, reasoner, ontologies, spp, true, false);
  }

  public static OWLOntology inferIndividuals(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner) {
    return infer(manager, inferredURI, reasoner, true, false);
  }

  public static OWLOntology inferIndividuals(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner,
      OWLSpecies spp) {
    return infer(manager, inferredURI, reasoner, spp, true, false);
  }

  public static OWLOntology inferIndividuals(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner,
      Set<OWLOntology> ontologies) {
    return infer(manager, inferredURI, reasoner, ontologies, true, false);
  }

  public static OWLOntology inferIndividuals(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    return infer(manager, inferredURI, reasoner, ontologies, spp, true, false);
  }

  public static OWLOntology inferIndividuals(OWLOntologyManager manager, URI inferredURI, OWLSpecies spp) {
    return infer(manager, inferredURI, spp, true, false);
  }

  public static OWLOntology inferIndividuals(OWLOntologyManager manager, URI inferredURI, Set<OWLOntology> ontologies) {
    return infer(manager, inferredURI, ontologies, true, false);
  }

  public static OWLOntology inferIndividuals(OWLOntologyManager manager, URI inferredURI, Set<OWLOntology> ontologies,
      OWLSpecies spp) {
    return infer(manager, inferredURI, ontologies, spp, true, false);
  }

  public static OWLOntology inferIndividuals(OWLOntologyManager manager, URI inferredURI, String reasoner) {
    return infer(manager, inferredURI, reasoner, true, false);
  }

  public static OWLOntology inferIndividuals(OWLOntologyManager manager, URI inferredURI, String reasoner,
      OWLSpecies spp) {
    return infer(manager, inferredURI, reasoner, spp, true, false);
  }

  public static OWLOntology inferIndividuals(OWLOntologyManager manager, URI inferredURI, String reasoner,
      Set<OWLOntology> ontologies) {
    return infer(manager, inferredURI, reasoner, ontologies, true, false);
  }

  public static OWLOntology inferIndividuals(OWLOntologyManager manager, URI inferredURI, String reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    return infer(manager, inferredURI, reasoner, ontologies, spp, true, false);
  }

  /**
   * <!-- inferTaxonomy -->
   * 
   * Static methods for making T-box inferences. Various forms of the method are
   * provided. This is the simplest, using all default values for the
   * inferences.
   * 
   * @param manager
   */
  public static void inferTaxonomy(OWLOntologyManager manager) {
    infer(manager, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, Class<OWLReasoner> reasoner) {
    infer(manager, reasoner, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, Class<OWLReasoner> reasoner, OWLSpecies spp) {
    infer(manager, reasoner, spp, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, Class<OWLReasoner> reasoner, Set<OWLOntology> ontologies) {
    infer(manager, reasoner, ontologies, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    infer(manager, reasoner, ontologies, spp, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLOntology inferred) {
    infer(manager, inferred, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner) {
    infer(manager, inferred, reasoner, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      OWLSpecies spp) {
    infer(manager, inferred, reasoner, spp, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies) {
    infer(manager, inferred, reasoner, ontologies, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLOntology inferred, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    infer(manager, inferred, reasoner, ontologies, spp, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner) {
    infer(manager, inferred, reasoner, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      OWLSpecies spp) {
    infer(manager, inferred, reasoner, spp, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      Set<OWLOntology> ontologies) {
    infer(manager, inferred, reasoner, ontologies, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    infer(manager, inferred, reasoner, ontologies, spp, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLOntology inferred, OWLSpecies spp) {
    infer(manager, inferred, spp, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLOntology inferred, Set<OWLOntology> ontologies) {
    infer(manager, inferred, ontologies, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLOntology inferred, Set<OWLOntology> ontologies,
      OWLSpecies spp) {
    infer(manager, inferred, ontologies, spp, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLOntology inferred, String reasoner) {
    infer(manager, inferred, reasoner, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLOntology inferred, String reasoner, OWLSpecies spp) {
    infer(manager, inferred, reasoner, spp, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLOntology inferred, String reasoner,
      Set<OWLOntology> ontologies) {
    infer(manager, inferred, reasoner, ontologies, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLOntology inferred, String reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    infer(manager, inferred, reasoner, ontologies, spp, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLReasoner reasoner) {
    infer(manager, reasoner, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLReasoner reasoner, OWLSpecies spp) {
    infer(manager, reasoner, spp, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLReasoner reasoner, Set<OWLOntology> ontologies) {
    infer(manager, reasoner, ontologies, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLReasoner reasoner, Set<OWLOntology> ontologies,
      OWLSpecies spp) {
    infer(manager, reasoner, ontologies, spp, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, OWLSpecies spp) {
    infer(manager, spp, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, Set<OWLOntology> ontologies) {
    infer(manager, ontologies, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, Set<OWLOntology> ontologies, OWLSpecies spp) {
    infer(manager, ontologies, spp, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, String reasoner) {
    infer(manager, reasoner, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, String reasoner, OWLSpecies spp) {
    infer(manager, reasoner, spp, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, String reasoner, Set<OWLOntology> ontologies) {
    infer(manager, reasoner, ontologies, false, true);
  }

  public static void inferTaxonomy(OWLOntologyManager manager, String reasoner, Set<OWLOntology> ontologies,
      OWLSpecies spp) {
    infer(manager, reasoner, ontologies, spp, false, true);
  }

  public static OWLOntology inferTaxonomy(OWLOntologyManager manager, URI inferredURI) {
    return infer(manager, inferredURI, false, true);
  }

  public static OWLOntology inferTaxonomy(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner) {
    return infer(manager, inferredURI, reasoner, false, true);
  }

  public static OWLOntology inferTaxonomy(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      OWLSpecies spp) {
    return infer(manager, inferredURI, reasoner, spp, false, true);
  }

  public static OWLOntology inferTaxonomy(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies) {
    return infer(manager, inferredURI, reasoner, ontologies, false, true);
  }

  public static OWLOntology inferTaxonomy(OWLOntologyManager manager, URI inferredURI, Class<OWLReasoner> reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    return infer(manager, inferredURI, reasoner, ontologies, spp, false, true);
  }

  public static OWLOntology inferTaxonomy(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner) {
    return infer(manager, inferredURI, reasoner, false, true);
  }

  public static OWLOntology inferTaxonomy(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner,
      OWLSpecies spp) {
    return infer(manager, inferredURI, reasoner, spp, false, true);
  }

  public static OWLOntology inferTaxonomy(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner,
      Set<OWLOntology> ontologies) {
    return infer(manager, inferredURI, reasoner, ontologies, false, true);
  }

  public static OWLOntology inferTaxonomy(OWLOntologyManager manager, URI inferredURI, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    return infer(manager, inferredURI, reasoner, ontologies, spp, false, true);
  }

  public static OWLOntology inferTaxonomy(OWLOntologyManager manager, URI inferredURI, OWLSpecies spp) {
    return infer(manager, inferredURI, spp, false, true);
  }

  public static OWLOntology inferTaxonomy(OWLOntologyManager manager, URI inferredURI, Set<OWLOntology> ontologies) {
    return infer(manager, inferredURI, ontologies, false, true);
  }

  public static OWLOntology inferTaxonomy(OWLOntologyManager manager, URI inferredURI, Set<OWLOntology> ontologies,
      OWLSpecies spp) {
    return infer(manager, inferredURI, ontologies, spp, false, true);
  }

  public static OWLOntology inferTaxonomy(OWLOntologyManager manager, URI inferredURI, String reasoner) {
    return infer(manager, inferredURI, reasoner, false, true);
  }

  public static OWLOntology inferTaxonomy(OWLOntologyManager manager, URI inferredURI, String reasoner, OWLSpecies spp) {
    return infer(manager, inferredURI, reasoner, spp, false, true);
  }

  public static OWLOntology inferTaxonomy(OWLOntologyManager manager, URI inferredURI, String reasoner,
      Set<OWLOntology> ontologies) {
    return infer(manager, inferredURI, reasoner, ontologies, false, true);
  }

  public static OWLOntology inferTaxonomy(OWLOntologyManager manager, URI inferredURI, String reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp) {
    return infer(manager, inferredURI, reasoner, ontologies, spp, false, true);
  }

  /**
   * <!-- inferWithExceptions -->
   * 
   * Generate all inferred axioms from the asserted ontology. This method is
   * based on Matthew Horridge's Example11.java (10-Dec-2007) for the OWL API.
   * This method constitutes the core functionality of this class.
   * 
   * @param manager
   * @param inferred
   * @param reasoner
   * @param ontologies
   * @param spp
   * @param realiseIndividuals
   * @param classifyTaxonomy
   * @throws OWLReasonerException
   * @throws InferredAxiomGeneratorException
   * @throws OWLOntologyChangeException
   */
  private static void inferWithExceptions(OWLOntologyManager manager, OWLOntology inferred, OWLReasoner reasoner,
      Set<OWLOntology> ontologies, OWLSpecies spp, boolean realiseIndividuals, boolean classifyTaxonomy)
      throws OWLException {
    reasoner.clearOntologies();
    reasoner.loadOntologies(ontologies);

    if(classifyTaxonomy) {
      reasoner.classify();
    }

    if(realiseIndividuals) {
      reasoner.realise();
    }

    List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();

    if(!(classifyTaxonomy || realiseIndividuals)) return;

    if(classifyTaxonomy) {
      gens.add(new InferredDataPropertyCharacteristicAxiomGenerator());
      gens.add(new InferredDisjointClassesAxiomGenerator());
      gens.add(new InferredEquivalentClassAxiomGenerator());
      gens.add(new InferredEquivalentDataPropertiesAxiomGenerator());
      gens.add(new InferredEquivalentObjectPropertyAxiomGenerator());
      gens.add(new InferredInverseObjectPropertiesAxiomGenerator());
      gens.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
      gens.add(new InferredSubClassAxiomGenerator());
      gens.add(new InferredSubDataPropertyAxiomGenerator());
      gens.add(new InferredSubObjectPropertyAxiomGenerator());
    }

    if(realiseIndividuals) {
      gens.add(new InferredClassAssertionAxiomGenerator());
      gens.add(new InferredPropertyAssertionGenerator());
    }

    // Now get the inferred ontology generator to generate some inferred axioms
    // for us (into our fresh ontology). We specify the reasoner that we want
    // to use and the inferred axiom generators that we want to use.
    InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);

    iog.fillOntology(manager, inferred);

    if(spp != null) spp.cleanOntology(inferred, manager);
  }

  /**
   * <!-- checkConsistency -->
   * 
   * Check the consistency of the input ontologies using the reasoner, and
   * populate sets of inconsistent ontologies and classes to record any
   * inconsistencies found. The method is also used to fill the set of original
   * axioms.
   * 
   * @throws OWLReasonerException
   */
  private void checkConsistency() throws OWLReasonerException {
    for(OWLOntology ontology: ontologies) {
      if(!reasoner.isConsistent(ontology)) {
        inconsistentOntologies.add(ontology);
      }
      originalAxioms.addAll(ontology.getAxioms());
    }
    inconsistentClasses.addAll(reasoner.getInconsistentClasses());
  }

  /**
   * <!-- getElapsedTime -->
   * 
   * @return the time taken for the reasoning process of this instance
   */
  public long getElapsedTime() {
    return elapsedTime;
  }

  /**
   * <!-- getInconsistentClasses -->
   * 
   * @return the set of inconsistent classes found during the reasoning process
   */
  public Set<OWLClass> getInconsistentClasses() {
    return inconsistentClasses;
  }

  /**
   * <!-- getInconsistentOntologies -->
   * 
   * @return the set of inconsistent ontologies found during the reasoning
   *         process
   */
  public Set<OWLOntology> getInconsistentOntologies() {
    return inconsistentOntologies;
  }

  /**
   * <!-- getInferredAxioms -->
   * 
   * @return the set of axioms inferred by the reasoning process
   */
  public Set<OWLAxiom> getInferredAxioms() {
    Set<OWLAxiom> axioms = new HashSet<OWLAxiom>(inferredAxioms);
    axioms.removeAll(originalAxioms);
    return axioms;
  }

  /**
   * <!-- getNInferredAxioms -->
   * 
   * @return the number of axioms inferred by the reasoning process
   */
  public int getNInferredAxioms() {
    return getInferredAxioms().size();
  }

  /**
   * <!-- getInferredOntology -->
   * 
   * @return the inferred ontology
   */
  public OWLOntology getInferredOntology() {
    return inferred;
  }

  /**
   * <!-- inferMeasured -->
   * 
   * Wrapper method on the inference process for instances, which sets up the
   * variables responsible for monitoring the process.
   * 
   * @throws OWLException
   */
  private void inferMeasured() throws OWLException {
    long startTime = System.currentTimeMillis();
    originalAxioms = new HashSet<OWLAxiom>();
    inferredAxioms = new HashSet<OWLAxiom>();
    inconsistentClasses = new HashSet<OWLClass>();
    inconsistentOntologies = new HashSet<OWLOntology>();
    checkConsistency();
    inferWithExceptions(manager, inferred, reasoner, ontologies, spp, realiseIndividuals, classifyTaxonomy);
    inferredAxioms.addAll(inferred.getAxioms());
    long stopTime = System.currentTimeMillis();
    elapsedTime = (stopTime - startTime);
  }

}
