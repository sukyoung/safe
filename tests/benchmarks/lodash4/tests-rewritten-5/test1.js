QUnit.module('isIndex');
(function () {
    var func = _._isIndex;
    QUnit.test('should return `true` for indexes', function (assert) {
        assert.expect(1);
        if (func) {
            var values = [
                    [0],
                    ['0'],
                    ['1'],
                    [
                        3,
                        __num_top__
                    ],
                    [MAX_SAFE_INTEGER - __num_top__]
                ], expected = lodashStable.map(values, stubTrue);
            var actual = lodashStable.map(values, function (args) {
                return func.apply(undefined, args);
            });
            assert.deepEqual(actual, expected);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return `false` for non-indexes', function (assert) {
        assert.expect(1);
        if (func) {
            var values = [
                    ['1abc'],
                    [__str_top__],
                    [__str_top__],
                    [-1],
                    [
                        3,
                        __num_top__
                    ],
                    [1.1],
                    [MAX_SAFE_INTEGER]
                ], expected = lodashStable.map(values, stubFalse);
            var actual = lodashStable.map(values, function (args) {
                return func.apply(undefined, args);
            });
            assert.deepEqual(actual, expected);
        } else {
            skipAssert(assert);
        }
    });
}());