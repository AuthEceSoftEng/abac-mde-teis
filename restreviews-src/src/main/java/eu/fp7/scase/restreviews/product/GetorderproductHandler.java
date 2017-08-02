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

import java.util.Iterator;
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
import eu.fp7.scase.restreviews.order.JavaorderModel;

import eu.fp7.scase.restreviews.account.JavaaccountModel;
import eu.fp7.scase.restreviews.product.JavaproductModel;
import eu.fp7.scase.restreviews.order.JavaorderModel;
import eu.fp7.scase.restreviews.review.JavareviewModel;

/* This class processes GET requests for product resources and creates the hypermedia to be returned to the client*/
public class GetorderproductHandler{


    private HibernateController oHibernateController;
    private UriInfo oApplicationUri; //Standard datatype that holds information on the URI info of this request
	private String strResourcePath; //relative path to the current resource
    private JavaproductModel oJavaproductModel;
    private JavaorderModel oJavaorderModel;
	private String authHeader;
	private JavaaccountModel oAuthenticationAccount;
	private Boolean bIsClientAuthenticated = false;
	private AzPDP pdp;

    public GetorderproductHandler(String authHeader, int orderId, int productId, UriInfo oApplicationUri){
        oJavaproductModel = new JavaproductModel();
        oJavaproductModel.setproductId(productId);
        this.oHibernateController = HibernateController.getHibernateControllerHandle();
        this.oApplicationUri = oApplicationUri;
		this.strResourcePath = calculateProperResourcePath();
        oJavaorderModel = new JavaorderModel();
        oJavaorderModel.setorderId(orderId);
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

    public JavaproductModel getJavaproductModel(){

    	//check if there is a non null authentication header
    	if(authHeader == null){
    		throw new WebApplicationException(Response.Status.FORBIDDEN);
    	}
		else if(authHeader.equalsIgnoreCase("guest")){ //if guest and authentication mode are allowed, check if the request originates from a guest user
	    	if (pdp.getPermission("product", Integer.toString(oJavaproductModel.getproductId()), null, Action.GET)
					.equals(AuthorizationResultCode.PERMIT)){
	        	return createHypermedia(oHibernateController.getproduct(oJavaproductModel));
	    	}else{
	    		throw new WebApplicationException(Response.Status.UNAUTHORIZED);
	    	}			
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

	    if (pdp.getPermission(oAuthenticationAccount, "product", Integer.toString(oJavaproductModel.getproductId()), null, Action.GET)
				.equals(AuthorizationResultCode.PERMIT)){
        	return createHypermedia(oHibernateController.getproduct(oJavaproductModel));
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
    public JavaproductModel createHypermedia(JavaproductModel oJavaproductModel){
		if (this.bIsClientAuthenticated == true){ //if the user is authenticated apply ABAC checks
		        /* Create hypermedia links towards this specific product resource. These can be GET, PUT and/or delete depending on what was specified in the service CIM.*/
		        oJavaproductModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Get the product", "GET", "Sibling"));
		        if (pdp.getPermission(oAuthenticationAccount, "product", Integer.toString(oJavaproductModel.getproductId()), null, Action.PUT).equals(AuthorizationResultCode.PERMIT)){
			        oJavaproductModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Update the product", "PUT", "Sibling"));
				}
		        if (pdp.getPermission(oAuthenticationAccount, "product", Integer.toString(oJavaproductModel.getproductId()), null, Action.DELETE).equals(AuthorizationResultCode.PERMIT)){
			        oJavaproductModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Delete the product", "DELETE", "Sibling"));
				}
		
		        /* Calculate the relative path towards any related resources of this one. Then add each new hypermedia link with that relative path to the hypermedia linklist to be sent back to client.*/
		        String oRelativePath;
		        oRelativePath = this.strResourcePath.substring(this.strResourcePath.indexOf("/product") + 1);
		        oJavaproductModel.getlinklist().add(new HypermediaLink(String.format("%s%s/%s", oApplicationUri.getBaseUri(), oRelativePath, "review"), "Get all the reviews of this product", "GET", "Child"));
		        if (pdp.getPermission(oAuthenticationAccount, "review", "product", Integer.toString(oJavaproductModel.getproductId()), null, Action.POST).equals(AuthorizationResultCode.PERMIT)){
			        oJavaproductModel.getlinklist().add(new HypermediaLink(String.format("%s%s/%s", oApplicationUri.getBaseUri(), oRelativePath, "review"), "Create a new review for this product", "POST", "Child"));
				}
		
		        /* Finally, truncate the current URI so as to point to the resource manager of which this resource is related.
		        Then create the hypermedia links towards the parent resources.*/
		        int iLastSlashIndex = String.format("%s%s", oApplicationUri.getBaseUri(), String.format("%s", this.strResourcePath).replaceAll("multiproduct","multiproductManager")).lastIndexOf("/");
				if (pdp.getPermission(oAuthenticationAccount, "product", Integer.toString(oJavaorderModel.getorderId()), null, Action.POST).equals(AuthorizationResultCode.PERMIT)){
			        oJavaproductModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), String.format("%s", this.strResourcePath).replaceAll("multiproduct","multiproductManager")).substring(0, iLastSlashIndex), "Create a new product", "POST", "Parent"));
				}
		        oJavaproductModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), String.format("%s", this.strResourcePath).replaceAll("multiproduct","multiproductManager")).substring(0, iLastSlashIndex), "Get all product of this order", "GET", "Parent"));
		
				/*Add any hypermedia link to specific parent resources*/
		        JavaproductModel oAccountParentsOfJavaproductModel = HibernateController.getHibernateControllerHandle().getproduct(oJavaproductModel);
				Iterator<JavaaccountModel> iteratorOfParentaccount = oAccountParentsOfJavaproductModel.getSetOfParentJavaaccountModel().iterator();
		
				while(iteratorOfParentaccount.hasNext()){
					JavaaccountModel oParentJavaaccountModel = iteratorOfParentaccount.next();
					if (pdp.getPermission(oAuthenticationAccount, "order", Integer.toString(oJavaorderModel.getorderId()), null, Action.GET).equals(AuthorizationResultCode.PERMIT)){
						oJavaproductModel.getlinklist().add(new HypermediaLink(String.format("%s%s/%d", oApplicationUri.getBaseUri(), "account", oParentJavaaccountModel.getaccountId()), "account", "GET", "Parent"));
					}
					if (pdp.getPermission(oAuthenticationAccount, "order", Integer.toString(oJavaorderModel.getorderId()), null, Action.PUT).equals(AuthorizationResultCode.PERMIT)){
						oJavaproductModel.getlinklist().add(new HypermediaLink(String.format("%s%s/%d", oApplicationUri.getBaseUri(), "account", oParentJavaaccountModel.getaccountId()), "account", "PUT", "Parent"));
					}
					if (pdp.getPermission(oAuthenticationAccount, "order", Integer.toString(oJavaorderModel.getorderId()), null, Action.DELETE).equals(AuthorizationResultCode.PERMIT)){
						oJavaproductModel.getlinklist().add(new HypermediaLink(String.format("%s%s/%d", oApplicationUri.getBaseUri(), "account", oParentJavaaccountModel.getaccountId()), "account", "DELETE", "Parent"));
					}
				}
		
		        JavaproductModel oOrderParentsOfJavaproductModel = HibernateController.getHibernateControllerHandle().getproduct(oJavaproductModel);
				Iterator<JavaorderModel> iteratorOfParentorder = oOrderParentsOfJavaproductModel.getSetOfParentJavaorderModel().iterator();
		
				while(iteratorOfParentorder.hasNext()){
					JavaorderModel oParentJavaorderModel = iteratorOfParentorder.next();
					oParentJavaorderModel = HibernateController.getHibernateControllerHandle().getorder(oParentJavaorderModel);
					if (pdp.getPermission(oAuthenticationAccount, "order", Integer.toString(oJavaorderModel.getorderId()), null, Action.GET).equals(AuthorizationResultCode.PERMIT)){
						oJavaproductModel.getlinklist().add(new HypermediaLink(String.format("%s%s/%d/%s/%d", oApplicationUri.getBaseUri(), "account", oParentJavaorderModel.getSetOfParentJavaaccountModel().iterator().next().getaccountId(), "order", oParentJavaorderModel.getorderId()), "order", "GET", "Parent"));
					}
					if (pdp.getPermission(oAuthenticationAccount, "order", Integer.toString(oJavaorderModel.getorderId()), null, Action.PUT).equals(AuthorizationResultCode.PERMIT)){
						oJavaproductModel.getlinklist().add(new HypermediaLink(String.format("%s%s/%d/%s/%d", oApplicationUri.getBaseUri(), "account", oParentJavaorderModel.getSetOfParentJavaaccountModel().iterator().next().getaccountId(), "order", oParentJavaorderModel.getorderId()), "order", "PUT", "Parent"));
					}
					if (pdp.getPermission(oAuthenticationAccount, "order", Integer.toString(oJavaorderModel.getorderId()), null, Action.DELETE).equals(AuthorizationResultCode.PERMIT)){
						oJavaproductModel.getlinklist().add(new HypermediaLink(String.format("%s%s/%d/%s/%d", oApplicationUri.getBaseUri(), "account", oParentJavaorderModel.getSetOfParentJavaaccountModel().iterator().next().getaccountId(), "order", oParentJavaorderModel.getorderId()), "order", "DELETE", "Parent"));
					}
				}
		
		 
		}
		else{ //otherwise apply first Basic Authentication checks and then ABAC
		        /* Create hypermedia links towards this specific product resource. These can be GET, PUT and/or delete depending on what was specified in the service CIM.*/
		        oJavaproductModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Get the product", "GET", "Sibling"));
		
		        /* Calculate the relative path towards any related resources of this one. Then add each new hypermedia link with that relative path to the hypermedia linklist to be sent back to client.*/
		        String oRelativePath;
		        oRelativePath = this.strResourcePath.substring(this.strResourcePath.indexOf("/product") + 1);
		        oJavaproductModel.getlinklist().add(new HypermediaLink(String.format("%s%s/%s", oApplicationUri.getBaseUri(), oRelativePath, "review"), "Get all the reviews of this product", "GET", "Child"));
		
		        /* Finally, truncate the current URI so as to point to the resource manager of which this resource is related.
		        Then create the hypermedia links towards the parent resources.*/
		        int iLastSlashIndex = String.format("%s%s", oApplicationUri.getBaseUri(), String.format("%s", this.strResourcePath).replaceAll("multiproduct","multiproductManager")).lastIndexOf("/");
		        oJavaproductModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), String.format("%s", this.strResourcePath).replaceAll("multiproduct","multiproductManager")).substring(0, iLastSlashIndex), "Get all product of this order", "GET", "Parent"));
		 
				/*Add any hypermedia link to specific parent resources*/
		        JavaproductModel oAccountParentsOfJavaproductModel = HibernateController.getHibernateControllerHandle().getproduct(oJavaproductModel);
				Iterator<JavaaccountModel> iteratorOfParentaccount = oAccountParentsOfJavaproductModel.getSetOfParentJavaaccountModel().iterator();
		
				while(iteratorOfParentaccount.hasNext()){
					JavaaccountModel oParentJavaaccountModel = iteratorOfParentaccount.next();
				}
		
		        JavaproductModel oOrderParentsOfJavaproductModel = HibernateController.getHibernateControllerHandle().getproduct(oJavaproductModel);
				Iterator<JavaorderModel> iteratorOfParentorder = oOrderParentsOfJavaproductModel.getSetOfParentJavaorderModel().iterator();
		
				while(iteratorOfParentorder.hasNext()){
					JavaorderModel oParentJavaorderModel = iteratorOfParentorder.next();
					oParentJavaorderModel = HibernateController.getHibernateControllerHandle().getorder(oParentJavaorderModel);
				}
		
		
		}

       return oJavaproductModel;
    }
}
