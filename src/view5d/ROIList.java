/*added by Connie Jiang*/
package view5d;

import java.util.*;

public class ROIList extends Object {
	Vector<ROI> list;
	int activeROI;
	
	public ROIList() {
		list = new Vector<ROI>();
		activeROI = -1;
	}
	
	public boolean Insert(ROI roi) {
		RectROI rROI = new RectROI(); //now we assume we only use Rect ROI, no PolygonROI
		rROI.setCellType(((RectROI)roi).getCellType());
		rROI.TakeSqrROIs(((RectROI)roi).ProjMin, ((RectROI)roi).ProjMax);
		return list.add(rROI);
	}
	
	public boolean SetActiveROI(float pos[]) {
		for(int i = 0; i < list.size(); i++) {
			if(list.elementAt(i).InROIRange((int)pos[0], (int)pos[1], (int)pos[2])) {
				activeROI = i;
				return true;
			}
		}
		return false;
	}
	
	public boolean Remove() {
		if(activeROI < 0) 
			return false;
		else
			return list.remove(activeROI) != null;
	}
	
	public String toString() {
		String s = "";
		for(int i = 0; i < list.size(); i++) {
			s += "[" + (i + 1) + "]" + list.elementAt(i).toString() + "\n";
		}
		return s;
	}

}
