/*
 * uk.ac.hutton.obiama.msb: ReasonerFactory.java Copyright (C) 2013 The James Hutton
 * Institute
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerFactory;
import org.semanticweb.owl.model.OWLOntologyManager;

import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.Panic;
import uk.ac.hutton.util.Reflection;

/**
 * ReasonerFactory
 * 
 * Build a reasoner, either by default, from the class passed as command line
 * argument, or using a specified reasoner class or reasoner class name.
 * 
 * @author Gary Polhill
 */
final class ReasonerFactory {
  private ReasonerFactory() {
    // Disable construction
  }

  /**
   * <!-- getReasoner -->
   * 
   * @param manager
   * @return the reasoner stipulated on the command line to OBIAMA
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws ClassNotFoundException
   * @throws IllegalArgumentException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  public static OWLReasoner getReasoner(OWLOntologyManager manager) throws SecurityException, NoSuchMethodException,
      ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException,
      InvocationTargetException {
    return getReasoner(manager, ObiamaSetUp.getRequestedReasonerClass());
  }

  /**
   * <!-- getReasoner -->
   * 
   * @param manager
   * @param reasonerClass
   * @return a reasoner of the specified class
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws IllegalArgumentException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  @SuppressWarnings("unchecked")
  public static OWLReasoner getReasoner(OWLOntologyManager manager, Class<?> reasonerClass) throws SecurityException,
      NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException,
      InvocationTargetException {
    if(Reflection.subType(reasonerClass, OWLReasoner.class)) {
      Constructor<OWLReasoner> builder =
        (Constructor<OWLReasoner>)reasonerClass.getConstructor(OWLOntologyManager.class);
      return builder.newInstance(manager);
    }
    else if(Reflection.subType(reasonerClass, OWLReasonerFactory.class)) {
      Constructor<OWLReasonerFactory> builder = (Constructor<OWLReasonerFactory>)reasonerClass.getConstructor();
      return builder.newInstance().createReasoner(manager);
    }
    else {
      throw new IllegalArgumentException("Class " + reasonerClass
        + " does not follow the OWLReasoner or OWLReasonerFactory interface");
    }
  }

  /**
   * <!-- getReasoner -->
   * 
   * @param manager
   * @param reasonerClassName
   * @return a reasoner of the specified class name
   * @throws SecurityException
   * @throws IllegalArgumentException
   * @throws NoSuchMethodException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws ClassNotFoundException
   */
  @SuppressWarnings("unchecked")
  public static OWLReasoner getReasoner(OWLOntologyManager manager, String reasonerClassName) throws SecurityException,
      IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException,
      InvocationTargetException, ClassNotFoundException {
    return getReasoner(manager, (Class<OWLReasoner>)Class.forName(reasonerClassName));
  }

  /**
   * <!-- getReasonerOrDie -->
   * 
   * Convenience method, handling exceptions
   * 
   * @param manager
   * @return the reasoner stipulated on the command line
   */
  public static OWLReasoner getReasonerOrDie(OWLOntologyManager manager) {
    return getReasonerOrDie(manager, (Class<OWLReasoner>)null);
  }

  /**
   * <!-- getReasonerOrDie -->
   * 
   * Convenience method, handling exceptions
   * 
   * @param manager
   * @param reasonerClassName
   * @return an instance of the requested reasoner class name
   */
  @SuppressWarnings("unchecked")
  public static OWLReasoner getReasonerOrDie(OWLOntologyManager manager, String reasonerClassName) {
    try {
      return getReasonerOrDie(manager, (Class<OWLReasoner>)Class.forName(reasonerClassName));
    }
    catch(ClassNotFoundException e) {
      ErrorHandler.fatal(e, "building reasoner");
      throw new Panic();
    }
  }

  /**
   * <!-- getReasonerOrDie -->
   * 
   * Convenience method, handing exceptions
   * 
   * @param manager
   * @param reasonerClass
   * @return an instance of the requested reasoner class
   */
  public static OWLReasoner getReasonerOrDie(OWLOntologyManager manager, Class<OWLReasoner> reasonerClass) {
    final String whilst = "building reasoner";
    try {
      return reasonerClass == null ? getReasoner(manager) : getReasoner(manager, reasonerClass);
    }
    catch(SecurityException e) {
      ErrorHandler.fatal(e, whilst);
      throw new Panic();
    }
    catch(IllegalArgumentException e) {
      ErrorHandler.fatal(e, whilst);
      throw new Panic();
    }
    catch(NoSuchMethodException e) {
      ErrorHandler.fatal(e, whilst);
      throw new Panic();
    }
    catch(ClassNotFoundException e) {
      ErrorHandler.fatal(e, whilst);
      throw new Panic();
    }
    catch(InstantiationException e) {
      ErrorHandler.fatal(e, whilst);
      throw new Panic();
    }
    catch(IllegalAccessException e) {
      ErrorHandler.fatal(e, whilst);
      throw new Panic();
    }
    catch(InvocationTargetException e) {
      ErrorHandler.fatal(e, whilst);
      throw new Panic();
    }
  }
}
