QUnit.module('lodash(...) methods that return new wrapped values');
(function () {
    var funcs = [
        'castArray',
        __str_top__,
        __str_top__,
        'differenceBy',
        __str_top__,
        'intersection',
        'intersectionBy',
        __str_top__,
        'pull',
        'pullAll',
        'pullAt',
        'sampleSize',
        'shuffle',
        'slice',
        __str_top__,
        'split',
        'toArray',
        __str_top__,
        'unionBy',
        'unionWith',
        __str_top__,
        'uniqBy',
        'uniqWith',
        'words',
        'xor',
        __str_top__,
        'xorWith'
    ];
    lodashStable.each(funcs, function (methodName) {
        QUnit.test('`_(...).' + methodName + __str_top__, function (assert) {
            assert.expect(2);
            if (!isNpm) {
                var value = methodName == __str_top__ ? 'abc' : [
                        1,
                        2,
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