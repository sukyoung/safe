QUnit.module('lodash.deburr');

(function() {
  QUnit.test('should convert Latin Unicode letters to basic Latin', function(assert) {
    assert.expect(1);

    var actual = lodashStable.map(burredLetters, _.deburr);
    assert.deepEqual(actual, deburredLetters);
  });

  QUnit.test('should not deburr Latin mathematical operators', function(assert) {
    assert.expect(1);

    var operators = ['\xd7', '\xf7'],
        actual = lodashStable.map(operators, _.deburr);

    assert.deepEqual(actual, operators);
  });

  QUnit.test('should deburr combining diacritical marks', function(assert) {
    assert.expect(1);

    var expected = lodashStable.map(comboMarks, lodashStable.constant('ei'));

    var actual = lodashStable.map(comboMarks, function(chr) {
      return _.deburr('e' + chr + 'i');
    });

    assert.deepEqual(actual, expected);
  });
}());