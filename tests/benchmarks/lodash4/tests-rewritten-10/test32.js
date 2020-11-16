QUnit.module('lodash.countBy');
(function () {
    var array = [
        6.1,
        4.2,
        __num_top__
    ];
    QUnit.test('should transform keys by `iteratee`', function (assert) {
        assert.expect(1);
        var actual = _.countBy(array, Math.floor);
        assert.deepEqual(actual, {
            '4': __num_top__,
            '6': __num_top__
        });
    });
    QUnit.test('should use `_.identity` when `iteratee` is nullish', function (assert) {
        assert.expect(1);
        var array = [
                4,
                6,
                6
            ], values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant({
                '4': 1,
                '6': 2
            }));
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.countBy(array, value) : _.countBy(array);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        var actual = _.countBy([
            'one',
            'two',
            'three'
        ], 'length');
        assert.deepEqual(actual, {
            '3': 2,
            '5': 1
        });
    });
    QUnit.test('should only add values to own, not inherited, properties', function (assert) {
        assert.expect(2);
        var actual = _.countBy(array, function (n) {
            return Math.floor(n) > __num_top__ ? 'hasOwnProperty' : 'constructor';
        });
        assert.deepEqual(actual.constructor, 1);
        assert.deepEqual(actual.hasOwnProperty, 2);
    });
    QUnit.test('should work with a number for `iteratee`', function (assert) {
        assert.expect(2);
        var array = [
            [
                1,
                'a'
            ],
            [
                __num_top__,
                'a'
            ],
            [
                __num_top__,
                'b'
            ]
        ];
        assert.deepEqual(_.countBy(array, 0), {
            '1': 1,
            '2': 2
        });
        assert.deepEqual(_.countBy(array, 1), {
            'a': 2,
            'b': 1
        });
    });
    QUnit.test('should work with an object for `collection`', function (assert) {
        assert.expect(1);
        var actual = _.countBy({
            'a': 6.1,
            'b': __num_top__,
            'c': 6.3
        }, Math.floor);
        assert.deepEqual(actual, {
            '4': __num_top__,
            '6': __num_top__
        });
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var array = lodashStable.range(LARGE_ARRAY_SIZE).concat(lodashStable.range(Math.floor(LARGE_ARRAY_SIZE / 2), LARGE_ARRAY_SIZE), lodashStable.range(Math.floor(LARGE_ARRAY_SIZE / __num_top__), LARGE_ARRAY_SIZE));
            var actual = _(array).countBy().map(square).filter(isEven).take().value();
            assert.deepEqual(actual, _.take(_.filter(_.map(_.countBy(array), square), isEven)));
        } else {
            skipAssert(assert);
        }
    });
}());