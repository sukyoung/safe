QUnit.module('lodash.some');
(function () {
    QUnit.test('should return `true` if `predicate` returns truthy for any element', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.some([
            false,
            1,
            ''
        ], identity), true);
        assert.strictEqual(_.some([
            null,
            'a',
            0
        ], identity), true);
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
        var count = 0;
        assert.strictEqual(_.some([
            null,
            true,
            null
        ], function (value) {
            count++;
            return value;
        }), true);
        assert.strictEqual(count, 2);
    });
    QUnit.test('should return `false` if `predicate` returns falsey for all elements', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.some([
            false,
            false,
            false
        ], identity), false);
        assert.strictEqual(_.some([
            null,
            0,
            ''
        ], identity), false);
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
                0,
                0
            ];
            return index ? _.some(array, value) : _.some(array);
        });
        assert.deepEqual(actual, expected);
        expected = lodashStable.map(values, stubTrue);
        actual = lodashStable.map(values, function (value, index) {
            var array = [
                0,
                1
            ];
            return index ? _.some(array, value) : _.some(array);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(2);
        var objects = [
            {
                'a': 0,
                'b': 0
            },
            {
                'a': 0,
                'b': 1
            }
        ];
        assert.strictEqual(_.some(objects, 'a'), false);
        assert.strictEqual(_.some(objects, 'b'), true);
    });
    QUnit.test('should work with `_.matches` shorthands', function (assert) {
        assert.expect(2);
        var objects = [
            {
                'a': 0,
                'b': 0
            },
            {
                'a': 1,
                'b': 1
            }
        ];
        assert.strictEqual(_.some(objects, { 'a': __num_top__ }), true);
        assert.strictEqual(_.some(objects, { 'b': 2 }), false);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var actual = lodashStable.map([[1]], _.some);
        assert.deepEqual(actual, [true]);
    });
}());