package persistenz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Aufgabe;
import model.Kanban;
import model.Nutzer;
import model.Projekt;

public class Datenbank 
{
	/**
	 * Instanzvariablen
	 */
	Connection conn; 
	
	/**
	 * Default-Konstruktor
	 */
	public Datenbank()
	{
		
	}
	
	/**
	 * Methode stellt Verbindung zur Datenbank her und liefert die Verbindung zurück
	 * @return Connection
	 */
	public Connection verbindungHerstellen()
	{
		try 
		{
			try 
			{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
			} 
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			}
		} 
		catch (InstantiationException e) 
		{
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		} 
		
		System.out.println("Connecting to database");
		
		this.conn = null; 
		
		try 
		{
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/myprojects?user=root&password=root");
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		} 
		
		return conn; 
	}
	
	/**
	 * Methode schließt die Verbindung zur Datenbank 
	 */
	public void verbindungSchliessen()
	{
		try
		{
			conn.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace(); 
		}
	}
	
	/**
	 * Methode legt einen neuen User an bzw. wirft einen Fehler, wenn dies nicht möglich ist 
	 * @param Nutzer
	 * @return boolean
	 */
	public boolean nutzerAnlegen(Nutzer p_nutzer)
	{
		boolean erfolgreich = false; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "INSERT INTO tbluser(user_nachname, user_vorname, user_benutzername, user_mail, user_passwort) "
					+ "VALUES ('" + p_nutzer.getNachname() + "','" + p_nutzer.getVorname() + "','" + p_nutzer.getBenutzername() + "','" + p_nutzer.getMail() + "','" + p_nutzer.getPasswort() + "');";
			stmt.execute(sql); 
			erfolgreich = true; 
		}
		catch(SQLException e)
		{
			e.printStackTrace(); 
		}
		
		
		return erfolgreich; 
	}
	
	/**
	 * Methode liefert - wenn in DB vorhanden - einen Nutzer mit Benutzername p_benutzername
	 * gibt es keinen Treffer in der DB liefert die Methode NULL
	 * @param p_benutzername
	 * @return Nutzer
	 */
	public Nutzer findeUserMitBenutzername(String p_benutzername)
	{
		Nutzer nutzer = null; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT * FROM tbluser WHERE user_benutzername='" + p_benutzername + "';"; 
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				nutzer = new Nutzer(rs.getInt("user_id"), rs.getString("user_vorname"), rs.getString("user_nachname"), rs.getString("user_benutzername"), rs.getString("user_passwort"));
				nutzer.setMail(rs.getString("user_mail"));
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		
		
		return nutzer; 
	}
	
	/**
	 * Methode liefert - wenn in DB vorhanden - ein Projekt mit Projektname p_projektname
	 * gibt es keinen Treffer in der DB liefert die Methode NULL
	 * @param p_projektname
	 * @return Projekt
	 */
	public Projekt findeProjektMitProjektname(String p_projektname)
	{
		Projekt projekt = null; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT * FROM tblprojekte WHERE projekt_name='" + p_projektname + "';";
			
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next())
			{
				projekt = new Projekt(rs.getInt("projekt_id"), rs.getString("projekt_name"), new Nutzer()); 
				projekt.setArchiviert(rs.getInt("projekt_archiviert"));
				projekt.setBeschreibung(rs.getString("projekt_beschreibung"));
				projekt.setDeadline(rs.getDate("projekt_deadline"));
				projekt.setKanban(new Kanban(rs.getInt("kanban_id")));
				projekt.setRechtzeitig(rs.getInt("projekt_rechtzeitig"));
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return projekt; 
	}
	
	/**
	 * Methode liefert - wenn in DB vorhanden - ein Kanbanobjekt mit Bezeichnung p_name
	 * gibt es keinen Treffer in der DB liefert die Methode NULL
	 * @param p_name
	 * @return Kanban
	 */
	public Kanban findeKanbanMitName(String p_name)
	{
		Kanban kanban = null; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT * FROM tblkanban WHERE kanban_bezeichnung='" + p_name + "';";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				kanban = new Kanban(rs.getString("kanban_bezeichnung"));
				kanban.setId(rs.getInt("kanban_id"));
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return kanban; 
	}
	
	/**
	 * Methode liefert - wenn in DB vorhanden - einen Nutzer mit Mailadresse p_mail
	 * gibt es keinen Treffer in der DB liefert die Methode NULL
	 * @param p_mail
	 * @return
	 */
	public Nutzer findeUserMitEmail(String p_mail)
	{
		Nutzer nutzer = null; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT * FROM tbluser WHERE user_mail='" + p_mail + "';";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				nutzer = new Nutzer(rs.getInt("user_id"), rs.getString("user_vorname"), rs.getString("user_nachname"), rs.getString("user_benutzername"), rs.getString("user_passwort"));
				nutzer.setMail(rs.getString("user_mail"));
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return nutzer; 
	}
	
	/**
	 * Methode liefert alle Prioritäten, die in der Datenbank eingetragen sind 
	 * @return List<String> 
	 */
	public List<String> ermittlePrioritäten()
	{
		List<String> prios = new ArrayList<String>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT prioritaet_bezeichnung FROM tblprioritaet;"; 
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				prios.add(rs.getString("prioritaet_bezeichnung")); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return prios; 
	}
	
	/**
	 * Methode liefert alle Projekte aus der Datenbank
	 * @return List<Projekt> 
	 */
	public List<Projekt> ermittleProjekte()
	{
		List<Projekt> projekte = new ArrayList<Projekt>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT projekt_id, projekt_name, projekt_beschreibung, projekt_deadline, prioritaet_bezeichnung, user_benutzername, projekt_archiviert, projekt_rechtzeitig, kanban_bezeichnung, kanban_id FROM tblprojekte " +
					"LEFT JOIN tbluser USING (user_id) "
					+ "LEFT JOIN tblprioritaet USING (prioritaet_id) "
					+ "LEFT JOIN tblkanban USING(kanban_id);";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				Projekt projekt = new Projekt(rs.getInt("projekt_id"), rs.getString("projekt_name"), new Nutzer(rs.getString("user_benutzername")));
				projekt.setBeschreibung(rs.getString("projekt_beschreibung"));
				projekt.setDeadline(rs.getDate("projekt_deadline"));
				projekt.setPrioritaet(rs.getString("prioritaet_bezeichnung"));
				projekt.setArchiviert(rs.getInt("projekt_archiviert"));
				projekt.setKanban(new Kanban(rs.getInt("kanban_id"), rs.getString("kanban_bezeichnung")));
				projekt.setRechtzeitig(rs.getInt("projekt_rechtzeitig"));
				projekte.add(projekt); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return projekte;
	}
	
	/**
	 * Methode liefert alle Aufgaben aus der Datenbank
	 * @return List<Aufgabe> 
	 */
	public List<Aufgabe> ermittleAufgaben()
	{
		List<Aufgabe> aufgabenListe = new ArrayList<Aufgabe>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT aufgabe_id, aufgabe_name, aufgabe_beschreibung, aufgabe_deadline, aufgabe_schaetzung, prioritaet_bezeichnung, status_name, projekt_name, aufgabe_aktiv, user_benutzername, aufgabe_rechtzeitig FROM tblaufgaben " 
					+ "LEFT JOIN tblprioritaet USING (prioritaet_id) "
					+ "LEFT JOIN tblstatus USING (status_id) "
					+ "LEFT JOIN tblprojekte USING (projekt_id) "
					+ "LEFT JOIN tbluser ON tblaufgaben.user_id = tbluser.user_id;";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				Aufgabe aufgabe = new Aufgabe(rs.getInt("aufgabe_id"), rs.getString("aufgabe_name"), rs.getString("aufgabe_beschreibung"), new Nutzer(rs.getString("user_benutzername"))); 
				aufgabe.setDeadline(rs.getDate("aufgabe_deadline"));
				aufgabe.setSchaetzung(rs.getInt("aufgabe_schaetzung"));
				aufgabe.setPrioritaet(rs.getString("prioritaet_bezeichnung"));
				aufgabe.setStatus(rs.getString("status_name"));
				aufgabe.setProjekt(new Projekt(rs.getString("projekt_name")));
				aufgabe.setArchiviert(rs.getInt("aufgabe_aktiv"));
				aufgabe.setRechtzeitig(rs.getInt("aufgabe_rechtzeitig"));
				aufgabenListe.add(aufgabe);  
				
			} 
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return aufgabenListe;
	}
	
	/**
	 * Liefert alle Aufgaben eines Projekts
	 * @param p_projekt
	 * @return List<Aufgabe> 
	 */
	public List<Aufgabe> ermittleAufgabenProjekt(Projekt p_projekt)
	{
		List<Aufgabe> liste = new ArrayList<Aufgabe>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql="SELECT aufgabe_id, aufgabe_name, aufgabe_beschreibung, aufgabe_deadline, aufgabe_schaetzung, prioritaet_bezeichnung, status_name, projekt_name, aufgabe_aktiv, aufgabe_rechtzeitig, user_benutzername FROM tblaufgaben " 
					+ "LEFT JOIN tblprioritaet USING (prioritaet_id) "
					+ "LEFT JOIN tblstatus USING (status_id) "
					+ "LEFT JOIN tblprojekte USING (projekt_id) "
					+ "LEFT JOIN tbluser ON tblaufgaben.user_id = tbluser.user_id "
					+ "WHERE projekt_name='" + p_projekt.getName() + "';"; 
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				Aufgabe aufgabe = new Aufgabe(rs.getInt("aufgabe_id"), rs.getString("aufgabe_name"), rs.getString("aufgabe_beschreibung"), new Nutzer(rs.getString("user_benutzername"))); 
				aufgabe.setDeadline(rs.getDate("aufgabe_deadline"));
				aufgabe.setSchaetzung(rs.getInt("aufgabe_schaetzung"));
				aufgabe.setPrioritaet(rs.getString("prioritaet_bezeichnung"));
				aufgabe.setStatus(rs.getString("status_name"));
				aufgabe.setProjekt(new Projekt(rs.getString("projekt_name")));
				aufgabe.setArchiviert(rs.getInt("aufgabe_aktiv"));
				aufgabe.setRechtzeitig(rs.getInt("aufgabe_rechtzeitig"));
				liste.add(aufgabe); 
			} 
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return liste; 
	}
	
	/**
	 * Liefert alle Aufgaben eines Projekts im Backlog
	 * @param p_projekt
	 * @return List<Aufgabe> 
	 */
	public List<Aufgabe> ermittleBacklog(Projekt p_projekt)
	{
		List<Aufgabe> backlog = new ArrayList<Aufgabe>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql="SELECT aufgabe_id, aufgabe_name, aufgabe_beschreibung, aufgabe_deadline, aufgabe_schaetzung, prioritaet_bezeichnung, status_name, projekt_name, aufgabe_aktiv, user_benutzername FROM tblaufgaben " 
					+ "LEFT JOIN tblprioritaet USING (prioritaet_id) "
					+ "LEFT JOIN tblstatus USING (status_id) "
					+ "LEFT JOIN tblprojekte USING (projekt_id) "
					+ "LEFT JOIN tbluser ON tblaufgaben.user_id = tbluser.user_id "
					+ "WHERE projekt_name='" + p_projekt.getName() + "' AND status_name ='Backlog';"; 
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				Aufgabe aufgabe = new Aufgabe(rs.getInt("aufgabe_id"), rs.getString("aufgabe_name"), rs.getString("aufgabe_beschreibung"), new Nutzer(rs.getString("user_benutzername"))); 
				aufgabe.setDeadline(rs.getDate("aufgabe_deadline"));
				aufgabe.setSchaetzung(rs.getInt("aufgabe_schaetzung"));
				aufgabe.setPrioritaet(rs.getString("prioritaet_bezeichnung"));
				aufgabe.setStatus(rs.getString("status_name"));
				aufgabe.setProjekt(new Projekt(rs.getString("projekt_name")));
				aufgabe.setArchiviert(rs.getInt("aufgabe_aktiv"));
				backlog.add(aufgabe);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return backlog; 
	}
	
	/**
	 * Liefert alle Aufgaben eines Projekts aus To Do 
	 * @param p_projekt
	 * @return List<Aufgabe>
	 */
	public List<Aufgabe> ermittleToDo(Projekt p_projekt)
	{
		List<Aufgabe> liste = new ArrayList<Aufgabe>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql="SELECT aufgabe_id, aufgabe_name, aufgabe_beschreibung, aufgabe_deadline, aufgabe_schaetzung, prioritaet_bezeichnung, status_name, projekt_name, aufgabe_aktiv, user_benutzername FROM tblaufgaben " 
					+ "LEFT JOIN tblprioritaet USING (prioritaet_id) "
					+ "LEFT JOIN tblstatus USING (status_id) "
					+ "LEFT JOIN tblprojekte USING (projekt_id) "
					+ "LEFT JOIN tbluser ON tblaufgaben.user_id = tbluser.user_id "
					+ "WHERE projekt_name='" + p_projekt.getName() + "' AND status_name ='To Do';"; 
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				Aufgabe aufgabe = new Aufgabe(rs.getInt("aufgabe_id"), rs.getString("aufgabe_name"), rs.getString("aufgabe_beschreibung"), new Nutzer(rs.getString("user_benutzername"))); 
				aufgabe.setDeadline(rs.getDate("aufgabe_deadline"));
				aufgabe.setSchaetzung(rs.getInt("aufgabe_schaetzung"));
				aufgabe.setPrioritaet(rs.getString("prioritaet_bezeichnung"));
				aufgabe.setStatus(rs.getString("status_name"));
				aufgabe.setProjekt(new Projekt(rs.getString("projekt_name")));
				aufgabe.setArchiviert(rs.getInt("aufgabe_aktiv"));
				liste.add(aufgabe);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return liste; 
	}
	
	/**
	 * Liefert alle Aufgaben eines Projekts aus In Progress
	 * @param p_projekt
	 * @return List<Aufgabe>
	 */
	public List<Aufgabe> ermittleInProgress(Projekt p_projekt)
	{
		List<Aufgabe> liste = new ArrayList<Aufgabe>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql="SELECT aufgabe_id, aufgabe_name, aufgabe_beschreibung, aufgabe_deadline, aufgabe_schaetzung, prioritaet_bezeichnung, status_name, projekt_name, aufgabe_aktiv, user_benutzername FROM tblaufgaben " 
					+ "LEFT JOIN tblprioritaet USING (prioritaet_id) "
					+ "LEFT JOIN tblstatus USING (status_id) "
					+ "LEFT JOIN tblprojekte USING (projekt_id) "
					+ "LEFT JOIN tbluser ON tblaufgaben.user_id = tbluser.user_id "
					+ "WHERE projekt_name='" + p_projekt.getName() + "' AND status_name ='In Progress';"; 
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				Aufgabe aufgabe = new Aufgabe(rs.getInt("aufgabe_id"), rs.getString("aufgabe_name"), rs.getString("aufgabe_beschreibung"), new Nutzer(rs.getString("user_benutzername"))); 
				aufgabe.setDeadline(rs.getDate("aufgabe_deadline"));
				aufgabe.setSchaetzung(rs.getInt("aufgabe_schaetzung"));
				aufgabe.setPrioritaet(rs.getString("prioritaet_bezeichnung"));
				aufgabe.setStatus(rs.getString("status_name"));
				aufgabe.setProjekt(new Projekt(rs.getString("projekt_name")));
				aufgabe.setArchiviert(rs.getInt("aufgabe_aktiv"));
				liste.add(aufgabe);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return liste; 
	}
	
	/**
	 * Liefert alle Aufgaben eines Projekts aus Testing
	 * @param p_projekt
	 * @return List<Aufgabe>
	 */
	public List<Aufgabe> ermittleTesting(Projekt p_projekt)
	{
		List<Aufgabe> liste = new ArrayList<Aufgabe>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql="SELECT aufgabe_id, aufgabe_name, aufgabe_beschreibung, aufgabe_deadline, aufgabe_schaetzung, prioritaet_bezeichnung, status_name, projekt_name, aufgabe_aktiv, user_benutzername FROM tblaufgaben " 
					+ "LEFT JOIN tblprioritaet USING (prioritaet_id) "
					+ "LEFT JOIN tblstatus USING (status_id) "
					+ "LEFT JOIN tblprojekte USING (projekt_id) "
					+ "LEFT JOIN tbluser ON tblaufgaben.user_id = tbluser.user_id "
					+ "WHERE projekt_name='" + p_projekt.getName() + "' AND status_name ='Testing';"; 
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				Aufgabe aufgabe = new Aufgabe(rs.getInt("aufgabe_id"), rs.getString("aufgabe_name"), rs.getString("aufgabe_beschreibung"), new Nutzer(rs.getString("user_benutzername"))); 
				aufgabe.setDeadline(rs.getDate("aufgabe_deadline"));
				aufgabe.setSchaetzung(rs.getInt("aufgabe_schaetzung"));
				aufgabe.setPrioritaet(rs.getString("prioritaet_bezeichnung"));
				aufgabe.setStatus(rs.getString("status_name"));
				aufgabe.setProjekt(new Projekt(rs.getString("projekt_name")));
				aufgabe.setArchiviert(rs.getInt("aufgabe_aktiv"));
				liste.add(aufgabe);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return liste; 
	}
	
	/**
	 * Liefert alle Aufgaben eines Projekts aus Done
	 * @param p_projekt
	 * @return List<Aufgabe>
	 */
	public List<Aufgabe> ermittleDone(Projekt p_projekt)
	{
		List<Aufgabe> liste = new ArrayList<Aufgabe>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql="SELECT aufgabe_id, aufgabe_name, aufgabe_beschreibung, aufgabe_deadline, aufgabe_schaetzung, prioritaet_bezeichnung, status_name, projekt_name, aufgabe_aktiv, aufgabe_rechtzeitig, user_benutzername FROM tblaufgaben " 
					+ "LEFT JOIN tblprioritaet USING (prioritaet_id) "
					+ "LEFT JOIN tblstatus USING (status_id) "
					+ "LEFT JOIN tblprojekte USING (projekt_id) "
					+ "LEFT JOIN tbluser ON tblaufgaben.user_id = tbluser.user_id "
					+ "WHERE projekt_name='" + p_projekt.getName() + "' AND status_name ='Done';"; 
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				Aufgabe aufgabe = new Aufgabe(rs.getInt("aufgabe_id"), rs.getString("aufgabe_name"), rs.getString("aufgabe_beschreibung"), new Nutzer(rs.getString("user_benutzername"))); 
				aufgabe.setDeadline(rs.getDate("aufgabe_deadline"));
				aufgabe.setSchaetzung(rs.getInt("aufgabe_schaetzung"));
				aufgabe.setPrioritaet(rs.getString("prioritaet_bezeichnung"));
				aufgabe.setStatus(rs.getString("status_name"));
				aufgabe.setProjekt(new Projekt(rs.getString("projekt_name")));
				aufgabe.setArchiviert(rs.getInt("aufgabe_aktiv"));
				aufgabe.setRechtzeitig(rs.getInt("aufgabe_rechtzeitig"));
				liste.add(aufgabe); 
			} 
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return liste; 
	}
	
	/**
	 * Liefert alle Aufgaben eines Projekts aus Burn
	 * @param p_projekt
	 * @return List<Aufgabe>
	 */
	public List<Aufgabe> ermittleBurn(Projekt p_projekt)
	{
		List<Aufgabe> liste = new ArrayList<Aufgabe>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql="SELECT aufgabe_id, aufgabe_name, aufgabe_beschreibung, aufgabe_deadline, aufgabe_schaetzung, prioritaet_bezeichnung, status_name, projekt_name, aufgabe_aktiv, aufgabe_rechtzeitig, user_benutzername FROM tblaufgaben " 
					+ "LEFT JOIN tblprioritaet USING (prioritaet_id) "
					+ "LEFT JOIN tblstatus USING (status_id) "
					+ "LEFT JOIN tblprojekte USING (projekt_id) "
					+ "LEFT JOIN tbluser ON tblaufgaben.user_id = tbluser.user_id "
					+ "WHERE projekt_name='" + p_projekt.getName() + "' AND status_name ='Burn';"; 
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				Aufgabe aufgabe = new Aufgabe(rs.getInt("aufgabe_id"), rs.getString("aufgabe_name"), rs.getString("aufgabe_beschreibung"), new Nutzer(rs.getString("user_benutzername"))); 
				aufgabe.setDeadline(rs.getDate("aufgabe_deadline"));
				aufgabe.setSchaetzung(rs.getInt("aufgabe_schaetzung"));
				aufgabe.setPrioritaet(rs.getString("prioritaet_bezeichnung"));
				aufgabe.setStatus(rs.getString("status_name"));
				aufgabe.setProjekt(new Projekt(rs.getString("projekt_name")));
				aufgabe.setArchiviert(rs.getInt("aufgabe_aktiv"));
				aufgabe.setRechtzeitig(rs.getInt("aufgabe_rechtzeitig"));
				liste.add(aufgabe); 
			} 
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return liste; 
	}
	
	/**
	 * Methode liefert alle Nutzer aus der Datenbank
	 * @return List<Nutzer> 
	 */
	public List<Nutzer> ermittleNutzer()
	{
		List<Nutzer> nutzerListe = new ArrayList<Nutzer>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT * FROM tbluser;";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				Nutzer nutzer = new Nutzer(rs.getInt("user_id"), rs.getString("user_vorname"), rs.getString("user_nachname"), rs.getString("user_benutzername"), rs.getString("user_passwort")); 
				nutzer.setMail(rs.getString("user_mail"));
				nutzerListe.add(nutzer); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return nutzerListe; 
	}
	
	/**
	 * Methode liefert alle Kanbans aus der Datenbank
	 * @return List<Kanban> 
	 */
	public List<Kanban> ermittleKanban()
	{
		List<Kanban> kanbanListe = new ArrayList<Kanban>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT * FROM tblkanban;";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				Kanban kanban = new Kanban(rs.getString("kanban_bezeichnung")); 
				kanban.setId(rs.getInt("kanban_id"));
				kanbanListe.add(kanban); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return kanbanListe; 
	}
	
	/**
	 * Methode liefert alle in der Datenbank eingetragenen Bearbeitungszustände für Aufgaben
	 * @return List<String>
	 */
	public List<String> ermittleStatus()
	{
		List<String> statusse = new ArrayList<String>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT status_name FROM tblstatus;";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				statusse.add(rs.getString("status_name"));
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return statusse; 
	}
	
	/**
	 * Methode legt ein neues Projekt in der Datenbank an
	 * @param Projekt
	 * @param prio_id
	 * @param user_id
	 * @return boolean
	 */
	public boolean projektAnlegen(Projekt p_projekt, int prio_id, int user_id, int kanban_id)
	{
		boolean erfolgreich = false; 
		Statement stmt = null; 
		String sql; 
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
		String deadlineString = df.format(p_projekt.getDeadline()); 
		
		//neues Projekt wird immer erst aktiv gesetzt 
		int archiviert = 1; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "INSERT INTO tblprojekte (projekt_name, projekt_beschreibung, projekt_deadline, prioritaet_id, user_id, projekt_archiviert, kanban_id) "
					+ "VALUES ('" + p_projekt.getName() + "','" + p_projekt.getBeschreibung() + "','" + deadlineString + "'," + prio_id + "," + user_id + ", " + archiviert + ", " + kanban_id + ");";
			stmt.execute(sql); 
			
			//ProjektId ermitteln
			int projekt_id = this.ermittleProjektIdNachName(p_projekt.getName()); 
			
			//Zwischentabelle ebenfalls füllen
			if(this.fuelleTblUserProjekt(projekt_id, user_id))
			{
				erfolgreich = true; 
			}
			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return erfolgreich;
	}
	
	/**
	 * Füllt in der DB die Zwischentabelle tblUserProjekt
	 * @param p_projektId
	 * @param p_userId
	 * @return
	 */
	public boolean fuelleTblUserProjekt(int p_projektId, int p_userId)
	{
		boolean erfolgreich = false; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "INSERT INTO tbluserprojekt(user_id, projekt_id) "
					+ "VALUES (" + p_userId + "," + p_projektId + ")"; 
			
			stmt.execute(sql);
			
			erfolgreich = true; 
			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return erfolgreich;
	}
	
	/**
	 * Methode legt neue Aufgabe in der Datenbank an
	 * @param Aufgabe
	 * @param prio_id
	 * @param status_id
	 * @param projekt_id
	 * @return boolean
	 */
	public boolean aufgabeAnlegen(Aufgabe p_aufgabe, int prio_id, int projekt_id, int user_id)
	{
		boolean erfolgreich = false; 
		Statement stmt = null; 
		String sql; 
		String status;
		int status_id; 
		int aktiv; 
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
		String deadlineString = df.format(p_aufgabe.getDeadline()); 
		
		//jedes Projekt wird zunächst als aktiv angelegt
		aktiv = 1; 
		
		//jedes Projekt wird zunächst in das Backlog gelegt
		status = "Backlog"; 
		status_id = this.ermittleStatusIdNachName(status); 
		
		//Aufgabe in Datenbank anlegen
		try
		{
			stmt = this.conn.createStatement();
			sql = "INSERT INTO tblaufgaben (aufgabe_name, aufgabe_beschreibung, aufgabe_deadline, aufgabe_schaetzung, prioritaet_id, status_id, projekt_id, aufgabe_aktiv, user_id) "
					+ "VALUES ('" + p_aufgabe.getName() + "','" + p_aufgabe.getBeschreibung() + "','" + deadlineString + "'," + p_aufgabe.getSchaetzung() + "," + prio_id + "," + status_id + "," + projekt_id + "," + aktiv + "," + user_id + ");";
			stmt.execute(sql); 
			
			erfolgreich = true; 
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return erfolgreich; 
	}
	
	/**
	 * Legt einen neuen Kanban-Datensatz an
	 * @param bezeichnung
	 * @param projekt_id
	 * @return boolean
	 */
	public boolean kanbanAnlegen(String bezeichnung)
	{
		boolean erfolgreich = false; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "INSERT INTO tblkanban (kanban_bezeichnung) "
					+ "VALUES ('" + bezeichnung + "');"; 
			stmt.execute(sql);
			
			erfolgreich = true; 
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return erfolgreich; 
	}
	
	/**
	 * Methode trägt einen neuen User als Teammitglied bei einem Projekt ein
	 * @param Nutzer
	 * @param Projekt
	 * @return boolean
	 */
	public boolean teamerAnlegen(Nutzer p_nutzer, Projekt p_projekt)
	{
		boolean erfolgreich = false; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "INSERT INTO tbluserprojekt (user_id, projekt_id) "
					+ "VALUES (" + p_nutzer.getId() + ", " + p_projekt.getId() + ");";
			
			stmt.execute(sql); 
			erfolgreich = true; 
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return erfolgreich;
		
	}
	
	/**
	 * Eintragen der Änderungen in der Datenbank
	 * @param Projekt
	 * @return boolean
	 */
	public boolean aktualisiereProjekt(Projekt p_projekt)
	{
		boolean erfolgreich = false;
		int prioId = -1; 
		int userId = -1; 
		int kanbanId = -1; 
		Statement stmt = null; 
		String sql = "";
		
		//Ermittle Prio-id
		prioId = this.ermittlePrioIdNachNamen(p_projekt.getPrioritaet());
		
		//User_id ermitteln
		userId = this.ermittleUserIdNachBenutzername(p_projekt.getInitiator().getBenutzername()); 
		
		//Kanban_id ermitteln
		kanbanId = this.ermittleKanbanIdNachName(p_projekt.getKanban().getKanbanName()); 
		
		//Date als String
		String p_deadline = new SimpleDateFormat("yyyy-MM-dd").format(p_projekt.getDeadline()); 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "UPDATE tblprojekte SET projekt_name='" + p_projekt.getName() + "', projekt_beschreibung='" + p_projekt.getBeschreibung() + "', projekt_deadline='" + p_deadline + "', prioritaet_id=" + prioId + ", user_id=" + userId + ", projekt_archiviert=" + p_projekt.getArchiviert() + ", kanban_id=" + kanbanId + " WHERE "
					+ "projekt_id=" + p_projekt.getId() + ";"; 
			stmt.execute(sql); 
			erfolgreich = true; 
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return erfolgreich; 
		
	}
	
	/**
	 * Eintragen der Änderung an einer Aufgabe in die Datenbank
	 * @param Aufgabe
	 * @return boolean
	 */
	public boolean aktualisiereAufgabe(Aufgabe p_aufgabe)
	{
		boolean erfolgreich = false; 
		int prio_id = -1; 
		int stand_id = -1; 
		int projekt_id = -1; 
		int user_id = -1; 
		Statement stmt = null; 
		String sql; 
		
		//prio_id ermitteln
		prio_id = this.ermittlePrioIdNachNamen(p_aufgabe.getPrioritaet()); 
		
		//stand_id ermitteln
		stand_id = this.ermittleStatusIdNachName(p_aufgabe.getStatus()); 
		
		//projekt_id ermitteln
		projekt_id = this.ermittleProjektIdNachName(p_aufgabe.getProjekt().getName()); 
		
		//user_id ermitteln
		user_id = this.ermittleUserIdNachBenutzername(p_aufgabe.getInitiator().getBenutzername()); 
		
		//Deadline als String
		String p_deadline = new SimpleDateFormat("yyyy-MM-dd").format(p_aufgabe.getDeadline()); 
		
		//Sollte Projekt als Done angelegt werden, prüfen, ob rechzeitig fertiggestellt oder nicht 
		int rechtzeitig; 
			
		if(p_aufgabe.getStatus().equals("Done"))
		{
			//aktuelles Datum ermitteln
			Date date = new Date(); 
			
			if(date.after(p_aufgabe.getDeadline()))
			{
				rechtzeitig = 0;
			}
			else
			{
				rechtzeitig = 1; 
			}
			
			sql = "UPDATE tblaufgaben "
					+ "SET aufgabe_name='" + p_aufgabe.getName() + "', aufgabe_beschreibung='" + p_aufgabe.getBeschreibung() + "', aufgabe_deadline='" + p_deadline 
					+ "', aufgabe_schaetzung=" + p_aufgabe.getSchaetzung() + ", prioritaet_id=" + prio_id + ", status_id=" + stand_id
					+ ", projekt_id=" + projekt_id + ", aufgabe_aktiv=" + p_aufgabe.getArchiviert() + ", user_id=" + user_id 
					+ ", aufgabe_rechtzeitig=" + rechtzeitig + 
					" WHERE aufgabe_id=" + p_aufgabe.getId() + ";";
		}
		else
		{
			sql = "UPDATE tblaufgaben "
					+ "SET aufgabe_name='" + p_aufgabe.getName() + "', aufgabe_beschreibung='" + p_aufgabe.getBeschreibung() + "', aufgabe_deadline='" + p_deadline 
					+ "', aufgabe_schaetzung=" + p_aufgabe.getSchaetzung() + ", prioritaet_id=" + prio_id + ", status_id=" + stand_id 
					+ ", projekt_id=" + projekt_id + ", aufgabe_aktiv=" + p_aufgabe.getArchiviert() + ", user_id=" + user_id + 
					" WHERE aufgabe_id=" + p_aufgabe.getId() + ";";
		}
		
		try
		{
			stmt = this.conn.createStatement();
			
			stmt.execute(sql); 
			
			erfolgreich = true; 
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}


		
		if(p_aufgabe.getStatus().equals("Done"))
		{
			if(this.aktualisiereProjektstand(p_aufgabe.getProjekt()))
			{
				erfolgreich = true; 
			}
			else
			{
				erfolgreich = false; 
			}
		}
		
		return erfolgreich; 
	}
	
	/**
	 * Methode aktualisiert einen Nutzereintrag in der Datenbank
	 * @param p_nutzer
	 * @return boolean
	 */
	public boolean aktualisiereNutzer(Nutzer p_nutzer)
	{
		boolean erfolgreich = false; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			
			sql = "UPDATE tbluser "
					+ "SET user_nachname='" + p_nutzer.getNachname() + "', "
					+ "user_vorname='" + p_nutzer.getVorname() + "', "
					+ "user_benutzername='" + p_nutzer.getBenutzername() + "', "
					+ "user_mail='" + p_nutzer.getMail() + "', "
					+ "user_passwort='" + p_nutzer.getPasswort() + "' "
					+ "WHERE user_id=" + p_nutzer.getId() + ";"; 
			
			stmt.execute(sql); 
			erfolgreich = true; 
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return erfolgreich; 
	}
	
	/**
	 * Speichert beim Einloggen die Systemzeit in die Datenbank
	 * @param p_nutzer
	 * @return boolean
	 */
	public boolean aktualisiereLoginZeit(Nutzer p_nutzer)
	{
		boolean erfolgreich = false; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement();
			
			sql = "UPDATE tbluser "
					+ "SET user_login=CURTIME() "
					+ "WHERE user_id =" + p_nutzer.getId() + ";";
			
			stmt.execute(sql); 
			erfolgreich = true; 
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return erfolgreich;
	}
	
	/**
	 * Methode aktualisiert den Bearbeitungsstand einer Aufgabe in der Datenbank
	 * @param p_aufgabe
	 * @param p_stand
	 * @return boolean
	 */
	public boolean aktualisiereBearbeitungsstand(Aufgabe p_aufgabe, String p_stand)
	{
		boolean erfolgreich = false; 
		int status_id; 
		Statement stmt = null; 
		String sql; 
		
		//status_id ermitteln 
		status_id = this.ermittleStatusIdNachName(p_stand); 
		
		//wenn done, dann prüfen, ib rechtzeitig fertig geworden
		int rechtzeitig; 
		
		if(p_stand.equals("Done"))
		{
			//aktuelles Datum ermitteln
			Date date = new Date(); 
			
			if(date.after(p_aufgabe.getDeadline()))
			{
				rechtzeitig = 0;
			}
			else
			{
				rechtzeitig = 1; 
			}
			
			sql = "UPDATE tblaufgaben "
					+ "SET status_id=" + status_id + ", aufgabe_rechtzeitig =" + rechtzeitig + " WHERE aufgabe_id=" + p_aufgabe.getId() + ";"; 
			
		}
		else
		{
			sql = "UPDATE tblaufgaben "
					+ "SET status_id=" + status_id + " WHERE aufgabe_id=" + p_aufgabe.getId() + ";"; 
		}
		
		try
		{
			stmt = this.conn.createStatement(); 
			
			stmt.execute(sql);
			erfolgreich = true; 
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		Projekt p_projekt = p_aufgabe.getProjekt(); 
		
		if(p_stand.equals("Done"))
		{
			if(this.aktualisiereProjektstand(p_projekt))
			{
				erfolgreich = true; 
			}
			else
			{
				erfolgreich = false; 
			}
		}
		
		
		return erfolgreich; 
	}
	
	/**
	 * Liefert id der gewählten Priorität oder -1, wenn Aufruf fehlschlägt
	 * @param bezeichnung
	 * @return int 
	 */
	public int ermittlePrioIdNachNamen(String bezeichnung)
	{
		int id = -1; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT prioritaet_id FROM tblprioritaet WHERE prioritaet_bezeichnung= '" + bezeichnung + "';";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				id = rs.getInt("prioritaet_id"); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return id; 
	}
	
	/**
	 * Liefert die Deadline des Projekts mit übergebener Id
	 * @param p_id
	 * @return Date
	 */
	public Date ermittleDeadlineProjekt(int p_id)
	{
		Date deadline = null; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT projekt_deadline FROM tblprojekte WHERE projekt_id="+ p_id + ";"; 
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				deadline = rs.getDate("projekt_deadline"); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return deadline;
	}
	
	/**
	 * Liefert Deadline des Projekts mit übergebenem Projektname
	 * @param p_name
	 * @return Date
	 */
	public Date ermittleDeadlineProjektNachName(String p_name)
	{
		Date deadline = null; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT projekt_deadline FROM tblprojekte WHERE projekt_name='"+ p_name + "';"; 
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				deadline = rs.getDate("projekt_deadline"); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return deadline;
	}
	
	/**
	 * Liefert id des gewählten Status oder -1, wenn Status nicht vorhanden in Datenbank oder wenn Aufruf fehlschlägt
	 * @param bezeichnung
	 * @return int
	 */
	public int ermittleStatusIdNachName(String bezeichnung)
	{
		int id = -1; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT status_id FROM tblstatus WHERE status_name='" + bezeichnung + "';"; 
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				id = rs.getInt("status_id"); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return id; 
	}
	
	/**
	 * Liefert Id zu einem Kanban-Namen
	 * @param bezeichnung
	 * @return int
	 */
	public int ermittleKanbanIdNachName(String bezeichnung)
	{
		int id = -1; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT kanban_id FROM tblkanban WHERE kanban_bezeichnung='" + bezeichnung + "';"; 
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				id = rs.getInt("kanban_id"); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return id; 
	}
	
	/**
	 * Methode liefert ID zu einem bestimmten Benutzername (unique)
	 * @param p_name
	 * @return int
	 */
	public int ermittleUserIdNachBenutzername(String p_name)
	{
		int id = -1; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT user_id FROM tbluser WHERE user_benutzername='" + p_name + "';"; 
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				id = rs.getInt("user_id"); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return id; 
	}
	
	/**
	 * Liefert id des gewählten Projekts oder -1, wenn Aufruf fehlschlägt
	 * @param name
	 * @return int
	 */
	public int ermittleProjektIdNachName(String name)
	{
		int id = -1; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT projekt_id FROM tblprojekte WHERE projekt_name= '" + name + "';";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				id = rs.getInt("projekt_id"); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return id; 
	}
	
	/**
	 * Methode liefert Bearbeitungsstand einer Aufgabe
	 * @param aufgabe
	 * @return String 
	 */
	public String ermittleBearbeitungsstandAufgabe(Aufgabe aufgabe)
	{
		String stand = ""; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT status_name FROM tblaufgaben "
					+ "LEFT JOIN tblstatus USING (status_id) WHERE aufgabe_id=" + aufgabe.getId() + ";"; 
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				stand = rs.getString("status_name"); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		
		return stand; 
	}
	
	public String ermittleBenutzernameAufgabe(Aufgabe aufgabe)
	{
		String name = ""; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT user_benutzername FROM tbluser "
					+ "LEFT JOIN tblaufgaben USING (user_id) WHERE aufgabe_id=" + aufgabe.getId() + ";"; 
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				name = rs.getString("user_benutzername"); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		
		return name; 
	}
	
	/**
	 * Methode löscht ein Projekt mit bestimmter Projekt_id aus der Datenbank
	 * @param p_projekt
	 * @return boolean
	 */
	public boolean loescheProjekt(Projekt p_projekt)
	{
		boolean erfolgreich = false; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			
			//Projekt löschen
			sql = "DELETE tblprojekte, tbluserprojekt FROM tblprojekte, tbluserprojekt "
					+ "WHERE tblprojekte.projekt_id=" + p_projekt.getId() + " AND tbluserprojekt.projekt_id =" + p_projekt.getId() + ";"; 
			
			stmt.execute(sql);
			
			try
			{
				System.out.println("Kanban: " + p_projekt.getKanban().getId());
				
				//zugehöriges Kanban löschen
				sql = "DELETE FROM tblkanban WHERE kanban_id='" + p_projekt.getKanban().getId() + "';"; 
				
				stmt.execute(sql);
				
				//alle Aufgaben des Projekts löschen
				try
				{
					sql = "DELETE FROM tblaufgaben WHERE projekt_id=" + p_projekt.getId() + ";"; 
					
					stmt.execute(sql);
					erfolgreich = true; 
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
			
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			} 
	
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return erfolgreich; 
	}
	
	/**
	 * Methode löscht Aufgabe aus der Datenbank
	 * @param p_aufgabe
	 * @return boolean
	 */
	public boolean loescheAufgabe(Aufgabe p_aufgabe)
	{
		boolean erfolgreich = false; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			
			//Aufgabe löschen
			sql = "DELETE FROM tblaufgaben WHERE aufgabe_id=" + p_aufgabe.getId() + ";"; 
			
			stmt.execute(sql);
			
			erfolgreich = true; 
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return erfolgreich; 
	}
	
	/**
	 * Löscht einen Nutzer mit Benutzername p_benutzername aus der DB und alle ihm zugeteilten Projekte
	 * @param nutzer
	 * @return boolean
	 */
	public boolean loescheNutzer(Nutzer nutzer)
	{
		boolean erfolgreich = false; 
		List<Projekt> projekte; 
		Statement stmt = null; 
		Statement stmt2 = null; 
		String sql;
		String sql2; 
		
		
		try
		{
			stmt = this.conn.createStatement(); 
			
			//Nutzer löschen
			sql = "DELETE FROM tbluser WHERE user_benutzername='" + nutzer.getBenutzername() + "';";
			
			//Projekte des Nutzer ermitteln
			projekte = this.ermittleProjekteMaster(nutzer); 
			
			//alle Projekt löschen 
			for(int i = 0; i < projekte.size(); i++)
			{
				this.loescheProjekt(projekte.get(i)); 
			}
			
			
			stmt.execute(sql); 
			
			//User aus der Zwischentabelle löschen 
			stmt2 = this.conn.createStatement(); 
			
			sql2 = "DELETE FROM tbluserprojekt WHERE user_id=" + nutzer.getId() + ";";
			
			stmt2.execute(sql2); 
			
			erfolgreich = true; 
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return erfolgreich; 
	}
	
	/**
	 * Liefert alle Projekte eines Nutzers
	 * @param p_nutzer
	 * @return List<Projekt> 
	 */
	public List<Projekt> ermittleProjekteNutzer(Nutzer p_nutzer)
	{
		List<Projekt> projekte = new ArrayList<Projekt>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			/*sql = "SELECT projekt_id, projekt_name, projekt_beschreibung, projekt_deadline, prioritaet_bezeichnung, user_benutzername, projekt_archiviert, kanban_bezeichnung, kanban_id FROM tblprojekte " +
					"LEFT JOIN tbluser USING (user_id) "
					+ "LEFT JOIN tblprioritaet USING (prioritaet_id) "
					+ "LEFT JOIN tblkanban USING(kanban_id) " 
					+ "WHERE user_benutzername='" + p_nutzer.getBenutzername() + "' ORDER BY projekt_deadline ASC;";*/
			sql = "SELECT projekt_id, projekt_name, projekt_beschreibung, projekt_deadline, prioritaet_bezeichnung, tblprojekte.user_id, projekt_archiviert, kanban_bezeichnung, kanban_id FROM tbluserprojekt " +
					"LEFT JOIN tblprojekte USING (projekt_id) "
					+ "LEFT JOIN tbluser ON (tbluserprojekt.user_id = tbluser.user_id) "
					+ "LEFT JOIN tblprioritaet USING (prioritaet_id) "
					+ "LEFT JOIN tblkanban USING(kanban_id) " 
					+ "WHERE tbluserprojekt.user_id=" + p_nutzer.getId() + ";";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				Nutzer nutzer = new Nutzer(rs.getInt("tblprojekte.user_id")); 
				nutzer.setBenutzername(this.ermittleBenutzernameNachId(rs.getInt("tblprojekte.user_id")));
				Projekt projekt = new Projekt(rs.getInt("projekt_id"), rs.getString("projekt_name"), nutzer);
				projekt.setBeschreibung(rs.getString("projekt_beschreibung"));
				projekt.setDeadline(rs.getDate("projekt_deadline"));
				projekt.setPrioritaet(rs.getString("prioritaet_bezeichnung"));
				projekt.setArchiviert(rs.getInt("projekt_archiviert"));
				projekt.setKanban(new Kanban(rs.getInt("kanban_id"), rs.getString("kanban_bezeichnung")));
				projekte.add(projekt);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return projekte;
	}
	
	/**
	 * Liefert alle Kanbans eines Nutzers
	 * @param p_nutzer
	 * @return List<Kanban> 
	 */
	public List<Kanban>ermittleKanbanNutzer(Nutzer p_nutzer)
	{
		List<Kanban> kanbanListe = new ArrayList<Kanban>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			/*sql = "SELECT kanban_id, kanban_bezeichnung, user_benutzername, projekt_name FROM tblkanban "
					+ "LEFT JOIN tblprojekte USING(kanban_id) "
					+ "LEFT JOIN tbluser USING(user_id) " 
					+ "WHERE user_benutzername='" + p_nutzer.getBenutzername() + "';";*/
			sql = "SELECT kanban_id, kanban_bezeichnung, tbluserprojekt.user_id, projekt_name FROM tbluserprojekt "
					+ "LEFT JOIN tblprojekte USING(projekt_id) "
					+ "LEFT JOIN tblkanban USING(kanban_id) " 
					+ "WHERE tbluserprojekt.user_id='" + p_nutzer.getId() + "';";
			
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				Kanban kanban = new Kanban(rs.getString("kanban_bezeichnung")); 
				kanban.setId(rs.getInt("kanban_id"));
				kanban.setProjektName(rs.getString("projekt_name"));
				kanbanListe.add(kanban); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return kanbanListe; 
		
	}
	
	/**
	 * Liefert das Projekt zu einem gegebenen Kanbanboard
	 * @param p_kanban
	 * @return Projekt
	 */
	public Projekt ermittleProjektKanban(Kanban p_kanban)
	{
		Projekt projekt = null; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql =  "SELECT projekt_name, projekt_archiviert, user_benutzername FROM tblprojekte " 
					+ "LEFT JOIN tblkanban USING (kanban_id) "
					+ "LEFT JOIN tbluser USING (user_id)"
					+ "WHERE kanban_id=" + p_kanban.getId() + ";";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				projekt = new Projekt(rs.getString("projekt_name"));
				projekt.setInitiator(new Nutzer(rs.getString("user_benutzername")));
				projekt.setArchiviert(rs.getInt("projekt_archiviert"));
			}
			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return projekt; 
	}
	
	/**
	 * methode liefert Projekt zu einer bestimmten Aufgabe
	 * @param p_aufgabe
	 * @return Projekt
	 */
	public Projekt ermittleProjektAufgabe(Aufgabe p_aufgabe)
	{
		Projekt projekt = null; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT projekt_name FROM tblprojekte LEFT JOIN tblaufgaben USING (projekt_id) WHERE aufgabe_id =" + p_aufgabe.getId() + ";"; 
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				projekt = new Projekt(rs.getString("projekt_name"));
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return projekt; 
	}
	
	/**
	 * Methode liefert alle Aufgaben eines bestimmten Nutzers sortiert nach Deadline aufsteigend
	 * @param p_nutzer
	 * @return
	 */
	public List<Aufgabe>ermittleAufgabenNutzerSortByDeadline(Nutzer p_nutzer)
	{
		List<Aufgabe> aufgabenListe = new ArrayList<Aufgabe>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT aufgabe_id, aufgabe_name, aufgabe_beschreibung, aufgabe_deadline, aufgabe_schaetzung, status_name, projekt_name, aufgabe_aktiv, user_benutzername, aufgabe_rechtzeitig, prioritaet_bezeichnung FROM tblaufgaben "
					+ "LEFT JOIN tblprojekte USING(projekt_id) "
					+ "LEFT JOIN tbluser ON tblaufgaben.user_id = tbluser.user_id "
					+ "LEFT JOIN tblprioritaet ON tblaufgaben.prioritaet_id = tblprioritaet.prioritaet_id "
					+ "LEFT JOIN tblstatus ON tblaufgaben.status_id = tblstatus.status_id "
					+ "WHERE user_benutzername='" + p_nutzer.getBenutzername() + "' ORDER BY aufgabe_deadline ASC;";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				Aufgabe aufgabe = new Aufgabe(rs.getInt("aufgabe_id"), rs.getString("aufgabe_name"), rs.getString("aufgabe_beschreibung"), new Nutzer(rs.getString("user_benutzername"))); 
				aufgabe.setArchiviert(rs.getInt("aufgabe_aktiv"));
				aufgabe.setDeadline(rs.getDate("aufgabe_deadline"));
				aufgabe.setPrioritaet(rs.getString("prioritaet_bezeichnung"));
				aufgabe.setRechtzeitig(rs.getInt("aufgabe_rechtzeitig"));
				aufgabe.setSchaetzung(rs.getInt("aufgabe_schaetzung"));
				aufgabe.setStatus(rs.getString("status_name"));
				aufgabe.setProjekt(new Projekt(rs.getString("projekt_name")));
				aufgabenListe.add(aufgabe); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return aufgabenListe; 
	}
	
	/**
	 * Liefert alle Projekte eines Nutzers aufsteigend sortiert nach Deadline
	 * @param p_nutzer
	 * @return List<Projekt> 
	 */
	public List<Projekt> ermittleProjekteNutzerSortByDeadline(Nutzer p_nutzer)
	{
		List<Projekt> projekte = new ArrayList<Projekt>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			/*sql = "SELECT projekt_id, projekt_name, projekt_beschreibung, projekt_deadline, prioritaet_bezeichnung, user_benutzername, projekt_archiviert, kanban_bezeichnung, kanban_id FROM tblprojekte " +
					"LEFT JOIN tbluser USING (user_id) "
					+ "LEFT JOIN tblprioritaet USING (prioritaet_id) "
					+ "LEFT JOIN tblkanban USING(kanban_id) " 
					+ "WHERE user_benutzername='" + p_nutzer.getBenutzername() + "' ORDER BY projekt_deadline ASC;";*/
			sql = "SELECT projekt_id, projekt_name, projekt_beschreibung, projekt_deadline, prioritaet_bezeichnung, tblprojekte.user_id, projekt_archiviert, kanban_bezeichnung, kanban_id FROM tbluserprojekt " +
					"LEFT JOIN tblprojekte USING (projekt_id) "
					+ "LEFT JOIN tbluser ON (tbluserprojekt.user_id = tbluser.user_id) "
					+ "LEFT JOIN tblprioritaet USING (prioritaet_id) "
					+ "LEFT JOIN tblkanban USING(kanban_id) " 
					+ "WHERE tbluserprojekt.user_id='" + p_nutzer.getId() + "' ORDER BY projekt_deadline ASC;";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				Nutzer nutzer = new Nutzer(rs.getInt("tblprojekte.user_id")); 
				nutzer.setBenutzername(this.ermittleBenutzernameNachId(rs.getInt("tblprojekte.user_id")));
				Projekt projekt = new Projekt(rs.getInt("projekt_id"), rs.getString("projekt_name"), nutzer);
				projekt.setBeschreibung(rs.getString("projekt_beschreibung"));
				projekt.setDeadline(rs.getDate("projekt_deadline"));
				projekt.setPrioritaet(rs.getString("prioritaet_bezeichnung"));
				projekt.setArchiviert(rs.getInt("projekt_archiviert"));
				projekt.setKanban(new Kanban(rs.getInt("kanban_id"), rs.getString("kanban_bezeichnung")));
				projekte.add(projekt);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return projekte;
	}
	
	/**
	 * Methode liefert den Benutzername zu einer User_ID
	 * @param p_id
	 * @return String
	 */
	private String ermittleBenutzernameNachId(int p_id) 
	{
		String benutzername = ""; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT user_benutzername FROM tbluser WHERE user_id=" + p_id + ";";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				benutzername = rs.getString("user_benutzername"); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return benutzername;
	}
	
	/**
	 * Liefert Benutzername des Masters eines bestimmten Projekts
	 * @param int (p_id)
	 * @return String
	 */
	public String ermittleMasterNachProjektid(int p_id)
	{
		String benutzername = ""; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT user_benutzername FROM tblprojekte "
					+ "LEFT JOIN tbluser USING (user_id) "
					+ "WHERE projekt_id =" + p_id + ";"; 
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				benutzername = rs.getString("user_benutzername"); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return benutzername; 
	}

	/**
	 * Liefert alle Projekte eines Masters
	 * @param p_master (Nutzer)
	 * @return List<Projekt> 
	 */
	public List<Projekt> ermittleProjekteMaster(Nutzer p_master)
	{
		List<Projekt> projekte = new ArrayList<Projekt>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT projekt_id, projekt_name, projekt_beschreibung, projekt_deadline, prioritaet_bezeichnung, user_benutzername, projekt_archiviert, kanban_bezeichnung, kanban_id FROM tblprojekte " +
					"LEFT JOIN tbluser USING (user_id) "
					+ "LEFT JOIN tblprioritaet USING (prioritaet_id) "
					+ "LEFT JOIN tblkanban USING(kanban_id) " 
					+ "WHERE user_benutzername='" + p_master.getBenutzername() + "';";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				Projekt projekt = new Projekt(rs.getInt("projekt_id"), rs.getString("projekt_name"), new Nutzer(rs.getString("user_benutzername")));
				projekt.setBeschreibung(rs.getString("projekt_beschreibung"));
				projekt.setDeadline(rs.getDate("projekt_deadline"));
				projekt.setPrioritaet(rs.getString("prioritaet_bezeichnung"));
				projekt.setArchiviert(rs.getInt("projekt_archiviert"));
				projekt.setKanban(new Kanban(rs.getInt("kanban_id"), rs.getString("kanban_bezeichnung")));
				projekte.add(projekt);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return projekte;
	}
	
	/**
	 * Methode liefert alle Teammitglieder eines bestimmten Projekts
	 * @param p_projekt
	 * @return List<Nutzer>
	 */
	public List<Nutzer> ermittleNutzerProjekt(Projekt p_projekt)
	{
		List<Nutzer> liste = new ArrayList<Nutzer>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT user_benutzername FROM tbluserprojekt "
					+ "LEFT JOIN tbluser USING (user_id) "
					+ "WHERE projekt_id =" + p_projekt.getId() + ";";
			
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next())
			{
				Nutzer nutzer = new Nutzer(rs.getString("user_benutzername")); 
				liste.add(nutzer); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return liste; 
	}
	
	/**
	 * Ändert den Status eines Objekts (archiviert/aktiv)
	 * @param p_projekt
	 * @return boolean
	 */
	public boolean archiviereProjekt(Projekt p_projekt)
	{
		boolean erfolgreich = false; 
		Statement stmt = null; 
		Statement stmt2 = null; 
		String sql = ""; 
		String sql2 = ""; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			stmt2 = this.conn.createStatement(); 
			
			if(p_projekt.getArchiviert()==1)
			{
				sql = "UPDATE tblprojekte SET projekt_archiviert =" + 0 + " WHERE projekt_id=" + p_projekt.getId() + ";";
				sql2 = "UPDATE tblaufgaben SET aufgabe_aktiv =" + 0 + " WHERE projekt_id=" + p_projekt.getId() + ";";
			}
			else
			{
				sql = "UPDATE tblprojekte SET projekt_archiviert =" + 1 + " WHERE projekt_id=" + p_projekt.getId() + ";";
				sql2 = "UPDATE tblaufgaben SET aufgabe_aktiv =" + 1 + " WHERE projekt_id=" + p_projekt.getId() + ";";
			}
			
			stmt.execute(sql);
			stmt2.execute(sql2); 
			erfolgreich = true;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return erfolgreich; 
	}
	
	/**
	 * Methode ändert den Status einer Aufgabe (aktiv/archiviert)
	 * @param p_aufgabe
	 * @return boolean
	 */
	public boolean archiviereAufgabe(Aufgabe p_aufgabe)
	{
		boolean erfolgreich = false; 
		Statement stmt = null; 
		String sql = "";
		
		try
		{
			stmt = this.conn.createStatement(); 
			
			if(p_aufgabe.getArchiviert() == 1)
			{
				sql = "UPDATE tblaufgaben SET aufgabe_aktiv=" + 0 + " WHERE aufgabe_id=" + p_aufgabe.getId() + ";";
			}
			else
			{
				sql = "UPDATE tblaufgaben SET aufgabe_aktiv=" + 1 + " WHERE aufgabe_id=" + p_aufgabe.getId() + ";";
			}
			
			stmt.execute(sql); 
			erfolgreich = true; 
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return erfolgreich; 
	}
	
	/**
	 * Methode liefert Projekt-Objekt zu einer Projekt_id
	 * @param id
	 * @return Projekt
	 */
	public Projekt ermittleProjektNachId(int id)
	{
		Projekt projekt = null; 
		List<Projekt> projekte; 
		
		projekte = this.ermittleProjekte(); 
		
		for(int i = 0; i < projekte.size(); i++)
		{
			if(projekte.get(i).getId() == id)
			{
				projekt = projekte.get(i);
				break; 
			}
		}
		
		return projekt; 
	}
	
	/**
	 * Methode liefert Nutzer aus Datenbank anhand des Benutzernamens 
	 * @param p_benutzername
	 * @return
	 */
	public Nutzer ermittleNutzerNachBenutzername(String p_benutzername)
	{
		Nutzer nutzer = null; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT * FROM tbluser WHERE user_benutzername='" + p_benutzername + "';";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				nutzer = new Nutzer(rs.getInt("user_id"), rs.getString("user_vorname"), rs.getString("user_nachname"), rs.getString("user_benutzername"), rs.getString("user_passwort")); 
				nutzer.setMail(rs.getString("user_mail"));
				nutzer.setLoginZeit(rs.getString("user_login"));
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return nutzer; 
	}
	
	/**
	 * ermittelt alle Projekte aus der Datenbank, die eine bestimmte Zeichenfolge beinhalten
	 * @param p_eingabe
	 * @return List<Projekt>
	 */
	public List<Projekt> ermittleProjekteNachChar(String p_eingabe)
	{
		List<Projekt> projekte = new ArrayList<Projekt>(); 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement();
			sql = "SELECT projekt_id, projekt_name, projekt_beschreibung, user_benutzername, projekt_deadline, kanban_bezeichnung, prioritaet_bezeichnung FROM tblprojekte " 
					+ "LEFT JOIN tbluser USING (user_id) "
					+ "LEFT JOIN tblkanban USING (kanban_id) "
					+ "LEFT JOIN tblprioritaet USING (prioritaet_id)"
					+ "WHERE projekt_name LIKE '%" + p_eingabe + "%';";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				Projekt projekt = new Projekt(rs.getInt("projekt_id"), rs.getString("projekt_name"), new Nutzer(rs.getString("user_benutzername"))); 
				projekt.setDeadline(rs.getDate("projekt_deadline")); 
				projekt.setKanban(new Kanban(rs.getString("kanban_bezeichnung")));
				projekt.setPrioritaet(rs.getString("prioritaet_bezeichnung"));
				projekt.setBeschreibung(rs.getString("projekt_beschreibung"));
				projekte.add(projekt); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return projekte; 
	}
	
	/**
	 * ermittelt alle Aufgaben aus der Datenbank, die im Namen eine bestimmte Zeichenfolge enthalten
	 * @param p_eingabe
	 * @return List<Aufgabe> 
	 */
	public List<Aufgabe> ermittleAufgabenNachChar(String p_eingabe)
	{
		List<Aufgabe> aufgaben = new ArrayList<Aufgabe>(); 
		Aufgabe aufgabe; 
		Statement stmt = null; 
		String sql;
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT aufgabe_id, aufgabe_name, aufgabe_beschreibung, projekt_name, user_benutzername, aufgabe_deadline, status_name, prioritaet_bezeichnung FROM tblaufgaben "
					+ "LEFT JOIN tblprojekte USING (projekt_id) "
					+ "LEFT JOIN tbluser ON (tblaufgaben.user_id = tbluser.user_id) "
					+ "LEFT JOIN tblstatus USING (status_id) "
					+ "LEFT JOIN tblprioritaet ON (tblaufgaben.prioritaet_id = tblprioritaet.prioritaet_id) "
					+ "WHERE aufgabe_name LIKE '%" + p_eingabe + "%';";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				aufgabe = new Aufgabe(rs.getInt("aufgabe_id")); 
				aufgabe.setName(rs.getString("aufgabe_name"));
				aufgabe.setProjekt(new Projekt(rs.getString("projekt_name")));
				aufgabe.setInitiator(new Nutzer(rs.getString("user_benutzername")));
				aufgabe.setDeadline(rs.getDate("aufgabe_deadline"));
				aufgabe.setBeschreibung(rs.getString("aufgabe_beschreibung"));
				aufgabe.setPrioritaet(rs.getString("prioritaet_bezeichnung"));
				aufgabe.setStatus(rs.getString("status_name"));
				aufgaben.add(aufgabe); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		
		return aufgaben; 
	}
	
	/**
	 * ermittelt alle Kanbans aus der Datenbank, die im Namen eine bestimmte Zeichendfolge enthalten
	 * @param p_eingabe
	 * @return List<Kanban>
	 */
	public List<Kanban> ermittleKanbansNachChar(String p_eingabe)
	{
		List<Kanban> kanbans = new ArrayList<Kanban>(); 
		Kanban kanban; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT kanban_id, kanban_bezeichnung, projekt_name, user_benutzername FROM tblkanban "
					+ "LEFT JOIN tblprojekte USING (kanban_id) "
					+ "LEFT JOIN tbluser USING (user_id) "
					+ "WHERE kanban_bezeichnung LIKE '%" + p_eingabe + "%';"; 
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				kanban = new Kanban(rs.getInt("kanban_id"), rs.getString("kanban_bezeichnung")); 
				kanbans.add(kanban);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace(); 
		}
		
		return kanbans; 
	}
	
	/**
	 * Methode liefert alle Aufgaben aus der Datenbank, die rechtzeitig fertig gestellt wurden
	 * @return List<Aufgabe> 
	 */
	public List<Aufgabe> ermittleAufgabenRechzeitig(Projekt p_projekt)
	{
		List<Aufgabe> aufgabenRechtzeitig = new ArrayList<Aufgabe>(); 
		Aufgabe aufgabe; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT * FROM tblaufgaben WHERE aufgabe_rechtzeitig = 1 AND projekt_id =" + p_projekt.getId() + ";";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				aufgabe = new Aufgabe(rs.getInt("aufgabe_id")); 
				aufgabenRechtzeitig.add(aufgabe);
			}
			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return aufgabenRechtzeitig; 
	}
	
	/**
	 * Methode liefert alle Aufgaben aus der Datenbank, die zu spät fertiggestellt wurden
	 * @return List<Aufgabe> 
	 */
	public List<Aufgabe> ermittleAufgabenVerspaetet(Projekt p_projekt)
	{
		List<Aufgabe> aufgabenVerspaetet = new ArrayList<Aufgabe>(); 
		Aufgabe aufgabe; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT * FROM tblaufgaben WHERE aufgabe_rechtzeitig IS NOT NULL AND aufgabe_rechtzeitig = 0 AND projekt_id =" + p_projekt.getId() + ";";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				aufgabe = new Aufgabe(rs.getInt("aufgabe_id")); 
				aufgabenVerspaetet.add(aufgabe);
			}
			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return aufgabenVerspaetet; 
	}
	
	/**
	 * Methode liefert alle Aufgaben aus der Datenbank, die noch offen sind 
	 * @return List<Aufgabe> 
	 */
	public List<Aufgabe> ermittleAufgabenOffen(Projekt p_projekt)
	{
		List<Aufgabe> aufgabenOffen = new ArrayList<Aufgabe>(); 
		Aufgabe aufgabe; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT * FROM tblaufgaben WHERE aufgabe_rechtzeitig IS NULL AND projekt_id =" + p_projekt.getId() + ";";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				aufgabe = new Aufgabe(rs.getInt("aufgabe_id")); 
				aufgabenOffen.add(aufgabe);
			}
			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return aufgabenOffen; 
	}
	
	/**
	 * Methode liefert Liste aller Projekte, die rechtzeitig fertiggestellt wurden
	 * @return List<Projekt>
	 */
	public List<Projekt> ermittleRechtzeitigeProjekteNachUser(Nutzer p_nutzer)
	{
		List<Projekt> projekte = new ArrayList<Projekt>(); 
		Projekt projekt; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT * FROM tblprojekte WHERE projekt_rechtzeitig = 1 AND user_id=" + p_nutzer.getId() + ";";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				projekt = new Projekt(rs.getInt("projekt_id")); 
				projekte.add(projekt); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return projekte; 
	}
	
	/**
	 * Methode liefert Liste mit Projekten, die verspätet abgegeben wurden
	 * @return List<Projekt>
	 */
	public List<Projekt> ermittleVerspaeteteProjekteNachUser(Nutzer p_nutzer)
	{
		List<Projekt> projekte = new ArrayList<Projekt>(); 
		Projekt projekt; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT * FROM tblprojekte WHERE projekt_rechtzeitig IS NOT NULL AND projekt_rechtzeitig = 0 AND user_id=" + p_nutzer.getId() + ";";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				projekt = new Projekt(rs.getInt("projekt_id")); 
				projekte.add(projekt); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return projekte;
	}
	
	/**
	 * Methode liefert Liste mit Projekten, die noch offen in der Bearbeitung sind
	 * @return List<Projekt> 
	 */
	public List<Projekt> ermittleOffeneProjekteNachUser(Nutzer p_nutzer)
	{
		List<Projekt> projekte = new ArrayList<Projekt>(); 
		Projekt projekt; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement(); 
			sql = "SELECT * FROM tblprojekte WHERE projekt_rechtzeitig IS NULL AND user_id=" + p_nutzer.getId() + ";";
			
			ResultSet rs = stmt.executeQuery(sql); 
			
			while(rs.next())
			{
				projekt = new Projekt(rs.getInt("projekt_id")); 
				projekte.add(projekt); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return projekte;
	}
	
	
	
	/**
	 * Methode liefert Auskunft, ob alle Aufgaben eines Projekts "Done" sind
	 * @param p_projekt
	 * @return boolean
	 */
	private boolean pruefeProjektstand(Projekt p_projekt)
	{
		boolean done = false; 
		List<Aufgabe> aufgaben; 
		int anzahl = 0; 
		
		aufgaben = this.ermittleAufgabenProjekt(p_projekt); 
		
		for(int i = 0; i < aufgaben.size(); i++)
		{
			if(!aufgaben.get(i).getStatus().equals("Done"))
			{
				break;
			}
			
			anzahl++;
		}
		
		if(anzahl == aufgaben.size())
		{
			done = true; 
		}
		
		return done; 
	}
	
	/**
	 * Methode aktualisiert den Projektstand: rechtzeitig fertig gestellt (1), verspätet fertig gestellt (0), noch offen (null)
	 * @param p_projekt
	 * @param p_deadline
	 * @return boolean
	 */
	private boolean aktualisiereProjektstand(Projekt p_projekt)
	{
		boolean erfolgreich = false;
		int rechtzeitig; 
		Statement stmt = null; 
		String sql; 
		
		//Ermitteln, ob Projekt abgeschlossen ist (alle Aufgaben sind done)
		if(this.pruefeProjektstand(p_projekt))
		{
			//Wenn abgeschlossen, dann Deadline mit aktuellem Datum vergleichen und speichern, ob Projekt rechtzeitig (1) oder
			//verspätet (0) fertiggestellt wurde
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
			//Projekt-Deadline ermitteln
			Date deadline = this.ermittleProjektDeadline(p_projekt); 
			
			//System-Datum
			String dateString = sdf.format(new Date()); 
			
			Date date;
			
			try 
			{
				date = sdf.parse(dateString); 
				System.out.println("Systemzeit:" + date.getTime());
				System.out.println("Deadline: " + deadline.getTime());
				
				if(date.getTime() > deadline.getTime())
				{
					rechtzeitig = 0;
				}
				else
				{
					rechtzeitig = 1; 
				}
				
				try
				{
					stmt = this.conn.createStatement(); 
					sql = "UPDATE tblprojekte SET projekt_rechtzeitig =" + rechtzeitig + 
							" WHERE projekt_name='" + p_projekt.getName() + "';"; 
					stmt.execute(sql); 
					erfolgreich = true; 
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
					
				
			} 
			catch (ParseException e1) 
			{
				e1.printStackTrace();
			}
		}
		else
		{
			erfolgreich = true; 
		}
		
		return erfolgreich; 
	}
	
	/**
	 * Ermittelt die Deadline eines Projekts mit Hilfe des Projektnamens 
	 * @param p_projekt
	 * @return Date
	 */
	private Date ermittleProjektDeadline(Projekt p_projekt)
	{
		Date date = null; 
		Statement stmt = null; 
		String sql; 
		
		try
		{
			stmt = this.conn.createStatement();
			sql = "SELECT projekt_deadline FROM tblprojekte WHERE projekt_name ='" + p_projekt.getName() + "';";
			
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next())
			{
				date = rs.getDate("projekt_deadline"); 
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return date;  
	}
	
	

}
