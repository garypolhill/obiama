/*
 * uk.ac.hutton.obiama.exception: ChangeToNonMutableIndividualException.java
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
package uk.ac.hutton.obiama.exception;

import java.net.URI;

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLProperty;

import uk.ac.hutton.obiama.action.Action;

/**
 * ChangeToNonMutableIndividualException
 * 
 * An exception caused when an action tries to change an individual that appears
 * in the model rather than the state ontology.
 * 
 * @author Gary Polhill
 */
public class ChangeToNonMutableIndividualException extends IntegrationInconsistencyException {
  /**
   * Serial number
   */
  private static final long serialVersionUID = -1813885937956377658L;

  /**
   * The aspect of the individual trying to be changed
   */
  URI aspect;

  /**
   * The kind of information about the individual trying to be changed
   */
  Class<? extends OWLEntity> aspectType;

  /**
   * The non-mutable individual trying to be changed
   */
  URI individual;

  /**
   * Constructor
   * 
   * @param originator The action trying to make an invalid change
   * @param entity The aspect of the individual trying to be changed
   * @param individual The non-mutable individual being changed
   */
  public ChangeToNonMutableIndividualException(Action originator, URI entity, URI individual) {
    super(originator);
    this.aspect = entity;
    this.aspectType = OWLEntity.class;
    this.individual = individual;
  }

  /**
   * Convenience constructor
   * 
   * @param originator
   * @param property
   * @param individual
   */
  public ChangeToNonMutableIndividualException(Action originator, OWLProperty<?, ?> property, OWLIndividual individual) {
    this(originator, property.getURI(), individual.getURI());
    this.aspectType = OWLProperty.class;
  }

  public ChangeToNonMutableIndividualException(Action originator, OWLDataProperty property, OWLIndividual subject) {
    this(originator, property.getURI(), subject.getURI());
    this.aspectType = OWLDataProperty.class;
  }

  public ChangeToNonMutableIndividualException(Action originator, OWLObjectProperty property, OWLIndividual subject) {
    this(originator, property.getURI(), subject.getURI());
    this.aspectType = OWLObjectProperty.class;
  }

  /**
   * @param action
   * @param concept
   * @param subject
   */
  public ChangeToNonMutableIndividualException(Action originator, OWLClass concept, OWLIndividual subject) {
    this(originator, concept.getURI(), subject.getURI());
    this.aspectType = OWLClass.class;
  }

  /**
   * @param action
   * @param individualURI
   */
  public ChangeToNonMutableIndividualException(Action originator, URI individualURI) {
    this(originator, null, individualURI);
  }

  /**
   * <!-- getErrorMessage -->
   * 
   * @see uk.ac.hutton.obiama.exception.IntegrationInconsistencyException#getErrorMessage()
   * @return
   */
  protected String getErrorMessage() {
    if(aspectType == null) return "Change to non-mutable individual " + individual;
    if(aspect == null) return "Change to " + aspectType.getName() + " of non-mutable individual " + individual;
    return "Change to " + aspectType.getName() + " " + aspect + " of non-mutable individual " + individual;
  }

}
