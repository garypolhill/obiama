/*
 * uk.ac.hutton.obiama.model: AbstractModelStructureOntology.java
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
package uk.ac.hutton.obiama.model;

import java.net.URI;

/**
 * <!-- AbstractModelStructureOntology -->
 * 
 * Odd though it may seem that a class creating a model structure ontology
 * should subclass from one assisting with the creation of a schedule ontology,
 * a model structure ontology is quite likely to use schedule ontology
 * constructs. In particular, it will do so if it uses any creators or queries.
 * A creator is simply a non-timed schedule, all the actions of which are
 * {@link uk.ac.hutton.obiama.action.Creator}s, and the
 * AbstractScheduleOntology contains methods to help build that. A query,
 * meanwhile, is an action (with a query ID), but only ever appears in the model
 * structure ontology.
 * 
 * @author Gary Polhill
 */
public abstract class AbstractModelStructureOntology extends AbstractScheduleOntology {

  /**
   * <!-- buildScheduleOntology -->
   * 
   * @see uk.ac.hutton.obiama.model.AbstractScheduleOntology#buildScheduleOntology()
   */
  @Override
  protected final void buildScheduleOntology() {
    ontologyImport(ObiamaOntology.ONTOLOGY_URI);
    buildModelStructureOntology();
  }

  protected abstract void buildModelStructureOntology();

  protected void addCreator(URI creatorURI, URI classURI) {
    annotationAssertionClass(classURI, ObiamaOntology.HAS_CREATOR_URI, creatorURI);
  }

  protected void addQuery(URI queryURI, URI classURI) {
    annotationAssertionClass(classURI, ObiamaOntology.HAS_QUERY_URI, queryURI);
  }

  protected void buildQuery(URI queryID, URI queryURI, URI implementationURI) {
    buildQuery(queryID, queryURI, implementationURI, null, null, (String)null, (String)null, (URI)null);
  }

  protected URI createQuery(URI queryID, URI implementationURI) {
    URI queryURI = Anon.QUERY.next(ontologyURI());
    buildQuery(queryID, queryURI, implementationURI);
    return queryURI;
  }

  protected void buildQuery(URI queryID, URI queryURI, URI implementationURI, URI... parameters) {
    buildQuery(queryID, queryURI, implementationURI, null, null, null, null, parameters);
  }

  protected URI createQuery(URI queryID, URI implementationURI, URI... parameters) {
    URI queryURI = Anon.QUERY.next(ontologyURI());
    buildQuery(queryID, queryURI, implementationURI, parameters);
    return queryURI;
  }

  protected void buildQuery(URI queryID, URI queryURI, URI implementationURI, String uriBase, String uriExtension) {
    buildQuery(queryID, queryURI, implementationURI, null, null, uriBase, uriExtension, (URI)null);
  }

  protected URI createQuery(URI queryID, URI implementationURI, String uriBase, String uriExtension) {
    URI queryURI = Anon.QUERY.next(ontologyURI());
    buildQuery(queryID, queryURI, implementationURI, uriBase, uriExtension);
    return queryURI;
  }

  protected void buildQuery(URI queryID, URI queryURI, URI implementationURI, String uriBase, String uriExtension,
      URI... parameters) {
    buildQuery(queryID, queryURI, implementationURI, null, null, uriBase, uriExtension, parameters);
  }

  protected URI createQuery(URI queryID, URI implementationURI, String uriBase, String uriExtension, URI... parameters) {
    URI queryURI = Anon.QUERY.next(ontologyURI());
    buildQuery(queryID, queryURI, implementationURI, uriBase, uriExtension, parameters);
    return queryURI;
  }

  protected void buildQuery(URI queryID, URI queryURI, URI implementationURI, URI provenanceURI, URI fcmpURI,
      String uriBase, String uriExtension, URI... parameters) {
    classAssertion(queryURI, ScheduleOntology.QUERY_URI);
    dataPropertyAssertion(queryURI, ScheduleOntology.QUERY_ID_URI, queryID);
    buildAction(queryURI, implementationURI, provenanceURI, fcmpURI, uriBase, uriExtension, parameters);
  }

  protected URI createQuery(URI queryID, URI implementationURI, URI provenanceURI, URI fcmpURI, String uriBase,
      String uriExtension, URI... parameters) {
    URI queryURI = Anon.QUERY.next(ontologyURI());
    buildQuery(queryID, queryURI, implementationURI, provenanceURI, fcmpURI, uriBase, uriExtension, parameters);
    return queryURI;
  }

}
