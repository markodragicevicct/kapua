/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.app.console.client.user.tabs.permission;

import java.util.List;

import org.eclipse.kapua.app.console.client.messages.ConsoleUserMessages;
import org.eclipse.kapua.app.console.client.ui.dialog.entity.EntityAddEditDialog;
import org.eclipse.kapua.app.console.client.ui.panel.FormPanel;
import org.eclipse.kapua.app.console.client.util.DialogUtils;
import org.eclipse.kapua.app.console.shared.model.GwtPermission;
import org.eclipse.kapua.app.console.shared.model.GwtPermission.GwtAction;
import org.eclipse.kapua.app.console.shared.model.GwtPermission.GwtDomain;
import org.eclipse.kapua.app.console.shared.model.GwtSession;
import org.eclipse.kapua.app.console.shared.model.authorization.GwtAccessInfo;
import org.eclipse.kapua.app.console.shared.model.authorization.GwtAccessPermission;
import org.eclipse.kapua.app.console.shared.model.authorization.GwtAccessPermissionCreator;
import org.eclipse.kapua.app.console.shared.service.GwtAccessInfoService;
import org.eclipse.kapua.app.console.shared.service.GwtAccessInfoServiceAsync;
import org.eclipse.kapua.app.console.shared.service.GwtAccessPermissionService;
import org.eclipse.kapua.app.console.shared.service.GwtAccessPermissionServiceAsync;
import org.eclipse.kapua.app.console.shared.service.GwtDomainService;
import org.eclipse.kapua.app.console.shared.service.GwtDomainServiceAsync;

import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PermissionAddDialog extends EntityAddEditDialog {

    private final static ConsoleUserMessages MSGS = GWT.create(ConsoleUserMessages.class);

    private final static GwtDomainServiceAsync gwtDomainService = GWT.create(GwtDomainService.class);
    private final static GwtAccessPermissionServiceAsync gwtAccessPermissionService = GWT.create(GwtAccessPermissionService.class);
    private final static GwtAccessInfoServiceAsync gwtAccessInfoService = GWT.create(GwtAccessInfoService.class);

    private SimpleComboBox<GwtDomain> domainsCombo;
    private SimpleComboBox<GwtAction> actionsCombo;
    private TextField<String> targetScopeIdTxtField;
    private TextField<String> groupIdTxtField;
    
    private String accessInfoId;

    public PermissionAddDialog(GwtSession currentSession, String userId) {
        super(currentSession);
        gwtAccessInfoService.findByUserIdOrCreate(currentSession.getSelectedAccount().getId(), userId, new AsyncCallback<GwtAccessInfo>() {

            @Override
            public void onSuccess(GwtAccessInfo result) {
                accessInfoId = result.getId();
                m_submitButton.enable();
            }

            @Override
            public void onFailure(Throwable caught) {
                m_exitMessage = MSGS.dialogAddPermissionErrorAccessInfo(caught.getLocalizedMessage());
                m_exitStatus = false;
                hide();
            }
        });

        DialogUtils.resizeDialog(this, 400, 400);
    }

    @Override
    public void submit() {
        GwtAccessPermissionCreator gwtAccessPermissionCreator = new GwtAccessPermissionCreator();

        gwtAccessPermissionCreator.setScopeId(currentSession.getSelectedAccount().getId());

        gwtAccessPermissionCreator.setAccessInfoId(accessInfoId);
        gwtAccessPermissionCreator.setPermission(new GwtPermission(domainsCombo.getValue().getValue(), actionsCombo.getValue().getValue(), targetScopeIdTxtField.getValue(), groupIdTxtField.getValue()));

        gwtAccessPermissionService.create(xsrfToken, gwtAccessPermissionCreator, new AsyncCallback<GwtAccessPermission>() {

            @Override
            public void onSuccess(GwtAccessPermission gwtAccessPermission) {
                m_exitStatus = true;
                m_exitMessage = MSGS.dialogAddPermissionConfirmation();   // TODO Localize
                hide();
            }

            @Override
            public void onFailure(Throwable cause) {
                unmask();

                m_submitButton.enable();
                m_cancelButton.enable();
                m_status.hide();

                m_exitStatus = false;
                m_exitMessage = MSGS.dialogAddError(MSGS.dialogAddPermissionError(cause.getLocalizedMessage()));

                hide();
            }
        });

        System.out.println("create access permission");

    }

    @Override
    public String getHeaderMessage() {
        return MSGS.dialogAddPermissionHeader();
    }

    @Override
    public String getInfoMessage() {
        return MSGS.dialogAddPermissionInfo();
    }

    @Override
    public void createBody() {
        FormPanel permissionFormPanel = new FormPanel(FORM_LABEL_WIDTH);

        //
        // Domain
        domainsCombo = new SimpleComboBox<GwtDomain>();
        domainsCombo.setEditable(false);
        domainsCombo.setTypeAhead(false);
        domainsCombo.setAllowBlank(false);
        domainsCombo.setFieldLabel(MSGS.dialogAddPermissionDomain());
        domainsCombo.setTriggerAction(TriggerAction.ALL);
        gwtDomainService.findAll(new AsyncCallback<List<GwtDomain>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        m_exitMessage = MSGS.dialogAddPermissionErrorDomains(caught.getLocalizedMessage());
                        m_exitStatus = false;
                        hide();
                    }

                    @Override
                    public void onSuccess(List<GwtDomain> result) {
                        domainsCombo.add(result);
                        actionsCombo.enable();
                    }
                });
        
        domainsCombo.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<GwtDomain>>() {
            
            @Override
            public void selectionChanged(SelectionChangedEvent<SimpleComboValue<GwtDomain>> se) {
                gwtDomainService.findActionsByDomainName(se.getSelectedItem().getValue().toString(), new AsyncCallback<List<GwtAction>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        m_exitMessage = MSGS.dialogAddPermissionErrorActions(caught.getLocalizedMessage());
                        m_exitStatus = false;
                        hide();
                    }

                    @Override
                    public void onSuccess(List<GwtAction> result) {
                        actionsCombo.removeAll();
                        actionsCombo.add(result);
                    }
                });
                
            }
        });
        permissionFormPanel.add(domainsCombo);
        
        //
        // Action
        actionsCombo = new SimpleComboBox<GwtAction>();
        actionsCombo.setEditable(false);
        actionsCombo.setTypeAhead(false);
        actionsCombo.setAllowBlank(false);
        actionsCombo.setFieldLabel(MSGS.dialogAddPermissionAction());
        actionsCombo.setTriggerAction(TriggerAction.ALL);
        
        actionsCombo.disable();
        permissionFormPanel.add(actionsCombo);
        
        targetScopeIdTxtField = new TextField<String>();
        targetScopeIdTxtField.setFieldLabel(MSGS.dialogAddPermissionTargetScopeId());
        
        permissionFormPanel.add(targetScopeIdTxtField);
        
        groupIdTxtField = new TextField<String>();
        groupIdTxtField.setFieldLabel(MSGS.dialogAddPermissionGroupId());
        
        permissionFormPanel.add(groupIdTxtField);
        
        //
        // Add form panel to body
        m_bodyPanel.add(permissionFormPanel);
    }

    @Override
    protected void onRender(Element parent, int pos) {
        super.onRender(parent, pos);
        m_submitButton.disable();
    }
}
