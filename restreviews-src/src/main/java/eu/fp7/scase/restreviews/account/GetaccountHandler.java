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


package eu.fp7.scase.restreviews.account;


import javax.ws.rs.core.UriInfo;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import com.sun.jersey.core.util.Base64;
import eu.fp7.scase.restreviews.account.JavaaccountModel;
import eu.fp7.scase.restreviews.utilities.authorization.core.AzPDP;
import eu.fp7.scase.restreviews.utilities.authorization.core.AzRequest;
import eu.fp7.scase.restreviews.utilities.authorization.enums.Action;
import eu.fp7.scase.restreviews.utilities.authorization.enums.AuthorizationResultCode;

import eu.fp7.scase.restreviews.utilities.HypermediaLink;
import eu.fp7.scase.restreviews.utilities.HibernateController;


/* This class processes GET requests for account resources and creates the hypermedia to be returned to the client*/
public class GetaccountHandler{


    private HibernateController oHibernateController;
    private UriInfo oApplicationUri; //Standard datatype that holds information on the URI info of this request
	private String strResourcePath; //relative path to the current resource
    private JavaaccountModel oJavaaccountModel;
	private String authHeader;
	private JavaaccountModel oAuthenticationAccount;
	private Boolean bIsClientAuthenticated = false;
	private AzPDP pdp;

    public GetaccountHandler(String authHeader, int accountId, UriInfo oApplicationUri){
        oJavaaccountModel = new JavaaccountModel();
        oJavaaccountModel.setaccountId(accountId);
        this.oHibernateController = HibernateController.getHibernateControllerHandle();
        this.oApplicationUri = oApplicationUri;
		this.strResourcePath = calculateProperResourcePath();
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

    public JavaaccountModel getJavaaccountModel(){

    	//check if there is a non null authentication header
    	if(authHeader == null){
    		throw new WebApplicationException(Response.Status.FORBIDDEN);
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

	    if (pdp.getPermission(oAuthenticationAccount, "account", Integer.toString(oJavaaccountModel.getaccountId()), null, Action.GET)
				.equals(AuthorizationResultCode.PERMIT)){
        	return createHypermedia(oHibernateController.getaccount(oJavaaccountModel));
    	}else{
    		throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    	}		
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
    public JavaaccountModel createHypermedia(JavaaccountModel oJavaaccountModel){

		        /* Create hypermedia links towards this specific account resource. These can be GET, PUT and/or delete depending on what was specified in the service CIM.*/
				//No Authorization check required, since the user already accessed this resource
		 		oJavaaccountModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Get the account", "GET", "Sibling"));
		        if (pdp.getPermission(oAuthenticationAccount, "account", Integer.toString(oJavaaccountModel.getaccountId()), null, Action.PUT).equals(AuthorizationResultCode.PERMIT)){
		            oJavaaccountModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Update the account", "PUT", "Sibling"));
		        }		
		        if (pdp.getPermission(oAuthenticationAccount, "account", Integer.toString(oJavaaccountModel.getaccountId()), null, Action.DELETE).equals(AuthorizationResultCode.PERMIT)){
		            oJavaaccountModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Delete the account", "DELETE", "Sibling"));
		        }
		
		        /* Calculate the relative path towards any related resources of this one. Then add each new hypermedia link with that relative path to the hypermedia linklist to be sent back to client.*/
		        String oRelativePath;
				oRelativePath = this.strResourcePath;
				//No Authorization check required, since the user will only get hypermedia links of resources he has access to.
		        oJavaaccountModel.getlinklist().add(new HypermediaLink(String.format("%s%s/%s/%s", oApplicationUri.getBaseUri(), "multiproductManager", oRelativePath, "product"), "Get all the products of this account", "GET", "Child"));
		        if (pdp.getPermission(oAuthenticationAccount, "product", "account", Integer.toString(oJavaaccountModel.getaccountId()), null, Action.POST).equals(AuthorizationResultCode.PERMIT)){
		        	oJavaaccountModel.getlinklist().add(new HypermediaLink(String.format("%s%s/%s/%s", oApplicationUri.getBaseUri(), "multiproductManager", oRelativePath, "product"), "Create a new product for this account", "POST", "Child"));
				}
		
				//No Authorization check required, since the user will only get hypermedia links of resources he has access to.
		        oJavaaccountModel.getlinklist().add(new HypermediaLink(String.format("%s%s/%s", oApplicationUri.getBaseUri(), oRelativePath, "order"), "Get all the orders of this account", "GET", "Child"));
		        if (pdp.getPermission(oAuthenticationAccount, "order", "account", Integer.toString(oJavaaccountModel.getaccountId()), null, Action.POST).equals(AuthorizationResultCode.PERMIT)){
		        	oJavaaccountModel.getlinklist().add(new HypermediaLink(String.format("%s%s/%s", oApplicationUri.getBaseUri(), oRelativePath, "order"), "Create a new order for this account", "POST", "Child"));
				}
		
		        /* Finally, truncate the current URI so as to point to the resource manager of which this resource is related.
		        Then create the hypermedia links towards the parent resources.*/
		        int iLastSlashIndex = String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath).lastIndexOf("/");
		        if (pdp.getPermission(oAuthenticationAccount, "account", null, null, Action.POST).equals(AuthorizationResultCode.PERMIT)){
		        	oJavaaccountModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath).substring(0, iLastSlashIndex), "Create a new account", "POST", "Parent"));
				}
		        oJavaaccountModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath).substring(0, iLastSlashIndex), "Get all accounts", "GET", "Parent"));

        return oJavaaccountModel;
    }
}
