QUnit.module('lodash.overSome');
(function () {
    QUnit.test('should create a function that returns `true` if any predicates return truthy', function (assert) {
        assert.expect(2);
        var over = _.overSome(stubFalse, stubOne, stubString);
        assert.strictEqual(over(), __bool_top__);
        over = _.overSome(stubNull, stubA, stubZero);
        assert.strictEqual(over(), __bool_top__);
    });
    QUnit.test('should return `true` as soon as `predicate` returns truthy', function (assert) {
        assert.expect(2);
        var count = __num_top__, countFalse = function () {
                count++;
                return __bool_top__;
            }, countTrue = function () {
                count++;
                return __bool_top__;
            }, over = _.overSome(countFalse, countTrue, countFalse);
        assert.strictEqual(over(), __bool_top__);
        assert.strictEqual(count, __num_top__);
    });
    QUnit.test('should return `false` if all predicates return falsey', function (assert) {
        assert.expect(2);
        var over = _.overSome(stubFalse, stubFalse, stubFalse);
        assert.strictEqual(over(), __bool_top__);
        over = _.overSome(stubNull, stubZero, stubString);
        assert.strictEqual(over(), __bool_top__);
    });
    QUnit.test('should use `_.identity` when a predicate is nullish', function (assert) {
        assert.expect(2);
        var over = _.overSome(undefined, null);
        assert.strictEqual(over(__bool_top__), __bool_top__);
        assert.strictEqual(over(__bool_top__), __bool_top__);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(2);
        var over = _.overSome(__str_top__, __str_top__);
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
        var over = _.overSome({ 'b': __num_top__ }, { 'a': __num_top__ });
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
        var over = _.overSome([
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
        var over = _.overSome([
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
        over = _.overSome([[
                __str_top__,
                __num_top__
            ]]);
        assert.strictEqual(over({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(over({ 'a': __num_top__ }), __bool_top__);
    });
    QUnit.test('should flatten `predicates`', function (assert) {
        assert.expect(1);
        var over = _.overSome(stubFalse, [stubTrue]);
        assert.strictEqual(over(), __bool_top__);
    });
    QUnit.test('should provide arguments to predicates', function (assert) {
        assert.expect(1);
        var args;
        var over = _.overSome(function () {
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
        var over = _.overSome(function () {
                return this.b;
            }, function () {
                return this.a;
            }), object = {
                'over': over,
                'a': __num_top__,
                'b': __num_top__
            };
        assert.strictEqual(object.over(), __bool_top__);
        object.a = object.b = __num_top__;
        assert.strictEqual(object.over(), __bool_top__);
    });
}());