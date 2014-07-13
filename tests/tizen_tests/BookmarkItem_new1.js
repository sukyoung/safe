/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
 var bmitem = new tizen.BookmarkItem("tizen", "https://www.tizen.org");

 var __result1 = bmitem.title;
 var __expect1 = "tizen"
 var __result2 = bmitem.url;
 var __expect2 = "https://www.tizen.org"
 var __result3 = bmitem.parent;
 var __expect3 = undefined