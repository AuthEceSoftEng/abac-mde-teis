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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.DefaultValue;


/* This class defines the web API of the individual product resource. It may handle PUT, GET and/or DELETE requests 
   depending on the specific CIM of the service.*/

@Path("/multiproduct")
public class JavaproductController{

    @Context
    private UriInfo oApplicationUri;

	/* This function handles http GET requests  
    and returns any response formatted as stated in the @Produces JAX-RS annotation below.*/
	@Path("/account/{accountId}/product/{productId}")
	@GET
	@Produces("application/JSON")
    public JavaproductModel getaccountproduct(@DefaultValue("guest") @HeaderParam("authorization") String authHeader, @PathParam("accountId") int accountId, @PathParam("productId") int productId){
        GetaccountproductHandler oGetaccountproductHandler = new GetaccountproductHandler(authHeader, accountId, productId, oApplicationUri);
        return oGetaccountproductHandler.getJavaproductModel();
    }

	/* This function handles http GET requests  
    and returns any response formatted as stated in the @Produces JAX-RS annotation below.*/
	@Path("/order/{orderId}/product/{productId}")
	@GET
	@Produces("application/JSON")
    public JavaproductModel getorderproduct(@DefaultValue("guest") @HeaderParam("authorization") String authHeader, @PathParam("orderId") int orderId, @PathParam("productId") int productId){
        GetorderproductHandler oGetorderproductHandler = new GetorderproductHandler(authHeader, orderId, productId, oApplicationUri);
        return oGetorderproductHandler.getJavaproductModel();
    }

	/* This function handles http PUT requests that are sent with any media type stated in the @Consumes JAX-RS annotation below 
    and returns any response formatted as stated in the @Produces JAX-RS annotation below.*/
	@Path("/account/{accountId}/product/{productId}")
	@PUT
	@Produces("application/JSON")
	@Consumes("application/JSON")
    public JavaproductModel putaccountproduct(@HeaderParam("authorization") String authHeader, @PathParam("accountId") int accountId, @PathParam("productId") int productId,  JavaproductModel oJavaproductModel, @QueryParam("strOptionalUpdateRelations") String strOptionalUpdateRelations, @QueryParam("strOptionalUpdateParent") String strOptionalUpdateParent, @QueryParam("strOptionalRelationName") String strOptionalRelationName, @QueryParam("strOptionalAddRelation") String strOptionalAddRelation, @QueryParam("iOptionalResourceId") Integer iOptionalResourceId){
        PutaccountproductHandler oPutaccountproductHandler = new PutaccountproductHandler(authHeader, accountId, productId, oJavaproductModel, oApplicationUri, strOptionalUpdateRelations, strOptionalUpdateParent, strOptionalRelationName, strOptionalAddRelation, iOptionalResourceId);
        return oPutaccountproductHandler.putJavaproductModel();
    }

	/* This function handles http PUT requests that are sent with any media type stated in the @Consumes JAX-RS annotation below 
    and returns any response formatted as stated in the @Produces JAX-RS annotation below.*/
	@Path("/order/{orderId}/product/{productId}")
	@PUT
	@Produces("application/JSON")
	@Consumes("application/JSON")
    public JavaproductModel putorderproduct(@HeaderParam("authorization") String authHeader, @PathParam("orderId") int orderId, @PathParam("productId") int productId,  JavaproductModel oJavaproductModel, @QueryParam("strOptionalUpdateRelations") String strOptionalUpdateRelations, @QueryParam("strOptionalUpdateParent") String strOptionalUpdateParent, @QueryParam("strOptionalRelationName") String strOptionalRelationName, @QueryParam("strOptionalAddRelation") String strOptionalAddRelation, @QueryParam("iOptionalResourceId") Integer iOptionalResourceId){
        PutorderproductHandler oPutorderproductHandler = new PutorderproductHandler(authHeader, orderId, productId, oJavaproductModel, oApplicationUri, strOptionalUpdateRelations, strOptionalUpdateParent, strOptionalRelationName, strOptionalAddRelation, iOptionalResourceId);
        return oPutorderproductHandler.putJavaproductModel();
    }

    /* This function handles http DELETE requests  
    and returns any response formatted as stated in the @Produces JAX-RS annotation below.*/
	@Path("/account/{accountId}/product/{productId}")
	@DELETE
	@Produces("application/JSON")
    public JavaproductModel deleteaccountproduct(@HeaderParam("authorization") String authHeader, @PathParam("accountId") int accountId, @PathParam("productId") int productId){
        DeleteaccountproductHandler oDeleteaccountproductHandler = new DeleteaccountproductHandler(authHeader, accountId, productId, oApplicationUri);
        return oDeleteaccountproductHandler.deleteJavaproductModel();
    }

    /* This function handles http DELETE requests  
    and returns any response formatted as stated in the @Produces JAX-RS annotation below.*/
	@Path("/order/{orderId}/product/{productId}")
	@DELETE
	@Produces("application/JSON")
    public JavaproductModel deleteorderproduct(@HeaderParam("authorization") String authHeader, @PathParam("orderId") int orderId, @PathParam("productId") int productId){
        DeleteorderproductHandler oDeleteorderproductHandler = new DeleteorderproductHandler(authHeader, orderId, productId, oApplicationUri);
        return oDeleteorderproductHandler.deleteJavaproductModel();
    }
}

