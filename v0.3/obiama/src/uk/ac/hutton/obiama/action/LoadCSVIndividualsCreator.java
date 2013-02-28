/*
 * uk.ac.hutton.obiama.action: LoadCSVIndividualsCreator.java
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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.msb.Instance;
import uk.ac.hutton.obiama.msb.Var;
import uk.ac.hutton.util.CSVException;
import uk.ac.hutton.util.CSVReader;
import uk.ac.hutton.util.HeadedTable;

/**
 * <!-- LoadCSVIndividualsCreator -->
 * 
 * <p>
 * This creator loads in a single row of data from a CSV file, using it to set
 * certain properties of an individual. (To load in all individuals, use
 * LoadCSVAllIndividualsCreator.) It can be used in a number of ways. First,
 * either an identifier column can be specified or not. If an identifier column
 * is specified, then the entries in that column will be used for the URIs of
 * the individuals created/modified. (If not, system-generated URIs will be
 * used.) If the schedule names an individual associated with this action, then
 * the step() method will look for the (next) row referring to that individual
 * and assign the values to the individual in that row.
 * </p>
 * 
 * <p>
 * For non-functional properties, you can specify a separator using
 * <code>nonFunctionalSeparator</code>. If this is set to anything that could be
 * construed as meaning a new line, e.g. "\\n", "\\r", "nl", "newline" or
 * "new line", and an <code>identifier</code> is specified, the CSV file is
 * assumed to have several consecutive rows each with cells to add to the
 * non-functional properties. e.g.
 * </p>
 * 
 * <table>
 * <tr>
 * <th>ID</th>
 * <th>age</th>
 * <th>likes</th>
 * <th>does not like</th>
 * </tr>
 * <tr>
 * <td>ind1</td>
 * <td>23</td>
 * <td>bananas</td>
 * <td>apples</td>
 * </tr>
 * <tr>
 * <td>ind1</td>
 * <td>&nbsp;</td>
 * <td>mangoes</td>
 * <td>oranges</td>
 * </tr>
 * <tr>
 * <td>ind1</td>
 * <td>&nbsp;</td>
 * <td>blackberries</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>ind1</td>
 * <td>&nbsp;</td>
 * <td>pears</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>ind2</td>
 * <td>7</td>
 * <td>grapes</td>
 * <td>grapefruit</td>
 * </tr>
 * <tr>
 * <td>ind2</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>tomatoes</td>
 * </tr>
 * </table>
 * 
 * <p>
 * Otherwise, the separator value will be used as an argument to the
 * <code>split</code> method, where it is treated as a regular expression.
 * </p>
 * 
 * <p>
 * The <code>columnPropertyMap</code> parameter can be used to map column
 * headings to OWL property names in the model structure ontology. Its format is
 * as per Java property-value list files, with one property=value pair on each
 * line. <!-- Assuming that OWL and the OWLAPI honour new lines in data property
 * entries... --> Here, the 'property' is the column heading, and if there is
 * any whitespace or an = in the column heading in the CSV file, then it should
 * be escaped using a backslash in the entry for this parameter. The 'value' is
 * the name of the OWL property. If the CSV file does not have column headings,
 * then this should be indicated using the <code>headers</code> parameter (with
 * value "false" rather than "true"). The column headings then default to "A",
 * "B", "C" ... "Z", "AA", "AB", ..., "ZZ", "AAA", ..., and mappings can still
 * be specified using these names. The <code>columnPropertyMap</code> parameter
 * can also be used to select a subset of the columns to use as data, as only
 * those columns named in the property-value list will be used. If the
 * <code>columnPropertyMap</code> parameter is not specified, then all columns
 * are assumed to be relevant, and (with the exception of the identifier column)
 * to have headings equal to the property name they correspond to. (A warning is
 * issued if the CSV file has no column headings in this case.)
 * </p>
 * 
 * @see java.lang.String.split()
 * @see java.util.Properties.load()
 * @see LoadCSVAllIndividualsCreator
 * @author Gary Polhill
 */
public class LoadCSVIndividualsCreator extends AbstractCreator {
  /**
   * CSV file to load data from
   */
  protected ActionParameter filename;

  /**
   * Column heading of the column containing identifiers for individuals
   */
  protected ActionParameter identifier;

  /**
   * Boolean: <code>true</code> if the CSV file has column headings,
   * <code>false</code> otherwise (when default headings will be applied).
   */
  protected ActionParameter headers;

  /**
   * Map of column headings to OWL property names, as property=value pairs
   */
  protected ActionParameter columnPropertyMap;

  /**
   * Separator for values for non-functional properties--if these are spread
   * over multiple lines, use "nl"; otherwise the value is a regular expression
   * used as argument to the String.split() method to separate the values from
   * the entry in a single cell.
   */
  protected ActionParameter nonFunctionalSeparator;

  /**
   * Row at which to start loading data (0 by default).
   */
  protected ActionParameter startRow;

  /**
   * Whether or not to ignore empty cells
   */
  protected ActionParameter ignoreEmpty;

  /**
   * The data read in from the CSV file
   */
  protected HeadedTable<String> data;

  /**
   * Names of properties from column headings in CSV file
   */
  protected Set<String> varNames;

  /**
   * Map of property names to column names
   */
  private Map<String, String> columnVars;

  /**
   * Map of Vars to column names
   */
  private Map<Var, String> varColumns;

  /**
   * Map of individual URIs to list of rows identified as pertaining to that
   * individual
   */
  private Map<URI, LinkedList<Integer>> rowIDs;

  /**
   * The next row to read
   */
  private int row;

  /**
   * Constructor, initialising the action parameters
   */
  public LoadCSVIndividualsCreator() {
    varNames = new HashSet<String>();
    columnVars = new HashMap<String, String>();
    varColumns = new HashMap<Var, String>();
    rowIDs = null;
    filename = new ActionParameter("filename", String.class, "The name of the CSV file from which to load individuals");
    identifier = new ActionParameter("identifier", String.class, "The name of the column to use for identifiers");
    headers =
      new ActionParameter("headers", Boolean.class, "Whether or not the CSV file has a header row. "
        + "(If false, then column headers default to \"A\", \"B\", ... \"Z\", \"AA\", ... etc.)");
    columnPropertyMap =
      new ActionParameter("columnPropertyMap", String.class, "Map of CSV column headers to OWL property names. "
        + "(Format is as per Java properties, one line per key=value pair. "
        + "White space in the key name should be escaped with \\.)");
    nonFunctionalSeparator =
      new ActionParameter("nonFunctionalSeparator", String.class,
          "Separator used for non-functional properties where multiple values are stored in a single cell");
    startRow =
      new ActionParameter("startRow", Integer.class, "0",
          "Row to start reading data from (0 first, header row not included)");
    ignoreEmpty =
      new ActionParameter("ignoreEmpty", Boolean.class, "false",
          "Whether to ignore empty cells. If true, empty cells will"
            + " result in no (new) assignment to the corresponding property");
  }

  /**
   * <!-- initialise -->
   * 
   * Read the CSV file and initialise the names of the vars that this action
   * will use
   * 
   * @see uk.ac.hutton.obiama.action.AbstractCreator#initialise()
   * @throws IntegrationInconsistencyException
   */
  @Override
  protected void initialise() throws IntegrationInconsistencyException {
    try {
      CSVReader reader = new CSVReader(filename.getParameter());
      data = headers.getBooleanParameter() ? reader.getHeadedTable() : new HeadedTable<String>(reader.getTable());
      Set<String> availableColumnSet = data.getColumnHeadingsSet();
      if(identifier.parameterSet() && !availableColumnSet.contains(identifier.getParameter())) {
        // TODO throw exception: Requested identifier column not found
      }
      if(identifier.parameterSet()) {
        availableColumnSet.remove(identifier.getParameter());
        initialiseRowIDs();
      }
      if(columnPropertyMap.parameterSet()) {
        Properties columnMap = columnPropertyMap.getPropertiesParameter();
        Set<String> keys = columnMap.stringPropertyNames();
        for(String key: keys) {
          if(availableColumnSet.contains(key)) {
            columnVars.put(columnMap.getProperty(key), key);
          }
          else {
            // TODO throw exception: No such column in the data
          }
        }
      }
      else {
        if(!headers.getBooleanParameter()) {
          ErrorHandler.warn(new Exception("CSV file " + filename.getParameter()
            + " is asserted to have no column headers and no mappings "
            + "from columns to property names have been specified"), "initialising Creator action " + getClass() + "("
            + uri + ")", "unless there are properties named \"A\", \"B\", ... (the default "
            + "labels assigned to columns), an inconsistency will be detected");
        }
        for(String name: availableColumnSet) {
          columnVars.put(name, name);
        }
      }

      varNames.addAll(columnVars.keySet());

      if(nonFunctionalSeparator.parameterSet()) {
        String sep = nonFunctionalSeparator.getParameter();

        if(sep.equals("\\n") || sep.equals("\\r") || sep.equals("\\r\\n") || sep.equals("\r") || sep.equals("\r\n")
          || sep.equalsIgnoreCase("nl") || sep.equalsIgnoreCase("newline") || sep.equalsIgnoreCase("new line")) {
          nonFunctionalSeparator.setParameter("\n");
        }
      }

      row = startRow.getIntParameter();
    }
    catch(IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch(CSVException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    for(String varName: varNames) {
      Var var = msb.getVariableName(buildURI(varName), this);
      vars.add(var);
      varColumns.put(var, columnVars.get(varName));
    }

  }

  /**
   * <!-- initialiseRowIDs -->
   * 
   * Initialise the map of individual URIs to list of rows pertaining to each
   * individual
   */
  private void initialiseRowIDs() {
    rowIDs = new HashMap<URI, LinkedList<Integer>>();

    String idCol = identifier.getParameter();

    for(int i = startRow.getIntParameter(); i < data.nrows(); i++) {
      String id = data.atRC(i, idCol);
      URI idURI = buildURI(id);
      if(rowIDs.containsKey(idURI)) {
        rowIDs.get(idURI).addLast(i);
      }
      else {
        LinkedList<Integer> idRows = new LinkedList<Integer>();
        idRows.addLast(i);
        rowIDs.put(idURI, idRows);
      }
    }
  }

  /**
   * <!-- getRow -->
   * 
   * @return The current row we are on
   */
  protected int getRow() {
    return row;
  }

  /**
   * <!-- step -->
   * 
   * Initialise the vars of the selected instance to the values found in the
   * table row specified.
   * 
   * @param individual The instance to assign values to
   * @throws IntegrationInconsistencyException
   */
  private void step(Instance individual) throws IntegrationInconsistencyException {
    if(row < 0 || row >= data.nrows()) {
      // TODO throw exception: run out of data in the file
    }

    for(Var var: vars) {

      String value = data.atRC(row, varColumns.get(var));

      if(value == null || value.length() == 0) {
        if(ignoreEmpty.getBooleanParameter()) continue;
        // TODO throw exception: unexpected empty cell
      }

      if(var.isDataVar()) {
        if(var.isFunctional()) {
          individual.setPropertyString(var, value);
        }
        else {
          String[] values =
            nonFunctionalSeparator.parameterSet() ? value.split(nonFunctionalSeparator.getParameter())
                                                 : new String[] { value };
          for(int i = 0; i < values.length; i++) {
            individual.addPropertyString(var, values[i]);
          }
        }
      }
      else {
        if(var.isFunctional()) {
          individual.setProperty(var, buildURI(value));
        }
        else {
          String[] values =
            nonFunctionalSeparator.parameterSet() ? value.split(nonFunctionalSeparator.getParameter())
                                                 : new String[] { value };
          for(int i = 0; i < values.length; i++) {
            individual.addProperty(var, buildURI(values[i]));
          }
        }
      }
    }
  }

  /**
   * <!-- step -->
   * 
   * @see uk.ac.hutton.obiama.action.AbstractCreator#step(java.net.URI,
   *      uk.ac.hutton.obiama.msb.Instance)
   * @param actor
   * @param creation
   * @throws IntegrationInconsistencyException
   */
  @Override
  protected void step(URI actor, Instance creation) throws IntegrationInconsistencyException {
    if(identifier.parameterSet()) {
      if(rowIDs.containsKey(creation)) {
        LinkedList<Integer> rowList = rowIDs.get(creation);
        if(rowList.size() > 0) {
          row = rowList.removeFirst();
          step(creation);
          if(nonFunctionalSeparator.parameterSet() && nonFunctionalSeparator.getParameter().equals("\n")) {
            while(rowList.size() > 0) {
              int nextRow = rowList.removeFirst();
              if(nextRow == row + 1) {
                row = nextRow;
                step(creation);
              }
              else {
                break;
              }
            }
          }
          return;
        }
      }
      // TODO throw exception: Identifier not found in table OR run out of rows
      // of data for the individual.
    }
    else {
      step(creation);
      row++;
    }

  }
}
