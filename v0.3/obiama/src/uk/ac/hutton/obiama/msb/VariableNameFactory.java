/*
 * uk.ac.hutton.obiama.msb: VariableNameFactory.java
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

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLPropertyExpression;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.action.Process;
import uk.ac.hutton.obiama.exception.InconsistentDomainException;
import uk.ac.hutton.obiama.exception.InconsistentRangeException;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;

/**
 * VariableNameFactory
 * 
 * A factory for building VariableNames
 * 
 * @author Gary Polhill
 */
class VariableNameFactory {

  /**
   * <!-- getVariableName -->
   * 
   * Build a Var for an object property with unspecified domain and range
   * 
   * @param objectProperty The object property
   * @param process The action requesting the Var
   * @param msb The model state broker
   * @return The right kind of Var
   */
  public static Var getVariableName(OWLObjectProperty objectProperty, Process process, AbstractModelStateBroker msb) {
    if(objectProperty.isFunctional(msb.getModel())) {
      return new FunctionalObjectVar(process, msb, objectProperty);
    }
    else {
      return new NonFunctionalObjectVar(process, msb, objectProperty);
    }
  }

  /**
   * <!-- getVariableName -->
   * 
   * Build a Var for a data property with unspecified domain and range
   * 
   * @param dataProperty The data property
   * @param process The action requestion the Var
   * @param msb The model state broker
   * @return The right kind of Var
   * @throws IntegrationInconsistencyException
   */
  public static Var getVariableName(OWLDataProperty dataProperty, Process process, AbstractModelStateBroker msb) throws IntegrationInconsistencyException {
    Set<OWLDataRange> ranges = dataProperty.getRanges(msb.getModel());
    XSDVocabulary type = null;
    for(OWLDataRange range: ranges) {
      type = XSDHelper.generaliseType(type, range);
    }
    if(dataProperty.isFunctional(msb.getModel())) {
      return new FunctionalDataVar(process, msb, dataProperty, type);
    }
    else {
      return new NonFunctionalDataVar(process, msb, dataProperty, type);
    }
  }

  /**
   * <!-- getVariableName -->
   * 
   * Build a VariableName for an object property on behalf of an action that
   * expects that property to have a certain domain and range.
   * 
   * @param objectProperty the object property
   * @param domain the expected domain of the object property
   * @param range the expected range of the object property
   * @param process the action with the expectations
   * @param msb the ModelStateBroker
   * @return a VariableName for the object property
   * @throws IntegrationInconsistencyException
   */
  public static Var getVariableName(OWLObjectProperty objectProperty, OWLClass domain, OWLClass range, Process process,
      AbstractModelStateBroker msb) throws IntegrationInconsistencyException {
    checkDomain(objectProperty, domain, process, msb.getModel());
    checkRange(objectProperty, range, process, msb.getModel());
    return getVariableName(objectProperty, process, msb);
  }

  public static Var getVariableName(OWLObjectProperty objectProperty, OWLClass range, Process process,
      AbstractModelStateBroker msb) throws IntegrationInconsistencyException {
    checkRange(objectProperty, range, process, msb.getModel());
    return getVariableName(objectProperty, process, msb);
  }

  /**
   * <!-- getVariableName -->
   * 
   * Build a VariableName for a data property on behalf of an action that
   * expects that property to have a certain domain and range; the latter being
   * expressed as an XSD datatype.
   * 
   * @param dataProperty the data property
   * @param domain the expected domain of the data property
   * @param range the expected range of the data property
   * @param process the action with the expectations
   * @param msb the ModelStateBroker
   * @return a VariableName for the data property
   * @throws IntegrationInconsistencyException
   */
  public static Var getVariableName(OWLDataProperty dataProperty, OWLClass domain, XSDVocabulary range,
      Process process, AbstractModelStateBroker msb) throws IntegrationInconsistencyException {
    checkDomain(dataProperty, domain, process, msb.getModel());
    checkRange(dataProperty, range, process, msb.getModel());
    if(dataProperty.isFunctional(msb.getModel())) {
      return new FunctionalDataVar(process, msb, dataProperty, range);
    }
    else {
      return new NonFunctionalDataVar(process, msb, dataProperty, range);
    }
  }

  public static Var getVariableName(OWLDataProperty dataProperty, XSDVocabulary range, Process process,
      AbstractModelStateBroker msb) throws IntegrationInconsistencyException {
    checkRange(dataProperty, range, process, msb.getModel());
    if(dataProperty.isFunctional(msb.getModel())) {
      return new FunctionalDataVar(process, msb, dataProperty, range);
    }
    else {
      return new NonFunctionalDataVar(process, msb, dataProperty, range);
    }
  }

  /**
   * <!-- checkRange -->
   * 
   * Check that the range of a data property and its equivalents contains a
   * range requested by an action. Ranges of a property are assumed to form an
   * intersection.
   * 
   * @param property the property to check
   * @param range the type of range the property is expected to have by the
   *          action
   * @param process the action with the expectation
   * @param model the model ontology
   * @throws InconsistentRangeException
   */
  static void checkRange(OWLDataProperty property, XSDVocabulary range, Process process, Set<OWLOntology> model)
      throws InconsistentRangeException {
    Set<OWLDataRange> propertyRanges = new HashSet<OWLDataRange>(property.getRanges(model));

    for(OWLDataPropertyExpression expr: property.getEquivalentProperties(model)) {
      propertyRanges.addAll(expr.getRanges(model));
    }

    for(OWLDataRange propertyRange: propertyRanges) {
      if(!XSDHelper.dataRangeContains(propertyRange, range)) {
        throw new InconsistentRangeException(property.getURI(), null, range.getURI(), process);
      }
    }
  }

  /**
   * <!-- checkRange -->
   * 
   * Check that the range of an object property contains a range requested by an
   * action
   * 
   * @param property the property to check
   * @param range URI of the concept expected to be included in the range of the
   *          property
   * @param process the action with the expectation
   * @param model the model ontology
   * @throws InconsistentRangeException
   */
  static void checkRange(OWLObjectProperty property, OWLClass range, Process process, Set<OWLOntology> model)
      throws InconsistentRangeException {
    Set<OWLDescription> descs = new HashSet<OWLDescription>(property.getRanges(model));
    for(OWLObjectPropertyExpression expr: property.getEquivalentProperties(model)) {
      descs.addAll(expr.getRanges(model));
    }
    if(!checkDescriptionSet(property.getRanges(model), range, process, model)) {
      throw new InconsistentRangeException(property.getURI(), null, range.getURI(), process);
    }
  }

  /**
   * <!-- checkDomain -->
   * 
   * Check that the domain of an object property contains a domain requested by
   * an action
   * 
   * @param property the property to check
   * @param domain URI of the concept expected to be included in the domain of
   *          the property
   * @param process the action with the expectation
   * @param model the model ontology
   * @throws InconsistentDomainException
   */
  static void checkDomain(OWLProperty<?, ?> property, OWLClass domain, Process process, Set<OWLOntology> model)
      throws InconsistentDomainException {
    Set<OWLDescription> descs = new HashSet<OWLDescription>(property.getDomains(model));
    for(OWLPropertyExpression<?, ?> expr: property.getEquivalentProperties(model)) {
      descs.addAll(expr.getDomains(model));
    }
    if(!checkDescriptionSet(descs, domain, process, model)) {
      throw new InconsistentDomainException(property.getURI(), domain.getURI(), process);
    }
  }

  /**
   * <!-- checkDescriptionSet -->
   * 
   * Check that a description set (e.g. the domains or ranges of a property)
   * includes a class expected by an action. The getDomains() and getRanges()
   * methods of the OWLProperty class, used to generate the description set,
   * return a set of OWLDescriptions. The OWL API says that the domain of the
   * property "is essentially the intersection of these descriptions".
   * Therefore, we confirm that the requested range is a subclass of (or
   * equivalent to) all the OWLDescriptions. This assumes the reasoner
   * exhaustively generates all subclass axioms, which it may not...
   * 
   * @param descs A set of descriptions
   * @param check The class to check
   * @param process The action generating the class
   * @param model The model ontology
   * @return true if the class to check is contained in the intersection of the
   *         set of the descriptions
   */

  private static boolean checkDescriptionSet(Set<OWLDescription> descs, OWLClass check, Process process,
      Set<OWLOntology> model) {
    Set<OWLDescription> checks = new HashSet<OWLDescription>();
    checks.add(check);
    checks.addAll(check.getSuperClasses(model));
    checks.addAll(check.getEquivalentClasses(model));
    for(OWLDescription desc: descs) {
      if(!(checks.contains(desc))) return false;
    }
    return true;
  }

}
