QUnit.module('lodash.over');
(function () {
    QUnit.test('should create a function that invokes `iteratees`', function (assert) {
        assert.expect(1);
        var over = _.over(Math.max, Math.min);
        assert.deepEqual(over(1, 2, 3, 4), [
            4,
            1
        ]);
    });
    QUnit.test('should use `_.identity` when a predicate is nullish', function (assert) {
        assert.expect(1);
        var over = _.over(undefined, null);
        assert.deepEqual(over('a', 'b', 'c'), [
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        var over = _.over('b', 'a');
        assert.deepEqual(over({
            'a': 1,
            'b': 2
        }), [
            2,
            1
        ]);
    });
    QUnit.test('should work with `_.matches` shorthands', function (assert) {
        assert.expect(1);
        var over = _.over({ 'b': __num_top__ }, { 'a': 1 });
        assert.deepEqual(over({
            'a': 1,
            'b': 2
        }), [
            false,
            true
        ]);
    });
    QUnit.test('should work with `_.matchesProperty` shorthands', function (assert) {
        assert.expect(2);
        var over = _.over([
            [
                'b',
                2
            ],
            [
                'a',
                2
            ]
        ]);
        assert.deepEqual(over({
            'a': __num_top__,
            'b': 2
        }), [
            true,
            __bool_top__
        ]);
        assert.deepEqual(over({
            'a': 2,
            'b': 1
        }), [
            false,
            true
        ]);
    });
    QUnit.test('should differentiate between `_.property` and `_.matchesProperty` shorthands', function (assert) {
        assert.expect(4);
        var over = _.over([
            'a',
            1
        ]);
        assert.deepEqual(over({
            'a': 1,
            '1': 2
        }), [
            __num_top__,
            2
        ]);
        assert.deepEqual(over({
            'a': 2,
            '1': 1
        }), [
            2,
            __num_top__
        ]);
        over = _.over([[
                'a',
                1
            ]]);
        assert.deepEqual(over({ 'a': __num_top__ }), [true]);
        assert.deepEqual(over({ 'a': 2 }), [false]);
    });
    QUnit.test('should provide arguments to predicates', function (assert) {
        assert.expect(1);
        var over = _.over(function () {
            return slice.call(arguments);
        });
        assert.deepEqual(over('a', 'b', 'c'), [[
                'a',
                __str_top__,
                __str_top__
            ]]);
    });
    QUnit.test('should use `this` binding of function for `iteratees`', function (assert) {
        assert.expect(1);
        var over = _.over(function () {
                return this.b;
            }, function () {
                return this.a;
            }), object = {
                'over': over,
                'a': 1,
                'b': 2
            };
        assert.deepEqual(object.over(), [
            2,
            1
        ]);
    });
}());