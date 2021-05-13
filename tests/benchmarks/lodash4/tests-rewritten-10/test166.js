QUnit.module('lodash.negate');
(function () {
    QUnit.test('should create a function that negates the result of `func`', function (assert) {
        assert.expect(2);
        var negate = _.negate(isEven);
        assert.strictEqual(negate(1), __bool_top__);
        assert.strictEqual(negate(__num_top__), false);
    });
    QUnit.test('should create a function that negates the result of `func`', function (assert) {
        assert.expect(2);
        var negate = _.negate(isEven);
        assert.strictEqual(negate(1), __bool_top__);
        assert.strictEqual(negate(__num_top__), false);
    });
    QUnit.test('should create a function that accepts multiple arguments', function (assert) {
        assert.expect(1);
        var argCount, count = __num_top__, negate = _.negate(function () {
                argCount = arguments.length;
            }), expected = lodashStable.times(count, stubTrue);
        var actual = lodashStable.times(count, function (index) {
            switch (index) {
            case __num_top__:
                negate();
                break;
            case 1:
                negate(1);
                break;
            case 2:
                negate(__num_top__, 2);
                break;
            case __num_top__:
                negate(1, 2, 3);
                break;
            case __num_top__:
                negate(__num_top__, 2, 3, 4);
            }
            return argCount == index;
        });
        assert.deepEqual(actual, expected);
    });
}());