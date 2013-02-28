/*
 * uk.ac.hutton.obiama.model: OWLSpecies.java
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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
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
import org.semanticweb.owl.model.OWLDataRangeRestriction;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataSubPropertyAxiom;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointUnionAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owl.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
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
import org.semanticweb.owl.model.OWLObjectPropertyInverse;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectSelfRestriction;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLObjectSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectUnionOf;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLPropertyExpression;
import org.semanticweb.owl.model.OWLPropertyRange;
import org.semanticweb.owl.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLRestriction;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owl.model.RemoveAxiom;

import uk.ac.hutton.obiama.exception.Panic;

/**
 * OWLSpecies
 * 
 * Various profiles (species/sublanguages) of OWL and OWL 2. This enumeration
 * can also be used to give some idea of whether a particular axiom, class
 * description or property expression can be used in that species. The
 * enumerations OWL_DL, OWL_Lite and OWL_Full apply to OWL, and OWL_2_DL,
 * OWL_EL, OWL_QL, OWL_RL and OWL_2_Full to OWL 2. The further enumerations
 * OWL_QL_Sub, OWL_QL_Super, OWL_RL_Sub, OWL_RL_Super and OWL_RL_Equiv apply to
 * specific syntactic contexts of the OWL QL and OWL RL sublanguages. They are
 * not recommended for general use, and are included to make coding this
 * enumeration more convenient.
 * 
 * @see http://www.w3.org/TR/owl-ref/
 * @see http://www.w3.org/TR/2008/WD-owl2-profiles-20081202/
 * @see http://www.w3.org/TR/2009/CR-owl2-syntax-20090611/
 * 
 * @author Gary Polhill
 */
public enum OWLSpecies {
  OWL_DL, OWL_Lite, OWL_Full, OWL_2_DL, OWL_EL, OWL_QL, OWL_RL, OWL_2_Full, OWL_QL_Sub, OWL_QL_Super, OWL_RL_Sub,
  OWL_RL_Super, OWL_RL_Equiv;

  private static final String OWL_DL_STR = "OWL-DL";
  private static final String OWL_Lite_STR = "OWL-Lite";
  private static final String OWL_Full_STR = "OWL-Full";
  private static final String OWL_2_DL_STR = "OWL-2 DL";
  private static final String OWL_EL_STR = "OWL-2 EL";
  private static final String OWL_QL_STR = "OWL-2 QL";
  private static final String OWL_RL_STR = "OWL-2 RL";
  private static final String OWL_2_Full_STR = "OWL-2 Full";
  private static final String OWL_QL_Sub_STR = "OWL-2 QL (Subclass)";
  private static final String OWL_QL_Super_STR = "OWL-2 QL (Superclass)";
  private static final String OWL_RL_Sub_STR = "OWL-2 RL (Subclass)";
  private static final String OWL_RL_Super_STR = "OWL-2 RL (Superclass)";
  private static final String OWL_RL_Equiv_STR = "OWL-2 RL (Equivalent)";

  /**
   * <!-- parseOWLSpecies -->
   * 
   * Return the enumeration corresponding to a particular string.
   * 
   * @param str The string to parse
   * @return The enumeration
   */
  public static OWLSpecies parseOWLSpecies(String str) {
    if(str.equals(OWL_DL_STR)) return OWL_DL;
    else if(str.equals(OWL_Lite_STR)) return OWL_Lite;
    else if(str.equals(OWL_Full_STR)) return OWL_Full;
    else if(str.equals(OWL_2_DL_STR)) return OWL_2_DL;
    else if(str.equals(OWL_EL_STR)) return OWL_EL;
    else if(str.equals(OWL_QL_STR)) return OWL_QL;
    else if(str.equals(OWL_RL_STR)) return OWL_RL;
    else if(str.equals(OWL_2_Full_STR)) return OWL_2_Full;
    else if(str.equals(OWL_QL_Sub_STR)) return OWL_QL_Sub;
    else if(str.equals(OWL_QL_Super_STR)) return OWL_QL_Super;
    else if(str.equals(OWL_RL_Sub_STR)) return OWL_RL_Sub;
    else if(str.equals(OWL_RL_Super_STR)) return OWL_RL_Super;
    else if(str.equals(OWL_RL_Equiv_STR)) return OWL_RL_Equiv;
    else
      throw new IllegalArgumentException("OWL species name not recognised: " + str);
  }

  /**
   * <!-- isOWL2 -->
   * 
   * Whether or not the enumeration is OWL 2
   * 
   * @return <code>true</code> if the enumeration is an OWL 2 sublanguage
   */
  public boolean isOWL2() {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return false;
    case OWL_2_DL:
    case OWL_EL:
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  /**
   * <!-- isFull -->
   * 
   * Use this to check whether the species has undecideable reasoning
   *
   * @return <code>true</code> if the species is OWL-Full or OWL 2-Full
   */
  public boolean isFull() {
    switch(this) {
    case OWL_Full:
    case OWL_2_Full:
      return true;
    case OWL_DL:
    case OWL_Lite:
    case OWL_2_DL:
    case OWL_EL:
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return false;
    default:
      throw new Panic();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Enum#toString()
   */
  public String toString() {
    switch(this) {
    case OWL_DL:
      return OWL_DL_STR;
    case OWL_Lite:
      return OWL_Lite_STR;
    case OWL_Full:
      return OWL_Full_STR;
    case OWL_2_DL:
      return OWL_2_DL_STR;
    case OWL_EL:
      return OWL_EL_STR;
    case OWL_QL:
      return OWL_QL_STR;
    case OWL_RL:
      return OWL_RL_STR;
    case OWL_2_Full:
      return OWL_2_Full_STR;
    case OWL_QL_Sub:
      return OWL_QL_Sub_STR;
    case OWL_QL_Super:
      return OWL_QL_Super_STR;
    case OWL_RL_Sub:
      return OWL_RL_Sub_STR;
    case OWL_RL_Super:
      return OWL_RL_Super_STR;
    case OWL_RL_Equiv:
      return OWL_RL_Equiv_STR;
    default:
      throw new Panic();
    }
  }

  /**
   * <!-- hasLogicalAxiom -->
   * 
   * Offer <i>some</i> indication of whether a particular logical axiom is
   * supported by the OWL (sub)language. If this method returns false, it is
   * definitely not supported. If true, there is a possibility that the axiom is
   * still not supported--this depending on other axioms on the ontology, or on
   * information not made available by the OWL API. Deprecated and annotation
   * axioms will return false. The following lists (some of) the undetected
   * conditions that would not satisfy certain sublanguages.
   * 
   * <ul>
   * <li>In OWL Lite, subClassOf axioms must have a named class as the subject.
   * This method will return <code>true</code> if the subject is a named class
   * or restriction.</li>
   * <li>In OWL Lite and OWL DL, transitive properties cannot have cardinality
   * constraints, and neither can their super-properties. This is not checked.</li>
   * <li>In OWL 2 DL, there are restrictions on the use of anonymous individuals
   * in objectPropertyAssertion axioms, which are not checked.</li>
   * <li>In OWL 2 DL, there are restrictions on the use of property chains that
   * are not checked.</li>
   * </ul>
   * 
   * @param axiom The axiom to check
   * @return <code>false</code> if the axiom is not supported, <code>true</code>
   *         if it possibly is.
   */
  public boolean hasLogicalAxiom(OWLAxiom axiom) {
    if(this == OWL_2_Full) return true;
    if(!axiom.isLogicalAxiom()) return false;
    if(axiom instanceof OWLAntiSymmetricObjectPropertyAxiom) {
      return hasLogicalAxiom((OWLAntiSymmetricObjectPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLClassAssertionAxiom) {
      return hasLogicalAxiom((OWLClassAssertionAxiom)axiom);
    }
    else if(axiom instanceof OWLDataPropertyAssertionAxiom) {
      return hasLogicalAxiom((OWLDataPropertyAssertionAxiom)axiom);
    }
    else if(axiom instanceof OWLDataPropertyDomainAxiom) {
      return hasLogicalAxiom((OWLDataPropertyDomainAxiom)axiom);
    }
    else if(axiom instanceof OWLDataPropertyRangeAxiom) {
      return hasLogicalAxiom((OWLDataPropertyRangeAxiom)axiom);
    }
    else if(axiom instanceof OWLDataSubPropertyAxiom) {
      return hasLogicalAxiom((OWLDataSubPropertyAxiom)axiom);
    }
    // Ignoring OWLDeprecated*Axioms
    else if(axiom instanceof OWLDifferentIndividualsAxiom) {
      return hasLogicalAxiom((OWLDifferentIndividualsAxiom)axiom);
    }
    else if(axiom instanceof OWLDisjointClassesAxiom) {
      return hasLogicalAxiom((OWLDisjointClassesAxiom)axiom);
    }
    else if(axiom instanceof OWLDisjointDataPropertiesAxiom) {
      return hasLogicalAxiom((OWLDisjointDataPropertiesAxiom)axiom);
    }
    else if(axiom instanceof OWLDisjointObjectPropertiesAxiom) {
      return hasLogicalAxiom((OWLDisjointObjectPropertiesAxiom)axiom);
    }
    else if(axiom instanceof OWLDisjointUnionAxiom) {
      return hasLogicalAxiom((OWLDisjointUnionAxiom)axiom);
    }
    else if(axiom instanceof OWLEquivalentClassesAxiom) {
      return hasLogicalAxiom((OWLEquivalentClassesAxiom)axiom);
    }
    else if(axiom instanceof OWLEquivalentDataPropertiesAxiom) {
      return hasLogicalAxiom((OWLEquivalentDataPropertiesAxiom)axiom);
    }
    else if(axiom instanceof OWLEquivalentObjectPropertiesAxiom) {
      return hasLogicalAxiom((OWLEquivalentObjectPropertiesAxiom)axiom);
    }
    else if(axiom instanceof OWLFunctionalDataPropertyAxiom) {
      return hasLogicalAxiom((OWLFunctionalDataPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLFunctionalObjectPropertyAxiom) {
      return hasLogicalAxiom((OWLFunctionalObjectPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLInverseFunctionalObjectPropertyAxiom) {
      return hasLogicalAxiom((OWLInverseFunctionalObjectPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLInverseObjectPropertiesAxiom) {
      return hasLogicalAxiom((OWLInverseObjectPropertiesAxiom)axiom);
    }
    else if(axiom instanceof OWLIrreflexiveObjectPropertyAxiom) {
      return hasLogicalAxiom((OWLIrreflexiveObjectPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLNegativeDataPropertyAssertionAxiom) {
      return hasLogicalAxiom((OWLNegativeDataPropertyAssertionAxiom)axiom);
    }
    else if(axiom instanceof OWLNegativeObjectPropertyAssertionAxiom) {
      return hasLogicalAxiom((OWLNegativeObjectPropertyAssertionAxiom)axiom);
    }
    else if(axiom instanceof OWLObjectPropertyAssertionAxiom) {
      return hasLogicalAxiom((OWLObjectPropertyAssertionAxiom)axiom);
    }
    else if(axiom instanceof OWLObjectPropertyChainSubPropertyAxiom) {
      return hasLogicalAxiom((OWLObjectPropertyChainSubPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLObjectPropertyDomainAxiom) {
      return hasLogicalAxiom((OWLObjectPropertyDomainAxiom)axiom);
    }
    else if(axiom instanceof OWLObjectPropertyRangeAxiom) {
      return hasLogicalAxiom((OWLObjectPropertyRangeAxiom)axiom);
    }
    else if(axiom instanceof OWLObjectSubPropertyAxiom) {
      return hasLogicalAxiom((OWLObjectSubPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLReflexiveObjectPropertyAxiom) {
      return hasLogicalAxiom((OWLReflexiveObjectPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLSameIndividualsAxiom) {
      return hasLogicalAxiom((OWLSameIndividualsAxiom)axiom);
    }
    else if(axiom instanceof OWLSubClassAxiom) {
      return hasLogicalAxiom((OWLSubClassAxiom)axiom);
    }
    else if(axiom instanceof OWLSymmetricObjectPropertyAxiom) {
      return hasLogicalAxiom((OWLSymmetricObjectPropertyAxiom)axiom);
    }
    else if(axiom instanceof OWLTransitiveObjectPropertyAxiom) {
      return hasLogicalAxiom((OWLTransitiveObjectPropertyAxiom)axiom);
    }
    // Ignoring SWRLRule
    return false;
  }

  public boolean hasLogicalAxiom(OWLAntiSymmetricObjectPropertyAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return false;
    case OWL_2_DL:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_EL:
      return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_RL:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLClassAssertionAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return hasClassDescription(axiom.getDescription());
    case OWL_2_DL:
      return hasClassDescription(axiom.getDescription());
    case OWL_EL:
      return hasClassDescription(axiom.getDescription());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return !axiom.getDescription().isAnonymous();
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return OWL_RL_Super.hasClassDescription(axiom.getDescription());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLDataPropertyAssertionAxiom axiom) {
    switch(this) {
    case OWL_DL:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_Lite:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_Full:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_2_DL:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_EL:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLDataPropertyDomainAxiom axiom) {
    switch(this) {
    case OWL_DL:
      return hasClassDescription(axiom.getDomain()) && hasPropertyExpression(axiom.getProperty());
    case OWL_Lite:
      return !axiom.getDomain().isAnonymous() && hasClassDescription(axiom.getDomain())
        && hasPropertyExpression(axiom.getProperty());
    case OWL_Full:
      return hasClassDescription(axiom.getDomain()) && hasPropertyExpression(axiom.getProperty());
    case OWL_2_DL:
      return hasClassDescription(axiom.getDomain()) && hasPropertyExpression(axiom.getProperty());
    case OWL_EL:
      return hasClassDescription(axiom.getDomain()) && hasPropertyExpression(axiom.getProperty());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return OWL_QL_Super.hasClassDescription(axiom.getDomain()) && hasPropertyExpression(axiom.getProperty());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return OWL_RL_Super.hasClassDescription(axiom.getDomain()) && hasPropertyExpression(axiom.getProperty());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLDataPropertyRangeAxiom axiom) {
    switch(this) {
    case OWL_DL:
      return hasClassDescription(axiom.getRange()) && hasPropertyExpression(axiom.getProperty());
    case OWL_Lite:
      return axiom.getRange().isDataType() && hasClassDescription(axiom.getRange())
        && hasPropertyExpression(axiom.getProperty());
    case OWL_Full:
      return hasClassDescription(axiom.getRange()) && hasPropertyExpression(axiom.getProperty());
    case OWL_2_DL:
      return hasClassDescription(axiom.getRange()) && hasPropertyExpression(axiom.getProperty());
    case OWL_EL:
      return hasClassDescription(axiom.getRange()) && hasPropertyExpression(axiom.getProperty());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return hasClassDescription(axiom.getRange()) && hasPropertyExpression(axiom.getProperty());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return OWL_RL_Super.hasClassDescription(axiom.getRange()) && hasPropertyExpression(axiom.getProperty());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLDataSubPropertyAxiom axiom) {
    switch(this) {
    case OWL_DL:
      return hasPropertyExpression(axiom.getSubProperty()) && hasPropertyExpression(axiom.getSuperProperty());
    case OWL_Lite:
      return hasPropertyExpression(axiom.getSubProperty()) && hasPropertyExpression(axiom.getSuperProperty());
    case OWL_Full:
      return hasPropertyExpression(axiom.getSubProperty()) && hasPropertyExpression(axiom.getSuperProperty());
    case OWL_2_DL:
      return hasPropertyExpression(axiom.getSubProperty()) && hasPropertyExpression(axiom.getSuperProperty());
    case OWL_EL:
      return hasPropertyExpression(axiom.getSubProperty()) && hasPropertyExpression(axiom.getSuperProperty());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasPropertyExpression(axiom.getSubProperty()) && hasPropertyExpression(axiom.getSuperProperty());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  // Ignoring OWLDeprecated*Axioms

  public boolean hasLogicalAxiom(OWLDifferentIndividualsAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
      return allNamedIndividuals(axiom.getIndividuals());
    case OWL_Full:
      return true;
    case OWL_2_DL:
      return allNamedIndividuals(axiom.getIndividuals());
    case OWL_EL:
      return true;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return true;
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return true;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLDisjointClassesAxiom axiom) {
    switch(this) {
    case OWL_DL:
      return axiom.getDescriptions().size() == 2 && hasClassDescription(axiom.getDescriptions());
    case OWL_Lite:
      return false;
    case OWL_Full:
      return axiom.getDescriptions().size() == 2 && hasClassDescription(axiom.getDescriptions());
    case OWL_2_DL:
      return hasClassDescription(axiom.getDescriptions());
    case OWL_EL:
      return hasClassDescription(axiom.getDescriptions());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return OWL_QL_Sub.hasClassDescription(axiom.getDescriptions());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return OWL_RL_Sub.hasClassDescription(axiom.getDescriptions());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLDisjointDataPropertiesAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return false;
    case OWL_2_DL:
      return hasDataPropertyExpressions(axiom.getProperties());
    case OWL_EL:
      return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return hasDataPropertyExpressions(axiom.getProperties());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasDataPropertyExpressions(axiom.getProperties());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLDisjointObjectPropertiesAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return false;
    case OWL_2_DL:
      return hasObjectPropertyExpressions(axiom.getProperties());
    case OWL_EL:
      return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return hasObjectPropertyExpressions(axiom.getProperties());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasObjectPropertyExpressions(axiom.getProperties());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLDisjointUnionAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      // In fact, the same effect can be achieved with equivalentClass(C,
      // UnionOf(C1 ... Cn)) and disjoint(C1 ... Cn). This is evidently some
      // syntactic sugar introduced in OWL2.
      return false;
    case OWL_2_DL:
      return hasClassDescription(axiom.getDescriptions());
    case OWL_EL:
      return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return false;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLEquivalentClassesAxiom axiom) {
    switch(this) {
    case OWL_DL:
      return axiom.getDescriptions().size() == 2 && hasClassDescription(axiom.getDescriptions());
    case OWL_Lite:
      // In fact in OWL Lite the subject must be a named class, and the object a
      // named class or restriction. However, since Sets are used by the API to
      // return the descriptions, it's not possible to tell.
      return axiom.getDescriptions().size() == 2 && allNamedClassesOrRestrictions(axiom.getDescriptions())
        && hasClassDescription(axiom.getDescriptions());
    case OWL_Full:
      return axiom.getDescriptions().size() == 2 && hasClassDescription(axiom.getDescriptions());
    case OWL_2_DL:
      return hasClassDescription(axiom.getDescriptions());
    case OWL_EL:
      return hasClassDescription(axiom.getDescriptions());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return OWL_QL_Sub.hasClassDescription(axiom.getDescriptions());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return OWL_RL_Equiv.hasClassDescription(axiom.getDescriptions());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLEquivalentDataPropertiesAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return hasDataPropertyExpressions(axiom.getProperties());
    case OWL_2_DL:
      return hasDataPropertyExpressions(axiom.getProperties());
    case OWL_EL:
      return hasDataPropertyExpressions(axiom.getProperties());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return hasDataPropertyExpressions(axiom.getProperties());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasDataPropertyExpressions(axiom.getProperties());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLEquivalentObjectPropertiesAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return hasObjectPropertyExpressions(axiom.getProperties());
    case OWL_2_DL:
      return hasObjectPropertyExpressions(axiom.getProperties());
    case OWL_EL:
      return hasObjectPropertyExpressions(axiom.getProperties());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return hasObjectPropertyExpressions(axiom.getProperties());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasObjectPropertyExpressions(axiom.getProperties());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLFunctionalDataPropertyAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_2_DL:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_EL:
      return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLFunctionalObjectPropertyAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_2_DL:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_EL:
      return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLInverseFunctionalObjectPropertyAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_2_DL:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_EL:
      return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLInverseObjectPropertiesAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return hasObjectPropertyExpressions(axiom.getProperties());
    case OWL_2_DL:
      return hasObjectPropertyExpressions(axiom.getProperties());
    case OWL_EL:
      return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return hasObjectPropertyExpressions(axiom.getProperties());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasObjectPropertyExpressions(axiom.getProperties());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLIrreflexiveObjectPropertyAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return false;
    case OWL_2_DL:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_EL:
      return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLNegativeDataPropertyAssertionAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return false;
    case OWL_2_DL:
      return !axiom.getSubject().isAnonymous() && hasPropertyExpression(axiom.getProperty());
    case OWL_EL:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLNegativeObjectPropertyAssertionAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return false;
    case OWL_2_DL:
      return !axiom.getSubject().isAnonymous() && hasPropertyExpression(axiom.getProperty());
    case OWL_EL:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLObjectPropertyAssertionAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_2_DL:
      // There are restrictions on the use of anonymous individuals in object
      // property assertions, but these cannot be checked without reference to
      // the rest of the ontology.
      return hasPropertyExpression(axiom.getProperty());
    case OWL_EL:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLObjectPropertyChainSubPropertyAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return false;
    case OWL_2_DL:
      // There are restrictions on the use of property chains, but these cannot
      // be checked without reference to the rest of the ontology
      return hasPropertyExpression(axiom.getSuperProperty())
        && hasObjectPropertyExpressions(new HashSet<OWLObjectPropertyExpression>(axiom.getPropertyChain()));
    case OWL_EL:
      return hasPropertyExpression(axiom.getSuperProperty())
        && hasObjectPropertyExpressions(new HashSet<OWLObjectPropertyExpression>(axiom.getPropertyChain()));
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasPropertyExpression(axiom.getSuperProperty())
        && hasObjectPropertyExpressions(new HashSet<OWLObjectPropertyExpression>(axiom.getPropertyChain()));
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLObjectPropertyDomainAxiom axiom) {
    switch(this) {
    case OWL_DL:
      return hasClassDescription(axiom.getDomain()) && hasPropertyExpression(axiom.getProperty());
    case OWL_Lite:
      return !axiom.getDomain().isAnonymous() && hasClassDescription(axiom.getDomain())
        && hasPropertyExpression(axiom.getProperty());
    case OWL_Full:
      return hasClassDescription(axiom.getDomain()) && hasPropertyExpression(axiom.getProperty());
    case OWL_2_DL:
      return hasClassDescription(axiom.getDomain()) && hasPropertyExpression(axiom.getProperty());
    case OWL_EL:
      return hasClassDescription(axiom.getDomain()) && hasPropertyExpression(axiom.getProperty());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return OWL_QL_Super.hasClassDescription(axiom.getDomain()) && hasPropertyExpression(axiom.getProperty());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return OWL_RL_Super.hasClassDescription(axiom.getDomain()) && hasPropertyExpression(axiom.getProperty());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLObjectPropertyRangeAxiom axiom) {
    switch(this) {
    case OWL_DL:
      return hasClassDescription(axiom.getRange()) && hasPropertyExpression(axiom.getProperty());
    case OWL_Lite:
      return !axiom.getRange().isAnonymous() && hasClassDescription(axiom.getRange())
        && hasPropertyExpression(axiom.getProperty());
    case OWL_Full:
      return hasClassDescription(axiom.getRange()) && hasPropertyExpression(axiom.getProperty());
    case OWL_2_DL:
      return hasClassDescription(axiom.getRange()) && hasPropertyExpression(axiom.getProperty());
    case OWL_EL:
      return hasClassDescription(axiom.getRange()) && hasPropertyExpression(axiom.getProperty());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return OWL_QL_Super.hasClassDescription(axiom.getRange()) && hasPropertyExpression(axiom.getProperty());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return OWL_RL_Super.hasClassDescription(axiom.getRange()) && hasPropertyExpression(axiom.getProperty());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLObjectSubPropertyAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return hasPropertyExpression(axiom.getSubProperty()) && hasPropertyExpression(axiom.getSuperProperty());
    case OWL_2_DL:
      return hasPropertyExpression(axiom.getSubProperty()) && hasPropertyExpression(axiom.getSuperProperty());
    case OWL_EL:
      return hasPropertyExpression(axiom.getSubProperty()) && hasPropertyExpression(axiom.getSuperProperty());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return hasPropertyExpression(axiom.getSubProperty()) && hasPropertyExpression(axiom.getSuperProperty());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasPropertyExpression(axiom.getSubProperty()) && hasPropertyExpression(axiom.getSuperProperty());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLReflexiveObjectPropertyAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return false;
    case OWL_2_DL:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_EL:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return false;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLSameIndividualsAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
      return axiom.getIndividuals().size() <= 2 && allNamedIndividuals(axiom.getIndividuals());
    case OWL_Full:
      return axiom.getIndividuals().size() <= 2;
    case OWL_2_DL:
      return allNamedIndividuals(axiom.getIndividuals());
    case OWL_EL:
      return true;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return true;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLSubClassAxiom axiom) {
    switch(this) {
    case OWL_DL:
      return hasClassDescription(axiom.getDescriptions());
    case OWL_Lite:
      return !axiom.getSubClass().isAnonymous()
        && allNamedClassesOrRestrictions(Collections.singleton(axiom.getSuperClass()));
    case OWL_Full:
      return hasClassDescription(axiom.getDescriptions());
    case OWL_2_DL:
      return hasClassDescription(axiom.getDescriptions());
    case OWL_EL:
      return hasClassDescription(axiom.getDescriptions());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      // Restrictions on sub and super-classes differ
      return OWL_QL_Sub.hasClassDescription(axiom.getSubClass())
        && OWL_QL_Super.hasClassDescription(axiom.getSuperClass());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      // Restrictions on sub and super-classes differ
      return OWL_RL_Sub.hasClassDescription(axiom.getSubClass())
        && OWL_RL_Super.hasClassDescription(axiom.getSuperClass());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLSymmetricObjectPropertyAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_2_DL:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_EL:
      return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasLogicalAxiom(OWLTransitiveObjectPropertyAxiom axiom) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
      // In OWL DL and OWL Lite, transitive properties cannot have cardinality
      // constraints. I thought they also could not be symmetric, but the OWL
      // reference guide doesn't say so.
      return hasPropertyExpression(axiom.getProperty());
    case OWL_Full:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_2_DL:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_EL:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasPropertyExpression(axiom.getProperty());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  /**
   * <!-- hasClassDescription -->
   * 
   * Check whether a class description conforms to the constraints imposed by a
   * particular species of OWL. If the method returns <code>false</code>, the
   * description is definitely supported, but in some cases, the method may
   * return <code>true</code> for a class description that would not be
   * supported by the sublanguage given other axioms in the ontology.
   * 
   * @param desc The class description
   * @return Whether or not the description conforms to the sublanguage of OWL
   */
  public boolean hasClassDescription(OWLPropertyRange desc) {
    if(desc instanceof OWLClass) return true;
    else if(desc instanceof OWLDataAllRestriction) {
      return hasClassDescription((OWLDataAllRestriction)desc);
    }
    else if(desc instanceof OWLDataComplementOf) {
      return hasClassDescription((OWLDataComplementOf)desc);
    }
    else if(desc instanceof OWLDataExactCardinalityRestriction) {
      return hasClassDescription((OWLDataExactCardinalityRestriction)desc);
    }
    else if(desc instanceof OWLDataMaxCardinalityRestriction) {
      return hasClassDescription((OWLDataMaxCardinalityRestriction)desc);
    }
    else if(desc instanceof OWLDataMinCardinalityRestriction) {
      return hasClassDescription((OWLDataMinCardinalityRestriction)desc);
    }
    else if(desc instanceof OWLDataOneOf) {
      return hasClassDescription((OWLDataOneOf)desc);
    }
    else if(desc instanceof OWLDataRangeRestriction) {
      return hasClassDescription((OWLDataRangeRestriction)desc);
    }
    else if(desc instanceof OWLDataSomeRestriction) {
      return hasClassDescription((OWLDataSomeRestriction)desc);
    }
    else if(desc instanceof OWLDataType) {
      return hasClassDescription((OWLDataType)desc);
    }
    else if(desc instanceof OWLDataValueRestriction) {
      return hasClassDescription((OWLDataValueRestriction)desc);
    }
    else if(desc instanceof OWLObjectAllRestriction) {
      return hasClassDescription((OWLObjectAllRestriction)desc);
    }
    else if(desc instanceof OWLObjectComplementOf) {
      return hasClassDescription((OWLObjectComplementOf)desc);
    }
    else if(desc instanceof OWLObjectExactCardinalityRestriction) {
      return hasClassDescription((OWLObjectExactCardinalityRestriction)desc);
    }
    else if(desc instanceof OWLObjectIntersectionOf) {
      return hasClassDescription((OWLObjectIntersectionOf)desc);
    }
    else if(desc instanceof OWLObjectMaxCardinalityRestriction) {
      return hasClassDescription((OWLObjectMaxCardinalityRestriction)desc);
    }
    else if(desc instanceof OWLObjectMinCardinalityRestriction) {
      return hasClassDescription((OWLObjectMinCardinalityRestriction)desc);
    }
    else if(desc instanceof OWLObjectOneOf) {
      return hasClassDescription((OWLObjectOneOf)desc);
    }
    else if(desc instanceof OWLObjectSelfRestriction) {
      return hasClassDescription((OWLObjectSelfRestriction)desc);
    }
    else if(desc instanceof OWLObjectSomeRestriction) {
      return hasClassDescription((OWLObjectSomeRestriction)desc);
    }
    else if(desc instanceof OWLObjectUnionOf) {
      return hasClassDescription((OWLObjectUnionOf)desc);
    }
    else if(desc instanceof OWLObjectValueRestriction) {
      return hasClassDescription((OWLObjectValueRestriction)desc);
    }
    return false;
  }

  private boolean hasClassDescription(Set<OWLDescription> subdescs) {
    for(OWLDescription subdesc: subdescs) {
      if(!hasClassDescription(subdesc)) return false;
    }
    return true;
  }

  public boolean hasClassDescription(OWLDataAllRestriction desc) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return hasPropertyExpression(desc.getProperty()) && hasClassDescription(desc.getFiller());
    case OWL_2_DL:
      return hasPropertyExpression(desc.getProperty()) && hasClassDescription(desc.getFiller());
    case OWL_EL:
      return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
      return hasPropertyExpression(desc.getProperty()) && hasClassDescription(desc.getFiller());
    case OWL_RL_Sub:
      return false;
    case OWL_RL_Super:
      return hasPropertyExpression(desc.getProperty()) && hasClassDescription(desc.getFiller());
    case OWL_RL_Equiv:
      return false;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLDataComplementOf desc) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return false;
    case OWL_2_DL:
      return true;
    case OWL_EL:
      return true;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return false;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLDataExactCardinalityRestriction desc) {
    switch(this) {
    case OWL_DL:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_Lite:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty())
        && desc.getCardinality() == 0 || desc.getCardinality() == 1;
    case OWL_Full:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_2_DL:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_EL:
      return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
    case OWL_RL_Sub:
      return false;
    case OWL_RL_Super:
      return false;
    case OWL_RL_Equiv:
      return false;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLDataMaxCardinalityRestriction desc) {
    switch(this) {
    case OWL_DL:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_Lite:
      return (desc.getCardinality() == 0 || desc.getCardinality() == 1) && hasClassDescription(desc.getFiller())
        && hasPropertyExpression(desc.getProperty());
    case OWL_Full:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_2_DL:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_EL:
      return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
      return (desc.getCardinality() == 0 || desc.getCardinality() == 1) && hasPropertyExpression(desc.getProperty())
        && hasClassDescription(desc.getFiller());
    case OWL_RL_Sub:
      return false;
    case OWL_RL_Super:
      return (desc.getCardinality() == 0 || desc.getCardinality() == 1) && hasPropertyExpression(desc.getProperty())
        && hasClassDescription(desc.getFiller());
    case OWL_RL_Equiv:
      return false;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLDataMinCardinalityRestriction desc) {
    switch(this) {
    case OWL_DL:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_Lite:
      return (desc.getCardinality() == 0 || desc.getCardinality() == 1) && hasClassDescription(desc.getFiller())
        && hasPropertyExpression(desc.getProperty());
    case OWL_Full:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_2_DL:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_EL:
      return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return false;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLDataOneOf desc) {
    switch(this) {
    case OWL_DL:
      return true;
    case OWL_Lite:
      return false;
    case OWL_Full:
      return true;
    case OWL_2_DL:
      return true;
    case OWL_EL:
      if(desc.getValues().size() == 1) return true;
      else
        return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return false;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLDataRangeRestriction desc) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return false;
    case OWL_2_DL:
      return hasClassDescription(desc.getDataRange());
    case OWL_EL:
      return hasClassDescription(desc.getDataRange());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return hasClassDescription(desc.getDataRange());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasClassDescription(desc.getDataRange());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLDataSomeRestriction desc) {
    switch(this) {
    case OWL_DL:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_Lite:
      return desc.getFiller().isDataType() && hasPropertyExpression(desc.getProperty());
    case OWL_Full:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_2_DL:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_EL:
      return true;
    case OWL_QL:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_QL_Sub:
      return false;
    case OWL_QL_Super:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_RL:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_RL_Sub:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_RL_Super:
      return false;
    case OWL_RL_Equiv:
      return false;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLDataType desc) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return true;
    case OWL_2_DL:
      return true;
    case OWL_EL:
      return true;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return true;
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return true;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLDataValueRestriction desc) {
    switch(this) {
    case OWL_DL:
      return true;
    case OWL_Lite:
      return false;
    case OWL_Full:
      return true;
    case OWL_2_DL:
      return true;
    case OWL_EL:
      return true;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
    case OWL_RL_Sub:
      return true;
    case OWL_RL_Super:
      return true;
    case OWL_RL_Equiv:
      return true;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLObjectAllRestriction desc) {
    switch(this) {
    case OWL_DL:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_Lite:
      return !desc.getFiller().isAnonymous() && hasPropertyExpression(desc.getProperty());
    case OWL_Full:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_2_DL:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_EL:
      return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_RL_Sub:
      return false;
    case OWL_RL_Super:
      return hasClassDescription(desc.getFiller()) && hasPropertyExpression(desc.getProperty());
    case OWL_RL_Equiv:
      return false;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLObjectComplementOf desc) {
    switch(this) {
    case OWL_DL:
      return hasClassDescription(desc.getOperand());
    case OWL_Lite:
      return false;
    case OWL_Full:
      return hasClassDescription(desc.getOperand());
    case OWL_2_DL:
      return hasClassDescription(desc.getOperand());
    case OWL_EL:
      return false;
    case OWL_QL:
      return OWL_QL_Sub.hasClassDescription(desc.getOperand());
    case OWL_QL_Sub:
      return false;
    case OWL_QL_Super:
      return OWL_QL_Sub.hasClassDescription(desc.getOperand());
    case OWL_RL:
      return OWL_RL_Sub.hasClassDescription(desc.getOperand());
    case OWL_RL_Sub:
      return false;
    case OWL_RL_Super:
      return OWL_RL_Sub.hasClassDescription(desc.getOperand());
    case OWL_RL_Equiv:
      return false;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLObjectExactCardinalityRestriction desc) {
    switch(this) {
    case OWL_DL:
      // Only OK for transitive properties, but need an OWLOntology to check
      // that.
      return hasPropertyExpression(desc.getProperty()) && hasClassDescription(desc.getFiller());
    case OWL_Lite:
      return (desc.getCardinality() == 0 || desc.getCardinality() == 1) && hasPropertyExpression(desc.getProperty())
        && hasClassDescription(desc.getFiller());
    case OWL_Full:
      return hasPropertyExpression(desc.getProperty()) && hasClassDescription(desc.getFiller());
    case OWL_2_DL:
      return hasPropertyExpression(desc.getProperty()) && hasClassDescription(desc.getFiller());
    case OWL_EL:
      return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return false;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLObjectIntersectionOf desc) {
    switch(this) {
    case OWL_DL:
      return hasClassDescription(desc.getOperands());
    case OWL_Lite:
      return desc.getOperands().size() > 1 && allNamedClassesOrRestrictions(desc.getOperands());
    case OWL_Full:
      return hasClassDescription(desc.getOperands());
    case OWL_2_DL:
      return hasClassDescription(desc.getOperands());
    case OWL_EL:
      return hasClassDescription(desc.getOperands());
    case OWL_QL:
      return hasClassDescription(desc.getOperands());
    case OWL_QL_Sub:
      return false;
    case OWL_QL_Super:
      return hasClassDescription(desc.getOperands());
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasClassDescription(desc.getOperands());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLObjectMaxCardinalityRestriction desc) {
    switch(this) {
    case OWL_DL:
      return hasPropertyExpression(desc.getProperty()) && hasClassDescription(desc.getFiller());
    case OWL_Lite:
      return (desc.getCardinality() == 0 || desc.getCardinality() == 1) && hasPropertyExpression(desc.getProperty())
        && hasClassDescription(desc.getFiller());
    case OWL_Full:
      return hasPropertyExpression(desc.getProperty()) && hasClassDescription(desc.getFiller());
    case OWL_2_DL:
      return hasPropertyExpression(desc.getProperty()) && hasClassDescription(desc.getFiller());
    case OWL_EL:
      return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
      return (desc.getCardinality() == 0 || desc.getCardinality() == 1)
        && (desc.getFiller().isOWLThing() || OWL_RL_Sub.hasClassDescription(desc.getFiller()))
        && hasPropertyExpression(desc.getProperty());
    case OWL_RL_Sub:
      return false;
    case OWL_RL_Super:
      return (desc.getCardinality() == 0 || desc.getCardinality() == 1)
        && (desc.getFiller().isOWLThing() || OWL_RL_Sub.hasClassDescription(desc.getFiller()))
        && hasPropertyExpression(desc.getProperty());
    case OWL_RL_Equiv:
      return false;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLObjectMinCardinalityRestriction desc) {
    switch(this) {
    case OWL_DL:
      return hasPropertyExpression(desc.getProperty()) && hasClassDescription(desc.getFiller());
    case OWL_Lite:
      return (desc.getCardinality() == 0 || desc.getCardinality() == 1) && hasPropertyExpression(desc.getProperty())
        && hasClassDescription(desc.getFiller());
    case OWL_Full:
      return hasPropertyExpression(desc.getProperty()) && hasClassDescription(desc.getFiller());
    case OWL_2_DL:
      return hasPropertyExpression(desc.getProperty()) && hasClassDescription(desc.getFiller());
    case OWL_EL:
      return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return false;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLObjectOneOf desc) {
    switch(this) {
    case OWL_DL:
      return true;
    case OWL_Lite:
      return false;
    case OWL_Full:
      return true;
    case OWL_2_DL:
      return true;
    case OWL_EL:
      if(desc.getIndividuals().size() == 1) return true;
      else
        return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
    case OWL_RL_Sub:
      return true;
    case OWL_RL_Super:
      return false;
    case OWL_RL_Equiv:
      return false;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLObjectSelfRestriction desc) {
    switch(this) {
    case OWL_DL:
    case OWL_Lite:
    case OWL_Full:
      return false;
    case OWL_2_DL:
      return hasPropertyExpression(desc.getProperty());
    case OWL_EL:
      return hasPropertyExpression(desc.getProperty());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return false;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLObjectSomeRestriction desc) {
    switch(this) {
    case OWL_DL:
      return hasPropertyExpression(desc.getProperty());
    case OWL_Lite:
      return hasPropertyExpression(desc.getProperty()) && !desc.getFiller().isAnonymous();
    case OWL_Full:
      return hasPropertyExpression(desc.getProperty());
    case OWL_2_DL:
      return hasPropertyExpression(desc.getProperty());
    case OWL_EL:
      return hasPropertyExpression(desc.getProperty());
    case OWL_QL:
      return hasPropertyExpression(desc.getProperty()) && !desc.getFiller().isAnonymous();
    case OWL_QL_Sub:
      return hasPropertyExpression(desc.getProperty()) && desc.getFiller().isOWLThing();
    case OWL_QL_Super:
      return hasPropertyExpression(desc.getProperty()) && !desc.getFiller().isAnonymous();
    case OWL_RL:
    case OWL_RL_Sub:
      return hasPropertyExpression(desc.getProperty())
        && (desc.getFiller().isOWLThing() || hasClassDescription(desc.getFiller()));
    case OWL_RL_Super:
      return false;
    case OWL_RL_Equiv:
      return false;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLObjectUnionOf desc) {
    switch(this) {
    case OWL_DL:
      return hasClassDescription(desc.getOperands());
    case OWL_Lite:
      return false;
    case OWL_Full:
      return hasClassDescription(desc.getOperands());
    case OWL_2_DL:
      return hasClassDescription(desc.getOperands());
    case OWL_EL:
      return false;
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
      return hasClassDescription(desc.getOperands());
    case OWL_RL_Sub:
      return hasClassDescription(desc.getOperands());
    case OWL_RL_Super:
      return false;
    case OWL_RL_Equiv:
      return false;
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  public boolean hasClassDescription(OWLObjectValueRestriction desc) {
    switch(this) {
    case OWL_DL:
      return hasPropertyExpression(desc.getProperty());
    case OWL_Lite:
      return false;
    case OWL_Full:
      return hasPropertyExpression(desc.getProperty());
    case OWL_2_DL:
      return hasPropertyExpression(desc.getProperty());
    case OWL_EL:
      return hasPropertyExpression(desc.getProperty());
    case OWL_QL:
    case OWL_QL_Sub:
    case OWL_QL_Super:
      return false;
    case OWL_RL:
    case OWL_RL_Sub:
    case OWL_RL_Super:
    case OWL_RL_Equiv:
      return hasPropertyExpression(desc.getProperty());
    case OWL_2_Full:
      return true;
    default:
      throw new Panic();
    }
  }

  /**
   * <!-- hasPropertyExpression -->
   * 
   * Check a property expression. In OWL 2 it is not necessary to name inverse
   * properties. (Note that OWL EL does not support inverse properties.)
   * 
   * @param expr The property expression to check
   * @return <code>true</code> if the sublanguage supports the expression.
   */
  public boolean hasPropertyExpression(OWLPropertyExpression<?, ?> expr) {
    if(expr instanceof OWLDataProperty) return true;
    else if(expr instanceof OWLObjectProperty) return true;
    else if(expr instanceof OWLObjectPropertyInverse) {
      switch(this) {
      case OWL_DL:
      case OWL_Lite:
      case OWL_Full:
        return false;
      case OWL_2_DL:
        return true;
      case OWL_EL:
        return false;
      case OWL_QL:
      case OWL_QL_Sub:
      case OWL_QL_Super:
        return true;
      case OWL_RL:
      case OWL_RL_Sub:
      case OWL_RL_Super:
      case OWL_RL_Equiv:
        return true;
      case OWL_2_Full:
        return true;
      default:
        throw new Panic();
      }
    }
    return false;
  }

  /**
   * <!-- hasDataPropertyExpressions -->
   * 
   * Check if a set of data property expressions satisfy the constraints of the
   * sublanguage.
   * 
   * @param exprs The set of data property expressions
   * @return <code>true</code> if all the expressions are within the sublanguage
   */
  private boolean hasDataPropertyExpressions(Set<OWLDataPropertyExpression> exprs) {
    for(OWLDataPropertyExpression expr: exprs) {
      if(!hasPropertyExpression(expr)) return false;
    }
    return true;
  }

  /**
   * <!-- hasObjectPropertyExpressions -->
   * 
   * Check if a set of object property expressions satisfy the constraints of
   * the sublanguage.
   * 
   * @param exprs The set of object property expressions
   * @return <code>true</code> if all the expressions are within the sublanguage
   */
  private boolean hasObjectPropertyExpressions(Set<OWLObjectPropertyExpression> exprs) {
    for(OWLObjectPropertyExpression expr: exprs) {
      if(!hasPropertyExpression(expr)) return false;
    }
    return true;
  }

  /**
   * <!-- allNamed -->
   * 
   * Check that a set of individuals are all named.
   * 
   * @param individuals The individuals to check
   * @return <code>true</code> if all the individuals are named
   */
  private boolean allNamedIndividuals(Set<OWLIndividual> entities) {
    for(OWLIndividual entity: entities) {
      if(entity.isAnonymous()) return false;
    }
    return true;
  }

  /**
   * <!-- allNamedClassesOrRestrictions -->
   * 
   * Check that a set of class descriptions are all named classes or
   * restrictions.
   * 
   * @param entities The class descriptions to check
   * @return <code>true</code> if all the class descriptions are named classes
   *         or restrictions.
   */
  private boolean allNamedClassesOrRestrictions(Set<OWLDescription> entities) {
    for(OWLDescription entity: entities) {
      if(entity.isAnonymous() && !(entity instanceof OWLRestriction)) return false;
    }
    return true;
  }
  
  /**
   * <!-- cleanOntology -->
   * 
   * Clean an ontology of axioms that are not expressible with this species
   *
   * @param ontology The ontology to clean
   * @param manager The manager responsible for it
   * @return The number of axioms removed
   * @throws OWLOntologyChangeException
   */
  public int cleanOntology(OWLOntology ontology, OWLOntologyManager manager) throws OWLOntologyChangeException {
    List<RemoveAxiom> axiomsToRemove = new LinkedList<RemoveAxiom>();
    for(OWLAxiom axiom: ontology.getAxioms()) {
      if(axiom.isLogicalAxiom() && !hasLogicalAxiom(axiom)) axiomsToRemove.add(new RemoveAxiom(ontology, axiom));
    }
    int nAxiomsRemoved = axiomsToRemove.size();
    manager.applyChanges(axiomsToRemove);
    return nAxiomsRemoved;
  }

}
