package view;

import java.io.Serializable;
import java.sql.Connection;
import java.text.DecimalFormat;
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


@ManagedBean(name="visualisierungFortschrittView")
@ViewScoped
public class VisualisierungFortschrittView implements Serializable
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
	private List<Aufgabe> backlog; 
	private List<Aufgabe> todo; 
	private List<Aufgabe> inprogress; 
	private List<Aufgabe> testing; 
	private List<Aufgabe> done; 
	private List<Aufgabe> burn; 
	
	private Datenbank db = new Datenbank(); 
	@SuppressWarnings("unused")
	private Connection conn = null; 
	
	@PostConstruct
	public void init()
	{
		//ausgewähltes Projekt aus der Session auslesen
		this.selectedProject = (Projekt) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("Projektfortschritt"); 
		
		//Aufgabenlisten ermitteln
		//DB-Verbindung aufbauen
		if(this.selectedProject != null)
		{
			this.conn = this.db.verbindungHerstellen(); 
			
			//Listen füllen
			this.aufgaben = this.db.ermittleAufgabenProjekt(this.selectedProject); 
			this.backlog = this.db.ermittleBacklog(selectedProject);
			this.todo = this.db.ermittleToDo(this.selectedProject); 
			this.inprogress = this.db.ermittleInProgress(this.selectedProject); 
			this.testing = this.db.ermittleTesting(this.selectedProject); 
			this.done = this.db.ermittleDone(this.selectedProject); 
			this.burn = this.db.ermittleBurn(this.selectedProject); 
			
			//DB-Verbindung schließen
			this.db.verbindungSchliessen();
			
			//Diagramm erzeugen
			this.createPieModels();
		}
	}
	
	/**
	 * Default-Konstruktor
	 */
	public VisualisierungFortschrittView()
	{
		
	}
	
	private void createPieModels()
	{
		this.createPieModel1(); 
	}
	
	private void createPieModel1()
	{
		this.pie = new PieChartModel(); 
		
		//prozentuale Angaben berechnen
		
		//Anzahl Backlog
		int anzahlBacklog = this.backlog.size(); 
		
		//Anzahl To Do
		int anzahlTodo = this.todo.size();
		
		//Anzahl In Progress
		int anzahlProgress = this.inprogress.size(); 
		
		//Anzahl Testing
		int anzahlTesting = this.testing.size(); 
		
		//Anzahl Done 
		int anzahlDone = this.done.size(); 
		
		//Anzahl Burn
		int anzahlBurn = this.burn.size(); 
		
		//Diagramm aufbauen
		this.pie.set("Backlog", anzahlBacklog);
		this.pie.set("To Do", anzahlTodo);
		this.pie.set("In Progress", anzahlProgress);
		this.pie.set("Testing", anzahlTesting);
		this.pie.set("Done", anzahlDone);
		this.pie.set("Burn", anzahlBurn);
		
		this.pie.setLegendPosition("w");
	}
	
	public void itemSelect(ItemSelectEvent event)
	{
		//Prozente berechnen
				
		double prozentBacklog = (this.backlog.size() * 1.0/this.aufgaben.size()) * 100.0; 
		double prozentTodo = (this.todo.size()* 1.0/this.aufgaben.size()) * 100; 
		double prozentProgress = (this.inprogress.size() * 1.0/this.aufgaben.size()) * 100.0; 
		double prozentTesting = (this.testing.size() * 1.0/this.aufgaben.size()) * 100.0; 
		double prozentDone = (this.done.size() * 1.0/this.aufgaben.size()) * 100.0; 
		double prozentBurn = (this.burn.size() * 1.0/this.aufgaben.size()) * 100.0; 
		
		DecimalFormat f = new DecimalFormat("#0.00"); 
		FacesMessage msg; 
		
		switch(event.getItemIndex())
		{
		case(0):
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Backlog", "Aktueller Stand: " + f.format(prozentBacklog) + "%");
			break;
		case(1):
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "To Do", "Aktueller Stand: " + f.format(prozentTodo) + "%");
			break; 
		case(2):
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "In Progress", "Aktueller Stand: " + f.format(prozentProgress) + "%");
			break; 
		case(3):
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Testing", "Aktueller Stand: " + f.format(prozentTesting) + "%");
			break; 
		case(4):
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Done", "Aktueller Stand: " + f.format(prozentDone) + "%");
			break; 
		case(5):
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Burn", "Aktueller Stand: " + f.format(prozentBurn) + "%");
		break;
		default:
			msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Kein Element ausgewählt", "Keine Angabe möglich"); 
		}
		
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	/**
	 * Liefert Diagramm
	 * @return PieChartModel
	 */
	public PieChartModel getPie() 
	{
		return this.pie;
	}
	
	/**
	 * Setzt Diagramm
	 * @param pie
	 */
	public void setPie(PieChartModel pie) 
	{
		this.pie = pie;
	}
	
	/**
	 * Liefert das asugewählte Projekt
	 * @return Projekt
	 */
	public Projekt getSelectedProject() 
	{
		return this.selectedProject;
	}
	
	/**
	 * Setzt das ausgewählte Projekt
	 * @param selectedProject
	 */
	public void setSelectedProject(Projekt selectedProject) 
	{
		this.selectedProject = selectedProject;
	}
	
	/**
	 * Liefert alle Aufgaben des ausgewählten Projekts
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
	 * Liefert alle Aufgaben des Projekts aus dem Backlog
	 * @return List<Aufgabe>
	 */
	public List<Aufgabe> getBacklog() 
	{
		return this.backlog;
	}
	
	/**
	 * Setzt Liste von Aufgaben ins Backlog
	 * @param backlog
	 */
	public void setBacklog(List<Aufgabe> backlog) 
	{
		this.backlog = backlog;
	}
	
	/**
	 * Liefert alle Aufgaben des Projekts aus To Do 
	 * @return List<Aufgabe>
	 */
	public List<Aufgabe> getTodo() 
	{
		return this.todo;
	}
	
	/**
	 * Setzt Liste von Aufgaben in To Do
	 * @param todo
	 */
	public void setTodo(List<Aufgabe> todo) 
	{
		this.todo = todo;
	}
	
	/**
	 * Liefert alle Aufgaben des Projekts aus In Progress
	 * @return List<Aufgabe> 
	 */
	public List<Aufgabe> getInprogress() 
	{
		return this.inprogress;
	}
	
	/**
	 * Setzt Liste von Aufgaben in In Progress
	 * @param inprogress
	 */
	public void setInprogress(List<Aufgabe> inprogress) 
	{
		this.inprogress = inprogress;
	}
	
	/**
	 * Liefert alle Aufgaben des Projekts aus Testing
	 * @return List<Aufgabe> 
	 */
	public List<Aufgabe> getTesting() 
	{
		return this.testing;
	}
	
	/**
	 * Setzt Liste von Aufgaben in Testing
	 * @param testing
	 */
	public void setTesting(List<Aufgabe> testing) 
	{
		this.testing = testing;
	}
	
	/**
	 * Liefert alle Aufgaben des Projekts aus Done
	 * @return List<Aufgabe>
	 */
	public List<Aufgabe> getDone() 
	{
		return this.done;
	}
	
	/**
	 * Setzt Liste von Aufgaben in Done
	 * @param done
	 */
	public void setDone(List<Aufgabe> done) 
	{
		this.done = done;
	}
	
	/**
	 * Liefert alle Aufgabend des Projekts, die burned sind
	 * @return List<Aufgabe>
	 */
	public List<Aufgabe> getBurn() 
	{
		return this.burn;
	}
	
	/**
	 * Setzt Liste von Aufgabe in Burn
	 * @param burn
	 */
	public void setBurn(List<Aufgabe> burn) 
	{
		this.burn = burn;
	}

}
