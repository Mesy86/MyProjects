package model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Aufgabe 
{
	/**
	 * Instanzvariablen
	 */
	private int id; 
	private String name; 
	private Date deadline; 
	private int schaetzung; 
	private String prioritaet;
	private String beschreibung; 
	private Nutzer initiator;
	private Projekt projekt;
	private String status; 
	private int archiviert; 
	private boolean aktiv; 
	private int rechtzeitig; 
	
	/**
	 * formatiertes Datum als String
	 * wird nur von außerhalb der Klasse gebraucht
	 */
	@SuppressWarnings("unused")
	private String formattedDate;
	
	/**
	 * Default-Konstruktor
	 */
	public Aufgabe()
	{
	}
	
	/**
	 * überladener Konstruktor
	 * @param id
	 */
	public Aufgabe(int id)
	{
		this.id = id; 
	}
	
	/**
	 * überladener Konstruktor 
	 * @param id
	 * @param name
	 * @param beschreibung
	 * @param projekt
	 */
	public Aufgabe(int id, String name, String beschreibung, Nutzer nutzer)
	{
		this.id = id; 
		this.name = name; 
		this.beschreibung = beschreibung; 
		this.initiator = nutzer; 
	}
	
	/**
	 * Liefert id
	 * @return int
	 */
	public int getId() 
	{
		return this.id;
	}
	
	/**
	 * Setzt id
	 * @param id
	 */
	public void setId(int id) 
	{
		this.id = id;
	}
	
	/**
	 * Liefert Namen der Aufgabe
	 * @return String
	 */
	public String getName() 
	{
		return this.name;
	}
	
	/**
	 * Setzt Namen der Aufgabe
	 * @param name
	 */
	public void setName(String name) 
	{
		this.name = name;
	}
	
	/**
	 * Liefert Deadline
	 * @return Date
	 */
	public Date getDeadline() 
	{
		return this.deadline;
	}
	
	/**
	 * Setzt Deadline
	 * @param deadline
	 */
	public void setDeadline(Date deadline) 
	{
		this.deadline = deadline;
	}
	
	/**
	 * Liefert Schätzung des Zeitaufwands in Stunden
	 * @return int
	 */
	public int getSchaetzung() 
	{
		return this.schaetzung;
	}
	
	/**
	 * Setzt geschätzten Zeitaufwand in Stunden
	 * @param schaetzung
	 */
	public void setSchaetzung(int schaetzung) 
	{
		this.schaetzung = schaetzung;
	}
	
	/**
	 * Liefert Priorität
	 * @return String
	 */
	public String getPrioritaet() 
	{
		return this.prioritaet;
	}
	
	/**
	 * Setzt Priorität der Aufgabe
	 * @param prioritaet
	 */
	public void setPrioritaet(String prioritaet) 
	{
		this.prioritaet = prioritaet;
	}
	
	/**
	 * Liefert Beschreibung der Aufgabe
	 * @return String
	 */
	public String getBeschreibung() 
	{
		return this.beschreibung;
	}
	
	/**
	 * Setzt Beschreibung der Aufgabe
	 * @param beschreibung
	 */
	public void setBeschreibung(String beschreibung) 
	{
		this.beschreibung = beschreibung;
	}
	
	/**
	 * Liefert den Nutzer, der Aufgabe angelegt hat
	 * @return Nutzer
	 */
	public Nutzer getInitiator() 
	{
		return this.initiator;
	}
	
	/**
	 * Setzt Nutzer, der die Aufgabe angelegt hat
	 * @param initiator
	 */
	public void setInitiator(Nutzer initiator) 
	{
		this.initiator = initiator;
	}
	
	/**
	 * Liefert übergeordnetes Projekt
	 * @return Projekt
	 */
	public Projekt getProjekt() 
	{
		return this.projekt;
	}
	
	/**
	 * Setzt übergeordnetes Projekt
	 * @param projekt
	 */
	public void setProjekt(Projekt projekt) 
	{
		this.projekt = projekt;
	}
	
	/**
	 * Liefert Bearbeitungsstand
	 * @return String
	 */
	public String getStatus() 
	{
		return this.status;
	}
	
	/**
	 * Setzt Bearbeitungsstand
	 * @param status
	 */
	public void setStatus(String status) 
	{
		this.status = status;
	}
	
	/**
	 * Liefert Deadline als String
	 * @return String
	 */
	public String getFormattedDate() 
	{
		return new SimpleDateFormat("yyyy-MM-dd").format(this.deadline);
	}
	
	/**
	 * Setzt Deadline als String
	 * @param formattedDate
	 */
	public void setFormattedDate(String formattedDate) 
	{
		this.formattedDate = formattedDate;
	}
	
	/**
	 * Liefert Status des Objekts (1=aktiv, 0=archiviert) 
	 * @return int
	 */
	public int getArchiviert() 
	{
		return this.archiviert;
	}
	
	/**
	 * Setzt Status des Objekts (1=aktiv, 0=archiviert)
	 * @param archiviert
	 */
	public void setArchiviert(int archiviert) 
	{
		this.archiviert = archiviert;
	}

	/**
	 * Liefert Status des Objekts als Boolsche Variable (aktiv = true, archiviert = false) 
	 * @return
	 */
	public boolean isAktiv() 
	{
		if(this.archiviert == 1)
		{
			this.aktiv = true; 
		}
		else
		{
			this.aktiv = false; 
		}
		
		return aktiv;
	}
	
	/**
	 * Setzt Status des Objekts (aktiv = true, archiviert = false)
	 * @param aktiv
	 */
	public void setAktiv(boolean aktiv) 
	{
		this.aktiv = aktiv;
	}
	
	/**
	 * Liefert, ob die Aufgabe rechtzeitig (1), verspätet (0) oder noch gar nicht (null) fertiggestellt wurde
	 * @return int
	 */
	public int getRechtzeitig() 
	{
		return this.rechtzeitig;
	}
	
	/**
	 * Setzt, ob eine Aufgabe rechtzeitig(1), verspätet(0) oder noch gar nicht (null) fertiggestellt ist
	 * @param rechtzeitig
	 */
	public void setRechtzeitig(int rechtzeitig) 
	{
		this.rechtzeitig = rechtzeitig;
	}

}
