/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.klajmajk.camunda.varna.ws;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ejb.Stateless;
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
    private SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ" );

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("data/")
    public HashMap<String, List<Object>> getDiagramData() {
        HashMap toReturn = new HashMap<String, List<Object>>();
        List list = new ArrayList();
        List<String> timeList = new ArrayList();
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        timeList.add(df.format(cal.getTime()));
        cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
        timeList.add(df.format(cal.getTime()));
        cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
        timeList.add(df.format(cal.getTime()));

        list.add(1);
        list.add(1.14);
        list.add(15.12);
        toReturn.put("Naměřená teplota", list);
        toReturn.put("Nastavená teplota", list);
        toReturn.put("Výkon", list);
        toReturn.put("Čas", timeList);
        return toReturn;
    }

}
