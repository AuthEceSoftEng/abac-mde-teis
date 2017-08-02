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


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.JoinTable;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.Table;
import javax.persistence.Transient;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.Session;
import org.hibernate.annotations.ForeignKey;

import eu.fp7.scase.restreviews.utilities.HypermediaLink;
import eu.fp7.scase.restreviews.product.JavaproductModel;
import eu.fp7.scase.restreviews.order.JavaorderModel;
import eu.fp7.scase.restreviews.review.JavareviewModel;
import eu.fp7.scase.restreviews.account.JavaaccountModel;



/* This class models the data of a account resource. It is enhanced with JAXB annotations for automated representation
parsing/marshalling as well as with Hibernate annotations for ORM transformations.*/
@XmlRootElement
@Entity
@Table(name="\"account\"")
public class JavaaccountModel{


    /* There follows a list with the properties that model the account resource, as prescribed in the service CIM*/
		@Column(name = "\"email\"")
		@Lob
		private String email;

		@Column(name = "\"password\"")
		@Lob
		private String password;

		@Column(name = "\"role\"")
		@Lob
		private String role;

		@Id
		@GeneratedValue
		@Column(name = "\"accountid\"")
		private int accountId;

		// The Linklist property holds all the hypermedia links to be sent back to the client
		@Transient
		private List<HypermediaLink> linklist = new ArrayList<HypermediaLink>();



		@ManyToMany(fetch = FetchType.LAZY, mappedBy = "SetOfParentJavaaccountModel")
		private Set<JavaproductModel> SetOfJavaproductModel = new HashSet<JavaproductModel>();

		@ManyToMany(fetch = FetchType.LAZY, mappedBy = "SetOfParentJavaaccountModel")
		private Set<JavaorderModel> SetOfJavaorderModel = new HashSet<JavaorderModel>();

		@OneToMany(fetch = FetchType.LAZY, mappedBy="account")
		private Set<JavareviewModel> SetOfJavareviewModel = new HashSet<JavareviewModel>();



    /* There follows a list of setter and getter functions.*/
	    public void setemail(String email){
        	this.email = email;
    	}

	    public void setpassword(String password){
        	this.password = password;
    	}

	    public void setrole(String role){
        	this.role = role;
    	}

	    public void setaccountId(int accountId){
        	this.accountId = accountId;
    	}

	    public void setlinklist(List<HypermediaLink> linklist){
        	this.linklist = linklist;
    	}

		@XmlTransient
		public void setSetOfJavaproductModel(Set<JavaproductModel> SetOfJavaproductModel){
        	this.SetOfJavaproductModel = SetOfJavaproductModel;
    	}

		@XmlTransient
		public void setSetOfJavaorderModel(Set<JavaorderModel> SetOfJavaorderModel){
        	this.SetOfJavaorderModel = SetOfJavaorderModel;
    	}


		@XmlTransient
		public void setSetOfJavareviewModel(Set<JavareviewModel> SetOfJavareviewModel){
	    	this.SetOfJavareviewModel = SetOfJavareviewModel;
		}


	    public String getemail(){
        	return this.email;
    	}

	    public String getpassword(){
        	return this.password;
    	}

	    public String getrole(){
        	return this.role;
    	}

	    public int getaccountId(){
        	return this.accountId;
    	}

	    public List<HypermediaLink> getlinklist(){
        	return this.linklist;
    	}

	    public Set<JavaproductModel> getSetOfJavaproductModel(){
        	return this.SetOfJavaproductModel;
    	}

	    public Set<JavaorderModel> getSetOfJavaorderModel(){
        	return this.SetOfJavaorderModel;
    	}


		public Set<JavareviewModel> getSetOfJavareviewModel(){
	    	return SetOfJavareviewModel;
		}

}
