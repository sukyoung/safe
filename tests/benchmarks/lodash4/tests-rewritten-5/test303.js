QUnit.module('lodash(...) methods that return unwrapped values');
(function () {
    var funcs = [
        'add',
        'camelCase',
        'capitalize',
        'ceil',
        'clone',
        'deburr',
        'defaultTo',
        'divide',
        'endsWith',
        'escape',
        'escapeRegExp',
        'every',
        'find',
        'floor',
        'has',
        'hasIn',
        __str_top__,
        'includes',
        'isArguments',
        'isArray',
        __str_top__,
        'isArrayLike',
        'isBoolean',
        'isBuffer',
        'isDate',
        'isElement',
        'isEmpty',
        'isEqual',
        'isError',
        'isFinite',
        'isFunction',
        'isInteger',
        'isMap',
        'isNaN',
        'isNative',
        'isNil',
        'isNull',
        'isNumber',
        'isObject',
        'isObjectLike',
        'isPlainObject',
        'isRegExp',
        'isSafeInteger',
        'isSet',
        'isString',
        'isUndefined',
        'isWeakMap',
        'isWeakSet',
        'join',
        'kebabCase',
        'last',
        'lowerCase',
        'lowerFirst',
        'max',
        'maxBy',
        'min',
        'minBy',
        'multiply',
        'nth',
        'pad',
        'padEnd',
        'padStart',
        __str_top__,
        'pop',
        'random',
        'reduce',
        'reduceRight',
        'repeat',
        'replace',
        'round',
        'sample',
        __str_top__,
        'size',
        'snakeCase',
        'some',
        'startCase',
        'startsWith',
        __str_top__,
        'sum',
        'toFinite',
        'toInteger',
        'toLower',
        'toNumber',
        'toSafeInteger',
        'toString',
        'toUpper',
        'trim',
        'trimEnd',
        'trimStart',
        'truncate',
        'unescape',
        'upperCase',
        'upperFirst'
    ];
    lodashStable.each(funcs, function (methodName) {
        QUnit.test('`_(...).' + methodName + '` should return an unwrapped value when implicitly chaining', function (assert) {
            assert.expect(1);
            if (!isNpm) {
                var actual = _()[methodName]();
                assert.notOk(actual instanceof _);
            } else {
                skipAssert(assert);
            }
        });
        QUnit.test('`_(...).' + methodName + '` should return a wrapped value when explicitly chaining', function (assert) {
            assert.expect(1);
            if (!isNpm) {
                var actual = _().chain()[methodName]();
                assert.ok(actual instanceof _);
            } else {
                skipAssert(assert);
            }
        });
    });
}());