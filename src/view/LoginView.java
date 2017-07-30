package view;

import java.io.Serializable;
import java.sql.Connection;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;

import model.Nutzer;
import persistenz.Datenbank;

@ManagedBean(name="loginView")
@SessionScoped
public class LoginView implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instanzvariablen
	 */
	@NotNull(message="Bitte Benutzername eintragen")
	private String benutzername; 
	
	@NotNull(message="Bitte Passwort eintragen")
	private String passwort; 
	
	private boolean loggedIn; 
	
	private Datenbank db = new Datenbank(); 
	Connection conn = null; 
	
	/**
	 * Default-Konstruktor
	 */
	public LoginView()
	{
	}
	
	/**
	 * Methode wird von JSF aufgerufen
	 * prüft die eingegebenen Userdaten mit der DB ab 
	 * Wenn Daten korrekt, dann einloggen
	 * Fehlerfall: Fehlermeldung und erneute Eingabe 
	 * @return String
	 */
	public String login()
	{
		Nutzer nutzer; 
		String ausgabe = ""; 
		
		//Eingegebene Daten prüfen (mit Datenbank abgleichen)
		//Nutzer entsprechende Benutzername aus DB holen
		this.conn = this.db.verbindungHerstellen(); 
		
		nutzer = this.db.findeUserMitBenutzername(benutzername); 
		
		if(nutzer != null)
		{
			//Passwörter vergleichen
			if(!nutzer.getPasswort().equals(this.passwort))
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Logindaten sind nicht korrekt", "Bitte erneut eingeben");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			else
			{
				
				//Systemzeit in Datenbank schreiben
				if(this.db.aktualisiereLoginZeit(nutzer))
				{
					this.loggedIn = true; 
					ausgabe = "login"; 
				}
				else
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login nicht möglich", "Systemzeit konnte nicht ermittelt werden");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Logindaten sind nicht korrekt"));
			
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Daten nicht korrekt", "Bitte erneut eingeben");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			
		}
		
		this.db.verbindungSchliessen();
		
		return ausgabe; 
	}
	
	/**
	 * Methode wird von JSF aufgerufen
	 * Liefert Benutzername
	 * @return String
	 */
	public String getBenutzername() 
	{
		return this.benutzername;
	}
	
	/**
	 * Methode wird von JSF aufgerufen
	 * Setzt Benutzername
	 * @param benutzername
	 */
	public void setBenutzername(String benutzername) 
	{
		this.benutzername = benutzername;
	}
	
	/**
	 * Methode wird von JSF aufgerufen
	 * Liefert Passwort
	 * @return String
	 */
	public String getPasswort() 
	{
		return this.passwort;
	}
	
	/**
	 * Methode wird von JSF aufgerufen
	 * Setzt Passwort
	 * @param passwort
	 */
	public void setPasswort(String passwort) 
	{
		this.passwort = passwort;
	}
	
	/**
	 * Liefert Auskunft, ob Nutzer eingeloggt ist oder noch nicht
	 * @return boolean
	 */
	public boolean isLoggedIn() 
	{
		return this.loggedIn;
	}
	
	/**
	 * Setzt, ob der Nutzer eingeloggt ist oder nicht
	 * @param loggedIn
	 */
	public void setLoggedIn(boolean loggedIn) 
	{
		this.loggedIn = loggedIn;
	}
	
	
	

}
