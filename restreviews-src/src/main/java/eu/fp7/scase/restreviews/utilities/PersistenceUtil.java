/*
 * ARISTOTLE UNIVERSITY OF THESSALONIKI
 * Copyright (C) 2015
 * Aristotle University of Thessaloniki
 * Department of Electrical & Computer Engineering
 * Division of Electronics & Computer Engineering
 * Intelligent Systems & Software Engineering Lab
 *
 * Project             : restreviews
 * WorkFile            : 
 * Compiler            : 
 * File Description    : 
 * Document Description: 
* Related Documents	   : 
* Note				   : 
* Programmer		   : RESTful MDE Engine created by Christoforos Zolotas
* Contact			   : christopherzolotas@issel.ee.auth.gr
*/
package eu.fp7.scase.restreviews.utilities;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;



public class PersistenceUtil 
{

	private static final EntityManager oEntityManager = buildEntityManager();
    private static final FullTextEntityManager oFullTextEntityManager = buildFullTextEntityManager();
    
    private static EntityManager buildEntityManager() 
    {
        try 
        {
           return (Persistence.createEntityManagerFactory("search")).createEntityManager();
        }
        catch (Throwable ex) 
        {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial entity manager factory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    private static FullTextEntityManager buildFullTextEntityManager() 
    {
        try 
        {  
        	return Search.getFullTextEntityManager(oEntityManager);
        }
        catch (Throwable ex) 
        {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial entity manager factory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static FullTextEntityManager getFullTextEntityManager()
    {
    	return oFullTextEntityManager;
    }
    
    public static void beginEntityManagerTransaction() 
    {
        oEntityManager.getTransaction().begin();
    }
    
    public static void endEntityManagerTransaction()
    {
    	oEntityManager.getTransaction().commit();
    }

}
