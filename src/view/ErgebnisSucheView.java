package view;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import model.Aufgabe;
import model.Kanban;
import model.Projekt;
import persistenz.Datenbank;

@ManagedBean(name="ergebnisSucheView")
@RequestScoped
public class ErgebnisSucheView implements Serializable
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
	private List<Aufgabe> aufgaben; 
	private List<Kanban> kanbans; 
	
	private Kanban selectedKanban; 
	private Aufgabe selectedAufgabe; 
	private Projekt selectedProjekt; 
	
	private Datenbank db = new Datenbank(); 
	@SuppressWarnings("unused")
	private Connection conn = null; 
	
	/**
	 * Methode füllt die Listen aus der Session, bevor die Oberfläche aufgebaut wird
	 */
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init()
	{
		this.selectedAufgabe = null; 
		this.selectedKanban = null; 
		this.selectedProjekt = null; 
		
		this.projekte = (List<Projekt>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("SucheProjekte");
		this.aufgaben = (List<Aufgabe>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("SucheAufgaben");
		this.kanbans = (List<Kanban>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("SucheKanbans");
	}
	/**
	 * Default-Konstruktor
	 */
	public ErgebnisSucheView()
	{	
	}
	
	/**
	 * Methode schreibt ausgewähltes Kanban in die Session und leitet weiter zur Ansicht des Boards
	 * @return String
	 */
	public String zeigeKanbanDetail()
	{
		String ausgabe = ""; 
		
		//Ermittle Projekt zu Kanban
		this.conn = this.db.verbindungHerstellen(); 
		Projekt projekt = this.db.ermittleProjektKanban(this.selectedKanban); 
		this.db.verbindungSchliessen();
		
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("Suchergebnis", projekt); 
		
		//Weiterleitung nur, wenn eigenes Kanban-Board
		if(projekt.getInitiator().getBenutzername().equals(this.benutzername))
		{
			//Weiterleitung je nach dem ob Projekt aktiv oder inaktiv
			System.out.println("" + projekt.isAktiv());
			if(projekt.isAktiv())
			{
				ausgabe="kanban";
			}
			else
			{
				ausgabe = "kanbanInaktiv"; 
			}
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Es können nur eigene Kanban-Boards eingesehen werden", "Weiterleitung nicht möglich"); 
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		
		return ausgabe; 
	}
	
	/**
	 * Methode schreibt ausgewähltes Projekt in die Session und leitet weiter zur Detailansicht
	 * @return String
	 */
	public String zeigeProjektDetail()
	{
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("Suchergebnis", this.selectedProjekt); 
		
		return "projekt"; 
	}
	
	/**
	 * Methode schreibt ausgewählte Aufgabe in die Session und leitet weiter zur Detailansicht
	 * @return String
	 */
	public String zeigeAufgabeDetail()
	{
		//zugehöriges Projekt ermitteln 
		this.conn = this.db.verbindungHerstellen(); 
		Projekt projekt = this.db.ermittleProjektAufgabe(this.selectedAufgabe); 
		this.db.verbindungSchliessen();
		
		//Projekt setzen 
		this.selectedAufgabe.setProjekt(projekt);
		
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("Suchergebnis", this.selectedAufgabe); 
		return "aufgabe"; 
	}
	
	/**
	 * Liefert Liste aller Treffer in Projekten
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
	 * Liefert Liste aller Treffer in Aufgaben
	 * @return List<Aufgabe>
	 */
	public List<Aufgabe> getAufgaben() 
	{
		return this.aufgaben;
	}
	
	/**
	 * Setzt Liste von Aufgaben
	 * @param aufgaben
	 */
	public void setAufgaben(List<Aufgabe> aufgaben) 
	{
		this.aufgaben = aufgaben;
	}
	
	/**
	 * Liefert Liste aller Treffer in Kanbans
	 * @return List<Kanban>
	 */
	public List<Kanban> getKanbans() 
	{
		return this.kanbans;
	}
	
	/**
	 * Setzt Liste von Kanbans
	 * @param kanbans
	 */
	public void setKanbans(List<Kanban> kanbans) 
	{
		this.kanbans = kanbans;
	}
	
	/**
	 * Liefert ausgewähltes Kanban-Board
	 * @return Kanban
	 */
	public Kanban getSelectedKanban() 
	{
		return this.selectedKanban;
	}
	
	/**
	 * Setzt Kanban
	 * @param selectedKanban
	 */
	public void setSelectedKanban(Kanban selectedKanban) 
	{
		this.selectedKanban = selectedKanban;
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
	 * Setzt Aufgabe
	 * @param selectedAufgabe
	 */
	public void setSelectedAufgabe(Aufgabe selectedAufgabe) 
	{
		this.selectedAufgabe = selectedAufgabe;
	}
	
	/**
	 * Liefert ausgewähltes Projekt
	 * @return Projekt
	 */
	public Projekt getSelectedProjekt() 
	{
		return this.selectedProjekt;
	}
	
	/**
	 * Setzt Projekt
	 * @param selectedProjekt
	 */
	public void setSelectedProjekt(Projekt selectedProjekt) 
	{
		this.selectedProjekt = selectedProjekt;
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
