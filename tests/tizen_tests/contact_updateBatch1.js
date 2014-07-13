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

function personsUpdatedCB() {
  __result1 = 1;
}

function personsFoundCB(persons) {
// The person has been successfully found
    for(var i = 0; i < persons.length; i++) {
      persons[i].isFavorite = true;
    }
    tizen.contact.updateBatch(persons, personsUpdatedCB, errorCB);
}

tizen.contact.find(personsFoundCB, errorCB);

var __expect1 = 1;
var __expect2 = "NotFoundError";