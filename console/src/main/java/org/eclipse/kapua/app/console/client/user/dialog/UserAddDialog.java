/*******************************************************************************
 * Copyright (c) 2011, 2017 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.client.user.dialog;

import org.eclipse.kapua.app.console.client.ui.dialog.entity.EntityAddEditDialog;
import org.eclipse.kapua.app.console.client.ui.panel.FormPanel;
import org.eclipse.kapua.app.console.client.util.ConfirmPasswordFieldValidator;
import org.eclipse.kapua.app.console.client.util.Constants;
import org.eclipse.kapua.app.console.client.util.DialogUtils;
import org.eclipse.kapua.app.console.client.util.PasswordFieldValidator;
import org.eclipse.kapua.app.console.client.util.TextFieldValidator;
import org.eclipse.kapua.app.console.client.util.TextFieldValidator.FieldType;
import org.eclipse.kapua.app.console.shared.model.GwtSession;
import org.eclipse.kapua.app.console.shared.model.authorization.GwtAccessRole;
import org.eclipse.kapua.app.console.shared.model.authorization.GwtAccessRoleCreator;
import org.eclipse.kapua.app.console.shared.model.user.GwtUser;
import org.eclipse.kapua.app.console.shared.model.user.GwtUserCreator;
import org.eclipse.kapua.app.console.shared.service.GwtRoleService;
import org.eclipse.kapua.app.console.shared.service.GwtRoleServiceAsync;
import org.eclipse.kapua.app.console.shared.service.GwtUserService;
import org.eclipse.kapua.app.console.shared.service.GwtUserServiceAsync;

import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class UserAddDialog extends EntityAddEditDialog {

    protected FieldSet statusFieldSet;
    protected FieldSet infoFieldSet;

    protected TextField<String> username;
    protected TextField<String> password;
    protected TextField<String> confirmPassword;
    protected TextField<String> displayName;
    protected TextField<String> email;
    protected TextField<String> phoneNumber;
    protected NumberField optlock;
    protected SimpleComboBox<String> statusCombo;
    protected CheckBox lockedCheckBox;
    protected CheckBoxGroup lockedCheckGroup;
    protected LabelField loginOnLabel;
    protected LabelField loginAttemptsLabel;
    protected LabelField loginAttemptsResetOnLabel;
    protected LabelField lockedOnLabel;
    protected LabelField unlockOnLabel;
    
    private GwtUserServiceAsync gwtUserService = GWT.create(GwtUserService.class);
    
    public UserAddDialog(GwtSession currentSession) {
        super(currentSession);
        
        DialogUtils.resizeDialog(this, 400, 400);
    }

    @Override
    public void createBody() {
        
        FormData formData = new FormData("0");

        FormPanel userFormPanel = new FormPanel(FORM_LABEL_WIDTH);
        userFormPanel.setFrame(false);
        userFormPanel.setBorders(false);
        userFormPanel.setBodyBorder(false);
        userFormPanel.setHeaderVisible(false);
        userFormPanel.setPadding(0);
        
        //
        // User info tab
        //
        infoFieldSet = new FieldSet();
        infoFieldSet.setHeading(MSGS.userFormInfo());
        infoFieldSet.setBorders(true);
        infoFieldSet.setStyleAttribute("margin", "0px 10px 0px 10px");

        FormLayout layout = new FormLayout();
        layout.setLabelWidth(Constants.LABEL_WIDTH_FORM);
        infoFieldSet.setLayout(layout);
        infoFieldSet.setStyleAttribute("background-color", "E8E8E8");

        username = new TextField<String>();
        username.setAllowBlank(false);
        username.setName("userName");
        username.setFieldLabel("* " + MSGS.userFormName());
        username.setValidator(new TextFieldValidator(username, FieldType.NAME));
        infoFieldSet.add(username, formData);

        password = new TextField<String>();
        password.setAllowBlank(false);
        password.setName("password");
        password.setFieldLabel("* " + MSGS.userFormPassword());
        password.setValidator(new PasswordFieldValidator(password));
        password.setPassword(true);
        infoFieldSet.add(password, formData);

        confirmPassword = new TextField<String>();
        confirmPassword.setAllowBlank(false);
        confirmPassword.setName("confirmPassword");
        confirmPassword.setFieldLabel("* " + MSGS.userFormConfirmPassword());
        confirmPassword.setValidator(new ConfirmPasswordFieldValidator(confirmPassword, password));
        confirmPassword.setPassword(true);
        infoFieldSet.add(confirmPassword, formData);

        LabelField tooltip = new LabelField();
        tooltip.setValue(MSGS.userFormPasswordTooltip());
        tooltip.setStyleAttribute("margin-top", "-5px");
        tooltip.setStyleAttribute("color", "gray");
        tooltip.setStyleAttribute("font-size", "10px");
        infoFieldSet.add(tooltip, formData);

        displayName = new TextField<String>();
        displayName.setName("displayName");
        displayName.setFieldLabel(MSGS.userFormDisplayName());
        infoFieldSet.add(displayName, formData);

        email = new TextField<String>();
        email.setName("userEmail");
        email.setFieldLabel(MSGS.userFormEmail());
        email.setValidator(new TextFieldValidator(email, FieldType.EMAIL));
        infoFieldSet.add(email, formData);

        phoneNumber = new TextField<String>();
        phoneNumber.setName("phoneNumber");
        phoneNumber.setFieldLabel(MSGS.userFormPhoneNumber());
        infoFieldSet.add(phoneNumber, formData);

        optlock = new NumberField();
        optlock.setName("optlock");
        optlock.setEditable(false);
        optlock.setVisible(false);
        infoFieldSet.add(optlock, formData);

//        loadUser();

        userFormPanel.add(infoFieldSet);
        
        m_bodyPanel.add(userFormPanel);

//        setEditability();
    }

    @Override
    public void submit() {
        GwtUserCreator gwtUserCreator = new GwtUserCreator();

        gwtUserCreator.setScopeId(currentSession.getSelectedAccount().getId());
        
        gwtUserCreator.setUsername(username.getValue());
        gwtUserCreator.setPassword(password.getValue());
        gwtUserCreator.setDisplayName(displayName.getValue());
        gwtUserCreator.setEmail(email.getValue());
        gwtUserCreator.setPhoneNumber(phoneNumber.getValue());
        
        gwtUserService.create(xsrfToken, gwtUserCreator, new AsyncCallback<GwtUser>() {

            @Override
            public void onSuccess(GwtUser arg0) {
                m_exitStatus = true;
                m_exitMessage = "OK"; // TODO Localize
                hide();
            }

            @Override
            public void onFailure(Throwable cause) {
                unmask();
                
                m_submitButton.enable();
                m_cancelButton.enable();
                m_status.hide();
                
                m_exitStatus = false;
                m_exitMessage = "KO"; // TODO Localize with cause
                
                hide();
            }
        });
        
        System.out.println("create user");

    }

    @Override
    public String getHeaderMessage() {
        return "Title";
    }

    @Override
    public String getInfoMessage() {
        return "Info";
    }

}
