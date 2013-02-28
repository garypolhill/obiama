/*
 * uk.ac.hutton.obiama.msb: Instance.java
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
 * <!-- Instance -->
 * 
 * <p>
 * Interface for OBIAMA Instances, which allow {@link Action}s to make
 * assertions about OWL Class membership. Convenience methods are also provided
 * to enable access to properties. Accessing these convenience methods should
 * not break the constraint that a particular {@link Value} can only be accessed
 * once--though this will not cater for cases where the Value is accessed
 * separately (i.e. not through the Instance). The bottom line is: if you are
 * going to access a Value through the Instance, then always do so, and if you
 * are going to access a Value directly from a Var, then don't use the Instance
 * accessor methods.
 * </p>
 * <p>
 * The convenience methods are written to allow access to Values in different
 * modes as provided by the {@link Var} interface:
 * {@link Var#getValueFor(java.net.URI)},
 * {@link Var#getExistingValueFor(java.net.URI)} and
 * {@link Var#getNewValueFor(java.net.URI)}. Once a Value has been accessed in
 * 'New' or 'Existing' mode through this Instance by an Action, it should
 * subsequently only be used with methods having that mode, or with 'default'
 * mode (i.e. neither 'New' nor 'Existing' in the name of the method).
 * </p>
 * 
 * @author Gary Polhill
 */
public interface Instance {
  /**
   * <!-- getURI -->
   * 
   * @return The URI of this instance
   */
  public URI getURI();

  /**
   * <!-- getConcepts -->
   * 
   * @return The Set of concepts this instance has been asserted to be a member
   *         of by the current Action
   */
  public Set<URI> getConcepts();

  /**
   * <!-- hasConcept -->
   * 
   * @param concept
   * @return <code>true</code> if this instance as been asserted to be a member
   *         of the concept by the current Action.
   */
  public boolean hasConcept(Concept concept);

  /**
   * <!-- addConcept -->
   * 
   * Assert that the instance is a member of a concept
   * 
   * @param concept The concept to assert the instance is a member of
   * @throws IntegrationInconsistencyException
   */
  public void addConcept(Concept concept) throws IntegrationInconsistencyException;

  /**
   * <!-- addConcept -->
   * 
   * Assert that the instance is a member of a concept passed as a URI
   * 
   * @param concept URI of the concept to assert the instance is a member of
   * @throws IntegrationInconsistencyException
   */
  public void addConcept(URI concept) throws IntegrationInconsistencyException;

  /**
   * <!-- removeConcept -->
   * 
   * Remove the assertion that the instance is a member of a concept
   * 
   * @param concept
   * @throws IntegrationInconsistencyException
   */
  public void removeConcept(Concept concept) throws IntegrationInconsistencyException;

  /**
   * <!-- removeConcept -->
   * 
   * Remove the assertion that the instance is a member of a concept passed as
   * URI
   * 
   * @param concept URI of the concept to remove the assertion of
   * @throws IntegrationInconsistencyException
   */
  public void removeConcept(URI concept) throws IntegrationInconsistencyException;

  /**
   * <!-- getVars -->
   * 
   * @return The set of {@link Var}s this instance has from the {@link Concept}s
   *         it is a member of
   */
  public Set<Var> getVars();

  /**
   * <!-- getValues -->
   * 
   * Generate a set of {@link Value}s to use from all the {@link Var}s this
   * instance has
   * 
   * @return The set of {@link Value}s
   * @throws IntegrationInconsistencyException
   */
  public Set<Value<?>> getValues() throws IntegrationInconsistencyException;

  /**
   * <!-- getValue -->
   * 
   * Get the {@link Value} for a particular property. The Value is accessed
   * using the {@link Var#getValueFor(java.net.URI)} method.
   * 
   * @param property The property to get the Value of
   * @return The {@link Value}
   * @throws IntegrationInconsistencyException
   */
  public <T> Value<T> getValue(Var property) throws IntegrationInconsistencyException;

  /**
   * <!-- getNewValue -->
   * 
   * Get the {@link Value} for a particular property where the Action expects
   * the Value not currently to be set for this individual. The Value is
   * accessed using the {@link Var#getNewValueFor(java.net.URI)} method.
   * 
   * @param property The property to get the new Value of
   * @return The {@link Value}
   * @throws IntegrationInconsistencyException
   */
  public <T> Value<T> getNewValue(Var property) throws IntegrationInconsistencyException;

  /**
   * <!-- getExistingValue -->
   * 
   * Get the {@link Value} for a particular property where the Action expects
   * the Value to be currently set for this individual. The Value is accessed
   * using the {@link Var#getExistingValueFor(java.net.URI)} method.
   * 
   * @param property The property to get the existing Value of
   * @return The {@link Value}
   * @throws IntegrationInconsistencyException
   */
  public <T> Value<T> getExistingValue(Var property) throws IntegrationInconsistencyException;

  /**
   * <!-- hasValueFor -->
   *
   * @param property The property to check
   * @return <code>true</code> if the individual has a value assigned for this property
   * @throws IntegrationInconsistencyException
   */
  public boolean hasValueFor(Var property) throws IntegrationInconsistencyException;

  /**
   * <!-- getQueries -->
   *
   * @return The Set of {@link uk.ac.hutton.obiama.action.Query}s this individual responds to.
   */
  public Set<Query<?>> getQueries();

  /**
   * <!-- getProperty -->
   *
   * @param property A functional property
   * @return The value of the property for this individual
   * @throws IntegrationInconsistencyException
   */
  public <T> T getProperty(Var property) throws IntegrationInconsistencyException;
  
  public String getPropertyString(Var property) throws IntegrationInconsistencyException;

  /**
   * <!-- getExistingProperty -->
   *
   * @param property A functional property expected to have a value
   * @return The value of the property for this individual
   * @throws IntegrationInconsistencyException
   */
  public <T> T getExistingProperty(Var property) throws IntegrationInconsistencyException;
  
  public String getExistingPropertyString(Var property) throws IntegrationInconsistencyException;

  /**
   * <!-- getPropertyAll -->
   *
   * @param property A non-functional property
   * @param repository A repository to put all the values of the property in
   * @throws IntegrationInconsistencyException
   */
  public <T> void getPropertyAll(Var property, Set<T> repository) throws IntegrationInconsistencyException;
  
  public void getPropertyAllString(Var property, Set<String> repository) throws IntegrationInconsistencyException;

  /**
   * <!-- getExistingPropertyAll -->
   *
   * @param property A non-functional property expected to have values
   * @param repository A repository to put all the values of the property in
   * @throws IntegrationInconsistencyException
   */
  public <T> void getExistingPropertyAll(Var property, Set<T> repository) throws IntegrationInconsistencyException;
  
  public void getExistingPropertyAllString(Var property, Set<String> repository) throws IntegrationInconsistencyException;

  /**
   * <!-- setProperty -->
   * 
   * Set the value for a functional property
   *
   * @param property The functional property to set the value of
   * @param value The value to set it to
   * @throws IntegrationInconsistencyException
   */
  public <T> void setProperty(Var property, T value) throws IntegrationInconsistencyException;
 
  /**
   * <!-- setNewProperty -->
   * 
   * Set the value for a property not expected to already have one
   *
   * @param property The functional property to set the value of
   * @param value The value to set it to
   * @throws IntegrationInconsistencyException
   */
  public <T> void setNewProperty(Var property, T value) throws IntegrationInconsistencyException;
  
  /**
   * <!-- setProperty -->
   * 
   * Set the value for a functional object property
   *
   * @param property The functional object property to set the value of
   * @param value The value to set it to
   * @throws IntegrationInconsistencyException
   */
  public void setProperty(Var property, Instance value) throws IntegrationInconsistencyException;
  
  /**
   * <!-- setNewProperty -->
   * 
   * Set the value for an object property not expected to already have one
   *
   * @param property The functional property to set the value of
   * @param value The value to set it to
   * @throws IntegrationInconsistencyException
   */
  public void setNewProperty(Var property, Instance value) throws IntegrationInconsistencyException;

  /**
   * <!-- setPropertyString -->
   * 
   * Set the value for a property not expected to already have one
   *
   * @param property The functional property to set the value of
   * @param value The string to parse to set the value
   * @throws IntegrationInconsistencyException
   */
  public void setPropertyString(Var property, String value) throws IntegrationInconsistencyException;

  /**
   * <!-- setNewPropertyString -->
   * 
   * Set the value for a property not expected to already have one
   *
   * @param property The functional property to set the value of
   * @param value The string to parse to set the value
   * @throws IntegrationInconsistencyException
   */
  public void setNewPropertyString(Var property, String value) throws IntegrationInconsistencyException;

  /**
   * <!-- unsetProperty -->
   * 
   * Unset the value of the functional property
   *
   * @param property The functional property
   * @throws IntegrationInconsistencyException
   */
  public void unsetProperty(Var property) throws IntegrationInconsistencyException;

  /**
   * <!-- unsetExistingProperty -->
   * 
   * Unset the value of a functional property
   *
   * @param property An existing functional property
   * @throws IntegrationInconsistencyException
   */
  public void unsetExistingProperty(Var property) throws IntegrationInconsistencyException;

  /**
   * <!-- addProperty -->
   * 
   * Add a value to a non-functional property
   *
   * @param property Non-functional property
   * @param value Value to add
   * @throws IntegrationInconsistencyException
   */
  public <T> void addProperty(Var property, T value) throws IntegrationInconsistencyException;

  /**
   * <!-- addExistingProperty -->
   * 
   * Add a value to a non-functional existing property
   *
   * @param property Non-functional existing property
   * @param value Value to add
   * @throws IntegrationInconsistencyException
   */
  public <T> void addExistingProperty(Var property, T value) throws IntegrationInconsistencyException;

  /**
   * <!-- addNewProperty -->
   * 
   * Add a value to a non-functional unset property
   *
   * @param property Non-functional property not set to any value
   * @param value Value to add to it
   * @throws IntegrationInconsistencyException
   */
  public <T> void addNewProperty(Var property, T value) throws IntegrationInconsistencyException;

  /**
   * <!-- addProperty -->
   * 
   * Add a value to a non-functional object property
   *
   * @param property Non-functional object property
   * @param value Value to add to it
   * @throws IntegrationInconsistencyException
   */
  public void addProperty(Var property, Instance value) throws IntegrationInconsistencyException;
  
  /**
   * <!-- addExistingProperty -->
   * 
   * Add a value to a non-functional existing object property
   *
   * @param property Non-functional existing object property
   * @param value Value to add
   * @throws IntegrationInconsistencyException
   */
  public void addExistingProperty(Var property, Instance value) throws IntegrationInconsistencyException;
  
  /**
   * <!-- addNewProperty -->
   * 
   * Add a value to a non-functional unset object property
   *
   * @param property Non-functional object property not set to any value
   * @param value Value to add to it
   * @throws IntegrationInconsistencyException
   */
  public void addNewProperty(Var property, Instance value) throws IntegrationInconsistencyException;
  
  /**
   * <!-- addPropertyString -->
   * 
   * Add a value to a non-functional property
   *
   * @param property Non-functional property
   * @param value String to parse to give the value to add
   * @throws IntegrationInconsistencyException
   */
  public void addPropertyString(Var property, String value) throws IntegrationInconsistencyException;

  /**
   * <!-- addExistingPropertyString -->
   * 
   * Add a value to a non-functional property with some values already set
   *
   * @param property Non-functional existing property
   * @param value String to parse to give the value to add
   * @throws IntegrationInconsistencyException
   */
  public void addExistingPropertyString(Var property, String value) throws IntegrationInconsistencyException;

  /**
   * <!-- addNewPropertyString -->
   * 
   * Add a value to a non-functional property without values already set
   *
   * @param property Non-functional new property
   * @param value String to parse to give the value to add
   * @throws IntegrationInconsistencyException
   */
  public void addNewPropertyString(Var property, String value) throws IntegrationInconsistencyException;

  /**
   * <!-- removeProperty -->
   *
   * Remove a value from a non-functional property
   *
   * @param property Non-functional property
   * @param value Value to remove
   * @return <code>true</code> if the value was found and removed
   * @throws IntegrationInconsistencyException
   */
  public <T> boolean removeProperty(Var property, T value) throws IntegrationInconsistencyException;

  /**
   * <!-- removeExistingProperty -->
   * 
   * Remove a value from a non-functional property with values expected to be set
   *
   * @param property Non-functional existing property
   * @param value Value to remove
   * @return <code>true</code> if the value was found and removed
   * @throws IntegrationInconsistencyException
   */
  public <T> boolean removeExistingProperty(Var property, T value) throws IntegrationInconsistencyException;

  /**
   * <!-- removeProperty -->
   * 
   * Remove a value fom a non-functional object property
   *
   * @param property Non-functional object property
   * @param value Value to remove
   * @return <code>true</code> if the value was found and removed
   * @throws IntegrationInconsistencyException
   */
  public boolean removeProperty(Var property, Instance value) throws IntegrationInconsistencyException;
  
  /**
   * <!-- removeExistingProperty -->
   * 
   * Remove a value from a non-functional object property with values expected to be set
   *
   * @param property Non-functional existing object property
   * @param value Value to remove
   * @return <code>true</code> if the value was found and removed
   * @throws IntegrationInconsistencyException
   */
  public boolean removeExistingProperty(Var property, Instance value) throws IntegrationInconsistencyException;
  
  /**
   * <!-- removePropertyString -->
   * 
   * Remove a value from a non-functional property
   *
   * @param property Non-functional property
   * @param value String to parse to get value to remove
   * @return <code>true</code> if the value was found and removed
   * @throws IntegrationInconsistencyException
   */
  public boolean removePropertyString(Var property, String value) throws IntegrationInconsistencyException;

  /**
   * <!-- removeExistingPropertyString -->
   * 
   * Remove a value from a non-functional property with values expected to be set
   *
   * @param property Non-functional existing property
   * @param value String to parse to get value to remove
   * @return <code>true</code> if the value was found and removed
   * @throws IntegrationInconsistencyException
   */
  public boolean removeExistingPropertyString(Var property, String value) throws IntegrationInconsistencyException;

  /**
   * <!-- clearProperty -->
   * 
   * Remove all values from a non-functional property
   *
   * @param property Non-functional property
   * @throws IntegrationInconsistencyException
   */
  public void clearProperty(Var property) throws IntegrationInconsistencyException;

  /**
   * <!-- clearExistingProperty -->
   * 
   * Remove all values from a non-functional property expected to have values to remove
   *
   * @param property Non-functional existing property
   * @throws IntegrationInconsistencyException
   */
  public void clearExistingProperty(Var property) throws IntegrationInconsistencyException;

  /**
   * <!-- hasProperty -->
   * 
   * Check whether a non-functional property has a value
   *
   * @param property Non-functional property
   * @param value Value to check it has
   * @return <code>true</code> if the property has the value
   * @throws IntegrationInconsistencyException
   */
  public <T> boolean hasProperty(Var property, T value) throws IntegrationInconsistencyException;

  /**
   * <!-- hasProperty -->
   * 
   * Check whether a non-functional object property has a value
   *
   * @param property Non-functional object property
   * @param value Value to check it has
   * @return <code>true</code> if the property has the value
   * @throws IntegrationInconsistencyException
   */
  public boolean hasProperty(Var property, Instance value) throws IntegrationInconsistencyException;
  
  /**
   * <!-- hasPropertyString -->
   * 
   * Check whether a non-functional property has a value
   *
   * @param property Non-functional property
   * @param value String to parse to get value to check
   * @return <code>true</code> if the property has the value
   * @throws IntegrationInconsistencyException
   */
  public boolean hasPropertyString(Var property, String value) throws IntegrationInconsistencyException;

  /**
   * <!-- ask -->
   * 
   * Ask the instance a query
   *
   * @param query Query to ask
   * @param requester Requesting instance
   * @param args Arguments for the query
   * @return The result of the query
   * @throws IntegrationInconsistencyException
   */
  public <T> T ask(Query<T> query, Instance requester, Object... args) throws IntegrationInconsistencyException;
  
  /**
   * <!-- copy -->
   * 
   * Copy the instance
   *
   * @return A copy of the instance
   * @throws IntegrationInconsistencyException
   */
  public Instance copy() throws IntegrationInconsistencyException;

  /**
   * <!-- die -->
   * 
   * Assert that the Instance is no longer an Agent
   *
   * @throws IntegrationInconsistencyException
   */
  public void die() throws IntegrationInconsistencyException;
  
  /**
   * <!-- delete -->
   * 
   * Remove all axioms about the Agent
   *
   * @throws IntegrationInconsistencyException
   */
  public void delete() throws IntegrationInconsistencyException;
}
