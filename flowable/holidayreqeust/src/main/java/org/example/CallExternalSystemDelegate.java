package org.example;

import org.flowable.engine.delegate.DelegateExecution;

public class CallExternalSystemDelegate {

    public void execute(DelegateExecution execution) {
        System.out.println("Calling the external system for employee "
                + execution.getVariable("employee"));
    }
}
