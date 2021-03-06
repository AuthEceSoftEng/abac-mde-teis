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


package eu.fp7.scase.restreviews.order;

import java.util.List;
import java.util.Arrays;
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
import eu.fp7.scase.restreviews.account.JavaaccountModel;

/* This class processes PUT requests for order resources and creates the hypermedia to be returned to the client*/

public class PutorderHandler{


    private HibernateController oHibernateController;
    private UriInfo oApplicationUri; //Standard datatype that holds information on the URI info of this request
	private String strResourcePath; //relative path to the current resource
    private JavaorderModel oJavaorderModel;
    private JavaaccountModel oJavaaccountModel;
    private String strOptionalUpdateRelations;
    private String strOptionalUpdateParent;
    private String strOptionalRelationName;
    private Integer iOptionalResourceId;
    private String strOptionalAddRelation;
    private final List<String> listOfParentResources = Arrays.asList("account");
    private final List<String> listOfChildrenResources = Arrays.asList("product");
	private String authHeader;
	private JavaaccountModel oAuthenticationAccount;
	private Boolean bIsClientAuthenticated = false;
	private AzPDP pdp;

    public PutorderHandler(String authHeader, int accountId, int orderId, JavaorderModel oJavaorderModel, UriInfo oApplicationUri, String strOptionalUpdateRelations, String strOptionalUpdateParent, String strOptionalRelationName, String strOptionalAddRelation, Integer iOptionalResourceId){
        this.oJavaorderModel = oJavaorderModel;
        this.oJavaorderModel.setorderId(orderId);
        this.oHibernateController = HibernateController.getHibernateControllerHandle();
        this.oApplicationUri = oApplicationUri;
		this.strResourcePath = calculateProperResourcePath();
        oJavaaccountModel = new JavaaccountModel();
        oJavaaccountModel.setaccountId(accountId);
        oJavaorderModel.getSetOfParentJavaaccountModel().add(this.oJavaaccountModel);
		this.authHeader = authHeader;
		this.oAuthenticationAccount = new JavaaccountModel(); 
		this.pdp = new AzPDP();
        this.strOptionalUpdateRelations = strOptionalUpdateRelations;
        this.strOptionalUpdateParent = strOptionalUpdateParent;
        this.strOptionalRelationName = strOptionalRelationName;
        this.iOptionalResourceId = iOptionalResourceId;
        this.strOptionalAddRelation = strOptionalAddRelation;
        
        if(this.strOptionalUpdateRelations != null && this.strOptionalUpdateRelations.equalsIgnoreCase("true")){
            checkRelationUpdateInfo();
        }
    }

    public String calculateProperResourcePath(){
        if(this.oApplicationUri.getPath().lastIndexOf('/') == this.oApplicationUri.getPath().length() - 1){
            return this.oApplicationUri.getPath().substring(0, this.oApplicationUri.getPath().length() - 1);
        }
        else{
            return this.oApplicationUri.getPath();
        }
    }

    private boolean hasParent(String strPossibleParentName){
        for(int i = 0; i < this.listOfParentResources.size(); i++){
            if(this.listOfParentResources.get(i).equalsIgnoreCase(strPossibleParentName)){
                return true;
            }
        }
        
        return false;
    }
    
    private boolean hasChild(String strPossibleChildName){
        for(int i = 0; i < this.listOfChildrenResources.size(); i++){
            if(this.listOfChildrenResources.get(i).equalsIgnoreCase(strPossibleChildName)){
                return true;
            }
        }
        
        return false;
    }
    
    private void checkRelationUpdateInfo(){
        
        //check if the request is about updating relations to parent resources
        if(this.strOptionalUpdateParent != null && this.strOptionalUpdateParent.equalsIgnoreCase("true")){
            //check if the request is to update relations to a valid parent resource
            if(this.strOptionalRelationName != null && hasParent(this.strOptionalRelationName)){
                if(this.strOptionalAddRelation != null && (this.strOptionalAddRelation.equalsIgnoreCase("true") || this.strOptionalAddRelation.equalsIgnoreCase("false"))
                   && (this.iOptionalResourceId != null && this.iOptionalResourceId > 0) ){
                    //then this is a valid request and should be processed
                    return;
                }
                else{ //else it is an invalid request
                    throw new WebApplicationException(Response.Status.BAD_REQUEST);
                }
            }
            else{ //else the selected parent resource is an invalid parent and this is a bad request
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
        }
        //of if it is about updating relations to children resources
        else if(this.strOptionalUpdateParent != null && this.strOptionalUpdateParent.equalsIgnoreCase("false")){
            //check if the request is to update relations to a valid child resource
            if(this.strOptionalRelationName != null && hasChild(this.strOptionalRelationName)){
                if(this.strOptionalAddRelation != null && (this.strOptionalAddRelation.equalsIgnoreCase("true") || this.strOptionalAddRelation.equalsIgnoreCase("false"))
                   && (this.iOptionalResourceId != null && this.iOptionalResourceId > 0) ){
                    //then this is a valid request and should be processed
                    return;
                }
                else{ //else it is an invalid request
                    throw new WebApplicationException(Response.Status.BAD_REQUEST);
                }
            }
            else{ //else the selected parent resource is an invalid parent and this is a bad request
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
        }
        else{ //otherwise this is a bad request
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
    }


    public JavaorderModel putJavaorderModel(){

    	//check if there is a non null authentication header
    	if(authHeader == null){
    		throw new WebApplicationException(Response.Status.UNAUTHORIZED);
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

		if (pdp.getPermission(oAuthenticationAccount, "order", Integer.toString(oJavaorderModel.getorderId()), oJavaorderModel, Action.PUT)
				.equals(AuthorizationResultCode.PERMIT)){
        	return createHypermedia(oHibernateController.putorder(oJavaorderModel, this.strOptionalUpdateRelations, this.strOptionalUpdateParent, this.strOptionalRelationName, this.strOptionalAddRelation, this.iOptionalResourceId));
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
    public JavaorderModel createHypermedia(JavaorderModel oJavaorderModel){

		        /* Create hypermedia links towards this specific Resource resource. These can be GET, PUT and/or delete depending on what was specified in the service CIM.*/
		        if (pdp.getPermission(oAuthenticationAccount, "order", Integer.toString(oJavaorderModel.getorderId()), null, Action.GET).equals(AuthorizationResultCode.PERMIT)){
			        oJavaorderModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Get the order", "GET", "Sibling"));
				}
		        oJavaorderModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Update the order", "PUT", "Sibling"));
		        if (pdp.getPermission(oAuthenticationAccount, "order", Integer.toString(oJavaorderModel.getorderId()), null, Action.DELETE).equals(AuthorizationResultCode.PERMIT)){
			        oJavaorderModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath), "Delete the order", "DELETE", "Sibling"));
				}
		
		        /* Calculate the relative path towards any related resources of this one. Then add each new hypermedia link with that relative path to the hypermedia linklist to be sent back to client.*/
		        String oRelativePath;
		        oRelativePath = this.strResourcePath.substring(this.strResourcePath.indexOf("/order") + 1);
		        oJavaorderModel.getlinklist().add(new HypermediaLink(String.format("%s%s/%s/%s", oApplicationUri.getBaseUri(), "multiproductManager", oRelativePath, "product"), "Get all the products of this order", "GET", "Child"));
		        if (pdp.getPermission(oAuthenticationAccount, "product", "order", Integer.toString(oJavaorderModel.getorderId()), null, Action.POST).equals(AuthorizationResultCode.PERMIT)){
			        oJavaorderModel.getlinklist().add(new HypermediaLink(String.format("%s%s/%s/%s", oApplicationUri.getBaseUri(), "multiproductManager", oRelativePath, "product"), "Create a new product for this order", "POST", "Child"));
				}
		
		        /* Finally, truncate the current URI so as to point to the resource manager of which this resource is related.
		        Then create the hypermedia links towards the parent resources.*/
		        int iLastSlashIndex = String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath).lastIndexOf("/");
				if (pdp.getPermission(oAuthenticationAccount, "order", "account", Integer.toString(oJavaaccountModel.getaccountId()), null, Action.POST).equals(AuthorizationResultCode.PERMIT)){
			        oJavaorderModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath).substring(0, iLastSlashIndex), "Create a new order", "POST", "Parent"));
				}
		        oJavaorderModel.getlinklist().add(new HypermediaLink(String.format("%s%s", oApplicationUri.getBaseUri(), this.strResourcePath).substring(0, iLastSlashIndex), "Get all orders of this account", "GET", "Parent"));
		 

        return oJavaorderModel;
    }
}
