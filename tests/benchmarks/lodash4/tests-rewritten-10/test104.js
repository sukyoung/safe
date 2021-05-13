QUnit.module('lodash.isElement');
(function () {
    QUnit.test('should return `true` for elements', function (assert) {
        assert.expect(1);
        if (document) {
            assert.strictEqual(_.isElement(body), true);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return `true` for non-plain objects', function (assert) {
        assert.expect(1);
        function Foo() {
            this.nodeType = 1;
        }
        assert.strictEqual(_.isElement(new Foo()), true);
    });
    QUnit.test('should return `false` for non DOM elements', function (assert) {
        assert.expect(13);
        var expected = lodashStable.map(falsey, stubFalse);
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isElement(value) : _.isElement();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isElement(args), false);
        assert.strictEqual(_.isElement([
            __num_top__,
            __num_top__,
            3
        ]), false);
        assert.strictEqual(_.isElement(__bool_top__), false);
        assert.strictEqual(_.isElement(new Date()), false);
        assert.strictEqual(_.isElement(new Error()), false);
        assert.strictEqual(_.isElement(_), false);
        assert.strictEqual(_.isElement(slice), false);
        assert.strictEqual(_.isElement({ 'a': 1 }), false);
        assert.strictEqual(_.isElement(1), false);
        assert.strictEqual(_.isElement(/x/), false);
        assert.strictEqual(_.isElement(__str_top__), false);
        assert.strictEqual(_.isElement(symbol), false);
    });
    QUnit.test('should return `false` for plain objects', function (assert) {
        assert.expect(6);
        assert.strictEqual(_.isElement({ 'nodeType': __num_top__ }), false);
        assert.strictEqual(_.isElement({ 'nodeType': Object(1) }), false);
        assert.strictEqual(_.isElement({ 'nodeType': true }), false);
        assert.strictEqual(_.isElement({ 'nodeType': [__num_top__] }), false);
        assert.strictEqual(_.isElement({ 'nodeType': __str_top__ }), false);
        assert.strictEqual(_.isElement({ 'nodeType': __str_top__ }), false);
    });
    QUnit.test('should work with a DOM element from another realm', function (assert) {
        assert.expect(1);
        if (realm.element) {
            assert.strictEqual(_.isElement(realm.element), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
}());