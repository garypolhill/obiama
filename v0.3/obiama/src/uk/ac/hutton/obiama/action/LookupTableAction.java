/*
 * uk.ac.hutton.obiama.action: LookupTableAction.java 
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
import java.util.Map;
import java.util.Set;

import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.msb.Var;

import uk.ac.hutton.util.CSVException;
import uk.ac.hutton.util.StringLookupTable;

/**
 * LookupTableAction
 * 
 * @author Gary Polhill
 */
public class LookupTableAction extends AbstractAction implements Action {
  ActionParameter lookupTableFile;
  ActionParameter nOutcomes;
  StringLookupTable table;
  Set<Var> inputVars;
  Set<Var> outcomeVars;
  Map<Var, String> var2label;

  /*
   * (non-Javadoc)
   * 
   * @see uk.ac.hutton.obiama.action.Action#getVars()
   */
  public Set<Var> getVars() {
    Set<Var> vars = new HashSet<Var>();
    vars.addAll(inputVars);
    vars.addAll(outcomeVars);
    return vars;
  }

  /*
   * (non-Javadoc)
   * 
   * @see uk.ac.hutton.obiama.action.Action#initialise(java.lang.String,
   * java.lang.String, uk.ac.hutton.obiama.msb.ModelStateBroker)
   */
  public void initialise() throws IntegrationInconsistencyException {
    // Read in the lookup table, and get a list of variables (column headings)
    // as URIs. The column headings will be entity names, and the URIs will need
    // to be created for them.
    try {
      table = new StringLookupTable(lookupTableFile.getParameter(), nOutcomes.getIntParameter());
    }
    catch(IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch(CSVException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    String[] inputLabels = table.getInputLabels();
    String[] outcomeLabels = table.getOutcomeLabels();

    var2label = new HashMap<Var, String>();

    inputVars = new HashSet<Var>();
    outcomeVars = new HashSet<Var>();

    for(int i = 0; i < inputLabels.length; i++) {
      Var var = msb.getVariableName(buildURI(inputLabels[i]), this);
      inputVars.add(var);
      var2label.put(var, inputLabels[i]);
    }
    for(int i = 0; i < outcomeLabels.length; i++) {
      Var var = msb.getVariableName(buildURI(outcomeLabels[i]), this);
      outcomeVars.add(var);
      var2label.put(var, outcomeLabels[i]);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see uk.ac.hutton.obiama.action.Action#step(java.net.URI)
   */
  public void step(URI individual) throws IntegrationInconsistencyException {
    // Build the input
    Map<String, String> input = new HashMap<String, String>();
    for(Var inputVar: inputVars) {
      input.put(var2label.get(inputVar), inputVar.getValueFor(individual).toString());
    }
    
    // Look up the outcome
    Map<String, String> outcome = table.lookupAll(input);
    if(outcome == null) {
      // There's no outcome defined for this input!
      StringBuffer buf = new StringBuffer();
      
      for(String inputLabel: table.getInputLabels()) {
        if(buf.length() > 0) buf.append(", ");
        buf.append(inputLabel + "=" + input.get(inputLabel));
      }
      ErrorHandler.redo(new Exception("No such row in lookup table " + lookupTableFile.getParameter()),
          "Looking up entry [" + buf + "] for individual " + individual);
    }
    
    // Set the outcome
    for(Var outcomeVar: outcomeVars) {
      String result = outcome.get(var2label.get(outcomeVar));
      outcomeVar.getValueFor(individual).set(result);
    }
  }

}
