/**
 *  @file
 *  @brief 
 *  @author wplaat
 *
 *  Copyright (C) 2008-2016 PlaatSoft
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 3.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package nl.plaatsoft.knightsquest.tools;

import java.util.List;
import java.util.Random;

import nl.plaatsoft.knightsquest.model.Land;

/**
 * The Class MyRandom.
 */
public class MyRandom {
	
	/** The rnd. */
	private static Random rnd;
	
	/**
	 * Clear.
	 */
	public static void clear() {
		rnd=null;
	}
	
	/**
	 * Next int.
	 *
	 * @param value the value
	 * @return the int
	 */
	public static int nextInt(int value) {
		
		if (rnd==null) {
			rnd = new Random(MyData.getSeed());
		}
		return rnd.nextInt(value);
	}
	
	/**
	 * Next land.
	 *
	 * @param list the list
	 * @return the land
	 */
	public static Land nextLand(List<Land> list) { 
		
		Land land = null;
		
		if (list.size()>0) {
			land = list.get(rnd.nextInt(list.size()));
		}
		return land;
	}
}