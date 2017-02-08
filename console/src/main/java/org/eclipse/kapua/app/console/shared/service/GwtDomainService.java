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
package org.eclipse.kapua.app.console.shared.service;

import java.util.List;

import org.eclipse.kapua.app.console.shared.GwtKapuaException;
import org.eclipse.kapua.app.console.shared.model.GwtPermission.GwtAction;
import org.eclipse.kapua.app.console.shared.model.GwtPermission.GwtDomain;

import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("domain")
public interface GwtDomainService extends RemoteService {

    /**
     * Returns the list of all Domains which belong to an account.
     * 
     * @return a list of {@link Domain} objects
     * @throws GwtKapuaException
     * 
     */
    public List<GwtDomain> findAll()
        throws GwtKapuaException;
    
    /**
     * Returns the list of all Actions which belong to a Domain.
     * 
     * @param domainIdStirng    the ID of the domain
     * @return a list of {@link Action} objects
     * @throws GwtKapuaException
     * 
     */
    public List<GwtAction> findActionsByDomainName(String domainName)
        throws GwtKapuaException;
}