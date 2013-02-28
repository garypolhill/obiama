/*
 * uk.ac.hutton.obiama.model: FcmpOntology.java
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

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyStorageException;
import org.semanticweb.owl.model.UnknownOWLOntologyException;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.exception.Bug;
import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.OntologyConfigurationException;
import uk.ac.hutton.obiama.exception.UsageException;
import uk.ac.hutton.util.ContractPrecisionFCmp;
import uk.ac.hutton.util.FloatingPointComparison;
import uk.ac.hutton.util.KnuthFCmp;
import uk.ac.hutton.util.LanguageDefaultFCmp;
import uk.ac.hutton.util.OffsetFCmp;
import uk.ac.hutton.util.StringConvertFCmp;
import uk.ac.hutton.util.ToleranceWindowsFCmp;
import uk.ac.hutton.util.ULPFCmp;

/**
 * FcmpOntology
 * 
 * An ontology describing various methods for comparing floating point numbers,
 * and any configuration parameters they take.
 * 
 * @author Gary Polhill
 */
public class FCmpOntology extends AbstractOntology {

  public static final URI ONTOLOGY_URI =
    URI.create("http://www.hutton.ac.uk/obiama/ontologies/FloatingPointComparisons.owl");

  public static final URI FCMP_URI = URI.create(ONTOLOGY_URI + "#FloatingPointComparisonMethod");
  public static final URI JAVA_FCMP_URI = URI.create(ONTOLOGY_URI + "#LanguageDefault");
  public static final URI CONTRACT_PRECISION_FCMP_URI = URI.create(ONTOLOGY_URI + "#ContractPrecision");
  public static final URI TOLERANCE_WINDOWS_FCMP_URI = URI.create(ONTOLOGY_URI + "#ToleranceWindow");
  public static final URI STRING_CONVERT_FCMP_URI = URI.create(ONTOLOGY_URI + "#StringConversion");
  public static final URI OFFSET_FCMP_URI = URI.create(ONTOLOGY_URI + "#Offset");
  public static final URI KNUTH_FCMP_URI = URI.create(ONTOLOGY_URI + "#Knuth");
  public static final URI ULP_FCMP_URI = URI.create(ONTOLOGY_URI + "#ULP");

  public static final URI EPSILON_URI = URI.create(ONTOLOGY_URI + "#epsilon");
  public static final URI SIGNIFICANT_FIGURES_URI = URI.create(ONTOLOGY_URI + "#significantFigures");
  public static final URI OFFSET_URI = URI.create(ONTOLOGY_URI + "#offset");
  public static final URI ULP_URI = URI.create(ONTOLOGY_URI + "#ulp");

  public FCmpOntology() throws OWLOntologyCreationException, OWLOntologyChangeException {
    super(ONTOLOGY_URI);
  }

  public FCmpOntology(URI physical) throws UnknownOWLOntologyException, OWLOntologyCreationException,
      OWLOntologyChangeException, OWLOntologyStorageException {
    super(ONTOLOGY_URI, physical);
  }

  public FCmpOntology(URI logical, URI physical) throws UnknownOWLOntologyException, OWLOntologyCreationException,
      OWLOntologyChangeException, OWLOntologyStorageException {
    super(logical, physical);
  }

  public FCmpOntology(URI logical, URI physical, OWLSpecies spp) throws OWLOntologyCreationException,
      OWLOntologyChangeException, OWLOntologyStorageException {
    super(logical, physical, spp);
  }

  @Override
  protected void buildOntology() {
    disjointClasses(JAVA_FCMP_URI, CONTRACT_PRECISION_FCMP_URI, TOLERANCE_WINDOWS_FCMP_URI, STRING_CONVERT_FCMP_URI,
        OFFSET_FCMP_URI, KNUTH_FCMP_URI, ULP_FCMP_URI);
    subClassOf(JAVA_FCMP_URI, FCMP_URI);
    subClassOf(CONTRACT_PRECISION_FCMP_URI, FCMP_URI);
    subClassOf(TOLERANCE_WINDOWS_FCMP_URI, FCMP_URI);
    subClassOf(STRING_CONVERT_FCMP_URI, FCMP_URI);
    subClassOf(OFFSET_FCMP_URI, FCMP_URI);
    subClassOf(KNUTH_FCMP_URI, FCMP_URI);
    subClassOf(ULP_FCMP_URI, FCMP_URI);

    classComment(FCMP_URI, "This class collects methods for comparing floating point numbers");
    classComment(JAVA_FCMP_URI, "This floating point comparison method simply uses the programming language default");
    classComment(CONTRACT_PRECISION_FCMP_URI, "This floating point comparison method makes comparisons using a lower"
      + " precision floating point datatype than the operands");
    classComment(TOLERANCE_WINDOWS_FCMP_URI, "This floating point comparison method uses a specified tolerance for "
      + "all comparisons");
    classComment(STRING_CONVERT_FCMP_URI, "This floating point comparison converts the floating point numbers into "
      + "strings with a specified number of decimal places and compares the parsed results");
    classComment(OFFSET_FCMP_URI, "This floating point comparison method adds a power of two to both operands "
      + "before making a comparison--it is not recommended");
    classComment(KNUTH_FCMP_URI, "This floating point comparison method implements Donald Knuth's recommendations "
      + "as per p. 233 of the third edition of Volume 2 of The Art of Computer Programming (Seminumerical "
      + "Algorithms)");
    classComment(ULP_FCMP_URI, "This floating point comparison method uses a multiplier of the unit in the last place"
      + " of one of the operands as the tolerance");

    disjointDataProperties(EPSILON_URI, SIGNIFICANT_FIGURES_URI, OFFSET_URI, ULP_URI);
    dataPropertyRange(EPSILON_URI, XSDVocabulary.DOUBLE);
    dataPropertyRange(SIGNIFICANT_FIGURES_URI, XSDVocabulary.INT);
    dataPropertyRange(OFFSET_URI, XSDVocabulary.INT);
    dataPropertyRange(ULP_URI, XSDVocabulary.INT);

    dataPropertyDomain(EPSILON_URI, TOLERANCE_WINDOWS_FCMP_URI, KNUTH_FCMP_URI);
    dataPropertyDomain(SIGNIFICANT_FIGURES_URI, STRING_CONVERT_FCMP_URI);
    dataPropertyDomain(OFFSET_URI, OFFSET_FCMP_URI);
    dataPropertyDomain(ULP_URI, ULP_FCMP_URI);

    dataPropertyFunctional(EPSILON_URI, SIGNIFICANT_FIGURES_URI, OFFSET_URI, ULP_URI);
  }

  public static FloatingPointComparison getFloatingPointComparison(AbstractOntology ontology, URI fcmpURI)
      throws OntologyConfigurationException {
    Set<URI> fcmpClasses = ontology.getAssertedClassesOf(fcmpURI);
    if(fcmpClasses.contains(JAVA_FCMP_URI)) {
      return new LanguageDefaultFCmp();
    }
    else if(fcmpClasses.contains(CONTRACT_PRECISION_FCMP_URI)) {
      return new ContractPrecisionFCmp();
    }
    else if(fcmpClasses.contains(TOLERANCE_WINDOWS_FCMP_URI)) {
      Double epsilon = ontology.getDoubleFunctionalDataPropertyOf(fcmpURI, EPSILON_URI);
      return epsilon == null ? new ToleranceWindowsFCmp() : new ToleranceWindowsFCmp(epsilon);
    }
    else if(fcmpClasses.contains(STRING_CONVERT_FCMP_URI)) {
      Integer sigfig = ontology.getIntegerFunctionalDataPropertyOf(fcmpURI, SIGNIFICANT_FIGURES_URI);
      return sigfig == null ? new StringConvertFCmp() : new StringConvertFCmp(sigfig);
    }
    else if(fcmpClasses.contains(OFFSET_FCMP_URI)) {
      Integer offset = ontology.getIntegerFunctionalDataPropertyOf(fcmpURI, OFFSET_URI);
      return offset == null ? new OffsetFCmp() : new OffsetFCmp(offset);
    }
    else if(fcmpClasses.contains(KNUTH_FCMP_URI)) {
      Double epsilon = ontology.getDoubleFunctionalDataPropertyOf(fcmpURI, EPSILON_URI);
      return epsilon == null ? new KnuthFCmp() : new KnuthFCmp(epsilon);
    }
    else if(fcmpClasses.contains(ULP_FCMP_URI)) {
      Integer multiplier = ontology.getIntegerFunctionalDataPropertyOf(fcmpURI, ULP_URI);
      return multiplier == null ? new ULPFCmp() : new ULPFCmp(multiplier);
    }
    else {
      throw new OntologyConfigurationException(ontology.getURI(), "Individual " + fcmpURI
        + " does not belong to a recognised class of floating-point comparison methods");
    }
  }

  public static FloatingPointComparison getFloatingPointComparison(String fcmpName, Map<String, String> fcmpArgs)
      throws FloatingPointComparisonOntologyException {
    try {
      FCmpOntology ontology = new FCmpOntology();
      Set<URI> fcmpClasses = ontology.getNamedSubClassesOf(FCMP_URI);
      URI fcmpInstance = URI.create(ONTOLOGY_URI + "#instance");
      boolean fcmpNameFound = false;
      for(URI fcmpClass: fcmpClasses) {
        if(fcmpClass.getFragment().equals(fcmpName)) {
          fcmpNameFound = true;
          ontology.individualHasClass(fcmpInstance, fcmpClass);
          Map<String, URI> fcmpProperties =
            ontology.buildFragmentURIMap(ontology.getAssertedDataPropertiesOf(fcmpClass));
          for(String fcmpArg: fcmpArgs.keySet()) {
            if(fcmpProperties.containsKey(fcmpArg)) {
              URI property = fcmpProperties.get(fcmpArg);
              XSDVocabulary type = ontology.getGeneralisedNamedDataRangeOf(property);
              if(type == null) {
                throw ontology.new FloatingPointComparisonOntologyException("Property " + property
                  + " in floating point comparison ontology " + ONTOLOGY_URI
                  + " does not have a simple datatype to initialise with " + fcmpArgs.get(fcmpArg));
              }
              ontology.individualHasDataPropertyValue(fcmpInstance, property, type, fcmpArgs.get(fcmpArg));
            }
            else {
              throw ontology.new FloatingPointComparisonOntologyException("Floating point comparison ontology "
                + ONTOLOGY_URI + " has no property named " + fcmpArg + " for comparison method class " + fcmpClass);
            }
          }
        }
      }
      if(fcmpNameFound) {
        try {
          return getFloatingPointComparison(ontology, fcmpInstance);
        }
        catch(OntologyConfigurationException e) {
          throw new Bug();
        }
      }
      else {
        throw ontology.new FloatingPointComparisonOntologyException("Floating point comparison ontology "
          + ONTOLOGY_URI + " has no floating point comparison method class named " + fcmpName);
      }
    }
    catch(OWLOntologyCreationException e) {
      throw new Bug();
    }
    catch(OWLOntologyChangeException e) {
      throw new Bug();
    }

  }

  /**
   * <!-- main -->
   * 
   * @param args
   */
  public static void main(String[] args) {
    try {
      createOntology(FCmpOntology.class, args);
    }
    catch(UsageException e) {
      ErrorHandler.fatal(e, "parsing command-line arguments");
    }
  }

  public class FloatingPointComparisonOntologyException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 8339665848245662902L;

    FloatingPointComparisonOntologyException(String message) {
      super(message);
    }
  }
}
