QUnit.module('extremum methods');
lodashStable.each([
    __str_top__,
    __str_top__,
    'min',
    'minBy'
], function (methodName) {
    var func = _[methodName], isMax = /^max/.test(methodName);
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var curr = new Date(), past = new Date(0);
        assert.strictEqual(func([
            curr,
            past
        ]), isMax ? curr : past);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array = lodashStable.range(0, __num_top__);
        assert.strictEqual(func(array), isMax ? 499999 : 0);
    });
    QUnit.test('`_.' + methodName + '` should work when chaining on an array with only one value', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var actual = _([40])[methodName]();
            assert.strictEqual(actual, 40);
        } else {
            skipAssert(assert);
        }
    });
});
lodashStable.each([
    __str_top__,
    'minBy'
], function (methodName) {
    var array = [
            1,
            2,
            3
        ], func = _[methodName], isMax = methodName == 'maxBy';
    QUnit.test('`_.' + methodName + '` should work with an `iteratee`', function (assert) {
        assert.expect(1);
        var actual = func(array, function (n) {
            return -n;
        });
        assert.strictEqual(actual, isMax ? __num_top__ : __num_top__);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(2);
        var objects = [
                { 'a': 2 },
                { 'a': 3 },
                { 'a': 1 }
            ], actual = func(objects, 'a');
        assert.deepEqual(actual, objects[isMax ? 1 : 2]);
        var arrays = [
            [2],
            [__num_top__],
            [1]
        ];
        actual = func(arrays, 0);
        assert.deepEqual(actual, arrays[isMax ? 1 : 2]);
    });
    QUnit.test('`_.' + methodName + '` should work when `iteratee` returns +/-Infinity', function (assert) {
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