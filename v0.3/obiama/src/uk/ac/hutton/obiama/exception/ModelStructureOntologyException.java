/* uk.ac.hutton.obiama.exception: ModelStructureOntologyException.java
 *
 * Copyright (C) 2013 The James Hutton Institute
 *
 * This file is part of obiama-0.3.
 *
 * obiama-0.3 is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 *
 * obiama-0.3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with obiama-0.3. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Contact information:
 *   Gary Polhill
 *   The James Hutton Institute, Craigiebuckler, Aberdeen. AB15 8QH. UK.
 *   gary.polhill@hutton.ac.uk
 */
package uk.ac.hutton.obiama.exception;

import java.net.URI;

/**
 * <!-- ModelStructureOntologyException -->
 *
 * @author Gary Polhill
 */
public class ModelStructureOntologyException extends Exception {
  /**
   * Version ID
   */
  private static final long serialVersionUID = -8693189180779230538L;

  /**
   * @param annotationURI URI of the annotation (from annotation.getAnnotationURI())
   * @param subject URI of the subject of the annotation
   * @param message message describing the problem
   */
  public ModelStructureOntologyException(URI annotationURI, URI subject, String message) {
    super(message + " in annotation " + annotationURI + " on " + subject);
  }

  /**
   * @param entityURI
   * @param message
   */
  public ModelStructureOntologyException(URI entityURI, String message) {
    super("Error with model structure ontology entity " + entityURI + ": " + message);
  }


}
