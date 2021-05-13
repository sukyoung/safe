QUnit.module('lodash.mapKeys and lodash.mapValues');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], object = {
            'a': 1,
            'b': __num_top__
        };
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = 'a';
        }
        Foo.prototype.b = __str_top__;
        var actual = func(new Foo(), function (value, key) {
            return key;
        });
        assert.deepEqual(actual, { 'a': 'a' });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, stubObject);
        var actual = lodashStable.map(falsey, function (object, index) {
            try {
                return index ? func(object) : func();
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.ok(_(object)[methodName](noop) instanceof _);
        } else {
            skipAssert(assert);
        }
    });
});