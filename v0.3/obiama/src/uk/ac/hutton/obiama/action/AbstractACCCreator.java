/*
 * uk.ac.hutton.obiama.action: AbstractACCCreator.java
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

import java.util.Set;

import uk.ac.hutton.obiama.exception.IntegrationInconsistencyException;
import uk.ac.hutton.obiama.model.ACCSpaceOntology;
import uk.ac.hutton.obiama.model.SpaceOntology;
import uk.ac.hutton.obiama.msb.Concept;
import uk.ac.hutton.obiama.msb.Instance;
import uk.ac.hutton.obiama.msb.Var;
import uk.ac.hutton.util.SetCreator;

/**
 * <!-- AbstractACCCreator -->
 * 
 * @author Gary Polhill
 */
public abstract class AbstractACCCreator extends AbstractCreator implements Creator {
  protected Concept cell0D;
  protected Concept cell1D;
  protected Concept cell2D;
  protected Concept complex2D;
  protected Var boundedBy;
  protected Var contains;
  protected Var containsLocation;
  protected Instance[][] cell2Darr;

  /**
   * <!-- initialise -->
   * 
   * @see uk.ac.hutton.obiama.action.AbstractAction#initialise()
   * @throws IntegrationInconsistencyException
   */
  @Override
  protected void initialise() throws IntegrationInconsistencyException {
    boundedBy =
      msb.getVariableName(ACCSpaceOntology.BOUNDED_BY_URI, ACCSpaceOntology.CELL_URI, ACCSpaceOntology.CELL_URI, this);
    contains =
      msb.getVariableName(ACCSpaceOntology.CONTAINS_URI, ACCSpaceOntology.COMPLEX_URI, ACCSpaceOntology.CELL_URI, this);

    Set<Var> cellVars = SetCreator.createSet(boundedBy);
    Set<Var> complexVars = SetCreator.createSet(contains);

    vars.add(boundedBy);
    vars.add(contains);

    cell0D = msb.getConcept(ACCSpaceOntology.CELL_0D_URI, this, cellVars, null);
    cell1D = msb.getConcept(ACCSpaceOntology.CELL_1D_URI, this, cellVars, null);
    cell2D = msb.getConcept(ACCSpaceOntology.CELL_2D_URI, this, cellVars, null);
    complex2D = msb.getConcept(ACCSpaceOntology.COMPLEX_URI, this, complexVars, null);

    containsLocation = msb.getVariableName(SpaceOntology.CONTAINS_LOCATIONS_URI, this);

    vars.add(containsLocation);

    concepts.add(cell0D);
    concepts.add(cell1D);
    concepts.add(cell2D);
    concepts.add(complex2D);

  }

  /**
   * <!-- buildSpace -->
   * 
   * Build an abstract cell complex consisting of a number of square locations
   * arranged to form a rectangle. Each location is a 2D complex, which contains
   * a 2D cell that is bounded by four 1D cells, each of which in turn is
   * bounded by two 0D cells. The cell complex may be wrapped in neither, either
   * or both of the two dimensions. If the complex is wrapped in both
   * dimensions, then the number of 0D cells equals the number of 2D cells.
   * Otherwise, for each dimension the complex is not wrapped, extra 0D cells
   * are needed to form the boundary.
   * 
   * @param theSpace URI of the space to create
   * @param nx Number of x cells in the space
   * @param ny Number of y cells in the space
   * @param xWrap Wrap the x dimension?
   * @param yWrap Wrap the y dimension?
   * @throws IntegrationInconsistencyException
   */

  protected void buildSpace(Instance theSpace, int nx, int ny, boolean xWrap, boolean yWrap)
      throws IntegrationInconsistencyException {
    cell2Darr = new Instance[nx][ny];
    Instance[][] cell0Darr = new Instance[nx + 1][ny + 1];

    // Create the complexes, 2D cells and lower-left corner 0D cells. (Thinking
    // of the origin in the bottom left.)

    for(int x = 0; x < nx; x++) {
      for(int y = 0; y < ny; y++) {
        Instance complex = complex2D.createInstance();
        Instance cell = cell2D.createInstance();

        theSpace.addProperty(containsLocation, complex.getURI());

        complex.addProperty(contains, cell.getURI());
        cell2Darr[x][y] = cell;

        cell0Darr[x][y] = cell0D.createInstance();
      }
    }

    // Create or initialise the 0D cells at the 'top' of the space. If the
    // y-dimension is wrapped, then the 0D cells at the top are the same as
    // those at the bottom. If not, then new 0D cells should be created.

    for(int x = 0; x < nx; x++) {
      if(yWrap) {
        cell0Darr[x][ny] = cell0Darr[x][0];
      }
      else {
        cell0Darr[x][ny] = cell0D.createInstance();
      }
    }

    // Create or initialise the 0D cells at the 'right' of the space,
    // depending on whether the x-dimension is wrapped.

    for(int y = 0; y < ny; y++) {
      if(xWrap) {
        cell0Darr[nx][y] = cell0Darr[0][y];
      }
      else {
        cell0Darr[nx][y] = cell0D.createInstance();
      }
    }

    // Create or initialise the 0D cell at the 'top-right' of the space,
    // depending on which if any of the x- and y-dimensions are wrapped.

    if(xWrap && yWrap) {
      cell0Darr[nx][ny] = cell0Darr[0][0];
    }
    else if(xWrap) {
      cell0Darr[nx][ny] = cell0Darr[0][ny];
    }
    else if(yWrap) {
      cell0Darr[nx][ny] = cell0Darr[nx][0];
    }
    else {
      cell0Darr[nx][ny] = cell0D.createInstance();
    }

    // Create all the vertically oriented (i.e. parallel to y-axis) 1D cells.

    for(int x = 0; x < (xWrap ? nx : nx + 1); x++) {
      for(int y = 0; y < ny; y++) {
        Instance cell = cell1D.createInstance();

        // Bound the 1D cell by 0D cells at the top and bottom

        cell.addProperty(boundedBy, cell0Darr[x][y].getURI());
        cell.addProperty(boundedBy, cell0Darr[x][y + 1].getURI());

        // Use the 1D cell to bound the 2D cells to the left and right, where
        // applicable

        if(x < nx) cell2Darr[x][y].addProperty(boundedBy, cell.getURI());
        if(x == 0) {
          if(xWrap) cell2Darr[nx - 1][y].addProperty(boundedBy, cell.getURI());
        }
        else {
          cell2Darr[x - 1][y].addProperty(boundedBy, cell.getURI());
        }
      }
    }

    // Create all the horizontally oriented 1D cells

    for(int x = 0; x < nx; x++) {
      for(int y = 0; y < (yWrap ? ny : ny + 1); y++) {
        Instance cell = cell1D.createInstance();

        // Bound the 1D cell with 0D cells to the left and right

        cell.addProperty(boundedBy, cell0Darr[x][y].getURI());
        cell.addProperty(boundedBy, cell0Darr[x + 1][y].getURI());

        // Use the 1D cell to bound the 2D cells above and below, where
        // applicable

        if(y < ny) cell2Darr[x][y].addProperty(boundedBy, cell.getURI());
        if(y == 0) {
          if(yWrap) cell2Darr[x][ny - 1].addProperty(boundedBy, cell.getURI());
        }
        else {
          cell2Darr[x][y - 1].addProperty(boundedBy, cell.getURI());
        }
      }
    }
  }
}
