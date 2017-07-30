package validation;

import java.sql.Connection;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import model.Nutzer;
import persistenz.Datenbank;

@FacesValidator("benutzernameValidator")
public class BenutzernameValidator implements Validator
{
	/**
	 * Instanzvariablen
	 */
	private Datenbank db = new Datenbank(); 
	Connection conn = null; 
	
	/**
	 * Prüft, ob eingegebener Benutzername bereits in Datenbank vorhanden ist
	 */
	@Override
	public void validate(FacesContext context, UIComponent ui, Object object) throws ValidatorException 
	{
		if(object != null)
		{
			String benutzername = object.toString(); 
			
			//Prüfen, ob Benutzername bereits in DB
			this.conn = db.verbindungHerstellen(); 
			
			Nutzer nutzer = db.findeUserMitBenutzername(benutzername); 
			
			if(nutzer != null)
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Benutzername bereits vergeben", "Bitte wähle einen anderen"); 
				throw new ValidatorException(msg); 
			}
			this.db.verbindungSchliessen();
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Benutzername eingeben", ""); 
			throw new ValidatorException(msg); 
		}
		
	}

}
