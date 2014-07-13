/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var c1 = new tizen.Contact({name: new tizen.ContactName({firstName:'Jeffrey',
                         lastName:'Hyman',
                         nicknames:['joey ramone']}),
                         emails:[new tizen.ContactEmailAddress('user1@domain.com')],
                         phoneNumbers:[new tizen.ContactPhoneNumber('123456789')]});

var __result1 = c1.name.firstName;
var __result2 = c1.id;
var __result3 = c1.emails[0].email;
var __result4 = c1.phoneNumbers[0].number;
var __expect1 = "Jeffrey";
var __expect2 = null;
var __expect3 = "user1@domain.com";
var __expect4 = "123456789";
