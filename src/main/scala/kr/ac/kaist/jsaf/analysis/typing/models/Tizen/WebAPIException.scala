/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.Tizen


abstract class WebAPIException {
  override def toString(): String = {
    this match {
      case UnknownError => "UnknownErr"
      case TypeMismatchError => "TypeMismatchErr"
      case InvalidValuesError => "InvalidValuesErr"
      case IOError => "IOErr"
      case ServiceNotAvailableError => "ServiceNotAvailableErr"
      case NetworkError => "NetworkErr"
      case NotFoundError => "NotFoundErr"
      case AbortError => "AbortErr"
      case SecurityError => "SecurityErr"
      case NotSupportedError => "NotSupportedErr"
    }
  }
}

case object UnknownError extends WebAPIException
case object TypeMismatchError extends WebAPIException
case object InvalidValuesError extends WebAPIException
case object IOError extends WebAPIException
case object ServiceNotAvailableError extends WebAPIException
case object NetworkError extends WebAPIException
case object NotFoundError extends WebAPIException
case object AbortError extends WebAPIException
case object SecurityError extends WebAPIException
case object NotSupportedError extends WebAPIException