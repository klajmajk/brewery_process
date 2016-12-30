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
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.UserTransaction;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;

/**
 * @author Adam
 */
@Named
@Singleton
public class SessionBean implements Serializable {

    @Inject
    private RuntimeService runtimeService;

    public String getSubprocessInstanceId(String processInstanceId) {
        if (processInstanceId != null) {
            ProcessInstance subprocessInstance = runtimeService.createProcessInstanceQuery().superProcessInstanceId(processInstanceId).singleResult();
            if (subprocessInstance != null) {
                return subprocessInstance.getProcessInstanceId();
            }
        }
        return null;
    }

    public String getSubProcessStage(String processInstanceId) {
        String subProcessId = getSubprocessInstanceId(processInstanceId);
        if (subProcessId != null) {
            VariableInstance vi = runtimeService.createVariableInstanceQuery().processInstanceIdIn(subProcessId).variableName("stage").singleResult();
            if (vi != null) {
                return (vi.getValue() != null ? vi.getValue().toString() : "none");
            }
        }
        return "none";
    }

    public Object getSubProcessVariable(String processInstanceId, String name) {
        String subProcessId = getSubprocessInstanceId(processInstanceId);
        if (subProcessId != null) {
            VariableInstance vi = runtimeService.createVariableInstanceQuery().processInstanceIdIn(subProcessId).variableName(name).singleResult();
            if (vi != null) {
                return vi.getValue();
            }
        }
        return null;
    }

    public Record getSuperProcessCurrent(String processInstanceId) {
        ProcessInstance subprocessInstance = runtimeService.createProcessInstanceQuery().subProcessInstanceId(processInstanceId).singleResult();
        if (subprocessInstance != null) {
            VariableInstance vi = runtimeService.createVariableInstanceQuery().processInstanceIdIn(subprocessInstance.getId()).variableName("current").singleResult();
            if (vi != null) {
                return (Record) vi.getValue();
            }
        }
        return null;
    }

    public Date getStageStart(String processInstanceId) {
        return (Date) getSubProcessVariable(processInstanceId, "stageStartDate");
    }

}
