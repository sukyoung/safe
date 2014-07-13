/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;
// The following example retrieves all songs from the album "The Joshua Tree", by artist "U2", ordered by the track number.
 var count = 100;
 var offset = 0;
 var sortMode = new tizen.SortMode("trackNumber", "ASC");
 var artistFilter = new tizen.AttributeFilter("artists", "EXACTLY", "U2");
 var albumFilter = new tizen.AttributeFilter("album", "EXACTLY", "The Joshua Tree");
 var filter = new tizen.CompositeFilter("INTERSECTION", [albumFilter, artistFilter]);
 tizen.content.find(findCB, errorCB, null, filter, sortMode, count, offset);

 function errorCB(err) {
    __result3 = err.name;
 }

 function findCB(contents) {
    __result1 = contents[0].album;
    __result2 = contents[0].artists[0];
 }



var __expect1 = "The Joshua Tree";
var __expect2 = "U2";
var __expect3 = "UnknownError";
