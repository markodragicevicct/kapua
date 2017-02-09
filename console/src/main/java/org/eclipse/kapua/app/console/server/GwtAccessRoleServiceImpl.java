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
package org.eclipse.kapua.app.console.server;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.app.console.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.shared.GwtKapuaException;
import org.eclipse.kapua.app.console.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.shared.model.authorization.GwtAccessRole;
import org.eclipse.kapua.app.console.shared.model.authorization.GwtAccessRoleCreator;
import org.eclipse.kapua.app.console.shared.service.GwtAccessRoleService;
import org.eclipse.kapua.app.console.shared.util.GwtKapuaModelConverter;
import org.eclipse.kapua.app.console.shared.util.KapuaGwtModelConverter;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoService;
import org.eclipse.kapua.service.authorization.access.AccessRole;
import org.eclipse.kapua.service.authorization.access.AccessRoleCreator;
import org.eclipse.kapua.service.authorization.access.AccessRoleListResult;
import org.eclipse.kapua.service.authorization.access.AccessRoleService;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RoleService;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class GwtAccessRoleServiceImpl extends KapuaRemoteServiceServlet implements GwtAccessRoleService {

    private static final long serialVersionUID = 3606053200278262228L;

    @Override
    public GwtAccessRole create(GwtXSRFToken xsrfToken, GwtAccessRoleCreator gwtAccessRoleCreator) throws GwtKapuaException {

        //
        // Checking XSRF token
        checkXSRFToken(xsrfToken);

        //
        // Do create
        GwtAccessRole gwtAccessRole = null;
        try {
            // Convert from GWT Entity
            AccessRoleCreator accessRoleCreator = GwtKapuaModelConverter.convert(gwtAccessRoleCreator);

            // Create
            KapuaLocator locator = KapuaLocator.getInstance();
            AccessRoleService accessRoleService = locator.getService(AccessRoleService.class);
            AccessRole accessRole = accessRoleService.create(accessRoleCreator);

            // Convert
            gwtAccessRole = KapuaGwtModelConverter.convert(accessRole);

        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        //
        // Return result
        return gwtAccessRole;
    }

    @Override
    public void delete(GwtXSRFToken gwtXsrfToken, String scopeShortId, String accessRoleShortId) throws GwtKapuaException {

        //
        // Checking XSRF token
        checkXSRFToken(gwtXsrfToken);

        //
        // Do delete
        try {
            // Convert from GWT Entity
            KapuaId scopeId = GwtKapuaModelConverter.convert(scopeShortId);
            KapuaId accessRoleId = GwtKapuaModelConverter.convert(accessRoleShortId);

            // Delete
            KapuaLocator locator = KapuaLocator.getInstance();
            AccessRoleService accessRoleService = locator.getService(AccessRoleService.class);
            accessRoleService.delete(scopeId, accessRoleId);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
    }

    @Override
    public PagingLoadResult<GwtAccessRole> findByUserId(PagingLoadConfig loadConfig, String scopeShortId, String userShortId) throws GwtKapuaException {
        //
        // Do get
        List<GwtAccessRole> gwtAccessRoles = new ArrayList<GwtAccessRole>();
        if (userShortId != null) {

            try {
                KapuaLocator locator = KapuaLocator.getInstance();
                RoleService roleService = locator.getService(RoleService.class);
                AccessInfoService accessInfoService = locator.getService(AccessInfoService.class);
                AccessRoleService accessRoleService = locator.getService(AccessRoleService.class);

                KapuaId scopeId = GwtKapuaModelConverter.convert(scopeShortId);
                KapuaId userId = GwtKapuaModelConverter.convert(userShortId);

                AccessInfo accessInfo = accessInfoService.findByUserId(scopeId, userId);
                
                if (accessInfo != null) {
                    AccessRoleListResult accessRoleList = accessRoleService.findByAccessInfoId(scopeId, accessInfo.getId());

                    for (AccessRole accessRole : accessRoleList.getItems()) {
                        Role role = roleService.find(scopeId, accessRole.getRoleId());
                        GwtAccessRole gwtAccessRole = KapuaGwtModelConverter.convert(role, accessRole);
                        gwtAccessRoles.add(gwtAccessRole);
                    }
                }
            } catch (Throwable t) {
                KapuaExceptionHandler.handle(t);
            }
        }
        return new BasePagingLoadResult<GwtAccessRole>(gwtAccessRoles, 0, gwtAccessRoles.size());
    }
}