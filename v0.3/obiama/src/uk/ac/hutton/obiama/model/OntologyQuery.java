/*
 * uk.ac.hutton.obiama.model: OntologyQuery.java
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

import uk.ac.hutton.obiama.action.Query;
import uk.ac.hutton.obiama.msb.ModelStateBroker;

/**
 * <!-- OntologyQuery -->
 * 
 * A query as stored in the Model Structure Ontology
 * 
 * @author Gary Polhill
 */
public class OntologyQuery extends AbstractNonScheduledAction {
  private final URI queryID;
  private Query<?> query;

  /**
   * @param queryID Identifier for the query (which matches that used by the
   *          action)
   * @param query The query itself, created by {@link ProcessFactory}
   * @param msb The model state broker
   * @param queryURI The URI of the query in the model structure ontology
   */
  public OntologyQuery(URI queryID, Query<?> query, ModelStateBroker msb, URI queryURI) {
    super(msb, queryURI);
    this.queryID = queryID;
    this.query = query;
  }

  /**
   * <!-- getQuery -->
   *
   * @return The query
   */
  public Query<?> getQuery() {
    return query;
  }

  /**
   * <!-- getQueryID -->
   *
   * @return The query identifier (to match with the {@link Process} using it
   */
  public URI getQueryID() {
    return queryID;
  }

}
