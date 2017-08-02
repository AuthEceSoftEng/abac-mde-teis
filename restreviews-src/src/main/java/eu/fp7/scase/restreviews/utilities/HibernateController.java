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


import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.sql.*;
import eu.fp7.scase.restreviews.utilities.authorization.core.ResourceAccessAttribute;
import eu.fp7.scase.restreviews.utilities.authorization.core.ResourceAccessCondition;
import eu.fp7.scase.restreviews.utilities.authorization.core.ResourceAccessPolicy;
import eu.fp7.scase.restreviews.utilities.authorization.core.ResourceAccessPolicySet;
import eu.fp7.scase.restreviews.utilities.authorization.core.ResourceAccessRule;
import eu.fp7.scase.restreviews.utilities.authorization.enums.Action;
import eu.fp7.scase.restreviews.utilities.authorization.enums.AttributeCategory;
import eu.fp7.scase.restreviews.utilities.authorization.enums.CombiningAlgorithmEnum;
import eu.fp7.scase.restreviews.utilities.authorization.enums.OperatorEnum;
import eu.fp7.scase.restreviews.utilities.authorization.enums.RuleType;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;

import eu.fp7.scase.restreviews.account.JavaaccountModel;
import eu.fp7.scase.restreviews.product.JavaproductModel;
import eu.fp7.scase.restreviews.order.JavaorderModel;
import eu.fp7.scase.restreviews.review.JavareviewModel;

/* HibernateController class is responsible to handle the low level activity between Hibernate and the service database.
 You may not alter existing functions, or the service may not function properly.
 Should you need more functions these could be added at the end of this file.
 You may add any exception handling to existing and/or new functions of this file.*/

public class HibernateController{

    private static HibernateController oHibernateController = new HibernateController();

    /* Since the class follows the singleton design pattern its constructor is kept private. The unique instance of it is accessed through its public API "getHibernateControllerHandle()".*/
    private HibernateController(){
		initializeAuthorizationTables();
	}

    /* Since this class follows the singleton design pattern, this function offers to the rest of the system a handle to its unique instance.*/
    public static HibernateController getHibernateControllerHandle(){
        return oHibernateController;
    }

	/* This function performs the actual authentication activity by looking up in the database wether the request's user is an authenticated user*/
	 public JavaaccountModel authenticateUser(JavaaccountModel oJavaaccountModel)
	 {
		 try
		 {
			//create a new session and begin the transaction
		    Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
			Transaction hibernateTransaction = hibernateSession.beginTransaction();
			
			//create the query in HQL language
			String strQuery = String.format("FROM JavaaccountModel WHERE (email = '%s' AND password = '%s')", oJavaaccountModel.getemail() , oJavaaccountModel.getpassword());
			Query  hibernateQuery = hibernateSession.createQuery(strQuery);
			
			oJavaaccountModel = null;
			
			//retrieve the unique result, if there is a result at all
			oJavaaccountModel = (JavaaccountModel) hibernateQuery.uniqueResult();
			
			if(oJavaaccountModel == null)
			{
	    		throw new WebApplicationException(Response.Status.UNAUTHORIZED);
			}
			
			//commit and terminate the session
			hibernateTransaction.commit();
			hibernateSession.close();
			
			//return the JavaaccountModel of the authenticated user, or null if authentication failed
			return oJavaaccountModel ;
		}
		catch (HibernateException exception)
		{
			System.out.println(exception.getCause());

			ResponseBuilderImpl builder = new ResponseBuilderImpl();
			builder.status(Response.Status.BAD_REQUEST);
			builder.entity(String.format("%s",exception.getCause()));
			Response response = builder.build();
			throw new WebApplicationException(response);
		}
	 }

    /* This function handles the low level JPA activities so as to add a new account resource to the service database.*/
    public JavaaccountModel postaccount(JavaaccountModel oJavaaccountModel){

    	/* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        /* Insert the new account to database*/
        int accountId = (Integer) hibernateSession.save(oJavaaccountModel);
		oJavaaccountModel.setaccountId(accountId);

        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();

        /* Return the JavaaccountModel with updated accountId*/
        return oJavaaccountModel;
    }
	
    /* This function handles the low level hibernate activities so as to update an existing account resource of the service database.*/
    public JavaaccountModel putaccount(JavaaccountModel oJavaaccountModel, String strOptionalUpdateRelations, String strOptionalUpdateParent, String strOptionalRelationName, String strOptionalAddRelation, Integer iOptionalResourceId){

    	/* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        //if the relations of this resource should also be updated
        if(strOptionalUpdateRelations != null){
            if(strOptionalUpdateRelations.equalsIgnoreCase("true")){
                if(strOptionalUpdateParent.equalsIgnoreCase("true")){//a parent relation must be updated
                }
                else{ //else a child relation must be updated
                    if(strOptionalRelationName.equalsIgnoreCase("product")){//if towards product is the required child relation to be updated
                        if(strOptionalAddRelation.equalsIgnoreCase("true")){ //then a relation must be added
                            JavaproductModel oChildJavaproductModel = (JavaproductModel) hibernateSession.get(JavaproductModel.class, iOptionalResourceId);
                            oChildJavaproductModel.getSetOfParentJavaaccountModel().add(oJavaaccountModel);
                            hibernateSession.update(oChildJavaproductModel);
                        }
                        else{ //else a relation must be deleted
                            JavaproductModel oChildJavaproductModel = (JavaproductModel) hibernateSession.get(JavaproductModel.class, iOptionalResourceId);

                            Iterator<JavaaccountModel> iterator = oChildJavaproductModel.getSetOfParentJavaaccountModel().iterator();
                            while(iterator.hasNext()){
                                JavaaccountModel oOldJavaaccountModel = iterator.next();
                                if(oOldJavaaccountModel.getaccountId() == oJavaaccountModel.getaccountId()){
                                    iterator.remove();
                                }
                            }
                            hibernateSession.update(oChildJavaproductModel);
                            hibernateTransaction.commit();
                            hibernateSession.close();
                            hibernateSession = HibernateUtil.getSessionFactory().openSession();
                            hibernateTransaction = hibernateSession.beginTransaction();
                        }
                    }
                    if(strOptionalRelationName.equalsIgnoreCase("order")){//if towards order is the required child relation to be updated
                        if(strOptionalAddRelation.equalsIgnoreCase("true")){ //then a relation must be added
                            JavaorderModel oChildJavaorderModel = (JavaorderModel) hibernateSession.get(JavaorderModel.class, iOptionalResourceId);
                            oChildJavaorderModel.getSetOfParentJavaaccountModel().add(oJavaaccountModel);
                            hibernateSession.update(oChildJavaorderModel);
                        }
                        else{ //else a relation must be deleted
                            JavaorderModel oChildJavaorderModel = (JavaorderModel) hibernateSession.get(JavaorderModel.class, iOptionalResourceId);

                            Iterator<JavaaccountModel> iterator = oChildJavaorderModel.getSetOfParentJavaaccountModel().iterator();
                            while(iterator.hasNext()){
                                JavaaccountModel oOldJavaaccountModel = iterator.next();
                                if(oOldJavaaccountModel.getaccountId() == oJavaaccountModel.getaccountId()){
                                    iterator.remove();
                                }
                            }
                            hibernateSession.update(oChildJavaorderModel);
                            hibernateTransaction.commit();
                            hibernateSession.close();
                            hibernateSession = HibernateUtil.getSessionFactory().openSession();
                            hibernateTransaction = hibernateSession.beginTransaction();
                        }
                    }
                }
            }
        }

        /* Update the existing account of the database*/
        hibernateSession.update(oJavaaccountModel);

        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();
        return oJavaaccountModel;
    }

    /* This function handles the low level hibernate activities so as to retrieve an existing account resource from the service database.*/
    public JavaaccountModel getaccount(JavaaccountModel oJavaaccountModel){

    	/* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        /* Retrieve the existing account from the database*/
        oJavaaccountModel = (JavaaccountModel) hibernateSession.get(JavaaccountModel.class, oJavaaccountModel.getaccountId());
		Hibernate.initialize(oJavaaccountModel.getSetOfJavaproductModel());
		Hibernate.initialize(oJavaaccountModel.getSetOfJavaorderModel());

        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();
        return oJavaaccountModel;
    }

    /* This function handles the low level hibernate activities so as to delete an existing account resource from the service database.*/
    public JavaaccountModel deleteaccount(JavaaccountModel oJavaaccountModel){

   		/* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        /* Retrieve the existing account from the database*/
        oJavaaccountModel = (JavaaccountModel) hibernateSession.get(JavaaccountModel.class, oJavaaccountModel.getaccountId());

        /* Delete any child resource related with the existing account from the database.
        Note: this is needed because hibernate does not cascade delete in the desired way on Many to Many relationships, when a child has multiple parents.*/
       	Iterator<JavaproductModel> iteratorProduct = oJavaaccountModel.getSetOfJavaproductModel().iterator();
        while(iteratorProduct.hasNext()){
            JavaproductModel oJavaproductModel = iteratorProduct.next();
            oJavaproductModel.getSetOfParentJavaaccountModel().remove(oJavaaccountModel);
            hibernateSession.update(oJavaproductModel);
			cascadeDeleteproduct(oJavaproductModel, hibernateSession);
        }
       	Iterator<JavaorderModel> iteratorOrder = oJavaaccountModel.getSetOfJavaorderModel().iterator();
        while(iteratorOrder.hasNext()){
            JavaorderModel oJavaorderModel = iteratorOrder.next();
            oJavaorderModel.getSetOfParentJavaaccountModel().remove(oJavaaccountModel);
            hibernateSession.update(oJavaorderModel);
			cascadeDeleteorder(oJavaorderModel, hibernateSession);
        }

        /* Delete the existing account from the database*/
        hibernateSession.delete(oJavaaccountModel);
        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();
        return oJavaaccountModel;
    }

    /* This function handles the low level hibernate activities so as to cascade delete any orphan children resources of an existing account resource from the service database.*/
    private void cascadeDeleteaccount(JavaaccountModel oJavaaccountModel, Session hibernateSession){
		
		//check if this resource has any other parent resources	
		boolean bHasParents = false;

        /* Delete any child resource related with the existing account from the database.
        Note: this is needed because hibernate does not cascade delete in the desired way on Many to Many relationships, when a child has multiple parents.*/
		if(bHasParents == false){
 	      	Iterator<JavaproductModel> iteratorProduct = oJavaaccountModel.getSetOfJavaproductModel().iterator();
    	    while(iteratorProduct.hasNext()){
        	    JavaproductModel oJavaproductModel = iteratorProduct.next();
            	oJavaproductModel.getSetOfParentJavaaccountModel().remove(oJavaaccountModel);
            	hibernateSession.update(oJavaproductModel);
				System.out.println("Removed " + oJavaproductModel.getproductId() + " product");
        	}
 	      	Iterator<JavaorderModel> iteratorOrder = oJavaaccountModel.getSetOfJavaorderModel().iterator();
    	    while(iteratorOrder.hasNext()){
        	    JavaorderModel oJavaorderModel = iteratorOrder.next();
            	oJavaorderModel.getSetOfParentJavaaccountModel().remove(oJavaaccountModel);
            	hibernateSession.update(oJavaorderModel);
				System.out.println("Removed " + oJavaorderModel.getorderId() + " order");
        	}
		}

        /* Delete the existing account from the database*/
		if(bHasParents == false){
        	hibernateSession.delete(oJavaaccountModel);
		}       
    }

    /* This function handles the low level hibernate activities so as to retrieve all the account resources from the service database.*/

    public Set<JavaaccountModel> getaccountList(Set<JavaaccountModel> SetOfaccountList){

        /* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        /* Retrieve the list of account resources that are needed.*/
        String strHibernateQuery = "FROM JavaaccountModel";
        Query hibernateQuery = hibernateSession.createQuery(strHibernateQuery);
        SetOfaccountList = new HashSet(hibernateQuery.list());

        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();
        return SetOfaccountList;
    }
    /* This function handles the low level JPA activities so as to add a new product resource to the service database.*/
    public JavaproductModel postproduct(JavaproductModel oJavaproductModel){

    	/* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        /* Insert the new product to database*/
        int productId = (Integer) hibernateSession.save(oJavaproductModel);
		oJavaproductModel.setproductId(productId);

        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();

        /* Return the JavaproductModel with updated productId*/
        return oJavaproductModel;
    }
	
    /* This function handles the low level hibernate activities so as to update an existing product resource of the service database.*/
    public JavaproductModel putproduct(JavaproductModel oJavaproductModel, String strOptionalUpdateRelations, String strOptionalUpdateParent, String strOptionalRelationName, String strOptionalAddRelation, Integer iOptionalResourceId){

    	/* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        //if the relations of this resource should also be updated
        if(strOptionalUpdateRelations != null){
            if(strOptionalUpdateRelations.equalsIgnoreCase("true")){
                if(strOptionalUpdateParent.equalsIgnoreCase("true")){//a parent relation must be updated
                    if(strOptionalRelationName.equalsIgnoreCase("account")){//if towards account is the required parent relation to be updated
                        if(strOptionalAddRelation.equalsIgnoreCase("true")){ //then a relation must be added
                            JavaproductModel oOldJavaproductModel = (JavaproductModel) hibernateSession.get(JavaproductModel.class, oJavaproductModel.getproductId());
                            oJavaproductModel.getSetOfParentJavaaccountModel().clear();
                            oJavaproductModel.getSetOfParentJavaaccountModel().addAll(oOldJavaproductModel.getSetOfParentJavaaccountModel());

                            JavaaccountModel oNewJavaaccountModel = new JavaaccountModel();
                            oNewJavaaccountModel.setaccountId(iOptionalResourceId);

							Boolean bRelationAlreadyExists = false;
							Iterator<JavaaccountModel> iterator = oOldJavaproductModel.getSetOfParentJavaaccountModel().iterator();

                            while(iterator.hasNext()){
                                JavaaccountModel oParentJavaaccountModel = iterator.next();
                                if(oParentJavaaccountModel.getaccountId() == iOptionalResourceId){
                                    bRelationAlreadyExists = true;
									break;
                                }
                            }
							
							if(bRelationAlreadyExists == false){
                            	oJavaproductModel.getSetOfParentJavaaccountModel().add(oNewJavaaccountModel);
							}
                            oJavaproductModel.getSetOfParentJavaorderModel().clear();
                            oJavaproductModel.getSetOfParentJavaorderModel().addAll(oOldJavaproductModel.getSetOfParentJavaorderModel());
                            hibernateTransaction.commit();
                            hibernateSession.close();
                            hibernateSession = HibernateUtil.getSessionFactory().openSession();
                            hibernateTransaction = hibernateSession.beginTransaction();
                        }
                        else{ //else a relation must be deleted
                            oJavaproductModel.getSetOfParentJavaaccountModel().clear();
                            JavaproductModel oOldJavaproductModel = (JavaproductModel) hibernateSession.get(JavaproductModel.class, oJavaproductModel.getproductId());
                            
                            Iterator<JavaaccountModel> iterator = oOldJavaproductModel.getSetOfParentJavaaccountModel().iterator();
                            while(iterator.hasNext()){
                                JavaaccountModel oParentJavaaccountModel = iterator.next();
                                if(oParentJavaaccountModel.getaccountId() != iOptionalResourceId){
                                    oJavaproductModel.getSetOfParentJavaaccountModel().add(oParentJavaaccountModel);
                                }
                            }
                            hibernateSession.close();
                            hibernateSession = HibernateUtil.getSessionFactory().openSession();
                            hibernateTransaction = hibernateSession.beginTransaction();
                        }
                    }
                    if(strOptionalRelationName.equalsIgnoreCase("order")){//if towards order is the required parent relation to be updated
                        if(strOptionalAddRelation.equalsIgnoreCase("true")){ //then a relation must be added
                            JavaproductModel oOldJavaproductModel = (JavaproductModel) hibernateSession.get(JavaproductModel.class, oJavaproductModel.getproductId());
                            oJavaproductModel.getSetOfParentJavaaccountModel().clear();
                            oJavaproductModel.getSetOfParentJavaaccountModel().addAll(oOldJavaproductModel.getSetOfParentJavaaccountModel());
                            oJavaproductModel.getSetOfParentJavaorderModel().clear();
                            oJavaproductModel.getSetOfParentJavaorderModel().addAll(oOldJavaproductModel.getSetOfParentJavaorderModel());

                            JavaorderModel oNewJavaorderModel = new JavaorderModel();
                            oNewJavaorderModel.setorderId(iOptionalResourceId);

							Boolean bRelationAlreadyExists = false;
							Iterator<JavaorderModel> iterator = oOldJavaproductModel.getSetOfParentJavaorderModel().iterator();

                            while(iterator.hasNext()){
                                JavaorderModel oParentJavaorderModel = iterator.next();
                                if(oParentJavaorderModel.getorderId() == iOptionalResourceId){
                                    bRelationAlreadyExists = true;
									break;
                                }
                            }
							
							if(bRelationAlreadyExists == false){
                            	oJavaproductModel.getSetOfParentJavaorderModel().add(oNewJavaorderModel);
							}
                            hibernateTransaction.commit();
                            hibernateSession.close();
                            hibernateSession = HibernateUtil.getSessionFactory().openSession();
                            hibernateTransaction = hibernateSession.beginTransaction();
                        }
                        else{ //else a relation must be deleted
                            oJavaproductModel.getSetOfParentJavaorderModel().clear();
                            JavaproductModel oOldJavaproductModel = (JavaproductModel) hibernateSession.get(JavaproductModel.class, oJavaproductModel.getproductId());
                            
                            Iterator<JavaorderModel> iterator = oOldJavaproductModel.getSetOfParentJavaorderModel().iterator();
                            while(iterator.hasNext()){
                                JavaorderModel oParentJavaorderModel = iterator.next();
                                if(oParentJavaorderModel.getorderId() != iOptionalResourceId){
                                    oJavaproductModel.getSetOfParentJavaorderModel().add(oParentJavaorderModel);
                                }
                            }
                            hibernateSession.close();
                            hibernateSession = HibernateUtil.getSessionFactory().openSession();
                            hibernateTransaction = hibernateSession.beginTransaction();
                        }
                    }
                }
                else{ //else a child relation must be updated
                    if(strOptionalRelationName.equalsIgnoreCase("review")){//if towards review is the required child relation to be updated
                        if(strOptionalAddRelation.equalsIgnoreCase("true")){ //then a relation must be added
                            JavareviewModel oChildJavareviewModel = (JavareviewModel) hibernateSession.get(JavareviewModel.class, iOptionalResourceId);
                            oChildJavareviewModel.getSetOfParentJavaproductModel().add(oJavaproductModel);
                            hibernateSession.update(oChildJavareviewModel);
                        }
                        else{ //else a relation must be deleted
                            JavareviewModel oChildJavareviewModel = (JavareviewModel) hibernateSession.get(JavareviewModel.class, iOptionalResourceId);

                            Iterator<JavaproductModel> iterator = oChildJavareviewModel.getSetOfParentJavaproductModel().iterator();
                            while(iterator.hasNext()){
                                JavaproductModel oOldJavaproductModel = iterator.next();
                                if(oOldJavaproductModel.getproductId() == oJavaproductModel.getproductId()){
                                    iterator.remove();
                                }
                            }
                            hibernateSession.update(oChildJavareviewModel);
                            hibernateTransaction.commit();
                            hibernateSession.close();
                            hibernateSession = HibernateUtil.getSessionFactory().openSession();
                            hibernateTransaction = hibernateSession.beginTransaction();
                        }
                    }
                }
            }
        }

        /* Update the existing product of the database*/
        hibernateSession.update(oJavaproductModel);

        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();
        return oJavaproductModel;
    }

    /* This function handles the low level hibernate activities so as to retrieve an existing product resource from the service database.*/
    public JavaproductModel getproduct(JavaproductModel oJavaproductModel){

    	/* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        /* Retrieve the existing product from the database*/
        oJavaproductModel = (JavaproductModel) hibernateSession.get(JavaproductModel.class, oJavaproductModel.getproductId());
        Hibernate.initialize(oJavaproductModel.getSetOfParentJavaaccountModel());
        Hibernate.initialize(oJavaproductModel.getSetOfParentJavaorderModel());
		Hibernate.initialize(oJavaproductModel.getSetOfJavareviewModel());

        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();
        return oJavaproductModel;
    }

    /* This function handles the low level hibernate activities so as to delete an existing product resource from the service database.*/
    public JavaproductModel deleteproduct(JavaproductModel oJavaproductModel){

   		/* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        /* Retrieve the existing product from the database*/
        oJavaproductModel = (JavaproductModel) hibernateSession.get(JavaproductModel.class, oJavaproductModel.getproductId());

        /* Delete any child resource related with the existing product from the database.
        Note: this is needed because hibernate does not cascade delete in the desired way on Many to Many relationships, when a child has multiple parents.*/
       	Iterator<JavareviewModel> iteratorReview = oJavaproductModel.getSetOfJavareviewModel().iterator();
        while(iteratorReview.hasNext()){
            JavareviewModel oJavareviewModel = iteratorReview.next();
            oJavareviewModel.getSetOfParentJavaproductModel().remove(oJavaproductModel);
            hibernateSession.update(oJavareviewModel);
			cascadeDeletereview(oJavareviewModel, hibernateSession);
        }

        /* Delete the existing product from the database*/
        hibernateSession.delete(oJavaproductModel);
        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();
        return oJavaproductModel;
    }

    /* This function handles the low level hibernate activities so as to cascade delete any orphan children resources of an existing product resource from the service database.*/
    private void cascadeDeleteproduct(JavaproductModel oJavaproductModel, Session hibernateSession){
		
		//check if this resource has any other parent resources	
		boolean bHasParents = false;

		if(oJavaproductModel.getSetOfParentJavaaccountModel().size() > 0 && bHasParents == false){
			bHasParents = true;
		}

		if(oJavaproductModel.getSetOfParentJavaorderModel().size() > 0 && bHasParents == false){
			bHasParents = true;
		}

        /* Delete any child resource related with the existing product from the database.
        Note: this is needed because hibernate does not cascade delete in the desired way on Many to Many relationships, when a child has multiple parents.*/
		if(bHasParents == false){
 	      	Iterator<JavareviewModel> iteratorReview = oJavaproductModel.getSetOfJavareviewModel().iterator();
    	    while(iteratorReview.hasNext()){
        	    JavareviewModel oJavareviewModel = iteratorReview.next();
            	oJavareviewModel.getSetOfParentJavaproductModel().remove(oJavaproductModel);
            	hibernateSession.update(oJavareviewModel);
				System.out.println("Removed " + oJavareviewModel.getreviewId() + " review");
        	}
		}

        /* Delete the existing product from the database*/
		if(bHasParents == false){
        	hibernateSession.delete(oJavaproductModel);
		}       
    }

	/* This function handles the low level hibernate activities so as to retrieve all the product resources from the service database
    that are related to a specific account resource.*/

    public JavaaccountModel getaccountproductList(JavaaccountModel oJavaaccountModel){

        /* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        /* Find the account of which the product resource list is needed*/
        oJavaaccountModel = (JavaaccountModel) hibernateSession.get(JavaaccountModel.class, oJavaaccountModel.getaccountId());
		Hibernate.initialize(oJavaaccountModel.getSetOfJavaproductModel());

        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();
        return oJavaaccountModel;
    }
	/* This function handles the low level hibernate activities so as to retrieve all the product resources from the service database
    that are related to a specific order resource.*/

    public JavaorderModel getorderproductList(JavaorderModel oJavaorderModel){

        /* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        /* Find the order of which the product resource list is needed*/
        oJavaorderModel = (JavaorderModel) hibernateSession.get(JavaorderModel.class, oJavaorderModel.getorderId());
		Hibernate.initialize(oJavaorderModel.getSetOfJavaproductModel());

        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();
        return oJavaorderModel;
    }
    /* This function handles the low level JPA activities so as to add a new order resource to the service database.*/
    public JavaorderModel postorder(JavaorderModel oJavaorderModel){

    	/* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        /* Insert the new order to database*/
        int orderId = (Integer) hibernateSession.save(oJavaorderModel);
		oJavaorderModel.setorderId(orderId);

        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();

        /* Return the JavaorderModel with updated orderId*/
        return oJavaorderModel;
    }
	
    /* This function handles the low level hibernate activities so as to update an existing order resource of the service database.*/
    public JavaorderModel putorder(JavaorderModel oJavaorderModel, String strOptionalUpdateRelations, String strOptionalUpdateParent, String strOptionalRelationName, String strOptionalAddRelation, Integer iOptionalResourceId){

    	/* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        //if the relations of this resource should also be updated
        if(strOptionalUpdateRelations != null){
            if(strOptionalUpdateRelations.equalsIgnoreCase("true")){
                if(strOptionalUpdateParent.equalsIgnoreCase("true")){//a parent relation must be updated
                    if(strOptionalRelationName.equalsIgnoreCase("account")){//if towards account is the required parent relation to be updated
                        if(strOptionalAddRelation.equalsIgnoreCase("true")){ //then a relation must be added
                            JavaorderModel oOldJavaorderModel = (JavaorderModel) hibernateSession.get(JavaorderModel.class, oJavaorderModel.getorderId());
                            oJavaorderModel.getSetOfParentJavaaccountModel().clear();
                            oJavaorderModel.getSetOfParentJavaaccountModel().addAll(oOldJavaorderModel.getSetOfParentJavaaccountModel());

                            JavaaccountModel oNewJavaaccountModel = new JavaaccountModel();
                            oNewJavaaccountModel.setaccountId(iOptionalResourceId);

							Boolean bRelationAlreadyExists = false;
							Iterator<JavaaccountModel> iterator = oOldJavaorderModel.getSetOfParentJavaaccountModel().iterator();

                            while(iterator.hasNext()){
                                JavaaccountModel oParentJavaaccountModel = iterator.next();
                                if(oParentJavaaccountModel.getaccountId() == iOptionalResourceId){
                                    bRelationAlreadyExists = true;
									break;
                                }
                            }
							
							if(bRelationAlreadyExists == false){
                            	oJavaorderModel.getSetOfParentJavaaccountModel().add(oNewJavaaccountModel);
							}
                            hibernateTransaction.commit();
                            hibernateSession.close();
                            hibernateSession = HibernateUtil.getSessionFactory().openSession();
                            hibernateTransaction = hibernateSession.beginTransaction();
                        }
                        else{ //else a relation must be deleted
                            oJavaorderModel.getSetOfParentJavaaccountModel().clear();
                            JavaorderModel oOldJavaorderModel = (JavaorderModel) hibernateSession.get(JavaorderModel.class, oJavaorderModel.getorderId());
                            
                            Iterator<JavaaccountModel> iterator = oOldJavaorderModel.getSetOfParentJavaaccountModel().iterator();
                            while(iterator.hasNext()){
                                JavaaccountModel oParentJavaaccountModel = iterator.next();
                                if(oParentJavaaccountModel.getaccountId() != iOptionalResourceId){
                                    oJavaorderModel.getSetOfParentJavaaccountModel().add(oParentJavaaccountModel);
                                }
                            }
                            hibernateSession.close();
                            hibernateSession = HibernateUtil.getSessionFactory().openSession();
                            hibernateTransaction = hibernateSession.beginTransaction();
                        }
                    }
                }
                else{ //else a child relation must be updated
                    if(strOptionalRelationName.equalsIgnoreCase("product")){//if towards product is the required child relation to be updated
                        if(strOptionalAddRelation.equalsIgnoreCase("true")){ //then a relation must be added
                            JavaproductModel oChildJavaproductModel = (JavaproductModel) hibernateSession.get(JavaproductModel.class, iOptionalResourceId);
                            oChildJavaproductModel.getSetOfParentJavaorderModel().add(oJavaorderModel);
                            hibernateSession.update(oChildJavaproductModel);
                        }
                        else{ //else a relation must be deleted
                            JavaproductModel oChildJavaproductModel = (JavaproductModel) hibernateSession.get(JavaproductModel.class, iOptionalResourceId);

                            Iterator<JavaorderModel> iterator = oChildJavaproductModel.getSetOfParentJavaorderModel().iterator();
                            while(iterator.hasNext()){
                                JavaorderModel oOldJavaorderModel = iterator.next();
                                if(oOldJavaorderModel.getorderId() == oJavaorderModel.getorderId()){
                                    iterator.remove();
                                }
                            }
                            hibernateSession.update(oChildJavaproductModel);
                            hibernateTransaction.commit();
                            hibernateSession.close();
                            hibernateSession = HibernateUtil.getSessionFactory().openSession();
                            hibernateTransaction = hibernateSession.beginTransaction();
                        }
                    }
                }
            }
        }

        /* Update the existing order of the database*/
        hibernateSession.update(oJavaorderModel);

        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();
        return oJavaorderModel;
    }

    /* This function handles the low level hibernate activities so as to retrieve an existing order resource from the service database.*/
    public JavaorderModel getorder(JavaorderModel oJavaorderModel){

    	/* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        /* Retrieve the existing order from the database*/
        oJavaorderModel = (JavaorderModel) hibernateSession.get(JavaorderModel.class, oJavaorderModel.getorderId());
        Hibernate.initialize(oJavaorderModel.getSetOfParentJavaaccountModel());
		Hibernate.initialize(oJavaorderModel.getSetOfJavaproductModel());

        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();
        return oJavaorderModel;
    }

    /* This function handles the low level hibernate activities so as to delete an existing order resource from the service database.*/
    public JavaorderModel deleteorder(JavaorderModel oJavaorderModel){

   		/* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        /* Retrieve the existing order from the database*/
        oJavaorderModel = (JavaorderModel) hibernateSession.get(JavaorderModel.class, oJavaorderModel.getorderId());

        /* Delete any child resource related with the existing order from the database.
        Note: this is needed because hibernate does not cascade delete in the desired way on Many to Many relationships, when a child has multiple parents.*/
       	Iterator<JavaproductModel> iteratorProduct = oJavaorderModel.getSetOfJavaproductModel().iterator();
        while(iteratorProduct.hasNext()){
            JavaproductModel oJavaproductModel = iteratorProduct.next();
            oJavaproductModel.getSetOfParentJavaorderModel().remove(oJavaorderModel);
            hibernateSession.update(oJavaproductModel);
			cascadeDeleteproduct(oJavaproductModel, hibernateSession);
        }

        /* Delete the existing order from the database*/
        hibernateSession.delete(oJavaorderModel);
        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();
        return oJavaorderModel;
    }

    /* This function handles the low level hibernate activities so as to cascade delete any orphan children resources of an existing order resource from the service database.*/
    private void cascadeDeleteorder(JavaorderModel oJavaorderModel, Session hibernateSession){
		
		//check if this resource has any other parent resources	
		boolean bHasParents = false;

		if(oJavaorderModel.getSetOfParentJavaaccountModel().size() > 0 && bHasParents == false){
			bHasParents = true;
		}

        /* Delete any child resource related with the existing order from the database.
        Note: this is needed because hibernate does not cascade delete in the desired way on Many to Many relationships, when a child has multiple parents.*/
		if(bHasParents == false){
 	      	Iterator<JavaproductModel> iteratorProduct = oJavaorderModel.getSetOfJavaproductModel().iterator();
    	    while(iteratorProduct.hasNext()){
        	    JavaproductModel oJavaproductModel = iteratorProduct.next();
            	oJavaproductModel.getSetOfParentJavaorderModel().remove(oJavaorderModel);
            	hibernateSession.update(oJavaproductModel);
				System.out.println("Removed " + oJavaproductModel.getproductId() + " product");
        	}
		}

        /* Delete the existing order from the database*/
		if(bHasParents == false){
        	hibernateSession.delete(oJavaorderModel);
		}       
    }

	/* This function handles the low level hibernate activities so as to retrieve all the order resources from the service database
    that are related to a specific account resource.*/

    public JavaaccountModel getorderList(JavaaccountModel oJavaaccountModel){

        /* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        /* Find the account of which the order resource list is needed*/
        oJavaaccountModel = (JavaaccountModel) hibernateSession.get(JavaaccountModel.class, oJavaaccountModel.getaccountId());
		Hibernate.initialize(oJavaaccountModel.getSetOfJavaorderModel());

        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();
        return oJavaaccountModel;
    }
    /* This function handles the low level JPA activities so as to add a new review resource to the service database.*/
    public JavareviewModel postreview(JavareviewModel oJavareviewModel){

    	/* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        /* Insert the new review to database*/
        int reviewId = (Integer) hibernateSession.save(oJavareviewModel);
		oJavareviewModel.setreviewId(reviewId);

        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();

        /* Return the JavareviewModel with updated reviewId*/
        return oJavareviewModel;
    }
	
    /* This function handles the low level hibernate activities so as to update an existing review resource of the service database.*/
    public JavareviewModel putreview(JavareviewModel oJavareviewModel, String strOptionalUpdateRelations, String strOptionalUpdateParent, String strOptionalRelationName, String strOptionalAddRelation, Integer iOptionalResourceId){

    	/* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        //if the relations of this resource should also be updated
        if(strOptionalUpdateRelations != null){
            if(strOptionalUpdateRelations.equalsIgnoreCase("true")){
                if(strOptionalUpdateParent.equalsIgnoreCase("true")){//a parent relation must be updated
                    if(strOptionalRelationName.equalsIgnoreCase("product")){//if towards product is the required parent relation to be updated
                        if(strOptionalAddRelation.equalsIgnoreCase("true")){ //then a relation must be added
                            JavareviewModel oOldJavareviewModel = (JavareviewModel) hibernateSession.get(JavareviewModel.class, oJavareviewModel.getreviewId());
                            oJavareviewModel.getSetOfParentJavaproductModel().clear();
                            oJavareviewModel.getSetOfParentJavaproductModel().addAll(oOldJavareviewModel.getSetOfParentJavaproductModel());

                            JavaproductModel oNewJavaproductModel = new JavaproductModel();
                            oNewJavaproductModel.setproductId(iOptionalResourceId);

							Boolean bRelationAlreadyExists = false;
							Iterator<JavaproductModel> iterator = oOldJavareviewModel.getSetOfParentJavaproductModel().iterator();

                            while(iterator.hasNext()){
                                JavaproductModel oParentJavaproductModel = iterator.next();
                                if(oParentJavaproductModel.getproductId() == iOptionalResourceId){
                                    bRelationAlreadyExists = true;
									break;
                                }
                            }
							
							if(bRelationAlreadyExists == false){
                            	oJavareviewModel.getSetOfParentJavaproductModel().add(oNewJavaproductModel);
							}
                            hibernateTransaction.commit();
                            hibernateSession.close();
                            hibernateSession = HibernateUtil.getSessionFactory().openSession();
                            hibernateTransaction = hibernateSession.beginTransaction();
                        }
                        else{ //else a relation must be deleted
                            oJavareviewModel.getSetOfParentJavaproductModel().clear();
                            JavareviewModel oOldJavareviewModel = (JavareviewModel) hibernateSession.get(JavareviewModel.class, oJavareviewModel.getreviewId());
                            
                            Iterator<JavaproductModel> iterator = oOldJavareviewModel.getSetOfParentJavaproductModel().iterator();
                            while(iterator.hasNext()){
                                JavaproductModel oParentJavaproductModel = iterator.next();
                                if(oParentJavaproductModel.getproductId() != iOptionalResourceId){
                                    oJavareviewModel.getSetOfParentJavaproductModel().add(oParentJavaproductModel);
                                }
                            }
                            hibernateSession.close();
                            hibernateSession = HibernateUtil.getSessionFactory().openSession();
                            hibernateTransaction = hibernateSession.beginTransaction();
                        }
                    }
                }
                else{ //else a child relation must be updated
                }
            }
        }

        /* Update the existing review of the database*/
        hibernateSession.update(oJavareviewModel);

        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();
        return oJavareviewModel;
    }

    /* This function handles the low level hibernate activities so as to retrieve an existing review resource from the service database.*/
    public JavareviewModel getreview(JavareviewModel oJavareviewModel){

    	/* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        /* Retrieve the existing review from the database*/
        oJavareviewModel = (JavareviewModel) hibernateSession.get(JavareviewModel.class, oJavareviewModel.getreviewId());
        Hibernate.initialize(oJavareviewModel.getSetOfParentJavaproductModel());

        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();
        return oJavareviewModel;
    }

    /* This function handles the low level hibernate activities so as to delete an existing review resource from the service database.*/
    public JavareviewModel deletereview(JavareviewModel oJavareviewModel){

   		/* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        /* Retrieve the existing review from the database*/
        oJavareviewModel = (JavareviewModel) hibernateSession.get(JavareviewModel.class, oJavareviewModel.getreviewId());


        /* Delete the existing review from the database*/
        hibernateSession.delete(oJavareviewModel);
        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();
        return oJavareviewModel;
    }

    /* This function handles the low level hibernate activities so as to cascade delete any orphan children resources of an existing review resource from the service database.*/
    private void cascadeDeletereview(JavareviewModel oJavareviewModel, Session hibernateSession){
		
		//check if this resource has any other parent resources	
		boolean bHasParents = false;

		if(oJavareviewModel.getSetOfParentJavaproductModel().size() > 0 && bHasParents == false){
			bHasParents = true;
		}


        /* Delete the existing review from the database*/
		if(bHasParents == false){
        	hibernateSession.delete(oJavareviewModel);
		}       
    }

	/* This function handles the low level hibernate activities so as to retrieve all the review resources from the service database
    that are related to a specific product resource.*/

    public JavaproductModel getreviewList(JavaproductModel oJavaproductModel){

        /* Create a new hibernate session and begin the transaction*/
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction hibernateTransaction = hibernateSession.beginTransaction();

        /* Find the product of which the review resource list is needed*/
        oJavaproductModel = (JavaproductModel) hibernateSession.get(JavaproductModel.class, oJavaproductModel.getproductId());
		Hibernate.initialize(oJavaproductModel.getSetOfJavareviewModel());
		Hibernate.initialize(oJavaproductModel.getSetOfParentJavaaccountModel());	
		Hibernate.initialize(oJavaproductModel.getSetOfParentJavaorderModel());	

        /* Commit and terminate the session*/
        hibernateTransaction.commit();
        hibernateSession.close();
        return oJavaproductModel;
    }
	public List<ResourceAccessPolicySet> getPolicySetByResource(String resourceType)
    {
    	List<ResourceAccessPolicySet> oResourceAccessPolicySet = new ArrayList<ResourceAccessPolicySet>();
    	try
    	{
		    Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
			Transaction hibernateTransaction = hibernateSession.beginTransaction();
			
			//create the query in HQL language
			String strQuery = String.format("FROM ResourceAccessPolicySet WHERE (auhtorizableResourceName = '%s')", resourceType);
			Query  hibernateQuery = hibernateSession.createQuery(strQuery);
			oResourceAccessPolicySet = ((List<ResourceAccessPolicySet>)(hibernateQuery.list()));
			
			//commit and terminate the session
			hibernateTransaction.commit();
			hibernateSession.close();
    	}
		catch (HibernateException exception)
		{
			System.out.println(exception.getCause());

			ResponseBuilderImpl builder = new ResponseBuilderImpl();
			builder.status(Response.Status.BAD_REQUEST);
			builder.entity(String.format("%s",exception.getCause()));
			Response response = builder.build();
			throw new WebApplicationException(response);
		}
    	
    	return oResourceAccessPolicySet;
    }

	private void initializeAuthorizationTables(){

		dropAuthorizationTables();

		try
    	{
		    Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
			Transaction hibernateTransaction = hibernateSession.beginTransaction();
			
			ResourceAccessPolicy policy = null;
			ResourceAccessRule rule = null;
			ResourceAccessCondition condition = null;

			ResourceAccessPolicySet ps1 = new ResourceAccessPolicySet("account",CombiningAlgorithmEnum.PERMIT_UNLESS_DENY);
			hibernateSession.save(ps1);
			policy = new ResourceAccessPolicy(CombiningAlgorithmEnum.PERMIT_OVERRIDES, ps1);
			hibernateSession.save(policy);
			rule = new ResourceAccessRule(RuleType.PERMIT,new HashSet<Action>(Arrays.asList(Action.POST)),policy);
			hibernateSession.save(rule);
			condition = new ResourceAccessCondition(OperatorEnum.EQUAL,rule,new ResourceAccessAttribute(AttributeCategory.INCLUDED_RESOURCE,"account","role"),new ResourceAccessAttribute(new String[]{"customer"},"String"));
			hibernateSession.save(condition);
			rule = new ResourceAccessRule(RuleType.PERMIT,new HashSet<Action>(Arrays.asList(Action.GET)),policy);
			hibernateSession.save(rule);
			condition = new ResourceAccessCondition(OperatorEnum.EQUAL,rule,new ResourceAccessAttribute(AttributeCategory.ACCESS_SUBJECT,"account","email"),new ResourceAccessAttribute(AttributeCategory.ACCESSED_RESOURCE,"account","email"));
			hibernateSession.save(condition);
			rule = new ResourceAccessRule(RuleType.PERMIT,new HashSet<Action>(Arrays.asList(Action.GET,Action.DELETE)),policy);
			hibernateSession.save(rule);
			condition = new ResourceAccessCondition(OperatorEnum.EQUAL,rule,new ResourceAccessAttribute(AttributeCategory.ACCESS_SUBJECT,"account","role"),new ResourceAccessAttribute(new String[]{"manager"},"String"));
			hibernateSession.save(condition);
			rule = new ResourceAccessRule(RuleType.PERMIT,new HashSet<Action>(Arrays.asList(Action.PUT)),policy);
			hibernateSession.save(rule);
			condition = new ResourceAccessCondition(OperatorEnum.EQUAL,rule,new ResourceAccessAttribute(AttributeCategory.ACCESS_SUBJECT,"account","email"),new ResourceAccessAttribute(AttributeCategory.ACCESSED_RESOURCE,"account","email"));
			hibernateSession.save(condition);
			condition = new ResourceAccessCondition(OperatorEnum.EQUAL,rule,new ResourceAccessAttribute(AttributeCategory.INCLUDED_RESOURCE,"account","role"),new ResourceAccessAttribute(new String[]{"customer"},"String"));
			hibernateSession.save(condition);
			condition = new ResourceAccessCondition(OperatorEnum.EQUAL,rule,new ResourceAccessAttribute(AttributeCategory.ACCESS_SUBJECT,"account","role"),new ResourceAccessAttribute(new String[]{"customer"},"String"));
			hibernateSession.save(condition);
			rule = new ResourceAccessRule(RuleType.PERMIT,new HashSet<Action>(Arrays.asList(Action.PUT)),policy);
			hibernateSession.save(rule);
			condition = new ResourceAccessCondition(OperatorEnum.EQUAL,rule,new ResourceAccessAttribute(AttributeCategory.ACCESS_SUBJECT,"account","email"),new ResourceAccessAttribute(AttributeCategory.ACCESSED_RESOURCE,"account","email"));
			hibernateSession.save(condition);
			condition = new ResourceAccessCondition(OperatorEnum.EQUAL,rule,new ResourceAccessAttribute(AttributeCategory.ACCESS_SUBJECT,"account","role"),new ResourceAccessAttribute(new String[]{"manager"},"String"));
			hibernateSession.save(condition);
			
			ResourceAccessPolicySet ps2 = new ResourceAccessPolicySet("product",CombiningAlgorithmEnum.PERMIT_UNLESS_DENY);
			hibernateSession.save(ps2);
			policy = new ResourceAccessPolicy(CombiningAlgorithmEnum.PERMIT_OVERRIDES, ps2);
			hibernateSession.save(policy);
			rule = new ResourceAccessRule(RuleType.PERMIT,new HashSet<Action>(Arrays.asList(Action.GET,Action.PUT,Action.POST,Action.DELETE)),policy);
			hibernateSession.save(rule);
			condition = new ResourceAccessCondition(OperatorEnum.EQUAL,rule,new ResourceAccessAttribute(AttributeCategory.ACCESS_SUBJECT,"account","role"),new ResourceAccessAttribute(new String[]{"manager"},"String"));
			hibernateSession.save(condition);
			rule = new ResourceAccessRule(RuleType.PERMIT,new HashSet<Action>(Arrays.asList(Action.GET)),policy);
			hibernateSession.save(rule);
			condition = new ResourceAccessCondition(OperatorEnum.EQUAL,rule,new ResourceAccessAttribute(AttributeCategory.ACCESSED_RESOURCE,"product","status"),new ResourceAccessAttribute(new String[]{"available"},"String"));
			hibernateSession.save(condition);
			
			ResourceAccessPolicySet ps3 = new ResourceAccessPolicySet("order",CombiningAlgorithmEnum.PERMIT_UNLESS_DENY);
			hibernateSession.save(ps3);
			policy = new ResourceAccessPolicy(CombiningAlgorithmEnum.PERMIT_OVERRIDES, ps3);
			hibernateSession.save(policy);
			rule = new ResourceAccessRule(RuleType.PERMIT,new HashSet<Action>(Arrays.asList(Action.POST)),policy);
			hibernateSession.save(rule);
			condition = new ResourceAccessCondition(OperatorEnum.EQUAL,rule,new ResourceAccessAttribute(AttributeCategory.ACCESS_SUBJECT,"account","role"),new ResourceAccessAttribute(new String[]{"customer"},"String"));
			hibernateSession.save(condition);
			rule = new ResourceAccessRule(RuleType.PERMIT,new HashSet<Action>(Arrays.asList(Action.GET,Action.PUT)),policy);
			hibernateSession.save(rule);
			condition = new ResourceAccessCondition(OperatorEnum.EQUAL,rule,new ResourceAccessAttribute(AttributeCategory.ACCESS_SUBJECT,"account","email"),new ResourceAccessAttribute(AttributeCategory.PARENT_RESOURCE,"account","email"));
			hibernateSession.save(condition);
			rule = new ResourceAccessRule(RuleType.PERMIT,new HashSet<Action>(Arrays.asList(Action.GET,Action.DELETE)),policy);
			hibernateSession.save(rule);
			condition = new ResourceAccessCondition(OperatorEnum.EQUAL,rule,new ResourceAccessAttribute(AttributeCategory.ACCESS_SUBJECT,"account","role"),new ResourceAccessAttribute(new String[]{"manager"},"String"));
			hibernateSession.save(condition);
			
			ResourceAccessPolicySet ps4 = new ResourceAccessPolicySet("review",CombiningAlgorithmEnum.PERMIT_UNLESS_DENY);
			hibernateSession.save(ps4);
			policy = new ResourceAccessPolicy(CombiningAlgorithmEnum.PERMIT_OVERRIDES, ps4);
			hibernateSession.save(policy);
			rule = new ResourceAccessRule(RuleType.PERMIT,new HashSet<Action>(Arrays.asList(Action.GET)),policy);
			hibernateSession.save(rule);
			condition = new ResourceAccessCondition(OperatorEnum.EQUAL,rule,new ResourceAccessAttribute(AttributeCategory.PARENT_RESOURCE,"product","status"),new ResourceAccessAttribute(new String[]{"available"},"String"));
			hibernateSession.save(condition);
			rule = new ResourceAccessRule(RuleType.PERMIT,new HashSet<Action>(Arrays.asList(Action.GET,Action.PUT,Action.DELETE)),policy);
			hibernateSession.save(rule);
			condition = new ResourceAccessCondition(OperatorEnum.EQUAL,rule,new ResourceAccessAttribute(AttributeCategory.ACCESS_SUBJECT,"account","email"),new ResourceAccessAttribute(AttributeCategory.PARENT_RESOURCE,"account","email"));
			hibernateSession.save(condition);
			rule = new ResourceAccessRule(RuleType.PERMIT,new HashSet<Action>(Arrays.asList(Action.GET,Action.DELETE)),policy);
			hibernateSession.save(rule);
			condition = new ResourceAccessCondition(OperatorEnum.EQUAL,rule,new ResourceAccessAttribute(AttributeCategory.ACCESS_SUBJECT,"account","role"),new ResourceAccessAttribute(new String[]{"manager"},"String"));
			hibernateSession.save(condition);
			rule = new ResourceAccessRule(RuleType.PERMIT,new HashSet<Action>(Arrays.asList(Action.POST)),policy);
			hibernateSession.save(rule);
			condition = new ResourceAccessCondition(OperatorEnum.EQUAL,rule,new ResourceAccessAttribute(AttributeCategory.PARENT_RESOURCE,"product","status"),new ResourceAccessAttribute(new String[]{"available"},"String"));
			hibernateSession.save(condition);
			condition = new ResourceAccessCondition(OperatorEnum.EQUAL,rule,new ResourceAccessAttribute(AttributeCategory.ACCESS_SUBJECT,"account","role"),new ResourceAccessAttribute(new String[]{"customer"},"String"));
			hibernateSession.save(condition);
			
	        /* Commit and terminate the session*/
			hibernateTransaction.commit();
	        hibernateSession.close();
    	}
		catch (HibernateException exception)
		{
			System.out.println(exception.getCause());

			ResponseBuilderImpl builder = new ResponseBuilderImpl();
			builder.status(Response.Status.BAD_REQUEST);
			builder.entity(String.format("%s",exception.getCause()));
			Response response = builder.build();
			throw new WebApplicationException(response);
		}
	}

	private void dropAuthorizationTables(){

		System.out.println("Drop authorization tables");
		Connection con = null;
		Statement stmt = null;
		try {
			
			 Properties connectionProps = new Properties();
			 connectionProps.put("user", "root");
			 connectionProps.put("password", "fp7s-case");
		     con = DriverManager.getConnection("jdbc:mysql://localhost:3306/restreviews",connectionProps);
			 System.out.println("Database Exists");
		      DatabaseMetaData meta = con.getMetaData();
		      ResultSet res = meta.getTables(null, null, "allowedaction", 
		         new String[] {"TABLE"});
		      System.out.println("Droping actions table if exists."); 
		      while (res.next()) {
	        	 System.out.println(res.getString("TABLE_NAME"));
	             stmt = con.createStatement();	             
	             String sql = "DROP TABLE " + res.getString("TABLE_NAME");
	             System.out.println(sql);
	             stmt.executeUpdate(sql);		        	
		      }
		      
		      res = meta.getTables(null, null, "resourceaccesscondition_value", 
				         new String[] {"TABLE"});
				      System.out.println("Droping values table if exists."); 
				      while (res.next()) {
			        	 System.out.println(res.getString("TABLE_NAME"));
			             stmt = con.createStatement();	             
			             String sql = "DROP TABLE " + res.getString("TABLE_NAME");
			             System.out.println(sql);
			             stmt.executeUpdate(sql);		        	
			}

		      res = meta.getTables(null, null, "abaccondition", 
				         new String[] {"TABLE"});
				      System.out.println("Droping condition table if exists."); 
				      while (res.next()) {
			        	 System.out.println(res.getString("TABLE_NAME"));
			             stmt = con.createStatement();	             
			             String sql = "DROP TABLE " + res.getString("TABLE_NAME");
			             System.out.println(sql);
			             stmt.executeUpdate(sql);		        	
			}
						      
		      res = meta.getTables(null, null, "rule", 
				         new String[] {"TABLE"});
				      System.out.println("Droping rule table if exists."); 
				      while (res.next()) {
			        	 System.out.println(res.getString("TABLE_NAME"));
			             stmt = con.createStatement();	             
			             String sql = "DROP TABLE " + res.getString("TABLE_NAME");
			             System.out.println(sql);
			             stmt.executeUpdate(sql);		        	
			}
		      res = meta.getTables(null, null, "policy", 
				         new String[] {"TABLE"});
				      System.out.println("Droping policy table if exists."); 
				      while (res.next()) {
			        	 System.out.println(res.getString("TABLE_NAME"));
			             stmt = con.createStatement();	             
			             String sql = "DROP TABLE " + res.getString("TABLE_NAME");
			             System.out.println(sql);
			             stmt.executeUpdate(sql);		        	
			}					      
		      res = meta.getTables(null, null, "policyset", 
				         new String[] {"TABLE"});
				      System.out.println("Droping policyset table if exists."); 
				      while (res.next()) {
			        	 System.out.println(res.getString("TABLE_NAME"));
			             stmt = con.createStatement();	             
			             String sql = "DROP TABLE " + res.getString("TABLE_NAME");
			             System.out.println(sql);
			             stmt.executeUpdate(sql);		        	
					}		
				      
		      res.close();

		      con.close();
		    } catch (SQLException e) {
		      System.err.println("Exception: "+e.getMessage());
		    }
	}
}
