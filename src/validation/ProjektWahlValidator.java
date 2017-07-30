package validation;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("projektWahlValidator")
public class ProjektWahlValidator implements Validator
{

	@Override
	public void validate(FacesContext context, UIComponent component, Object object) throws ValidatorException 
	{
		if(object != null)
		{
			String projektname = object.toString(); 
			
			if(projektname.equals("Bitte auswählen"))
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Projekt auswählen", ""); 
				throw new ValidatorException(msg); 
			}
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Projekt auswählen", "");  
			throw new ValidatorException(msg); 
		}
		
	}

}
