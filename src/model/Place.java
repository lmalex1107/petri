package model;


/**
 * Klasse zur Repräsentation einer Stelle eines Petrinetzes
 * @author Alexander Adams
 *
 */
public class Place extends Knot {
	private boolean initialMarking = false;
	private int token = 0;
	
	/**
	 * erstellte eine neue Stelle
	 * @param id Identifikationsnummer
	 */
	public Place(String id) {
		super(id);
	}
	
	/**
	 * Gibt die Anfangsmarkierung der Stelle zurück.
	 */
	public boolean getInitialMarking () {
		return this.initialMarking;
	}
	
	/**
	 * Setzt die Anfangsmarkierung.
	 * @param initialMarking
	 */
	
	public void setInitialMarking (boolean initialMarking) {
		this.initialMarking = initialMarking;
	}
	
	/**
	 * Gibt die Anzal der Marken zurück.
	 */
	public int getToken() {
		return this.token;
	}
	
	/**
	 * Erhöht die Anzahl der Marken um eins.
	 */
	public void plusToken(int token) {
		this.token += token;
	}
	
	/**
	 * Setzt die Anzahl der Marken.
	 */
	public void setToken(int token) {
		this.token = token;
	}
	
}
