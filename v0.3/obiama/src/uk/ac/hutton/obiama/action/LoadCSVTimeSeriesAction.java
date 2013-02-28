/*
 * uk.ac.hutton.obiama.action: LoadCSVTimeSeries.java
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
import java.util.HashMap;
import java.util.Map;

import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.msb.Value;
import uk.ac.hutton.obiama.msb.Var;
import uk.ac.hutton.util.CSVException;
import uk.ac.hutton.util.CSVReader;
import uk.ac.hutton.util.HeadedTable;

/**
 * <!-- LoadCSVTimeSeries -->
 * 
 * Load time series in from a CSV file. There is expected to be one row in the
 * file for each time step and agent for which the step method is called. Each
 * column in the file is expected to correspond to a property that each agent
 * has. The first row in the file is expected to contain the URIs of those
 * properties.
 * 
 * @author Gary Polhill
 */
public class LoadCSVTimeSeriesAction extends AbstractAction {
  ActionParameter csvFile;
  private HeadedTable<String> table;
  private int row;
  private Map<Var, String> varMap;
  private boolean warned;

  public LoadCSVTimeSeriesAction() {
    csvFile = new ActionParameter("csvFile", String.class, "The file from which to load the time series");
    varMap = new HashMap<Var, String>();
    warned = false;
  }

  /**
   * <!-- initialise -->
   * 
   * @see uk.ac.hutton.obiama.action.AbstractAction#initialise()
   * @throws IntegrationInconsistencyException
   */
  @Override
  protected void initialise() throws IntegrationInconsistencyException {
    try {
      CSVReader csvReader = new CSVReader(csvFile.getParameter());
      table = csvReader.getHeadedTable();
      row = 0;
      for(String name: table.getColumnHeadingsSet()) {
        Var var = msb.getVariableName(buildURI(name), this);
        vars.add(var);
        varMap.put(var, name);
      }
    }
    catch(IOException e) {
      ErrorHandler.redo(e, "Trying to read from CSV file " + csvFile.getParameter());
    }
    catch(CSVException e) {
      ErrorHandler.redo(e, "Reading from CSV file " + csvFile.getParameter());
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
    if(row >= table.nrows()) {
      if(!warned) {
        ErrorHandler.warn(new ArrayIndexOutOfBoundsException(), "reading row " + row + " from "
          + csvFile.getParameter(), "no further changes to " + table.getColumnHeadingsSet() + " will be made ");
        warned = true;
      }
    }
    for(Var var: vars) {
      Value<?> value = var.getValueFor(individual);
      value.setString(table.getRow(row).get(varMap.get(var)));
    }
    row++;
  }

}
