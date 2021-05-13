QUnit.module('lodash.overEvery');
(function () {
    QUnit.test('should create a function that returns `true` if all predicates return truthy', function (assert) {
        assert.expect(1);
        var over = _.overEvery(stubTrue, stubOne, stubA);
        assert.strictEqual(over(), __bool_top__);
    });
    QUnit.test('should return `false` as soon as a predicate returns falsey', function (assert) {
        assert.expect(2);
        var count = __num_top__, countFalse = function () {
                count++;
                return __bool_top__;
            }, countTrue = function () {
                count++;
                return __bool_top__;
            }, over = _.overEvery(countTrue, countFalse, countTrue);
        assert.strictEqual(over(), __bool_top__);
        assert.strictEqual(count, __num_top__);
    });
    QUnit.test('should use `_.identity` when a predicate is nullish', function (assert) {
        assert.expect(2);
        var over = _.overEvery(undefined, null);
        assert.strictEqual(over(__bool_top__), __bool_top__);
        assert.strictEqual(over(__bool_top__), __bool_top__);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(2);
        var over = _.overEvery(__str_top__, __str_top__);
        assert.strictEqual(over({
            'a': __num_top__,
            'b': __num_top__
        }), __bool_top__);
        assert.strictEqual(over({
            'a': __num_top__,
            'b': __num_top__
        }), __bool_top__);
    });
    QUnit.test('should work with `_.matches` shorthands', function (assert) {
        assert.expect(2);
        var over = _.overEvery({ 'b': __num_top__ }, { 'a': __num_top__ });
        assert.strictEqual(over({
            'a': __num_top__,
            'b': __num_top__
        }), __bool_top__);
        assert.strictEqual(over({
            'a': __num_top__,
            'b': __num_top__
        }), __bool_top__);
    });
    QUnit.test('should work with `_.matchesProperty` shorthands', function (assert) {
        assert.expect(2);
        var over = _.overEvery([
            [
                __str_top__,
                __num_top__
            ],
            [
                __str_top__,
                __num_top__
            ]
        ]);
        assert.strictEqual(over({
            'a': __num_top__,
            'b': __num_top__
        }), __bool_top__);
        assert.strictEqual(over({
            'a': __num_top__,
            'b': __num_top__
        }), __bool_top__);
    });
    QUnit.test('should differentiate between `_.property` and `_.matchesProperty` shorthands', function (assert) {
        assert.expect(5);
        var over = _.overEvery([
            __str_top__,
            __num_top__
        ]);
        assert.strictEqual(over({
            'a': __num_top__,
            '1': __num_top__
        }), __bool_top__);
        assert.strictEqual(over({
            'a': __num_top__,
            '1': __num_top__
        }), __bool_top__);
        assert.strictEqual(over({
            'a': __num_top__,
            '1': __num_top__
        }), __bool_top__);
        over = _.overEvery([[
                __str_top__,
                __num_top__
            ]]);
        assert.strictEqual(over({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(over({ 'a': __num_top__ }), __bool_top__);
    });
    QUnit.test('should flatten `predicates`', function (assert) {
        assert.expect(1);
        var over = _.overEvery(stubTrue, [stubFalse]);
        assert.strictEqual(over(), __bool_top__);
    });
    QUnit.test('should provide arguments to predicates', function (assert) {
        assert.expect(1);
        var args;
        var over = _.overEvery(function () {
            args = slice.call(arguments);
        });
        over(__str_top__, __str_top__, __str_top__);
        assert.deepEqual(args, [
            __str_top__,
            __str_top__,
            __str_top__
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
                'a': __num_top__,
                'b': __num_top__
            };
        assert.strictEqual(object.over(), __bool_top__);
        object.a = __num_top__;
        assert.strictEqual(object.over(), __bool_top__);
    });
}());