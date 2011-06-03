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


import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.awt.Color;

public class Teams {
	private final Vector teams = new Vector();
	private final Team noteam = new Team("noteam", Color.black);
	private final HashMap players = new HashMap();
	public Teams() {
	}
	/*
	 * move all people from teams into noteam
	 */
	public void clear() {
		Iterator ti = iterator();
		while (ti.hasNext()) {
			Team t = (Team)ti.next();
			t.dumpTo(noteam);
		}
	}
	public Team getNoTeam() {
		return noteam;
	}
	public Teams(File source) throws IOException {
		addFromFile(source);
	}
	public Iterator iterator() {
		return teams.iterator();
	}

	public void saveToFile(File dest) throws IOException {
		PrintStream out = new PrintStream(new FileOutputStream(dest));
		Iterator i = iterator();
		while (i.hasNext()) {
			Team t = (Team)i.next();
			t.saveToFile(out);
			System.out.println(t.getName() + " " + t.getStatus());
		}
		noteam.saveToFile(out);
	}

	private void addFromFile(File source) throws IOException {
		BufferedReader buf = new BufferedReader(new FileReader(source));
		Team ct = null; /* currentteam */
		//Pattern p = Pattern.compile("([^\t}]*)\t([^\t]*)\t([^\t]*)\t(.)\t(.)");
		Pattern p = Pattern.compile("\"?([^\t\"]*)\"?\t\"?([^\t\"]*)\"?\t\"?([^\t\"]*)\"?\t\"?([^\t\"]*)\"?\t\"?([^\t\"]*)\"?\t\"?([^\t\"]*)(.*)");
		int line = 0;
		while (buf.ready()) {
			String red = buf.readLine();
			line++;
			try {
				Matcher mat = p.matcher(red);
				if (mat.matches()) {
					int i = 0;
					String teamname = mat.group(++i);
					String fname = mat.group(++i);
					String sname = mat.group(++i);
					char gender = mat.group(++i).charAt(0);
					int skill = Integer.parseInt(mat.group(++i));
					String skillstr = mat.group(++i);
					if (ct == null || !ct.getName().equals(teamname)) {
						if (teamname.equals("noteam")) {
							ct = noteam;
						} else {
							ct = new Team(teamname, Color.red); /* TODO: fix color */
							teams.add(ct);
						}
					}
					Player pp = new Player(fname, sname, skill, gender, skillstr);
					players.put(pp.getName(), pp);
					ct.add(pp);
					pp.setBaggages(mat.group(++i));
					System.out.println("match:" + mat.group(1)+ mat.group(2)+ mat.group(3));
				} else {
					System.out.println("no match - " + red);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("At line " + line);
			}
		}

		//Convert baggage strings to Players
		Iterator i = players.values().iterator();
		while (i.hasNext()) {
			Player pp = (Player)i.next();
			pp.fixBaggage(players);
		}
		i = teams.iterator();
		while (i.hasNext()) {
			Team t = (Team)i.next();
			t.fixBaggage();
		}
		//System.out.println(players);
	}
	public static void main(String args[]) throws Exception {
		Teams t = new Teams(new File(args[0]));
		System.out.println(t);
	}
	public String toString() {
		return teams.toString();
	}
	/**
	 * take people out of noteam and put them in teams
	 */
	public void simpleSort() {
		Iterator i = iterator();
		while (noteam.size() > 0) {
			if (!i.hasNext()) {
				i = iterator();
			}
			Team t = (Team)i.next();
			Player p = (Player)noteam.top();
			noteam.remove(p);
			t.add(p);
		}
	}
	/**
	 * take people out of noteam and put them in teams
	 */
	public void simpleSort2() {
		Vector t2 = new Vector();
		Iterator i = null;

		while (noteam.size() > 0) {
			if (t2.size() == 0) {
				t2.addAll(teams);
			}
			Player p = (Player)noteam.top();
			Team t = genderPick(t2, p.isMale());
			t2.remove(t);
			noteam.remove(p);
			t.add(p);
		}
	}
	/**
	 * find the best team from t2 to place
	 * a player that is male iff male
	 */
	private Team genderPick(Vector t2, boolean male) {
		Vector t3 = new Vector();
		int maxm = -1;
		Iterator i = t2.iterator();
		while (i.hasNext()) {
			Team t = (Team)i.next();
			if (t.getM() > maxm) {
				maxm = t.getM();
			}
		}
		i = t2.iterator();
		while (i.hasNext()) {
			Team t = (Team)i.next();
			if (male) {
				if (t.getM() != maxm) {
					t3.add(t);
				}
			} else {
				if (t.getM() == maxm) {
					t3.add(t);
				}
			}
		}
		if (t3.size() == 0) {
			t3.addAll(t2);
		}
		return skillPick(t3);
	}
	/**
	 * pick the team with the lowest skill level
	 */
	private Team skillPick(Vector t3) {
		Iterator i = t3.iterator();
		Vector t4 = new Vector();
		float smin = Float.MAX_VALUE;
		while (i.hasNext()) {
			Team t = (Team)i.next();
			if (t.getSkillScore() < smin) {
				smin = t.getSkillScore();
			}
		}
		i = t3.iterator();
		while (i.hasNext()) {
			Team t = (Team)i.next();
			if (t.getSkillScore() == smin) {
				t4.add(t);
			}
		}
		return maxHappyPick(t4);
	}
	/**
	 * pick the team with the lowest max happiness (baggage) score
	 */
	private Team maxHappyPick(Vector t4) {
		Iterator i = t4.iterator();
		int hmin = Integer.MAX_VALUE;
		Team tmin = null;
		while (i.hasNext()) {
			Team t = (Team)i.next();
			if (t.getMaxHappyScore() < hmin) {
				hmin = t.getMaxHappyScore();
				tmin = t;
			}
		}
		return tmin;
	}

	public void baggageSwap() {
		baggageSwap(1000);
	}
	public void baggageSwap2() {
		baggageSwap2(1000);
	}
	/**
	 * swap people onto teams to be with their friends
	 */
	public void baggageSwap(int swaps) {
		System.out.println("Running BAGGAGESWAP " + swaps);
		for (int i = 0; i < swaps; i++) {
			Player p = findUnhappy();
			if (p == null) {
				System.out.println("No unhappy players at i = " + i);
				return;
			}
			System.out.print(" ");
			Iterator friends = p.getBaggages().iterator();
FRIENDZ: while (friends.hasNext()) {
				Player fnd = (Player)friends.next();
				Player swap = fnd.getTeam().getSwap(p, fnd);
				if (swap != null) {
					System.out.println("FOUND: ");
					System.out.println("\tp:\t" + p);
					System.out.println("\tfriend:\t" + fnd);
					System.out.println("\tswap:\t" + swap);
					swap(p, swap);
					System.out.print("!");
					break FRIENDZ;
				} else {
					System.out.print("X");
				}
			}
		}
		System.out.println("Done baggage swap");
	}
	/**
	 * swap friends onto teams to be with their people
	 * TODO: rewrite to avoid moving friends out of teams as swap
	 */
	public void baggageSwap2(int swaps) {
		System.out.println("Running BAGGAGESWAP2 " + swaps);
		for (int i = 0; i < swaps; i++) {
			Player p = findUnhappy2();
			if (p == null) {
				System.out.println("\nNo unhappy2 players at i = " + i);
				return;
			}
			System.out.print(" ");
			Iterator friends = p.getBaggages().iterator();
FRIENDZ: while (friends.hasNext()) {
				Player fnd = (Player)friends.next();
				if (fnd.getTeam() == p.getTeam()) {
					continue;
				}
				Player swap = p.getTeam().getSwap(fnd, p);
				if (swap != null) {
					System.out.println("\nFOUND2: ");
					System.out.println("\tp:\t" + p);
					System.out.println("\tfriend:\t" + fnd);
					System.out.println("\tswap:\t" + swap);
					swap(fnd, swap);
					System.out.print("!");
					break FRIENDZ;
				} else {
					System.out.print("X");
				}
			}
		}
		System.out.println("Done baggage swap");
	}
	private final Random r = new Random();

	public Vector findUnhappyVector() {
		Vector unhappy = new Vector();
		if (players.size() == 0) {
			System.out.println("no players");
			return unhappy;
		}
		Iterator i = players.values().iterator();
		while (i.hasNext()) {
			Player p = (Player)i.next();
			if (p.isUnhappy()) {
				unhappy.add(p);
			}
		}
		return unhappy;
	}
	public Player findUnhappy() {
		Vector unhappy = findUnhappyVector();
		if (unhappy.size() == 0) {
			return null;
		}
		return (Player)unhappy.elementAt(r.nextInt(unhappy.size()));
	}
	public void printHappyStats() {
		System.out.println("Unhappy Count: " + findUnhappyVector().size());
		System.out.println("Unhappy2 Count: " + findUnhappyVector2().size());
	}

	public Vector findUnhappyVector2() {
		Vector unhappy = new Vector();
		if (players.size() == 0) {
			System.out.println("no players");
			return unhappy;
		}
		Iterator i = players.values().iterator();
		while (i.hasNext()) {
			Player p = (Player)i.next();
			if (p.getHappyness() != p.getMaxHappyness()) {
				unhappy.add(p);
			}
		}
		return unhappy;
	}
	public Player findUnhappy2() {
		Vector unhappy = findUnhappyVector2();
		if (unhappy.size() == 0) {
			return null;
		}
		return (Player)(unhappy.elementAt(r.nextInt(unhappy.size())));
	}




	private void swap(Player a, Player b) {
		Team aa = a.getTeam();
		Team bb = b.getTeam();
		aa.remove(a);
		bb.remove(b);
		aa.add(b);
		bb.add(a);
	}
				
		


	/**
	 * take people out of noteam and put them in teams
	 *
	public void simpleGenderSort() {
		Iterator i = iterator();
		while (noteam.size() > 0) {
			if (!i.hasNext()) {
				i = iterator();
			}
			Team t = (Team)i.next();
			Player p = (Player)noteam.top();
			noteam.remove(p);
			t.add(p);
		}
	}
	*/
}

