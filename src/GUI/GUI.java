package GUI;
import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;

import calculation.MiniMaxi;

import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import static global.GlobalConstants.*;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUI implements Observer{

	private JFrame frame;
	
	private MiniMaxi cal;
	
	private Spalte[] spalten;
	
	private JPanel playfield;
	
	private JLabel lblStatus;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
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
	public GUI() {
		cal = new MiniMaxi(this);
		
		initialize();
		
		spalten = new Spalte[SPALTEN];
		for(byte i = 0; i < SPALTEN; i ++) {
			spalten[i] = new Spalte(this, cal, i);
			playfield.add(spalten[i]);
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 634, 494);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		playfield = new JPanel();
		frame.getContentPane().add(playfield, BorderLayout.CENTER);
		playfield.setLayout(new GridLayout(1, 7, 0, 0));
		
		JPanel southPanel = new JPanel();
		southPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		frame.getContentPane().add(southPanel, BorderLayout.SOUTH);
		southPanel.setLayout(new BorderLayout(0, 0));
		
		JButton reset = new JButton("reset");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		southPanel.add(reset, BorderLayout.CENTER);
		
		JPanel northPanel = new JPanel();
		frame.getContentPane().add(northPanel, BorderLayout.NORTH);
		northPanel.setLayout(new BorderLayout(0, 0));
		
		lblStatus = new JLabel("playing");
		northPanel.add(lblStatus, BorderLayout.CENTER);
	}

	@Override
	public void update(Observable o, Object arg) {
		byte[][] spielfeld = cal.getSpielfeld();
		
		for(byte spalte = 0; spalte < SPALTEN; spalte ++) {
			byte[] spalteArr = new byte[ZEILEN];
			for(byte zeile = 0; zeile < ZEILEN; zeile ++) {
				spalteArr[zeile] = spielfeld[zeile][spalte];
			}
			
			spalten[spalte].updateSpalte(spalteArr);
		}
	}
	
	public void setStatus(String status) {
		this.lblStatus.setText(status);
	}
	
	private void reset() {
		for(byte spalte = 0; spalte < SPALTEN; spalte ++) {
			spalten[spalte].reset();
		}
		cal.init();
	}
}
