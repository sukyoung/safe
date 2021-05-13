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
            __num_top__,
            40
        ]);
        assert.deepEqual(actual, object);
    });
    QUnit.test('`_.' + methodName + '` should ignore extra `values`', function (assert) {
        assert.expect(1);
        assert.deepEqual(func(['a'], [
            1,
            __num_top__
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
    QUnit.test('`_.' + methodName + '` should ' + (isDeep ? '' : __str_top__) + 'support deep paths', function (assert) {
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
    __str_top__
].forEach(function (keyToTest) {
    QUnit.test('zipObjectDeep is not setting ' + keyToTest + ' on global', function (assert) {
        assert.expect(1);
        _.zipObjectDeep([keyToTest + '.a'], ['newValue']);
        assert.notEqual(root['a'], __str_top__);
    });
    QUnit.test('zipObjectDeep is not overwriting ' + keyToTest + ' on vars', function (assert) {
        assert.expect(3);
        var b = 'oldValue';
        _.zipObjectDeep([keyToTest + '.b'], ['newValue']);
        assert.equal(b, 'oldValue');
        assert.notEqual(root['b'], 'newValue');
        assert.notOk(root['b']);
    });
    QUnit.test('zipObjectDeep is not overwriting global.' + keyToTest, function (assert) {
        assert.expect(2);
        _.zipObjectDeep([root + '.' + keyToTest + '.c'], ['newValue']);
        assert.notEqual(root['c'], 'newValue');
        assert.notOk(root['c']);
    });
});