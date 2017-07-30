package view;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="visualisierungErklaerungView")
@ViewScoped
public class VisualisierungErklaerungView implements Serializable
{

	/**
	 * Default UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Default-Konstruktor
	 */
	public VisualisierungErklaerungView()
	{
	}
	
	/**
	 * Methode liefert Erkl�rungstext zu Diagramm Projektfortschritt (Einzelprojekt)
	 * @return String
	 */
	public String erklaereFortschritt()
	{
		String ausgabe = "Das Kuchen-Diagramm zeigt den Fortschritt eines ausgew�hlten Projekts durch prozentuale Darstellung "
				+ "der Anzahl von Aufgaben in den verschiedenen Stationen des Kanban-Boards"; 
		return ausgabe; 
	}
	
	/**
	 * Methode liefert Erkl�rungstext zu Diagramm Termineinhaltung (Einzelprojekt)
	 * @return String
	 */
	public String erklaereEinhaltung()
	{
		String ausgabe = "Das Kuchen-Diagramm visualisiert die Einhaltung von Aufgaben-Deadlines eines ausgew�hlten Projekts. "
				+ "Es wird konkretisiert wie viele Aufgaben rechtzeitig, p�nktlich oder noch gar nicht fertiggestellt wurden."; 
		return ausgabe; 
	}
	
	/**
	 * Methode liefert Erkl�rungstext zu Diagramm Fertigstellung (alle Projekte)
	 * @return String
	 */
	public String erklaereGesamt()
	{
		String ausgabe = "Das Kuchen-Diagramm stellt die Einhaltung der Deadlines der bisher bearbeiteten Projekte grafisch dar. "
				+ "Die Visualisierung zeigt, wie viele Projekte rechzeitig, p�nktlich oder noch gar nicht fertiggestellt wurden."; 
		return ausgabe; 
	}

}
