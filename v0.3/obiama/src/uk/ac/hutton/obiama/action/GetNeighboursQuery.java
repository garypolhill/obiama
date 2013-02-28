/*
 * uk.ac.hutton.obiama.action: GetNeighboursQuery.java 
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
import java.util.HashSet;
import java.util.Set;

import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.model.SpaceOntology;
import uk.ac.hutton.obiama.msb.Concept;
import uk.ac.hutton.obiama.msb.Value;
import uk.ac.hutton.obiama.msb.Var;

/**
 * <!-- GetNeighboursQuery -->
 * 
 * <p>
 * This is a simple neighbourhood computation query, for an agent to ask another
 * agent their neighbourhood. Spatially, the neighbourhood of an agent is
 * computed as per the EC relation from RCC: the set of regions ("locations"
 * here, to be more general) that connect with, but do not overlap the subject
 * location. The ontology assumed by this query consists of the class #Location,
 * relations #locatedAt and #locationOf (range #Location and domain #Location
 * respectively), which are mutually inverse, and location relations
 * #connectedTo and #overlaps. All are defined in the Space Ontology.
 * </p>
 * 
 * <p>
 * The returned values depend on whether the agent the neighbours of which are
 * requested is itself a location. If so, then the query will return
 * neighbouring locations; if not, then the query will return all objects that
 * the neighbouring locations are the #locationOf.
 * </p>
 * 
 * @author Gary Polhill
 * @see uk.ac.hutton.obiama.model.SpaceOntology
 */
public class GetNeighboursQuery extends AbstractQuery<Set<URI>> implements Query<Set<URI>> {
  Var locatedAt;
  Var locationOf;
  Var connectedTo;
  Var overlaps;
  Concept location;

  /**
   * <!-- initialise -->
   * 
   * Set up the query
   * 
   * @see uk.ac.hutton.obiama.action.AbstractQuery#initialise()
   * @throws IntegrationInconsistencyException
   */
  public void initialise() throws IntegrationInconsistencyException {
    locatedAt = getVar(SpaceOntology.LOCATED_AT_URI);
    locationOf = getVar(SpaceOntology.LOCATION_OF_URI);
    connectedTo = getVar(SpaceOntology.CONNECTED_TO_URI);
    overlaps = getVar(SpaceOntology.OVERLAPS_URI);
    vars.add(locatedAt);
    vars.add(locationOf);
    vars.add(connectedTo);
    vars.add(overlaps);
    Set<Var> locationVars = new HashSet<Var>();
    locationVars.add(connectedTo);
    locationVars.add(overlaps);
    locationVars.add(locationOf);
    location = getConcept(SpaceOntology.LOCATION_URI, locationVars);
  }

  /**
   * <!-- ask -->
   * 
   * Perform the query. Find the set of locations that connect but do not
   * overlap with the location(s) of the agent. If the agent is a location,
   * return this set; else return the agents located at the set.
   * 
   * @param agent The agent the neighbourhood of which is requested
   * @param requester The agent requiring the knowledge (not used)
   * @return Either a set of locations (if the agent is a location) or a set of
   *         agents at the neighbouring locations
   * @throws IntegrationInconsistencyException
   */
  public Set<URI> ask(URI agent, URI requester) throws IntegrationInconsistencyException {
    boolean returnLocations = true;

    Set<URI> locs = new HashSet<URI>();

    // Is the agent a location?
    if(location.hasInstance(agent)) {
      locs.add(agent);
      returnLocations = false;
    }
    else {
      Value<URI> vLocs = locatedAt.getValueFor(agent);
      vLocs.getAll(locs);
    }

    // Find all the neighbours of the agent('s location(s))
    Set<URI> overlapping = new HashSet<URI>();
    for(URI loc: locs) {
      Value<URI> vOver = overlaps.getValueFor(loc);
      vOver.getAll(overlapping);
    }

    Set<URI> nbrs = new HashSet<URI>();
    for(URI loc: locs) {
      Value<URI> vConnx = connectedTo.getValueFor(loc);
      for(URI nbr: vConnx) {
        if(!overlapping.contains(nbr)) nbrs.add(nbr);
      }
    }

    // Return the locations if the agent is a location
    if(returnLocations) return nbrs;

    // Return the objects located in the locations otherwise
    Set<URI> objs = new HashSet<URI>();
    for(URI nbr: nbrs) {
      Value<URI> vObjs = locationOf.getValueFor(nbr);
      vObjs.getAll(objs);
    }

    return objs;
  }

}
