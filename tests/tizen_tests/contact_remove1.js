/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1;

function personsFoundCB(persons) {
// The person has been successfully found
    if(persons.length > 0) {
          __result1 = tizen.contact.remove(persons[0].id);
    }
}

function errorCB(e){

}
tizen.contact.find(personsFoundCB, errorCB);

var __expect1 = undefined;
