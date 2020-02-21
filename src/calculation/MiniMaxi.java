package calculation;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static global.GlobalConstants.*;

public class MiniMaxi extends Observable {
	private List<Observer> beobachter = new ArrayList<>();

	private final int IMPOSSIBLE = 2000;
	private final int VICTORY = 500;
	private final int[] scores = {0,0,15,30,VICTORY,VICTORY,VICTORY,VICTORY};
	
	private byte[][] spielfeld;
	private byte[] belegung;
	private byte besteSpalte;
	private byte level;
	private int felder;
	private int belegteFelder;
	
	public MiniMaxi(Observer beobachter) {
		init();
		this.beobachter.add(beobachter);
	}
	
	public void init() {
		besteSpalte = -1;
		level = 5;
		belegung = new byte[SPALTEN];
		spielfeld = new byte[ZEILEN][SPALTEN];
		felder = ZEILEN*SPALTEN;
		belegteFelder = 0;
		for(Observer b : beobachter) {
			b.update(this, null);
		}
	}
	
	public byte getBesteSpalte() {
		return besteSpalte;
	}
	
	public byte[][] getSpielfeld() {
		return spielfeld;
	}
	
	public boolean isGewonnen(int score) {
		return score == VICTORY;
	}
	
	private int computerZug() {
		mini((byte) 0, (byte) -VICTORY);
		int score = machZug(besteSpalte, COMPUTER, (byte) 0); 
		
		for(Observer b : beobachter) {
			b.update(this, null);
		}
		
		return score;
	}
	
	public byte menschZug(byte spalte) {
		int score = machZug(spalte, MENSCH, (byte) 0);
		if(isGewonnen(score)) return SIEG_MENSCH;
		score = computerZug();
		if(isGewonnen(score)) return SIEG_COMPUTER;
		if(isVoll()) return UNENTSCHIEDEN;
		return NOCH_KEIN_SIEG;
	}
	
	public boolean isVoll() {
		int belegteFelder = 0;
		for(int zeile = 0; zeile < ZEILEN; zeile ++) {
			for(int spalte = 0; spalte < SPALTEN; spalte ++) {
				if(spielfeld[zeile][spalte] != 0) belegteFelder++;
			}
		}
		return belegteFelder == felder;
	}
	
	//computer
	private int mini(byte tiefe, int alpha) {		
		int minimalScore = IMPOSSIBLE;
		for (byte spalte = 0; spalte < SPALTEN; spalte++) {
			if(belegung[spalte] >= ZEILEN) continue;
			
			int score = -machZug(spalte, COMPUTER, tiefe);
			
			if(!(-score + tiefe == VICTORY || tiefe == level || belegteFelder == felder)) {
				score = maxi((byte) (tiefe + 1), minimalScore);
			}
			
			if(score < alpha && tiefe > 0) {
				//maxi wird Zug nicht zulassen
				loescheZug(spalte);
				return score;
			}
			
			if(score < minimalScore) {
				//besseren zug gefunden
				minimalScore = score;
				if(tiefe == 0) {
					besteSpalte = spalte;
				}
			}
			loescheZug(spalte);
		}
		
		return minimalScore;
	}
	
	//mensch
	private int maxi(byte tiefe, int beta) {
		int maximalScore = - IMPOSSIBLE;
		for (byte spalte = 0; spalte < SPALTEN; spalte++) {
			if(belegung[spalte] >= ZEILEN) continue;
			
			int score = machZug(spalte, MENSCH, tiefe);
			
			if(!(score + tiefe == VICTORY || tiefe == level || belegteFelder == felder)) {
				score = mini((byte) (tiefe + 1), maximalScore);
			}
			
			if(score > beta && tiefe > 0) {
				//mini wird Zug nicht zulassen
				loescheZug(spalte);
				return score;
			}
			
			if(score > maximalScore) {
				//besseren zug gefunden
				maximalScore = score;
				if(tiefe == 0) {
					besteSpalte = spalte;
				}
			}
			loescheZug(spalte);
		}
		
		return maximalScore;
	}
	
	private int machZug(byte spalte, byte spieler, byte tiefe) {	
		spielfeld[belegung[spalte]][spalte] = spieler;
		int score = berechneScore(belegung[spalte], spalte, spieler);
		belegteFelder++;
		belegung[spalte]++;
		return score - tiefe;
	}
	
	private void loescheZug(byte spalte) {
		belegung[spalte]--;
		spielfeld[belegung[spalte]][spalte] = 0;
		belegteFelder--;
	}
	
	public int berechneScore(byte zeile, byte spalte, byte spieler){
		int score = horizontal(zeile, spalte, spieler);
		if(score >= VICTORY) return VICTORY;
		
		score += diag1(zeile, spalte, spieler);
		if(score >= VICTORY) return VICTORY;
		
		score += diag2(zeile, spalte, spieler);
		if(score >= VICTORY) return VICTORY;
		
		score += runter(zeile, spalte, spieler);
		if(score >= VICTORY) return VICTORY;
		
		return score;
	}
	
	private int horizontal(byte zeile, byte spalte, byte spieler) {
		int maxPunkte = 0;
		int start = 0;
		boolean stop = false;
		int j = 0;
		while(!stop && j < 3) {
			try {
				j ++;
				byte feld = spielfeld[zeile][spalte-j];
				if(feld == spieler) {
					start = j;
				}else if(feld != 0) {
					throw new RuntimeException();
				}
			} catch(RuntimeException e) {
				stop = true;
			}
		}
		
		stop = false;
		
		while(!stop) {
			int i = 0;
			int punkte = 0;
			while(!stop && i < 4) {
				try {
					byte feld = spielfeld[zeile][spalte-start+i];
					if(feld == spieler) {
						punkte ++;
					} else if(feld != 0) {
						throw new RuntimeException();
					}
					i++;
				} catch(RuntimeException e) {
					stop = true;
				}
			}
			if(punkte > maxPunkte) {
				maxPunkte = punkte;
			}
			start++;
		}
		return scores[maxPunkte]+10;
	}
	
	private int diag1(byte zeile, byte spalte, byte spieler) {
		int maxPunkte = 0;
		int start = 0;
		boolean stop = false;
		int j = 0;
		while(!stop && j < 3) {
			try {
				j ++;
				byte feld = spielfeld[zeile-j][spalte-j];
				if(feld == spieler) {
					start = j;
				}else if(feld != 0) {
					throw new RuntimeException();
				}
			} catch(RuntimeException e) {
				stop = true;
			}
		}
		
		stop = false;
		
		while(!stop) {
			int i = 0;
			int punkte = 0;
			while(!stop && i < 4) {
				try {
					byte feld = spielfeld[zeile-start+i][spalte-start+i];
					if(feld == spieler) {
						punkte ++;
					} else if(feld != 0) {
						throw new RuntimeException();
					}
					i++;
				} catch(RuntimeException e) {
					stop = true;
				}
			}
			if(punkte > maxPunkte) {
				maxPunkte = punkte;
			}
			start++;
		}
		return scores[maxPunkte];
	}
	
	private int diag2(byte zeile, byte spalte, byte spieler) {
		int maxPunkte = 0;
		int start = 0;
		boolean stop = false;
		int j = 0;
		while(!stop && j < 3) {
			try {
				j ++;
				byte feld = spielfeld[zeile+j][spalte-j];
				if(feld == spieler) {
					start = j;
				}else if(feld != 0) {
					throw new RuntimeException();
				}
			} catch(RuntimeException e) {
				stop = true;
			}
		}
		
		stop = false;
		
		while(!stop) {
			int i = 0;
			int punkte = 0;
			while(!stop && i < 4) {
				try {
					byte feld = spielfeld[zeile+start-i][spalte-start+i];
					if(feld == spieler) {
						punkte ++;
					} else if(feld != 0) {
						throw new RuntimeException();
					}
					i++;
				} catch(RuntimeException e) {
					stop = true;
				}
			}
			if(punkte > maxPunkte) {
				maxPunkte = punkte;
			}
			start++;
		}
		return scores[maxPunkte];
	}
	
	private int runter(byte zeile, byte spalte, byte spieler) {
		int punkte = 0;
		
		for(int i = 0; i < 4; i++) {
			try {
				byte feld = spielfeld[zeile--][spalte];
				if(feld == spieler) {
					punkte++;
				} else {
					break;
				}
			} catch(RuntimeException e) {
				break;
			}
		}
		
		return scores[punkte];
	}
	
//	public static void main(String[] args) {
//		MiniMaxi mm = new MiniMaxi(null);
//		byte[][] sf = {
//				{0,0,0,0,0,0,0},
//				{0,0,0,0,0,0,0},
//				{0,0,0,0,0,0,0},
//				{0,0,0,0,0,0,0},
//				{0,0,0,0,0,0,0},
//				{2,0,2,2,2,0,2}};
//		mm.spielfeld = sf;
//		System.out.println(mm.horizontal((byte)5, (byte)2, (byte)2));
//		
//		byte[][] sf2 = {
//				{1,0,0,0,0,0,0},
//				{0,1,0,0,0,0,0},
//				{0,0,1,0,0,0,0},
//				{0,0,0,1,0,0,0},
//				{0,0,0,0,2,0,0},
//				{1,0,2,2,2,1,0}};
//		mm.spielfeld = sf2;
//		System.out.println(mm.diag1((byte)3, (byte)3, (byte)1));
//		
//		byte[][] sf3 = {
//				{0,0,0,0,0,0,2},
//				{0,0,0,0,2,1,0},
//				{0,0,0,1,2,0,0},
//				{0,0,2,0,0,0,0},
//				{0,2,2,0,0,0,0},
//				{1,0,2,2,2,1,0}};
//		mm.spielfeld = sf3;
//		System.out.println(mm.diag2((byte)2, (byte)4, (byte)2));
//		
//		byte[][] sf4 = {
//				{0,0,2,0,0,0,0}, //0
//				{0,0,2,0,2,0,0}, //1
//				{0,0,2,0,0,0,0}, //2
//				{0,0,0,0,0,0,0}, //3
//				{0,2,1,0,0,0,0}, //4
//				{1,0,2,2,2,1,0}};//5
//		mm.spielfeld = sf4;
//		System.out.println(mm.runter((byte)2, (byte)2, (byte)2));
//	}
}
