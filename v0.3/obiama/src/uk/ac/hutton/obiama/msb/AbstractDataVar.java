/* uk.ac.hutton.obiama.msb: AbstractDataVar.java
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
package uk.ac.hutton.obiama.msb;

import java.net.URI;
import java.util.Set;

import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.action.Process;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.NoSuchIndividualException;

/**
 * AbstractDataVar
 *
 * Abstract class for data variables.
 *
 * @author Gary Polhill
 */
public abstract class AbstractDataVar extends AbstractVar {
  /**
   * More specific class for the property ivar
   */
  final OWLDataProperty property;
  
  /**
   * The XSD datatype this variable contains
   */
  XSDVocabulary type;

  /**
   * Constructor
   * 
   * @param process The action originating the request for the variable
   * @param msb The model state broker containing the ontology the variable belongs to
   * @param property The property the variable represents
   * @param range The expected range of the property
   * @throws IntegrationInconsistencyException 
   */
  AbstractDataVar(Process process, AbstractModelStateBroker msb,
      OWLDataProperty property, XSDVocabulary range) throws IntegrationInconsistencyException {
    super(process, msb);
    this.property = property;
    if(range == null) {
      type = msb.getDataRangeOf(this, process);
    }
    else {
      type = range;
    }
    type = type == null ? XSDVocabulary.ANY_TYPE : type;
  }

  /**
   * <!-- hasValueFor -->
   * 
   * Check if the data property has a value for the specified individual
   *
   * @see uk.ac.hutton.obiama.msb.Var#hasValueFor(java.net.URI)
   */
  public boolean hasValueFor(URI individual) throws NoSuchIndividualException {
    Set<String> values =
      msb.getDataPropertyValues(msb.getIndividual(process, individual), property);
    return values != null && values.size() > 0;
  }

  /**
   * <!-- isDataVar -->
   *
   * @see uk.ac.hutton.obiama.msb.Var#isDataVar()
   */
  public boolean isDataVar() {
    return true;
  }

  /**
   * <!-- isObjectVar -->
   *
   * @see uk.ac.hutton.obiama.msb.Var#isObjectVar()
   */
  public boolean isObjectVar() {
    return false;
  }
  
  /**
   * <!-- getType -->
   *
   * @see uk.ac.hutton.obiama.msb.Var#getType()
   * @return The datatype assigned to this Var
   */
  public XSDVocabulary getType() {
    return type;
  }
  
  /**
   * <!-- getJavaType -->
   *
   * @see uk.ac.hutton.obiama.msb.Var#getJavaType()
   * @return A recommended Java type to store data in this Var
   */
  public Class<?> getJavaType() {
    return XSDHelper.recommendedClassFor(type);
  }
  
  /**
   * <!-- getProperty -->
   *
   * @see uk.ac.hutton.obiama.msb.AbstractVar#getProperty()
   * @return The property
   */
  OWLDataProperty getProperty() {
    return property;
  }
  
  /**
   * <!-- getURI -->
   *
   * @see uk.ac.hutton.obiama.msb.AbstractVar#getURI()
   * @return The URI of this Var (i.e. that of the property)
   */
  public URI getURI() {
    return property.getURI();
  }

}
