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


package eu.fp7.scase.restreviews.product;


import javax.ws.rs.core.UriInfo;
import java.util.Iterator;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import com.sun.jersey.core.util.Base64;
import eu.fp7.scase.restreviews.account.JavaaccountModel;

import eu.fp7.scase.restreviews.utilities.authorization.core.AzPDP;
import eu.fp7.scase.restreviews.utilities.authorization.core.AzRequest;
import eu.fp7.scase.restreviews.utilities.authorization.enums.Action;
import eu.fp7.scase.restreviews.utilities.authorization.enums.AuthorizationResultCode;

import java.util.Iterator;
import eu.fp7.scase.restreviews.utilities.HypermediaLink;
import eu.fp7.scase.restreviews.utilities.HibernateController;
import eu.fp7.scase.restreviews.account.JavaaccountModel;

/* This class processes GET requests for product resources and creates the hypermedia to be returned to the client*/
public class GetaccountproductListHandler{


    private HibernateController oHibernateController;
    private UriInfo oApplicationUri; //Standard datatype that holds information on the URI info of this request
	private String strResourcePath; //relative path to the current resource
    private JavaaccountModel oJavaaccountModel;
	private String authHeader;
	private JavaaccountModel oAuthenticationAccount;
	private Boolean bIsClientAuthenticated = false;
	private AzPDP pdp;

    public GetaccountproductListHandler(String authHeader, int accountId, UriInfo oApplicationUri){
        this.oHibernateController = HibernateController.getHibernateControllerHandle();
        this.oApplicationUri = oApplicationUri;
		this.strResourcePath = calculateProperResourcePath();
        oJavaaccountModel = new JavaaccountModel();
        oJavaaccountModel.setaccountId(accountId);
		this.authHeader = authHeader;
		this.oAuthenticationAccount = new JavaaccountModel(); 
		this.pdp = new AzPDP();
    }

	public String calculateProperResourcePath(){
    	if(this.oApplicationUri.getPath().lastIndexOf('/') == this.oApplicationUri.getPath().length() - 1){
        	return this.oApplicationUri.getPath().substring(0, this.oApplicationUri.getPath().length() - 1);
    	}
    	else{
        	return this.oApplicationUri.getPath();
    	}
	}

    public JavaproductModelManager getJavaproductModelManager(){

    	//check if there is a non null authentication header
    	if(authHeader == null){
    		throw new WebApplicationException(Response.Status.FORBIDDEN);
    	}
		else if(authHeader.equalsIgnoreCase("guest")){ //if guest and authentication mode are allowed, check if the request originates from a guest user
			oJavaaccountModel = oHibernateController.getaccountproductList(oJavaaccountModel);
        	return createHypermedia(oJavaaccountModel);
		}
		else{
	    	//decode the auth header
    		decodeAuthorizationHeader();

        	//authenticate the user against the database
        	oAuthenticationAccount = oHibernateController.authenticateUser(oAuthenticationAccount);

			//check if the authentication failed
			if(oAuthenticationAccount == null){
        		throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        	}
			else{
				this.bIsClientAuthenticated = true;
			}
		}

        oJavaaccountModel = oHibernateController.getaccountproductList(oJavaaccountModel);
        return createHypermedia(oJavaaccountModel);
    }

	/* This function performs the decoding of the authentication header */
    public void decodeAuthorizationHeader()
    {
    	//check if this request has basic authentication
    	if( !authHeader.contains("Basic "))
    	{
    		throw new WebApplicationException(Response.Status.BAD_REQUEST);
    	}
    	
        authHeader = authHeader.substring("Basic ".length());
        String[] decodedHeader;
        decodedHeader = Base64.base64Decode(authHeader).split(":");
        
        if( decodedHeader == null)
        {
        	throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        
        oAuthenticationAccount.setemail(decodedHeader[0]);
        oAuthenticationAccount.setpassword(decodedHeader[1]);
    }

    /* This function produces hypermedia links to be sent to the client so as it will be able to forward the application state in a valid way.*/
    public JavaproductModelManager createHypermedia(JavaaccountModel oJavaaccountModel){
        JavaproductModelManager oJavaproductModelManager = new JavaproductModelManager();

		if (this.bIsClientAuthenticated == true){ //if the user is authenticated apply ABAC checks
		        /* Create hypermedia links towards this specific product resource. These must be GET and POST as it is prescribed in the meta-models.*/
		        oJavaproductModelManager.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Get all products of this account", "GET", "Sibling"));
				if (pdp.getPermission(oAuthenticationAccount, "product", "account", Integer.toString(oJavaaccountModel.getaccountId()), null, Action.POST).equals(AuthorizationResultCode.PERMIT)){
			        oJavaproductModelManager.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Create a new product", "POST", "Sibling"));
				}
		
		        /* Then calculate the relative path to any related resource of this one and add for each one a hypermedia link to the Linklist.*/
		        String oRelativePath;
		        oRelativePath = this.strResourcePath.replaceAll("multiproductManager/", "multiproduct/");
		        Iterator<JavaproductModel> setIterator = oJavaaccountModel.getSetOfJavaproductModel().iterator();
		        while(setIterator.hasNext()){
		            JavaproductModel oNextJavaproductModel = new JavaproductModel();
		            oNextJavaproductModel = setIterator.next();
		        	if (pdp.getPermission(oAuthenticationAccount, "product", Integer.toString(oNextJavaproductModel.getproductId()), null, Action.GET).equals(AuthorizationResultCode.PERMIT)){
			            oJavaproductModelManager.getlinklist().add(new HypermediaLink(String.format("%s%s/%d", oApplicationUri.getBaseUri(), oRelativePath, oNextJavaproductModel.getproductId()), String.valueOf(oNextJavaproductModel.gettitle()), "GET", "Child", oNextJavaproductModel.getproductId()));
					}
		        }
		
		        /* Finally calculate the relative path towards the resources of which this one is related and add one hypermedia link for each one of them in the Linklist.*/
				oRelativePath = this.strResourcePath.replaceAll("multiproductManager/", "");
		        int iLastSlashIndex = String.format("%s%s", oApplicationUri.getBaseUri(), oRelativePath).lastIndexOf("/");
				if (pdp.getPermission(oAuthenticationAccount, "account", Integer.toString(oJavaaccountModel.getaccountId()), null, Action.DELETE).equals(AuthorizationResultCode.PERMIT)){
			        oJavaproductModelManager.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), oRelativePath).substring(0, iLastSlashIndex), "Delete the parent account", "DELETE", "Parent"));
				}
				if (pdp.getPermission(oAuthenticationAccount, "account", Integer.toString(oJavaaccountModel.getaccountId()), null, Action.GET).equals(AuthorizationResultCode.PERMIT)){
			        oJavaproductModelManager.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), oRelativePath).substring(0, iLastSlashIndex), "Get the parent account", "GET", "Parent"));
				}
				if (pdp.getPermission(oAuthenticationAccount, "account", Integer.toString(oJavaaccountModel.getaccountId()), null, Action.PUT).equals(AuthorizationResultCode.PERMIT)){
			        oJavaproductModelManager.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), oRelativePath).substring(0, iLastSlashIndex), "Update the account", "PUT", "Parent"));
				}
		
		}
		else{ //otherwise apply first Basic Authentication checks and then ABAC
		         /* Create hypermedia links towards this specific product resource. These must be GET and POST as it is prescribed in the meta-models.*/
		        oJavaproductModelManager.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Get all products of this account", "GET", "Sibling"));
		
		        /* Then calculate the relative path to any related resource of this one and add for each one a hypermedia link to the Linklist.*/
		        String oRelativePath;
		        oRelativePath = this.strResourcePath.replaceAll("multiproductManager/", "multiproduct/");
		        Iterator<JavaproductModel> setIterator = oJavaaccountModel.getSetOfJavaproductModel().iterator();
		        while(setIterator.hasNext()){
		            JavaproductModel oNextJavaproductModel = new JavaproductModel();
		            oNextJavaproductModel = setIterator.next();
		        	if (pdp.getPermission("product", Integer.toString(oNextJavaproductModel.getproductId()), null, Action.GET).equals(AuthorizationResultCode.PERMIT)){
			            oJavaproductModelManager.getlinklist().add(new HypermediaLink(String.format("%s%s/%d", oApplicationUri.getBaseUri(), oRelativePath, oNextJavaproductModel.getproductId()), String.valueOf(oNextJavaproductModel.gettitle()), "GET", "Child", oNextJavaproductModel.getproductId()));
					}
		        }
		
		        /* Finally calculate the relative path towards the resources of which this one is related and add one hypermedia link for each one of them in the Linklist.*/
				oRelativePath = this.strResourcePath.replaceAll("multiproductManager/", "");
		        int iLastSlashIndex = String.format("%s%s", oApplicationUri.getBaseUri(), oRelativePath).lastIndexOf("/");
		
		}

        return oJavaproductModelManager;
    }
}
