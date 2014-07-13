/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.bug_detector

import kr.ac.kaist.jsaf.analysis.typing.CallContext

class BugOption(defaultForUser: Boolean = true) {
  ////////////////////////////////////////////////////////////////////////////////
  // Settings
  ////////////////////////////////////////////////////////////////////////////////
  val contextSensitive: Array[CallContext.SensitivityFlagType] = new Array(BugKindCounter + 1)

  var ArrayConstructor_Check                                    = true
  var ArrayConstructor_ArgumentMustBeUIntInEveryState           = false
  var ArrayConstructor_ArgumentMustBeUIntInEveryLocation        = false
  var ArrayConstructor_ArgumentMustBeUIntDefinitely             = false

  var AbsentReadProperty_Check                                  = true
  var AbsentReadProperty_PropertyMustExistInEveryState          = false
  var AbsentReadProperty_PropertyMustExistInEveryLocation       = false
  var AbsentReadProperty_PropertyMustExistForAllValue           = false
  var AbsentReadProperty_PropertyMustExistDefinitely            = false
  var AbsentReadProperty_CheckAbstractIndexValue                = false

  var AbsentReadVariable_Check                                  = true
  var AbsentReadVariable_VariableMustExistInEveryState          = false
  var AbsentReadVariable_VariableMustExistDefinitely            = false

  var BinaryOpSecondType_Check                                  = true
  var BinaryOpSecondType_OperandMustBeCorrectInEveryState       = false
  var BinaryOpSecondType_OperandMustBeCorrectInEveryLocation    = false
  var BinaryOpSecondType_OperandMustBeCorrectForAllValue        = false

  var BuiltinThrow_Check                                        = true
  var BuiltinThrow_MustBeThrownInEveryState                     = false
  var BuiltinThrow_MustBeThrownDefinitely                       = false

  var BuiltinRange_Check                                        = true
  var BuiltinRange_ArgumentMustBeCorrectInEveryState            = false
  var BuiltinRange_ArgumentMustBeCorrectInEveryLocation         = false
  var BuiltinRange_ArgumentMustBeCorrectDefinitely              = false

  var BuiltinWrongArgType_Check                                 = true
  var BuiltinWrongArgType_TypeMustBeCorrectInEveryState         = false
  var BuiltinWrongArgType_TypeMustBeCorrectForAllValue          = false
  var BuiltinWrongArgType_CheckObjectType                       = true
  var BuiltinWrongArgType_CheckFunctionType                     = true

  var CallConstFunc_Check                                       = true

  var CallNonConstructor_Check                                  = true
  var CallNonConstructor_MustBeConstructableInEveryState        = false
  var CallNonConstructor_MustBeConstructableForEveryLocation    = false
  var CallNonConstructor_MustBeConstructableDefinitely          = false

  var CallNonFunction_Check                                     = true
  var CallNonFunction_MustBeCallableInEveryState                = false
  var CallNonFunction_MustBeCallableForEveryLocation            = false
  var CallNonFunction_MustBeCallableDefinitely                  = false

  var CondBranch_Check                                          = true
  var CondBranch_ConditionMustBeTrueOrFalseInEveryState         = false
  var CondBranch_ConditionMustBeTrueOrFalseForAllValue          = false
  var CondBranch_CheckIf                                        = true
  var CondBranch_CheckLoop                                      = false
  var CondBranch_CheckTernary                                   = true

  var ConvertUndefToNum_Check                                   = true
  var ConvertUndefToNum_UndefMustBeConvertedInEveryState        = false
  var ConvertUndefToNum_VariableMustHaveUndefinedOnly           = true
  var ConvertUndefToNum_ToNumberMustBeCalledForExactValue       = true

  var DefaultValue_Check                                        = true
  var DefaultValue_MustBeCallableInEveryState                   = false
  var DefaultValue_MustBeCallableForEveryLocation               = false
  var DefaultValue_MustBeCallableDefinitely                     = false

  var FunctionArgSize_Check                                     = true
  var FunctionArgSize_CheckNativeFunction                       = true
  var FunctionArgSize_CheckUserFunction                         = true
  var FunctionArgSize_CheckTooFew                               = true
  var FunctionArgSize_CheckTooMany                              = true

  var GlobalThis_Check                                          = true
  var GlobalThis_MustReferInEveryState                          = false
  var GlobalThis_MustReferExactly                               = true

  var ImplicitTypeConvert_Check                                 = true
  var ImplicitTypeConvert_MustBeConvertedInEveryState           = false
  var ImplicitTypeConvert_MustBeConvertedForAllValue            = true
  var ImplicitTypeConvert_CheckNullAndUndefined                 = true
  var ImplicitTypeConvert_CheckStringAndNumber                  = false
  var ImplicitTypeConvert_CheckBooleanAndUndefined              = true
  var ImplicitTypeConvert_CheckBooleanAndNull                   = true
  var ImplicitTypeConvert_CheckBooleanAndNumber                 = true
  var ImplicitTypeConvert_CheckBooleanAndString                 = true
  var ImplicitTypeConvert_CheckObjectAndNumber                  = false
  var ImplicitTypeConvert_CheckObjectAndString                  = false
  var ImplicitTypeConvert_CheckObjectAndBoolean                 = false

  var NullOrUndefined_Check                                     = true
  var NullOrUndefined_BugMustExistInEveryState                  = false
  var NullOrUndefined_OnlyWhenPrimitive                         = true
  var NullOrUndefined_OnlyNullOrUndefined                       = true

  var PrimitiveToObject_Check                                   = true
  var PrimitiveToObject_PrimitiveMustBeConvertedInEveryState    = false
  var PrimitiveToObject_PrimitiveMustBeConvertedForAllValue     = false
  var PrimitiveToObject_CheckEvenThoughPrimitiveIsString        = false

  var Shadowing_Check                                           = true

  var StrictMode_Check                                          = true

  var UnreachableCode_Check                                     = false

  var UnreferencedFunction_Check                                = false

  var UncalledFunction_Check                                    = false

  var UnusedVarProp_Check                                       = false

  var VaryingTypeArguments_Check                                = false
  var VaryingTypeArguments_CheckUndefined                       = false

  var WrongThisType_Check                                       = true
  var WrongThisType_TypeMustBeWrongInEveryState                 = false
  var WrongThisType_TypeMustBeWrongInEveryFunctionId            = false
  var WrongThisType_TypeMustBeWrongInEveryFunctionLocation      = false
  var WrongThisType_TypeMustBeWrongInEveryThisLocation          = false

  ////////////////////////////////////////////////////////////////////////////////
  // Constructor
  ////////////////////////////////////////////////////////////////////////////////
  {
    if(defaultForUser) setToDefaultForUser()
    else setToDefaultForDeveloper()
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Default settings
  ////////////////////////////////////////////////////////////////////////////////
  // For user
  def setToDefaultForUser(): Unit = {
    for(i <- 0 until BugKindCounter) {
      //soundnessLevel(i) = SOUNDNESS_LEVEL_LOW
      contextSensitive(i) = CallContext._MOST_SENSITIVE
    }

    // ArrayConstructor
    ArrayConstructor_ArgumentMustBeUIntInEveryState = true
    ArrayConstructor_ArgumentMustBeUIntInEveryLocation = false
    ArrayConstructor_ArgumentMustBeUIntDefinitely = false

    // AbsentReadProperty
    AbsentReadProperty_PropertyMustExistInEveryState = false
    AbsentReadProperty_PropertyMustExistInEveryLocation = false
    AbsentReadProperty_PropertyMustExistForAllValue = false
    AbsentReadProperty_PropertyMustExistDefinitely = false
    AbsentReadProperty_CheckAbstractIndexValue = false

    // AbsentReadVariable
    AbsentReadVariable_VariableMustExistInEveryState = true
    AbsentReadVariable_VariableMustExistDefinitely = false

    // BinaryOpSecondType
    BinaryOpSecondType_OperandMustBeCorrectInEveryState = true
    BinaryOpSecondType_OperandMustBeCorrectInEveryLocation = false
    BinaryOpSecondType_OperandMustBeCorrectForAllValue = false

    // BuiltinCallable
    BuiltinThrow_MustBeThrownInEveryState = true
    BuiltinThrow_MustBeThrownDefinitely = false

    // BuiltinRange
    BuiltinRange_Check = true
    BuiltinRange_ArgumentMustBeCorrectInEveryState = true
    BuiltinRange_ArgumentMustBeCorrectInEveryLocation = false
    BuiltinRange_ArgumentMustBeCorrectDefinitely = false

    // BuiltinWrongArgType
    BuiltinWrongArgType_TypeMustBeCorrectInEveryState = false
    BuiltinWrongArgType_TypeMustBeCorrectForAllValue = false
    BuiltinWrongArgType_CheckObjectType = true
    BuiltinWrongArgType_CheckFunctionType = true

    // CallNonConstructor
    CallNonConstructor_MustBeConstructableInEveryState = true
    CallNonConstructor_MustBeConstructableForEveryLocation = false
    CallNonConstructor_MustBeConstructableDefinitely = false

    // CallNonFunction
    CallNonFunction_MustBeCallableInEveryState = true
    CallNonFunction_MustBeCallableForEveryLocation = false
    CallNonFunction_MustBeCallableDefinitely = false

    // CondBranch
    CondBranch_ConditionMustBeTrueOrFalseInEveryState = false
    CondBranch_ConditionMustBeTrueOrFalseForAllValue = false
    CondBranch_CheckIf = true
    CondBranch_CheckLoop = false
    CondBranch_CheckTernary = true

    // ConvertUndefToNum
    ConvertUndefToNum_UndefMustBeConvertedInEveryState = false
    ConvertUndefToNum_VariableMustHaveUndefinedOnly = true
    ConvertUndefToNum_ToNumberMustBeCalledForExactValue = true

    // DefaultValue
    DefaultValue_MustBeCallableInEveryState = false
    DefaultValue_MustBeCallableForEveryLocation = false
    DefaultValue_MustBeCallableDefinitely = false

    // FunctionArgSize
    FunctionArgSize_CheckNativeFunction = true
    FunctionArgSize_CheckUserFunction = true

    // GlobalThis
    GlobalThis_MustReferInEveryState = false
    GlobalThis_MustReferExactly = true

    // ImplicitTypeConvert
    ImplicitTypeConvert_MustBeConvertedInEveryState = false
    ImplicitTypeConvert_MustBeConvertedForAllValue = true
    ImplicitTypeConvert_CheckNullAndUndefined = true
    ImplicitTypeConvert_CheckStringAndNumber = false
    ImplicitTypeConvert_CheckBooleanAndUndefined = true
    ImplicitTypeConvert_CheckBooleanAndNull = true
    ImplicitTypeConvert_CheckBooleanAndNumber = true
    ImplicitTypeConvert_CheckBooleanAndString = true
    ImplicitTypeConvert_CheckObjectAndNumber = false
    ImplicitTypeConvert_CheckObjectAndString = false
    ImplicitTypeConvert_CheckObjectAndBoolean = false

    // NullOrUndefined
    NullOrUndefined_BugMustExistInEveryState = true
    NullOrUndefined_OnlyWhenPrimitive = true
    NullOrUndefined_OnlyNullOrUndefined = true

    // PrimitiveToObject
    PrimitiveToObject_PrimitiveMustBeConvertedInEveryState = false
    PrimitiveToObject_PrimitiveMustBeConvertedForAllValue = false
    PrimitiveToObject_CheckEvenThoughPrimitiveIsString = false

    // VaryingTypeArguments
    VaryingTypeArguments_CheckUndefined = false

    // WrongThisType
    WrongThisType_TypeMustBeWrongInEveryState = true
    WrongThisType_TypeMustBeWrongInEveryFunctionId = false
    WrongThisType_TypeMustBeWrongInEveryFunctionLocation = false
    WrongThisType_TypeMustBeWrongInEveryThisLocation = false
  }

  // For SAFE developer
  def setToDefaultForDeveloper(): Unit = {
    for(i <- 0 until BugKindCounter) {
      //soundnessLevel(i) = SOUNDNESS_LEVEL_HIGH
      contextSensitive(i) = CallContext._MOST_SENSITIVE
    }

    // ArrayConstructor
    ArrayConstructor_ArgumentMustBeUIntInEveryState = true
    ArrayConstructor_ArgumentMustBeUIntInEveryLocation = true
    ArrayConstructor_ArgumentMustBeUIntDefinitely = true

    // AbsentReadProperty
    AbsentReadProperty_PropertyMustExistInEveryState = true
    AbsentReadProperty_PropertyMustExistInEveryLocation = true
    AbsentReadProperty_PropertyMustExistForAllValue = true
    AbsentReadProperty_PropertyMustExistDefinitely = true
    AbsentReadProperty_CheckAbstractIndexValue = true

    // AbsentReadVariable
    AbsentReadVariable_VariableMustExistInEveryState = true
    AbsentReadVariable_VariableMustExistDefinitely = true

    // BinaryOpSecondType
    BinaryOpSecondType_OperandMustBeCorrectInEveryState = true
    BinaryOpSecondType_OperandMustBeCorrectInEveryLocation = true
    BinaryOpSecondType_OperandMustBeCorrectForAllValue = true

    // BuiltinCallable
    BuiltinThrow_MustBeThrownInEveryState = true
    BuiltinThrow_MustBeThrownDefinitely = true

    // BuiltinRange
    BuiltinRange_Check = true
    BuiltinRange_ArgumentMustBeCorrectInEveryState = true
    BuiltinRange_ArgumentMustBeCorrectInEveryLocation = true
    BuiltinRange_ArgumentMustBeCorrectDefinitely = true

    // BuiltinWrongArgType
    BuiltinWrongArgType_TypeMustBeCorrectInEveryState = true
    BuiltinWrongArgType_TypeMustBeCorrectForAllValue = true
    BuiltinWrongArgType_CheckObjectType = true
    BuiltinWrongArgType_CheckFunctionType = true

    // CallNonConstructor
    CallNonConstructor_MustBeConstructableInEveryState = true
    CallNonConstructor_MustBeConstructableForEveryLocation = true
    CallNonConstructor_MustBeConstructableDefinitely = true

    // CallNonFunction
    CallNonFunction_MustBeCallableInEveryState = true
    CallNonFunction_MustBeCallableForEveryLocation = true
    CallNonFunction_MustBeCallableDefinitely = true

    // CondBranch
    CondBranch_ConditionMustBeTrueOrFalseInEveryState = true
    CondBranch_ConditionMustBeTrueOrFalseForAllValue = true
    CondBranch_CheckIf = true
    CondBranch_CheckLoop = false
    CondBranch_CheckTernary = true

    // ConvertUndefToNum
    ConvertUndefToNum_UndefMustBeConvertedInEveryState = true
    ConvertUndefToNum_VariableMustHaveUndefinedOnly = false
    ConvertUndefToNum_ToNumberMustBeCalledForExactValue = false

    // DefaultValue
    DefaultValue_MustBeCallableInEveryState = true
    DefaultValue_MustBeCallableForEveryLocation = true
    DefaultValue_MustBeCallableDefinitely = true

    // FunctionArgSize
    FunctionArgSize_CheckNativeFunction = true
    FunctionArgSize_CheckUserFunction = true

    // GlobalThis
    GlobalThis_MustReferInEveryState = true
    GlobalThis_MustReferExactly = false

    // ImplicitTypeConvert
    ImplicitTypeConvert_MustBeConvertedInEveryState = true
    ImplicitTypeConvert_MustBeConvertedForAllValue = false
    ImplicitTypeConvert_CheckNullAndUndefined = true
    ImplicitTypeConvert_CheckStringAndNumber = true
    ImplicitTypeConvert_CheckBooleanAndUndefined = true
    ImplicitTypeConvert_CheckBooleanAndNull = true
    ImplicitTypeConvert_CheckBooleanAndNumber = true
    ImplicitTypeConvert_CheckBooleanAndString = true
    ImplicitTypeConvert_CheckObjectAndNumber = true
    ImplicitTypeConvert_CheckObjectAndString = true
    ImplicitTypeConvert_CheckObjectAndBoolean = true

    // NullOrUndefined
    NullOrUndefined_BugMustExistInEveryState = true
    NullOrUndefined_OnlyWhenPrimitive = false
    NullOrUndefined_OnlyNullOrUndefined = false

    // PrimitiveToObject
    PrimitiveToObject_PrimitiveMustBeConvertedInEveryState = true
    PrimitiveToObject_PrimitiveMustBeConvertedForAllValue = true
    PrimitiveToObject_CheckEvenThoughPrimitiveIsString = true

    // VaryingTypeArguments
    VaryingTypeArguments_CheckUndefined = true

    // WrongThisType
    WrongThisType_TypeMustBeWrongInEveryState = true
    WrongThisType_TypeMustBeWrongInEveryFunctionId = true
    WrongThisType_TypeMustBeWrongInEveryFunctionLocation = true
    WrongThisType_TypeMustBeWrongInEveryThisLocation = true
  }
}
