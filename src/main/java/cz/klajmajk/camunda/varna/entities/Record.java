/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.klajmajk.camunda.varna.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author Adam
 */
public class Record implements Serializable{
    private Date date;
    private Set<Entry> entries;

    public Record(Date date, Set<Entry> entries) {
        this.date = date;
        this.entries = entries;
    }
    
    

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Set<Entry> getEntries() {
        return entries;
    }

    public void setEntries(Set<Entry> entries) {
        this.entries = entries;
    }
    
    
    
    
}
