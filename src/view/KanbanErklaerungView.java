package view;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="kanbanErklaerungView")
@ViewScoped
public class KanbanErklaerungView implements Serializable
{

	/**
	 * Default UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Default-Konstruktor
	 */
	public KanbanErklaerungView()
	{
	}
	
	/**
	 * Methode liefert Erklärungstext für Kanban
	 * @return String
	 */
	public String erklaereKanban()
	{
		String ausgabe = "Kanban in der Softwareentwicklung ist ein Vorgehensmodell, bei dem die Anzahl paralleler Arbeiten (= Work in Progress WiP) "
				+ "begrenzt wird. Ziel sind kürzere Durchlaufzeiten und das schnelle Sichtbarmachen von Engpässen. Der Arbeitsfluss wird für "
				+ "alle Beteiligten visualisiert und Regeln explizit gemacht."; 
		return ausgabe; 
	}
	
	/**
	 * Methode liefert Erklärungstext für Kanban-Board allgemeien
	 * @return String
	 */
	public String erklaereBoard()
	{
		String ausgabe = "Das Kanban-Board dient der Darstellung des Arbeitsprozesses. Es besteht aus mehreren Spalten/Stationen, die jede Aufgabe "
				+ "durchlaufen muss. Die Anzahl begonnener, unfertiger Arbeiten wird begrenzt. Der Projektfortschritt wird "
				+ "überwacht. Die Projektarbeit kann mithilfe des Boards angepasst und verbessert werden."; 
		return ausgabe; 
	}
	
	/**
	 * Methode liefert Erklärungstext für Kanban-Board im Programm
	 * @return String
	 */
	public String erklaereBoardProgramm()
	{
		String ausgabe = "Das hier verwendete Kanban-Board umfasst die Stationen Backlog, To Do, In Progress, Testing und Done. "
				+ "Im Backlog befinden sich alle zu erledigenden Aufgaben. In To Do stehen Aufgaben, die als nächstes gemacht werden müssen. "
				+ "In Progress umfasst alle Aufgaben, an denen aktuell gearbeitet wird. Nach Fertigstellung werden die Aufgaben in Testing geschoben. "
				+ "Nach erfolgreichem Testen gilt eine Aufgabe als erledigt (= Done). Durch Löschen der erledigten Aufgaben bekommen diese den Status "
				+ "Burn und tauchen nicht mehr im Kanban-Board auf."; 
		return ausgabe; 
	}
	
	/**
	 * Methode liefert Erklärungstext zu Regeln des Kanban-Boards im Programm
	 * @return String
	 */
	public String erklaereRegeln()
	{
		String ausgabe = "Das Verschieben von Aufgaben ist bei aktiven Projekten per Drag and Drop möglich. Die Spalten Backlog und Done können beliebig viele "
				+ "Aufgaben umfassen. Hingegen in To Do dürfen höchstens 6, in den übrigen Spalten höchstens 3 Aufgaben gleichzeitig stehen. "
				+ "Ein Ziehen in Done direkt ist nur von Testing aus zulässig, ein Überspringen der Testphase somit nicht möglich. Soll "
				+ "eine Aufgabe zurück gezogen werden, kann diese nur in To Do gesetzt werden. Erledigte Aufgaben in Done können nicht mehr "
				+ "verschoben werden. Ein Leeren der Liste (= Burn) ist unwiderruflich. Bei inaktiven Projekten ist Drag and Drop nicht möglich."; 
		return ausgabe; 
	}

}
