/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
try {
	"p" in 1;
}
catch (e) {
	var __result1 = e.name;
	var __expect1 = "TypeError"
}