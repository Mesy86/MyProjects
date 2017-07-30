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

@ManagedBean(name="visualisierungAuswahlView")
@ViewScoped
public class VisualisierungAuswahlView implements Serializable
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
	
	private String projektname; 
	
	private Projekt selectedProject; 
	
	private List<Projekt> projekte; 
	
	private Datenbank db = new Datenbank(); 
	@SuppressWarnings("unused")
	private Connection conn = null; 
	
	/**
	 * Methode wird vor Aufbau der Seite aufgerufen
	 * Holt alle projekte aus der Datenbank 
	 */
	@PostConstruct
	public void init()
	{
		if(this.benutzername != null)
		{
			this.conn = this.db.verbindungHerstellen(); 
			Nutzer p_nutzer = this.db.ermittleNutzerNachBenutzername(this.benutzername); 
			this.projekte = this.db.ermittleProjekteNutzer(p_nutzer); 
			this.db.verbindungSchliessen();
			
		}
		
	}
	
	/**
	 * Default-Konstruktor
	 */
	public VisualisierungAuswahlView()
	{
	}
	
	/**
	 * Methode ermittelt das gewählte Projekt und schreibt dieses in die Session
	 * @return String
	 */
	public String zeigeFortschritt()
	{
		String ausgabe = ""; 
		
		//Projekt ermitteln
		if(!this.projektname.equals("Bitte auswählen"))
		{
			for(int i = 0; i < this.projekte.size(); i++)
			{
				if(this.projekte.get(i).getName().equals(this.projektname))
				{
					this.selectedProject = this.projekte.get(i); 
					break; 
				}
			}
			
			//Suchergebnis in SessionMap schreiben
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("Projektfortschritt", this.selectedProject); 
			
			ausgabe = "fortschritt"; 
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Suchbegriff eingeben", "Bitte wählen Sie ein Projekt aus"); 
			FacesContext.getCurrentInstance().addMessage(null, msg);
			
		}
		return ausgabe; 
	}
	
	/**
	 * Methode ermittelt das ausgewählte Projekt und schreibt dieses in die Session
	 * @return String
	 */
	public String zeigeTermineinhaltung()
	{
		String ausgabe = ""; 
		
		//Projekt ermitteln
		if(!this.projektname.equals("Bitte auswählen"))
		{
			for(int i = 0; i < this.projekte.size(); i++)
			{
				if(this.projekte.get(i).getName().equals(this.projektname))
				{
					this.selectedProject = this.projekte.get(i); 
					break; 
				}
			}
			
			//Suchergebnis in SessionMap schreiben
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("Termineinhaltung", this.selectedProject); 
			
			ausgabe = "einhaltung"; 
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Suchbegriff eingeben", "Bitte wählen Sie ein Projekt aus"); 
			FacesContext.getCurrentInstance().addMessage(null, msg);
			
		}
		return ausgabe; 
	}
	
	/**
	 * Liefert Projektnamen
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
	 * Liefert ausgewähltes Projekt
	 * @return Projekt
	 */
	public Projekt getSelectedProject() 
	{
		return this.selectedProject;
	}
	
	/**
	 * Setzt ausgewähltes Projekt
	 * @param selectedProject
	 */
	public void setSelectedProject(Projekt selectedProject) 
	{
		this.selectedProject = selectedProject;
	}
	
	/**
	 * Liefert Liste aller eigenen Projekte aus der DB 
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

}
