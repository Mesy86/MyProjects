package validation;

import java.sql.Connection;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import model.Kanban;
import persistenz.Datenbank;

@FacesValidator("kanbanValidator")
public class KanbanValidator implements Validator
{
	/**
	 * Instanz-Variablen
	 */
	private Datenbank db = new Datenbank(); 
	
	@SuppressWarnings("unused")
	private Connection conn = null; 

	@Override
	public void validate(FacesContext context, UIComponent component, Object object) throws ValidatorException 
	{
		if(object != null)
		{
			String kanbanName = object.toString(); 
			
			//Prüfen, ob Benutzername bereits in DB
			this.conn = db.verbindungHerstellen(); 
			
			Kanban kanban = this.db.findeKanbanMitName(kanbanName); 
			
			if(kanban != null)
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kanban-Name bereits vergeben", "Bitte wähle einen anderen"); 
				throw new ValidatorException(msg); 
			}
			this.db.verbindungSchliessen();
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Kanban-Namen eingeben", ""); 
			throw new ValidatorException(msg); 
		}
		
	}

}
