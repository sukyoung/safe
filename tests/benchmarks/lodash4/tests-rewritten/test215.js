QUnit.module('lodash.sampleSize');
(function () {
    var array = [
        __num_top__,
        __num_top__,
        __num_top__
    ];
    QUnit.test('should return an array of random elements', function (assert) {
        assert.expect(2);
        var actual = _.sampleSize(array, __num_top__);
        assert.strictEqual(actual.length, __num_top__);
        assert.deepEqual(lodashStable.difference(actual, array), []);
    });
    QUnit.test('should contain elements of the collection', function (assert) {
        assert.expect(1);
        var actual = _.sampleSize(array, array.length).sort();
        assert.deepEqual(actual, array);
    });
    QUnit.test('should treat falsey `size` values, except `undefined`, as `0`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, function (value) {
            return value === undefined ? [__str_top__] : [];
        });
        var actual = lodashStable.map(falsey, function (size, index) {
            return index ? _.sampleSize([__str_top__], size) : _.sampleSize([__str_top__]);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return an empty array when `n` < `1` or `NaN`', function (assert) {
        assert.expect(3);
        lodashStable.each([
            __num_top__,
            -__num_top__,
            -Infinity
        ], function (n) {
            assert.deepEqual(_.sampleSize(array, n), []);
        });
    });
    QUnit.test('should return all elements when `n` >= `length`', function (assert) {
        assert.expect(4);
        lodashStable.each([
            __num_top__,
            __num_top__,
            Math.pow(__num_top__, __num_top__),
            Infinity
        ], function (n) {
            var actual = _.sampleSize(array, n).sort();
            assert.deepEqual(actual, array);
        });
    });
    QUnit.test('should coerce `n` to an integer', function (assert) {
        assert.expect(1);
        var actual = _.sampleSize(array, __num_top__);
        assert.strictEqual(actual.length, __num_top__);
    });
    QUnit.test('should return an empty array for empty collections', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(empties, stubArray);
        var actual = lodashStable.transform(empties, function (result, value) {
            try {
                result.push(_.sampleSize(value, __num_top__));
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should sample an object', function (assert) {
        assert.expect(2);
        var object = {
                'a': __num_top__,
                'b': __num_top__,
                'c': __num_top__
            }, actual = _.sampleSize(object, __num_top__);
        assert.strictEqual(actual.length, __num_top__);
        assert.deepEqual(lodashStable.difference(actual, lodashStable.values(object)), []);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var actual = lodashStable.map([[__str_top__]], _.sampleSize);
        assert.deepEqual(actual, [[__str_top__]]);
    });
}());