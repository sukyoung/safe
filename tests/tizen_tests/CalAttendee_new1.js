/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var attendee = new tizen.CalendarAttendee('mailto:bob@domain.com');
//var attendee = new tizen.CalendarAttendee('mailto:bob@domain.com', {role: "CHAIR", RSVP: true});

var __result1 = attendee.uri;
var __expect1 = 'mailto:bob@domain.com'
var __result2 = attendee.name;
var __expect2 = "";
var __result3 = attendee.role;
var __expect3 = "REQ_PARTICIPANT"
var __result4 = attendee.status;
var __expect4 = "PENDING"
var __result5 = attendee.RSVP;
var __expect5 = false
var __result6 = attendee.type;
var __expect6 = "INDIVIDUAL"
var __result7 = attendee.group;
var __expect7 = ""
var __result8 = attendee.delegatorURI;
var __expect8 = ""
var __result9 = attendee.delegateURI;
var __expect9 = ""
var __result10 = attendee.contactRef.addressBookId;
var __expect10 = ""
var __result11 = attendee.contactRef.contactId;
var __expect11 = ""


