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
	 * Methode liefert Erkl�rungstext f�r Kanban
	 * @return String
	 */
	public String erklaereKanban()
	{
		String ausgabe = "Kanban in der Softwareentwicklung ist ein Vorgehensmodell, bei dem die Anzahl paralleler Arbeiten (= Work in Progress WiP) "
				+ "begrenzt wird. Ziel sind k�rzere Durchlaufzeiten und das schnelle Sichtbarmachen von Engp�ssen. Der Arbeitsfluss wird f�r "
				+ "alle Beteiligten visualisiert und Regeln explizit gemacht."; 
		return ausgabe; 
	}
	
	/**
	 * Methode liefert Erkl�rungstext f�r Kanban-Board allgemeien
	 * @return String
	 */
	public String erklaereBoard()
	{
		String ausgabe = "Das Kanban-Board dient der Darstellung des Arbeitsprozesses. Es besteht aus mehreren Spalten/Stationen, die jede Aufgabe "
				+ "durchlaufen muss. Die Anzahl begonnener, unfertiger Arbeiten wird begrenzt. Der Projektfortschritt wird "
				+ "�berwacht. Die Projektarbeit kann mithilfe des Boards angepasst und verbessert werden."; 
		return ausgabe; 
	}
	
	/**
	 * Methode liefert Erkl�rungstext f�r Kanban-Board im Programm
	 * @return String
	 */
	public String erklaereBoardProgramm()
	{
		String ausgabe = "Das hier verwendete Kanban-Board umfasst die Stationen Backlog, To Do, In Progress, Testing und Done. "
				+ "Im Backlog befinden sich alle zu erledigenden Aufgaben. In To Do stehen Aufgaben, die als n�chstes gemacht werden m�ssen. "
				+ "In Progress umfasst alle Aufgaben, an denen aktuell gearbeitet wird. Nach Fertigstellung werden die Aufgaben in Testing geschoben. "
				+ "Nach erfolgreichem Testen gilt eine Aufgabe als erledigt (= Done). Durch L�schen der erledigten Aufgaben bekommen diese den Status "
				+ "Burn und tauchen nicht mehr im Kanban-Board auf."; 
		return ausgabe; 
	}
	
	/**
	 * Methode liefert Erkl�rungstext zu Regeln des Kanban-Boards im Programm
	 * @return String
	 */
	public String erklaereRegeln()
	{
		String ausgabe = "Das Verschieben von Aufgaben ist bei aktiven Projekten per Drag and Drop m�glich. Die Spalten Backlog und Done k�nnen beliebig viele "
				+ "Aufgaben umfassen. Hingegen in To Do d�rfen h�chstens 6, in den �brigen Spalten h�chstens 3 Aufgaben gleichzeitig stehen. "
				+ "Ein Ziehen in Done direkt ist nur von Testing aus zul�ssig, ein �berspringen der Testphase somit nicht m�glich. Soll "
				+ "eine Aufgabe zur�ck gezogen werden, kann diese nur in To Do gesetzt werden. Erledigte Aufgaben in Done k�nnen nicht mehr "
				+ "verschoben werden. Ein Leeren der Liste (= Burn) ist unwiderruflich. Bei inaktiven Projekten ist Drag and Drop nicht m�glich."; 
		return ausgabe; 
	}

}
