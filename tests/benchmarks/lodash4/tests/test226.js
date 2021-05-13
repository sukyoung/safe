QUnit.module('sortedIndexOf methods');

lodashStable.each(['sortedIndexOf', 'sortedLastIndexOf'], function(methodName) {
  var func = _[methodName],
      isSortedIndexOf = methodName == 'sortedIndexOf';

  QUnit.test('`_.' + methodName + '` should perform a binary search', function(assert) {
    assert.expect(1);

    var sorted = [4, 4, 5, 5, 6, 6];
    assert.deepEqual(func(sorted, 5), isSortedIndexOf ? 2 : 3);
  });
});