/*
 * uk.ac.hutton.obiama.msb: ModelStateBroker.java
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
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.action.Process;
import uk.ac.hutton.obiama.action.Query;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.StateOntologyHasTBoxAxiomsException;
import uk.ac.hutton.obiama.model.ObiamaSchedule;

/**
 * <!-- ModelStateBroker -->
 * 
 * 
 * @author Gary Polhill
 */
public interface ModelStateBroker {
  public static final String SAVED_STATE_PREFIX = "state-T";
  public static final String PREV_STATE_CHAIN_FILE = ".previous";
  public static final String NEXT_STATE_CHAIN_FILE = ".next";

  public URI getBaseURI();
  
  public Set<URI> find(String name);
  
  public Set<URI> findModelName(String name);

  public Var getVariableName(URI name, URI domain, URI range, Process process) throws IntegrationInconsistencyException;

  public Var getVariableName(URI name, URI domain, XSDVocabulary range, Process process)
      throws IntegrationInconsistencyException;

  public Var getVariableName(URI name, URI range, Process process) throws IntegrationInconsistencyException;

  public Var getVariableName(URI name, XSDVocabulary range, Process process) throws IntegrationInconsistencyException;

  public Var getVariableName(URI name, Process process) throws IntegrationInconsistencyException;

  public Concept getConcept(URI name, Process process, Set<Var> vars,
      @SuppressWarnings("rawtypes") Map<URI, Class<? extends Query>> queries) throws IntegrationInconsistencyException;

  public Concept getRangeOf(Var var, Process process) throws IntegrationInconsistencyException;
  
  public XSDVocabulary getDataRangeOf(Var var, Process process) throws IntegrationInconsistencyException;

  public void updateCreators() throws IntegrationInconsistencyException;

  public void update() throws IntegrationInconsistencyException;

  public void reset();

  public void loadState(String stateOntologyURI) throws URISyntaxException, StateOntologyHasTBoxAxiomsException;

  public Exception saveState(String directory);

  /**
   * <!-- createState -->
   * 
   * If there are no asserted state ontologies, create one
   * 
   * @throws OWLOntologyCreationException
   */
  public void createState();

  public Set<ObiamaSchedule> getCreators();
}
