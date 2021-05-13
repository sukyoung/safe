QUnit.module('lodash.overEvery');
(function () {
    QUnit.test('should create a function that returns `true` if all predicates return truthy', function (assert) {
        assert.expect(1);
        var over = _.overEvery(stubTrue, stubOne, stubA);
        assert.strictEqual(over(), true);
    });
    QUnit.test('should return `false` as soon as a predicate returns falsey', function (assert) {
        assert.expect(2);
        var count = 0, countFalse = function () {
                count++;
                return false;
            }, countTrue = function () {
                count++;
                return true;
            }, over = _.overEvery(countTrue, countFalse, countTrue);
        assert.strictEqual(over(), false);
        assert.strictEqual(count, 2);
    });
    QUnit.test('should use `_.identity` when a predicate is nullish', function (assert) {
        assert.expect(2);
        var over = _.overEvery(undefined, null);
        assert.strictEqual(over(true), true);
        assert.strictEqual(over(false), false);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(2);
        var over = _.overEvery('b', __str_top__);
        assert.strictEqual(over({
            'a': 1,
            'b': 1
        }), true);
        assert.strictEqual(over({
            'a': 0,
            'b': __num_top__
        }), false);
    });
    QUnit.test('should work with `_.matches` shorthands', function (assert) {
        assert.expect(2);
        var over = _.overEvery({ 'b': 2 }, { 'a': 1 });
        assert.strictEqual(over({
            'a': 1,
            'b': 2
        }), true);
        assert.strictEqual(over({
            'a': __num_top__,
            'b': 2
        }), false);
    });
    QUnit.test('should work with `_.matchesProperty` shorthands', function (assert) {
        assert.expect(2);
        var over = _.overEvery([
            [
                'b',
                2
            ],
            [
                'a',
                1
            ]
        ]);
        assert.strictEqual(over({
            'a': 1,
            'b': __num_top__
        }), true);
        assert.strictEqual(over({
            'a': 0,
            'b': 2
        }), false);
    });
    QUnit.test('should differentiate between `_.property` and `_.matchesProperty` shorthands', function (assert) {
        assert.expect(5);
        var over = _.overEvery([
            'a',
            1
        ]);
        assert.strictEqual(over({
            'a': 1,
            '1': 1
        }), true);
        assert.strictEqual(over({
            'a': 1,
            '1': 0
        }), false);
        assert.strictEqual(over({
            'a': 0,
            '1': 1
        }), __bool_top__);
        over = _.overEvery([[
                'a',
                1
            ]]);
        assert.strictEqual(over({ 'a': 1 }), true);
        assert.strictEqual(over({ 'a': 2 }), false);
    });
    QUnit.test('should flatten `predicates`', function (assert) {
        assert.expect(1);
        var over = _.overEvery(stubTrue, [stubFalse]);
        assert.strictEqual(over(), false);
    });
    QUnit.test('should provide arguments to predicates', function (assert) {
        assert.expect(1);
        var args;
        var over = _.overEvery(function () {
            args = slice.call(arguments);
        });
        over('a', 'b', 'c');
        assert.deepEqual(args, [
            'a',
            'b',
            'c'
        ]);
    });
    QUnit.test('should use `this` binding of function for `predicates`', function (assert) {
        assert.expect(2);
        var over = _.overEvery(function () {
                return this.b;
            }, function () {
                return this.a;
            }), object = {
                'over': over,
                'a': 1,
                'b': 2
            };
        assert.strictEqual(object.over(), true);
        object.a = 0;
        assert.strictEqual(object.over(), false);
    });
}());