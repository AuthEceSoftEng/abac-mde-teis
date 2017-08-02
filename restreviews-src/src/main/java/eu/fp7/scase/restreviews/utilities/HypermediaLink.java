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


package eu.fp7.scase.restreviews.utilities;


/* This class models the HypermediaLink datatype that is used to send links back to client so as to be
able to forward the application state.*/

public class HypermediaLink{

    private String LinkURI; // LinkURI is the URI that the client has to follow to perfom some aciton
    private String LinkRel; // LinkRel is a string that show the relation of the entity at that LinkURI with the current one
    private String LinkVerb; // LinkVerb refers to the HTTP verb to be used on that LinkURI
    private String LinkType; // LinkType denotes the hierarchical relation of the resource at that LinkURI. Can be either Parent, Sibling, Children
    private int IdType;

    public HypermediaLink(){}

    public HypermediaLink(String strLinkURI, String strLinkRel, String strLinkVerb, String strLinkType, int iIdType){
        this.LinkURI = strLinkURI;
        this.LinkRel = strLinkRel;
        this.LinkVerb = strLinkVerb;
        this.LinkType = strLinkType;
        this.IdType = iIdType;
    }

    public HypermediaLink(String strLinkURI, String strLinkRel, String strLinkVerb, String strLinkType){
        this.LinkURI = strLinkURI;
        this.LinkRel = strLinkRel;
        this.LinkVerb = strLinkVerb;
        this.LinkType = strLinkType;
    }

    /* Setter and getter function follow*/
    public void setLinkURI(String strLinkURI){
        this.LinkURI = strLinkURI;
    }

    public void setLinkRel(String strLinkRel){
        this.LinkRel = strLinkRel;
    }

    public void setLinkVerb(String strLinkVerb){
        this.LinkVerb = strLinkVerb;
    }

    public void setLinkType(String strLinkType){
        this.LinkType = strLinkType;
    }

    public void setIdType(int iIdType){
        this.IdType = iIdType;
    }

    public String getLinkURI(){
        return this.LinkURI;
    }

    public String getLinkRel(){
        return this.LinkRel;
    }

    public String getLinkVerb(){
        return this.LinkVerb;
    }

    public String getLinkType(){
        return this.LinkType;
    }

    public int getIdType(){
        return this.IdType;
    }
}
