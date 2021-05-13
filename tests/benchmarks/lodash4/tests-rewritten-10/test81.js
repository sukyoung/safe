QUnit.module('lodash.groupBy');
(function () {
    var array = [
        6.1,
        4.2,
        6.3
    ];
    QUnit.test('should transform keys by `iteratee`', function (assert) {
        assert.expect(1);
        var actual = _.groupBy(array, Math.floor);
        assert.deepEqual(actual, {
            '4': [4.2],
            '6': [
                6.1,
                6.3
            ]
        });
    });
    QUnit.test('should use `_.identity` when `iteratee` is nullish', function (assert) {
        assert.expect(1);
        var array = [
                __num_top__,
                4,
                6
            ], values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant({
                '4': [4],
                '6': [
                    6,
                    6
                ]
            }));
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.groupBy(array, value) : _.groupBy(array);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        var actual = _.groupBy([
            __str_top__,
            'two',
            'three'
        ], __str_top__);
        assert.deepEqual(actual, {
            '3': [
                'one',
                __str_top__
            ],
            '5': ['three']
        });
    });
    QUnit.test('should only add values to own, not inherited, properties', function (assert) {
        assert.expect(2);
        var actual = _.groupBy(array, function (n) {
            return Math.floor(n) > 4 ? 'hasOwnProperty' : __str_top__;
        });
        assert.deepEqual(actual.constructor, [4.2]);
        assert.deepEqual(actual.hasOwnProperty, [
            6.1,
            6.3
        ]);
    });
    QUnit.test('should work with a number for `iteratee`', function (assert) {
        assert.expect(2);
        var array = [
            [
                __num_top__,
                'a'
            ],
            [
                2,
                'a'
            ],
            [
                2,
                __str_top__
            ]
        ];
        assert.deepEqual(_.groupBy(array, 0), {
            '1': [[
                    1,
                    'a'
                ]],
            '2': [
                [
                    2,
                    'a'
                ],
                [
                    2,
                    'b'
                ]
            ]
        });
        assert.deepEqual(_.groupBy(array, 1), {
            'a': [
                [
                    1,
                    __str_top__
                ],
                [
                    2,
                    'a'
                ]
            ],
            'b': [[
                    2,
                    'b'
                ]]
        });
    });
    QUnit.test('should work with an object for `collection`', function (assert) {
        assert.expect(1);
        var actual = _.groupBy({
            'a': 6.1,
            'b': 4.2,
            'c': __num_top__
        }, Math.floor);
        assert.deepEqual(actual, {
            '4': [4.2],
            '6': [
                6.1,
                6.3
            ]
        });
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var array = lodashStable.range(LARGE_ARRAY_SIZE).concat(lodashStable.range(Math.floor(LARGE_ARRAY_SIZE / __num_top__), LARGE_ARRAY_SIZE), lodashStable.range(Math.floor(LARGE_ARRAY_SIZE / 1.5), LARGE_ARRAY_SIZE));
            var iteratee = function (value) {
                    value.push(value[0]);
                    return value;
                }, predicate = function (value) {
                    return isEven(value[0]);
                }, actual = _(array).groupBy().map(iteratee).filter(predicate).take().value();
            assert.deepEqual(actual, _.take(_.filter(lodashStable.map(_.groupBy(array), iteratee), predicate)));
        } else {
            skipAssert(assert);
        }
    });
}());