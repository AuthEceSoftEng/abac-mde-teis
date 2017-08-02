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

import java.util.Iterator;
import eu.fp7.scase.restreviews.utilities.HypermediaLink;
import eu.fp7.scase.restreviews.utilities.HibernateController;
import eu.fp7.scase.restreviews.product.JavaproductModel;

/* This class processes GET requests for review resources and creates the hypermedia to be returned to the client*/
public class GetreviewListHandler{

    private HibernateController oHibernateController;
    private UriInfo oApplicationUri; //Standard datatype that holds information on the URI info of this request
	private String strResourcePath; //relative path to the current resource
    private JavaproductModel oJavaproductModel;
	private String authHeader;
	private JavaaccountModel oAuthenticationAccount;
	private Boolean bIsClientAuthenticated = false;
	private AzPDP pdp;

    public GetreviewListHandler(String authHeader, int productId, UriInfo oApplicationUri){
        this.oHibernateController = HibernateController.getHibernateControllerHandle();
        this.oApplicationUri = oApplicationUri;
		this.strResourcePath = calculateProperResourcePath();
        oJavaproductModel = new JavaproductModel();
        oJavaproductModel.setproductId(productId);
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

    public JavareviewModelManager getJavareviewModelManager(){

    	//check if there is a non null authentication header
    	if(authHeader == null){
    		throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    	}
		else if(authHeader.equalsIgnoreCase("guest")){ //if guest and authentication mode are allowed, check if the request originates from a guest user
        	oJavaproductModel = oHibernateController.getreviewList(oJavaproductModel);
        	return createHypermedia(oJavaproductModel);
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

        oJavaproductModel = oHibernateController.getreviewList(oJavaproductModel);
        return createHypermedia(oJavaproductModel);
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
    public JavareviewModelManager createHypermedia(JavaproductModel oJavaproductModel){
        JavareviewModelManager oJavareviewModelManager = new JavareviewModelManager();

		if (this.bIsClientAuthenticated == true){ //if the user is authenticated apply ABAC checks
		        /* Create hypermedia links towards this specific review resource. These must be GET and POST as it is prescribed in the meta-models.*/
		        oJavareviewModelManager.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Get all reviews of this product", "GET", "Sibling"));
				if (pdp.getPermission(oAuthenticationAccount, "review", "product", Integer.toString(oJavaproductModel.getproductId()), null, Action.POST).equals(AuthorizationResultCode.PERMIT)){
			        oJavareviewModelManager.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Create a new review", "POST", "Sibling"));
				}
		
		        /* Then calculate the relative path to any related resource of this one and add for each one a hypermedia link to the Linklist.*/
		        String oRelativePath;
		        oRelativePath = this.strResourcePath;
		        Iterator<JavareviewModel> setIterator = oJavaproductModel.getSetOfJavareviewModel().iterator();
		        while(setIterator.hasNext()){
		            JavareviewModel oNextJavareviewModel = new JavareviewModel();
		            oNextJavareviewModel = setIterator.next();
		        	if (pdp.getPermission(oAuthenticationAccount, "review", Integer.toString(oNextJavareviewModel.getreviewId()), null, Action.GET).equals(AuthorizationResultCode.PERMIT)){
			            oJavareviewModelManager.getlinklist().add(new HypermediaLink(String.format("%s%s/%d", oApplicationUri.getBaseUri(), oRelativePath, oNextJavareviewModel.getreviewId()), String.valueOf(oNextJavareviewModel.gettitle()), "GET", "Child", oNextJavareviewModel.getreviewId()));
					}
		        }
		
		        /* Finally calculate the relative path towards the resources of which this one is related and add one hypermedia link for each one of them in the Linklist.*/
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
		        	oJavareviewModelManager.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), oRelativePath).substring(0, iLastSlashIndex), "Delete the parent JavaproductModel", "DELETE", "Parent"));
				}
				if (pdp.getPermission(oAuthenticationAccount, "product", Integer.toString(oJavaproductModel.getproductId()), null, Action.GET).equals(AuthorizationResultCode.PERMIT)){
			        oJavareviewModelManager.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), oRelativePath).substring(0, iLastSlashIndex), "Get the parent JavaproductModel", "GET", "Parent"));
				}
				if (pdp.getPermission(oAuthenticationAccount, "product", Integer.toString(oJavaproductModel.getproductId()), null, Action.PUT).equals(AuthorizationResultCode.PERMIT)){
			        oJavareviewModelManager.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), oRelativePath).substring(0, iLastSlashIndex), "Update the JavaproductModel", "PUT", "Parent"));
				}
		
		}
		else{ //otherwise apply first Basic Authentication checks and then ABAC
		        /* Create hypermedia links towards this specific review resource. These must be GET and POST as it is prescribed in the meta-models.*/
		        oJavareviewModelManager.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Get all reviews of this product", "GET", "Sibling"));
		
		        /* Then calculate the relative path to any related resource of this one and add for each one a hypermedia link to the Linklist.*/
		        String oRelativePath;
		        oRelativePath = this.strResourcePath;
		        Iterator<JavareviewModel> setIterator = oJavaproductModel.getSetOfJavareviewModel().iterator();
		        while(setIterator.hasNext()){
		            JavareviewModel oNextJavareviewModel = new JavareviewModel();
		            oNextJavareviewModel = setIterator.next();
		        	if (pdp.getPermission("review", Integer.toString(oNextJavareviewModel.getreviewId()), null, Action.GET).equals(AuthorizationResultCode.PERMIT)){
			            oJavareviewModelManager.getlinklist().add(new HypermediaLink(String.format("%s%s/%d", oApplicationUri.getBaseUri(), oRelativePath, oNextJavareviewModel.getreviewId()), String.valueOf(oNextJavareviewModel.gettitle()), "GET", "Child", oNextJavareviewModel.getreviewId()));
					}
		        }
		
		        /* Finally calculate the relative path towards the resources of which this one is related and add one hypermedia link for each one of them in the Linklist.*/
				if(this.oJavaproductModel.getSetOfParentJavaaccountModel() != null && 
				   this.oJavaproductModel.getSetOfParentJavaaccountModel().isEmpty() == false){
		        	oRelativePath = String.format("multiproduct/account/%d/%s", this.oJavaproductModel.getSetOfParentJavaaccountModel().iterator().next().getaccountId(), this.strResourcePath);
				}
		
		        else		if(this.oJavaproductModel.getSetOfParentJavaorderModel() != null && 
				   this.oJavaproductModel.getSetOfParentJavaorderModel().isEmpty() == false){
		        	oRelativePath = String.format("multiproduct/order/%d/%s", this.oJavaproductModel.getSetOfParentJavaorderModel().iterator().next().getorderId(), this.strResourcePath);
				}
		        int iLastSlashIndex = String.format("%s%s", oApplicationUri.getBaseUri(), oRelativePath).lastIndexOf("/");
				if (pdp.getPermission("product", Integer.toString(oJavaproductModel.getproductId()), null, Action.DELETE).equals(AuthorizationResultCode.PERMIT)){
			        oJavareviewModelManager.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), oRelativePath).substring(0, iLastSlashIndex), "Delete the parent JavaproductModel", "DELETE", "Parent"));
				}
				if (pdp.getPermission("product", Integer.toString(oJavaproductModel.getproductId()), null, Action.GET).equals(AuthorizationResultCode.PERMIT)){
			        oJavareviewModelManager.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), oRelativePath).substring(0, iLastSlashIndex), "Get the parent JavaproductModel", "GET", "Parent"));
				}
				if (pdp.getPermission("product", Integer.toString(oJavaproductModel.getproductId()), null, Action.PUT).equals(AuthorizationResultCode.PERMIT)){
			        oJavareviewModelManager.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), oRelativePath).substring(0, iLastSlashIndex), "Update the JavaproductModel", "PUT", "Parent"));
				}
		
		}

        return oJavareviewModelManager;
    }
}
