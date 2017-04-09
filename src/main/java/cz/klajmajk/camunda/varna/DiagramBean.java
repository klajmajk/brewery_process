/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.klajmajk.camunda.varna;

import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.cdi.BusinessProcess;
import org.camunda.bpm.engine.runtime.ProcessInstance;

/**
 *
 * @author Adam
 */
@Stateless
@Named
public class DiagramBean {

    @Inject
    private RepositoryService rs;
    @Inject
    private BusinessProcess bp;
    @Inject
    private RuntimeService runtimeService;

    private ProcessInstance pi;

    @PostConstruct
    private void init() {
        pi = runtimeService.createProcessInstanceQuery().processInstanceId(bp.getProcessInstanceId()).singleResult();
    }

    public String getProcessDiagram() throws IOException {
        InputStream is = rs.getProcessModel(pi.getProcessDefinitionId());

        return IOUtils.toString(is, "UTF-8").replaceAll("\\$\\{.*?\\}", "");
    }

    public String getElements() {
        String toReturn = "";
        for (String id : runtimeService.getActiveActivityIds(pi.getId())) {
            toReturn += "'"+id+ "',";
        }
        return toReturn.substring(0, toReturn.length() -1);
        
    }
}
