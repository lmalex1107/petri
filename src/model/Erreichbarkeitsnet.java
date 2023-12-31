package model;

import java.awt.Paint;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Klasse zur Erstellung eines Erreichbarkeitsgraphen
 * @author Alexander Adams
 *
 */
public class Erreichbarkeitsnet {
	ArrayList<ERKnot> erknotlist;
	ArrayList<ERArc> erarclist;
	ERKnot firstERKnot;
	ERKnot actual_ERKnot;
	boolean Doublette = false;
	boolean finity = true;
	ArrayList<ERKnot> PathToInfinity = null;
	ArrayList<ERArc> ArcToInfinity = null;					//Liste der Kanten des 
	private ERKnot Knot_m1;									//entspricht Knoten m
	private ERKnot Knot_m2;									//entspricht Knoten m'
	
	/** 
	 * Erstellt ein neues Erreichbarkeitsnetz
	 */
	public Erreichbarkeitsnet (int[] startmarking) {
		erknotlist = new ArrayList<ERKnot> ();
		erarclist = new ArrayList<ERArc> ();
		int[] startmarkingcopy = Arrays.copyOf(startmarking, startmarking.length);
		firstERKnot = new ERKnot(startmarkingcopy);
		firstERKnot.setInitialKnot(true);
		erknotlist.add(firstERKnot);
		System.out.println("First-Test");
		testprint();
	}
	
	/**
	 * Ändert die Selektion an einer bestimmten Stelle.
	 */
	public void changeSelection (String label) {
		for (int i = 0; i < this.erknotlist.size(); i++) {
			if (this.erknotlist.get(i).getLabel().equals(label)) {
				if (this.erknotlist.get(i).getSelection() == false) {
					this.erknotlist.get(i).setSelection(true);
				}
				else {
					this.erknotlist.get(i).setSelection(false);
				}
				
			}
			else {
				this.erknotlist.get(i).setSelection(false);
			}
		}
		
	}
	
	/**
	 * Setzt die Selektion überall auf false.
	 * 
	 */
	public void changeSelection () {
		for (int i = 0; i < this.erknotlist.size(); i++) {
			this.erknotlist.get(i).setSelection(false);
		}
	}
	
	/**
	 * Gibt die Anzahl der Knoten des Erreichbarkeitsnetzes zurück. Dies ist wichtig für die Angaben 
	 * beschränkter Petrinetze.
	 */
	public int getNumberOfKnots() {
		return this.erknotlist.size() ;
	}
	
	/**
	 * Gibt die Anzahl der Kanten des Erreichbarkeitsnetzes zurückt. Dies ist wichtig für die Angaben 
	 * beschränkter Petrinetze.
	 */
	public int getNumberOfArcs() {
		return this.erarclist.size();
	}
	
	/**
	 * Gibt die Liste der Knoten zurück
	 */
	public ArrayList<ERKnot> getERknotlist() {
		return this.erknotlist;
	}
	
	/**
	 * Gibt die Liste der Kanten zurück.
	 * @return
	 */
	public ArrayList<ERArc> getERArclist(){
		return this.erarclist;
	}
	
	/**
	 * Meldet zurück, ob eine Dublette eingefügt werden sollte.
	 * @return
	 */
	public boolean isDublette() {
		return this.Doublette;
	}
	
	/**
	 * Gibt die Knoten zurück, die auf dem Pfad liegen, auf dem sich m und m' befinden.
	 * @return
	 */
	public ArrayList<ERKnot> getPathToInfinity() {
		return this.PathToInfinity;
	}
	
	/**
	 * Gibt die Kanten zurück, die auf dem Pfad liegen, auf dem sich m und m' befinden.
	 * @return
	 */
	public ArrayList<ERArc> getArcToInfinity() {
		return this.ArcToInfinity;
	}
	
	/**
	 * 
	 * Rückgabe der Knoten m und m'
	 * 
	 * erknot1[0] = m'
	 * erknots[1] = m
	 */
	public ERKnot[] getInfinityKnots() {
		ERKnot[] erknots = new ERKnot[2];
			erknots[0] = this.Knot_m1;
			erknots[1] = this.Knot_m2;
		return erknots;
	}
	
	
	
	/**
	 * In der Methode 'insert_knot ('Knoten einfügen') wird eine Liste von Markierungen zur Bildung eines neuen Knotens übergeben.
	 * Die zweite Markierungsliste zeigt an, an welcher Stelle, also an welchen Vorgänger-Knoten, 
	 * der neue Knoten angefügt wird. Außerdem gibt eine ID an, welcher Transitor im zugehörigen Petri-
	 * Netz geschaltet wurde.
	 * @param ERKnot
	 * @param ERSuccessor
	 * @param TransitionID
	 */
	public void insert_knot(int[] Marking, int[] ERSuccessor, String TransitionID) {	//überarbeiten!!!
		String current_label = ERKnot.MarkingToString(Marking);
		String former_label = ERKnot.MarkingToString(ERSuccessor);
		this.Doublette = false;
		
		if (find(current_label) == null) {										//Knoten noch nicht vorhanden - einfügen
			ERKnot erknot = new ERKnot(Marking);
			
			
			if (find(former_label) != null) {
				erknot.setPredecessor(find(former_label));
				find(former_label).setSuccessor(erknot);						//Vorgänger, Nachfolger eintragen
				
				ERArc erarc = new ERArc (TransitionID);
				erarc.setSource(former_label);
				erarc.setTarget(current_label);
				this.erarclist.add(erarc);
				
			}
			erknotlist.add(erknot);
			System.out.println("Knoten zum ersten Mal aufgetaucht.");
		}
		else {																				//Wenn Knoten bereits vorhanden...
			System.out.println("Knoten bereits vorhanden.");
			if (find(former_label) != null) {
				boolean found = false;
				for (int i = 0; i < this.erarclist.size(); i++) {							//Dieser Überprüfung soll Dubletten ausschließen
					if (this.erarclist.get(i).getId() == TransitionID) {
						if ((this.erarclist.get(i).getSource().equals(former_label) == true) && (this.erarclist.get(i).getTarget().equals(current_label))){
							found = true;
							this.Doublette = true;
						}
					}
				}
				if (found == false) {														//Verbindungen setzen
					find(current_label).setPredecessor(find(former_label));
					find(former_label).setSuccessor(find(current_label));
					
					ERArc erarc = new ERArc (TransitionID);
					erarc.setSource(find(former_label).getLabel());
					erarc.setTarget(find(current_label).getLabel());
					this.erarclist.add(erarc);
				}
			}
			
		}
		
		
		testprint();
		findingPaths();
	}
	
	
	/**
	 * Gibt den Knoten zuürck, dessen Label bergeben wurde
	 *
	 */
	public ERKnot find (String label) {
		ERKnot erknot = null;
		boolean found = false;
		int i = 0;
		do {
			if (this.erknotlist.get(i).getLabel().equals(label)) {
				found = true;
				erknot = this.erknotlist.get(i);
			}
			else {
				i++;
			}
		}
		while ((found == false) && (i < this.erknotlist.size()));
		
		
		return erknot;
	}
	
	/**
	 * Die nachfolgende Methode überprüft, ob der Knoten bereits vorhanden ist
	 * und aktualisiert die vorhandenen Verbindungen zu ebendiesem Knoten.
	 */
	private void complete_connections() {
		
	}
	
	/**
	 * Das Herzstück der Klasse und des gesamten Programmes. Hierin wird überprüft, ob
	 * der Erreichbarkeitsgraph endlich ist oder nicht. Wenn ja, dann wird isinfinity auf true gesetzt
	 * @return
	 */
	public void findingPaths() {
		System.out.println("Knoten im Erreichbarkeitsnetz ohne Nachfolger:");			//Test
		for (int i = 0; i < this.erknotlist.size(); i++) {
			if (this.erknotlist.get(i).getSuccessor().size() == 0) {
				ArrayList<ERKnot>Path = new ArrayList<ERKnot>();
				Path.add(this.erknotlist.get(i));
				ERKnot actuelle_ERKnot = this.erknotlist.get(i);
				System.out.println(Path.get(Path.size() - 1).getLabel());
				intersection(Path);
			}																			//dabei alle Pfade nehmend.
		}
		System.out.println("-------+++++-----");										//Test
	}
	
	//Methode, die benötigt wird, wenn der Pfad auf eine Abzweigung stößt 
	private void intersection(ArrayList<ERKnot> Path) {
		if (Path.get(Path.size() - 1).getPredecessor().size() != 0) {
			System.out.println("Schleifentest VI");
			System.out.println(Path.get(Path.size() -1 ).getLabel());
			System.out.println(Path.get(Path.size() - 1).getPredecessor().size());
			for (int i = 0; i < Path.get(Path.size() - 1).getPredecessor().size(); i++) {	
				if (knotalreadyadded (Path, Path.get(Path.size() - 1).getPredecessor().get(i)) == false) {
					ArrayList<ERKnot> new_Path = new ArrayList<ERKnot>();
					for (int j = 0; j < Path.size(); j++) {	
						new_Path.add(Path.get(j));
					}
					new_Path.add(Path.get(Path.size() - 1).getPredecessor().get(i));
					intersection(new_Path);
				}
			}
		}
		else {
			System.out.println("+++++-----++++++");											//Test
			System.out.println("Gültiger Pfad gefunden");									//Test
			for (int k = 0; k < Path.size(); k++) {											//Test
				System.out.println(Path.get(k).getLabel()  + Path.get(k).Testmarking());	//Test
			}																				//Test
			System.out.println("-----+++++------");											//Test
			for (int l = 0; l < Path.size() ; l++) {
				System.out.println(Path.get(l).getLabel() + Path.get(l).Testmarking());
				for (int m = l + 1; m < Path.size(); m++) {
					System.out.println("   " + Path.get(m).getLabel() + " " + Path.get(m).Testmarking() );
					System.out.println("   " + m_compare(Path.get(l).getMarking(), Path.get(m).getMarking()));
					if (m_compare(Path.get(l).getMarking(), Path.get(m).getMarking()) == true) {
						findingPathtoInfinity(Path, Path.get(l) , Path.get(m) );
						this.finity = false;
					break;
					}
					
				}
				if (this.finity == false) {
					break;
				}
			}
		}
	}
	
	
	//Die Methode findingPathtoInfinity liefert den Teil des Erreichbarkeitsgraphen zurück, 
	//der m und m' enthält.
	private void findingPathtoInfinity (ArrayList<ERKnot> Path, ERKnot Knot1, ERKnot Knot2) {
		System.out.println("K n o t e n 1 :" + Knot1.getLabel());				//Test
		System.out.println("K n o t e n 2 :" + Knot2.getLabel());				//Test
		for (int test = 0; test < Path.size(); test++) {						//Test
			System.out.println(Path.get(test).getLabel());
		}
		
		ArrayList<ERKnot> temp_Path = new ArrayList<ERKnot>();
		int i = 0;
		do {
			if (Path.get(i).getLabel().equals(Knot1.getLabel()) == true) {
				for (int l = i; l < Path.size(); l++) {
					Path.get(l).setPartOfInfinityPath(true);
					temp_Path.add(Path.get(l));
				}
			}
			i++;
		}
		while ((i < Path.size()) && (Path.get(i).getLabel().equals(Knot1.getLabel()) == false));
		
		ArrayList<ERArc> temp_Arc = new ArrayList<ERArc>();
		for (int j = 0; j < temp_Path.size() - 1; j++) {
			for (int k = 0; k < this.erarclist.size(); k++) {
				if ((this.erarclist.get(k).getSource().equals(temp_Path.get(j +1).getLabel())) && (this.erarclist.get(k).getTarget().equals(temp_Path.get(j).getLabel()))){
					this.erarclist.get(k).setPartOfInfinityPath(true);
					temp_Arc.add(this.erarclist.get(k));
				}
			}
		}
		
		if (PathToInfinity == null) {
			this.PathToInfinity = temp_Path;
			this.ArcToInfinity = temp_Arc;
			this.Knot_m1 = Knot1;
			this.Knot_m1.setknot_mx(1);
			this.Knot_m2 = Knot2;
			this.Knot_m2.setknot_mx(2);
		}
		
		
		System.out.println("Knoten-Test");							//Test
		for (int test2 = 0; test2 < temp_Arc.size(); test2++) {		//Test
			System.out.println(temp_Arc.get(test2).getId());		//Test
		}
	}
	
	/**
	 * überprüft, ob Knoten bereits vorhanden
	 * @param knotlist Liste der Knoten
	 * @param erknot zu überprüfender Knoten
	 * @return Wahrheitswert
	 */
	private boolean knotalreadyadded(ArrayList<ERKnot> knotlist, ERKnot erknot){
		boolean knotalreadyadded = false;
		for (int i = 0; i < knotlist.size(); i++) {
			if (knotlist.get(i).getLabel().equals(erknot.getLabel())) {
				knotalreadyadded = true;
				System.out.println("Knoten bereits im Erreichbarkeitsgraph vorhanden");
			}
		}
		return knotalreadyadded;
	}
	
	/**
	 * gibt zurück, ob das Erreichnbarkeitsnetz endlich oder unendlich ist
	 * @return
	 */
	public boolean isfinity() {
		return this.finity;
	}
	
	/**
	 * Testausdruck
	 * 
	 */
	public void testprint () {
		System.out.println("");
		System.out.println("aktueller Erreichbarkeitsgraph: ");
		for (int i = 0; i < this.erknotlist.size(); i++) {
			System.out.println(this.erknotlist.get(i).getLabel());
			if (this.erknotlist.get(i).getPredecessor().size() > 0) {
				System.out.println("VORGÄNGER ");
				for (int j = 0; j < this.erknotlist.get(i).getPredecessor().size(); j++) {
					System.out.println("   " + this.erknotlist.get(i).getPredecessor().get(j).getLabel());
				}
			}
			
			
		}
		System.out.println("");
	}
	
	/**
	 * Die Markierungen zweier Knoten werden verglichen. Das Ergebnis ist true, wenn
	 * @param Markierung1
	 * @param Markierung2
	 * @return Wahrheitswert
	 */
	//das m-m'-Kriterium zutrifft.
	private boolean m_compare(int[] marking1, int[] marking2) {				
		int sum1 = 0;
		int sum2 = 0;
		boolean m_criteria = true;
		
		for (int j = 0; j < marking1.length; j++) {
			sum1 = sum1 + marking1[j];
			sum2 = sum2 + marking2[j];
		}	
		if (sum1 > sum2) {
			for (int i = 0; i < marking2.length; i++) {
				if (marking1[i] < marking2[i]) {
					m_criteria = false;
				}
			}
		}
		else {
			m_criteria = false;
		}
		return m_criteria;
	}
	
}
