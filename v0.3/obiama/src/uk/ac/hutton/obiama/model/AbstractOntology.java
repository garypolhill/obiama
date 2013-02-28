/*
 * uk.ac.hutton.obiama.model: AbstractOntology.java
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLInconsistentOntologyException;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.io.RDFXMLOntologyFormat;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLCommentAnnotation;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataOneOf;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataRangeRestriction;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLIndividualAxiom;
import org.semanticweb.owl.model.OWLNamedObject;
import org.semanticweb.owl.model.OWLObjectIntersectionOf;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLObjectUnionOf;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLOntologyStorageException;
import org.semanticweb.owl.model.OWLTypedConstant;
import org.semanticweb.owl.model.UnknownOWLOntologyException;
import org.semanticweb.owl.vocab.OWLRestrictedDataRangeFacetVocabulary;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.Panic;
import uk.ac.hutton.obiama.exception.UsageException;
import uk.ac.hutton.obiama.msb.ObiamaSetUp;
import uk.ac.hutton.obiama.msb.OntologyIOHelper;
import uk.ac.hutton.obiama.msb.XSDHelper;
import uk.ac.hutton.obiama.msb.InferredOntologyCreator;

/**
 * <!-- AbstractOntology -->
 * 
 * <p>
 * This is an abstract class for other classes managing ontologies to subclass
 * from. Certain functionality in the subclasses is enabled if they are
 * appropriately implemented. Specifically, they should have a constructor
 * corresponding to each of the non-private constructors, but without the
 * logical URI argument (this presumably is defined by the subclass). For
 * example, here there is the constructor
 * <code>AbstractOntology(URI logical)</code>. A subclass should have the
 * constructor <code>MyOntology()</code>, with the single line
 * <code>super(ONTOLOGY_URI)</code>, where <code>ONTOLOGY_URI</code> is the
 * logical URI of the ontology defined by the subclass. Indeed, subclasses
 * should define the static field <code>ONTOLOGY_URI</code> in this way. By
 * conforming to this convention, subclasses can be used with the
 * <code>createOntology()</code> family of methods, and the
 * <code>imports(? extends AbstractOntology ...)</code> method.
 * </p>
 * 
 * <p>
 * Specifically, the <code>imports()</code> method relies on the
 * <code>ONTOLOGY_URI</code> static field being defined by the class in its
 * argument, and on that class implementing the constructor taking an
 * <code>AbstractOntology</code> as argument.
 * </p>
 * 
 * <p>
 * The <code>createOntology()</code> methods rely on the
 * <code>MyOntology()</code>, <code>MyOntology(URI)</code>,
 * <code>MyOntology(OWLSpecies)</code>, and
 * <code>MyOntology(URI, OWLSpecies)</code> methods being defined. The
 * <code>createOntology()</code> methods are designed to assist subclasses in
 * providing a <code>main()</code> method allowing the ontologies they define to
 * be saved to a file. The <code>main()</code> method, in its simplest form,
 * would look like this (replacing <code><i>MyOntology</i></code> with the
 * subclass):
 * </p>
 * 
 * <p>
 * <code>&nbsp;&nbsp;public static void main(String args[]) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;try {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;createOntology(<i>MyOntology</i>.class, args);<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;catch(UsageException e) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ErrorHandler.fatal(e, "parsing command line arguments");<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;}</code>
 * </p>
 * 
 * <p>
 * An ontology can then be created with
 * </p>
 * <p>
 * <code>java <i>MyOntology</i> [--physical &lt;</code><i>physical URI</i>
 * <code>&gt;] [--species &lt;</code><i>sublanguage</i>
 * <code>&gt;] [--inferred]</code>
 * </p>
 * <p>
 * where <i>physical URI</i> is the URI of the location to save the ontology to,
 * <i>sublanguage</i> is the sublanguage of OWL to use (see {@link OWLSpecies}
 * enumeration), and if the <code>--inferred</code> flag is given, then the
 * saved ontology will include axioms inferred using a default reasoner (a known
 * one with a valid environment--see {@link ObiamaSetUp}).
 * </p>
 * 
 * @author Gary Polhill
 */
public abstract class AbstractOntology {
  public static final String DEFAULT_OWL_SUFFIX = ".owl";
  public static final URI RDF_URI = URI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns");
  public static final URI RDFS_URI = URI.create("http://www.w3.org/2000/01/rdf-schema");
  public static final URI XSD_URI = URI.create("http://www.w3.org/2001/XMLSchema");
  public static final URI OWL_URI = URI.create("http://www.w3.org/2002/07/owl");
  
  public static final OWLSpecies DEFAULT_OWL_SPECIES = OWLSpecies.OWL_2_DL;

  public static final URI OWL_CLASS_URI(OWLSpecies spp) {
    return URI.create(OWL_URI + "#Class");
  }

  public static final URI OWL_DATA_PROPERTY_URI(OWLSpecies spp) {
    if(spp.isOWL2()) return URI.create(OWL_URI + "#DataProperty");
    else
      return URI.create(OWL_URI + "#DatatypeProperty");
  }

  public static final URI OWL_OBJECT_PROPERTY_URI(OWLSpecies spp) {
    return URI.create(OWL_URI + "#ObjectProperty");
  }

  public static final URI OWL_THING_URI(OWLSpecies spp) {
    return URI.create(OWL_URI + "#Thing");
  }

  public static final CommandLineArgument[] argList = new CommandLineArgument[] {
    new CommandLineArgument("--species", "-s", "sublanguage", "OWL sublanguage to use"),
    new CommandLineArgument("--physical", "-p", "URI", "Physical location to save ontology to"),
    new CommandLineArgument("--inferred", "-i", "Build an inferred ontology") };

  private static Set<CommandLineArgument> argSet = new HashSet<CommandLineArgument>();
  static {
    for(int i = 0; i < argList.length; i++) {
      argSet.add(argList[i]);
    }
  };

  protected static Map<String, String> argMap = new HashMap<String, String>();

  final class OWLConceptExpression {
    OWLDescription desc;

    OWLConceptExpression(OWLDescription desc) {
      this.desc = desc;
    }
  }

  OWLSpecies spp;
  private OWLOntologyManager manager;
  private OWLDataFactory factory;
  private OWLOntology ontology;
  private Set<OWLAxiom> axioms;
  private boolean mutable;
  private OWLOntology nonInferredOntology;

  /**
   * Default constructor, using OWL DL. Subclasses are expected to have a
   * corresponding constructor with no arguments calling this one with their
   * ONTOLOGY_URI parameter.
   * 
   * @param logical Logical URI for the ontology
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  AbstractOntology(URI logical) throws OWLOntologyCreationException, OWLOntologyChangeException {
    this(logical, DEFAULT_OWL_SPECIES);
  }

  /**
   * Constructor allowing the ontology to be saved to a location. Subclasses
   * should have a corresponding constructor with a single argument for the
   * physical URI, calling this one with ONTOLOGY_URI as the first argument.
   * 
   * @param logical Logical URI for the ontology
   * @param physical Physical URI (where it is to be saved to)
   * @throws OWLOntologyCreationException
   * @throws UnknownOWLOntologyException
   * @throws OWLOntologyChangeException
   * @throws OWLOntologyStorageException
   */
  AbstractOntology(URI logical, URI physical) throws OWLOntologyCreationException, UnknownOWLOntologyException,
      OWLOntologyChangeException, OWLOntologyStorageException {
    this(logical, physical, DEFAULT_OWL_SPECIES);
  }

  /**
   * Constructor allowing a sublanguage for the ontology to be specified.
   * Subclasses should have a corresponding constructor with a single argument
   * for the sublanguage, calling this one with ONTOLOGY_URI as the first
   * argument.
   * 
   * @param logical Logical URI for the ontology
   * @param spp Sublanguage for the ontology
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  AbstractOntology(URI logical, OWLSpecies spp) throws OWLOntologyCreationException, OWLOntologyChangeException {
    this(logical, spp, OWLManager.createOWLOntologyManager());
  }

  /**
   * Constructor allowing a sublanguage for the ontology and a physical URI for
   * it to be saved to to be specified. Subclasses should have a corresponding
   * constructor having just the physical URI and sublanguage (in that order) as
   * arguments, calling this one with ONTOLOGY_URI as the first argument.
   * 
   * @param logical Logical URI for the ontology
   * @param physical Physical URI (where the ontology is to be saved to)
   * @param spp sublanguage
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   * @throws OWLOntologyStorageException
   */
  AbstractOntology(URI logical, URI physical, OWLSpecies spp) throws OWLOntologyCreationException,
      OWLOntologyChangeException, OWLOntologyStorageException {
    this(logical, spp);
    saveOntology(physical);
  }

  /**
   * Constructor allowing this ontology to be created in the same environment as
   * another. Subclasses should have a corresponding constructor having just the
   * ontology as argument, calling this one with ONTOLOGY_URI as the first
   * argument.
   * 
   * @param logical Logical URI
   * @param related Related ontology
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  AbstractOntology(URI logical, AbstractOntology related) throws OWLOntologyCreationException,
      OWLOntologyChangeException {
    this(logical, related.spp, related.manager);
  }

  /**
   * Constructor called by all the others, which should not be overridden.
   * 
   * @param logical Logical URI
   * @param spp Sublanguage
   * @param manager Manager to create the ontology in
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  private AbstractOntology(URI logical, OWLSpecies spp, OWLOntologyManager manager)
      throws OWLOntologyCreationException, OWLOntologyChangeException {
    this.spp = spp;
    this.manager = manager;
    factory = manager.getOWLDataFactory();
    ontology = manager.createOntology(logical);
    axioms = new HashSet<OWLAxiom>();
    mutable = true;
    buildOntology();
    updateOntology();
    mutable = false;
    nonInferredOntology = null;
  }

  /**
   * Constructor adopting an ontology
   * 
   * @param anOntology
   * @param scheduleOntology
   */
  AbstractOntology(OWLOntology anOntology, OWLSpecies spp, OWLOntologyManager manager) {
    this.spp = spp;
    this.manager = manager;
    factory = manager.getOWLDataFactory();
    ontology = anOntology;
    axioms = anOntology.getAxioms();
    for(OWLAxiom axiom: axioms) {
      if(axiom.isLogicalAxiom() && !spp.hasLogicalAxiom(axiom)) {
        // TODO Throw exception? Remove the axiom?
      }
    }
    mutable = false;
    nonInferredOntology = null;
  }

  /**
   * Create a new ontology in the same environment as
   * <code>toThisOntology</code>, which imports <code>toThisOntology</code>, and
   * then populates the new ontology with individuals in
   * <code>importIndividualsFromOntology</code>. The individual axioms imported
   * from the latter ontology, if they reference entities that are not
   * individuals, must reference such entities in the ontologies managed by the
   * <code>OWLOntologyManager</code> of <code>toThisOntology</code>. This method
   * does not call <code>buildOntology()</code> in the subclass.
   * 
   * @param importIndividualsFromOntology
   * @param toThisOntology
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  public AbstractOntology(URI logical, OWLOntology importIndividualsFromOntology, AbstractOntology toThisOntology)
      throws OWLOntologyCreationException, OWLOntologyChangeException {
    this.spp = toThisOntology.getSpecies();
    this.manager = toThisOntology.manager;
    factory = manager.getOWLDataFactory();
    ontology = manager.createOntology(logical);
    axioms = new HashSet<OWLAxiom>();
    mutable = true;
    imports(toThisOntology.getURI());
    importIndividuals(importIndividualsFromOntology, toThisOntology);
    updateOntology();
    mutable = false;
    nonInferredOntology = null;
  }

  public OWLSpecies getSpecies() {
    return spp;
  }

  /*
   * #########################################################################
   * 
   * Axioms pertaining to the ontology
   */

  Set<AbstractOntology> imports(Class<? extends AbstractOntology>... clses) {
    if(!mutable) throw new Bug();
    Set<AbstractOntology> ontologies = new HashSet<AbstractOntology>();
    for(Class<? extends AbstractOntology> cls: clses) {
      try {
        Constructor<? extends AbstractOntology> cons = cls.getConstructor(AbstractOntology.class);
        AbstractOntology imported = cons.newInstance(this);
        ontologies.add(imported);
        imports(imported.getURI());
      }
      catch(SecurityException e) {
        throw new Bug();
      }
      catch(NoSuchMethodException e) {
        throw new Bug();
      }
      catch(IllegalArgumentException e) {
        throw new Bug();
      }
      catch(InstantiationException e) {
        throw new Bug();
      }
      catch(IllegalAccessException e) {
        throw new Bug();
      }
      catch(InvocationTargetException e) {
        throw new Bug();
      }
    }
    return ontologies;
  }

  void imports(URI... ontologyURIs) {
    if(!mutable) throw new Bug();
    for(URI ontologyURI: ontologyURIs) {
      axioms.add(factory.getOWLImportsDeclarationAxiom(ontology, ontologyURI));
    }
  }

  void ontologyComment(String comment) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLOntologyAnnotationAxiom(ontology, factory.getCommentAnnotation(comment)));
  }

  /*
   * #########################################################################
   * 
   * Axioms pertaining to classes
   */

  void declareClass(URI... classURIs) {
    if(!mutable) throw new Bug();
    for(URI classURI: classURIs) {
      axioms.add(factory.getOWLDeclarationAxiom(factory.getOWLClass(classURI)));
    }
  }

  void classComment(URI classURI, String comment) {
    if(!mutable) throw new Bug();
    axioms
        .add(factory.getOWLEntityAnnotationAxiom(factory.getOWLClass(classURI), factory.getCommentAnnotation(comment)));
  }

  void classLabel(URI classURI, String label) {
    if(!mutable) throw new Bug();
    axioms
        .add(factory.getOWLEntityAnnotationAxiom(factory.getOWLClass(classURI), factory.getOWLLabelAnnotation(label)));
  }

  void classAnnotation(URI classURI, URI annotationURI, URI individualURI) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLEntityAnnotationAxiom(factory.getOWLClass(classURI), annotationURI,
        factory.getOWLIndividual(individualURI)));
  }

  void classAnnotation(URI classURI, URI annotationURI, double value) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLEntityAnnotationAxiom(factory.getOWLClass(classURI), annotationURI,
        factory.getOWLTypedConstant(value)));
  }

  void classAnnotation(URI classURI, URI annotationURI, float value) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLEntityAnnotationAxiom(factory.getOWLClass(classURI), annotationURI,
        factory.getOWLTypedConstant(value)));
  }

  void classAnnotation(URI classURI, URI annotationURI, int value) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLEntityAnnotationAxiom(factory.getOWLClass(classURI), annotationURI,
        factory.getOWLTypedConstant(value)));
  }

  void classAnnotation(URI classURI, URI annotationURI, long value) {
    classAnnotation(classURI, annotationURI, XSDVocabulary.LONG, Long.toString(value));
  }

  void classAnnotation(URI classURI, URI annotationURI, boolean value) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLEntityAnnotationAxiom(factory.getOWLClass(classURI), annotationURI,
        factory.getOWLTypedConstant(value)));
  }

  void classAnnotation(URI classURI, URI annotationURI, String value) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLEntityAnnotationAxiom(factory.getOWLClass(classURI), annotationURI,
        factory.getOWLTypedConstant(value)));
  }

  void classAnnotation(URI classURI, URI annotationURI, XSDVocabulary type, String value) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLEntityAnnotationAxiom(factory.getOWLClass(classURI), annotationURI,
        factory.getOWLTypedConstant(value, factory.getOWLDataType(type.getURI()))));
  }

  void disjointClasses(URI... classURIs) {
    if(!mutable) throw new Bug();
    Set<OWLClass> disjoints = buildClassSet(classURIs);
    if(spp.isOWL2()) {
      axioms.add(factory.getOWLDisjointClassesAxiom(disjoints));
    }
    else {
      for(OWLClass disjoint1: disjoints) {
        for(OWLClass disjoint2: disjoints) {
          if(disjoint1 != disjoint2) {
            axioms.add(factory.getOWLDisjointClassesAxiom(disjoint1, disjoint2));
          }
        }
      }
    }
  }

  void disjointClasses(OWLConceptExpression... exprs) {
    if(!mutable) throw new Bug();
    Set<OWLDescription> disjoints = new HashSet<OWLDescription>();
    for(OWLConceptExpression expr: exprs) {
      disjoints.add(expr.desc);
    }
    if(spp.isOWL2()) {
      axioms.add(factory.getOWLDisjointClassesAxiom(disjoints));
    }
    else {
      for(OWLDescription disjoint1: disjoints) {
        for(OWLDescription disjoint2: disjoints) {
          if(disjoint1 != disjoint2) {
            axioms.add(factory.getOWLDisjointClassesAxiom(disjoint1, disjoint2));
          }
        }
      }
    }
  }

  void disjointClasses(URI classURI, OWLConceptExpression expr) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLDisjointClassesAxiom(factory.getOWLClass(classURI), expr.desc));
  }

  void equivalentClasses(URI... classURIs) {
    if(!mutable) throw new Bug();
    if(classURIs.length == 2) {
      axioms.add(factory.getOWLEquivalentClassesAxiom(factory.getOWLClass(classURIs[0]),
          factory.getOWLClass(classURIs[1])));
    }
    else {
      axioms.add(factory.getOWLEquivalentClassesAxiom(buildClassSet(classURIs)));
    }
  }

  void equivalentClasses(URI classURI, OWLConceptExpression expr) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLEquivalentClassesAxiom(factory.getOWLClass(classURI), expr.desc));
  }

  void equivalentClasses(OWLConceptExpression... exprs) {
    if(!mutable) throw new Bug();
    Set<OWLDescription> descs = new HashSet<OWLDescription>();
    for(OWLConceptExpression expr: exprs) {
      descs.add(expr.desc);
    }
    axioms.add(factory.getOWLEquivalentClassesAxiom(descs));
  }

  void subClassOf(URI class1URI, URI... class2URIs) {
    if(!mutable) throw new Bug();
    OWLClass class1 = factory.getOWLClass(class1URI);
    for(URI class2URI: class2URIs) {
      axioms.add(factory.getOWLSubClassAxiom(class1, factory.getOWLClass(class2URI)));
    }
  }

  void subClassOf(URI classURI, OWLConceptExpression expr) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLSubClassAxiom(factory.getOWLClass(classURI), expr.desc));
  }

  void subClassOf(OWLConceptExpression subExpr, OWLConceptExpression superExpr) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLSubClassAxiom(subExpr.desc, superExpr.desc));
  }

  void subClassOfUnion(URI class1URI, URI... class2URIs) {
    if(!mutable) throw new Bug();
    OWLObjectUnionOf union = buildUnion(class2URIs);
    axioms.add(factory.getOWLSubClassAxiom(factory.getOWLClass(class1URI), union));
  }

  void subClassOfIntersection(URI class1URI, URI... class2URIs) {
    if(!mutable) throw new Bug();
    OWLObjectIntersectionOf intersection = buildIntersection(class2URIs);
    axioms.add(factory.getOWLSubClassAxiom(factory.getOWLClass(class1URI), intersection));
  }

  void disjointUnion(URI class1URI, URI... class2URIs) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLDisjointUnionAxiom(factory.getOWLClass(class1URI), buildClassSet(class2URIs)));
  }

  void disjointUnion(URI class1URI, OWLConceptExpression... exprs) {
    if(!mutable) throw new Bug();
    Set<OWLDescription> descs = new HashSet<OWLDescription>();
    for(OWLConceptExpression expr: exprs) {
      descs.add(expr.desc);
    }
    axioms.add(factory.getOWLDisjointUnionAxiom(factory.getOWLClass(class1URI), descs));
  }

  /*
   * #########################################################################
   * 
   * Axioms pertaining to restrictions
   */

  OWLConceptExpression namedClass(URI classURI) {
    return new OWLConceptExpression(factory.getOWLClass(classURI));
  }

  OWLConceptExpression dataAllOneOfRestriction(URI propURI, Object... values) {
    Set<OWLTypedConstant> consts = buildConstantSet(values);
    return new OWLConceptExpression(factory.getOWLDataAllRestriction(factory.getOWLDataProperty(propURI),
        factory.getOWLDataOneOf(consts)));
  }

  OWLConceptExpression dataAllLTRestriction(URI propURI, Number value) {
    OWLDataRangeRestriction range =
      buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.MAX_EXCLUSIVE,
          XSDHelper.getTypeFor(value), value.toString());
    return new OWLConceptExpression(factory.getOWLDataAllRestriction(factory.getOWLDataProperty(propURI), range));
  }

  OWLConceptExpression dataAllLERestriction(URI propURI, Number value) {
    OWLDataRangeRestriction range =
      buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.MAX_INCLUSIVE,
          XSDHelper.getTypeFor(value), value.toString());
    return new OWLConceptExpression(factory.getOWLDataAllRestriction(factory.getOWLDataProperty(propURI), range));
  }

  OWLConceptExpression dataAllGTRestriction(URI propURI, Number value) {
    OWLDataRangeRestriction range =
      buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.MIN_EXCLUSIVE,
          XSDHelper.getTypeFor(value), value.toString());
    return new OWLConceptExpression(factory.getOWLDataAllRestriction(factory.getOWLDataProperty(propURI), range));
  }

  OWLConceptExpression dataAllGERestriction(URI propURI, Number value) {
    OWLDataRangeRestriction range =
      buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.MIN_INCLUSIVE,
          XSDHelper.getTypeFor(value), value.toString());
    return new OWLConceptExpression(factory.getOWLDataAllRestriction(factory.getOWLDataProperty(propURI), range));
  }

  OWLConceptExpression dataAllLengthRestriction(URI propURI, int value) {
    OWLDataRangeRestriction range =
      buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.LENGTH, XSDVocabulary.STRING,
          Integer.toString(value));
    return new OWLConceptExpression(factory.getOWLDataAllRestriction(factory.getOWLDataProperty(propURI), range));
  }

  OWLConceptExpression dataAllMaxLengthRestriction(URI propURI, int value) {
    OWLDataRangeRestriction range =
      buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.MAX_LENGTH, XSDVocabulary.STRING,
          Integer.toString(value));
    return new OWLConceptExpression(factory.getOWLDataAllRestriction(factory.getOWLDataProperty(propURI), range));
  }

  OWLConceptExpression dataAllMinLengthRestriction(URI propURI, int value) {
    OWLDataRangeRestriction range =
      buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.MIN_LENGTH, XSDVocabulary.STRING,
          Integer.toString(value));
    return new OWLConceptExpression(factory.getOWLDataAllRestriction(factory.getOWLDataProperty(propURI), range));
  }

  OWLConceptExpression dataAllPatternRestriction(URI propURI, String pattern) {
    OWLDataRangeRestriction range =
      buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.PATTERN, XSDVocabulary.STRING, pattern);
    return new OWLConceptExpression(factory.getOWLDataAllRestriction(factory.getOWLDataProperty(propURI), range));
  }

  OWLConceptExpression dataAllTypeRestriction(URI propURI, XSDVocabulary type) {
    return new OWLConceptExpression(factory.getOWLDataAllRestriction(factory.getOWLDataProperty(propURI),
        factory.getOWLDataType(type.getURI())));
  }

  OWLConceptExpression dataExactCardinalityRestriction(URI propURI, int card) {
    return new OWLConceptExpression(factory.getOWLDataExactCardinalityRestriction(factory.getOWLDataProperty(propURI),
        card));
  }

  OWLConceptExpression dataMaxCardinalityRestriction(URI propURI, int card) {
    return new OWLConceptExpression(factory.getOWLDataMaxCardinalityRestriction(factory.getOWLDataProperty(propURI),
        card));
  }

  OWLConceptExpression dataMinCardinalityRestriction(URI propURI, int card) {
    return new OWLConceptExpression(factory.getOWLDataMinCardinalityRestriction(factory.getOWLDataProperty(propURI),
        card));
  }

  OWLConceptExpression dataSomeOneOfRestriction(URI propURI, Object... values) {
    Set<OWLTypedConstant> consts = buildConstantSet(values);
    return new OWLConceptExpression(factory.getOWLDataSomeRestriction(factory.getOWLDataProperty(propURI),
        factory.getOWLDataOneOf(consts)));
  }

  OWLConceptExpression dataSomeLTRestriction(URI propURI, Number value) {
    OWLDataRangeRestriction range =
      buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.MAX_EXCLUSIVE,
          XSDHelper.getTypeFor(value), value.toString());
    return new OWLConceptExpression(factory.getOWLDataSomeRestriction(factory.getOWLDataProperty(propURI), range));
  }

  OWLConceptExpression dataSomeLERestriction(URI propURI, Number value) {
    OWLDataRangeRestriction range =
      buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.MAX_INCLUSIVE,
          XSDHelper.getTypeFor(value), value.toString());
    return new OWLConceptExpression(factory.getOWLDataSomeRestriction(factory.getOWLDataProperty(propURI), range));
  }

  OWLConceptExpression dataSomeGTRestriction(URI propURI, Number value) {
    OWLDataRangeRestriction range =
      buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.MIN_EXCLUSIVE,
          XSDHelper.getTypeFor(value), value.toString());
    return new OWLConceptExpression(factory.getOWLDataSomeRestriction(factory.getOWLDataProperty(propURI), range));
  }

  OWLConceptExpression dataSomeGERestriction(URI propURI, Number value) {
    OWLDataRangeRestriction range =
      buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.MIN_INCLUSIVE,
          XSDHelper.getTypeFor(value), value.toString());
    return new OWLConceptExpression(factory.getOWLDataSomeRestriction(factory.getOWLDataProperty(propURI), range));
  }

  OWLConceptExpression dataSomeLengthRestriction(URI propURI, int value) {
    OWLDataRangeRestriction range =
      buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.LENGTH, XSDVocabulary.STRING,
          Integer.toString(value));
    return new OWLConceptExpression(factory.getOWLDataSomeRestriction(factory.getOWLDataProperty(propURI), range));
  }

  OWLConceptExpression dataSomeMaxLengthRestriction(URI propURI, int value) {
    OWLDataRangeRestriction range =
      buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.MAX_LENGTH, XSDVocabulary.STRING,
          Integer.toString(value));
    return new OWLConceptExpression(factory.getOWLDataSomeRestriction(factory.getOWLDataProperty(propURI), range));
  }

  OWLConceptExpression dataSomeMinLengthRestriction(URI propURI, int value) {
    OWLDataRangeRestriction range =
      buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.MIN_LENGTH, XSDVocabulary.STRING,
          Integer.toString(value));
    return new OWLConceptExpression(factory.getOWLDataSomeRestriction(factory.getOWLDataProperty(propURI), range));
  }

  OWLConceptExpression dataSomePatternRestriction(URI propURI, String pattern) {
    OWLDataRangeRestriction range =
      buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.PATTERN, XSDVocabulary.STRING, pattern);
    return new OWLConceptExpression(factory.getOWLDataSomeRestriction(factory.getOWLDataProperty(propURI), range));
  }

  OWLConceptExpression dataSomeTypeRestriction(URI propURI, XSDVocabulary type) {
    return new OWLConceptExpression(factory.getOWLDataSomeRestriction(factory.getOWLDataProperty(propURI),
        factory.getOWLDataType(type.getURI())));
  }

  OWLConceptExpression dataValueRestriction(URI propURI, Object value) {
    return dataValueRestriction(propURI, XSDHelper.getTypeFor(value), value.toString());
  }

  OWLConceptExpression dataValueRestriction(URI propURI, XSDVocabulary type, String value) {
    return new OWLConceptExpression(factory.getOWLDataValueRestriction(factory.getOWLDataProperty(propURI),
        factory.getOWLTypedConstant(value, factory.getOWLDataType(type.getURI()))));
  }

  OWLConceptExpression objectAllRestriction(URI propURI, OWLConceptExpression expr) {
    return new OWLConceptExpression(
        factory.getOWLObjectAllRestriction(factory.getOWLObjectProperty(propURI), expr.desc));
  }

  OWLConceptExpression objectAllRestriction(URI propURI, URI classURI) {
    return objectAllRestriction(propURI, namedClass(classURI));
  }

  OWLConceptExpression objectComplementOf(OWLConceptExpression expr) {
    return new OWLConceptExpression(factory.getOWLObjectComplementOf(expr.desc));
  }

  OWLConceptExpression objectComplementOf(URI classURI) {
    return objectComplementOf(namedClass(classURI));
  }

  OWLConceptExpression objectExactCardinalityRestriction(URI propURI, int value) {
    return new OWLConceptExpression(factory.getOWLObjectExactCardinalityRestriction(
        factory.getOWLObjectProperty(propURI), value));
  }

  OWLConceptExpression objectIntersectionOf(OWLConceptExpression... exprs) {
    Set<OWLDescription> descs = buildDescriptionSet(exprs);
    return new OWLConceptExpression(factory.getOWLObjectIntersectionOf(descs.toArray(new OWLDescription[0])));
  }

  OWLConceptExpression objectIntersectionOf(URI... classURIs) {
    OWLConceptExpression[] exprs = new OWLConceptExpression[classURIs.length];
    for(int i = 0; i < classURIs.length; i++) {
      exprs[i] = namedClass(classURIs[i]);
    }
    return objectIntersectionOf(exprs);
  }

  OWLConceptExpression objectMaxCardinalityRestriction(URI propURI, int value) {
    return new OWLConceptExpression(factory.getOWLObjectMaxCardinalityRestriction(
        factory.getOWLObjectProperty(propURI), value));
  }

  OWLConceptExpression objectMinCardinalityRestriction(URI propURI, int value) {
    return new OWLConceptExpression(factory.getOWLObjectMinCardinalityRestriction(
        factory.getOWLObjectProperty(propURI), value));
  }

  OWLConceptExpression objectOneOf(URI... indURIs) {
    return new OWLConceptExpression(factory.getOWLObjectOneOf(buildIndividualSet(indURIs)));
  }

  OWLConceptExpression objectSelfRestriction(URI propURI) {
    return new OWLConceptExpression(factory.getOWLObjectSelfRestriction(factory.getOWLObjectProperty(propURI)));
  }

  OWLConceptExpression objectSomeRestriction(URI propURI, OWLConceptExpression expr) {
    return new OWLConceptExpression(factory.getOWLObjectSomeRestriction(factory.getOWLObjectProperty(propURI),
        expr.desc));
  }

  OWLConceptExpression objectSomeRestriction(URI propURI, URI classURI) {
    return objectSomeRestriction(propURI, namedClass(classURI));
  }

  OWLConceptExpression objectUnionOf(OWLConceptExpression... exprs) {
    Set<OWLDescription> descs = buildDescriptionSet(exprs);
    return new OWLConceptExpression(factory.getOWLObjectUnionOf(descs.toArray(new OWLDescription[0])));
  }

  OWLConceptExpression objectUnionOf(URI... classURIs) {
    OWLConceptExpression[] exprs = new OWLConceptExpression[classURIs.length];
    for(int i = 0; i < classURIs.length; i++) {
      exprs[i] = namedClass(classURIs[i]);
    }
    return objectUnionOf(exprs);
  }

  OWLConceptExpression objectValueRestriction(URI propURI, URI indURI) {
    return new OWLConceptExpression(factory.getOWLObjectValueRestriction(factory.getOWLObjectProperty(propURI),
        factory.getOWLIndividual(indURI)));
  }

  /*
   * #########################################################################
   * 
   * Axioms pertaining to object properties
   */

  void declareObjectProperty(URI... propURIs) {
    if(!mutable) throw new Bug();
    for(URI propURI: propURIs) {
      axioms.add(factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(propURI)));
    }
  }

  void commentObjectProperty(URI propURI, String comment) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLEntityAnnotationAxiom(factory.getOWLObjectProperty(propURI),
        factory.getCommentAnnotation(comment)));
  }

  void labelObjectProperty(URI propURI, String label) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLEntityAnnotationAxiom(factory.getOWLObjectProperty(propURI),
        factory.getOWLLabelAnnotation(label)));
  }

  void objectPropertyDomain(URI propURI, URI... classURIs) {
    if(!mutable) throw new Bug();
    OWLObjectProperty prop = factory.getOWLObjectProperty(propURI);
    if(classURIs.length == 1) {
      axioms.add(factory.getOWLObjectPropertyDomainAxiom(prop, factory.getOWLClass(classURIs[0])));
    }
    else {
      axioms.add(factory.getOWLObjectPropertyDomainAxiom(prop, buildUnion(classURIs)));
    }
  }

  void objectPropertyDomain(URI propURI, OWLConceptExpression expr) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLObjectPropertyDomainAxiom(factory.getOWLObjectProperty(propURI), expr.desc));
  }

  void objectPropertyDomainIntersection(URI propURI, URI... classURIs) {
    if(!mutable) throw new Bug();
    OWLObjectProperty prop = factory.getOWLObjectProperty(propURI);
    axioms.add(factory.getOWLObjectPropertyDomainAxiom(prop, buildIntersection(classURIs)));
  }

  void objectPropertyRange(URI propURI, URI... classURIs) {
    if(!mutable) throw new Bug();
    OWLObjectProperty prop = factory.getOWLObjectProperty(propURI);
    if(classURIs.length == 1) {
      axioms.add(factory.getOWLObjectPropertyRangeAxiom(prop, factory.getOWLClass(classURIs[0])));
    }
    else {
      axioms.add(factory.getOWLObjectPropertyRangeAxiom(prop, buildUnion(classURIs)));
    }
  }

  void objectPropertyRange(URI propURI, OWLConceptExpression expr) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLObjectPropertyDomainAxiom(factory.getOWLObjectProperty(propURI), expr.desc));
  }

  void objectPropertyRangeIntersection(URI propURI, URI... classURIs) {
    if(!mutable) throw new Bug();
    OWLObjectProperty prop = factory.getOWLObjectProperty(propURI);
    axioms.add(factory.getOWLObjectPropertyRangeAxiom(prop, buildIntersection(classURIs)));
  }

  void objectPropertyRangeOneOf(URI propURI, URI... indURIs) {
    if(!mutable) throw new Bug();
    Set<OWLIndividual> inds = buildIndividualSet(indURIs);
    OWLObjectProperty prop = factory.getOWLObjectProperty(propURI);
    axioms.add(factory.getOWLObjectPropertyRangeAxiom(prop, factory.getOWLObjectOneOf(inds)));
  }

  void objectPropertyInverse(URI prop1URI, URI prop2URI) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLInverseObjectPropertiesAxiom(factory.getOWLObjectProperty(prop1URI),
        factory.getOWLObjectProperty(prop2URI)));
  }

  void objectPropertySymmetric(URI... propURIs) {
    if(!mutable) throw new Bug();
    for(URI propURI: propURIs) {
      axioms.add(factory.getOWLSymmetricObjectPropertyAxiom(factory.getOWLObjectProperty(propURI)));
    }
  }

  void objectPropertyAntiSymmetric(URI... propURIs) {
    if(!mutable) throw new Bug();
    for(URI propURI: propURIs) {
      axioms.add(factory.getOWLAntiSymmetricObjectPropertyAxiom(factory.getOWLObjectProperty(propURI)));
    }
  }

  void objectPropertyFunctional(URI... propURIs) {
    if(!mutable) throw new Bug();
    for(URI propURI: propURIs) {
      axioms.add(factory.getOWLFunctionalObjectPropertyAxiom(factory.getOWLObjectProperty(propURI)));
    }
  }

  void objectPropertyInverseFunctional(URI... propURIs) {
    if(!mutable) throw new Bug();
    for(URI propURI: propURIs) {
      axioms.add(factory.getOWLInverseFunctionalObjectPropertyAxiom(factory.getOWLObjectProperty(propURI)));
    }
  }

  void objectPropertyReflexive(URI... propURIs) {
    if(!mutable) throw new Bug();
    for(URI propURI: propURIs) {
      axioms.add(factory.getOWLReflexiveObjectPropertyAxiom(factory.getOWLObjectProperty(propURI)));
    }
  }

  void objectPropertyIrreflexive(URI... propURIs) {
    if(!mutable) throw new Bug();
    for(URI propURI: propURIs) {
      axioms.add(factory.getOWLIrreflexiveObjectPropertyAxiom(factory.getOWLObjectProperty(propURI)));
    }
  }

  void objectPropertyTransitive(URI... propURIs) {
    if(!mutable) throw new Bug();
    for(URI propURI: propURIs) {
      axioms.add(factory.getOWLTransitiveObjectPropertyAxiom(factory.getOWLObjectProperty(propURI)));
    }
  }

  void subObjectPropertyOf(URI propURI, URI... superPropURIs) {
    if(!mutable) throw new Bug();
    OWLObjectProperty prop = factory.getOWLObjectProperty(propURI);
    for(URI superPropURI: superPropURIs) {
      axioms.add(factory.getOWLSubObjectPropertyAxiom(prop, factory.getOWLObjectProperty(superPropURI)));
    }
  }

  void superPropertyOfChain(URI superPropURI, URI... propChainURIs) {
    if(!mutable) throw new Bug();
    List<OWLObjectProperty> propChain = new LinkedList<OWLObjectProperty>();
    for(URI propChainURI: propChainURIs) {
      propChain.add(factory.getOWLObjectProperty(propChainURI));
    }
    OWLObjectProperty superProp = factory.getOWLObjectProperty(superPropURI);
    axioms.add(factory.getOWLObjectPropertyChainSubPropertyAxiom(propChain, superProp));
  }

  void disjointObjectProperties(URI... propURIs) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLDisjointObjectPropertiesAxiom(buildObjectPropertySet(propURIs)));
  }

  void equivalentObjectProperties(URI... propURIs) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLEquivalentObjectPropertiesAxiom(buildObjectPropertySet(propURIs)));
  }

  /*
   * #########################################################################
   * 
   * Axioms pertaining to data properties
   */

  void declareDataProperty(URI... propURIs) {
    if(!mutable) throw new Bug();
    for(URI propURI: propURIs) {
      axioms.add(factory.getOWLDeclarationAxiom(factory.getOWLDataProperty(propURI)));
    }
  }

  void commentDataProperty(URI propURI, String comment) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLEntityAnnotationAxiom(factory.getOWLDataProperty(propURI),
        factory.getCommentAnnotation(comment)));
  }

  void labelDataProperty(URI propURI, String label) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLEntityAnnotationAxiom(factory.getOWLDataProperty(propURI),
        factory.getOWLLabelAnnotation(label)));
  }

  void dataPropertyDomain(URI propURI, URI... classURIs) {
    if(!mutable) throw new Bug();
    OWLDataProperty prop = factory.getOWLDataProperty(propURI);
    if(classURIs.length == 1) {
      axioms.add(factory.getOWLDataPropertyDomainAxiom(prop, factory.getOWLClass(classURIs[0])));
    }
    else {
      axioms.add(factory.getOWLDataPropertyDomainAxiom(prop, buildUnion(classURIs)));
    }
  }

  void dataPropertyDomain(URI propURI, OWLConceptExpression expr) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLDataPropertyDomainAxiom(factory.getOWLDataProperty(propURI), expr.desc));
  }

  void dataPropertyDomainIntersection(URI propURI, URI... classURIs) {
    if(!mutable) throw new Bug();
    OWLDataProperty prop = factory.getOWLDataProperty(propURI);
    axioms.add(factory.getOWLDataPropertyDomainAxiom(prop, buildIntersection(classURIs)));
  }

  void dataPropertyRange(URI propURI, XSDVocabulary type) {
    if(!mutable) throw new Bug();
    OWLDataProperty prop = factory.getOWLDataProperty(propURI);
    axioms.add(factory.getOWLDataPropertyRangeAxiom(prop, factory.getOWLDataType(type.getURI())));
  }

  void dataPropertyRangeOneOf(URI propURI, Object... values) {
    if(!mutable) throw new Bug();
    Set<OWLTypedConstant> consts = buildConstantSet(values);
    OWLDataOneOf oneOf = factory.getOWLDataOneOf(consts);
    axioms.add(factory.getOWLDataPropertyRangeAxiom(factory.getOWLDataProperty(propURI), oneOf));
  }

  void dataPropertyRangeMaxExclusive(URI propURI, XSDVocabulary type, Number max) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLDataPropertyRangeAxiom(factory.getOWLDataProperty(propURI),
        buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.MAX_EXCLUSIVE, type, max.toString())));
  }

  void dataPropertyRangeMinExclusive(URI propURI, XSDVocabulary type, Number min) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLDataPropertyRangeAxiom(factory.getOWLDataProperty(propURI),
        buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.MIN_EXCLUSIVE, type, min.toString())));
  }

  void dataPropertyRangeMaxInclusive(URI propURI, XSDVocabulary type, Number max) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLDataPropertyRangeAxiom(factory.getOWLDataProperty(propURI),
        buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.MAX_INCLUSIVE, type, max.toString())));
  }

  void dataPropertyRangeMinInclusive(URI propURI, XSDVocabulary type, Number min) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLDataPropertyRangeAxiom(factory.getOWLDataProperty(propURI),
        buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.MIN_INCLUSIVE, type, min.toString())));
  }

  void dataPropertyRangeLength(URI propURI, int length) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLDataPropertyRangeAxiom(
        factory.getOWLDataProperty(propURI),
        buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.LENGTH, XSDVocabulary.STRING,
            Integer.toString(length))));
  }

  void dataPropertyRangeMinLength(URI propURI, int length) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLDataPropertyRangeAxiom(
        factory.getOWLDataProperty(propURI),
        buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.MIN_LENGTH, XSDVocabulary.STRING,
            Integer.toString(length))));
  }

  void dataPropertyRangeMaxLength(URI propURI, int length) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLDataPropertyRangeAxiom(
        factory.getOWLDataProperty(propURI),
        buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.MAX_LENGTH, XSDVocabulary.STRING,
            Integer.toString(length))));
  }

  void dataPropertyRangePattern(URI propURI, String pattern) {
    if(!mutable) throw new Bug();
    axioms
        .add(factory.getOWLDataPropertyRangeAxiom(
            factory.getOWLDataProperty(propURI),
            buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary.LENGTH, XSDVocabulary.STRING,
                pattern)));
  }

  void dataPropertyFunctional(URI... propURIs) {
    if(!mutable) throw new Bug();
    for(URI propURI: propURIs) {
      axioms.add(factory.getOWLFunctionalDataPropertyAxiom(factory.getOWLDataProperty(propURI)));
    }
  }

  void disjointDataProperties(URI... propURIs) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLDisjointDataPropertiesAxiom(buildDataPropertySet(propURIs)));
  }

  void equivalentDataProperties(URI... propURIs) {
    if(!mutable) throw new Bug();
    axioms.add(factory.getOWLEquivalentDataPropertiesAxiom(buildDataPropertySet(propURIs)));
  }

  void subDataPropertyOf(URI propURI, URI... superPropURIs) {
    if(!mutable) throw new Bug();
    OWLDataProperty prop = factory.getOWLDataProperty(propURI);
    for(URI superPropURI: superPropURIs) {
      axioms.add(factory.getOWLSubDataPropertyAxiom(prop, factory.getOWLDataProperty(superPropURI)));
    }
  }

  /*
   * #########################################################################
   * 
   * Axioms pertaining to individuals
   */

  void declareIndividual(URI... indURIs) {
    for(URI indURI: indURIs) {
      axioms.add(factory.getOWLDeclarationAxiom(factory.getOWLIndividual(indURI)));
    }
  }

  void commentIndividual(URI indURI, String comment) {
    axioms.add(factory.getOWLEntityAnnotationAxiom(factory.getOWLIndividual(indURI),
        factory.getCommentAnnotation(comment)));
  }

  void labelIndividual(URI indURI, String label) {
    axioms.add(factory.getOWLEntityAnnotationAxiom(factory.getOWLIndividual(indURI),
        factory.getOWLLabelAnnotation(label)));
  }

  void individualHasClass(URI indURI, URI... classURIs) {
    OWLIndividual individual = factory.getOWLIndividual(indURI);
    if(classURIs.length == 1) {
      axioms.add(factory.getOWLClassAssertionAxiom(individual, factory.getOWLClass(classURIs[0])));
    }
    else {
      axioms.add(factory.getOWLClassAssertionAxiom(individual, buildUnion(classURIs)));
    }
  }

  void individualHasClass(URI indURI, OWLConceptExpression... exprs) {
    OWLIndividual individual = factory.getOWLIndividual(indURI);
    if(exprs.length == 1) {
      axioms.add(factory.getOWLClassAssertionAxiom(individual, exprs[0].desc));
    }
    else {
      axioms.add(factory.getOWLClassAssertionAxiom(individual, objectUnionOf(exprs).desc));
    }
  }

  void differentIndividuals(URI... indURIs) {
    axioms.add(factory.getOWLDifferentIndividualsAxiom(buildIndividualSet(indURIs)));
  }

  void sameIndividuals(URI... indURIs) {
    if(spp.isOWL2() || indURIs.length == 2) {
      axioms.add(factory.getOWLSameIndividualsAxiom(buildIndividualSet(indURIs)));
    }
    else {
      for(int i = 0; i < indURIs.length; i++) {
        for(int j = i + 1; j < indURIs.length; j++) {
          axioms.add(factory.getOWLSameIndividualsAxiom(buildIndividualSet(indURIs[i], indURIs[j])));
        }
      }
    }
  }

  void individualHasDataPropertyValue(URI indURI, URI propURI, XSDVocabulary type, String... values) {
    OWLIndividual individual = factory.getOWLIndividual(indURI);
    OWLDataProperty property = factory.getOWLDataProperty(propURI);
    OWLDataType datatype = factory.getOWLDataType(type.getURI());
    for(String value: values) {
      OWLConstant constant = factory.getOWLTypedConstant(value, datatype);
      axioms.add(factory.getOWLDataPropertyAssertionAxiom(individual, property, constant));
    }
  }

  void individualHasDataPropertyValue(URI indURI, URI propURI, boolean... values) {
    String[] valueStr = new String[values.length];
    for(int i = 0; i < values.length; i++) {
      valueStr[i] = Boolean.toString(values[i]);
    }
    individualHasDataPropertyValue(indURI, propURI, XSDVocabulary.BOOLEAN, valueStr);
  }

  void individualHasDataPropertyValue(URI indURI, URI propURI, char... values) {
    String[] valueStr = new String[values.length];
    for(int i = 0; i < values.length; i++) {
      valueStr[i] = Character.toString(values[i]);
    }
    individualHasDataPropertyValue(indURI, propURI, XSDVocabulary.BYTE, valueStr);
  }

  void individualHasDataPropertyValue(URI indURI, URI propURI, short... values) {
    String[] valueStr = new String[values.length];
    for(int i = 0; i < values.length; i++) {
      valueStr[i] = Short.toString(values[i]);
    }
    individualHasDataPropertyValue(indURI, propURI, XSDVocabulary.SHORT, valueStr);
  }

  void individualHasDataPropertyValue(URI indURI, URI propURI, int... values) {
    String[] valueStr = new String[values.length];
    for(int i = 0; i < values.length; i++) {
      valueStr[i] = Integer.toString(values[i]);
    }
    individualHasDataPropertyValue(indURI, propURI, XSDVocabulary.INT, valueStr);
  }

  void individualHasDataPropertyValue(URI indURI, URI propURI, long... values) {
    String[] valueStr = new String[values.length];
    for(int i = 0; i < values.length; i++) {
      valueStr[i] = Long.toString(values[i]);
    }
    individualHasDataPropertyValue(indURI, propURI, XSDVocabulary.LONG, valueStr);
  }

  void individualHasDataPropertyValue(URI indURI, URI propURI, float... values) {
    String[] valueStr = new String[values.length];
    for(int i = 0; i < values.length; i++) {
      if(Float.isNaN(values[i])) valueStr[i] = "NaN";
      else if(Float.isInfinite(values[i])) valueStr[i] = values[i] < 0.0F ? "-INF" : "INF";
      else
        valueStr[i] = Float.toString(values[i]);
    }
    individualHasDataPropertyValue(indURI, propURI, XSDVocabulary.FLOAT, valueStr);
  }

  void individualHasDataPropertyValue(URI indURI, URI propURI, double... values) {
    String[] valueStr = new String[values.length];
    for(int i = 0; i < values.length; i++) {
      if(Double.isNaN(values[i])) valueStr[i] = "NaN";
      else if(Double.isInfinite(values[i])) valueStr[i] = values[i] < 0.0 ? "-INF" : "INF";
      else
        valueStr[i] = Double.toString(values[i]);
    }
    individualHasDataPropertyValue(indURI, propURI, XSDVocabulary.DOUBLE, valueStr);
  }

  void individualHasDataPropertyValue(URI indURI, URI propURI, String... values) {
    individualHasDataPropertyValue(indURI, propURI, XSDVocabulary.STRING, values);
  }

  void individualHasDataPropertyValue(URI indURI, URI propURI, URI... values) {
    String[] valueStr = new String[values.length];
    for(int i = 0; i < values.length; i++) {
      valueStr[i] = values[i].toString();
    }
    individualHasDataPropertyValue(indURI, propURI, XSDVocabulary.ANY_URI, valueStr);
  }

  void individualHasNotDataPropertyValue(URI indURI, URI propURI, XSDVocabulary type, String... values) {
    OWLIndividual individual = factory.getOWLIndividual(indURI);
    OWLDataProperty property = factory.getOWLDataProperty(propURI);
    OWLDataType datatype = factory.getOWLDataType(type.getURI());
    for(String value: values) {
      OWLConstant constant = factory.getOWLTypedConstant(value, datatype);
      axioms.add(factory.getOWLNegativeDataPropertyAssertionAxiom(individual, property, constant));
    }
  }

  void individualHasNotDataPropertyValue(URI indURI, URI propURI, boolean... values) {
    String[] valueStr = new String[values.length];
    for(int i = 0; i < values.length; i++) {
      valueStr[i] = Boolean.toString(values[i]);
    }
    individualHasNotDataPropertyValue(indURI, propURI, XSDVocabulary.BOOLEAN, valueStr);
  }

  void individualHasNotDataPropertyValue(URI indURI, URI propURI, char... values) {
    String[] valueStr = new String[values.length];
    for(int i = 0; i < values.length; i++) {
      valueStr[i] = Character.toString(values[i]);
    }
    individualHasNotDataPropertyValue(indURI, propURI, XSDVocabulary.BYTE, valueStr);
  }

  void individualHasNotDataPropertyValue(URI indURI, URI propURI, short... values) {
    String[] valueStr = new String[values.length];
    for(int i = 0; i < values.length; i++) {
      valueStr[i] = Short.toString(values[i]);
    }
    individualHasNotDataPropertyValue(indURI, propURI, XSDVocabulary.SHORT, valueStr);
  }

  void individualHasNotDataPropertyValue(URI indURI, URI propURI, int... values) {
    String[] valueStr = new String[values.length];
    for(int i = 0; i < values.length; i++) {
      valueStr[i] = Integer.toString(values[i]);
    }
    individualHasNotDataPropertyValue(indURI, propURI, XSDVocabulary.INT, valueStr);
  }

  void individualHasNotDataPropertyValue(URI indURI, URI propURI, long... values) {
    String[] valueStr = new String[values.length];
    for(int i = 0; i < values.length; i++) {
      valueStr[i] = Long.toString(values[i]);
    }
    individualHasNotDataPropertyValue(indURI, propURI, XSDVocabulary.LONG, valueStr);
  }

  void individualHasNotDataPropertyValue(URI indURI, URI propURI, float... values) {
    String[] valueStr = new String[values.length];
    for(int i = 0; i < values.length; i++) {
      if(Float.isNaN(values[i])) valueStr[i] = "NaN";
      else if(Float.isInfinite(values[i])) valueStr[i] = values[i] < 0.0F ? "-INF" : "INF";
      else
        valueStr[i] = Float.toString(values[i]);
    }
    individualHasNotDataPropertyValue(indURI, propURI, XSDVocabulary.FLOAT, valueStr);
  }

  void individualHasNotDataPropertyValue(URI indURI, URI propURI, double... values) {
    String[] valueStr = new String[values.length];
    for(int i = 0; i < values.length; i++) {
      if(Double.isNaN(values[i])) valueStr[i] = "NaN";
      else if(Double.isInfinite(values[i])) valueStr[i] = values[i] < 0.0 ? "-INF" : "INF";
      else
        valueStr[i] = Double.toString(values[i]);
    }
    individualHasNotDataPropertyValue(indURI, propURI, XSDVocabulary.DOUBLE, valueStr);
  }

  void individualHasNotDataPropertyValue(URI indURI, URI propURI, String... values) {
    individualHasNotDataPropertyValue(indURI, propURI, XSDVocabulary.STRING, values);
  }

  void individualHasNotDataPropertyValue(URI indURI, URI propURI, URI... values) {
    String[] valueStr = new String[values.length];
    for(int i = 0; i < values.length; i++) {
      valueStr[i] = values[i].toString();
    }
    individualHasNotDataPropertyValue(indURI, propURI, XSDVocabulary.ANY_URI, valueStr);
  }

  void individualHasObjectPropertyValue(URI indURI, URI propURI, URI... objURIs) {
    OWLIndividual individual = factory.getOWLIndividual(indURI);
    OWLObjectProperty property = factory.getOWLObjectProperty(propURI);
    for(URI objURI: objURIs) {
      axioms.add(factory.getOWLObjectPropertyAssertionAxiom(individual, property, factory.getOWLIndividual(objURI)));
    }
  }

  void individualHasNotObjectPropertyValue(URI indURI, URI propURI, URI... objURIs) {
    OWLIndividual individual = factory.getOWLIndividual(indURI);
    OWLObjectProperty property = factory.getOWLObjectProperty(propURI);
    for(URI objURI: objURIs) {
      axioms.add(factory.getOWLNegativeObjectPropertyAssertionAxiom(individual, property,
          factory.getOWLIndividual(objURI)));
    }
  }

  /*
   * #########################################################################
   * 
   * Other tools and utilities
   */

  public URI getURI() {
    return ontology.getURI();
  }

  private URI getInferredURI() {
    String baseURI = ontology.getURI().toString();
    if(baseURI.endsWith(DEFAULT_OWL_SUFFIX)) {
      return URI.create(baseURI.substring(baseURI.length() - DEFAULT_OWL_SUFFIX.length()) + "-inferred"
        + DEFAULT_OWL_SUFFIX);
    }
    else {
      return URI.create(baseURI + "-inferred");
    }
  }

  public void makeInferences() throws OWLOntologyCreationException, OWLReasonerException, ClassNotFoundException {
    makeInferences(getInferredURI());
  }

  public void makeInferences(String reasonerClassName) throws OWLOntologyCreationException, OWLReasonerException {
    makeInferences(getInferredURI(), reasonerClassName);
  }

  public void makeInferences(URI inferredOntologyURI) throws OWLOntologyCreationException, ClassNotFoundException,
      OWLReasonerException {
    String reasonerClassName = ObiamaSetUp.knownReasonerWithValidEnvironment();
    if(reasonerClassName == null) {
      throw new ClassNotFoundException("No known reasoner class is available. "
        + ObiamaSetUp.knownReasonerRequirementsMessage());
    }
    makeInferences(inferredOntologyURI, reasonerClassName);
  }

  public void makeInferences(URI inferredOntologyURI, String reasonerClassName) throws OWLOntologyCreationException,
      OWLReasonerException {
    if(nonInferredOntology == null) {
      nonInferredOntology = ontology;
    }
    else {
      manager.removeOntology(ontology.getURI());
    }
    ontology = manager.createOntology(inferredOntologyURI);
    InferredOntologyCreator.infer(manager, ontology, reasonerClassName);
  }

  public void makeInferencesSave(URI inferredLogicalURI, URI inferredPhysicalURI) throws OWLOntologyCreationException,
      UnknownOWLOntologyException, OWLOntologyStorageException, ClassNotFoundException, OWLReasonerException {
    makeInferences(inferredLogicalURI);
    saveOntology(inferredPhysicalURI);
  }

  public void makeInferencesSave(URI inferredPhysicalURI) throws UnknownOWLOntologyException,
      OWLOntologyCreationException, OWLOntologyStorageException, OWLReasonerException, ClassNotFoundException {
    makeInferencesSave(getInferredURI(), inferredPhysicalURI);
  }

  public void makeInferencesSave(URI inferredLogicalURI, URI inferredPhysicalURI, String reasonerClassName)
      throws OWLOntologyCreationException, UnknownOWLOntologyException, OWLOntologyStorageException,
      OWLReasonerException {
    makeInferences(inferredLogicalURI, reasonerClassName);
    saveOntology(inferredPhysicalURI);
  }

  public void makeInferencesSave(URI inferredPhysicalURI, String reasonerClassName) throws UnknownOWLOntologyException,
      OWLOntologyCreationException, OWLOntologyStorageException, OWLReasonerException {
    makeInferencesSave(getInferredURI(), inferredPhysicalURI, reasonerClassName);
  }

  void importIndividuals(URI ontologyURI) throws OWLOntologyCreationException {
    OWLOntologyManager myManager = OWLManager.createOWLOntologyManager();
    OWLOntology abox = OntologyIOHelper.load(ontologyURI, myManager);
    importIndividuals(abox);
    myManager.removeOntology(ontologyURI);
  }

  void importIndividuals(URI ontologyURI, Set<URI> ignoredImports) throws OWLOntologyCreationException {
    OWLOntologyManager myManager = OWLManager.createOWLOntologyManager();
    OntologyIOHelper helper = new OntologyIOHelper();
    helper.configure(myManager);
    for(URI ignoredImportsURI: ignoredImports) {
      helper.ignoreFailedImport(ignoredImportsURI.toString());
    }
    OWLOntology abox = helper.loadOntology(ontologyURI, myManager);
    importIndividuals(abox);
    myManager.removeOntology(ontologyURI);
  }

  void importIndividuals(OWLOntology abox) {
    importIndividuals(abox, null);
  }

  private void importIndividuals(OWLOntology abox, AbstractOntology tbox) {
    Set<OWLIndividual> individuals = abox.getReferencedIndividuals();
    for(OWLIndividual individual: individuals) {
      Set<OWLIndividualAxiom> aboxAxioms = abox.getAxioms(individual);
      for(OWLIndividualAxiom aboxAxiom: aboxAxioms) {
        boolean includeAxiom = false;
        for(OWLEntity aboxAxiomEntity: aboxAxiom.getReferencedEntities()) {
          if(aboxAxiomEntity.isOWLIndividual()) continue;
          Set<OWLOntology> searchSet = new HashSet<OWLOntology>();
          if(tbox != null) searchSet.add(tbox.ontology);
          searchSet.addAll(manager.getOntologies());
          for(OWLOntology tboxOntology: searchSet) {
            if(tboxOntology.equals(abox)) continue;
            if(tboxOntology.containsEntityReference(aboxAxiomEntity)) {
              includeAxiom = true;
            }
          }
        }
        if(includeAxiom) axioms.add(aboxAxiom);
      }
    }

  }

  public Set<URI> getAssertedMembersOf(URI classURI) {
    OWLClass owlClass = factory.getOWLClass(classURI);
    Set<OWLIndividual> owlInds = owlClass.getIndividuals(manager.getImportsClosure(ontology));
    if(owlInds == null) return new HashSet<URI>();
    return buildNamedObjectURISet(owlInds.toArray(new OWLIndividual[0]));
  }

  public Set<URI> getAssertedSubClassesOf(URI classURI) {
    OWLClass owlClass = factory.getOWLClass(classURI);
    Set<OWLDescription> owlDescs = owlClass.getSubClasses(manager.getImportsClosure(ontology));
    if(owlDescs == null) return new HashSet<URI>();
    return buildNamedObjectURISet(buildNamedClassSet(owlDescs.toArray(new OWLDescription[0])).toArray(new OWLClass[0]));
  }

  public Set<URI> getNamedSubClassesOf(URI classURI) {
    OWLClass owlClass = factory.getOWLClass(classURI);
    Set<OWLClass> subClasses = getNamedSubClassesOf(owlClass);
    return buildNamedObjectURISet(subClasses.toArray(new OWLClass[0]));
  }

  private Set<OWLClass> getNamedSubClassesOf(OWLClass owlClass) {
    Set<OWLDescription> owlDescs = new HashSet<OWLDescription>();
    owlDescs.addAll(owlClass.getSubClasses(manager.getImportsClosure(ontology)));
    owlDescs.addAll(owlClass.getEquivalentClasses(manager.getImportsClosure(ontology)));
    return getNamedSubClassesOf(owlDescs);
  }

  private Set<OWLClass> getNamedSubClassesOf(Set<OWLDescription> owlDescs) {
    Set<OWLClass> subClasses = new HashSet<OWLClass>();
    for(OWLDescription owlDesc: owlDescs) {
      if(!owlDesc.isAnonymous()) {
        subClasses.add(owlDesc.asOWLClass());
        subClasses.addAll(getNamedSubClassesOf(owlDesc.asOWLClass()));
      }
    }
    return subClasses;
  }

  public Set<URI> getNamedSuperClassesOf(URI classURI) {
    OWLClass owlClass = factory.getOWLClass(classURI);
    Set<OWLClass> superClasses = getNamedSuperClassesOf(owlClass);
    return buildNamedObjectURISet(superClasses.toArray(new OWLClass[0]));
  }

  private Set<OWLClass> getNamedSuperClassesOf(OWLClass owlClass) {
    Set<OWLDescription> owlDescs = new HashSet<OWLDescription>();
    owlDescs.addAll(owlClass.getSuperClasses(manager.getImportsClosure(ontology)));
    owlDescs.addAll(owlClass.getEquivalentClasses(manager.getImportsClosure(ontology)));
    return getNamedSuperClassesOf(owlDescs);
  }

  private Set<OWLClass> getNamedSuperClassesOf(Set<OWLDescription> owlDescs) {
    Set<OWLClass> superClasses = new HashSet<OWLClass>();
    for(OWLDescription owlDesc: owlDescs) {
      if(!owlDesc.isAnonymous()) {
        superClasses.add(owlDesc.asOWLClass());
        superClasses.addAll(getNamedSuperClassesOf(owlDesc.asOWLClass()));
      }
    }
    return superClasses;
  }

  public Set<URI> getAssertedDataPropertiesOf(URI classURI) {
    OWLClass owlClass = factory.getOWLClass(classURI);
    Set<OWLDataProperty> allDataProperties = ontology.getReferencedDataProperties();
    Set<OWLDataProperty> dataDomains = new HashSet<OWLDataProperty>();
    for(OWLDataProperty dataProperty: allDataProperties) {
      Set<OWLDescription> domain = dataProperty.getDomains(manager.getImportsClosure(ontology));
      if(domain.contains(owlClass)) dataDomains.add(dataProperty);
      else if(getNamedSubClassesOf(domain).contains(owlClass)) dataDomains.add(dataProperty);
    }
    return buildNamedObjectURISet(dataDomains.toArray(new OWLDataProperty[0]));
  }

  public Set<URI> getAssertedClassesOf(URI indURI) {
    OWLIndividual owlInd = factory.getOWLIndividual(indURI);
    Set<OWLDescription> owlClasses = owlInd.getTypes(manager.getImportsClosure(ontology));
    if(owlClasses == null) return new HashSet<URI>();
    Set<URI> classURIs = new HashSet<URI>();
    for(OWLDescription desc: owlClasses) {
      if(!desc.isAnonymous()) {
        classURIs.add(desc.asOWLClass().getURI());
      }
    }
    return classURIs;
  }

  public Set<URI> getNamedClassesOf(URI indURI) {
    Set<URI> assertedClasses = getAssertedClassesOf(indURI);
    Set<URI> namedClasses = new HashSet<URI>(assertedClasses);
    for(URI classURI: assertedClasses) {
      namedClasses.addAll(getNamedSuperClassesOf(classURI));
    }
    return namedClasses;
  }

  public XSDVocabulary getGeneralisedNamedDataRangeOf(URI propURI) {
    OWLDataProperty owlProp = factory.getOWLDataProperty(propURI);
    Set<OWLDataRange> ranges = owlProp.getRanges(manager.getImportsClosure(ontology));
    XSDVocabulary xsdRange = null;
    for(OWLDataRange range: ranges) {
      if(range.isDataType()) {
        OWLDataType type = (OWLDataType)range;
        if(xsdRange == null) {
          xsdRange = XSDHelper.xsdTypes.get(type.getURI());
        }
        else {
          xsdRange = XSDHelper.generaliseType(xsdRange, type);
        }
      }
    }
    return xsdRange;
  }

  public boolean isAssertedA(URI indURI, URI classURI) {
    return getAssertedClassesOf(indURI).contains(classURI);
  }

  public boolean isAssertedA(URI indURI, OWLConceptExpression expr) {
    OWLIndividual ind = factory.getOWLIndividual(indURI);
    Set<OWLDescription> owlDescs = ind.getTypes(manager.getImportsClosure(ontology));
    for(OWLDescription owlDesc: owlDescs) {
      if(owlDesc.equals(expr.desc)) return true;
    }
    return false;
  }

  public boolean isNamedA(URI indURI, URI classURI) {
    return getNamedClassesOf(indURI).contains(classURI);
  }

  public Set<URI> getObjectPropertyOf(URI indURI, URI propURI) {
    OWLObjectProperty prop = factory.getOWLObjectProperty(propURI);
    OWLIndividual owlInd = factory.getOWLIndividual(indURI);
    Map<OWLObjectPropertyExpression, Set<OWLIndividual>> allProps = owlInd.getObjectPropertyValues(ontology);
    if(allProps == null) return new HashSet<URI>();
    Set<OWLIndividual> owlInds = allProps.get(prop);
    if(owlInds == null) return new HashSet<URI>();
    return buildNamedObjectURISet(owlInds.toArray(new OWLIndividual[0]));
  }

  public URI getFunctionalObjectPropertyOf(URI indURI, URI propURI) {
    URI[] arr = (getObjectPropertyOf(indURI, propURI)).toArray(new URI[0]);
    if(arr == null) return null;
    if(arr.length == 0) return null;
    if(arr.length > 1) throw new Bug();
    return arr[0];
  }

  private Set<OWLConstant> getDataPropertyOf(URI indURI, URI propURI) {
    OWLDataProperty prop = factory.getOWLDataProperty(propURI);
    OWLIndividual owlInd = factory.getOWLIndividual(indURI);
    Map<OWLDataPropertyExpression, Set<OWLConstant>> allProps = owlInd.getDataPropertyValues(ontology);
    if(allProps == null) return new HashSet<OWLConstant>();
    Set<OWLConstant> owlConsts = allProps.get(prop);
    if(owlConsts == null) return new HashSet<OWLConstant>();
    return owlConsts;
  }

  public Set<String> getStringDataPropertyOf(URI indURI, URI propURI) {
    Set<OWLConstant> owlConsts = getDataPropertyOf(indURI, propURI);
    Set<String> strings = new HashSet<String>();
    for(OWLConstant owlConst: owlConsts) {
      strings.add(owlConst.getLiteral());
    }
    return strings;
  }

  public String getStringFunctionalDataPropertyOf(URI indURI, URI propURI) {
    String[] arr = (getStringDataPropertyOf(indURI, propURI)).toArray(new String[0]);
    if(arr == null) return null;
    if(arr.length == 0) return null;
    if(arr.length > 1) throw new Bug();
    return arr[0];
  }

  public Set<Double> getDoubleDataPropertyOf(URI indURI, URI propURI) {
    Set<OWLConstant> owlConsts = getDataPropertyOf(indURI, propURI);
    Set<Double> doubles = new HashSet<Double>();
    for(OWLConstant owlConst: owlConsts) {
      if(owlConst.isTyped()) {
        OWLTypedConstant typedConst = owlConst.asOWLTypedConstant();
        if(XSDHelper.datatypeContains(XSDVocabulary.DOUBLE, XSDHelper.xsdTypes.get(typedConst.getDataType().getURI()))) {
          doubles.add(new Double(typedConst.getLiteral()));
        }
      }
      else {
        try {
          doubles.add(Double.parseDouble(owlConst.getLiteral()));
        }
        catch(NumberFormatException e) {
          // do nothing
        }
      }
    }
    return doubles;
  }

  public Double getDoubleFunctionalDataPropertyOf(URI indURI, URI propURI) {
    Double[] arr = (getDoubleDataPropertyOf(indURI, propURI)).toArray(new Double[0]);
    if(arr == null) return null;
    if(arr.length == 0) return null;
    if(arr.length > 1) throw new Bug();
    return arr[0];
  }

  public Set<Float> getFloatDataPropertyOf(URI indURI, URI propURI) {
    Set<OWLConstant> owlConsts = getDataPropertyOf(indURI, propURI);
    Set<Float> floats = new HashSet<Float>();
    for(OWLConstant owlConst: owlConsts) {
      if(owlConst.isTyped()) {
        OWLTypedConstant typedConst = owlConst.asOWLTypedConstant();
        if(XSDHelper.datatypeContains(XSDVocabulary.FLOAT, XSDHelper.xsdTypes.get(typedConst.getDataType().getURI()))) {
          floats.add(new Float(typedConst.getLiteral()));
        }
      }
      else {
        try {
          floats.add(Float.parseFloat(owlConst.getLiteral()));
        }
        catch(NumberFormatException e) {
          // do nothing
        }
      }
    }
    return floats;
  }

  public Float getFloatFunctionalDataPropertyOf(URI indURI, URI propURI) {
    Float[] arr = (getFloatDataPropertyOf(indURI, propURI)).toArray(new Float[0]);
    if(arr == null) return null;
    if(arr.length == 0) return null;
    if(arr.length > 1) throw new Bug();
    return arr[0];
  }

  public Set<Long> getLongDataPropertyOf(URI indURI, URI propURI) {
    Set<OWLConstant> owlConsts = getDataPropertyOf(indURI, propURI);
    Set<Long> longs = new HashSet<Long>();
    for(OWLConstant owlConst: owlConsts) {
      if(owlConst.isTyped()) {
        OWLTypedConstant typedConst = owlConst.asOWLTypedConstant();
        if(XSDHelper.datatypeContains(XSDVocabulary.LONG, XSDHelper.xsdTypes.get(typedConst.getDataType().getURI()))) {
          longs.add(new Long(typedConst.getLiteral()));
        }
      }
      else {
        try {
          longs.add(Long.parseLong(owlConst.getLiteral()));
        }
        catch(NumberFormatException e) {
          // do nothing
        }
      }
    }
    return longs;
  }

  public Long getLongFunctionalDataPropertyOf(URI indURI, URI propURI) {
    Long[] arr = (getLongDataPropertyOf(indURI, propURI)).toArray(new Long[0]);
    if(arr == null) return null;
    if(arr.length == 0) return null;
    if(arr.length > 1) throw new Bug();
    return arr[0];
  }

  public Set<Integer> getIntegerDataPropertyOf(URI indURI, URI propURI) {
    Set<OWLConstant> owlConsts = getDataPropertyOf(indURI, propURI);
    Set<Integer> integers = new HashSet<Integer>();
    for(OWLConstant owlConst: owlConsts) {
      if(owlConst.isTyped()) {
        OWLTypedConstant typedConst = owlConst.asOWLTypedConstant();
        if(XSDHelper.datatypeContains(XSDVocabulary.INT, XSDHelper.xsdTypes.get(typedConst.getDataType().getURI()))) {
          integers.add(new Integer(typedConst.getLiteral()));
        }
      }
      else {
        try {
          integers.add(Integer.parseInt(owlConst.getLiteral()));
        }
        catch(NumberFormatException e) {
          // do nothing
        }
      }
    }
    return integers;
  }

  public Integer getIntegerFunctionalDataPropertyOf(URI indURI, URI propURI) {
    Integer[] arr = (getIntegerDataPropertyOf(indURI, propURI)).toArray(new Integer[0]);
    if(arr == null) return null;
    if(arr.length == 0) return null;
    if(arr.length > 1) throw new Bug();
    return arr[0];
  }

  public Set<Short> getShortDataPropertyOf(URI indURI, URI propURI) {
    Set<OWLConstant> owlConsts = getDataPropertyOf(indURI, propURI);
    Set<Short> shorts = new HashSet<Short>();
    for(OWLConstant owlConst: owlConsts) {
      if(owlConst.isTyped()) {
        OWLTypedConstant typedConst = owlConst.asOWLTypedConstant();
        if(XSDHelper.datatypeContains(XSDVocabulary.SHORT, XSDHelper.xsdTypes.get(typedConst.getDataType().getURI()))) {
          shorts.add(new Short(typedConst.getLiteral()));
        }
      }
      else {
        try {
          shorts.add(Short.parseShort(owlConst.getLiteral()));
        }
        catch(NumberFormatException e) {
          // do nothing
        }
      }
    }
    return shorts;
  }

  public Short getShortFunctionalDataPropertyOf(URI indURI, URI propURI) {
    Short[] arr = (getShortDataPropertyOf(indURI, propURI)).toArray(new Short[0]);
    if(arr == null) return null;
    if(arr.length == 0) return null;
    if(arr.length > 1) throw new Bug();
    return arr[0];
  }

  public Set<Byte> getByteDataPropertyOf(URI indURI, URI propURI) {
    Set<OWLConstant> owlConsts = getDataPropertyOf(indURI, propURI);
    Set<Byte> bytes = new HashSet<Byte>();
    for(OWLConstant owlConst: owlConsts) {
      if(owlConst.isTyped()) {
        OWLTypedConstant typedConst = owlConst.asOWLTypedConstant();
        if(XSDHelper.datatypeContains(XSDVocabulary.BYTE, XSDHelper.xsdTypes.get(typedConst.getDataType().getURI()))) {
          bytes.add(new Byte(typedConst.getLiteral()));
        }
      }
      else {
        try {
          bytes.add(Byte.parseByte(owlConst.getLiteral()));
        }
        catch(NumberFormatException e) {
          // do nothing
        }
      }
    }
    return bytes;
  }

  public Byte getByteFunctionalDataPropertyOf(URI indURI, URI propURI) {
    Byte[] arr = (getByteDataPropertyOf(indURI, propURI)).toArray(new Byte[0]);
    if(arr == null) return null;
    if(arr.length == 0) return null;
    if(arr.length > 1) throw new Bug();
    return arr[0];
  }

  public Set<URI> getURIDataPropertyOf(URI indURI, URI propURI) {
    Set<OWLConstant> owlConsts = getDataPropertyOf(indURI, propURI);
    Set<URI> uris = new HashSet<URI>();
    for(OWLConstant owlConst: owlConsts) {
      if(owlConst.isTyped()) {
        OWLTypedConstant typedConst = owlConst.asOWLTypedConstant();
        if(XSDVocabulary.ANY_URI.getURI().equals(typedConst.getDataType().getURI())) {
          uris.add(URI.create(typedConst.getLiteral()));
        }
      }
      else {
        try {
          uris.add(new URI(owlConst.getLiteral()));
        }
        catch(URISyntaxException e) {
          // do nothing
        }
      }
    }
    return uris;
  }

  public URI getURIFunctionalDataPropertyOf(URI indURI, URI propURI) {
    URI[] arr = (getURIDataPropertyOf(indURI, propURI)).toArray(new URI[0]);
    if(arr == null) return null;
    if(arr.length == 0) return null;
    if(arr.length > 1) throw new Bug();
    return arr[0];
  }

  public Set<String> getCommentsOnIndividual(URI indURI) {
    OWLIndividual ind = factory.getOWLIndividual(indURI);
    return getCommentsOn(ind);
  }

  public Set<String> getCommentsOnDataProperty(URI propURI) {
    OWLDataProperty prop = factory.getOWLDataProperty(propURI);
    return getCommentsOn(prop);
  }

  public Set<String> getCommentsOnObjectProperty(URI propURI) {
    OWLObjectProperty prop = factory.getOWLObjectProperty(propURI);
    return getCommentsOn(prop);
  }

  public Set<String> getCommentsOnConcept(URI classURI) {
    OWLClass owlClass = factory.getOWLClass(classURI);
    return getCommentsOn(owlClass);
  }

  private Set<String> getCommentsOn(OWLEntity entity) {
    Set<String> comments = new HashSet<String>();
    for(OWLAnnotation<?> note: entity.getAnnotations(ontology)) {
      if(note instanceof OWLCommentAnnotation && note.isAnnotationByConstant()) {
        comments.add(note.getAnnotationValueAsConstant().getLiteral());
      }
    }
    return comments;
  }

  public void printAxiomsAbout(URI entityURI) {
    int axiomCount = 0;
    System.out.println("Axioms referencing " + entityURI + " in ontology " + ontology.getURI());
    for(OWLAxiom axiom: ontology.getAxioms()) {
      boolean printThisAxiom = false;
      for(OWLEntity entity: axiom.getReferencedEntities()) {
        if(entity.getURI().equals(entityURI)) {
          printThisAxiom = true;
          break;
        }
      }
      if(printThisAxiom) {
        axiomCount++;
        System.out.println(axiomCount + ": " + axiom);
      }
    }
    if(axiomCount == 0) System.out.println("(None)");
  }

  protected abstract void buildOntology();

  private OWLObjectUnionOf buildUnion(URI... classURIs) {
    return factory.getOWLObjectUnionOf(buildClassSet(classURIs));
  }

  private OWLObjectIntersectionOf buildIntersection(URI... classURIs) {
    return factory.getOWLObjectIntersectionOf(buildClassSet(classURIs));
  }

  private Set<URI> buildNamedObjectURISet(OWLNamedObject... owlObjs) {
    Set<URI> uris = new HashSet<URI>();
    for(OWLNamedObject obj: owlObjs) {
      uris.add(obj.getURI());
    }
    return uris;
  }

  protected Set<String> buildURIFragmentSet(Set<URI> uris) {
    Set<String> frags = new HashSet<String>();
    for(URI uri: uris) {
      frags.add(uri.getFragment());
    }
    return frags;
  }

  protected Map<String, URI> buildFragmentURIMap(Set<URI> uris) {
    Map<String, URI> frag2uri = new HashMap<String, URI>();
    for(URI uri: uris) {
      if(uri.getFragment() != null) frag2uri.put(uri.getFragment(), uri);
    }
    return frag2uri;
  }

  private Set<OWLClass> buildClassSet(URI... classURIs) {
    Set<OWLClass> owlClasses = new HashSet<OWLClass>();
    for(URI classURI: classURIs) {
      owlClasses.add(factory.getOWLClass(classURI));
    }
    return owlClasses;
  }

  private Set<OWLClass> buildNamedClassSet(OWLDescription... owlDescs) {
    Set<OWLClass> owlClasses = new HashSet<OWLClass>();
    for(OWLDescription owlDesc: owlDescs) {
      if(!owlDesc.isAnonymous()) owlClasses.add(owlDesc.asOWLClass());
    }
    return owlClasses;
  }

  private Set<OWLObjectProperty> buildObjectPropertySet(URI... propURIs) {
    Set<OWLObjectProperty> owlProps = new HashSet<OWLObjectProperty>();
    for(URI propURI: propURIs) {
      owlProps.add(factory.getOWLObjectProperty(propURI));
    }
    return owlProps;
  }

  private Set<OWLDataProperty> buildDataPropertySet(URI... propURIs) {
    Set<OWLDataProperty> owlProps = new HashSet<OWLDataProperty>();
    for(URI propURI: propURIs) {
      owlProps.add(factory.getOWLDataProperty(propURI));
    }
    return owlProps;
  }

  private Set<OWLIndividual> buildIndividualSet(URI... indURIs) {
    Set<OWLIndividual> owlInds = new HashSet<OWLIndividual>();
    for(URI indURI: indURIs) {
      owlInds.add(factory.getOWLIndividual(indURI));
    }
    return owlInds;
  }

  private Set<OWLTypedConstant> buildConstantSet(Object... values) {
    Set<OWLTypedConstant> consts = new HashSet<OWLTypedConstant>();
    for(Object value: values) {
      consts.add(factory.getOWLTypedConstant(value.toString(),
          factory.getOWLDataType(XSDHelper.getTypeFor(value).getURI())));
    }
    return consts;
  }

  private Set<OWLDescription> buildDescriptionSet(OWLConceptExpression... exprs) {
    Set<OWLDescription> descs = new HashSet<OWLDescription>();
    for(OWLConceptExpression expr: exprs) {
      descs.add(expr.desc);
    }
    return descs;
  }

  private OWLDataRangeRestriction buildDataPropertyRangeRestriction(OWLRestrictedDataRangeFacetVocabulary restrictType,
      XSDVocabulary type, String value) {
    OWLDataType dataType = factory.getOWLDataType(type.getURI());
    OWLTypedConstant constValue;
    switch(restrictType) {
    case LENGTH:
    case MAX_LENGTH:
    case MIN_LENGTH:
    case FRACTION_DIGITS:
    case TOTAL_DIGITS:
      constValue = factory.getOWLTypedConstant(Integer.parseInt(value));
    default:
      constValue = factory.getOWLTypedConstant(value, dataType);
    }
    return factory.getOWLDataRangeRestriction(dataType, restrictType, constValue);
  }

  private void updateOntology() throws OWLOntologyChangeException {
    List<AddAxiom> addAxioms = new LinkedList<AddAxiom>();
    for(OWLAxiom axiom: axioms) {
      if(spp.hasLogicalAxiom(axiom) || !axiom.isLogicalAxiom()) addAxioms.add(new AddAxiom(ontology, axiom));
      else {
        ErrorHandler.warn("Axiom " + axiom + " cannot be expressed in OWL species " + spp, "updating ontology "
          + ontology.getURI(), "this axiom will not be saved");
      }
    }
    axioms.clear();
    manager.applyChanges(addAxioms);
  }

  public void saveOntology(URI physical) throws UnknownOWLOntologyException, OWLOntologyStorageException {
    manager.saveOntology(ontology, new RDFXMLOntologyFormat(), physical);
  }

  static void addArgument(CommandLineArgument arg) {
    argSet.add(arg);
  }

  static Set<CommandLineArgument> getArguments() {
    return new HashSet<CommandLineArgument>(argSet);
  }

  static AbstractOntology createOntology(Class<? extends AbstractOntology> cls) {
    return createOntology(cls, new HashMap<String, String>());
  }

  static AbstractOntology createOntology(Class<? extends AbstractOntology> cls, String[] args) throws UsageException {
    argMap = CommandLineArgument.parseArgs(cls.getName(), argSet, args);
    return createOntology(cls, argMap);
  }

  static AbstractOntology createOntology(Class<? extends AbstractOntology> cls, Map<String, String> optargs) {
    try {
      Constructor<? extends AbstractOntology> cons;
      AbstractOntology ont;
      if(optargs.containsKey("species") && optargs.containsKey("physical") && !optargs.containsKey("inferred")) {
        cons = cls.getConstructor(URI.class, OWLSpecies.class);
        ont = cons.newInstance(URI.create(optargs.get("physical")), OWLSpecies.parseOWLSpecies(optargs.get("species")));
      }
      else if(optargs.containsKey("species")) {
        cons = cls.getConstructor(OWLSpecies.class);
        ont = cons.newInstance(OWLSpecies.parseOWLSpecies(optargs.get("species")));
      }
      else if(optargs.containsKey("physical") && !optargs.containsKey("inferred")) {
        cons = cls.getConstructor(URI.class);
        ont = cons.newInstance(URI.create(optargs.get("physical")));
      }
      else {
        cons = cls.getConstructor();
        ont = cons.newInstance();
      }
      if(optargs.containsKey("inferred")) {
        if(optargs.containsKey("physical")) {
          ont.makeInferencesSave(URI.create(optargs.get("physical")));
        }
        else {
          ont.makeInferences();
        }
      }
      return ont;
    }
    catch(SecurityException e) {
      ErrorHandler.fatal(e, "building ontology " + cls.getName());
      throw new Panic();
    }
    catch(NoSuchMethodException e) {
      ErrorHandler.fatal(e, "building ontology " + cls.getName());
      throw new Panic();
    }
    catch(IllegalArgumentException e) {
      ErrorHandler.fatal(e, "building ontology " + cls.getName());
      throw new Panic();
    }
    catch(InstantiationException e) {
      ErrorHandler.fatal(e, "building ontology " + cls.getName());
      throw new Panic();
    }
    catch(IllegalAccessException e) {
      ErrorHandler.fatal(e, "building ontology " + cls.getName());
      throw new Panic();
    }
    catch(InvocationTargetException e) {
      ErrorHandler.fatal(e, "building ontology " + cls.getName());
      throw new Panic();
    }
    catch(OWLInconsistentOntologyException e) {
      ErrorHandler.fatal(e, "making inferences on ontology " + cls.getName());
      throw new Panic();
    }
    catch(UnknownOWLOntologyException e) {
      ErrorHandler.fatal(e, "making inferences on ontology " + cls.getName());
      throw new Panic();
    }
    catch(OWLOntologyCreationException e) {
      ErrorHandler.fatal(e, "making inferences on ontology " + cls.getName());
      throw new Panic();
    }
    catch(OWLOntologyStorageException e) {
      ErrorHandler.fatal(e, "making inferences on ontology " + cls.getName());
      throw new Panic();
    }
    catch(ClassNotFoundException e) {
      ErrorHandler.fatal(e, "making inferences on ontology " + cls.getName());
      throw new Panic();
    }
    catch(OWLReasonerException e) {
      ErrorHandler.fatal(e, "making inferences on ontology " + cls.getName());
      throw new Panic();
    }
  }
}
