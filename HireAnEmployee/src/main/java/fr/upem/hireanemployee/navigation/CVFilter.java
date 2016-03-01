package fr.upem.hireanemployee.navigation;

import fr.upem.hireanemployee.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Baxtalou on 01/03/2016.
 */
public class CVFilter extends Logger implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Get the cvViewBean from session attribute
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String pageName = httpRequest.getRequestURI().toString();
        boolean sessionState = httpRequest.isRequestedSessionIdValid();
        log("doFilter - for page " + pageName + " session state " + sessionState);

        // Redirecting to the connexion page if the session is not valid anymore.
        if (!sessionState && !pageName.equals("/hireanemployee/connection.xhtml")) {
            ((HttpServletResponse) response).sendRedirect(httpRequest.getContextPath() + "/" + Constants.CONNECTION);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
