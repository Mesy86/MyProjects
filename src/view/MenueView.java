package view;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import model.Aufgabe;
import model.Kanban;
import model.Nutzer;
import model.Projekt;
import persistenz.Datenbank;

@ManagedBean(name="menueView")
@ViewScoped
public class MenueView implements Serializable
{

	/**
	 * Default UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instanz-Variablen
	 */
	@ManagedProperty(value="#{loginView.benutzername}")
	private String benutzername;
	
	private String vorname; 
	private String nachname; 
	private String email; 
	
	private String zeit; 
	
	private String suchbegriff; 
	
	private List<Kanban> kanbans; 
	private List<Projekt> projekte; 
	private List<Aufgabe> aufgaben; 
	
	private Projekt selectedProject; 
	private Kanban selectedKanban; 
	private Aufgabe selectedAufgabe; 
	
	private Datenbank db = new Datenbank(); 
	private Connection conn = null; 
	
	/**
	 * Methode liefert nötige Daten, bevor die Oberfläche aufgebaut wird
	 */
	@PostConstruct
	public void init()
	{
		this.selectedAufgabe = null; 
		this.selectedKanban = null; 
		this.selectedProject = null; 
		
		//Nutzer aus Datenbank ermitteln
		//DB-Verbindung herstellen 
		this.conn = this.db.verbindungHerstellen(); 
		
		Nutzer nutzer = this.db.ermittleNutzerNachBenutzername(this.benutzername); 
		
		//Angaben füllen
		this.vorname = nutzer.getVorname(); 
		this.nachname = nutzer.getNachname(); 
		this.email = nutzer.getMail();
		this.zeit = nutzer.getLoginZeit(); 
		
		//Listen füllen
		this.projekte = this.db.ermittleProjekteNutzerSortByDeadline(nutzer); 
		this.kanbans = this.db.ermittleKanbanNutzer(nutzer); 
		this.aufgaben = this.db.ermittleAufgabenNutzerSortByDeadline(nutzer); 
		
		
		this.db.verbindungSchliessen();
	}
	
	/**
	 * Default-Konstruktor
	 */
	public MenueView()
	{
	}
	
	/**
	 * Methode schreibt ausgewähltes Kanban in die Session und leitet weiter zur Ansicht des Boards
	 * @return String
	 */
	public String zeigeKanbanDetail()
	{
		String ausgabe = ""; 
		
		//Ermittle Projekt zu Kanban
		this.conn = this.db.verbindungHerstellen(); 
		Projekt projekt = this.db.ermittleProjektKanban(this.selectedKanban); 
		this.db.verbindungSchliessen();
		
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("Suchergebnis", projekt); 
		
		//Weiterleitung je nach dem ob Projekt aktiv oder inaktiv
		System.out.println("" + projekt.isAktiv());
		if(projekt.isAktiv())
		{
			ausgabe="kanban";
		}
		else
		{
			ausgabe = "kanbanInaktiv"; 
		}
		
		return ausgabe; 
	}
	
	/**
	 * Methode schreibt ausgewähltes Projekt in die Session und leitet weiter zur Detailansicht
	 * @return String
	 */
	public String zeigeProjektDetail()
	{
		Projekt projekt = null; 
		
		for(int i = 0; i < this.projekte.size(); i++)
		{
			if(this.projekte.get(i).getName().equals(this.selectedProject.getName()))
			{
				projekt = this.projekte.get(i);
				break; 
			}
		}
		
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("Suchergebnis", projekt); 
		
		return "projekt"; 
	}
	
	/**
	 * Methode schreibt ausgewählte Aufgabe in die Session und leitet weiter zur Detailansicht
	 * @return String
	 */
	public String zeigeAufgabeDetail()
	{
		//zugehöriges Projekt ermitteln 
		this.conn = this.db.verbindungHerstellen(); 
		Projekt projekt = this.db.ermittleProjektAufgabe(this.selectedAufgabe); 
		this.db.verbindungSchliessen();
		
		//Projekt setzen 
		this.selectedAufgabe.setProjekt(projekt);
		
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("Suchergebnis", this.selectedAufgabe); 
		return "aufgabe"; 
	}
	
	/**
	 * Methode leitet weiter zur Ansicht der eigenen Nutzerdaten
	 * @return String
	 */
	public String zeigeUserDetail()
	{
		return "profil"; 
	}
	
	/**
	 * Zeigt Erklärung zu Projekt in Dialogfenster an
	 * @return String
	 */
	public String zeigeProjektText()
	{
		String ausgabe = "Neues Projekt anlegen, bestehende Projekte einsehen, eigene Projekte bearbeiten"; 
		
		return ausgabe; 
	}
	
	/**
	 * Zeigt Erklärung zu Aufgabe in Dialogfenster an
	 * @return String
	 */
	public String zeigeAufgabeText()
	{
		String ausgabe = "Neue Aufgabe anlegen, bestehende Aufgaben einsehen, eigene Aufgaben bearbeiten"; 
		
		return ausgabe; 
	}
	
	/**
	 * Zeigt Erklärung zu Kanban-Board in Dialogfenster an
	 * @return String
	 */
	public String zeigeKanbanText()
	{
		String ausgabe = "Erklärung Kanban-Board, inaktive eigene Boards einsehen, aktive eigene Boards bearbeiten"; 
		
		return ausgabe; 
	}
	
	/**
	 * Zeigt Erklärung zu Visualisierung in Dialogfenster an
	 * @return String
	 */
	public String zeigeVisualisierungText()
	{
		String ausgabe = "Erklärung der Diagramme, grafische Darstellung der Projektfortschritte und der Termineinhaltung"; 
		
		return ausgabe; 
	}
	
	/**
	 * Zeigt Erklärung zu eigene Daten in Dialogfenster an
	 * @return String
	 */
	public String zeigeDatenText()
	{
		String ausgabe = "Ansehen und Bearbeiten der eigenen Nutzerdaten, Liste aller Nutzer, Nutzer zu Projekt hinzufügen"; 
		
		return ausgabe; 
	}
	
	/**
	 * Zeigt Erklärung zu Zurück in Dialogfenster an
	 * @return String
	 */
	public String zeigeZurückText()
	{
		String ausgabe = "Aufrufen des Hauptmenüs"; 
		
		return ausgabe; 
	}
	
	/**
	 * Methode meldet die Session des aktuellen Nutzers ab und führt zum Login zurück
	 * @return String
	 */
	public String logout()
	{
		System.out.println("logout");
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		
		return "logout";
	}
	
	/**
	 * Methode durchsucht Projekte, Aufgaben und Kanbans auf die eingegebene Buchstabenfolge hin
	 * @return String
	 */
	public String suche()
	{
		System.out.println("test");
		
		//lokale Variablen
		List<Projekt> projekte; 
		List<Aufgabe> aufgaben; 
		List<Kanban> kanbans; 
		
		//DB-Verbindung herstellen
		this.conn = this.db.verbindungHerstellen(); 
		
		//alle möglichen Projekte ermitteln 
		projekte = this.db.ermittleProjekteNachChar(this.suchbegriff); 
		
		//alle möglichen Aufgaben ermitteln
		aufgaben = this.db.ermittleAufgabenNachChar(this.suchbegriff); 
		
		//alle möglichen Kanbans ermitteln 
		kanbans = this.db.ermittleKanbansNachChar(this.suchbegriff); 
		
		this.db.verbindungSchliessen();
		
		//Listen in Session schreiben
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("SucheProjekte", projekte); 
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("SucheAufgaben", aufgaben);
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("SucheKanbans", kanbans);
		
		return "ergebnis"; 
	}
	
	/**
	 * Liefert den Suchbegriff
	 * @return String
	 */
	public String getSuchbegriff() 
	{
		return this.suchbegriff;
	}
	
	/**
	 * Setzt den Suchbegriff
	 * @param suchbegriff
	 */
	public void setSuchbegriff(String suchbegriff) 
	{
		this.suchbegriff = suchbegriff;
	}
	
	/**
	 * Liefert die Datenbankverbindung
	 * @return Connection
	 */
	public Connection getConn() 
	{
		return this.conn;
	}
	
	/**
	 * Setzt die Datenbankverbindung
	 * @param conn
	 */
	public void setConn(Connection conn) 
	{
		this.conn = conn;
	}
	
	/**
	 * Liefert Benutzername der aktuellen Session
	 * @return String
	 */
	public String getBenutzername() 
	{
		return this.benutzername;
	}
	
	/**
	 * Setzt Benutzername
	 * @param benutzername
	 */
	public void setBenutzername(String benutzername) 
	{
		this.benutzername = benutzername;
	}
	
	/**
	 * Liefert Vorname des angemeldeten Nutzers
	 * @return String
	 */
	public String getVorname() 
	{
		return this.vorname;
	}
	
	/**
	 * Setzt Vornamen
	 * @param vorname
	 */
	public void setVorname(String vorname) 
	{
		this.vorname = vorname;
	}
	
	/**
	 * Liefert Nachnamen des angemeldeten Nutzers
	 * @return String
	 */
	public String getNachname() 
	{
		return this.nachname;
	}
	
	/**
	 * Setzt Nachnamen
	 * @param nachname
	 */
	public void setNachname(String nachname) 
	{
		this.nachname = nachname;
	}
	
	/**
	 * Liefert Email des angemeldeten Nutzers
	 * @return String
	 */
	public String getEmail() 
	{
		return this.email;
	}
	
	/**
	 * Setzt Email
	 * @param email
	 */
	public void setEmail(String email) 
	{
		this.email = email;
	}
	
	/**
	 * Liefert Systemzeit
	 * @return String
	 */
	public String getZeit() 
	{
		return this.zeit;
	}
	
	/**
	 * Setzt Zeit als String
	 * @param zeit
	 */
	public void setZeit(String zeit) 
	{
		this.zeit = zeit;
	}
	
	/**
	 * Liefert alle Kanbans des angemeldeten Nutzers
	 * @return List<Kanban>
	 */
	public List<Kanban> getKanbans() 
	{
		return this.kanbans;
	}
	
	/**
	 * Setzt Liste von Kanbans
	 * @param kanbans
	 */
	public void setKanbans(List<Kanban> kanbans) 
	{
		this.kanbans = kanbans;
	}
	
	/**
	 * Liefert alle Projekte des angemeldeten Nutzers
	 * @return List<Projekt>
	 */
	public List<Projekt> getProjekte() 
	{
		return this.projekte;
	}
	
	/**
	 * Setzt Liste von Projekten
	 * @param projekte
	 */
	public void setProjekte(List<Projekt> projekte) 
	{
		this.projekte = projekte;
	}
	
	/**
	 * Liefert alle Aufgaben des angemeldeten Nutzers
	 * @return List<Aufgabe>
	 */
	public List<Aufgabe> getAufgaben() 
	{
		return this.aufgaben;
	}
	
	/**
	 * Setzt Liste von Aufgaben
	 * @param aufgaben
	 */
	public void setAufgaben(List<Aufgabe> aufgaben) 
	{
		this.aufgaben = aufgaben;
	}
	
	/**
	 * Liefert das ausgewählte Projekt
	 * @return Projekt
	 */
	public Projekt getSelectedProject() 
	{
		return this.selectedProject;
	}
	
	/**
	 * Setzt Projekt
	 * @param selectedProject
	 */
	public void setSelectedProject(Projekt selectedProject) 
	{
		this.selectedProject = selectedProject;
	}
	
	/**
	 * Liefert ausgewähltes Kanban
	 * @return Kanban
	 */
	public Kanban getSelectedKanban() 
	{
		return this.selectedKanban;
	}
	
	/**
	 * Setzt Kanban
	 * @param selectedKanban
	 */
	public void setSelectedKanban(Kanban selectedKanban) 
	{
		this.selectedKanban = selectedKanban;
	}
	
	/**
	 * Liefert ausgewählte Aufgabe
	 * @return Aufgabe
	 */
	public Aufgabe getSelectedAufgabe() 
	{
		return this.selectedAufgabe;
	}
	
	/**
	 * Setzt Aufgabe
	 * @param selectedAufgabe
	 */
	public void setSelectedAufgabe(Aufgabe selectedAufgabe) 
	{
		this.selectedAufgabe = selectedAufgabe;
	}
	
	

}
