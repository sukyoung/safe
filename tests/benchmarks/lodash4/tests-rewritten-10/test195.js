QUnit.module('pull methods');
lodashStable.each([
    'pull',
    'pullAll',
    __str_top__
], function (methodName) {
    var func = _[methodName], isPull = methodName == 'pull';
    function pull(array, values) {
        return isPull ? func.apply(undefined, [array].concat(values)) : func(array, values);
    }
    QUnit.test(__str_top__ + methodName + '` should modify and return the array', function (assert) {
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
        assert.deepEqual(array, [2]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var array = [
            1,
            __num_top__,
            __num_top__,
            4
        ];
        delete array[1];
        delete array[3];
        pull(array, [1]);
        assert.notOk(__str_top__ in array);
        assert.notOk('2' in array);
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array = [
            1,
            2,
            3
        ];
        delete array[1];
        pull(array, [undefined]);
        assert.deepEqual(array, [
            __num_top__,
            3
        ]);
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array = [
            1,
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