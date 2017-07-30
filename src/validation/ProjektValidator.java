package validation;

import java.sql.Connection;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import model.Projekt;
import persistenz.Datenbank;

@FacesValidator("projektValidator")
public class ProjektValidator implements Validator
{
	/**
	 * Instanzvariablen
	 */
	private Datenbank db = new Datenbank(); 
	Connection conn = null; 

	@Override
	public void validate(FacesContext context, UIComponent component, Object object) throws ValidatorException 
	{
		if(object != null)
		{
			String projektname = object.toString(); 
			
			//Prüfen, ob Benutzername bereits in DB
			this.conn = db.verbindungHerstellen(); 
			
			Projekt projekt = this.db.findeProjektMitProjektname(projektname); 
			
			if(projekt != null)
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Projektname bereits vergeben", "Bitte wähle einen anderen"); 
				throw new ValidatorException(msg); 
			}
			this.db.verbindungSchliessen();
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Projektname eingeben", ""); 
			throw new ValidatorException(msg); 
		}
		
	}

}
