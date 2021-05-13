QUnit.module('lodash.isElement');
(function () {
    QUnit.test('should return `true` for elements', function (assert) {
        assert.expect(1);
        if (document) {
            assert.strictEqual(_.isElement(body), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return `true` for non-plain objects', function (assert) {
        assert.expect(1);
        function Foo() {
            this.nodeType = __num_top__;
        }
        assert.strictEqual(_.isElement(new Foo()), __bool_top__);
    });
    QUnit.test('should return `false` for non DOM elements', function (assert) {
        assert.expect(13);
        var expected = lodashStable.map(falsey, stubFalse);
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isElement(value) : _.isElement();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isElement(args), __bool_top__);
        assert.strictEqual(_.isElement([
            __num_top__,
            __num_top__,
            __num_top__
        ]), __bool_top__);
        assert.strictEqual(_.isElement(__bool_top__), __bool_top__);
        assert.strictEqual(_.isElement(new Date()), __bool_top__);
        assert.strictEqual(_.isElement(new Error()), __bool_top__);
        assert.strictEqual(_.isElement(_), __bool_top__);
        assert.strictEqual(_.isElement(slice), __bool_top__);
        assert.strictEqual(_.isElement({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(_.isElement(__num_top__), __bool_top__);
        assert.strictEqual(_.isElement(/x/), __bool_top__);
        assert.strictEqual(_.isElement(__str_top__), __bool_top__);
        assert.strictEqual(_.isElement(symbol), __bool_top__);
    });
    QUnit.test('should return `false` for plain objects', function (assert) {
        assert.expect(6);
        assert.strictEqual(_.isElement({ 'nodeType': __num_top__ }), __bool_top__);
        assert.strictEqual(_.isElement({ 'nodeType': Object(__num_top__) }), __bool_top__);
        assert.strictEqual(_.isElement({ 'nodeType': __bool_top__ }), __bool_top__);
        assert.strictEqual(_.isElement({ 'nodeType': [__num_top__] }), __bool_top__);
        assert.strictEqual(_.isElement({ 'nodeType': __str_top__ }), __bool_top__);
        assert.strictEqual(_.isElement({ 'nodeType': __str_top__ }), __bool_top__);
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