/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.klajmajk.camunda.varna;

import cz.klajmajk.camunda.varna.entities.Entry;
import cz.klajmajk.camunda.varna.entities.Record;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.cdi.BusinessProcess;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.TimeUnit;
import org.ocpsoft.prettytime.units.JustNow;

/**
 *
 * @author Adam
 */
@ViewScoped
@Named
public class ManagedBean implements Serializable {

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    @Inject
    private SessionBean sessionBean;

    @Inject
    private RuntimeService runtimeService;

    @Inject
    private BusinessProcess businessProcess;

    public String getCurrentTimeAgo() {
        return timeAgo(sessionBean.getCurrent().getDate());
    }

    public String getStage(String pid) {
        return sessionBean.getSubProcessStage(pid);
    }

    public String getStageStart(String pid) {
        Date start = sessionBean.getStageStart(pid);
        if (start != null) {
            PeriodFormatter formatter = new PeriodFormatterBuilder().
                    printZeroAlways()
                    .appendHours().minimumPrintedDigits(2).appendSuffix(":")
                    .printZeroAlways()
                    .appendMinutes().minimumPrintedDigits(2).appendSuffix(":")
                    .printZeroAlways()
                    .appendSeconds().minimumPrintedDigits(2)
                    .printZeroNever()
                    .toFormatter();

            Period period = new Period(new Date().getTime() - sessionBean.getStageStart(pid).getTime());
            return formatter.print(period);
        }
        return "Neprobíhá";
    }

    public List<Object> getTempMeasuredList(List<Record> records) {
        return getEntriesByType(records, "temp1");

    }

    public List<Object> getPowerList(List<Record> records) {
        return getEntriesByType(records, "power1");

    }

    public String getDates() {

        return getJson(getDates(sessionBean.getRecords()));
    }

    public static void main(String[] args) throws JAXBException {
        List<Object> list = new ArrayList<>();
        list.add("sdafsad");
        list.add("sdafsaed");
        ManagedBean mb = new ManagedBean();
        System.out.println(mb.getJson(list));
    }

    public String getTempMeasured() {
        return getJson(getTempMeasuredList(sessionBean.getRecords()));
    }

    public String getPower() {
        return getJson(getPowerList(sessionBean.getRecords()));
    }

    public String getJson(List<Object> list) {
        String json = "[";
        for (Object object : list) {
            json += "\"" + object.toString() + "\", ";
        }
        if (json.length() > 1) {
            json = json.substring(0, json.length() - 2);
        }
        json += "]";
        return json;
    }

    private static Marshaller getDefaultMarshaller(Class[] ca) throws JAXBException {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(MarshallerProperties.MEDIA_TYPE, "application/json");
        properties.put(MarshallerProperties.JSON_INCLUDE_ROOT, false);

        JAXBContext ctx = JAXBContext.newInstance(ca, properties);
        return ctx.createMarshaller();

    }

    public List<Object> getDates(List<Record> records) {
        List<Object> toReturn = new ArrayList<>();
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

    public Object getEntry(String name) {
        if (sessionBean.getCurrent() != null) {
            return getEntryByType(sessionBean.getCurrent().getEntries(), name);
        }
        return null;
    }

    private Object getEntryByType(Set<Entry> entries, String type) {
        for (Entry entry : entries) {
            if (type.equals(entry.getType())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public String getCurentTaskName() {
        for (ActivityInstance inst : runtimeService.getActivityInstance(businessProcess.getProcessInstanceId()).getChildActivityInstances()) {
            if (!inst.getActivityId().equals("monitoring")) {
                return inst.getActivityName();
            }
        }
        return null;
    }

    public String timeAgo(Date date) {
        if (date != null) {
            PrettyTime p = new PrettyTime();
            for (TimeUnit t : p.getUnits()) {
                if (t instanceof JustNow) {
                    ((JustNow) t).setMaxQuantity(1000L);
                }
            }
            String toReturn = p.format(date);
            if (toReturn.equals("před chvílí")) {
                return "teď";
            }
            return p.format(date);
        } else {
            return "";
        }
    }

}
