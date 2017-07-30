package view;

import java.io.Serializable;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.RowEditEvent;

import model.Aufgabe;
import model.Konstanten;
import model.Nutzer;
import model.Projekt;
import persistenz.Datenbank;

@ManagedBean(name="aufgabenAnsichtView")
@ViewScoped
public class AufgabenAnsichtView implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instanz-Variablen
	 */
	@ManagedProperty(value="#{loginView.benutzername}")
	private String benutzername; 
	
	private List<Aufgabe> aufgaben; 
	private List<Projekt> projekte; 
	private List<String> prios; 
	private List<String> statusse;
	private List<Nutzer> nutzer; 
	private List<Aufgabe> todo; 
	private List<Aufgabe> inprogress; 
	private List<Aufgabe> testing; 
	private boolean archiviert[] = new boolean[2]; 

	private Aufgabe selectedAufgabe;
	
	private Datenbank db = new Datenbank(); 
	@SuppressWarnings("unused")
	private Connection conn = null; 
	
	/**
	 * Instanzvariablen, die nur von au�erhalb der Klasse aufgerufen werden
	 */
	@SuppressWarnings("unused")
	private String[] statusseBacklog; 
	@SuppressWarnings("unused")
	private String[] statusseToDo; 
	@SuppressWarnings("unused")
	private String[] statusseInProgress; 
	@SuppressWarnings("unused")
	private String[] statusseTesting; 
	@SuppressWarnings("unused")
	private String[] statusseDone; 
	@SuppressWarnings("unused")
	private String[] statusseBurn; 
	
	/**
	 * Methode f�llt die Listen vor Aufbau der Oberfl�che 
	 */
	@PostConstruct
	public void init()
	{
		this.archiviert[0] = false; 
		this.archiviert[1] = true; 
		
		
		//DB-Verbindung herstellen
		this.conn = this.db.verbindungHerstellen(); 
		
		Nutzer nutzer = this.db.ermittleNutzerNachBenutzername(this.benutzername); 
		
		//Listen aus Datenbank f�llen
		this.nutzer = this.db.ermittleNutzer(); 
		this.statusse = this.db.ermittleStatus(); 
		this.prios = this.db.ermittlePriorit�ten(); 
		this.projekte = this.db.ermittleProjekteMaster(nutzer);  
		this.aufgaben = this.db.ermittleAufgaben();
		
		//DB-Verbindung schlie�en
		this.db.verbindungSchliessen();
	}
	
	/**
	 * Default-Konstruktor
	 */
	public AufgabenAnsichtView()
	{
	}
	
	/**
	 * Methode f�hrt die �nderungen an der gew�hlten Aufgabe aus 
	 * @param event
	 */
	public void onRowEdit(RowEditEvent event)
	{
		//�nderung in Datenbank speichern
		String p_deadline = new SimpleDateFormat("yyyy-MM-dd").format(((Aufgabe) event.getObject()).getDeadline()); 
		int aufgabe_id = ((Aufgabe)event.getObject()).getId(); 
		String p_name = ((Aufgabe)event.getObject()).getName(); 
		String p_beschreibung = ((Aufgabe) event.getObject()).getBeschreibung(); 
		int p_aufwand = ((Aufgabe)event.getObject()).getSchaetzung(); 
		String p_prio = ((Aufgabe)event.getObject()).getPrioritaet(); 
		String p_stand = ((Aufgabe)event.getObject()).getStatus(); 
		Nutzer p_user = ((Aufgabe)event.getObject()).getInitiator();
		Projekt p_projekt = ((Aufgabe)event.getObject()).getProjekt();  
		int p_status; 
		boolean p_aktiv = ((Aufgabe)event.getObject()).isAktiv(); 
		
		
		//"Konvertierung" boolean in int f�r Datenbank
		if(p_aktiv)
		{
			p_status = 1;
		}
		else
		{
			p_status = 0; 
		}

		//Umwandlung in Date
		Date deadline = null; 
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
		try 
		{
			deadline = formatter.parse(p_deadline);
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		
		//Aufgaben-Objekt suchen und Daten setzen 
		Aufgabe aufgabe = null; 
		
		for(int i = 0; i < this.aufgaben.size(); i++)
		{
			if(this.aufgaben.get(i).getId() == aufgabe_id)
			{
				aufgabe = this.aufgaben.get(i); 
				aufgabe.setId(aufgabe_id);
				aufgabe.setName(p_name);
				aufgabe.setBeschreibung(p_beschreibung);
				aufgabe.setInitiator(p_user);
				aufgabe.setDeadline(deadline);
				aufgabe.setSchaetzung(p_aufwand);
				aufgabe.setPrioritaet(p_prio);
				aufgabe.setStatus(p_stand);
				aufgabe.setProjekt(p_projekt);
				aufgabe.setArchiviert(p_status);
			}
		}
		
		//bisherigen Bearbeitungsstand aus DB ermitteln und speichern
		//DB-Verbindung herstellen
		this.conn = this.db.verbindungHerstellen();
		
		String stand_alt = this.db.ermittleBearbeitungsstandAufgabe(aufgabe);  
		
		//Nutzer aus DB ermitteln
		String benutzernameDB = this.db.ermittleBenutzernameAufgabe(aufgabe); 
		
		//Nur Bearbeitung eigener Aufgaben und eigener Projekte zul�ssig 
		if(benutzernameDB.equals(this.benutzername))
		{
			
			//wenn Bearbeitungsstand ver�ndert:pr�fen, ob Verschiebung in gew�nschten Bearbeitungsstand �berhaupt zul�ssig
			//zul�ssigen WIP ermitteln 
			boolean zulaessig = false; 
			
			if(!p_stand.equals(stand_alt))
			{
				switch(p_stand)
				{
				case("To Do"):
					this.todo = this.db.ermittleToDo(p_projekt);
					if(this.todo.size() < Konstanten.WIPTODO)
					{
						zulaessig = true; 
					}
					break; 
				case("In Progress"):
					this.inprogress = this.db.ermittleInProgress(p_projekt); 
					if(this.inprogress.size() < Konstanten.WIPINPROGRESS)
					{
						zulaessig = true; 
					}
					break; 
				case("Testing"):
					System.out.println("test");
					this.testing = this.db.ermittleTesting(p_projekt); 
					if(this.testing.size() < Konstanten.WIPTESTING)
					{
						zulaessig = true; 
					}
					break; 
				default:
					System.out.println("Default");
					zulaessig = true; 
				}
			}
			else
			{
				zulaessig = true; 
			}

			if(zulaessig)
			{
				if(this.db.aktualisiereAufgabe(aufgabe))
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Eintrag ge�ndert", "Aufgabenname: " + p_name);
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				else
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Eintrag nicht ge�ndert", "Aufgabenname: " + p_name);
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Grenzanzahl f�r gew�hlten Status erreicht", "�nderung nicht m�glich");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}

		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "fehlende Berechtigung", "�nderung nur an eigenen Aufgaben zul�ssig");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			this.aufgaben = this.db.ermittleAufgaben(); 
		}
		 
		this.db.verbindungSchliessen();
		
	}
	
	/**
	 * Methode bricht Bearbeitung ohne �nderung ab
	 * @param event
	 */
	public void onRowCancel(RowEditEvent event)
	{
		//Meldung
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Keine �nderung vorgenommen", "Aufgabenname: " + ((Aufgabe) event.getObject()).getName());
		FacesContext.getCurrentInstance().addMessage(null, msg);
		
	}
	
	/**
	 * Methode l�scht ausgew�hlte Aufgabe aus der Datenbank
	 */
	public void loescheAufgabe()
	{
		//L�schen nur bei eigener Aufgabe zul�ssig
		if(this.selectedAufgabe.getInitiator().getBenutzername().equals(this.benutzername))
		{
			//ausgew�hltes Projekt aus DB l�schen
			this.conn = this.db.verbindungHerstellen(); 
			
			
			if(this.db.loescheAufgabe(this.selectedAufgabe))
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "L�schen erfolgreich", this.selectedAufgabe.getName() + " wurde gel�scht"); 
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "L�schen fehlgeschlagen", this.selectedAufgabe.getName() + " nicht gel�scht"); 
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			this.aufgaben = this.db.ermittleAufgaben();
			this.db.verbindungSchliessen(); 
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "fehlende Berechtigung", "Fremde Projekte d�rfen nicht gel�scht werden");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	/**
	 * Methode �ndert Zustand einer Aufgabe (aktiv, archiviert)
	 */
	public void archiviereAufgabe()
	{
		//Nur eigene Aufgabe darf bearbeitet werden
		if(this.selectedAufgabe.getInitiator().getBenutzername().equals(this.benutzername))
		{
			//ausgew�hltes Projekt archivieren und in DB aktualisieren
			this.conn = this.db.verbindungHerstellen(); 
			
			if(this.db.archiviereAufgabe(this.selectedAufgabe))
			{
				if(this.selectedAufgabe.getArchiviert()==1)
				{
					this.selectedAufgabe.setArchiviert(0);
				}
				else
				{
					this.selectedAufgabe.setArchiviert(1);
				}
				
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Status ge�ndert", this.selectedAufgabe.getName());
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Status nicht ge�ndert", this.selectedAufgabe.getName());
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			this.projekte = this.db.ermittleProjekte(); 
			this.db.verbindungSchliessen();
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "fehlende Berechtigung", "fremde Projekte d�rfen nicht archiviert werden");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	/**
	 * Liefert Benutzernamen der aktuellen Session
	 * @return String
	 */
	public String getBenutzername() 
	{
		return this.benutzername;
	}
	
	/**
	 * Setzt Benutzernamen der aktuellen Session
	 * @param benutzername
	 */
	public void setBenutzername(String benutzername) 
	{
		this.benutzername = benutzername;
	}
	
	/**
	 * Liefert Liste aller Projekte aus der Datenbank
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
	 * Liefert Liste aller Priorit�ten aus der Datenbank
	 * @return List<String> 
	 */
	public List<String> getPrios() 
	{
		return this.prios;
	}
	
	/**
	 * Setzt Priorit�ten 
	 * @param prios
	 */
	public void setPrios(List<String> prios) 
	{
		this.prios = prios;
	}
	
	/**
	 * Liefert Liste aller Bearbeitungszust�nde aus der Datenbank
	 * @return List<String> 
	 */
	public List<String> getStatusse() 
	{
		return this.statusse;
	}

	/**
	 * Setzt Bearbeitungszust�nde
	 * @param statusse
	 */
	public void setStatusse(List<String> statusse) 
	{
		this.statusse = statusse;
	}
	
	/**
	 * Liefert ausgew�hlte Aufgabe
	 * @return Aufgabe
	 */
	public Aufgabe getSelectedAufgabe() 
	{
		return this.selectedAufgabe;
	}
	
	/**
	 * Setzt ausgew�hlte Aufgabe
	 * @param selectedProjekt
	 */
	public void setSelectedAufgabe(Aufgabe selectedAufgabe) 
	{
		this.selectedAufgabe = selectedAufgabe;
	}
	
	/**
	 * Liefert Liste aller Aufgaben aus der Datenbank
	 * @return List<Aufgabe> 
	 */
	public List<Aufgabe> getAufgaben() 
	{
		return this.aufgaben;
	}
	
	/**
	 * Setzt Aufgaben
	 * @param aufgaben
	 */
	public void setAufgaben(List<Aufgabe> aufgaben) 
	{
		this.aufgaben = aufgaben;
	}
	
	/**
	 * Liefert Liste aller Nutzer aus der Datenbank
	 * @return List<Nutzer>
	 */
	public List<Nutzer> getNutzer() 
	{
		return this.nutzer;
	}
	
	/**
	 * Setzt Nutzer
	 * @param nutzer
	 */
	public void setNutzer(List<Nutzer> nutzer) 
	{
		this.nutzer = nutzer;
	}
	
	/**
	 * Liefert Array mit true und false
	 * @return boolean[]
	 */
	public boolean[] getArchiviert() 
	{
		return this.archiviert;
	}
	
	/**
	 * Setzt Array mit Zust�nden (true, false)
	 * @param archiviert
	 */
	public void setArchiviert(boolean[] archiviert) 
	{
		this.archiviert = archiviert;
	}
	
	/**
	 * Liefert alle Aufgaben mit Status "To Do"
	 * @return List<Aufgabe>
	 */
	public List<Aufgabe> getTodo() 
	{
		return this.todo;
	}
	
	/**
	 * Setzt Aufgaben in Status "To Do"
	 * @param todo
	 */
	public void setTodo(List<Aufgabe> todo) 
	{
		this.todo = todo;
	}
	
	/**
	 * Liefert alle Aufgaben mit Status "In Progress"
	 * @return List<Aufgabe>
	 */
	public List<Aufgabe> getInprogress() 
	{
		return this.inprogress;
	}
	
	/**
	 * Setzt Aufgaben in Status "In Progress" 
	 * @param inprogress
	 */
	public void setInprogress(List<Aufgabe> inprogress) 
	{
		this.inprogress = inprogress;
	}
	
	/**
	 * Liefert alle Aufgaben mit Status "Testing"
	 * @return List<Aufgabe>
	 */
	public List<Aufgabe> getTesting() 
	{
		return this.testing;
	}
	
	/**
	 * Setzt Aufgaben in Status "Testing"
	 * @param testing
	 */
	public void setTesting(List<Aufgabe> testing) 
	{
		this.testing = testing;
	}
	
	/**
	 * Liefert alle verf�gbaren Statusse, wenn Aufgabe im Backlog
	 * @return String[]
	 */
	public String[] getStatusseBacklog() 
	{
		return Konstanten.STATUSBACKLOG;
	}
	
	/**
	 * Setzt Statusse f�r Backlog
	 * @param statusseBacklog
	 */
	public void setStatusseBacklog(String[] statusseBacklog) 
	{
		this.statusseBacklog = statusseBacklog;
	}
	
	/**
	 * Liefert alle verf�gbaren Statusse, wenn Aufgabe in To Do
	 * @return String[]
	 */
	public String[] getStatusseToDo() 
	{
		return Konstanten.STATUSTODO;
	}
	
	/**
	 * Setzt Statusse f�r To Do
	 * @param statusseToDo
	 */
	public void setStatusseToDo(String[] statusseToDo) 
	{
		this.statusseToDo = statusseToDo;
	}
	
	/**
	 * Liefert alle verf�gbaren Statusse f�r Aufgabe in In Progress
	 * @return String[]
	 */
	public String[] getStatusseInProgress() 
	{
		return Konstanten.STATUSINPROGRESS;
	}
	
	/**
	 * Setzt Statusse f�r In Progress
	 * @param statusseInProgress
	 */
	public void setStatusseInProgress(String[] statusseInProgress) 
	{
		this.statusseInProgress = statusseInProgress;
	}
	
	/**
	 * Liefert alle verf�gbaren Statusse f�r Aufgabe in Testing
	 * @return List<String>
	 */
	public String[] getStatusseTesting() 
	{
		return Konstanten.STATUSTESTING;
	}
	
	/**
	 * Setzt Statusse f�r Testing
	 * @param statusseTesting
	 */
	public void setStatusseTesting(String[] statusseTesting) 
	{
		this.statusseTesting = statusseTesting;
	}
	
	/**
	 * Liefert alle verf�gbaren Statusse f�r Aufgabe in Done
	 * @return String[]
	 */
	public String[] getStatusseDone() 
	{
		return Konstanten.STATUSDONE;
	}
	
	/**
	 * Setzt Statusse f�r Done
	 * @param statusseDone
	 */
	public void setStatusseDone(String[] statusseDone) 
	{
		this.statusseDone = statusseDone;
	}
	
	/**
	 * Liefert alle verf�gbaren Statusse f�r Aufgabe in Burn
	 * @return String[]
	 */
	public String[] getStatusseBurn() 
	{
		return Konstanten.STATUSBURN;
	}
	
	/**
	 * Setzt Statusse f�r Burn
	 * @param statusseBurn
	 */
	public void setStatusseBurn(String[] statusseBurn) 
	{
		this.statusseBurn = statusseBurn;
	}
	
	
	

}
