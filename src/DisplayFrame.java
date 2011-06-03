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

import javax.swing.JInternalFrame;
import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/*
 * DisplayFrame.java is a 1.4 application that requires:
 *   TeamFrame.java
 */
public class DisplayFrame extends JFrame
                               implements ActionListener, PlayerSelector {
    JDesktopPane desktop;
		Teams teams;
		Vector teamframes = new Vector();
		String sourcefile;

    public DisplayFrame(String sourcefile) {
        super("ultihat");
				this.sourcefile = sourcefile;

        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                  screenSize.width  - inset*2,
                  screenSize.height - inset*2);

        //Set up the GUI.
        desktop = new JDesktopPane(); //a specialized layered pane
        createFrame(); //create first "window"
        setContentPane(desktop);
        setJMenuBar(createMenuBar());

        //Make dragging a little faster but perhaps uglier.
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    }

    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        //Set up the lone menu.
        JMenu menu = new JMenu("Document");
        menu.setMnemonic(KeyEvent.VK_D);
        menuBar.add(menu);


        //Set up the first menu item.
        JMenuItem menuItem = new JMenuItem("New");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("new");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        //Set up the second menu item.
        menuItem = new JMenuItem("Quit");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("quit");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        //Set up the second menu item.
        menuItem = new JMenuItem("Clear");
        menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_C, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("clear");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        //Set up the second menu item.
        menuItem = new JMenuItem("Save");
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("save");
        menuItem.addActionListener(this);
        menu.add(menuItem);


        menu = new JMenu("Teams");
        menu.setMnemonic(KeyEvent.VK_T);
        menuBar.add(menu);

        menuItem = new JMenuItem("Simple Hat");
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("simplesort");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem("Simple Hat 2");
        menuItem.setMnemonic(KeyEvent.VK_2);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("simplesort2");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem("Baggage Swapping");
        menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_B, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("baggageswap");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem("Baggage Swapping 2");
        menuItem.setMnemonic(KeyEvent.VK_G);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_G, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("baggageswap2");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem("All");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_A, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("all");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem("Show Stats");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_A, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("stats");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        return menuBar;
    }

    //React to menu selections.
    public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
        if (cmd.equals("new")) {
					System.out.println("new NYI");
//            createFrame();
				} else if (cmd.equals("quit")) {
            quit();
        } else if (cmd.equals("clear")) { 
						teams.clear();
        } else if (cmd.equals("simplesort")) {
						teams.simpleSort();
        } else if (cmd.equals("simplesort2")) {
						teams.simpleSort2();
        } else if (cmd.equals("baggageswap")) {
						teams.baggageSwap();
        } else if (cmd.equals("baggageswap2")) {
						teams.baggageSwap2();
				} else if (cmd.equals("stats")) {
						teams.printHappyStats();
        } else if (cmd.equals("all")) {
						teams.clear();
						teams.simpleSort2();
						teams.baggageSwap();
						teams.baggageSwap2();
				} else if (cmd.equals("save")) {
					try {
						File f = new File(System.currentTimeMillis() + ".tsv");
						teams.saveToFile(f);
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				} else {
					System.out.println("UNKNOW COMMAND: " + cmd);
				}
    }

    //Create a new internal frame.
    protected void createFrame() {
			try {
				teams = new Teams(new File(sourcefile));
				Iterator i = teams.iterator();
				addTeam(teams.getNoTeam());
				while (i.hasNext()) {
					addTeam((Team)i.next());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
    }

		private void addTeam(Team t) {
	        try {
        	TeamFrame frame = new TeamFrame(t, this);
					teamframes.add(frame);
        	frame.setVisible(true); 
	        desktop.add(frame);
 	           frame.setSelected(true);
 	       } catch (java.beans.PropertyVetoException e) {
						e.printStackTrace();
				 }
		}
		public void selectPlayers(Vector players) {
			Iterator i = teamframes.iterator();
			while (i.hasNext()) {
				TeamFrame tf = (TeamFrame)i.next();
				tf.getPlayerList().selectPlayers(players);
			}
		}


    //Quit the application.
    protected void quit() {
        System.exit(0);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI(String source) {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        DisplayFrame frame = new DisplayFrame(source);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Display the window.
        frame.setVisible(true);
    }

    public static void main(String[] args) {
				String s = "default.tsv";
				if (args.length > 0) {
					s = args[0];
				}
				final String source = s;
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(source);
            }
        });
    }
}
interface PlayerSelector {
	public void selectPlayers(Vector players);
}
