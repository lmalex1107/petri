package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Klasse zur Bildung eines Petrinetzes
 * @author Alexander Adams
 *
 */
public class PetriNet {
	private ArrayList<Knot> knotlist;						//Liste der Knoten des Petrinetzes
	private ArrayList<Arc> arclist;							//Liste der Kanten des Petrinetzes
	private ArrayList<Knot> placelist;						//Liste der Stellen des Petrinetzes
	private ArrayList<Knot> transitionlist;					//Liste der Transitionen des Petrinetzes
	private Knot actual_transition = null;					//
	private int[] Marking;									//aktuelle Markierung des Petrinetzes
	private int[] former_Marking;							//vorherige Markierung des Petrinetzes
	private int[] initial_Marking;							//Anfangsmarkierung
	private Erreichbarkeitsnet ernet;						//zugeordnetes Erreichbarkeitsnetz
	
	private boolean isfinity  = false;
	
	
	
	
	
	
	/**
	 * Im Konstruktor werden die Listen der Knoten und ihren Verbindungen übergeben.
	 * Es wird aus den Angaben in den @Biden Listen ein Petri-Netz erstellt.
	 * @param knotlist
	 * @param arclist
	 */
	public PetriNet(ArrayList<Knot> knotlist, ArrayList<Arc> arclist) {
		this.knotlist = knotlist;
		this.arclist = arclist;
		connectKnots();
		makePlacelist();
		makeTransitionlist();
		this.Marking = new int[this.placelist.size()];
		changeMarking();
		erase_ERNet();
		setInitialMarking(this.Marking);				//Sicherung der Anfangsmarkierung
		this.changeSelection();
	}
	
	/**
	 * löscht den bereits ermittelten Erreichbarkeitsgraphen
	 */
	public void erase_ERNet() {
		ernet = new Erreichbarkeitsnet(this.Marking);
	}
	
	/**
	 * getInitialMarking() gibt die aktuelle Anfangsmarkierung zurück, welche entweder bereits aus der pnml-Datei
	 * geladen oder selbst editiert wurde.
	 * @return
	 */
	public int[] getInitialMarking () {
		return this.initial_Marking;
	}
	
	/**
	 * setzt eine neue Anfangsmarierung
	 * @param Marking
	 */
	public void setInitialMarking (int[] Marking) {
		this.initial_Marking = Arrays.copyOf(Marking, Marking.length);
	}
	
	/**
	 * Rückgabe des mit diesem Petrinetz verknüpften Erreichbarkeitsgraphen
	 * 
	 */
	public Erreichbarkeitsnet getERNet() {
		return this.ernet;
	}
	
	/**
	 * Ändert, welche Stelle selektiert wurde.
	 * @param id
	 */
	public void changeSelection (String id) {
		System.out.println("C H A N G E S E L E C T I O N -  Test");				//Test
		System.out.println ("PRETEST");												//Test
		for (int l = 0;  l < this.knotlist.size(); l++) {							//Test
			System.out.println(this.knotlist.get(l).getId() + " " + this.knotlist.get(l).getSelection()); //Test
		}																			//Test
		Knot foundknot = find (id);
		System.out.println("gefundener Knoten: " + foundknot.getName());			//Test
		for (int i = 0; i < this.knotlist.size(); i++) {
			System.out.println("Ergebnis von find: " + find(id).getId());
			if (find(id).getId().equals(this.knotlist.get(i).getId())) {
				if (this.knotlist.get(i).getSelection() == true) {
					this.knotlist.get(i).setSelection(false);
				}
				else {
					this.knotlist.get(i).setSelection(true);
				}
			}
			else {
				this.knotlist.get(i).setSelection(false);
			}
		}
		
		System.out.println ("POSTTEST");
		for (int l = 0;  l < this.knotlist.size(); l++) {
			System.out.println(this.knotlist.get(l).getId() + " " + this.knotlist.get(l).getSelection());
		}
		System.out.println("END C H A N G E S E L E C T I O N -  Test");
	}
	
	/**
	 * Löscht die Selektion jeder Stelle.
	 */
	public void changeSelection () {
		for (int i = 0; i < this.knotlist.size(); i++) {
			this.knotlist.get(i).setSelection(false);
		}
	}
	
	
	/**
	 * Fidet den Knoten mit der übergebenen ID.
	 * @param id
	 * @return
	 */
	public Knot find (String id) {
		Knot knot = null;
		boolean found = false;
		int i = 0;
		do {
			if (this.knotlist.get(i).getId().equals(id)) {
				System.out.println("Ergebnis find: gefunden");
				knot = this.knotlist.get(i);
				found = true;
			}
			else {
				i++;
			}
		}
		while ((found == false) && (i < this.knotlist.size()));
		return knot;
	}
	
	/*
	 * Verbindet die benachbarte Knoten miteinander.
	 */
 	private void connectKnots() {										
		for (int i = 0; i < arclist.size(); i++) {					//jeweils zuweisen von Vorgängern und Nachfolgern
			boolean found = false;
			int j = 0;
			do {
				System.out.println("Petrinet-Konstruktor-Test" + j);
				if (arclist.get(i).getSource().equals(knotlist.get(j).getId())) {
					System.out.println("Test2");
					found = true;
					
				}
				else {
					j++;
				}
			}
			while (found == false);
			
			found = false;
			int k = 0;
			
			do {
				if (arclist.get(i).getTarget().equals(knotlist.get(k).getId())) {
					found = true;
				}
				else {
					k++;
				}
			}
			while (found == false);
			knotlist.get(j).setSuccessor(knotlist.get(k));
			knotlist.get(k).setPredecessor(knotlist.get(j));
		}
	}
	
 	/*
 	 * Erstellt eine Liste, die nur aus Stellen besteht.
 	 */
	private void makePlacelist() {
		this.placelist = new ArrayList<Knot>();
		for (int i = 0; i < this.knotlist.size(); i++) {
			System.out.println("Test IV");
			if (this.knotlist.get(i).getClass().toString().equals("class model.Place")) {
				System.out.println("Test V");
				this.placelist.add(this.knotlist.get(i));
			}
		}
		Collections.sort(this.placelist);
	}
	/* Erstellt eine Liste, die nur aus Transitionen besteht
	 * 
	 */
	private void makeTransitionlist() {
		this.transitionlist= new ArrayList<Knot>();
		for (int i = 0; i < this.knotlist.size(); i++) {
			if (this.knotlist.get(i).getClass().toString().equals("class model.Transition")) {
				this.transitionlist.add(this.knotlist.get(i));
			}
		}
		
		System.out.println("T R A N S I T I O N L I S T - T E S T");
		for (int k = 0; k < this.transitionlist.size(); k++) {
			System.out.println(this.transitionlist.get(k).getName() + "  " + this.transitionlist.get(k).getId());
		}
		System.out.println ("END  T R A N S I T I O N - T E S T");
	}
	
  	
	/**
	 * Schalten die Transition mit der zugehörigen ID
	 * @param id
	 */
  	public void firing(String id) {
		if ((hasenoughtokens(id) == true) || (find(id).getPredecessorList().size() == 0)) {
			
				for (int i = 0; i < find(id).getPredecessorList().size(); i++) {
					find(id).getPredecessorList().get(i).plusToken(-1);
				}
				
				for (int j = 0; j < find(id).getSuccessorList().size(); j++ ) {
					find(id).getSuccessorList().get(j).plusToken(1);
				}
			changeMarking();
			int[]Markingcopy = Arrays.copyOf(this.Marking, this.Marking.length);
			int[]former_Markingcopy = Arrays.copyOf(this.former_Marking, this.former_Marking.length);
			ernet.insert_knot(Markingcopy, former_Markingcopy, find(id).getId());

		}
	}
  	
  	
  	
  	
  	
  	
  	
  	
  	
  	/**
  	 * Durchschalten des Petrigraphen bis ein Ergebnis vorliegt. Dazu werden alle möglichen(!) Zustände
  	 * einmal aufgerufen. Wird für die Stapelverarbeitung benötigt.
  	 */
  	public void firing_all (int[] Marking) {
  		setMarking(Marking);
  		int[] CoMarking = Arrays.copyOf(Marking, Marking.length);
  		if ((isfiringpossible() == true) && (ernet.isfinity() == true)) {
  			ArrayList<int[]>MarkingBackup = new ArrayList<int[]>();	
	  		for (int i = 0; i < transitionlist.size(); i++) {
	  			setMarking(CoMarking);
	  			if (hasenoughtokens(transitionlist.get(i).getId()) == true) {
	  				firing(this.transitionlist.get(i).getId());
	  				if (ernet.isDublette() == false) {
	  					int[] newMarking = Arrays.copyOf(this.getMarking(), this.getMarking().length);
		  				MarkingBackup.add(newMarking);
	  				}
	  			}
	  		}
	  		if (MarkingBackup != null) {
	  			System.out.println("T BU KNL");
	  			if (MarkingBackup.size() > 0) {
	  				System.out.println("Größe von MarkingBackup" + MarkingBackup.size());
	  				for (int j = 0; j < MarkingBackup.size(); j++) {
	  					firing_all(MarkingBackup.get(j));
	  				}
	  			}
	  		}
  		}
  		if (ernet.isfinity() == false) {
  			System.out.println("Der ERREICHBARKEITSGRAPH IST UNBESCHRÄNKT");
  			this.isfinity = false;
  		}
  		else {
  			System.out.println("Der Erreichbarkeitsgraph ist beschränkt.");
  			this.isfinity = true;
  		}
   	}
  	

  	
  	private void testMarkingPrint() {
  		System.out.println("M A R K I N G - T E S T");
  		for (int i = 0; i < this.Marking.length; i++) {
  			System.out.println(i + ": " + this.Marking[i]);
  		}
  		System.out.println("M A R K I N G - T E S T    E N D E");
  	}
  	
  	/**
  	 * Setzt das Petrinet auf eine übergebene Markierung.
  	 * @param newMarking
  	 */
  	public void setMarking(int[] newMarking) {
  		for (int i = 0; i < this.placelist.size(); i++) {
  			this.placelist.get(i).setToken(newMarking[i]);
  		}
  		changeMarking();
  	}
  	

  	
  	/**
  	 * Gibt zurück, ob an einer bestimmte Transition geschaltet werden kann.
  	 * @param id ID der Transition
  	 * @return true, wenn Schalten möglich; false, wenn Schalten nicht möglich
  	 */
  	public boolean hasenoughtokens(String id) {	//Kann an einer bestimmten Transition noch geschaltet werden?
  		boolean enoughtokens = true;
  		if (find(id) != null) {
  			System.out.println("Beta-Test");
  			if (find(id).getPredecessorList().size() > 0) {
  	  			for (int i = 0; i < find(id).getPredecessorList().size(); i++) {
  	  	  			if (find(id).getPredecessorList().get(i).getToken() < 1) {
  	  	  				enoughtokens = false;
  	  	  			}
  	  	  		}
  	  		}
  		}
  		return enoughtokens;
  	}
  	

 	/**
 	 * Gibt für das gesamte Petrinetz zurück, ob noch geschaltet werden kann.
 	 * @return true, wenn noch geschaltet werden kann; false, wenn nicht mehr geschaltet werden kann.
 	 */
 	private boolean isfiringpossible() {
 		boolean firingpossible = false;
 		for (int i = 0; i < transitionlist.size(); i++) {
 			if (hasenoughtokens(transitionlist.get(i).getId()) == true) {
 				firingpossible = true;
 			}
  		}
 		return firingpossible;
 	}
 	
 
 	private void proovefiringpossibility () {
 		for (int i = 0; i < this.transitionlist.size(); i++) {
 			if (hasenoughtokens(this.transitionlist.get(i).getId())){
 				this.transitionlist.get(i).setfiringpossible(true);
 			}
 			else {
 				this.transitionlist.get(i).setfiringpossible(false);
 			}
 		}
 	}
 	
	/**
	 * gibt das komplette Petri-Netz zurück,
	 * d.h. den Graphen mit allen Knoten, jeweils mit Vorgänger
	 * und Nachfolger versehen.
	 * @return this.knotlist
	 */
	public ArrayList<Knot> getcompleteKnotList(){
		return this.knotlist;
	}
	
	/**
	 * Gibt die aktuelle Markierung des Petrinetzes zurück
	 * @return
	 */
	public int[] getMarking() {
		return this.Marking;
	}
	
	/**
	 * Gibt die Markierung zurück, die als vorletztes geschaltet wurde. Dies ist wichtig für as Einfügen in das Petrinetz.
	 * @return
	 */
	public int[] getFormer_Marking() {
		return this.former_Marking;
	}
	
	/**
	 * 
	 */
	public void changeMarking() {
		this.former_Marking = Arrays.copyOf(this.Marking, this.Marking.length);
		
		for (int j = 0; j < this.Marking.length; j++) {
			this.Marking[j] = placelist.get(j).getToken();
		}
		proovefiringpossibility();
	}
	
	/**
	 * Rückgabe der Knoten des Petrinetzes
	 * @return
	 */
	public ArrayList<Knot>getKnotlist(){
		return this.knotlist;
	}
	
	/**
	 * Gibt eine Liste der Transitionen des Petrinetzes zurück
	 */
	public ArrayList<Knot> getTransitionlist(){
		return this.transitionlist;
	}
	
	/**
	 * Gibt eine Liste der Stellen des Petrinetzes zurück.
	 * @return
	 */
	public ArrayList<Knot> getPlacelist(){
		return this.placelist;
	}
	
	/**
	 * Gibt zurück, ob das Petrinetz beschränkt oder unbeschränkt ist.
	 * @return
	 */
	public boolean isInfinity () {
		return this.isfinity;
	}
}
