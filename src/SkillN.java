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
public class SkillN implements Skill {
	public int SKILLMAX = 4;
/*	public static final SkillN[] skills = new SkillN[4];
	static {
		for (int i = 0; i < skills.length; i++) {
			skills[i] = new SkillN(i);
		}
	}*/



	private Color col;
	protected int lvl;
	private double lvlf;  /* so that people are randomly sorted within a level */
	private String str;
	public int getSkillLvl() {
		return lvl;
	}
	public String getSaveString() {
		return "" + lvl;
	}

	public SkillN(int lvl, boolean ismale) {
		if (lvl > SKILLMAX) {
			throw new Error("skill lvl too high: " + lvl);
		}
		if (lvl < 0) {
			throw new Error("skill lvl too low: " + lvl);
		}
		this.lvl = lvl;

		//so genders are grouped together
		lvlf = lvl + Math.random() / 2 + (ismale ? 0 : 0.5);
		str = lvl + "";
		col = new Color(255 * lvl / (SKILLMAX + 1), 0, 0);
	}
	public Color getColor() {
		return col;
	}
	public int compareTo(Object o) {
		SkillN s = (SkillN) o;
		double d = lvlf - s.lvlf;
		if (d > 0) {
			return 1;
		} 
		if (d < 0) {
			return -1;
		}
		return 0;
	}
	public boolean equals(Object o) {
		return this == o;
	}
	public String toString() {
		return str;
	}
}
