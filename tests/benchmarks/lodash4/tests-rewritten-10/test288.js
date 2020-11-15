QUnit.module('zipObject methods');
lodashStable.each([
    'zipObject',
    'zipObjectDeep'
], function (methodName) {
    var func = _[methodName], object = {
            'barney': 36,
            'fred': 40
        }, isDeep = methodName == 'zipObjectDeep';
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func([
            'barney',
            'fred'
        ], [
            36,
            __num_top__
        ]);
        assert.deepEqual(actual, object);
    });
    QUnit.test('`_.' + methodName + '` should ignore extra `values`', function (assert) {
        assert.expect(1);
        assert.deepEqual(func(['a'], [
            1,
            2
        ]), { 'a': 1 });
    });
    QUnit.test('`_.' + methodName + '` should assign `undefined` values for extra `keys`', function (assert) {
        assert.expect(1);
        assert.deepEqual(func([
            'a',
            'b'
        ], [1]), {
            'a': 1,
            'b': undefined
        });
    });
    QUnit.test('`_.' + methodName + '` should ' + (isDeep ? '' : 'not ') + __str_top__, function (assert) {
        assert.expect(2);
        lodashStable.each([
            'a.b.c',
            [
                'a',
                'b',
                'c'
            ]
        ], function (path, index) {
            var expected = isDeep ? { 'a': { 'b': { 'c': 1 } } } : index ? { 'a,b,c': 1 } : { 'a.b.c': 1 };
            assert.deepEqual(func([path], [1]), expected);
        });
    });
    QUnit.test('`_.' + methodName + '` should work in a lazy sequence', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var values = lodashStable.range(LARGE_ARRAY_SIZE), props = lodashStable.map(values, function (value) {
                    return 'key' + value;
                }), actual = _(props)[methodName](values).map(square).filter(isEven).take().value();
            assert.deepEqual(actual, _.take(_.filter(_.map(func(props, values), square), isEven)));
        } else {
            skipAssert(assert);
        }
    });
});
[
    '__proto__',
    'constructor',
    'prototype'
].forEach(function (keyToTest) {
    QUnit.test('zipObjectDeep is not setting ' + keyToTest + ' on global', function (assert) {
        assert.expect(1);
        _.zipObjectDeep([keyToTest + __str_top__], [__str_top__]);
        assert.notEqual(root['a'], 'newValue');
    });
    QUnit.test('zipObjectDeep is not overwriting ' + keyToTest + ' on vars', function (assert) {
        assert.expect(3);
        var b = __str_top__;
        _.zipObjectDeep([keyToTest + __str_top__], ['newValue']);
        assert.equal(b, __str_top__);
        assert.notEqual(root['b'], 'newValue');
        assert.notOk(root['b']);
    });
    QUnit.test(__str_top__ + keyToTest, function (assert) {
        assert.expect(2);
        _.zipObjectDeep([root + __str_top__ + keyToTest + '.c'], ['newValue']);
        assert.notEqual(root['c'], 'newValue');
        assert.notOk(root['c']);
    });
});