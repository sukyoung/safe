QUnit.module('curry methods');
lodashStable.each([
    __str_top__,
    'curryRight'
], function (methodName) {
    var func = _[methodName], fn = function (a, b) {
            return slice.call(arguments);
        }, isCurry = methodName == __str_top__;
    QUnit.test('`_.' + methodName + '` should not error on functions with the same name as lodash methods', function (assert) {
        assert.expect(1);
        function run(a, b) {
            return a + b;
        }
        var curried = func(run);
        try {
            var actual = curried(1)(2);
        } catch (e) {
        }
        assert.strictEqual(actual, 3);
    });
    QUnit.test('`_.' + methodName + '` should work for function names that shadow those on `Object.prototype`', function (assert) {
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
        assert.deepEqual(curried(1)(2)(3), expected);
    });
    QUnit.test('`_.' + methodName + '` should work as an iteratee for methods like `_.map`', function (assert) {
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
                    __str_top__
                ] : [
                    'b',
                    __str_top__
                ]));
            var actual = lodashStable.map(curries, function (curried) {
                return curried('a')(__str_top__);
            });
            assert.deepEqual(actual, expected);
        });
    });
});