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
 *******************************************************************************/

INSERT INTO athz_domain
	VALUES
		(NULL,  1, NOW(), 1, 'account',				'accountService'),
		(NULL,  2, NOW(), 1, 'broker',				'brokerService'),
		(NULL,  3, NOW(), 1, 'credential',			'credentialService'),
		(NULL,  4, NOW(), 1, 'device',				'deviceService'),
		(NULL,  5, NOW(), 1, 'device_connection',	'deviceConnectionService'),
		(NULL,  6, NOW(), 1, 'device_management',	'deviceManagementService'),
		(NULL,  7, NOW(), 1, 'device_event',		'deviceEventService'),
		(NULL,  8, NOW(), 1, 'device_lifecycle',	'deviceLifecycleService'),
		(NULL,  9, NOW(), 1, 'data',				'datastoreService'),
		(NULL, 10, NOW(), 1, 'domain',				'domainService'),
		(NULL, 11, NOW(), 1, 'group',				'groupService'),
		(NULL, 12, NOW(), 1, 'access_info',			'accessInfoService'),
		(NULL, 13, NOW(), 1, 'access_token',		'accessTokenService'),
		(NULL, 14, NOW(), 1, 'role',				'roleService'),
		(NULL, 15, NOW(), 1, 'user',				'userService');
