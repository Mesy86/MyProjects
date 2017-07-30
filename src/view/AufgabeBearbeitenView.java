package view;

import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import model.Aufgabe;
import model.Konstanten;
import model.Nutzer;
import model.Projekt;
import persistenz.Datenbank;

@ManagedBean(name="aufgabeBearbeitenView")
@RequestScoped
public class AufgabeBearbeitenView implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instanzvariablen
	 */
	@ManagedProperty(value="#{projektSucheView}")
	private ProjektSucheView view; 
	
	private Aufgabe selectedAufgabe; 
	
	@ManagedProperty(value="#{loginView.benutzername}")
	private String benutzername; 
	
	private String aufgabenname; 
	
	private Date aufgabenDeadline;  
	
	private int aufgabenSchaetzung; 
	
	private String aufgabenPrio; 
	
	private String aufgabenStand; 
	
	private Projekt projekt; 
	
	private String projektname; 
	
	private String aufgabenBeschreibung; 
	
	private String aufgabenZustand; 
	
	private Nutzer nutzer; 
	
	private List<String> prios;
	
	private List<String> staende;
	
	/**
	 * Instanz-Variablen, die nur von außerhalb benötigt werden
	 */
	@SuppressWarnings("unused")
	private String[] statusseBacklog; 
	@SuppressWarnings("unused")
	private String[] statusseTodo; 
	@SuppressWarnings("unused")
	private String[] statusseInprogress; 
	@SuppressWarnings("unused")
	private String[] statusseTesting; 
	@SuppressWarnings("unused")
	private String[] statusseDone; 
	@SuppressWarnings("unused")
	private String[] statusseBurn; 
	@SuppressWarnings("unused")
	private String formattedDeadline;
	
	
	private List<Projekt> projekte; 
	
	private List<String> zustaende = new ArrayList<String>(); 
	
	private Datenbank db = new Datenbank(); 
	@SuppressWarnings("unused")
	private Connection conn = null;
	
	/**
	 * Methode liest das gewählte Objekt und die Instanz-Variablen, bevor die Oberfläche aufgebaut wird
	 */
	@PostConstruct
	public void init()
	{
		//Listen füllen
		this.conn = this.db.verbindungHerstellen(); 
		
		Nutzer p_nutzer = this.db.ermittleNutzerNachBenutzername(this.benutzername); 
		this.prios = this.db.ermittlePrioritäten(); 
		this.staende = this.db.ermittleStatus(); 
		this.projekte = this.db.ermittleProjekteMaster(p_nutzer);  
		
		this.zustaende.add("archiviert"); 
		this.zustaende.add("aktiv"); 
		
		
		//selectedAufgabe aus SessionMap auslesen
		this.selectedAufgabe = (Aufgabe) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("Suchergebnis"); 
		
		
		//Angaben füllen
		if(this.selectedAufgabe != null)
		{
			this.aufgabenname = this.selectedAufgabe.getName(); 
			this.aufgabenDeadline = this.selectedAufgabe.getDeadline(); 
			this.formattedDeadline = this.selectedAufgabe.getFormattedDate(); 
			this.aufgabenSchaetzung = this.selectedAufgabe.getSchaetzung(); 
			this.aufgabenPrio = this.selectedAufgabe.getPrioritaet(); 
			this.aufgabenStand = this.selectedAufgabe.getStatus(); 
			this.projekt = this.selectedAufgabe.getProjekt(); 
			this.projektname = this.selectedAufgabe.getProjekt().getName(); 
			this.aufgabenBeschreibung = this.selectedAufgabe.getBeschreibung();
			this.nutzer = this.selectedAufgabe.getInitiator(); 
			
			if(this.selectedAufgabe.getArchiviert() == 0)
			{
				this.aufgabenZustand = "archiviert";
			}
			else
			{
				this.aufgabenZustand = "aktiv"; 
			}
			
		}
		
		this.db.verbindungSchliessen();
	}
	
	/**
	 * Default-Konstruktor
	 */
	public AufgabeBearbeitenView()
	{
	}
	
	/**
	 * Methode prüft, ob Änderung zulässig ist und nimmt ggf die Änderung in der Datenbank vor
	 * @return String
	 */
	public String speichern()
	{
		String ausgabe = ""; 
		
		//Änderungen speichern 
		int p_id = this.selectedAufgabe.getId();  
		int p_zustand;  
		
		if(this.aufgabenZustand.equals("archiviert"))
		{
			p_zustand = 0; 
			
		}
		else
		{
			p_zustand = 1; 
		}
		
		//Wenn eigene Aufgabe, dann Änderung in Datenbank übernehmen
		if(this.nutzer.getBenutzername().equals(this.benutzername))
		{
			//DB-Verbindung herstellen
			this.conn = this.db.verbindungHerstellen(); 
			
			//prüfen, ob Verschiebung in gewünschten Bearbeitungsstand überhaupt zulässig
			 
			//zulässigen WIP ermitteln 
			boolean zulaessig = false; 
			
			switch(this.aufgabenStand)
			{
			case("To Do"):
				List<Aufgabe> todo = this.db.ermittleToDo(this.selectedAufgabe.getProjekt());
				if(todo.size() < Konstanten.WIPTODO)
				{
					zulaessig = true; 
				}
				break; 
			case("In Progress"):
				List<Aufgabe> inprogress = this.db.ermittleInProgress(this.selectedAufgabe.getProjekt()); 
				if(inprogress.size() < Konstanten.WIPINPROGRESS)
				{
					zulaessig = true; 
				}
				break; 
			case("Testing"):
				List<Aufgabe> testing = this.db.ermittleTesting(this.selectedAufgabe.getProjekt()); 
				if(testing.size() < Konstanten.WIPTESTING)
				{
					zulaessig = true; 
				}
				break; 
			default:
				zulaessig = true; 
			}
			
			if(zulaessig)
			{
				//Aufgabe anlegen 
				Aufgabe aufgabe = new Aufgabe(p_id, this.aufgabenname, this.aufgabenBeschreibung, this.nutzer); 
				aufgabe.setDeadline(this.aufgabenDeadline);
				aufgabe.setSchaetzung(this.aufgabenSchaetzung);
				aufgabe.setPrioritaet(this.aufgabenPrio);
				aufgabe.setStatus(this.aufgabenStand);
				aufgabe.setProjekt(this.projekt);
				aufgabe.setArchiviert(p_zustand);
				
				// Speichern
				if(this.db.aktualisiereAufgabe(aufgabe))
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aufgabenänderung gespeichert", ""); 
					FacesContext.getCurrentInstance().addMessage(null, msg);
					ausgabe = "detail"; 
				}
				else
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aufgabenänderung nicht gespeichert", ""); 
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Genzanzahl für gewählten Bereich bereits erreicht", "");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			
			this.db.verbindungSchliessen();
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehlende Berechtigung", "Nur eigene Aufgaben dürfen bearbeitet werden"); 
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		return ausgabe; 
	}
	
	/**
	 * Methode löscht die ausgewählte Aufgabe
	 * @return String
	 */
	public String loeschen()
	{
		String ausgabe = ""; 
		//Löschen nur zulässig, wenn eigene Aufgabe
		if(this.nutzer.getBenutzername().equals(this.benutzername))
		{
			//DB-Verbindung herstellen
			this.conn = this.db.verbindungHerstellen(); 
			
			//Aufgabe löschen
			if(this.db.loescheAufgabe(this.selectedAufgabe))
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Löschen erfolgreich", this.selectedAufgabe.getName() + " wurde gelöscht"); 
				FacesContext.getCurrentInstance().addMessage(null, msg);
				
				ausgabe = "zurück"; 
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Löschen fehlgeschlagen", this.selectedAufgabe.getName() + " nicht gelöscht"); 
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			this.db.verbindungSchliessen();
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Fehlende Berechtigung", "fremde Aufgabe dürfen nicht gelöscht werden");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		return ausgabe;
	}
	
	/**
	 * Methode ändert den Zustand der Aufgabe (aktiv/archiviert) 
	 */
	public String archivieren()
	{
		String ausgabe = ""; 
		//Archivieren nur zulässig, wenn eigene Aufgabe
		if(this.nutzer.getBenutzername().equals(this.benutzername))
		{
			//DB-Verbindung herstellen
			this.conn = this.db.verbindungHerstellen(); 
			
			if(this.db.archiviereAufgabe(this.selectedAufgabe))
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Änderung erfolgreich", this.selectedAufgabe.getName() + ": Zustand wurde geändert"); 
				FacesContext.getCurrentInstance().addMessage(null, msg); 
				
				ausgabe = "zurück"; 
				
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Änderung fehlgeschlagen", this.selectedAufgabe.getName() + ": Zustand nicht geändert"); 
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			this.db.verbindungSchliessen();
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Fehlende Berechtigung", "fremde Aufgabe dürfen nicht gelöscht werden"); 
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		return ausgabe; 
	}
	
	/**
	 * Liefert Objekt der ProjektSucheView
	 * @return ProjektSucheView
	 */
	public ProjektSucheView getView() 
	{
		return this.view;
	}
	
	/**
	 * Setzt Objekt der ProjektSucheView
	 * @param view
	 */
	public void setView(ProjektSucheView view) 
	{
		this.view = view;
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
	 * Setzt ausgewählte Aufgabe
	 * @param selectedAufgabe
	 */
	public void setSelectedAufgabe(Aufgabe selectedAufgabe) 
	{
		this.selectedAufgabe = selectedAufgabe;
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
	 * Setzt benutzername der aktuellen Session
	 * @param benutzername
	 */
	public void setBenutzername(String benutzername) 
	{
		this.benutzername = benutzername;
	}
	
	/**
	 * Liefert Name der Aufgabe
	 * @return String
	 */
	public String getAufgabenname() 
	{
		return this.aufgabenname;
	}
	
	/**
	 * Setzt Name der Aufgabe
	 * @param aufgabenname
	 */
	public void setAufgabenname(String aufgabenname) 
	{
		this.aufgabenname = aufgabenname;
	}
	
	/**
	 * Liefert Deadline der Aufgabe
	 * @return Date
	 */
	public Date getAufgabenDeadline() 
	{
		return this.aufgabenDeadline;
	}
	
	/**
	 * Setzt Deadline der Aufgabe
	 * @param aufgabenDeadline
	 */
	public void setAufgabenDeadline(Date aufgabenDeadline) 
	{
		this.aufgabenDeadline = aufgabenDeadline;
	}
	
	/**
	 * Liefert geschätzten Aufwand in h 
	 * @return int
	 */
	public int getAufgabenSchaetzung() 
	{
		return this.aufgabenSchaetzung;
	}
	
	/**
	 * Setzt geschätzten Aufwand in h 
	 * @param aufgabenSchaetzung
	 */
	public void setAufgabenSchaetzung(int aufgabenSchaetzung) 
	{
		this.aufgabenSchaetzung = aufgabenSchaetzung;
	}
	
	/**
	 * Liefert Aufgabenpriorität
	 * @return String
	 */
	public String getAufgabenPrio() 
	{
		return this.aufgabenPrio;
	}
	
	/**
	 * Setzt Aufgabenpriorität
	 * @param aufgabenPrio
	 */
	public void setAufgabenPrio(String aufgabenPrio) 
	{
		this.aufgabenPrio = aufgabenPrio;
	}
	
	/**
	 * Liefert Stand der Bearbeitung
	 * @return String
	 */
	public String getAufgabenStand() 
	{
		return this.aufgabenStand;
	}
	
	/**
	 * Setzt Bearbeitungsstand
	 * @param aufgabenStand
	 */
	public void setAufgabenStand(String aufgabenStand) 
	{
		this.aufgabenStand = aufgabenStand;
	}
	
	/**
	 * Liefert Projekt der Aufgabe
	 * @return Projekt
	 */
	public Projekt getProjekt() 
	{
		return this.projekt;
	}
	
	/**
	 * Setzt Projekt der Aufgabe
	 * @param projekt
	 */
	public void setProjekt(Projekt projekt) 
	{
		this.projekt = projekt;
	}
	
	/**
	 * Liefert Aufgabenbeschreibung
	 * @return String
	 */
	public String getAufgabenBeschreibung() 
	{
		return this.aufgabenBeschreibung;
	}
	
	/**
	 * Setzt Aufgabenbeschreibung
	 * @param aufgabenBeschreibung
	 */
	public void setAufgabenBeschreibung(String aufgabenBeschreibung) 
	{
		this.aufgabenBeschreibung = aufgabenBeschreibung;
	}
	
	/**
	 * Liefert Zustand der Aufgabe (aktiv/archiviert)
	 * @return String
	 */
	public String getAufgabenZustand() 
	{
		return this.aufgabenZustand;
	}
	
	/**
	 * Setzt Zustand der Aufgabe (aktiv/archiviert)
	 * @param aufgabenZustand
	 */
	public void setAufgabenZustand(String aufgabenZustand) 
	{
		this.aufgabenZustand = aufgabenZustand;
	}
	
	/**
	 * Liefert Nutzer, der Aufgabe angelegt hat
	 * @return Nutzer
	 */
	public Nutzer getNutzer() 
	{
		return this.nutzer;
	}
	
	/**
	 * Setzt Nutzer, der Aufgabe angelegt hat
	 * @param nutzer
	 */
	public void setNutzer(Nutzer nutzer) 
	{
		this.nutzer = nutzer;
	}
	
	/**
	 * Liefert alle Prioritäten aus der Datenbank
	 * @return List<String> 
	 */
	public List<String> getPrios() 
	{
		return this.prios;
	}
	
	/**
	 * Setzt Prioritäten
	 * @param prios
	 */
	public void setPrios(List<String> prios) 
	{
		this.prios = prios;
	}
	
	/**
	 * Liefert alle Bearbeitungsstände aus der Datenbank
	 * @return List<String> 
	 */
	public List<String> getStaende() 
	{
		return this.staende;
	}
	
	/**
	 * Setzt Bearbeitungsstände 
	 * @param staende
	 */
	public void setStaende(List<String> staende) 
	{
		this.staende = staende;
	}
	
	/**
	 * Liefert alle Projekte aus der Datenbank
	 * @return List<Projekt> 
	 */
	public List<Projekt> getProjekte() 
	{
		return this.projekte;
	}
	
	/**
	 * Setzt Projekte
	 * @param projekte
	 */
	public void setProjekte(List<Projekt> projekte) 
	{
		this.projekte = projekte;
	}
	
	/**
	 * Liefert alle Zustände(aktiv/archiviert)
	 * @return List<String> 
	 */
	public List<String> getZustaende() 
	{
		return this.zustaende;
	}
	
	/**
	 * Setzt Zustände
	 * @param zustaende
	 */
	public void setZustaende(List<String> zustaende) 
	{
		this.zustaende = zustaende;
	}
	
	/**
	 * Liefert Projektname
	 * @return String
	 */
	public String getProjektname() 
	{
		return this.projektname;
	}
	
	/**
	 * Setzt Projektname
	 * @param projektname
	 */
	public void setProjektname(String projektname) 
	{
		this.projektname = projektname;
	}
	
	/**
	 * Liefert Deadline als String
	 * @return String
	 */
	public String getFormattedDeadline() 
	{
		return new SimpleDateFormat("yyyy-MM-dd").format(this.aufgabenDeadline);
	}
	
	/**
	 * Setzt Deadline als String
	 * @param formattedDeadline
	 */
	public void setFormattedDeadline(String formattedDeadline) 
	{
		this.formattedDeadline = formattedDeadline;
	}
	
	/**
	 * Liefert verfügbare Statusse der Aufgaben im Backlog
	 * @return String[]
	 */
	public String[] getStatusseBacklog() 
	{
		
		//neue Liste ausgeben
		return Konstanten.STATUSBACKLOG;
	}
	
	/**
	 * Setzt Statusse für Backlog
	 * @param statusseBacklog
	 */
	public void setStatusseBacklog(String[] statusseBacklog) 
	{
		this.statusseBacklog = statusseBacklog;
	}
	
	/**
	 * Liefert alle verfügbaren Statusse der Aufgaben in To Do
	 * @return String[]
	 */
	public String[] getStatusseTodo() 
	{
		return Konstanten.STATUSTODO;
	}
	
	/**
	 * Setzt Statusse für To Do
	 * @param statusseTodo
	 */
	public void setStatusseTodo(String[] statusseTodo) 
	{
		this.statusseTodo = statusseTodo;
	}
	
	/**
	 * Liefert alle verfügbaren Statusse der Aufgaben in In Progress
	 * @return String[]
	 */
	public String[] getStatusseInprogress() 
	{
		return Konstanten.STATUSINPROGRESS;
	}
	
	/**
	 * Setzt Statusse für In Progress
	 * @param statusseInprogress
	 */
	public void setStatusseInprogress(String[] statusseInprogress) 
	{
		this.statusseInprogress = statusseInprogress;
	}
	
	/**
	 * Liefert alle verfügbaren Statusse der Aufgaben in Testing
	 * @return String[]
	 */
	public String[] getStatusseTesting() 
	{
		return Konstanten.STATUSTESTING;
	}
	
	/**
	 * Setzt Statusse für Testing
	 * @param statusseTesting
	 */
	public void setStatusseTesting(String[] statusseTesting) 
	{
		this.statusseTesting = statusseTesting;
	}
	
	/**
	 * Liefert verfügbare Statusse der Aufgaben in Done
	 * @return String[]
	 */
	public String[] getStatusseDone() 
	{
		return Konstanten.STATUSDONE;
	}
	
	/**
	 * Setzt Statusse für Done
	 * @param statusseDone
	 */
	public void setStatusseDone(String[] statusseDone) 
	{
		this.statusseDone = statusseDone;
	}
	
	/**
	 * Liefert verfügbare Statusse der Aufgaben in Burn
	 * @return String[]
	 */
	public String[] getStatusseBurn() 
	{
		return Konstanten.STATUSBURN;
	}
	
	/**
	 * Setzt Statusse für Burn
	 */
	public void setStatusseBurn(String[] statusseBurn) 
	{
		this.statusseBurn = statusseBurn;
	}
	
	

}
