QUnit.module('pull methods');
lodashStable.each([
    'pull',
    'pullAll',
    'pullAllWith'
], function (methodName) {
    var func = _[methodName], isPull = methodName == 'pull';
    function pull(array, values) {
        return isPull ? func.apply(undefined, [array].concat(values)) : func(array, values);
    }
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var array = [
                1,
                2,
                3
            ], actual = pull(array, [
                1,
                3
            ]);
        assert.strictEqual(actual, array);
        assert.deepEqual(array, [__num_top__]);
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var array = [
            1,
            __num_top__,
            3,
            4
        ];
        delete array[__num_top__];
        delete array[3];
        pull(array, [__num_top__]);
        assert.notOk('0' in array);
        assert.notOk('2' in array);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array = [
            1,
            2,
            3
        ];
        delete array[__num_top__];
        pull(array, [undefined]);
        assert.deepEqual(array, [
            1,
            3
        ]);
    });
    QUnit.test('`_.' + methodName + '` should match `NaN`', function (assert) {
        assert.expect(1);
        var array = [
            __num_top__,
            NaN,
            3,
            NaN
        ];
        pull(array, [NaN]);
        assert.deepEqual(array, [
            1,
            3
        ]);
    });
});