package validation;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("passwortValidator")
public class PasswortValidator implements Validator
{

	@Override
	public void validate(FacesContext context, UIComponent component, Object object) throws ValidatorException 
	{
		if(object != null)
		{
			String passwort = object.toString(); 
			
			System.out.println("Passwort: " + passwort);
			
			UIInput confirmation = (UIInput)component.getAttributes().get("passwortWdh"); 
			
			if(confirmation != null)
			{
				String passwortWdh = confirmation.getSubmittedValue().toString(); 
				
				System.out.println("Wiederholung: " + passwortWdh);
				
				if(!passwort.equals(passwortWdh))
				{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Passwörter stimmen nicht überein", "Bitte Passwörter prüfen"); 
					throw new ValidatorException(msg); 
				}
			}
		}
		
	}
	

}
