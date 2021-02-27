/****************************************************************************
 *   Copyright (C) 1996-2007 by Rainer Heintzmann                          *
 *   heintzmann@gmail.com                                                  *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************
*/
// By making the appropriate class "View5D" or "View5D_" public and renaming the file, this code can be toggled between Applet and ImageJ respectively
package view5d;
import ij.*;

// import java.io.*;
import java.awt.*;

class RectROI extends ROI {
	String cellType = "Unknown";
    int ProjMin[], ProjMax[];
     RectROI() {
         super();
     }
     
    double GetROISize(int dim) {
        return (ProjMax[dim] - ProjMin[dim])+1;  // returns number of pixels including both borders
     }

    public Rectangle GetSqrROI(int dim) {
        if (ProjMin == null)
            return null;
        if (dim == 0)
            return new Rectangle(ProjMin[2],ProjMin[1],
            (int) GetROISize(2)-1,(int) GetROISize(1)-1);
        if (dim == 1)
            return new Rectangle(ProjMin[0],ProjMin[2],
            (int) GetROISize(0)-1,(int) GetROISize(2)-1);
        if (dim == 2)
            return new Rectangle(ProjMin[0],ProjMin[1],
            (int) GetROISize(0)-1,(int) GetROISize(1)-1);
        return null;
    }
    

    void TakeSqrROIs(int Pmin[], int Pmax[]) {
        if (ProjMin == null)
        {
            ProjMin = new int[3];
            ProjMax = new int[3];
        }
        for (int d=0;d<3;d++)
        {
            ProjMin[d] = Pmin[d];ProjMax[d] = Pmax[d];
        }
    }

    public void UpdateSqrROI(int ROIX,int ROIY, int ROIXe, int ROIYe,int dir)
    {
        if (dir ==0)
        {
            ProjMin[2] = ROIX;ProjMax[2] = ROIXe;
            ProjMin[1] = ROIY;ProjMax[1] = ROIYe;
        }
        else if (dir==1)
        {
            ProjMin[0] = ROIX;ProjMax[0] = ROIXe;
            ProjMin[2] = ROIY;ProjMax[2] = ROIYe;
        }
        else
        {
            ProjMin[0] = ROIX;ProjMax[0] = ROIXe;
            ProjMin[1] = ROIY;ProjMax[1] = ROIYe;
        }
    }

    boolean InROIRange(int x,int y,int z) { //added by Connie Jiang
         if  (ProjMin == null)
             return true;
         if (x < ProjMin[0])
             return false;
         if (y < ProjMin[1])
             return false;
         if (z < ProjMin[2])
             return false;
         if (x > ProjMax[0])
             return false;
         if (y > ProjMax[1])
             return false;
         if (z > ProjMax[2])
             return false;
         return true;
     }
    
    boolean InROIRange(int x,int y,int z,int dim) { //Added by Connie Jiang
    	if  (ProjMin == null)
            return true;
    	
    	switch(dim) {
    		case 0: //x or (yz)
    			if ((x < ProjMin[0]) || (x > ProjMax[0]))
    				return false;
    			break;
    		case 1: //y or (xz)
    			if ((y < ProjMin[1]) || (y > ProjMax[1]))
    				return false;
    			break;
    		case 2: //z or (xy)
    			if ((z < ProjMin[2]) || (z > ProjMax[2]))
    				return false;
    			break;
    	}
        return true;
    }
    
    /*
     * Added by Connie Jiang
     */
    public String toString() {
    	String s;
    	s = "(" + ProjMin[0] + ", " + ProjMin[1] + ", " + ProjMin[2] + ")";
    	s += "(" + ProjMax[0] + ", " + ProjMax[1] + ", " + ProjMax[2] + ")";
    	s += "[" + cellType + "]";
    	return s;
    }
    
    /*
     * Added by Connie Jiang
     */
    public void classify(My3DData my3ddata) {
    	final double ChannelRatioThresh = 0.10; //if percentage is higher than this value, we consider the ROI contains this color
    	int[] count = new int[10]; //assuming we will not have more than 10 channels
    	for(int i = 0; i < 10; i++) 
    		count[i] = 0;
    	
    	for(int c = 0; c < my3ddata.Elements; c++) {
    		double minThresh;
    		
    		//minThresh= my3ddata.GetMinThresh(c); Not using this since it is too tedious to set thresholds using view5d
    		
    		switch(c) {
    		case 0:
    			minThresh = 250.0; //green
    			break;
    		case 1:
    			minThresh = 100.0; //grey
    			break;
    		case 2:
    			minThresh = 1000.0; //blue
    			break;
    		case 3:
    			minThresh = 1000.0; //red
    			break;
    		default:
    			minThresh = 0.0;
    		}
    		for(int i = ProjMin[0]; i < ProjMax[0]; i++) {
    			for (int j = ProjMin[1]; j < ProjMax[1]; j++) {
    				for(int k = ProjMin[2]; k < ProjMax[2]; k++) {
    					if (my3ddata.ElementAt(c).GetValueAt(i, j, k) >= minThresh) {
    						count[c] += 1;
    					}
    				}
    			}
    		}
    	}
;    	int total = (ProjMax[0] - ProjMin[0] + 1)
    			* (ProjMax[1] - ProjMin[1] + 1)
    			* (ProjMax[2] - ProjMin[2] + 1);
    	
    	int vector[] = new int[4];
    	for(int c = 0; c < 4; c++) {
    		if ((double) count[c]/total > ChannelRatioThresh)
    			vector[c] = 1;
    		else 
    			vector[c] = 0;
    	}
    	
    	IJ.log("Channel Vector:(" + (double) count[0]/total + ", " + (double) count[1]/total + ", " + (double) count[2]/total + ", " + (double) count[3]/total + ")\n");
    	
    	if(vector[0] > 0)
    		//green 
    		if(vector[2] > 0) //green and blue
    			cellType = "Supporting and Hair";
    		else //green only
    			cellType = "Hair";
    	else // no green
    		if(vector[2] > 0) //blue only
    			cellType = "Supporting";
    		else //grey only 
    			cellType = "Unknown";
    	
    	if(vector[3] > 0) {
    		cellType += "(Tagged)";
    	}
    	IJ.log("Cell Type: " + cellType + "\n");
    }
    /*
     * by Connie Jiang
     */
    public void setCellType(String ctype) {
    	cellType = ctype;
    }
    
    /*
     * by Connie Jiang
     */
    public String getCellType() {
    	return cellType;
    }
}
