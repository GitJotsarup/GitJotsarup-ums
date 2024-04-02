package io.muzoo.ssc.webapp.service;

import io.muzoo.ssc.webapp.model.User;
import lombok.Getter;
import lombok.Setter;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserService is used in too many places and we only need one instance of it so we will make it singleton
 */

@Setter
public class UserService {

    private static final String INSERT_USER_SQL = "INSERT INTO tbl_user (username, password, display_name) VALUES (?, ?, ?);";
    private static final String SELECT_USER_SQL = "SELECT * FROM tbl_user WHERE username = ?;";
    private static final String SELECT_ALL_USERS_SQL = "SELECT * FROM tbl_user;";
    private static final String DELETE_USER_SQL = "DELETE FROM tbl_user WHERE username = ?;";
    private static final String UPDATE_USER_SQL = "UPDATE tbl_user SET display_name =? WHERE username = ?;";
    private static final String UPDATE_USER_PASSWORD_SQL = "UPDATE tbl_user SET password =? WHERE username = ?;";





    @Setter
    private DatabaseConnectionService databaseConnectionService;

    private static UserService service;

    private UserService() {
    }

    public static UserService getInstance() {
        if (service == null) {
            service = new UserService();
            service.setDatabaseConnectionService(DatabaseConnectionService.getInstance());
        }
        return service;
    }

    // creating new users
    public void createUser(String username, String password, String displayName) throws UserServiceException {
        try(
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(INSERT_USER_SQL);
                ) {
            // setting all columns
            ps.setString(1, username);
            ps.setString(2, BCrypt.hashpw(password, BCrypt.gensalt()));
            ps.setString(3, displayName);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new UsernameNotUniqueException(String.format("Username %s has already been taken", username));
        } catch (SQLException throwables) {
            throw new UserServiceException(throwables.getMessage());
        }
    }

    // find by username
    public User findByUsername(String username) {
        try(Connection connection = databaseConnectionService.getConnection();
            PreparedStatement ps = connection.prepareStatement(SELECT_USER_SQL);
            ) {
            // setting all columns
            ps.setString(1, username);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            return new User(
                    resultSet.getLong("id"),
                    resultSet.getString("username"),
                    resultSet.getString("password"),  // it'll show the hashed one
                    resultSet.getString("display_name")
            );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    // finds all usernames, never null
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try(
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(SELECT_ALL_USERS_SQL);
                ) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                users.add(new User
                        (
                                resultSet.getLong("id"),
                                resultSet.getString("username"),
                                resultSet.getString("password"),
                                resultSet.getString("display_name")
                        ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return users;
    }

    // deleting users
    public boolean deleteUserByUsername(String username) {
        try (
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(DELETE_USER_SQL);
        ) {
            // setting all columns
            ps.setString(1, username);
            int deleteCount = ps.executeUpdate();
            connection.commit();
            return deleteCount > 0;
        } catch (SQLException throwables) {
            return false;
        }
    }

    // update user by user id. can change their display name
    public void updateUserbyUsername(String username, String displayName) throws UserServiceException {
        try(
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(UPDATE_USER_SQL);
        ) {
            // setting all columns
            ps.setString(1, displayName);
            ps.setString(2, username);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException throwables) {
            throw new UserServiceException(throwables.getMessage());
        }
    }

    public void changePassword(String username, String newPassword) throws UserServiceException {
        try(
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(UPDATE_USER_PASSWORD_SQL);
        ) {
            // setting all columns
            ps.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            ps.setString(2, username);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException throwables) {
            throw new UserServiceException(throwables.getMessage());
        }
    }

    //    public static void main(String[] args) {
//        UserService userService = new UserService();
//        userService.setDatabaseConnectionService(new DatabaseConnectionService());
//        List<User> users = userService.findAll();
//        for (User user : users) {
//            System.out.println(user.getUsername());
//        }
//    }
}


//    public static void main(String[] args) throws UserServiceException {
//        UserService userService = new UserService();
//        userService.setDatabaseConnectionService(new DatabaseConnectionService());
//        userService.createUser("jot2", "hardpass", "jsn");
//    }

//    public static void main(String[] args) {
//        UserService userService = new UserService();
//        userService.setDatabaseConnectionService(new DatabaseConnectionService());
//        User user = userService.findByUsername("jot2");
//        System.out.println(user.getDisplayName());
//    }
