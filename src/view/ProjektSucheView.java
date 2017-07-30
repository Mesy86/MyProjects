package view;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import model.Projekt;
import persistenz.Datenbank;

@ManagedBean(name="projektSucheView")
@ViewScoped
public class ProjektSucheView implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instanz-Variablen
	 */
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
		this.conn = this.db.verbindungHerstellen(); 
		this.projekte = this.db.ermittleProjekte(); 
		this.db.verbindungSchliessen();
	}
	
	/**
	 * Default-Konstruktor
	 */
	public ProjektSucheView()
	{
		
	}
	
	/**
	 * Methode sucht Projekt anhand des Projektnamens 
	 * @return String
	 */
	public String sucheProjekt()
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
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("Suchergebnis", this.selectedProject); 
			
			ausgabe = "bearbeiten"; 
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Suchbegriff eingeben", "Bitte wählen Sie ein Projekt aus"); 
			FacesContext.getCurrentInstance().addMessage(null, msg);
			
		}
		
		
		return ausgabe; 
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
	 * Liefert alle Projekte aus der DB
	 * @return List<Projekt>
	 */
	public List<Projekt> getProjekte() 
	{
		return this.projekte;
	}
	
	/**
	 * Setzt alle Projekte
	 * @param projekte
	 */
	public void setProjekte(List<Projekt> projekte) 
	{
		this.projekte = projekte;
	}
	
	
}
