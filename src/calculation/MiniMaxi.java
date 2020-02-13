package calculation;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static global.GlobalConstants.*;

public class MiniMaxi extends Observable {
	private List<Observer> beobachter = new ArrayList<>();

	private final byte VICTORY = 127;
	private final byte[] scores = {0,0,15,30,VICTORY,VICTORY,VICTORY,VICTORY};
	
	private byte[][] spielfeld;
	private byte[] belegung;
	private byte besteSpalte;
	private byte level;
	private byte zeilen;
	private byte spalten;
	private int felder;
	
	private int belegteFelder;
	
	private boolean gewonnen;
	
	//bester zug mensch
	private byte alpha;
	
	//bester zug computer
	private byte beta;
	
	public MiniMaxi(Observer beobachter) {
		besteSpalte = -1;
		level = 6;
		zeilen = ZEILEN;
		spalten = SPALTEN;
		belegung = new byte[spalten];
		spielfeld = new byte[zeilen][spalten];
		felder = zeilen*spalten;
		belegteFelder = 0;
		gewonnen = false;
		this.beobachter.add(beobachter);
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
	
	public boolean isGewonnen(byte score) {
		return score == VICTORY;
	}
	
	private byte computerZug() {
		mini((byte) 0, (byte) -VICTORY);
		byte score = machZug(besteSpalte, COMPUTER); 
		
		for(Observer b : beobachter) {
			b.update(this, null);
		}
		
		return score;
	}
	
	public byte menschZug(byte spalte) {
		byte score = machZug(spalte, MENSCH);
		if(isGewonnen(score)) return SIEG_MENSCH;
		score = computerZug();
		if(isGewonnen(score)) return SIEG_COMPUTER;
		if(isVoll()) return UNENTSCHIEDEN;
		return NOCH_KEIN_SIEG;
	}
	
	public boolean isVoll() {
		belegteFelder = 0;
		for(int zeile = 0; zeile < zeilen; zeile ++) {
			for(int spalte = 0; spalte < spalten; spalte ++) {
				if(spielfeld[zeile][spalte] != 0) belegteFelder++;
			}
		}
		return belegteFelder == felder;
	}
	
	//computer
	private byte mini(byte tiefe, byte alpha) {		
		byte minimalScore = VICTORY;
		for (byte spalte = 0; spalte < this.spalten; spalte++) {
			if(belegung[spalte] >= zeilen) continue;
			
			byte score = (byte) - machZug(spalte, COMPUTER);
			
			if(score < alpha && tiefe > 0) {
				//maxi wird Zug nicht zulassen
				loescheZug(spalte);
				continue;
			}
			
			if(score == -VICTORY || tiefe == level) {
				if(tiefe == 0) {
					besteSpalte = spalte;
					//gewonnen = true;
				}
				loescheZug(spalte);
				return score;
			}
			
			score = maxi((byte) (tiefe + 1), minimalScore);
			
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
	private byte maxi(byte tiefe, byte beta) {
		byte maximalScore = - VICTORY;
		for (byte spalte = 0; spalte < this.spalten; spalte++) {
			if(belegung[spalte] >= zeilen-1) continue;
			
			byte score = (byte) machZug(spalte, MENSCH);
			
			if(score > beta && tiefe > 0) {
				//maxi wird Zug nicht zulassen
				loescheZug(spalte);
				continue;
			}
			
			if(score == VICTORY || tiefe == level) {
				if(tiefe == 0) {
					besteSpalte = spalte;
					//gewonnen = true;
				}
				loescheZug(spalte);
				return score;
			}
			
			score = mini((byte) (tiefe + 1), maximalScore);
			
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
	
	private byte machZug(byte spalte, byte spieler) {	
		spielfeld[belegung[spalte]][spalte] = spieler;
		byte score = berechneScore(belegung[spalte], spalte);
		
		belegung[spalte]++;
		return score;
	}
	
	private void loescheZug(byte spalte) {
		belegung[spalte]--;
		spielfeld[belegung[spalte]][spalte] = 0;
	}
	
	public byte berechneScore(byte zeile, byte spalte){
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
