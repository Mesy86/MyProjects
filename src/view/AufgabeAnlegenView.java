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

import model.Aufgabe;
import model.Nutzer;
import model.Projekt;
import persistenz.Datenbank;

@ManagedBean(name="aufgabeAnlegenView")
@RequestScoped
public class AufgabeAnlegenView implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instanz-Variablen
	 */
	private int aufgabeId; 
	
	@NotNull (message="Bitte einen Aufgabennamen angeben")
	private String aufgabename; 
	
	@NotNull (message="Bitte eine Deadline eingeben")
	private Date aufgabeDeadline; 
	
	private String aufgabePrio; 
	
	private String aufgabeStatus; 
	
	private String aufgabeBeschreibung; 
	
	private int aufgabeSchaetzung; 
	
	private String projektname; 
	
	private Nutzer nutzer; 
	
	private List<String> prios;
	private List<String> statusse; 
	private List<Projekt> projekte; 
	
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
	
	/**
	 * Methode wird ausgeführt, bevor die Oberfläche aufgebaut wird
	 */
	@PostConstruct
	public void init()
	{	
		this.conn = this.db.verbindungHerstellen(); 
		
		this.prios = this.db.ermittlePrioritäten(); 
		this.statusse = this.db.ermittleStatus(); 
		
		if(this.benutzername != null)
		{	
			Nutzer p_nutzer = this.db.ermittleNutzerNachBenutzername(this.benutzername); 
			this.projekte = this.db.ermittleProjekteNutzer(p_nutzer); 
		}
		
		this.db.verbindungSchliessen();
	}
	
	/**
	 * Default-Konstruktor
	 */
	public AufgabeAnlegenView()
	{
	}
	
	/**
	 * Speichert neue Aufgabe in der Datenbank
	 */
	public void speichern()
	{
		this.conn = this.db.verbindungHerstellen(); 
		
		//nutzer ermitteln
		this.nutzer = this.db.findeUserMitBenutzername(this.benutzername);
		
		//user_id ermitteln
		int user_id = this.nutzer.getId(); 
				
		//Priorität-id ermitteln anhand Bezeichnung
		int prioritaet_id = this.db.ermittlePrioIdNachNamen(aufgabePrio); 
		
		//Projekt-id ermitteln anhand Name
		int projekt_id = this.db.ermittleProjektIdNachName(this.projektname); 
		
		//Prüfen, ob Deadline nach aktuellem Datum und innerhalb der Projekt-Deadline liegt
		//aktuelles Datum ermitteln
		Date aktuellesDatum = new Date(); 
		
		//Deadline des Projekts ermitteln 
		Date deadlineProjekt = this.db.ermittleDeadlineProjekt(projekt_id); 
		
		//String-Repräsentanten
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
		String aktuellString = df.format(aktuellesDatum); 
		String projektString = df.format(deadlineProjekt); 
		
		//Abgleich und ggf Eintrag in Datenbank
		if(this.aufgabeDeadline.after(aktuellesDatum))
		{
			if(this.aufgabeDeadline.before(deadlineProjekt))
			{
				//Aufgabe anlegen
				Aufgabe aufgabe = new Aufgabe();
				aufgabe.setName(this.aufgabename);
				aufgabe.setBeschreibung(this.aufgabeBeschreibung);
				aufgabe.setDeadline(this.aufgabeDeadline);
				aufgabe.setSchaetzung(this.aufgabeSchaetzung);
				
				//Aufgabe in DB speichern
				if(this.db.aufgabeAnlegen(aufgabe, prioritaet_id, projekt_id, user_id))
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aufgabe wurde angelegt", "Aufgabe: " + this.aufgabename);
					FacesContext.getCurrentInstance().addMessage(null, msg);
					
					this.felderleeren();
				}
				else
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aufgabe konnte nicht angelegt werden", "Aufgabe: " + this.aufgabename);
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Deadline liegt nach Projektdeadline", "Deadline muss vor " + projektString + " liegen");
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
		this.setAufgabename("");
		this.setAufgabeDeadline(null);
		this.setAufgabeSchaetzung(0);
		this.setAufgabePrio("Bitte auswählen");
		this.setProjektname("Bitte auswählen");
		this.setAufgabeBeschreibung("");
	}
	
	/**
	 * Liefert Aufgabe_Id
	 * @return int
	 */
	public int getAufgabeId() 
	{
		return this.aufgabeId;
	}
	
	/**
	 * Setzt Aufgabe_id
	 * @param aufgabeId
	 */
	public void setAufgabeId(int aufgabeId) 
	{
		this.aufgabeId = aufgabeId;
	}
	
	/**
	 * Liefert Name der Aufgabe
	 * @return String
	 */
	public String getAufgabename() 
	{
		return this.aufgabename;
	}
	
	/**
	 * Setzt Name der Aufgabe
	 * @param aufgabename
	 */
	public void setAufgabename(String aufgabename) 
	{
		this.aufgabename = aufgabename;
	}
	
	/**
	 * Liefert Deadline der Aufgabe
	 * @return Date
	 */
	public Date getAufgabeDeadline() 
	{
		return this.aufgabeDeadline;
	}
	
	/**
	 * Setzt Deadline der Aufgabe
	 * @param aufgabeDeadline
	 */
	public void setAufgabeDeadline(Date aufgabeDeadline) 
	{
		this.aufgabeDeadline = aufgabeDeadline;
	}
	
	/**
	 * Liefert Priorität der Aufgabe
	 * @return String
	 */
	public String getAufgabePrio() 
	{
		return this.aufgabePrio;
	}
	
	/**
	 * Setzt Priorität der Aufgabe
	 * @param aufgabePrio
	 */
	public void setAufgabePrio(String aufgabePrio) 
	{
		this.aufgabePrio = aufgabePrio;
	}
	
	/**
	 * Liefert Bearbeitungsstatus der Aufgabe
	 * @return String
	 */
	public String getAufgabeStatus() 
	{
		return this.aufgabeStatus;
	}
	
	/**
	 * Setzt Bearbeitungsstatus der Aufgabe
	 * @param aufgabeStatus
	 */
	public void setAufgabeStatus(String aufgabeStatus) 
	{
		this.aufgabeStatus = aufgabeStatus;
	}
	
	/**
	 * Liefert Aufgabenbeschreibung
	 * @return String
	 */
	public String getAufgabeBeschreibung() 
	{
		return this.aufgabeBeschreibung;
	}
	
	/**
	 * Setzt Aufgabenbeschreibung
	 * @param aufgabeBeschreibung
	 */
	public void setAufgabeBeschreibung(String aufgabeBeschreibung) 
	{
		this.aufgabeBeschreibung = aufgabeBeschreibung;
	}
	
	/**
	 * Liefert Schätzung des Arbeitsaufwands in Stunden
	 * @return int
	 */
	public int getAufgabeSchaetzung() 
	{
		return this.aufgabeSchaetzung;
	}
	
	/**
	 * Setzt geschätzten Aufwand in Stunden
	 * @param aufgabeSchaetzung
	 */
	public void setAufgabeSchaetzung(int aufgabeSchaetzung) 
	{
		this.aufgabeSchaetzung = aufgabeSchaetzung;
	}
	
	/**
	 * Liefert übergeordnetes Projekt (Name)
	 * @return Projekt
	 */
	public String getProjektname() 
	{
		return this.projektname;
	}
	
	/**
	 * Setzt übergeordnetes Projekt (Name)
	 * @param projekt
	 */
	public void setProjektname(String projektname) 
	{
		this.projektname = projektname;
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
	 * Liefert alle Bearbeitungszustände aus der Datenbank
	 * @return List <String> 
	 */
	public List<String> getStatusse() 
	{
		return this.statusse;
	}
	
	/**
	 * Setzt Bearbeitungszustände
	 * @param statusse
	 */
	public void setStatusse(List<String> statusse) 
	{
		this.statusse = statusse;
	}
	
	/**
	 * Liefert Instanz der LoginView
	 * @return LoginView
	 */
	public LoginView getLogin() 
	{
		return this.login;
	}
	
	/**
	 * Setzt Instanz der LoginView
	 * @param login
	 */
	public void setLogin(LoginView login) 
	{
		this.login = login;
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
	 * Setzt Benutzername der Session
	 * @param benutzername
	 */
	public void setBenutzername(String benutzername) 
	{
		this.benutzername = benutzername;
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
	 * Liefert aktuellen Nutzer
	 * @return Nutzer
	 */
	public Nutzer getNutzer() 
	{
		return this.nutzer;
	}
	
	/**
	 * Setzt aktuellen Nutzer
	 * @param nutzer
	 */
	public void setNutzer(Nutzer nutzer) 
	{
		this.nutzer = nutzer;
	}

}
