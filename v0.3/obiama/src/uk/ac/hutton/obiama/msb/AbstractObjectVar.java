/*
 * uk.ac.hutton.obiama.msb: AbstractObjectVar.java
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

import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.action.Process;
import uk.ac.hutton.obiama.exception.NoSuchIndividualException;

/**
 * AbstractObjectVar
 * 
 * Abstract class for object properties.
 * 
 * @author Gary Polhill
 */
public abstract class AbstractObjectVar extends AbstractVar {
  /**
   * Give the property ivar a more specific class
   */
  final OWLObjectProperty property;

  /**
   * Constructor
   * 
   * @param process The action initiating the request for this variable
   * @param msb The model state broker
   * @param property The property the variable represents
   */
  AbstractObjectVar(Process process, AbstractModelStateBroker msb, OWLObjectProperty property) {
    super(process, msb);
    if(property == null) {
      throw new NullPointerException();
    }
    this.property = property;
  }

  /**
   * <!-- hasValueFor -->
   * 
   * Check if the object property has a value in the case of the individual
   * given as argument
   * 
   * @see uk.ac.hutton.obiama.msb.Var#hasValueFor(java.net.URI)
   */
  public boolean hasValueFor(URI individual) throws NoSuchIndividualException {
    Set<OWLIndividual> individuals = msb.getObjectPropertyValues(msb.getIndividual(process, individual), property);
    return individuals != null && individuals.size() > 0;
  }

  /**
   * <!-- isDataVar -->
   * 
   * @see uk.ac.hutton.obiama.msb.Var#isDataVar()
   */
  public boolean isDataVar() {
    return false;
  }

  /**
   * <!-- isObjectVar -->
   * 
   * @see uk.ac.hutton.obiama.msb.Var#isObjectVar()
   */
  public boolean isObjectVar() {
    return true;
  }

  /**
   * <!-- getType -->
   * 
   * @see uk.ac.hutton.obiama.msb.Var#getType()
   * @return The URI type
   */
  public XSDVocabulary getType() {
    return XSDVocabulary.ANY_URI;
  }

  /**
   * <!-- getJavaType -->
   * 
   * @see uk.ac.hutton.obiama.msb.Var#getJavaType()
   * @return The recommended Java type for this Var
   */
  public Class<?> getJavaType() {
    return URI.class;
  }

  /**
   * <!-- getProperty -->
   * 
   * @see uk.ac.hutton.obiama.msb.AbstractVar#getProperty()
   * @return The property
   */
  OWLObjectProperty getProperty() {
    return property;
  }

  /**
   * <!-- getURI -->
   * 
   * @see uk.ac.hutton.obiama.msb.AbstractVar#getURI()
   * @return The URI of this var--i.e. that of the property
   */
  public URI getURI() {
    return property.getURI();
  }
}
