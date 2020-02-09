import java.util.Arrays;

public class Calculator {

	private byte zeilen;
	private byte spalten;
	private byte felder;
	
	private byte[] belegung;
	private byte siegWert;
	private byte foreSight;
	
	private final byte VERTIKAL = 0;
	private final byte DIAG_1 = 1;
	private final byte HORIZONTAL = 2;
	private final byte DIAG_2 = 3;
	
	private byte besterZug;
	private byte belegteFelder;
	private byte[][] spielfeld;
	private boolean gewonnen;
	//private 
	
	public Calculator(){
		this.zeilen = 6; 
		this.spalten = 7;
		this.felder = (byte)(zeilen*spalten);
		this.spielfeld = createSpielfeld();
		this.besterZug = 100;
		this.belegteFelder = getBelegteFelder();
		this.gewonnen = false;
		this.belegung = new byte[spalten];
		this.siegWert = (byte)(felder/2);
		this.foreSight = 6;
	}
	
	
	

	
	private byte calculate3(byte[][] spielfeld){
		//niemals ein gewonnenes Feld einspielen!
		boolean maxiAmZug = true;
		byte besterWert = (byte)(-siegWert);
		byte besterZug = 0;
		byte zuegeMaxi = 0;
		byte zuegeMini = 0;
		
		//spalten iiterieren
		for (byte spalte = 0; spalte < this.spalten; spalte++) { 
			if (belegung[spalte] < this.zeilen) {
				//Wert setzen
				spielfeld[belegung[spalte]][spalte] = 1;
				zuegeMaxi++;
				belegung[spalte]++;
				
				//Rekursion
				byte wert = calculate3(spielfeld, !maxiAmZug, zuegeMaxi,zuegeMini);
				System.out.println(wert);
				//Spielfeld rücksetzen
				belegung[spalte]--; ;
				spielfeld[belegung[spalte]][spalte] = 0;
				
				//Besten Wert Prüfen
				if(wert > besterWert){
					besterWert = wert;
					besterZug = spalte;
				}
				zuegeMaxi--;
			}
		}
		return besterZug;
	}
	
	private byte calculate3(byte[][] spielfeld, boolean maxiAmZug, byte zuegeMaxi, byte zuegeMini){
		byte besterWert = 0;
		if(maxiAmZug){
			if(check2(spielfeld)) {
				return  (byte) -(this.siegWert - zuegeMaxi);
			}
			besterWert = (byte) -siegWert;
		}else{
			if(check2(spielfeld)){
				return (byte) (this.siegWert - zuegeMini);
			}
			
			besterWert = siegWert;
		}
		
		if (zuegeMini < 4) {
			//spalten iterieren
			for (byte spalte = 0; spalte < this.spalten; spalte++) {
				if (belegung[spalte] < this.zeilen) {

					//Wert setzen
					if (maxiAmZug) {
						spielfeld[belegung[spalte]][spalte] = 1;
						belegung[spalte]++;
						zuegeMaxi++;
						//System.out.println("Maxi: " + zuegeMaxi);
					} else {
						spielfeld[belegung[spalte]][spalte] = -1;
						belegung[spalte]++;
						zuegeMini++;
						//System.out.println("Mini: " + zuegeMini);
					}

					//Rekursion
					byte wert = calculate3(spielfeld, !maxiAmZug, zuegeMaxi, zuegeMini);
					//Spielfeld rücksetzen
					belegung[spalte]--;
					
//					if(level == 2){
//						System.out.println(wert);
//					}
					
					spielfeld[belegung[spalte]][spalte] = 0;

					//Besten Wert Prüfen
					if (maxiAmZug) {
						if (wert > besterWert) {
							besterWert = wert;
						}
						zuegeMaxi--;
					} else {
						if (wert < besterWert) {
							besterWert = wert;
						}
						zuegeMini--;
					}
				}
			}
		}else{
			besterWert = 0;
		}
		//System.out.println();
		return besterWert;
	}
	
	public byte getBelegteFelder(){
		byte belegteFelder = 0;

		for (byte i = 0; i < this.zeilen; i++) {
			for (byte j = 0; j < this.spalten; j++) {
				if (spielfeld[i][j] != 0) {
					belegteFelder++;
				}
			}
		}
		return belegteFelder;
	}

	public byte[][] createSpielfeld(){
		byte[][] spielfeld = new byte[this.zeilen][this.spalten];
		
		for (byte i = 0; i < this.zeilen; i++) {
			for (byte j = 0; j < this.spalten; j++) {
				spielfeld[i][j] = 0;
			}
		}
		
		return spielfeld;
	}
	
	public boolean check2(byte[][] spielfeld){
		boolean gewonnen = false;
		byte length = 0;
		
		for (byte i = 0; i < this.zeilen; i++) {
			for (byte j = 0; j < this.spalten; j++) {
				length = 0;
				if (spielfeld[i][j] != 0) {
					byte richtung = VERTIKAL;
					try {
						if (i<zeilen-1) {
							if (spielfeld[i][j] == spielfeld[i + 1][j]) {
								richtung = VERTIKAL;
								length = 2;
								gewonnen = check2(spielfeld, length, VERTIKAL, (byte)(i+1), j);
								if (gewonnen)
									return gewonnen;
							} 
						}
						if (i<zeilen-1 && j<spalten-1) {
							if (spielfeld[i][j] == spielfeld[i + 1][j + 1]) {
								richtung = DIAG_1;
								length = 2;
								gewonnen = check2(spielfeld, length, DIAG_1, (byte)(i+1), (byte)(j+1));
								if (gewonnen)
									return gewonnen;
							} 
						}
						if (j<spalten-1) {
							if (spielfeld[i][j] == spielfeld[i][j + 1]) {
								richtung = HORIZONTAL;
								length = 2;
								gewonnen = check2(spielfeld, length, HORIZONTAL, i, (byte)(j+1));
								if (gewonnen)
									return gewonnen;
							} 
						}
						if (i<zeilen-1 && j>0) {
							if (spielfeld[i][j] == spielfeld[i + 1][j - 1]) {
								richtung = DIAG_2;
								length = 2;
								gewonnen = check2(spielfeld, length, DIAG_2, (byte)(i+1), (byte)(j - 1));
								if (gewonnen)
									return gewonnen;
							} 
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
					}
				}
			}
		}
		return gewonnen;
	}
	
	public boolean check2(byte[][] spielfeld,byte length, byte richtung, byte zeile, byte spalte){
		boolean gewonnen = false;
		
		//for (int i = zeile; i < this.zeilen; i++) {
			//for (int j = spalte; j < this.spalten; j++) {

					switch (richtung){
						case VERTIKAL: 	
							if(length == 4){
								gewonnen = true;
								return gewonnen;
							}
						if (zeile < this.zeilen-1) {
							if (spielfeld[zeile][spalte] == spielfeld[zeile + 1][spalte]) {
								length++;
								gewonnen = check2(spielfeld, length, VERTIKAL, (byte)(zeile+1), spalte);
								return gewonnen;
							}
						}
						length = 0;
						return false;
					//break;
						case DIAG_1: 	
							if(length == 4){
								gewonnen = true;
								return gewonnen;
							}
							if (zeile<zeilen-1 && spalte<spalten-1) {
								if (spielfeld[zeile][spalte] == spielfeld[zeile + 1][spalte + 1]) {
									length++;
									gewonnen = check2(spielfeld, length, DIAG_1, (byte)(zeile+1), (byte)(spalte+1));
									return gewonnen;
								} 
							}
							length=0;
							return false;
							//break;
						case HORIZONTAL: 	
							if(length == 4){ 
								gewonnen = true;
								return gewonnen;
							}
							if (spalte<spalten-1) {
								if (spielfeld[zeile][spalte] == spielfeld[zeile][spalte + 1]) {
									length++;
									gewonnen = check2(spielfeld, length, HORIZONTAL, zeile, (byte)(spalte+1));
									return gewonnen;
								} 
							}
							length=0;
							return false;
							//break;
						case DIAG_2: 	
							if(length == 4){
								gewonnen = true;
								return gewonnen;
							}
							if (zeile<zeilen-1 && spalte>0) {
								if (spielfeld[zeile][spalte] == spielfeld[zeile + 1][spalte - 1]) {
									length++;
									gewonnen = check2(spielfeld, length, DIAG_2, (byte)(zeile+1), (byte)(spalte-1));
									return gewonnen;
								} 
							}
							length=0;
							return false;
							//break;
					}
				
			//}
		//}
		return false;
	}
	
	
	public byte computerZug(){
		besterZug = calculate3(spielfeld);
		belegteFelder = getBelegteFelder();
		//System.out.println("besterZug: " + besterZug);
		this.gewonnen = check2(this.spielfeld);
		return besterZug;
	}
	
	private void printSpielfeld(byte[][] spielfeld){
		for (byte i = 0; i < this.zeilen; i++) {
			System.out.println(Arrays.toString(spielfeld[i]));
		}
		System.out.println();
	}
	
	private void searchZug(byte[][] spielfeld){
		
	}
	
	//Getter / Setter
	public void setSpielfeld(byte[][] spielfeld){
		this.spielfeld = spielfeld;
	}
	
	public byte[][] getSpielfeld(){
		return this.spielfeld;
	}
	
	public void setFeld(byte zeile, byte spalte, byte wert){
		this.spielfeld[zeile][spalte] = wert;
	}
	
	public void setBelegteFelder(){
		this.belegteFelder=getBelegteFelder();
	}
	
	public byte getZeilen(){
		return this.zeilen;
	}
	public void setBelegung(byte spalte){
		this.belegung[spalte]++;
	}
	
	public boolean getGewonnen(){
		gewonnen = check2(this.spielfeld);
		return gewonnen;
	}
	
	public byte getSpalten(){
		return this.spalten;
	}
	
	public byte getFelder(){
		return this.felder;
	}
	
	public byte[] getBelegung(){
		return this.belegung;
	}
	
	
	
	public static void main(String[] args) {
		/*Calculator cal = new Calculator();
		byte[][] sp = {{1,1,-1,-1,-1,1,0},{1,0,-1,-1,1,0,0},{1,0,-1,-1,-1,0,0},{-1,0,1,1,0,0,0},{0,0,0,0,0,0,0},{0,0,0,0,0,0,0}};
		//int[][] sp = {{1,1,-1,-1,0,-1,1},{-1,0,0,0,0,1,0},{0,0,0,0,0,0,0},{0,0,0,0,0,0,0},{0,0,0,0,0,0,0},{0,0,0,0,0,0,0}};		
		cal.setSpielfeld(sp);
		cal.setBelegteFelder();
		cal.belegung[0]=4;
		cal.belegung[1]=1;
		cal.belegung[2]=4;
		cal.belegung[3]=4;
		cal.belegung[4]=3;
		cal.belegung[5]=1;
		cal.belegung[6]=0;
		
		//System.out.println(cal.check2(cal.getSpielfeld()));
		System.out.println(cal.calculate3(cal.spielfeld));*/
		boolean[][] spielfeld = new boolean[3][3];

	}

}
//Archiv

//private byte calculate(byte[][] spielfeld, byte level){
//	byte[][] spielfeldTemp = spielfeld;
//	byte besterWert = 0;
//	if(level%2==0){
//		//Maxi am Zug
//		besterWert = -1; //worstCase
//	}else{
//		//Mini am Zug
//		besterWert = 1; //worstCase
//	}
//	
//	for (byte feld = 0; feld < this.spalten; feld++) {
//
//		try {
//			if (belegung[feld]<this.zeilen) {
//					besterZug = feld;
//				if (level % 2 == 0) {
//						//Maxi am Zug
//						spielfeldTemp[belegung[feld]][feld] = 1;
//						System.out.println(level);
//						printSpielfeld(spielfeldTemp);
//						//printSpielfeld(spielfeldTemp);
//						belegung[feld]++;
//						belegteFelder++;
//
//						if (check2(spielfeldTemp)) {
//							besterWert = 1;
//							System.out.println(level);
//							printSpielfeld(spielfeldTemp);
//							if (level == 0) {
//								//Siegeszug gefunden
//								besterZug = feld;
//							}
//							belegung[feld]--;
//							spielfeldTemp[belegung[feld]][feld] = 0;
//							belegteFelder--;
//							return besterWert; //auf vorheriges level zurückgehen
//						}
//					} else {
//						//Mini am Zug
//						spielfeldTemp[belegung[feld]][feld] = -1;
//						
//						//printSpielfeld(spielfeldTemp);
//						belegung[feld]++;
//						belegteFelder++;
//
//						if (check2(spielfeldTemp)) {
//							besterWert = -1;
//							System.out.println(level);
//							printSpielfeld(spielfeldTemp);
//							if (level == 0) {
//								//Siegeszug gefunden
//								besterZug = feld;
//							}
//							belegung[feld]--;
//							spielfeldTemp[belegung[feld]][feld] = 0;
//							belegteFelder--;
//							return besterWert; //auf vorheriges level zurückgehen
//						}
//					}
//
//					if ((belegteFelder == felder)||(level>4)) {
//						belegung[feld]--;
//						spielfeldTemp[belegung[feld]][feld] = 0;
//						belegteFelder--;
//						besterWert = 0;
//						//level--;
//						return besterWert;
//					}
//
//					//Rekursion
//					level++;
//					byte wert = calculate(spielfeldTemp, level);
//					level--;
//					belegung[feld]--;
//					spielfeldTemp[belegung[feld]][feld] = 0;
//					belegteFelder--;
//
//					if (level % 2 == 0) {
//						//Maxi am Zug
//						if (wert > besterWert) {
//							besterWert = wert;
//							if (level == 0) {
//								besterZug = feld;
//							}
//						}
//					} else {
//						//Mini am Zug
//						if (wert < besterWert) {
//							besterWert = wert;
//						}
//					}
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	return besterWert;
//}

//private byte calculate2(byte[][] spielfeld, byte level){
//	boolean maxi = true;
//	byte besterWert = 0;
//	if(level%2!=0){
//		//Mini am Zug am letzten Zug war Maxi dran
//		if(check2(spielfeld)) return 1;
//		maxi = false;
//		besterWert = 1;
//	}else{
//		//Maxi am Zug am letzten Zug war Mini dran
//		if(check2(spielfeld)){
//			return -1;
//		}
//		besterWert = -1;
//	}
//	if(belegteFelder == felder){
//		besterZug = 3;
//		return 0;
//	}
//	
//
//		for (byte spalte = 0; spalte < this.spalten; spalte++) {
//			if (belegung[spalte] < this.zeilen) {
//				if (spielfeld[belegung[spalte]][spalte] == 0) {
//					if (maxi) {
//						//Maxi am Zug
//						spielfeld[belegung[spalte]][spalte] = 1;
//						level++;
//						belegung[spalte]++;
//						belegteFelder++;
//					} else {
//						//Mini am Zug
//						spielfeld[belegung[spalte]][spalte] = -1;
//						level++;
//						belegung[spalte]++;
//						belegteFelder++;
//					}
//
//					byte wert = calculate2(spielfeld, level);
//					belegung[spalte]--;
//					level--;
//					belegteFelder--;
//					spielfeld[belegung[spalte]][spalte] = 0;
//
//					if (maxi) {
//						//Maxi am Zug
//						if (wert == 1) {
//							if (level == 0) {
//								besterZug = spalte;
//							}
//							return 1;
//						} else if (wert > besterWert) {
//							if (level == 0) {
//								besterZug = spalte;
//							}
//							besterWert = wert;
//						}
//					} else {
//						//Mini am Zug
//						if (wert == -1) {
//							
//							return -1;
//						} else if (wert < besterWert) {
//							
//							besterWert = wert;
//						}
//					}
//				} 
//			}
//		} 
//	
//	return besterWert;
//}