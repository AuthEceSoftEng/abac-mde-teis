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


import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import eu.fp7.scase.restreviews.account.JavaaccountModel;
import eu.fp7.scase.restreviews.product.JavaproductModel;
import eu.fp7.scase.restreviews.order.JavaorderModel;
import eu.fp7.scase.restreviews.review.JavareviewModel;

import eu.fp7.scase.restreviews.utilities.authorization.core.ResourceAccessAttribute;
import eu.fp7.scase.restreviews.utilities.authorization.core.ResourceAccessCondition;
import eu.fp7.scase.restreviews.utilities.authorization.core.ResourceAccessPolicy;
import eu.fp7.scase.restreviews.utilities.authorization.core.ResourceAccessPolicySet;
import eu.fp7.scase.restreviews.utilities.authorization.core.ResourceAccessRule;

/* This class follows the singleton pattern in order to build once and provide a unique hibernate session instance*/

public class HibernateUtil{

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory(){
        try {
        /* Create the unique hibernate session. All resource models should be added here.*/
            return new AnnotationConfiguration().configure()
					.addAnnotatedClass(JavaaccountModel.class)
					.addAnnotatedClass(JavaproductModel.class)
					.addAnnotatedClass(JavaorderModel.class)
					.addAnnotatedClass(JavareviewModel.class)
					.addAnnotatedClass(ResourceAccessPolicySet.class)
					.addAnnotatedClass(ResourceAccessPolicy.class)
					.addAnnotatedClass(ResourceAccessRule.class)
					.addAnnotatedClass(ResourceAccessCondition.class)
					.addAnnotatedClass(ResourceAccessAttribute.class)
                    .buildSessionFactory();
        }
        catch (Throwable ex){
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory(){
        return sessionFactory;
    }

}
