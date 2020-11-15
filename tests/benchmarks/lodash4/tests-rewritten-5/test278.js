QUnit.module('update methods');
lodashStable.each([
    'update',
    __str_top__
], function (methodName) {
    var func = _[methodName], oldValue = __num_top__;
    QUnit.test('`_.' + methodName + '` should invoke `updater` with the value on `path` of `object`', function (assert) {
        assert.expect(4);
        var object = { 'a': [{ 'b': { 'c': oldValue } }] }, expected = oldValue + 1;
        lodashStable.each([
            'a[0].b.c',
            [
                'a',
                __str_top__,
                'b',
                'c'
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