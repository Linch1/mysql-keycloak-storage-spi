package mysql.storage.repo;
import mysql.storage.entity.User;
import mysql.storage.enums.IdentityProvider;
import mysql.storage.enums.Language;
import mysql.storage.enums.UserGenre;
import mysql.storage.main.HibernateFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.keycloak.models.cache.*;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class UserRepo {
	
	private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
	
    public void add(User car) {
        HibernateFactory hibernateFactory = new HibernateFactory();
        Session session = hibernateFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.save(car);
            session.getTransaction().commit();
        } catch (Exception ex) {
            transaction.rollback();
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            session.close();
            hibernateFactory.close();
        }
    }

     public List<User> findAll(){
         return findAll(0, null);
     }

     public List<User> findAll(int start, int max){
         return findAll((Integer)start, (Integer)max);
     }
     
     public List<User> findAll(Integer start, Integer max) {
    	String baseQuery = "SELECT u FROM user u";
     	HibernateFactory hibernateFactory = new HibernateFactory();
     	Session session = hibernateFactory.getSessionFactory().openSession();
     	Transaction transaction = session.beginTransaction();
     	List<User> result = new ArrayList();
     	try {
     		Query q = session.createQuery(baseQuery, User.class);
     		q.setFirstResult(start);
     		if(!(max == null)) q.setMaxResults(max);
     		result = q.getResultList();
     		session.getTransaction().commit();
        } catch (Exception ex) {
        	transaction.rollback();
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            session.close();
            hibernateFactory.close();
        }
     	return result;
     }
     
     
     public List<User> findUsers(String query, Integer start, Integer max) {
   		return findAll().stream()
   		  .filter(user ->  user.getEmail().contains(query))
   		  .collect(Collectors.toList());
     }

     public void getUserByUsername(String username) {

     }

     public void getUserByEmail(String email) {
     }

     public User searchForUserByUsernameOrEmail(String searchString) {
         return searchForUserByUsernameOrEmail(searchString, null, null);
     }

     public User searchForUserByUsernameOrEmail(String searchString, int start, int max) {
         return searchForUserByUsernameOrEmail(searchString, (Integer)start, (Integer)max);
     }

     private User searchForUserByUsernameOrEmail(String searchString, Integer start, Integer max) {
    	log.infov("\nRepo.searchForUserByUsernameOrEmail > 1. Searching users with findAll and Filtering them ");
    
       	
       	User found = findAll().stream()
   			      .filter(user -> user.getEmail().equalsIgnoreCase(searchString))
   			      .findFirst().orElse(new User());
       	
       	log.infov("\nRepo.searchForUserByUsernameOrEmail > Found user " + found.getEmail());
       	return found;

     }
     
     public User findUserById(String id) {
    	 log.infov("\nRepo.findUserById > Getting user with id " + id);
    	 HibernateFactory hibernateFactory = new HibernateFactory();
         Session session = hibernateFactory.getSessionFactory().openSession();
         Transaction transaction = session.beginTransaction();
         User returnedUser = new User();
         try {
        	log.infov("\nRepo.findUserById > Searching..");
         	User user = session.get(User.class, Integer.parseInt(id));
         	if( user == null) { log.infov("\nRepo.findUserById > Returning null user");}
         	else {returnedUser = user;}
         	log.infov("\nRepo.findUserById > Returning some user");
         	session.getTransaction().commit();
         } catch (Exception ex) {
        	 transaction.rollback();
             ex.printStackTrace();
             throw new RuntimeException(ex);
         } finally {
             session.close();
             hibernateFactory.close();
         }
         return returnedUser;
     }

     public User getUserById(String id) {
   	     log.infov("\nRepo.getUserById > [ NOT IMPLEMENTED ] Creating user ");
         return null;
     }

     public User createUser(User user) {
   	     log.infov("\nRepo.createUser > [ NOT IMPLEMENTED ] Creating user ");
         return null;
     }

     public void deleteUser(User user) {
   	     log.infov("\nRepo.deleteUser > [ NOT IMPLEMENTED ] Deleting user ");
     }

     public void close() {
   	     log.infov("\nRepo.close > [ NOT IMPLEMENTED ] closing ");
     }
     
     public int getUsersCount(){
    	log.infov("\nRepo.validateCredentials > Validating given password ");
     	HibernateFactory hibernateFactory = new HibernateFactory();
       	Session session = hibernateFactory.getSessionFactory().openSession();
       	Transaction transaction = session.beginTransaction();
       	Integer returnedCount = 0;
       	try {
       		returnedCount = (Integer) session.createQuery("select count(*) from user").uniqueResult();
       		session.getTransaction().commit();
        } catch (Exception ex) {
        	transaction.rollback();
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            session.close();
            hibernateFactory.close();
        }
       	return returnedCount;
     }

     public User updateUser(User userEntity) {
         return new User();
     }
     
     public boolean validateCredentials(String username, String password) {
    	 log.infov("\nRepo.validateCredentials > Validating " + username);
    	 if(username == null ) return false;
    	 HibernateFactory hibernateFactory = new HibernateFactory();
         Session session = hibernateFactory.getSessionFactory().openSession();
         Transaction transaction = session.beginTransaction();
         boolean returnStatus = false;
         try {
        	 Query q = session.createQuery("select password from user where email=:nickname");
             q.setString("nickname", username);
             String pass = (String) q.list().get(0);
             returnStatus = bCryptPasswordEncoder.matches(password, pass);
             session.getTransaction().commit();
         } catch (Exception ex) {
        	 transaction.rollback();
             ex.printStackTrace();
             throw new RuntimeException(ex);
         } finally {
             session.close();
             hibernateFactory.close();
         }
         return returnStatus;
         
     }
     
     public boolean updateCredentials(String username, String password) {
    	 log.infov("\nRepo.updateCredentials > Generating enc password");
    	 String new_pwd = bCryptPasswordEncoder.encode(password);
    	 log.infov("\nRepo.updateCredentials > Updating password");
    	 
    	 HibernateFactory hibernateFactory = new HibernateFactory();
         Session session = hibernateFactory.getSessionFactory().openSession();
         Transaction transaction = session.beginTransaction();
         try {
        	 Query q = session.createQuery("UPDATE user u SET u.password=:new_pwd WHERE u.email=:username ");
             q.setString("new_pwd", new_pwd);
             q.setString("username", username);
             int count = q.executeUpdate(); 
             session.getTransaction().commit();
             log.infov("\nRepo.updateCredentials > Updated " + count + " Records");
         } catch (Exception ex) {
             transaction.rollback();
             ex.printStackTrace();
             throw new RuntimeException(ex);
         } finally {
             session.close();
             hibernateFactory.close();
         }
         return true;
     }
     
     public User CreateNewUser(String username) {
    	 log.infov("\nRepo.CreateNewUser > Creating new user " + username);
    	 
    	 HibernateFactory hibernateFactory = new HibernateFactory();
         Session session = hibernateFactory.getSessionFactory().openSession();
         Transaction transaction = session.beginTransaction();
         try {
        	 Query q = session.createSQLQuery("INSERT INTO "
        	 		+ "user (created_date, last_modified_date, address, birth_date, city, email, enabled, first_name, genre, identity_provider, language, last_name, nick_name, password, province ) "
        	 		+ "VALUES ( :created_date, :last_modified_date, :address, :birth_date, :city, :email, :enabled, :first_name, :genre, :identity_provider, :language, :last_name, :nick_name, :password, :province )");
             
        	 q.setTimestamp("created_date", new Date());
             q.setTimestamp("last_modified_date", new Date());
             q.setTimestamp("birth_date", new Date());
             q.setString("address", "");
             q.setString("city", "");
             q.setString("email", username);
             q.setBoolean("enabled", false);
             q.setString("first_name", "");
             q.setString("genre", UserGenre.M.getDisplayValue());
             q.setString("identity_provider", IdentityProvider.EMPTY.getDisplayValue());
             q.setString("language", Language.ENG.getDisplayValue());
             q.setString("last_name", "");
             q.setString("nick_name", username);
             
             BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
             q.setString("password", bCryptPasswordEncoder.encode("$$Tefano1111"));
             
             q.setString("province", "");
             
             int count = q.executeUpdate(); 
             session.getTransaction().commit();
             log.infov("\nRepo.updateCredentials > Updated " + count + " Records");
         } catch (Exception ex) {
             transaction.rollback();
             ex.printStackTrace();
             throw new RuntimeException(ex);
         } finally {
             session.close();
             hibernateFactory.close();
         }
         return searchForUserByUsernameOrEmail(username);
     }
     
     public Boolean DeleteUser(UserModel username) {
    	 log.infov("\nRepo.DeleteUser > Deleting user " + username);
    	 
    	 HibernateFactory hibernateFactory = new HibernateFactory();
         Session session = hibernateFactory.getSessionFactory().openSession();
         Transaction transaction = session.beginTransaction();
         try {
        	 Query q = session.createQuery("Delete from user WHERE id=:id");
        	 q.setString("id", StorageId.externalId(username.getId()));
             int count = q.executeUpdate(); 
             session.getTransaction().commit();
             
             log.infov("\nRepo.updateCredentials > Updated " + count + " Records");
         } catch (Exception ex) {
             transaction.rollback();
             ex.printStackTrace();
             throw new RuntimeException(ex);
         } finally {
             session.close();
             hibernateFactory.close();
         }
         return true;
     }
     
     
     public int size() {
         return 0;
     }
}
