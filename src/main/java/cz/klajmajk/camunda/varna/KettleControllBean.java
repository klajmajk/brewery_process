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

/**
 *
 * @author Adam
 */
@Named
@Stateless
public class KettleControllBean implements Serializable {

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
            varnaRest.setPower(power + 10);
        }

    }

    public void powerDown() throws Exception {
        int power = getPower();
        if (power - 10 >= 0) {
            varnaRest.setPower(power - 10);
        }
    }

    public void setPowerTo(int power) throws Exception {
        varnaRest.setPower(power);
    }

    public boolean heatingPhaseFinished(float tempHold, float delta) {
        Logger.getLogger(KettleControllBean.class.getName()).log(Level.INFO, tempHold + ", " + delta + ", " + getTempMeasured());
        Logger.getLogger(KettleControllBean.class.getName()).log(Level.INFO, "Return" + ((tempHold - delta < getTempMeasured()) && (tempHold + delta > getTempMeasured())));
        return (tempHold - delta < getTempMeasured()) && (tempHold + delta > getTempMeasured());
    }

    public boolean shouldHeatMore(float tempHold, float delta) {
        Logger.getLogger(KettleControllBean.class.getName()).log(Level.INFO, "Testing if should heat more");
        return (tempHold - delta > getTempMeasured());
    }

    public boolean shouldHeatLess(float tempHold, float delta) {
        return (tempHold + delta < getTempMeasured());
    }

    public boolean shouldHeatFaster(float tempHold, float delta) {
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

    public void setupNewStage(int power) throws Exception {
        setPowerTo(power);
        sessionBean.setStageStart(new Date());
    }
}
