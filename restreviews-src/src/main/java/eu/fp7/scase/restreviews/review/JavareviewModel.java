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


import java.util.ArrayList;
import java.util.HashSet;
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
import javax.persistence.Table;
import javax.persistence.Transient;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.ForeignKey;

import eu.fp7.scase.restreviews.utilities.HypermediaLink;
import eu.fp7.scase.restreviews.account.JavaaccountModel;
import eu.fp7.scase.restreviews.product.JavaproductModel;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import eu.fp7.scase.restreviews.utilities.SetStringFieldBridge;


/* This class models the data of a review resource. It is enhanced with JAXB annotations for automated representation
parsing/marshalling as well as with Hibernate annotations for ORM transformations.*/
@XmlRootElement
@Entity
@Table(name="\"review\"")
@Indexed
public class JavareviewModel{


    /* There follows a list with the properties that model the review resource, as prescribed in the service CIM*/
		@Column(name = "\"title\"")
		@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
		@Lob
		private String title;

		@Column(name = "\"description\"")
		@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
		@Lob
		private String description;

		@Id
		@GeneratedValue
		@Column(name = "\"reviewid\"")
		private int reviewId;

		// The Linklist property holds all the hypermedia links to be sent back to the client
		@Transient
		private List<HypermediaLink> linklist = new ArrayList<HypermediaLink>();

		@ManyToMany(fetch = FetchType.LAZY)
		@JoinTable(name = "product_review", joinColumns = { 
			@JoinColumn(name = "reviewid", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "productid", nullable = false, updatable = false) })
		private Set<JavaproductModel> SetOfParentJavaproductModel = new HashSet<JavaproductModel>();


		@ManyToOne(fetch = FetchType.EAGER)
		@JoinColumn(name="accountId", updatable=false)
		@ForeignKey(name = "fk_account_review")
		private JavaaccountModel account;



    /* There follows a list of setter and getter functions.*/
	    public void settitle(String title){
        	this.title = title;
    	}

	    public void setdescription(String description){
        	this.description = description;
    	}

	    public void setreviewId(int reviewId){
        	this.reviewId = reviewId;
    	}

	    public void setlinklist(List<HypermediaLink> linklist){
        	this.linklist = linklist;
    	}


		@XmlTransient
		public void setSetOfParentJavaproductModel(Set<JavaproductModel> SetOfJavaproductModel){
			this.SetOfParentJavaproductModel = SetOfJavaproductModel;
		}

		@XmlTransient
		public void setaccount(JavaaccountModel account){
	    	this.account = account;
		}


	    public String gettitle(){
        	return this.title;
    	}

	    public String getdescription(){
        	return this.description;
    	}

	    public int getreviewId(){
        	return this.reviewId;
    	}

	    public List<HypermediaLink> getlinklist(){
        	return this.linklist;
    	}


		public Set<JavaproductModel> getSetOfParentJavaproductModel(){
			return this.SetOfParentJavaproductModel;
		}

		public JavaaccountModel getaccount(){
	    	return account;
		}

}
