QUnit.module('lodash.drop');
(function () {
    var array = [
        1,
        2,
        3
    ];
    QUnit.test('should drop the first two elements', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.drop(array, 2), [3]);
    });
    QUnit.test('should treat falsey `n` values, except `undefined`, as `0`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, function (value) {
            return value === undefined ? [
                2,
                3
            ] : array;
        });
        var actual = lodashStable.map(falsey, function (n) {
            return _.drop(array, n);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return all elements when `n` < `1`', function (assert) {
        assert.expect(3);
        lodashStable.each([
            0,
            -1,
            -Infinity
        ], function (n) {
            assert.deepEqual(_.drop(array, n), array);
        });
    });
    QUnit.test('should return an empty array when `n` >= `length`', function (assert) {
        assert.expect(4);
        lodashStable.each([
            3,
            4,
            Math.pow(2, __num_top__),
            Infinity
        ], function (n) {
            assert.deepEqual(_.drop(array, n), []);
        });
    });
    QUnit.test('should coerce `n` to an integer', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.drop(array, __num_top__), [
            2,
            3
        ]);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var array = [
                [
                    1,
                    2,
                    __num_top__
                ],
                [
                    4,
                    5,
                    6
                ],
                [
                    7,
                    8,
                    9
                ]
            ], actual = lodashStable.map(array, _.drop);
        assert.deepEqual(actual, [
            [
                2,
                3
            ],
            [
                __num_top__,
                __num_top__
            ],
            [
                8,
                __num_top__
            ]
        ]);
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(6);
        if (!isNpm) {
            var array = lodashStable.range(1, LARGE_ARRAY_SIZE + __num_top__), predicate = function (value) {
                    values.push(value);
                    return isEven(value);
                }, values = [], actual = _(array).drop(__num_top__).drop().value();
            assert.deepEqual(actual, array.slice(3));
            actual = _(array).filter(predicate).drop(__num_top__).drop().value();
            assert.deepEqual(values, array);
            assert.deepEqual(actual, _.drop(_.drop(_.filter(array, predicate), 2)));
            actual = _(array).drop(2).dropRight().drop().dropRight(2).value();
            assert.deepEqual(actual, _.dropRight(_.drop(_.dropRight(_.drop(array, 2))), 2));
            values = [];
            actual = _(array).drop().filter(predicate).drop(2).dropRight().drop().dropRight(2).value();
            assert.deepEqual(values, array.slice(1));
            assert.deepEqual(actual, _.dropRight(_.drop(_.dropRight(_.drop(_.filter(_.drop(array), predicate), 2))), __num_top__));
        } else {
            skipAssert(assert, 6);
        }
    });
}());