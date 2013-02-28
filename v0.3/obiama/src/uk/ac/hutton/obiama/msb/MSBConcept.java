/*
 * uk.ac.hutton.obiama.msb: MSBConcept.java
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

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLIndividual;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.action.Process;
import uk.ac.hutton.obiama.action.Query;
import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.ConceptDoesNotHaveQueryException;
import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ScheduleException;
import uk.ac.hutton.obiama.model.ObiamaSchedule;

/**
 * MSBConcept
 * 
 * Implementation of the {@link Concept} interface to go with the
 * {@link OWLAPIInferredMSB}
 * 
 * @author Gary Polhill
 */
public class MSBConcept implements Concept {
  /**
   * {@link Var}s this concept has
   */
  private Set<Var> vars;

  /**
   * {@link Query}s this concept responds to
   */
  private Set<Query<?>> queries;

  @SuppressWarnings("rawtypes")
  private Map<Class<? extends Query>, Map<URI, Query<?>>> queryMap;

  /**
   * {@link Creator} {@link ObiamaSchedule}s this concept has when building new
   * instances
   */
  private Set<ObiamaSchedule> creators;

  /**
   * OWL class this concept represents
   */
  private OWLClass concept;

  /**
   * Action requesting this concept
   */
  private Process originator;

  /**
   * The model state broker
   */
  private AbstractModelStateBroker msb;

  /**
   * Constructor -- can only be called from this package
   * 
   * @param concept OWL class this concept represents
   * @param originator Action requesting this object
   * @param msb The model state broker
   */
  @SuppressWarnings("rawtypes")
  MSBConcept(OWLClass concept, Process originator, AbstractModelStateBroker msb) {
    vars = new HashSet<Var>();
    this.concept = concept;
    this.originator = originator;
    this.msb = msb;
    creators = new HashSet<ObiamaSchedule>();
    queries = new HashSet<Query<?>>();
    queryMap = new HashMap<Class<? extends Query>, Map<URI, Query<?>>>();
  }

  /**
   * Constructor passing in creators -- can only be called from this package
   * 
   * @param concept OWL class this concept represents
   * @param originator Action requesting this concept
   * @param msb The model state broker
   * @param creators Set of creator schedules this concept has
   */
  MSBConcept(OWLClass concept, Process originator, AbstractModelStateBroker msb, Set<ObiamaSchedule> creators) {
    this(concept, originator, msb);
    this.creators.addAll(creators);
  }

  /**
   * <!-- getVars -->
   * 
   * @see uk.ac.hutton.obiama.msb.Concept#getVars()
   * @return The {@link Var}s this concept has
   */
  public Set<Var> getVars() {
    return vars;
  }

  /**
   * <!-- getQueries -->
   * 
   * @see uk.ac.hutton.obiama.msb.Concept#getQueries()
   * @return The {@link Query}s this concept has
   */
  public Set<Query<?>> getQueries() {
    return queries;
  }

  /**
   * <!-- addVar -->
   * 
   * Add a {@link Var} to the concept. This is called from the model state
   * broker.
   * 
   * @param var
   */
  void addVar(Var var) {
    vars.add(var);
  }

  /**
   * <!-- addQuery -->
   * 
   * Add a {@link Query} to the concept. This is called from the model state
   * broker.
   * 
   * @param query
   */
  void addQuery(Query<?> query, @SuppressWarnings("rawtypes") Class<? extends Query> queryClass, URI queryID) {
    queries.add(query);
    if(!queryMap.containsKey(queryClass)) {
      queryMap.put(queryClass, new HashMap<URI, Query<?>>());
    }

    queryMap.get(queryClass).put(queryID, query);
  }

  /**
   * <!-- getQuery -->
   * 
   * @see uk.ac.hutton.obiama.msb.Concept#getQuery(java.lang.Class,
   *      java.net.URI)
   * @param queryClass
   * @param queryID
   * @return The query
   * @throws IntegrationInconsistencyException
   */
  @SuppressWarnings("unchecked")
  public <T extends Query<?>> T getQuery(Class<T> queryClass, URI queryID) throws IntegrationInconsistencyException {
    if(queryMap.containsKey(queryClass) && queryMap.get(queryClass).containsKey(queryID)) {
      try {
        return (T)queryMap.get(queryClass).get(queryID);
      }
      catch(ClassCastException e) {
        // The Model State Broker should have checked already that the
        // implementation of the query stipulated in the model structure
        // ontology is a subtype (or equal) of the class requested by the action
        throw new Bug();
      }
    }
    throw new ConceptDoesNotHaveQueryException(originator, concept.getURI(), queryID, queryClass.getName());
  }

  /**
   * <!-- getQuery -->
   * 
   * Return the query associated with the query ID.
   *
   * @see uk.ac.hutton.obiama.msb.Concept#getQuery(java.net.URI)
   * @param queryID
   * @return
   * @throws IntegrationInconsistencyException
   */
  public Query<?> getQuery(URI queryID) throws IntegrationInconsistencyException {
    for(Map<URI, Query<?>> map: queryMap.values()) {
      if(map.containsKey(queryID)) return map.get(queryID);
    }
    throw new ConceptDoesNotHaveQueryException(originator, concept.getURI(), queryID);
  }

  /**
   * <!-- createInstance -->
   * 
   * Create a new instance of this concept with a generated URI.
   * 
   * @see uk.ac.hutton.obiama.msb.Concept#createInstance()
   * @return The instance
   * @throws IntegrationInconsistencyException
   */
  public Instance createInstance() throws IntegrationInconsistencyException {
    return createInstance(msb.createInstanceURI(concept.getURI()));
  }

  /**
   * <!-- createInstance -->
   * 
   * Create a new instance of this concept with the specified URI. The URI of
   * that individual may already exist. This will simply cause the existing
   * individual to be asserted a member of this concept as well as any concept
   * it is already asserted to be a member of.
   * 
   * @see uk.ac.hutton.obiama.msb.Concept#createInstance(java.net.URI)
   * @param individual
   * @return
   * @throws IntegrationInconsistencyException
   */
  public Instance createInstance(URI individual) throws IntegrationInconsistencyException {
    return createInstance(new MSBInstance(individual, originator, msb, this));
  }

  /**
   * <!-- createInstance -->
   * 
   * Create a new instance of this concept using the specified {@link Instance}.
   * The Instance could have just been created, or pre-exist. The purpose of
   * this method is simply to assert that the individual is a member of this
   * concept, and to run any {@link Creator}s needed to initialise its
   * variables. If you don't want Creators to be run, then use
   * {@link #addInstance(URI)} or {@link #addInstance(Instance)} instead.
   * 
   * @see uk.ac.hutton.obiama.msb.Concept#createInstance(uk.ac.hutton.obiama.msb.Instance)
   * @param individual
   * @return
   * @throws IntegrationInconsistencyException
   */
  public Instance createInstance(Instance individual) throws IntegrationInconsistencyException {
    msb.createInstance((Action)originator, (AbstractInstance)individual, this);

    for(ObiamaSchedule creator: creators) {
      try {
        System.out.println("Running creator " + creator.getURI() + " on instance " + individual.getURI());
        creator.runCreator(individual);
      }
      catch(ScheduleException e) {
        ErrorHandler.redo(e, "running creator schedule for " + concept.getURI());
      }
    }

    return individual;
  }

  /**
   * <!-- addInstance -->
   * 
   * Assert that the instance is a member of this concept without running the
   * creators
   * 
   * @see uk.ac.hutton.obiama.msb.Concept#addInstance(java.net.URI)
   * @param individual
   * @return
   * @throws IntegrationInconsistencyException
   */
  public Instance addInstance(URI individual) throws IntegrationInconsistencyException {
    return new MSBInstance(individual, originator, msb, this);
  }

  /**
   * <!-- addInstance -->
   * 
   * Assert that the instance is a member of this concept without running the
   * creators
   * 
   * @see uk.ac.hutton.obiama.msb.Concept#addInstance(uk.ac.hutton.obiama.msb.Instance)
   * @param individual
   * @return
   * @throws IntegrationInconsistencyException
   */
  public Instance addInstance(Instance individual) throws IntegrationInconsistencyException {
    individual.addConcept(this);
    return individual;
  }

  /**
   * <!-- getInstance -->
   * 
   * @see uk.ac.hutton.obiama.msb.Concept#getInstance(java.net.URI)
   * @param individual
   * @return The instance
   * @throws IntegrationInconsistencyException An
   *           {@link IndividualNotInstanceOfConceptException} if the instance
   *           is not already a member of this concept
   */
  public Instance getInstance(URI individual) throws IntegrationInconsistencyException {
    OWLIndividual ind = msb.getIndividual(originator, individual);
    return new MSBInstance(ind.getURI(), originator, msb, this);
  }

  public Set<Instance> getInstances() throws IntegrationInconsistencyException {
    Set<OWLIndividual> individuals = msb.getMembers(originator, concept.getURI());
    Set<Instance> members = new HashSet<Instance>();
    for(OWLIndividual individual: individuals) {
      members.add(getInstance(individual.getURI()));
    }
    return members;
  }

  public void declassifyInstance(Instance individual) throws IntegrationInconsistencyException {
    individual.removeConcept(this);
  }

  public void declassifyInstance(URI individual) throws IntegrationInconsistencyException {
    declassifyInstance(getInstance(individual));
  }

  public void deleteInstance(Instance individual) throws IntegrationInconsistencyException {
    individual.delete();
  }

  public void deleteInstance(URI individual) throws IntegrationInconsistencyException {
    deleteInstance(getInstance(individual));
  }

  public URI getURI() {
    return concept.getURI();
  }

  public boolean subConceptOf(URI superConcept) {
    return msb.getSuperClassesOf(concept.getURI()).contains(superConcept);
  }

  /**
   * <!-- hasInstance -->
   * 
   * @see uk.ac.hutton.obiama.msb.Concept#hasInstance(java.net.URI)
   * @param individual
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public boolean hasInstance(URI individual) throws IntegrationInconsistencyException {
    OWLIndividual ind = msb.getIndividual(originator, individual);
    return msb.getMembers(originator, concept.getURI()).contains(ind);
  }

}
