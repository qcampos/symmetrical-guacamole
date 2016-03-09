package fr.upem.hireanemployee.navigation;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UrlRewriteFilter implements Filter {

    public UrlRewriteFilter() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest srequest = (HttpServletRequest) request;
        HttpServletResponse sresponse = (HttpServletResponse) response;

        String url = srequest.getRequestURI().trim();

        // Process the written URL in the form
        // http://tld.com/myapp/store/specials/value_item_description
        // Forward to the original page silently without
        // the knowledge of the browser, URL displayed in
        // browser remains the same.
        //
        // In our case, before we forward the page
        // we first make sure the descriptive text part
        // of the URL was not changed by comparing it
        // against the database record and
        // do a redirect if it was changed and then
        // takes us back here eventually.

        if (url.contains("store/specials")) {
            StringBuilder forward = new StringBuilder();
            forward.append("/store.xhtml?");
            forward.append(srequest.getQueryString());
            request.getRequestDispatcher(forward.toString()).forward(request, response);

            // Process access to the original URL in the form
            // http://tld.com/myapp/store_page.xhtml?item=value&subitem=value2
            // This takes another loop to this filter but to the if part of this
            // block.  The browser is aware of the redirect and URL displayed
            // in the browser is rewritten.

        } else if (url.contains("/store_page.xhtml")) {
            sresponse.setStatus(301);
            sresponse.sendRedirect("http://tld.com/myapp/" + rebuildUrl(srequest));
        } else {
            chain.doFilter(request, response);
        }

    }

    String rebuildUrl(HttpServletRequest srequest) {

        final String itemValue = srequest.getParameter("item");
        final String subItemValue = srequest.getParameter("sub_item");
        String description = "retrieve value from database or some where else";

        // In our case, we included itemValue to the rewritten
        // URL because itemValue is the key to our database. We retrieve
        // page data based on itemValue.

        description = itemValue + "-" +
                subItemValue + "-" +
                description.replaceAll("[^a-z0-9]", "_");

        // replaceAll above replaces all non alpha-numeric characters in
        // the description with an underscore.

        return description;
    }


    @Override
    public void destroy() {
        // your code
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // your code
    }

}