QUnit.module('lodash.fromPairs');
(function () {
    QUnit.test('should accept a two dimensional array', function (assert) {
        assert.expect(1);
        var array = [
                [
                    __str_top__,
                    __num_top__
                ],
                [
                    __str_top__,
                    __num_top__
                ]
            ], object = {
                'a': __num_top__,
                'b': __num_top__
            }, actual = _.fromPairs(array);
        assert.deepEqual(actual, object);
    });
    QUnit.test('should accept a falsey `array`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, stubObject);
        var actual = lodashStable.map(falsey, function (array, index) {
            try {
                return index ? _.fromPairs(array) : _.fromPairs();
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should not support deep paths', function (assert) {
        assert.expect(1);
        var actual = _.fromPairs([[
                __str_top__,
                __num_top__
            ]]);
        assert.deepEqual(actual, { 'a.b': __num_top__ });
    });
    QUnit.test('should support consuming the return value of `_.toPairs`', function (assert) {
        assert.expect(1);
        var object = { 'a.b': __num_top__ };
        assert.deepEqual(_.fromPairs(_.toPairs(object)), object);
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var array = lodashStable.times(LARGE_ARRAY_SIZE, function (index) {
                return [
                    __str_top__ + index,
                    index
                ];
            });
            var actual = _(array).fromPairs().map(square).filter(isEven).take().value();
            assert.deepEqual(actual, _.take(_.filter(_.map(_.fromPairs(array), square), isEven)));
        } else {
            skipAssert(assert);
        }
    });
}());