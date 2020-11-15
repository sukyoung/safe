QUnit.module('update methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], oldValue = __num_top__;
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(4);
        var object = { 'a': [{ 'b': { 'c': oldValue } }] }, expected = oldValue + __num_top__;
        lodashStable.each([
            'a[0].b.c',
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
            object.a[0].b.c = oldValue;
        });
    });
});