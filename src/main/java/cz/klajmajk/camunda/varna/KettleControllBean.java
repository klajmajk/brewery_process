/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.klajmajk.camunda.varna;

import cz.klajmajk.camunda.varna.entities.Entry;
import cz.klajmajk.camunda.varna.entities.Record;
import cz.klajmajk.camunda.varna.ws.VarnaRESTController;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import org.camunda.bpm.engine.cdi.BusinessProcess;

/**
 *
 * @author Adam
 */
@Named
@Stateless
public class KettleControllBean implements Serializable {
    @Inject
    private BusinessProcess businessProcess;
    @Inject
    private SessionBean sessionBean;
    @Inject
    private VarnaRESTController varnaRest;

    private int getPower() {
        int power = -1;
        if (sessionBean.getCurrent() != null) {
            for (Entry entry : sessionBean.getCurrent().getEntries()) {
                if ("power1".equals(entry.getType())) {
                    power = (Integer) entry.getValue();
                }
            }

        }
        return power;
    }

    private float getTempMeasured() {
        float tempMeasured = -1;
        if (sessionBean.getCurrent() != null) {
            for (Entry entry : sessionBean.getCurrent().getEntries()) {
                if ("temp1".equals(entry.getType())) {
                    tempMeasured = (Float) entry.getValue();
                }
            }
        }
        return tempMeasured;
    }

    public void powerUp() throws Exception {
        int power = getPower();
        if (power + 10 <= 100) {
            setPowerTo(power + 10);
        }

    }

    public void powerDown() throws Exception {
        int power = getPower();
        if (power - 10 >= 0) {
            setPowerTo(power - 10);
        }
    }

    public void setPowerTo(int power) throws Exception {
        Logger.getLogger(KettleControllBean.class.getName()).log(Level.INFO, "Setting power to "+power);
        varnaRest.setPower(power, businessProcess.getVariable("brewUrl").toString());
    }

    public boolean heatingPhaseFinished(float tempHold, float delta) {
        return (tempHold - delta < getTempMeasured()) && (tempHold + delta > getTempMeasured());
    }

    public boolean shouldHeatMore(float tempHold, float delta) {
        Logger.getLogger(KettleControllBean.class.getName()).log(Level.INFO, "Testing if should heat more");
        return (tempHold - delta/2 > getTempMeasured());
    }

    public boolean shouldHeatLess(float tempHold, float delta) {
        return (tempHold  < getTempMeasured());
    }

    public boolean shouldHeatFaster(float tempToReach, float delta) {
        Logger.getLogger(KettleControllBean.class.getName()).log(Level.INFO, "Testing if should heat faster");
//        float temp1 = -1;
//        float temp2 = -1;
//        int tempDiff = 0;
//        
//        for (Record record : sessionBean.getRecords()) {
//            for (Entry entry : record.getEntries()) {
//                if ("tempMeasured1".equals(entry.getType())) {
//                    if(record.getDate().)
//                    int tempMeasured = (Integer) entry.getValue();
//                }
//            }
//
//        }
        return false;
    }

    public void setupNewStage(int power){
        try {
            setPowerTo(power * 10);
        } catch (Exception ex) {
            Logger.getLogger(KettleControllBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void setupStageHold(int power) throws Exception {
        Logger.getLogger(KettleControllBean.class.getName()).log(Level.INFO, "Setting up hold");
        setPowerTo(power * 10);
        businessProcess.setVariable("stageStartDate", new Date());
    }
}
