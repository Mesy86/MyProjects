package model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Projekt 
{
	/**
	 * Instanz-Variablen
	 */
	private int id; 
	private String name; 
	private Date deadline; 
	private String prioritaet;
	private String beschreibung; 
	private Nutzer initiator;
	private Kanban kanban; 
	private int archiviert; 
	private boolean aktiv; 
	private int rechtzeitig; 
	private List<Nutzer> mitglieder = new ArrayList<Nutzer>(); 
	
	/**
	 * formatiertes Datum als String
	 * wird nur außerhalb der Klasse gebraucht
	 */
	@SuppressWarnings("unused")
	private String formattedDate; 
	
	/**
	 * Default-Konstruktor
	 */
	public Projekt()
	{
		this.archiviert = 1; 
		this.aktiv = true; 
	}
	
	/**
	 * überladener Konstruktor
	 * @param p_id
	 * @param p_name
	 * @param p_kanban
	 * @param p_initiator
	 */
	public Projekt(int p_id, String p_name, Nutzer p_initiator)
	{
		this.id = p_id; 
		this.name = p_name; 
		this.initiator = p_initiator;  
		this.archiviert = 1; 
		this.aktiv = true; 
	}
	
	/**
	 * überladener Konstruktor
	 * @param name
	 */
	public Projekt(String name)
	{
		this.name = name; 
	}
	/**
	 * überladener Konstruktor
	 * @param p_id
	 */
	public Projekt(int p_id)
	{
		this.id = p_id; 
		this.archiviert = 1; 
		this.aktiv = true; 
	}
	
	/**
	 * Fügt einen Nutzer zur Mitgliederliste hinzu
	 * @param p_nutzer
	 */
	public void userHinzufuegen(Nutzer p_nutzer)
	{
		this.mitglieder.add(p_nutzer); 
	}
	
	/**
	 * Liefert Projekt-Id
	 * @return int
	 */
	public int getId() 
	{
		return this.id;
	}
	
	/**
	 * Liefert Projektname
	 * @return String
	 */
	public String getName() 
	{
		return this.name;
	}
	
	/**
	 * Setzt Projektname
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
	 * Liefert die Priorität
	 * @return String
	 */
	public String getPrioritaet() 
	{
		return this.prioritaet;
	}
	
	/**
	 * Setzt Priorität
	 * @param prioritaet
	 */
	public void setPrioritaet(String prioritaet) 
	{
		this.prioritaet = prioritaet;
	}
	
	/**
	 * Liefert beschreibung 
	 * @return String 
	 */
	public String getBeschreibung() 
	{
		return this.beschreibung;
	}
	
	/**
	 * Setzt Beschreibung des Projekts
	 * @param beschreibung
	 */
	public void setBeschreibung(String beschreibung) 
	{
		this.beschreibung = beschreibung;
	}
	
	/**
	 * Liefert Initiator des Projekts 
	 * @return Nutzer
	 */
	public Nutzer getInitiator() 
	{
		return this.initiator;
	}
	
	/**
	 * Setzt Initiator des Projekts
	 * @param initiator
	 */
	public void setInitiator(Nutzer initiator) 
	{
		this.initiator = initiator;
	}
	
	/**
	 * Liefert formatierten String der Deadline
	 * @return String
	 */
	public String getFormattedDate() 
	{
		return new SimpleDateFormat("yyyy-MM-dd").format(this.deadline);
	}
	
	/**
	 * Setzt formatierten String der Deadline
	 * @param formattedDate
	 */
	public void setFormattedDate(String formattedDate) 
	{
		this.formattedDate = formattedDate;
	}
	
	/**
	 * Liefert Status des Objekts (aktiv = 1 oder archiviert = 0)
	 * @return int
	 */
	public int getArchiviert() 
	{
		return this.archiviert;
	}
	
	/**
	 * Setzt Status des Objekts (aktiv = 1 oder archiviert = 0) 
	 * @param archiviert
	 */
	public void setArchiviert(int archiviert) 
	{
		this.archiviert = archiviert;
	}
	
	/**
	 * Liefert Status des Objekts als Boolsche Variable (aktiv = true, archiviert = false) 
	 * @return boolean
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
	 * Liefert zugehöriges Kanban-Objekt
	 * @return Kanban
	 */
	public Kanban getKanban() 
	{
		return this.kanban;
	}
	
	/**
	 * Setzt zugehöriges Kanban
	 * @param kanban
	 */
	public void setKanban(Kanban kanban) 
	{
		this.kanban = kanban;
	}
	
	/**
	 * Liefert, ob Projekt rechtzeitig fertiggestellt wurde (1) oder nicht(0) oder noch offen ist(null)
	 * @return
	 */
	public int getRechtzeitig() 
	{
		return this.rechtzeitig;
	}
	
	/**
	 * Setzt, ob Projekt rechtzeitig (1) oder verspätet (0) oder noch nicht (null) fertiggestellt wurde
	 * @param rechtzeitig
	 */
	public void setRechtzeitig(int rechtzeitig) 
	{
		this.rechtzeitig = rechtzeitig;
	}
	
	/**
	 * Setzt Id
	 * @param id
	 */
	public void setId(int id) 
	{
		this.id = id;
	}
	
	/**
	 * Liefert eine Liste aller Teammitglieder eines Projekts
	 * @return List<Nutzer>
	 */
	public List<Nutzer> getMitglieder() 
	{
		return this.mitglieder;
	}
	
	/**
	 * Setzt Liste von Teammitgliedern des Projekts
	 * @param mitglieder
	 */
	public void setMitglieder(List<Nutzer> mitglieder) 
	{
		this.mitglieder = mitglieder;
	}
	
	

}
