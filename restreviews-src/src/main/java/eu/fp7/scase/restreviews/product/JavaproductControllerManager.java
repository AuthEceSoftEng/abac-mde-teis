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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.DefaultValue;


/* This class defines the web API of the manager product resource. It handles POST and GET HTTP requests, as it is prescribed by the meta-models.*/
@Path("/multiproductManager")
public class JavaproductControllerManager{

    @Context
    private UriInfo oApplicationUri;

	/* This function handles POST requests that are sent with any media type stated in the @Consumes JAX-RS annotation below 
     and returns any response in any media type stated in the @Produces JAX-RS annotation below.*/
	@Path("/account/{accountId}/product/")
	@POST
	@Produces("application/JSON")
	@Consumes("application/JSON")
    public JavaproductModel postaccountproduct(@HeaderParam("authorization") String authHeader, @PathParam("accountId")int accountId, JavaproductModel oJavaproductModel){
        PostaccountproductHandler oPostaccountproductHandler = new PostaccountproductHandler(authHeader, accountId, oJavaproductModel, oApplicationUri);
        return oPostaccountproductHandler.postJavaproductModel();
    }

	/* This function handles POST requests that are sent with any media type stated in the @Consumes JAX-RS annotation below 
     and returns any response in any media type stated in the @Produces JAX-RS annotation below.*/
	@Path("/order/{orderId}/product/")
	@POST
	@Produces("application/JSON")
	@Consumes("application/JSON")
    public JavaproductModel postorderproduct(@HeaderParam("authorization") String authHeader, @PathParam("orderId")int orderId, JavaproductModel oJavaproductModel){
        PostorderproductHandler oPostorderproductHandler = new PostorderproductHandler(authHeader, orderId, oJavaproductModel, oApplicationUri);
        return oPostorderproductHandler.postJavaproductModel();
    }

    /* This function handles GET requests  
     and returns any response in any media type stated in the @Produces JAX-RS annotation below.*/
	@Path("/account/{accountId}/product/")
	@GET
	@Produces("application/JSON")
    public JavaproductModelManager getaccountproductList(@DefaultValue("guest") @HeaderParam("authorization") String authHeader, @PathParam("accountId")int accountId){
        GetaccountproductListHandler oGetaccountproductListHandler = new GetaccountproductListHandler(authHeader, accountId, oApplicationUri);
        return oGetaccountproductListHandler.getJavaproductModelManager();
    }

    /* This function handles GET requests  
     and returns any response in any media type stated in the @Produces JAX-RS annotation below.*/
	@Path("/order/{orderId}/product/")
	@GET
	@Produces("application/JSON")
    public JavaproductModelManager getorderproductList(@DefaultValue("guest") @HeaderParam("authorization") String authHeader, @PathParam("orderId")int orderId){
        GetorderproductListHandler oGetorderproductListHandler = new GetorderproductListHandler(authHeader, orderId, oApplicationUri);
        return oGetorderproductListHandler.getJavaproductModelManager();
    }

}
