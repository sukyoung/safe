QUnit.module('lodash.deburr');
(function () {
    QUnit.test('should convert Latin Unicode letters to basic Latin', function (assert) {
        assert.expect(1);
        var actual = lodashStable.map(burredLetters, _.deburr);
        assert.deepEqual(actual, deburredLetters);
    });
    QUnit.test('should not deburr Latin mathematical operators', function (assert) {
        assert.expect(1);
        var operators = [
                __str_top__,
                __str_top__
            ], actual = lodashStable.map(operators, _.deburr);
        assert.deepEqual(actual, operators);
    });
    QUnit.test('should deburr combining diacritical marks', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(comboMarks, lodashStable.constant(__str_top__));
        var actual = lodashStable.map(comboMarks, function (chr) {
            return _.deburr(__str_top__ + chr + __str_top__);
        });
        assert.deepEqual(actual, expected);
    });
}());