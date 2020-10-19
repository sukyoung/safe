QUnit.module('lodash.over');
(function () {
    QUnit.test('should create a function that invokes `iteratees`', function (assert) {
        assert.expect(1);
        var over = _.over(Math.max, Math.min);
        assert.deepEqual(over(__num_top__, __num_top__, __num_top__, __num_top__), [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should use `_.identity` when a predicate is nullish', function (assert) {
        assert.expect(1);
        var over = _.over(undefined, null);
        assert.deepEqual(over(__str_top__, __str_top__, __str_top__), [
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        var over = _.over(__str_top__, __str_top__);
        assert.deepEqual(over({
            'a': __num_top__,
            'b': __num_top__
        }), [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should work with `_.matches` shorthands', function (assert) {
        assert.expect(1);
        var over = _.over({ 'b': __num_top__ }, { 'a': __num_top__ });
        assert.deepEqual(over({
            'a': __num_top__,
            'b': __num_top__
        }), [
            __bool_top__,
            __bool_top__
        ]);
    });
    QUnit.test('should work with `_.matchesProperty` shorthands', function (assert) {
        assert.expect(2);
        var over = _.over([
            [
                __str_top__,
                __num_top__
            ],
            [
                __str_top__,
                __num_top__
            ]
        ]);
        assert.deepEqual(over({
            'a': __num_top__,
            'b': __num_top__
        }), [
            __bool_top__,
            __bool_top__
        ]);
        assert.deepEqual(over({
            'a': __num_top__,
            'b': __num_top__
        }), [
            __bool_top__,
            __bool_top__
        ]);
    });
    QUnit.test('should differentiate between `_.property` and `_.matchesProperty` shorthands', function (assert) {
        assert.expect(4);
        var over = _.over([
            __str_top__,
            __num_top__
        ]);
        assert.deepEqual(over({
            'a': __num_top__,
            '1': __num_top__
        }), [
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(over({
            'a': __num_top__,
            '1': __num_top__
        }), [
            __num_top__,
            __num_top__
        ]);
        over = _.over([[
                __str_top__,
                __num_top__
            ]]);
        assert.deepEqual(over({ 'a': __num_top__ }), [__bool_top__]);
        assert.deepEqual(over({ 'a': __num_top__ }), [__bool_top__]);
    });
    QUnit.test('should provide arguments to predicates', function (assert) {
        assert.expect(1);
        var over = _.over(function () {
            return slice.call(arguments);
        });
        assert.deepEqual(over(__str_top__, __str_top__, __str_top__), [[
                __str_top__,
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
                'a': __num_top__,
                'b': __num_top__
            };
        assert.deepEqual(object.over(), [
            __num_top__,
            __num_top__
        ]);
    });
}());