/*
 * uk.ac.hutton.obiama.action: ActionFactory.java
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

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.NoSuchProcessImplementationException;
import uk.ac.hutton.obiama.exception.OntologyConfigurationException;
import uk.ac.hutton.obiama.exception.ScheduleException;
import uk.ac.hutton.obiama.model.FCmpOntology;
import uk.ac.hutton.obiama.model.ScheduleOntology;
import uk.ac.hutton.obiama.msb.ModelStateBroker;
import uk.ac.hutton.obiama.msb.XSDHelper;
import uk.ac.hutton.util.FloatingPointComparison;
import uk.ac.hutton.util.LoadClass;
import uk.ac.hutton.util.Reflection;

/**
 * ActionFactory
 * 
 * Build an action.
 * 
 * @author Gary Polhill
 */
@Deprecated
public class ActionFactory {
  private ActionFactory() {
    // disable construction
  }

  /**
   * <!-- buildAction -->
   * 
   * Build an action from an action individual in the schedule ontology. If
   * multiple implementations are available, the first successfully loaded one
   * will be selected.
   * 
   * <!-- A more advanced method would have some sort of policy for selecting
   * among multiple implementations. -->
   * 
   * @param actionURI URI of the action individual to build
   * @param msb ModelStateBroker
   * @param ontology schedule ontology
   * @return The action
   * @throws NoSuchProcessImplementationException
   */
  public static Action buildAction(URI actionURI, ModelStateBroker msb, ScheduleOntology ontology)
      throws NoSuchProcessImplementationException {
    Set<URI> implementations = ontology.getObjectPropertyOf(actionURI, ScheduleOntology.IMPLEMENTED_BY_URI);
    String uriBase = ontology.getStringFunctionalDataPropertyOf(actionURI, ScheduleOntology.URI_BASE_URI);
    String uriExtension = ontology.getStringFunctionalDataPropertyOf(actionURI, ScheduleOntology.URI_EXTENSION_URI);

    Map<String, String> errMap = new HashMap<String, String>();
    Set<URI> tryToLoad = new HashSet<URI>();

    // Try to load the implementations with jars currently available
    for(URI implementation: implementations) {
      String implementationClass =
        ontology.getStringFunctionalDataPropertyOf(implementation, ScheduleOntology.CLASS_NAME_URI);
      if(!Reflection.classPresent(implementationClass)) {
        String jarName = ontology.getStringFunctionalDataPropertyOf(implementation, ScheduleOntology.JAR_FILE_URI);
        if(!Reflection.jarPresent(jarName)) {
          tryToLoad.add(implementation);
          continue;
        }
      }
      Action action = tryAction(implementationClass, uriBase, uriExtension, msb, actionURI, ontology, errMap);
      if(action != null) return action;
    }

    // Try to load implementations by dynamically loading jars and classes
    for(URI implementation: tryToLoad) {
      String implementationClass =
        ontology.getStringFunctionalDataPropertyOf(implementation, ScheduleOntology.CLASS_NAME_URI);
      String jarName = ontology.getStringFunctionalDataPropertyOf(implementation, ScheduleOntology.JAR_FILE_URI);
      Set<URI> jarSearch = ontology.getURIDataPropertyOf(implementation, ScheduleOntology.JAR_FILE_SEARCH_URI);
      Map<URI, Set<Exception>> loadErrors = new HashMap<URI, Set<Exception>>();
      if(LoadClass.classSearch(implementationClass, jarName, jarSearch, loadErrors)) {
        Action action = tryAction(implementationClass, uriBase, uriExtension, msb, actionURI, ontology, errMap);
        if(action != null) return action;
      }
      else {
        errMap.put(implementationClass, buildErrorMessage(loadErrors, implementationClass, jarName));
      }
    }
    throw new NoSuchProcessImplementationException(actionURI, Action.class, errMap);
  }

  /**
   * <!-- buildErrorMessage -->
   * 
   * @param loadErrors Map of URIs looked in to exceptions found
   * @param jarName The name of the JAR file we tried to find
   * @return
   */
  private static String buildErrorMessage(Map<URI, Set<Exception>> loadErrors, String className, String jarName) {
    StringBuffer buff = new StringBuffer("failed to load " + className + " from JAR " + jarName);
    buff.append(loadErrors.size() == 0 ? "." : ":");
    boolean firstURI = true;
    for(URI uri: loadErrors.keySet()) {
      if(!firstURI) buff.append("; ");
      else
        firstURI = false;
      buff.append(" tried " + uri.toString());
      Set<Exception> exceptions = loadErrors.get(uri);
      if(exceptions == null || exceptions.size() == 0) {
        buff.append(", failed for some unknown reason");
        continue;
      }
      buff.append(exceptions.size() == 1 ? ", got error " : ", got errors ");
      boolean firstError = true;
      for(Exception e: exceptions) {
        if(!firstError) buff.append(", ");
        else
          firstError = false;
        buff.append(e.getClass().getName() + "--message \"" + e.getMessage() + "\"");
      }
    }
    return buff.toString();
  }

  /**
   * <!-- tryAction -->
   * 
   * Attempt to build the action, maintaining a list of error messages on
   * failure.
   * 
   * @param implementationClass The name of the class containing the action
   * @param uriBase A base URI for the action ontology
   * @param uriExtension An extension to the URI for the action ontology
   * @param msb The model state broker
   * @param actionURI The action URI in the schedule ontology
   * @param ontology The schedule ontology
   * @param errMap A map of implementation class to error message for trying to
   *          implement it
   * @return A successfully created action, or null on failure
   */
  @SuppressWarnings("unchecked")
  private static Action tryAction(String implementationClass, String uriBase, String uriExtension,
      ModelStateBroker msb, URI actionURI, ScheduleOntology ontology, Map<String, String> errMap) {
    try {
      Class<?> namedClass = Class.forName(implementationClass);
      if(Reflection.classImplements(namedClass, Action.class)) {
        Class<Action> actionClass = (Class<Action>)namedClass;
        return createAction(actionClass, uriBase, uriExtension, msb, actionURI, ontology);
      }
      else {
        errMap.put(implementationClass, "not an Action (does not implement the inferface)");
      }
    }
    catch(ClassNotFoundException e) {
      errMap.put(implementationClass, "class not found");
    }
    catch(InstantiationException e) {
      errMap.put(implementationClass, "could not instiate instance");
    }
    catch(IllegalAccessException e) {
      errMap.put(implementationClass, "security violation");
    }
    catch(IntegrationInconsistencyException e) {
      errMap.put(implementationClass, "caused integration inconsistency: " + e.getMessage());
    }
    catch(ScheduleException e) {
      errMap.put(implementationClass, "caused schedule exception: " + e.getMessage());
    }
    return null;
  }

  /**
   * <!-- createAction -->
   * 
   * Create the action, initialising its parameters.
   * 
   * @param actionClass The class of the action to build
   * @param uriBase Base URI for the action to use for ontological entities it
   *          refers to (possibly null)
   * @param uriExtension Extension to URIs for the action to use for ontological
   *          entities it refers to (possibly null)
   * @param msb ModelStateBroker
   * @param actionURI URI of the action in the schedule ontology
   * @param ontology Schedule ontology
   * @return The action
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws IntegrationInconsistencyException
   * @throws ScheduleException 
   * @throws OntologyConfigurationException
   */
  private static Action createAction(Class<Action> actionClass, String uriBase, String uriExtension,
      ModelStateBroker msb, URI actionURI, ScheduleOntology ontology) throws InstantiationException,
      IllegalAccessException, IntegrationInconsistencyException, ScheduleException {
    Action action = actionClass.newInstance();
    Set<URI> params = ontology.getObjectPropertyOf(actionURI, ScheduleOntology.HAS_PARAMETERS_URI);
    Map<String, ActionParameter> paramMap = new HashMap<String, ActionParameter>();
    for(URI param: params) {
      String paramName = ontology.getStringFunctionalDataPropertyOf(param, ScheduleOntology.PARAMETER_NAME_URI);
      String paramValue = ontology.getStringFunctionalDataPropertyOf(param, ScheduleOntology.PARAMETER_VALUE_URI);
      URI paramType = ontology.getURIFunctionalDataPropertyOf(param, ScheduleOntology.PARAMETER_TYPE_URI);
      Set<String> comments = ontology.getCommentsOnIndividual(param);
      String comment = null;
      if(comments.size() == 1) comment = comments.iterator().next();
      if(paramType == null) {
        paramMap.put(paramName, new ActionParameter(paramName, paramValue, comment));
      }
      else {
        XSDVocabulary paramXSDType = XSDHelper.xsdTypes.get(paramType);
        if(paramXSDType == null) {
          ErrorHandler.warn(new ScheduleException(actionURI, "Unrecognised type " + paramType
            + " specified for parameter " + param), "processing parameters of action " + actionURI,
              "the requested type will be ignored");
          paramMap.put(paramName, new ActionParameter(paramName, paramValue, comment)); 
        }
        else {
          Class<?> paramTypeClass = XSDHelper.recommendedClassFor(paramXSDType);
          paramMap.put(paramName, new ActionParameter(paramName, paramTypeClass, paramValue, comment));
        }
      }
    }

    Set<URI> fcmps = ontology.getObjectPropertyOf(actionURI, ScheduleOntology.USE_FLOATING_POINT_COMPARISON_URI);
    Map<URI, FloatingPointComparison> fcmpMap = new HashMap<URI, FloatingPointComparison>();
    for(URI fcmp: fcmps) {
      URI varURI =
        ontology.getSpecies().isFull() ? ontology.getFunctionalObjectPropertyOf(fcmp,
            ScheduleOntology.COMPARE_PROPERTY_URI) : ontology.getURIFunctionalDataPropertyOf(fcmp,
            ScheduleOntology.COMPARE_PROPERTY_URI);
      URI fcmpURI = ontology.getFunctionalObjectPropertyOf(fcmp, ScheduleOntology.HAS_COMPARISON_METHOD_URI);
      FloatingPointComparison cmp;
      try {
        cmp = FCmpOntology.getFloatingPointComparison(ontology, fcmpURI);
        fcmpMap.put(varURI, cmp);
      }
      catch(OntologyConfigurationException e) {
        ErrorHandler.warn(e, "initialising action " + actionURI + " in ontology " + ontology.getURI(),
            "floating point comparison " + fcmpURI + " will be ignored");
      }
    }
    action.initialise(actionURI, uriBase, uriExtension, msb, paramMap, fcmpMap);
    return action;
  }
}
