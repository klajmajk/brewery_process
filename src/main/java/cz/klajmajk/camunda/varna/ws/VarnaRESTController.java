/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.klajmajk.camunda.varna.ws;

import cz.klajmajk.camunda.varna.entities.Entry;
import cz.klajmajk.camunda.varna.entities.Record;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.inject.Named;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Adam
 */
@Named
@Singleton
public class VarnaRESTController implements Serializable {
    
    public Record getCurrentState() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost/varna");
        JsonObject response = target.request(MediaType.APPLICATION_JSON).get(JsonObject.class); 
        Set<Entry> entries = new HashSet<>();
        entries.add(new Entry("temp1", response.getJsonNumber("temp1").bigDecimalValue().floatValue()));
        entries.add(new Entry("temp2", response.getJsonNumber("temp2").bigDecimalValue().floatValue()));
        entries.add(new Entry("power1", response.getJsonNumber("power1").intValue()));
        entries.add(new Entry("power2", response.getJsonNumber("power2").intValue()));
        return new Record(new Date(), entries);
    }
    
    public void setPower(int i) throws Exception {
        
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost/varna?power=" + i);
        Response response = target.request(MediaType.APPLICATION_JSON).get();
        if (response.getStatusInfo().getStatusCode() != 200) {
            throw new Exception("Error while setting power");
        }
        
    }
}
