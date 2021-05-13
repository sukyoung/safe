QUnit.module('iteration methods');
(function () {
    var methods = [
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__
    ];
    var arrayMethods = [
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__
    ];
    var collectionMethods = [
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__
    ];
    var forInMethods = [
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__
    ];
    var iterationMethods = [
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__
    ];
    var objectMethods = [
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__
    ];
    var rightMethods = [
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__
    ];
    var unwrappedMethods = [
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__,
        __str_top__
    ];
    lodashStable.each(methods, function (methodName) {
        var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ], func = _[methodName], isBy = /(^partition|By)$/.test(methodName), isFind = /^find/.test(methodName), isOmitPick = /^(?:omit|pick)By$/.test(methodName), isSome = methodName == __str_top__;
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            if (func) {
                var args, expected = [
                        __num_top__,
                        __num_top__,
                        array
                    ];
                func(array, function () {
                    args || (args = slice.call(arguments));
                });
                if (lodashStable.includes(rightMethods, methodName)) {
                    expected[__num_top__] = __num_top__;
                    expected[__num_top__] = __num_top__;
                }
                if (lodashStable.includes(objectMethods, methodName)) {
                    expected[__num_top__] += __str_top__;
                }
                if (isBy) {
                    expected.length = isOmitPick ? __num_top__ : __num_top__;
                }
                assert.deepEqual(args, expected);
            } else {
                skipAssert(assert);
            }
        });
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            if (func) {
                var array = [__num_top__];
                array[__num_top__] = __num_top__;
                var expected = lodashStable.includes(objectMethods, methodName) ? [
                    [
                        __num_top__,
                        __str_top__,
                        array
                    ],
                    [
                        undefined,
                        __str_top__,
                        array
                    ],
                    [
                        __num_top__,
                        __str_top__,
                        array
                    ]
                ] : [
                    [
                        __num_top__,
                        __num_top__,
                        array
                    ],
                    [
                        undefined,
                        __num_top__,
                        array
                    ],
                    [
                        __num_top__,
                        __num_top__,
                        array
                    ]
                ];
                if (isBy) {
                    expected = lodashStable.map(expected, function (args) {
                        return args.slice(__num_top__, isOmitPick ? __num_top__ : __num_top__);
                    });
                } else if (lodashStable.includes(objectMethods, methodName)) {
                    expected = lodashStable.map(expected, function (args) {
                        args[__num_top__] += __str_top__;
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
                __num_top__,
                __num_top__,
                __num_top__
            ], func = _[methodName], isEvery = methodName == __str_top__;
        array.a = __num_top__;
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            if (func) {
                var keys = [];
                func(array, function (value, key) {
                    keys.push(key);
                    return isEvery;
                });
                assert.notOk(lodashStable.includes(keys, __str_top__));
            } else {
                skipAssert(assert);
            }
        });
    });
    lodashStable.each(lodashStable.difference(methods, unwrappedMethods), function (methodName) {
        var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ], isBaseEach = methodName == __str_top__;
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
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
            __num_top__,
            __num_top__,
            __num_top__
        ];
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            if (!isNpm) {
                var actual = _(array)[methodName](noop);
                assert.notOk(actual instanceof _);
            } else {
                skipAssert(assert);
            }
        });
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
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
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            function Foo() {
                this.a = __num_top__;
            }
            Foo.prototype.b = __num_top__;
            if (func) {
                var values = [];
                func(new Foo(), function (value) {
                    values.push(value);
                });
                assert.deepEqual(values, [__num_top__]);
            } else {
                skipAssert(assert);
            }
        });
    });
    lodashStable.each(iterationMethods, function (methodName) {
        var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ], func = _[methodName];
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
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
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(3);
            if (func) {
                var isIteratedAsObject = function (object) {
                    var result = __bool_top__;
                    func(object, function () {
                        result = __bool_top__;
                    }, __num_top__);
                    return result;
                };
                var values = [
                        -__num_top__,
                        __str_top__,
                        __num_top__,
                        Object(__num_top__),
                        MAX_SAFE_INTEGER + __num_top__
                    ], expected = lodashStable.map(values, stubTrue);
                var actual = lodashStable.map(values, function (length) {
                    return isIteratedAsObject({ 'length': length });
                });
                var Foo = function (a) {
                };
                Foo.a = __num_top__;
                assert.deepEqual(actual, expected);
                assert.ok(isIteratedAsObject(Foo));
                assert.notOk(isIteratedAsObject({ 'length': __num_top__ }));
            } else {
                skipAssert(assert, 3);
            }
        });
    });
    lodashStable.each(methods, function (methodName) {
        var func = _[methodName], isFind = /^find/.test(methodName), isSome = methodName == __str_top__, isReduce = /^reduce/.test(methodName);
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            if (func) {
                var count = __num_top__, array = [__num_top__];
                func(array, function () {
                    if (++count == __num_top__) {
                        array.push(__num_top__);
                    }
                    return !(isFind || isSome);
                }, isReduce ? array : null);
                assert.strictEqual(count, __num_top__);
            } else {
                skipAssert(assert);
            }
        });
    });
    lodashStable.each(lodashStable.difference(lodashStable.union(methods, collectionMethods), arrayMethods), function (methodName) {
        var func = _[methodName], isFind = /^find/.test(methodName), isSome = methodName == __str_top__, isReduce = /^reduce/.test(methodName);
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            if (func) {
                var count = __num_top__, object = { 'a': __num_top__ };
                func(object, function () {
                    if (++count == __num_top__) {
                        object.b = __num_top__;
                    }
                    return !(isFind || isSome);
                }, isReduce ? object : null);
                assert.strictEqual(count, __num_top__);
            } else {
                skipAssert(assert);
            }
        });
    });
}());