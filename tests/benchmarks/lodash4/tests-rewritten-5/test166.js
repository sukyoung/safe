QUnit.module('lodash.negate');
(function () {
    QUnit.test('should create a function that negates the result of `func`', function (assert) {
        assert.expect(2);
        var negate = _.negate(isEven);
        assert.strictEqual(negate(1), true);
        assert.strictEqual(negate(2), false);
    });
    QUnit.test('should create a function that negates the result of `func`', function (assert) {
        assert.expect(2);
        var negate = _.negate(isEven);
        assert.strictEqual(negate(1), true);
        assert.strictEqual(negate(__num_top__), __bool_top__);
    });
    QUnit.test('should create a function that accepts multiple arguments', function (assert) {
        assert.expect(1);
        var argCount, count = 5, negate = _.negate(function () {
                argCount = arguments.length;
            }), expected = lodashStable.times(count, stubTrue);
        var actual = lodashStable.times(count, function (index) {
            switch (index) {
            case 0:
                negate();
                break;
            case 1:
                negate(__num_top__);
                break;
            case 2:
                negate(1, 2);
                break;
            case __num_top__:
                negate(1, 2, 3);
                break;
            case 4:
                negate(1, __num_top__, 3, 4);
            }
            return argCount == index;
        });
        assert.deepEqual(actual, expected);
    });
}());