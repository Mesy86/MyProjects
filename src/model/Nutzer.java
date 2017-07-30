package model;

public class Nutzer 
{
	/**
	 * Instanz-Variablen
	 */
	private int id; 
	private String vorname; 
	private String nachname; 
	private String benutzername; 
	private String mail; 
	private String passwort; 
	private String loginZeit; 
	
	/**
	 * Default-Konstruktor
	 */
	public Nutzer()
	{
		
	}
	
	/**
	 * überladener Konstruktor mit ID
	 * @param int
	 */
	public Nutzer(int p_id)
	{
		this.id = p_id; 
	}
	
	/**
	 * überladener Konstruktor mit allen Pflichtangaben 
	 * @param vorname
	 * @param nachname
	 * @param benutzername
	 * @param passwort
	 */
	public Nutzer(int id, String vorname, String nachname, String benutzername, String passwort)
	{
		this.id = id; 
		this.vorname = vorname; 
		this.nachname = nachname; 
		this.benutzername = benutzername; 
		this.passwort = passwort; 
	}
	
	/**
	 * überladener Konstruktor mit Benutzername
	 * @param benutzername
	 */
	public Nutzer(String benutzername) 
	{
		this.benutzername = benutzername; 
	}

	/**
	 * Liefert Id
	 * @return int
	 */
	public int getId() 
	{
		return this.id;
	}
	
	/**
	 * Liefert Vorname
	 * @return String
	 */
	public String getVorname() 
	{
		return this.vorname;
	}
	
	/**
	 * Setzt Vorname
	 * @param vorname
	 */
	public void setVorname(String vorname) 
	{
		this.vorname = vorname;
	}
	
	/**
	 * Liefert Nachname
	 * @return String
	 */
	public String getNachname() 
	{
		return this.nachname;
	}
	
	/**
	 * Setzt Nachname
	 * @param nachname
	 */
	public void setNachname(String nachname) 
	{
		this.nachname = nachname;
	}
	
	/**
	 * Liefert Benutzername
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
	
	/**
	 * Liefert Mailadresse
	 * @return String
	 */
	public String getMail() 
	{
		return this.mail;
	}
	
	/**
	 * Setzt Mailadresse
	 * @param mail
	 */
	public void setMail(String mail) 
	{
		this.mail = mail;
	}
	
	/**
	 * Liefert Passwort
	 * @return String
	 */
	public String getPasswort() 
	{
		return this.passwort;
	}
	
	/**
	 * Setzt Passwort
	 * @param passwort
	 */
	public void setPasswort(String passwort) 
	{
		this.passwort = passwort;
	}
	
	/**
	 * Liefert die Zeit des Einloggens als String
	 * @return String
	 */
	public String getLoginZeit() 
	{
		return this.loginZeit;
	}
	
	/**
	 * Setzt Login-Zeit
	 * @param loginZeit
	 */
	public void setLoginZeit(String loginZeit) 
	{
		this.loginZeit = loginZeit;
	}
	
	
	
}
