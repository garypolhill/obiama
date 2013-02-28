/*
 * uk.ac.hutton.obiama.action: LoadACCCreator.java
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

import uk.ac.hutton.obiama.exception.ErrorHandler;
import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.msb.Instance;
import uk.ac.hutton.obiama.msb.Value;
import uk.ac.hutton.obiama.msb.Var;
import uk.ac.hutton.util.GISRaster;
import uk.ac.hutton.util.GISRasterReader;
import uk.ac.hutton.util.Table;

/**
 * <!-- LoadACCCreator -->
 * 
 * A creator that creates an ACC and assigns cells values from a file. The
 * property assigned values must be functional.
 * 
 * @author Gary Polhill
 */
public class LoadACCCreator extends AbstractACCCreator implements Creator {
  /**
   * The name of the file to load the image data from
   */
  protected ActionParameter fileName;

  /**
   * The property in the model structure ontology to assert values for each cell
   */
  private Var propertyVar;

  /**
   * Default name for the property
   */
  public static URI RASTER_PROPERTY_URI = URI.create(Action.BUILT_IN_ACTION_PATH + "LoadACC.owl#setProperty");

  /**
   * Constructor--initialise the action parameters
   */
  public LoadACCCreator() {
    fileName = new ActionParameter("fileName", String.class, "File to load the image data from");
  }

  /**
   * <!-- initialiseCreator -->
   * 
   * Initialise the propertyVar
   * 
   * @see uk.ac.hutton.obiama.action.AbstractACCCreator#initialiseCreator()
   * @throws IntegrationInconsistencyException
   */
  @Override
  protected void initialise() throws IntegrationInconsistencyException {
    super.initialise();
    propertyVar = msb.getVariableName(getURIFor(RASTER_PROPERTY_URI), this);
  }

  /**
   * <!-- stepCreator -->
   * 
   * Step for a specific Space instance
   * 
   * @see uk.ac.hutton.obiama.action.AbstractCreator#stepCreator(java.net.URI)
   * @param individual
   * @throws IntegrationInconsistencyException
   */
  @Override
  protected void step(URI actor, Instance creation) throws IntegrationInconsistencyException {
    buildSpace(creation);
  }

  /**
   * <!-- buildSpace -->
   * 
   * Build the space. Call super to do the actual construction, then assign
   * values from the raster type.
   * 
   * @param theSpace Instance to assign the ACC space to.
   * @throws IntegrationInconsistencyException
   */
  private void buildSpace(Instance theSpace) throws IntegrationInconsistencyException {
    try {
      GISRaster<?> raster = GISRasterReader.read(fileName.getParameter());
      Table<String> table = raster.asStringTable();

      buildSpace(theSpace, raster.ncols(), raster.nrows(), false, false);
      for(int x = 0; x < raster.ncols(); x++) {
        for(int y = 0; y < raster.nrows(); y++) {
          Instance i = cell2Darr[x][y];
          if(propertyVar.isDataVar()) {
            Value<?> value = propertyVar.getValueFor(i.getURI());
            value.setString(table.atXY(x, y));
          }
          else {
            Value<URI> value = propertyVar.getValueFor(i.getURI());
            value.set(buildURI(table.atXY(x, y)));
          }
        }
      }
    }
    catch(IOException e) {
      ErrorHandler.redo(e, "loading raster from " + fileName.getParameter() + " in creator " + getURI());
    }
  }

}
