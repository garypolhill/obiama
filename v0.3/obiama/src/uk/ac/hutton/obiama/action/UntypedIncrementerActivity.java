/*
 * uk.ac.hutton.obiama.action: UntypedIncrementer.java
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
package uk.ac.hutton.obiama.action;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;

import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.hutton.obiama.exception.InconsistentRangeException;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.exception.NeedDataGotObjectPropertyException;
import uk.ac.hutton.obiama.msb.Value;
import uk.ac.hutton.obiama.msb.XSDHelper;

/**
 * <!-- UntypedIncrementer -->
 * 
 * @author Gary Polhill
 */
public class UntypedIncrementerActivity extends IncrementerActivity {
  protected XSDVocabulary type;

  /**
   * <!-- initialise -->
   * 
   * @see uk.ac.hutton.obiama.action.AbstractAction#initialise()
   * @throws IntegrationInconsistencyException
   */
  @Override
  protected void initialise() throws IntegrationInconsistencyException {
    incrementedVar = msb.getVariableName(INCREMENTED_PROPERTY_URI, this);
    if(!incrementedVar.isDataVar()) {
      throw new NeedDataGotObjectPropertyException(this, incrementedVar.getURI());
    }
    type = incrementedVar.getType();
    if(!(XSDHelper.datatypeContains(XSDVocabulary.DECIMAL, type) || type == XSDVocabulary.FLOAT || type == XSDVocabulary.DOUBLE)) {
      throw new InconsistentRangeException(incrementedVar.getURI(), type.getURI(), XSDVocabulary.DECIMAL.getURI(), this);
    }
  }

  /**
   * <!-- step -->
   * 
   * @see uk.ac.hutton.obiama.action.Action#step(java.net.URI)
   * @param individual
   * @throws IntegrationInconsistencyException
   */
  @Override
  public void step(URI individual) throws IntegrationInconsistencyException {
    switch(type) {
    case DOUBLE:
      Value<Double> value_d = incrementedVar.getExistingValueFor(individual);
      value_d.set(value_d.get() + 1.0);
      break;
    case FLOAT:
      Value<Float> value_f = incrementedVar.getExistingValueFor(individual);
      value_f.set(value_f.get() + 1.0F);
      break;
    case LONG:
      Value<Long> value_l = incrementedVar.getExistingValueFor(individual);
      value_l.set(value_l.get() + 1L);
      break;
    case INT:
      Value<Integer> value_i = incrementedVar.getExistingValueFor(individual);
      value_i.set(value_i.get() + 1);
      break;
    case SHORT:
      Value<Short> value_s = incrementedVar.getExistingValueFor(individual);
      value_s.set((short)(value_s.get() + 1));
      break;
    case BYTE:
      Value<Byte> value_b = incrementedVar.getExistingValueFor(individual);
      value_b.set((byte)(value_b.get() + 1));
      break;
    case DECIMAL:
      Value<BigDecimal> value_bd = incrementedVar.getExistingValueFor(individual);
      value_bd.set(value_bd.get().add(BigDecimal.ONE));
      break;
    default: // All integer types and subtypes
      Value<BigInteger> value_bi = incrementedVar.getExistingValueFor(individual);
      value_bi.set(value_bi.get().add(BigInteger.ONE));
    }
  }

}
