package fr.upem.hireanemployee.navigation;

import fr.upem.hireanemployee.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Performs checks for session expired, and tells the client's browser to no cache its
 * Web pages (when using the back button for example).
 */
public class RequestFilter extends Logger implements Filter {

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

        // Telling the client's browser to not cache JSF dynamic web pages.
        HttpServletResponse res = (HttpServletResponse) response;
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        res.setDateHeader("Expires", 0); // Proxies.
        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {

    }
}
