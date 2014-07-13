/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var contWS1 = new tizen.ContactWebSite();
var contWS2 = new tizen.ContactWebSite('http://www.domain.com', 'BLOG');

var __result1 = contWS1.url;
var __expect1 = "undefined"
var __result2 = contWS1.type;
var __expect2 = "HOMEPAGE"

var __result4 = contWS2.url;
var __expect4 = "http://www.domain.com"
var __result5 = contWS2.type;
var __expect5 = "BLOG"
