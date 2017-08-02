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
import eu.fp7.scase.restreviews.review.JavareviewModel;
import eu.fp7.scase.restreviews.account.JavaaccountModel;
import eu.fp7.scase.restreviews.account.JavaaccountModel;
import eu.fp7.scase.restreviews.order.JavaorderModel;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import eu.fp7.scase.restreviews.utilities.SetStringFieldBridge;


/* This class models the data of a product resource. It is enhanced with JAXB annotations for automated representation
parsing/marshalling as well as with Hibernate annotations for ORM transformations.*/
@XmlRootElement
@Entity
@Table(name="\"product\"")
@Indexed
public class JavaproductModel{


    /* There follows a list with the properties that model the product resource, as prescribed in the service CIM*/
		@Column(name = "\"title\"")
		@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
		@Lob
		private String title;

		@Column(name = "\"description\"")
		@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
		@Lob
		private String description;

		@Column(name = "\"cost\"")
		private float cost;

		@Column(name = "\"status\"")
		@Lob
		private String status;

		@Id
		@GeneratedValue
		@Column(name = "\"productid\"")
		private int productId;

		// The Linklist property holds all the hypermedia links to be sent back to the client
		@Transient
		private List<HypermediaLink> linklist = new ArrayList<HypermediaLink>();



		@ManyToMany(fetch = FetchType.LAZY)
		@JoinTable(name = "account_product", joinColumns = { 
			@JoinColumn(name = "productid", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "accountid", nullable = false, updatable = false) })
		private Set<JavaaccountModel> SetOfParentJavaaccountModel = new HashSet<JavaaccountModel>();

		@ManyToMany(fetch = FetchType.LAZY)
		@JoinTable(name = "order_product", joinColumns = { 
			@JoinColumn(name = "productid", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "orderid", nullable = false, updatable = false) })
		private Set<JavaorderModel> SetOfParentJavaorderModel = new HashSet<JavaorderModel>();

		@ManyToMany(fetch = FetchType.LAZY, mappedBy = "SetOfParentJavaproductModel")
		private Set<JavareviewModel> SetOfJavareviewModel = new HashSet<JavareviewModel>();

		@Column(name = "\"accountid\"", updatable=false)
		private int accountId;



    /* There follows a list of setter and getter functions.*/
	    public void settitle(String title){
        	this.title = title;
    	}

	    public void setdescription(String description){
        	this.description = description;
    	}

	    public void setcost(float cost){
        	this.cost = cost;
    	}

	    public void setstatus(String status){
        	this.status = status;
    	}

	    public void setproductId(int productId){
        	this.productId = productId;
    	}

	    public void setlinklist(List<HypermediaLink> linklist){
        	this.linklist = linklist;
    	}



		@XmlTransient
		public void setSetOfJavareviewModel(Set<JavareviewModel> SetOfJavareviewModel){
        	this.SetOfJavareviewModel = SetOfJavareviewModel;
    	}

		@XmlTransient
		public void setSetOfParentJavaaccountModel(Set<JavaaccountModel> SetOfJavaaccountModel){
			this.SetOfParentJavaaccountModel = SetOfJavaaccountModel;
		}
		@XmlTransient
		public void setSetOfParentJavaorderModel(Set<JavaorderModel> SetOfJavaorderModel){
			this.SetOfParentJavaorderModel = SetOfJavaorderModel;
		}

		public void setaccountId(int accountId){
			this.accountId = accountId;
		}


	    public String gettitle(){
        	return this.title;
    	}

	    public String getdescription(){
        	return this.description;
    	}

	    public float getcost(){
        	return this.cost;
    	}

	    public String getstatus(){
        	return this.status;
    	}

	    public int getproductId(){
        	return this.productId;
    	}

	    public List<HypermediaLink> getlinklist(){
        	return this.linklist;
    	}



	    public Set<JavareviewModel> getSetOfJavareviewModel(){
        	return this.SetOfJavareviewModel;
    	}

		public Set<JavaaccountModel> getSetOfParentJavaaccountModel(){
			return this.SetOfParentJavaaccountModel;
		}
		public Set<JavaorderModel> getSetOfParentJavaorderModel(){
			return this.SetOfParentJavaorderModel;
		}

		public int getaccountId(){
			return this.accountId;
		}


}
