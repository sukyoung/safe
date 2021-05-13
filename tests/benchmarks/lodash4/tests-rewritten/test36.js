QUnit.module('curry methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], fn = function (a, b) {
            return slice.call(arguments);
        }, isCurry = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        function run(a, b) {
            return a + b;
        }
        var curried = func(run);
        try {
            var actual = curried(__num_top__)(__num_top__);
        } catch (e) {
        }
        assert.strictEqual(actual, __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var curried = _.curry(function hasOwnProperty(a, b, c) {
            return [
                a,
                b,
                c
            ];
        });
        var expected = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        assert.deepEqual(curried(__num_top__)(__num_top__)(__num_top__), expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
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
                    __str_top__,
                    __str_top__
                ] : [
                    __str_top__,
                    __str_top__
                ]));
            var actual = lodashStable.map(curries, function (curried) {
                return curried(__str_top__)(__str_top__);
            });
            assert.deepEqual(actual, expected);
        });
    });
});