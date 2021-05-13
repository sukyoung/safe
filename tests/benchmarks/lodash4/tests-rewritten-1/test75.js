QUnit.module('iteration methods');
(function () {
    var methods = [
        '_baseEach',
        'countBy',
        'every',
        'filter',
        'find',
        'findIndex',
        'findKey',
        'findLast',
        'findLastIndex',
        'findLastKey',
        'forEach',
        'forEachRight',
        'forIn',
        'forInRight',
        'forOwn',
        'forOwnRight',
        'groupBy',
        'keyBy',
        'map',
        'mapKeys',
        'mapValues',
        'maxBy',
        'minBy',
        'omitBy',
        'partition',
        'pickBy',
        'reject',
        'some'
    ];
    var arrayMethods = [
        'findIndex',
        'findLastIndex',
        'maxBy',
        'minBy'
    ];
    var collectionMethods = [
        '_baseEach',
        'countBy',
        'every',
        'filter',
        'find',
        'findLast',
        'forEach',
        'forEachRight',
        'groupBy',
        'keyBy',
        'map',
        'partition',
        'reduce',
        'reduceRight',
        'reject',
        'some'
    ];
    var forInMethods = [
        'forIn',
        'forInRight',
        'omitBy',
        'pickBy'
    ];
    var iterationMethods = [
        '_baseEach',
        'forEach',
        'forEachRight',
        'forIn',
        'forInRight',
        'forOwn',
        'forOwnRight'
    ];
    var objectMethods = [
        'findKey',
        'findLastKey',
        'forIn',
        'forInRight',
        'forOwn',
        'forOwnRight',
        'mapKeys',
        'mapValues',
        'omitBy',
        'pickBy'
    ];
    var rightMethods = [
        'findLast',
        'findLastIndex',
        'findLastKey',
        'forEachRight',
        'forInRight',
        'forOwnRight'
    ];
    var unwrappedMethods = [
        'each',
        'eachRight',
        'every',
        'find',
        'findIndex',
        'findKey',
        'findLast',
        'findLastIndex',
        'findLastKey',
        'forEach',
        'forEachRight',
        'forIn',
        'forInRight',
        'forOwn',
        'forOwnRight',
        'max',
        'maxBy',
        'min',
        'minBy',
        'some'
    ];
    lodashStable.each(methods, function (methodName) {
        var array = [
                1,
                2,
                3
            ], func = _[methodName], isBy = /(^partition|By)$/.test(methodName), isFind = /^find/.test(methodName), isOmitPick = /^(?:omit|pick)By$/.test(methodName), isSome = methodName == 'some';
        QUnit.test('`_.' + methodName + '` should provide correct iteratee arguments', function (assert) {
            assert.expect(1);
            if (func) {
                var args, expected = [
                        1,
                        0,
                        array
                    ];
                func(array, function () {
                    args || (args = slice.call(arguments));
                });
                if (lodashStable.includes(rightMethods, methodName)) {
                    expected[0] = 3;
                    expected[1] = 2;
                }
                if (lodashStable.includes(objectMethods, methodName)) {
                    expected[1] += '';
                }
                if (isBy) {
                    expected.length = isOmitPick ? 2 : 1;
                }
                assert.deepEqual(args, expected);
            } else {
                skipAssert(assert);
            }
        });
        QUnit.test('`_.' + methodName + '` should treat sparse arrays as dense', function (assert) {
            assert.expect(1);
            if (func) {
                var array = [1];
                array[2] = 3;
                var expected = lodashStable.includes(objectMethods, methodName) ? [
                    [
                        1,
                        '0',
                        array
                    ],
                    [
                        undefined,
                        '1',
                        array
                    ],
                    [
                        3,
                        '2',
                        array
                    ]
                ] : [
                    [
                        1,
                        0,
                        array
                    ],
                    [
                        undefined,
                        1,
                        array
                    ],
                    [
                        3,
                        2,
                        array
                    ]
                ];
                if (isBy) {
                    expected = lodashStable.map(expected, function (args) {
                        return args.slice(0, isOmitPick ? 2 : 1);
                    });
                } else if (lodashStable.includes(objectMethods, methodName)) {
                    expected = lodashStable.map(expected, function (args) {
                        args[1] += '';
                        return args;
                    });
                }
                if (lodashStable.includes(rightMethods, methodName)) {
                    expected.reverse();
                }
                var argsList = [];
                func(array, function () {
                    argsList.push(slice.call(arguments));
                    return !(isFind || isSome);
                });
                assert.deepEqual(argsList, expected);
            } else {
                skipAssert(assert);
            }
        });
    });
    lodashStable.each(lodashStable.difference(methods, objectMethods), function (methodName) {
        var array = [
                1,
                2,
                3
            ], func = _[methodName], isEvery = methodName == 'every';
        array.a = 1;
        QUnit.test('`_.' + methodName + '` should not iterate custom properties on arrays', function (assert) {
            assert.expect(1);
            if (func) {
                var keys = [];
                func(array, function (value, key) {
                    keys.push(key);
                    return isEvery;
                });
                assert.notOk(lodashStable.includes(keys, 'a'));
            } else {
                skipAssert(assert);
            }
        });
    });
    lodashStable.each(lodashStable.difference(methods, unwrappedMethods), function (methodName) {
        var array = [
                1,
                2,
                3
            ], isBaseEach = methodName == '_baseEach';
        QUnit.test(__str_top__ + methodName + '` should return a wrapped value when implicitly chaining', function (assert) {
            assert.expect(1);
            if (!(isBaseEach || isNpm)) {
                var wrapped = _(array)[methodName](noop);
                assert.ok(wrapped instanceof _);
            } else {
                skipAssert(assert);
            }
        });
    });
    lodashStable.each(unwrappedMethods, function (methodName) {
        var array = [
            1,
            2,
            3
        ];
        QUnit.test('`_.' + methodName + '` should return an unwrapped value when implicitly chaining', function (assert) {
            assert.expect(1);
            if (!isNpm) {
                var actual = _(array)[methodName](noop);
                assert.notOk(actual instanceof _);
            } else {
                skipAssert(assert);
            }
        });
        QUnit.test('`_.' + methodName + '` should return a wrapped value when explicitly chaining', function (assert) {
            assert.expect(2);
            if (!isNpm) {
                var wrapped = _(array).chain(), actual = wrapped[methodName](noop);
                assert.ok(actual instanceof _);
                assert.notStrictEqual(actual, wrapped);
            } else {
                skipAssert(assert, 2);
            }
        });
    });
    lodashStable.each(lodashStable.difference(methods, arrayMethods, forInMethods), function (methodName) {
        var func = _[methodName];
        QUnit.test('`_.' + methodName + '` iterates over own string keyed properties of objects', function (assert) {
            assert.expect(1);
            function Foo() {
                this.a = 1;
            }
            Foo.prototype.b = 2;
            if (func) {
                var values = [];
                func(new Foo(), function (value) {
                    values.push(value);
                });
                assert.deepEqual(values, [1]);
            } else {
                skipAssert(assert);
            }
        });
    });
    lodashStable.each(iterationMethods, function (methodName) {
        var array = [
                1,
                2,
                3
            ], func = _[methodName];
        QUnit.test('`_.' + methodName + '` should return the collection', function (assert) {
            assert.expect(1);
            if (func) {
                assert.strictEqual(func(array, Boolean), array);
            } else {
                skipAssert(assert);
            }
        });
    });
    lodashStable.each(collectionMethods, function (methodName) {
        var func = _[methodName];
        QUnit.test('`_.' + methodName + '` should use `isArrayLike` to determine whether a value is array-like', function (assert) {
            assert.expect(3);
            if (func) {
                var isIteratedAsObject = function (object) {
                    var result = false;
                    func(object, function () {
                        result = true;
                    }, 0);
                    return result;
                };
                var values = [
                        -1,
                        '1',
                        1.1,
                        Object(1),
                        MAX_SAFE_INTEGER + 1
                    ], expected = lodashStable.map(values, stubTrue);
                var actual = lodashStable.map(values, function (length) {
                    return isIteratedAsObject({ 'length': length });
                });
                var Foo = function (a) {
                };
                Foo.a = 1;
                assert.deepEqual(actual, expected);
                assert.ok(isIteratedAsObject(Foo));
                assert.notOk(isIteratedAsObject({ 'length': 0 }));
            } else {
                skipAssert(assert, 3);
            }
        });
    });
    lodashStable.each(methods, function (methodName) {
        var func = _[methodName], isFind = /^find/.test(methodName), isSome = methodName == 'some', isReduce = /^reduce/.test(methodName);
        QUnit.test('`_.' + methodName + '` should ignore changes to `length`', function (assert) {
            assert.expect(1);
            if (func) {
                var count = 0, array = [1];
                func(array, function () {
                    if (++count == 1) {
                        array.push(2);
                    }
                    return !(isFind || isSome);
                }, isReduce ? array : null);
                assert.strictEqual(count, 1);
            } else {
                skipAssert(assert);
            }
        });
    });
    lodashStable.each(lodashStable.difference(lodashStable.union(methods, collectionMethods), arrayMethods), function (methodName) {
        var func = _[methodName], isFind = /^find/.test(methodName), isSome = methodName == 'some', isReduce = /^reduce/.test(methodName);
        QUnit.test('`_.' + methodName + '` should ignore added `object` properties', function (assert) {
            assert.expect(1);
            if (func) {
                var count = 0, object = { 'a': 1 };
                func(object, function () {
                    if (++count == 1) {
                        object.b = 2;
                    }
                    return !(isFind || isSome);
                }, isReduce ? object : null);
                assert.strictEqual(count, 1);
            } else {
                skipAssert(assert);
            }
        });
    });
}());