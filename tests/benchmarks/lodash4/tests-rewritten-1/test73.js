QUnit.module('forIn methods');
lodashStable.each([
    'forIn',
    'forInRight'
], function (methodName) {
    var func = _[methodName];
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = 1;
        }
        Foo.prototype.b = 2;
        var keys = [];
        func(new Foo(), function (value, key) {
            keys.push(key);
        });
        assert.deepEqual(keys.sort(), [
            'a',
            'b'
        ]);
    });
});