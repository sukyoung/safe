/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

// 10.2 Lexical Environments
class LexEnv(
    val envRecord: EnvRecord, // Environment Record
    val outer: (AbsLoc, AbsNull) // Lexical Environment or null
) {
}
