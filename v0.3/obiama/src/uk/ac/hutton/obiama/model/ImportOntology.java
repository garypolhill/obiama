/*
 * uk.ac.hutton.obiama.model: ImportOntology.java
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLCommentAnnotation;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLConstantAnnotation;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataComplementOf;
import org.semanticweb.owl.model.OWLDataExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataOneOf;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataRangeFacetRestriction;
import org.semanticweb.owl.model.OWLDataRangeRestriction;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataSubPropertyAxiom;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDeclarationAxiom;
import org.semanticweb.owl.model.OWLDeprecatedClassAxiom;
import org.semanticweb.owl.model.OWLDeprecatedDataPropertyAxiom;
import org.semanticweb.owl.model.OWLDeprecatedObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointUnionAxiom;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owl.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLImportsDeclaration;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLLabelAnnotation;
import org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectAnnotation;
import org.semanticweb.owl.model.OWLObjectComplementOf;
import org.semanticweb.owl.model.OWLObjectExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectIntersectionOf;
import org.semanticweb.owl.model.OWLObjectMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectOneOf;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectSelfRestriction;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLObjectSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectUnionOf;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLOntologyStorageException;
import org.semanticweb.owl.model.OWLQuantifiedRestriction;
import org.semanticweb.owl.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTypedConstant;
import org.semanticweb.owl.model.SWRLRule;
import org.semanticweb.owl.model.UnknownOWLOntologyException;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.UsageException;
import uk.ac.hutton.obiama.msb.XSDHelper;

/**
 * <!-- ImportOntology -->
 * 
 * Take a pre-existing OWL ontology and create a Java class that will create it.
 * The Java class uses AbstractOntology to create the OWL ontology (rather than
 * the OWL API directly), and this means that not all axioms are creatable. If
 * command line options are configured appropriately, this class will save a
 * copy of the original OWL ontology, but as <i>should be</i> created by the
 * Java class this class is creating. So, there are potentially three
 * ontologies: the original ontology A, the ontology created by this class if
 * given a physical URI to save it to B, and the ontology created by the Java
 * class this class creates if compiled and run C. It is quite likely that A
 * will not be the same as B or C, because not all axioms can be realised
 * through method calls to AbstractOntology, but B should be the same as C. (If
 * B and C are different, that would suggest a bug.)
 * 
 * @author Gary Polhill
 */
public class ImportOntology extends AbstractOntology {

  /**
   * URI of the ontology to be created--this will be specified in the OWL file
   */
  public static URI ONTOLOGY_URI;

  /**
   * Name of the Java class to create--specified on the command line
   */
  private String className;

  /**
   * Name of the package to create the Java class in
   */
  private String packageName;

  /**
   * Name of the directory where the Java class is created (not including
   * subdirectories for the full class name)
   */
  private String classDir;

  /**
   * Full name of the Java class
   */
  private String classFullName;

  /**
   * To get the location of the Java file use
   * <code>classDir + System.getProperty("file.separator") + classDirName</code>
   */
  private String classDirName;

  /**
   * Map of named ontological entities to the name of the class variable used to
   * contain their URI
   */
  private Map<OWLEntity, String> namedEntities;

  /**
   * Map of the URI of named ontological entities to the named of the class
   * variable used to contain their URI
   */
  private Map<URI, String> namedURIs;

  /**
   * Set of class variable names used to contain URIs of entities.
   */
  private Set<String> names;

  /**
   * Default constructor
   * 
   * @param logical Logical URI of the ontology
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  public ImportOntology() throws OWLOntologyCreationException, OWLOntologyChangeException {
    super(ONTOLOGY_URI);
  }

  /**
   * Constructor saving a copy of the OWL ontology the created class should
   * create
   * 
   * @param logical Logical URI of the ontology
   * @param physical Physical URI of the OWL ontology the class this class is
   *          creating should create
   * @throws OWLOntologyCreationException
   * @throws UnknownOWLOntologyException
   * @throws OWLOntologyChangeException
   * @throws OWLOntologyStorageException
   */
  public ImportOntology(URI physical) throws OWLOntologyCreationException, UnknownOWLOntologyException,
      OWLOntologyChangeException, OWLOntologyStorageException {
    super(ONTOLOGY_URI, physical);
  }

  /**
   * Constructor for a particular OWL species
   * 
   * @param logical Logical URI of the ontology
   * @param spp OWL sublanguage to use
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  public ImportOntology(OWLSpecies spp) throws OWLOntologyCreationException, OWLOntologyChangeException {
    super(ONTOLOGY_URI, spp);
  }

  /**
   * Constructor for OWL species and physical URI specified
   * 
   * @param logical Logical URI of the ontology
   * @param physical Physical URI of the ontology the class created by this
   *          class should create
   * @param spp OWL sublanguage to use
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   * @throws OWLOntologyStorageException
   */
  public ImportOntology(URI physical, OWLSpecies spp) throws OWLOntologyCreationException, OWLOntologyChangeException,
      OWLOntologyStorageException {
    super(ONTOLOGY_URI, physical, spp);
  }

  /**
   * @param logical
   * @param related
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyChangeException
   */
  public ImportOntology(AbstractOntology related) throws OWLOntologyCreationException, OWLOntologyChangeException {
    super(ONTOLOGY_URI, related);
  }

  /**
   * <!-- getClassName -->
   * 
   * @return The (short) name of the class
   */
  public String getClassName() {
    return className;
  }

  /**
   * <!-- getClassFullName -->
   * 
   * @return The long name of the class
   */
  public String getClassFullName() {
    return classFullName;
  }

  /**
   * <!-- getClassDirName -->
   * 
   * @return The class as a filename, including subdirectories for its package
   */
  public String getClassDirName() {
    return classDirName;
  }

  /**
   * <!-- getClassDir -->
   * 
   * @return The directory in which the class is created
   */
  public String getClassDir() {
    return classDir;
  }

  /**
   * <!-- getClassLocation -->
   * 
   * @return The location of the java class file
   */
  public String getClassLocation() {
    return classDir + System.getProperty("file.separator") + classDirName;
  }

  /**
   * <!-- buildOntology -->
   * 
   * @see uk.ac.hutton.obiama.model.AbstractOntology#buildOntology()
   */
  @Override
  protected void buildOntology() {
    namedEntities = new HashMap<OWLEntity, String>();
    namedURIs = new HashMap<URI, String>();
    names = new HashSet<String>();
    try {
      PrintWriter fp = new PrintWriter(new FileWriter(getJavaFile()));

      OWLOntologyManager imanager = OWLManager.createOWLOntologyManager();
      OWLOntology ontology = null;
      if(argMap.containsKey("ontology.import")) {
        ontology = imanager.loadOntologyFromPhysicalURI(URI.create(argMap.get("ontology.import")));
      }
      else {
        ErrorHandler.fatal(new UsageException("--ontology-import", "argument must be specified", CommandLineArgument
            .usage(getClass().getName(), getArguments())), "getting imported ontology");
      }

      fp.println("/* Class " + className + ":");
      fp.println(" * Created from ontology " + ontology.getURI());
      fp.println(" * located at " + argMap.get("ontology.import"));
      fp.println(" * by " + this.getClass().getSimpleName());
      fp.println(" * on " + new Date(System.currentTimeMillis()));
      fp.println(" */");
      if(packageName != null) {
        fp.println("package " + packageName + ";");
      }
      if(packageName == null || !packageName.equals(AbstractOntology.class.getPackage().getName())) {
        fp.println("import " + AbstractOntology.class.getPackage().getName() + ".AbstractOntology;");
      }
      fp.println("import java.net.URI;");
      fp.println("import org.semanticweb.owl.model.OWLOntologyChangeException;");
      fp.println("import org.semanticweb.owl.model.OWLOntologyCreationException;");
      fp.println("import org.semanticweb.owl.model.OWLOntologyStorageException;");
      fp.println("import org.semanticweb.owl.model.UnknownOWLOntologyException;");
      fp.println("import org.semanticweb.owl.vocab.XSDVocabulary;");
      fp.println("import uk.ac.hutton.obiama.exception.ErrorHandler;");
      fp.println("import uk.ac.hutton.obiama.exception.UsageException;");
      fp.println("public class " + className + " extends AbstractOntology {");

      fp.println("  public static final URI ONTOLOGY_URI = URI.create(\"" + ontology.getURI() + "\");");

      ArrayList<OWLEntity> entities = new ArrayList<OWLEntity>();

      entities.addAll(ontology.getReferencedClasses());
      entities.addAll(ontology.getReferencedObjectProperties());
      entities.addAll(ontology.getReferencedDataProperties());
      entities.addAll(ontology.getReferencedIndividuals());

      Class<? extends OWLEntity> lastClass = null;
      for(OWLEntity entity: entities) {
        if(entity.getClass() != lastClass) {
          fp.println("  // " + entity.getClass().getSimpleName() + "s:");
        }
        URI entityURI = entity.getURI();
        String[] pathFragment = entityURI.toString().split("#");
        if(pathFragment.length > 1 && pathFragment[0].equals(ontology.getURI().toString())) {
          String entityName = convertEntityName(entityURI);
          fp.println("  public static final URI " + entityName + " = URI.create(ONTOLOGY_URI + \"#"
            + entityURI.getFragment() + "\");");
          namedEntities.put(entity, entityName);
          namedURIs.put(entity.getURI(), entityName);
        }
        lastClass = entity.getClass();
      }

      fp.println("  public " + className + "() throws OWLOntologyCreationException, OWLOntologyChangeException {");
      fp.println("    super(ONTOLOGY_URI);");
      fp.println("  }");
      fp.println("  public " + className + "(URI physical)  throws OWLOntologyCreationException,");
      fp.println("      UnknownOWLOntologyException, OWLOntologyChangeException, OWLOntologyStorageException {");
      fp.println("    super(ONTOLOGY_URI, physical);");
      fp.println("  }");
      fp.println("  public " + className + "(OWLSpecies spp) throws OWLOntologyCreationException,");
      fp.println("      OWLOntologyChangeException {");
      fp.println("    super(ONTOLOGY_URI, spp);");
      fp.println("  }");
      fp.println("  public " + className + "(URI physical, OWLSpecies spp) throws OWLOntologyCreationException,");
      fp.println("      OWLOntologyChangeException, OWLOntologyStorageException {");
      fp.println("    super(ONTOLOGY_URI, physical, spp);");
      fp.println("  }");
      fp.println("  public " + className + "(AbstractOntology related) throws OWLOntologyCreationException,");
      fp.println("      OWLOntologyChangeException {");
      fp.println("    super(ONTOLOGY_URI, related);");
      fp.println("  }");

      fp.println("  @Override");
      fp.println("  void buildOntology() {");

      for(OWLAxiom axiom: ontology.getAxioms()) {
        fp.print("    ");
        writeAxiom(fp, axiom);
      }

      fp.println("  }");
      fp.println("  public static void main(String[] args) {");
      fp.println("    try {");
      fp.println("      createOntology(" + className + ".class, args);");
      fp.println("    }");
      fp.println("    catch(UsageException e) {");
      fp.println("      ErrorHandler.fatal(e, \"processing command line arguments\");");
      fp.println("    }");
      fp.println("  }");
      fp.println("}");
      fp.close();
    }
    catch(IOException e) {
      ErrorHandler.fatal(e, "building ontology " + ONTOLOGY_URI + " in Java class " + className);
    }
    catch(OWLOntologyCreationException e) {
      ErrorHandler.fatal(e, "building ontology " + ONTOLOGY_URI + " in Java class " + className);
    }
  }

  /**
   * <!-- writeAxiom -->
   * 
   * Write the code to produce the axiom. Various overloaded methods handle each
   * type of axiom individually.
   * 
   * @param fp Where to write the code to
   * @param axiom The axiom to write the code for
   */
  private void writeAxiom(PrintWriter fp, OWLAxiom axiom) {
    if(axiom instanceof OWLAntiSymmetricObjectPropertyAxiom) {
      writeAxiom(fp, (OWLAntiSymmetricObjectPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLAxiomAnnotationAxiom) {
      writeAxiom(fp, (OWLAxiomAnnotationAxiom)axiom);
    }
    else if(axiom instanceof OWLClassAssertionAxiom) {
      writeAxiom(fp, (OWLClassAssertionAxiom)axiom);
    }
    else if(axiom instanceof OWLDataPropertyAssertionAxiom) {
      writeAxiom(fp, (OWLDataPropertyAssertionAxiom)axiom);
    }
    else if(axiom instanceof OWLDataPropertyDomainAxiom) {
      writeAxiom(fp, (OWLDataPropertyDomainAxiom)axiom);
    }
    else if(axiom instanceof OWLDataPropertyRangeAxiom) {
      writeAxiom(fp, (OWLDataPropertyRangeAxiom)axiom);
    }
    else if(axiom instanceof OWLDataSubPropertyAxiom) {
      writeAxiom(fp, (OWLDataSubPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLDeclarationAxiom) {
      writeAxiom(fp, (OWLDeclarationAxiom)axiom);
    }
    else if(axiom instanceof OWLDeprecatedClassAxiom) {
      writeAxiom(fp, (OWLDeprecatedClassAxiom)axiom);
    }
    else if(axiom instanceof OWLDeprecatedDataPropertyAxiom) {
      writeAxiom(fp, (OWLDeprecatedDataPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLDeprecatedObjectPropertyAxiom) {
      writeAxiom(fp, (OWLDeprecatedObjectPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLDifferentIndividualsAxiom) {
      writeAxiom(fp, (OWLDifferentIndividualsAxiom)axiom);
    }
    else if(axiom instanceof OWLDisjointClassesAxiom) {
      writeAxiom(fp, (OWLDisjointClassesAxiom)axiom);
    }
    else if(axiom instanceof OWLDisjointDataPropertiesAxiom) {
      writeAxiom(fp, (OWLDisjointDataPropertiesAxiom)axiom);
    }
    else if(axiom instanceof OWLDisjointObjectPropertiesAxiom) {
      writeAxiom(fp, (OWLDisjointObjectPropertiesAxiom)axiom);
    }
    else if(axiom instanceof OWLDisjointUnionAxiom) {
      writeAxiom(fp, (OWLDisjointUnionAxiom)axiom);
    }
    else if(axiom instanceof OWLEntityAnnotationAxiom) {
      writeAxiom(fp, (OWLEntityAnnotationAxiom)axiom);
    }
    else if(axiom instanceof OWLEquivalentClassesAxiom) {
      writeAxiom(fp, (OWLEquivalentClassesAxiom)axiom);
    }
    else if(axiom instanceof OWLEquivalentDataPropertiesAxiom) {
      writeAxiom(fp, (OWLEquivalentDataPropertiesAxiom)axiom);
    }
    else if(axiom instanceof OWLEquivalentObjectPropertiesAxiom) {
      writeAxiom(fp, (OWLEquivalentObjectPropertiesAxiom)axiom);
    }
    else if(axiom instanceof OWLFunctionalDataPropertyAxiom) {
      writeAxiom(fp, (OWLFunctionalDataPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLFunctionalObjectPropertyAxiom) {
      writeAxiom(fp, (OWLFunctionalObjectPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLImportsDeclaration) {
      writeAxiom(fp, (OWLImportsDeclaration)axiom);
    }
    else if(axiom instanceof OWLInverseFunctionalObjectPropertyAxiom) {
      writeAxiom(fp, (OWLInverseFunctionalObjectPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLInverseObjectPropertiesAxiom) {
      writeAxiom(fp, (OWLInverseObjectPropertiesAxiom)axiom);
    }
    else if(axiom instanceof OWLIrreflexiveObjectPropertyAxiom) {
      writeAxiom(fp, (OWLIrreflexiveObjectPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLNegativeDataPropertyAssertionAxiom) {
      writeAxiom(fp, (OWLNegativeDataPropertyAssertionAxiom)axiom);
    }
    else if(axiom instanceof OWLNegativeObjectPropertyAssertionAxiom) {
      writeAxiom(fp, (OWLNegativeObjectPropertyAssertionAxiom)axiom);
    }
    else if(axiom instanceof OWLObjectPropertyAssertionAxiom) {
      writeAxiom(fp, (OWLObjectPropertyAssertionAxiom)axiom);
    }
    else if(axiom instanceof OWLObjectPropertyChainSubPropertyAxiom) {
      writeAxiom(fp, (OWLObjectPropertyChainSubPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLObjectPropertyDomainAxiom) {
      writeAxiom(fp, (OWLObjectPropertyDomainAxiom)axiom);
    }
    else if(axiom instanceof OWLObjectPropertyRangeAxiom) {
      writeAxiom(fp, (OWLObjectPropertyRangeAxiom)axiom);
    }
    else if(axiom instanceof OWLObjectSubPropertyAxiom) {
      writeAxiom(fp, (OWLObjectSubPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLOntologyAnnotationAxiom) {
      writeAxiom(fp, (OWLOntologyAnnotationAxiom)axiom);
    }
    else if(axiom instanceof OWLReflexiveObjectPropertyAxiom) {
      writeAxiom(fp, (OWLReflexiveObjectPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLSameIndividualsAxiom) {
      writeAxiom(fp, (OWLSameIndividualsAxiom)axiom);
    }
    else if(axiom instanceof OWLSubClassAxiom) {
      writeAxiom(fp, (OWLSubClassAxiom)axiom);
    }
    else if(axiom instanceof OWLSymmetricObjectPropertyAxiom) {
      writeAxiom(fp, (OWLSymmetricObjectPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLTransitiveObjectPropertyAxiom) {
      writeAxiom(fp, (OWLTransitiveObjectPropertyAxiom)axiom);
    }
    else if(axiom instanceof SWRLRule) {
      writeAxiom(fp, (SWRLRule)axiom);
    }
    else {
      cantDoIt(axiom, "axiom class not recognised " + axiom.getClass().getSimpleName());
    }
  }

  private void writeAxiom(PrintWriter fp, OWLAntiSymmetricObjectPropertyAxiom axiom) {
    OWLObjectPropertyExpression expr = axiom.getProperty();
    if(expr.isAnonymous()) {
      cantDoIt(axiom, "anonymous property expression type " + expr.getClass().getSimpleName());
    }
    else {
      fp.println("objectPropertyAntiSymmetric(" + getURIStr(expr.asOWLObjectProperty()) + ");");
      objectPropertyAntiSymmetric(expr.asOWLObjectProperty().getURI());
    }
  }

  private void writeAxiom(PrintWriter fp, OWLAxiomAnnotationAxiom axiom) {
    cantDoIt(axiom, "axiom annotations");
  }

  private void writeAxiom(PrintWriter fp, OWLClassAssertionAxiom axiom) {
    StringBuffer code = new StringBuffer();
    OWLConceptExpression expr = expression(axiom.getDescription(), code);
    if(expr == null) {
      cantDoIt(axiom, "the class expression");
    }
    else {
      fp.println("individualHasClass(" + getURIStr(axiom.getIndividual()) + ", " + code + ");");
      individualHasClass(axiom.getIndividual().getURI(), expr);
    }
  }

  private void writeAxiom(PrintWriter fp, OWLDataPropertyAssertionAxiom axiom) {
    OWLDataPropertyExpression expr = axiom.getProperty();
    if(expr.isAnonymous()) {
      cantDoIt(axiom, "anonymous property expression type " + expr.getClass().getSimpleName());
    }
    else {
      OWLConstant value = axiom.getObject();
      if(value.isTyped()) {
        fp.println("individualHasDataPropertyValue(" + getURIStr(axiom.getSubject()) + ", "
          + getURIStr(expr.asOWLDataProperty()) + ", XSDVocabulary."
          + XSDHelper.xsdTypes.get(value.asOWLTypedConstant().getDataType().getURI()).name() + ", \""
          + value.getLiteral() + "\");");
        individualHasDataPropertyValue(axiom.getSubject().getURI(), expr.asOWLDataProperty().getURI(),
            XSDHelper.xsdTypes.get(value.asOWLTypedConstant().getDataType().getURI()), value.getLiteral());
      }
      else {
        fp.println("individualHasDataPropertyValue(" + getURIStr(axiom.getSubject()) + ", "
          + getURIStr(expr.asOWLDataProperty()) + ", XSDVocabulary.ANY_TYPE, \"" + value.getLiteral() + "\");");
        individualHasDataPropertyValue(axiom.getSubject().getURI(), expr.asOWLDataProperty().getURI(),
            XSDVocabulary.ANY_TYPE, value.getLiteral());
      }
    }
  }

  private void writeAxiom(PrintWriter fp, OWLDataPropertyDomainAxiom axiom) {
    OWLDataPropertyExpression prop = axiom.getProperty();
    if(prop.isAnonymous()) {
      cantDoIt(axiom, "anonymous property expression type " + prop.getClass().getSimpleName());
      return;
    }
    StringBuffer code = new StringBuffer();
    OWLConceptExpression expr = expression(axiom.getDomain(), code);
    if(expr == null) {
      cantDoIt(axiom, "the class expression");
      return;
    }
    fp.println("dataPropertyDomain(" + getURIStr(prop.asOWLDataProperty()) + ", " + code + ");");
    dataPropertyDomain(prop.asOWLDataProperty().getURI(), expr);
  }

  private void writeAxiom(PrintWriter fp, OWLDataPropertyRangeAxiom axiom) {
    OWLDataPropertyExpression prop = axiom.getProperty();
    if(prop.isAnonymous()) {
      cantDoIt(axiom, "anonymous property expression type " + prop.getClass().getSimpleName());
      return;
    }
    OWLDataRange range = axiom.getRange();
    if(range instanceof OWLDataComplementOf) {
      cantDoIt(axiom, "data range complement of axioms");
    }
    else if(range instanceof OWLDataOneOf) {
      Set<OWLConstant> values = ((OWLDataOneOf)range).getValues();
      Object[] objects = new Object[values.size()];
      int i = 0;
      StringBuffer buff = new StringBuffer("dataPropertyRangeOneOf(" + getURIStr(prop.asOWLDataProperty()));
      for(OWLConstant value: values) {
        if(!value.isTyped()) {
          cantDoIt(axiom, "untyped constants in data range one-of axioms");
          return;
        }
        buff.append(", ");
        objects[i] = XSDHelper.instantiate(value.asOWLTypedConstant().getDataType(), value.getLiteral());
        if(objects[i] instanceof javax.xml.datatype.XMLGregorianCalendar
          || objects[i] instanceof javax.xml.datatype.Duration) {
          buff.append("(new javax.xml.datatype.DatatypeFactory()).new" + objects[i].getClass().getSimpleName() + "(\""
            + value.getLiteral() + "\")");
        }
        else {
          buff.append("new " + objects[i].getClass().getCanonicalName() + "(\"" + value.getLiteral() + "\")");
        }
        i++;
      }
      fp.println(buff + ");");
      dataPropertyRangeOneOf(prop.asOWLDataProperty().getURI(), objects);
    }
    else if(range instanceof OWLDataRangeRestriction) {
      OWLDataRangeRestriction rest = (OWLDataRangeRestriction)range;
      Set<OWLDataRangeFacetRestriction> facet = rest.getFacetRestrictions();
      if(facet.size() > 1) {
        cantDoIt(axiom, "range facet restrictions with more than one restriction");
        return;
      }
      String code = null;
      for(OWLDataRangeFacetRestriction f: facet) {
        OWLTypedConstant c = f.getFacetValue();
        try {
          switch(f.getFacet()) {
          case FRACTION_DIGITS:
            cantDoIt(axiom, "FRACTION_DIGITS range facet restrictions");
            return;
          case LENGTH:
            code =
              new String("dataPropertyRangeLength(" + getURIStr(prop.asOWLDataProperty()) + ", " + c.getLiteral()
                + ");");
            dataPropertyRangeLength(prop.asOWLDataProperty().getURI(), new Integer(c.getLiteral()));
          case MAX_EXCLUSIVE:
            code =
              new String("dataPropertyRangeMaxExclusive(" + getURIStr(prop.asOWLDataProperty()) + ", XSDVocabulary."
                + XSDHelper.xsdTypes.get(c.getDataType().getURI()).name() + c.getLiteral() + ");");
            dataPropertyRangeMaxExclusive(prop.asOWLDataProperty().getURI(), XSDHelper.xsdTypes.get(c.getDataType()
                .getURI()), (Number)XSDHelper.instantiate(c.getDataType(), c.getLiteral()));
          case MAX_INCLUSIVE:
            code =
              new String("dataPropertyRangeMaxInclusive(" + getURIStr(prop.asOWLDataProperty()) + ", XSDVocabulary."
                + XSDHelper.xsdTypes.get(c.getDataType().getURI()).name() + c.getLiteral() + ");");
            dataPropertyRangeMaxInclusive(prop.asOWLDataProperty().getURI(), XSDHelper.xsdTypes.get(c.getDataType()
                .getURI()), (Number)XSDHelper.instantiate(c.getDataType(), c.getLiteral()));
          case MAX_LENGTH:
            code =
              new String("dataPropertyRangeMaxLength(" + getURIStr(prop.asOWLDataProperty()) + ", " + c.getLiteral()
                + ");");
            dataPropertyRangeMaxLength(prop.asOWLDataProperty().getURI(), new Integer(c.getLiteral()));
          case MIN_EXCLUSIVE:
            code =
              new String("dataPropertyRangeMinExclusive(" + getURIStr(prop.asOWLDataProperty()) + ", XSDVocabulary."
                + XSDHelper.xsdTypes.get(c.getDataType().getURI()).name() + c.getLiteral() + ");");
            dataPropertyRangeMinExclusive(prop.asOWLDataProperty().getURI(), XSDHelper.xsdTypes.get(c.getDataType()
                .getURI()), (Number)XSDHelper.instantiate(c.getDataType(), c.getLiteral()));
          case MIN_INCLUSIVE:
            code =
              new String("dataPropertyRangeMinInclusive(" + getURIStr(prop.asOWLDataProperty()) + ", XSDVocabulary."
                + XSDHelper.xsdTypes.get(c.getDataType().getURI()).name() + c.getLiteral() + ")");
            dataPropertyRangeMinInclusive(prop.asOWLDataProperty().getURI(), XSDHelper.xsdTypes.get(c.getDataType()
                .getURI()), (Number)XSDHelper.instantiate(c.getDataType(), c.getLiteral()));
          case MIN_LENGTH:
            code =
              new String("dataPropertyRangeMinLength(" + getURIStr(prop.asOWLDataProperty()) + ", " + c.getLiteral()
                + ");");
            dataPropertyRangeMinLength(prop.asOWLDataProperty().getURI(), new Integer(c.getLiteral()));
          case PATTERN:
            code =
              new String("dataPropertyRangePattern(" + getURIStr(prop.asOWLDataProperty()) + ", \"" + c.getLiteral()
                + "\");");
            dataPropertyRangePattern(prop.asOWLDataProperty().getURI(), c.getLiteral());
          case TOTAL_DIGITS:
            cantDoIt(axiom, "TOTAL_DIGITS range facet restrictions");
            return;
          default:
            cantDoIt(axiom, "unrecognised range facet restriction " + f.getFacet().name());
            return;
          }
        }
        catch(NumberFormatException e) {
          cantDoIt(axiom, f.getFacet().name() + " with value " + c.getLiteral());
          return;
        }
        catch(ClassCastException e) {
          cantDoIt(axiom, f.getFacet().name() + " with value " + c.getLiteral());
          return;
        }
      }
      fp.println(code);
    }
    else if(range instanceof OWLDataType) {
      OWLDataType dataType = (OWLDataType)range;
      XSDVocabulary xsdType = XSDHelper.xsdTypes.get(dataType.getURI());
      if(xsdType != null) {
        fp.println("dataPropertyRange(" + getURIStr(prop.asOWLDataProperty()) + ", XSDVocabulary." + xsdType.name()
          + ");");
        dataPropertyRange(prop.asOWLDataProperty().getURI(), xsdType);
      }
      else {
        cantDoIt(axiom, "unrecognised data type " + dataType.getURI());
      }
    }
    else {
      cantDoIt(axiom, "unrecognised OWLDataRange type " + range.getClass().getSimpleName());
    }
  }

  private void writeAxiom(PrintWriter fp, OWLDataSubPropertyAxiom axiom) {
    if(axiom.getSubProperty().isAnonymous() || axiom.getSuperProperty().isAnonymous()) {
      cantDoIt(axiom, "anonymous property expression type "
        + (axiom.getSubProperty().isAnonymous() ? axiom.getSubProperty().getClass().getSimpleName() : axiom
            .getSuperProperty().getClass().getSimpleName()));
    }
    fp.println("subDataPropertyOf(" + getURIStr(axiom.getSubProperty().asOWLDataProperty()) + ", "
      + getURIStr(axiom.getSuperProperty().asOWLDataProperty()) + ");");
    subDataPropertyOf(axiom.getSubProperty().asOWLDataProperty().getURI(), axiom.getSuperProperty().asOWLDataProperty()
        .getURI());
  }

  private void writeAxiom(PrintWriter fp, OWLDeclarationAxiom axiom) {
    if(axiom.getEntity() instanceof OWLClass) {
      fp.println("declareClass(" + getURIStr(axiom.getEntity()) + ");");
      declareClass(axiom.getEntity().getURI());
    }
    else if(axiom.getEntity() instanceof OWLDataProperty) {
      fp.println("declareDataProperty(" + getURIStr(axiom.getEntity()) + ");");
      declareDataProperty(axiom.getEntity().getURI());
    }
    else if(axiom.getEntity() instanceof OWLObjectProperty) {
      fp.println("declareObjectProperty(" + getURIStr(axiom.getEntity()) + ");");
      declareObjectProperty(axiom.getEntity().getURI());
    }
    else if(axiom.getEntity() instanceof OWLIndividual) {
      fp.println("declareIndividual(" + getURIStr(axiom.getEntity()) + ");");
      declareIndividual(axiom.getEntity().getURI());
    }
    else {
      cantDoIt(axiom, "unknown entity type " + axiom.getEntity().getClass().getSimpleName());
    }
  }

  private void writeAxiom(PrintWriter fp, OWLDeprecatedClassAxiom axiom) {
    cantDoIt(axiom, "deprecated class axioms");
  }

  private void writeAxiom(PrintWriter fp, OWLDeprecatedDataPropertyAxiom axiom) {
    cantDoIt(axiom, "deprecated data property axioms");
  }

  private void writeAxiom(PrintWriter fp, OWLDeprecatedObjectPropertyAxiom axiom) {
    cantDoIt(axiom, "deprecated object property axioms");
  }

  private void writeAxiom(PrintWriter fp, OWLDifferentIndividualsAxiom axiom) {
    Set<OWLIndividual> inds = axiom.getIndividuals();
    URI[] indURIs = new URI[inds.size()];
    int i = 0;
    fp.print("differentIndividuals(");
    for(OWLIndividual ind: inds) {
      indURIs[i] = ind.getURI();
      if(i > 0) fp.print(", ");
      fp.print(getURIStr(ind));
      i++;
    }
    fp.println(");");
    differentIndividuals(indURIs);
  }

  private void writeAxiom(PrintWriter fp, OWLDisjointClassesAxiom axiom) {
    Set<OWLDescription> descs = axiom.getDescriptions();
    OWLConceptExpression[] expr = new OWLConceptExpression[descs.size()];
    StringBuffer buff = new StringBuffer("disjointClasses(");
    int i = 0;
    for(OWLDescription desc: descs) {
      if(i > 0) buff.append(", ");
      expr[i] = expression(desc, buff);
      if(expr[i] == null) {
        cantDoIt(axiom, "concept expression " + desc);
        return;
      }
      i++;
    }
    fp.println(buff + ");");
    disjointClasses(expr);
  }

  private void writeAxiom(PrintWriter fp, OWLDisjointDataPropertiesAxiom axiom) {
    Set<OWLDataPropertyExpression> props = axiom.getProperties();
    URI[] propURIs = new URI[props.size()];
    StringBuffer buff = new StringBuffer("disjointDataProperties(");
    int i = 0;
    for(OWLDataPropertyExpression prop: props) {
      if(prop.isAnonymous()) {
        cantDoIt(axiom, "anonymous property expression type " + prop.getClass().getSimpleName());
        return;
      }
      if(i > 0) buff.append(", ");
      buff.append(getURIStr(prop.asOWLDataProperty()));
      propURIs[i] = prop.asOWLDataProperty().getURI();
      i++;
    }
    fp.println(buff + ");");
    disjointDataProperties(propURIs);
  }

  private void writeAxiom(PrintWriter fp, OWLDisjointObjectPropertiesAxiom axiom) {
    Set<OWLObjectPropertyExpression> props = axiom.getProperties();
    URI[] propURIs = new URI[props.size()];
    StringBuffer buff = new StringBuffer("disjointObjectProperties(");
    int i = 0;
    for(OWLObjectPropertyExpression prop: props) {
      if(prop.isAnonymous()) {
        cantDoIt(axiom, "anonymous property expression type " + prop.getClass().getSimpleName());
        return;
      }
      if(i > 0) buff.append(", ");
      buff.append(getURIStr(prop.asOWLObjectProperty()));
      propURIs[i] = prop.asOWLObjectProperty().getURI();
      i++;
    }
    fp.println(buff + ");");
    disjointObjectProperties(propURIs);
  }

  private void writeAxiom(PrintWriter fp, OWLDisjointUnionAxiom axiom) {
    Set<OWLDescription> descs = axiom.getDescriptions();
    OWLConceptExpression[] exprs = new OWLConceptExpression[descs.size()];
    StringBuffer buff = new StringBuffer("disjointUnion(" + getURIStr(axiom.getOWLClass()));
    int i = 0;
    for(OWLDescription desc: descs) {
      buff.append(", ");
      exprs[i] = expression(desc, buff);
      if(exprs[i] == null) {
        cantDoIt(axiom, "concept expression " + desc);
        return;
      }
      i++;
    }
    fp.println(buff + ");");
    disjointUnion(axiom.getOWLClass().getURI(), exprs);
  }

  private void writeAxiom(PrintWriter fp, OWLEntityAnnotationAxiom axiom) {
    OWLEntity entity = axiom.getSubject();
    if(entity instanceof OWLClass) {
      OWLAnnotation<?> note = axiom.getAnnotation();
      if(note instanceof OWLCommentAnnotation) {
        fp.println("classComment(" + getURIStr(entity) + ", \""
          + ((OWLCommentAnnotation)note).getAnnotationValue().getLiteral() + "\");");
        classComment(entity.getURI(), ((OWLCommentAnnotation)note).getAnnotationValue().getLiteral());
      }
      else if(note instanceof OWLLabelAnnotation) {
        fp.println("classLabel(" + getURIStr(entity) + ", \""
          + ((OWLLabelAnnotation)note).getAnnotationValue().getLiteral() + "\");");
        classLabel(entity.getURI(), ((OWLLabelAnnotation)note).getAnnotationValue().getLiteral());
      }
      else if(note instanceof OWLConstantAnnotation) {
        OWLConstantAnnotation cnote = (OWLConstantAnnotation)note;
        if(!cnote.getAnnotationValue().isTyped()) {
          cantDoIt(axiom, "untyped constant in constant annotation");
        }
        XSDVocabulary type =
          XSDHelper.xsdTypes.get(cnote.getAnnotationValue().asOWLTypedConstant().getDataType().getURI());
        fp.println("classAnnotation(" + getURIStr(entity) + ", " + getURIStr(cnote.getAnnotationURI())
          + ", XSDVocabulary." + type.name() + ", \"" + cnote.getAnnotationValue().getLiteral() + "\");");
        classAnnotation(entity.getURI(), cnote.getAnnotationURI(), type, cnote.getAnnotationValue().getLiteral());
      }
      else if(note instanceof OWLObjectAnnotation) {
        OWLObjectAnnotation onote = (OWLObjectAnnotation)note;
        fp.println("classAnnotation(" + getURIStr(entity) + ", " + getURIStr(onote.getAnnotationURI()) + ", "
          + getURIStr(onote.getAnnotationValue()) + ");");
        classAnnotation(entity.getURI(), onote.getAnnotationURI(), onote.getAnnotationValue().getURI());
      }
      else {
        cantDoIt(axiom, "unknown annotation type " + note.getClass().getSimpleName());
      }
    }
    else if(entity instanceof OWLDataProperty) {
      OWLAnnotation<?> note = axiom.getAnnotation();
      if(note instanceof OWLCommentAnnotation) {
        fp.println("commentDataProperty(" + getURIStr(entity) + ", \""
          + ((OWLCommentAnnotation)note).getAnnotationValue().getLiteral() + "\");");
        commentDataProperty(entity.getURI(), ((OWLCommentAnnotation)note).getAnnotationValue().getLiteral());
      }
      else if(note instanceof OWLLabelAnnotation) {
        fp.println("labelDataProperty(" + getURIStr(entity) + ", \""
          + ((OWLLabelAnnotation)note).getAnnotationValue().getLiteral() + "\");");
        labelDataProperty(entity.getURI(), ((OWLLabelAnnotation)note).getAnnotationValue().getLiteral());
      }
      else if(note instanceof OWLConstantAnnotation) {
        cantDoIt(axiom, "constant annotation of data property");
      }
      else if(note instanceof OWLObjectAnnotation) {
        cantDoIt(axiom, "object annotation of data property");
      }
      else {
        cantDoIt(axiom, "unknown annotation type " + note.getClass().getSimpleName());
      }
    }
    else if(entity instanceof OWLObjectProperty) {
      OWLAnnotation<?> note = axiom.getAnnotation();
      if(note instanceof OWLCommentAnnotation) {
        fp.println("commentObjectProperty(" + getURIStr(entity) + ", \""
          + ((OWLCommentAnnotation)note).getAnnotationValue().getLiteral() + "\");");
        commentObjectProperty(entity.getURI(), ((OWLCommentAnnotation)note).getAnnotationValue().getLiteral());
      }
      else if(note instanceof OWLLabelAnnotation) {
        fp.println("labelObjectProperty(" + getURIStr(entity) + ", \""
          + ((OWLLabelAnnotation)note).getAnnotationValue().getLiteral() + "\");");
        labelObjectProperty(entity.getURI(), ((OWLLabelAnnotation)note).getAnnotationValue().getLiteral());
      }
      else if(note instanceof OWLConstantAnnotation) {
        cantDoIt(axiom, "constant annotation of object property");
      }
      else if(note instanceof OWLObjectAnnotation) {
        cantDoIt(axiom, "object annotation of object property");
      }
      else {
        cantDoIt(axiom, "unknown annotation type " + note.getClass().getSimpleName());
      }
    }
    else if(entity instanceof OWLIndividual) {
      OWLAnnotation<?> note = axiom.getAnnotation();
      if(note instanceof OWLCommentAnnotation) {
        fp.println("commentIndividual(" + getURIStr(entity) + ", \""
          + ((OWLCommentAnnotation)note).getAnnotationValue().getLiteral() + "\");");
        commentIndividual(entity.getURI(), ((OWLCommentAnnotation)note).getAnnotationValue().getLiteral());
      }
      else if(note instanceof OWLLabelAnnotation) {
        fp.println("labelIndividual(" + getURIStr(entity) + ", \""
          + ((OWLLabelAnnotation)note).getAnnotationValue().getLiteral() + "\");");
        labelIndividual(entity.getURI(), ((OWLLabelAnnotation)note).getAnnotationValue().getLiteral());
      }
      else if(note instanceof OWLConstantAnnotation) {
        cantDoIt(axiom, "constant annotation of individual");
      }
      else if(note instanceof OWLObjectAnnotation) {
        cantDoIt(axiom, "object annotation of individual");
      }
      else {
        cantDoIt(axiom, "unknown annotation type " + note.getClass().getSimpleName());
      }
    }
    else {
      cantDoIt(axiom, "annotation of unknown entity type " + entity.getClass().getSimpleName());
    }
  }

  private void writeAxiom(PrintWriter fp, OWLEquivalentClassesAxiom axiom) {
    Set<OWLDescription> descs = axiom.getDescriptions();
    StringBuffer buff = new StringBuffer("equivalentClasses(");
    OWLConceptExpression[] exprs = new OWLConceptExpression[descs.size()];
    int i = 0;
    for(OWLDescription desc: descs) {
      if(i > 0) buff.append(", ");
      exprs[i] = expression(desc, buff);
      if(exprs[i] == null) {
        cantDoIt(axiom, "concept expression " + desc);
        return;
      }
      i++;
    }
    fp.println(buff + ");");
    equivalentClasses(exprs);
  }

  private void writeAxiom(PrintWriter fp, OWLEquivalentDataPropertiesAxiom axiom) {
    Set<OWLDataPropertyExpression> props = axiom.getProperties();
    StringBuffer buff = new StringBuffer("equivalentDataProperties(");
    URI[] propURIs = new URI[props.size()];
    int i = 0;
    for(OWLDataPropertyExpression prop: props) {
      if(prop.isAnonymous()) {
        cantDoIt(axiom, "data property expression " + prop.getClass().getSimpleName());
        return;
      }
      if(i > 0) buff.append(", ");
      propURIs[i] = prop.asOWLDataProperty().getURI();
      buff.append(getURIStr(prop.asOWLDataProperty()));
      i++;
    }
    fp.println(buff + ");");
    equivalentDataProperties(propURIs);
  }

  private void writeAxiom(PrintWriter fp, OWLEquivalentObjectPropertiesAxiom axiom) {
    Set<OWLObjectPropertyExpression> props = axiom.getProperties();
    StringBuffer buff = new StringBuffer("equivalentObjectProperties(");
    URI[] propURIs = new URI[props.size()];
    int i = 0;
    for(OWLObjectPropertyExpression prop: props) {
      if(prop.isAnonymous()) {
        cantDoIt(axiom, "object property expression " + prop);
        return;
      }
      if(i > 0) buff.append(", ");
      propURIs[i] = prop.asOWLObjectProperty().getURI();
      buff.append(getURIStr(prop.asOWLObjectProperty()));
      i++;
    }
    fp.println(buff + ");");
    equivalentObjectProperties(propURIs);
  }

  private void writeAxiom(PrintWriter fp, OWLFunctionalDataPropertyAxiom axiom) {
    if(axiom.getProperty().isAnonymous()) {
      cantDoIt(axiom, "data property expression " + axiom.getProperty());
      return;
    }
    fp.println("dataPropertyFunctional(" + getURIStr(axiom.getProperty().asOWLDataProperty()) + ");");
    dataPropertyFunctional(axiom.getProperty().asOWLDataProperty().getURI());
  }

  private void writeAxiom(PrintWriter fp, OWLFunctionalObjectPropertyAxiom axiom) {
    if(axiom.getProperty().isAnonymous()) {
      cantDoIt(axiom, "object property expression " + axiom.getProperty());
      return;
    }
    fp.println("objectPropertyFunctional(" + getURIStr(axiom.getProperty().asOWLObjectProperty()) + ");");
    objectPropertyFunctional(axiom.getProperty().asOWLObjectProperty().getURI());
  }

  private void writeAxiom(PrintWriter fp, OWLImportsDeclaration axiom) {
    fp.println("imports(" + axiom.getImportedOntologyURI() + ");");
    imports(axiom.getImportedOntologyURI());
  }

  private void writeAxiom(PrintWriter fp, OWLInverseFunctionalObjectPropertyAxiom axiom) {
    if(axiom.getProperty().isAnonymous()) {
      cantDoIt(axiom, "object property expression " + axiom.getProperty());
      return;
    }
    fp.println("objectPropertyInverseFunctional(" + getURIStr(axiom.getProperty().asOWLObjectProperty()) + ");");
    objectPropertyInverseFunctional(axiom.getProperty().asOWLObjectProperty().getURI());
  }

  private void writeAxiom(PrintWriter fp, OWLInverseObjectPropertiesAxiom axiom) {
    if(axiom.getFirstProperty().isAnonymous() || axiom.getSecondProperty().isAnonymous()) {
      cantDoIt(axiom, "object property expression "
        + (axiom.getFirstProperty().isAnonymous() ? axiom.getFirstProperty() : axiom.getSecondProperty()));
      return;
    }
    fp.println("objectPropertyInverse(" + getURIStr(axiom.getFirstProperty().asOWLObjectProperty()) + ", "
      + getURIStr(axiom.getSecondProperty().asOWLObjectProperty()) + ");");
    objectPropertyInverse(axiom.getFirstProperty().asOWLObjectProperty().getURI(), axiom.getSecondProperty()
        .asOWLObjectProperty().getURI());
  }

  private void writeAxiom(PrintWriter fp, OWLIrreflexiveObjectPropertyAxiom axiom) {
    if(axiom.getProperty().isAnonymous()) {
      cantDoIt(axiom, "object property expression " + axiom.getProperty());
      return;
    }
    fp.println("objectPropertyIrreflexive(" + getURIStr(axiom.getProperty().asOWLObjectProperty()) + ");");
    objectPropertyIrreflexive(axiom.getProperty().asOWLObjectProperty().getURI());
  }

  private void writeAxiom(PrintWriter fp, OWLNegativeDataPropertyAssertionAxiom axiom) {
    if(axiom.getProperty().isAnonymous()) {
      cantDoIt(axiom, "data property expression " + axiom.getProperty());
      return;
    }
    OWLConstant value = axiom.getObject();
    if(!value.isTyped()) {
      cantDoIt(axiom, "untyped constant " + value);
      return;
    }
    fp.println("individualHasNotDataPropertyValue(" + getURIStr(axiom.getSubject()) + ", "
      + getURIStr(axiom.getProperty().asOWLDataProperty()) + ", XSDVocabulary."
      + XSDHelper.xsdTypes.get(value.asOWLTypedConstant().getDataType().getURI()).name() + ", \"" + value.getLiteral()
      + "\");");
    individualHasNotDataPropertyValue(axiom.getSubject().getURI(), axiom.getProperty().asOWLDataProperty().getURI(),
        XSDHelper.xsdTypes.get(value.asOWLTypedConstant().getDataType().getURI()), value.getLiteral());
  }

  private void writeAxiom(PrintWriter fp, OWLNegativeObjectPropertyAssertionAxiom axiom) {
    if(axiom.getProperty().isAnonymous()) {
      cantDoIt(axiom, "object property expression " + axiom.getProperty());
      return;
    }
    fp.println("individualHasNotObjectPropertyValue(" + getURIStr(axiom.getSubject()) + ", "
      + getURIStr(axiom.getProperty().asOWLObjectProperty()) + ", " + getURIStr(axiom.getObject()) + ");");
    individualHasNotObjectPropertyValue(axiom.getSubject().getURI(),
        axiom.getProperty().asOWLObjectProperty().getURI(), axiom.getObject().getURI());
  }

  private void writeAxiom(PrintWriter fp, OWLObjectPropertyAssertionAxiom axiom) {
    if(axiom.getProperty().isAnonymous()) {
      cantDoIt(axiom, "object property expression " + axiom.getProperty());
      return;
    }
    fp.println("individualHasObjectPropertyValue(" + getURIStr(axiom.getSubject()) + ", "
      + getURIStr(axiom.getProperty().asOWLObjectProperty()) + ", " + getURIStr(axiom.getObject()) + ");");
    individualHasObjectPropertyValue(axiom.getSubject().getURI(), axiom.getProperty().asOWLObjectProperty().getURI(),
        axiom.getObject().getURI());
  }

  private void writeAxiom(PrintWriter fp, OWLObjectPropertyChainSubPropertyAxiom axiom) {
    List<OWLObjectPropertyExpression> chain = axiom.getPropertyChain();
    OWLObjectPropertyExpression prop = axiom.getSuperProperty();
    if(prop.isAnonymous()) {
      cantDoIt(axiom, "object property expression " + prop);
      return;
    }
    StringBuffer buff = new StringBuffer("superPropertyOfChain(" + getURIStr(prop.asOWLObjectProperty()));
    URI[] chainURIs = new URI[chain.size()];
    int i = 0;
    for(OWLObjectPropertyExpression chainProp: chain) {
      if(chainProp.isAnonymous()) {
        cantDoIt(axiom, "object property expression " + chainProp);
        return;
      }
      buff.append(", " + getURIStr(chainProp.asOWLObjectProperty()));
      chainURIs[i] = chainProp.asOWLObjectProperty().getURI();
    }
    fp.println(buff + ");");
    superPropertyOfChain(prop.asOWLObjectProperty().getURI(), chainURIs);
  }

  private void writeAxiom(PrintWriter fp, OWLObjectPropertyDomainAxiom axiom) {
    if(axiom.getProperty().isAnonymous()) {
      cantDoIt(axiom, "object property expression " + axiom.getProperty());
      return;
    }
    StringBuffer buff =
      new StringBuffer("objectPropertyDomain(" + getURIStr(axiom.getProperty().asOWLObjectProperty()) + ", ");
    OWLConceptExpression expr = expression(axiom.getDomain(), buff);
    if(expr == null) {
      cantDoIt(axiom, "class expression " + axiom.getDomain());
      return;
    }
    fp.println(buff + ");");
    objectPropertyDomain(axiom.getProperty().asOWLObjectProperty().getURI(), expr);
  }

  private void writeAxiom(PrintWriter fp, OWLObjectPropertyRangeAxiom axiom) {
    if(axiom.getProperty().isAnonymous()) {
      cantDoIt(axiom, "object property expression " + axiom.getProperty());
      return;
    }
    StringBuffer buff =
      new StringBuffer("objectPropertyRange(" + getURIStr(axiom.getProperty().asOWLObjectProperty()) + ", ");
    OWLConceptExpression expr = expression(axiom.getRange(), buff);
    if(expr == null) {
      cantDoIt(axiom, "class expression " + axiom.getRange());
      return;
    }
    fp.println(buff + ");");
    objectPropertyRange(axiom.getProperty().asOWLObjectProperty().getURI(), expr);
  }

  private void writeAxiom(PrintWriter fp, OWLObjectSubPropertyAxiom axiom) {
    if(axiom.getSubProperty().isAnonymous() || axiom.getSuperProperty().isAnonymous()) {
      cantDoIt(axiom, "object property expression "
        + (axiom.getSubProperty().isAnonymous() ? axiom.getSubProperty() : axiom.getSuperProperty()));
      return;
    }
    fp.println("subObjectPropertyOf(" + getURIStr(axiom.getSubProperty().asOWLObjectProperty()) + ", "
      + getURIStr(axiom.getSuperProperty().asOWLObjectProperty()) + ");");
    subObjectPropertyOf(axiom.getSubProperty().asOWLObjectProperty().getURI(), axiom.getSuperProperty()
        .asOWLObjectProperty().getURI());
  }

  private void writeAxiom(PrintWriter fp, OWLOntologyAnnotationAxiom axiom) {
    if(axiom.getAnnotation() instanceof OWLCommentAnnotation) {
      fp.println("ontologyComment(\"" + axiom.getAnnotation().getAnnotationValueAsConstant().getLiteral() + "\");");
      ontologyComment(axiom.getAnnotation().getAnnotationValueAsConstant().getLiteral());
    }
    else {
      cantDoIt(axiom, "ontology annotation class " + axiom.getAnnotation().getClass().getSimpleName());
    }
  }

  private void writeAxiom(PrintWriter fp, OWLReflexiveObjectPropertyAxiom axiom) {
    if(axiom.getProperty().isAnonymous()) {
      cantDoIt(axiom, "object property expression " + axiom.getProperty());
      return;
    }
    fp.println("objectPropertyReflexive(" + getURIStr(axiom.getProperty().asOWLObjectProperty()) + ");");
    objectPropertyReflexive(axiom.getProperty().asOWLObjectProperty().getURI());
  }

  private void writeAxiom(PrintWriter fp, OWLSameIndividualsAxiom axiom) {
    URI[] indURIs = new URI[axiom.getIndividuals().size()];
    fp.print("sameIndividuals(");
    int i = 0;
    for(OWLIndividual ind: axiom.getIndividuals()) {
      if(i > 0) fp.print(", ");
      fp.print(getURIStr(ind));
      indURIs[i] = ind.getURI();
    }
    fp.println(");");
    sameIndividuals(indURIs);
  }

  private void writeAxiom(PrintWriter fp, OWLSubClassAxiom axiom) {
    StringBuffer buff = new StringBuffer("subClassOf(");
    OWLConceptExpression subExpr = expression(axiom.getSubClass(), buff);
    if(subExpr == null) {
      cantDoIt(axiom, "concept expression " + axiom.getSubClass());
      return;
    }
    buff.append(", ");
    OWLConceptExpression superExpr = expression(axiom.getSuperClass(), buff);
    if(superExpr == null) {
      cantDoIt(axiom, "concept expression " + axiom.getSuperClass());
      return;
    }
    fp.println(buff + ");");
    subClassOf(subExpr, superExpr);
  }

  private void writeAxiom(PrintWriter fp, OWLSymmetricObjectPropertyAxiom axiom) {
    if(axiom.getProperty().isAnonymous()) {
      cantDoIt(axiom, "object property expression " + axiom.getProperty());
      return;
    }
    fp.println("objectPropertySymmetric(" + getURIStr(axiom.getProperty().asOWLObjectProperty()) + ");");
    objectPropertySymmetric(axiom.getProperty().asOWLObjectProperty().getURI());
  }

  private void writeAxiom(PrintWriter fp, OWLTransitiveObjectPropertyAxiom axiom) {
    if(axiom.getProperty().isAnonymous()) {
      cantDoIt(axiom, "object property expression " + axiom.getProperty());
      return;
    }
    fp.println("objectPropertyTransitive(" + getURIStr(axiom.getProperty().asOWLObjectProperty()) + ");");
    objectPropertyTransitive(axiom.getProperty().asOWLObjectProperty().getURI());
  }

  private void writeAxiom(PrintWriter fp, SWRLRule axiom) {
    cantDoIt(axiom, "SWRL rule axioms");
  }

  /**
   * <!-- expression -->
   * 
   * Create the code to produce an OWLConceptExpression from an OWLDescription.
   * Various overloaded methods handle each OWLDescription type individually.
   * 
   * @param desc The OWLDescription to encode using OWLConceptExpressions
   * @param buff The buffer to which to add the code
   * @return The OWLConceptExpression that the code should produce
   */
  private OWLConceptExpression expression(OWLDescription desc, StringBuffer buff) {
    if(desc instanceof OWLClass) {
      buff.append("namedClass(" + getURIStr(desc.asOWLClass()) + ")");
      return namedClass(desc.asOWLClass().getURI());
    }
    else if(desc instanceof OWLDataAllRestriction) {
      return expression((OWLDataAllRestriction)desc, buff);
    }
    else if(desc instanceof OWLDataExactCardinalityRestriction) {
      return expression((OWLDataExactCardinalityRestriction)desc, buff);
    }
    else if(desc instanceof OWLDataMaxCardinalityRestriction) {
      return expression((OWLDataMaxCardinalityRestriction)desc, buff);
    }
    else if(desc instanceof OWLDataMinCardinalityRestriction) {
      return expression((OWLDataMinCardinalityRestriction)desc, buff);
    }
    else if(desc instanceof OWLDataSomeRestriction) {
      return expression((OWLDataSomeRestriction)desc, buff);
    }
    else if(desc instanceof OWLDataValueRestriction) {
      return expression((OWLDataValueRestriction)desc, buff);
    }
    else if(desc instanceof OWLObjectAllRestriction) {
      return expression((OWLObjectAllRestriction)desc, buff);
    }
    else if(desc instanceof OWLObjectComplementOf) {
      return expression((OWLObjectComplementOf)desc, buff);
    }
    else if(desc instanceof OWLObjectExactCardinalityRestriction) {
      return expression((OWLObjectExactCardinalityRestriction)desc, buff);
    }
    else if(desc instanceof OWLObjectIntersectionOf) {
      return expression((OWLObjectIntersectionOf)desc, buff);
    }
    else if(desc instanceof OWLObjectMinCardinalityRestriction) {
      return expression((OWLObjectMinCardinalityRestriction)desc, buff);
    }
    else if(desc instanceof OWLObjectMaxCardinalityRestriction) {
      return expression((OWLObjectMaxCardinalityRestriction)desc, buff);
    }
    else if(desc instanceof OWLObjectOneOf) {
      return expression((OWLObjectOneOf)desc, buff);
    }
    else if(desc instanceof OWLObjectSelfRestriction) {
      return expression((OWLObjectSelfRestriction)desc, buff);
    }
    else if(desc instanceof OWLObjectSomeRestriction) {
      return expression((OWLObjectSomeRestriction)desc, buff);
    }
    else if(desc instanceof OWLObjectUnionOf) {
      return expression((OWLObjectUnionOf)desc, buff);
    }
    else if(desc instanceof OWLObjectValueRestriction) {
      return expression((OWLObjectValueRestriction)desc, buff);
    }
    else {
      return null;
    }
  }

  private OWLConceptExpression expression(OWLQuantifiedRestriction<OWLDataPropertyExpression, OWLDataRange> desc,
      StringBuffer buff) {
    OWLDataPropertyExpression expr = desc.getProperty();
    if(expr.isAnonymous()) return null;
    OWLDataRange range = desc.getFiller();
    String someAll =
      desc instanceof OWLDataSomeRestriction ? "Some" : (desc instanceof OWLDataAllRestriction ? "All" : null);
    if(someAll == null) throw new Bug();
    if(range instanceof OWLDataComplementOf) {
      return null;
    }
    else if(range instanceof OWLDataOneOf) {
      OWLDataOneOf oneOf = (OWLDataOneOf)range;
      Set<OWLConstant> values = oneOf.getValues();
      Object[] arr = new Object[values.size()];
      buff.append("data" + someAll + "OneOfRestriction(" + getURIStr(expr.asOWLDataProperty()));
      int i = 0;
      for(OWLConstant value: values) {
        buff.append(", ");
        if(value.isTyped()) {
          OWLTypedConstant tvalue = value.asOWLTypedConstant();
          arr[i] = XSDHelper.instantiate(tvalue.getDataType(), tvalue.getLiteral());
          if(arr[i] instanceof javax.xml.datatype.XMLGregorianCalendar || arr[i] instanceof javax.xml.datatype.Duration) {
            buff.append("(new javax.xml.datatype.DatatypeFactory()).new" + arr[i].getClass().getSimpleName() + "(\""
              + tvalue.getLiteral() + "\")");
          }
          else {
            buff.append("new " + arr[i].getClass().getCanonicalName() + "(\"" + tvalue.getLiteral() + "\")");
          }
        }
        else {
          buff.append("\"" + value.getLiteral() + "\"");
          arr[i] = new String(value.getLiteral());
        }
        i++;
      }
      buff.append(")");
      return someAll.equals("All") ? dataAllOneOfRestriction(expr.asOWLDataProperty().getURI(), arr)
                                  : dataSomeOneOfRestriction(expr.asOWLDataProperty().getURI(), arr);
    }
    else if(range instanceof OWLDataRangeRestriction) {
      OWLDataRangeRestriction rest = (OWLDataRangeRestriction)range;
      Set<OWLDataRangeFacetRestriction> facet = rest.getFacetRestrictions();
      if(facet.size() > 1) return null;
      for(OWLDataRangeFacetRestriction f: facet) {
        OWLTypedConstant c = f.getFacetValue();
        try {
          switch(f.getFacet()) {
          case FRACTION_DIGITS:
            return null;
          case LENGTH:
            buff.append("data" + someAll + "AllLengthRestriction(" + getURIStr(expr.asOWLDataProperty()) + ", "
              + c.getLiteral() + ")");
            return someAll.equals("All") ? dataAllLengthRestriction(expr.asOWLDataProperty().getURI(), new Integer(c
                .getLiteral())) : dataSomeLengthRestriction(expr.asOWLDataProperty().getURI(), new Integer(c
                .getLiteral()));
          case MAX_EXCLUSIVE:
            buff.append("data" + someAll + "LTRestriction(" + getURIStr(expr.asOWLDataProperty()) + ", "
              + c.getLiteral() + ")");
            return someAll.equals("All") ? dataAllLTRestriction(expr.asOWLDataProperty().getURI(), (Number)XSDHelper
                .instantiate(c.getDataType(), c.getLiteral())) : dataSomeLTRestriction(expr.asOWLDataProperty()
                .getURI(), (Number)XSDHelper.instantiate(c.getDataType(), c.getLiteral()));
          case MAX_INCLUSIVE:
            buff.append("data" + someAll + "LERestriction(" + getURIStr(expr.asOWLDataProperty()) + ", "
              + c.getLiteral() + ")");
            return someAll.equals("All") ? dataAllLERestriction(expr.asOWLDataProperty().getURI(), (Number)XSDHelper
                .instantiate(c.getDataType(), c.getLiteral())) : dataSomeLERestriction(expr.asOWLDataProperty()
                .getURI(), (Number)XSDHelper.instantiate(c.getDataType(), c.getLiteral()));
          case MAX_LENGTH:
            buff.append("data" + someAll + "MaxLengthRestriction(" + getURIStr(expr.asOWLDataProperty()) + ", "
              + c.getLiteral() + ")");
            return someAll.equals("All") ? dataAllMaxLengthRestriction(expr.asOWLDataProperty().getURI(), new Integer(c
                .getLiteral())) : dataSomeMaxLengthRestriction(expr.asOWLDataProperty().getURI(), new Integer(c
                .getLiteral()));
          case MIN_EXCLUSIVE:
            buff.append("data" + someAll + "GTRestriction(" + getURIStr(expr.asOWLDataProperty()) + ", "
              + c.getLiteral() + ")");
            return someAll.equals("All") ? dataAllGTRestriction(expr.asOWLDataProperty().getURI(), (Number)XSDHelper
                .instantiate(c.getDataType(), c.getLiteral())) : dataSomeGTRestriction(expr.asOWLDataProperty()
                .getURI(), (Number)XSDHelper.instantiate(c.getDataType(), c.getLiteral()));
          case MIN_INCLUSIVE:
            buff.append("data" + someAll + "GERestriction(" + getURIStr(expr.asOWLDataProperty()) + ", "
              + c.getLiteral() + ")");
            return someAll.equals("All") ? dataAllGERestriction(expr.asOWLDataProperty().getURI(), (Number)XSDHelper
                .instantiate(c.getDataType(), c.getLiteral())) : dataSomeGERestriction(expr.asOWLDataProperty()
                .getURI(), (Number)XSDHelper.instantiate(c.getDataType(), c.getLiteral()));
          case MIN_LENGTH:
            buff.append("data" + someAll + "MinLengthRestriction(" + getURIStr(expr.asOWLDataProperty()) + ", "
              + c.getLiteral() + ")");
            return someAll.equals("All") ? dataAllMinLengthRestriction(expr.asOWLDataProperty().getURI(), new Integer(c
                .getLiteral())) : dataSomeMinLengthRestriction(expr.asOWLDataProperty().getURI(), new Integer(c
                .getLiteral()));
          case PATTERN:
            buff.append("data" + someAll + "PatternRestriction(" + getURIStr(expr.asOWLDataProperty()) + ", \""
              + c.getLiteral() + "\")");
            return someAll.equals("All") ? dataAllPatternRestriction(expr.asOWLDataProperty().getURI(), c.getLiteral())
                                        : dataSomePatternRestriction(expr.asOWLDataProperty().getURI(), c.getLiteral());
          case TOTAL_DIGITS:
            return null;
          default:
            return null;
          }
        }
        catch(NumberFormatException e) {
          return null;
        }
        catch(ClassCastException e) {
          return null;
        }
      }
    }
    else if(range instanceof OWLDataType) {
      XSDVocabulary xsdType = XSDHelper.xsdTypes.get(((OWLDataType)range).getURI());
      if(xsdType == null) return null;
      buff.append("dataAllTypeRestriction(" + getURIStr(expr.asOWLDataProperty()) + ", XSDVocabulary." + xsdType.name()
        + ")");
      return dataAllTypeRestriction(expr.asOWLDataProperty().getURI(), xsdType);
    }
    else {
      return null;
    }
    return null;

  }

  private OWLConceptExpression expression(OWLDataAllRestriction desc, StringBuffer buff) {
    return expression((OWLQuantifiedRestriction<OWLDataPropertyExpression, OWLDataRange>)desc, buff);
  }

  private OWLConceptExpression expression(OWLDataExactCardinalityRestriction desc, StringBuffer buff) {
    if(desc.getProperty().isAnonymous()) {
      return null;
    }
    buff.append("dataExactCardinalityRestriction(" + getURIStr(desc.getProperty().asOWLDataProperty()) + ", "
      + desc.getCardinality() + ")");
    return dataExactCardinalityRestriction(desc.getProperty().asOWLDataProperty().getURI(), desc.getCardinality());
  }

  private OWLConceptExpression expression(OWLDataMaxCardinalityRestriction desc, StringBuffer buff) {
    if(desc.getProperty().isAnonymous()) {
      return null;
    }
    buff.append("dataMaxCardinalityRestriction(" + getURIStr(desc.getProperty().asOWLDataProperty()) + ", "
      + desc.getCardinality() + ")");
    return dataMaxCardinalityRestriction(desc.getProperty().asOWLDataProperty().getURI(), desc.getCardinality());
  }

  private OWLConceptExpression expression(OWLDataMinCardinalityRestriction desc, StringBuffer buff) {
    if(desc.getProperty().isAnonymous()) {
      return null;
    }
    buff.append("dataMinCardinalityRestriction(" + getURIStr(desc.getProperty().asOWLDataProperty()) + ", "
      + desc.getCardinality() + ")");
    return dataMinCardinalityRestriction(desc.getProperty().asOWLDataProperty().getURI(), desc.getCardinality());
  }

  private OWLConceptExpression expression(OWLDataSomeRestriction desc, StringBuffer buff) {
    return expression((OWLQuantifiedRestriction<OWLDataPropertyExpression, OWLDataRange>)desc, buff);
  }

  private OWLConceptExpression expression(OWLDataValueRestriction desc, StringBuffer buff) {
    if(desc.getProperty().isAnonymous()) return null;
    OWLConstant value = desc.getValue();
    if(value.isTyped()) {
      buff.append("dataValueRestriction(" + getURIStr(desc.getProperty().asOWLDataProperty()) + ", XSDVocabulary."
        + XSDHelper.xsdTypes.get(value.asOWLTypedConstant().getDataType().getURI()).name() + ", \""
        + value.getLiteral() + "\")");
      return dataValueRestriction(desc.getProperty().asOWLDataProperty().getURI(), XSDHelper.xsdTypes.get(value
          .asOWLTypedConstant().getDataType().getURI()), value.getLiteral());
    }
    else {
      buff.append("dataValueRestriction(" + getURIStr(desc.getProperty().asOWLDataProperty())
        + ", XSDVocabulary.ANY_TYPE, " + value.getLiteral() + ")");
      return dataValueRestriction(desc.getProperty().asOWLDataProperty().getURI(), XSDVocabulary.ANY_TYPE, value
          .getLiteral());
    }
  }

  private OWLConceptExpression expression(OWLObjectAllRestriction desc, StringBuffer buff) {
    if(desc.getProperty().isAnonymous()) return null;
    StringBuffer code = new StringBuffer();
    OWLConceptExpression expr = expression(desc.getFiller(), code);
    if(expr == null) return null;
    buff.append("objectAllRestriction(" + getURIStr(desc.getProperty().asOWLObjectProperty()) + ", " + code + ")");
    return objectAllRestriction(desc.getProperty().asOWLObjectProperty().getURI(), expr);
  }

  private OWLConceptExpression expression(OWLObjectComplementOf desc, StringBuffer buff) {
    StringBuffer code = new StringBuffer();
    OWLConceptExpression expr = expression(desc.getOperand(), code);
    if(expr == null) return null;
    buff.append("objectComplementOf(" + code + ")");
    return objectComplementOf(expr);
  }

  private OWLConceptExpression expression(OWLObjectExactCardinalityRestriction desc, StringBuffer buff) {
    if(desc.getProperty().isAnonymous()) return null;
    buff.append("objectExactCardinalityRestriction(" + getURIStr(desc.getProperty().asOWLObjectProperty()) + ", "
      + desc.getCardinality());
    return objectExactCardinalityRestriction(desc.getProperty().asOWLObjectProperty().getURI(), desc.getCardinality());
  }

  private OWLConceptExpression expression(OWLObjectIntersectionOf desc, StringBuffer buff) {
    buff.append("objectIntersectionOf(");
    OWLConceptExpression[] exprs = new OWLConceptExpression[desc.getOperands().size()];
    int i = 0;
    for(OWLDescription subdesc: desc.getOperands()) {
      if(i > 0) buff.append(", ");
      exprs[i] = expression(subdesc, buff);
      if(exprs[i] == null) return null;
      i++;
    }
    buff.append(")");
    return objectIntersectionOf(exprs);
  }

  private OWLConceptExpression expression(OWLObjectMinCardinalityRestriction desc, StringBuffer buff) {
    if(desc.getProperty().isAnonymous()) return null;
    buff.append("objectMinCardinalityRestriction(" + getURIStr(desc.getProperty().asOWLObjectProperty()) + ", "
      + desc.getCardinality());
    return objectMinCardinalityRestriction(desc.getProperty().asOWLObjectProperty().getURI(), desc.getCardinality());
  }

  private OWLConceptExpression expression(OWLObjectMaxCardinalityRestriction desc, StringBuffer buff) {
    if(desc.getProperty().isAnonymous()) return null;
    buff.append("objectMaxCardinalityRestriction(" + getURIStr(desc.getProperty().asOWLObjectProperty()) + ", "
      + desc.getCardinality());
    return objectMaxCardinalityRestriction(desc.getProperty().asOWLObjectProperty().getURI(), desc.getCardinality());
  }

  private OWLConceptExpression expression(OWLObjectOneOf desc, StringBuffer buff) {
    buff.append("objectOneOf(");
    URI[] individuals = new URI[desc.getIndividuals().size()];
    int i = 0;
    for(OWLIndividual ind: desc.getIndividuals()) {
      if(i > 0) buff.append(", ");
      individuals[i] = ind.getURI();
      buff.append(getURIStr(ind));
    }
    buff.append(")");
    return objectOneOf(individuals);
  }

  private OWLConceptExpression expression(OWLObjectSelfRestriction desc, StringBuffer buff) {
    if(desc.getProperty().isAnonymous()) return null;
    buff.append("objectSelfRestriction(" + getURIStr(desc.getProperty().asOWLObjectProperty()) + ")");
    return objectSelfRestriction(desc.getProperty().asOWLObjectProperty().getURI());
  }

  private OWLConceptExpression expression(OWLObjectSomeRestriction desc, StringBuffer buff) {
    if(desc.getProperty().isAnonymous()) return null;
    StringBuffer code = new StringBuffer();
    OWLConceptExpression expr = expression(desc.getFiller(), code);
    if(code == null) return null;
    buff.append("objectSomeRestriction(" + getURIStr(desc.getProperty().asOWLObjectProperty()) + ", " + code + ")");
    return objectSomeRestriction(desc.getProperty().asOWLObjectProperty().getURI(), expr);
  }

  private OWLConceptExpression expression(OWLObjectUnionOf desc, StringBuffer buff) {
    OWLConceptExpression[] exprs = new OWLConceptExpression[desc.getOperands().size()];
    buff.append("objectUnionOf(");
    int i = 0;
    for(OWLDescription subdesc: desc.getOperands()) {
      if(i > 0) buff.append(", ");
      exprs[i] = expression(subdesc, buff);
      if(exprs[i] == null) return null;
      i++;
    }
    buff.append(")");
    return objectUnionOf(exprs);
  }

  private OWLConceptExpression expression(OWLObjectValueRestriction desc, StringBuffer buff) {
    if(desc.getProperty().isAnonymous()) return null;
    buff.append("objectValueRestriction(" + getURIStr(desc.getProperty().asOWLObjectProperty()) + ", "
      + getURIStr(desc.getValue()) + ")");
    return objectValueRestriction(desc.getProperty().asOWLObjectProperty().getURI(), desc.getValue().getURI());
  }

  /**
   * <!-- getURIStr -->
   * 
   * @param entity An entity in the ontology
   * @return A string to use to obtain the entity's URI in the Java
   *         class--either the name of its corresponding class variable, or an
   *         expression to create the URI inline.
   */
  private String getURIStr(OWLEntity entity) {
    return namedEntities.containsKey(entity) ? namedEntities.get(entity) : "URI.create(\"" + entity.getURI().toString()
      + "\")";
  }

  /**
   * <!-- getURIStr -->
   * 
   * @param entity An entity URI in the ontology
   * @return A string to use to obtain the URI--either one of the variables, or
   *         an expression to create the URI.
   */
  private String getURIStr(URI entity) {
    return namedURIs.containsKey(entity) ? namedURIs.get(entity) : "URI.create(\"" + entity.toString() + "\")";
  }

  /**
   * <!-- cantDoIt -->
   * 
   * Notify the user that an axiom cannot be expressed using AbstractOntology
   * methods
   * 
   * @param axiom The axiom that cannot be expressed
   * @param reason The reason why
   */
  private void cantDoIt(OWLAxiom axiom, String reason) {
    ErrorHandler.note("Import of ontology " + ONTOLOGY_URI + " to " + className + " does not include axiom \"" + axiom
      + "\" as functionality to enable " + reason + " has not been implemented.");
  }

  /**
   * <!-- getJavaFile -->
   * 
   * <p>
   * Return the full path to the Java file to create. If the --java-path command
   * line option is given, the top directory will be as specified there,
   * otherwise the current working directory is used as the top directory. The
   * method then splits the name of the Java class given on the command line
   * with the --java-class argument and creates subdirectories as required to
   * locate the Java file in the proper place.
   * </p>
   * 
   * <p>
   * The method also initialises the classDir, classFullName, and classDirName
   * instance variables for accessor methods.
   * </p>
   * 
   * @return The full path to the Java file to create
   */
  private String getJavaFile() {
    StringBuffer buffer = new StringBuffer();
    if(argMap.containsKey("java.path")) {
      buffer.append(argMap.get("java.path"));
    }
    else {
      buffer.append(System.getProperty("user.dir"));
    }

    File dir = new File(buffer.toString());

    classDir = new String(buffer);

    if(!dir.isDirectory() || !dir.canWrite()) {
      ErrorHandler.fatal(new Exception("Java path " + buffer + " is not a writeable directory"),
          " creating Java class directory");
    }

    if(argMap.containsKey("java.class")) {
      String classNameStr = argMap.get("java.class");

      classFullName = new String(classNameStr);
      if(classFullName.contains(".")) {
        packageName = classFullName.substring(0, classFullName.lastIndexOf("."));
      }
      else {
        packageName = null;
      }

      String[] javaPath = classNameStr.split("\\.");

      classDirName = new String(javaPath[0]);
      for(int i = 1; i < javaPath.length; i++) {
        classDirName += System.getProperty("file.separator");
        classDirName += javaPath[i];
      }
      classDirName += ".java";

      for(int i = 0; i < javaPath.length - 1; i++) {
        buffer.append(System.getProperty("file.separator"));
        buffer.append(javaPath[i]);
        dir = new File(buffer.toString());
        if(!dir.exists()) {
          dir.mkdir();
        }
        else if(!dir.isDirectory() || !dir.canWrite()) {
          ErrorHandler.fatal(new Exception("File " + buffer
            + " in the path to the Java file is not a writeable directory"), " creating Java class directory");
        }
      }
      buffer.append(System.getProperty("file.separator"));
      className = javaPath[javaPath.length - 1];
      buffer.append(className);
      buffer.append(".java");
    }
    else {
      ErrorHandler.fatal(new UsageException("--java-class", "argument must be specified", CommandLineArgument.usage(
          getClass().getName(), getArguments())), "creating Java class name");
    }

    return buffer.toString();
  }

  /**
   * <!-- convertEntityName -->
   * 
   * Convert an entity URI fragment into a variable name to use for that URI.
   * Entity names are assumed to use camel format (e.g. theQuickBrownFox),
   * whereas variable names (which are actually class constants) are in upper
   * case, with underscores to separate words (e.g. THE_QUICK_BROWN_FOX). At the
   * same time, entity names that cannot be used as part of a Java identifier
   * need to be converted to underscores to create valid syntax.
   * 
   * @param name The URI to use for the entity
   * @return The variable name to use for the URI
   */
  private String convertEntityName(URI name) {
    StringBuffer buffer = new StringBuffer();

    boolean prevlower = false;
    boolean first = true;
    for(char c: name.getFragment().toCharArray()) {
      if(first) {
        buffer.append(Character.isJavaIdentifierStart(c) ? Character.toUpperCase(c) : '_');
        first = false;
      }
      else {
        if(Character.isUpperCase(c) && prevlower) {
          buffer.append("_");
        }
        if(!Character.isIdentifierIgnorable(c))
          buffer.append(Character.isJavaIdentifierPart(c) ? Character.toUpperCase(c) : '_');
      }
      prevlower = Character.isLowerCase(c);
    }

    String dupbuffer = new String(buffer);
    int i = 1;
    while(names.contains(dupbuffer.toString() + "_URI")) {
      i++;
      dupbuffer = new String(buffer.toString() + "_" + i);
    }
    dupbuffer += "_URI";

    names.add(dupbuffer);

    return dupbuffer;
  }

  /**
   * <!-- main -->
   * 
   * @param args
   */
  public static void main(String[] args) {
    addArgument(new CommandLineArgument("--ontology-import", "-o", "Ontology (physical) URI",
        "Physical URI of ontology to create code for"));
    addArgument(new CommandLineArgument("--java-path", "-j", "Java file location",
        "Path (i.e. directory) to save java file to"));
    addArgument(new CommandLineArgument("--java-class", "-c", "Java class name", "Name of java class to create"));
    try {
      int ontologyarg = -1;
      for(int i = 0; i < args.length; i++) {
        if(args[i].equals("-o") || args[i].equals("--ontology-import")) {
          ontologyarg = i + 1;
          break;
        }
      }
      if(ontologyarg < 0 || ontologyarg >= args.length) {
        ErrorHandler.fatal(new Exception("You must specify a --ontology-import command-line option with an argument"),
            "determining ontology physical URI to import");
      }
      OWLOntologyManager mymanager = OWLManager.createOWLOntologyManager();
      ONTOLOGY_URI = (mymanager.loadOntologyFromPhysicalURI(URI.create(args[ontologyarg]))).getURI();
      ImportOntology obj = (ImportOntology)createOntology(ImportOntology.class, args);
      String slash = System.getProperty("file.separator");
      String colon = System.getProperty("path.separator");
      System.out.println("Java class " + obj.getClassName() + " source code located in " + obj.getClassLocation());
      System.out.println("To compile this class, do:");
      System.out.println("cd " + obj.getClassDir());
      System.out.println("$JAVA_HOME" + slash + "bin" + slash + "javac -classpath $OWLAPI_BIN_HOME" + slash
        + "owlapi-bin.jar" + colon + "$OBIAMA_BIN_HOME " + obj.getClassDirName());
      System.out.println("To run it, do:");
      System.out.println("$JAVA_HOME" + slash + "bin" + slash + "java -classpath $OWLAPI_BIN_HOME" + slash
        + "owlapi-bin.jar" + colon + "$OBIAMA_BIN_HOME" + colon + ". " + obj.getClassFullName()
        + " --physical file:$ONTOLOGY_PATH");
      System.out.println("where:");
      System.out.println("  JAVA_HOME is where a Java 6 development kit is located");
      System.out.println("  OWLAPI_BIN_HOME is a directory containing owlapi-bin.jar");
      System.out.println("  OBIAMA_BIN_HOME is the top level binary directory for OBIAMA classes or a");
      System.out.println("    full path to obiama.jar");
      System.out.println("  ONTOLOGY_PATH is the full path to where you want the class to save its");
      System.out.println("  version of " + ONTOLOGY_URI);
    }
    catch(UsageException e) {
      ErrorHandler.fatal(e, "parsing commandline arguments");
    }
    catch(OWLOntologyCreationException e) {
      ErrorHandler.fatal(e, "loading imported ontology to get its logical URI");
    }
  }

}
