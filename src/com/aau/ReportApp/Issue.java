package com.aau.ReportApp;

/**
 * Created by kaspe on 19-11-2015.
 */
public class Issue {
    String title;
    String description;
    String subject;
    String issuer;
    String location;

    String status;





    public void setTitle (String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }
    public void setSubject(String subject){
        this.subject = subject;
    }
    public String getSubject(){
        return subject;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getDescription(){
        return description;
    }
    public void setIssuer(String issuer){
        this.issuer = issuer;
    }
    public String getIssuer(){
        return issuer;
    }
    public void setLocation(String location ){
        if(location==null){
            this.location="suck my ass (setLocation)";
        }
        else {
            this.location = location;
        }

    }
    public String getLocation(){
        if(location==null){
            this.location="suck my ass(getLocation)";
        }

        return location;
    }


}
