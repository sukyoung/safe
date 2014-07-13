/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1;
// Define the error callback for all the asynchronous calls
function errorCB(err) {

}

function personsFoundCB(persons) {
// The persons has been successfully found
    __result1 = persons[0].link(persons[1].id);
}

tizen.contact.find(personsFoundCB, errorCB);

var __expect1 = undefined;