/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.klajmajk.camunda.varna.ws;

import cz.klajmajk.camunda.varna.SessionBean;
import cz.klajmajk.camunda.varna.entities.Entry;
import cz.klajmajk.camunda.varna.entities.Record;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Adam
 */
@Stateless
@Named
@Path("/diagram")
public class WebServiceFacade {

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    @Inject
    private SessionBean sessionBean;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("data/")
    public HashMap<String, List<Object>> getDiagramData() {
        HashMap toReturn = new HashMap<String, List<Object>>();
//        List list = new ArrayList();
//        List<String> timeList = new ArrayList();
//        Calendar cal = Calendar.getInstance(); // creates calendar
//        cal.setTime(new Date()); // sets calendar time/date
//        timeList.add(df.format(cal.getTime()));
//        cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
//        timeList.add(df.format(cal.getTime()));
//        cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
//        timeList.add(df.format(cal.getTime()));
//
//        list.add(1);
//        list.add(1.14);
//        list.add(15.12);
//        toReturn.put("Nastavená teplota", list);
        toReturn.put("Naměřená teplota", getTempMeasuredList(sessionBean.getRecords()));
        toReturn.put("Výkon", getPowerList(sessionBean.getRecords()));
        toReturn.put("Čas", getDates(sessionBean.getRecords()));
        return toReturn;
    }

    public List<Object> getTempMeasuredList(List<Record> records) {
        return getEntriesByType(records, "temp1");

    }

    public List<Object> getPowerList(List<Record> records) {
        return getEntriesByType(records, "power1");

    }

    public List<String> getDates(List<Record> records) {
        List<String> toReturn = new ArrayList<>();
        if (records != null) {
            for (Record record : records) {
                toReturn.add(df.format(record.getDate()));
            }
        }
        return toReturn;
    }

    private List<Object> getEntriesByType(List<Record> records, String type) {
        List<Object> toReturn = new ArrayList<>();
        if (records != null) {
            for (Record record : records) {
                toReturn.add(getEntryByType(record.getEntries(), type));
            }
        }
        return toReturn;
    }

    private Object getEntryByType(Set<Entry> entries, String type) {
        for (Entry entry : entries) {
            if (type.equals(entry.getType())) {
                return entry.getValue();
            }
        }
        return null;
    }

}
