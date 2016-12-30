/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.klajmajk.camunda.varna;

import cz.klajmajk.camunda.varna.entities.Record;
import cz.klajmajk.camunda.varna.ws.VarnaRESTController;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.inject.Named;
import org.camunda.bpm.engine.RuntimeService;

/**
 *
 * @author Adam
 */
@Startup
@Singleton
@Named
public class SchedulerBean {

    @Resource
    private TimerService timerService;

    @Inject
    private VarnaRESTController wsConsumer;

    private List<Record> records;

    private Record current;

    private String processInstanceId;

    @Inject
    private RuntimeService runtimeService;

    @Inject
    private SessionBean sessionBean;

    private static final String PERSIST = "persist";
    private static final String REFRESH = "refresh";

    public void init(String processInstanceId) {
        clear();
        timerService.createTimer(1000, 1000, REFRESH);
        timerService.createTimer(10000, 10000, PERSIST);
        this.processInstanceId = processInstanceId;

    }

    public void touch(String processInstanceId) {
        if (this.processInstanceId == null) {
            init(processInstanceId);
        }
    }

    private void clear() {
        for (Timer timer : timerService.getTimers()) {
            if (PERSIST.equals(timer.getInfo()) || REFRESH.equals(timer.getInfo())) {
                timer.cancel();
            }
        }
        current = null;
        records = null;
        processInstanceId = null;
    }

    public void finish() {
        clear();
    }

    @Timeout
    public void execute(Timer timer) {
        if (processInstanceId != null) {
            if (runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult() != null) {
                if (REFRESH.equals(timer.getInfo())) {
                    refresh(processInstanceId);
                } else if (PERSIST.equals(timer.getInfo())) {
                    persist(processInstanceId);
                }
            }
            else{
                clear();
            }
        }

    }

    public void persist(String processInstanceId) {
        Logger.getLogger(SessionBean.class.getName()).log(Level.INFO, "persist called");
        if (records == null) {
            records = new ArrayList<Record>();
        }
        if (current != null) {
            records.add(current);
        }
        runtimeService.setVariable(processInstanceId, "records", records);
    }

    public void refresh(String processInstanceId) {
        current = getRecordFromREST();
        Logger.getLogger(SessionBean.class.getName()).log(Level.INFO, "refresh called setting current to: " + current);
//        runtimeService.setVariable(processInstanceId, "subProcessStage", sessionBean.getSubProcessStage(processInstanceId));
        runtimeService.setVariable(sessionBean.getSubprocessInstanceId(processInstanceId), "current", current);

    }

    private Record getRecordFromREST() {
//        Set<Entry> entries = new HashSet();
//        entries.add(new Entry("tempMeasured", (new Random().nextFloat()) * 40));
//        entries.add(new Entry("tempSet", (new Random().nextFloat()) * 40));
//        entries.add(new Entry("power", (new Random().nextInt(11)) * 10));

        return wsConsumer.getCurrentState();
    }

    public List<Record> getRecords(String processInstanceId) {
        if (records != null) {
            return records;
        }
        return new ArrayList<>();
    }

    public Record getCurrent() {
        return current;
    }
}
