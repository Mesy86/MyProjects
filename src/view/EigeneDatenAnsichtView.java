package view;

import java.io.Serializable;
import java.sql.Connection;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import model.Nutzer;
import persistenz.Datenbank;

@ManagedBean(name="eigeneDatenAnsichtView")
@ViewScoped
public class EigeneDatenAnsichtView implements Serializable
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
	
	@NotNull(message="Bitte einen Nachnamen eingeben")
	@Pattern(regexp="[a-zA-Z-äöüÄÖÜ]+", message="ungültiger Nachname")
	private String nachname; 
	
	private String vorname; 
	
	@Pattern(regexp="[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}", message="ungültige Emailadresse")
	private String email; 
	
	private String passwortAlt; 
	
	@Pattern(regexp="((?=.*\\d)(?=.*[a-z])(?=.*[@#$%]).{6,})", message="Neues Passwort besteht aus mind. einer Zahl, einem Kleinbuchtstaben sowie einem Sonderzeichen und hat mindestens sechs Zeichen")
	private String passwortNeu; 
	private String passwortNeuWiederholung; 
	
	private Datenbank db = new Datenbank(); 
	@SuppressWarnings("unused")
	private Connection conn = null; 
	
	/**
	 * Methode füllt die Variablen bevor die Oberfläche erstellt wird
	 */
	@PostConstruct
	public void init()
	{
		if(this.benutzername != null)
		{
			this.conn = this.db.verbindungHerstellen(); 
			this.nutzer = this.db.ermittleNutzerNachBenutzername(this.benutzername);
			
			this.db.verbindungSchliessen();
			
			//Variablen füllen
			this.nachname = nutzer.getNachname(); 
			this.vorname = nutzer.getVorname(); 
			this.email = nutzer.getMail(); 
		}
	}
	
	/**
	 * Default-Konstruktor
	 */
	public EigeneDatenAnsichtView()
	{
	}
	
	/**
	 * Methode löscht den Account und beendet die Session
	 * @return String
	 */
	public String accountLoeschen()
	{
		String ausgabe = ""; 
		
		this.conn = this.db.verbindungHerstellen(); 
		
		
		if(this.db.loescheNutzer(this.nutzer))
		{
			//Session beenden
			FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
			
			ausgabe = "registration"; 
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Account konnte nicht gelöscht werden", "Fehler");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		this.db.verbindungSchliessen();

		return ausgabe; 
	}
	
	/**
	 * Methode speichert die geänderten Daten eines Users in der Datenbank
	 * @return String
	 */
	public String datenSpeichern()
	{
		String ausgabe = ""; 

		//Änderung ist nur möglich, wenn bisheriges Passwort eingegeben wurde
		if(this.passwortAlt != null && this.passwortAlt.equals(this.nutzer.getPasswort()))
		{
			this.conn = this.db.verbindungHerstellen(); 
			
			//prüfen, ob neues Passwort angegeben wurde. Wenn ja, dann mit Wiederholung vergleichen
			if(this.passwortNeu!=null)
			{
				if(this.passwortNeu.equals(this.passwortNeuWiederholung))
				{
					//Nutzer ändern, neues Passwort
					this.nutzer.setPasswort(this.passwortNeu);
					this.nutzer.setNachname(this.nachname);
					this.nutzer.setMail(this.email);
					
					//Änderung in Datenbank
					if(this.db.aktualisiereNutzer(this.nutzer))
					{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Änderung erfolgreich", "Daten wurden geändert"); 
						FacesContext.getCurrentInstance().addMessage(null, msg);
						
						ausgabe = "ansicht"; 
					}
					else
					{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Änderung konnte nicht gespeichert werden", "Fehler bei DB-Zugriff");
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				}
				else
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "neue Passwörter stimmen nicht überein", "Passwörter neu eingeben"); 
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}
			else
			{
				if(this.passwortNeuWiederholung==null)
				{
					//Nutzer ändern, altes Passwort 
					this.nutzer.setPasswort(this.passwortAlt);
					this.nutzer.setNachname(this.nachname);
					this.nutzer.setMail(this.email);
					
					//Änderung in Datenbank
					if(this.db.aktualisiereNutzer(this.nutzer))
					{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Änderung erfolgreich", "Daten wurden geändert"); 
						FacesContext.getCurrentInstance().addMessage(null, msg);
						
						ausgabe = "ansicht"; 
					}
					else
					{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Änderung konnte nicht gespeichert werden", "Fehler bei DB-Zugriff");
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				}
				else
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bei Passwortänderung bitte neues Passwort zweimal angeben, ansonsten beide Felder leer lassen", "Keine Änderung"); 
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}
			
			this.db.verbindungSchliessen();
			
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Änderung nur mit richtigem Passwort zulässig", "Bitte geben Sie Ihr bisheriges Passwort ein"); 
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		return ausgabe; 
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
	 * Setzt den Benutzername der aktuellen Session
	 * @param benutzername
	 */
	public void setBenutzername(String benutzername) 
	{
		this.benutzername = benutzername;
	}
	
	/**
	 * Liefert Nachname des aktuellen Nutzers
	 * @return String
	 */
	public String getNachname() 
	{
		return nachname;
	}
	
	/**
	 * Setzt den Nachnamen des aktuellen Nutzers
	 * @param nachname
	 */
	public void setNachname(String nachname) 
	{
		this.nachname = nachname;
	}
	
	/**
	 * Liefert den Vornamen des aktuellen Nutzers
	 * @return String
	 */
	public String getVorname() 
	{
		return this.vorname;
	}
	
	/**
	 * Setzt den Vornamen des aktuellen Nutzers
	 * @param vorname
	 */
	public void setVorname(String vorname) 
	{
		this.vorname = vorname;
	}
	
	/**
	 * Liefert Emailadresse des aktuellen Nutzers
	 * @return String
	 */
	public String getEmail() 
	{
		return this.email;
	}
	
	/**
	 * Setzt Emailadresse des aktuellen Nutzers
	 * @param email
	 */
	public void setEmail(String email) 
	{
		this.email = email;
	}
	
	/**
	 * Liefert aktuellen Nutzer
	 * @return Nutzer
	 */
	public Nutzer getNutzer() 
	{
		return this.nutzer;
	}
	
	/**
	 * Setzt aktuellen Nutzer
	 * @param nutzer
	 */
	public void setNutzer(Nutzer nutzer) 
	{
		this.nutzer = nutzer;
	}
	
	/**
	 * Liefert bisheriges Passwort
	 * @return String
	 */
	public String getPasswortAlt() 
	{
		return this.passwortAlt;
	}
	
	/**
	 * Setzt bisheriges Passwort
	 * @param passwortAlt
	 */
	public void setPasswortAlt(String passwortAlt) 
	{
		this.passwortAlt = passwortAlt;
	}
	
	/**
	 * Liefert neues Passwort
	 * @return String
	 */
	public String getPasswortNeu() 
	{
		return this.passwortNeu;
	}
	
	/**
	 * Setzt neues Passwort
	 * @param passwortNeu
	 */
	public void setPasswortNeu(String passwortNeu) 
	{
		this.passwortNeu = passwortNeu;
	}
	
	/**
	 * Liefert Wiederholung des neuen Passworts
	 * @return String
	 */
	public String getPasswortNeuWiederholung() 
	{
		return this.passwortNeuWiederholung;
	}
	
	/**
	 * Setzt Wiederholung des neuen Passworts
	 * @param passwortNeuWiederholung
	 */
	public void setPasswortNeuWiederholung(String passwortNeuWiederholung) 
	{
		this.passwortNeuWiederholung = passwortNeuWiederholung;
	}
	

}
