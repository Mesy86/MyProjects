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

import model.Kanban;
import model.Nutzer;
import model.Projekt;
import persistenz.Datenbank;

@ManagedBean(name="projektBearbeitenView")
@RequestScoped
public class ProjektBearbeitenView implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instanz-Variablen
	 */
	@ManagedProperty(value="#{projektSucheView}")
	private ProjektSucheView view; 
	
	private Projekt selectedProject; 
	
	@ManagedProperty(value="#{loginView.benutzername}")
	private String benutzername; 
	
	private String projektname; 
	
	private Date projektDeadline; 
	
	private String projektPrio; 
	
	private String projektBeschreibung;  
	
	private String kanbanName; 
	
	private String masterName; 
	
	private Nutzer nutzer; 
	
	private String projektZustand; 
	
	private List<String> prios;
	
	private List<String> zustaende = new ArrayList<String>(); 
	
	private List<Nutzer> initiatoren; 
	
	private Datenbank db = new Datenbank(); 
	@SuppressWarnings("unused")
	private Connection conn = null; 
	
	/**
	 * Instanzvariable, die nur außerhalb der Klasse benötigt wird
	 */
	@SuppressWarnings("unused")
	private String formattedDeadline;
	
	@PostConstruct
	public void init()
	{	
		//Prioritäten-Liste füllen
		this.conn = this.db.verbindungHerstellen(); 
		this.prios = this.db.ermittlePrioritäten(); 
		this.initiatoren = this.db.ermittleNutzer(); 
		this.db.verbindungSchliessen(); 
		
		this.zustaende.add("archiviert"); 
		this.zustaende.add("aktiv"); 
		
		//selectedProject auslesen
		this.selectedProject = (Projekt) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("Suchergebnis"); 
		
		//Angaben füllen
		if(this.selectedProject != null)
		{
			this.projektname = this.selectedProject.getName(); 
			this.projektDeadline = this.selectedProject.getDeadline(); 
			this.formattedDeadline = this.selectedProject.getFormattedDate(); 
			this.projektPrio = this.selectedProject.getPrioritaet(); 
			this.projektBeschreibung = this.selectedProject.getBeschreibung(); 
			this.kanbanName = this.selectedProject.getKanban().getKanbanName(); 
			this.nutzer = this.selectedProject.getInitiator(); 
			this.masterName = this.nutzer.getBenutzername(); 
			
			System.out.println("Name: " + this.nutzer.getBenutzername());
			
			if(this.selectedProject.getArchiviert() == 0)
			{
				this.projektZustand = "archiviert";
			}
			else
			{
				this.projektZustand = "aktiv"; 
			}
		}
	}
	
	/**
	 * Default-Konstruktor
	 */
	public ProjektBearbeitenView()
	{
	}
	
	/**
	 * Methode speichert Änderungen am Projekt, wenn es ein eigenes Projekt ist
	 */
	public String speichern()
	{
		String ausgabe = ""; 
		
		//Änderungen speichern
		
		int projekt_id = this.selectedProject.getId();

		// Nutzer des Projekts ermitteln
		//Nutzer p_user = this.selectedProject.getInitiator(); 
		Kanban p_kanban = this.selectedProject.getKanban(); 
		int p_status;  
		
		//Verbindung herstellen
		this.conn = this.db.verbindungHerstellen(); 
		
		//neuen Nutzer ermitteln
		Nutzer masterNeu = this.db.ermittleNutzerNachBenutzername(this.masterName); 
		
		if(this.projektZustand.equals("archiviert"))
		{
			p_status = 0; 
			
		}
		else
		{
			p_status = 1; 
		}
	
		
		//Ermitteln und Speichern des bisherigen masters
		String masterAltBenutzername = this.db.ermittleMasterNachProjektid(projekt_id); 
		
		if(masterAltBenutzername.equals(this.benutzername))
		{
			//Änderung speichern
			
			//Projekt anlegen
			Projekt projekt = new Projekt(projekt_id, this.projektname, masterNeu); 
			projekt.setBeschreibung(this.projektBeschreibung);
			projekt.setDeadline(this.projektDeadline);
			projekt.setPrioritaet(this.projektPrio);
			projekt.setArchiviert(p_status);
			projekt.setKanban(p_kanban);
			
			// Speichern
			if(this.db.aktualisiereProjekt(projekt))
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Projektänderung gespeichert", ""); 
				FacesContext.getCurrentInstance().addMessage(null, msg);
				
				//prüfen, ob (neuer) Master bereits Teamer
				//Mitglieder des gewählten Projekts ermitteln aus der Datenbank
				boolean imTeam = false; 
				List<Nutzer> team = this.db.ermittleNutzerProjekt(projekt); 
				
				//prüfen, ob User bereits in Liste des gewählten Projekts
				for(int i = 0; i < team.size(); i++)
				{
					if(team.get(i).getBenutzername().equals(masterNeu.getBenutzername()))
					{
						imTeam = true; 
						break;
					}
				}
				
				//Wenn nein, dann als neuen Teamer dort eintragen
				if(!imTeam)
				{
					//in Datenbank hinzufügen
					if(this.db.teamerAnlegen(masterNeu, projekt))
					{
						msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Teamer wurde hinzugefügt", "Nutzer ist nun im Projektteam");
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}
					else
					{
						msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Teamer konnte nicht hinzugefügt werden", "Bitte erneut versuchen");
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				}
				
				
				ausgabe = "detail"; 
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Projektänderung nicht gespeichert", ""); 
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "fehlende Berechtigung", "Projekt darf nicht bearbeitet werden"); 
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		this.db.verbindungSchliessen();
		
		return ausgabe; 
	}
	
	/**
	 * Methode löscht angezeigtes Projekt, wenn es ein eigenes Projekt ist 
	 * @return String
	 */
	public String loeschen()
	{
		String ausgabe = ""; 
		// Datensatz löschen
		if(this.selectedProject.getInitiator().getBenutzername().equals(this.benutzername))
		{
			this.conn = this.db.verbindungHerstellen(); 
			
			if(this.db.loescheProjekt(this.selectedProject))
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Löschen erfolgreich", this.selectedProject.getName() + " wurde gelöscht"); 
				FacesContext.getCurrentInstance().addMessage(null, msg);
				ausgabe = "zurück"; 
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Löschen fehlgeschlagen", this.selectedProject.getName() + " nicht gelöscht"); 
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			this.db.verbindungSchliessen(); 
			
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Fehlende Berechtigung", "fremde Projekte dürfen nicht gelöscht werden");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		return ausgabe;
		
	}
	
	/**
	 * Methode archiviert angezeigtes Projekt, wenn es ein eigenes Projekt ist 
	 */
	public String archivieren()
	{
		String ausgabe = ""; 
		
		// Datensatz archivieren
		if(this.selectedProject.getInitiator().getBenutzername().equals(this.benutzername))
		{
			this.conn = this.db.verbindungHerstellen(); 
			
			if(this.db.archiviereProjekt(this.selectedProject))
			{
				if(this.selectedProject.getArchiviert()==1)
				{
					this.selectedProject.setArchiviert(0);
				}
				else
				{
					this.selectedProject.setArchiviert(1);
				}
				
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Status geändert", this.selectedProject.getName());
				FacesContext.getCurrentInstance().addMessage(null, msg);
				
				ausgabe="zurück"; 
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Status nicht geändert", this.selectedProject.getName());
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			this.db.verbindungSchliessen();
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "fehlende Berechtigung", "fremdes Projekt darf nicht archiviert werden"); 
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		return ausgabe; 
	}
	
	/**
	 * Liefert View aus ManagedProperty
	 * @return ProjektSucheView
	 */
	public ProjektSucheView getView() 
	{
		return this.view;
	}
	
	/**
	 * Setzt View aus ManagedProperty
	 * @param view
	 */
	public void setView(ProjektSucheView view) 
	{
		this.view = view;
	}
	
	/**
	 * Liefert ausgewähltes Projekt aus ManagedProperty
	 * @return Projekt
	 */
	public Projekt getSelectedProject() 
	{
		return this.selectedProject;
	}
	
	/**
	 * Setzt ausgewählte Projekt aus ManagedProperty
	 * @param selectedProject
	 */
	public void setSelectedProject(Projekt selectedProject) 
	{
		this.selectedProject = selectedProject;
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
	 * Liefert Deadline
	 * @return Date
	 */
	public Date getProjektDeadline() 
	{
		return this.projektDeadline;
	}
	
	/**
	 * Setzt Deadline 
	 * @param projektDeadline
	 */
	public void setProjektDeadline(Date projektDeadline) 
	{
		this.projektDeadline = projektDeadline;
	}
	
	/**
	 * Liefert Projekt-Priorität
	 * @return String
	 */
	public String getProjektPrio() 
	{
		return this.projektPrio;
	}
	
	/**
	 * Setzt Projekt-Priorität
	 * @param projektPrio
	 */
	public void setProjektPrio(String projektPrio) 
	{
		this.projektPrio = projektPrio;
	}
	
	/**
	 * Liefert Projekt-Beschreibung
	 * @return String
	 */
	public String getProjektBeschreibung() 
	{
		return this.projektBeschreibung;
	}
	
	/**
	 * Setzt Projektbeschreibung
	 * @param projektBeschreibung
	 */
	public void setProjektBeschreibung(String projektBeschreibung) 
	{
		this.projektBeschreibung = projektBeschreibung;
	}
	
	/**
	 * Liefert Kanban-Namen
	 * @return String
	 */
	public String getKanbanName() 
	{
		return this.kanbanName;
	}
	
	/**
	 * Setzt Kanban-Name
	 * @param kanbanName
	 */
	public void setKanbanName(String kanbanName) 
	{
		this.kanbanName = kanbanName;
	}
	
	/**
	 * Liefert Projekt-Initiator
	 * @return Nutzer
	 */
	public Nutzer getNutzer() 
	{
		return this.nutzer;
	}
	
	/**
	 * Setzt Projekt-Initiator
	 * @param nutzer
	 */
	public void setNutzer(Nutzer nutzer) 
	{
		this.nutzer = nutzer;
	}
	
	/**
	 * Liefert alle Prioritäten aus Datenbank
	 * @return List<String> 
	 */
	public List<String> getPrios() 
	{
		return this.prios;
	}
	
	/**
	 * Setzt alle Prioritäten
	 * @param prios
	 */
	public void setPrios(List<String> prios) 
	{
		this.prios = prios;
	}
	
	/**
	 * Liefert Benutzername der Session
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
	 * Liefert Zustand des Projekts (aktiv/archiviert)
	 * @return String
	 */
	public String getProjektZustand() 
	{
		return this.projektZustand;
	}
	
	/**
	 * Setzt Zustand des Projekts (aktiv/archiviert)
	 * @param projektZustand
	 */
	public void setProjektZustand(String projektZustand) 
	{
		this.projektZustand = projektZustand;
	}
	
	/**
	 * Liefert Zustände 
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
	 * Liefert Deadline als String
	 * @return String
	 */
	public String getFormattedDeadline() 
	{
		return new SimpleDateFormat("yyyy-MM-dd").format(this.projektDeadline);
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
	 * Liefert den Namen des Projektmasters
	 * @return String
	 */
	public String getMasterName() 
	{
		return this.masterName;
	}
	
	/**
	 * Setzt Namen des Projektmasters
	 * @param masterName
	 */
	public void setMasterName(String masterName) 
	{
		this.masterName = masterName;
	}
	
	/**
	 * Liefert Liste aller Nutzer
	 * @return List<Nutzer>
	 */
	public List<Nutzer> getInitiatoren() 
	{
		return this.initiatoren;
	}
	
	/**
	 * Setzt Liste von Nutzern
	 * @param initiatoren
	 */
	public void setInitiatoren(List<Nutzer> initiatoren) 
	{
		this.initiatoren = initiatoren;
	}
	
	
	
	

}
