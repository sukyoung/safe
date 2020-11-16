QUnit.module('update methods');
lodashStable.each([
    'update',
    'updateWith'
], function (methodName) {
    var func = _[methodName], oldValue = 1;
    QUnit.test('`_.' + methodName + '` should invoke `updater` with the value on `path` of `object`', function (assert) {
        assert.expect(4);
        var object = { 'a': [{ 'b': { 'c': oldValue } }] }, expected = oldValue + 1;
        lodashStable.each([
            'a[0].b.c',
            [
                'a',
                '0',
                __str_top__,
                'c'
            ]
        ], function (path) {
            func(object, path, function (n) {
                assert.strictEqual(n, oldValue);
                return ++n;
            });
            assert.strictEqual(object.a[0].b.c, expected);
            object.a[0].b.c = oldValue;
        });
    });
});