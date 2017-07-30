package view;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import model.Nutzer;
import persistenz.Datenbank;

@ManagedBean(name="userAnsichtView")
@ViewScoped
public class UserAnsichtView implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instanz-Variablen
	 */
	private List<Nutzer> nutzerListe; 
	
	private Nutzer selectedNutzer;  
	
	private Datenbank db = new Datenbank(); 
	@SuppressWarnings("unused")
	private Connection conn; 
	
	/**
	 * Methode füllt die Listen, bevor die Oberfläche erstellt wird
	 */
	@PostConstruct
	public void init()
	{
		this.conn = this.db.verbindungHerstellen(); 
		
		//Alle Nutzer aus der Datenbank auslesen
		this.nutzerListe = this.db.ermittleNutzer(); 
		
		this.db.verbindungSchliessen();
		
	}
	
	/**
	 * Default-Konstruktor
	 */
	public UserAnsichtView()
	{
	}
	
	public String einladen()
	{
		
		//Nutzer in Session schreiben
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("Nutzer", this.selectedNutzer); 
		
		return "einladung"; 
	}
	
	/**
	 * Methode liefert Nutzerliste
	 * @return List<Nutzer>
	 */
	public List<Nutzer> getNutzerListe() 
	{
		return this.nutzerListe;
	}
	
	/**
	 * Methode setzt eine Liste von Nutzern
	 * @param nutzer
	 */
	public void setNutzerListe(List<Nutzer> nutzerListe) 
	{
		this.nutzerListe = nutzerListe;
	}

	
	/**
	 * Methode liefert den ausgewählten Nutzer
	 * @return Nutzer
	 */
	public Nutzer getSelectedNutzer() 
	{
		return this.selectedNutzer;
	}
	
	/**
	 * Methode setzt einen Nutzer als ausgewählten Nutzer
	 * @param selectedNutzer
	 */
	public void setSelectedNutzer(Nutzer selectedNutzer) 
	{
		this.selectedNutzer = selectedNutzer;
	}



}
