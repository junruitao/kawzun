package com.kwz.entity;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;


public abstract class KwzBaseBean implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @NotNull @NotEmpty
    protected String name; //name /title
    private Date created = new Date();
    public String getId(){
        return String.valueOf(System.identityHashCode(this));
    }
    public Date getCreated() {
        return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
}
