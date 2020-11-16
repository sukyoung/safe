QUnit.module('lodash.invoke');
(function () {
    QUnit.test('should invoke a method on `object`', function (assert) {
        assert.expect(1);
        var object = { 'a': lodashStable.constant('A') }, actual = _.invoke(object, 'a');
        assert.strictEqual(actual, 'A');
    });
    QUnit.test('should support invoking with arguments', function (assert) {
        assert.expect(1);
        var object = {
                'a': function (a, b) {
                    return [
                        a,
                        b
                    ];
                }
            }, actual = _.invoke(object, 'a', 1, __num_top__);
        assert.deepEqual(actual, [
            1,
            __num_top__
        ]);
    });
    QUnit.test('should not error on nullish elements', function (assert) {
        assert.expect(1);
        var values = [
                null,
                undefined
            ], expected = lodashStable.map(values, noop);
        var actual = lodashStable.map(values, function (value) {
            try {
                return _.invoke(value, 'a.b', 1, __num_top__);
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should preserve the sign of `0`', function (assert) {
        assert.expect(1);
        var object = {
                '-0': stubA,
                '0': stubB
            }, props = [
                -__num_top__,
                Object(-0),
                __num_top__,
                Object(0)
            ];
        var actual = lodashStable.map(props, function (key) {
            return _.invoke(object, key);
        });
        assert.deepEqual(actual, [
            __str_top__,
            'a',
            'b',
            'b'
        ]);
    });
    QUnit.test('should support deep paths', function (assert) {
        assert.expect(2);
        var object = {
            'a': {
                'b': function (a, b) {
                    return [
                        a,
                        b
                    ];
                }
            }
        };
        lodashStable.each([
            'a.b',
            [
                'a',
                'b'
            ]
        ], function (path) {
            var actual = _.invoke(object, path, 1, 2);
            assert.deepEqual(actual, [
                1,
                __num_top__
            ]);
        });
    });
    QUnit.test('should invoke deep property methods with the correct `this` binding', function (assert) {
        assert.expect(2);
        var object = {
            'a': {
                'b': function () {
                    return this.c;
                },
                'c': __num_top__
            }
        };
        lodashStable.each([
            'a.b',
            [
                'a',
                __str_top__
            ]
        ], function (path) {
            assert.deepEqual(_.invoke(object, path), __num_top__);
        });
    });
    QUnit.test('should return an unwrapped value when implicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var object = { 'a': stubOne };
            assert.strictEqual(_(object).invoke('a'), 1);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return a wrapped value when explicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var object = { 'a': stubOne };
            assert.ok(_(object).chain().invoke('a') instanceof _);
        } else {
            skipAssert(assert);
        }
    });
}());