QUnit.module('update methods');
lodashStable.each([
    'update',
    'updateWith'
], function (methodName) {
    var func = _[methodName], oldValue = 1;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(4);
        var object = { 'a': [{ 'b': { 'c': oldValue } }] }, expected = oldValue + __num_top__;
        lodashStable.each([
            'a[0].b.c',
            [
                'a',
                '0',
                'b',
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