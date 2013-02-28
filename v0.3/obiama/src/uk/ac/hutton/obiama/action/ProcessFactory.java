/*
 * uk.ac.hutton.obiama.Action: ProcessFactory.java
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
 * ProcessFactory
 * 
 * Build a Process -- specifically, an Action or a Query.
 * 
 * @author Gary Polhill
 */
public class ProcessFactory {
  private ProcessFactory() {
    // disable construction
  }

  /**
   * <!-- buildProcess -->
   * 
   * Build an Process from an Process individual in the schedule ontology. If
   * multiple implementations are available, the first successfully loaded one
   * will be selected.
   * 
   * <!-- A more advanced method would have some sort of policy for selecting
   * among multiple implementations. -->
   * 
   * @param processURI URI of the Process individual to build
   * @param msb ModelStateBroker
   * @param ontology schedule ontology
   * @param processClass the subclass of Process to build
   * @return The Process
   * @throws NoSuchProcessImplementationException
   */
  public static <P extends Process> P buildProcess(URI processURI, ModelStateBroker msb, ScheduleOntology ontology,
      Class<P> processClass) throws NoSuchProcessImplementationException {
    System.out.println("Building process " + processURI);
    Set<URI> implementations = ontology.getObjectPropertyOf(processURI, ScheduleOntology.IMPLEMENTED_BY_URI);
    String uriBase = ontology.getStringFunctionalDataPropertyOf(processURI, ScheduleOntology.URI_BASE_URI);
    String uriExtension = ontology.getStringFunctionalDataPropertyOf(processURI, ScheduleOntology.URI_EXTENSION_URI);

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
      P process = tryProcess(implementationClass, uriBase, uriExtension, msb, processURI, ontology, errMap);
      if(process != null) return process;
    }

    // Try to load implementations by dynamically loading jars and classes
    for(URI implementation: tryToLoad) {
      String implementationClass =
        ontology.getStringFunctionalDataPropertyOf(implementation, ScheduleOntology.CLASS_NAME_URI);
      String jarName = ontology.getStringFunctionalDataPropertyOf(implementation, ScheduleOntology.JAR_FILE_URI);
      Set<URI> jarSearch = ontology.getURIDataPropertyOf(implementation, ScheduleOntology.JAR_FILE_SEARCH_URI);
      Map<URI, Set<Exception>> loadErrors = new HashMap<URI, Set<Exception>>();
      if(LoadClass.classSearch(implementationClass, jarName, jarSearch, loadErrors)) {
        P process = tryProcess(implementationClass, uriBase, uriExtension, msb, processURI, ontology, errMap);
        if(process != null) return process;
      }
      else {
        errMap.put(implementationClass, buildErrorMessage(loadErrors, implementationClass, jarName));
      }
    }
    throw new NoSuchProcessImplementationException(processURI, processClass, errMap);
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
   * <!-- tryProcess -->
   * 
   * Attempt to build the Process, maintaining a list of error messages on
   * failure.
   * 
   * @param implementationClass The name of the class containing the Process
   * @param uriBase A base URI for the Process ontology
   * @param uriExtension An extension to the URI for the Process ontology
   * @param msb The model state broker
   * @param ProcessURI The Process URI in the schedule ontology
   * @param ontology The schedule ontology
   * @param errMap A map of implementation class to error message for trying to
   *          implement it
   * @return A successfully created Process, or null on failure
   */
  @SuppressWarnings("unchecked")
  private static <P extends Process> P tryProcess(String implementationClass, String uriBase, String uriExtension,
      ModelStateBroker msb, URI ProcessURI, ScheduleOntology ontology, Map<String, String> errMap) {
    try {
      Class<?> namedClass = Class.forName(implementationClass);
      if(Reflection.classImplements(namedClass, Process.class)) {
        Class<P> ProcessClass = (Class<P>)namedClass;
        return createProcess(ProcessClass, uriBase, uriExtension, msb, ProcessURI, ontology);
      }
      else {
        errMap.put(implementationClass, "not a process (does not implement the inferface)");
      }
    }
    catch(ClassNotFoundException e) {
      errMap.put(implementationClass, "class not found");
    }
    catch(InstantiationException e) {
      errMap.put(implementationClass, "could not instantiate instance");
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
   * <!-- createProcess -->
   * 
   * Create the Process, initialising its parameters.
   * 
   * @param processClass The subclass of Process to build
   * @param uriBase Base URI for the Process to use for ontological entities it
   *          refers to (possibly null)
   * @param uriExtension Extension to URIs for the Process to use for
   *          ontological entities it refers to (possibly null)
   * @param msb ModelStateBroker
   * @param processURI URI of the Process in the schedule ontology
   * @param ontology Schedule ontology
   * @return The Process
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws IntegrationInconsistencyException
   * @throws ScheduleException
   * @throws OntologyConfigurationException
   */
  private static <P extends Process> P createProcess(Class<P> processClass, String uriBase, String uriExtension,
      ModelStateBroker msb, URI processURI, ScheduleOntology ontology) throws InstantiationException,
      IllegalAccessException, IntegrationInconsistencyException, ScheduleException {
    P process = processClass.newInstance();
    Set<URI> params = ontology.getObjectPropertyOf(processURI, ScheduleOntology.HAS_PARAMETERS_URI);
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
          ErrorHandler.warn(new ScheduleException(processURI, "Unrecognised type " + paramType
            + " specified for parameter " + param), "processing parameters of Process " + processURI,
              "the requested type will be ignored");
          paramMap.put(paramName, new ActionParameter(paramName, paramValue, comment));
        }
        else {
          Class<?> paramTypeClass = XSDHelper.recommendedClassFor(paramXSDType);
          paramMap.put(paramName, new ActionParameter(paramName, paramTypeClass, paramValue, comment));
        }
      }
    }

    Set<URI> fcmps = ontology.getObjectPropertyOf(processURI, ScheduleOntology.USE_FLOATING_POINT_COMPARISON_URI);
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
        ErrorHandler.warn(e, "initialising Process " + processURI + " in ontology " + ontology.getURI(),
            "floating point comparison " + fcmpURI + " will be ignored");
      }
    }
    process.initialise(processURI, uriBase, uriExtension, msb, paramMap, fcmpMap);
    return process;
  }
}
