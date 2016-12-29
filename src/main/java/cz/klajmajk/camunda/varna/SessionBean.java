/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.klajmajk.camunda.varna;

import cz.klajmajk.camunda.varna.entities.Record;
import cz.klajmajk.camunda.varna.ws.VarnaRESTController;
import javax.inject.Named;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import org.camunda.bpm.engine.cdi.BusinessProcess;
import org.camunda.bpm.engine.cdi.annotation.BusinessProcessScoped;

/**
 * @author Adam
 */
@Named
@ApplicationScoped
public class SessionBean implements Serializable {

    @Inject
    private BusinessProcess businessProcess;

    @Inject
    private VarnaRESTController wsConsumer;

    private List<Record> records;
    private Record current;


    private Date stageStart;

    public void persist() {
        Logger.getLogger(SessionBean.class.getName()).log(Level.INFO, "persist called");
        if (records == null) {
            records = (List<Record>) businessProcess.getVariable("records");
            if (records == null) {
                records = new ArrayList<Record>();
            }
        }
        records.add(businessProcess.getVariable("current"));
        businessProcess.setVariable("records", records);
    }

    public void refresh() {
        current = getRecordFromREST();
        Logger.getLogger(SessionBean.class.getName()).log(Level.INFO, "refresh called setting current to: "+current);
        businessProcess.setVariable("current", current);

    }

    private Record getRecordFromREST() {
//        Set<Entry> entries = new HashSet();
//        entries.add(new Entry("tempMeasured", (new Random().nextFloat()) * 40));
//        entries.add(new Entry("tempSet", (new Random().nextFloat()) * 40));
//        entries.add(new Entry("power", (new Random().nextInt(11)) * 10));

        return wsConsumer.getCurrentState();
    }

    public Record getCurrent() {
        if(current != null) return current;
        return businessProcess.getVariable("current");
    }

    public Date getStageStart() {
        return stageStart;
    }

    public void setStageStart(Date stageStart) {
        this.stageStart = stageStart;
    }

    public List<Record> getRecords() {
        return records;
    }

}
