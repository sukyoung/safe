QUnit.module('lodash.some');
(function () {
    QUnit.test('should return `true` if `predicate` returns truthy for any element', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.some([
            __bool_top__,
            __num_top__,
            __str_top__
        ], identity), __bool_top__);
        assert.strictEqual(_.some([
            null,
            __str_top__,
            __num_top__
        ], identity), __bool_top__);
    });
    QUnit.test('should return `false` for empty collections', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(empties, stubFalse);
        var actual = lodashStable.map(empties, function (value) {
            try {
                return _.some(value, identity);
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return `true` as soon as `predicate` returns truthy', function (assert) {
        assert.expect(2);
        var count = __num_top__;
        assert.strictEqual(_.some([
            null,
            __bool_top__,
            null
        ], function (value) {
            count++;
            return value;
        }), __bool_top__);
        assert.strictEqual(count, __num_top__);
    });
    QUnit.test('should return `false` if `predicate` returns falsey for all elements', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.some([
            __bool_top__,
            __bool_top__,
            __bool_top__
        ], identity), __bool_top__);
        assert.strictEqual(_.some([
            null,
            __num_top__,
            __str_top__
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
            var array = [
                __num_top__,
                __num_top__
            ];
            return index ? _.some(array, value) : _.some(array);
        });
        assert.deepEqual(actual, expected);
        expected = lodashStable.map(values, stubTrue);
        actual = lodashStable.map(values, function (value, index) {
            var array = [
                __num_top__,
                __num_top__
            ];
            return index ? _.some(array, value) : _.some(array);
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
        assert.strictEqual(_.some(objects, __str_top__), __bool_top__);
        assert.strictEqual(_.some(objects, __str_top__), __bool_top__);
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
        assert.strictEqual(_.some(objects, { 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(_.some(objects, { 'b': __num_top__ }), __bool_top__);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var actual = lodashStable.map([[__num_top__]], _.some);
        assert.deepEqual(actual, [__bool_top__]);
    });
}());