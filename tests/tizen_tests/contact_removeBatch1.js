/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;

function errorCB(err) {
  __result2 = err.name;
}

function personsRemovedCB() {
  __result1 = 1;
}

function personsFoundCB(persons) {
// The person has been successfully found
    if(persons.length > 2) {
          tizen.contact.removeBatch([persons[0].id, persons[1].id], personsRemovedCB, errorCB);
    }
}

tizen.contact.find(personsFoundCB, errorCB);

var __expect1 = 1;
var __expect2 = "NotFoundError";