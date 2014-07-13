/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var reqAppControl = tizen.application.getCurrentApplication().getRequestedAppControl();
var __result1 = reqAppControl.replyFailure();
var __expect1 = undefined;