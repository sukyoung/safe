/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

try {
	if (true)
		throw 11;
}
catch (e) {
	throw 22;
}
throw 33;