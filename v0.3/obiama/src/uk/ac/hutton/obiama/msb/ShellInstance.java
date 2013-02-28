/*
 * uk.ac.hutton.obiama.msb: ShellInstance.java
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
import java.util.Collection;
import java.util.Set;

import uk.ac.hutton.obiama.action.Query;
import uk.ac.hutton.obiama.action.Process;
import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ModificationOfLockedInstanceException;

/**
 * <!-- ShellInstance -->
 * 
 * An instance created when a {@link uk.ac.hutton.obiama.action.Process}
 * requires access to a value that has already been registered. This allows
 * read-only access to the instance. This just means access to the class
 * assertions made about the instance--all the {@link Value}s can be accessed
 * normally, though of course, if any of them have been already accessed, then
 * they will be returned as {@link ShellValue} s. Likewise, if the process that
 * earlier accessed this instance subsequently tries to access a Value for it
 * that has been accessed from this instance, then the earlier process will get
 * a ShellValue returned.
 * 
 * @author Gary Polhill
 */
public class ShellInstance extends AbstractInstance implements Instance {

  /**
   * Instance this instance is shadowing
   */
  AbstractInstance shadowInstance;

  /**
   * @param process Process trying to make a duplicate access to the instance
   * @param instance The original instance trying to access this one.
   */
  public ShellInstance(Process process, AbstractInstance instance) {
    super(process);
    this.shadowInstance = instance;
  }

  /**
   * <!-- update -->
   * 
   * If this method is called, then there is a bug. No ShellInstance should be
   * registered with the MSB for update.
   * 
   * @see uk.ac.hutton.obiama.msb.AbstractInstance#update(uk.ac.hutton.obiama.msb.AbstractModelStateBroker)
   * @param msb
   * @throws IntegrationInconsistencyException
   */
  @Override
  void update(AbstractModelStateBroker msb) throws IntegrationInconsistencyException {
    throw new Bug();
  }

  /**
   * <!-- addConcept -->
   * 
   * Attempt made to modify locked instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addConcept(uk.ac.hutton.obiama.msb.Concept)
   * @param concept
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void addConcept(Concept concept) throws IntegrationInconsistencyException {
    throw new ModificationOfLockedInstanceException(process, shadowInstance.getURI());
  }

  /**
   * <!-- addConcept -->
   * 
   * Attempt made to modify locked instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addConcept(java.net.URI)
   * @param concept
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void addConcept(URI concept) throws IntegrationInconsistencyException {
    throw new ModificationOfLockedInstanceException(process, shadowInstance.getURI());
  }

  /**
   * <!-- addExistingProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addExistingProperty(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.Object)
   * @param <T>
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public <T> void addExistingProperty(Var property, T value) throws IntegrationInconsistencyException {
    shadowInstance.addExistingProperty(property, value);
  }

  /**
   * <!-- addExistingProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addExistingProperty(uk.ac.hutton.obiama.msb.Var,
   *      uk.ac.hutton.obiama.msb.Instance)
   * @param <T>
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void addExistingProperty(Var property, Instance value) throws IntegrationInconsistencyException {
    shadowInstance.addExistingProperty(property, value);
  }

  /**
   * <!-- addExistingPropertyString -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addExistingPropertyString(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.String)
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void addExistingPropertyString(Var property, String value) throws IntegrationInconsistencyException {
    shadowInstance.addExistingPropertyString(property, value);
  }

  /**
   * <!-- addNewProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addNewProperty(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.Object)
   * @param <T>
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public <T> void addNewProperty(Var property, T value) throws IntegrationInconsistencyException {
    shadowInstance.addNewProperty(property, value);
  }

  /**
   * <!-- addNewProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addNewProperty(uk.ac.hutton.obiama.msb.Var,
   *      uk.ac.hutton.obiama.msb.Instance)
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void addNewProperty(Var property, Instance value) throws IntegrationInconsistencyException {
    shadowInstance.addNewProperty(property, value);
  }

  /**
   * <!-- addNewPropertyString -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addNewPropertyString(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.String)
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void addNewPropertyString(Var property, String value) throws IntegrationInconsistencyException {
    shadowInstance.addNewPropertyString(property, value);
  }

  /**
   * <!-- addProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addProperty(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.Object)
   * @param <T>
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public <T> void addProperty(Var property, T value) throws IntegrationInconsistencyException {
    shadowInstance.addProperty(property, value);
  }

  /**
   * <!-- addProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addProperty(uk.ac.hutton.obiama.msb.Var,
   *      uk.ac.hutton.obiama.msb.Instance)
   * @param <T>
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void addProperty(Var property, Instance value) throws IntegrationInconsistencyException {
    shadowInstance.addProperty(property, value);
  }

  /**
   * <!-- addPropertyString -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addPropertyString(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.String)
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void addPropertyString(Var property, String value) throws IntegrationInconsistencyException {
    shadowInstance.addPropertyString(property, value);
  }

  /**
   * <!-- ask -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#ask(uk.ac.hutton.obiama.action.Query,
   *      uk.ac.hutton.obiama.msb.Instance, java.lang.Object[])
   * @param <T>
   * @param query
   * @param requester
   * @param args
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public <T> T ask(Query<T> query, Instance requester, Object... args) throws IntegrationInconsistencyException {
    return shadowInstance.ask(query, requester, args);
  }

  /**
   * <!-- clearExistingProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#clearExistingProperty(uk.ac.hutton.obiama.msb.Var)
   * @param property
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void clearExistingProperty(Var property) throws IntegrationInconsistencyException {
    shadowInstance.clearExistingProperty(property);
  }

  /**
   * <!-- clearProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#clearProperty(uk.ac.hutton.obiama.msb.Var)
   * @param property
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void clearProperty(Var property) throws IntegrationInconsistencyException {
    shadowInstance.clearProperty(property);
  }

  /**
   * <!-- die -->
   * 
   * Attempt made to modify locked instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#die()
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void die() throws IntegrationInconsistencyException {
    throw new ModificationOfLockedInstanceException(process, shadowInstance.getURI());
  }
  
  /**
   * <!-- delete -->
   * 
   * Attempt made to modify locked instance
   *
   * @see uk.ac.hutton.obiama.msb.Instance#delete()
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void delete() throws IntegrationInconsistencyException {
    throw new ModificationOfLockedInstanceException(process, shadowInstance.getURI());
  }

  /**
   * <!-- getConcepts -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getConcepts()
   * @return
   */
  @Override
  public Set<URI> getConcepts() {
    return shadowInstance.getConcepts();
  }

  /**
   * <!-- getExistingProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getExistingProperty(uk.ac.hutton.obiama.msb.Var)
   * @param <T>
   * @param property
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public <T> T getExistingProperty(Var property) throws IntegrationInconsistencyException {
    return shadowInstance.getExistingProperty(property);
  }

  /**
   * <!-- getExistingPropertyString -->
   *
   * @see uk.ac.hutton.obiama.msb.Instance#getExistingPropertyString(uk.ac.hutton.obiama.msb.Var)
   * @param property
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public String getExistingPropertyString(Var property) throws IntegrationInconsistencyException {
    return shadowInstance.getExistingPropertyString(property);
  }

  /**
   * <!-- getExistingPropertyAll -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getExistingPropertyAll(uk.ac.hutton.obiama.msb.Var,
   *      java.util.Set)
   * @param <T>
   * @param property
   * @param repository
   * @throws IntegrationInconsistencyException
   */
  @Override
  public <T> void getExistingPropertyAll(Var property, Set<T> repository) throws IntegrationInconsistencyException {
    shadowInstance.getExistingPropertyAll(property, repository);
  }

  /**
   * <!-- getExistingPropertyAllString -->
   *
   * @see uk.ac.hutton.obiama.msb.Instance#getExistingPropertyAllString(uk.ac.hutton.obiama.msb.Var, java.util.Set)
   * @param property
   * @param repository
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void getExistingPropertyAllString(Var property, Set<String> repository)
      throws IntegrationInconsistencyException {
    shadowInstance.getExistingPropertyAllString(property, repository);
  }

  /**
   * <!-- getExistingValue -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getExistingValue(uk.ac.hutton.obiama.msb.Var)
   * @param <T>
   * @param property
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public <T> Value<T> getExistingValue(Var property) throws IntegrationInconsistencyException {
    return shadowInstance.getExistingValue(property);
  }

  /**
   * <!-- getNewValue -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getNewValue(uk.ac.hutton.obiama.msb.Var)
   * @param <T>
   * @param property
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public <T> Value<T> getNewValue(Var property) throws IntegrationInconsistencyException {
    return shadowInstance.getNewValue(property);
  }

  /**
   * <!-- getProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getProperty(uk.ac.hutton.obiama.msb.Var)
   * @param <T>
   * @param property
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public <T> T getProperty(Var property) throws IntegrationInconsistencyException {
    return shadowInstance.getProperty(property);
  }
  

  /**
   * <!-- getPropertyString -->
   *
   * @see uk.ac.hutton.obiama.msb.Instance#getPropertyString(uk.ac.hutton.obiama.msb.Var)
   * @param property
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public String getPropertyString(Var property) throws IntegrationInconsistencyException {
    return shadowInstance.getPropertyString(property);
  }


  /**
   * <!-- getPropertyAll -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getPropertyAll(uk.ac.hutton.obiama.msb.Var,
   *      java.util.Set)
   * @param <T>
   * @param property
   * @param repository
   * @throws IntegrationInconsistencyException
   */
  @Override
  public <T> void getPropertyAll(Var property, Set<T> repository) throws IntegrationInconsistencyException {
    shadowInstance.getPropertyAll(property, repository);
  }

  /**
   * <!-- getPropertyAllString -->
   *
   * @see uk.ac.hutton.obiama.msb.Instance#getPropertyAllString(uk.ac.hutton.obiama.msb.Var, java.util.Set)
   * @param property
   * @param repository
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void getPropertyAllString(Var property, Set<String> repository) throws IntegrationInconsistencyException {
    shadowInstance.getPropertyAllString(property, repository);
  }

  /**
   * <!-- getQueries -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getQueries()
   * @return
   */
  @Override
  public Set<Query<?>> getQueries() {
    return shadowInstance.getQueries();
  }

  /**
   * <!-- getURI -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getURI()
   * @return
   */
  @Override
  public URI getURI() {
    return shadowInstance.getURI();
  }

  /**
   * <!-- getValue -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getValue(uk.ac.hutton.obiama.msb.Var)
   * @param <T>
   * @param property
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public <T> Value<T> getValue(Var property) throws IntegrationInconsistencyException {
    return shadowInstance.getValue(property);
  }

  /**
   * <!-- getValues -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getValues()
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public Set<Value<?>> getValues() throws IntegrationInconsistencyException {
    return shadowInstance.getValues();
  }

  /**
   * <!-- getVars -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getVars()
   * @return
   */
  @Override
  public Set<Var> getVars() {
    return shadowInstance.getVars();
  }

  /**
   * <!-- hasConcept -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#hasConcept(uk.ac.hutton.obiama.msb.Concept)
   * @param concept
   * @return
   */
  @Override
  public boolean hasConcept(Concept concept) {
    return shadowInstance.hasConcept(concept);
  }

  /**
   * <!-- hasProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#hasProperty(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.Object)
   * @param <T>
   * @param property
   * @param value
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public <T> boolean hasProperty(Var property, T value) throws IntegrationInconsistencyException {
    return shadowInstance.hasProperty(property, value);
  }

  /**
   * <!-- hasProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#hasProperty(uk.ac.hutton.obiama.msb.Var,
   *      uk.ac.hutton.obiama.msb.Instance)
   * @param <T>
   * @param property
   * @param value
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public boolean hasProperty(Var property, Instance value) throws IntegrationInconsistencyException {
    return shadowInstance.hasProperty(property, value);
  }

  /**
   * <!-- hasPropertyString -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#hasPropertyString(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.String)
   * @param property
   * @param value
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public boolean hasPropertyString(Var property, String value) throws IntegrationInconsistencyException {
    return shadowInstance.hasPropertyString(property, value);
  }

  /**
   * <!-- hasValueFor -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#hasValueFor(uk.ac.hutton.obiama.msb.Var)
   * @param property
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public boolean hasValueFor(Var property) throws IntegrationInconsistencyException {
    return shadowInstance.hasValueFor(property);
  }

  /**
   * <!-- removeConcept -->
   * 
   * Attempt made to modify locked instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#removeConcept(uk.ac.hutton.obiama.msb.Concept)
   * @param concept
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void removeConcept(Concept concept) throws IntegrationInconsistencyException {
    throw new ModificationOfLockedInstanceException(process, shadowInstance.getURI());
  }

  /**
   * <!-- removeConcept -->
   * 
   * Attempt made to modify locked instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#removeConcept(java.net.URI)
   * @param concept
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void removeConcept(URI concept) throws IntegrationInconsistencyException {
    throw new ModificationOfLockedInstanceException(process, shadowInstance.getURI());
  }

  /**
   * <!-- removeExistingProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#removeExistingProperty(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.Object)
   * @param <T>
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public <T> boolean removeExistingProperty(Var property, T value) throws IntegrationInconsistencyException {
    return shadowInstance.removeExistingProperty(property, value);
  }

  /**
   * <!-- removeExistingProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#removeExistingProperty(uk.ac.hutton.obiama.msb.Var,
   *      uk.ac.hutton.obiama.msb.Instance)
   * @param <T>
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public boolean removeExistingProperty(Var property, Instance value) throws IntegrationInconsistencyException {
    return shadowInstance.removeExistingProperty(property, value);
  }

  /**
   * <!-- removeExistingPropertyString -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#removeExistingPropertyString(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.String)
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public boolean removeExistingPropertyString(Var property, String value) throws IntegrationInconsistencyException {
    return shadowInstance.removeExistingPropertyString(property, value);
  }

  /**
   * <!-- removeProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#removeProperty(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.Object)
   * @param <T>
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public <T> boolean removeProperty(Var property, T value) throws IntegrationInconsistencyException {
    return shadowInstance.removeProperty(property, value);
  }

  /**
   * <!-- removeProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#removeProperty(uk.ac.hutton.obiama.msb.Var,
   *      uk.ac.hutton.obiama.msb.Instance)
   * @param <T>
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public boolean removeProperty(Var property, Instance value) throws IntegrationInconsistencyException {
    return shadowInstance.removeProperty(property, value);
  }

  /**
   * <!-- removePropertyString -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#removePropertyString(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.String)
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public boolean removePropertyString(Var property, String value) throws IntegrationInconsistencyException {
    return shadowInstance.removePropertyString(property, value);
  }

  /**
   * <!-- setNewProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#setNewProperty(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.Object)
   * @param <T>
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public <T> void setNewProperty(Var property, T value) throws IntegrationInconsistencyException {
    shadowInstance.setNewProperty(property, value);
  }

  /**
   * <!-- setNewProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#setNewProperty(uk.ac.hutton.obiama.msb.Var,
   *      uk.ac.hutton.obiama.msb.Instance)
   * @param <T>
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void setNewProperty(Var property, Instance value) throws IntegrationInconsistencyException {
    shadowInstance.setNewProperty(property, value);
  }

  /**
   * <!-- setNewPropertyString -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#setNewPropertyString(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.String)
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void setNewPropertyString(Var property, String value) throws IntegrationInconsistencyException {
    shadowInstance.setNewPropertyString(property, value);
  }

  /**
   * <!-- setProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#setProperty(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.Object)
   * @param <T>
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public <T> void setProperty(Var property, T value) throws IntegrationInconsistencyException {
    shadowInstance.setProperty(property, value);
  }

  /**
   * <!-- setProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#setProperty(uk.ac.hutton.obiama.msb.Var,
   *      uk.ac.hutton.obiama.msb.Instance)
   * @param <T>
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void setProperty(Var property, Instance value) throws IntegrationInconsistencyException {
    shadowInstance.setProperty(property, value);
  }

  /**
   * <!-- setPropertyString -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#setPropertyString(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.String)
   * @param property
   * @param value
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void setPropertyString(Var property, String value) throws IntegrationInconsistencyException {
    shadowInstance.setPropertyString(property, value);
  }

  /**
   * <!-- unsetExistingProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#unsetExistingProperty(uk.ac.hutton.obiama.msb.Var)
   * @param property
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void unsetExistingProperty(Var property) throws IntegrationInconsistencyException {
    shadowInstance.unsetExistingProperty(property);
  }

  /**
   * <!-- unsetProperty -->
   * 
   * Forward to shadowed instance
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#unsetProperty(uk.ac.hutton.obiama.msb.Var)
   * @param property
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void unsetProperty(Var property) throws IntegrationInconsistencyException {
    shadowInstance.unsetProperty(property);
  }

  /**
   * <!-- copy -->
   *
   * @see uk.ac.hutton.obiama.msb.Instance#copy()
   * @return
   * @throws IntegrationInconsistencyException
   */
  @Override
  public Instance copy() throws IntegrationInconsistencyException {
    return shadowInstance.copy();
  }

  /**
   * <!-- setVars -->
   *
   * @see uk.ac.hutton.obiama.msb.AbstractInstance#setVars(java.util.Collection)
   * @param vars
   */
  @Override
  void setVars(Collection<Var> vars) throws IntegrationInconsistencyException {
    throw new ModificationOfLockedInstanceException(process, shadowInstance.getURI());   
  }

  /**
   * <!-- addQuietConcept -->
   *
   * @see uk.ac.hutton.obiama.msb.AbstractInstance#addQuietConcept(java.net.URI)
   * @param concept
   */
  @Override
  void addQuietConcept(URI concept) throws IntegrationInconsistencyException {
    throw new ModificationOfLockedInstanceException(process, shadowInstance.getURI());    
  }

}
