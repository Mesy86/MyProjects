package view;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import model.Aufgabe;
import model.Projekt;
import persistenz.Datenbank;

@ManagedBean(name="aufgabeSucheView")
@ViewScoped
public class AufgabeSucheView implements Serializable 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instanz-Variablen
	 */
	private String aufgabenname; 
	private String projektname; 
	
	private Aufgabe selectedAufgabe = null; 
	
	private List<Aufgabe> aufgaben; 
	private List<Projekt> projekte; 
	
	private Datenbank db = new Datenbank(); 
	@SuppressWarnings("unused")
	private Connection conn; 
	
	/**
	 * Methode füllt die Listen aus der Datenbank, bevor die Oberfläche aufgebaut wird
	 */
	@PostConstruct
	public void init()
	{
		//DB-Verbindung herstellen
		this.conn = this.db.verbindungHerstellen(); 
		
		//Listen aus der Datenbank füllen
		this.aufgaben = this.db.ermittleAufgaben(); 
		this.projekte = this.db.ermittleProjekte(); 
		
		//DB-Verbindung schließen
		this.db.verbindungSchliessen();
	}
	
	/**
	 * Default-Konstruktor
	 */
	public AufgabeSucheView()
	{
	}
	
	/**
	 * Methode sucht Aufgabe anhand des Aufgabennamens und des Projektnamens
	 * leitet weiter zur Detailansicht der Aufgabe
	 * @return String 
	 */
	public String sucheAufgabe()
	{
		String ausgabe = ""; 
		
		//Aufgabe ermitteln
		if(!this.aufgabenname.equals("Bitte auswählen") && !this.projektname.equals("Bitte auswählen"))
		{
			//Aufgabe anhand gewähltem Namen ermitteln
			for(int i = 0; i < this.aufgaben.size(); i++)
			{
				if(this.aufgaben.get(i).getName().equals(this.aufgabenname))
				{
					//prüfen, ob angegebenes Projekt auch passt
					if(this.aufgaben.get(i).getProjekt().getName().equals(this.projektname))
					{
						this.selectedAufgabe = this.aufgaben.get(i); 
						
						//Suchergebnis in SessionMap schreiben
						FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("Suchergebnis", this.selectedAufgabe); 
						
						ausgabe = "bearbeiten"; 
						break; 
					}
				}
			}
			
			if(this.selectedAufgabe == null)
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "passende Begriffe eingeben", "Aufgabe und Projekt passen nicht zusammen"); 
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		}
		else
		{
			
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Suchbegriff(e) eingeben", "Bitte wählen Sie Aufgabe und Projekt"); 
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		return ausgabe; 
	}
	
	/**
	 * Liefert Namen der Aufgabe
	 * @return String
	 */
	public String getAufgabenname() 
	{
		return this.aufgabenname;
	}
	
	/**
	 * Setzt Namen der Aufgabe
	 * @param aufgabenname
	 */
	public void setAufgabenname(String aufgabenname) 
	{
		this.aufgabenname = aufgabenname;
	}
	
	/**
	 * Liefert Namen des übergeordneten Projekts
	 * @return String
	 */
	public String getProjektname() 
	{
		return this.projektname;
	}
	
	/**
	 * Setzt Namen des Projekts
	 * @param projektname
	 */
	public void setProjektname(String projektname) 
	{
		this.projektname = projektname;
	}
	
	/**
	 * Liefert gewählte Aufgabe
	 * @return Aufgabe
	 */
	public Aufgabe getSelectedAufgabe() 
	{
		return this.selectedAufgabe;
	}
	
	/**
	 * Setzt gewählte Aufgabe
	 * @param selectedAufgabe
	 */
	public void setSelectedAufgabe(Aufgabe selectedAufgabe) 
	{
		this.selectedAufgabe = selectedAufgabe;
	}
	
	/**
	 * Liefert alle Aufgaben aus der Datenbank
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
	 * Liefert alle Projekte aus der Datenbank
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
	
	
	

}
