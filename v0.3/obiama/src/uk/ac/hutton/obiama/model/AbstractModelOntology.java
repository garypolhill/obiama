/*
 * uk.ac.hutton.obiama.model: AbstractModelOntology.java
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

import java.io.File;
import java.net.URI;

import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyStorageException;
import org.semanticweb.owl.model.UnknownOWLOntologyException;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.model.AbstractOntology.OWLConceptExpression;

/**
 * <!-- AbstractModelOntology -->
 * 
 * <p>
 * A class to be used outside the package for OBIAMA models to create their
 * ontologies with. It offers slightly reduced functionality than
 * {@link AbstractOntology}, but does not require subclasses to interact with
 * the OWL API.
 * </p>
 * 
 * <p>
 * To create a model ontology using this class, do the following:
 * </p>
 * 
 * <ol>
 * <li>Create a subclass of <code>AbstractModelOntology</code>.</li>
 * <li>Define <code>public static</code> URI fields for each named entity in
 * your ontology. The general form is to define an <code>ONTOLOGY_URI</code>
 * field, which is the logical URI of your ontology, and then each URI in the
 * ontology is defined as e.g.
 * <code>public static URI MY_CLASS_URI = URI.create(ONTOLOGY_URI + "#MyClass");</code>
 * </li>
 * <li>Create an implementation of {@link #ontologyURI()}, which returns
 * <code>ONTOLOGY_URI</code>.</li>
 * <li>Create an implementation of {@link #buildOntology()}, which calls
 * protected methods in this class to create the axioms in the ontology.</li>
 * <li>Create an implementation of <code>main(String[])</code>, which calls the
 * class's constructor, and then calls {@link #saveOntology} to save the
 * ontology to a file -- e.g. supplied as a command line argument.</li>
 * </ol>
 * 
 * <p>
 * The class is implemented as a wrapper around {@link AbstractOntology},
 * largely because this works around Java language constraints in constructors.
 * (e.g. you can't call a non-static method in <code>super(method())</code>, and
 * static methods cannot be abstract.)
 * </p>
 * 
 * <p>
 * The protected methods creating axioms in the ontology are named in accordance
 * with the <a href="http://www.w3.org/TR/owl2-syntax/">OWL 2 Web Ontology
 * Language Structural Specification and Functional-Style Syntax</a> <a
 * href="http://www.w3.org/TR/2009/REC-owl2-syntax-20091027/">W3C Recommendation
 * 27 October 2009</a>, with a bit of 'licence' to allow for Java naming
 * conventions, and, where methods combine certain expressions, convenience.
 * </p>
 * 
 * @author Gary Polhill
 */
public abstract class AbstractModelOntology {
  /**
   * Wrapped AbstractOntology
   */
  private ModelOntology ontology;

  /**
   * Constructor. Problems creating the ontology are fatal exceptions.
   */
  public AbstractModelOntology() {
    try {
      ontology = new ModelOntology(ontologyURI());
    }
    catch(OWLOntologyCreationException e) {
      ErrorHandler.fatal(e, "Creating model ontology " + ontologyURI());
      throw new Bug();
    }
    catch(OWLOntologyChangeException e) {
      ErrorHandler.fatal(e, "Creating model ontology " + ontologyURI());
      throw new Bug();
    }
  }

  /**
   * <!-- ontologyURI -->
   * 
   * Subclasses should implement this method to return the logical ontology URI
   * of their model ontology. If this is implemented as suggested above, then
   * this will simply <code>return ONTOLOGY_URI;</code>
   * 
   * @return the ontology logical URI
   */
  protected abstract URI ontologyURI();

  /**
   * <!-- buildOntology -->
   * 
   * This is called out from the {@link #ModelOntology} instance created in the
   * constructor. The argument should be the same object as {@link ontology},
   * however, the assignment in the constructor will not have taken place yet,
   * and all the wrapper methods below rely on {@link ontology} pointing to the
   * instance.
   * 
   * @param ontology {@link #ModelOntology} object build in the {@link
   *          AbstractModelOntology()} constructor.
   */
  private void buildOntology(ModelOntology ontology) {
    this.ontology = ontology;
    buildOntology();
  }

  /**
   * <!-- buildOntology -->
   * 
   * Subclasses should implement this method as a series of calls to the
   * <code>protected</code> methods in this class that wrap calls to
   * corresponding {@link AbstractOntology} methods that make the assertions in
   * the ontology.
   */
  protected abstract void buildOntology();

  /**
   * <!-- getURI -->
   * 
   * This method should return the same value as {@link #ontologyURI()}, but is
   * provided as a wrapper of {@link AbstractOntology#getURI()}. There are
   * circumstances when a different value might be returned (e.g. if
   * <code>AbstractModelOntology<code> ever provides wrappers for the methods for
   * building inferred ontologies in {@link AbstractOntology}.)
   * 
   * @return The URI of the {@link #ontology}
   */
  public URI getURI() {
    return ontology.getURI();
  }

  /**
   * <!-- saveOntology -->
   * 
   * Provide a wrapper for {@link AbstractOntology#saveOntology(String)}.
   * Exceptions are treated as 'redos' allowing methods calling this to catch
   * the {@link uk.ac.hutton.obiama.exception.Redo} and try again.
   * 
   * @param filename
   */
  public void saveOntology(String filename) {
    File file = new File(filename);

    try {
      ontology.saveOntology(file.toURI());
    }
    catch(UnknownOWLOntologyException e) {
      ErrorHandler.redo(e, "saving model ontology " + getURI() + " to file " + filename);
      throw new Bug();
    }
    catch(OWLOntologyStorageException e) {
      ErrorHandler.redo(e, "saving model ontology " + getURI() + " to file " + filename);
      throw new Bug();
    }
  }

  /**
   * <!-- ontologyImport -->
   * 
   * Assert that the ontology imports some ontologies.
   * 
   * <code>Ontology(... Import(<i>X</i>) ...)</code>
   * 
   * @param uris logical URI(s) of the ontology(/ies) to import
   */
  protected void ontologyImport(URI... uris) {
    ontology.imports(uris);
  }

  /**
   * <!-- ontologyAnnotationComment -->
   * 
   * <code>Ontology(... Annotation(rdfs:comment <i>X</i>) ...)</code>
   * 
   * @param comment The comment on the ontology
   */
  protected void ontologyAnnotationComment(String comment) {
    ontology.ontologyComment(comment);
  }

  /**
   * <!-- declarationClass -->
   * 
   * Provide an implementation of the <code>Declaration(Class(<i>X</i>))</code>
   * axiom.
   * 
   * @param classURIs URIs of entities to be declared as classes.
   */
  protected void declarationClass(URI... classURIs) {
    ontology.declareClass(classURIs);
  }

  /**
   * <!-- annotationAssertionCommentClass -->
   * 
   * Add a comment to a class
   * 
   * <code>AnnotationAssertion(rdfs:comment <i>class</i> <i>comment</i>)</code>
   * 
   * @param classURI URI of class to comment on
   * @param comment the comment
   */
  protected void annotationAssertionCommentClass(URI classURI, String comment) {
    ontology.classComment(classURI, comment);
  }

  /**
   * <!-- annotationAssertionLabelClass -->
   * 
   * Add a human readable label to a class
   * 
   * <code>AnnotationAssertion(rdfs:label <i>class</i> <i>comment</i>)</code>
   * 
   * @param classURI
   * @param label
   */
  protected void annotationAssertionLabelClass(URI classURI, String label) {
    ontology.classLabel(classURI, label);
  }

  protected void annotationAssertionClass(URI classURI, URI annotationURI, URI individualURI) {
    ontology.classAnnotation(classURI, annotationURI, individualURI);
  }

  protected void annotationAssertionClass(URI classURI, URI annotationURI, double value) {
    ontology.classAnnotation(classURI, annotationURI, value);
  }

  protected void annotationAssertionClass(URI classURI, URI annotationURI, float value) {
    ontology.classAnnotation(classURI, annotationURI, value);
  }

  protected void annotationAssertionClass(URI classURI, URI annotationURI, int value) {
    ontology.classAnnotation(classURI, annotationURI, value);
  }

  protected void annotationAssertionClass(URI classURI, URI annotationURI, long value) {
    ontology.classAnnotation(classURI, annotationURI, value);
  }

  protected void annotationAssertionClass(URI classURI, URI annotationURI, boolean value) {
    ontology.classAnnotation(classURI, annotationURI, value);
  }

  protected void annotationAssertionClass(URI classURI, URI annotationURI, String value) {
    ontology.classAnnotation(classURI, annotationURI, value);
  }

  protected void annotationAssertionClass(URI classURI, URI annotationURI, XSDVocabulary type, String value) {
    ontology.classAnnotation(classURI, annotationURI, type, value);
  }

  protected void disjointClasses(URI... classURIs) {
    ontology.disjointClasses(classURIs);
  }

  protected void disjointClasses(OWLConceptExpression... exprs) {
    ontology.disjointClasses(exprs);
  }

  protected void disjointClasses(URI classURI, OWLConceptExpression expr) {
    ontology.disjointClasses(classURI, expr);
  }

  protected void equivalentClasses(URI... classURIs) {
    ontology.equivalentClasses(classURIs);
  }

  protected void equivalentClasses(URI classURI, OWLConceptExpression expr) {
    ontology.equivalentClasses(classURI, expr);
  }

  protected void subClassOf(URI class1URI, URI... class2URIs) {
    ontology.subClassOf(class1URI, class2URIs);
  }

  protected void subClassOf(URI classURI, OWLConceptExpression expr) {
    ontology.subClassOf(classURI, expr);
  }

  protected void subClassOf(OWLConceptExpression subExpr, OWLConceptExpression superExpr) {
    ontology.subClassOf(subExpr, superExpr);
  }

  /**
   * <!-- subClassOfObjectUnionOf -->
   * 
   * <code>SubClassOf(<i>X</i> ObjectUnionOf(<i>Y1</i> <i>Y2</i> ...))</code>
   * 
   * @param class1URI
   * @param class2URIs
   */
  protected void subClassOfObjectUnionOf(URI class1URI, URI... class2URIs) {
    ontology.subClassOfUnion(class1URI, class2URIs);
  }

  /**
   * <!-- subClassOfObjectIntersectionOf -->
   * 
   * <code>SubClassOf(<i>X</i> ObjectIntersectionof(<i>Y1</i> <i>Y2</i> ...))</code>
   * 
   * @param class1URI
   * @param class2URIs
   */
  protected void subClassOfObjectIntersectionOf(URI class1URI, URI... class2URIs) {
    ontology.subClassOfIntersection(class1URI, class2URIs);
  }

  protected void disjointUnion(URI class1URI, URI... class2URIs) {
    ontology.disjointUnion(class1URI, class2URIs);
  }

  protected void disjointUnion(URI class1URI, OWLConceptExpression... exprs) {
    ontology.disjointUnion(class1URI, exprs);
  }

  /**
   * <!-- namedClass -->
   * 
   * Provide an implementation of the <code>Class(<i>X</i>)</code>
   * ClassExpression. When using class expressions, note that the
   * {@link AbstractOntology#OWLConceptExpression} object returned by this and
   * other methods providing ClassExpressions will not be usable, as this inner
   * class is not visible. It is suggested that class expressions are simply
   * built up using nested method calls. For example
   * <code>ObjectComplementOf(ObjectUnionOf(A, B))</code> could be written
   * <code>objectComplementOf(objectUnionOf(namedClass(A), namedClass(B)))</code>
   * 
   * @param classURI URI of class to be given as a ClassExpression
   * @return ClassExpression consisting of the class.
   */
  protected OWLConceptExpression namedClass(URI classURI) {
    return ontology.namedClass(classURI);
  }

  /**
   * <!-- dataAllValuesFromDataOneOf -->
   * 
   * Provide an implementation of the ClassExpression
   * <code>DataAllValuesFrom(<i>X</i> DataOneOf(<i>Y</i> ...))</code>
   * 
   * @param propURI URI of property being restricted
   * @param values values it is being restricted to
   * @return ClassExpression
   */
  protected OWLConceptExpression dataAllValuesFromDataOneOf(URI propURI, Object... values) {
    return ontology.dataAllOneOfRestriction(propURI, values);
  }

  /**
   * <!-- dataAllValuesFromDatatypeRestrictionMinExclusive -->
   * 
   * Provide an implementation of the ClassExpression
   * <code>DataAllValuesFrom(<i>X</i> DatatypeRestriction(<i>type</i> xsd:maxExclusive
   * <i>Y</i>))</code>
   * 
   * <code><i>type</i></code> is inferred from the type of <code>value</code>
   * 
   * @param propURI URI of property being restricted
   * @param value
   * @return ClassExpression
   */
  protected OWLConceptExpression dataAllValuesFromDatatypeRestrictionMaxExclusive(URI propURI, Number value) {
    return ontology.dataAllLTRestriction(propURI, value);
  }

  /**
   * <!-- dataAllValuesFromDatatypeRestrictionMaxInclusive -->
   * 
   * Provide an implementation of the ClassExpression
   * <code>DataAllValuesFrom(<i>X</i> DatatypeRestriction(<i>type</i> xsd:maxInclusive <i>Y</i>))</code>
   * 
   * @param propURI URI of property being restricted
   * @param value
   * @return ClassExpression
   */
  protected OWLConceptExpression dataAllValuesFromDatatypeRestrictionMaxInclusive(URI propURI, Number value) {
    return ontology.dataAllLERestriction(propURI, value);
  }

  /**
   * <!-- dataAllValuesFromDatatypeRestrictionMinInclusive -->
   * 
   * Provide an implementation of the ClassExpression
   * <code>DataAllValuesFrom(<i>X</i> DatatypeRestriction(<i>type</i> xsd:minInclusive <i>Y</i>))</code>
   * 
   * @param propURI URI of property being restricted
   * @param value
   * @return ClassExpression
   */
  protected OWLConceptExpression dataAllValuesFromDatatypeRestrictionMinInclusive(URI propURI, Number value) {
    return ontology.dataAllGTRestriction(propURI, value);
  }

  /**
   * <!-- dataAllValuesFromDatatypeRestrictionMinExclusive -->
   * 
   * Provide an implementation of the ClassExpression
   * <code>DataAllValuesFrom(<i>X</i> DatatypeRestriction(<i>type</i> xsd:maxExclusive <i>Y</i>))</code>
   * 
   * @param propURI URI of property being restricted
   * @param value
   * @return ClassExpression
   */
  protected OWLConceptExpression dataAllValuesFromDatatypeRestrictionMinExclusive(URI propURI, Number value) {
    return ontology.dataAllGERestriction(propURI, value);
  }

  /**
   * <!-- dataAllValuesFromDatatypeRestrictionLength -->
   * 
   * Provide an implementation of the ClassExpression
   * <code>DataAllValuesFrom(<i>X</i> DatatypeRestriction(xsd:string xsd:length <i>Y</i>))</code>
   * 
   * @param propURI URI of property being restricted
   * @param value length
   * @return ClassExpression
   */
  protected OWLConceptExpression dataAllValuesFromDatatypeRestrictionLength(URI propURI, int value) {
    return ontology.dataAllLengthRestriction(propURI, value);
  }

  /**
   * <!-- dataAllValuesFromDatatypeRestrictionMaxLength -->
   * 
   * Provide an implementation of the ClassExpression
   * <code>DataAllValuesFrom(<i>X</i> DatatypeRestriction(xsd:string xsd:maxLength <i>Y</i>))</code>
   * 
   * @param propURI URI of property being restricted
   * @param value maximum length
   * @return ClassExpression
   */
  protected OWLConceptExpression dataAllValuesFromDatatypeRestrictionMaxLength(URI propURI, int value) {
    return ontology.dataAllMaxLengthRestriction(propURI, value);
  }

  /**
   * <!-- dataAllValuesFromDatatypeRestrictionMinLength -->
   * 
   * Provide an implementation of the ClassExpression
   * <code>DataAllValuesFrom(<i>X</i> DatatypeRestriction(xsd:string xsd:minLength <i>Y</i>))</code>
   * 
   * @param propURI URI of property being restricted
   * @param value minimum length
   * @return ClassExpression
   */
  protected OWLConceptExpression dataAllValuesFromDatatypeRestrictionMinLength(URI propURI, int value) {
    return ontology.dataAllMinLengthRestriction(propURI, value);
  }

  /**
   * <!-- dataAllValuesFromDatatypeRestrictionPattern -->
   * 
   * Provide an implementation of the ClassExpression
   * <code>DataAllValuesFrom(<i>X</i> DatatypeRestriction(xsd:string xsd:pattern <i>Y</i>))</code>
   * 
   * @param propURI URI of property being restricted
   * @param pattern
   * @return ClassExpression
   */
  protected OWLConceptExpression dataAllValuesFromDatatypeRestrictionPattern(URI propURI, String pattern) {
    return ontology.dataAllPatternRestriction(propURI, pattern);
  }

  /**
   * <!-- dataAllValuesFromDatatypeRestriction -->
   * 
   * Provide an implementation of the ClassExpression
   * <code>DataAllValuesFrom(<i>X</i> DatatypeRestriction(<i>type</i>))</code>
   * 
   * @param propURI URI of property being restricted
   * @param type
   * @return ClassExpression
   */
  protected OWLConceptExpression dataAllValuesFromDatatypeRestriction(URI propURI, XSDVocabulary type) {
    return ontology.dataAllTypeRestriction(propURI, type);
  }

  /**
   * <!-- dataExactCardinality -->
   * 
   * Provide an implementation of the ClassExpression
   * <code>DataExactCardinality(<i>n</i> <i>X</i>)</code>
   * 
   * @param propURI URI of property being restricted
   * @param card exact cardinality
   * @return ClassExpression
   */
  protected OWLConceptExpression dataExactCardinality(URI propURI, int card) {
    return ontology.dataExactCardinalityRestriction(propURI, card);
  }

  /**
   * <!-- dataMaxCardinality -->
   * 
   * Provide an implementation of the ClassExpression
   * <code>DataMaxCardinality(<i>n</i> <i>X</i>)</code>
   * 
   * @param propURI URI of property being restricted
   * @param card exact cardinality
   * @return ClassExpression
   */
  protected OWLConceptExpression dataMaxCardinality(URI propURI, int card) {
    return ontology.dataMaxCardinalityRestriction(propURI, card);
  }

  /**
   * <!-- dataMinCardinality -->
   * 
   * Provide an implementation of the ClassExpression
   * <code>DataMinCardinality(<i>n</i> <i>X</i>)</code>
   * 
   * @param propURI URI of property being restricted
   * @param card exact cardinality
   * @return ClassExpression
   */
  protected OWLConceptExpression dataMinCardinality(URI propURI, int card) {
    return ontology.dataMinCardinalityRestriction(propURI, card);
  }

  /**
   * <!-- dataSomeValuesFromDataOneOf -->
   * 
   * Provide an implementation of the ClassExpression
   * <code>DataSomeValuesFrom(<i>X</i> DataOneOf(<i>Y</i>...))</code>
   * 
   * @param propURI
   * @param values
   * @return
   */
  protected OWLConceptExpression dataSomeValuesFromDataOneOf(URI propURI, Object... values) {
    return ontology.dataSomeOneOfRestriction(propURI, values);
  }

  /**
   * <!-- dataSomeValuesFromDatatypeRestrictionMaxExclusive -->
   * 
   * Provide an implementation of the ClassExpression
   * <code>DataSomeValuesFrom(<i>X</i> DatatypeRestriction(<i>type</i> xsd:maxExclusive
   * <i>Y</i>))</code>
   * 
   * <code><i>type</i></code> is inferred from the type of <code>value</code>
   * 
   * @param propURI URI of property being restricted
   * @param value
   * @return ClassExpression
   */
  protected OWLConceptExpression dataSomeValuesFromDatatypeRestrictionMaxExclusive(URI propURI, Number value) {
    return ontology.dataSomeLTRestriction(propURI, value);
  }

  protected OWLConceptExpression dataSomeValuesFromDatatypeRestrictionMaxInclusive(URI propURI, Number value) {
    return ontology.dataSomeLERestriction(propURI, value);
  }

  protected OWLConceptExpression dataSomeValuesFromDatatypeRestrictionMinExclusive(URI propURI, Number value) {
    return ontology.dataSomeGTRestriction(propURI, value);
  }

  protected OWLConceptExpression dataSomeValuesFromDatatypeRestrictionMinInclusive(URI propURI, Number value) {
    return ontology.dataSomeGERestriction(propURI, value);
  }

  protected OWLConceptExpression dataSomeValuesFromDatatypeRestrictionLength(URI propURI, int value) {
    return ontology.dataSomeLengthRestriction(propURI, value);
  }

  protected OWLConceptExpression dataSomeValuesFromDatatypeRestrictionMaxLength(URI propURI, int value) {
    return ontology.dataSomeMaxLengthRestriction(propURI, value);
  }

  protected OWLConceptExpression dataSomeValuesFromDatatypeRestrictionMinLength(URI propURI, int value) {
    return ontology.dataSomeMinLengthRestriction(propURI, value);
  }

  protected OWLConceptExpression dataSomeValuesFromDatatypeRestrictionPattern(URI propURI, String pattern) {
    return ontology.dataSomePatternRestriction(propURI, pattern);
  }

  protected OWLConceptExpression dataSomeValuesFromDatatypeRestriction(URI propURI, XSDVocabulary type) {
    return ontology.dataSomeTypeRestriction(propURI, type);
  }

  /**
   * <!-- dataHasValue -->
   * 
   * <code>DataHasValue(<i>X</i> <i>Y</i>^^<i>type</i>)</code>
   * 
   * Type of <code><i>Y</i></code> inferred from <code>value</code>
   * 
   * @param propURI
   * @param value
   * @return
   */
  protected OWLConceptExpression dataHasValue(URI propURI, Object value) {
    return ontology.dataValueRestriction(propURI, value);
  }

  protected OWLConceptExpression dataHasValue(URI propURI, XSDVocabulary type, String value) {
    return ontology.dataValueRestriction(propURI, type, value);
  }

  /**
   * <!-- objectAllValuesFrom -->
   * 
   * <code>ObjectAllValuesFrom(<i>X</i> <i>expr</i>)</code>
   * 
   * @param propURI
   * @param expr
   * @return
   */
  protected OWLConceptExpression objectAllValuesFrom(URI propURI, OWLConceptExpression expr) {
    return ontology.objectAllRestriction(propURI, expr);
  }

  protected OWLConceptExpression objectAllValuesFrom(URI propURI, URI classURI) {
    return ontology.objectAllRestriction(propURI, classURI);
  }

  protected OWLConceptExpression objectComplementOf(OWLConceptExpression expr) {
    return ontology.objectComplementOf(expr);
  }

  protected OWLConceptExpression objectComplementOf(URI classURI) {
    return ontology.objectComplementOf(classURI);
  }

  protected OWLConceptExpression objectExactCardinality(URI propURI, int value) {
    return ontology.objectExactCardinalityRestriction(propURI, value);
  }

  protected OWLConceptExpression objectIntersectionOf(OWLConceptExpression... exprs) {
    return ontology.objectIntersectionOf(exprs);
  }

  protected OWLConceptExpression objectIntersectionOf(URI... classURIs) {
    return ontology.objectIntersectionOf(classURIs);
  }

  protected OWLConceptExpression objectMaxCardinality(URI propURI, int value) {
    return ontology.objectMaxCardinalityRestriction(propURI, value);
  }

  protected OWLConceptExpression objectMinCardinality(URI propURI, int value) {
    return ontology.objectMinCardinalityRestriction(propURI, value);
  }

  protected OWLConceptExpression objectOneOf(URI... indURIs) {
    return ontology.objectOneOf(indURIs);
  }

  protected OWLConceptExpression objectHasSelf(URI propURI) {
    return ontology.objectSelfRestriction(propURI);
  }

  protected OWLConceptExpression objectSomeValuesFrom(URI propURI, OWLConceptExpression expr) {
    return ontology.objectSomeRestriction(propURI, expr);
  }

  protected OWLConceptExpression objectSomeValuesFrom(URI propURI, URI classURI) {
    return ontology.objectSomeRestriction(propURI, classURI);
  }

  protected OWLConceptExpression objectUnionOf(OWLConceptExpression... exprs) {
    return ontology.objectUnionOf(exprs);
  }

  protected OWLConceptExpression objectUnionOf(URI... classURIs) {
    return ontology.objectUnionOf(classURIs);
  }

  protected OWLConceptExpression objectHasValue(URI propURI, URI indURI) {
    return ontology.objectValueRestriction(propURI, indURI);
  }

  /**
   * <!-- declareObjectProperty -->
   * 
   * Provide an implementation of the
   * <code>Declaration(ObjectProperty(<i>X</i>))</code> axiom.
   * 
   * @param propURIs URIs of entities to be declared object properties.
   */
  protected void declarationObjectProperty(URI... propURIs) {
    ontology.declareObjectProperty(propURIs);
  }

  /**
   * <!-- annotationAssertionCommentObjectProperty -->
   * 
   * <code>AnnotationAssertion(rdfs:comment <i>object property</i> <i>comment</i>)</code>
   * 
   * @param propURI
   * @param comment
   */
  protected void annotationAssertionCommentObjectProperty(URI propURI, String comment) {
    ontology.commentObjectProperty(propURI, comment);
  }

  /**
   * <!-- annotationAssertionLabelObjectProperty -->
   * 
   * <code>AnnotationAssertion(rdfs:label <i>object property</i> <i>comment</i>)</code>
   * 
   * @param propURI
   * @param label
   */
  protected void annotationAssertionLabelObjectProperty(URI propURI, String label) {
    ontology.labelObjectProperty(propURI, label);
  }

  protected void objectPropertyDomain(URI propURI, URI... classURIs) {
    ontology.objectPropertyDomain(propURI, classURIs);
  }

  protected void objectPropertyDomain(URI propURI, OWLConceptExpression expr) {
    ontology.objectPropertyDomain(propURI, expr);
  }

  /**
   * <!-- objectPropertyDomainObjectIntersectionOf -->
   * 
   * <code>ObjectPropertyDomain(<i>X</i> ObjectIntersectionOf(<i>Y1</i> <i>Y2</i> ...))</code>
   * 
   * @param propURI
   * @param classURIs
   */
  protected void objectPropertyDomainObjectIntersectionOf(URI propURI, URI... classURIs) {
    ontology.objectPropertyDomainIntersection(propURI, classURIs);
  }

  protected void objectPropertyRange(URI propURI, URI... classURIs) {
    ontology.objectPropertyRange(propURI, classURIs);
  }

  protected void objectPropertyRange(URI propURI, OWLConceptExpression expr) {
    ontology.objectPropertyRange(propURI, expr);
  }

  /**
   * <!-- objectPropertyRangeObjectIntersectionOf -->
   * 
   * <code>ObjectPropertyRange(<i>X</i> ObjectIntersectionOf(<i>Y1</i> <i>Y2</i> ...))</code>
   * 
   * @param propURI
   * @param classURIs
   */
  protected void objectPropertyRangeObjectIntersectionOf(URI propURI, URI... classURIs) {
    ontology.objectPropertyRangeIntersection(propURI, classURIs);
  }

  /**
   * <!-- objectPropertyRangeObjectOneOf -->
   * 
   * <code>ObjectPropertyRange(<i>X</i> ObjectOneOf(<i>Y1</i> <i>Y2</i> ...))</code>
   * 
   * @param propURI
   * @param indURIs
   */
  protected void objectPropertyRangeObjectOneOf(URI propURI, URI... indURIs) {
    ontology.objectPropertyRangeOneOf(propURI, indURIs);
  }

  protected void inverseObjectProperties(URI prop1URI, URI prop2URI) {
    ontology.objectPropertyInverse(prop1URI, prop2URI);
  }

  protected void symmetricObjectProperty(URI... propURIs) {
    ontology.objectPropertySymmetric(propURIs);
  }

  protected void asymmetricObjectProperty(URI... propURIs) {
    ontology.objectPropertyAntiSymmetric(propURIs);
  }

  protected void functionalObjectProperty(URI... propURIs) {
    ontology.objectPropertyFunctional(propURIs);
  }

  protected void inverseFunctionalObjectProperty(URI... propURIs) {
    ontology.objectPropertyInverseFunctional(propURIs);
  }

  protected void reflexiveObjectProperty(URI... propURIs) {
    ontology.objectPropertyReflexive(propURIs);
  }

  protected void irreflexiveObjectProperty(URI... propURIs) {
    ontology.objectPropertyIrreflexive(propURIs);
  }

  protected void transitiveObjectProperty(URI... propURIs) {
    ontology.objectPropertyTransitive(propURIs);
  }

  protected void subObjectPropertyOf(URI propURI, URI... superPropURIs) {
    ontology.subObjectPropertyOf(propURI, superPropURIs);
  }

  /**
   * <!-- subObjectPropertyOfObjectPropertyChain -->
   * 
   * <code>SubObjectPropertyOf(ObjectPropertyChain(<i>Y1</i> <i>Y2</i> ...) <i>X</i>)</code>
   * 
   * @param superPropURI
   * @param propChainURIs
   */
  protected void subObjectPropertyOfObjectPropertyChain(URI superPropURI, URI... propChainURIs) {
    ontology.superPropertyOfChain(superPropURI, propChainURIs);
  }

  protected void disjointObjectProperties(URI... propURIs) {
    ontology.disjointObjectProperties(propURIs);
  }

  protected void equivalentObjectProperties(URI... propURIs) {
    ontology.equivalentObjectProperties(propURIs);
  }

  /**
   * <!-- declarationDataProperty -->
   * 
   * Provide an implementation of the
   * <code>Declaration(DataProperty(<i>X</i>))</code> axiom.
   * 
   * @param propURIs URIs of entities to be declared as data properties.
   */
  protected void declarationDataProperty(URI... propURIs) {
    ontology.declareDataProperty(propURIs);
  }

  protected void annotationAssertionCommentDataProperty(URI propURI, String comment) {
    ontology.commentDataProperty(propURI, comment);
  }

  protected void annotationAssertionLabelDataProperty(URI propURI, String label) {
    ontology.labelDataProperty(propURI, label);
  }

  protected void dataPropertyDomain(URI propURI, URI... classURIs) {
    ontology.dataPropertyDomain(propURI, classURIs);
  }

  protected void dataPropertyDomain(URI propURI, OWLConceptExpression expr) {
    ontology.dataPropertyDomain(propURI, expr);
  }

  protected void dataPropertyDomainObjectIntersectionOf(URI propURI, URI... classURIs) {
    ontology.dataPropertyDomainIntersection(propURI, classURIs);
  }

  protected void dataPropertyRange(URI propURI, XSDVocabulary type) {
    ontology.dataPropertyRange(propURI, type);
  }

  protected void dataPropertyRangeDataOneOf(URI propURI, Object... values) {
    ontology.dataPropertyRangeOneOf(propURI, values);
  }

  /**
   * <!-- dataPropertyRangeDatatypeRestrictionMaxExclusive -->
   * 
   * <code>DataPropertyRange(<i>X</i> DatatypeRestriction(<i>type</i> xsd:maxExclusive <i>Y</i>))</code>
   * 
   * @param propURI
   * @param type
   * @param max
   */
  protected void dataPropertyRangeDatatypeRestrictionMaxExclusive(URI propURI, XSDVocabulary type, Number max) {
    ontology.dataPropertyRangeMaxExclusive(propURI, type, max);
  }

  protected void dataPropertyRangeDatatypeRestrictionMinExclusive(URI propURI, XSDVocabulary type, Number min) {
    ontology.dataPropertyRangeMinExclusive(propURI, type, min);
  }

  protected void dataPropertyRangeDatatypeRestrictionMaxInclusive(URI propURI, XSDVocabulary type, Number max) {
    ontology.dataPropertyRangeMaxInclusive(propURI, type, max);
  }

  protected void dataPropertyRangeDatatypeRestrictionMinInclusive(URI propURI, XSDVocabulary type, Number min) {
    ontology.dataPropertyRangeMinInclusive(propURI, type, min);
  }

  protected void dataPropertyRangeDatatypeRestrictionLength(URI propURI, int length) {
    ontology.dataPropertyRangeLength(propURI, length);
  }

  protected void dataPropertyRangeDatatypeRestrictionMinLength(URI propURI, int length) {
    ontology.dataPropertyRangeMinLength(propURI, length);
  }

  protected void dataPropertyRangeDatatypeRestrictionMaxLength(URI propURI, int length) {
    ontology.dataPropertyRangeMaxLength(propURI, length);
  }

  protected void dataPropertyRangeDatatypeRestrictionPattern(URI propURI, String pattern) {
    ontology.dataPropertyRangePattern(propURI, pattern);
  }

  protected void functionalDataProperty(URI... propURIs) {
    ontology.dataPropertyFunctional(propURIs);
  }

  protected void disjointDataProperties(URI... propURIs) {
    ontology.disjointDataProperties(propURIs);
  }

  protected void equivalentDataProperties(URI... propURIs) {
    ontology.equivalentDataProperties(propURIs);
  }

  protected void subDataPropertyOf(URI propURI, URI... superPropURIs) {
    ontology.subDataPropertyOf(propURI, superPropURIs);
  }

  /**
   * <!-- declarationNamedIndividual -->
   * 
   * Provide an implementation of the
   * <code>Declaration(NamedIndividual(<i>X</i>))</code> axiom.
   * 
   * @param indURIs URIs of entities to be declared as named individuals.
   */
  protected void declarationNamedIndividual(URI... indURIs) {
    ontology.declareIndividual(indURIs);
  }

  /**
   * <!-- annotationAssertionCommentIndividual -->
   * 
   * <code>AnnotationAssertion(rdfs:comment <i>individual</i> <i>comment</i>)</code>
   * 
   * @param indURI
   * @param comment
   */
  protected void annotationAssertionCommentIndividual(URI indURI, String comment) {
    ontology.commentIndividual(indURI, comment);
  }

  protected void annotationAssertionLabelIndividual(URI indURI, String label) {
    ontology.labelIndividual(indURI, label);
  }

  protected void classAssertion(URI indURI, URI... classURIs) {
    ontology.individualHasClass(indURI, classURIs);
  }

  protected void classAssertion(URI indURI, OWLConceptExpression... exprs) {
    ontology.individualHasClass(indURI, exprs);
  }

  protected void differentIndividuals(URI... indURIs) {
    ontology.differentIndividuals(indURIs);
  }

  protected void sameIndividuals(URI... indURIs) {
    ontology.sameIndividuals(indURIs);
  }

  protected void dataPropertyAssertion(URI indURI, URI propURI, XSDVocabulary type, String... values) {
    ontology.individualHasDataPropertyValue(indURI, propURI, type, values);
  }

  protected void dataPropertyAssertion(URI indURI, URI propURI, boolean... values) {
    ontology.individualHasDataPropertyValue(indURI, propURI, values);
  }

  protected void dataPropertyAssertion(URI indURI, URI propURI, char... values) {
    ontology.individualHasDataPropertyValue(indURI, propURI, values);
  }

  protected void dataPropertyAssertion(URI indURI, URI propURI, short... values) {
    ontology.individualHasDataPropertyValue(indURI, propURI, values);
  }

  protected void dataPropertyAssertion(URI indURI, URI propURI, int... values) {
    ontology.individualHasDataPropertyValue(indURI, propURI, values);
  }
  
  protected void dataPropertyAssertion(URI indURI, URI propURI, int value) {
    dataPropertyAssertion(indURI, propURI, new int[] { value });
  }

  protected void dataPropertyAssertion(URI indURI, URI propURI, long... values) {
    ontology.individualHasDataPropertyValue(indURI, propURI, values);
  }

  protected void dataPropertyAssertion(URI indURI, URI propURI, float... values) {
    ontology.individualHasDataPropertyValue(indURI, propURI, values);
  }

  protected void dataPropertyAssertion(URI indURI, URI propURI, double... values) {
    ontology.individualHasDataPropertyValue(indURI, propURI, values);
  }

  protected void dataPropertyAssertion(URI indURI, URI propURI, String... values) {
    ontology.individualHasDataPropertyValue(indURI, propURI, values);
  }

  protected void dataPropertyAssertion(URI indURI, URI propURI, URI... values) {
    ontology.individualHasDataPropertyValue(indURI, propURI, values);
  }

  protected void negativeDataPropertyAssertion(URI indURI, URI propURI, XSDVocabulary type, String... values) {
    ontology.individualHasNotDataPropertyValue(indURI, propURI, type, values);
  }

  protected void negativeDataPropertyAssertion(URI indURI, URI propURI, boolean... values) {
    ontology.individualHasNotDataPropertyValue(indURI, propURI, values);
  }

  protected void negativeDataPropertyAssertion(URI indURI, URI propURI, char... values) {
    ontology.individualHasNotDataPropertyValue(indURI, propURI, values);
  }

  protected void negativeDataPropertyAssertion(URI indURI, URI propURI, short... values) {
    ontology.individualHasNotDataPropertyValue(indURI, propURI, values);
  }

  protected void negativeDataPropertyAssertion(URI indURI, URI propURI, int... values) {
    ontology.individualHasNotDataPropertyValue(indURI, propURI, values);
  }

  protected void negativeDataPropertyAssertion(URI indURI, URI propURI, long... values) {
    ontology.individualHasNotDataPropertyValue(indURI, propURI, values);
  }

  protected void negativeDataPropertyAssertion(URI indURI, URI propURI, float... values) {
    ontology.individualHasNotDataPropertyValue(indURI, propURI, values);
  }

  protected void negativeDataPropertyAssertion(URI indURI, URI propURI, double... values) {
    ontology.individualHasNotDataPropertyValue(indURI, propURI, values);
  }

  protected void negativeDataPropertyAssertion(URI indURI, URI propURI, String... values) {
    ontology.individualHasNotDataPropertyValue(indURI, propURI, values);
  }

  protected void negativeDataPropertyAssertion(URI indURI, URI propURI, URI... values) {
    ontology.individualHasNotDataPropertyValue(indURI, propURI, values);
  }

  protected void objectPropertyAssertion(URI indURI, URI propURI, URI... objURIs) {
    ontology.individualHasObjectPropertyValue(indURI, propURI, objURIs);
  }

  protected void negativeObjectPropertyAssertion(URI indURI, URI propURI, URI... objURIs) {
    ontology.individualHasNotObjectPropertyValue(indURI, propURI, objURIs);
  }

  private class ModelOntology extends AbstractOntology {

    /**
     * Constructor used to pass in the ontology logical URI
     * 
     * @param logical logical URI
     * @throws OWLOntologyCreationException
     * @throws OWLOntologyChangeException
     */
    public ModelOntology(URI logical) throws OWLOntologyCreationException, OWLOntologyChangeException {
      super(logical);
    }

    /**
     * Provide for the creation of an ontology using a particular sublanguage of
     * OWL. Not used at present.
     * 
     * @param logical logical URI
     * @param spp OWL sublanguage
     * @throws OWLOntologyCreationException
     * @throws OWLOntologyChangeException
     */
    public ModelOntology(URI logical, OWLSpecies spp) throws OWLOntologyCreationException, OWLOntologyChangeException {
      super(logical, spp);
    }

    /**
     * Provide a constructor allowing this ontology to be created in the same
     * environment as another. For now, this is not used.
     * 
     * @param logical The logical URI of the ontology to create
     * @param related The ontology the environment of which this ontology is to
     *          be created in.
     * @throws OWLOntologyCreationException
     * @throws OWLOntologyChangeException
     */
    public ModelOntology(URI logical, AbstractOntology related) throws OWLOntologyCreationException,
        OWLOntologyChangeException {
      super(logical, related);
    }

    /**
     * <!-- buildOntology -->
     * 
     * Implement the build by calling out to
     * {@link AbstractModelOntology#buildOntology(ModelOntology)}.
     * 
     * @see uk.ac.hutton.obiama.model.AbstractOntology#buildOntology()
     */
    protected void buildOntology() {
      AbstractModelOntology.this.buildOntology(this);
    }
  }

}
