package mysql.storage.repo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.extern.jbosslog.JBossLog;
import mysql.storage.entity.User;
import mysql.storage.main.HibernateFactory;

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
     	try {
     		Query q = session.createQuery(baseQuery, User.class);
     		q.setFirstResult(start);
     		if(!(max == null)) q.setMaxResults(max);
            return q.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            session.close();
        }
     }
     
     
     public List<User> findUsers(String query, Integer start, Integer max) {
   		return findAll().stream()
   		  .filter(user -> user.getNickName().contains(query) || user.getEmail().contains(query))
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
   			      .filter(user -> user.getNickName().equalsIgnoreCase(searchString) || user.getEmail().equalsIgnoreCase(searchString))
   			      .findFirst().orElse(new User());
       	
       	log.infov("\nRepo.searchForUserByUsernameOrEmail > Found user " + found.getNickName());
       	return found;

     }
     
     public User findUserById(String id) {
    	 HibernateFactory hibernateFactory = new HibernateFactory();
         Session session = hibernateFactory.getSessionFactory().openSession();
         try {
         	 User user = session.get(User.class, Integer.parseInt(id));
             return user;
         } catch (Exception ex) {
             ex.printStackTrace();
             throw new RuntimeException(ex);
         } finally {
             session.close();
         }
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
       	try {
            return (Integer) session.createQuery("select count(*) from user").uniqueResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            session.close();
        }
     }

     public User updateUser(User userEntity) {
         return new User();
     }
     
     public boolean validateCredentials(String username, String password) {
    	 log.infov("\nRepo.validateCredentials > Validating " + username);
    	 HibernateFactory hibernateFactory = new HibernateFactory();
         Session session = hibernateFactory.getSessionFactory().openSession();
         try {
        	 Query q = session.createQuery("select password from user where nick_name=:nickname");
             q.setString("nickname", username);
             String pass = (String) q.list().get(0);
             return bCryptPasswordEncoder.matches(password, pass);
         } catch (Exception ex) {
             ex.printStackTrace();
             throw new RuntimeException(ex);
         } finally {
             session.close();
         }
         
     }
     
     public boolean updateCredentials(String username, String password) {
    	 log.infov("\nRepo.updateCredentials > Generating enc password");
    	 String new_pwd = bCryptPasswordEncoder.encode(password);
    	 log.infov("\nRepo.updateCredentials > Updating password");
    	 
    	 HibernateFactory hibernateFactory = new HibernateFactory();
         Session session = hibernateFactory.getSessionFactory().openSession();
         Transaction transaction = session.beginTransaction();
         try {
        	 Query q = session.createQuery("UPDATE user u SET u.password=:new_pwd WHERE u.nickName=:username ");
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
         }
         return true;
     }
     
     
     public int size() {
         return 0;
     }
}
