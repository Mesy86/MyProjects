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

@FacesValidator("emailValidator")
public class EmailValidator implements Validator
{
	/**
	 * Instanz-Variablen
	 */
	private Datenbank db = new Datenbank(); 
	
	@SuppressWarnings("unused")
	private Connection conn = null; 

	@Override
	public void validate(FacesContext context, UIComponent ui, Object object) throws ValidatorException 
	{
		if(object != null)
		{
			String mail = object.toString(); 
			
			this.conn = this.db.verbindungHerstellen(); 
			
			Nutzer nutzer = this.db.findeUserMitEmail(mail); 
			
			if(nutzer != null)
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email-Adresse existiert bereits", "Bitte Email überprüfen"); 
				throw new ValidatorException(msg); 
			}
			
			this.db.verbindungSchliessen();
		}
		
	}

}
