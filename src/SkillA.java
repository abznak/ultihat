/*
ultihat - Generate Hat Tournament Teams
Copyright (C) 2011  Tim Smith <ultihat@abznak.com>

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/


import java.awt.Color;
/**
 * integer array based skill measurement
 */
public class SkillA extends SkillN {
	public static final String NAMES[] = {"Ex", "Sk", "Co", "Sp"}; //experience	skill	condition	speed
	public static final int M = NAMES.length;
	public final String str;
	public String getSaveString() {
		return "" + lvl + "\t" + str;
	}

	public SkillA(int lvl, String str, boolean ismale) {
		super(lvl, ismale);
		this.str = str;
	}
	public int[] getLvls() {
		if (str == null || str.length() != M) {
			return new int[0];
		}
		int lvls[] = new int[str.length()];
		
		for (int i = 0; i < lvls.length; i++) {
			lvls[i] = str.charAt(i) - '0';
		}
//		System.out.println("getLvls: '" + str + "'");
		return lvls;
	}
}
