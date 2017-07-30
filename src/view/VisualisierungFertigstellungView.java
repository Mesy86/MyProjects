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

import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.PieChartModel;

import model.Nutzer;
import model.Projekt;
import persistenz.Datenbank;

@ManagedBean(name="visualisierungFertigstellungView")
@ViewScoped
public class VisualisierungFertigstellungView implements Serializable
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
	
	private PieChartModel pie; 
	private List<Projekt> projekte; 
	private List<Projekt> projekteRechtzeitig; 
	private List<Projekt> projekteVerspaetet; 
	private List<Projekt> projekteOffen; 

	private Datenbank db = new Datenbank(); 
	@SuppressWarnings("unused")
	private Connection conn = null; 
	
	@PostConstruct
	public void init()
	{
		this.conn = this.db.verbindungHerstellen(); 
		
		//Ermittle  Nutzer
		Nutzer nutzer = this.db.ermittleNutzerNachBenutzername(this.benutzername); 
		
		//Listen füllen
		this.projekteOffen = this.db.ermittleOffeneProjekteNachUser(nutzer);
		this.projekteRechtzeitig = this.db.ermittleRechtzeitigeProjekteNachUser(nutzer); 
		this.projekteVerspaetet = this.db.ermittleVerspaeteteProjekteNachUser(nutzer); 
		
		//DB-Verbindung schließen
		this.db.verbindungSchliessen();
		
		//Diagramm erzeugen
		this.createPieModels();
	}
	
	/**
	 * Default-Konstruktor
	 */
	public VisualisierungFertigstellungView()
	{
		
	}
	
	/**
	 * Methode ruft einzelne Diagramm-Methoden auf
	 */
	private void createPieModels()
	{
		this.createPieModel1(); 
	}
	
	/**
	 * Methode erstellt Kuchendiagramm
	 */
	private void createPieModel1()
	{
		this.pie = new PieChartModel(); 
		
		this.pie.set("Rechtzeitig", this.projekteRechtzeitig.size());
		this.pie.set("Verspätet", this.projekteVerspaetet.size());
		this.pie.set("Offen", this.projekteOffen.size());
		
		//this.pie.setTitle("Fertigstellung der Projekte");
		this.pie.setLegendPosition("w");
	}
	
	/**
	 * Methode liefert die interaktive Message
	 * @param event
	 */
	public void itemSelect(ItemSelectEvent event)
	{
		FacesMessage msg; 
		
		//Ermitteln anzahl verspäteter Aufgaben
		int anzahlVerspaetet = this.projekteVerspaetet.size(); 
		
		//Ermitteln anzahl pünktlicher Aufgaben
		int anzahlRechtzeitig = this.projekteRechtzeitig.size(); 
		
		//Ermitteln anzahl offener Aufgaben
		int anzahlOffen = this.projekteOffen.size(); 
		
		switch(event.getItemIndex())
		{
		case(0):
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Rechtzeitig erledigt", "Anzahl: " + anzahlRechtzeitig + " Projekt(e)"); 
			break; 
		case(1):
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Zu spät erledigt", "Anzahl: " + anzahlVerspaetet + " Projekt(e)"); 
			break; 
		case(2):
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Noch offen", "Anzahl: " + anzahlOffen + " Projekt(e)"); 
			break;
		default:
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Kein Element ausgewählt", "Keine Angabe möglich"); 
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
	 * Liefert alle Projekte, die rechtzeitig fertiggestellt wurden
	 * @return List<Projekt>
	 */
	public List<Projekt> getProjekteRechtzeitig() 
	{
		return this.projekteRechtzeitig;
	}
	
	/**
	 * Setzt Liste von Projekten, die rechtzeitig fertiggestellt wurden
	 * @param projekteRechtzeitig
	 */
	public void setProjekteRechtzeitig(List<Projekt> projekteRechtzeitig) 
	{
		this.projekteRechtzeitig = projekteRechtzeitig;
	}
	
	/**
	 * Liefert alle Projekte, die verspätet fertiggestellt wurden
	 * @return List<Projekt>
	 */
	public List<Projekt> getProjekteVerspaetet() 
	{
		return this.projekteVerspaetet;
	}
	
	/**
	 * Setzt Liste von Projekten, die verspätet fertiggestellt wurden
	 * @param projekteVerspaetet
	 */
	public void setProjekteVerspaetet(List<Projekt> projekteVerspaetet) 
	{
		this.projekteVerspaetet = projekteVerspaetet;
	}
	
	/**
	 * Liefert alle Projekte, die noch offen sind
	 * @return List<Projekt> 
	 */
	public List<Projekt> getProjekteOffen() 
	{
		return this.projekteOffen;
	}
	
	/**
	 * Setzt Liste von Projekten, die noch offen sind
	 * @param projekteOffen
	 */
	public void setProjekteOffen(List<Projekt> projekteOffen) 
	{
		this.projekteOffen = projekteOffen;
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
	 * Liefert Benutzernamen des aktuellen Users
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
