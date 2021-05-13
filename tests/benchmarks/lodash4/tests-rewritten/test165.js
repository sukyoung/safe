QUnit.module('lodash.overArgs');
(function () {
    function fn() {
        return slice.call(arguments);
    }
    QUnit.test('should transform each argument', function (assert) {
        assert.expect(1);
        var over = _.overArgs(fn, doubled, square);
        assert.deepEqual(over(__num_top__, __num_top__), [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should use `_.identity` when a predicate is nullish', function (assert) {
        assert.expect(1);
        var over = _.overArgs(fn, undefined, null);
        assert.deepEqual(over(__str_top__, __str_top__), [
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        var over = _.overArgs(fn, __str_top__, __str_top__);
        assert.deepEqual(over({ 'b': __num_top__ }, { 'a': __num_top__ }), [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should work with `_.matches` shorthands', function (assert) {
        assert.expect(1);
        var over = _.overArgs(fn, { 'b': __num_top__ }, { 'a': __num_top__ });
        assert.deepEqual(over({ 'b': __num_top__ }, { 'a': __num_top__ }), [
            __bool_top__,
            __bool_top__
        ]);
    });
    QUnit.test('should work with `_.matchesProperty` shorthands', function (assert) {
        assert.expect(1);
        var over = _.overArgs(fn, [
            [
                __str_top__,
                __num_top__
            ],
            [
                __str_top__,
                __num_top__
            ]
        ]);
        assert.deepEqual(over({ 'b': __num_top__ }, { 'a': __num_top__ }), [
            __bool_top__,
            __bool_top__
        ]);
    });
    QUnit.test('should differentiate between `_.property` and `_.matchesProperty` shorthands', function (assert) {
        assert.expect(2);
        var over = _.overArgs(fn, [
            __str_top__,
            __num_top__
        ]);
        assert.deepEqual(over({ 'a': __num_top__ }, { '1': __num_top__ }), [
            __num_top__,
            __num_top__
        ]);
        over = _.overArgs(fn, [[
                __str_top__,
                __num_top__
            ]]);
        assert.deepEqual(over({ 'a': __num_top__ }), [__bool_top__]);
    });
    QUnit.test('should flatten `transforms`', function (assert) {
        assert.expect(1);
        var over = _.overArgs(fn, [
            doubled,
            square
        ], String);
        assert.deepEqual(over(__num_top__, __num_top__, __num_top__), [
            __num_top__,
            __num_top__,
            __str_top__
        ]);
    });
    QUnit.test('should not transform any argument greater than the number of transforms', function (assert) {
        assert.expect(1);
        var over = _.overArgs(fn, doubled, square);
        assert.deepEqual(over(__num_top__, __num_top__, __num_top__), [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should not transform any arguments if no transforms are given', function (assert) {
        assert.expect(1);
        var over = _.overArgs(fn);
        assert.deepEqual(over(__num_top__, __num_top__, __num_top__), [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should not pass `undefined` if there are more transforms than arguments', function (assert) {
        assert.expect(1);
        var over = _.overArgs(fn, doubled, identity);
        assert.deepEqual(over(__num_top__), [__num_top__]);
    });
    QUnit.test('should provide the correct argument to each transform', function (assert) {
        assert.expect(1);
        var argsList = [], transform = function () {
                argsList.push(slice.call(arguments));
            }, over = _.overArgs(noop, transform, transform, transform);
        over(__str_top__, __str_top__);
        assert.deepEqual(argsList, [
            [__str_top__],
            [__str_top__]
        ]);
    });
    QUnit.test('should use `this` binding of function for `transforms`', function (assert) {
        assert.expect(1);
        var over = _.overArgs(function (x) {
            return this[x];
        }, function (x) {
            return this === x;
        });
        var object = {
            'over': over,
            'true': __num_top__
        };
        assert.strictEqual(object.over(object), __num_top__);
    });
}());