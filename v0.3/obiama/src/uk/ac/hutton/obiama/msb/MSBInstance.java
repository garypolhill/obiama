/*
 * uk.ac.hutton.obiama.msb: MSBInstance.java
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.action.Query;
import uk.ac.hutton.obiama.action.Process;
import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.InconsistentRangeException;
import uk.ac.hutton.obiama.exception.IndividualDoesNotHavePropertyException;
import uk.ac.hutton.obiama.exception.IndividualDoesNotHaveQueryException;
import uk.ac.hutton.obiama.exception.IndividualIsDeadException;
import uk.ac.hutton.obiama.exception.IndividualNotInstanceOfConceptException;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.util.Panic;

/**
 * <!-- MSBInstance -->
 * 
 * An implementation of the Instance interface.
 * 
 * @author Gary Polhill
 */
public class MSBInstance extends AbstractInstance {
  private enum ValueAccessMode {
    NEW, EXISTING, DEFAULT;
  }

  /**
   * The set of Concepts this instance belongs to
   */
  Set<URI> concepts;

  /**
   * The set of Concepts this instance belonged to at the point the constructor
   * was called.
   */
  Set<URI> priorConcepts;

  /**
   * The set of Concepts to remove from the instance
   */
  Set<URI> removeConcepts;

  /**
   * The set of Concepts this instance has that the calling process probably
   * doesn't know about. This will be non-empty when copying an instance.
   */
  private Set<URI> quietConcepts;

  /**
   * The set of Vars this instance has
   */
  Set<Var> vars;

  /**
   * The set of Queries this instance responds to
   */
  Set<Query<?>> queries;

  /**
   * Map from Var to Value of the values this instance has accessed
   */
  Map<Var, Value<?>> values;

  /**
   * Modes that have been used to access the values
   */
  Map<Value<?>, ValueAccessMode> modes;

  /**
   * URI of this instance
   */
  URI uri;

  /**
   * Process this instance belongs to
   */
  Process originator;

  /**
   * Whether the instance is to be 'killed'
   */
  boolean killedOff;

  /**
   * Whether the instance is to be deleted
   */
  boolean deleted;

  /**
   * A reference to the MSB, for use when copying
   */
  private AbstractModelStateBroker msb;

  /**
   * Constructor creating an empty instance (no concepts or vars)
   * 
   * @param uri URI of the instance
   * @param originator Process using the instance
   * @param msb The model state broker
   */
  MSBInstance(URI uri, Process originator, AbstractModelStateBroker msb) {
    super(originator);
    this.uri = uri;
    this.originator = originator;
    concepts = new HashSet<URI>();
    vars = new HashSet<Var>();
    queries = new HashSet<Query<?>>();
    values = new HashMap<Var, Value<?>>();
    modes = new IdentityHashMap<Value<?>, ValueAccessMode>();
    msb.registerInstance(this);
    priorConcepts = msb.getClassesOf(uri);
    removeConcepts = new HashSet<URI>();
    killedOff = false;
    deleted = false;
    quietConcepts = new HashSet<URI>();
    this.msb = msb;
  }

  /**
   * Constructor initialising on a number of concepts
   * 
   * @param uri URI of the instance
   * @param originator Process using the instance
   * @param msb The model state broker
   * @param concepts Concepts the instance is to belong to
   * @throws IntegrationInconsistencyException
   */
  MSBInstance(URI uri, Process originator, AbstractModelStateBroker msb, Concept... concepts)
      throws IntegrationInconsistencyException {
    this(uri, originator, msb, false, concepts);
  }

  /**
   * Constructor checking concept list or adding concepts
   * 
   * @param uri URI of individual
   * @param originator Process using the instance
   * @param msb The model state broker
   * @param check <code>true</code> if concepts are to be checked rather than
   *          added
   * @param concepts Concepts the instance is to belong to or to check it
   *          belongs to
   * @throws IntegrationInconsistencyException An
   *           {@link IndividualNotInstanceOfConceptException} is thrown if
   *           <code>check</code> is <code>true</code>
   */
  MSBInstance(URI uri, Process originator, AbstractModelStateBroker msb, boolean check, Concept... concepts)
      throws IntegrationInconsistencyException {
    this(uri, originator, msb);

    for(Concept concept: concepts) {
      if(check) {
        if(!priorConcepts.contains(concept.getURI())) {
          throw new IndividualNotInstanceOfConceptException(uri, concept.getURI(), originator);
        }
      }
      else {
        addConcept(concept);
      }
    }
  }

  /**
   * <!-- addConcept -->
   * 
   * Add a concept to the individual
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addConcept(uk.ac.hutton.obiama.msb.Concept)
   * @param concept
   * @throws IntegrationInconsistencyException
   */
  public void addConcept(Concept concept) throws IntegrationInconsistencyException {
    Set<Var> concVars = concept.getVars();
    Set<Query<?>> concQueries = concept.getQueries();
    if(concVars != null) vars.addAll(concVars);
    if(concQueries != null) queries.addAll(concQueries);
    addConcept(concept.getURI());
  }

  /**
   * <!-- addConcept -->
   * 
   * Add a concept to the individual
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addConcept(java.net.URI)
   * @param concept
   * @throws IntegrationInconsistencyException
   */
  public void addConcept(URI concept) throws IntegrationInconsistencyException {
    if(removeConcepts.contains(concept)) {
      removeConcepts.remove(concept);
    }
    if(quietConcepts.contains(concept)) {
      quietConcepts.remove(concept);
    }
    concepts.add(concept);
  }

  /**
   * <!-- addQuietConcept -->
   * 
   * Add a concept to the individual when copying another individual. This will
   * be called from the MSB when the individual is being copied.
   * 
   * @param concept
   */
  void addQuietConcept(URI concept) throws IntegrationInconsistencyException {
    quietConcepts.add(concept);
  }

  /**
   * <!-- addExistingProperty -->
   * 
   * Add a value to a non-functional property that already has a value
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addExistingProperty(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.Object)
   * @param property The property to add to
   * @param value The value to add
   * @throws IntegrationInconsistencyException
   */
  public <T> void addExistingProperty(Var property, T value) throws IntegrationInconsistencyException {
    Value<T> entry = getExistingValueFor(property);
    entry.add(value);
  }

  /**
   * <!-- addExistingProperty -->
   * 
   * Add a value to a non-functional object property that already has a value
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addExistingProperty(uk.ac.hutton.obiama.msb.Var,
   *      uk.ac.hutton.obiama.msb.Instance)
   * @param property The property to add to
   * @param value The value to add
   * @throws IntegrationInconsistencyException
   */
  public void addExistingProperty(Var property, Instance value) throws IntegrationInconsistencyException {
    Value<URI> entry = getExistingValueFor(property);
    entry.add(value.getURI());
  }

  /**
   * <!-- addExistingPropertyString -->
   * 
   * Add a string value to a non-functional property that already has a value
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addExistingPropertyString(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.String)
   * @param property The property to add to
   * @param value The value to add
   * @throws IntegrationInconsistencyException
   */
  public void addExistingPropertyString(Var property, String value) throws IntegrationInconsistencyException {
    Value<?> entry = getExistingValueFor(property);
    entry.addString(value);
  }

  /**
   * <!-- addNewProperty -->
   * 
   * Add a value to a non-functional property that does not already have a value
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addNewProperty(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.Object)
   * @param property The property to add to
   * @param value The value to add
   * @throws IntegrationInconsistencyException
   */
  public <T> void addNewProperty(Var property, T value) throws IntegrationInconsistencyException {
    Value<T> entry = getNewValueFor(property);
    entry.add(value);
  }

  /**
   * <!-- addNewProperty -->
   * 
   * Add a value to a non-functional object property that does not already have
   * a value
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addNewProperty(uk.ac.hutton.obiama.msb.Var,
   *      uk.ac.hutton.obiama.msb.Instance)
   * @param property The property to add to
   * @param value The value to add
   * @throws IntegrationInconsistencyException
   */
  public void addNewProperty(Var property, Instance value) throws IntegrationInconsistencyException {
    Value<URI> entry = getNewValueFor(property);
    entry.add(value.getURI());
  }

  /**
   * <!-- addNewPropertyString -->
   * 
   * Add a string value to a non-functional property that does not already have
   * a value
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addNewPropertyString(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.String)
   * @param property The property to add a value to
   * @param value The value to add
   * @throws IntegrationInconsistencyException
   */
  public void addNewPropertyString(Var property, String value) throws IntegrationInconsistencyException {
    Value<?> entry = getNewValueFor(property);
    entry.addString(value);
  }

  /**
   * <!-- addProperty -->
   * 
   * Add a value to a non-functional var
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addProperty(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.Object)
   * @param property The non-functional var to which to add a value
   * @param value The value to add
   */
  public <T> void addProperty(Var property, T value) throws IntegrationInconsistencyException {
    if(vars.contains(property)) {
      Value<T> entry = getValueFor(property);
      entry.add(value);
    }
    else {
      throw new IndividualDoesNotHavePropertyException(originator, uri, property.getURI());
    }
  }

  /**
   * <!-- addProperty -->
   * 
   * Add a value to a non-functional object var
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addProperty(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.Object)
   * @param property The non-functional var to which to add a value
   * @param value The value to add
   */
  public void addProperty(Var property, Instance value) throws IntegrationInconsistencyException {
    if(vars.contains(property)) {
      Value<URI> entry = getValueFor(property);
      entry.add(value.getURI());
    }
    else {
      throw new IndividualDoesNotHavePropertyException(originator, uri, property.getURI());
    }
  }

  /**
   * <!-- addPropertyString -->
   * 
   * Add a string to a non-functional var the individual has
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#addPropertyString(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.String)
   * @param property The non-functional var the individual has
   * @param value The string value to add to it
   * @throws IntegrationInconsistencyException
   */
  public void addPropertyString(Var property, String value) throws IntegrationInconsistencyException {
    if(vars.contains(property)) {
      Value<?> entry = getValueFor(property);
      entry.addString(value);
    }
    else {
      throw new IndividualDoesNotHavePropertyException(originator, uri, property.getURI());
    }
  }

  /**
   * <!-- ask -->
   * 
   * Ask a query of an individual
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#ask(uk.ac.hutton.obiama.action.Query,
   *      uk.ac.hutton.obiama.msb.Instance, java.lang.Object[])
   * @param query The query to ask
   * @param requester The requesting instance
   * @param args Arguments to the query
   * @return The answer
   * @throws IntegrationInconsistencyException
   */
  public <T> T ask(Query<T> query, Instance requester, Object... args) throws IntegrationInconsistencyException {
    if(killedOff) throw new IndividualIsDeadException(uri, originator);
    if(queries.contains(query)) {
      return query.ask(uri, requester.getURI(), originator, args);
    }
    else {
      throw new IndividualDoesNotHaveQueryException(originator, query, requester.getURI(), uri);
    }
  }

  /**
   * <!-- clearExistingProperty -->
   * 
   * Remove all entries from a non-functional property expected to have values
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#clearExistingProperty(uk.ac.hutton.obiama.msb.Var)
   * @param property The property to remove entries from
   * @throws IntegrationInconsistencyException
   */
  public void clearExistingProperty(Var property) throws IntegrationInconsistencyException {
    Value<?> entry = getExistingValueFor(property);
    entry.clear();
  }

  /**
   * <!-- clearProperty -->
   * 
   * Remove all entries from a (non-functional) property
   * 
   * @param property The property to remove entries from
   * @throws IntegrationInconsistencyException
   */
  public void clearProperty(Var property) throws IntegrationInconsistencyException {
    Value<?> entry = getValueFor(property);
    entry.clear();
  }

  /**
   * <!-- copy -->
   * 
   * Return a copy of the instance. This needs to be a deeper copy than just the
   * Vars in this Instance.
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#copy()
   * @return A copy of the instance
   * @throws IntegrationInconsistencyException
   */
  public Instance copy() throws IntegrationInconsistencyException {
    AbstractInstance copy = msb.copyIndividual(this, originator);

    return copy;
  }

  /**
   * <!-- die -->
   * 
   * Assert that the instance is to be destroyed.
   * 
   * @throws IntegrationInconsistencyException
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#die()
   */
  public void die() throws IntegrationInconsistencyException {
    killedOff = true;
  }

  /**
   * <!-- delete -->
   * 
   * Assert that the instance is to be removed from the ontology completely
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#delete()
   * @throws IntegrationInconsistencyException
   */
  public void delete() throws IntegrationInconsistencyException {
    deleted = true;
    killedOff = true;
  }

  /**
   * <!-- getConcepts -->
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getConcepts()
   * @return A copy of the set of concepts this instance belongs to
   */
  public Set<URI> getConcepts() {
    return new HashSet<URI>(concepts);
  }

  /**
   * <!-- getExistingProperty -->
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getExistingProperty(uk.ac.hutton.obiama.msb.Var)
   * @param property The functional property to get the value of
   * @return The value of the property for this individual
   * @throws IntegrationInconsistencyException
   */
  public <T> T getExistingProperty(Var property) throws IntegrationInconsistencyException {
    Value<T> entry = getExistingValueFor(property);
    return entry.get();
  }
  
  public String getExistingPropertyString(Var property) throws IntegrationInconsistencyException {
    Value<?> entry = getExistingValueFor(property);
    return entry.getString();
  }

  /**
   * <!-- getExistingPropertyAll -->
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getExistingPropertyAll(uk.ac.hutton.obiama.msb.Var,
   *      java.util.Set)
   * @param property The non-functional property (expected to have values) to
   *          get all values of
   * @param repository The repository to put the values in
   * @throws IntegrationInconsistencyException
   */
  public <T> void getExistingPropertyAll(Var property, Set<T> repository) throws IntegrationInconsistencyException {
    Value<T> entry = getExistingValueFor(property);
    entry.getAll(repository);
  }
  
  public void getExistingPropertyAllString(Var property, Set<String> repository) throws IntegrationInconsistencyException {
    Value<?> entry = getExistingValueFor(property);
    entry.getAllString(repository);
  }

  /**
   * <!-- getExistingValue -->
   * 
   * Get a value for the property expected already to be set
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getExistingValue(uk.ac.hutton.obiama.msb.Var)
   * @param property The property to access
   * @return Its value for this instance
   * @throws IntegrationInconsistencyException
   */
  public <T> Value<T> getExistingValue(Var property) throws IntegrationInconsistencyException {
    return getExistingValueFor(property);
  }

  private <T> Value<T> getExistingValueFor(Var var) throws IntegrationInconsistencyException {
    return getValueFor(var, ValueAccessMode.EXISTING);
  }

  /**
   * <!-- getNewValue -->
   * 
   * Get a value for the property expected not currently to be set
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getNewValue(uk.ac.hutton.obiama.msb.Var)
   * @param property The property to access
   * @return Its (unset) value for this instance
   * @throws IntegrationInconsistencyException
   */
  public <T> Value<T> getNewValue(Var property) throws IntegrationInconsistencyException {
    return getNewValueFor(property);
  }

  /**
   * <!-- getNewValueFor -->
   * 
   * Private method to get a 'new' Value&lt;T&gt;--one that does not have any
   * value assigned
   * 
   * @param var The property to get
   * @return Its Value
   * @throws IntegrationInconsistencyException
   */
  private <T> Value<T> getNewValueFor(Var var) throws IntegrationInconsistencyException {
    return getValueFor(var, ValueAccessMode.NEW);
  }

  /**
   * <!-- getProperty -->
   * 
   * Get the value of a functional property the instance has
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getProperty(uk.ac.hutton.obiama.msb.Var)
   * @param property The property to access
   * @return The value of the property
   * @throws IntegrationInconsistencyException
   */
  public <T> T getProperty(Var property) throws IntegrationInconsistencyException {
    Value<T> value = getValueFor(property);
    return value.get();
  }
  
  public String getPropertyString(Var property) throws IntegrationInconsistencyException {
    Value<?> value = getValueFor(property);
    return value.getString();
  }

  /**
   * <!-- getPropertyAll -->
   * 
   * Get all values of a non-functional property the individual has
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getPropertyAll(uk.ac.hutton.obiama.msb.Var,
   *      java.util.Set)
   * @param property The non-functional property
   * @param repository A repository in which to put all the values
   * @throws IntegrationInconsistencyException
   */
  public <T> void getPropertyAll(Var property, Set<T> repository) throws IntegrationInconsistencyException {
    Value<T> value = getValueFor(property);
    value.getAll(repository);
  }
  
  public void getPropertyAllString(Var property, Set<String> repository) throws IntegrationInconsistencyException {
    Value<?> value = getValueFor(property);
    value.getAllString(repository);
  }

  /**
   * <!-- getQueries -->
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getQueries()
   * @return A copy of the set of queries the instance responds to
   */
  public Set<Query<?>> getQueries() {
    return new HashSet<Query<?>>(queries);
  }

  /**
   * <!-- getURI -->
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getURI()
   * @return The URI of the instance
   */
  public URI getURI() {
    return uri;
  }

  /**
   * <!-- getValue -->
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getValue(uk.ac.hutton.obiama.msb.Var)
   * @param property The property to get the value of
   * @return The value of the property
   * @throws IntegrationInconsistencyException
   */
  public <T> Value<T> getValue(Var property) throws IntegrationInconsistencyException {
    return getValueFor(property);
  }

  /**
   * <!-- getValueFor -->
   * 
   * Access a value the instance has for one of its vars in default mode
   * 
   * @param var The variable to access
   * @return The value the instance has for its vars
   * @throws IntegrationInconsistencyException
   */
  private <T> Value<T> getValueFor(Var var) throws IntegrationInconsistencyException {
    return getValueFor(var, ValueAccessMode.DEFAULT);
  }

  /**
   * <!-- getValueFor -->
   * 
   * Main private method for accessing values. All methods accessing a value of
   * a property should eventually call this one. The method maintains a Map of
   * Vars that have already been accessed to prevent the same Value being
   * accessed more than once (an error). The mode argument uses the private enum
   * to determine which Var method to use to return the Value.
   * 
   * @param var The property to get the Value of
   * @param mode A mode indicating which Var method to use to access the Value
   * @return The Value
   * @throws IntegrationInconsistencyException
   */
  @SuppressWarnings("unchecked")
  private <T> Value<T> getValueFor(Var var, ValueAccessMode mode) throws IntegrationInconsistencyException {
    Value<T> value;
    if(values.containsKey(var)) {
      try {
        value = (Value<T>)values.get(var);
        if(mode != ValueAccessMode.DEFAULT && mode != modes.get(value)) {
          throw new Bug();
        }
      }
      catch(ClassCastException e) {
        throw new InconsistentRangeException(var.getURI(), var.getType().getURI(), null, originator);
      }
    }
    else {
      switch(mode) {
      case NEW:
        value = var.getNewValueFor(uri);
        break;
      case EXISTING:
        value = var.getExistingValueFor(uri);
        break;
      case DEFAULT:
        value = var.getValueFor(uri);
        break;
      default:
        throw new Panic();
      }
      values.put(var, value);
      modes.put(value, mode);
    }
    return value;
  }

  /**
   * <!-- getValues -->
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getValues()
   * @return A Set containing Values for all the Vars this individual has
   * @throws IntegrationInconsistencyException
   */
  public Set<Value<?>> getValues() throws IntegrationInconsistencyException {
    Set<Value<?>> myValues = new HashSet<Value<?>>();
    for(Var var: vars) {
      myValues.add(getValueFor(var));
    }
    return myValues;
  }

  /**
   * <!-- getVars -->
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#getVars()
   * @return A Set containing all Vars this individual has
   */
  public Set<Var> getVars() {
    return new HashSet<Var>(vars);
  }

  /**
   * <!-- hasConcept -->
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#hasConcept(uk.ac.hutton.obiama.msb.Concept)
   * @param concept The concept to check
   * @return <code>true</code> if the instance has the Concept
   */
  public boolean hasConcept(Concept concept) {
    return concepts.contains(concept);
  }

  /**
   * <!-- hasProperty -->
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#hasProperty(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.Object)
   * @param property The non-functional property to check
   * @param value The value to check in the property
   * @return <code>true</code> if the individual has the value in the
   *         non-functional property
   * @throws IntegrationInconsistencyException
   */
  public <T> boolean hasProperty(Var property, T value) throws IntegrationInconsistencyException {
    Value<T> entry = getValueFor(property);
    return entry.has(value);
  }

  /**
   * <!-- hasProperty -->
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#hasProperty(uk.ac.hutton.obiama.msb.Var,
   *      uk.ac.hutton.obiama.msb.Instance)
   * @param property The non-functional object property to check
   * @param value The value to check in the property
   * @return <code>true</code> if the individual has the value in the
   *         non-functional property
   * @throws IntegrationInconsistencyException
   */
  public boolean hasProperty(Var property, Instance value) throws IntegrationInconsistencyException {
    Value<URI> entry = getValueFor(property);
    return entry.has(value);
  }

  /**
   * <!-- hasPropertyString -->
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#hasPropertyString(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.String)
   * @param property The non-functional property to check
   * @param value The value to check in the property
   * @return <code>true</code> if the individual has the value in the
   *         non-functional property
   * @throws IntegrationInconsistencyException
   */
  public boolean hasPropertyString(Var property, String value) throws IntegrationInconsistencyException {
    Value<?> entry = getValueFor(property);
    return entry.hasString(value);
  }

  /**
   * <!-- hasValueFor -->
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#hasValueFor(uk.ac.hutton.obiama.msb.Var)
   * @param property The property to check
   * @return <code>true</code> if the individual has a value for the property
   * @throws IntegrationInconsistencyException
   */
  public boolean hasValueFor(Var property) throws IntegrationInconsistencyException {
    return property.hasValueFor(uri);
  }

  /**
   * <!-- removeConcept -->
   * 
   * Remove a concept from the individual
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#removeConcept(uk.ac.hutton.obiama.msb.Concept)
   * @param concept
   * @throws IntegrationInconsistencyException
   */
  public void removeConcept(Concept concept) throws IntegrationInconsistencyException {
    if(concepts.contains(concept.getURI())) {
      vars.removeAll(concept.getVars());
      queries.removeAll(concept.getQueries());
    }
    removeConcept(concept.getURI());
  }

  /**
   * <!-- removeConcept -->
   * 
   * Remove a concept from the individual
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#removeConcept(java.net.URI)
   * @param concept
   * @throws IntegrationInconsistencyException
   */
  public void removeConcept(URI concept) throws IntegrationInconsistencyException {
    if(concepts.contains(concept)) {
      concepts.remove(concept);
    }
    else if(quietConcepts.contains(concept)) {
      quietConcepts.remove(concept);
    }
    else if(!priorConcepts.contains(concept)) {
      throw new IndividualNotInstanceOfConceptException(getURI(), concept, originator);
    }
    removeConcepts.add(concept);
  }

  /**
   * <!-- removeExistingProperty -->
   * 
   * Remove a value from a property accessed using
   * {@link Var#getExistingValueFor(java.net.URI)}.
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#removeExistingProperty(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.Object)
   * @param property The non-functional property from which to remove a value
   * @param value The value to remove
   * @throws IntegrationInconsistencyException
   */
  public <T> boolean removeExistingProperty(Var property, T value) throws IntegrationInconsistencyException {
    Value<T> entry = getExistingValueFor(property);
    return entry.remove(value);
  }

  /**
   * <!-- removeExistingProperty -->
   * 
   * Remove a value from a property accessed using
   * {@link Var#getExistingValueFor(java.net.URI)}.
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#removeExistingProperty(uk.ac.hutton.obiama.msb.Var,
   *      uk.ac.hutton.obiama.msb.Instance)
   * @param property The non-functional property from which to remove a value
   * @param value The value to remove
   * @throws IntegrationInconsistencyException
   */
  public boolean removeExistingProperty(Var property, Instance value) throws IntegrationInconsistencyException {
    Value<URI> entry = getExistingValueFor(property);
    return entry.remove(value);
  }

  /**
   * <!-- removeExistingPropertyString -->
   * 
   * Remove a value (passed as a string) from a property accessed using
   * {@link Var#getExistingValueFor(java.net.URI)}.
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#removeExistingPropertyString(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.String)
   * @param property The non-functional property from which to remove a value
   * @param value The value to remove
   * @throws IntegrationInconsistencyException
   */
  public boolean removeExistingPropertyString(Var property, String value) throws IntegrationInconsistencyException {
    Value<?> entry = getExistingValueFor(property);
    return entry.removeString(value);
  }

  /**
   * <!-- removeProperty -->
   * 
   * Remove a value from a non-functional property accessed using
   * {@link Var#getValueFor(java.net.URI)}
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#removeProperty(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.Object)
   * @param property The non-functional property from which to remove a value
   * @param value The value to remove
   * @throws IntegrationInconsistencyException
   */
  public <T> boolean removeProperty(Var property, T value) throws IntegrationInconsistencyException {
    Value<T> entry = getValueFor(property);
    return entry.remove(value);
  }

  /**
   * <!-- removeProperty -->
   * 
   * Remove a value from a non-functional object property accessed using
   * {@link Var#getValueFor(java.net.URI)}
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#removeProperty(uk.ac.hutton.obiama.msb.Var,
   *      uk.ac.hutton.obiama.msb.Instance)
   * @param property The non-functional property from which to remove a value
   * @param value The value to remove
   * @throws IntegrationInconsistencyException
   */
  public boolean removeProperty(Var property, Instance value) throws IntegrationInconsistencyException {
    Value<URI> entry = getValueFor(property);
    return entry.remove(value);
  }

  /**
   * <!-- removePropertyString -->
   * 
   * Remove a value passed as a string from a non-functional property accessed
   * using {@link Var#getValueFor(java.net.URI)}.
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#removePropertyString(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.String)
   * @param property The non-functional property from which to remove a value
   * @param value The value to remove
   * @throws IntegrationInconsistencyException
   */
  public boolean removePropertyString(Var property, String value) throws IntegrationInconsistencyException {
    Value<?> entry = getValueFor(property);
    return entry.removeString(value);
  }

  /**
   * <!-- setNewProperty -->
   * 
   * Set the value of a functional property accessed using
   * {@link Var#getNewValueFor(java.net.URI)}.
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#setNewProperty(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.Object)
   * @param property The functional property to set
   * @param value The value to set it to
   * @throws IntegrationInconsistencyException
   */
  public <T> void setNewProperty(Var property, T value) throws IntegrationInconsistencyException {
    Value<T> entry = getNewValueFor(property);
    entry.set(value);
  }

  /**
   * <!-- setNewProperty -->
   * 
   * Set the value of a functional object property accessed using
   * {@link Var#getNewValueFor(java.net.URI)}.
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#setNewProperty(uk.ac.hutton.obiama.msb.Var,
   *      uk.ac.hutton.obiama.msb.Instance)
   * @param property The functional property to set
   * @param value The value to set it to
   * @throws IntegrationInconsistencyException
   */
  public void setNewProperty(Var property, Instance value) throws IntegrationInconsistencyException {
    Value<URI> entry = getNewValueFor(property);
    entry.set(value);
  }

  /**
   * <!-- setNewPropertyString -->
   * 
   * Set the value (passed as a string) of a functional property accessed using
   * {@link Var#getNewValueFor(java.net.URI)}.
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#setNewPropertyString(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.String)
   * @param property The functional property to set
   * @param value A string to parse to give it a value
   * @throws IntegrationInconsistencyException
   */
  public void setNewPropertyString(Var property, String value) throws IntegrationInconsistencyException {
    Value<?> entry = getNewValueFor(property);    
    entry.setString(value);
  }

  /**
   * <!-- setProperty -->
   * 
   * Set the value of a functional property accessed using
   * {@link Var#getValueFor(java.net.URI)}.
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#setProperty(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.Object)
   * @param property The functional property to set
   * @param value The value to set it to
   * @throws IntegrationInconsistencyException
   */
  public <T> void setProperty(Var property, T value) throws IntegrationInconsistencyException {
    Value<T> entry = getValueFor(property);
    entry.set(value);
  }

  /**
   * <!-- setProperty -->
   * 
   * Set the value of a functional object property accessed using
   * {@link Var#getValueFor(java.net.URI)}.
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#setProperty(uk.ac.hutton.obiama.msb.Var,
   *      uk.ac.hutton.obiama.msb.Instance)
   * @param property The functional property to set
   * @param value The value to set it to
   * @throws IntegrationInconsistencyException
   */
  public void setProperty(Var property, Instance value) throws IntegrationInconsistencyException {
    Value<URI> entry = getValueFor(property);
    entry.set(value);
  }

  /**
   * <!-- setPropertyString -->
   * 
   * Set the value (passed as a string) of a functional property accessed using
   * {@link Var#getValueFor(java.net.URI)}.
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#setPropertyString(uk.ac.hutton.obiama.msb.Var,
   *      java.lang.String)
   * @param property The functional property to set
   * @param value A string to parse to set the value
   * @throws IntegrationInconsistencyException
   */
  public void setPropertyString(Var property, String value) throws IntegrationInconsistencyException {
    Value<?> entry = getValueFor(property);
    entry.setString(value);
  }

  /**
   * <!-- setVars -->
   * 
   * Set the Vars of the individual -- called when copying another one.
   *
   * @see uk.ac.hutton.obiama.msb.AbstractInstance#setVars(java.util.Collection)
   * @param vars
   */
  void setVars(Collection<Var> vars) throws IntegrationInconsistencyException {
    this.vars.addAll(vars);
  }
  
  /**
   * <!-- unsetExistingProperty -->
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#unsetExistingProperty(uk.ac.hutton.obiama.msb.Var)
   * @param property The (existing, functional) property to unset the value of
   * @throws IntegrationInconsistencyException
   */
  public void unsetExistingProperty(Var property) throws IntegrationInconsistencyException {
    Value<?> entry = getExistingValueFor(property);
    entry.unset();
  }

  /**
   * <!-- unsetProperty -->
   * 
   * @see uk.ac.hutton.obiama.msb.Instance#unsetProperty(uk.ac.hutton.obiama.msb.Var)
   * @param property The functional property to unset the value of
   * @throws IntegrationInconsistencyException
   */
  public void unsetProperty(Var property) throws IntegrationInconsistencyException {
    Value<?> entry = getValueFor(property);
    entry.clear();
  }

  /**
   * <!-- update -->
   * 
   * Update the model state broker with changes stored in this instance
   * 
   * @see uk.ac.hutton.obiama.msb.AbstractInstance#update(uk.ac.hutton.obiama.msb.AbstractModelStateBroker)
   * @param msb The model state broker to update
   * @throws IntegrationInconsistencyException
   */
  @Override
  void update(AbstractModelStateBroker msb) throws IntegrationInconsistencyException {
    if(deleted) msb.deleteIndividual((Action)originator, getURI());
    else {
      for(URI concept: removeConcepts) {
        if(priorConcepts.contains(concept)) {
          msb.removeClassAssertion((Action)originator, getURI(), concept);
        }
      }
      for(URI concept: concepts) {
        if(!priorConcepts.contains(concept)) {
          msb.addClassAssertion((Action)originator, getURI(), concept);
        }
      }
      for(URI concept: quietConcepts) {
        msb.addClassAssertion((Action)originator, getURI(), concept);
      }
      if(killedOff) msb.killIndividual((Action)originator, getURI());
    }
  }

}
