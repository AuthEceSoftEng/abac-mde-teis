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


/* This class defines the web API of the individual review resource. It may handle PUT, GET and/or DELETE requests 
   depending on the specific CIM of the service.*/

@Path("/product/{productId}/review/{reviewId}")
public class JavareviewController{

    @Context
    private UriInfo oApplicationUri;

	/* This function handles http GET requests  
    and returns any response formatted as stated in the @Produces JAX-RS annotation below.*/
	@Path("/")
	@GET
	@Produces("application/JSON")
    public JavareviewModel getreview(@DefaultValue("guest") @HeaderParam("authorization") String authHeader, @PathParam("productId") int productId, @PathParam("reviewId") int reviewId){
        GetreviewHandler oGetreviewHandler = new GetreviewHandler(authHeader, productId, reviewId, oApplicationUri);
        return oGetreviewHandler.getJavareviewModel();
    }

	/* This function handles http PUT requests that are sent with any media type stated in the @Consumes JAX-RS annotation below 
    and returns any response formatted as stated in the @Produces JAX-RS annotation below.*/
	@Path("/")
	@PUT
	@Produces("application/JSON")
	@Consumes("application/JSON")
    public JavareviewModel putreview(@HeaderParam("authorization") String authHeader, @PathParam("productId") int productId, @PathParam("reviewId") int reviewId,  JavareviewModel oJavareviewModel, @QueryParam("strOptionalUpdateRelations") String strOptionalUpdateRelations, @QueryParam("strOptionalUpdateParent") String strOptionalUpdateParent, @QueryParam("strOptionalRelationName") String strOptionalRelationName, @QueryParam("strOptionalAddRelation") String strOptionalAddRelation, @QueryParam("iOptionalResourceId") Integer iOptionalResourceId){
        PutreviewHandler oPutreviewHandler = new PutreviewHandler(authHeader, productId, reviewId, oJavareviewModel, oApplicationUri, strOptionalUpdateRelations, strOptionalUpdateParent, strOptionalRelationName, strOptionalAddRelation, iOptionalResourceId);
        return oPutreviewHandler.putJavareviewModel();
    }

    /* This function handles http DELETE requests  
    and returns any response formatted as stated in the @Produces JAX-RS annotation below.*/
	@Path("/")
	@DELETE
	@Produces("application/JSON")
    public JavareviewModel deletereview(@HeaderParam("authorization") String authHeader, @PathParam("productId") int productId, @PathParam("reviewId") int reviewId){
        DeletereviewHandler oDeletereviewHandler = new DeletereviewHandler(authHeader, productId, reviewId, oApplicationUri);
        return oDeletereviewHandler.deleteJavareviewModel();
    }
}

