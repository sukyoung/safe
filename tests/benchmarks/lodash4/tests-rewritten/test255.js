QUnit.module('number coercion methods');
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName];
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var values = [
                __num_top__,
                __str_top__,
                -__num_top__,
                __str_top__
            ], expected = [
                [
                    __num_top__,
                    Infinity
                ],
                [
                    __num_top__,
                    Infinity
                ],
                [
                    -__num_top__,
                    -Infinity
                ],
                [
                    -__num_top__,
                    -Infinity
                ]
            ];
        lodashStable.times(__num_top__, function (index) {
            var others = lodashStable.map(values, index ? Object : identity);
            var actual = lodashStable.map(others, function (value) {
                var result = func(value);
                return [
                    result,
                    __num_top__ / result
                ];
            });
            assert.deepEqual(actual, expected);
        });
    });
});
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isToFinite = methodName == __str_top__, isToLength = methodName == __str_top__, isToNumber = methodName == __str_top__, isToSafeInteger = methodName == __str_top__;
    function negative(string) {
        return __str_top__ + string;
    }
    function pad(string) {
        return whitespace + string + whitespace;
    }
    function positive(string) {
        return __str_top__ + string;
    }
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
            __num_top__,
            __num_top__,
            NaN
        ];
        var expected = lodashStable.map(values, function (value) {
            return !isToNumber && value !== value ? __num_top__ : value;
        });
        var actual = lodashStable.map(values, func);
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
            __num_top__,
            __num_top__,
            MAX_SAFE_INTEGER,
            MAX_INTEGER,
            Infinity,
            NaN
        ];
        var expected = lodashStable.map(values, function (value) {
            if (!isToNumber) {
                if (!isToFinite && value == __num_top__) {
                    value = __num_top__;
                } else if (value == Infinity) {
                    value = MAX_INTEGER;
                } else if (value !== value) {
                    value = __num_top__;
                }
                if (isToLength || isToSafeInteger) {
                    value = Math.min(value, isToLength ? MAX_ARRAY_LENGTH : MAX_SAFE_INTEGER);
                }
            }
            var neg = isToLength ? __num_top__ : -value;
            return [
                value,
                value,
                neg,
                neg
            ];
        });
        var actual = lodashStable.map(values, function (value) {
            return [
                func(value),
                func(Object(value)),
                func(-value),
                func(Object(-value))
            ];
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var transforms = [
            identity,
            pad,
            positive,
            negative
        ];
        var values = [
            __str_top__,
            __str_top__,
            MAX_SAFE_INTEGER + __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ];
        var expected = lodashStable.map(values, function (value) {
            var n = +value;
            if (!isToNumber) {
                if (!isToFinite && n == __num_top__) {
                    n = __num_top__;
                } else if (n == Infinity) {
                    n = MAX_INTEGER;
                } else if (!isToFinite && n == Number.MIN_VALUE || n !== n) {
                    n = __num_top__;
                }
                if (isToLength || isToSafeInteger) {
                    n = Math.min(n, isToLength ? MAX_ARRAY_LENGTH : MAX_SAFE_INTEGER);
                }
            }
            var neg = isToLength ? __num_top__ : -n;
            return [
                n,
                n,
                n,
                n,
                n,
                n,
                neg,
                neg
            ];
        });
        var actual = lodashStable.map(values, function (value) {
            return lodashStable.flatMap(transforms, function (mod) {
                return [
                    func(mod(value)),
                    func(Object(mod(value)))
                ];
            });
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var numbers = [
                __num_top__,
                __num_top__,
                __num_top__
            ], transforms = [
                identity,
                pad
            ], values = [
                __str_top__,
                __str_top__,
                __str_top__
            ];
        var expected = lodashStable.map(numbers, function (n) {
            return lodashStable.times(__num_top__, lodashStable.constant(n));
        });
        var actual = lodashStable.map(values, function (value) {
            var upper = value.toUpperCase();
            return lodashStable.flatMap(transforms, function (mod) {
                return [
                    func(mod(value)),
                    func(Object(mod(value))),
                    func(mod(upper)),
                    func(Object(mod(upper)))
                ];
            });
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__ + (isToNumber ? __str_top__ : __str_top__) + __str_top__, function (assert) {
        assert.expect(1);
        var transforms = [
                identity,
                pad,
                positive,
                negative
            ], values = [
                __str_top__,
                __str_top__,
                __str_top__,
                __str_top__,
                __str_top__,
                __str_top__
            ];
        var expected = lodashStable.map(values, function (n) {
            return lodashStable.times(__num_top__, lodashStable.constant(isToNumber ? NaN : __num_top__));
        });
        var actual = lodashStable.map(values, function (value) {
            return lodashStable.flatMap(transforms, function (mod) {
                return [
                    func(mod(value)),
                    func(Object(mod(value)))
                ];
            });
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__ + (isToNumber ? __str_top__ : __str_top__) + __str_top__, function (assert) {
        assert.expect(1);
        if (Symbol) {
            var object1 = Object(symbol), object2 = Object(symbol), values = [
                    symbol,
                    object1,
                    object2
                ], expected = lodashStable.map(values, lodashStable.constant(isToNumber ? NaN : __num_top__));
            object2.valueOf = undefined;
            var actual = lodashStable.map(values, func);
            assert.deepEqual(actual, expected);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = falsey.concat(whitespace);
        var expected = lodashStable.map(values, function (value) {
            return isToNumber && value !== whitespace ? Number(value) : __num_top__;
        });
        var actual = lodashStable.map(values, function (value, index) {
            return index ? func(value) : func();
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
            {},
            [],
            [__num_top__],
            [
                __num_top__,
                __num_top__
            ],
            { 'valueOf': __str_top__ },
            {
                'valueOf': __str_top__,
                'toString': lodashStable.constant(__str_top__)
            },
            {
                'valueOf': lodashStable.constant(__str_top__),
                'toString': __str_top__
            },
            {
                'valueOf': lodashStable.constant(__str_top__),
                'toString': lodashStable.constant(__str_top__)
            },
            { 'valueOf': lodashStable.constant(__str_top__) },
            { 'toString': lodashStable.constant(__str_top__) },
            { 'valueOf': lodashStable.constant(__str_top__) },
            { 'toString': lodashStable.constant(__str_top__) },
            { 'valueOf': lodashStable.constant(__str_top__) },
            { 'toString': lodashStable.constant(__str_top__) }
        ];
        var expected = [
            NaN,
            __num_top__,
            __num_top__,
            NaN,
            NaN,
            __num_top__,
            __num_top__,
            __num_top__,
            NaN,
            NaN,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ];
        if (isToFinite) {
            expected = [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ];
        } else if (!isToNumber) {
            expected = [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ];
        }
        var actual = lodashStable.map(values, func);
        assert.deepEqual(actual, expected);
    });
});