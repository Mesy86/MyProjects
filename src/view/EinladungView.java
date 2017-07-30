package view;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import model.Nutzer;
import model.Projekt;
import persistenz.Datenbank;

@ManagedBean(name="einladungView")
@ViewScoped
public class EinladungView implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instanz-Variablen
	 */
	private List<Projekt> projekte;
	
	private Nutzer selectedNutzer; 
	private Projekt selectedProjekt; 
	
	private String projektname; 
	
	
	@ManagedProperty(value="#{loginView.benutzername}")
	private String benutzername;
	
	private Datenbank db = new Datenbank(); 
	@SuppressWarnings("unused")
	private Connection conn; 
	
	/**
	 * Methode f�llt die Listen, bevor die Oberfl�che erstellt wird
	 */
	@PostConstruct
	public void init()
	{
		this.conn = this.db.verbindungHerstellen(); 
		
		//aktuellen Nutzer aus der Session auslesen
		this.selectedNutzer = (Nutzer) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("Nutzer");
		
		//angemeldeten Nutzer ermitteln
		Nutzer nutzer = this.db.ermittleNutzerNachBenutzername(this.benutzername);
		
		//Alle Projekte aus Datenbank auslesen, zu denen der User Master ist
		this.projekte = this.db.ermittleProjekteMaster(nutzer);
		
		this.db.verbindungSchliessen();
		
	}
	
	/**
	 * Default-Konstruktor
	 */
	public EinladungView()
	{
	}
	
	public String userHinzufuegen()
	{
		boolean imTeam = false; 
		
		this.conn = this.db.verbindungHerstellen(); 
		
		//Projekt ermitteln aus DB
		this.selectedProjekt = this.db.findeProjektMitProjektname(this.projektname); 
		
		//Mitglieder des gew�hlten Projekts ermitteln aus der Datenbank
		List<Nutzer> team = this.db.ermittleNutzerProjekt(this.selectedProjekt); 
		
		//pr�fen, ob User bereits in Liste des gew�hlten Projekts
		for(int i = 0; i < team.size(); i++)
		{
			if(team.get(i).getBenutzername().equals(this.selectedNutzer.getBenutzername()))
			{
				imTeam = true; 
				break;
			}
		}
		
		if(imTeam)
		{
			//Wenn ja, entsprechender Hinweis
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Nutzer ist bereits im Projektteam", "Hinzuf�gen nicht n�tig");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		else
		{
			//Wenn nein, dann hinzuf�gen und best�tigung
			//Zu Liste hinzuf�gen
			this.selectedProjekt.userHinzufuegen(this.selectedNutzer);
			
			//in Datenbank hinzuf�gen
			if(this.db.teamerAnlegen(this.selectedNutzer, this.selectedProjekt))
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Nutzer wurde hinzugef�gt", "Nutzer ist nun im Projektteam");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nutzer konnte nicht hinzugef�gt werden", "Bitte erneut versuchen");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			
		}
		
		this.db.verbindungSchliessen();
		
		return "ansicht"; 
		
	}
	
	/**
	 * Methode liefert eine Liste von Projekten
	 * @return List<Projekt>
	 */
	public List<Projekt> getProjekte() 
	{
		return this.projekte;
	}
	
	/**
	 * Methode setzt eine Liste von Nutzern
	 * @param projekte
	 */
	public void setProjekte(List<Projekt> projekte) 
	{
		this.projekte = projekte;
	}
	
	/**
	 * Methode liefert den ausgew�hlten Nutzer
	 * @return Nutzer
	 */
	public Nutzer getSelectedNutzer() 
	{
		return this.selectedNutzer;
	}
	
	/**
	 * Methode setzt einen Nutzer als ausgew�hlten Nutzer
	 * @param selectedNutzer
	 */
	public void setSelectedNutzer(Nutzer selectedNutzer) 
	{
		this.selectedNutzer = selectedNutzer;
	}
	
	/**
	 * Liefert das gew�hlte Projekt
	 * @return Projekt
	 */
	public Projekt getSelectedProjekt() 
	{
		return this.selectedProjekt;
	}
	
	/**
	 * Setzt das gew�hlte Projekt
	 * @param selectedProjekt
	 */
	public void setSelectedProjekt(Projekt selectedProjekt) 
	{
		this.selectedProjekt = selectedProjekt;
	}
	
	/**
	 * Liefert Benutzername des aktuellen Nutzers
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

	public String getProjektname() {
		return projektname;
	}

	public void setProjektname(String projektname) {
		this.projektname = projektname;
	} 
	

}
