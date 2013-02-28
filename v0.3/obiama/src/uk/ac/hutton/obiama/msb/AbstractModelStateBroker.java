/*
 * uk.ac.hutton.obiama.msb: AbstractModelStateBroker.java
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

import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.action.Action;
import uk.ac.hutton.obiama.action.Process;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.NoSuchIndividualException;

/**
 * AbstractModelStateBroker
 * 
 * This is used to declare methods ModelStateBroker classes should implement
 * with default visibility
 * 
 * @author Gary Polhill
 */
abstract class AbstractModelStateBroker implements ModelStateBroker {

  abstract Set<OWLOntology> getModel();

  abstract Set<OWLOntology> getAssertedModel();

  abstract URI createInstanceURI(URI conceptURI);

  abstract AbstractInstance copyIndividual(AbstractInstance instance, Process originator)
      throws IntegrationInconsistencyException;

  abstract OWLIndividual getIndividual(Process action, URI individual) throws NoSuchIndividualException;

  abstract Set<OWLIndividual> getMembers(Process action, URI concept) throws IntegrationInconsistencyException;

  abstract Set<OWLIndividual> getObjectPropertyValues(OWLIndividual individual, OWLObjectProperty property);

  abstract void removeObjectPropertyAssertionValue(Action action, OWLIndividual subject, OWLObjectProperty property,
      OWLIndividual object) throws IntegrationInconsistencyException;

  abstract void addObjectPropertyAssertionValue(Action action, OWLIndividual subject, OWLObjectProperty property,
      OWLIndividual object) throws IntegrationInconsistencyException;

  abstract Set<String> getDataPropertyValues(OWLIndividual individual, OWLDataProperty property);

  abstract <T> void removeDataPropertyAssertionValue(Action action, OWLIndividual subject, OWLDataProperty property,
      T value, XSDVocabulary type) throws IntegrationInconsistencyException;

  abstract <T> void addDataPropertyAssertionValue(Action action, OWLIndividual subject, OWLDataProperty property,
      T value, XSDVocabulary type) throws IntegrationInconsistencyException;

  abstract void addClassAssertion(Action action, AbstractInstance instance, Concept concept)
      throws IntegrationInconsistencyException;

  abstract void addClassAssertion(Action action, URI instanceURI, URI conceptURI)
      throws IntegrationInconsistencyException;

  abstract void createInstance(Action action, AbstractInstance instance, Concept concept)
      throws IntegrationInconsistencyException;

  abstract void removeClassAssertion(Action action, AbstractInstance instance, Concept concept)
      throws IntegrationInconsistencyException;

  abstract void removeClassAssertion(Action action, URI instanceURI, URI conceptURI)
      throws IntegrationInconsistencyException;

  abstract <T> AbstractValue<T> registerValue(AbstractValue<T> value) throws IntegrationInconsistencyException;

  abstract AbstractInstance registerInstance(AbstractInstance instance);

  abstract Set<URI> getClassesOf(URI individualURI);

  abstract Set<URI> getSuperClassesOf(URI classURI);

  abstract void killIndividual(Action action, URI individualURI) throws IntegrationInconsistencyException;

  abstract void deleteIndividual(Action action, URI uri) throws IntegrationInconsistencyException;

}
