/*
 * uk.ac.hutton.obiama.action: AbstractProcess.java
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
package uk.ac.hutton.obiama.action;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ScheduleException;
import uk.ac.hutton.obiama.msb.Concept;
import uk.ac.hutton.obiama.msb.ModelStateBroker;
import uk.ac.hutton.obiama.msb.Var;
import uk.ac.hutton.util.FloatingPointComparison;
import uk.ac.hutton.util.Reflection;

/**
 * <!-- AbstractProcess -->
 * 
 * <p>
 * Partial implementation of Process interface, providing functionality to
 * configure parameters and floating point comparison methods. Empty vars and
 * concepts Sets (together with their accessor methods) are also provided for
 * subclasses to fill. It is normally expected that programmers taking this
 * route to implement the interfaces will subclass from one of the
 * AbstractQuery, AbstractAction or AbstractCreator classes rather than this
 * class.
 * </p>
 * 
 * <p>
 * Subclasses wanting parameters initialised for them should declare all
 * parameters as ActionParameters. The Schedule Ontology will expect the names
 * of the parameter ivars to be used for the names of the parameters.
 * </p>
 * 
 * @author Gary Polhill
 * @see Process
 * @see AbstractQuery
 * @see AbstractAction
 * @see AbstractCreator
 * @see ActionParameter
 * @see uk.ac.hutton.obiama.model.ScheduleOntology
 */
public abstract class AbstractProcess implements Process {
  protected Set<ActionParameter> parameters;
  private Set<Field> myParams;
  protected Map<URI, FloatingPointComparison> fcmps;
  protected URI uri;
  protected ModelStateBroker msb;
  protected Set<Var> vars;
  protected Set<Concept> concepts;
  protected String baseURI;
  protected String extendURI;

  /**
   * Constructor, building the set of accessible fields in this class with
   * ActionParameter type, and initialising other ivars.
   * 
   * @see ActionParameter
   */
  public AbstractProcess() {
    myParams = Reflection.getAccessibleFields(this.getClass(), ActionParameter.class);
    parameters = new HashSet<ActionParameter>();
    concepts = new HashSet<Concept>();
    vars = new HashSet<Var>();
    fcmps = null;
    baseURI = null;
    extendURI = null;
  }

  /**
   * <!-- getConcepts -->
   * 
   * @see uk.ac.hutton.obiama.action.Process#getConcepts()
   * @return The set of concepts this Process declares (or null if none)
   */
  public Set<Concept> getConcepts() {
    return concepts.size() == 0 ? null : concepts;
  }

  /**
   * <!-- getVars -->
   * 
   * @see uk.ac.hutton.obiama.action.Process#getVars()
   * @return The set of Vars this Process declares (or null if none)
   */
  public Set<Var> getVars() {
    return vars.size() == 0 ? null : vars;
  }

  /**
   * <!-- getBaseURI -->
   * 
   * @see uk.ac.hutton.obiama.action.Process#getBaseURI()
   * @return The base URI this Process was configured with
   */
  public String getBaseURI() {
    return baseURI;
  }

  /**
   * <!-- getURIExtension -->
   * 
   * @see uk.ac.hutton.obiama.action.Process#getURIExtension()
   * @return The URI extension this Process was configured with
   */
  public String getURIExtension() {
    return extendURI;
  }

  /**
   * <!-- getFCmp -->
   * 
   * @see uk.ac.hutton.obiama.action.Process#getFCmp(uk.ac.hutton.obiama.msb.Var)
   * @param var
   * @return The floating point comparison method to use for the specified Var
   */
  public FloatingPointComparison getFCmp(Var var) {
    return fcmps.get(var.getURI());
  }

  /**
   * <!-- getParameters -->
   * 
   * @see uk.ac.hutton.obiama.action.Process#getParameters()
   * @return The set of parameters this Process uses.
   */
  public Set<ActionParameter> getParameters() {
    return parameters;
  }

  /**
   * <!-- getURI -->
   * 
   * @see uk.ac.hutton.obiama.action.Process#getURI()
   * @return The URI of this Process
   */
  public URI getURI() {
    return uri;
  }

  public Process getOriginator() {
    return null;
  }

  /**
   * <!-- getModelStateBroker -->
   * 
   * @see uk.ac.hutton.obiama.action.Process#getModelStateBroker()
   * @return The Model State Broker used by this Process
   */
  public ModelStateBroker getModelStateBroker() {
    return msb;
  }

  /**
   * <!-- getVar -->
   * 
   * Convenience method to obtain a Var from the MSB
   * 
   * @param property
   * @return
   * @throws IntegrationInconsistencyException
   */
  protected final Var getVar(URI property) throws IntegrationInconsistencyException {
    Var var = msb.getVariableName(getURIFor(property), this);
    addVar(var);
    return var;
  }

  /**
   * <!-- getVar -->
   * 
   * Convenience method to obtain a Var with a stipulated range from the MSB
   * 
   * @param property
   * @param range
   * @return
   * @throws IntegrationInconsistencyException
   */
  protected final Var getVar(URI property, URI range) throws IntegrationInconsistencyException {
    Var var = msb.getVariableName(getURIFor(property), getURIFor(range), this);
    addVar(var);
    return var;
  }

  /**
   * <!-- getVar -->
   * 
   * Convenience method to obtain a Var with a stipulated range from the MSB
   * 
   * @param property
   * @param range
   * @return
   * @throws IntegrationInconsistencyException
   */
  protected final Var getVar(URI property, XSDVocabulary range) throws IntegrationInconsistencyException {
    Var var = msb.getVariableName(getURIFor(property), range, this);
    addVar(var);
    return var;
  }

  /**
   * <!-- getVar -->
   * 
   * Convenience method to obtain a Var with a stipulated domain and range from
   * the MSB
   * 
   * @param property
   * @param domain
   * @param range
   * @return
   * @throws IntegrationInconsistencyException
   */
  protected final Var getVar(URI property, URI domain, URI range) throws IntegrationInconsistencyException {
    Var var = msb.getVariableName(getURIFor(property), getURIFor(domain), getURIFor(range), this);
    addVar(var);
    return var;
  }

  /**
   * <!-- getVar -->
   * 
   * Convenience method to obtain a Var with a stipulated domain and range from
   * the MSB
   * 
   * @param property
   * @param domain
   * @param range
   * @return
   * @throws IntegrationInconsistencyException
   */
  protected final Var getVar(URI property, URI domain, XSDVocabulary range) throws IntegrationInconsistencyException {
    Var var = msb.getVariableName(getURIFor(property), getURIFor(domain), range, this);
    addVar(var);
    return var;
  }

  /**
   * <!-- getConcept -->
   * 
   * Convenience method to obtain a concept from the MSB
   * 
   * @param concept
   * @return
   * @throws IntegrationInconsistencyException
   */
  protected final Concept getConcept(URI concept) throws IntegrationInconsistencyException {
    return getConcept(concept, (Set<Var>)null, null);
  }

  /**
   * <!-- getConcept -->
   * 
   * Convenience method to obtain a concept with a set of vars it is in the
   * domain of from the MSB
   * 
   * @param concept
   * @param vars
   * @return
   * @throws IntegrationInconsistencyException
   */
  protected final Concept getConcept(URI concept, Set<Var> vars) throws IntegrationInconsistencyException {
    return getConcept(concept, vars, null);
  }

  /**
   * <!-- getConcept -->
   * 
   * Convenience method to obtain a concept with no vars but a single query
   * 
   * @param concept
   * @param queryID
   * @param queryClass
   * @return
   * @throws IntegrationInconsistencyException
   */
  protected final Concept getConcept(URI concept, URI queryID,
      @SuppressWarnings("rawtypes") Class<? extends Query> queryClass) throws IntegrationInconsistencyException {
    return getConcept(concept, null, queryID, queryClass);
  }

  /**
   * <!-- getConcept -->
   * 
   * Convenience method to obtain a concept with no vars and multiple queries
   * 
   * @param concept
   * @param queryID
   * @param queryClass
   * @return
   * @throws IntegrationInconsistencyException
   */
  protected final Concept getConcept(URI concept, URI[] queryID,
      @SuppressWarnings("rawtypes") Class<? extends Query>... queryClass) throws IntegrationInconsistencyException {
    return getConcept(concept, null, queryID, queryClass);
  }

  /**
   * <!-- getConcept -->
   * 
   * Get a concept with some queries it is expected to have, but no vars
   * 
   * @param concept
   * @param queries
   * @return
   * @throws IntegrationInconsistencyException
   */
  protected final Concept getConcept(URI concept, @SuppressWarnings("rawtypes") Map<URI, Class<? extends Query>> queries)
      throws IntegrationInconsistencyException {
    return getConcept(concept, null, queries);
  }

  /**
   * <!-- getConcept -->
   * 
   * Convenience method to get a concept with a single query
   * 
   * @param concept
   * @param queryID
   * @param queryClass
   * @return
   * @throws IntegrationInconsistencyException
   */
  @SuppressWarnings("rawtypes")
  protected final Concept getConcept(URI concept, Set<Var> vars, URI queryID, Class<? extends Query> queryClass)
      throws IntegrationInconsistencyException {
    Map<URI, Class<? extends Query>> queryMap = new HashMap<URI, Class<? extends Query>>();
    queryMap.put(getURIFor(queryID), queryClass);
    return getConcept(concept, vars, queryMap);
  }

  /**
   * <!-- getConcept -->
   * 
   * Convenience method to get a concept with multiple queries
   * 
   * @param concept
   * @param vars
   * @param queryID
   * @param queryClass
   * @return
   * @throws IntegrationInconsistencyException
   */
  @SuppressWarnings("rawtypes")
  protected final Concept getConcept(URI concept, Set<Var> vars, URI[] queryID, Class<? extends Query>... queryClass)
      throws IntegrationInconsistencyException {
    if(queryID.length != queryClass.length) {
      throw new Bug("Process " + this.getClass().getName() + " attempts to get a concept with " + queryID.length
        + " query IDs and " + queryClass.length + " query classes, when these numbers must be the same");
    }

    Map<URI, Class<? extends Query>> queryMap = new HashMap<URI, Class<? extends Query>>();

    for(int i = 0; i < queryID.length; i++) {
      queryMap.put(getURIFor(queryID[i]), queryClass[i]);
    }

    return getConcept(concept, vars, queryMap);
  }

  /**
   * <!-- getConcept -->
   * 
   * Convenience method to obtain a concept with a set of vars it is in the
   * domain of and a set of queries it responds to from the MSB
   * 
   * @param concept
   * @param vars
   * @param queries
   * @return
   * @throws IntegrationInconsistencyException
   */
  protected final Concept getConcept(URI concept, Set<Var> vars,
      @SuppressWarnings("rawtypes") Map<URI, Class<? extends Query>> queries) throws IntegrationInconsistencyException {
    Concept conc = msb.getConcept(getURIFor(concept), this, vars, queries);
    addConcept(conc);
    return conc;
  }

  /**
   * <!-- addVar -->
   * 
   * Add a Var to this Process's list of Vars
   * 
   * @param var
   */
  protected final void addVar(Var var) {
    vars.add(var);
  }

  /**
   * <!-- addVars -->
   * 
   * Add lots of Vars to this Process's list of Vars
   * 
   * @param vars
   */
  protected final void addVars(Var... vars) {
    for(Var var: vars) {
      addVar(var);
    }
  }

  /**
   * <!-- addConcept -->
   * 
   * Add a concept to this process's list of concepts
   * 
   * @param concept
   */
  protected final void addConcept(Concept concept) {
    concepts.add(concept);
  }

  /**
   * <!-- addConcepts -->
   * 
   * Add lots of concepts to this process's list of concepts
   * 
   * @param concepts
   */
  protected final void addConcepts(Concept... concepts) {
    for(Concept concept: concepts) {
      addConcept(concept);
    }
  }

  /**
   * <!-- initialiseURIs -->
   * 
   * Initialise the URIs for this process and assign the ModelStateBroker
   * 
   * @param msb The model state broker
   * @param uri URI of the process in the schedule ontology
   * @param uriBase Base URI of the process
   * @param uriExtension URI extension of the process
   */
  protected void initialiseURIs(ModelStateBroker msb, URI uri, String uriBase, String uriExtension) {
    this.uri = uri;
    this.msb = msb;
    baseURI = uriBase;
    // if(baseURI == null) baseURI = msb.getBaseURI().toString();
    extendURI = uriExtension;
  }

  /**
   * <!-- initialiseParameters -->
   * 
   * Initialise the set of parameters for this Process. For each parameter in
   * inputParams, it expects there to be an instance variable in the Process
   * having the same name with type ActionParameter, and vice versa.
   * 
   * @param inputParams Map of String to ActionParameter defining the parameters
   *          for this Process
   * @throws ScheduleException
   */
  protected void initialiseParameters(Map<String, ActionParameter> inputParams) throws ScheduleException {
    Set<String> myParamNames = new HashSet<String>();
    for(Field paramField: myParams) {
      myParamNames.add(paramField.getName());
      try {
        ActionParameter actionParam = (ActionParameter)paramField.get(this);
        if(actionParam == null) {
          throw new Bug("Process " + getClass().getName() + " has not initialised ActionParameter "
            + paramField.getName());
        }
        if(inputParams.containsKey(paramField.getName())) {
          actionParam.set(inputParams.get(paramField.getName()));
          parameters.add(actionParam);
        }
        else if(inputParams.containsKey(actionParam.getParameterName())) {
          actionParam.set(inputParams.get(actionParam.getParameterName()));
          parameters.add(actionParam);
        }
        else if(!actionParam.parameterSet()) {
          throw new ScheduleException(getURI(), "No value specified for parameter " + actionParam.getParameterName()
            + " and no default value available");
          // TODO Throw an unspecified parameter exception

          // TODO No. Actually I think this should work a bit differently.
          // Actions should create their ActionParameters during Construction,
          // assigning them default values (if appropriate), names, comments and
          // types.
          // ActionParameters should know whether they have had their value set
          // or not, and throw the exception I would have thrown here when asked
          // for a value if none has been specified.
        }
        else {
          ErrorHandler.note("Setting parameter " + actionParam.getParameterName() + " to default value "
            + actionParam.getParameter() + " in action " + uri);
        }
      }
      catch(IllegalArgumentException e) {
        throw new Bug();
      }
      catch(IllegalAccessException e) {
        throw new Bug();
      }
    }
    for(String paramName: inputParams.keySet()) {
      if(!myParamNames.contains(paramName)) {
        // TODO Throw an unrecognised parameter exception
      }
    }
  }

  /**
   * <!-- initialiseFCmps -->
   * 
   * Define the set of floating point comparisons this Process uses
   * 
   * @param fcmps Map of URI to FloatingPointComparison method defining how
   *          floating point comparisons are to be handled by this Process.
   */
  protected void initialiseFCmps(Map<URI, FloatingPointComparison> fcmps) {
    this.fcmps = fcmps;
  }

  /**
   * <!-- initialise -->
   * 
   * Main initialisation method for the Process. It is expected that this will
   * be called from an initialisation method specific to the Action, Query or
   * Creator.
   * 
   * @see uk.ac.hutton.obiama.action.Process#initialise(java.net.URI,
   *      uk.ac.hutton.obiama.msb.ModelStateBroker, java.util.Map,
   *      java.util.Map)
   * @see uk.ac.hutton.obiama.model.ScheduleOntology
   * @param uri The URI of this Process (in the Schedule)
   * @param uriBase Base URI to use for ontological entities in this Process
   * @param uriExtension Extension to URI to use for ontological entities in
   *          this Process
   * @param msb The Model State Broker
   * @param inputParams Parameters for this Process
   * @param fcmps Floating point comparison methods for this Process
   * @throws IntegrationInconsistencyException
   * @throws ScheduleException
   */
  public void initialise(URI uri, String uriBase, String uriExtension, ModelStateBroker msb,
      Map<String, ActionParameter> inputParams, Map<URI, FloatingPointComparison> fcmps)
      throws IntegrationInconsistencyException, ScheduleException {
    initialiseURIs(msb, uri, uriBase, uriExtension);
    initialiseParameters(inputParams);
    initialiseFCmps(fcmps);
    initialiseLocal();
  }

  public abstract void initialiseLocal() throws IntegrationInconsistencyException;

  /**
   * <!-- buildURI -->
   * 
   * Construct a URI from a column or row heading
   * 
   * @param name the column or row heading
   * @return a URI with that name in the ontology, or <code>null</code> if there
   *         was a problem
   */
  protected URI buildURI(String name) {
    if(name.startsWith("#")) {
      Set<URI> names = msb.find(name);
      if(names.size() == 1) {
        return names.iterator().next();
      }
      else {
        ErrorHandler.warn("ambiguous name specification: " + name + ", which matches " + names, "building URI",
            "data pertaining to this name will be ignored");
        return null;
      }
    }
    else {
      try {
        return new URI(name);
      }
      catch(URISyntaxException e) {
        ErrorHandler.warn(e, "building URI", "data pertaining to this name will be ignored");
        return null;
      }
    }

  }

  /**
   * <!-- getURIFor -->
   * 
   * A method providing a URI to use for an ontological entity. The entity is
   * expected to be a fully qualified URI, including scheme and path. If the
   * baseURI for this action is specified, the method will over-write the
   * entity's baseURI. If the extendURI for this action is specified, this will
   * extend whatever is this action's base URI as given above. The fragment will
   * not be changed.
   * 
   * @param entity URI to convert
   * @return converted URI
   */
  protected URI getURIFor(URI entity) {
    return Process.Adjust.URI(entity, baseURI, extendURI);
  }

}
