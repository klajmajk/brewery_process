/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.klajmajk.camunda.varna.entities;

import java.io.Serializable;

/**
 *
 * @author Adam
 */
public class Entry<T> implements Serializable{
    
    private String type;
    private T value;

    public Entry(String type, T value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Entry{" + "type=" + type + ", value=" + value + '}';
    }
    
    

    
    
}
