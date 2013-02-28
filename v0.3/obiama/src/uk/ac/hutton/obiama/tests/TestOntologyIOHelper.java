/*
 * uk.ac.hutton.obiama.tests: TestOntologyIOHelper.java
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
package uk.ac.hutton.obiama.tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.io.RDFXMLOntologyFormat;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;

import uk.ac.hutton.obiama.exception.UsageException;
import uk.ac.hutton.obiama.model.Log;
import uk.ac.hutton.obiama.msb.ObiamaSetUp;
import uk.ac.hutton.obiama.msb.OntologyIOHelper;

import junit.framework.TestCase;

/**
 * <!-- TestOntologyIOHelper -->
 * 
 * @author Gary Polhill
 */
public class TestOntologyIOHelper extends TestCase {
  private static final String ontologyBase = "http://www.obiama.org/test/";
  private static final String[] ontologyNames =
    new String[] { "ontology1.owl", "ontology2.owl", "ontology3.owl", "ontology4.owl" };
  private static final String[][] ontologyImports =
    new String[][] { { "http://www.obiama.org/ontology/obiama.owl" }, { "http://www.obiama.org/test/ontology1.owl" },
      { "http://www.obiama.org/test/ontology2.owl" }, { "http://www.obiama.org/test/ontology1.owl" } };
  private static final String[][] ontologyClasses =
    new String[][] { { "#Class1_1", "#Class1_2", "#Class1_3" }, { "#Class2_1", "#Class2_2" },
      { "#Class3_1", "#Class3_2", "#Class3_3", "#Class3_4" }, { "#Class4_1" } };
  private static final String tempdir = "/var/tmp";
  private static final String ontodir = "test";
  private static final String mapfile = "map.csv";
  private static final String logfile = tempdir + "/" + ontodir + "/OBIAMA.log";

  /**
   * @param name
   */
  public TestOntologyIOHelper(String name) {
    super(name);
  }

  /**
   * <!-- setUp -->
   * 
   * @see junit.framework.TestCase#setUp()
   * @throws java.lang.Exception
   */
  protected void setUp() throws Exception {
    super.setUp();
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    OWLDataFactory factory = manager.getOWLDataFactory();
    File temp = new File(tempdir + "/" + ontodir);
    temp.mkdir();
    File map = new File(tempdir + "/" + ontodir + "/" + mapfile);
    PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(map)));
    writer.println("logical,physical");
    for(int i = 0; i < ontologyNames.length; i++) {
      OWLOntology ontology = manager.createOntology(new URI(ontologyBase + ontologyNames[i]));
      Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
      for(int j = 0; j < ontologyImports[i].length; j++) {
        axioms.add(factory.getOWLImportsDeclarationAxiom(ontology, new URI(ontologyImports[i][j])));
      }
      for(int j = 0; j < ontologyClasses[i].length; j++) {
        axioms.add(factory.getOWLDeclarationAxiom(factory
            .getOWLClass(new URI(ontologyNames[i] + ontologyClasses[i][j]))));
      }
      manager.addAxioms(ontology, axioms);
      URI physical = new URI("file:" + temp.getAbsolutePath() + "/" + ontologyNames[i]);
      manager.saveOntology(ontology, new RDFXMLOntologyFormat(), physical);
      writer.println(ontology.getURI() + "," + physical);
    }
    writer.close();
  }

  /**
   * <!-- tearDown -->
   * 
   * @see junit.framework.TestCase#tearDown()
   * @throws java.lang.Exception
   */
  protected void tearDown() throws Exception {
    super.tearDown();
    File temp = new File(tempdir + "/" + ontodir);
    for(File file: temp.listFiles()) {
      file.delete();
    }
    temp.delete();
  }

  private final OntologyIOHelper createIOHelper(OWLOntologyManager manager, String[] args) throws UsageException {
    ObiamaSetUp.getObiamaOptions(this.getName(), args);
    OntologyIOHelper helper = new OntologyIOHelper();
    helper.configure(manager);
    return helper;
  }
  
  private final void printLog() throws IOException {
    File logfile = new File(Log.logfile());
    BufferedReader reader = new BufferedReader(new FileReader(logfile));
    String line;
    while((line = reader.readLine()) != null) {
      System.out.println(line);
    }
  }

  /**
   * Test method for
   * {@link uk.ac.hutton.obiama.msb.OntologyIOHelper#loadOntology(java.net.URI, org.semanticweb.owl.model.OWLOntologyManager)}
   * .
   * 
   * @throws UsageException
   * @throws URISyntaxException
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyCreationException
   */
  public final void testLoadOntology() throws UsageException, URISyntaxException, OWLOntologyCreationException {
    OWLOntologyManager manager1 = OWLManager.createOWLOntologyManager();
    OntologyIOHelper helper1 = createIOHelper(manager1, new String[] { "--log", logfile });
    for(int i = 0; i < ontologyNames.length; i++) {
      try {
        helper1.loadOntology(new URI(ontologyBase + ontologyNames[i]), manager1);
        fail("The helper was able to load ontology " + ontologyNames[i] + " despite having no way to find it");
      }
      catch(OWLOntologyCreationException e) {
        assertTrue(true);
      }
    }
    manager1 = null;
    helper1 = null;
    System.gc();
    ObiamaSetUp.reset();

    OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
    OntologyIOHelper helper2 =
      createIOHelper(manager2, new String[] { "--ontology-search-path", tempdir + "/" + ontodir, "--log", logfile });
    helper2.ignoreFailedImport("http://www.obiama.org/ontology/obiama.owl");
    for(int i = 0; i < ontologyNames.length; i++) {
      OWLOntology ontology = helper2.loadOntology(new URI(ontologyBase + ontologyNames[i]), manager2);

      Set<URI> classes = new HashSet<URI>();
      for(int j = 0; j < ontologyClasses[i].length; j++) {
        URI classURI = URI.create(ontologyBase + ontologyNames[i] + ontologyClasses[i][j]);
        classes.add(classURI);
        if(!ontology.containsClassReference(classURI)) {
          fail("The class " + classURI + " should be in ontology " + ontology.getURI() + " but isn't");
        }
      }

      for(OWLEntity entity: ontology.getReferencedEntities()) {
        if(!classes.contains(entity.getURI())) {
          fail("The entity " + entity.getURI() + " is referenced in ontology " + ontology.getURI()
            + ", but shouldn't be");
        }
        else if(!(entity instanceof OWLClass)) {
          fail("The entity " + entity.getURI() + " in ontology " + ontology.getURI() + " is a "
            + entity.getClass().getName() + " but should be an OWLClass");
        }
        else {
          assertTrue(true);
        }
      }
      manager2.removeOntology(ontology.getURI());
    }
    manager2 = null;
    helper2 = null;
    System.gc();
    ObiamaSetUp.reset();

    OWLOntologyManager manager3 = OWLManager.createOWLOntologyManager();
    OntologyIOHelper helper3 =
      createIOHelper(manager3, new String[] { "--ontology-search-path", tempdir + "/" + ontodir, "--log", logfile });
    for(int i = 0; i < ontologyNames.length; i++) {
      try {
        helper3.loadOntology(new URI(ontologyBase + ontologyNames[i]), manager3);
        fail("Ontology " + ontologyNames[i] + " should not have loaded");
      }
      catch(OWLOntologyCreationException e) {
        assertTrue(true);
      }
    }
    manager3 = null;
    helper3 = null;
    System.gc();
    ObiamaSetUp.reset();
  }

  /**
   * Test method for
   * {@link uk.ac.hutton.obiama.msb.OntologyIOHelper#loadOntologyClosure(java.net.URI, org.semanticweb.owl.model.OWLOntologyManager, java.util.Set)}
   * .
   * 
   * @throws UsageException
   * @throws OWLOntologyCreationException
   * @throws IOException 
   */
  public final void testLoadOntologyClosureURIOWLOntologyManagerSetOfOWLOntology() throws UsageException,
      OWLOntologyCreationException, IOException {
    Map<String, Set<String>> imports = new HashMap<String, Set<String>>();
    for(int i = 0; i < ontologyNames.length; i++) {
      String ontologyName = ontologyBase + ontologyNames[i];
      imports.put(ontologyName, new HashSet<String>(Collections.singleton(ontologyName)));
      for(int j = 0; j < ontologyImports[i].length; j++) {
        imports.get(ontologyName).add(ontologyImports[i][j]);
        if(imports.containsKey(ontologyImports[i][j])) {
          imports.get(ontologyName).addAll(imports.get(ontologyImports[i][j]));
        }
      }

      OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
      OntologyIOHelper helper =
        createIOHelper(manager, new String[] { "--ontology-search-path", tempdir + "/" + ontodir, "--log", logfile });
      helper.ignoreFailedImport("http://www.obiama.org/ontology/obiama.owl");

      Set<OWLOntology> closure = new HashSet<OWLOntology>();
      
      try {
        helper.loadOntologyClosure(URI.create(ontologyName), manager, closure);
      }
      catch(OWLOntologyCreationException e) {
        printLog();
        throw e;
      }
      
      Set<String> testClosure = imports.get(ontologyName);
      Set<String> loadedClosure = new HashSet<String>();
      for(OWLOntology ontology: closure) {
        String uriStr = ontology.getURI().toString();
        loadedClosure.add(uriStr);
        if(!testClosure.contains(uriStr)) {
          fail("Closure of " + ontologyName + " does not contain " + ontology.getURI());
        }
      }
      for(String testStr: testClosure) {
        if(testStr.equals("http://www.obiama.org/ontology/obiama.owl")) {
          if(loadedClosure.contains(testStr)) {
            System.out.println("N.B. Imports closure contains ignored failed import");
          }
          else {
            System.out.println("N.B. Imports closure does not contain ignored failed import");
          }
        }
        else if(!loadedClosure.contains(testStr)) {
          fail("Loaded closure of " + ontologyName + " did not include " + testStr);
        }
      }
            
      manager = null;
      helper = null;
      ObiamaSetUp.reset();
      System.gc();
    }

  }

}
