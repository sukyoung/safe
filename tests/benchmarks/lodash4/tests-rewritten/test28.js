QUnit.module('lodash.cond');
(function () {
    QUnit.test('should create a conditional function', function (assert) {
        assert.expect(3);
        var cond = _.cond([
            [
                lodashStable.matches({ 'a': __num_top__ }),
                stubA
            ],
            [
                lodashStable.matchesProperty(__str_top__, __num_top__),
                stubB
            ],
            [
                lodashStable.property(__str_top__),
                stubC
            ]
        ]);
        assert.strictEqual(cond({
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        }), __str_top__);
        assert.strictEqual(cond({
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        }), __str_top__);
        assert.strictEqual(cond({
            'a': -__num_top__,
            'b': __num_top__,
            'c': __num_top__
        }), __str_top__);
    });
    QUnit.test('should provide arguments to functions', function (assert) {
        assert.expect(2);
        var args1, args2, expected = [
                __str_top__,
                __str_top__,
                __str_top__
            ];
        var cond = _.cond([[
                function () {
                    args1 || (args1 = slice.call(arguments));
                    return __bool_top__;
                },
                function () {
                    args2 || (args2 = slice.call(arguments));
                }
            ]]);
        cond(__str_top__, __str_top__, __str_top__);
        assert.deepEqual(args1, expected);
        assert.deepEqual(args2, expected);
    });
    QUnit.test('should work with predicate shorthands', function (assert) {
        assert.expect(3);
        var cond = _.cond([
            [
                { 'a': __num_top__ },
                stubA
            ],
            [
                [
                    __str_top__,
                    __num_top__
                ],
                stubB
            ],
            [
                __str_top__,
                stubC
            ]
        ]);
        assert.strictEqual(cond({
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        }), __str_top__);
        assert.strictEqual(cond({
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        }), __str_top__);
        assert.strictEqual(cond({
            'a': -__num_top__,
            'b': __num_top__,
            'c': __num_top__
        }), __str_top__);
    });
    QUnit.test('should return `undefined` when no condition is met', function (assert) {
        assert.expect(1);
        var cond = _.cond([[
                stubFalse,
                stubA
            ]]);
        assert.strictEqual(cond({ 'a': __num_top__ }), undefined);
    });
    QUnit.test('should throw a TypeError if `pairs` is not composed of functions', function (assert) {
        assert.expect(2);
        lodashStable.each([
            __bool_top__,
            __bool_top__
        ], function (value) {
            assert.raises(function () {
                _.cond([[
                        stubTrue,
                        value
                    ]])();
            }, TypeError);
        });
    });
    QUnit.test('should use `this` binding of function for `pairs`', function (assert) {
        assert.expect(1);
        var cond = _.cond([[
                function (a) {
                    return this[a];
                },
                function (a, b) {
                    return this[b];
                }
            ]]);
        var object = {
            'cond': cond,
            'a': __num_top__,
            'b': __num_top__
        };
        assert.strictEqual(object.cond(__str_top__, __str_top__), __num_top__);
    });
}());