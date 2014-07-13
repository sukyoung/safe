/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
// Define the error callback.
function errorCB(err) {
   __result2 = err.name;
}

// Define the person search success callback.
function personsFoundCB(persons) {
   __result1 = persons[0].displayName;
}

// Finds all the persons in the contact DB that have the word Ramone in their display name
var filter = new tizen.AttributeFilter('displayName', 'CONTAINS', 'Ramone');

// The persons returned by the find() query will be sorted in the ascending order of their display name.
var sortingMode =  new tizen.SortMode('displayName', 'ASC');
tizen.contact.find(personsFoundCB, errorCB, filter, sortingMode);


var __expect1 = "Ramone Salmon";
var __expect2 = "UnknownError";
