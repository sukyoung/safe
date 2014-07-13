/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result2, __result3, __result4;
var adapter = tizen.bluetooth.getDefaultAdapter();
var __result1 = adapter.name;

adapter.setPowered(true,
  function(){
    __result2 = adapter.powered;
  },
  function(e){
    __result3 = e.name;
    __result4 = adapter.powered;
  });

 var __expect1 = "bluetooth"
 var __expect2 = true;
 var __expect3 = "ServiceNotAvailableError";
 var __expect4 = false;