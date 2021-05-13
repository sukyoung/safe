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
        'splice',
        'split',
        'toArray',
        'union',
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
                var value = methodName == 'split' ? 'abc' : [
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