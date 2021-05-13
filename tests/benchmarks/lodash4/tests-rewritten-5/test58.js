QUnit.module('lodash.fill');
(function () {
    QUnit.test('should use a default `start` of `0` and a default `end` of `length`', function (assert) {
        assert.expect(1);
        var array = [
            1,
            2,
            3
        ];
        assert.deepEqual(_.fill(array, 'a'), [
            'a',
            'a',
            'a'
        ]);
    });
    QUnit.test('should use `undefined` for `value` if not given', function (assert) {
        assert.expect(2);
        var array = [
                1,
                2,
                3
            ], actual = _.fill(array);
        assert.deepEqual(actual, Array(__num_top__));
        assert.ok(lodashStable.every(actual, function (value, index) {
            return index in actual;
        }));
    });
    QUnit.test('should work with a positive `start`', function (assert) {
        assert.expect(1);
        var array = [
            1,
            2,
            3
        ];
        assert.deepEqual(_.fill(array, 'a', 1), [
            1,
            'a',
            'a'
        ]);
    });
    QUnit.test('should work with a `start` >= `length`', function (assert) {
        assert.expect(4);
        lodashStable.each([
            3,
            4,
            Math.pow(2, 32),
            Infinity
        ], function (start) {
            var array = [
                1,
                2,
                3
            ];
            assert.deepEqual(_.fill(array, 'a', start), [
                1,
                2,
                3
            ]);
        });
    });
    QUnit.test('should treat falsey `start` values as `0`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, lodashStable.constant([
            'a',
            'a',
            'a'
        ]));
        var actual = lodashStable.map(falsey, function (start) {
            var array = [
                1,
                2,
                3
            ];
            return _.fill(array, 'a', start);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with a negative `start`', function (assert) {
        assert.expect(1);
        var array = [
            1,
            2,
            3
        ];
        assert.deepEqual(_.fill(array, 'a', -1), [
            1,
            __num_top__,
            'a'
        ]);
    });
    QUnit.test('should work with a negative `start` <= negative `length`', function (assert) {
        assert.expect(3);
        lodashStable.each([
            -3,
            -4,
            -Infinity
        ], function (start) {
            var array = [
                1,
                2,
                3
            ];
            assert.deepEqual(_.fill(array, 'a', start), [
                'a',
                'a',
                'a'
            ]);
        });
    });
    QUnit.test('should work with `start` >= `end`', function (assert) {
        assert.expect(2);
        lodashStable.each([
            2,
            3
        ], function (start) {
            var array = [
                1,
                2,
                3
            ];
            assert.deepEqual(_.fill(array, 'a', start, __num_top__), [
                1,
                2,
                3
            ]);
        });
    });
    QUnit.test('should work with a positive `end`', function (assert) {
        assert.expect(1);
        var array = [
            1,
            2,
            3
        ];
        assert.deepEqual(_.fill(array, 'a', 0, 1), [
            'a',
            2,
            3
        ]);
    });
    QUnit.test('should work with a `end` >= `length`', function (assert) {
        assert.expect(4);
        lodashStable.each([
            3,
            4,
            Math.pow(2, 32),
            Infinity
        ], function (end) {
            var array = [
                1,
                2,
                3
            ];
            assert.deepEqual(_.fill(array, 'a', 0, end), [
                'a',
                'a',
                'a'
            ]);
        });
    });
    QUnit.test('should treat falsey `end` values, except `undefined`, as `0`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, function (value) {
            return value === undefined ? [
                'a',
                'a',
                'a'
            ] : [
                1,
                __num_top__,
                3
            ];
        });
        var actual = lodashStable.map(falsey, function (end) {
            var array = [
                1,
                2,
                3
            ];
            return _.fill(array, 'a', 0, end);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with a negative `end`', function (assert) {
        assert.expect(1);
        var array = [
            1,
            2,
            3
        ];
        assert.deepEqual(_.fill(array, 'a', 0, -1), [
            'a',
            'a',
            3
        ]);
    });
    QUnit.test('should work with a negative `end` <= negative `length`', function (assert) {
        assert.expect(3);
        lodashStable.each([
            -3,
            -4,
            -Infinity
        ], function (end) {
            var array = [
                1,
                2,
                3
            ];
            assert.deepEqual(_.fill(array, 'a', 0, end), [
                1,
                2,
                3
            ]);
        });
    });
    QUnit.test('should coerce `start` and `end` to integers', function (assert) {
        assert.expect(1);
        var positions = [
            [
                0.1,
                1.6
            ],
            [
                '0',
                1
            ],
            [
                0,
                '1'
            ],
            ['1'],
            [
                NaN,
                1
            ],
            [
                1,
                NaN
            ]
        ];
        var actual = lodashStable.map(positions, function (pos) {
            var array = [
                1,
                2,
                3
            ];
            return _.fill.apply(_, [
                array,
                'a'
            ].concat(pos));
        });
        assert.deepEqual(actual, [
            [
                'a',
                2,
                3
            ],
            [
                'a',
                2,
                3
            ],
            [
                'a',
                2,
                3
            ],
            [
                1,
                'a',
                'a'
            ],
            [
                'a',
                2,
                3
            ],
            [
                1,
                2,
                3
            ]
        ]);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var array = [
                [
                    1,
                    2
                ],
                [
                    __num_top__,
                    4
                ]
            ], actual = lodashStable.map(array, _.fill);
        assert.deepEqual(actual, [
            [
                0,
                0
            ],
            [
                1,
                1
            ]
        ]);
    });
    QUnit.test('should return a wrapped value when chaining', function (assert) {
        assert.expect(3);
        if (!isNpm) {
            var array = [
                    1,
                    2,
                    3
                ], wrapped = _(array).fill('a'), actual = wrapped.value();
            assert.ok(wrapped instanceof _);
            assert.strictEqual(actual, array);
            assert.deepEqual(actual, [
                'a',
                'a',
                'a'
            ]);
        } else {
            skipAssert(assert, 3);
        }
    });
}());