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
	private byte zeilen;
	private byte spalten;
	private int felder;
	private int belegteFelder;
	
	public MiniMaxi(Observer beobachter) {
		init();
		this.beobachter.add(beobachter);
	}
	
	public void init() {
		besteSpalte = -1;
		level = 10;
		zeilen = ZEILEN;
		spalten = SPALTEN;
		belegung = new byte[spalten];
		spielfeld = new byte[zeilen][spalten];
		felder = zeilen*spalten;
		belegteFelder = 0;
		for(Observer b : beobachter) {
			b.update(this, null);
		}
	}
	
	public byte getBesteSpalte() {
		return besteSpalte;
	}
	
	public byte getZeilen() {
		return zeilen;
	}
	
	public byte getSpalten() {
		return spalten;
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
		for(int zeile = 0; zeile < zeilen; zeile ++) {
			for(int spalte = 0; spalte < spalten; spalte ++) {
				if(spielfeld[zeile][spalte] != 0) belegteFelder++;
			}
		}
		return belegteFelder == felder;
	}
	
	//computer
	private int mini(byte tiefe, int alpha) {		
		int minimalScore = IMPOSSIBLE;
		for (byte spalte = 0; spalte < this.spalten; spalte++) {
			if(belegung[spalte] >= zeilen) continue;
			
			int score = -machZug(spalte, COMPUTER, tiefe);
			
			if(score < alpha && tiefe > 0) {
				//maxi wird Zug nicht zulassen
				loescheZug(spalte);
				continue;
			}
			
			if(!(-score + tiefe == VICTORY || tiefe == level || belegteFelder == felder)) {
				score = maxi((byte) (tiefe + 1), minimalScore);
			}
			
			if(score <= minimalScore) {
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
		for (byte spalte = 0; spalte < this.spalten; spalte++) {
			if(belegung[spalte] >= zeilen) continue;
			
			int score = machZug(spalte, MENSCH, tiefe);
			
			if(score > beta && tiefe > 0) {
				//mini wird Zug nicht zulassen
				loescheZug(spalte);
				continue;
			}
			
			if(!(score + tiefe == VICTORY || tiefe == level || belegteFelder == felder)) {
				score = mini((byte) (tiefe + 1), maximalScore);
			}
			
			if(score >= maximalScore) {
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
		try {
			spielfeld[belegung[spalte]][spalte] = spieler;
		}catch(Exception e) {
			System.out.println("spalte: " + spalte 
					+ "\ttiefe: " + tiefe 
					+ "\tbelegung: " + belegung[spalte]);
		}
		int score = berechneScore(belegung[spalte], spalte);
		belegteFelder++;
		belegung[spalte]++;
		return score - tiefe;
	}
	
	private void loescheZug(byte spalte) {
		belegung[spalte]--;
		spielfeld[belegung[spalte]][spalte] = 0;
		belegteFelder--;
	}
	
	public int berechneScore(byte zeile, byte spalte){
		byte score = 0;
		
		byte laenge = moveUpRight((byte) 1, zeile, spalte);
		laenge += moveDownLeft((byte) 0, zeile, spalte);
		if(laenge > 3) return VICTORY;
		score += scores[laenge];
		
		laenge = moveRight((byte) 1, zeile, spalte);
		laenge += moveLeft((byte) 0, zeile, spalte);
		if(laenge > 3) return VICTORY;
		score += scores[laenge];
		
		laenge = moveDownRight((byte) 1, zeile, spalte);
		laenge += moveUpLeft((byte) 0, zeile, spalte);
		if(laenge > 3) return VICTORY;
		score += scores[laenge];
		
		laenge = moveDown((byte) 1, zeile, spalte);
		if(laenge > 3) return VICTORY;
		score += scores[laenge];
		
		return score;
	}
	
	private byte moveUpRight(int laenge, int zeile, int spalte) {
		if(spalte < spalten-1 && zeile < zeilen-1 
				&& spielfeld[zeile][spalte] == spielfeld[zeile + 1][spalte + 1]) {
			return moveUpRight(laenge+1, zeile+1, spalte+1);
		}
		return (byte) laenge;
	}
	
	private byte moveRight(int laenge, int zeile, int spalte) {
		if(spalte < spalten-1 
				&& spielfeld[zeile][spalte] == spielfeld[zeile][spalte + 1]) {
			return moveRight(laenge+1, zeile, spalte+1);
		}
		return (byte) laenge;
	}
	
	private byte moveDownRight(int laenge, int zeile, int spalte) {
		if(spalte < spalten-1 && zeile > 0 
				&& spielfeld[zeile][spalte] == spielfeld[zeile - 1][spalte + 1]) {
			return moveDownRight(laenge+1, zeile-1, spalte+1);
		}
		return (byte) laenge;
	}
	
	private byte moveDown(int laenge, int zeile, int spalte) {
		if(zeile > 0 
				&& spielfeld[zeile][spalte] == spielfeld[zeile - 1][spalte]) {
			return moveDown(laenge+1, zeile-1, spalte);
		}
		return (byte) laenge;
	}
	
	private byte moveDownLeft(int laenge, int zeile, int spalte) {
		if(spalte > 0 && zeile > 0 
				&& spielfeld[zeile][spalte] == spielfeld[zeile - 1][spalte - 1]) {
			return moveDownLeft(laenge+1, zeile-1, spalte-1);
		}
		return (byte) laenge;
	}
	
	private byte moveLeft(int laenge, int zeile, int spalte) {
		if(spalte > 0 
				&& spielfeld[zeile][spalte] == spielfeld[zeile][spalte - 1]) {
			return moveLeft(laenge+1, zeile, spalte-1);
		}
		return (byte) laenge;
	}
	
	private byte moveUpLeft(int laenge, int zeile, int spalte) {
		if(spalte > 0 && zeile < zeilen-1 
				&& spielfeld[zeile][spalte] == spielfeld[zeile + 1][spalte - 1]) {
			return moveUpLeft(laenge+1, zeile+1, spalte-1);
		}
		return (byte) laenge;
	}

}
