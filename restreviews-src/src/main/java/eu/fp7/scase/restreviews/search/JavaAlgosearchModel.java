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


package eu.fp7.scase.restreviews.search;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import eu.fp7.scase.restreviews.utilities.HypermediaLink;

//Please add any needed imports here.


/* JavaAlgosearchModel class is responsible to model any data the search resource handles. Since this one 
 is not automatable, the developer has to manually define it by providing its properties and setter/getter functions.*/
@XmlRootElement
public class JavaAlgosearchModel{

    /* There follows a list with the properties that model the search resource, as prescribed in the service CIM*/
	// The Linklist property holds all the hypermedia links to be sent back to the client
	@Transient
	private List<HypermediaLink> linklist = new ArrayList<HypermediaLink>();
    //Please add any properties of this model here.

    /* There follows a list of setter and getter functions.*/
	public void setlinklist(List<HypermediaLink> linklist){
        this.linklist = linklist;
    }

	public List<HypermediaLink> getlinklist(){
        return this.linklist;
    }
    //Please add the constructors and any functions of this model here.

}
