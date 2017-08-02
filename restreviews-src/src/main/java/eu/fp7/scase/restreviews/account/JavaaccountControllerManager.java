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


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.DefaultValue;


/* This class defines the web API of the manager account resource. It handles POST and GET HTTP requests, as it is prescribed by the meta-models.*/
@Path("/account")
public class JavaaccountControllerManager{

    @Context
    private UriInfo oApplicationUri;

	/* This function handles POST requests that are sent with any media type stated in the @Consumes JAX-RS annotation below 
     and returns any response in any media type stated in the @Produces JAX-RS annotation below.*/
	@Path("/")
	@POST
	@Produces("application/JSON")
	@Consumes("application/JSON")
    public JavaaccountModel postaccount(@DefaultValue("guest") @HeaderParam("authorization") String authHeader, JavaaccountModel oJavaaccountModel){
        PostaccountHandler oPostaccountHandler = new PostaccountHandler(authHeader, oJavaaccountModel, oApplicationUri);
        return oPostaccountHandler.postJavaaccountModel();
    }

    /* This function handles GET requests  
     and returns any response in any media type stated in the @Produces JAX-RS annotation below.*/
	@Path("/")
	@GET
	@Produces("application/JSON")
    public JavaaccountModelManager getaccountList(@HeaderParam("authorization") String authHeader){
        GetaccountListHandler oGetaccountListHandler = new GetaccountListHandler(authHeader, oApplicationUri);
        return oGetaccountListHandler.getJavaaccountModelManager();
    }

}
