import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Window.Type;

public class VierGewinnt {

	private JFrame frame;
	
	private Calculator cal;
	
	private byte[][] spielfeld;
	
	private JPanel[] spaltenPanels;
	
	private byte zeilen;
	private byte spalten;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VierGewinnt window = new VierGewinnt();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public VierGewinnt() {
		cal = new Calculator();
		this.zeilen = cal.getZeilen();
		this.spalten = cal.getSpalten();
		this.spielfeld = cal.getSpielfeld();
		spaltenPanels = new JPanel[this.spalten];
		initialize();
	}
	

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setType(Type.UTILITY);
		frame.setBounds(100, 100, 624, 670);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.setResizable(false);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new GridLayout(1, cal.getSpalten(), 2, 2));
		panel_1.setBackground(Color.BLACK);
		
		JPanel panel_2 = new JPanel();
		frame.getContentPane().add(panel_2, BorderLayout.NORTH);
		panel_2.setBackground(Color.BLACK);
		
		JLabel siegLabel = new JLabel(" ");
		siegLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
		siegLabel.setBackground(Color.BLACK);
		siegLabel.setForeground(Color.WHITE);
		panel_2.add(siegLabel);
		
		for(byte spalte=0; spalte<this.spalten; spalte++){
			spaltenPanels[spalte] = new JPanel();
			spaltenPanels[spalte].setBackground(Color.WHITE);
			spaltenPanels[spalte].setLayout(new GridLayout(this.zeilen, 1, 2,2));
			JPanel[] felder = new JPanel[this.zeilen];

			byte i = spalte;
			spaltenPanels[spalte].addMouseListener(new MouseListener(){
				
				@Override
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mousePressed(MouseEvent e) {
					JPanel jP = new JPanel(); 
					jP.setBackground(Color.YELLOW);
					spaltenPanels[i].add(jP);
					frame.validate();

					spielfeld[cal.getBelegung()[i]][i]=-1;
					cal.setSpielfeld(spielfeld);
					cal.setBelegung(i);
					if(cal.getGewonnen()){  
						siegLabel.setText("wtf...");
						return;
					}
					
					
					byte zug = cal.computerZug();
					spielfeld[cal.getBelegung()[zug]][zug]=1;
					JPanel jP_2 = new JPanel();
					jP_2.setBackground(Color.RED);
					spaltenPanels[zug].add(jP_2);
					cal.setSpielfeld(spielfeld);
					cal.setBelegung(zug);
					frame.validate();
					if(cal.getGewonnen()){
						siegLabel.setText("haha, du hast verloren!!!");
					}else if(cal.getBelegteFelder()==cal.getFelder()){
						siegLabel.setText("unentschieden, nicht schlecht");
					}
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					spaltenPanels[i].setBackground(Color.BLACK);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					spaltenPanels[i].setBackground(Color.WHITE);
				}
			});

					
				
			
			panel_1.add(spaltenPanels[spalte]);
		}
		
		JButton btnReset = new JButton("reset");
		btnReset.setFont(new Font("Tahoma", Font.PLAIN, 24));
		btnReset.setBackground(Color.WHITE);
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cal = new Calculator();
				panel_1.removeAll();
				spielfeld = cal.getSpielfeld();
				siegLabel.setText(" ");
				for(byte spalte=0; spalte<cal.getSpalten(); spalte++){
					spaltenPanels[spalte] = new JPanel();
					spaltenPanels[spalte].setBackground(Color.WHITE);
					spaltenPanels[spalte].setLayout(new GridLayout(cal.getZeilen(), 1, 2,2));
					JPanel[] felder = new JPanel[cal.getZeilen()];
					
					byte i = spalte;
					spaltenPanels[spalte].addMouseListener(new MouseListener(){
						
						@Override
						public void mouseClicked(MouseEvent e) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void mousePressed(MouseEvent e) {
							JPanel jP = new JPanel(); 
							jP.setBackground(Color.YELLOW);
							spaltenPanels[i].add(jP);
							frame.validate();

							spielfeld[cal.getBelegung()[i]][i]=-1;
							cal.setSpielfeld(spielfeld);
							cal.setBelegung(i);
							if(cal.getGewonnen()){  
								siegLabel.setText("wtf...");
								return;
							}
							
							
							byte zug = cal.computerZug();
							spielfeld[cal.getBelegung()[zug]][zug]=1;
							JPanel jP_2 = new JPanel();
							jP_2.setBackground(Color.RED);
							spaltenPanels[zug].add(jP_2);
							cal.setSpielfeld(spielfeld);
							cal.setBelegung(zug);
							frame.validate();
							if(cal.getGewonnen()){
								siegLabel.setText("haha, du hast verloren!!!");
							}else if(cal.getBelegteFelder()==cal.getFelder()){
								siegLabel.setText("unentschieden, nicht schlecht");
							}
							
						}

						@Override
						public void mouseReleased(MouseEvent e) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void mouseEntered(MouseEvent e) {
							// TODO Auto-generated method stub
							spaltenPanels[i].setBackground(Color.BLACK);
						}

						@Override
						public void mouseExited(MouseEvent e) {
							// TODO Auto-generated method stub
							spaltenPanels[i].setBackground(Color.WHITE);
						}
					});

							
						
					
					panel_1.add(spaltenPanels[spalte]);
				}
				frame.validate();
			}
		});
		panel.add(btnReset);
	}
	private void reset(){
		
	}

}
