QUnit.module('lodash.overArgs');
(function () {
    function fn() {
        return slice.call(arguments);
    }
    QUnit.test('should transform each argument', function (assert) {
        assert.expect(1);
        var over = _.overArgs(fn, doubled, square);
        assert.deepEqual(over(5, 10), [
            10,
            100
        ]);
    });
    QUnit.test('should use `_.identity` when a predicate is nullish', function (assert) {
        assert.expect(1);
        var over = _.overArgs(fn, undefined, null);
        assert.deepEqual(over('a', 'b'), [
            'a',
            __str_top__
        ]);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        var over = _.overArgs(fn, 'b', 'a');
        assert.deepEqual(over({ 'b': 2 }, { 'a': 1 }), [
            2,
            1
        ]);
    });
    QUnit.test('should work with `_.matches` shorthands', function (assert) {
        assert.expect(1);
        var over = _.overArgs(fn, { 'b': 1 }, { 'a': 1 });
        assert.deepEqual(over({ 'b': 2 }, { 'a': 1 }), [
            __bool_top__,
            true
        ]);
    });
    QUnit.test('should work with `_.matchesProperty` shorthands', function (assert) {
        assert.expect(1);
        var over = _.overArgs(fn, [
            [
                __str_top__,
                1
            ],
            [
                'a',
                1
            ]
        ]);
        assert.deepEqual(over({ 'b': __num_top__ }, { 'a': 1 }), [
            false,
            true
        ]);
    });
    QUnit.test('should differentiate between `_.property` and `_.matchesProperty` shorthands', function (assert) {
        assert.expect(2);
        var over = _.overArgs(fn, [
            'a',
            1
        ]);
        assert.deepEqual(over({ 'a': 1 }, { '1': 2 }), [
            1,
            2
        ]);
        over = _.overArgs(fn, [[
                'a',
                1
            ]]);
        assert.deepEqual(over({ 'a': 1 }), [true]);
    });
    QUnit.test('should flatten `transforms`', function (assert) {
        assert.expect(1);
        var over = _.overArgs(fn, [
            doubled,
            square
        ], String);
        assert.deepEqual(over(5, 10, 15), [
            10,
            100,
            '15'
        ]);
    });
    QUnit.test('should not transform any argument greater than the number of transforms', function (assert) {
        assert.expect(1);
        var over = _.overArgs(fn, doubled, square);
        assert.deepEqual(over(5, 10, 18), [
            10,
            100,
            18
        ]);
    });
    QUnit.test('should not transform any arguments if no transforms are given', function (assert) {
        assert.expect(1);
        var over = _.overArgs(fn);
        assert.deepEqual(over(5, 10, 18), [
            5,
            10,
            __num_top__
        ]);
    });
    QUnit.test('should not pass `undefined` if there are more transforms than arguments', function (assert) {
        assert.expect(1);
        var over = _.overArgs(fn, doubled, identity);
        assert.deepEqual(over(5), [10]);
    });
    QUnit.test('should provide the correct argument to each transform', function (assert) {
        assert.expect(1);
        var argsList = [], transform = function () {
                argsList.push(slice.call(arguments));
            }, over = _.overArgs(noop, transform, transform, transform);
        over('a', 'b');
        assert.deepEqual(argsList, [
            ['a'],
            ['b']
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
            'true': 1
        };
        assert.strictEqual(object.over(object), 1);
    });
}());