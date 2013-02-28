/*
 * uk.ac.hutton.obiama.model: AbstractScheduleOntology.java
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

import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.action.Process;
import uk.ac.hutton.obiama.msb.XSDHelper;

/**
 * <!-- AbstractScheduleOntology -->
 * 
 * <p>
 * This class provides tools for creating a schedule ontology programmatically.
 * Methods are provided that create the assertions needed to build all the
 * components of a schedule. Each method has two modes in which it can be
 * called, <code>buildXXX(URI xxxURI, ...)</code> and
 * <code>createXXX(...)</code>. The <code>buildXXX()</code> methods require a
 * URI of the entity to be created, whilst the <code>createXXX()</code> methods
 * create the entity URI for you, returning it. The latter allows you to nest
 * <code>createXXX()</code> methods when building your schedule, like this:
 * </p>
 * 
 * <pre>
 * URI scheduleURI = createScheduleSequence(
 *   createConcurrentActionForEach(
 *     aClassURI,
 *     createImplementation(AnActionClass.class)
 *   ),
 *   createRepeatingActionGroup(
 *     6,
 *     createSequentialActionGroup(
 *       createIndividualAction(
 *         anIndividualURI,
 *         createImplementation(AnotherActionClass.class)
 *       )
 *     )
 *   )
 * )
 * </pre>
 * 
 * <p>
 * However, this also means no explicit names for your entities, and unless laid
 * out carefully, could prove difficult to read.
 * </p>
 * 
 * @author Gary Polhill
 */
public abstract class AbstractScheduleOntology extends AbstractModelOntology {

  public static final double DEFAULT_START_TIME = 0.0;

  public static final double DEFAULT_INTERVAL = 1.0;

  /**
   * <!-- Anon -->
   * 
   * Generate URIs for 'anonymous' entities. (They then won't really be
   * anonymous in the ontology, but they are anonymous in that they are not
   * explicitly named in the subclass.)
   * 
   * @author Gary Polhill
   */
  protected enum Anon {
    FCMP, SEQUENCE, LOOP, PARALLEL, ACTION, QUERY, IMPLEMENTATION, PARAMETER, SCHEDULE;

    private int nextID = 0;

    @Override
    public String toString() {
      return name().toLowerCase();
    }

    protected URI next(URI ontologyURI) {
      nextID++;
      return URI.create(ontologyURI + "#" + toString() + "_" + nextID);
    }

  }

  /**
   * <!-- buildOntology -->
   * 
   * Add the import statement for the Schedule Ontology, and then call another
   * abstract method to build the rest of the ontology.
   * 
   * @see uk.ac.hutton.obiama.model.AbstractModelOntology#buildOntology()
   */
  protected final void buildOntology() {
    ontologyImport(ScheduleOntology.ONTOLOGY_URI);
    buildScheduleOntology();
  }

  /**
   * <!-- buildScheduleOntology -->
   * 
   * Implement this method to build your ontology. You do not need to import the
   * Schedule Ontology.
   */
  protected abstract void buildScheduleOntology();

  protected void timedEvent(URI actionOrActionGroupURI, double startTime) {
    classAssertion(actionOrActionGroupURI, ScheduleOntology.TIMED_EVENT_URI);
    dataPropertyAssertion(actionOrActionGroupURI, ScheduleOntology.START_TIME_URI, startTime);
  }

  protected void timedEventSequence(URI sequentialActionGroupURI, double increment) {
    classAssertion(sequentialActionGroupURI, ScheduleOntology.TIMED_EVENT_SEQUENCE_URI);
    dataPropertyAssertion(sequentialActionGroupURI, ScheduleOntology.INCREMENT_URI, increment);
  }

  /**
   * <!-- buildSchedule -->
   * 
   * Basic method to build a (non-timed) schedule
   * 
   * @param scheduleURI
   * @param actionOrActionGroupURI
   */
  protected void buildSchedule(URI scheduleURI, URI actionOrActionGroupURI) {
    classAssertion(scheduleURI, ScheduleOntology.SCHEDULE_URI);
    objectPropertyAssertion(scheduleURI, ScheduleOntology.HAS_ACTION_GROUP_URI, actionOrActionGroupURI);
  }

  protected URI createSchedule(URI actionOrActionGroupURI) {
    URI scheduleURI = Anon.SCHEDULE.next(ontologyURI());
    buildSchedule(scheduleURI, actionOrActionGroupURI);
    return scheduleURI;
  }

  /**
   * <!-- buildTimedSchedule -->
   * 
   * Basic method to build a timed schedule
   * 
   * @param scheduleURI
   * @param stopTime
   * @param timedEventURI
   */
  protected void buildTimedSchedule(URI scheduleURI, double stopTime, URI timedEventURI) {
    classAssertion(scheduleURI, ScheduleOntology.TIMED_SCHEDULE_URI);
    objectPropertyAssertion(scheduleURI, ScheduleOntology.HAS_ACTION_GROUP_URI, timedEventURI);
    dataPropertyAssertion(scheduleURI, ScheduleOntology.STOP_TIME_URI, stopTime);
  }

  protected URI createTimedSchedule(double stopTime, URI timedEventURI) {
    URI scheduleURI = Anon.SCHEDULE.next(ontologyURI());
    buildTimedSchedule(scheduleURI, stopTime, timedEventURI);
    return scheduleURI;
  }

  /**
   * <!-- buildScheduleSequence -->
   * 
   * Convenience method to build a non-timed schedule consisting of a sequence
   * of actions or action groups. (Action groups should not be sequences.) It is
   * assumed this will be most convenient for creating initialisation schedules.
   * 
   * @param scheduleURI
   * @param actionsOrActionGroups
   */
  protected void buildScheduleSequence(URI scheduleURI, URI... actionsOrActionGroups) {
    URI sequenceURI = Anon.SEQUENCE.next(ontologyURI());
    buildSchedule(scheduleURI, sequenceURI);
    buildSequentialActionGroup(sequenceURI, actionsOrActionGroups);
  }

  protected URI createScheduleSequence(URI... actionsOrActionGroups) {
    URI scheduleURI = Anon.SCHEDULE.next(ontologyURI());
    buildScheduleSequence(scheduleURI, actionsOrActionGroups);
    return scheduleURI;
  }

  protected void buildScheduleRepeatingSequence(URI scheduleURI, double stopTime, URI... actionsOrActionGroups) {
    buildScheduleRepeatingSequence(scheduleURI, DEFAULT_START_TIME, stopTime, DEFAULT_INTERVAL, actionsOrActionGroups);
  }

  protected URI createScheduleRepeatingSequence(double stopTime, URI... actionsOrActionGroups) {
    URI scheduleURI = Anon.SCHEDULE.next(ontologyURI());
    buildScheduleRepeatingSequence(scheduleURI, stopTime, actionsOrActionGroups);
    return scheduleURI;
  }

  protected void buildScheduleRepeatingSequence(URI scheduleURI, double stopTime, double interval,
      URI... actionsOrActionGroups) {
    buildScheduleRepeatingSequence(scheduleURI, DEFAULT_START_TIME, stopTime, interval, actionsOrActionGroups);
  }

  protected URI createScheduleRepeatingSequence(double stopTime, double interval, URI... actionsOrActionGroups) {
    URI scheduleURI = Anon.SCHEDULE.next(ontologyURI());
    buildScheduleRepeatingSequence(scheduleURI, stopTime, interval, actionsOrActionGroups);
    return scheduleURI;
  }

  /**
   * <!-- buildScheduleRepeatingSequence -->
   * 
   * Convenience method to build a timed schedule consisting of a repeated
   * sequence of actions or action groups. (Action groups should not be
   * sequences.) It is assumed this will be most convenient for creating main
   * schedules.
   * 
   * @param scheduleURI
   * @param startTime
   * @param stopTime
   * @param interval
   * @param actionsOrActionGroups
   */
  protected void buildScheduleRepeatingSequence(URI scheduleURI, double startTime, double stopTime, double interval,
      URI... actionsOrActionGroups) {
    URI loopURI = Anon.LOOP.next(ontologyURI());
    URI sequenceURI = Anon.SEQUENCE.next(ontologyURI());

    buildTimedSchedule(scheduleURI, stopTime, loopURI);
    buildRecurrentTimedActionGroup(loopURI, interval, sequenceURI);
    dataPropertyAssertion(loopURI, ScheduleOntology.START_TIME_URI, startTime);
    buildSequentialActionGroup(sequenceURI, actionsOrActionGroups);
  }

  protected URI createScheduleRepeatingSequence(double startTime, double stopTime, double interval,
      URI... actionsOrActionGroups) {
    URI scheduleURI = Anon.SCHEDULE.next(ontologyURI());
    buildScheduleRepeatingSequence(scheduleURI, startTime, stopTime, interval, actionsOrActionGroups);
    return scheduleURI;
  }

  protected void buildScheduleRepeatingSequence(URI scheduleURI, double startTime, double stopTime, double interval,
      double increment, URI... actionsOrActionGroups) {
    URI loopURI = Anon.LOOP.next(ontologyURI());
    URI sequenceURI = Anon.SEQUENCE.next(ontologyURI());

    buildTimedSchedule(scheduleURI, stopTime, loopURI);
    buildRecurrentTimedActionGroup(loopURI, interval, sequenceURI);
    dataPropertyAssertion(loopURI, ScheduleOntology.START_TIME_URI, startTime);
    buildSequentialActionGroup(sequenceURI, actionsOrActionGroups);
    timedEventSequence(sequenceURI, increment);
  }

  protected URI createScheduleRepeatingSequence(double startTime, double stopTime, double interval, double increment,
      URI... actionsOrActionGroups) {
    URI scheduleURI = Anon.SCHEDULE.next(ontologyURI());
    buildScheduleRepeatingSequence(scheduleURI, startTime, stopTime, interval, increment, actionsOrActionGroups);
    return scheduleURI;
  }

  protected void buildConcurrentActionGroup(URI actionGroupURI, URI... actions) {
    classAssertion(actionGroupURI, ScheduleOntology.CONCURRENT_ACTION_GROUP_URI);
    objectPropertyAssertion(actionGroupURI, ScheduleOntology.HAS_CONCURRENT_ACTIONS_URI, actions);
  }

  protected URI createConcurrentActionGroup(URI... actions) {
    URI actionGroupURI = Anon.PARALLEL.next(ontologyURI());
    buildConcurrentActionGroup(actionGroupURI, actions);
    return actionGroupURI;
  }

  protected void buildRepeatingActionGroup(URI actionGroupURI, int repetitions, URI repeatedActionGroup) {
    classAssertion(actionGroupURI, ScheduleOntology.REPEATING_ACTION_GROUP_URI);
    objectPropertyAssertion(actionGroupURI, ScheduleOntology.HAS_RECURRENT_ACTION_GROUP_URI, repeatedActionGroup);
    dataPropertyAssertion(actionGroupURI, ScheduleOntology.REPETITIONS_URI, repetitions);
  }

  protected URI createRepeatingActionGroup(int repetitions, URI repeatedActionGroup) {
    URI actionGroupURI = Anon.LOOP.next(ontologyURI());
    buildRepeatingActionGroup(actionGroupURI, repetitions, repeatedActionGroup);
    return actionGroupURI;
  }

  protected void buildRecurrentTimedActionGroup(URI actionGroupURI, double interval, URI repeatedActionGroup) {
    classAssertion(actionGroupURI, ScheduleOntology.RECURRENT_TIMED_ACTION_GROUP_URI);
    objectPropertyAssertion(actionGroupURI, ScheduleOntology.HAS_RECURRENT_ACTION_GROUP_URI, repeatedActionGroup);
    dataPropertyAssertion(actionGroupURI, ScheduleOntology.INTERVAL_URI, interval);
  }

  protected URI createRecurrentTimedActionGroup(double interval, URI repeatedActionGroup) {
    URI actionGroupURI = Anon.LOOP.next(ontologyURI());
    buildRecurrentTimedActionGroup(actionGroupURI, interval, repeatedActionGroup);
    return actionGroupURI;
  }

  protected void buildSequentialActionGroup(URI actionGroupURI, URI... actionsOrActionGroups) {
    URI nextSequence = null;

    for(int i = 0; i < actionsOrActionGroups.length; i++) {
      URI thisSequence = (i == 0 ? actionGroupURI : nextSequence);

      classAssertion(thisSequence, ScheduleOntology.SEQUENTIAL_ACTION_GROUP_URI);
      objectPropertyAssertion(thisSequence, ScheduleOntology.HAS_FIRST_ACTION_GROUP_URI, actionsOrActionGroups[i]);

      if(i < actionsOrActionGroups.length - 1) {
        nextSequence = Anon.SEQUENCE.next(ontologyURI());
        objectPropertyAssertion(thisSequence, ScheduleOntology.HAS_NEXT_ACTION_GROUP_URI, nextSequence);
      }
    }
  }

  protected URI createSequentialActionGroup(URI... actionsOrActionGroups) {
    URI actionGroupURI = Anon.SEQUENCE.next(ontologyURI());
    buildSequentialActionGroup(actionGroupURI, actionsOrActionGroups);
    return actionGroupURI;
  }

  protected void buildAscendingOrderActionForEach(URI classURI, URI propertyURI, URI actionURI, URI implementationURI) {
    buildAscendingOrderActionForEach(classURI, propertyURI, actionURI, implementationURI, null, null, (String)null,
        (String)null, (URI)null);
  }

  protected URI createAscendingOrderActionForEach(URI classURI, URI propertyURI, URI implementationURI) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildAscendingOrderActionForEach(classURI, propertyURI, actionURI, implementationURI);
    return actionURI;
  }

  protected void buildAscendingOrderActionForEach(URI classURI, URI propertyURI, URI actionURI, URI implementationURI,
      URI... parameters) {
    buildAscendingOrderActionForEach(classURI, propertyURI, actionURI, implementationURI, null, null, null, null,
        parameters);
  }

  protected URI createAscendingOrderActionForEach(URI classURI, URI propertyURI, URI implementationURI,
      URI... parameters) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildAscendingOrderActionForEach(classURI, propertyURI, actionURI, implementationURI, parameters);
    return actionURI;
  }

  protected void buildAscendingOrderActionForEach(URI classURI, URI propertyURI, URI actionURI, URI implementationURI,
      String uriBase, String uriExtension) {
    buildAscendingOrderActionForEach(classURI, propertyURI, actionURI, implementationURI, null, null, uriBase,
        uriExtension, (URI)null);
  }

  protected URI createAscendingOrderActionForEach(URI classURI, URI propertyURI, URI implementationURI, String uriBase,
      String uriExtension) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildAscendingOrderActionForEach(classURI, propertyURI, actionURI, implementationURI, uriBase, uriExtension);
    return actionURI;
  }

  protected void buildAscendingOrderActionForEach(URI classURI, URI propertyURI, URI actionURI, URI implementationURI,
      String uriBase, String uriExtension, URI... parameters) {
    buildAscendingOrderActionForEach(classURI, propertyURI, actionURI, implementationURI, null, null, uriBase,
        uriExtension, parameters);
  }

  protected URI createAscendingOrderActionForEach(URI classURI, URI propertyURI, URI implementationURI, String uriBase,
      String uriExtension, URI... parameters) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildAscendingOrderActionForEach(classURI, propertyURI, actionURI, implementationURI, uriBase, uriExtension,
        parameters);
    return actionURI;
  }

  protected void buildAscendingOrderActionForEach(URI classURI, URI propertyURI, URI actionURI, URI implementationURI,
      URI provenanceURI, URI fcmpURI, String uriBase, String uriExtension, URI... parameters) {
    classAssertion(actionURI, ScheduleOntology.ASCENDING_ORDER_ACTION_FOR_EACH_URI);
    buildOrderedActionForEach(classURI, propertyURI, actionURI, implementationURI, provenanceURI, fcmpURI, uriBase,
        uriExtension, parameters);
  }

  protected URI createAscendingOrderActionForEach(URI classURI, URI propertyURI, URI implementationURI,
      URI provenanceURI, URI fcmpURI, String uriBase, String uriExtension, URI... parameters) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildAscendingOrderActionForEach(classURI, propertyURI, actionURI, implementationURI, provenanceURI, fcmpURI,
        uriBase, uriExtension, parameters);
    return actionURI;
  }

  protected void buildDescendingOrderActionForEach(URI classURI, URI propertyURI, URI actionURI, URI implementationURI) {
    buildDescendingOrderActionForEach(classURI, propertyURI, actionURI, implementationURI, null, null, (String)null,
        (String)null, (URI)null);
  }

  protected URI createDescendingOrderActionForEach(URI classURI, URI propertyURI, URI implementationURI) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildDescendingOrderActionForEach(classURI, propertyURI, actionURI, implementationURI);
    return actionURI;
  }

  protected void buildDescendingOrderActionForEach(URI classURI, URI propertyURI, URI actionURI, URI implementationURI,
      URI... parameters) {
    buildDescendingOrderActionForEach(classURI, propertyURI, actionURI, implementationURI, null, null, null, null,
        parameters);
  }

  protected URI createDescendingOrderActionForEach(URI classURI, URI propertyURI, URI implementationURI,
      URI... parameters) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildDescendingOrderActionForEach(classURI, propertyURI, actionURI, implementationURI, parameters);
    return actionURI;
  }

  protected void buildDescendingOrderActionForEach(URI classURI, URI propertyURI, URI actionURI, URI implementationURI,
      String uriBase, String uriExtension) {
    buildDescendingOrderActionForEach(classURI, propertyURI, actionURI, implementationURI, null, null, uriBase,
        uriExtension, (URI)null);
  }

  protected URI createDescendingOrderActionForEach(URI classURI, URI propertyURI, URI implementationURI,
      String uriBase, String uriExtension) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildDescendingOrderActionForEach(classURI, propertyURI, actionURI, implementationURI, uriBase, uriExtension);
    return actionURI;
  }

  protected void buildDescendingOrderActionForEach(URI classURI, URI propertyURI, URI actionURI, URI implementationURI,
      String uriBase, String uriExtension, URI... parameters) {
    buildDescendingOrderActionForEach(classURI, propertyURI, actionURI, implementationURI, null, null, uriBase,
        uriExtension, parameters);
  }

  protected URI createDescendingOrderActionForEach(URI classURI, URI propertyURI, URI implementationURI,
      String uriBase, String uriExtension, URI... parameters) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildDescendingOrderActionForEach(classURI, propertyURI, actionURI, implementationURI, uriBase, uriExtension,
        parameters);
    return actionURI;
  }

  protected void buildDescendingOrderActionForEach(URI classURI, URI propertyURI, URI actionURI, URI implementationURI,
      URI provenanceURI, URI fcmpURI, String uriBase, String uriExtension, URI... parameters) {
    classAssertion(actionURI, ScheduleOntology.DESCENDING_ORDER_ACTION_FOR_EACH_URI);
    buildOrderedActionForEach(classURI, propertyURI, actionURI, implementationURI, provenanceURI, fcmpURI, uriBase,
        uriExtension, parameters);
  }

  protected URI createDescendingOrderActionForEach(URI classURI, URI propertyURI, URI implementationURI,
      URI provenanceURI, URI fcmpURI, String uriBase, String uriExtension, URI... parameters) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildDescendingOrderActionForEach(classURI, propertyURI, actionURI, implementationURI, provenanceURI, fcmpURI,
        uriBase, uriExtension, parameters);
    return actionURI;
  }

  protected void buildRandomOrderActionForEach(URI classURI, URI actionURI, URI implementationURI) {
    buildRandomOrderActionForEach(classURI, actionURI, implementationURI, null, null, (String)null, (String)null,
        (URI)null);
  }

  protected URI createRandomOrderActionForEach(URI classURI, URI implementationURI) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildRandomOrderActionForEach(classURI, actionURI, implementationURI);
    return actionURI;
  }

  protected void buildRandomOrderActionForEach(URI classURI, URI actionURI, URI implementationURI, URI... parameters) {
    buildRandomOrderActionForEach(classURI, actionURI, implementationURI, null, null, null, null, parameters);
  }

  protected URI createRandomOrderActionForEach(URI classURI, URI implementationURI, URI... parameters) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildRandomOrderActionForEach(classURI, actionURI, implementationURI, parameters);
    return actionURI;
  }

  protected void buildRandomOrderActionForEach(URI classURI, URI actionURI, URI implementationURI, String uriBase,
      String uriExtension) {
    buildRandomOrderActionForEach(classURI, actionURI, implementationURI, null, null, uriBase, uriExtension, (URI)null);
  }

  protected URI createRandomOrderActionForEach(URI classURI, URI implementationURI, String uriBase, String uriExtension) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildRandomOrderActionForEach(classURI, actionURI, implementationURI, uriBase, uriExtension);
    return actionURI;
  }

  protected void buildRandomOrderActionForEach(URI classURI, URI actionURI, URI implementationURI, String uriBase,
      String uriExtension, URI... parameters) {
    buildRandomOrderActionForEach(classURI, actionURI, implementationURI, null, null, uriBase, uriExtension, parameters);
  }

  protected URI createRandomOrderActionForEach(URI classURI, URI implementationURI, String uriBase,
      String uriExtension, URI... parameters) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildRandomOrderActionForEach(classURI, actionURI, implementationURI, uriBase, uriExtension, parameters);
    return actionURI;
  }

  protected void buildRandomOrderActionForEach(URI classURI, URI actionURI, URI implementationURI, URI provenanceURI,
      URI fcmpURI, String uriBase, String uriExtension, URI... parameters) {
    classAssertion(actionURI, ScheduleOntology.RANDOM_ORDER_ACTION_FOR_EACH_URI);
    buildActionForEach(classURI, actionURI, implementationURI, provenanceURI, fcmpURI, uriBase, uriExtension,
        parameters);
  }

  protected URI createRandomOrderActionForEach(URI classURI, URI implementationURI, URI provenanceURI, URI fcmpURI,
      String uriBase, String uriExtension, URI... parameters) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildRandomOrderActionForEach(classURI, actionURI, implementationURI, provenanceURI, fcmpURI, uriBase,
        uriExtension, parameters);
    return actionURI;
  }

  protected void buildConcurrentActionForEach(URI classURI, URI actionURI, URI implementationURI) {
    buildConcurrentActionForEach(classURI, actionURI, implementationURI, null, null, (String)null, (String)null,
        (URI)null);
  }

  protected URI createConcurrentActionForEach(URI classURI, URI implementationURI) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildConcurrentActionForEach(classURI, actionURI, implementationURI);
    return actionURI;
  }

  protected void buildConcurrentActionForEach(URI classURI, URI actionURI, URI implementationURI, URI... parameters) {
    buildConcurrentActionForEach(classURI, actionURI, implementationURI, null, null, null, null, parameters);
  }

  protected URI createConcurrentActionForEach(URI classURI, URI implementationURI, URI... parameters) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildConcurrentActionForEach(classURI, actionURI, implementationURI, parameters);
    return actionURI;
  }

  protected void buildConcurrentActionForEach(URI classURI, URI actionURI, URI implementationURI, String uriBase,
      String uriExtension) {
    buildConcurrentActionForEach(classURI, actionURI, implementationURI, null, null, uriBase, uriExtension, (URI)null);
  }

  protected URI createConcurrentActionForEach(URI classURI, URI implementationURI, String uriBase, String uriExtension) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildConcurrentActionForEach(classURI, actionURI, implementationURI, uriBase, uriExtension);
    return actionURI;
  }

  protected void buildConcurrentActionForEach(URI classURI, URI actionURI, URI implementationURI, String uriBase,
      String uriExtension, URI... parameters) {
    buildConcurrentActionForEach(classURI, actionURI, implementationURI, null, null, uriBase, uriExtension, parameters);
  }

  protected URI createConcurrentActionForEach(URI classURI, URI implementationURI, String uriBase, String uriExtension,
      URI... parameters) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildConcurrentActionForEach(classURI, actionURI, implementationURI, uriBase, uriExtension, parameters);
    return actionURI;
  }

  protected void buildConcurrentActionForEach(URI classURI, URI actionURI, URI implementationURI, URI provenanceURI,
      URI fcmpURI, String uriBase, String uriExtension, URI... parameters) {
    classAssertion(actionURI, ScheduleOntology.CONCURRENT_ACTION_FOR_EACH_URI);
    buildActionForEach(classURI, actionURI, implementationURI, provenanceURI, fcmpURI, uriBase, uriExtension,
        parameters);
  }

  protected URI createConcurrentActionForEach(URI classURI, URI implementationURI, URI provenanceURI, URI fcmpURI,
      String uriBase, String uriExtension, URI... parameters) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildConcurrentActionForEach(classURI, actionURI, implementationURI, provenanceURI, fcmpURI, uriBase, uriExtension,
        parameters);
    return actionURI;
  }

  protected void buildIndividualAction(URI indURI, URI actionURI, URI implementationURI) {
    buildIndividualAction(indURI, actionURI, implementationURI, null, null, (String)null, (String)null, (URI)null);
  }

  protected URI createIndividualAction(URI indURI, URI implementationURI) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildIndividualAction(indURI, actionURI, implementationURI);
    return actionURI;
  }

  protected void buildIndividualAction(URI indURI, URI actionURI, URI implementationURI, URI... parameters) {
    buildIndividualAction(indURI, actionURI, implementationURI, null, null, null, null, parameters);
  }

  protected URI createIndividualAction(URI indURI, URI implementationURI, URI... parameters) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildIndividualAction(indURI, actionURI, implementationURI, parameters);
    return actionURI;
  }

  protected void buildIndividualAction(URI indURI, URI actionURI, URI implementationURI, String uriBase,
      String uriExtension) {
    buildIndividualAction(indURI, actionURI, implementationURI, null, null, uriBase, uriExtension, (URI)null);
  }

  protected URI createIndividualAction(URI indURI, URI implementationURI, String uriBase, String uriExtension) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildIndividualAction(indURI, actionURI, implementationURI, uriBase, uriExtension);
    return actionURI;
  }

  protected void buildIndividualAction(URI indURI, URI actionURI, URI implementationURI, String uriBase,
      String uriExtension, URI... parameters) {
    buildIndividualAction(indURI, actionURI, implementationURI, null, null, uriBase, uriExtension, parameters);
  }

  protected URI createIndividualAction(URI indURI, URI implementationURI, String uriBase, String uriExtension,
      URI... parameters) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildIndividualAction(indURI, actionURI, implementationURI, uriBase, uriExtension, parameters);
    return actionURI;
  }

  protected void buildIndividualAction(URI indURI, URI actionURI, URI implementationURI, URI provenanceURI,
      URI fcmpURI, String uriBase, String uriExtension, URI... parameters) {
    classAssertion(actionURI, ScheduleOntology.INDIVIDUAL_ACTION_URI);
    objectPropertyAssertion(actionURI, ScheduleOntology.HAS_AGENT_URI, indURI);
    buildAction(actionURI, implementationURI, provenanceURI, fcmpURI, uriBase, uriExtension, parameters);
  }

  protected URI createIndividualAction(URI indURI, URI implementationURI, URI provenanceURI, URI fcmpURI,
      String uriBase, String uriExtension, URI... parameters) {
    URI actionURI = Anon.ACTION.next(ontologyURI());
    buildIndividualAction(indURI, actionURI, implementationURI, provenanceURI, fcmpURI, uriBase, uriExtension,
        parameters);
    return actionURI;
  }

  private void buildOrderedActionForEach(URI classURI, URI orderedPropertyURI, URI actionURI, URI implementationURI,
      URI provenanceURI, URI fcmpURI, String uriBase, String uriExtension, URI... parameters) {
    buildActionForEach(classURI, actionURI, implementationURI, provenanceURI, fcmpURI, uriBase, uriExtension,
        parameters);
    dataPropertyAssertion(actionURI, ScheduleOntology.ORDERED_BY_URI, orderedPropertyURI);
  }

  private void buildActionForEach(URI classURI, URI actionURI, URI implementationURI, URI provenanceURI, URI fcmpURI,
      String uriBase, String uriExtension, URI... parameters) {
    buildAction(actionURI, implementationURI, provenanceURI, fcmpURI, uriBase, uriExtension, parameters);
    dataPropertyAssertion(actionURI, ScheduleOntology.HAS_AGENT_CLASS_URI, classURI);
  }

  protected void buildAction(URI actionURI, URI implementationURI, URI provenanceURI, URI fcmpURI, String uriBase,
      String uriExtension, URI... parameters) {
    objectPropertyAssertion(actionURI, ScheduleOntology.IMPLEMENTED_BY_URI, implementationURI);
    if(parameters != null && parameters.length > 0 && parameters[0] != null) {
      objectPropertyAssertion(actionURI, ScheduleOntology.HAS_PARAMETERS_URI, parameters);
    }
    if(uriBase != null) {
      dataPropertyAssertion(actionURI, ScheduleOntology.URI_BASE_URI, uriBase);
    }
    if(uriExtension != null) {
      dataPropertyAssertion(actionURI, ScheduleOntology.URI_EXTENSION_URI, uriExtension);
    }
    if(provenanceURI != null) {
      objectPropertyAssertion(actionURI, ScheduleOntology.PROVENANCE_URI, provenanceURI);
    }
    if(fcmpURI != null) {
      objectPropertyAssertion(actionURI, ScheduleOntology.USE_FLOATING_POINT_COMPARISON_URI, fcmpURI);
    }
  }

  protected void buildImplementation(URI implementationURI, Class<? extends Process> processClass) {
    classAssertion(implementationURI, ScheduleOntology.IMPLEMENTATION_URI);
    dataPropertyAssertion(implementationURI, ScheduleOntology.CLASS_NAME_URI, processClass.getCanonicalName());
  }

  protected URI createImplemention(Class<? extends Process> processClass) {
    URI implementationURI = Anon.IMPLEMENTATION.next(ontologyURI());
    buildImplementation(implementationURI, processClass);
    return implementationURI;
  }

  protected void buildImplementation(URI implementationURI, Class<? extends Process> processClass, String jarFile) {
    buildImplementation(implementationURI, processClass);
    dataPropertyAssertion(implementationURI, ScheduleOntology.JAR_FILE_URI, jarFile);
  }

  protected URI createImplemention(Class<? extends Process> processClass, String jarFile) {
    URI implementationURI = Anon.IMPLEMENTATION.next(ontologyURI());
    buildImplementation(implementationURI, processClass, jarFile);
    return implementationURI;
  }

  protected void buildImplementation(URI implementationURI, Class<? extends Process> processClass, String jarFile,
      URI... jarSearch) {
    buildImplementation(implementationURI, processClass, jarFile);
    dataPropertyAssertion(implementationURI, ScheduleOntology.JAR_FILE_SEARCH_URI, jarSearch);
  }

  protected URI createImplemention(Class<? extends Process> processClass, String jarFile, URI... jarSearch) {
    URI implementationURI = Anon.IMPLEMENTATION.next(ontologyURI());
    buildImplementation(implementationURI, processClass, jarFile, jarSearch);
    return implementationURI;
  }

  protected void buildParameter(URI parameterURI, String name, Object value) {
    classAssertion(parameterURI, ScheduleOntology.ACTION_PARAMETER_URI);
    dataPropertyAssertion(parameterURI, ScheduleOntology.PARAMETER_NAME_URI, name);
    XSDVocabulary type = XSDHelper.getTypeFor(value);
    dataPropertyAssertion(parameterURI, ScheduleOntology.PARAMETER_VALUE_URI, type, value.toString());
    dataPropertyAssertion(parameterURI, ScheduleOntology.PARAMETER_TYPE_URI, type.getURI());
  }

  protected URI createParameter(String name, Object value) {
    URI parameterURI = Anon.PARAMETER.next(ontologyURI());
    buildParameter(parameterURI, name, value);
    return parameterURI;
  }

  protected void buildFloatingPointComparison(URI fcmpURI, URI propertyURI, URI methodClassURI, URI paramName,
      double paramValue) {
    URI methodURI = buildFloatingPointComparison(fcmpURI, propertyURI, methodClassURI);
    dataPropertyAssertion(methodURI, paramName, paramValue);
  }

  protected URI createFloatingPointComparison(URI propertyURI, URI methodClassURI, URI paramName, double paramValue) {
    URI fcmpURI = Anon.FCMP.next(ontologyURI());
    buildFloatingPointComparison(fcmpURI, propertyURI, methodClassURI, paramName, paramValue);
    return fcmpURI;
  }

  protected void buildFloatingPointComparison(URI fcmpURI, URI propertyURI, URI methodClassURI, URI paramName,
      int paramValue) {
    URI methodURI = buildFloatingPointComparison(fcmpURI, propertyURI, methodClassURI);
    dataPropertyAssertion(methodURI, paramName, paramValue);
  }

  protected URI createFloatingPointComparison(URI propertyURI, URI methodClassURI, URI paramName, int paramValue) {
    URI fcmpURI = Anon.FCMP.next(ontologyURI());
    buildFloatingPointComparison(fcmpURI, propertyURI, methodClassURI, paramName, paramValue);
    return fcmpURI;
  }

  private URI buildFloatingPointComparison(URI fcmpURI, URI propertyURI, URI methodClassURI) {
    classAssertion(fcmpURI, ScheduleOntology.FP_COMPARISON_URI);
    dataPropertyAssertion(fcmpURI, ScheduleOntology.COMPARE_PROPERTY_URI, propertyURI);

    URI methodURI = Anon.FCMP.next(ontologyURI());
    classAssertion(methodURI, methodClassURI);
    return methodURI;
  }
}
