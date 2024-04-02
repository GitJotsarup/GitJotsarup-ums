/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.muzoo.ssc.webapp.servlet;

import io.muzoo.ssc.webapp.Routable;
import io.muzoo.ssc.webapp.model.User;
import io.muzoo.ssc.webapp.service.SecurityService;
import io.muzoo.ssc.webapp.service.UserService;
import org.mariadb.jdbc.util.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author gigadot
 */
public class DeleteUserServlet extends HttpServlet implements Routable {

    private SecurityService securityService;

    @Override
    public String getMapping() {
        return "/user/delete";
    }

    @Override
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request);
        if (authorized) {
            // do MVC in here
            String username = (String) request.getSession().getAttribute("username");
            UserService userService = UserService.getInstance();

            // just in case there is any error, we will silently suppress the error with nice error message
            try {
                User currentUser = userService.findByUsername(username);
                // We will delete user by username, so we need to get requested username from parameter
                User deletingUser = userService.findByUsername(request.getParameter( "username"));
                // let's prevent deleting own account. the user cant do it from the ui but can still request directly to the server
                if (currentUser.getUsername().equals(deletingUser.getUsername())) {
                    request.getSession().setAttribute("hasError", true);
                    request.getSession().setAttribute("message", "You can't delete your own account");
                } else {
                    if (userService.deleteUserByUsername(deletingUser.getUsername())) {
                        // go to user list page with successful delete message
                        // we will put message in the session
                        // these attributes are added to session so they will persist unless removed from session and db
                        // we need to ensure that they are deleted when they are read next time
                        // since in all cases it will be redirected to home page so we will remove them in home servlet
                        request.getSession().setAttribute("hasError", false);
                        request.getSession().setAttribute("message", String.format("User %s is successfully deleted.", deletingUser.getUsername()));
                    } else {
                        // go to user list page with error delete message
                        request.getSession().setAttribute("hasError", true);
                        request.getSession().setAttribute("message", String.format("Unable to delete User %s", deletingUser.getUsername()));
                    }
                }
            } catch (Exception e) {
                // go to user list page with error delete message
                request.getSession().setAttribute("hasError", true);
                request.getSession().setAttribute("message", String.format("Unable to delete User %s", request.getParameter( "username")));
            }

            response.sendRedirect("/");
        } else {
            response.sendRedirect("/login");
        }
    }
}
