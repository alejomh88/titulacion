/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package ec.edu.utmachala.titulacion.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.text.ParseException;  

/**
 *
 * @author paae_
 */
public class UtilityJUnitTest {
    
    public UtilityJUnitTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
     public void convertirDateATextoTest() {
         //System.out.println(UtilsDate.convertirDateATexto(new Date()));
    	 SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", new Locale("ES"));  
    	 Date date = new Date();
    	    try {  
    	        date = formatter.parse("01/07/2023");  
    	        System.out.println("Date is: "+date);  
    	    } catch (ParseException e) {e.printStackTrace();}  
         assertEquals("un días del mes de julio de dos mil ventitres", UtilsDate.convertirDateATexto(date));
     }
}
