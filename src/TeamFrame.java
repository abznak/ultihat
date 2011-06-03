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


import javax.swing.*;

import java.awt.event.*;
import java.awt.*;
import javax.swing.event.*;

/* Used by InternalFrameDemo.java. */
public class TeamFrame extends JInternalFrame implements ListDataListener {
    private static int count = 0;
		private static boolean down = false;
    private static final int xs = 180, ys = 400;
		private final Team team;
		private final JLabel status;
		private PlayerList jl;
		public PlayerList getPlayerList() {
			return jl;
		}

    public TeamFrame(Team team, PlayerSelector ps) {
        super(team.getName(),
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable
				this.team = team;
	team.addListDataListener(this);

        setSize(xs,ys);

        //Set the window's location.
        setLocation(xs*count, down ? ys : 0);
				if (down) {
					count++;
				}
				down = !down;

		 		status = new JLabel();
				jl = new PlayerList(team, ps);
				update();
				getContentPane().setBackground(team.getColor());
				getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
				getContentPane().add(status);
				getContentPane().add(new JScrollPane(jl));
    }
		private void update() {
			status.setText(team.getStatus());
			double r = team.getRatio();
			status.setOpaque(true);
			status.setBackground(new Color((int)((1-r)* 255),0,(int)((r)*255)));
			status.setMinimumSize(new Dimension(xs, 24));
//			status.setForeground(new Color((int)((1-r)* 255),0,(int)((r)*255)));
		}
		public void contentsChanged(ListDataEvent e) {
			update();
		}
		public void intervalAdded(ListDataEvent e) {
			update();
		}
		public void intervalRemoved(ListDataEvent e) {
			update();
		}
}
