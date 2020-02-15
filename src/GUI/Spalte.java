package GUI;
import static global.GlobalConstants.*;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import calculation.MiniMaxi;

public class Spalte extends JPanel {

	private JPanel[] felder;
	private MiniMaxi cal;
	private byte spalte;
	private GUI gui;
	
	public Spalte(GUI gui, MiniMaxi cal, byte spalte) {
		this.cal = cal;
		this.spalte = spalte;
		this.gui = gui;
		setLayout(new GridLayout(ZEILEN, 1, 0, 0));
		
		felder = new JPanel[ZEILEN];
		
		for(int i = 0; i < ZEILEN; i++) {
			felder[i] = new JPanel();
			felder[i].setBorder(new LineBorder(new Color(0, 0, 0)));
			this.add(felder[i]);
			felder[i].setBackground(Color.WHITE);
		}
		
		JPanel jp = this;
		
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				machZug(SPIELER_MENSCH);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				jp.setBackground(Color.BLACK);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				jp.setBackground(Color.WHITE);
			}
			
		});
	}
	
	public void updateSpalte(byte[] spalte) {
		for(byte i = 0; i < ZEILEN; i++) {
			if(spalte[i] == COMPUTER) {
				felder[ZEILEN-1-i].setBackground(Color.RED);
				continue;
			}
			
			if(spalte[i] == MENSCH) {
				felder[ZEILEN-1-i].setBackground(Color.YELLOW);
				continue;
			}
			
			felder[ZEILEN-1-i].setBackground(Color.WHITE);
		}
	}
	
	public void machZug(boolean spieler) {
		byte state = cal.menschZug(spalte);
		
		handleState(state);
	}
	
	public void reset() {
		for(JPanel jp : felder) {
			jp.setBackground(Color.WHITE);
		}
	}
	
	private void handleState(byte state) {
		switch(state) {
			case NOCH_KEIN_SIEG: 
				gui.setStatus("spiel läuft...");
				break;
			case SIEG_COMPUTER: 
				gui.setStatus("du hast verloren, du Idiot! HAHAHA");
				break;
			case SIEG_MENSCH: 
				gui.setStatus("ach du scheisse!");
				break;
			case UNENTSCHIEDEN: 
				gui.setStatus("wir sind wohl beide gleich dumm");
				break;
		}
	}

}
