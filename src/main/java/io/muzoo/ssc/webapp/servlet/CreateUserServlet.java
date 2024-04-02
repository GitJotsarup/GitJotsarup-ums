package io.muzoo.ssc.webapp.servlet;

import io.muzoo.ssc.webapp.Routable;
import io.muzoo.ssc.webapp.service.SecurityService;
import io.muzoo.ssc.webapp.service.UserService;
import org.apache.commons.lang.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CreateUserServlet extends HttpServlet implements Routable {

    private SecurityService securityService;

    @Override
    public String getMapping() {
        return "/user/create";
    }

    @Override
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws  ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request);
        if (authorized) {
            // do MVC in here
//            String username = (String) request.getSession().getAttribute("username");
//            UserService userService = UserService.getInstance();

//            request.setAttribute("user", userService.findByUsername(username));

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/create.jsp");
            rd.include(request, response);

            request.getSession().removeAttribute("hasError");
            request.getSession().removeAttribute("message");
        } else {
            // the remove attribute is here to add some xtra precaution
            request.removeAttribute("hasError");
            request.removeAttribute("message");
            response.sendRedirect("/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request);
        if (authorized) {
            // ensure that username and displayName do not contain leading and trailing spaces
            String username = StringUtils.trim((String) request.getParameter ("username"));
            String displayName = StringUtils.trim((String) request.getParameter("displayName"));
            String password = (String) request.getParameter ("password");
            String cpassword = (String) request.getParameter("cpassword");
            UserService userService = UserService.getInstance();
            String errorMessage = null;
            // check if username is valid
            if (userService.findByUsername (username) != null) {
                errorMessage = String.format("Username %s has already been taken.", username);
            }

            // check if displayName is valid
            else if (StringUtils.isBlank (displayName)) {
                errorMessage = "Display Name cannot be blank";
            }
            // check if the password is blank
            else if (StringUtils.isBlank (password)) {
                errorMessage = "Display Name cannot be blank";
            }
            // check if confirmed password is correct
            else if (!StringUtils.equals (password, cpassword)) {
                // confirmed password mismatched
                errorMessage = "The confirm password doesn't match the password";
            }

            if (errorMessage != null) {
                request.getSession().setAttribute("hasError", true);
                request.getSession().setAttribute("message", errorMessage);
            } else{
                // create the user
                try{
                    userService.createUser(username, password, displayName);
                    // if there isn't an error redirect
                    request.getSession().setAttribute("hasError", false);
                    request.getSession().setAttribute("message", String.format("User %s has already exists", username));
                    response.sendRedirect("/");
                } catch (Exception e) {
                    request.getSession().setAttribute("hasError", true);
                    request.getSession().setAttribute("message", e.getMessage());
                }
            }
            // let;s prefill the form
            request.setAttribute("username", username);
            request.setAttribute("displayName", displayName);
            request.setAttribute("password", password);
            request.setAttribute("cpassword", cpassword);

            // if it isn't a success, it will arrive here
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/create.jsp");
            rd.include(request, response);

            request.getSession().removeAttribute("hasError");
            request.getSession().removeAttribute("message");
        } else {
            // the remove attribute is here to add some xtra precaution
            request.removeAttribute("hasError");
            request.removeAttribute("message");
            response.sendRedirect("/login");
        }    }
}
