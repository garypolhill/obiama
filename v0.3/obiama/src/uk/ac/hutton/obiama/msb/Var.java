/*
 * uk.ac.hutton.obiama.msb: VariableName.java
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
import java.util.Comparator;

import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.NoSuchIndividualException;

/**
 * VariableName
 * 
 * VariableNames are part of the interface between the ontology and the actions.
 * VariableNames store the name of a variable expected by an Action to exist in
 * the ontology.
 * 
 * @author Gary Polhill
 */
public interface Var extends Comparator<Instance> {
  /**
   * <!-- getValueFor -->
   * 
   * Get the value of a variable for an individual
   * 
   * @param <T> The type of the variable (for object properties, this will be
   *          URI)
   * @param individual The individual to get the value of
   * @return The value of the variable for the individual
   * @throws IntegrationInconsistencyException
   */
  public <T> Value<T> getValueFor(URI individual) throws IntegrationInconsistencyException;

  /**
   * <!-- getExistingValueFor -->
   * 
   * Get the value of a variable for an individual, which is expected to exist
   *
   * @param <T> The type of the variable (for object properties, this will be URI)
   * @param individual The individual to get the value of
   * @return The value of the variable for the individual
   * @throws IntegrationInconsistencyException
   */
  public <T> Value<T> getExistingValueFor(URI individual) throws IntegrationInconsistencyException;
  
  /**
   * <!-- getNewValueFor -->
   * 
   * Get an empty value of a variable for an individual. The individual must not
   * currently have a value for that variable.
   * 
   * @param individual The individual to get the value of
   * @return An empty value ready to be set
   * @throws IntegrationInconsistencyException
   */
  public <T> Value<T> getNewValueFor(URI individual) throws IntegrationInconsistencyException;

  /**
   * <!-- hasValueFor -->
   * 
   * Confirm that an individual has a value for this variable
   * 
   * @param individual The individual to check
   * @return true iff the individual has a value for this variable
   * @throws NoSuchIndividualException
   */
  public boolean hasValueFor(URI individual) throws NoSuchIndividualException;

  /**
   * <!-- isFunctional -->
   * 
   * @return true iff the variable is based on a functional OWL property
   */
  public boolean isFunctional();

  /**
   * <!-- isNonFunctional -->
   * 
   * @return true iff the variable is based on a non-functional OWL property
   */
  public boolean isNonFunctional();

  /**
   * <!-- isDataVar -->
   * 
   * @return <code>true</code> iff the variable is based on an OWL data property
   */
  public boolean isDataVar();

  /**
   * <!-- isObjectVar -->
   * 
   * @return <code>true</code> iff the variable is based on an OWL object property
   */
  public boolean isObjectVar();
  
  /**
   * <!-- readOnly -->
   *
   * @return <code>true</code> iff the variable is read-only
   */
  public boolean readOnly();
  
  /**
   * <!-- getType -->
   *
   * @return The XSD type of this Var
   */
  public XSDVocabulary getType();
  
  /**
   * <!-- getJavaType -->
   *
   * @return The recommended Java type of this Var
   */
  public Class<?> getJavaType();
  
  /**
   * <!-- getURI -->
   * 
   * @return The URI of the property this Var represents
   */
  public URI getURI();
}
