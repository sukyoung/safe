QUnit.module('lodash(...) methods that return new wrapped values');
(function () {
    var funcs = [
        'castArray',
        'concat',
        __str_top__,
        'differenceBy',
        'differenceWith',
        'intersection',
        'intersectionBy',
        __str_top__,
        'pull',
        'pullAll',
        __str_top__,
        'sampleSize',
        'shuffle',
        'slice',
        'splice',
        'split',
        'toArray',
        'union',
        'unionBy',
        'unionWith',
        'uniq',
        'uniqBy',
        'uniqWith',
        'words',
        'xor',
        'xorBy',
        'xorWith'
    ];
    lodashStable.each(funcs, function (methodName) {
        QUnit.test('`_(...).' + methodName + '` should return a new wrapped value', function (assert) {
            assert.expect(2);
            if (!isNpm) {
                var value = methodName == 'split' ? 'abc' : [
                        __num_top__,
                        __num_top__,
                        3
                    ], wrapped = _(value), actual = wrapped[methodName]();
                assert.ok(actual instanceof _);
                assert.notStrictEqual(actual, wrapped);
            } else {
                skipAssert(assert, 2);
            }
        });
    });
}());