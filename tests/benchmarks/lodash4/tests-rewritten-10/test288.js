QUnit.module('zipObject methods');
lodashStable.each([
    'zipObject',
    'zipObjectDeep'
], function (methodName) {
    var func = _[methodName], object = {
            'barney': 36,
            'fred': 40
        }, isDeep = methodName == 'zipObjectDeep';
    QUnit.test('`_.' + methodName + '` should zip together key/value arrays into an object', function (assert) {
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
            __num_top__,
            2
        ]), { 'a': __num_top__ });
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
    QUnit.test('`_.' + methodName + __str_top__ + (isDeep ? '' : 'not ') + 'support deep paths', function (assert) {
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
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
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
    __str_top__,
    'constructor',
    'prototype'
].forEach(function (keyToTest) {
    QUnit.test(__str_top__ + keyToTest + ' on global', function (assert) {
        assert.expect(1);
        _.zipObjectDeep([keyToTest + '.a'], ['newValue']);
        assert.notEqual(root['a'], 'newValue');
    });
    QUnit.test('zipObjectDeep is not overwriting ' + keyToTest + __str_top__, function (assert) {
        assert.expect(3);
        var b = 'oldValue';
        _.zipObjectDeep([keyToTest + '.b'], ['newValue']);
        assert.equal(b, 'oldValue');
        assert.notEqual(root['b'], 'newValue');
        assert.notOk(root['b']);
    });
    QUnit.test('zipObjectDeep is not overwriting global.' + keyToTest, function (assert) {
        assert.expect(2);
        _.zipObjectDeep([root + '.' + keyToTest + __str_top__], ['newValue']);
        assert.notEqual(root[__str_top__], 'newValue');
        assert.notOk(root['c']);
    });
});