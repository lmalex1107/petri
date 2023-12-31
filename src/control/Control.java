package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JDialog;

import org.graphstream.ui.view.ViewerListener;

import model.Arc;
import model.Erreichbarkeitsnet;
import model.Knot;
import model.PetriNet;
import view.Erreichbarkeitsgraph;
import view.GUI;
import view.PetriGraph;

/**
 * Der Konstruktor des Klasse wird aufgerufen und es finden die wichtigsten Zuweisungen der beiden Netze sowie der 
 * darzustellenden Graphen statt.
 * @author Alexander Adams
 *
 */
public class Control implements ActionListener, ViewerListener, MouseListener{
	private ArrayList<Knot> knotlist = null;
	private ArrayList<Arc> arclist = null;
	private GUI gui;
	private PetriGraph petrigraph = null;
	private PetriNet petrinet = null;
	private Erreichbarkeitsnet ernet = null;
	private Erreichbarkeitsgraph ergraph = null;
	
	private File[] xmlfiles = null;
	private File xmlfile = null; 
	
	/*
	 * Der Konstruktor der Klasse öffnet ein Programmfenster und initiiert die darztstellenden Graphen
	 */
	public Control () {
		petrigraph = new PetriGraph(knotlist, arclist);
		ergraph = new Erreichbarkeitsgraph();
		
		gui = new GUI(this, knotlist, arclist, petrigraph, ergraph);
		
		System.out.println(System.identityHashCode( petrigraph ));			//Test
		
	
	}
	/*
	 * In der überschriebenen Methode actionPerformed() wird überprüft, welches
	 * Element der grafischen Struktur einen Mausklick zurückgemeldet hat. Anschließend wird die jeweilige Anfrage 
	 * bearbeitet und an die entsprechenden Stellen im Programmverlauf verwiesen.
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == gui.oeffnen) {
			this.petrigraph = gui.open(this.petrigraph);
			if (this.petrigraph != null) {
				this.xmlfile = gui.getXmlFile();
				this.knotlist = gui.getKnotList();
				this.arclist = gui.getArcList();
				this.petrinet = new PetriNet(this.knotlist, this.arclist);	
				this.petrinet.changeMarking();
				this.petrigraph.changeGraph(this.knotlist);
				this.ernet = this.petrinet.getERNet(); 																//Übergabe des Petrinetzes an die Klasse zur
				this.ergraph.changeGraph(this.ernet.getERknotlist(), this.ernet.getERArclist());					//Berechnung des Erreichbarkeitsgraphen. 
				System.out.println(System.identityHashCode( petrigraph ));
			}
		}
		
		if (ae.getSource() == gui.neu_laden) {												//Petrinetz neu laden														
			petrigraph = gui.reopen(petrigraph);
			this.petrinet.changeMarking();
			this.petrigraph.changeGraph(this.knotlist);
			this.knotlist = gui.getKnotList();
			this.arclist = gui.getArcList();
			this.petrinet = new PetriNet(this.knotlist, this.arclist);
			
			petrigraph.loadGraph(this.knotlist, this.arclist);
			petrigraph.changeGraph(this.knotlist);
			
			this.ernet = this.petrinet.getERNet();
			this.ergraph.changeGraph(this.ernet.getERknotlist(), this.ernet.getERArclist());
			
		}
		
		if (ae.getSource() == gui.analyse_ALL) {
			xmlfiles = gui.analyse_ALL();
			Analyse analyse = new Analyse (xmlfiles, gui.textarea);
			analyse.analyse_ALL();
		}
		
		if (ae.getSource() == gui.beenden) {
			gui.exit();
		}
		
		if (ae.getSource() == gui.analyse_ONE) {
			String outputStringtextfeld;
			String[] outputStringwindow = new String[2];
			outputStringwindow[0] = this.xmlfile.getName();
			this.petrinet.setMarking(this.petrinet.getInitialMarking());
			this.petrinet.changeMarking();
			this.petrigraph.changeGraph(this.petrinet.getKnotlist());
			this.petrinet.erase_ERNet();
			this.ergraph.clear();
			Analyse analyse = new Analyse(this.xmlfile , gui.textarea);
			if (analyse.analyse_ONE(this.petrinet) == true) {
				outputStringtextfeld = this.xmlfile.getName() + "\n" + " - der zugehörige Erreichbarkeitsgraph ist endlich; das Petrinetz ist beschränkt." + "\n" + "\n";
				outputStringwindow[01] = "Das Petrinetz ist beschränkt";
				gui.textarea.append(outputStringtextfeld);
			}
			else {
				outputStringtextfeld = this.xmlfile.getName() + "\n" + " - der dazugehörige Erreichbarkeitsgraph ist unendlich; das Petrinetz ist unbeschränkt." + "\n" + "\n" ;
				outputStringwindow[1] = "Das Petrinetz ist unbeschränkt";
				gui.textarea.append(outputStringtextfeld);
			}
			gui.showResult(outputStringwindow);
			this.ernet = analyse.getERNet();
			this.ergraph.changeGraph(this.ernet.getERknotlist(), this.ernet.getERArclist());
		}
		
		if (ae.getSource() == gui.EG_loeschen) {
			this.petrinet.setMarking(this.petrinet.getInitialMarking());
			this.petrigraph.changeGraph(this.knotlist);
			this.petrinet.erase_ERNet();
			this.ernet = this.petrinet.getERNet();
			this.ergraph.clear();
			this.ergraph.changeGraph(this.ernet.getERknotlist(), this.ernet.getERArclist());
		}
		
		if (ae.getSource() == gui.reset) {
			this.petrinet.setMarking(this.petrinet.getInitialMarking());
			this.petrinet.changeMarking();
			this.petrinet.changeSelection();
			this.knotlist = this.petrinet.getKnotlist();
			this.petrigraph.changeGraph(this.knotlist);
		}
		
		if (ae.getSource() == gui.minus) {
			for (int i = 0; i < this.petrinet.getPlacelist().size(); i++ ) {
				if (this.petrinet.getPlacelist().get(i).getSelection() == true) {
					if (this.petrinet.getPlacelist().get(i).getToken() - 1 >= 0) {
						this.petrinet.getPlacelist().get(i).plusToken(-1);
					}
					break;
				}
			}
			this.petrinet.changeMarking();
			this.petrinet.setInitialMarking(this.petrinet.getMarking());
			this.petrigraph.changeGraph(this.knotlist);
			this.petrinet.erase_ERNet();
			this.ernet = this.petrinet.getERNet();
			this.ergraph.clear();
			this.ergraph.changeGraph(this.ernet.getERknotlist(), this.ernet.getERArclist());
		}

		if (ae.getSource() == gui.plus) {
			for (int i = 0; i < this.petrinet.getPlacelist().size(); i++ ) {
				if (this.petrinet.getPlacelist().get(i).getSelection() == true) {
					this.petrinet.getPlacelist().get(i).plusToken(1);
				}
			}
			this.petrinet.changeMarking();
			this.petrinet.setInitialMarking(this.petrinet.getMarking());
			this.petrigraph.changeGraph(this.knotlist);
			this.petrinet.erase_ERNet();
			this.ernet = this.petrinet.getERNet();
			this.ergraph.clear();
			this.ergraph.changeGraph(this.ernet.getERknotlist(), this.ernet.getERArclist());
		}
		
		if (ae.getSource() == gui.text_loeschen) {
			gui.delete_text();
		}
		
		
	}
	
	
	public void viewClosed( String id ) {
		
	}
	/*
	 * Hier wird überprüft, welcher Knoten (Petrinetz oder Erreichbarkeitsgrapg angeklickt wurde.
	 * Anschließend wird werden die entsprechenden Subroutinen aufgerufen.
	 */
	public void buttonPushed( String id ) {
		System.out.println("ButtonPushedTest");
		if (this.petrinet.find(id) != null) {
			if (this.petrinet.find(id).getClass().toString().equals("class model.Place")) {					// Stelle wird markiert.
				System.out.println(this.petrigraph.getNode(id).getAttribute("id").toString());
				System.out.println(this.petrinet.find(id).getId());
				System.out.println("PushedButtonTest II");
				this.petrinet.changeSelection(this.petrinet.find(id).getId());
				this.ernet.changeSelection();
				System.out.println(this.petrinet.find(id).getId());
			}
			if (this.petrinet.find(id).getClass().toString().equals("class model.Transition")){				// Transition wird geschaltet.
				System.out.println("Es wurde geschaltet.");
				if (this.petrinet.hasenoughtokens(id) == true) {
					this.petrinet.firing(id);
					this.ernet.insert_knot(this.petrinet.getMarking(), this.petrinet.getFormer_Marking(), this.petrinet.find(id).getId());
					
				}
			}
			this.petrigraph.changeGraph(this.petrinet.getKnotlist());
			this.ergraph.changeGraph(this.ernet.getERknotlist(), this.ernet.getERArclist());
		}
		
		else {
			if (this.ernet.find(id) != null) {
				for (int j = 0; j < this.ernet.getERknotlist().size(); j++) {		
					if (this.ergraph.getNode(id).getAttribute("id").toString().equals(this.ernet.getERknotlist().get(j).getId()) == true) {
						this.ernet.changeSelection(this.ernet.getERknotlist().get(j).getLabel());
						this.petrinet.changeSelection();
						petrinet.setMarking(this.ernet.getERknotlist().get(j).getMarking());
						petrinet.changeMarking();
					}
				}
				this.ergraph.changeGraph(this.ernet.getERknotlist(), this.ernet.getERArclist());
				this.petrigraph.changeGraph(this.petrinet.getKnotlist());
			}
			
		}
		
			
	}
	
	/**
	 * für dieses Programm nicht weiter relevant
	 */
	public void buttonReleased( String id ) {
		
	}
	
	/**
	 * für dieses Programm nicht weiter relevant
	 */
	@Override
	public void mouseClicked(MouseEvent me) {
		
	}

	/**
	 * für dieses Programm nicht weiter relevant
	 */
	@Override
	public void mouseEntered(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * für dieses Programm nicht weiter relevant
	 */
	@Override
	public void mouseExited(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Diese überschriebene Methode sorgt dafür, dass sich beide Graphen immer wieder aktualisieren
	 */
	@Override
	public void mousePressed(MouseEvent me) {
		if (gui != null) {
			gui.pump();
		}
		
	}
	/*
	 * Diese überschriebene Methode sorgt dafür, dass sich beide Graphen immer wieder aktualisieren
	 */
	@Override
	public void mouseReleased(MouseEvent me) {
		
		if (gui != null)	{
			gui.pump();
		}
	}
	
	/**
	 * Test
	 */
	public void testprint() {
		
	}
	
	public static void main (String args[]) {
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Control control = new Control();
			}
		});
		
	}

	
	
	
}
