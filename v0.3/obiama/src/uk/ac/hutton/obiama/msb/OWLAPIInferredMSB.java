/*
 * uk.ac.hutton.obiama.msb: OWLAPIInferredMSB.java
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.io.RDFXMLOntologyFormat;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAnnotationAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLCommentAnnotation;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLConstantAnnotation;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLIndividualAxiom;
import org.semanticweb.owl.model.OWLLogicalAxiom;
import org.semanticweb.owl.model.OWLNamedObject;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectAnnotation;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLOntologyStorageException;
import org.semanticweb.owl.model.OWLPropertyExpression;
import org.semanticweb.owl.model.OWLTypedConstant;
import org.semanticweb.owl.model.RemoveAxiom;
import org.semanticweb.owl.model.UnknownOWLOntologyException;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;
import org.semanticweb.owl.vocab.OWLXMLVocabulary;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.action.Creator;
import uk.ac.hutton.obiama.action.Process;
import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.action.Query;
import uk.ac.hutton.obiama.exception.AmbiguousConceptException;
import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.CannotRemoveInferredAxiomException;
import uk.ac.hutton.obiama.exception.ChangeToNonMutableIndividualException;
import uk.ac.hutton.obiama.exception.ConceptDoesNotHaveQueryException;
import uk.ac.hutton.obiama.exception.ConceptNotInDomainOfPropertyException;
import uk.ac.hutton.obiama.exception.DeathOfDeadAgentException;
import uk.ac.hutton.obiama.exception.DeathOfNonAgentException;
import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ModelStructureOntologyException;
import uk.ac.hutton.obiama.exception.NeedDataGotObjectPropertyException;
import uk.ac.hutton.obiama.exception.NeedObjectGotDataPropertyException;
import uk.ac.hutton.obiama.exception.NoSuchConceptException;
import uk.ac.hutton.obiama.exception.NoSuchDataTypeException;
import uk.ac.hutton.obiama.exception.NoSuchIndividualException;
import uk.ac.hutton.obiama.exception.NoSuchProcessImplementationException;
import uk.ac.hutton.obiama.exception.NoSuchPropertyException;
import uk.ac.hutton.obiama.exception.NonClassAccessedAsClassException;
import uk.ac.hutton.obiama.exception.OntologyConfigurationException;
import uk.ac.hutton.obiama.exception.Panic;
import uk.ac.hutton.obiama.exception.ScheduleException;
import uk.ac.hutton.obiama.exception.StateOntologyHasTBoxAxiomsException;
import uk.ac.hutton.obiama.exception.UsageException;
import uk.ac.hutton.obiama.model.Log;
import uk.ac.hutton.obiama.model.Model;
import uk.ac.hutton.obiama.model.ObiamaOntology;
import uk.ac.hutton.obiama.model.ObiamaSchedule;
import uk.ac.hutton.obiama.model.OntologyQuery;
import uk.ac.hutton.obiama.model.ScheduleOntologyInstance;
import uk.ac.hutton.util.Reflection;
import uk.ac.hutton.util.URIComparator;

/**
 * OWLAPIInferredMSB
 * 
 * This is a ModelStateBroker that works by generating the inferred ontology in
 * full once, and then answers queries without using the reasoner. It uses the
 * OWLAPI from Manchester University.
 * 
 * @author Gary Polhill
 */
public class OWLAPIInferredMSB extends AbstractModelStateBroker {
  /**
   * Used when constructing URIs for new instances
   */
  public static final String URI_INSTANCE_SEPARATOR = "_";

  /**
   * Also used when constructing instance URIs
   */
  public static final String DEFAULT_INSTANCE_URI_STR = "http://www.hutton.ac.uk/obiama/instances.owl#instance";

  /**
   * Default URI to use for the state ontology
   */
  public static final URI DEFAULT_STATE_ONTOLOGY_URI = URI.create("state.owl");

  /**
   * The OWL Ontology Manager used by this MSB
   */
  private OWLOntologyManager manager;

  /**
   * An ontology containing all inferences from the model ontology
   */
  private OWLOntology inferredModel;

  /**
   * An ontology containing all inferences from the state ontology
   */
  private OWLOntology inferredState;

  /**
   * Ontologies containing assertions about the model structure
   */
  private Set<OWLOntology> assertedModel;

  /**
   * Ontologies containing assertions about the model state
   */
  private Set<OWLOntology> assertedState;

  /**
   * The closure of the asserted model ontology
   */
  private Set<OWLOntology> modelClosure;

  /**
   * The set of model ontologies to search
   */
  private Set<OWLOntology> modelSearch;

  /**
   * A factory to build OWL components
   */
  private OWLDataFactory factory;

  /**
   * The current set of variable values pending update
   */
  private Set<AbstractValue<?>> values;

  /**
   * Map of URIs of properties to individuals for values pending update
   */
  private Map<URI, Map<URI, AbstractValue<?>>> lockedValues;

  /**
   * The current set of instances pending update
   */
  private Set<AbstractInstance> instances;

  /**
   * Set of URIs of individuals pending update
   */
  private Map<URI, AbstractInstance> lockedInstances;

  /**
   * A set of non-mutable individuals
   */
  private Set<OWLIndividual> nonMutableIndividuals;

  /**
   * A set of axioms to add to the state ontology
   */
  private Set<OWLAxiom> addAxioms;

  /**
   * A map of axioms to add to the action responsible
   */
  private Map<OWLAxiom, Action> actionsAddingAxioms;

  /**
   * A set of axioms to remove from state ontology
   */
  private Set<OWLAxiom> removeAxioms;

  /**
   * A map of axioms to remove to the action responsible
   */
  private Map<OWLAxiom, Action> actionsRemovingAxioms;

  /**
   * Map to store a unique identifier for creating instances of OWL classes
   */
  private Map<URI, Integer> nextInstanceID;

  /**
   * Map to store any creators for classes in the model structure ontology
   */
  private Map<URI, Set<ObiamaSchedule>> creators;

  /**
   * Set of created instances since last update
   */
  private Set<URI> createdInstances;

  /**
   * Stored sets of equivalent entities in the model structure ontology
   */
  private Map<URI, Set<URI>> equivalentEntities;

  /**
   * Map equivalent entities to the named entity in the model structure ontology
   * -- this is to enable assertions to be made using the vocabulary of the user
   * rather than the default ontologies in the actions.
   */
  private Map<URI, URI> modelEntities;

  /**
   * Stored sets of super entities in the model structure ontology
   */
  private Map<URI, Set<URI>> superEntities;

  /**
   * Stored sets of sub entities in the model structure ontology
   */
  private Map<URI, Set<URI>> subEntities;

  /**
   * Queries mentioned in the model state ontology, associated with the URI of
   * the class they are linked to
   */
  private Map<URI, Set<OntologyQuery>> queries;

  /**
   * Pointer to the Model object
   */
  private Model model;

  private String prevSaveDir;

  private String nextSaveDir;

  private Provenance provenance;

  /**
   * Constructor for an MSB
   */
  private OWLAPIInferredMSB(Model model) {
    manager = OWLManager.createOWLOntologyManager();
    factory = manager.getOWLDataFactory();
    assertedModel = new HashSet<OWLOntology>();
    assertedState = new HashSet<OWLOntology>();
    modelClosure = new HashSet<OWLOntology>();
    modelSearch = new HashSet<OWLOntology>();
    values = new HashSet<AbstractValue<?>>();
    lockedValues = new HashMap<URI, Map<URI, AbstractValue<?>>>();
    instances = new HashSet<AbstractInstance>();
    lockedInstances = new HashMap<URI, AbstractInstance>();
    createdInstances = new HashSet<URI>();
    nextInstanceID = new HashMap<URI, Integer>();
    inferredModel = null;
    inferredState = null;
    removeAxioms = new HashSet<OWLAxiom>();
    actionsRemovingAxioms = new HashMap<OWLAxiom, Action>();
    addAxioms = new HashSet<OWLAxiom>();
    actionsAddingAxioms = new HashMap<OWLAxiom, Action>();
    this.model = model;
    prevSaveDir = null;
    nextSaveDir = null;
    creators = new HashMap<URI, Set<ObiamaSchedule>>();
    equivalentEntities = new HashMap<URI, Set<URI>>();
    modelEntities = new HashMap<URI, URI>();
    superEntities = new HashMap<URI, Set<URI>>();
    subEntities = new HashMap<URI, Set<URI>>();
    queries = new HashMap<URI, Set<OntologyQuery>>();
    provenance = ProvenanceFactory.getProvenance();
  }

  /**
   * Public constructor
   * 
   * This method loads and initialises the asserted and inferred model
   * 
   * @param model
   * 
   * @param modelOntologies A set of URIs containing the ontologies to load to
   *          cover the model structure
   * @param helper An initialised helper object configured with information on
   *          the mappings of logical to physical URIs for each ontology
   * @throws ModelStructureOntologyException
   */
  public OWLAPIInferredMSB(Model model, Set<URI> modelOntologies, OntologyIOHelper helper)
      throws ModelStructureOntologyException {
    this(model);

    // Load the model ontologies

    helper.configure(manager);
    for(URI uri: modelOntologies) {
      try {
        assertedModel.add(helper.loadOntologyClosure(uri, manager, modelClosure));
      }
      catch(OWLOntologyCreationException e) {
        ErrorHandler.fatal(e, "loading ontology " + uri.toString());
        throw new Panic();
      }
    }
    modelSearch.addAll(modelClosure);

    // Create the inferred model
    try {
      inferredModel = manager.createOntology(AnonymousURI.createAnonymousURI());
      InferredOntologyCreator.infer(manager, inferredModel);
    }
    catch(OWLOntologyCreationException e) {
      ErrorHandler.fatal(e, "creating inferred model ontology");
      throw new Panic();
    }
    // TODO the infer() above should be done using the URI not the
    // ontology. The modelSearch should just be the inferredModel.

    // I think what I mean here is that inferredModel should contain all the
    // axioms in assertedModel. It doesn't at present.

    // But surely it does? Perhaps what I meant is that we should rid ourselves
    // of modelSearch and use inferredModel instead.

    modelSearch.add(inferredModel);
    nonMutableIndividuals = new HashSet<OWLIndividual>(inferredModel.getReferencedIndividuals());

    buildEquivalentEntities();

    buildSuperEntities();

    buildCreatorsAndQueries();

    // Create an empty ontology with the inferred state
    try {
      inferredState = manager.createOntology(AnonymousURI.createAnonymousURI());
    }
    catch(OWLOntologyCreationException e) {
      ErrorHandler.fatal(e, "creating inferred state ontology");
      throw new Panic();
    }
  }

  /**
   * <!-- buildCreatorsAndQueries -->
   * 
   * @throws ModelStructureOntologyException
   * 
   */
  private void buildCreatorsAndQueries() throws ModelStructureOntologyException {
    // Build creators from the inferred model structure ontology

    try {
      ScheduleOntologyInstance modelCreatorSchedules =
        new ScheduleOntologyInstance(AnonymousURI.createAnonymousURI(), inferredModel);

      Map<OWLAnnotationAxiom<? extends OWLObject>, OWLOntology> assertedAnnotations =
        new HashMap<OWLAnnotationAxiom<? extends OWLObject>, OWLOntology>();
      for(OWLOntology assertedOntology: assertedModel) {
        for(OWLAnnotationAxiom<? extends OWLObject> annotationAxiom: assertedOntology.getAnnotationAxioms()) {
          assertedAnnotations.put(annotationAxiom, assertedOntology);
        }
      }

      for(OWLAnnotationAxiom<? extends OWLObject> axiom: assertedAnnotations.keySet()) {
        OWLAnnotation<?> annotation = axiom.getAnnotation();
        OWLOntology ontology = assertedAnnotations.get(axiom);

        if(axiom instanceof OWLEntityAnnotationAxiom) {
          OWLEntity subject = ((OWLEntityAnnotationAxiom)axiom).getSubject();
          URI property = annotation.getAnnotationURI();
          if(subject instanceof OWLClass) {
            if(property.equals(ObiamaOntology.HAS_CREATOR_URI)) {
              addCreator(modelCreatorSchedules, subject, getAnnotationObjectURI(annotation, subject.getURI()));
            }
            else if(property.equals(ObiamaOntology.HAS_QUERY_URI)) {
              addQuery(modelCreatorSchedules, subject.getURI(), getAnnotationObjectURI(annotation, subject.getURI()));
            }
            else if(property.equals(OWLRDFVocabulary.RDFS_COMMENT.getURI())) {
              if(annotation instanceof OWLConstantAnnotation) {
                try {
                  URI commentURI = getAnnotationValueURI((OWLConstantAnnotation)annotation);
                  if(commentURI != null) {
                    OWLIndividual commentIndividual = factory.getOWLAnonymousIndividual(commentURI);
                    for(OWLAxiom commentAxiom: ontology.getReferencingAxioms(commentIndividual)) {
                      if(commentAxiom instanceof OWLObjectPropertyAssertionAxiom) {
                        OWLObjectPropertyAssertionAxiom commentPropAxiom =
                          (OWLObjectPropertyAssertionAxiom)commentAxiom;
                        if(!commentPropAxiom.getProperty().isAnonymous()
                          && commentPropAxiom.getProperty().asOWLObjectProperty().getURI()
                              .equals(ObiamaOntology.HAS_CREATOR_URI)) {
                          addCreator(modelCreatorSchedules, subject, commentPropAxiom.getObject().getURI());
                        }
                        else if(!commentPropAxiom.getProperty().isAnonymous()
                          && commentPropAxiom.getProperty().asOWLObjectProperty().getURI()
                              .equals(ObiamaOntology.HAS_QUERY_URI)) {
                          addQuery(modelCreatorSchedules, subject.getURI(), commentPropAxiom.getObject().getURI());
                        }
                        else {
                          System.out.println("It isn't " + ObiamaOntology.HAS_CREATOR_URI + " or "
                            + ObiamaOntology.HAS_QUERY_URI + ", it's "
                            + commentPropAxiom.getProperty().asOWLObjectProperty().getURI());
                        }
                      }
                      else {
                        System.out.println("It isn't an object property assertion axiom");
                      }
                    }
                  }
                }
                catch(URISyntaxException e) {
                  // Do nothing: this comment doesn't contain an axiom asserting
                  // #hasCreator
                }
              }
            }
          }
        }
      }
    }
    catch(OWLOntologyCreationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch(OWLOntologyChangeException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch(NoSuchProcessImplementationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch(IntegrationInconsistencyException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch(ScheduleException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch(OntologyConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private URI getAnnotationObjectURI(OWLAnnotation<?> annotation, URI subject) throws ModelStructureOntologyException {
    if(annotation instanceof OWLObjectAnnotation) {
      OWLIndividual object = ((OWLObjectAnnotation)annotation).getAnnotationValue();
      return object.getURI();
    }
    else if(annotation instanceof OWLConstantAnnotation) {
      try {
        URI value = getAnnotationValueURI((OWLConstantAnnotation)annotation);
        if(value == null) {
          throw new ModelStructureOntologyException(annotation.getAnnotationURI(), subject, "null value");
        }
        else {
          return value;
        }
      }
      catch(URISyntaxException e) {
        throw new ModelStructureOntologyException(annotation.getAnnotationURI(), subject,
            "URI syntax exception in value " + annotation.getAnnotationValue().toString() + ": " + e.getMessage());
      }
    }
    else {
      throw new Bug();
    }
  }

  private void buildEquivalentEntities() {
    for(OWLOntology ontology: assertedModel) {
      URI ontologyURI = ontology.getURI();

      for(OWLEntity entity: ontology.getReferencedEntities()) {
        if(!(entity instanceof OWLIndividual) && !equivalentEntities.containsKey(entity.getURI())) {
          Set<URI> equivs = getEquivalentEntities(entity);
          URI modelName = null;
          for(URI equiv: equivs) {
            equivalentEntities.put(equiv, equivs);
            if(ontologyURI.relativize(equiv).toString().equals("#" + equiv.getFragment())) {
              // This entity is a named entity of the current ontology
              modelName = equiv;
            }
          }
          if(modelName != null) {
            for(URI equiv: equivs) {
              modelEntities.put(equiv, modelName);
            }
          }
        }
      }
    }
  }

  private void buildSuperEntities() {
    for(OWLOntology ontology: assertedModel) {
      for(OWLEntity entity: ontology.getReferencedEntities()) {
        if(!(entity instanceof OWLIndividual) && !superEntities.containsKey(entity.getURI())) {
          Set<URI> supers = getSuperEntities(entity);
          superEntities.put(entity.getURI(), supers);
        }
      }
    }
  }

  private URI getAnnotationValueURI(OWLConstantAnnotation annotation) throws URISyntaxException {
    OWLConstant object = ((OWLConstantAnnotation)annotation).getAnnotationValue();
    if(object.isTyped()) {
      OWLTypedConstant typedObject = object.asOWLTypedConstant();
      if(typedObject.getDataType().getURI().equals(XSDVocabulary.ANY_URI.getURI())) {
        return new URI(typedObject.getLiteral());
      }
      else {
        return null;
      }
    }
    else {
      return new URI(object.getLiteral());
    }
  }

  private void addCreator(ScheduleOntologyInstance modelCreatorSchedules, OWLEntity subject, URI creatorURI)
      throws NoSuchProcessImplementationException, IntegrationInconsistencyException, ScheduleException,
      OntologyConfigurationException, ModelStructureOntologyException {
    ObiamaSchedule creatorSchedule = modelCreatorSchedules.getSchedule(creatorURI, this);

    if(creatorSchedule.isTimed()) {
      throw new ModelStructureOntologyException(creatorURI, "creator schedule is timed");
    }
    if(!creators.containsKey(subject.getURI())) {
      creators.put(subject.getURI(), new HashSet<ObiamaSchedule>());
    }
    creators.get(subject.getURI()).add(creatorSchedule);
  }

  private void addQuery(ScheduleOntologyInstance scheduleOnt, URI classURI, URI queryURI)
      throws ModelStructureOntologyException, NoSuchProcessImplementationException {
    OntologyQuery query = scheduleOnt.buildQuery(queryURI, this);

    if(!queries.containsKey(classURI)) {
      queries.put(classURI, new HashSet<OntologyQuery>());
    }
    queries.get(classURI).add(query);
  }

  public void loadState(String stateOntologyURI) throws URISyntaxException, StateOntologyHasTBoxAxiomsException {
    Set<OWLOntology> closure = new HashSet<OWLOntology>();
    try {
      OntologyIOHelper.loadClosure(new URI(stateOntologyURI), manager, closure, modelSearch);
    }
    catch(OWLOntologyCreationException e) {
      ErrorHandler.redo(e, "loading state ontology from " + stateOntologyURI);
    }

    for(OWLOntology inClosure: closure) {
      for(OWLAxiom axiom: inClosure.getAxioms()) {
        if(axiom instanceof OWLLogicalAxiom && !(axiom instanceof OWLIndividualAxiom)) {
          throw new StateOntologyHasTBoxAxiomsException(stateOntologyURI, inClosure.getURI(), axiom.toString());
        }
      }
    }

    assertedState.addAll(closure);
    closure.add(inferredModel);
    InferredOntologyCreator.infer(manager, inferredState, closure);
  }

  public void createState() {
    if(assertedState.size() == 0) {
      try {
        OWLOntology newState = manager.createOntology(DEFAULT_STATE_ONTOLOGY_URI);
        manager.addAxiom(newState, factory.getOWLImportsDeclarationAxiom(newState, getBaseURI()));
        assertedState.add(newState);
      }
      catch(OWLOntologyCreationException e) {
        ErrorHandler.redo(e, "creating default state ontology " + DEFAULT_STATE_ONTOLOGY_URI);
      }
      catch(OWLOntologyChangeException e) {
        ErrorHandler.redo(e, "creating default state ontology " + DEFAULT_STATE_ONTOLOGY_URI);
      }
    }
  }

  public Set<Action> getActionSet() {
    Set<Action> set = new HashSet<Action>();

    for(Set<ObiamaSchedule> creatorSet: creators.values()) {
      for(ObiamaSchedule creator: creatorSet) {
        set.addAll(creator.getActionSet());
      }
    }

    return set;
  }

  Set<OWLOntology> getModel() {
    return Collections.singleton(inferredModel);
  }

  Set<OWLOntology> getAssertedModel() {
    return modelSearch;
  }

  public Set<URI> find(String name) {
    Set<URI> found = new HashSet<URI>();
    for(OWLOntology ont: new OWLOntology[] { inferredModel, inferredState }) {
      for(OWLEntity ent: ont.getReferencedEntities()) {
        URI entName = ent.getURI();
        if(name.startsWith("#") && name.equals("#" + entName.getFragment())) {
          found.add(entName);
        }
        else if(name.equals(entName.toString()) || name.equals(entName.getFragment())) {
          found.add(entName);
        }
      }
    }
    return found;
  }

  public Set<URI> findModelName(String name) {
    Set<URI> found = find(name);
    Set<URI> model = new HashSet<URI>();

    for(URI uri: found) {
      if(modelEntities.containsKey(uri)) {
        model.add(modelEntities.get(uri));
      }
      else {
        model.add(uri);
      }
    }

    return model;
  }

  boolean isDataProperty(URI name) {
    for(OWLOntology ont: modelSearch) {
      if(ont.containsDataPropertyReference(name)) return true;
    }
    return false;
  }

  boolean isObjectProperty(URI name) {
    for(OWLOntology ont: modelSearch) {
      if(ont.containsObjectPropertyReference(name)) return true;
    }
    return false;
  }

  boolean isClass(URI name) {
    for(OWLOntology ont: modelSearch) {
      if(ont.containsClassReference(name)) return true;
    }
    return false;
  }

  boolean isDataType(URI name) {
    for(OWLOntology ont: modelSearch) {
      if(ont.containsDataTypeReference(name)) return true;
    }
    return false;
  }

  @Override
  URI createInstanceURI(URI conceptURI) {
    int id = nextInstanceID.containsKey(conceptURI) ? nextInstanceID.get(conceptURI) : 1;

    URI instanceURI = null;
    do {
      try {
        instanceURI = new URI(conceptURI.toString() + URI_INSTANCE_SEPARATOR + Integer.toString(id));
        id++;
      }
      catch(URISyntaxException e) {
        break;
      }
    } while(inferredState.containsEntityReference(factory.getOWLIndividual(instanceURI)));

    if(instanceURI != null) {
      nextInstanceID.put(conceptURI, id);
      return instanceURI;
    }

    if(conceptURI.equals(getBaseURI())) {
      try {
        return createInstanceURI(new URI(DEFAULT_INSTANCE_URI_STR));
      }
      catch(URISyntaxException e) {
        // The DEFAULT_INSTANCE_URI_STR is not a valid URI
        throw new Bug();
      }
    }
    else if(conceptURI.toString().equals(DEFAULT_INSTANCE_URI_STR)) {
      // The DEFAULT_INSTANCE_URI_STR cannot be used with the
      // URI_INSTANCE_SEPARATOR and an integer to create an instance URI
      throw new Bug();
    }
    else {
      return createInstanceURI(getBaseURI());
    }
  }

  private Set<URI> getSuperEntities(OWLEntity entity) {
    if(entity.isOWLClass()) {
      return getAllAssertedSuperClassesOf(entity.getURI());
    }
    else if(entity.isOWLDataProperty()) {
      return getAllAssertedSuperDataPropertiesOf(entity.getURI());
    }
    else if(entity.isOWLObjectProperty()) {
      return getAllAssertedSuperObjectPropertiesOf(entity.getURI());
    }
    else {
      throw new Bug();
    }
  }

  Set<URI> getAllAssertedSuperDataPropertiesOf(URI propURI) {
    Set<OWLDataPropertyExpression> exprs =
      getAllAssertedSuperPropertiesOf((OWLDataPropertyExpression)factory.getOWLDataProperty(propURI));
    Set<URI> props = new HashSet<URI>();
    for(OWLDataPropertyExpression expr: exprs) {
      if(!expr.isAnonymous()) {
        props.add(expr.asOWLDataProperty().getURI());
      }
    }
    return props;
  }

  Set<URI> getAllAssertedSuperObjectPropertiesOf(URI propURI) {
    Set<OWLObjectPropertyExpression> exprs =
      getAllAssertedSuperPropertiesOf((OWLObjectPropertyExpression)factory.getOWLObjectProperty(propURI));
    Set<URI> props = new HashSet<URI>();
    for(OWLObjectPropertyExpression expr: exprs) {
      if(!expr.isAnonymous()) {
        props.add(expr.asOWLObjectProperty().getURI());
      }
    }
    return props;
  }

  Set<URI> getSuperClassesOf(URI classURI) {
    OWLClass owlClass = factory.getOWLClass(classURI);
    Set<OWLDescription> descs = owlClass.getSuperClasses(assertedModel);
    Set<URI> classURIs = new HashSet<URI>();
    for(OWLDescription desc: descs) {
      if(!desc.isAnonymous()) classURIs.add(desc.asOWLClass().getURI());
    }
    return classURIs;
  }

  Set<URI> getAllAssertedSuperClassesOf(URI classURI) {
    OWLClass owlClass = factory.getOWLClass(classURI);
    Set<URI> classURIs = new HashSet<URI>();
    for(OWLClass superClass: getAllAssertedSuperClassesOf(owlClass)) {
      classURIs.add(superClass.getURI());
    }
    return classURIs;
  }

  private Set<OWLClass> getAllAssertedSuperClassesOf(OWLClass owlClass) {
    return getAllAssertedSuperClassesOf(owlClass, new HashSet<OWLClass>(Collections.singleton(owlClass)));
  }

  private Set<OWLClass> getAllAssertedSuperClassesOf(OWLClass owlClass, Set<OWLClass> stack) {
    Set<OWLClass> classes = new HashSet<OWLClass>();
    classes.add(owlClass);
    if(owlClass.isOWLThing()) return classes;
    Set<OWLDescription> descs = new HashSet<OWLDescription>();
    Set<OWLDescription> equivs = new HashSet<OWLDescription>();
    equivs.add(owlClass);
    equivs.addAll(owlClass.getEquivalentClasses(modelSearch));
    for(OWLDescription equiv: equivs) {
      if(!equiv.isAnonymous()) {
        for(OWLDescription desc: equiv.asOWLClass().getSuperClasses(modelSearch)) {
          descs.add(desc);
          if(!desc.isAnonymous()) {
            descs.addAll(desc.asOWLClass().getEquivalentClasses(modelSearch));
          }
        }
      }
    }
    for(OWLDescription desc: descs) {
      if(!desc.isAnonymous()) {
        OWLClass superOrEquivalentClass = desc.asOWLClass();
        if(!stack.contains(superOrEquivalentClass)) {
          stack.add(superOrEquivalentClass);
          classes.addAll(getAllAssertedSuperClassesOf(superOrEquivalentClass, stack));
        }
      }
    }
    return classes;
  }

  private <P extends OWLPropertyExpression<P, ?>> Set<P> getAllAssertedSuperPropertiesOf(P prop) {
    return getAllAssertedSuperPropertiesOf(prop, new HashSet<P>());
  }

  private <P extends OWLPropertyExpression<P, ?>> Set<P> getAllAssertedSuperPropertiesOf(P prop, Set<P> stack) {
    Set<P> props = new HashSet<P>();
    props.add(prop);
    Set<P> exprs = new HashSet<P>();
    Set<P> equivs = new HashSet<P>();
    equivs.add(prop);
    equivs.addAll(prop.getEquivalentProperties(modelSearch));
    for(P equiv: equivs) {
      for(P expr: equiv.getSuperProperties(modelSearch)) {
        exprs.add(expr);
        exprs.addAll(expr.getEquivalentProperties(modelSearch));
      }
    }
    for(P expr: exprs) {
      if(!stack.contains(expr)) {
        stack.add(expr);
        props.addAll(getAllAssertedSuperPropertiesOf(expr, stack));
      }
    }
    return props;
  }

  @Override
  Set<URI> getClassesOf(URI individualURI) {
    OWLIndividual individual = factory.getOWLIndividual(individualURI);
    Set<OWLDescription> descs = individual.getTypes(assertedState);
    descs.addAll(individual.getTypes(modelSearch));

    Set<URI> classes = new HashSet<URI>();
    for(OWLDescription desc: descs) {
      if(!desc.isAnonymous()) classes.add(desc.asOWLClass().getURI());
    }

    return classes;
  }

  Set<Var> getPropertiesOf(URI individualURI, Process process) throws IntegrationInconsistencyException {
    OWLIndividual individual = factory.getOWLIndividual(individualURI);
    
    Map<OWLDataPropertyExpression, Set<OWLConstant>> dataValues = individual.getDataPropertyValues(inferredState);
    dataValues.putAll(individual.getDataPropertyValues(inferredModel));
    
    Map<OWLObjectPropertyExpression, Set<OWLIndividual>> objectValues =
      individual.getObjectPropertyValues(inferredState);
    objectValues.putAll(individual.getObjectPropertyValues(inferredModel));

    Set<Var> vars = new HashSet<Var>();

    for(OWLDataPropertyExpression expr: dataValues.keySet()) {
      if(!expr.isAnonymous()) {
        vars.add(getVariableName(expr.asOWLDataProperty().getURI(), process));
      }
    }

    for(OWLObjectPropertyExpression expr: objectValues.keySet()) {
      if(!expr.isAnonymous()) {
        vars.add(getVariableName(expr.asOWLObjectProperty().getURI(), process));
      }
    }

    return vars;
  }

  AbstractInstance copyIndividual(AbstractInstance instance, Process originator)
      throws IntegrationInconsistencyException {
    URI copyURI = createInstanceURI(instance.getURI());
    MSBInstance copy = new MSBInstance(copyURI, originator, this);
    createdInstances.add(copyURI);

    OWLIndividual original = factory.getOWLIndividual(instance.getURI());

    Set<URI> originalConcepts = instance.getConcepts();

    for(OWLDescription desc: original.getTypes(assertedState)) {
      if(!desc.isAnonymous()) {
        URI classURI = desc.asOWLClass().getURI();
        if(originalConcepts.contains(classURI)) {
          copy.addConcept(classURI);
        }
        else {
          copy.addQuietConcept(classURI);
        }
      }
    }

    Map<OWLDataPropertyExpression, Set<OWLConstant>> dataValues = original.getDataPropertyValues(inferredState);
    Map<OWLObjectPropertyExpression, Set<OWLIndividual>> objectValues = original.getObjectPropertyValues(inferredState);

    Map<URI, Var> vars = new HashMap<URI, Var>();
    for(Var origVar: instance.getVars()) {
      vars.put(origVar.getURI(), origVar);
    }
    copy.setVars(vars.values());

    for(OWLDataPropertyExpression expr: dataValues.keySet()) {
      if(!expr.isAnonymous()) {
        URI dataURI = expr.asOWLDataProperty().getURI();

        Var var = vars.containsKey(dataURI) ? vars.get(dataURI) : getVariableName(dataURI, originator);
        Value<?> value = vars.containsKey(dataURI) ? copy.getValue(var) : var.getValueFor(copy.getURI());

        for(OWLConstant origValue: dataValues.get(expr)) {
          if(var.isFunctional()) {
            value.setString(origValue.getLiteral());
          }
          else {
            value.addString(origValue.getLiteral());
          }
        }
      }
    }

    for(OWLObjectPropertyExpression expr: objectValues.keySet()) {
      if(!expr.isAnonymous()) {
        URI objectURI = expr.asOWLObjectProperty().getURI();

        boolean deep =
          expr.getSuperProperties(modelSearch).contains(
              factory.getOWLObjectProperty(ObiamaOntology.COPYING_PART_OF_URI));

        Var var = vars.containsKey(objectURI) ? vars.get(objectURI) : getVariableName(objectURI, originator);
        Value<URI> value;
        if(vars.containsKey(objectURI)) {
          value = copy.getValue(var);
        }
        else {
          value = var.getValueFor(copy.getURI());
        }

        for(OWLIndividual ind: objectValues.get(expr)) {
          if(deep) {
            AbstractInstance origInst = new MSBInstance(ind.getURI(), originator, this);
            AbstractInstance deepInst = copyIndividual(origInst, originator);

            if(var.isFunctional()) {
              value.set(deepInst);
            }
            else {
              value.add(deepInst);
            }
          }
          else {
            if(var.isFunctional()) {
              value.set(ind.getURI());
            }
            else {
              value.add(ind.getURI());
            }
          }
        }
      }
    }

    return copy;
  }

  private Set<URI> getEquivalentEntities(OWLEntity entity) {
    Set<URI> equivs = new HashSet<URI>();
    if(entity instanceof OWLDataProperty) {
      getEquivalentProperties((OWLDataProperty)entity, equivs);
    }
    else if(entity instanceof OWLObjectProperty) {
      getEquivalentProperties((OWLObjectProperty)entity, equivs);
    }
    else if(entity instanceof OWLClass) {
      getEquivalentClasses((OWLClass)entity, equivs);
    }
    return equivs;
  }

  private void getEquivalentProperties(OWLDataProperty property, Set<URI> equivs) {
    if(equivs.contains(property.getURI())) return;
    equivs.add(property.getURI());

    Set<OWLDataPropertyExpression> knownEquivs = property.getEquivalentProperties(modelSearch);
    for(OWLDataPropertyExpression expr: knownEquivs) {
      if(!expr.isAnonymous()) {
        getEquivalentProperties(expr.asOWLDataProperty(), equivs);
      }
    }
  }

  private void getEquivalentProperties(OWLObjectProperty property, Set<URI> equivs) {
    if(equivs.contains(property.getURI())) return;
    equivs.add(property.getURI());

    Set<OWLObjectPropertyExpression> knownEquivs = property.getEquivalentProperties(modelSearch);
    for(OWLObjectPropertyExpression expr: knownEquivs) {
      if(!expr.isAnonymous()) {
        getEquivalentProperties(expr.asOWLObjectProperty(), equivs);
      }
    }
  }

  private void getEquivalentClasses(OWLClass owlClass, Set<URI> equivs) {
    if(equivs.contains(owlClass.getURI())) return;
    equivs.add(owlClass.getURI());

    Set<OWLDescription> knownEquivs = owlClass.getEquivalentClasses(modelSearch);
    for(OWLDescription desc: knownEquivs) {
      if(!desc.isAnonymous()) {
        getEquivalentClasses(desc.asOWLClass(), equivs);
      }
    }
  }

  private Set<URI> getSuperOrEquivalentEntities(URI entityURI) {
    Set<URI> superEquiv = new HashSet<URI>();
    if(equivalentEntities.containsKey(entityURI)) superEquiv.addAll(equivalentEntities.get(entityURI));
    if(superEntities.containsKey(entityURI)) superEquiv.addAll(superEntities.get(entityURI));
    return superEquiv;
  }

  Set<OWLIndividual> getObjectValueRestrictions(OWLClass concept, OWLObjectProperty property) {
    Set<OWLIndividual> restrictionSet = new HashSet<OWLIndividual>();

    Set<OWLDescription> superConcepts = concept.getSuperClasses(modelSearch);
    Set<OWLDescription> equivConcepts = concept.getEquivalentClasses(modelSearch);
    superConcepts.addAll(equivConcepts);
    for(OWLDescription aSuper: superConcepts) {
      if(aSuper instanceof OWLObjectValueRestriction) {
        OWLObjectValueRestriction restriction = (OWLObjectValueRestriction)aSuper;
        OWLObjectPropertyExpression p = restriction.getProperty();
        if(p instanceof OWLObjectProperty && p.equals(property)) {
          restrictionSet.add(restriction.getValue());
        }
      }
    }

    return restrictionSet;
  }

  Set<OWLIndividual> getObjectAnnotationProperties(OWLEntity thing, URI annoteURI) {
    Set<OWLIndividual> annotated = new HashSet<OWLIndividual>();

    Set<OWLObjectAnnotation> annotes = new HashSet<OWLObjectAnnotation>();
    for(OWLOntology model: modelSearch) {
      @SuppressWarnings("rawtypes")
      Set<OWLAnnotation> modelNotes = thing.getAnnotations(model, annoteURI);
      for(@SuppressWarnings("rawtypes")
      OWLAnnotation note: modelNotes) {
        if(note instanceof OWLObjectAnnotation) {
          annotes.add((OWLObjectAnnotation)note);
        }
      }
    }
    for(OWLObjectAnnotation annote: annotes) {
      annotated.add(annote.getAnnotationValue());
    }

    return annotated;
  }

  public Set<ObiamaSchedule> getCreators() {
    Set<ObiamaSchedule> creatorSchedules = new HashSet<ObiamaSchedule>();
    for(Set<ObiamaSchedule> creatorSchedule: creators.values()) {
      creatorSchedules.addAll(creatorSchedule);
    }
    return creatorSchedules;
  }

  private Set<ObiamaSchedule> getCreators(OWLClass concept) {
    Set<ObiamaSchedule> creatorSet = new HashSet<ObiamaSchedule>();

    Set<ObiamaSchedule> myCreators = creators.get(concept.getURI());
    if(myCreators != null) {
      creatorSet.addAll(myCreators);
    }

    // Here we trust that the inference engine puts all super classes (up to
    // owl:Thing) in a class's superclass set, and that these include super
    // classes of equivalent classes.

    // Set<OWLClass> equivSuper = new HashSet<OWLClass>();
    // equivSuper.addAll(concept.getEquivalentClasses(inferredModel));
    // equivSuper.addAll(concept.getSuperClasses(inferredModel));
    // for(OWLDescription desc: equivSuper) {
    // if(!desc.isAnonymous()) {
    // Set<ObiamaSchedule> theirCreators =
    // creators.get(desc.asOWLClass().getURI());
    // if(theirCreators != null) {
    // creatorSet.addAll(theirCreators);
    // }
    // }
    // }

    // Here we don't trust the inference engine

    Set<URI> equivSuper = getSuperOrEquivalentEntities(concept.getURI());

    for(URI uri: equivSuper) {
      Set<ObiamaSchedule> theirCreators = creators.get(uri);
      if(theirCreators != null) {
        creatorSet.addAll(theirCreators);
      }
    }

    System.out.println("Found " + creatorSet.size() + " creators for " + concept.getURI());
    for(ObiamaSchedule creator: creatorSet) {
      System.out.println(creator.getURI());
    }

    return creatorSet;
  }

  private Map<URI, Query<?>> getQueries(URI classURI) {
    Map<URI, Query<?>> queryMap = new HashMap<URI, Query<?>>();

    if(queries.containsKey(classURI)) addQueries(queryMap, queries.get(classURI));

    Set<URI> equivSuper = getSuperOrEquivalentEntities(classURI);

    for(URI uri: equivSuper) {
      if(queries.containsKey(uri)) addQueries(queryMap, queries.get(uri));
    }

    return queryMap;
  }

  private void addQueries(Map<URI, Query<?>> queryMap, Set<OntologyQuery> queries) {
    for(OntologyQuery query: queries) {
      queryMap.put(query.getQueryID(), query.getQuery());
    }
  }

  public Concept getConcept(URI name, Process action, Set<Var> vars,
      @SuppressWarnings("rawtypes") Map<URI, Class<? extends Query>> queries) throws IntegrationInconsistencyException {
    URI modelName = modelEntities.containsKey(name) ? modelEntities.get(name) : name;
    if(!isClass(modelName)) {
      throw new NonClassAccessedAsClassException(modelName, action);
    }

    OWLClass owlClass = factory.getOWLClass(modelName);

    MSBConcept concept;

    Set<ObiamaSchedule> creatorSet = getCreators(owlClass);

    if(creatorSet == null || creatorSet.size() == 0) {
      concept = new MSBConcept(owlClass, action, this);
    }
    else {
      concept = new MSBConcept(owlClass, action, this, creatorSet);
    }

    if(vars != null) {
      Set<OWLDescription> superClasses = owlClass.getSuperClasses(modelSearch);
      for(Var var: vars) {
        Set<OWLDescription> domains;
        if(var.isDataVar()) domains = factory.getOWLDataProperty(var.getURI()).getDomains(modelSearch);
        else if(var.isObjectVar()) domains = factory.getOWLObjectProperty(var.getURI()).getDomains(modelSearch);
        else
          throw new Bug();

        boolean inDomain = false;
        for(OWLDescription desc: domains) {
          if(superClasses.contains(desc) || owlClass.equals(desc.asOWLClass())) {
            inDomain = true;
            break;
          }
        }
        if(!inDomain) {
          throw new ConceptNotInDomainOfPropertyException(action, owlClass.getURI(), var.getURI());
        }

        concept.addVar(var);
      }
    }

    if(queries != null) {
      Map<URI, Query<?>> queryMap = getQueries(modelName);

      for(URI queryID: queries.keySet()) {
        if(queryMap.containsKey(queryID)) {
          Query<?> query = queryMap.get(queryID);
          @SuppressWarnings("rawtypes")
          Class<? extends Query> queryClass = queries.get(queryID);
          if(Reflection.subType(query.getClass(), queryClass)) {
            concept.addQuery(query, queryClass, queryID);
          }
          else {
            throw new ConceptDoesNotHaveQueryException(action, owlClass.getURI(), queryID,
                queryClass.getName());
          }
        }
        else {
          throw new ConceptDoesNotHaveQueryException(action, owlClass.getURI(), queryID);
        }
      }
    }

    return concept;
  }

  public Concept getRangeOf(Var var, Process action) throws IntegrationInconsistencyException {
    if(var.isObjectVar()) {
      OWLObjectProperty property = factory.getOWLObjectProperty(var.getURI());
      Set<OWLDescription> ranges = property.getRanges(modelSearch);
      OWLClass firstMatch = null;
      OWLClass otherMatch = null;
      boolean uniqueClass = true;
      for(OWLDescription range: ranges) {
        if(!range.isAnonymous()) {
          if(firstMatch == null) firstMatch = range.asOWLClass();
          else {
            OWLClass rangeClass = range.asOWLClass();
            Set<OWLDescription> equivs = rangeClass.getEquivalentClasses(modelSearch);
            if(equivs.contains(firstMatch)) {
              if(URIComparator.relativeDifference(firstMatch.getURI(), rangeClass.getURI(), getBaseURI(), true) < 0) {
                firstMatch = rangeClass;
              }
            }
            else {
              uniqueClass = false;
              otherMatch = rangeClass;
              break;
            }
          }
        }
      }
      if(uniqueClass) {
        return getConcept(firstMatch.getURI(), action, null, null);
      }
      else {
        throw new AmbiguousConceptException(action, var.getURI(), firstMatch.getURI(), otherMatch.getURI());
      }
    }
    else {
      throw new NeedObjectGotDataPropertyException(action, var.getURI());
    }
  }
  
  public XSDVocabulary getDataRangeOf(Var var, Process process) throws IntegrationInconsistencyException {
    if(var.isDataVar()) {
      OWLDataProperty prop = factory.getOWLDataProperty(var.getURI());
      
      XSDVocabulary dataRange = null;
      
      for(OWLDataRange range: prop.getRanges(modelSearch)) {
        dataRange = XSDHelper.generaliseType(dataRange, range);
      }
      
      return dataRange;
    }
    else {
      throw new NeedDataGotObjectPropertyException(process, var.getURI());
    }
  }

  /**
   * <!-- getVariableName -->
   * 
   * @see uk.ac.hutton.obiama.msb.ModelStateBroker#getVariableName(java.net.URI,
   *      java.net.URI, java.net.URI, uk.ac.hutton.obiama.action.Action)
   */
  @Override
  public Var getVariableName(URI name, URI domain, URI range, Process action) throws IntegrationInconsistencyException {
    URI modelName = modelEntities.containsKey(name) ? modelEntities.get(name) : name;
    if(isDataProperty(modelName)) {
      if(XSDVocabulary.ALL_DATATYPES.contains(range)) {
        return VariableNameFactory.getVariableName(factory.getOWLDataProperty(modelName), factory.getOWLClass(domain),
            XSDHelper.xsdTypes.get(range), action, this);
      }
      else {
        throw new NoSuchDataTypeException(action, range);
      }
    }
    else if(isObjectProperty(modelName)) {
      return VariableNameFactory.getVariableName(factory.getOWLObjectProperty(modelName), factory.getOWLClass(domain),
          factory.getOWLClass(range), action, this);
    }
    else {
      throw new NoSuchPropertyException(action, modelName);
    }
  }

  @Override
  public Var getVariableName(URI name, URI range, Process action) throws IntegrationInconsistencyException {
    URI modelName = modelEntities.containsKey(name) ? modelEntities.get(name) : name;

    if(isDataProperty(modelName)) {
      if(XSDVocabulary.ALL_DATATYPES.contains(range)) {
        return VariableNameFactory.getVariableName(factory.getOWLDataProperty(modelName),
            XSDHelper.xsdTypes.get(range), action, this);
      }
      else {
        throw new NoSuchDataTypeException(action, range);
      }
    }
    else if(isObjectProperty(modelName)) {
      return VariableNameFactory.getVariableName(factory.getOWLObjectProperty(modelName), factory.getOWLClass(range),
          action, this);
    }
    else {
      throw new NoSuchPropertyException(action, modelName);
    }
  }

  /**
   * <!-- getVariableName -->
   * 
   * @see uk.ac.hutton.obiama.msb.ModelStateBroker#getVariableName(java.net.URI,
   *      uk.ac.hutton.obiama.action.Action)
   */
  public Var getVariableName(URI name, Process process) throws IntegrationInconsistencyException {
    URI modelName = modelEntities.containsKey(name) ? modelEntities.get(name) : name;

    if(isDataProperty(modelName)) {
      return VariableNameFactory.getVariableName(factory.getOWLDataProperty(modelName), process, this);
    }
    else if(isObjectProperty(modelName)) {
      return VariableNameFactory.getVariableName(factory.getOWLObjectProperty(modelName), process, this);
    }
    else {
      throw new NoSuchPropertyException(process, modelName);
    }
  }

  /**
   * <!-- getVariableName -->
   * 
   * @see uk.ac.hutton.obiama.msb.ModelStateBroker#getVariableName(java.net.URI,
   *      java.net.URI, org.semanticweb.owl.vocab.XSDVocabulary,
   *      uk.ac.hutton.obiama.action.Action)
   */
  @Override
  public Var getVariableName(URI name, URI domain, XSDVocabulary range, Process action)
      throws IntegrationInconsistencyException {
    URI modelName = modelEntities.containsKey(name) ? modelEntities.get(name) : name;

    if(isDataProperty(modelName)) {
      return VariableNameFactory.getVariableName(factory.getOWLDataProperty(modelName), factory.getOWLClass(domain),
          range, action, this);
    }
    else {
      throw new NoSuchPropertyException(action, modelName);
    }
  }

  @Override
  public Var getVariableName(URI name, XSDVocabulary range, Process action) throws IntegrationInconsistencyException {
    URI modelName = modelEntities.containsKey(name) ? modelEntities.get(name) : name;

    if(isDataProperty(modelName)) {
      return VariableNameFactory.getVariableName(factory.getOWLDataProperty(modelName), range, action, this);
    }
    else {
      throw new NoSuchPropertyException(action, modelName);
    }
  }

  @Override
  OWLIndividual getIndividual(Process action, URI individual) throws NoSuchIndividualException {
    if(inferredState.containsIndividualReference(individual)) {
      return factory.getOWLIndividual(individual);
    }
    else if(createdInstances.contains(individual)) {
      return factory.getOWLIndividual(individual);
    }
    else {
      for(OWLOntology state: assertedState) {
        if(state.containsIndividualReference(individual)) {
          return factory.getOWLIndividual(individual);
        }
      }
      for(OWLOntology model: assertedModel) {
        if(model.containsIndividualReference(individual)) {
          OWLIndividual ind = factory.getOWLIndividual(individual);
          if(!nonMutableIndividuals.contains(ind)) nonMutableIndividuals.add(ind);
          return ind;
        }
      }
    }
    throw new NoSuchIndividualException(action, individual);
  }

  @Override
  Set<OWLIndividual> getMembers(Process action, URI concept) throws IntegrationInconsistencyException {
    Set<OWLIndividual> members = new HashSet<OWLIndividual>();

    if(!isClass(concept)) {
      throw new NoSuchConceptException(action, concept);
    }

    OWLClass owlClass = factory.getOWLClass(concept);

    // We rely on the reasoner to infer class membership properly.
    members.addAll(owlClass.getIndividuals(inferredState));

    return members;
  }

  @Override
  Set<OWLIndividual> getObjectPropertyValues(OWLIndividual individual, OWLObjectProperty property) {
    Map<OWLObjectPropertyExpression, Set<OWLIndividual>> allProperties =
      individual.getObjectPropertyValues(inferredState);
    return allProperties.get(property);
  }

  @Override
  Set<String> getDataPropertyValues(OWLIndividual individual, OWLDataProperty property) {
    Map<OWLDataPropertyExpression, Set<OWLConstant>> allProperties = individual.getDataPropertyValues(inferredState);
    Set<OWLConstant> values = allProperties.get(property);
    if(values == null) return null;
    Set<String> literals = new LinkedHashSet<String>();
    for(OWLConstant value: values) {
      literals.add(value.getLiteral());
    }
    return literals;
  }

  @Override
  void addObjectPropertyAssertionValue(Action action, OWLIndividual subject, OWLObjectProperty property,
      OWLIndividual object) throws IntegrationInconsistencyException {
    if(subject == null || property == null || object == null) {
      ErrorHandler.warn("null entity", "adding object property assertion "
        + (subject == null ? "(null subject)" : subject.getURI().toString()) + " "
        + (property == null ? "(null property)" : property.getURI().toString()) + " "
        + (object == null ? "(null object)" : object.getURI().toString()) + " in action " + action.getURI(),
          "the axiom won't be added");
      return;
    }
    OWLObjectPropertyAssertionAxiom axiom = factory.getOWLObjectPropertyAssertionAxiom(subject, property, object);
    addAxioms.add(axiom);
    actionsAddingAxioms.put(axiom, action);
  }

  @Override
  void removeObjectPropertyAssertionValue(Action action, OWLIndividual subject, OWLObjectProperty property,
      OWLIndividual object) throws IntegrationInconsistencyException {
    if(nonMutableIndividuals.contains(subject)) {
      throw new ChangeToNonMutableIndividualException(action, property, subject);
    }
    OWLObjectPropertyAssertionAxiom axiom = factory.getOWLObjectPropertyAssertionAxiom(subject, property, object);
    removeAxioms.add(axiom);
    actionsRemovingAxioms.put(axiom, action);
  }

  @Override
  <T> void addDataPropertyAssertionValue(Action action, OWLIndividual subject, OWLDataProperty property, T value,
      XSDVocabulary type) throws IntegrationInconsistencyException {
    OWLDataType datatype = factory.getOWLDataType(type.getURI());
    OWLTypedConstant constant = factory.getOWLTypedConstant(value.toString(), datatype);
    OWLDataPropertyAssertionAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(subject, property, constant);
    addAxioms.add(axiom);
    actionsAddingAxioms.put(axiom, action);
  }

  @Override
  <T> void removeDataPropertyAssertionValue(Action action, OWLIndividual subject, OWLDataProperty property, T value,
      XSDVocabulary type) throws IntegrationInconsistencyException {
    if(nonMutableIndividuals.contains(subject)) {
      throw new ChangeToNonMutableIndividualException(action, property, subject);
    }
    OWLDataType datatype = factory.getOWLDataType(type.getURI());
    OWLTypedConstant constant = factory.getOWLTypedConstant(value.toString(), datatype);
    OWLDataPropertyAssertionAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(subject, property, constant);
    removeAxioms.add(axiom);
    actionsRemovingAxioms.put(axiom, action);
  }

  /**
   * <!-- addClassAssertion -->
   * 
   * @see uk.ac.hutton.obiama.msb.AbstractModelStateBroker#addClassAssertion(uk.ac.hutton.obiama.action.Action,
   *      org.semanticweb.owl.model.OWLIndividual,
   *      org.semanticweb.owl.model.OWLClass)
   * @param action
   * @param subject
   * @param concept
   * @throws IntegrationInconsistencyException
   */
  @Override
  void addClassAssertion(Action action, AbstractInstance instance, Concept concept)
      throws IntegrationInconsistencyException {
    addClassAssertion(action, instance.getURI(), concept.getURI());
  }

  @Override
  void addClassAssertion(Action action, URI instanceURI, URI conceptURI) throws IntegrationInconsistencyException {
    OWLIndividual subject = factory.getOWLIndividual(instanceURI);
    OWLClass owlClass = factory.getOWLClass(conceptURI);
    if(nonMutableIndividuals.contains(subject)) {
      throw new ChangeToNonMutableIndividualException(action, conceptURI, instanceURI);
    }
    OWLClassAssertionAxiom axiom = factory.getOWLClassAssertionAxiom(subject, owlClass);
    addAxioms.add(axiom);
    actionsAddingAxioms.put(axiom, action);
  }

  @Override
  void createInstance(Action action, AbstractInstance instance, Concept concept)
      throws IntegrationInconsistencyException {
    addClassAssertion(action, instance.getURI(), concept.getURI());
    createdInstances.add(instance.getURI());
  }

  /**
   * <!-- removeClassAssertion -->
   * 
   * @see uk.ac.hutton.obiama.msb.AbstractModelStateBroker#removeClassAssertion(uk.ac.hutton.obiama.action.Action,
   *      org.semanticweb.owl.model.OWLIndividual,
   *      org.semanticweb.owl.model.OWLClass)
   * @param action
   * @param subject
   * @param concept
   * @throws IntegrationInconsistencyException
   */
  @Override
  void removeClassAssertion(Action action, AbstractInstance instance, Concept concept)
      throws IntegrationInconsistencyException {
    removeClassAssertion(action, instance.getURI(), concept.getURI());
  }

  @Override
  void removeClassAssertion(Action action, URI instanceURI, URI conceptURI) throws IntegrationInconsistencyException {
    OWLIndividual subject = factory.getOWLIndividual(instanceURI);
    OWLClass owlClass = factory.getOWLClass(conceptURI);
    if(nonMutableIndividuals.contains(subject)) {
      throw new ChangeToNonMutableIndividualException(action, conceptURI, instanceURI);
    }
    OWLClassAssertionAxiom axiom = factory.getOWLClassAssertionAxiom(subject, owlClass);
    removeAxioms.add(axiom);
    actionsRemovingAxioms.put(axiom, action);
  }

  private Set<OWLAxiom> getAllAxioms(OWLIndividual individual) {
    Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
    for(OWLOntology ontology: assertedState) {
      axioms.addAll(ontology.getReferencingAxioms(individual));
    }
    return axioms;
  }

  @Override
  void deleteIndividual(Action action, URI individualURI) throws IntegrationInconsistencyException {
    OWLIndividual individual = factory.getOWLIndividual(individualURI);
    if(nonMutableIndividuals.contains(individual)) {
      throw new ChangeToNonMutableIndividualException(action, individualURI);
    }
    for(OWLAxiom axiom: getAllAxioms(individual)) {
      if(axiom instanceof OWLClassAssertionAxiom || axiom instanceof OWLDataPropertyAssertionAxiom
        || axiom instanceof OWLObjectPropertyAssertionAxiom) {
        removeAxioms.add(axiom);
        actionsRemovingAxioms.put(axiom, action);
      }
    }
  }

  @Override
  void killIndividual(Action action, URI individualURI) throws IntegrationInconsistencyException {
    OWLIndividual individual = factory.getOWLIndividual(individualURI);
    if(nonMutableIndividuals.contains(individual)) {
      throw new ChangeToNonMutableIndividualException(action, individualURI);
    }
    Set<OWLAxiom> axioms = getAllAxioms(individual);
    boolean wasAgent = false;
    for(OWLAxiom axiom: axioms) {
      wasAgent |= killIndividualAxiom(action, individual, axiom);
    }
    if(wasAgent) {
      OWLClassAssertionAxiom axiom =
        factory.getOWLClassAssertionAxiom(individual, factory.getOWLClass(ObiamaOntology.EX_AGENT_URI));
      addAxioms.add(axiom);
      actionsAddingAxioms.put(axiom, action);
    }
  }

  private boolean killIndividualAxiom(Action action, OWLIndividual individual, OWLAxiom axiom)
      throws IntegrationInconsistencyException {
    if(axiom instanceof OWLClassAssertionAxiom) return killIndividualAxiom(action, individual,
        (OWLClassAssertionAxiom)axiom);
    else if(axiom instanceof OWLDataPropertyAssertionAxiom) return killIndividualAxiom(action, individual,
        (OWLDataPropertyAssertionAxiom)axiom);
    else if(axiom instanceof OWLObjectPropertyAssertionAxiom)
      return killIndividualAxiom(action, individual, (OWLObjectPropertyAssertionAxiom)axiom);
    return false;
  }

  private boolean killIndividualAxiom(Action action, OWLIndividual individual, OWLClassAssertionAxiom axiom)
      throws DeathOfNonAgentException {
    OWLDescription desc = axiom.getDescription();
    if(!desc.isAnonymous()) {
      OWLClass owlClass = desc.asOWLClass();

      Set<URI> classURIs = getSuperOrEquivalentEntities(owlClass.getURI());
      if(classURIs.contains(ObiamaOntology.EX_AGENT_URI)) {
        throw new DeathOfDeadAgentException(action, individual.getURI());
      }
      if(classURIs.contains(ObiamaOntology.AGENT_URI)) {
        removeAxioms.add(axiom);
        actionsRemovingAxioms.put(axiom, action);
        OWLDataProperty wasA = factory.getOWLDataProperty(ObiamaOntology.WAS_A_URI);
        OWLTypedConstant classURI =
          factory.getOWLTypedConstant(owlClass.getURI().toString(),
              factory.getOWLDataType(XSDVocabulary.ANY_URI.getURI()));
        OWLDataPropertyAssertionAxiom wasAAxiom = factory.getOWLDataPropertyAssertionAxiom(individual, wasA, classURI);
        addAxioms.add(wasAAxiom);
        actionsAddingAxioms.put(wasAAxiom, action);
        return true;
      }
      else {
        throw new DeathOfNonAgentException(action, individual.getURI());
      }
    }
    return false;
  }

  private boolean killIndividualAxiom(Action action, OWLIndividual individual, OWLDataPropertyAssertionAxiom axiom) {
    OWLDataPropertyExpression expr = axiom.getProperty();
    Set<OWLDataPropertyExpression> props = getAllAssertedSuperPropertiesOf(expr);
    props.addAll(expr.getEquivalentProperties(modelSearch));
    if(props.contains(factory.getOWLDataProperty(ObiamaOntology.AGENT_DATA_PROPERTY_URI))) {
      removeAxioms.add(axiom);
      actionsRemovingAxioms.put(axiom, action);
    }
    return false;
  }

  private boolean killIndividualAxiom(Action action, OWLIndividual individual, OWLObjectPropertyAssertionAxiom axiom)
      throws IntegrationInconsistencyException {
    OWLObjectPropertyExpression expr = axiom.getProperty();
    Set<OWLObjectPropertyExpression> props = getAllAssertedSuperPropertiesOf(expr);
    props.addAll(expr.getEquivalentProperties(modelSearch));
    if(props.contains(factory.getOWLObjectProperty(ObiamaOntology.PART_OF_URI))) {
      // TODO need to check that getting the superproperties retains any inverse
      // of part-of...
      // That is to say, is the expression inv(prop) a subproperty of part-of,
      // or is the expression a subproperty of inv(part-of)
      // In the former case, we still want to kill the individual. In the
      // latter, we don't want part-of listed in the superproperties.
      killIndividual(action, axiom.getObject().getURI());
    }
    if(props.contains(factory.getOWLObjectProperty(ObiamaOntology.AGENT_OBJECT_PROPERTY_URI))) {
      removeAxioms.add(axiom);
      actionsRemovingAxioms.put(axiom, action);
    }
    return false;
  }

  @Override
  <T> AbstractValue<T> registerValue(AbstractValue<T> value) throws IntegrationInconsistencyException {
    URI propertyURI = value.getVar().getURI();
    Set<URI> equivalentProperties =
      equivalentEntities.containsKey(propertyURI) ? equivalentEntities.get(propertyURI) : Collections
          .singleton(propertyURI);
    URI individualURI = value.getIndividual();
    if(lockedValues.containsKey(propertyURI)) {
      if(lockedValues.get(propertyURI).containsKey(individualURI))
        return new ShellValue<T>(value.getAbstractVar().process, lockedValues.get(propertyURI).get(individualURI));
    }
    else {
      for(URI equivURI: equivalentProperties) {
        lockedValues.put(equivURI, new HashMap<URI, AbstractValue<?>>());
      }
    }
    values.add(value);
    for(URI equivURI: equivalentProperties) {
      lockedValues.get(equivURI).put(individualURI, value);
    }
    return value;
  }

  @Override
  AbstractInstance registerInstance(AbstractInstance instance) {
    if(lockedInstances.containsKey(instance.getURI())) return lockedInstances.get(instance.getURI());
    // TODO should the above return a ShellInstance, just like registerValue
    // does with ShellValue?
    // i.e. return new ShellInstance(instance.getProcess(),
    // lockedInstances.get(instance.getURI()))?
    // This would mean MSBInstance had to implement static 'manifest' methods,
    // like the Value hierarchy, and registerInstance() call taken out of
    // MSBInstance's constructor.
    lockedInstances.put(instance.getURI(), instance);
    instances.add(instance);
    return instance;
  }

  public void updateCreators() throws IntegrationInconsistencyException {
    // Get all the values belonging to Creators and call their update() methods
    // to get their axioms
    Set<AbstractValue<?>> creatorValues = new HashSet<AbstractValue<?>>();
    for(AbstractValue<?> value: values) {
      if(value.getProcess() instanceof Creator) {
        creatorValues.add(value);
        value.update(this);
      }
    }
    values.removeAll(creatorValues);

    // Get all the instances belonging to this action and call their update()
    // methods to get their axioms
    Set<AbstractInstance> creatorInstances = new HashSet<AbstractInstance>();
    for(AbstractInstance instance: instances) {
      if(instance.getProcess() instanceof Creator) {
        creatorInstances.add(instance);
        instance.update(this);
      }
    }
    instances.removeAll(creatorInstances);

    // Update the axioms
    int nRemoved = removeAxioms();
    int nAdded = addAxioms();
    inferState();
    saveState();

    // Manage locked values and instances
    for(AbstractValue<?> value: creatorValues) {
      URI propertyURI = value.getAbstractVar().getURI();
      URI individualURI = value.getIndividual();
      if(lockedValues.containsKey(propertyURI) && lockedValues.get(propertyURI).containsKey(individualURI)) {
        lockedValues.get(propertyURI).remove(individualURI);
        if(lockedValues.get(propertyURI).size() == 0) lockedValues.remove(propertyURI);
      }
    }

    for(AbstractInstance instance: creatorInstances) {
      if(lockedInstances.containsKey(instance.getURI())) {
        lockedInstances.remove(instance.getURI());
      }
    }

    createdInstances.clear();
    Log.update(nRemoved, nAdded);
  }

  public void update() throws IntegrationInconsistencyException {
    // TODO Decide whether axioms to add should be edited for deleted and killed
    // individuals and if so, edit them.
    // Call the registered values to get property assertion axiom changes
    for(AbstractValue<?> value: values) {
      value.update(this);
    }
    values.clear();

    // Call the registered instances to get class assertion axiom changes
    for(AbstractInstance instance: instances) {
      instance.update(this);
    }
    instances.clear();

    // Update the axioms
    int nRemoved = removeAxioms();
    int nAdded = addAxioms();
    inferState();
    saveState();

    // Manage locked values and instances
    lockedValues = new HashMap<URI, Map<URI, AbstractValue<?>>>();
    lockedInstances = new HashMap<URI, AbstractInstance>();

    createdInstances.clear();
    Log.update(nRemoved, nAdded);
  }

  /**
   * <!-- saveState -->
   * 
   */
  private void saveState() {
    // Save the ontology
    if(nextSaveDir == null) nextSaveDir = getStateDir();
    String dir = nextSaveDir;
    if(saveState(dir) == null) { // i.e. there were no exceptions
      if(prevSaveDir != null) saveStateChain(prevSaveDir, dir);
      prevSaveDir = dir;
      nextSaveDir = getStateDir();
    }
  }

  /**
   * <!-- inferState -->
   * 
   */
  private void inferState() {
    // Do the inference
    Set<OWLOntology> inferFrom = new HashSet<OWLOntology>(assertedModel);
    inferFrom.addAll(assertedState);
    manager.removeOntology(inferredState.getURI());
    URI inferredStateURI = getInferredStateURI();
    try {
      inferredState = manager.createOntology(inferredStateURI);
    }
    catch(OWLOntologyCreationException e1) {
      ErrorHandler.fatal(e1, "creating inferred ontology " + inferredStateURI);
    }
    InferredOntologyCreator.inferIndividuals(manager, inferredState, inferFrom);
  }

  /**
   * <!-- addAxioms -->
   * 
   */
  private int addAxioms() {
    // Implement axiom additions
    List<AddAxiom> axiomsToAdd = new LinkedList<AddAxiom>();
    for(OWLAxiom axiom: addAxioms) {
      Action action = actionsAddingAxioms.get(axiom);
      OWLCommentAnnotation note = factory.getCommentAnnotation(action.getClass().getName());
      OWLAxiomAnnotationAxiom noteAxiom = factory.getOWLAxiomAnnotationAxiom(axiom, note);
      // REALLY, THIS SUGGESTS WE SHOULD HAVE JUST ONE assertedState...
      // ... unless we can think of an intelligent way to handle it.
      if(assertedState.size() == 0) {
        // Got no ontologies to add the assertion to!
        throw new Bug();
      }
      else if(assertedState.size() == 1) {
        // The best case scenario... there's just one asserted state!
        for(OWLOntology state: assertedState) {
          axiomsToAdd.add(new AddAxiom(state, axiom));
          axiomsToAdd.add(new AddAxiom(state, noteAxiom));
        }
      }
      else {
        // There are various ways we could have chosen which ontology to add to.
        // Here we try to find an ontology with a URI matching the subject.

        OWLIndividual subject;
        if(axiom instanceof OWLClassAssertionAxiom) {
          subject = ((OWLClassAssertionAxiom)axiom).getIndividual();
        }
        else if(axiom instanceof OWLDataPropertyAssertionAxiom) {
          subject = ((OWLDataPropertyAssertionAxiom)axiom).getSubject();
        }
        else if(axiom instanceof OWLObjectPropertyAssertionAxiom) {
          subject = ((OWLObjectPropertyAssertionAxiom)axiom).getSubject();
        }
        else {
          throw new Bug();
        }

        // See if we can find a state with a URI matching that of the subject
        OWLOntology ontoAdd = null;
        for(OWLOntology state: assertedState) {
          if(state.getURI().relativize(subject.getURI()).toString().equals("#" + subject.getURI().getFragment())) {
            // relativize will return the fragment if the subject and the
            // ontology otherwise have the same URI
            ontoAdd = state;
            break;
          }
        }

        if(ontoAdd == null) {
          // See if we can find a state that imports a model with a URI matching
          // that of the subject
          for(OWLOntology model: assertedModel) {
            if(model.getURI().relativize(subject.getURI()).toString().equals("#" + subject.getURI().getFragment())) {
              for(OWLOntology state: assertedState) {
                if(state.getImports(manager).contains(model)) {
                  ontoAdd = state;
                  break;
                }
              }
              break;
            }
          }

          // Deal with the case when we couldn't...

          if(ontoAdd == null && ObiamaSetUp.getInitialScheduleInstanceURI() != null) {
            // Use the initial asserted state ontology
            ontoAdd = manager.getOntology(URI.create(ObiamaSetUp.getInitialScheduleInstanceURI()));
          }

          if(ontoAdd == null) {
            // Just use the first state ontology
            for(OWLOntology state: assertedState) {
              ontoAdd = state;
              break;
            }
          }
        }

        axiomsToAdd.add(new AddAxiom(ontoAdd, axiom));
        axiomsToAdd.add(new AddAxiom(ontoAdd, noteAxiom));

      }
    }
    try {
      List<OWLOntologyChange> list = manager.applyChanges(axiomsToAdd);
      for(OWLOntologyChange ch: list) {
        OWLAxiom axiom = ch.getAxiom();

        if(actionsAddingAxioms.containsKey(axiom)) {
          Action action = actionsAddingAxioms.get(axiom);
          String time = Double.toString(model.getTimeStep());
          URI inOntology = getSavePhysicalURI(ch.getOntology().getURI(), prevSaveDir);
          URI outOntology = getSavePhysicalURI(ch.getOntology().getURI(), nextSaveDir);

          if(axiom instanceof OWLClassAssertionAxiom) {
            OWLClassAssertionAxiom ca = (OWLClassAssertionAxiom)axiom;
            provenance.recordAssertion(action, inOntology, outOntology, time, ca.getIndividual().getURI(),
                OWLXMLVocabulary.CLASS_ASSERTION.getURI(), ca.getDescription().asOWLClass().getURI());
          }
          else if(axiom instanceof OWLObjectPropertyAssertionAxiom) {
            OWLObjectPropertyAssertionAxiom opa = (OWLObjectPropertyAssertionAxiom)axiom;
            provenance.recordAssertion(action, inOntology, outOntology, time, opa.getSubject().getURI(), opa
                .getProperty().asOWLObjectProperty().getURI(), opa.getObject().getURI());
          }
          else if(axiom instanceof OWLDataPropertyAssertionAxiom) {
            OWLDataPropertyAssertionAxiom dpa = (OWLDataPropertyAssertionAxiom)axiom;
            OWLConstant obj = dpa.getObject();
            if(obj.isTyped()) {
              provenance.recordAssertion(action, inOntology, outOntology, time, dpa.getSubject().getURI(), dpa
                  .getProperty().asOWLDataProperty().getURI(), obj.getLiteral(),
                  XSDHelper.xsdTypes.get(obj.asOWLTypedConstant().getDataType().getURI()));
            }
            else {
              provenance.recordAssertion(action, inOntology, outOntology, time, dpa.getSubject().getURI(), dpa
                  .getProperty().asOWLDataProperty().getURI(), obj.getLiteral(), XSDVocabulary.ANY_TYPE);
            }
          }
        }

        Log.addedAxiom(axiom.toString(), ch.getOntology().getURI());
      }
      addAxioms.clear();
      return list.size();
    }
    catch(OWLOntologyChangeException e) {
      ErrorHandler.fatal(e, "applying changes to the state ontology");
      throw new Bug();
    }
  }

  /**
   * <!-- removeAxioms -->
   * 
   * @throws CannotRemoveInferredAxiomException
   */
  private int removeAxioms() throws CannotRemoveInferredAxiomException {
    // Implement axiom removals
    List<RemoveAxiom> axiomsToRemove = new LinkedList<RemoveAxiom>();
    for(OWLAxiom axiom: removeAxioms) {
      boolean ontologyFound = false;
      for(OWLOntology state: assertedState) {
        if(state.containsAxiom(axiom)) {
          axiomsToRemove.add(new RemoveAxiom(state, axiom));
          ontologyFound = true;
        }
      }
      if(!ontologyFound) {
        if(inferredState.containsAxiom(axiom)) {
          int count = axiomsToRemove.size();
          axiomsToRemove.addAll(removeAxiomsEquivalentTo(axiom));
          if(axiomsToRemove.size() == count) {
            // Nick and I decided in a meeting on 17 August 2010 that if this
            // didn't remove the offending axiom, then there is an inconsistency
            throw new CannotRemoveInferredAxiomException(actionsRemovingAxioms.get(axiom), axiom.toString());
          }
        }
        else {
          throw new Bug("Cannot find ontology to remove axiom " + axiom);
        }
      }
    }
    try {
      List<OWLOntologyChange> list = manager.applyChanges(axiomsToRemove);
      for(OWLOntologyChange ch: list) {
        OWLAxiom axiom = ch.getAxiom();

        if(actionsRemovingAxioms.containsKey(axiom)) {
          Action action = actionsRemovingAxioms.get(axiom);
          String time = Double.toString(model.getTimeStep());
          URI inOntology = getSavePhysicalURI(ch.getOntology().getURI(), prevSaveDir);
          URI outOntology = getSavePhysicalURI(ch.getOntology().getURI(), nextSaveDir);

          if(axiom instanceof OWLClassAssertionAxiom) {
            OWLClassAssertionAxiom ca = (OWLClassAssertionAxiom)axiom;
            provenance.recordRetraction(action, inOntology, outOntology, time, ca.getIndividual().getURI(),
                OWLXMLVocabulary.CLASS_ASSERTION.getURI(), ca.getDescription().asOWLClass().getURI());
          }
          else if(axiom instanceof OWLObjectPropertyAssertionAxiom) {
            OWLObjectPropertyAssertionAxiom opa = (OWLObjectPropertyAssertionAxiom)axiom;
            provenance.recordRetraction(action, inOntology, outOntology, time, opa.getSubject().getURI(), opa
                .getProperty().asOWLObjectProperty().getURI(), opa.getObject().getURI());
          }
          else if(axiom instanceof OWLDataPropertyAssertionAxiom) {
            OWLDataPropertyAssertionAxiom dpa = (OWLDataPropertyAssertionAxiom)axiom;
            OWLConstant obj = dpa.getObject();
            if(obj.isTyped()) {
              provenance.recordRetraction(action, inOntology, outOntology, time, dpa.getSubject().getURI(), dpa
                  .getProperty().asOWLDataProperty().getURI(), obj.getLiteral(),
                  XSDHelper.xsdTypes.get(obj.asOWLTypedConstant().getDataType().getURI()));
            }
            else {
              provenance.recordRetraction(action, inOntology, outOntology, time, dpa.getSubject().getURI(), dpa
                  .getProperty().asOWLDataProperty().getURI(), obj.getLiteral(), XSDVocabulary.ANY_TYPE);
            }
          }
        }

        Log.removedAxiom(axiom.toString(), ch.getOntology().getURI());
      }
      removeAxioms.clear();
      return list.size();
    }
    catch(OWLOntologyChangeException e) {
      ErrorHandler.fatal(e, "applying changes to the state ontology");
      throw new Bug();
    }
  }

  /**
   * <!-- getInferredStateURI -->
   * 
   * @return A logical URI to use for inferred states.
   */
  private URI getInferredStateURI() {
    URI baseURI = getBaseURI();
    String[] basePath = baseURI.toString().split("/");
    StringBuffer inferredURI = new StringBuffer();
    for(int i = 0; i < basePath.length - 1; i++) {
      if(i > 0) inferredURI.append("/");
      if(basePath[i] != null) inferredURI.append(basePath[i]);
    }
    inferredURI.append("/");
    inferredURI.append("Inferred-");
    inferredURI.append(RunID.getRunID());
    inferredURI.append("-T");
    inferredURI.append(model.getTimeStep());
    inferredURI.append(".owl");

    try {
      return new URI(inferredURI.toString());
    }
    catch(URISyntaxException e) {
      return AnonymousURI.createAnonymousURI("Inferred");
    }
  }

  /**
   * <!-- saveStateChain -->
   * 
   * Save information linking two directories together.
   * 
   * @param prev Previous directory
   * @param next Next directory (may be null, in which case, nothing happens)
   */
  private void saveStateChain(String prev, String next) {
    if(next == null) return;

    File prevDir = new File(prev);
    File nextDir = new File(next);

    if(prevDir.exists() && prevDir.isDirectory() && nextDir.exists() && nextDir.isDirectory()) {
      try {
        String nextName = prev + System.getProperty("file.separator") + NEXT_STATE_CHAIN_FILE;
        PrintWriter nextWriter = new PrintWriter(new BufferedWriter(new FileWriter(nextName)));
        String prevName = next + System.getProperty("file.separator") + PREV_STATE_CHAIN_FILE;
        PrintWriter prevWriter = new PrintWriter(new BufferedWriter(new FileWriter(prevName)));
        nextWriter.println(nextDir.getAbsolutePath());
        prevWriter.println(prevDir.getAbsolutePath());
        nextWriter.close();
        prevWriter.close();
      }
      catch(IOException e) {
        ErrorHandler.warn(e, "saving state chain", "saved state ontologies will not have links recorded");
      }

    }
  }

  /**
   * <!-- getStateDir -->
   * 
   * Get a directory to save the state to. This can return <code>null</code>. If
   * the directory exists, an increment is used to find a non-existent directory
   * name.
   * 
   * @return The name of a directory to save the state to, or <code>null</code>
   *         if the user has not specified any such name.
   */
  private String getStateDir() {
    String topdir = ObiamaSetUp.getSaveDir();
    if(topdir == null) return null;

    StringBuffer dir = new StringBuffer(topdir);
    dir.append(File.separator);
    dir.append(SAVED_STATE_PREFIX);
    dir.append(model.getTimeStep());
    File file = new File(dir.toString());
    int i = 0;
    while(file.exists()) {
      file = new File(dir.toString() + "-" + i);
      i++;
    }
    if(i > 0) {
      dir.append("-");
      dir.append(i);
    }
    return dir.toString();
  }

  /**
   * <!-- saveState -->
   * 
   * Saves the state to a specified directory.
   */
  public Exception saveState(String directory) {
    if(directory == null) return null;
    File dir = new File(directory);
    if(!dir.exists()) {
      try {
        dir.mkdirs();
      }
      catch(SecurityException e) {
        Log.saveOntologyFail(null, dir.toURI(), e);
        return e;
      }
    }
    for(OWLOntology ontology: assertedState) {
      Exception e = saveOntology(ontology, directory);
      if(e != null) return e;
    }
    if(ObiamaSetUp.getSaveInferred()) {
      Exception e = saveOntology(inferredState, directory);
      if(e != null) return e;
    }
    try {
      if(ObiamaSetUp.getHistoryProvenanceURI() != null) {
        URI provenanceLoc = getSavePhysicalURI(URI.create(ObiamaSetUp.getHistoryProvenanceURI()), directory);
        provenance.saveHistoryProvenanceOntology(provenanceLoc);
      }
    }
    catch(UsageException e) {
      ErrorHandler.redo(e, "saving history provenance");
    }
    return null;
  }

  private Exception saveOntology(OWLOntology ontology, String directory) {
    URI logicalURI = ontology.getURI();
    URI physicalURI = getSavePhysicalURI(logicalURI, directory);
    try {
      manager.saveOntology(ontology, new RDFXMLOntologyFormat(), physicalURI);
    }
    catch(UnknownOWLOntologyException e) {
      throw new Bug();
    }
    catch(OWLOntologyStorageException e) {
      Log.saveOntologyFail(logicalURI, physicalURI, e);
      return e;
    }
    Log.saveOntologySuccess(logicalURI, physicalURI);
    return null;
  }

  private URI getSavePhysicalURI(URI logicalURI, String directory) {
    if(directory == null) return null;
    String[] logicalPath = logicalURI.getPath().split("/");
    String logicalName = logicalPath[logicalPath.length - 1];
    File file = new File(directory + File.separator + logicalName);
    return file.toURI();
  }

  /**
   * <!-- getBaseURI -->
   * 
   * Could possibly handle this more intelligently--e.g. returning the URI of an
   * ontology everything imports
   * 
   * @see uk.ac.hutton.obiama.msb.ModelStateBroker#getBaseURI()
   */
  public URI getBaseURI() {
    return ObiamaSetUp.getOntologyURI();
  }

  /**
   * <!-- reset -->
   * 
   * @see uk.ac.hutton.obiama.msb.ModelStateBroker#reset()
   */
  public void reset() {
    for(OWLOntology state: assertedState) {
      manager.removeOntology(state.getURI());
    }
    assertedState.clear();
    manager.removeOntology(inferredState.getURI());
    try {
      inferredState = manager.createOntology(AnonymousURI.createAnonymousURI());
    }
    catch(OWLOntologyCreationException e) {
      throw new Bug();
    }
    removeAxioms.clear();
    addAxioms.clear();
    values.clear();
    instances.clear();
    actionsAddingAxioms.clear();
    actionsRemovingAxioms.clear();
    lockedValues.clear();
    lockedInstances.clear();
    provenance = ProvenanceFactory.getProvenance();
    RunID.reset();
    Log.reset(RunID.getRunID());
  }

  private Set<RemoveAxiom> removeAxiomsEquivalentTo(OWLAxiom axiom) {
    if(axiom instanceof OWLDataPropertyAssertionAxiom) {
      return removeAxiomsEquivalentTo((OWLDataPropertyAssertionAxiom)axiom);
    }
    else if(axiom instanceof OWLObjectPropertyAssertionAxiom) {
      return removeAxiomsEquivalentTo((OWLObjectPropertyAssertionAxiom)axiom);
    }
    else if(axiom instanceof OWLClassAssertionAxiom) {
      return removeAxiomsEquivalentTo((OWLClassAssertionAxiom)axiom);
    }
    else
      return new HashSet<RemoveAxiom>();
  }

  private Set<RemoveAxiom> removeAxiomsEquivalentTo(OWLDataPropertyAssertionAxiom axiom) {
    System.out.println("Trying to find axioms equivalent to " + axiom);
    OWLDataPropertyExpression expr = axiom.getProperty();
    Set<RemoveAxiom> equivAxioms = new HashSet<RemoveAxiom>();
    if(expr.isAnonymous()) return equivAxioms;
    OWLDataProperty prop = expr.asOWLDataProperty();
    Set<URI> equivs = equivalentEntities.get(prop.getURI());
    System.out.println("Looking through " + equivs.size() + " properties equivalent to " + prop
      + "; inferred model structure ontology has " + inferredModel.getAxioms().size() + " axioms");
    for(URI equiv: equivs) {
      System.out.println(equiv + " is equivalent to " + prop);
      OWLDataPropertyAssertionAxiom equivAxiom =
        factory.getOWLDataPropertyAssertionAxiom(axiom.getSubject(), factory.getOWLDataProperty(equiv),
            axiom.getObject());
      for(OWLOntology ontology: assertedState) {
        if(ontology.containsAxiom(equivAxiom)) {
          equivAxioms.add(new RemoveAxiom(ontology, equivAxiom));
        }
      }
    }
    return equivAxioms;
  }

  private Set<RemoveAxiom> removeAxiomsEquivalentTo(OWLObjectPropertyAssertionAxiom axiom) {
    OWLObjectPropertyExpression expr = axiom.getProperty();
    Set<RemoveAxiom> equivAxioms = new HashSet<RemoveAxiom>();
    if(expr.isAnonymous()) return equivAxioms;
    OWLObjectProperty prop = expr.asOWLObjectProperty();
    Set<OWLObjectPropertyExpression> equivs = prop.getEquivalentProperties(modelSearch);
    for(OWLObjectPropertyExpression equiv: equivs) {
      if(equiv.isAnonymous()) continue;
      OWLObjectPropertyAssertionAxiom equivAxiom =
        factory.getOWLObjectPropertyAssertionAxiom(axiom.getSubject(), equiv, axiom.getObject());
      for(OWLOntology ontology: assertedState) {
        if(ontology.containsAxiom(equivAxiom)) {
          equivAxioms.add(new RemoveAxiom(ontology, equivAxiom));
        }
      }
    }
    return equivAxioms;
  }

  private Set<RemoveAxiom> removeAxiomsEquivalentTo(OWLClassAssertionAxiom axiom) {
    OWLDescription desc = axiom.getDescription();
    Set<RemoveAxiom> equivAxioms = new HashSet<RemoveAxiom>();
    if(desc.isAnonymous()) return equivAxioms;
    OWLClass cls = desc.asOWLClass();
    Set<OWLDescription> equivs = cls.getEquivalentClasses(modelSearch);
    for(OWLDescription equiv: equivs) {
      if(equiv.isAnonymous()) continue;
      OWLClassAssertionAxiom equivAxiom = factory.getOWLClassAssertionAxiom(axiom.getIndividual(), equiv);
      for(OWLOntology ontology: assertedState) {
        if(ontology.containsAxiom(equivAxiom)) {
          equivAxioms.add(new RemoveAxiom(ontology, equivAxiom));
        }
      }
    }
    return equivAxioms;
  }
}
