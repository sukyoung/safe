QUnit.module('lodash(...) methods that return new wrapped values');
(function () {
    var funcs = [
        'castArray',
        'concat',
        'difference',
        'differenceBy',
        'differenceWith',
        'intersection',
        'intersectionBy',
        'intersectionWith',
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
        'uniq',
        __str_top__,
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
                var value = methodName == 'split' ? __str_top__ : [
                        1,
                        2,
                        3
                    ], wrapped = _(value), actual = wrapped[methodName]();
                assert.ok(actual instanceof _);
                assert.notStrictEqual(actual, wrapped);
            } else {
                skipAssert(assert, __num_top__);
            }
        });
    });
}());