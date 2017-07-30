package validation;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("prioValidator")
public class PrioValidator implements Validator
{

	@Override
	public void validate(FacesContext context, UIComponent component, Object object) throws ValidatorException 
	{
		if(object != null)
		{
			String prio = object.toString(); 
			
			if(prio.equals("Bitte auswählen"))
			{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Priorität auswählen", ""); 
				throw new ValidatorException(msg); 
			}
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Priorität auswählen", ""); 
			throw new ValidatorException(msg); 
		}
	}

}
