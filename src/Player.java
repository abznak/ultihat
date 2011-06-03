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


import java.awt.datatransfer.*;
import java.awt.*;
import java.util.*;

public class Player implements Comparable {
	private static int pnn = 0;
	private final int pn = pnn++;

	private Team team;
	private final String name;
	private final String fname;
	private final String sname;
	private final Skill skill;
	private final char gender;
	/**
	 * number of people on my team that I want there
	 */
	private int happyness = 0;

	/**
	 * number of people that want me on this team
	 */
	private int stickyness = 0;
	
	//how many people baggage link to or from this player
	private int linkcount = 0;
	
	public String baggagesstr;
	public final Vector baggages = new Vector();

	public String getSaveString() {
		return fname + "\t" + sname + "\t" + gender + "\t" + skill.getSaveString() + "\t" + baggagesstr;
	}

	public int getHappyness() {
		return happyness;
	}
	public boolean isUnhappy() {
		if (baggages.size() == 0) {
			return false;
		}
		return getHappyness() == 0;
	}
	public int getMaxHappyness() {
		return baggages.size();
	}
	public void setTeam(Team t) {
		this.team = t;
	}
	public Team getTeam() {
		return team;
	}
	public void teamedWith(Player p) {
		if (baggages.contains(p)) {
			happyness++;
			p.stickyness++;
		}
	}
	public void unteamedWith(Player p) {
		if (baggages.contains(p)) {
			happyness--;
			p.stickyness--;
		}
	}



	public String getName() {
		return name;
	}
	public Skill getSkill() {
		return skill;
	}
	public int getLinkCount() {
		return linkcount;
	}
	public void fixBaggage(HashMap h) {
		StringTokenizer toks = new StringTokenizer(baggagesstr, "\t\"", false);
		while (toks.hasMoreTokens()) {
			String pstr = toks.nextToken();
			Player p = (Player)h.get(pstr);
			if (p == null) {
				System.out.println("UNKNOWN FRIEND FOR " + getName() + ": '" + pstr + "'");
			} else {
				baggages.add(p);
				p.linkcount++;
			}
			//System.out.println(getName() + "\t" + toks.nextToken());
		}
	}
	public Vector getBaggages() {
		return baggages;
	}
	public void addBaggages(Player p) {
		this.baggages.add(p);
	}
	public void setBaggages(String p) {
		this.baggagesstr = p;
	}
	public char getGender() {
		return gender;
	}
	public boolean isMale() {
		return gender == 'm' || gender == 'M';
	}
	public Player(String fname, String sname, int skill, 
		 char gender, String skillstr) {
		this.name = fname + " " + sname;
		this.fname = fname;
		this.sname = sname;
		//this.skill = new SkillN(skill, isMale()); 
		this.skill = new SkillA(skill, skillstr, isMale()); 
		this.gender = gender;
	}
	public String toString() {
		String ret = skill + " " + (gender == 'm' ? " " : "*") + " " + getBaggageStats() + " " + name + " --- ";
		Iterator i = baggages.iterator();
		while (i.hasNext()) {
			ret += ((Player)i.next()).getName() + ", ";
		}
		return ret;
	}
	public String getBaggageStats() {
		if (baggages.size() == 0) {
			return "   " + getStickyStats();
		} else {
			return happyness + "/" + baggages.size()+getStickyStats();
		}
	}
	private String getStickyStats() {
		return (stickyness == 0 ? "  " : " " + stickyness);
	}
	public Color getColor() {
		return skill.getColor();
	}

	public int compareTo(Object o) {
		Player p = (Player)o;
		int ret = p.getSkill().compareTo(getSkill());
		if (ret == 0) {
			if ((getGender() == 'm') && (p.getGender() == 'f')) {
				ret = -1;
			}
			if ((getGender() == 'f') && (p.getGender() == 'm')) {
				ret = 1;
			}
		}
		if (ret == 0) {
			ret = name.compareTo(p.name);
		}
		if (ret == 0) {
			ret = pn - p.pn;
		}
		return ret;
	}

}
class PlayersTransfer implements Transferable {
	private Object[] p;  /* should be players */
	public PlayersTransfer(Object p[]) {
		this.p = p;
	}
	public Object[] getPlayers() {
		return p;
	}

	public static final DataFlavor playerflavor = new DataFlavor(Player.class, "Player");
	private DataFlavor[] dataflavors = {
	 playerflavor
	};
	public DataFlavor[] getTransferDataFlavors() {
		return dataflavors;
	}
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for (int i = 0; i < dataflavors.length; i++) {
			if (flavor == dataflavors[i]) {
				return true;
			}
		}
		return false;
	}
	public Object getTransferData(DataFlavor flavor)
	 throws UnsupportedFlavorException {
		if (flavor == playerflavor) {
			return this;
		}
		throw new UnsupportedFlavorException(flavor);
	}
}
