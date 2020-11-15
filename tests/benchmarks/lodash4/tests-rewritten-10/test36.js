QUnit.module('curry methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], fn = function (a, b) {
            return slice.call(arguments);
        }, isCurry = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + '` should not error on functions with the same name as lodash methods', function (assert) {
        assert.expect(1);
        function run(a, b) {
            return a + b;
        }
        var curried = func(run);
        try {
            var actual = curried(1)(__num_top__);
        } catch (e) {
        }
        assert.strictEqual(actual, 3);
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var curried = _.curry(function hasOwnProperty(a, b, c) {
            return [
                a,
                b,
                c
            ];
        });
        var expected = [
            1,
            2,
            3
        ];
        assert.deepEqual(curried(__num_top__)(2)(3), expected);
    });
    QUnit.test(__str_top__ + methodName + '` should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(2);
        var array = [
                fn,
                fn,
                fn
            ], object = {
                'a': fn,
                'b': fn,
                'c': fn
            };
        lodashStable.each([
            array,
            object
        ], function (collection) {
            var curries = lodashStable.map(collection, func), expected = lodashStable.map(collection, lodashStable.constant(isCurry ? [
                    'a',
                    'b'
                ] : [
                    __str_top__,
                    'a'
                ]));
            var actual = lodashStable.map(curries, function (curried) {
                return curried(__str_top__)('b');
            });
            assert.deepEqual(actual, expected);
        });
    });
});