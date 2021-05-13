QUnit.module('extremum methods');
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isMax = /^max/.test(methodName);
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var curr = new Date(), past = new Date(__num_top__);
        assert.strictEqual(func([
            curr,
            past
        ]), isMax ? curr : past);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array = lodashStable.range(__num_top__, __num_top__);
        assert.strictEqual(func(array), isMax ? __num_top__ : __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var actual = _([__num_top__])[methodName]();
            assert.strictEqual(actual, __num_top__);
        } else {
            skipAssert(assert);
        }
    });
});
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var array = [
            __num_top__,
            __num_top__,
            __num_top__
        ], func = _[methodName], isMax = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func(array, function (n) {
            return -n;
        });
        assert.strictEqual(actual, isMax ? __num_top__ : __num_top__);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(2);
        var objects = [
                { 'a': __num_top__ },
                { 'a': __num_top__ },
                { 'a': __num_top__ }
            ], actual = func(objects, __str_top__);
        assert.deepEqual(actual, objects[isMax ? __num_top__ : __num_top__]);
        var arrays = [
            [__num_top__],
            [__num_top__],
            [__num_top__]
        ];
        actual = func(arrays, __num_top__);
        assert.deepEqual(actual, arrays[isMax ? __num_top__ : __num_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var value = isMax ? -Infinity : Infinity, object = { 'a': value };
        var actual = func([
            object,
            { 'a': value }
        ], function (object) {
            return object.a;
        });
        assert.strictEqual(actual, object);
    });
});