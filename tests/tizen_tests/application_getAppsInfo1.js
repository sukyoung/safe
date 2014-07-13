/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

function onListInstalledApps(applications) {
  __result1 = applications[0].id;
 }

 tizen.application.getAppsInfo(onListInstalledApps);

var __expect1 = "a";