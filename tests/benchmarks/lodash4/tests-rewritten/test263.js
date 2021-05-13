QUnit.module('trim methods');
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName, index) {
    var func = _[methodName], parts = [];
    if (index != __num_top__) {
        parts.push(__str_top__);
    }
    if (index != __num_top__) {
        parts.push(__str_top__);
    }
    parts = parts.join(__str_top__);
    QUnit.test(__str_top__ + methodName + __str_top__ + parts + __str_top__, function (assert) {
        assert.expect(1);
        var string = whitespace + __str_top__ + whitespace, expected = (index == __num_top__ ? whitespace : __str_top__) + __str_top__ + (index == __num_top__ ? whitespace : __str_top__);
        assert.strictEqual(func(string), expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = { 'toString': lodashStable.constant(whitespace + __str_top__ + whitespace) }, expected = (index == __num_top__ ? whitespace : __str_top__) + __str_top__ + (index == __num_top__ ? whitespace : __str_top__);
        assert.strictEqual(func(object), expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__ + parts + __str_top__, function (assert) {
        assert.expect(1);
        var string = __str_top__, expected = (index == __num_top__ ? __str_top__ : __str_top__) + __str_top__ + (index == __num_top__ ? __str_top__ : __str_top__);
        assert.strictEqual(func(string, __str_top__), expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = { 'toString': lodashStable.constant(__str_top__) }, string = __str_top__, expected = (index == __num_top__ ? __str_top__ : __str_top__) + __str_top__ + (index == __num_top__ ? __str_top__ : __str_top__);
        assert.strictEqual(func(string, object), expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(6);
        lodashStable.each([
            null,
            __str_top__
        ], function (chars) {
            assert.strictEqual(func(null, chars), __str_top__);
            assert.strictEqual(func(undefined, chars), __str_top__);
            assert.strictEqual(func(__str_top__, chars), __str_top__);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var string = whitespace + __str_top__ + whitespace, expected = (index == __num_top__ ? whitespace : __str_top__) + __str_top__ + (index == __num_top__ ? whitespace : __str_top__);
        assert.strictEqual(func(string, undefined), expected);
        assert.strictEqual(func(string, __str_top__), string);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var string = Object(whitespace + __str_top__ + whitespace), trimmed = (index == __num_top__ ? whitespace : __str_top__) + __str_top__ + (index == __num_top__ ? whitespace : __str_top__), actual = lodashStable.map([
                string,
                string,
                string
            ], func);
        assert.deepEqual(actual, [
            trimmed,
            trimmed,
            trimmed
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var string = whitespace + __str_top__ + whitespace, expected = (index == __num_top__ ? whitespace : __str_top__) + __str_top__ + (index == __num_top__ ? whitespace : __str_top__);
            assert.strictEqual(_(string)[methodName](), expected);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var string = whitespace + __str_top__ + whitespace;
            assert.ok(_(string).chain()[methodName]() instanceof _);
        } else {
            skipAssert(assert);
        }
    });
});