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

import model.Kanban;
import model.Nutzer;
import model.Projekt;
import persistenz.Datenbank;

@ManagedBean(name="projektAnsichtView")
@ViewScoped
public class ProjektAnsichtView implements Serializable
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
	
	private List<Projekt> projekte; 
	private List<Nutzer>  initiatoren; 
	private List<String> prios; 
	private List<Kanban> kanbans; 
	private Projekt selectedProjekt;
	private boolean status[] = new boolean[2]; 
	private Nutzer master;
	private String masterName;
	
	private Datenbank db = new Datenbank(); 
	@SuppressWarnings("unused")
	private Connection conn = null; 
	
	@PostConstruct
	public void init()
	{
		this.status[0] = false; 
		this.status[1] = true; 
		
		//DB Verbindung herstellen
		this.conn = this.db.verbindungHerstellen(); 
		
		//Liste mit Projekten füllen aus Datenbank
		this.projekte = this.db.ermittleProjekte(); 
		
		//Liste mit Prioritäten füllen
		this.prios = this.db.ermittlePrioritäten(); 
		
		//Liste mit Nutzern füllen
		this.initiatoren = this.db.ermittleNutzer(); 
		
		//Liste mit Kanbans füllen
		this.kanbans = this.db.ermittleKanban(); 
		
		//DB-Verbindung schließen
		this.db.verbindungSchliessen();
		
	}
	
	/**
	 * Default-Konstruktor
	 */
	public ProjektAnsichtView()
	{
	}
	
	/**
	 * Methode führt Tabellenänderung durch
	 * @param event
	 */
	public void onRowEdit(RowEditEvent event)
	{
		//DB-Verbindung herstellen
		this.conn = this.db.verbindungHerstellen();
				
		//Änderung in Datenbank speichern
		String p_deadline = new SimpleDateFormat("yyyy-MM-dd").format(((Projekt) event.getObject()).getDeadline());  
		int projekt_id = ((Projekt)event.getObject()).getId(); 
		String p_name = ((Projekt)event.getObject()).getName(); 
		String p_beschreibung = ((Projekt) event.getObject()).getBeschreibung(); 
		String p_prio = ((Projekt)event.getObject()).getPrioritaet(); 
		Nutzer p_user = ((Projekt)event.getObject()).getInitiator();
		Kanban p_kanban = ((Projekt)event.getObject()).getKanban(); 
		int p_status; 
		boolean p_aktiv = ((Projekt)event.getObject()).isAktiv(); 
		
		
		//User-Objekt ermitteln
		Nutzer userNeu = this.db.ermittleNutzerNachBenutzername(p_user.getBenutzername()); 
		
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
		
		//"Konvertierung" boolean in int für Datenbank
		if(p_aktiv)
		{
			p_status = 1;
		}
		else
		{
			p_status = 0; 
		}
		
		//Projekt-Objekt suchen und neue Daten setzen
		Projekt projekt = null; 
		
		for(int i = 0; i < this.projekte.size(); i++)
		{
			if(this.projekte.get(i).getName().equals(p_name))
			{
				projekt = this.projekte.get(i); 
				projekt.setId(projekt_id);
				projekt.setName(p_name);
				projekt.setBeschreibung(p_beschreibung);
				projekt.setPrioritaet(p_prio);
				projekt.setInitiator(p_user);
				projekt.setArchiviert(p_status);
				projekt.setKanban(p_kanban);
				projekt.setDeadline(deadline);
				
			}
		}
		
		//Ermitteln und Speichern des bisherigen Masters
		String masterAltBenutzername = this.db.ermittleMasterNachProjektid(projekt_id);
		
		
		if(masterAltBenutzername.equals(this.benutzername))
		{
			
			if(this.db.aktualisiereProjekt(projekt))
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Eintrag geändert", "Projektname: " + ((Projekt) event.getObject()).getName());
				FacesContext.getCurrentInstance().addMessage(null, msg);
				
				//prüfen, ob (neuer) Master bereits Teamer
				//Mitglieder des gewählten Projekts ermitteln aus der Datenbank
				boolean imTeam = false; 
				List<Nutzer> team = this.db.ermittleNutzerProjekt(projekt); 
				
				//prüfen, ob User bereits in Liste des gewählten Projekts
				for(int i = 0; i < team.size(); i++)
				{
					if(team.get(i).getBenutzername().equals(p_user.getBenutzername()))
					{
						imTeam = true; 
						break;
					}
				}
				
				//Wenn nein, dann als neuen Teamer dort eintragen
				if(!imTeam)
				{
					//in Datenbank hinzufügen
					if(this.db.teamerAnlegen(userNeu, projekt))
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
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Eintrag nicht geändert", "Projektname: " + ((Projekt) event.getObject()).getName());
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			//this.projekte = this.db.ermittleProjekte(); 
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "fehlende Berechtigung", "Änderung nur an eigenen Projekten zulässig");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			this.projekte = this.db.ermittleProjekte(); 
		}
		
		this.db.verbindungSchliessen();
	}
	
	/**
	 * Methode bricht Tabellenänderung ab
	 * @param event
	 */
	public void onRowCancel(RowEditEvent event)
	{
		//Meldung
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Keine Änderung vorgenommen", "Projektname: " + ((Projekt) event.getObject()).getName());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	/**
	 * Methode löscht gewählte Projekt aus der Datenbank
	 */
	public void deleteProject()
	{
		
		if(this.selectedProjekt.getInitiator().getBenutzername().equals(this.benutzername))
		{
			//ausgewähltes Projekt aus DB löschen
			this.conn = this.db.verbindungHerstellen(); 
			
			
			if(this.db.loescheProjekt(this.selectedProjekt))
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Löschen erfolgreich", this.selectedProjekt.getName() + " wurde gelöscht"); 
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Löschen fehlgeschlagen", this.selectedProjekt.getName() + " nicht gelöscht"); 
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			this.projekte = this.db.ermittleProjekte(); 
			this.db.verbindungSchliessen(); 
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "fehlende Berechtigung", "Fremde Projekte dürfen nicht gelöscht werden");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
	}
	
	/**
	 * Methode ändert den Zustand des Projekts (aktiv, archiviert)
	 */
	public void archiveProjekt()
	{
		if(this.selectedProjekt.getInitiator().getBenutzername().equals(this.benutzername))
		{
			//ausgewähltes Projekt archivieren und in DB aktualisieren
			this.conn = this.db.verbindungHerstellen(); 
			
			if(this.db.archiviereProjekt(this.selectedProjekt))
			{
				if(this.selectedProjekt.getArchiviert()==1)
				{
					this.selectedProjekt.setArchiviert(0);
				}
				else
				{
					this.selectedProjekt.setArchiviert(1);
				}
				
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Status geändert", this.selectedProjekt.getName());
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Status nicht geändert", this.selectedProjekt.getName());
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			this.projekte = this.db.ermittleProjekte(); 
			this.db.verbindungSchliessen();
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "fehlende Berechtigung", "fremde Projekte dürfen nicht archiviert werden");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		
		
	}
	
	/**
	 * Liefert Projekte
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
	 * Methode liefert alle eingetragenen User
	 * @return List<Nutzer>
	 */
	public List<Nutzer> getInitiatoren() 
	{
		return this.initiatoren;
	}
	
	/**
	 * Methode setzt alle Nutzer
	 * @param initiatoren
	 */
	public void setInitiatoren(List<Nutzer> initiatoren) 
	{
		this.initiatoren = initiatoren;
	}
	
	/**
	 * Methode liefert alle eingetragenen Prioritäten aus der Datenbank
	 * @return List<String> 
	 */
	public List<String> getPrios() 
	{
		return this.prios;
	}
	
	/**
	 * Methode setzt alle Prioritäten
	 * @param prios
	 */
	public void setPrios(List<String> prios) 
	{
		this.prios = prios;
	}

	public Projekt getSelectedProjekt() {
		return selectedProjekt;
	}

	public void setSelectedProjekt(Projekt selectedProjekt) {
		this.selectedProjekt = selectedProjekt;
	}
	
	/**
	 * Liefert die zwei Status eines Objekts (true, false)
	 * @return boolean[]
	 */
	public boolean[] getStatus() 
	{
		return this.status;
	}
	
	/**
	 * Setzt Status eines Objekts (true, false)
	 * @param status
	 */
	public void setStatus(boolean[] status) 
	{
		this.status = status;
	}
	
	/**
	 * Liefert alle Kanbans aus Datenbank
	 * @return List<Kanban>
	 */
	public List<Kanban> getKanbans() 
	{
		return this.kanbans;
	}
	
	/**
	 * Setzt Kanbans
	 * @param kanbans
	 */
	public void setKanbans(List<Kanban> kanbans) 
	{
		this.kanbans = kanbans;
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
	 * Setzt Benutzername der aktuellen Session
	 * @param benutzername
	 */
	public void setBenutzername(String benutzername) 
	{
		this.benutzername = benutzername;
	}
	
	/**
	 * Liefert den Master des Projekts (Verantwortlicher)
	 * @return Nutzer
	 */
	public Nutzer getMaster() 
	{
		return this.master;
	}
	
	/**
	 * Setzt den Master (Verantwortlichen) des Projekts
	 * @param master
	 */
	public void setMaster(Nutzer master) 
	{
		this.master = master;
	}
	
	/**
	 * Liefert Benutzername des masters
	 * @return String
	 */
	public String getMasterName() 
	{
		return masterName;
	}
	
	/**
	 * Setzt Benutzername des masters
	 * @param masterName
	 */
	public void setMasterName(String masterName) 
	{
		this.masterName = masterName;
	}
	
	
		
}
