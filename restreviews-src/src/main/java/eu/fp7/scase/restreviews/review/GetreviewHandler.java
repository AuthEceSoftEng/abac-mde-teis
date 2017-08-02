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
import eu.fp7.scase.restreviews.product.JavaproductModel;

import eu.fp7.scase.restreviews.account.JavaaccountModel;
import eu.fp7.scase.restreviews.product.JavaproductModel;
import eu.fp7.scase.restreviews.order.JavaorderModel;
import eu.fp7.scase.restreviews.review.JavareviewModel;

/* This class processes GET requests for review resources and creates the hypermedia to be returned to the client*/
public class GetreviewHandler{


    private HibernateController oHibernateController;
    private UriInfo oApplicationUri; //Standard datatype that holds information on the URI info of this request
	private String strResourcePath; //relative path to the current resource
    private JavareviewModel oJavareviewModel;
    private JavaproductModel oJavaproductModel;
	private String authHeader;
	private JavaaccountModel oAuthenticationAccount;
	private Boolean bIsClientAuthenticated = false;
	private AzPDP pdp;

    public GetreviewHandler(String authHeader, int productId, int reviewId, UriInfo oApplicationUri){
        oJavareviewModel = new JavareviewModel();
        oJavareviewModel.setreviewId(reviewId);
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

    public JavareviewModel getJavareviewModel(){

    	//check if there is a non null authentication header
    	if(authHeader == null){
    		throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    	}
		else if(authHeader.equalsIgnoreCase("guest")){ //if guest and authentication mode are allowed, check if the request originates from a guest user
	    	if (pdp.getPermission("review", Integer.toString(oJavareviewModel.getreviewId()), null, Action.GET)
					.equals(AuthorizationResultCode.PERMIT)){
	        	return createHypermedia(oHibernateController.getreview(oJavareviewModel));
	    	}else{
	    		throw new WebApplicationException(Response.Status.FORBIDDEN);
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

	    if (pdp.getPermission(oAuthenticationAccount, "review", Integer.toString(oJavareviewModel.getreviewId()), null, Action.GET)
				.equals(AuthorizationResultCode.PERMIT)){
        	return createHypermedia(oHibernateController.getreview(oJavareviewModel));
    	}else{
    		throw new WebApplicationException(Response.Status.FORBIDDEN);
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

		if (this.bIsClientAuthenticated == true){ //if the user is authenticated apply ABAC checks
		        /* Create hypermedia links towards this specific review resource. These can be GET, PUT and/or delete depending on what was specified in the service CIM.*/
		        oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Get the review", "GET", "Sibling"));
		        if (pdp.getPermission(oAuthenticationAccount, "review", Integer.toString(oJavareviewModel.getreviewId()), null, Action.PUT).equals(AuthorizationResultCode.PERMIT)){
			        oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Update the review", "PUT", "Sibling"));
				}
		        if (pdp.getPermission(oAuthenticationAccount, "review", Integer.toString(oJavareviewModel.getreviewId()), null, Action.DELETE).equals(AuthorizationResultCode.PERMIT)){
			        oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Delete the review", "DELETE", "Sibling"));
				}
		
		
		       /* Finally, truncate the current URI so as to point to the resource manager of which this resource is related.
		        Then create the hypermedia links towards the parent resources.*/
		        int iLastSlashIndex = String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath).lastIndexOf("/");
				if (pdp.getPermission(oAuthenticationAccount, "review", "product", Integer.toString(oJavaproductModel.getproductId()), null, Action.POST).equals(AuthorizationResultCode.PERMIT)){
			        oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath).substring(0, iLastSlashIndex), "Create a new review", "POST", "Parent"));
				}
		        oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath).substring(0, iLastSlashIndex), "Get all reviews of this product", "GET", "Parent"));
		
		
				/*Add any hypermedia link to specific parent resources*/
		        JavareviewModel oParentsOfJavareviewModel = HibernateController.getHibernateControllerHandle().getreview(oJavareviewModel);
				Iterator<JavaproductModel> iteratorOfParentproduct = oParentsOfJavareviewModel.getSetOfParentJavaproductModel().iterator();
		
				while(iteratorOfParentproduct.hasNext()){
					JavaproductModel oParentJavaproductModel = iteratorOfParentproduct.next();
					oParentJavaproductModel = HibernateController.getHibernateControllerHandle().getproduct(oParentJavaproductModel);
		
					if(oParentJavaproductModel.getSetOfParentJavaaccountModel().isEmpty() == false && oParentJavaproductModel.getSetOfParentJavaaccountModel() != null){
						JavaaccountModel oGParentJavaaccountModel = oParentJavaproductModel.getSetOfParentJavaaccountModel().iterator().next();
						if (pdp.getPermission(oAuthenticationAccount, "product", Integer.toString(oJavaproductModel.getproductId()), null, Action.GET).equals(AuthorizationResultCode.PERMIT)){
							oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%smulti%s/%s/%d/%s/%d", oApplicationUri.getBaseUri(), "product", "account", oGParentJavaaccountModel.getaccountId(), "product", oParentJavaproductModel.getproductId()), "product", "GET", "Parent"));
						}
						if (pdp.getPermission(oAuthenticationAccount, "product", Integer.toString(oJavaproductModel.getproductId()), null, Action.PUT).equals(AuthorizationResultCode.PERMIT)){
							oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%smulti%s/%s/%d/%s/%d", oApplicationUri.getBaseUri(), "product", "account", oGParentJavaaccountModel.getaccountId(), "product", oParentJavaproductModel.getproductId()), "product", "PUT", "Parent"));
						}
						if (pdp.getPermission(oAuthenticationAccount, "product", Integer.toString(oJavaproductModel.getproductId()), null, Action.DELETE).equals(AuthorizationResultCode.PERMIT)){
							oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%smulti%s/%s/%d/%s/%d", oApplicationUri.getBaseUri(), "product", "account", oGParentJavaaccountModel.getaccountId(), "product", oParentJavaproductModel.getproductId()), "product", "DELETE", "Parent"));
						}
					}
		
		        else			if(oParentJavaproductModel.getSetOfParentJavaorderModel().isEmpty() == false && oParentJavaproductModel.getSetOfParentJavaorderModel() != null){
						JavaorderModel oGParentJavaorderModel = oParentJavaproductModel.getSetOfParentJavaorderModel().iterator().next();
						if (pdp.getPermission(oAuthenticationAccount, "product", Integer.toString(oJavaproductModel.getproductId()), null, Action.GET).equals(AuthorizationResultCode.PERMIT)){
							oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%smulti%s/%s/%d/%s/%d", oApplicationUri.getBaseUri(), "product", "order", oGParentJavaorderModel.getorderId(), "product", oParentJavaproductModel.getproductId()), "product", "GET", "Parent"));
						}
						if (pdp.getPermission(oAuthenticationAccount, "product", Integer.toString(oJavaproductModel.getproductId()), null, Action.PUT).equals(AuthorizationResultCode.PERMIT)){
							oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%smulti%s/%s/%d/%s/%d", oApplicationUri.getBaseUri(), "product", "order", oGParentJavaorderModel.getorderId(), "product", oParentJavaproductModel.getproductId()), "product", "PUT", "Parent"));
						}
						if (pdp.getPermission(oAuthenticationAccount, "product", Integer.toString(oJavaproductModel.getproductId()), null, Action.DELETE).equals(AuthorizationResultCode.PERMIT)){
							oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%smulti%s/%s/%d/%s/%d", oApplicationUri.getBaseUri(), "product", "order", oGParentJavaorderModel.getorderId(), "product", oParentJavaproductModel.getproductId()), "product", "DELETE", "Parent"));
						}
					}
				}
		
		
		}
		else{ //otherwise apply first Basic Authentication checks and then ABAC
		        /* Create hypermedia links towards this specific review resource. These can be GET, PUT and/or delete depending on what was specified in the service CIM.*/
		        oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Get the review", "GET", "Sibling"));
		
		
		        /* Finally, truncate the current URI so as to point to the resource manager of which this resource is related.
		        Then create the hypermedia links towards the parent resources.*/
		        int iLastSlashIndex = String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath).lastIndexOf("/");
		        oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath).substring(0, iLastSlashIndex), "Get all reviews of this product", "GET", "Parent"));
		
				/*Add any hypermedia link to specific parent resources*/
		        JavareviewModel oParentsOfJavareviewModel = HibernateController.getHibernateControllerHandle().getreview(oJavareviewModel);
				Iterator<JavaproductModel> iteratorOfParentproduct = oParentsOfJavareviewModel.getSetOfParentJavaproductModel().iterator();
		
				while(iteratorOfParentproduct.hasNext()){
					JavaproductModel oParentJavaproductModel = iteratorOfParentproduct.next();
					oParentJavaproductModel = HibernateController.getHibernateControllerHandle().getproduct(oParentJavaproductModel);
		
					if(oParentJavaproductModel.getSetOfParentJavaaccountModel().isEmpty() == false && oParentJavaproductModel.getSetOfParentJavaaccountModel() != null){
						JavaaccountModel oGParentJavaaccountModel = oParentJavaproductModel.getSetOfParentJavaaccountModel().iterator().next();
						if (pdp.getPermission("product", Integer.toString(oJavaproductModel.getproductId()), null, Action.GET).equals(AuthorizationResultCode.PERMIT)){
							oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%smulti%s/%s/%d/%s/%d", oApplicationUri.getBaseUri(), "product", "account", oGParentJavaaccountModel.getaccountId(), "product", oParentJavaproductModel.getproductId()), "product", "GET", "Parent"));
						}
						if (pdp.getPermission("product", Integer.toString(oJavaproductModel.getproductId()), null, Action.PUT).equals(AuthorizationResultCode.PERMIT)){
							oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%smulti%s/%s/%d/%s/%d", oApplicationUri.getBaseUri(), "product", "account", oGParentJavaaccountModel.getaccountId(), "product", oParentJavaproductModel.getproductId()), "product", "PUT", "Parent"));
						}
						if (pdp.getPermission("product", Integer.toString(oJavaproductModel.getproductId()), null, Action.DELETE).equals(AuthorizationResultCode.PERMIT)){
							oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%smulti%s/%s/%d/%s/%d", oApplicationUri.getBaseUri(), "product", "account", oGParentJavaaccountModel.getaccountId(), "product", oParentJavaproductModel.getproductId()), "product", "DELETE", "Parent"));
						}
					}
		
		        else			if(oParentJavaproductModel.getSetOfParentJavaorderModel().isEmpty() == false && oParentJavaproductModel.getSetOfParentJavaorderModel() != null){
						JavaorderModel oGParentJavaorderModel = oParentJavaproductModel.getSetOfParentJavaorderModel().iterator().next();
						if (pdp.getPermission("product", Integer.toString(oJavaproductModel.getproductId()), null, Action.GET).equals(AuthorizationResultCode.PERMIT)){
							oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%smulti%s/%s/%d/%s/%d", oApplicationUri.getBaseUri(), "product", "order", oGParentJavaorderModel.getorderId(), "product", oParentJavaproductModel.getproductId()), "product", "GET", "Parent"));
						}
						if (pdp.getPermission("product", Integer.toString(oJavaproductModel.getproductId()), null, Action.PUT).equals(AuthorizationResultCode.PERMIT)){
							oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%smulti%s/%s/%d/%s/%d", oApplicationUri.getBaseUri(), "product", "order", oGParentJavaorderModel.getorderId(), "product", oParentJavaproductModel.getproductId()), "product", "PUT", "Parent"));
						}
						if (pdp.getPermission("product", Integer.toString(oJavaproductModel.getproductId()), null, Action.DELETE).equals(AuthorizationResultCode.PERMIT)){
							oJavareviewModel.getlinklist().add(new HypermediaLink(String.format("%smulti%s/%s/%d/%s/%d", oApplicationUri.getBaseUri(), "product", "order", oGParentJavaorderModel.getorderId(), "product", oParentJavaproductModel.getproductId()), "product", "DELETE", "Parent"));
						}
					}
				}
		
		
		}

        return oJavareviewModel;
    }
}
