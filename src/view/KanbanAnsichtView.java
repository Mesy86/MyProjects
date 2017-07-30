package view;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.DragDropEvent;

import model.Aufgabe;
import model.Konstanten;
import model.Projekt;
import persistenz.Datenbank;

@ManagedBean(name="kanbanAnsichtView")
@RequestScoped
public class KanbanAnsichtView implements Serializable
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
	
	private Projekt selectedProjekt;
	private Aufgabe selectedAufgabe; 
	
	private List<Aufgabe> aufgaben; 
	private List<Aufgabe> backlog; 
	private List<Aufgabe> toDo; 
	private List<Aufgabe> inProgress; 
	private List<Aufgabe> testing; 
	private List<Aufgabe> done; 
	
	private Datenbank db = new Datenbank(); 
	@SuppressWarnings("unused")
	private Connection conn = null; 
	
	
	@PostConstruct
	public void init()
	{
		//Listen füllen und selectedProject ermitteln
		//selectedProject auslesen
		this.selectedProjekt = (Projekt) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("Suchergebnis"); 
		
		//DB-Verbindung herstellen
		if(this.selectedProjekt != null)
		{
			this.conn = this.db.verbindungHerstellen(); 
			
			//Listen füllen
			this.aufgaben = this.db.ermittleAufgabenProjekt(this.selectedProjekt);
			
			this.backlog = this.db.ermittleBacklog(this.selectedProjekt); 
			this.toDo = this.db.ermittleToDo(this.selectedProjekt); 
			this.inProgress = this.db.ermittleInProgress(this.selectedProjekt); 
			this.testing = this.db.ermittleTesting(this.selectedProjekt);
			this.done = this.db.ermittleDone(this.selectedProjekt); 
			
			this.db.verbindungSchliessen();
		}
	}
	
	/**
	 * Default-Konstruktor
	 */
	public KanbanAnsichtView()
	{
		
	}
	
	/**
	 * Methode verbrennt eine Aufgabe, die erledigt ist und setzt sie inaktiv
	 */
	public void burn()
	{
		boolean erfolgreich = false; 
		this.conn = this.db.verbindungHerstellen(); 
		
		//Datenbankzugriff und alle Aufgaben der liste Bearbeitungsstand bearbeiten
		for(int i = 0; i < this.done.size(); i++)
		{
			if(this.db.aktualisiereBearbeitungsstand(this.done.get(i), "Burn"))
			{
				//Aufgabe archivieren
				if(this.db.archiviereAufgabe(this.done.get(i)))
				{
					erfolgreich = true; 
				}
			}
			else
			{
				erfolgreich = false; 
			}
		}
		
		if(erfolgreich)
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aufgaben wurden verbrannt", "Aufgaben nicht mehr verfügbar");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aufgaben konnten nicht verbrannt werden", "Fehler");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		this.db.verbindungSchliessen();
		
	}
	
	/**
	 * Methode nimmt alle Drop-Events entgegen, ermittelt das aktuelle Feld der Aufgabe
	 * und leitet die Verarbeitung entsprechend der Richtung des Ziehens weiter
	 * 
	 */
	@Asynchronous
	public synchronized void onDrop(DragDropEvent ddEvent)
	{
		//Zuteilung zu richtiger Funktion
		String dragId = ddEvent.getDragId(); 
		String dropId = ddEvent.getDropId(); 
		
		if(dragId.contains("availableTask"))
		{
			if(!dropId.contains("backlogId") && !dropId.contains("doneId"))
			{
				//prüfen, ob WiP von Zielfeld noch nicht erreicht
				//Wenn noch frei, dann Methode zum Zug aufrufen
				if(dropId.contains("toDoId"))
				{
					if(this.toDo.size() < Konstanten.WIPTODO)
					{
						this.onTaskDropBacklogTodo(ddEvent);
					}
					else
					{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Grenzanzahl der Aufgaben in 'To Do' bereits erreicht", "Bitte nur " + Konstanten.WIPTODO + " Elemente in 'To Do' halten"); 
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				}
				else if(dropId.contains("inProgressId"))
				{
					if(this.inProgress.size() < Konstanten.WIPINPROGRESS)
					{
						this.onTaskDropInProgress(ddEvent);
					}
					else
					{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Grenzanzahl der Aufgaben in 'In Progress' bereits erreicht", "Bitte nur " + Konstanten.WIPINPROGRESS + " Elemente in 'In Progress' halten"); 
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				}
				else
				{
					if(this.testing.size() < Konstanten.WIPTESTING)
					{
						this.onTaskDropTesting(ddEvent);
					}
					else
					{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Grenzanzahl der Aufgaben in 'Testing' bereits erreicht", "Bitte nur " + this.testing + " Elemente in 'Testing' halten"); 
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				}
			}
			else
			{
				if(dropId.contains("doneId"))
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aufgabe darf nicht direkt in Done verschoben werden", "Erst in Testing schieben"); 
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				else
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Keine Änderung vorgenommen", "Fehler");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				
			}
		}
		else if(dragId.contains("toDoTaskId"))
		{
			if(!dropId.contains("backlogId") && !dropId.contains("toDoId") && !dropId.contains("doneId"))
			{
				//prüfen, ob WiP noch nicht erreicht
				if(dropId.contains("inProgressId"))
				{
					if(this.inProgress.size() < Konstanten.WIPINPROGRESS)
					{
						this.onTaskDropInProgress(ddEvent);
					}
					else
					{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Grenzanzahl der Aufgaben in 'In Progress' bereits erreicht", "Bitte nur " + Konstanten.WIPINPROGRESS + " Elemente in 'In Progress' halten"); 
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				}
				else
				{
					if(this.testing.size() < Konstanten.WIPTESTING)
					{
						this.onTaskDropTesting(ddEvent);
					}
					else
					{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Grenzanzahl der Aufgaben in 'Testing' bereits erreicht", "Bitte nur " + Konstanten.WIPTESTING + " Elemente in 'Testing' halten"); 
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				}

			}
			else
			{
				if(dropId.contains("toDoId"))
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Keine Änderung vorgenommen", "Fehler");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				else if(dropId.contains("backlogId"))
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aufgabe darf nicht zurück in Backlog geschoben werden", "Fehler");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				else
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aufgabe darf nicht direkt in Done verschoben werden", "Erst in Testing schieben"); 
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				
			}
			
		}
		else if(dragId.contains("inProgressTaskId"))
		{
			if(dropId.contains("testingId"))
			{
				//prüfen, ob WiP noch nicht erreicht
				if(this.testing.size() < Konstanten.WIPTESTING)
				{
					this.onTaskDropTesting(ddEvent);
				}
				else
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Grenzanzahl der Aufgaben in 'Testing' bereits erreicht", "Bitte nur " + Konstanten.WIPTESTING + " Elemente in 'Testing' halten"); 
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				
			}
			else
			{
				if(dropId.contains("inProgressId"))
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Keine Änderung vorgenommen", "Fehler");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				else if(dropId.contains("doneId"))
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aufgabe darf nicht direkt in Done verschoben werden", "Erst in Testing schieben"); 
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				else if(dropId.contains("toDoId"))
				{
					//Methoden-Aufruf zurück ziehen
					if(this.toDo.size() < Konstanten.WIPTODO)
					{
						this.onTaskDropToDoBack(ddEvent); 
					}
					else
					{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Grenzanzahl der Aufgaben in 'To Do' bereits erreicht", "Bitte nur " + Konstanten.WIPTODO + " Elemente in 'In Progress' halten"); 
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}
					
				}
				else
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aufgabe darf nur in To Do zurück geschoben werden", "Fehler");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				
			}
			
		}
		else if(dragId.contains("testingTaskId"))
		{
			if(!dropId.contains("doneId") && !dropId.contains("testingId"))
			{
				if(dropId.contains("toDoId"))
				{
					//this.onTaskDropInProgressBack(ddEvent);
					//prüfen, ob WiP noch nicht erreicht
					if(this.toDo.size() < Konstanten.WIPTODO)
					{
						this.onTaskDropToDoBack(ddEvent);
					}
					else
					{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Grenzanzahl der Aufgaben in 'To Do' bereits erreicht", "Bitte nur " + Konstanten.WIPTODO + " Elemente in 'In Progress' halten"); 
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}
					
				}
				else
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aufgabe darf nur in To Do zurück geschoben werden", "Fehler");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				
			}
			else
			{
				if(dropId.contains("doneId"))
				{
					this.onTaskDropDone(ddEvent);
				}
				else
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Keine Änderung vorgenommen", "Fehler");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			
			}
		}
		else
		{
			this.onTaskDropFail();
		}
	}
	
	/**
	 * Methode nimmt in Listen und in Datenbank Verschiebung der Aufgabe von Backlog in To Do vor
	 * @param ddEvent
	 */
	private void onTaskDropBacklogTodo(DragDropEvent ddEvent)
	{
		//Aufgabe ermitteln
		//String DrapId bei : splitten
		String[] elemente = ddEvent.getDragId().split("[:]"); 
		
		Aufgabe aufgabe = this.backlog.get(Integer.parseInt(elemente[2])); 
		
		if(aufgabe.getInitiator().getBenutzername().equals(this.benutzername))
		{
			this.toDo.add(aufgabe); 
			this.backlog.remove(aufgabe); 
			
			//Datenbank anpassen aufgabe
			this.conn = this.db.verbindungHerstellen(); 
			
			if(this.db.aktualisiereBearbeitungsstand(aufgabe, "To Do"))
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bearbeitungsstand wurde geändert", "Aufgabe: " + aufgabe.getName());
				FacesContext.getCurrentInstance().addMessage(null, msg);
				
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bearbeitungsstand wurde nicht geändert", "Aufgabe: " + aufgabe.getName());
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			this.db.verbindungSchliessen();
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Bearbeitungsstand nicht geändert", "nur eigene Aufgaben dürfen verschoben werden");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		
	}
	
	/**
	 * Methode nimmt in Listen und Datenbank Verschiebung zu In Progress vor
	 * @param ddEvent
	 */
	private void onTaskDropInProgress(DragDropEvent ddEvent)
	{
		String dragId = ddEvent.getDragId(); 
		Aufgabe aufgabe = null; 
		
		//Aufgabe ermitteln
		//String DrapId bei : splitten
		String[] elemente = dragId.split("[:]");  
		
		if(dragId.contains("availableTask"))
		{
			aufgabe = this.backlog.get(Integer.parseInt(elemente[2]));
			
			if(aufgabe.getInitiator().getBenutzername().equals(this.benutzername))
			{
				this.inProgress.add(aufgabe);
				this.backlog.remove(aufgabe);
			}
			 
		}
		else if(dragId.contains("toDoTaskId"))
		{
			aufgabe = this.toDo.get(Integer.parseInt(elemente[2]));
			
			if(aufgabe.getInitiator().getBenutzername().equals(this.benutzername))
			{
				this.inProgress.add(aufgabe);
				this.toDo.remove(aufgabe); 
			}
			
		}
		
		if(aufgabe.getInitiator().getBenutzername().equals(this.benutzername))
		{
			//Datenbank anpassen aufgabe
			this.conn = this.db.verbindungHerstellen(); 
			
			if(this.db.aktualisiereBearbeitungsstand(aufgabe, "In Progress"))
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bearbeitungsstand wurde geändert", "Aufgabe: " + aufgabe.getName());
				FacesContext.getCurrentInstance().addMessage(null, msg);
				
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bearbeitungsstand wurde nicht geändert", "Aufgabe: " + aufgabe.getName());
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			this.db.verbindungSchliessen();
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Bearbeitungsstand wurde nicht geändert", "Nur eigene Aufgaben dürfen verschoben werden");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
	}
	
	/**
	 * Methode nimmt in Listen und Datenbank Verschiebung zu Testing vor
	 * @param ddEvent
	 */
	private void onTaskDropTesting(DragDropEvent ddEvent)
	{
		String dragId = ddEvent.getDragId(); 
		Aufgabe aufgabe = null; 
		
		//Aufgabe ermitteln
		//String DrapId bei : splitten
		String[] elemente = dragId.split("[:]");  
		
		if(dragId.contains("availableTask"))
		{
			aufgabe = this.backlog.get(Integer.parseInt(elemente[2]));
			
			if(aufgabe.getInitiator().getBenutzername().equals(this.benutzername))
			{
				this.testing.add(aufgabe);
				this.backlog.remove(aufgabe); 
			}
			
		}
		else if(dragId.contains("toDoTaskId"))
		{
			aufgabe = this.toDo.get(Integer.parseInt(elemente[2]));
			
			if(aufgabe.getInitiator().getBenutzername().equals(this.benutzername))
			{
				this.testing.add(aufgabe);
				this.toDo.remove(aufgabe); 
			}
			
		}
		else if(dragId.contains("inProgressTaskId"))
		{
			aufgabe = this.inProgress.get(Integer.parseInt(elemente[2]));
			
			if(aufgabe.getInitiator().getBenutzername().equals(this.benutzername))
			{
				this.testing.add(aufgabe); 
				this.inProgress.remove(aufgabe); 
			}
			
		}
		
		if(aufgabe.getInitiator().getBenutzername().equals(this.benutzername))
		{
			//Datenbank anpassen aufgabe
			this.conn = this.db.verbindungHerstellen(); 
			
			if(this.db.aktualisiereBearbeitungsstand(aufgabe, "Testing"))
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bearbeitungsstand wurde geändert", "Aufgabe: " + aufgabe.getName());
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bearbeitungsstand wurde nicht geändert", "Aufgabe: " + aufgabe.getName());
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			this.db.verbindungSchliessen(); 
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Bearbeitungsstand wurde nicht geändert", "Nur eigene Aufgaben dürfen verschoben werden");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
	}
	
	/**
	 * Methode nimmt in Listen und in Datenbank Verschiebung der Aufgabe von Testing in Done vor
	 * @param ddEvent
	 */
	private void onTaskDropDone(DragDropEvent ddEvent)
	{
		//Aufgabe ermitteln
		//String DrapId bei : splitten
		String[] elemente = ddEvent.getDragId().split("[:]"); 
		
		Aufgabe aufgabe = this.testing.get(Integer.parseInt(elemente[2])); 
		
		
		if(aufgabe.getInitiator().getBenutzername().equals(this.benutzername))
		{
			this.done.add(aufgabe); 
			this.testing.remove(aufgabe); 
			
			//Datenbank anpassen aufgabe
			this.conn = this.db.verbindungHerstellen(); 
			
			if(this.db.aktualisiereBearbeitungsstand(aufgabe, "Done"))
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bearbeitungsstand wurde geändert", "Aufgabe: " + aufgabe.getName());
				FacesContext.getCurrentInstance().addMessage(null, msg);
			
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bearbeitungsstand wurde nicht geändert", "Aufgabe: " + aufgabe.getName());
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			this.db.verbindungSchliessen();
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Bearbeitungsstand wurde nicht geändert", "Nur eigene Aufgaben dürfen verschoben werden");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
	}
	
	/**
	 * Methode nimmt in Listen und Datenbank Verschiebung zurück in To Do vor
	 * @param ddEvent
	 */
	private void onTaskDropToDoBack(DragDropEvent ddEvent)
	{
		String dragId = ddEvent.getDragId(); 
		Aufgabe aufgabe = null; 
		
		//Aufgabe ermitteln
		//String DrapId bei : splitten
		String[] elemente = dragId.split("[:]");  
		
		if(dragId.contains("inProgressTaskId"))
		{
			aufgabe = this.inProgress.get(Integer.parseInt(elemente[2]));
			
			if(aufgabe.getInitiator().getBenutzername().equals(this.benutzername))
			{
				this.toDo.add(aufgabe);
				this.inProgress.remove(aufgabe); 
			}
			
		}
		else if(dragId.contains("testingTaskId"))
		{
			aufgabe = this.testing.get(Integer.parseInt(elemente[2]));
			
			if(aufgabe.getInitiator().getBenutzername().equals(this.benutzername))
			{
				this.toDo.add(aufgabe);
				this.testing.remove(aufgabe); 
			}
			
		}
		
		if(aufgabe.getInitiator().getBenutzername().equals(this.benutzername))
		{
			//Datenbank anpassen aufgabe
			this.conn = this.db.verbindungHerstellen(); 
			
			if(this.db.aktualisiereBearbeitungsstand(aufgabe, "To Do"))
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bearbeitungsstand wurde geändert", "Aufgabe: " + aufgabe.getName());
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			else
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bearbeitungsstand wurde nicht geändert", "Aufgabe: " + aufgabe.getName());
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			this.db.verbindungSchliessen();
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Bearbeitungsstand wurde nicht geändert", "Nur eigene Aufgaben dürfen verschoben werden");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		
	}
	
	/**
	 * Methode wird aufgerufen, wenn eine Aufgabe in ein falsches Feld gezogen wird
	 */
	private void onTaskDropFail()
	{
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "falsches Drop-Feld", "Aufgabe kann nur in das darauffolgende Feld gezogen werden");
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	/**
	 * Liefert ausgewähltes Projekt
	 * @return Projekt
	 */
	public Projekt getSelectedProjekt() 
	{
		return this.selectedProjekt;
	}
	
	/**
	 * Setzt ausgewähltes Projekt
	 * @param selectedProjekt
	 */
	public void setSelectedProjekt(Projekt selectedProjekt) 
	{
		this.selectedProjekt = selectedProjekt;
	}
	
	/**
	 * Liefert alle Aufgaben des Projekts
	 * @return List<Aufgabe> 
	 */
	public List<Aufgabe> getAufgaben() 
	{
		return this.aufgaben;
	}
	
	/**
	 * Setzt alle Aufgaben 
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
	 * Setzt Aufgaben des Projekts im Backlog
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
	public List<Aufgabe> getToDo() 
	{
		return this.toDo;
	}
	
	/**
	 * Setzt Aufgaben des Projekts in To Do
	 * @param toDo
	 */
	public void setToDo(List<Aufgabe> toDo) 
	{
		this.toDo = toDo;
	}
	
	/**
	 * Liefert alle Aufgaben des Projekts aus In Progress
	 * @return List<Aufgabe> 
	 */
	public List<Aufgabe> getInProgress() 
	{
		return this.inProgress;
	}
	
	/**
	 * Setzt Aufgaben des Projekts in In Progress
	 * @param inProgress
	 */
	public void setInProgress(List<Aufgabe> inProgress) 
	{
		this.inProgress = inProgress;
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
	 * Setzt Aufgaben des Projekts in Testing
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
	 * Setzt Aufgaben des Projekts in Done
	 * @param done
	 */
	public void setDone(List<Aufgabe> done) 
	{
		this.done = done;
	}
	
	/**
	 * Liefert ausgewählte Aufgabe
	 * @return Aufgabe
	 */
	public Aufgabe getSelectedAufgabe() 
	{
		return this.selectedAufgabe;
	}
	
	/**
	 * Setzt Aufgabe
	 * @param selectedAufgabe
	 */
	public void setSelectedAufgabe(Aufgabe selectedAufgabe) 
	{
		this.selectedAufgabe = selectedAufgabe;
	}
	
	/**
	 * Liefert Benutzername des aktuellen Users
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
