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
import eu.fp7.scase.restreviews.account.JavaaccountModel;
import eu.fp7.scase.restreviews.account.JavaaccountModel;



/* This class models the data of a order resource. It is enhanced with JAXB annotations for automated representation
parsing/marshalling as well as with Hibernate annotations for ORM transformations.*/
@XmlRootElement
@Entity
@Table(name="\"order\"")
public class JavaorderModel{


    /* There follows a list with the properties that model the order resource, as prescribed in the service CIM*/
		@Column(name = "\"discountcoupon\"")
		@Lob
		private String discountCoupon;

		@Column(name = "\"orderdate\"")
		private Date orderDate;

		@Id
		@GeneratedValue
		@Column(name = "\"orderid\"")
		private int orderId;

		// The Linklist property holds all the hypermedia links to be sent back to the client
		@Transient
		private List<HypermediaLink> linklist = new ArrayList<HypermediaLink>();


		@ManyToMany(fetch = FetchType.LAZY)
		@JoinTable(name = "account_order", joinColumns = { 
			@JoinColumn(name = "orderid", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "accountid", nullable = false, updatable = false) })
		private Set<JavaaccountModel> SetOfParentJavaaccountModel = new HashSet<JavaaccountModel>();

		@ManyToMany(fetch = FetchType.LAZY, mappedBy = "SetOfParentJavaorderModel")
		private Set<JavaproductModel> SetOfJavaproductModel = new HashSet<JavaproductModel>();

		@Column(name = "\"accountid\"", updatable=false)
		private int accountId;



    /* There follows a list of setter and getter functions.*/
	    public void setdiscountCoupon(String discountCoupon){
        	this.discountCoupon = discountCoupon;
    	}

	    public void setorderDate(Date orderDate){
        	this.orderDate = orderDate;
    	}

	    public void setorderId(int orderId){
        	this.orderId = orderId;
    	}

	    public void setlinklist(List<HypermediaLink> linklist){
        	this.linklist = linklist;
    	}


		@XmlTransient
		public void setSetOfJavaproductModel(Set<JavaproductModel> SetOfJavaproductModel){
        	this.SetOfJavaproductModel = SetOfJavaproductModel;
    	}

		@XmlTransient
		public void setSetOfParentJavaaccountModel(Set<JavaaccountModel> SetOfJavaaccountModel){
			this.SetOfParentJavaaccountModel = SetOfJavaaccountModel;
		}

		public void setaccountId(int accountId){
			this.accountId = accountId;
		}


	    public String getdiscountCoupon(){
        	return this.discountCoupon;
    	}

	    public Date getorderDate(){
        	return this.orderDate;
    	}

	    public int getorderId(){
        	return this.orderId;
    	}

	    public List<HypermediaLink> getlinklist(){
        	return this.linklist;
    	}


	    public Set<JavaproductModel> getSetOfJavaproductModel(){
        	return this.SetOfJavaproductModel;
    	}

		public Set<JavaaccountModel> getSetOfParentJavaaccountModel(){
			return this.SetOfParentJavaaccountModel;
		}

		public int getaccountId(){
			return this.accountId;
		}


}
