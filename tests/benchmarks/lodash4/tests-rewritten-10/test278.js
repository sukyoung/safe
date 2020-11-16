QUnit.module('update methods');
lodashStable.each([
    __str_top__,
    'updateWith'
], function (methodName) {
    var func = _[methodName], oldValue = 1;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(4);
        var object = { 'a': [{ 'b': { 'c': oldValue } }] }, expected = oldValue + 1;
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__,
                __str_top__,
                __str_top__
            ]
        ], function (path) {
            func(object, path, function (n) {
                assert.strictEqual(n, oldValue);
                return ++n;
            });
            assert.strictEqual(object.a[__num_top__].b.c, expected);
            object.a[__num_top__].b.c = oldValue;
        });
    });
});