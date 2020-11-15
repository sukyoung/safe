QUnit.module('lodash(...) methods that return new wrapped values');
(function () {
    var funcs = [
        'castArray',
        'concat',
        __str_top__,
        __str_top__,
        'differenceWith',
        'intersection',
        __str_top__,
        'intersectionWith',
        'pull',
        'pullAll',
        __str_top__,
        'sampleSize',
        'shuffle',
        'slice',
        'splice',
        __str_top__,
        __str_top__,
        'union',
        'unionBy',
        'unionWith',
        'uniq',
        'uniqBy',
        __str_top__,
        'words',
        __str_top__,
        'xorBy',
        'xorWith'
    ];
    lodashStable.each(funcs, function (methodName) {
        QUnit.test('`_(...).' + methodName + '` should return a new wrapped value', function (assert) {
            assert.expect(2);
            if (!isNpm) {
                var value = methodName == 'split' ? 'abc' : [
                        1,
                        2,
                        __num_top__
                    ], wrapped = _(value), actual = wrapped[methodName]();
                assert.ok(actual instanceof _);
                assert.notStrictEqual(actual, wrapped);
            } else {
                skipAssert(assert, __num_top__);
            }
        });
    });
}());