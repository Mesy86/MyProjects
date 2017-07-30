package view;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.PieChartModel;

import model.Aufgabe;
import model.Projekt;
import persistenz.Datenbank;

@ManagedBean(name="visualisierungTermineinhaltungView")
@ViewScoped
public class VisualisierungTermineinhaltungView implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instanz-Variablen
	 */
	private PieChartModel pie; 
	private Projekt selectedProject; 
	private List<Aufgabe> aufgaben; 
	private List<Aufgabe> aufgabenRechtzeitig;
	private List<Aufgabe> aufgabenVerspaetet; 
	private List<Aufgabe> aufgabenOffen; 

	private Datenbank db = new Datenbank(); 
	@SuppressWarnings("unused")
	private Connection conn = null; 
	
	@PostConstruct
	public void init()
	{
		//ausgewähltes Projekt aus der Session auslesen
		this.selectedProject = (Projekt) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("Termineinhaltung"); 
		
		//Aufgabenlisten ermitteln
		//DB-Verbindung aufbauen
		if(this.selectedProject != null)
		{
			this.conn = this.db.verbindungHerstellen(); 
			
			//Listen füllen
			this.aufgaben = this.db.ermittleAufgabenProjekt(this.selectedProject);
			this.aufgabenRechtzeitig = this.db.ermittleAufgabenRechzeitig(this.selectedProject); 
			this.aufgabenVerspaetet = this.db.ermittleAufgabenVerspaetet(this.selectedProject); 
			this.aufgabenOffen = this.db.ermittleAufgabenOffen(this.selectedProject); 
			
			
			//DB-Verbindung schließen
			this.db.verbindungSchliessen();
			
			//Diagramm erzeugen
			this.createPieModels();
			
		}
	}
	
	/**
	 * Default-Konstruktor
	 */
	public VisualisierungTermineinhaltungView()
	{
		
	}
	
	private void createPieModels()
	{
		this.createPieModel1(); 
	}
	
	private void createPieModel1()
	{
		this.pie = new PieChartModel(); 
		
		this.pie.set("Rechtzeitig", this.aufgabenRechtzeitig.size());
		this.pie.set("Verspätet", this.aufgabenVerspaetet.size());
		this.pie.set("Offen", this.aufgabenOffen.size());

		this.pie.setLegendPosition("w");
	}
	
	public void itemSelect(ItemSelectEvent event)
	{
		FacesMessage msg; 
		
		//Ermitteln anzahl verspäteter Aufgaben
		int anzahlVerspaetet = this.aufgabenVerspaetet.size(); 
		
		//Ermitteln anzahl pünktlicher Aufgaben
		int anzahlRechtzeitig = this.aufgabenRechtzeitig.size(); 
		
		//Ermitteln anzahl offener Aufgaben
		int anzahlOffen = this.aufgabenOffen.size(); 
		
		switch(event.getItemIndex())
		{
		case(0):
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Rechtzeitig erledigt", "Anzahl: " + anzahlRechtzeitig + " Aufgaben"); 
			break; 
		case(1):
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Zu spät erledigt", "Anzahl: " + anzahlVerspaetet + " Aufgaben"); 
			break; 
		case(2):
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Noch offen", "Anzahl: " + anzahlOffen + " Aufgaben"); 
			break;
		default:
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Kein Element ausgewählt", "Keine Angabe möglich"); 
		}
		
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	/**
	 * Liefert Pie-Diagramm
	 * @return PieChartModel
	 */
	public PieChartModel getPie() 
	{
		return this.pie;
	}
	
	/**
	 * Setzt Pie-Diagramm
	 * @param pie
	 */
	public void setPie(PieChartModel pie) 
	{
		this.pie = pie;
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
	 * Liefert Liste alle Aufgabend des Projekts
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
	 * Liefert alle Aufgaben, die rechtzeitig fertiggestellt wurden
	 * @return List<Aufgabe>
	 */
	public List<Aufgabe> getAufgabenRechtzeitig() 
	{
		return this.aufgabenRechtzeitig;
	}
	
	/**
	 * Setzt Liste von Aufgaben,die rechtzeitig fertiggestellt wurden
	 * @param aufgabenRechtzeitig
	 */
	public void setAufgabenRechtzeitig(List<Aufgabe> aufgabenRechtzeitig) 
	{
		this.aufgabenRechtzeitig = aufgabenRechtzeitig;
	}
	
	/**
	 * Liefert alle Aufgaben des Projekts, die nicht rechtzeitig fertiggestellt wurden
	 * @return List<Aufgabe> 
	 */
	public List<Aufgabe> getAufgabenVerspaetet() 
	{
		return this.aufgabenVerspaetet;
	}
	
	/**
	 * Setzt Liste von Aufgaben, die verspätet fertiggestellt wurden
	 * @param aufgabenVerspaetet
	 */
	public void setAufgabenVerspaetet(List<Aufgabe> aufgabenVerspaetet) 
	{
		this.aufgabenVerspaetet = aufgabenVerspaetet;
	}
	
	/**
	 * Liefert alle Aufgaben des Projekts, die noch nicht fertiggestellt sind
	 * @return List<Aufgabe>
	 */
	public List<Aufgabe> getAufgabenOffen() 
	{
		return this.aufgabenOffen;
	}
	
	/**
	 * Setzt Liste von Aufgaben, die noch nicht fertiggestellt sind
	 * @param aufgabenOffen
	 */
	public void setAufgabenOffen(List<Aufgabe> aufgabenOffen) 
	{
		this.aufgabenOffen = aufgabenOffen;
	}
	

}
