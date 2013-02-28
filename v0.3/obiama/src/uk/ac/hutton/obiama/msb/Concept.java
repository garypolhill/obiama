/*
 * uk.ac.hutton.obiama.msb: Concept.java
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
import java.util.Set;

import uk.ac.hutton.obiama.action.Query;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;

/**
 * Concept
 * 
 * A Concept is a collection of Vars, with the functionality to create
 * Instances.
 * 
 * @author Gary Polhill
 */
public interface Concept {
  /**
   * <!-- createInstance -->
   * 
   * Create a new instance of the concept, with an arbitrary URI, and initialise
   * its Vars using Creators
   * 
   * @return the Instance
   * @throws IntegrationInconsistencyException
   */
  public Instance createInstance() throws IntegrationInconsistencyException;

  /**
   * <!-- createInstance -->
   * 
   * Assert that the individual with the given URI is a member of this Concept,
   * and initialise its Vars using Creators
   * 
   * @param individual the URI of the individual to create/instantiate
   * @return the Instance
   * @throws IntegrationInconsistencyException
   */
  public Instance createInstance(URI individual) throws IntegrationInconsistencyException;

  /**
   * <!-- createInstance -->
   * 
   * Assert that the Instance is a member of this Concept, and initialise its
   * Vars using Creators
   * 
   * @param individual the instance to instantiate
   * @return the Instance
   * @throws IntegrationInconsistencyException
   */
  public Instance createInstance(Instance individual) throws IntegrationInconsistencyException;

  /**
   * <!-- addInstance -->
   * 
   * Assert that the individual with the given URI is a member of this Concept,
   * but do not run any Creators.
   * 
   * @param individual URI of the individual to be the subject of the assertion
   * @return The individual as an Instance
   * @throws IntegrationInconsistencyException
   */
  public Instance addInstance(URI individual) throws IntegrationInconsistencyException;

  /**
   * <!-- addInstance -->
   * 
   * Assert that the Instance is a member of this Concept, but do not run any
   * Creators.
   * 
   * @param individual Instance that is the subject of the assertion
   * @return The Instance
   * @throws IntegrationInconsistencyException
   */
  public Instance addInstance(Instance individual) throws IntegrationInconsistencyException;

  /**
   * <!-- getInstance -->
   * 
   * Return a pre-existing instance of the concept.
   * 
   * @param individual The URI of the instance to get
   * @return the Instance
   */
  public Instance getInstance(URI individual) throws IntegrationInconsistencyException;

  /**
   * <!-- getInstances -->
   * 
   * @return A set of all instances of this concept
   * @throws IntegrationInconsistencyException
   */
  public Set<Instance> getInstances() throws IntegrationInconsistencyException;

  /**
   * <!-- declassifyInstance -->
   * 
   * Remove the assertion that the instance is a member of this class. Also
   * remove assertions of the properties the instance has where these have
   * domain this class, and assertions in other instances where this instance is
   * the subject of the relation, and the relation has range this class.
   * 
   * @param individual
   * @throws IntegrationInconsistencyException
   */
  public void declassifyInstance(Instance individual) throws IntegrationInconsistencyException;

  /**
   * <!-- declassifyInstance -->
   * 
   * Remove assertions allowing the individual to be inferred a member of this
   * class.
   * 
   * @param individual
   * @throws IntegrationInconsistencyException
   */
  public void declassifyInstance(URI individual) throws IntegrationInconsistencyException;

  /**
   * <!-- deleteInstance -->
   * 
   * Remove all assertions about the individual
   * 
   * @param individual
   * @throws IntegrationInconsistencyException
   */
  public void deleteInstance(Instance individual) throws IntegrationInconsistencyException;

  /**
   * <!-- deleteInstance -->
   * 
   * Remove all assertions about the individual
   * 
   * @param individual
   * @throws IntegrationInconsistencyException
   */
  public void deleteInstance(URI individual) throws IntegrationInconsistencyException;

  /**
   * <!-- hasInstance -->
   * 
   * @param agent
   * @return
   * @throws IntegrationInconsistencyException
   */
  public boolean hasInstance(URI individual) throws IntegrationInconsistencyException;

  /**
   * <!-- getVars -->
   * 
   * @return The set of Vars this Concept has
   */
  public Set<Var> getVars();

  /**
   * <!-- getQueries -->
   * 
   * @return The set of Queries members of this Concept respond to
   */
  public Set<Query<?>> getQueries();

  /**
   * <!-- getQuery -->
   * 
   * Get a query of a specified type and query ID from the concept
   *
   * @param queryClass Class query is expected to have
   * @param queryID ID of query
   * @return The query
   * @throws IntegrationInconsistencyException
   */
  public <T extends Query<?>> T getQuery(Class<T> queryClass, URI queryID) throws IntegrationInconsistencyException;
  
  public Query<?> getQuery(URI queryID) throws IntegrationInconsistencyException;

  /**
   * <!-- getURI -->
   * 
   * @return The URI of this Concept
   */
  public URI getURI();

  /**
   * <!-- subConceptOf -->
   * 
   * @param superConcept
   * @return <code>true</code> iff this concept is a subconcept of the argument
   */
  public boolean subConceptOf(URI superConcept);
}
