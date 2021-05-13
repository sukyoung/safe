QUnit.module('pad methods');
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isPad = methodName == __str_top__, isStart = methodName == __str_top__, string = __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        assert.strictEqual(func(string, __num_top__), string);
        assert.strictEqual(func(string, __num_top__), string);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        lodashStable.each([
            __num_top__,
            -__num_top__
        ], function (length) {
            assert.strictEqual(func(string, length), string);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        lodashStable.each([
            __str_top__,
            __str_top__
        ], function (length) {
            var actual = length ? isStart ? __str_top__ : __str_top__ : string;
            assert.strictEqual(func(string, length), actual);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(6);
        lodashStable.each([
            undefined,
            __str_top__
        ], function (chars) {
            var expected = chars ? isPad ? __str_top__ : chars : __str_top__;
            assert.strictEqual(func(null, __num_top__, chars), expected);
            assert.strictEqual(func(undefined, __num_top__, chars), expected);
            assert.strictEqual(func(__str_top__, __num_top__, chars), expected);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
                __str_top__,
                Object(__str_top__)
            ], expected = lodashStable.map(values, lodashStable.constant(string));
        var actual = lodashStable.map(values, function (value) {
            return _.pad(string, __num_top__, value);
        });
        assert.deepEqual(actual, expected);
    });
});