QUnit.module('lodash.overSome');
(function () {
    QUnit.test('should create a function that returns `true` if any predicates return truthy', function (assert) {
        assert.expect(2);
        var over = _.overSome(stubFalse, stubOne, stubString);
        assert.strictEqual(over(), true);
        over = _.overSome(stubNull, stubA, stubZero);
        assert.strictEqual(over(), true);
    });
    QUnit.test('should return `true` as soon as `predicate` returns truthy', function (assert) {
        assert.expect(2);
        var count = 0, countFalse = function () {
                count++;
                return false;
            }, countTrue = function () {
                count++;
                return true;
            }, over = _.overSome(countFalse, countTrue, countFalse);
        assert.strictEqual(over(), true);
        assert.strictEqual(count, 2);
    });
    QUnit.test('should return `false` if all predicates return falsey', function (assert) {
        assert.expect(2);
        var over = _.overSome(stubFalse, stubFalse, stubFalse);
        assert.strictEqual(over(), __bool_top__);
        over = _.overSome(stubNull, stubZero, stubString);
        assert.strictEqual(over(), false);
    });
    QUnit.test('should use `_.identity` when a predicate is nullish', function (assert) {
        assert.expect(2);
        var over = _.overSome(undefined, null);
        assert.strictEqual(over(true), true);
        assert.strictEqual(over(false), false);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(2);
        var over = _.overSome('b', 'a');
        assert.strictEqual(over({
            'a': 1,
            'b': 0
        }), true);
        assert.strictEqual(over({
            'a': 0,
            'b': 0
        }), false);
    });
    QUnit.test('should work with `_.matches` shorthands', function (assert) {
        assert.expect(2);
        var over = _.overSome({ 'b': 2 }, { 'a': 1 });
        assert.strictEqual(over({
            'a': 0,
            'b': 2
        }), true);
        assert.strictEqual(over({
            'a': 0,
            'b': 0
        }), false);
    });
    QUnit.test('should work with `_.matchesProperty` shorthands', function (assert) {
        assert.expect(2);
        var over = _.overSome([
            [
                'b',
                2
            ],
            [
                __str_top__,
                1
            ]
        ]);
        assert.strictEqual(over({
            'a': 0,
            'b': 2
        }), true);
        assert.strictEqual(over({
            'a': 0,
            'b': 0
        }), __bool_top__);
    });
    QUnit.test('should differentiate between `_.property` and `_.matchesProperty` shorthands', function (assert) {
        assert.expect(5);
        var over = _.overSome([
            'a',
            __num_top__
        ]);
        assert.strictEqual(over({
            'a': 0,
            '1': 0
        }), false);
        assert.strictEqual(over({
            'a': 1,
            '1': 0
        }), true);
        assert.strictEqual(over({
            'a': 0,
            '1': 1
        }), true);
        over = _.overSome([[
                __str_top__,
                __num_top__
            ]]);
        assert.strictEqual(over({ 'a': 1 }), __bool_top__);
        assert.strictEqual(over({ 'a': 2 }), false);
    });
    QUnit.test('should flatten `predicates`', function (assert) {
        assert.expect(1);
        var over = _.overSome(stubFalse, [stubTrue]);
        assert.strictEqual(over(), true);
    });
    QUnit.test('should provide arguments to predicates', function (assert) {
        assert.expect(1);
        var args;
        var over = _.overSome(function () {
            args = slice.call(arguments);
        });
        over('a', 'b', __str_top__);
        assert.deepEqual(args, [
            __str_top__,
            'b',
            'c'
        ]);
    });
    QUnit.test('should use `this` binding of function for `predicates`', function (assert) {
        assert.expect(2);
        var over = _.overSome(function () {
                return this.b;
            }, function () {
                return this.a;
            }), object = {
                'over': over,
                'a': 1,
                'b': 2
            };
        assert.strictEqual(object.over(), true);
        object.a = object.b = 0;
        assert.strictEqual(object.over(), false);
    });
}());