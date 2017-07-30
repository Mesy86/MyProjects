package model;

public class Kanban 
{	
	/**
	 * Instanz-Variablen
	 */
	private String kanbanName; 
	private int id; 
	private String projektName; 
	
	/**
	 * Default-Konstruktor
	 */
	public Kanban()
	{
		
	}
	
	/**
	 * überladener Konstruktor
	 * @param p_id
	 */
	public Kanban(int p_id)
	{
		this.id = p_id; 
	}
	
	/**
	 * überladener Konstruktor 
	 * @param p_name
	 */
	public Kanban(String p_name)
	{
		this.kanbanName = p_name; 
	}
	
	/**
	 * überladener Konstruktor
	 * @param p_id
	 * @param p_name
	 */
	public Kanban(int p_id, String p_name)
	{
		this.id = p_id; 
		this.kanbanName = p_name; 
	}
	
	/**
	 * Liefert Kanban-Name
	 * @return String
	 */
	public String getKanbanName() 
	{
		return this.kanbanName;
	}
	
	/**
	 * Setzt Kanban-Name
	 * @param kanbanName
	 */
	public void setKanbanName(String kanbanName) 
	{
		this.kanbanName = kanbanName;
	}
	
	/**
	 * Liefert Datenbank Id
	 * @return int
	 */
	public int getId() 
	{
		return this.id;
	}
	
	/**
	 * Setzt Id
	 * @param id
	 */
	public void setId(int id) 
	{
		this.id = id;
	}

	/**
	 * Liefert Name des zugehörigen Projekts
	 * @return String
	 */
	public String getProjektName() 
	{
		return this.projektName;
	}
	
	/**
	 * Setzt Namen des zugehörigen Projekts
	 * @param projektName
	 */
	public void setProjektName(String projektName) 
	{
		this.projektName = projektName;
	}

}
