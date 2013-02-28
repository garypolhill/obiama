/*
 * uk.ac.hutton.obiama.msb: NonFunctionalObjectValue.java
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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.model.OWLIndividual;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.exception.IndividualAlreadyHasPropertyException;
import uk.ac.hutton.obiama.exception.IndividualDoesNotHavePropertyException;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ModificationOfReadOnlyValueException;

/**
 * NonFunctionalObjectValue
 * 
 * A value storing a non-functional object property of an individual. A slightly
 * different approach to storage is needed here than used in
 * FunctionalObjectValue. In order to return an iterator of type URI, it is
 * easier to store the values modified by the action as URIs rather than
 * OWLIndividual. However, to ensure that the stored value only contains URIs
 * representing OWLIndividuals in the ontology, we need to check at add time
 * that a URI is valid. A map is thus maintained of URIs to OWLIndividuals,
 * which when populated at add time, ensures that the required check is made,
 * and provides a basis for easily obtaining the OWLIndividuals for the stored
 * URIs to be added when updating.
 * 
 * @author Gary Polhill
 */
public class NonFunctionalObjectValue extends AbstractNonFunctionalValue<URI> {
  /**
   * Set of URIs of objects of the relation, for the caller to manipulate
   */
  Set<URI> stored;

  /**
   * Original set of URIs of objects of the relation, to compare with the stored
   * value when deciding what changes are needed to the ontology
   */
  Set<OWLIndividual> original;

  /**
   * Map of URIs to OWLIndividuals
   */
  Map<URI, OWLIndividual> urimap;

  /**
   * Override var to be of appropriate type
   */
  NonFunctionalObjectVar var;

  boolean writeOnly;
  
  static Value<URI> manifest(URI individual, NonFunctionalObjectVar var) throws IntegrationInconsistencyException {
    return manifest(var.msb, new NonFunctionalObjectValue(individual, var));
  }
  
  static Value<URI> manifest(URI individual, NonFunctionalObjectVar var, boolean existing) throws IntegrationInconsistencyException {
    return manifest(var.msb, new NonFunctionalObjectValue(individual, var, existing));
  }

  private NonFunctionalObjectValue(URI individual, NonFunctionalObjectVar var) throws IntegrationInconsistencyException {
    super(individual, var);
    this.var = var;
    
    Set<OWLIndividual> individuals = var.msb.getObjectPropertyValues(this.individual, var.property);

    // Use LinkedHashSet to preserve any ordering from the ontology
    stored = new LinkedHashSet<URI>();
    original = new LinkedHashSet<OWLIndividual>();
    urimap = new HashMap<URI, OWLIndividual>();

    if(individuals == null || individuals.size() == 0) {
      writeOnly = true;
    }
    else {
      writeOnly = false;
      for(OWLIndividual ind: individuals) {
        original.add(ind);
        stored.add(ind.getURI());
        urimap.put(ind.getURI(), ind);
      }
    }

  }

  /**
   * Constructor. This uses a LinkedHashSet to store the values retrieved from
   * the ontology in a bid to conserve ordering if there is any.
   * 
   * @param individual
   * @param var
   * @param existing <code>true</code> if the individual is expected to have a
   *          value for the property
   * @throws IntegrationInconsistencyException
   */
  private NonFunctionalObjectValue(URI individual, NonFunctionalObjectVar var, boolean existing)
      throws IntegrationInconsistencyException {
    this(individual, var);

    if(existing && writeOnly) throw new IndividualDoesNotHavePropertyException(var.process, individual,
        var.property.getURI());
    else if(!existing && !writeOnly)
      throw new IndividualAlreadyHasPropertyException(var.process, individual, var.property.getURI(),
          original.iterator().next().getURI());
  }

  /**
   * <!-- update -->
   * 
   * Update the ontology for changes made to the property by the action. For
   * each item in the original set not in the stored set, delete it; for each
   * item in the stored set not in the original set, add it.
   * 
   * @see uk.ac.hutton.obiama.msb.AbstractValue#update(uk.ac.hutton.obiama.msb.AbstractModelStateBroker)
   */
  @Override
  void update(AbstractModelStateBroker msb) throws IntegrationInconsistencyException {
    if(stored.size() == 0 && original.size() == 0) return;
    
    // Assemble the changes
    Set<OWLIndividual> removed = new LinkedHashSet<OWLIndividual>();
    Set<OWLIndividual> added = new LinkedHashSet<OWLIndividual>();
    for(OWLIndividual original_item: original) {
      if(!stored.contains(original_item.getURI())) removed.add(original_item);
    }
    for(URI current_item: stored) {
      OWLIndividual ind = urimap.get(current_item);
      if(!original.contains(ind)) added.add(ind);
    }
    
    if(var.readOnly() && (removed.size() > 0 || added.size() > 0)) {
      throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(),
          var.property.getURI());
    }


    
    // Make them
    for(OWLIndividual remove: removed) {
      msb.removeObjectPropertyAssertionValue((Action)var.process, individual, var.property, remove);
    }
    for(OWLIndividual add: added) {
      msb.addObjectPropertyAssertionValue((Action)var.process, individual, var.property, add);
    }
  }

  /**
   * <!-- add -->
   * 
   * Just pass this on to the stored set
   * 
   * @see uk.ac.hutton.obiama.msb.Value#add(java.lang.Object)
   */
  public void add(URI value) throws IntegrationInconsistencyException {
    if(var.readOnly()) {
      throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(),
          var.property.getURI());
    }

    urimap.put(value, var.msb.getIndividual(var.process, value));
    stored.add(value);
  }
  
  public void add(Instance value) throws IntegrationInconsistencyException {
    add(value.getURI());
  }

  public void addString(String value) throws IntegrationInconsistencyException {
    if(var.readOnly()) {
      throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(),
          var.property.getURI());
    }

    URI uri = URI.create(value);
    urimap.put(uri, var.msb.getIndividual(var.process, uri));
    stored.add(uri);
  }

  /**
   * <!-- has -->
   * 
   * Use the stored set
   * 
   * @see uk.ac.hutton.obiama.msb.Value#has(java.lang.Object)
   */
  public boolean has(URI value) throws IntegrationInconsistencyException {
    return stored.contains(value);
  }
  
  public boolean has(Instance value) throws IntegrationInconsistencyException {
    return stored.contains(value.getURI());
  }

  public boolean hasString(String value) throws IntegrationInconsistencyException {
    return stored.contains(URI.create(value));
  }

  /**
   * <!-- remove -->
   * 
   * Pass on to the stored set
   * 
   * @see uk.ac.hutton.obiama.msb.Value#remove(java.lang.Object)
   */
  public boolean remove(URI value) throws IntegrationInconsistencyException {
    if(var.readOnly()) {
      throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(),
          var.property.getURI());
    }

    return stored.remove(value);
  }
  
  public boolean remove(Instance value) throws IntegrationInconsistencyException {
    if(var.readOnly()) {
      throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(),
          var.property.getURI());
    }

    return stored.remove(value.getURI());    
  }

  public boolean removeString(String value) throws IntegrationInconsistencyException {
    if(var.readOnly()) {
      throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(),
          var.property.getURI());
    }

    return stored.remove(URI.create(value));
  }

  /**
   * <!-- clear -->
   * 
   * Remove all entries from the property
   *
   * @see uk.ac.hutton.obiama.msb.Value#clear()
   * @throws IntegrationInconsistencyException
   */
  public void clear() throws IntegrationInconsistencyException {
    if(var.readOnly()) {
      throw new ModificationOfReadOnlyValueException(var.process, individual.getURI(),
          var.property.getURI());
    }

    stored.clear();
  }

  /**
   * <!-- nElements -->
   *
   * @see uk.ac.hutton.obiama.msb.Value#nElements()
   * @return The number of elements
   */
  public int nElements() {
    return stored.size();
  }

  /**
   * <!-- iterator -->
   * 
   * Pass on to the stored set
   * 
   * @see java.lang.Iterable#iterator()
   */
  public Iterator<URI> iterator() {
    return stored.iterator();
  }

  /**
   * <!-- getVar -->
   *
   * @see uk.ac.hutton.obiama.msb.AbstractValue#getVar()
   * @return
   */
  public Var getVar() {
    return var;
  }
  
  AbstractVar getAbstractVar() {
    return var;
  }

  /**
   * <!-- readOnly -->
   *
   * @see uk.ac.hutton.obiama.msb.AbstractValue#readOnly()
   * @return
   */
  @Override
  public boolean readOnly() {
    return var.readOnly();
  }

}
