/*
 * uk.ac.hutton.obiama.msb: ProvenanceFactory.java
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

import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.UsageException;

/**
 * <!-- ProvenanceFactory -->
 * 
 * Factory for the provenance API. Builds a provenance object in accordance with
 * the user's specifications on the command line. There is only ever one
 * provenance instance, which, once created, can be accessed at any time from
 * any class.
 * 
 * @author Gary Polhill
 */
public class ProvenanceFactory {

  /**
   * Provenance instance
   */
  private static Provenance provenance = null;

  /**
   * Disable the constructor
   */
  private ProvenanceFactory() {
    // singleton
  }

  /**
   * <!-- getProvenance -->
   * 
   * @return The provenance instance, as configured by the command line. Note
   *         that an instance of NoProvenance will be returned if the user
   *         doesn't want provenance.
   */
  public static Provenance getProvenance() {
    if(provenance == null) {
      if(ObiamaSetUp.getProvenanceImplementation() != null) {
        provenance = new OWLAPIProvenance();

        Provenance.Implementation implementation =
          Provenance.Implementation.valueOf(ObiamaSetUp.getProvenanceImplementation());
        implementation.configureRecorder(provenance);

        try {
          provenance.setHistoryProvenanceOntology(URI.create(ObiamaSetUp.getHistoryProvenanceURI()));
        }
        catch(UsageException e) {
          ErrorHandler.redo(e, "building the provenance recorder");
        }
      }
      else {
        provenance = new NoProvenance();
      }
    }

    return provenance;
  }
}
