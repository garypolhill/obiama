/*
 * uk.ac.hutton.obiama.action: DecisionTreeAction.java
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

import java.io.IOException;
import java.net.URI;

import uk.ac.hutton.obiama.action.DecisionTree.Exception;
import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.msb.Value;
import uk.ac.hutton.obiama.msb.Var;
import uk.ac.hutton.util.CSVException;
import uk.ac.hutton.util.CSVReader;
import uk.ac.hutton.util.HeadedTable;

/**
 * <!-- DecisionTreeAction -->
 * 
 * <p>
 * Loads in a decision tree from a CSV file.
 * </p>
 * 
 * 
 * @author Gary Polhill
 */
public class DecisionTreeAction extends AbstractAction {
  ActionParameter treeFile;
  ActionParameter base;
  private DecisionTree tree;
  private Var choice;

  public static final URI CHOICE_URI = URI.create(BUILT_IN_ACTION_PATH + "DecisionTreeAction.owl#choice");

  /**
   * 
   */
  public DecisionTreeAction() {
    treeFile = new ActionParameter("treeFile", String.class, "CSV file from which to load the decision tree");
    base = new ActionParameter("base", String.class, "URI to prepend URI fragments referred to in CSV file");
  }

  /**
   * <!-- initialise -->
   * 
   * @see uk.ac.hutton.obiama.action.AbstractAction#initialise()
   * @throws IntegrationInconsistencyException
   */
  @Override
  protected void initialise() throws IntegrationInconsistencyException {
    choice = getVar(CHOICE_URI);
    try {
      CSVReader csv = new CSVReader(treeFile.getParameter());
      HeadedTable<String> table = csv.getHeadedTable();
      tree = new DecisionTree(this, table, base.getURIParameter());
    }
    catch(IOException e) {
      ErrorHandler.redo(e, "reading decision tree CSV file " + treeFile);
    }
    catch(CSVException e) {
      ErrorHandler.redo(e, "reading decision tree CSV file " + treeFile);
    }
    catch(DecisionTree.Exception e) {
      ErrorHandler.redo(e, "reading decision tree CSV file " + treeFile);
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
    Value<?> value = choice.getValueFor(individual);
    String valueStr = tree.decide(individual);
    value.setString(valueStr);
  }

}
