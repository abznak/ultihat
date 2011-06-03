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


import javax.swing.AbstractListModel;
import java.util.*;
import java.awt.Color;
import java.io.*;

public class Team extends AbstractListModel {
	private final String name;
	private final Color col;
	private int m = 0, f = 0;
	private float skillscore = 0;
	private final TreeSet pset = new TreeSet();
	private int maxhappy = 0;
	private String skilla = "";
	private Player players[] = new Player[0]; /* copy of pset, made by fix() */

	public void makeSkillA() {
		if (players.length == 0) {
			skilla = ""; return;
		}
		if (players[0] == null) {
			skilla = ""; return;
		}
		if (!(players[0].getSkill() instanceof SkillA)) {
			skilla = ""; return;
		}
		int s[] = new int[SkillA.M];
		int cnt = 0;
		for (int i = 0; i < players.length; i++) {
			SkillA sa = (SkillA)players[i].getSkill();
			int lvl[] = sa.getLvls();
			if (lvl.length > 0) {
				cnt++;
				for (int j = 0; j < lvl.length; j++) {
					s[j] += lvl[j];
				}
			}
		}
		
		String ret = "";
		if (cnt > 0) {
			for (int j = 0; j < s.length; j++) {
				ret += SkillA.NAMES[j] + ": " + ((int)(s[j]*10/cnt)) + "   ";	
			}
		}
		skilla = ret;
	}
	public int getM() {
		return m;
	}
	public int getF() {
		return f;
	}
	public float getSkillScore() {
		return skillscore;
	}
	public int getMaxHappyScore() {
		return maxhappy;
	}



	public void saveToFile(PrintStream out) {
		for (int i = 0; i < players.length; i++) {
			out.println(name + "\t" + players[i].getSaveString());
		}
	}
	public Team(String name, Color col) {
		this.name = name;
		this.col = col;
	}
	public String getName() {
		return name;
	}
	public Color getColor() {
		return col;
	}
	public Iterator iterator() {
		return pset.iterator();
	}

	/**
	 * get this teams top player
	 */
	public Player top() {
		return (Player)getElementAt(0);
	}

	public Object getElementAt(int i) {
		return players[i];
	}
	/**
	 * move all players out of this team into dest.
	 * terribly inefficient, but I don't care
	 */
	public void dumpTo(Team dest) {
		while (size() > 0) {
			Player move = (Player)getElementAt(0);
			remove(move);
			dest.add(move);
		}
	}
	public void fixBaggage() {
		for (int i = 0; i < players.length; i++) {
			maxhappy += players[i].getMaxHappyness();
			for (int j = 0; j < players.length; j++) {
				players[i].teamedWith(players[j]);
			}
		}
	}
	public void add(Player p) {
		p.setTeam(this);
		maxhappy += p.getMaxHappyness();
		for (int i = 0; i < players.length; i++) {
			players[i].teamedWith(p);
			p.teamedWith(players[i]);
		}
		pset.add(p);
		if (p.getGender() == 'm') {
			m++;
		} else {
			f++;
		}
		skillscore += ((SkillN)p.getSkill()).getSkillLvl();
		fix();
		int i = getIndex(p);
		fireIntervalAdded(this, i, i);
		System.out.println("add: " + p + " at " + i);
	}
	public boolean contains(Object p) {
		return pset.contains(p);
	}
	public int getIndex(Player p) {
		for (int i = 0; i < players.length; i++) {
			if (p == players[i]) {
				return i;
			}
		}
		return -1;
	}
	public void remove(Player p) {
		maxhappy -= p.getMaxHappyness();
		for (int i = 0; i < players.length; i++) {
			players[i].unteamedWith(p);
			p.unteamedWith(players[i]);
		}
		pset.remove(p);
		if (p.getGender() == 'm') {
			m--;
		} else {
			f--;
		}
		skillscore -= ((SkillN)p.getSkill()).getSkillLvl();
		fix();
		fireIntervalRemoved(this, 0, size());
		System.out.println("remove: " + p);
	}
	public double getRatio() {
		return (double)m / size();
	}
	public int getSize() {
		return size();
	}
	public int size() {
		return players.length;
	}
	private void fix() {
		players = (Player[]) pset.toArray(new Player[0]);
	}
	public String toString() {
		return "[TEAM: "+ name + " " + size() + "]";
	}
	public String getStatus() {
		makeSkillA();
		return "  t: " + size() + "   m: " + m + "   f: " + f + "   s: " + (int)skillscore + "   " + skilla;
	}
	/**
	 * find someone on this team that player
	 * p can swap with so they 
	 * are with friend fnd
	 */
	public Player getSwap(Player p, Player fnd) {
		for (int i = 0; i < players.length; i++) {
			Player pot = players[i];
			if (((((SkillN)p.getSkill()).getSkillLvl() == 
			 ((SkillN)pot.getSkill()).getSkillLvl())) &&
			 p.getGender() == pot.getGender() &&
			 pot.getHappyness() == 0 &&
			 pot != fnd) {
				return pot;
			}
		}
		return null;
	}
}
