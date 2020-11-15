QUnit.module('forIn methods');
lodashStable.each([
    __str_top__,
    'forInRight'
], function (methodName) {
    var func = _[methodName];
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = __num_top__;
        }
        Foo.prototype.b = __num_top__;
        var keys = [];
        func(new Foo(), function (value, key) {
            keys.push(key);
        });
        assert.deepEqual(keys.sort(), [
            'a',
            __str_top__
        ]);
    });
});