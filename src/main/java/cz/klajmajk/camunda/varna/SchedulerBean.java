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

    private String processInstanceId;

    @Inject
    private RuntimeService runtimeService;
    
    @Inject
    private SessionBean sessionBean;

    private static final String PERSIST = "persist";
    private static final String REFRESH = "refresh";

    public void init(String processInstanceId) {
        clear();
        System.out.println("Inniting timers");
        timerService.createTimer(1000, 1000, processInstanceId + ";" + REFRESH);
        timerService.createTimer(10000, 10000, processInstanceId + ";" + PERSIST);

    }

    public void touch(String processInstanceId) {
        if (this.processInstanceId == null) {
            init(processInstanceId);
        }
    }

    private void clear() {
        for (Timer timer : timerService.getTimers()) {
            if (timer.getInfo() != null) {
                if (((String) timer.getInfo()).contains(PERSIST) || ((String) timer.getInfo()).contains(REFRESH)) {
                    timer.cancel();
                }
            }
        }
    }

    public void finish() {
        clear();
    }

    @Timeout
    public void execute(Timer timer) {
        if (timer.getInfo() != null) {
            String[] data = ((String) timer.getInfo()).split(";");
            this.processInstanceId = data[0];
            if (runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult() != null) {
                if (REFRESH.equals(data[1])) {
                    refresh(processInstanceId);
                } else if (PERSIST.equals(data[1])) {
                    persist(processInstanceId);
                }
            } else {
                clear();
            }
        }

    }

    public void persist(String processInstanceId) {
        
        if (sessionBean.getCurrent() != null) {
            sessionBean.getRecords().add(sessionBean.getCurrent());
        }
        runtimeService.setVariable(processInstanceId, "records", sessionBean.getRecords());
    }

    public void refresh(String processInstanceId) {
        Record newRecord = getRecordFromREST(runtimeService.getVariable(processInstanceId, "brewUrl").toString());
        if (newRecord != null) {
            sessionBean.setCurrent(newRecord);
//        runtimeService.setVariable(processInstanceId, "subProcessStage", sessionBean.getSubProcessStage(processInstanceId));
            String subProcessId = sessionBean.getSubprocessInstanceId(processInstanceId);
            if (subProcessId != null) {
                runtimeService.setVariable(subProcessId, "current", sessionBean.getCurrent());
            }
        }

    }

    private Record getRecordFromREST(String url) {
//        Set<Entry> entries = new HashSet();
//        entries.add(new Entry("tempMeasured", (new Random().nextFloat()) * 40));
//        entries.add(new Entry("tempSet", (new Random().nextFloat()) * 40));
//        entries.add(new Entry("power", (new Random().nextInt(11)) * 10));

        return wsConsumer.getCurrentState(url);
    }
}
