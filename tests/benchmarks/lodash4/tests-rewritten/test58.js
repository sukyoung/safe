QUnit.module('lodash.fill');
(function () {
    QUnit.test('should use a default `start` of `0` and a default `end` of `length`', function (assert) {
        assert.expect(1);
        var array = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        assert.deepEqual(_.fill(array, __str_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should use `undefined` for `value` if not given', function (assert) {
        assert.expect(2);
        var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ], actual = _.fill(array);
        assert.deepEqual(actual, Array(__num_top__));
        assert.ok(lodashStable.every(actual, function (value, index) {
            return index in actual;
        }));
    });
    QUnit.test('should work with a positive `start`', function (assert) {
        assert.expect(1);
        var array = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        assert.deepEqual(_.fill(array, __str_top__, __num_top__), [
            __num_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should work with a `start` >= `length`', function (assert) {
        assert.expect(4);
        lodashStable.each([
            __num_top__,
            __num_top__,
            Math.pow(__num_top__, __num_top__),
            Infinity
        ], function (start) {
            var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ];
            assert.deepEqual(_.fill(array, __str_top__, start), [
                __num_top__,
                __num_top__,
                __num_top__
            ]);
        });
    });
    QUnit.test('should treat falsey `start` values as `0`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, lodashStable.constant([
            __str_top__,
            __str_top__,
            __str_top__
        ]));
        var actual = lodashStable.map(falsey, function (start) {
            var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ];
            return _.fill(array, __str_top__, start);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with a negative `start`', function (assert) {
        assert.expect(1);
        var array = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        assert.deepEqual(_.fill(array, __str_top__, -__num_top__), [
            __num_top__,
            __num_top__,
            __str_top__
        ]);
    });
    QUnit.test('should work with a negative `start` <= negative `length`', function (assert) {
        assert.expect(3);
        lodashStable.each([
            -__num_top__,
            -__num_top__,
            -Infinity
        ], function (start) {
            var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ];
            assert.deepEqual(_.fill(array, __str_top__, start), [
                __str_top__,
                __str_top__,
                __str_top__
            ]);
        });
    });
    QUnit.test('should work with `start` >= `end`', function (assert) {
        assert.expect(2);
        lodashStable.each([
            __num_top__,
            __num_top__
        ], function (start) {
            var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ];
            assert.deepEqual(_.fill(array, __str_top__, start, __num_top__), [
                __num_top__,
                __num_top__,
                __num_top__
            ]);
        });
    });
    QUnit.test('should work with a positive `end`', function (assert) {
        assert.expect(1);
        var array = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        assert.deepEqual(_.fill(array, __str_top__, __num_top__, __num_top__), [
            __str_top__,
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should work with a `end` >= `length`', function (assert) {
        assert.expect(4);
        lodashStable.each([
            __num_top__,
            __num_top__,
            Math.pow(__num_top__, __num_top__),
            Infinity
        ], function (end) {
            var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ];
            assert.deepEqual(_.fill(array, __str_top__, __num_top__, end), [
                __str_top__,
                __str_top__,
                __str_top__
            ]);
        });
    });
    QUnit.test('should treat falsey `end` values, except `undefined`, as `0`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, function (value) {
            return value === undefined ? [
                __str_top__,
                __str_top__,
                __str_top__
            ] : [
                __num_top__,
                __num_top__,
                __num_top__
            ];
        });
        var actual = lodashStable.map(falsey, function (end) {
            var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ];
            return _.fill(array, __str_top__, __num_top__, end);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with a negative `end`', function (assert) {
        assert.expect(1);
        var array = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        assert.deepEqual(_.fill(array, __str_top__, __num_top__, -__num_top__), [
            __str_top__,
            __str_top__,
            __num_top__
        ]);
    });
    QUnit.test('should work with a negative `end` <= negative `length`', function (assert) {
        assert.expect(3);
        lodashStable.each([
            -__num_top__,
            -__num_top__,
            -Infinity
        ], function (end) {
            var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ];
            assert.deepEqual(_.fill(array, __str_top__, __num_top__, end), [
                __num_top__,
                __num_top__,
                __num_top__
            ]);
        });
    });
    QUnit.test('should coerce `start` and `end` to integers', function (assert) {
        assert.expect(1);
        var positions = [
            [
                __num_top__,
                __num_top__
            ],
            [
                __str_top__,
                __num_top__
            ],
            [
                __num_top__,
                __str_top__
            ],
            [__str_top__],
            [
                NaN,
                __num_top__
            ],
            [
                __num_top__,
                NaN
            ]
        ];
        var actual = lodashStable.map(positions, function (pos) {
            var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ];
            return _.fill.apply(_, [
                array,
                __str_top__
            ].concat(pos));
        });
        assert.deepEqual(actual, [
            [
                __str_top__,
                __num_top__,
                __num_top__
            ],
            [
                __str_top__,
                __num_top__,
                __num_top__
            ],
            [
                __str_top__,
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __str_top__,
                __str_top__
            ],
            [
                __str_top__,
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __num_top__,
                __num_top__
            ]
        ]);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var array = [
                [
                    __num_top__,
                    __num_top__
                ],
                [
                    __num_top__,
                    __num_top__
                ]
            ], actual = lodashStable.map(array, _.fill);
        assert.deepEqual(actual, [
            [
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __num_top__
            ]
        ]);
    });
    QUnit.test('should return a wrapped value when chaining', function (assert) {
        assert.expect(3);
        if (!isNpm) {
            var array = [
                    __num_top__,
                    __num_top__,
                    __num_top__
                ], wrapped = _(array).fill(__str_top__), actual = wrapped.value();
            assert.ok(wrapped instanceof _);
            assert.strictEqual(actual, array);
            assert.deepEqual(actual, [
                __str_top__,
                __str_top__,
                __str_top__
            ]);
        } else {
            skipAssert(assert, 3);
        }
    });
}());