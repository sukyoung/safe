/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var detailInfo1 = new tizen.NotificationDetailInfo('Missed Call from James', 'Feb 11 2013');

var __result1 = detailInfo1.mainText;
var __expect1 = "Missed Call from James";
var __result2 = detailInfo1.subText;
var __expect2 = "Feb 11 2013";