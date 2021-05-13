QUnit.module('forIn methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName];
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
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
            __str_top__,
            __str_top__
        ]);
    });
});