package view;

import java.io.Serializable;
import java.sql.Connection;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import model.Nutzer;
import persistenz.Datenbank;

@ManagedBean(name="registrationView")
@ViewScoped
public class RegistrationView implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Instanzvariablen
	 */
	@NotNull(message="Bitte einen Vorname eingeben")
	@Pattern(regexp="[a-zA-Z-äöüÄÖÜ]+", message="ungültiger Vorname")
	private String vorname; 
	
	@NotNull(message="Bitte einen Nachnamen eingeben")
	@Pattern(regexp="[a-zA-Z-äöüÄÖÜ]+", message="ungültiger Nachname")
	private String nachname; 
	
	@NotNull(message="Bitte einen Benutzernamen eingeben")
	@Pattern(regexp="[a-zA-Z1-9]+", message="ungültiger Benutzername")
	private String benutzername; 
	
	@Pattern(regexp="[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}", message="ungültige Emailadresse")
	private String mail; 
	
	@NotNull(message="Bitte ein Passwort eingeben")
	@Pattern(regexp="((?=.*\\d)(?=.*[a-z])(?=.*[@#$%]).{6,})", message="Passwort besteht aus mind. einer Zahl, einem Kleinbuchtstaben sowie einem Sonderzeichen und hat mindestens sechs Zeichen")
	private String passwort; 
	
	@NotNull(message="Bitte Passwort wiederholen")
	private String passwortWdh; 
	
	private Datenbank db = new Datenbank(); 
	Connection conn = null; 
	
	/**
	 * Default-Konstruktor
	 */
	public RegistrationView()
	{
	}
	
	/**
	 * Methode wird von JSF aufgerufen
	 * speichert neuen User in der Datenbank
	 * leitet User zur Login.xhtml weiter
	 * @return String
	 */
	public String registrieren()
	{
		
		String ausgabe = ""; 
		
		//Datenbankverbindung herstellen
		this.conn = db.verbindungHerstellen(); 
		
		//Nutzer-Objekt erstellen
		Nutzer nutzer = new Nutzer(this.benutzername); 
		nutzer.setVorname(this.vorname);
		nutzer.setNachname(this.nachname);
		nutzer.setMail(this.mail);
		nutzer.setPasswort(this.passwort);
		
		//Nutzer eintragen
		if(db.nutzerAnlegen(nutzer))
		{	
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Neuer Nutzer wurde angelegt", "Login möglich"); 
			FacesContext.getCurrentInstance().addMessage(null, msg);
			
			ausgabe = "einloggen"; 
		}
		else
		{	
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nutzer konnte nicht angelegt werden", "Bitte erneut versuchen"); 
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		this.db.verbindungSchliessen();
		
		return ausgabe; 
	}
	
	/**
	 * liefert den Vorname des Users
	 * @return String
	 */
	public String getVorname() 
	{
		return this.vorname;
	}
	
	/**
	 * setzt den Vornamen 
	 * @param vorname
	 */
	public void setVorname(String vorname) 
	{
		this.vorname = vorname;
	}
	
	/**
	 * Liefert den Nachnamen 
	 * @return String
	 */
	public String getNachname() 
	{
		return this.nachname;
	}
	
	/**
	 * Setzt den Nachnamen
	 * @param nachname
	 */
	public void setNachname(String nachname) 
	{
		this.nachname = nachname;
	}
	
	/**
	 * Liefert den Benutzername
	 * @return String
	 */
	public String getBenutzername() 
	{
		return this.benutzername;
	}
	
	/**
	 * Setzt den Benutzernamen
	 * @param benutzername
	 */
	public void setBenutzername(String benutzername) 
	{
		this.benutzername = benutzername;
	}
	
	/**
	 * Liefert die Mailadresse
	 * @return String
	 */
	public String getMail() 
	{
		return this.mail;
	}
	
	/**
	 * Setzt die Mailadresse
	 * @param mail
	 */
	public void setMail(String mail) 
	{
		this.mail = mail;
	}
	
	/**
	 * Liefert das Passwort
	 * @return String
	 */
	public String getPasswort() 
	{
		return this.passwort;
	}
	
	/**
	 * Setzt das Passwort
	 * @param passwort
	 */
	public void setPasswort(String passwort) 
	{
		this.passwort = passwort;
	}
	
	/**
	 * Liefert die Wiederholung des Passworts
	 * @return String
	 */
	public String getPasswortWdh() 
	{
		return this.passwortWdh;
	}
	
	/**
	 * Setzt die Wiederholung des Passworts
	 * @param passwortWdh
	 */
	public void setPasswortWdh(String passwortWdh) 
	{
		this.passwortWdh = passwortWdh;
	}
	
	
}
