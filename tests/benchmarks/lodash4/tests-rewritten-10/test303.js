QUnit.module('lodash(...) methods that return unwrapped values');
(function () {
    var funcs = [
        'add',
        'camelCase',
        'capitalize',
        'ceil',
        __str_top__,
        'deburr',
        'defaultTo',
        'divide',
        'endsWith',
        __str_top__,
        'escapeRegExp',
        'every',
        'find',
        'floor',
        'has',
        'hasIn',
        'head',
        __str_top__,
        'isArguments',
        'isArray',
        'isArrayBuffer',
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
        __str_top__,
        'isMap',
        'isNaN',
        __str_top__,
        'isNil',
        __str_top__,
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
        __str_top__,
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
        'shift',
        'size',
        'snakeCase',
        'some',
        'startCase',
        'startsWith',
        'subtract',
        'sum',
        'toFinite',
        'toInteger',
        'toLower',
        'toNumber',
        'toSafeInteger',
        __str_top__,
        'toUpper',
        'trim',
        'trimEnd',
        'trimStart',
        'truncate',
        'unescape',
        __str_top__,
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