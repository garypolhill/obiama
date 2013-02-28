/*
 * uk.ac.hutton.obiama.action: LoadCSVIndividualsAction.java
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
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.msb.Value;
import uk.ac.hutton.obiama.msb.Var;
import uk.ac.hutton.util.CSVException;
import uk.ac.hutton.util.CSVReader;
import uk.ac.hutton.util.HeadedTable;

/**
 * <!-- LoadCSVIndividualsAction -->
 * 
 * Set values for individuals' functional properties from a CSV file.
 * Individuals are expected to be named in row headings, and properties in
 * column headings. Fragments can be used as names (if they begin with a #).
 * 
 * @author Gary Polhill
 */
public class LoadCSVIndividualsAction extends AbstractAction {

  protected Set<Var> columns;

  protected ActionParameter filename;
  
  public static final String FILENAME_PARAMETER = "filename";

  private HeadedTable<String> table;

  private Map<URI, Integer> properties;

  private Map<URI, Integer> individuals;

  /**
   * Initialise the filename parameter
   */
  public LoadCSVIndividualsAction() {
    filename = new ActionParameter(FILENAME_PARAMETER, String.class, "CSV file from which to load the values");
    
    columns = new HashSet<Var>();
    properties = new HashMap<URI, Integer>();
    individuals = new HashMap<URI, Integer>();
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
      CSVReader reader = new CSVReader(filename.getParameter());
      table = reader.getHeadedTable();

      String[] cols = table.getColumnHeadings();

      for(int c = 1; c < table.ncols(); c++) {
        URI propURI = buildURI(cols[c]);
        if(propURI != null) {
          properties.put(propURI, c);
          columns.add(getVar(propURI));
        }
      }

      for(int r = 0; r < table.nrows(); r++) {
        String ind = table.atRC(r, 0);
        if(ind != null) {
          URI indURI = buildURI(ind);
          if(indURI != null) {
            individuals.put(indURI, r);
          }
        }
      }
    }
    catch(IOException e) {
      ErrorHandler.redo(e, "reading CSV file " + filename.getParameter());
    }
    catch(CSVException e) {
      ErrorHandler.redo(e, "reading CSV file " + filename.getParameter());
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
    if(individuals.containsKey(individual)) {
      int row = individuals.get(individual);
      for(Var var: columns) {
        Value<?> value = var.getValueFor(individual);
        value.setString(table.atRC(row, properties.get(var.getURI())));
      }
    }
    else {
      ErrorHandler
          .warn("No value found in CSV file " + filename.getParameter() + " for individual " + individual.toString(),
              "stepping " + this.getClass().getCanonicalName(),
              "no values have been set from the file for this individual");
    }
  }
}
