/*
 * uk.ac.hutton.obiama.tests: TestXSDHelper.java
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

import java.math.BigDecimal;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLDataComplementOf;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataOneOf;
import org.semanticweb.owl.model.OWLDataRangeRestriction;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.vocab.OWLRestrictedDataRangeFacetVocabulary;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.msb.XSDHelper;

import junit.framework.TestCase;

/**
 * <!-- TestXSDHelper -->
 * 
 * @author Gary Polhill
 */
public class TestXSDHelper extends TestCase {

  /**
   * @param name
   */
  public TestXSDHelper(String name) {
    super(name);
  }

  /**
   * Test method for
   * {@link uk.ac.hutton.obiama.msb.XSDHelper#datatypeCompatible(org.semanticweb.owl.vocab.XSDVocabulary, java.lang.Object, org.semanticweb.owl.vocab.XSDVocabulary)}
   * .
   */
  public final void testDatatypeCompatible() {
    assertTrue(XSDHelper.datatypeCompatible(XSDVocabulary.DOUBLE, 2.0, XSDVocabulary.DOUBLE));
    assertTrue((new BigDecimal("2.0")).compareTo(new BigDecimal("2")) == 0);
    assertTrue(XSDHelper.datatypeCompatible(XSDVocabulary.DOUBLE, "2.0", XSDVocabulary.STRING));
    assertTrue(XSDHelper.datatypeCompatible(XSDVocabulary.STRING, 2.0, XSDVocabulary.DOUBLE));
    assertTrue(!XSDHelper.datatypeCompatible(XSDVocabulary.DOUBLE, "nonsense", XSDVocabulary.STRING));
    assertTrue(XSDHelper.datatypeCompatible(XSDVocabulary.INT, 2.0, XSDVocabulary.DOUBLE));
    assertTrue(!XSDHelper.datatypeCompatible(XSDVocabulary.INT, 2.5, XSDVocabulary.DOUBLE));
    assertTrue(XSDHelper.datatypeCompatible(XSDVocabulary.DOUBLE, Float.MAX_VALUE, XSDVocabulary.FLOAT));
    assertTrue(!XSDHelper.datatypeCompatible(XSDVocabulary.FLOAT, Double.MAX_VALUE, XSDVocabulary.DOUBLE));
  }

  /**
   * Test method for
   * {@link uk.ac.hutton.obiama.msb.XSDHelper#datatypeContains(org.semanticweb.owl.vocab.XSDVocabulary, org.semanticweb.owl.vocab.XSDVocabulary)}
   * .
   */
  public final void testDatatypeContains() {
    assertTrue(XSDHelper.datatypeContains(XSDVocabulary.STRING, XSDVocabulary.TOKEN));
    assertTrue(!XSDHelper.datatypeContains(XSDVocabulary.TOKEN, XSDVocabulary.NORMALIZED_STRING));
    assertTrue(XSDHelper.datatypeContains(XSDVocabulary.DECIMAL, XSDVocabulary.INT));
    assertTrue(!XSDHelper.datatypeContains(XSDVocabulary.DECIMAL, XSDVocabulary.DOUBLE));
  }

  /**
   * Test method for
   * {@link uk.ac.hutton.obiama.msb.XSDHelper#recommendedClassFor(org.semanticweb.owl.vocab.XSDVocabulary)}
   * .
   */
  public final void testRecommendedClassFor() {
    assertTrue(XSDHelper.recommendedClassFor(XSDVocabulary.DOUBLE).equals(Double.class));
    assertTrue(XSDHelper.recommendedClassFor(XSDVocabulary.TOKEN).equals(String.class));
    assertTrue(XSDHelper.recommendedClassFor(XSDVocabulary.BYTE).equals(Byte.class));
    assertTrue(XSDHelper.recommendedClassFor(XSDVocabulary.ANY_URI).equals(java.net.URI.class));
  }

  /**
   * Test method for
   * {@link uk.ac.hutton.obiama.msb.XSDHelper#instantiate(org.semanticweb.owl.vocab.XSDVocabulary, java.lang.String)}
   * .
   */
  public final void testInstantiate() {
    assertEquals(2.0, XSDHelper.instantiate(XSDVocabulary.DOUBLE, "2"));
    assertEquals(2, XSDHelper.instantiate(XSDVocabulary.INT, "2"));
    assertEquals("2", XSDHelper.instantiate(XSDVocabulary.STRING, "2"));
    try {
      @SuppressWarnings("unused")
      String str = XSDHelper.instantiate(XSDVocabulary.DOUBLE, "2.4");
      fail("No class cast exception thrown");
    }
    catch(ClassCastException e) {
      assertTrue(true);
    }
  }

  /**
   * Test method for
   * {@link uk.ac.hutton.obiama.msb.XSDHelper#getTypeFor(java.lang.Object)}.
   */
  public final void testGetTypeForObject() {
    assertTrue(XSDHelper.getTypeFor(2.0).equals(XSDVocabulary.DOUBLE));
    assertTrue(XSDHelper.getTypeFor(2).equals(XSDVocabulary.INT));
    assertTrue(XSDHelper.getTypeFor("trousers").equals(XSDVocabulary.STRING));
    Double d1 = 2.0;
    testGetTypeFor(d1, XSDVocabulary.DOUBLE);
  }

  private <T> void testGetTypeFor(T object, XSDVocabulary type) {
    assertTrue(XSDHelper.getTypeFor(object).equals(type));
  }

  /**
   * Test method for
   * {@link uk.ac.hutton.obiama.msb.XSDHelper#getTypeFor(java.lang.Class)}.
   */
  public final void testGetTypeForClassOfQ() {
    assertTrue(XSDHelper.getTypeFor(Double.class).equals(XSDVocabulary.DOUBLE));
    assertTrue(XSDHelper.getTypeFor(Integer.class).equals(XSDVocabulary.INT));
    assertTrue(XSDHelper.getTypeFor(String.class).equals(XSDVocabulary.STRING));
  }

  /**
   * Test method for
   * {@link uk.ac.hutton.obiama.msb.XSDHelper#getSuperType(org.semanticweb.owl.vocab.XSDVocabulary)}
   * .
   */
  public final void testGetSuperType() {
    assertTrue(XSDHelper.getSuperType(XSDVocabulary.INT).equals(XSDVocabulary.LONG));
    assertTrue(XSDHelper.getSuperType(XSDVocabulary.NORMALIZED_STRING).equals(XSDVocabulary.STRING));
    assertTrue(XSDHelper.getSuperType(XSDVocabulary.TIME).equals(XSDVocabulary.ANY_SIMPLE_TYPE));
  }

  /**
   * Test method for
   * {@link uk.ac.hutton.obiama.msb.XSDHelper#generaliseType(org.semanticweb.owl.vocab.XSDVocabulary, org.semanticweb.owl.vocab.XSDVocabulary)}
   * .
   */
  public final void testGeneraliseTypeXSDVocabularyXSDVocabulary() {
    assertTrue(XSDHelper.generaliseType(XSDVocabulary.DECIMAL, XSDVocabulary.DOUBLE).equals(
        XSDVocabulary.ANY_SIMPLE_TYPE));
    assertTrue(XSDHelper.generaliseType(XSDVocabulary.INT, XSDVocabulary.POSITIVE_INTEGER)
        .equals(XSDVocabulary.INTEGER));
  }

  /**
   * Test method for
   * {@link uk.ac.hutton.obiama.msb.XSDHelper#generaliseType(org.semanticweb.owl.vocab.XSDVocabulary, org.semanticweb.owl.model.OWLDataRange)}
   * .
   */
  public final void testGeneraliseTypeXSDVocabularyOWLDataRange() {
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    OWLDataFactory factory = manager.getOWLDataFactory();
    OWLDataType range1 = factory.getOWLDataType(XSDVocabulary.NMTOKEN.getURI());
    assertTrue(XSDHelper.generaliseType(XSDVocabulary.ID, range1).equals(XSDVocabulary.TOKEN));
    OWLDataType range2 = factory.getOWLDataType(XSDVocabulary.NEGATIVE_INTEGER.getURI());
    assertTrue(XSDHelper.generaliseType(XSDVocabulary.INT, range2).equals(XSDVocabulary.INTEGER));
    OWLDataType rangeInt = factory.getOWLDataType(XSDVocabulary.INT.getURI());
    OWLDataOneOf range3 =
      factory.getOWLDataOneOf(factory.getOWLTypedConstant("1", rangeInt), factory.getOWLTypedConstant("2", rangeInt),
          factory.getOWLTypedConstant("3", rangeInt));
    assertTrue(XSDHelper.generaliseType(XSDVocabulary.INT, range3).equals(XSDVocabulary.INT));
  }

  /**
   * Test method for
   * {@link uk.ac.hutton.obiama.msb.XSDHelper#dataRangeContains(org.semanticweb.owl.model.OWLDataRange, org.semanticweb.owl.vocab.XSDVocabulary)}
   * .
   */
  public final void testDataRangeContains() {
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    OWLDataFactory factory = manager.getOWLDataFactory();
    OWLDataType range1 = factory.getOWLDataType(XSDVocabulary.INT.getURI());
    assertTrue(XSDHelper.dataRangeContains(range1, XSDVocabulary.SHORT));
    assertTrue(!XSDHelper.dataRangeContains(range1, XSDVocabulary.LONG));
    assertTrue(XSDHelper.dataRangeContains(range1, XSDVocabulary.INT));
    OWLDataRangeRestriction range2 =
      factory.getOWLDataRangeRestriction(range1, factory.getOWLDataRangeFacetRestriction(
          OWLRestrictedDataRangeFacetVocabulary.MIN_INCLUSIVE, 1), factory.getOWLDataRangeFacetRestriction(
          OWLRestrictedDataRangeFacetVocabulary.MAX_INCLUSIVE, 10));
    assertTrue(XSDHelper.dataRangeContains(range2, XSDVocabulary.INT));
    OWLDataComplementOf range3 = factory.getOWLDataComplementOf(range2);
    assertTrue(XSDHelper.dataRangeContains(range3, XSDVocabulary.SHORT));
  }

}
