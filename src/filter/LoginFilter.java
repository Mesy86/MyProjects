package filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import view.LoginView;

public class LoginFilter implements Filter
{

	@Override
	public void destroy() 
	{
		// TODO Auto-generated method stub	
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException 
	{
		HttpServletRequest httpRequest = (HttpServletRequest) arg0; 
		HttpServletResponse httpResponse = (HttpServletResponse) arg1; 
		
		LoginView loginView = (LoginView) httpRequest.getSession().getAttribute("loginView");
		System.out.println("Filter:" + loginView);
		
		if(loginView == null || !loginView.isLoggedIn())
		{
			httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.xhtml");
		}
		
		try
		{
			arg2.doFilter(arg0, arg1);
		}
		catch(ServletException e)
		{
			//hier passiert nichts
		}
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
