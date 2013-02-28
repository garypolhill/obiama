/*
 * uk.ac.hutton.obiama.action: GetSpacesQuery.java
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
import uk.ac.hutton.obiama.model.ACCSpaceOntology;
import uk.ac.hutton.obiama.msb.Concept;
import uk.ac.hutton.obiama.msb.Instance;

/**
 * <!-- GetSpacesQuery -->
 * 
 * This query should be extended to GetMembersQuery
 * 
 * @author Gary Polhill
 */
public class GetSpacesQuery extends AbstractQuery<Set<URI>> implements Query<Set<URI>> {
  Concept euclidean2DSpace;

  /**
   * <!-- initialise -->
   * 
   * @see uk.ac.hutton.obiama.action.AbstractQuery#initialise()
   * @throws IntegrationInconsistencyException
   */
  @Override
  protected void initialise() throws IntegrationInconsistencyException {
    euclidean2DSpace =
      msb.getConcept(ACCSpaceOntology.EUCLIDEAN_2D_ACC_SPACE_URI, this, null, null);
  }

  /**
   * <!-- ask -->
   * 
   * Return the set of URIs of all members of #Euclidean2DACCSpace.
   *
   * @param agent Ignored (should be ObiamaOntology.GLOBAL_AGENT_URI)
   * @param requester Ignored (should possibly be ObiamaOntology.EXOGENOUS_AGENT_URI)
   * @return Set of URIs of members of #Euclidean2DACCSpace
   * @throws IntegrationInconsistencyException
   */
  public Set<URI> ask(URI agent, URI requester) throws IntegrationInconsistencyException {
    Set<Instance> possibleSpaces = euclidean2DSpace.getInstances();

    Set<URI> spaces = new HashSet<URI>();

    for(Instance possibleSpace: possibleSpaces) {
      spaces.add(possibleSpace.getURI());
    }

    return spaces;
  }


}
