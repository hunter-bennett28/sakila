/**
 * Name: DbUtils.java
 * Author: Charles, from Technojeeves.com/joomla/index.pho/free/59-resultset-to-tablemodel
 * 
 * Date: Jul 10, 2012 Revised by Bill Pulling...set the type of the Vectors to <String> and <Object> 
 * REVISED July 12, 2018, to accommodate change in constructor method from JDK 1.8
 *         to JDK 1.9 for the DefaultTableModel object. 
 *         See REVISION note below. 
 *         NOTE: Special thanks to Lynn Koudsi of Section 02 for suggesting the change to a Vector of vectors for the 
 *               method parameter to make it JDK 1.9 compliant.
 *               
 * Date: Jul. 29, 2020, Implemented into the Sakila project and refined
 * Desc: This provides a static method that will convert a ResultSet of data into a 
 * 			TableModel which can easily be displayed in a JTable Component
 */

import java.sql.ResultSet;
import java.sql.ResultSetMetaData; 
import java.util.Vector; 
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
 

public class DbUtils
{
	/*
	 * Method Name: resultSetToTableModel
	 * Purpose: converts a ResultsSet object to a TableModel object
	 *          for use in a JTable object
	 * Accepts: a ResultsSet object from a SQL query
	 * Returns: a TableModel object which can then be used
	 *          as an argument in the constructor method of JTable
	 *          to construct a JTable to display the data.
	 */
     public static TableModel resultSetToTableModel(ResultSet rs)
     {
         try {
        	 //get the metadata for number of columns and column names
             ResultSetMetaData metaData = rs.getMetaData();
             int numberOfColumns = metaData.getColumnCount();
             Vector<String> columnNames = new Vector<String>();
 
            // Get the column names and store in vector
             for (int column = 0; column < numberOfColumns; column++)
             {
                 columnNames.addElement(metaData.getColumnLabel(column + 1));
             }

             // Get all rows data and store in a 2D Vector.              
             Vector<Vector<Object>> rows = new Vector<Vector<Object>>();
 
             while (rs.next())
             {
                 Vector<Object> newRow = new Vector<Object>();
 
                for (int i = 1; i <= numberOfColumns; i++)
                {
                     newRow.addElement(rs.getObject(i));
                }//end for

                 rows.addElement(newRow);
             }//end while

            //return the DefaultTableModel object to the line that called it		
             return new DefaultTableModel(rows, columnNames);
         } catch (Exception e) 
         {
        	 System.out.println("Exception parsing ResultSet to TableModel in DbUtils");
             e.printStackTrace();
             return null;
         }//end catch
     }//end method
 }//end class
