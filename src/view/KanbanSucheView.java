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

import model.Kanban;
import model.Nutzer;
import model.Projekt;
import persistenz.Datenbank;

@ManagedBean(name="kanbanSucheView")
@ViewScoped
public class KanbanSucheView implements Serializable
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
	
	private Nutzer nutzer; 
	
	private String projektname; 
	
	private String kanbanName; 
	
	private Projekt selectedProject; 
	
	private List<Projekt> projekte; 
	
	private List<Kanban> kanban; 
	
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
		this.selectedProject = null; 
		
		if(this.benutzername != null)
		{
			this.conn = this.db.verbindungHerstellen(); 
			this.nutzer = this.db.ermittleNutzerNachBenutzername(this.benutzername); 
			
			this.projekte = this.db.ermittleProjekteNutzer(this.nutzer);  
			this.kanban = this.db.ermittleKanbanNutzer(this.nutzer); 
			
			this.db.verbindungSchliessen();
		}
		
	}
	
	/**
	 * Default-Konstruktor
	 */
	public KanbanSucheView()
	{
		
	}
	
	/**
	 * Methode sucht Kanban-Board anhand des Projektnamens und des Namens des Kanban-Boards
	 * @return String
	 */
	public String sucheKanban()
	{
		String ausgabe = ""; 
		int i; 
		
		//Projekt ermitteln
		if(!this.projektname.equals("Bitte auswählen") && !this.kanbanName.equals("Bitte auswählen"))
		{
			for(i = 0; i < this.projekte.size(); i++)
			{
				if(this.projekte.get(i).getName().equals(this.projektname) && this.projekte.get(i).getKanban().getKanbanName().equals(this.kanbanName))
				{
					this.selectedProject = this.projekte.get(i);
					break; 
				}
			}
			
			if(this.selectedProject == null)
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Keine Übereinstimmung", "Projekt und Kanbanboard stimmen nicht überein");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			else
			{
				//Suchergebnis in SessionMap schreiben
				FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("Suchergebnis", this.selectedProject); 
				
				//Weiterleitung je nach dem ob Projekt aktiv oder inaktiv
				if(this.selectedProject.isAktiv())
				{
					ausgabe="kanban";
				}
				else
				{
					ausgabe = "kanbanInaktiv"; 
				}
				
			}
			
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Suchbegriffe eingeben", "Bitte wählen Sie Projekt und Kanbanboard aus"); 
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
	 * Liefert alle Kanbans aus der Datenbank
	 * @return List<Kanban>
	 */
	public List<Kanban> getKanban() 
	{
		return this.kanban;
	}
	
	/**
	 * Setzt Kanbans
	 * @param kanban
	 */
	public void setKanban(List<Kanban> kanban) 
	{
		this.kanban = kanban;
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
	 * Liefert Nutzer der aktuellen Session
	 * @return Nutzer
	 */
	public Nutzer getNutzer() 
	{
		return this.nutzer;
	}
	
	/**
	 * Setzt Nutzer der aktuellen Session
	 * @param nutzer
	 */
	public void setNutzer(Nutzer nutzer) 
	{
		this.nutzer = nutzer;
	}

}
