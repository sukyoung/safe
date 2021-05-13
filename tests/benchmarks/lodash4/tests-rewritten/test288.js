QUnit.module('zipObject methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], object = {
            'barney': __num_top__,
            'fred': __num_top__
        }, isDeep = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func([
            __str_top__,
            __str_top__
        ], [
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(actual, object);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.deepEqual(func([__str_top__], [
            __num_top__,
            __num_top__
        ]), { 'a': __num_top__ });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.deepEqual(func([
            __str_top__,
            __str_top__
        ], [__num_top__]), {
            'a': __num_top__,
            'b': undefined
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__ + (isDeep ? __str_top__ : __str_top__) + __str_top__, function (assert) {
        assert.expect(2);
        lodashStable.each([
            __str_top__,
            [
                __str_top__,
                __str_top__,
                __str_top__
            ]
        ], function (path, index) {
            var expected = isDeep ? { 'a': { 'b': { 'c': __num_top__ } } } : index ? { 'a,b,c': __num_top__ } : { 'a.b.c': __num_top__ };
            assert.deepEqual(func([path], [__num_top__]), expected);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var values = lodashStable.range(LARGE_ARRAY_SIZE), props = lodashStable.map(values, function (value) {
                    return __str_top__ + value;
                }), actual = _(props)[methodName](values).map(square).filter(isEven).take().value();
            assert.deepEqual(actual, _.take(_.filter(_.map(func(props, values), square), isEven)));
        } else {
            skipAssert(assert);
        }
    });
});
[
    __str_top__,
    __str_top__,
    __str_top__
].forEach(function (keyToTest) {
    QUnit.test(__str_top__ + keyToTest + __str_top__, function (assert) {
        assert.expect(1);
        _.zipObjectDeep([keyToTest + __str_top__], [__str_top__]);
        assert.notEqual(root[__str_top__], __str_top__);
    });
    QUnit.test(__str_top__ + keyToTest + __str_top__, function (assert) {
        assert.expect(3);
        var b = __str_top__;
        _.zipObjectDeep([keyToTest + __str_top__], [__str_top__]);
        assert.equal(b, __str_top__);
        assert.notEqual(root[__str_top__], __str_top__);
        assert.notOk(root[__str_top__]);
    });
    QUnit.test(__str_top__ + keyToTest, function (assert) {
        assert.expect(2);
        _.zipObjectDeep([root + __str_top__ + keyToTest + __str_top__], [__str_top__]);
        assert.notEqual(root[__str_top__], __str_top__);
        assert.notOk(root[__str_top__]);
    });
});