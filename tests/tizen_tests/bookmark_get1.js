/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
 var bmitem = new tizen.BookmarkItem("tizen", "https://www.tizen.org");
 tizen.bookmark.add(bmitem);
 var allBookmarks = tizen.bookmark.get(null);

 var __result1 = allBookmarks[0].parent;
 var __expect1 = null
 var __result2 = allBookmarks[0].title;
 var __expect2 = "tizen"
 var __result3 = allBookmarks[0].url;
 var __expect3 = "https://www.tizen.org"