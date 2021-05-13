QUnit.module('lodash.over');
(function () {
    QUnit.test('should create a function that invokes `iteratees`', function (assert) {
        assert.expect(1);
        var over = _.over(Math.max, Math.min);
        assert.deepEqual(over(1, 2, __num_top__, 4), [
            4,
            1
        ]);
    });
    QUnit.test('should use `_.identity` when a predicate is nullish', function (assert) {
        assert.expect(1);
        var over = _.over(undefined, null);
        assert.deepEqual(over('a', __str_top__, 'c'), [
            'a',
            'a'
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
        var over = _.over({ 'b': 1 }, { 'a': __num_top__ });
        assert.deepEqual(over({
            'a': 1,
            'b': 2
        }), [
            __bool_top__,
            true
        ]);
    });
    QUnit.test('should work with `_.matchesProperty` shorthands', function (assert) {
        assert.expect(2);
        var over = _.over([
            [
                __str_top__,
                2
            ],
            [
                __str_top__,
                2
            ]
        ]);
        assert.deepEqual(over({
            'a': 1,
            'b': 2
        }), [
            true,
            false
        ]);
        assert.deepEqual(over({
            'a': 2,
            'b': 1
        }), [
            __bool_top__,
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
            1,
            2
        ]);
        assert.deepEqual(over({
            'a': 2,
            '1': 1
        }), [
            2,
            1
        ]);
        over = _.over([[
                'a',
                1
            ]]);
        assert.deepEqual(over({ 'a': 1 }), [true]);
        assert.deepEqual(over({ 'a': __num_top__ }), [false]);
    });
    QUnit.test('should provide arguments to predicates', function (assert) {
        assert.expect(1);
        var over = _.over(function () {
            return slice.call(arguments);
        });
        assert.deepEqual(over('a', 'b', __str_top__), [[
                'a',
                'b',
                'c'
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
                'b': __num_top__
            };
        assert.deepEqual(object.over(), [
            2,
            1
        ]);
    });
}());