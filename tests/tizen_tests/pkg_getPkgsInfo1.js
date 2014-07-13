/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
function onListInstalledPackages(packages) {
  __result1 = packages[0].name;
}

tizen.package.getPackagesInfo(
                onListInstalledPackages,
                function (err) {__result2 = err.name;});

var __expect1 = "a"
var __expect2 = "NotFoundError"