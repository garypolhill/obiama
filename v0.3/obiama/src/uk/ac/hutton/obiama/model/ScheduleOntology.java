/*
 * uk.ac.hutton.obiama.model: ScheduleOntology.java
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyStorageException;
import org.semanticweb.owl.model.UnknownOWLOntologyException;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.action.ProcessFactory;
import uk.ac.hutton.obiama.action.Query;
import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ModelStructureOntologyException;
import uk.ac.hutton.obiama.exception.NoSuchProcessImplementationException;
import uk.ac.hutton.obiama.exception.OntologyConfigurationException;
import uk.ac.hutton.obiama.exception.ScheduleException;
import uk.ac.hutton.obiama.exception.UsageException;
import uk.ac.hutton.obiama.msb.ModelStateBroker;

/**
 * ScheduleOntology
 * 
 * <p>
 * Ontology for the schedule. This class is runnable to create the ontology from
 * the command line. The ontology can also be created programmatically.
 * </p>
 * 
 * <p>
 * There are two kinds of schedule. One is a timed schedule, in which actions
 * have a time at which they will occur, and for repeated actions, an interval
 * at which they will recur. Such schedules are suitable for implementing in
 * many ABM platforms. The other kind of schedule has no timings, but specifies
 * an ordering of actions. The latter are suitable for initialisation.
 * </p>
 * 
 * <p>
 * There is no theoretical reason to avoid mixing schedule types in some cases.
 * Specifically, so long as all timed schedules have a timed top-level action
 * group, sub-actions without a specified time can be assumed to inherit their
 * time from the containing action group. Indeed, in the case of sequences,
 * timed sub-actions could conflict with the timings of the sequence. It
 * therefore makes sense to regard a timed sequence as either an action group
 * without a time, but with sub-actions all having timings, or an action group
 * with a time (and an increment for subsequent actions) and all sub-actions not
 * having timings. Concurrent action groups should likewise expect untimed
 * sub-actions. However, untimed schedules must have all sub-actions without
 * times.
 * </p>
 * 
 * @author Gary Polhill
 */
public class ScheduleOntology extends AbstractOntology {
  /**
   * URI for an OWL ontology to import.
   */
  private URI owlImport = null;

  /**
   * Constructor with logical and physical URIs, and OWL species
   * 
   * @param physical physical URI
   * @param spp species
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   * @throws OWLOntologyStorageException
   */
  public ScheduleOntology(URI physical, OWLSpecies spp) throws OWLOntologyCreationException,
      OWLOntologyChangeException, OWLOntologyStorageException {
    super(ONTOLOGY_URI, physical, spp);
  }

  /**
   * Constructor with logical and physical URIs.
   * 
   * @param physical Physical URI to save the ontology.
   * @throws OWLOntologyCreationException
   * @throws UnknownOWLOntologyException
   * @throws OWLOntologyChangeException
   * @throws OWLOntologyStorageException
   */
  public ScheduleOntology(URI physical) throws OWLOntologyCreationException, UnknownOWLOntologyException,
      OWLOntologyChangeException, OWLOntologyStorageException {
    super(ONTOLOGY_URI, physical);
  }

  /**
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  public ScheduleOntology() throws OWLOntologyCreationException, OWLOntologyChangeException {
    super(ONTOLOGY_URI);
  }

  /**
   * @param ontology
   * @param scheduleOntology
   * @throws OWLOntologyChangeException
   * @throws OWLOntologyCreationException
   */
  public ScheduleOntology(URI ontologyURI, ScheduleOntology scheduleOntology) throws OWLOntologyCreationException,
      OWLOntologyChangeException {
    super(ontologyURI, scheduleOntology);
  }

  /**
   * @param spp
   * @throws OWLOntologyChangeException
   * @throws OWLOntologyCreationException
   */
  public ScheduleOntology(OWLSpecies spp) throws OWLOntologyCreationException, OWLOntologyChangeException {
    super(ONTOLOGY_URI, spp);
  }

  /**
   * anOntology is expected to contain some individual axioms using the
   * scheduleOntology classes; create a ScheduleOntology object importing
   * individuals from that ontology.
   * 
   * @param importIndividualsFromOntology
   * @param scheduleOntology
   * @throws OWLOntologyChangeException
   * @throws OWLOntologyCreationException
   */
  public ScheduleOntology(URI ontologyURI, OWLOntology importIndividualsFromOntology, ScheduleOntology scheduleOntology)
      throws OWLOntologyCreationException, OWLOntologyChangeException {
    super(ontologyURI, importIndividualsFromOntology, scheduleOntology);
  }

  /* Ontology-related constants */

  /**
   * URI of the schedule ontology
   */
  public static final URI ONTOLOGY_URI = URI.create("http://www.hutton.ac.uk/obiama/ontologies/schedule.owl");

  // Classes

  public static final URI ACTION_URI = URI.create(ONTOLOGY_URI + "#Action");
  public static final URI AGENT_URI = URI.create(ONTOLOGY_URI + "#Agent");
  public static final URI QUERY_URI = URI.create(ONTOLOGY_URI + "#Query");
  public static final URI PROCESS_URI = URI.create(ONTOLOGY_URI + "#Process");
  public static final URI INDIVIDUAL_ACTION_URI = URI.create(ONTOLOGY_URI + "#IndividualAction");
  public static final URI ACTION_FOR_EACH_URI = URI.create(ONTOLOGY_URI + "#ActionForEach");
  public static final URI CONCURRENT_ACTION_FOR_EACH_URI = URI.create(ONTOLOGY_URI + "#ConcurrentActionForEach");
  public static final URI RANDOM_ORDER_ACTION_FOR_EACH_URI = URI.create(ONTOLOGY_URI + "#RandomOrderActionForEach");
  public static final URI ASCENDING_ORDER_ACTION_FOR_EACH_URI = URI.create(ONTOLOGY_URI
    + "#AscendingOrderActionForEach");
  public static final URI DESCENDING_ORDER_ACTION_FOR_EACH_URI = URI.create(ONTOLOGY_URI
    + "#DescendingOrderActionForEach");
  public static final URI ACTION_GROUP_URI = URI.create(ONTOLOGY_URI + "#ActionGroup");
  public static final URI CONCURRENT_ACTION_GROUP_URI = URI.create(ONTOLOGY_URI + "#ConcurrentActionGroup");
  public static final URI RECURRENT_ACTION_GROUP_URI = URI.create(ONTOLOGY_URI + "#RecurrentActionGroup");
  public static final URI SEQUENTIAL_ACTION_GROUP_URI = URI.create(ONTOLOGY_URI + "#SequentialActionGroup");
  public static final URI ACTION_PARAMETER_URI = URI.create(ONTOLOGY_URI + "#ActionParameter");
  public static final URI SCHEDULE_URI = URI.create(ONTOLOGY_URI + "#Schedule");
  public static final URI IMPLEMENTATION_URI = URI.create(ONTOLOGY_URI + "#Implementation");
  public static final URI FP_COMPARISON_URI = URI.create(ONTOLOGY_URI + "#FloatingPointComparison");
  public static final URI NON_TIMED_SCHEDULE_URI = URI.create(ONTOLOGY_URI + "#NonTimedSchedule");
  public static final URI TIMED_EVENT_URI = URI.create(ONTOLOGY_URI + "#TimedEvent");
  public static final URI RECURRENT_TIMED_ACTION_GROUP_URI = URI.create(ONTOLOGY_URI + "#RecurrentTimedActionGroup");
  public static final URI REPEATING_ACTION_GROUP_URI = URI.create(ONTOLOGY_URI + "#RepeatingActionGroup");
  public static final URI TIMED_EVENT_SEQUENCE_URI = URI.create(ONTOLOGY_URI + "#TimedEventSequence");
  public static final URI TIMED_SCHEDULE_URI = URI.create(ONTOLOGY_URI + "#TimedSchedule");

  // Data properties

  public static final URI CLASS_NAME_URI = URI.create(ONTOLOGY_URI + "#className");
  public static final URI JAR_FILE_URI = URI.create(ONTOLOGY_URI + "#jarFile");
  public static final URI JAR_FILE_SEARCH_URI = URI.create(ONTOLOGY_URI + "#jarFileSearch");
  public static final URI PARAMETER_VALUE_URI = URI.create(ONTOLOGY_URI + "#parameterValue");
  public static final URI PARAMETER_TYPE_URI = URI.create(ONTOLOGY_URI + "#parameterType");
  public static final URI PARAMETER_NAME_URI = URI.create(ONTOLOGY_URI + "#parameterName");
  public static final URI CLOCK_TICK_URI = URI.create(ONTOLOGY_URI + "#clockTick");
  public static final URI STOP_TIME_URI = URI.create(ONTOLOGY_URI + "#stopTime");
  public static final URI REPETITIONS_URI = URI.create(ONTOLOGY_URI + "#repetitions");
  public static final URI START_TIME_URI = URI.create(ONTOLOGY_URI + "#startTime");
  public static final URI INTERVAL_URI = URI.create(ONTOLOGY_URI + "#interval");
  public static final URI INCREMENT_URI = URI.create(ONTOLOGY_URI + "#increment");
  public static final URI URI_BASE_URI = URI.create(ONTOLOGY_URI + "#uriBase");
  public static final URI URI_EXTENSION_URI = URI.create(ONTOLOGY_URI + "#uriExtension");
  public static final URI QUERY_ID_URI = URI.create(ONTOLOGY_URI + "#queryID");

  // Data/RDF properties

  public static final URI HAS_AGENT_CLASS_URI = URI.create(ONTOLOGY_URI + "#hasAgentClass");
  public static final URI ORDERED_BY_URI = URI.create(ONTOLOGY_URI + "#orderedBy");
  public static final URI COMPARE_PROPERTY_URI = URI.create(ONTOLOGY_URI + "#compareProperty");

  // Object properties

  public static final URI IMPLEMENTED_BY_URI = URI.create(ONTOLOGY_URI + "#implementedBy");
  public static final URI HAS_PARAMETERS_URI = URI.create(ONTOLOGY_URI + "#hasParameters");
  public static final URI HAS_AGENT_URI = URI.create(ONTOLOGY_URI + "#hasAgent");
  public static final URI HAS_CONCURRENT_ACTIONS_URI = URI.create(ONTOLOGY_URI + "#hasConcurrentActions");
  public static final URI HAS_RECURRENT_ACTION_GROUP_URI = URI.create(ONTOLOGY_URI + "#hasRecurrentActionGroup");
  public static final URI HAS_FIRST_ACTION_GROUP_URI = URI.create(ONTOLOGY_URI + "#hasFirstActionGroup");
  public static final URI HAS_NEXT_ACTION_GROUP_URI = URI.create(ONTOLOGY_URI + "#hasNextActionGroup");
  public static final URI HAS_ACTION_GROUP_URI = URI.create(ONTOLOGY_URI + "#hasActionGroup");
  public static final URI PROVENANCE_URI = URI.create(ONTOLOGY_URI + "#provenance");
  public static final URI HAS_COMPARISON_METHOD_URI = URI.create(ONTOLOGY_URI + "#hasComparisonMethod");
  public static final URI USE_FLOATING_POINT_COMPARISON_URI = URI.create(ONTOLOGY_URI + "#useFloatingPointComparison");

  // Individuals

  public static final URI BOOTSTRAPPER_URI = URI.create(ONTOLOGY_URI + "#exogenousAgent");

  /**
   * <!-- buildOntology -->
   * 
   * Build the schedule ontology
   * 
   * @see uk.ac.hutton.obiama.model.AbstractOntology#buildOntology()
   */
  @Override
  protected void buildOntology() {
    imports(FCmpOntology.ONTOLOGY_URI);

    if(owlImport != null) {
      imports(owlImport);
    }
    disjointClasses(AGENT_URI, PROCESS_URI, ACTION_GROUP_URI, ACTION_PARAMETER_URI, SCHEDULE_URI, IMPLEMENTATION_URI,
        FP_COMPARISON_URI);
    disjointClasses(TIMED_EVENT_URI, IMPLEMENTATION_URI);
    disjointClasses(TIMED_EVENT_URI, FP_COMPARISON_URI);
    disjointClasses(TIMED_EVENT_URI, ACTION_PARAMETER_URI);
    disjointClasses(INDIVIDUAL_ACTION_URI, ACTION_FOR_EACH_URI);
    disjointClasses(ACTION_URI, QUERY_URI);
    disjointClasses(CONCURRENT_ACTION_FOR_EACH_URI, RANDOM_ORDER_ACTION_FOR_EACH_URI,
        ASCENDING_ORDER_ACTION_FOR_EACH_URI, DESCENDING_ORDER_ACTION_FOR_EACH_URI);
    disjointClasses(CONCURRENT_ACTION_GROUP_URI, RECURRENT_ACTION_GROUP_URI, SEQUENTIAL_ACTION_GROUP_URI);
    disjointClasses(REPEATING_ACTION_GROUP_URI, RECURRENT_TIMED_ACTION_GROUP_URI);
    disjointClasses(REPEATING_ACTION_GROUP_URI, TIMED_EVENT_URI);
    disjointClasses(TIMED_SCHEDULE_URI, NON_TIMED_SCHEDULE_URI);

    subClassOf(ACTION_URI, PROCESS_URI);
    subClassOf(QUERY_URI, PROCESS_URI);
    subClassOf(INDIVIDUAL_ACTION_URI, ACTION_URI);
    subClassOf(ACTION_FOR_EACH_URI, ACTION_URI);
    subClassOf(CONCURRENT_ACTION_FOR_EACH_URI, ACTION_FOR_EACH_URI);
    subClassOf(RANDOM_ORDER_ACTION_FOR_EACH_URI, ACTION_FOR_EACH_URI);
    subClassOf(ASCENDING_ORDER_ACTION_FOR_EACH_URI, ACTION_FOR_EACH_URI);
    subClassOf(DESCENDING_ORDER_ACTION_FOR_EACH_URI, ACTION_FOR_EACH_URI);
    subClassOf(CONCURRENT_ACTION_GROUP_URI, ACTION_GROUP_URI);
    subClassOf(RECURRENT_ACTION_GROUP_URI, ACTION_GROUP_URI);
    subClassOf(RECURRENT_TIMED_ACTION_GROUP_URI, RECURRENT_ACTION_GROUP_URI, TIMED_EVENT_URI);
    subClassOf(REPEATING_ACTION_GROUP_URI, RECURRENT_ACTION_GROUP_URI);
    subClassOf(SEQUENTIAL_ACTION_GROUP_URI, ACTION_GROUP_URI);
    subClassOf(TIMED_EVENT_SEQUENCE_URI, SEQUENTIAL_ACTION_GROUP_URI, TIMED_EVENT_URI);
    subClassOf(TIMED_SCHEDULE_URI, SCHEDULE_URI, TIMED_EVENT_URI);
    subClassOf(NON_TIMED_SCHEDULE_URI, SCHEDULE_URI);

    subClassOf(TIMED_EVENT_URI, dataAllGERestriction(START_TIME_URI, 0.0));
    subClassOf(RECURRENT_TIMED_ACTION_GROUP_URI, dataAllGTRestriction(INTERVAL_URI, 0.0));
    subClassOf(REPEATING_ACTION_GROUP_URI, dataAllGTRestriction(REPETITIONS_URI, 0));
    subClassOf(TIMED_EVENT_SEQUENCE_URI, dataAllGTRestriction(INCREMENT_URI, 0.0));
    subClassOf(TIMED_SCHEDULE_URI, dataAllGTRestriction(STOP_TIME_URI, 0.0));
    subClassOf(TIMED_SCHEDULE_URI, dataAllGTRestriction(CLOCK_TICK_URI, 0.0));

    equivalentClasses(
        NON_TIMED_SCHEDULE_URI,
        objectIntersectionOf(
            namedClass(SCHEDULE_URI),
            objectAllRestriction(
                HAS_ACTION_GROUP_URI,
                objectIntersectionOf(objectComplementOf(TIMED_EVENT_URI),
                    objectUnionOf(namedClass(ACTION_URI), namedClass(ACTION_GROUP_URI))))));

    equivalentClasses(
        TIMED_SCHEDULE_URI,
        objectIntersectionOf(
            namedClass(SCHEDULE_URI),
            objectAllRestriction(HAS_ACTION_GROUP_URI,
                objectIntersectionOf(namedClass(TIMED_EVENT_URI), objectUnionOf(ACTION_URI, ACTION_GROUP_URI)))));

    equivalentClasses(
        ACTION_FOR_EACH_URI,
        objectIntersectionOf(
            namedClass(ACTION_URI),
            objectUnionOf(CONCURRENT_ACTION_FOR_EACH_URI, RANDOM_ORDER_ACTION_FOR_EACH_URI,
                ASCENDING_ORDER_ACTION_FOR_EACH_URI, DESCENDING_ORDER_ACTION_FOR_EACH_URI)));

    equivalentClasses(ACTION_URI, objectUnionOf(INDIVIDUAL_ACTION_URI, ACTION_FOR_EACH_URI));

    equivalentClasses(ACTION_GROUP_URI,
        objectUnionOf(CONCURRENT_ACTION_GROUP_URI, RECURRENT_ACTION_GROUP_URI, SEQUENTIAL_ACTION_GROUP_URI));

    equivalentClasses(RECURRENT_ACTION_GROUP_URI,
        objectUnionOf(RECURRENT_TIMED_ACTION_GROUP_URI, REPEATING_ACTION_GROUP_URI));

    equivalentClasses(RECURRENT_TIMED_ACTION_GROUP_URI,
        objectIntersectionOf(RECURRENT_ACTION_GROUP_URI, TIMED_EVENT_URI));

    equivalentClasses(REPEATING_ACTION_GROUP_URI,
        objectIntersectionOf(namedClass(RECURRENT_ACTION_GROUP_URI), objectComplementOf(TIMED_EVENT_URI)));

    equivalentClasses(
        TIMED_EVENT_SEQUENCE_URI,
        objectIntersectionOf(namedClass(SEQUENTIAL_ACTION_GROUP_URI),
            objectAllRestriction(HAS_FIRST_ACTION_GROUP_URI, TIMED_EVENT_URI),
            objectAllRestriction(HAS_NEXT_ACTION_GROUP_URI, TIMED_EVENT_URI)));

    dataPropertyDomain(CLASS_NAME_URI, IMPLEMENTATION_URI);
    dataPropertyDomain(JAR_FILE_URI, IMPLEMENTATION_URI);
    dataPropertyDomain(JAR_FILE_SEARCH_URI, IMPLEMENTATION_URI);
    dataPropertyDomain(PARAMETER_VALUE_URI, ACTION_PARAMETER_URI);
    dataPropertyDomain(PARAMETER_TYPE_URI, ACTION_PARAMETER_URI);
    dataPropertyDomain(PARAMETER_NAME_URI, ACTION_PARAMETER_URI);
    dataPropertyDomain(CLOCK_TICK_URI, TIMED_SCHEDULE_URI);
    dataPropertyDomain(STOP_TIME_URI, TIMED_SCHEDULE_URI);
    dataPropertyDomain(START_TIME_URI, TIMED_EVENT_URI);
    dataPropertyDomain(INTERVAL_URI, RECURRENT_TIMED_ACTION_GROUP_URI);
    dataPropertyDomain(URI_BASE_URI, PROCESS_URI);
    dataPropertyDomain(URI_EXTENSION_URI, PROCESS_URI);
    dataPropertyDomain(REPETITIONS_URI, REPEATING_ACTION_GROUP_URI);
    dataPropertyDomain(INCREMENT_URI, TIMED_EVENT_SEQUENCE_URI);
    dataPropertyDomain(QUERY_ID_URI, QUERY_URI);

    dataPropertyRange(CLASS_NAME_URI, XSDVocabulary.STRING);
    dataPropertyRange(JAR_FILE_URI, XSDVocabulary.STRING);
    dataPropertyRange(JAR_FILE_SEARCH_URI, XSDVocabulary.ANY_URI);
    dataPropertyRange(PARAMETER_VALUE_URI, XSDVocabulary.STRING);
    dataPropertyRange(PARAMETER_NAME_URI, XSDVocabulary.STRING);
    dataPropertyRange(CLOCK_TICK_URI, XSDVocabulary.DOUBLE);
    dataPropertyRange(STOP_TIME_URI, XSDVocabulary.DOUBLE);
    dataPropertyRange(START_TIME_URI, XSDVocabulary.DOUBLE);
    dataPropertyRange(INTERVAL_URI, XSDVocabulary.DOUBLE);
    dataPropertyRange(URI_BASE_URI, XSDVocabulary.STRING);
    dataPropertyRange(URI_EXTENSION_URI, XSDVocabulary.STRING);
    dataPropertyRange(REPETITIONS_URI, XSDVocabulary.INT);
    dataPropertyRange(INCREMENT_URI, XSDVocabulary.DOUBLE);
    dataPropertyRange(QUERY_ID_URI, XSDVocabulary.ANY_URI);

    if(spp.isFull() || spp == OWLSpecies.OWL_DL || spp == OWLSpecies.OWL_2_DL) {
      dataPropertyRangeOneOf(PARAMETER_TYPE_URI, XSDVocabulary.STRING.getURI(), XSDVocabulary.ANY_URI.getURI(),
          XSDVocabulary.DOUBLE.getURI(), XSDVocabulary.FLOAT.getURI(), XSDVocabulary.LONG.getURI(),
          XSDVocabulary.INT.getURI(), XSDVocabulary.BOOLEAN.getURI());
    }
    else {
      dataPropertyRange(PARAMETER_TYPE_URI, XSDVocabulary.ANY_URI);
    }

    if(!spp.isFull()) {
      dataPropertyDomain(HAS_AGENT_CLASS_URI, ACTION_FOR_EACH_URI);
      dataPropertyDomain(ORDERED_BY_URI, ASCENDING_ORDER_ACTION_FOR_EACH_URI, DESCENDING_ORDER_ACTION_FOR_EACH_URI);
      dataPropertyDomain(COMPARE_PROPERTY_URI, FP_COMPARISON_URI);
      dataPropertyRange(HAS_AGENT_CLASS_URI, XSDVocabulary.ANY_URI);
      dataPropertyRange(ORDERED_BY_URI, XSDVocabulary.ANY_URI);
      dataPropertyRange(COMPARE_PROPERTY_URI, XSDVocabulary.ANY_URI);
      dataPropertyFunctional(HAS_AGENT_CLASS_URI, ORDERED_BY_URI, COMPARE_PROPERTY_URI);
    }

    dataPropertyFunctional(CLASS_NAME_URI, JAR_FILE_URI, PARAMETER_VALUE_URI, PARAMETER_TYPE_URI, PARAMETER_NAME_URI,
        CLOCK_TICK_URI, STOP_TIME_URI, START_TIME_URI, INTERVAL_URI, URI_BASE_URI, URI_EXTENSION_URI, REPETITIONS_URI);

    objectPropertyDomain(IMPLEMENTED_BY_URI, PROCESS_URI);
    objectPropertyDomain(HAS_PARAMETERS_URI, PROCESS_URI);
    objectPropertyDomain(HAS_AGENT_URI, INDIVIDUAL_ACTION_URI);
    objectPropertyDomain(HAS_CONCURRENT_ACTIONS_URI, CONCURRENT_ACTION_GROUP_URI);
    objectPropertyDomain(HAS_RECURRENT_ACTION_GROUP_URI, RECURRENT_ACTION_GROUP_URI);
    objectPropertyDomain(HAS_FIRST_ACTION_GROUP_URI, SEQUENTIAL_ACTION_GROUP_URI);
    objectPropertyDomain(HAS_NEXT_ACTION_GROUP_URI, SEQUENTIAL_ACTION_GROUP_URI);
    objectPropertyDomain(HAS_ACTION_GROUP_URI, SCHEDULE_URI);
    objectPropertyDomain(PROVENANCE_URI, PROCESS_URI);
    objectPropertyDomain(HAS_COMPARISON_METHOD_URI, FP_COMPARISON_URI);
    objectPropertyDomain(USE_FLOATING_POINT_COMPARISON_URI, PROCESS_URI);

    if(spp.isFull()) {
      objectPropertyDomain(HAS_AGENT_CLASS_URI, ACTION_FOR_EACH_URI);
      objectPropertyDomain(ORDERED_BY_URI, ASCENDING_ORDER_ACTION_FOR_EACH_URI, DESCENDING_ORDER_ACTION_FOR_EACH_URI);
      objectPropertyDomain(COMPARE_PROPERTY_URI, FP_COMPARISON_URI);
      objectPropertyRange(HAS_AGENT_CLASS_URI, OWL_CLASS_URI(spp));
      objectPropertyRange(ORDERED_BY_URI, OWL_DATA_PROPERTY_URI(spp));
      objectPropertyRange(COMPARE_PROPERTY_URI, OWL_DATA_PROPERTY_URI(spp));
      objectPropertyFunctional(HAS_AGENT_CLASS_URI, ORDERED_BY_URI, COMPARE_PROPERTY_URI);
    }

    objectPropertyRange(IMPLEMENTED_BY_URI, IMPLEMENTATION_URI);
    objectPropertyRange(HAS_PARAMETERS_URI, ACTION_PARAMETER_URI);
    objectPropertyRange(HAS_AGENT_URI, AGENT_URI);
    objectPropertyRange(HAS_CONCURRENT_ACTIONS_URI, ACTION_URI);
    objectPropertyRange(HAS_RECURRENT_ACTION_GROUP_URI, CONCURRENT_ACTION_GROUP_URI, SEQUENTIAL_ACTION_GROUP_URI);
    objectPropertyRange(HAS_FIRST_ACTION_GROUP_URI, CONCURRENT_ACTION_GROUP_URI, RECURRENT_ACTION_GROUP_URI, ACTION_URI);
    objectPropertyRange(HAS_NEXT_ACTION_GROUP_URI, SEQUENTIAL_ACTION_GROUP_URI);
    objectPropertyRange(HAS_ACTION_GROUP_URI, ACTION_URI, ACTION_GROUP_URI);
    objectPropertyRange(PROVENANCE_URI, OWL_THING_URI(spp));
    objectPropertyRange(HAS_COMPARISON_METHOD_URI, FCmpOntology.FCMP_URI);
    objectPropertyRange(USE_FLOATING_POINT_COMPARISON_URI, FP_COMPARISON_URI);

    objectPropertyFunctional(HAS_AGENT_URI, HAS_RECURRENT_ACTION_GROUP_URI, HAS_FIRST_ACTION_GROUP_URI,
        HAS_NEXT_ACTION_GROUP_URI, HAS_ACTION_GROUP_URI, HAS_COMPARISON_METHOD_URI);

    individualHasClass(BOOTSTRAPPER_URI, AGENT_URI);
  }

  /**
   * <!-- getSchedule -->
   * 
   * Get a particular schedule
   * 
   * @param scheduleURI URI of the schedule to get
   * @param msb the model state broker
   * @return the schedule
   * @throws NoSuchProcessImplementationException
   * @throws IntegrationInconsistencyException
   * @throws ScheduleException
   * @throws OntologyConfigurationException
   */
  public ObiamaSchedule getSchedule(URI scheduleURI, ModelStateBroker msb) throws NoSuchProcessImplementationException,
      IntegrationInconsistencyException, ScheduleException, OntologyConfigurationException {
    /*
     * if(!spp.isFull()) try { makeInferences(); }
     * catch(OWLOntologyCreationException e) { throw new Bug(); }
     * catch(OWLReasonerException e) { throw new
     * OntologyConfigurationException(getURI(),
     * "Reasoner unable to work with this ontology"); }
     * catch(ClassNotFoundException e) { ErrorHandler.fatal(e,
     * "initialising reasoner"); }
     */
    ObiamaSchedule schedule = new ObiamaSchedule(scheduleURI, msb, this);

    if(isAssertedA(scheduleURI, NON_TIMED_SCHEDULE_URI) && schedule.isTimed()) {
      throw new ScheduleException(schedule.getActionGroup(), " timed action cannot be part of non-timed schedule "
        + scheduleURI);
    }
    else if(isAssertedA(scheduleURI, TIMED_SCHEDULE_URI) && !schedule.isTimed()) {
      throw new ScheduleException(schedule.getActionGroup(), " non-timed action cannot part of timed schedule "
        + scheduleURI);
    }

    return schedule;
  }
  
  /**
   * <!-- buildQuery -->
   * 
   * Build a query from the model structure ontology
   *
   * @param queryURI URI of the query to build
   * @param msb The model state broker
   * @return The query as stored in the model structure ontology
   * @throws ModelStructureOntologyException
   * @throws NoSuchProcessImplementationException
   */
  public OntologyQuery buildQuery(URI queryURI, ModelStateBroker msb) throws ModelStructureOntologyException, NoSuchProcessImplementationException {
    Query<?> query = ProcessFactory.buildProcess(queryURI, msb, this, Query.class);
    
    URI queryID = getURIFunctionalDataPropertyOf(queryURI, QUERY_ID_URI);
    if(queryID == null) {
      throw new ModelStructureOntologyException(queryURI, "Query has no query ID");
    }
    query.setQueryID(queryID);
    
    return new OntologyQuery(queryID, query, msb, queryURI);
  }

  /**
   * <!-- getAllSchedules -->
   * 
   * Get all schedules in the ontology
   * 
   * @param msb the model state broker
   * @return a set of all schedules
   * @throws NoSuchProcessImplementationException
   * @throws IntegrationInconsistencyException
   * @throws ScheduleException
   * @throws OntologyConfigurationException
   */
  Set<ObiamaSchedule> getAllSchedules(ModelStateBroker msb) throws NoSuchProcessImplementationException,
      IntegrationInconsistencyException, ScheduleException, OntologyConfigurationException {
    Set<URI> scheduleURIs = new HashSet<URI>();
    scheduleURIs.addAll(getAssertedMembersOf(SCHEDULE_URI));
    for(URI classURI: getNamedSubClassesOf(SCHEDULE_URI)) {
      scheduleURIs.addAll(getAssertedMembersOf(classURI));
    }
    Set<ObiamaSchedule> schedules = new HashSet<ObiamaSchedule>();
    for(URI scheduleURI: scheduleURIs) {
      schedules.add(getSchedule(scheduleURI, msb));
    }
    return schedules;
  }

  /**
   * <!-- getScheduledAction -->
   * 
   * Create a scheduled action from this schedule ontology--which will also
   * entail the creation of any sub-actions (in the case of action groups).
   * 
   * @param actionURI URI of the scheduled action to create
   * @param msb The model state broker
   * @param createdActions A map of any existing actions already created
   * @return the scheduled action
   * @throws NoSuchProcessImplementationException
   * @throws IntegrationInconsistencyException
   * @throws ScheduleException
   */
  AbstractScheduledAction getScheduledAction(URI actionURI, ModelStateBroker msb,
      Map<URI, AbstractScheduledAction> createdActions) throws NoSuchProcessImplementationException,
      IntegrationInconsistencyException, ScheduleException {

    Set<URI> namedClasses = getNamedClassesOf(actionURI);
    Set<AbstractScheduledAction> assertedTimed = new HashSet<AbstractScheduledAction>();

    AbstractScheduledAction scheduledAction =
      getScheduledAction(actionURI, msb, namedClasses, createdActions, assertedTimed);

    for(AbstractScheduledAction activity: assertedTimed) {
      if(!activity.isTimed()) {
        throw new ScheduleException(activity, "asserted a " + TIMED_EVENT_URI.getFragment() + " but cannot infer a "
          + START_TIME_URI.getFragment());
      }
    }
    return scheduledAction;
  }

  /**
   * <!-- getScheduledAction -->
   * 
   * This is a private method to implement recursive creation of scheduled
   * actions.
   * 
   * @param actionURI URI of the action to create
   * @param msb the model state broker
   * @param namedClasses set of named classes to which the action can cheaply be
   *          inferred to belong
   * @param createdActions map of action that have already been created (to
   *          handle the case whereby the same sub-action occurs in two action
   *          groups
   * @param assertedTimed a list of actions which have been asserted as being
   *          timed, but have not been given a time (perhaps by mistake, perhaps
   *          because they were expected to inherit a time)
   * @return the scheduled action
   * @throws NoSuchProcessImplementationException
   * @throws IntegrationInconsistencyException
   * @throws ScheduleException
   */
  private AbstractScheduledAction getScheduledAction(URI actionURI, ModelStateBroker msb, Set<URI> namedClasses,
      Map<URI, AbstractScheduledAction> createdActions, Set<AbstractScheduledAction> assertedTimed)
      throws NoSuchProcessImplementationException, IntegrationInconsistencyException, ScheduleException {
    Double time = null;
    boolean timed = false;
    AbstractScheduledAction activity;

    if(namedClasses.contains(TIMED_EVENT_URI)) {
      time = getDoubleFunctionalDataPropertyOf(actionURI, START_TIME_URI);
      timed = true;
    }

    if(createdActions.containsKey(actionURI)) {
      activity = createdActions.get(actionURI);
      if(time != null) activity.setTime(time);
      return activity;
    }

    if(namedClasses.contains(ACTION_URI)) {
      if(namedClasses.contains(INDIVIDUAL_ACTION_URI)) {
        activity = buildIndividualAction(actionURI, msb, time);
      }
      else if(namedClasses.contains(ACTION_FOR_EACH_URI)) {
        if(namedClasses.contains(CONCURRENT_ACTION_FOR_EACH_URI)) {
          activity = buildConcurrentActionForEach(actionURI, msb, time);
        }
        else if(namedClasses.contains(RANDOM_ORDER_ACTION_FOR_EACH_URI)) {
          activity = buildRandomOrderActionForEach(actionURI, msb, time);
        }
        else if(namedClasses.contains(ASCENDING_ORDER_ACTION_FOR_EACH_URI)
          || namedClasses.contains(DESCENDING_ORDER_ACTION_FOR_EACH_URI)) {
          activity =
            buildOrderedActionForEach(actionURI, msb, time, namedClasses.contains(ASCENDING_ORDER_ACTION_FOR_EACH_URI));
        }
        else {
          throw new ScheduleException(actionURI, "Action does not belong to a recognised specific class of "
            + ACTION_FOR_EACH_URI.getFragment());
        }
      }
      else {
        throw new ScheduleException(actionURI, "Action does not belong to a recognised specific class of "
          + ACTION_URI.getFragment());
      }
    }
    else if(namedClasses.contains(ACTION_GROUP_URI)) {
      if(namedClasses.contains(CONCURRENT_ACTION_GROUP_URI)) {
        activity = buildConcurrentActionGroup(actionURI, msb, time, createdActions, assertedTimed);
      }
      else if(namedClasses.contains(RECURRENT_ACTION_GROUP_URI)) {
        if(time == null && timed) {
          throw new ScheduleException(actionURI, RECURRENT_ACTION_GROUP_URI.getFragment() + " must have "
            + START_TIME_URI.getFragment() + " stated explicitly");
        }
        activity = buildRecurrentActionGroup(actionURI, msb, time, createdActions, assertedTimed);
      }
      else if(namedClasses.contains(SEQUENTIAL_ACTION_GROUP_URI)) {
        activity = buildSequentialActionGroup(actionURI, msb, time, createdActions, assertedTimed);
      }
      else {
        throw new ScheduleException(actionURI, "Not a recognised specific subclass of "
          + ACTION_GROUP_URI.getFragment());
      }
    }
    else {
      throw new ScheduleException(actionURI, "The individual does not belong to " + ACTION_URI.getFragment() + " or "
        + ACTION_GROUP_URI.getFragment() + " (individual does belong to " + namedClasses + ")");
    }

    if(!createdActions.containsKey(actionURI)) createdActions.put(actionURI, activity);
    if(timed && !activity.isTimed()) {
      assertedTimed.add(activity);
    }

    return activity;
  }

  /**
   * <!-- buildIndividualAction -->
   * 
   * Build an action performed by a single agent
   * 
   * @param actionURI URI of the individual action
   * @param msb the model state broker
   * @param time time at which the action is to occur (or null if none given)
   * @return the individual action
   * @throws ScheduleException
   * @throws NoSuchProcessImplementationException
   */
  private IndividualAction buildIndividualAction(URI actionURI, ModelStateBroker msb, Double time)
      throws ScheduleException, NoSuchProcessImplementationException {
    Action action = ProcessFactory.buildProcess(actionURI, msb, this, Action.class);

    URI agent = getFunctionalObjectPropertyOf(actionURI, HAS_AGENT_URI);
    if(agent == null) {
      throw new ScheduleException(actionURI, "Individual action has no agent");
    }

    if(time == null) {
      return new IndividualAction(agent, action, msb, actionURI, isAssertedA(actionURI,
          objectComplementOf(TIMED_EVENT_URI)));
    }
    else {
      return new IndividualAction(agent, action, msb, actionURI, time);
    }
  }

  /**
   * <!-- buildRandomOrderActionForEach -->
   * 
   * Build an action performed by all agents belonging to a particular class;
   * the actions by each agent occurring one after the other in a random order
   * 
   * @param actionURI URI of the random order action for each
   * @param msb the model state broker
   * @param time time at which the action of the first agent occurs (or null if
   *          none given; note also that the times of the other agents
   *          performing the action are treated as though the delay can be
   *          ignored)
   * @return the random-order action for each
   * @throws ScheduleException
   * @throws IntegrationInconsistencyException
   * @throws NoSuchProcessImplementationException
   */
  private RandomOrderActionForEach buildRandomOrderActionForEach(URI actionURI, ModelStateBroker msb, Double time)
      throws ScheduleException, IntegrationInconsistencyException, NoSuchProcessImplementationException {

    Action action = ProcessFactory.buildProcess(actionURI, msb, this, Action.class);
    URI agentClass =
      spp.isFull() ? getFunctionalObjectPropertyOf(actionURI, HAS_AGENT_CLASS_URI) : getURIFunctionalDataPropertyOf(
          actionURI, HAS_AGENT_CLASS_URI);
    if(agentClass == null) {
      throw new ScheduleException(actionURI, "Action for each has no agent class");
    }

    if(time == null) {
      return new RandomOrderActionForEach(agentClass, action, msb, actionURI, isAssertedA(actionURI,
          objectComplementOf(TIMED_EVENT_URI)));
    }
    else {
      return new RandomOrderActionForEach(agentClass, action, msb, actionURI, time);
    }
  }

  /**
   * <!-- buildConcurrentActionForEach -->
   * 
   * Build an action performed by all agents belonging to a particular class;
   * the actions by each agent happening at the same time
   * 
   * @param actionURI URI of the concurrent action for each
   * @param msb the model state broker
   * @param time time at which the concurrent action is to start (or null if
   *          none given)
   * @return the concurrent action for each
   * @throws ScheduleException
   * @throws NoSuchProcessImplementationException
   * @throws IntegrationInconsistencyException
   */
  private ConcurrentActionForEach buildConcurrentActionForEach(URI actionURI, ModelStateBroker msb, Double time)
      throws ScheduleException, NoSuchProcessImplementationException, IntegrationInconsistencyException {

    Action action = ProcessFactory.buildProcess(actionURI, msb, this, Action.class);
    URI agentClass =
      spp.isFull() ? getFunctionalObjectPropertyOf(actionURI, HAS_AGENT_CLASS_URI) : getURIFunctionalDataPropertyOf(
          actionURI, HAS_AGENT_CLASS_URI);
    if(agentClass == null) {
      throw new ScheduleException(actionURI, "Action for each has no agent class");
    }

    if(time == null) {
      return new ConcurrentActionForEach(agentClass, action, msb, actionURI, isAssertedA(actionURI,
          objectComplementOf(TIMED_EVENT_URI)));
    }
    else {
      return new ConcurrentActionForEach(agentClass, action, msb, actionURI, time);
    }

  }

  /**
   * <!-- buildOrderedActionForEach -->
   * 
   * Build an ascending or descending order action for each--an action performed
   * by all members of a class in ascending or descending order of one of their
   * functional data properties.
   * 
   * @param actionURI URI of the ordered action for each
   * @param msb the model state broker
   * @param time the time at which the first agent does the action (or null if
   *          none given; note that any interval between the first agent and any
   *          other agent is ignored)
   * @param ascending <code>true</code> if the order is ascending,
   *          <code>false</code> if descending
   * @return the ordered action for each
   * @throws NoSuchProcessImplementationException
   * @throws ScheduleException
   * @throws IntegrationInconsistencyException
   */
  private OrderedActionForEach buildOrderedActionForEach(URI actionURI, ModelStateBroker msb, Double time,
      boolean ascending) throws NoSuchProcessImplementationException, ScheduleException,
      IntegrationInconsistencyException {

    Action action = ProcessFactory.buildProcess(actionURI, msb, this, Action.class);
    URI agentClass =
      spp.isFull() ? getFunctionalObjectPropertyOf(actionURI, HAS_AGENT_CLASS_URI) : getURIFunctionalDataPropertyOf(
          actionURI, HAS_AGENT_CLASS_URI);
    if(agentClass == null) {
      throw new ScheduleException(actionURI, "Action for each has no agent class");
    }

    URI dataProperty =
      spp.isFull() ? getFunctionalObjectPropertyOf(actionURI, ORDERED_BY_URI) : getURIFunctionalDataPropertyOf(
          actionURI, ORDERED_BY_URI);
    if(dataProperty == null) {
      throw new ScheduleException(actionURI, "Ascending or descending order action for each has no property to sort on");
    }

    if(time == null) {
      return new OrderedActionForEach(agentClass, action, msb, actionURI, isAssertedA(actionURI,
          objectComplementOf(TIMED_EVENT_URI)), dataProperty, ascending);
    }
    else {
      return new OrderedActionForEach(agentClass, action, msb, actionURI, dataProperty, ascending, time);
    }
  }

  /**
   * <!-- buildConcurrentActionGroup -->
   * 
   * Build an action group consisting of a set of sub-actions which are to be
   * run concurrently. (Note that concurrency simply means that any changes to
   * the model state ontology must be made without a clash--e.g. two writes to
   * the same variable.) A timed concurrent action group may inherit its time
   * from any timed sub-action; all sub-actions inherit their time from a timed
   * concurrent action group.
   * 
   * @param actionURI URI of the concurrent action group
   * @param msb the model state broker
   * @param time time at which the actions are to start (or null if none
   *          specified)
   * @param createdActions map of any actions already created
   * @param assertedTimed set of actions asserted as timed actions, but not
   *          given a time
   * @return the concurrent action group
   * @throws ScheduleException
   * @throws NoSuchProcessImplementationException
   * @throws IntegrationInconsistencyException
   */
  private ConcurrentActionGroup buildConcurrentActionGroup(URI actionURI, ModelStateBroker msb, Double time,
      Map<URI, AbstractScheduledAction> createdActions, Set<AbstractScheduledAction> assertedTimed)
      throws ScheduleException, NoSuchProcessImplementationException, IntegrationInconsistencyException {

    ConcurrentActionGroup concAct;
    if(time == null) {
      concAct = new ConcurrentActionGroup(msb, actionURI, isAssertedA(actionURI, objectComplementOf(TIMED_EVENT_URI)));
    }
    else {
      concAct = new ConcurrentActionGroup(msb, actionURI, time);
    }

    createdActions.put(actionURI, concAct);

    Set<URI> concurrentActions = getObjectPropertyOf(actionURI, HAS_CONCURRENT_ACTIONS_URI);
    if(concurrentActions == null) {
      throw new ScheduleException(actionURI, "No concurrent actions defined");
    }

    for(URI concurrentAction: concurrentActions) {
      AbstractScheduledAction modelAction =
        getScheduledAction(concurrentAction, msb, getNamedClassesOf(concurrentAction), createdActions, assertedTimed);
      if(modelAction instanceof AbstractNoUpdateAction) {
        concAct.addAction((AbstractNoUpdateAction)modelAction);
      }
      else if(modelAction instanceof ConcurrentActionGroup) {
        concAct.addAction((ConcurrentActionGroup)modelAction);
      }
      else {
        throw new ScheduleException(actionURI, "Action " + modelAction.getURI()
          + " cannot be run as a part of a concurrent action");
      }
    }

    return concAct;
  }

  /**
   * <!-- buildRecurrentActionGroup -->
   * 
   * Build a recurrent action group, which consists of an action or action group
   * to be repeated. If timed, a recurrent action group has a start time and
   * repeat interval; if non-timed, a recurrent action group has a specified
   * number of repetitions. All sub-actions of a recurrent action group must be
   * non-timed. (They will inherit a repeat interval and start time from this
   * action.)
   * 
   * @param actionURI URI of the recurrent action
   * @param msb the model state broker
   * @param time the start time of this action (or null if none given)
   * @param createdActions any actions already built
   * @param assertedTimed actions that should inherit a time
   * @return the recurrent scheduled action
   * @throws ScheduleException
   * @throws NoSuchProcessImplementationException
   * @throws IntegrationInconsistencyException
   */
  private RecurrentActionGroup buildRecurrentActionGroup(URI actionURI, ModelStateBroker msb, Double time,
      Map<URI, AbstractScheduledAction> createdActions, Set<AbstractScheduledAction> assertedTimed)
      throws ScheduleException, NoSuchProcessImplementationException, IntegrationInconsistencyException {

    URI recurrentActionGroup = getFunctionalObjectPropertyOf(actionURI, HAS_RECURRENT_ACTION_GROUP_URI);
    if(recurrentActionGroup == null) {
      throw new ScheduleException(actionURI, "No recurrent actions");
    }

    AbstractScheduledAction modelAction =
      getScheduledAction(recurrentActionGroup, msb, getNamedClassesOf(recurrentActionGroup), createdActions,
          assertedTimed);

    if(time != null) {
      Double interval = getDoubleFunctionalDataPropertyOf(actionURI, INTERVAL_URI);
      if(interval == null) {
        throw new ScheduleException(actionURI, "No interval specified for "
          + RECURRENT_TIMED_ACTION_GROUP_URI.getFragment());
      }
      return new RecurrentActionGroup(modelAction, interval, msb, actionURI, time);
    }
    else {
      Integer repetitions = getIntegerFunctionalDataPropertyOf(actionURI, REPETITIONS_URI);
      if(repetitions == null) {
        throw new ScheduleException(actionURI, "No repetitions specified for "
          + REPEATING_ACTION_GROUP_URI.getFragment());
      }
      return new RecurrentActionGroup(modelAction, repetitions, msb, actionURI, true);
    }

  }

  /**
   * <!-- buildSequentialActionGroup -->
   * 
   * Build a sequential action group, which consists of a series of actions to
   * be executed in order. Timed sequential action groups have a positive
   * increment representing any elapsed time between actions in the sequence.
   * 
   * @param actionURI URI of the sequential action group
   * @param msb the model state broker
   * @param time the time at which the first action in the sequence starts (or
   *          null if none given)
   * @param createdActions any actions that have already been created
   * @param assertedTimed actions expecting to inherit a time
   * @return the scheduled action sequence
   * @throws ScheduleException
   * @throws NoSuchProcessImplementationException
   * @throws IntegrationInconsistencyException
   */
  private SequentialActionGroup buildSequentialActionGroup(URI actionURI, ModelStateBroker msb, Double time,
      Map<URI, AbstractScheduledAction> createdActions, Set<AbstractScheduledAction> assertedTimed)
      throws ScheduleException, NoSuchProcessImplementationException, IntegrationInconsistencyException {

    URI firstActionGroup = getFunctionalObjectPropertyOf(actionURI, HAS_FIRST_ACTION_GROUP_URI);
    if(firstActionGroup == null) {
      throw new ScheduleException(actionURI, SEQUENTIAL_ACTION_GROUP_URI.getFragment() + " has no "
        + HAS_FIRST_ACTION_GROUP_URI.getFragment());
    }
    AbstractScheduledAction firstAction =
      getScheduledAction(firstActionGroup, msb, getNamedClassesOf(firstActionGroup), createdActions, assertedTimed);

    // Don't care if the first action is also a sequence...

    SequentialActionGroup seq;
    if(time == null) {
      seq =
        new SequentialActionGroup(msb, actionURI, isAssertedA(actionURI, objectComplementOf(TIMED_EVENT_URI)),
            firstAction);
    }
    else {
      Double increment = getDoubleFunctionalDataPropertyOf(actionURI, INCREMENT_URI);
      if(increment == null) {
        throw new ScheduleException(actionURI, TIMED_EVENT_SEQUENCE_URI.getFragment() + " has no "
          + INCREMENT_URI.getFragment());
      }

      seq = new SequentialActionGroup(msb, actionURI, firstAction, time, increment);
    }

    URI nextActionGroup = getFunctionalObjectPropertyOf(actionURI, HAS_NEXT_ACTION_GROUP_URI);

    if(nextActionGroup != null) {
      createdActions.put(actionURI, seq);

      AbstractScheduledAction nextAction =
        getScheduledAction(nextActionGroup, msb, getNamedClassesOf(nextActionGroup), createdActions, assertedTimed);
      if(nextAction instanceof SequentialActionGroup) {
        seq.addAction(nextAction);
      }
      else {
        throw new ScheduleException(actionURI, HAS_NEXT_ACTION_GROUP_URI.getFragment() + " is not a "
          + SEQUENTIAL_ACTION_GROUP_URI.getFragment());
      }
    }

    return seq;
  }

  /**
   * <!-- setImport -->
   * 
   * Set the ontology import URI
   * 
   * @param imp String containing the URI of the ontology to import
   */
  private void setImport(String imp) {
    owlImport = URI.create(imp);
  }

  /**
   * <!-- main -->
   * 
   * Main method--create a schedule ontology and save it.
   * 
   * @param args
   */
  public static void main(String[] args) {
    try {
      addArgument(new CommandLineArgument("-I", "--import", "Ontology URI",
          "Request that the schedule ontology import the specified ontology"));
      ScheduleOntology obj = (ScheduleOntology)createOntology(ScheduleOntology.class, args);
      if(argMap.containsKey("import")) {
        obj.setImport(argMap.get("import"));
      }
    }
    catch(UsageException e) {
      ErrorHandler.fatal(e, "parsing command-line arguments");
    }
  }
}
