package view;

import java.io.Serializable;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;

import model.Kanban;
import model.Nutzer;
import model.Projekt;
import persistenz.Datenbank;

@ManagedBean(name="projektAnlegenView")
@RequestScoped
public class ProjektAnlegenView implements Serializable 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instanz-Variablen
	 */
	@NotNull(message="Bitte Projektnamen eingeben")
	private String projektname; 
	
	@NotNull(message="Bitte Deadline eingeben")
	private Date projektDeadline; 
	
	private String projektPrio; 
	
	private String projektBeschreibung; 
	
	@NotNull(message="Bitte Name für Kanban-Board eingeben")
	private String kanbanName; 
	private Nutzer nutzer; 
	private List<String> prios;
	
	private Datenbank db = new Datenbank(); 
	@SuppressWarnings("unused")
	private Connection conn = null; 
	
	/**
	 * Instanz-Variablen aus anderen Klassen
	 */
	@ManagedProperty(value="#{loginView}")
	private LoginView login; 
	
	@ManagedProperty(value="#{loginView.benutzername}")
	private String benutzername; 
	
	@PostConstruct
	public void init()
	{
		//Nutzer bestimmen
		this.conn = this.db.verbindungHerstellen(); 
		
		//Prioritäten auslesen
		this.prios = this.db.ermittlePrioritäten(); 
		
		//DB schließen
		this.db.verbindungSchliessen();
	}
	
	
	/**
	 * Default-Konstruktor
	 */
	public ProjektAnlegenView()
	{
	}
	
	public void speichern()
	{
		
		//neues Projekt in Datenbank speichern
		this.conn = this.db.verbindungHerstellen(); 
		
		//nutzer ermitteln
		this.nutzer = this.db.findeUserMitBenutzername(this.benutzername);
		
		//user_id ermitteln
		int user_id = this.nutzer.getId(); 
		
		//Prio-Id ermittlen
		int prio_id = this.db.ermittlePrioIdNachNamen(this.projektPrio); 
		
		//Prüfen, ob Deadline nach aktuellem Datum und innerhalb der Projekt-Deadline liegt
		//aktuelles Datum ermitteln
		Date aktuellesDatum = new Date(); 
		
		//String-Repräsentanten
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
		String aktuellString = df.format(aktuellesDatum); 
		
		//Abgleich und ggf Eintrag in Datenbank
		if(this.projektDeadline.after(aktuellesDatum))
		{
			//Kanban anlegen
			if(this.db.kanbanAnlegen(this.kanbanName))
			{
				
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Kanban-Board wurde angelegt", "");
				FacesContext.getCurrentInstance().addMessage(null, msg);
				
				//Kanban_Id ermitteln
				int kanban_id = this.db.ermittleKanbanIdNachName(this.kanbanName);
				
				//Kanban-Objekt auslesen und id zuteilen
				Kanban kanban = this.db.findeKanbanMitName(this.kanbanName); 
				kanban.setId(kanban_id);
				
				//Projekt-Objekt anlegen
				Projekt projekt = new Projekt(this.projektname); 
				projekt.setBeschreibung(this.projektBeschreibung);
				projekt.setDeadline(this.projektDeadline);
				projekt.setInitiator(this.nutzer);
				
				//Projekt anlegen 
				if(this.db.projektAnlegen(projekt, prio_id, user_id, kanban_id))
				{
					
					msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Projekt wurde angelegt", "");
					FacesContext.getCurrentInstance().addMessage(null, msg);
					//projekt = this.db.findeProjektMitProjektname(this.projektname); 
					//projekt.setKanban(kanban);
					
					//Nutzer zur Mitgliederliste des Projekts hinzufügen
					projekt.userHinzufuegen(this.nutzer);
					
					this.felderleeren();
					
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Projekt konnte nicht angelegt werden"));
					
					msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Projekt konnte nicht angelegt werden", "");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}
			else
			{
				
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kanban-Board konnte nicht angelegt werden", "");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Deadline liegt in der Vergangenheit", "Deadline muss nach " + aktuellString + " liegen");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}

		this.db.verbindungSchliessen();
	}
	
	/**
	 * interne Methode
	 * leert die Felder des Formulars
	 */
	private void felderleeren()
	{
		this.setProjektname("");
		this.setProjektDeadline(null);
		this.setProjektPrio("Bitte auswählen");
		this.setKanbanName("");
		this.setProjektBeschreibung("");
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
	 * Liefert Deadline des Projekts
	 * @return Date
	 */
	public Date getProjektDeadline() 
	{
		return this.projektDeadline;
	}
	
	/**
	 * Setzt Deadline des Projekts
	 * @param projektDeadline
	 */
	public void setProjektDeadline(Date projektDeadline) 
	{
		this.projektDeadline = projektDeadline;
	}
	
	/**
	 * Liefert Priorität des Projekts
	 * @return String
	 */
	public String getProjektPrio() 
	{
		return this.projektPrio;
	}
	
	/**
	 * Setzt Priorität des Projekts
	 * @param projektPrio
	 */
	public void setProjektPrio(String projektPrio) 
	{
		this.projektPrio = projektPrio;
	}
	
	/**
	 * Liefert beschreibung des Projekts
	 * @return String
	 */
	public String getProjektBeschreibung() 
	{
		return this.projektBeschreibung;
	}
	
	/**
	 * Setzt Beschreibung des Projekts
	 * @param projektBeschreibung
	 */
	public void setProjektBeschreibung(String projektBeschreibung) 
	{
		this.projektBeschreibung = projektBeschreibung;
	}
	
	/**
	 * Liefert Namen des Kanban-Boards
	 * @return String
	 */
	public String getKanbanName() 
	{
		return this.kanbanName;
	}
	
	/**
	 * Setzt Namen des Kanban-Boards
	 * @param kanbanName
	 */
	public void setKanbanName(String kanbanName) 
	{
		this.kanbanName = kanbanName;
	}

	/**
	 * Liefert angemeldeten Nutzer
	 * @return Nutzer
	 */
	public Nutzer getNutzer() 
	{
		return this.nutzer;
	}

	/**
	 * Liefert alle Prioritäten aus DB
	 * @return List<String>
	 */
	public List<String> getPrios() 
	{
		return this.prios;
	}

	/**
	 * Liefert Bean LoginView
	 * @return LoginView
	 */
	public LoginView getLogin() 
	{
		return this.login;
	}

	/**
	 * Setzt Bean LoginView
	 * @param login
	 */
	public void setLogin(LoginView login) 
	{
		this.login = login;
	}

	
	/**
	 * Liefert benutzername aus LoginView
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
	
	
	

}
