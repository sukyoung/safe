QUnit.module('lodash.every');
(function () {
    QUnit.test('should return `true` if `predicate` returns truthy for all elements', function (assert) {
        assert.expect(1);
        assert.strictEqual(lodashStable.every([
            __bool_top__,
            __num_top__,
            __str_top__
        ], identity), __bool_top__);
    });
    QUnit.test('should return `true` for empty collections', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(empties, stubTrue);
        var actual = lodashStable.map(empties, function (value) {
            try {
                return _.every(value, identity);
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return `false` as soon as `predicate` returns falsey', function (assert) {
        assert.expect(2);
        var count = __num_top__;
        assert.strictEqual(_.every([
            __bool_top__,
            null,
            __bool_top__
        ], function (value) {
            count++;
            return value;
        }), __bool_top__);
        assert.strictEqual(count, __num_top__);
    });
    QUnit.test('should work with collections of `undefined` values (test in IE < 9)', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.every([
            undefined,
            undefined,
            undefined
        ], identity), __bool_top__);
    });
    QUnit.test('should use `_.identity` when `predicate` is nullish', function (assert) {
        assert.expect(2);
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, stubFalse);
        var actual = lodashStable.map(values, function (value, index) {
            var array = [__num_top__];
            return index ? _.every(array, value) : _.every(array);
        });
        assert.deepEqual(actual, expected);
        expected = lodashStable.map(values, stubTrue);
        actual = lodashStable.map(values, function (value, index) {
            var array = [__num_top__];
            return index ? _.every(array, value) : _.every(array);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(2);
        var objects = [
            {
                'a': __num_top__,
                'b': __num_top__
            },
            {
                'a': __num_top__,
                'b': __num_top__
            }
        ];
        assert.strictEqual(_.every(objects, __str_top__), __bool_top__);
        assert.strictEqual(_.every(objects, __str_top__), __bool_top__);
    });
    QUnit.test('should work with `_.matches` shorthands', function (assert) {
        assert.expect(2);
        var objects = [
            {
                'a': __num_top__,
                'b': __num_top__
            },
            {
                'a': __num_top__,
                'b': __num_top__
            }
        ];
        assert.strictEqual(_.every(objects, { 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(_.every(objects, { 'b': __num_top__ }), __bool_top__);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var actual = lodashStable.map([[__num_top__]], _.every);
        assert.deepEqual(actual, [__bool_top__]);
    });
}());