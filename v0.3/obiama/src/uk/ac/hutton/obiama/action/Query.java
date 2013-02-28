/*
 * uk.ac.hutton.obiama.action: Query.java
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
import java.util.Map;

import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.ScheduleException;
import uk.ac.hutton.util.FloatingPointComparison;

/**
 * <!-- Query -->
 * 
 * Interface for Queries. Queries create derived information from the ontology,
 * returning it to an action needing it, but without making changes to the
 * query.
 * 
 * @author Gary Polhill
 */
public interface Query<T> extends Process {
  public static final String ASK_METHOD_NAME = "ask";

  /**
   * Path to use for URIs of built-in query ontological entities
   */
  public static final String BUILT_IN_QUERY_PATH = "http://www.hutton.ac.uk/obiama/ontologies/built-in/query/";

  /**
   * <!-- ask -->
   * 
   * Get the information provided by the query
   * 
   * @param agent The agent providing the information
   * @param requester The agent requesting the information
   * @param args Arguments to the query (if any)
   * @return The information
   * @throws IntegrationInconsistencyException
   */
  public T ask(URI agent, URI requester, Process originator, Object... args) throws IntegrationInconsistencyException;

  public void setQueryID(URI queryID);
}
