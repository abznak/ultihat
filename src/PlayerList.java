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


import java.awt.event.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

import java.util.Hashtable;
import java.util.List;
import java.util.Iterator;
import java.util.Vector;

import java.io.*;
import java.io.IOException;

import javax.swing.*;




public class PlayerList extends JList 
 implements DropTargetListener,DragSourceListener, DragGestureListener    {

  /**
   * enables this component to be a dropTarget.
	 * It is used, presuambly by reflection somewhere.
   */

  DropTarget dropTarget = null;

  /**
   * enables this component to be a Drag Source
   */
  DragSource dragSource = null;

	private final PlayerSelector ps;

	public Team getTeam() {
		return (Team)getModel();
	}

	public PlayerList(ListModel l, PlayerSelector ps) {
		super(l);
		this.ps = ps;
		init();
	}
	private void init() {
    dropTarget = new DropTarget (this, this);
    dragSource = new DragSource();
    dragSource.createDefaultDragGestureRecognizer( this, DnDConstants.ACTION_MOVE, this);
		setCellRenderer(new PlayerCellRenderer());

		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = locationToIndex(e.getPoint());
					Player p = (Player)(getModel().getElementAt(index));
					ps.selectPlayers(p.getBaggages());
				}
			}
		};
		addMouseListener(mouseListener);

	}
	public void selectPlayers(Vector players) {
		Team team = (Team)getModel();
		Iterator i = players.iterator();
		int sel[] = new int[players.size()];
		int seli=0;
		while (i.hasNext()) {
			int index = team.getIndex((Player)i.next());
			if (index != -1) {
				sel[seli] = index;
				seli++;
			}
		}
		int sel2[] = new int[seli];
		for (int j = 0; j < seli; j++) {
			sel2[j] = sel[j];
		}
		setSelectedIndices(sel2);
	}

  public void dragEnter(DropTargetDragEvent event) {
    event.acceptDrag(DnDConstants.ACTION_MOVE);
  }

  public void dragExit (DropTargetEvent event) {
  }

  public void dragOver(DropTargetDragEvent event) {
  }

  public void drop(DropTargetDropEvent event) {
    
    try {
        Transferable transferable = event.getTransferable();
                   
        // we accept only Players
        if (transferable.isDataFlavorSupported (PlayersTransfer.playerflavor)) {
        
            event.acceptDrop(DnDConstants.ACTION_MOVE);
            Object p[] = ((PlayersTransfer)transferable.getTransferData(
						 PlayersTransfer.playerflavor)).getPlayers();
						if (((Team)getModel()).contains(p[0])) {
							event.rejectDrop();  /* dont drop on self */
						} else {
							for (int i = 0; i < p.length; i++) {
		            addElement(p[i]);  /* after complete, in case we are moving to ourself */
							}
            	event.getDropTargetContext().dropComplete(true);
						}
        } else {
            event.rejectDrop();
        }
    } catch (IOException exception) {
        exception.printStackTrace();
        event.rejectDrop();
    } catch (UnsupportedFlavorException ufException ) {
      ufException.printStackTrace();
      event.rejectDrop();
    }
  }

  public void dropActionChanged(DropTargetDragEvent event) {
  }
  
  public void dragGestureRecognized(DragGestureEvent event) {
    Object selected = new PlayersTransfer((Object[])getSelectedValues());
    if ( selected != null){
			if (selected instanceof Transferable) {
        dragSource.startDrag (event, DragSource.DefaultMoveDrop, 
				 (Transferable)selected, this);
			} else {
        System.out.println("not transferable - " + selected.getClass());
			}
    } else {
        System.out.println("nothing was selected");   
    }
  }

  public void dragDropEnd (DragSourceDropEvent event) {   
    if ( event.getDropSuccess()){
        removeElement();
    }
  }

  public void dragEnter(DragSourceDragEvent event) {
  }

  public void dragExit(DragSourceEvent event) {
  }

  public void dragOver(DragSourceDragEvent event) {
  }

  public void dropActionChanged(DragSourceDragEvent event) {
  }

  public void addElement(Object p){
        ((Team)getModel()).add((Player)p);
  }

  /**
   * removes an element from itself
   */
   
  public void removeElement(){
		Team t = (Team)getModel();
		Object players[] = getSelectedValues();
		for (int i = 0; i < players.length; i++) {
	    t.remove((Player)players[i]);
		}
  }
  
}
class PlayerCellRenderer extends JLabel implements ListCellRenderer {
	private Player player;
	private Color cols[] = {Color.black, Color.gray, Color.yellow, Color.white};
	public PlayerCellRenderer() {
		setOpaque(true);
		setFont(new Font("Monospaced",Font.BOLD,12));
	}
	public Component getListCellRendererComponent(
		JList list,
		Object value,
		int index,
		boolean isSelected,
		boolean cellHasFocus)
	{
		if (value == null) {
			setText("null");
			return this;
		}
		Player p = (Player) value;
		this.player = p;
		setText("   " + p.toString());
		setBackground(p.getColor());
		Color c = (isSelected ? Color.blue : Color.green);
		if (!isSelected && p.getGender() == 'm') {
			c = c.darker();
		}
		setForeground(c);
		return this;
	}
	public void paint(Graphics g) {
		super.paint(g);
		if (player != null) {
			Skill s = player.getSkill();
			if (s instanceof SkillA) {
				int width = 3;
				int height = 17;
				int dy = 3;

				SkillA sa = (SkillA)s;
				g.setColor(Color.green);
				int sl[] = sa.getLvls();
				if (sl.length == 0) {
					g.drawString("?", 2, height - 1);
				}
	
				for (int i = 0; i < sl.length; i++) {
					int hit = sl[i]*dy;
					//g.setColor(i % 2 == 0 ? Color.blue : Color.green);
					if (sl[i] > 0) {
						g.setColor(getCol(sl[i]-1));
						g.fillRect(i * width, height-hit, width,  hit);
					}
				}
			}
		}
	}
	public Color getCol(int i) {
		i = Math.max(i, 0);	
		i = Math.min(i, cols.length - 1);
		return cols[i];
	}

}
