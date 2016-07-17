import org.hibernate.HibernateException; 
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.Reader;
 
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
 
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
/**
 * The application class to read employee data from a JSON file and store the data in the database,
 * and retrieve the data from the database and write to a JSON file
 * @author Serena Lee
 *
 */
public class ManageEmployee {
	private static SessionFactory factory;
	public static void main(String[] args) {
		try{
	         factory = new Configuration().configure().buildSessionFactory();
	      }catch (Throwable ex) { 
	         System.err.println("Failed to create sessionFactory object." + ex);
	         throw new ExceptionInInitializerError(ex); 
	      }
	      ManageEmployee me = new ManageEmployee();
	      try(Reader reader = new InputStreamReader(ManageEmployee.class.getResourceAsStream("FullTimeEmployee.json"), "UTF-8")){
	    	  Gson gson = new GsonBuilder().create();
	    	  FullTime f = gson.fromJson(reader, FullTime.class);
	    	  me.addEmployee(f);
	      }
	      try(Writer writer = new OutputStreamWriter(new FileOutputStream("Output.json") , "UTF-8")){
	            Gson gson = new GsonBuilder().create();
	            me.listEmployees("FT",gson,writer);
	      }

	}
	/* Method to CREATE an employee in the database */
	   public Integer addEmployee(Employee emp){
	      Session session = factory.openSession();
	      Transaction tx = null;
	      Integer employeeID = null;
	      try{
	         tx = session.beginTransaction();
	         employeeID = (Integer) session.save(emp); 
	         tx.commit();
	      }catch (HibernateException e) {
	         if (tx!=null) tx.rollback();
	         e.printStackTrace(); 
	      }finally {
	         session.close(); 
	      }
	      return employeeID;
	   }
	   /* Method to  READ all the employees */
	   public void listEmployees(String type, Gson gson, Writer writer){
	      Session session = factory.openSession();
	      Transaction tx = null;
	      String table = null;
	      try{
	         tx = session.beginTransaction();
	         switch(type){
	         	case "FT": table = "FULLTIME"; break;
	         	case "PT": table = "PARTTIME"; break;
	         	case "I": table = "INTERN"; break;
	         	default: table = "FULLTIME"; break;
	         }
	         List employees = session.createQuery("FROM " + table).list(); 
	         for (Iterator iterator = employees.iterator(); iterator.hasNext();){
	            Employee employee = (Employee) iterator.next(); 
	            gson.toJson(employee.getId(),writer); 
	            gson.toJson(employee.getName(),writer); 
	            gson.toJson(employee.getPositionTitle(),writer); 
	            gson.toJson(employee.getPayType(),writer); 
	         }
	         tx.commit();
	      }catch (HibernateException e) {
	         if (tx!=null) tx.rollback();
	         e.printStackTrace(); 
	      }finally {
	         session.close(); 
	      }
	   }
}