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


package eu.fp7.scase.restreviews.review;


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

import eu.fp7.scase.restreviews.utilities.HypermediaLink;
import eu.fp7.scase.restreviews.utilities.HibernateController;
import eu.fp7.scase.restreviews.product.JavaproductModel;

/* This class processes POST requests for review resources and creates the hypermedia to be returned to the client*/

public class PostreviewHandler{


    private HibernateController oHibernateController;
    private UriInfo oApplicationUri; //Standard datatype that holds information on the URI info of this request
	private String strResourcePath; //relative path to the current resource
    private JavareviewModel oJavareviewModel;
    private JavaproductModel oJavaproductModel;
	private String authHeader;
	private JavaaccountModel oAuthenticationAccount;
	private Boolean bIsClientAuthenticated = false;
	private AzPDP pdp;

    public PostreviewHandler(String authHeader, int productId, JavareviewModel oJavareviewModel, UriInfo oApplicationUri){
        this.oJavareviewModel = oJavareviewModel;
        this.oHibernateController = HibernateController.getHibernateControllerHandle();
        this.oApplicationUri = oApplicationUri;
		this.strResourcePath = calculateProperResourcePath();
        oJavaproductModel = new JavaproductModel();
        oJavaproductModel.setproductId(productId);
        oJavareviewModel.getSetOfParentJavaproductModel().add(this.oJavaproductModel);
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

    public JavareviewModel postJavareviewModel(){

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

	    if (pdp.getPermission(oAuthenticationAccount, "review", "product", Integer.toString(oJavaproductModel.getproductId()), oJavareviewModel, Action.POST)
				.equals(AuthorizationResultCode.PERMIT)){
			  oJavareviewModel.setaccount(this.oAuthenticationAccount);
        	  return createHypermedia(oHibernateController.postreview(oJavareviewModel));
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
    public JavareviewModel createHypermedia(JavareviewModel oJavareviewModel){

		        /* Create hypermedia links towards this specific review resource. These must be GET and POST as it is prescribed in the meta-models.*/
		        oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Get all reviews of this product", "GET", "Sibling"));
		        oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Create a new review", "POST", "Sibling"));
		
		        /* Then calculate the relative path to any resources that are related of this one and add the according hypermedia links to the Linklist.*/
		        String oRelativePath;
		        oRelativePath = this.strResourcePath;
		        if (pdp.getPermission(oAuthenticationAccount, "review", Integer.toString(oJavareviewModel.getreviewId()), null, Action.GET).equals(AuthorizationResultCode.PERMIT)){
		        	oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%s%s/%d", oApplicationUri.getBaseUri(), oRelativePath, oJavareviewModel.getreviewId()), String.valueOf(oJavareviewModel.gettitle()), "GET", "Child", oJavareviewModel.getreviewId()));
				}
		        if (pdp.getPermission(oAuthenticationAccount, "review", Integer.toString(oJavareviewModel.getreviewId()), null, Action.PUT).equals(AuthorizationResultCode.PERMIT)){
			        oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%s%s/%d", oApplicationUri.getBaseUri(), oRelativePath, oJavareviewModel.getreviewId()), String.valueOf(oJavareviewModel.gettitle()), "PUT", "Child", oJavareviewModel.getreviewId()));
				}
		        if (pdp.getPermission(oAuthenticationAccount, "review", Integer.toString(oJavareviewModel.getreviewId()), null, Action.DELETE).equals(AuthorizationResultCode.PERMIT)){
			        oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%s%s/%d", oApplicationUri.getBaseUri(), oRelativePath, oJavareviewModel.getreviewId()), String.valueOf(oJavareviewModel.gettitle()), "DELETE", "Child", oJavareviewModel.getreviewId()));
				}
		
		        /* Finally, calculate the relative path towards the resources of which this one is related.
		        Then add hypermedia links for each one of them*/
		        this.oJavaproductModel = HibernateController.getHibernateControllerHandle().getproduct(this.oJavaproductModel);
				if(this.oJavaproductModel.getSetOfParentJavaaccountModel() != null &&
		           this.oJavaproductModel.getSetOfParentJavaaccountModel().isEmpty() == false){
		        	oRelativePath = String.format("multiproduct/account/%d/%s", this.oJavaproductModel.getSetOfParentJavaaccountModel().iterator().next().getaccountId(), this.strResourcePath);
				}
		
		        else		if(this.oJavaproductModel.getSetOfParentJavaorderModel() != null &&
		           this.oJavaproductModel.getSetOfParentJavaorderModel().isEmpty() == false){
		        	oRelativePath = String.format("multiproduct/order/%d/%s", this.oJavaproductModel.getSetOfParentJavaorderModel().iterator().next().getorderId(), this.strResourcePath);
				}
		        int iLastSlashIndex = String.format("%s%s", oApplicationUri.getBaseUri(), oRelativePath).lastIndexOf("/");
				if (pdp.getPermission(oAuthenticationAccount, "product", Integer.toString(oJavaproductModel.getproductId()), null, Action.DELETE).equals(AuthorizationResultCode.PERMIT)){
			        oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), oRelativePath).substring(0, iLastSlashIndex), "Delete the parent JavaproductModel", "DELETE", "Parent"));
				}
				if (pdp.getPermission(oAuthenticationAccount, "product", Integer.toString(oJavaproductModel.getproductId()), null, Action.GET).equals(AuthorizationResultCode.PERMIT)){
			        oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), oRelativePath).substring(0, iLastSlashIndex), "Get the parent JavaproductModel", "GET", "Parent"));
				}
				if (pdp.getPermission(oAuthenticationAccount, "product", Integer.toString(oJavaproductModel.getproductId()), null, Action.PUT).equals(AuthorizationResultCode.PERMIT)){
			        oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), oRelativePath).substring(0, iLastSlashIndex), "Update the JavaproductModel", "PUT", "Parent"));
				}
		

        return oJavareviewModel;
    }
}
