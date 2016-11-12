/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

function __saveDump(data, filename){
    if(!data) {
        console.error('Console.save: No data')
        return;
    }

    if(!filename) filename = 'console.json'

    if(typeof data === "object"){
        data = JSON.stringify(data, undefined, 4)
    }

    var blob = new Blob([data], {type: 'text/json'}),
        e    = document.createEvent('MouseEvents'),
        a    = document.createElement('a')

    a.download = filename
    a.href = window.URL.createObjectURL(blob)
    a.dataset.downloadurl =  ['text/json', a.download, a.href].join(':')
    e.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null)
    a.dispatchEvent(e)
}

function __mk_lex_obj(pfid, fid, span) {
    __lexicals[fid] = {
        'pfid': pfid,
        'map': {},
        'update': function(variable, value) {
            this.map[variable] = value;
        },
        'getValue': function(variable) {
            return this.map[variable];
        },
		'span': span
    };
};

function __check_scope(fid, variable) {
    if (fid === 1)
        return 1;
    var lex_obj = __lexicals[fid];
    if (lex_obj.map.hasOwnProperty(variable))
        return fid;
    return __check_scope(lex_obj.pfid, variable);
};

function __dec_lex(fid, variable, initValue) {
    var lex_obj = __lexicals[fid];
    lex_obj.update(variable, initValue);
};

function __st_lex(fid, variable, value) {
    var target_fid = __check_scope(fid, variable);
    var lex_obj = __lexicals[target_fid];
    lex_obj.update(variable, value);
    return value;
};

function __ld_lex(fid, variable) {
    var target_fid = __check_scope(fid, variable);
    var lex_obj = __lexicals[target_fid];
    return lex_obj.getValue(variable);
};

function __newFid() {
    return __funcNum++;
};

var __funcNum = 0;
var __lexicals = {};
var __fid = __newFid();

/* JSED Object */
var __JSED = {};
(function(){
	
	var locF = "[[loc]]";
	var spanF = "__span";
	var global = getGlobal();
	var interP = "[[";
	var interPS = "[[";
	var interPE = "]]";
	var loc = 1;
	var exceptList = [document.navigator, DOMStringMap];
	var instMember = ['__mk_lex_obj', '__check_scope', '__dec_lex', '__st_lex', '__ld_lex', '__newFid', '__funcNum', '__fid', '__span', '__loc', '__dump', '_outscope','__addLoadEvent','__JSED','__event_info','__lexicals','__real_add_event_fun','__real_remove_event_fun', '__printed', '__bind_info', '__real_bind_fun', '__real_interval_fun', '__real_clearInt_fun', '__real_timeout_fun', '__real_clearTim_fun', '__silentAddEventListener', '__real_add_eve_fun', '__real_remove_eve_fun', '[[loc]]','[[printed]]', '__saveDump'];
	var preDefNameList = [];
	var PREDLOC_DEBUG = false;
	
	var nExtList = {
		'list': [],
		'add': function(_trip, undefined){
			
			if((_trip instanceof triple) == false)
				throw "# nExtList can accept only one type, triple.";
			var check = false;
			
			for(var i=0; i<this.list.length; i++){
				var o = this.list[i].fst;
				var p = this.list[i].snd;
				if(o == _trip.fst && p == _trip.snd){
					this.list[i].thd = _trip.thd;
                    check = true;
                }
			}
			if(check === false)
				this.list.push(_trip);
		},
		'get': function(_obj, _prop, undefined){
			for(var i=0; i<this.list.length; i++){
				var o = this.list[i].fst;
				var p = this.list[i].snd;
				if(o === _obj && p === _prop)
					return this.list[i].thd;
			}
			return undefined;
		},
		'getProps': function(_obj){
			var props = [];
			for(var i=0; i<this.list.length; i++){
				var o = this.list[i].fst;
				var p = this.list[i].snd;
				if(o === _obj)
					props.push(p);
			}
			return props;
		}
	};
	
	// a queue structure used by JSEDLocMembers to avoid recursion 
	var queue = {
	    'list': [],
	    'add': function(obj) {
	        this.list[this.list.length] = obj;
	    },
	    'poll': function() {
	        if (this.list.length == 0)
	            return null;
	        var ele = this.list[0];
			this.list.splice(0,1);
	        return ele;
	    },
	    'size': function() {
	        return this.list.length;
	    }
	};


	/* helper functions */
	// return global object
	function getGlobal() {
		return (function() {
			return this;
		})();
	};
	
	//
	function isGlobalExcepted(_o, _prop){
        return false;
	};
	
	// triple data storage
	function triple(_fst, _snd, _thd){
		this.fst = _fst;
		this.snd = _snd;
		this.thd = _thd;
	};
	
	// pair data storage
	function pair(_fst, _snd){
		this.fst = _fst;
		this.snd = _snd;
	};
	
	// 
	function native(_obj, _name){
		putPropValue(_obj,interPS + "funcname" + interPE,_name);	
	};
	
	//
	function init_native_name(){
		native(global.escape,'escape');
		native(global.unescape,'unescape');
		native(Map,'Map');
		native(SyntaxError,'SyntaxError');
		native(Int16Array,'Int16Array');
		native(Promise,'Promise');
		native(Date,'Date');
		native(Number,'Number');
		native(eval,'eval');
		native(encodeURI,'encodeURI');
		native(Uint8Array,'Uint8Array');
		native(Boolean,'Boolean');
		native(Error,'Error');
		native(Uint16Array,'Uint16Array');
		native(Int8Array,'Int8Array');
		native(RangeError,'RangeError');
		native(WeakMap,'WeakMap');
		native(Symbol,'Symbol');
		native(Float64Array,'Float64Array');
		native(URIError,'URIError');
		native(Uint8ClampedArray,'Uint8ClampedArray');
		native(Uint32Array,'Uint32Array');
		native(Int32Array,'Int32Array');
		native(isFinite,'isFinite');
		native(TypeError,'TypeError');
		native(isNaN,'isNaN');
		native(ArrayBuffer,'ArrayBuffer');
		native(decodeURIComponent,'decodeURIComponent');
		native(decodeURI,'decodeURI');
		native(DataView,'DataView');
		native(parseFloat,'parseFloat');
		native(parseInt,'parseInt');
		native(escape,'escape');
		native(String,'String');
		native(Function,'Function');
		native(Object,'Object');
		native(EvalError,'EvalError');
		native(Set,'Set');
		native(Float32Array,'Float32Array');
		native(WeakSet,'WeakSet');
		native(Array,'Array');
		native(RegExp,'RegExp');
		native(ReferenceError,'ReferenceError');
		native(encodeURIComponent,'encodeURIComponent');
		native(webkitOfflineAudioContext,'webkitOfflineAudioContext');
		native(webkitAudioContext,'webkitAudioContext');
		native(webkitSpeechRecognitionEvent,'webkitSpeechRecognitionEvent');
		native(webkitSpeechRecognitionError,'webkitSpeechRecognitionError');
		native(webkitSpeechRecognition,'webkitSpeechRecognition');
		native(webkitSpeechGrammarList,'webkitSpeechGrammarList');
		native(webkitSpeechGrammar,'webkitSpeechGrammar');
		native(webkitRTCPeerConnection,'webkitRTCPeerConnection');
		native(ServiceWorkerRegistration,'ServiceWorkerRegistration');
		native(ServiceWorkerContainer,'ServiceWorkerContainer');
		native(ServiceWorker,'ServiceWorker');
		native(ScreenOrientation,'ScreenOrientation');
		native(Notification,'Notification');
		native(MediaSource,'MediaSource');
		native(BatteryManager,'BatteryManager');
		native(webkitMediaStream,'webkitMediaStream');
		native(webkitIDBTransaction,'webkitIDBTransaction');
		native(webkitIDBRequest,'webkitIDBRequest');
		native(webkitIDBObjectStore,'webkitIDBObjectStore');
		native(webkitIDBKeyRange,'webkitIDBKeyRange');
		native(webkitIDBIndex,'webkitIDBIndex');
		native(webkitIDBFactory,'webkitIDBFactory');
		native(webkitIDBDatabase,'webkitIDBDatabase');
		native(webkitIDBCursor,'webkitIDBCursor');
		native(WebSocket,'WebSocket');
		native(TextEncoder,'TextEncoder');
		native(TextDecoder,'TextDecoder');
		native(SpeechSynthesisUtterance,'SpeechSynthesisUtterance');
		native(SpeechSynthesisEvent,'SpeechSynthesisEvent');
		native(RTCSessionDescription,'RTCSessionDescription');
		native(RTCIceCandidate,'RTCIceCandidate');
		native(MediaStreamTrack,'MediaStreamTrack');
		native(MediaStreamEvent,'MediaStreamEvent');
		native(IDBVersionChangeEvent,'IDBVersionChangeEvent');
		native(IDBOpenDBRequest,'IDBOpenDBRequest');
		native(IDBCursorWithValue,'IDBCursorWithValue');
		native(GamepadEvent,'GamepadEvent');
		native(Gamepad,'Gamepad');
		native(DeviceOrientationEvent,'DeviceOrientationEvent');
		native(DeviceMotionEvent,'DeviceMotionEvent');
		native(CryptoKey,'CryptoKey');
		native(CloseEvent,'CloseEvent');
		native(WaveShaperNode,'WaveShaperNode');
		native(ScriptProcessorNode,'ScriptProcessorNode');
		native(PeriodicWave,'PeriodicWave');
		native(OscillatorNode,'OscillatorNode');
		native(OfflineAudioCompletionEvent,'OfflineAudioCompletionEvent');
		native(MediaStreamAudioSourceNode,'MediaStreamAudioSourceNode');
		native(MediaStreamAudioDestinationNode,'MediaStreamAudioDestinationNode');
		native(MediaElementAudioSourceNode,'MediaElementAudioSourceNode');
		native(GainNode,'GainNode');
		native(DynamicsCompressorNode,'DynamicsCompressorNode');
		native(DelayNode,'DelayNode');
		native(ConvolverNode,'ConvolverNode');
		native(ChannelSplitterNode,'ChannelSplitterNode');
		native(ChannelMergerNode,'ChannelMergerNode');
		native(BiquadFilterNode,'BiquadFilterNode');
		native(AudioProcessingEvent,'AudioProcessingEvent');
		native(AudioParam,'AudioParam');
		native(AudioNode,'AudioNode');
		native(AudioListener,'AudioListener');
		native(AudioDestinationNode,'AudioDestinationNode');
		native(AudioBufferSourceNode,'AudioBufferSourceNode');
		native(AudioBuffer,'AudioBuffer');
		native(AnalyserNode,'AnalyserNode');
		native(XSLTProcessor,'XSLTProcessor');
		native(SharedWorker,'SharedWorker');
		native(SVGTransformList,'SVGTransformList');
		native(SVGTransform,'SVGTransform');
		native(SVGStringList,'SVGStringList');
		native(SVGRenderingIntent,'SVGRenderingIntent');
		native(SVGPreserveAspectRatio,'SVGPreserveAspectRatio');
		native(SVGPointList,'SVGPointList');
		native(SVGPathSegMovetoRel,'SVGPathSegMovetoRel');
		native(SVGPathSegMovetoAbs,'SVGPathSegMovetoAbs');
		native(SVGPathSegList,'SVGPathSegList');
		native(SVGPathSegLinetoVerticalRel,'SVGPathSegLinetoVerticalRel');
		native(SVGPathSegLinetoVerticalAbs,'SVGPathSegLinetoVerticalAbs');
		native(SVGPathSegLinetoRel,'SVGPathSegLinetoRel');
		native(SVGPathSegLinetoHorizontalRel,'SVGPathSegLinetoHorizontalRel');
		native(SVGPathSegLinetoHorizontalAbs,'SVGPathSegLinetoHorizontalAbs');
		native(SVGPathSegLinetoAbs,'SVGPathSegLinetoAbs');
		native(SVGPathSegCurvetoQuadraticSmoothRel,'SVGPathSegCurvetoQuadraticSmoothRel');
		native(SVGPathSegCurvetoQuadraticSmoothAbs,'SVGPathSegCurvetoQuadraticSmoothAbs');
		native(SVGPathSegCurvetoQuadraticRel,'SVGPathSegCurvetoQuadraticRel');
		native(SVGPathSegCurvetoQuadraticAbs,'SVGPathSegCurvetoQuadraticAbs');
		native(SVGPathSegCurvetoCubicSmoothRel,'SVGPathSegCurvetoCubicSmoothRel');
		native(SVGPathSegCurvetoCubicSmoothAbs,'SVGPathSegCurvetoCubicSmoothAbs');
		native(SVGPathSegCurvetoCubicRel,'SVGPathSegCurvetoCubicRel');
		native(SVGPathSegCurvetoCubicAbs,'SVGPathSegCurvetoCubicAbs');
		native(SVGPathSeg,'SVGPathSeg');
		native(SVGPathSegClosePath,'SVGPathSegClosePath');
		native(SVGPathSegArcRel,'SVGPathSegArcRel');
		native(SVGPathSegArcAbs,'SVGPathSegArcAbs');
		native(SVGNumberList,'SVGNumberList');
		native(SVGLengthList,'SVGLengthList');
		native(SVGLength,'SVGLength');
		native(SVGAnimatedTransformList,'SVGAnimatedTransformList');
		native(SVGAnimatedString,'SVGAnimatedString');
		native(SVGAnimatedRect,'SVGAnimatedRect');
		native(SVGAnimatedPreserveAspectRatio,'SVGAnimatedPreserveAspectRatio');
		native(SVGAnimatedNumberList,'SVGAnimatedNumberList');
		native(SVGAnimatedNumber,'SVGAnimatedNumber');
		native(SVGAnimatedLengthList,'SVGAnimatedLengthList');
		native(SVGAnimatedLength,'SVGAnimatedLength');
		native(SVGAnimatedInteger,'SVGAnimatedInteger');
		native(SVGAnimatedEnumeration,'SVGAnimatedEnumeration');
		native(SVGAnimatedBoolean,'SVGAnimatedBoolean');
		native(SVGAnimatedAngle,'SVGAnimatedAngle');
		native(MediaKeyEvent,'MediaKeyEvent');
		native(MediaKeyError,'MediaKeyError');
		native(HTMLPictureElement,'HTMLPictureElement');
		native(TimeRanges,'TimeRanges');
		native(MediaError,'MediaError');
		native(HTMLVideoElement,'HTMLVideoElement');
		native(HTMLSourceElement,'HTMLSourceElement');
		native(HTMLMediaElement,'HTMLMediaElement');
		native(Audio,'Audio');
		native(HTMLAudioElement,'HTMLAudioElement');
		native(XPathResult,'XPathResult');
		native(XPathExpression,'XPathExpression');
		native(XPathEvaluator,'XPathEvaluator');
		native(XMLSerializer,'XMLSerializer');
		native(XMLHttpRequestUpload,'XMLHttpRequestUpload');
		native(XMLHttpRequestProgressEvent,'XMLHttpRequestProgressEvent');
		native(XMLHttpRequest,'XMLHttpRequest');
		native(XMLDocument,'XMLDocument');
		native(Worker,'Worker');
		native(Window,'Window');
		native(window.DataTransferItem,'window.DataTransferItem');
		native(window.DataTransferItem.prototype.getAsFile,'window.DataTransferItem.prototype.getAsFile');
		native(window.DataTransferItem.prototype.getAsString,'window.DataTransferItem.prototype.getAsString');
		native(window.DataTransferItem.prototype.webkitGetAsEntry,'window.DataTransferItem.prototype.webkitGetAsEntry');
		native(window.__proto__.fetch,'window.[[proto]].fetch');
		native(WheelEvent,'WheelEvent');
		native(WebKitCSSMatrix,'WebKitCSSMatrix');
		native(WebKitAnimationEvent,'WebKitAnimationEvent');
		native(WebGLUniformLocation,'WebGLUniformLocation');
		native(WebGLTexture,'WebGLTexture');
		native(WebGLShaderPrecisionFormat,'WebGLShaderPrecisionFormat');
		native(WebGLShader,'WebGLShader');
		native(WebGLRenderingContext,'WebGLRenderingContext');
		native(WebGLRenderbuffer,'WebGLRenderbuffer');
		native(WebGLProgram,'WebGLProgram');
		native(WebGLFramebuffer,'WebGLFramebuffer');
		native(WebGLContextEvent,'WebGLContextEvent');
		native(WebGLBuffer,'WebGLBuffer');
		native(WebGLActiveInfo,'WebGLActiveInfo');
		native(ValidityState,'ValidityState');
		native(VTTCue,'VTTCue');
		native(URL,'URL');
		native(UIEvent,'UIEvent');
		native(TreeWalker,'TreeWalker');
		native(TransitionEvent,'TransitionEvent');
		native(TrackEvent,'TrackEvent');
		native(TouchList,'TouchList');
		native(TouchEvent,'TouchEvent');
		native(Touch,'Touch');
		native(TextTrackList,'TextTrackList');
		native(TextTrackCueList,'TextTrackCueList');
		native(TextTrackCue,'TextTrackCue');
		native(TextTrack,'TextTrack');
		native(TextMetrics,'TextMetrics');
		native(TextEvent,'TextEvent');
		native(Text,'Text');
		native(StyleSheetList,'StyleSheetList');
		native(StyleSheet,'StyleSheet');
		native(StorageEvent,'StorageEvent');
		native(Storage,'Storage');
		native(ShadowRoot,'ShadowRoot');
		native(Selection,'Selection');
		native(SecurityPolicyViolationEvent,'SecurityPolicyViolationEvent');
		native(Screen,'Screen');
		native(SVGZoomEvent,'SVGZoomEvent');
		native(SVGViewSpec,'SVGViewSpec');
		native(SVGViewElement,'SVGViewElement');
		native(SVGUseElement,'SVGUseElement');
		native(SVGUnitTypes,'SVGUnitTypes');
		native(SVGTitleElement,'SVGTitleElement');
		native(SVGTextPositioningElement,'SVGTextPositioningElement');
		native(SVGTextPathElement,'SVGTextPathElement');
		native(SVGTextElement,'SVGTextElement');
		native(SVGTextContentElement,'SVGTextContentElement');
		native(SVGTSpanElement,'SVGTSpanElement');
		native(SVGSymbolElement,'SVGSymbolElement');
		native(SVGSwitchElement,'SVGSwitchElement');
		native(SVGStyleElement,'SVGStyleElement');
		native(SVGStopElement,'SVGStopElement');
		native(SVGSetElement,'SVGSetElement');
		native(SVGScriptElement,'SVGScriptElement');
		native(SVGSVGElement,'SVGSVGElement');
		native(SVGRectElement,'SVGRectElement');
		native(SVGRect,'SVGRect');
		native(SVGRadialGradientElement,'SVGRadialGradientElement');
		native(SVGPolylineElement,'SVGPolylineElement');
		native(SVGPolygonElement,'SVGPolygonElement');
		native(SVGPoint,'SVGPoint');
		native(SVGPatternElement,'SVGPatternElement');
		native(SVGPathElement,'SVGPathElement');
		native(SVGNumber,'SVGNumber');
		native(SVGMetadataElement,'SVGMetadataElement');
		native(SVGMatrix,'SVGMatrix');
		native(SVGMaskElement,'SVGMaskElement');
		native(SVGMarkerElement,'SVGMarkerElement');
		native(SVGMPathElement,'SVGMPathElement');
		native(SVGLinearGradientElement,'SVGLinearGradientElement');
		native(SVGLineElement,'SVGLineElement');
		native(SVGImageElement,'SVGImageElement');
		native(SVGGraphicsElement,'SVGGraphicsElement');
		native(SVGGradientElement,'SVGGradientElement');
		native(SVGGeometryElement,'SVGGeometryElement');
		native(SVGGElement,'SVGGElement');
		native(SVGForeignObjectElement,'SVGForeignObjectElement');
		native(SVGFilterElement,'SVGFilterElement');
		native(SVGFETurbulenceElement,'SVGFETurbulenceElement');
		native(SVGFETileElement,'SVGFETileElement');
		native(SVGFESpotLightElement,'SVGFESpotLightElement');
		native(SVGFESpecularLightingElement,'SVGFESpecularLightingElement');
		native(SVGFEPointLightElement,'SVGFEPointLightElement');
		native(SVGFEOffsetElement,'SVGFEOffsetElement');
		native(SVGFEMorphologyElement,'SVGFEMorphologyElement');
		native(SVGFEMergeNodeElement,'SVGFEMergeNodeElement');
		native(SVGFEMergeElement,'SVGFEMergeElement');
		native(SVGFEImageElement,'SVGFEImageElement');
		native(SVGFEGaussianBlurElement,'SVGFEGaussianBlurElement');
		native(SVGFEFuncRElement,'SVGFEFuncRElement');
		native(SVGFEFuncGElement,'SVGFEFuncGElement');
		native(SVGFEFuncBElement,'SVGFEFuncBElement');
		native(SVGFEFuncAElement,'SVGFEFuncAElement');
		native(SVGFEFloodElement,'SVGFEFloodElement');
		native(SVGFEDropShadowElement,'SVGFEDropShadowElement');
		native(SVGFEDistantLightElement,'SVGFEDistantLightElement');
		native(SVGFEDisplacementMapElement,'SVGFEDisplacementMapElement');
		native(SVGFEDiffuseLightingElement,'SVGFEDiffuseLightingElement');
		native(SVGFEConvolveMatrixElement,'SVGFEConvolveMatrixElement');
		native(SVGFECompositeElement,'SVGFECompositeElement');
		native(SVGFEComponentTransferElement,'SVGFEComponentTransferElement');
		native(SVGFEColorMatrixElement,'SVGFEColorMatrixElement');
		native(SVGFEBlendElement,'SVGFEBlendElement');
		native(SVGEllipseElement,'SVGEllipseElement');
		native(SVGElement,'SVGElement');
		native(SVGDiscardElement,'SVGDiscardElement');
		native(SVGDescElement,'SVGDescElement');
		native(SVGDefsElement,'SVGDefsElement');
		native(SVGCursorElement,'SVGCursorElement');
		native(SVGComponentTransferFunctionElement,'SVGComponentTransferFunctionElement');
		native(SVGClipPathElement,'SVGClipPathElement');
		native(SVGCircleElement,'SVGCircleElement');
		native(SVGAnimationElement,'SVGAnimationElement');
		native(SVGAnimateTransformElement,'SVGAnimateTransformElement');
		native(SVGAnimateMotionElement,'SVGAnimateMotionElement');
		native(SVGAnimateElement,'SVGAnimateElement');
		native(SVGAngle,'SVGAngle');
		native(SVGAElement,'SVGAElement');
		native(Range,'Range');
		native(RadioNodeList,'RadioNodeList');
		native(ProgressEvent,'ProgressEvent');
		native(ProcessingInstruction,'ProcessingInstruction');
		native(PopStateEvent,'PopStateEvent');
		native(Plugin,'Plugin');
		native(PluginArray,'PluginArray');
		native(PerformanceTiming,'PerformanceTiming');
		native(PerformanceResourceTiming,'PerformanceResourceTiming');
		native(PerformanceNavigation,'PerformanceNavigation');
		native(PerformanceMeasure,'PerformanceMeasure');
		native(PerformanceMark,'PerformanceMark');
		native(PerformanceEntry,'PerformanceEntry');
		native(Performance,'Performance');
		native(Path2D,'Path2D');
		native(PageTransitionEvent,'PageTransitionEvent');
		native(NodeList,'NodeList');
		native(NodeIterator,'NodeIterator');
		native(NodeFilter,'NodeFilter');
		native(Node,'Node');
		native(Navigator,'Navigator');
		native(NamedNodeMap,'NamedNodeMap');
		native(MutationRecord,'MutationRecord');
		native(MutationObserver,'MutationObserver');
		native(MutationEvent,'MutationEvent');
		native(MouseEvent,'MouseEvent');
		native(MimeType,'MimeType');
		native(MimeTypeArray,'MimeTypeArray');
		native(MessagePort,'MessagePort');
		native(MessageEvent,'MessageEvent');
		native(MessageChannel,'MessageChannel');
		native(MediaQueryListEvent,'MediaQueryListEvent');
		native(MediaQueryList,'MediaQueryList');
		native(MediaList,'MediaList');
		native(Location,'Location');
		native(KeyboardEvent,'KeyboardEvent');
		native(InputMethodContext,'InputMethodContext');
		native(ImageData,'ImageData');
		native(ImageBitmap,'ImageBitmap');
		native(History,'History');
		native(HashChangeEvent,'HashChangeEvent');
		native(HTMLUnknownElement,'HTMLUnknownElement');
		native(HTMLUListElement,'HTMLUListElement');
		native(HTMLTrackElement,'HTMLTrackElement');
		native(HTMLTitleElement,'HTMLTitleElement');
		native(HTMLTextAreaElement,'HTMLTextAreaElement');
		native(HTMLTemplateElement,'HTMLTemplateElement');
		native(HTMLTableSectionElement,'HTMLTableSectionElement');
		native(HTMLTableRowElement,'HTMLTableRowElement');
		native(HTMLTableElement,'HTMLTableElement');
		native(HTMLTableColElement,'HTMLTableColElement');
		native(HTMLTableCellElement,'HTMLTableCellElement');
		native(HTMLTableCaptionElement,'HTMLTableCaptionElement');
		native(HTMLStyleElement,'HTMLStyleElement');
		native(HTMLSpanElement,'HTMLSpanElement');
		native(HTMLShadowElement,'HTMLShadowElement');
		native(HTMLSelectElement,'HTMLSelectElement');
		native(HTMLScriptElement,'HTMLScriptElement');
		native(HTMLQuoteElement,'HTMLQuoteElement');
		native(HTMLProgressElement,'HTMLProgressElement');
		native(HTMLPreElement,'HTMLPreElement');
		native(HTMLParamElement,'HTMLParamElement');
		native(HTMLParagraphElement,'HTMLParagraphElement');
		native(HTMLOutputElement,'HTMLOutputElement');
		native(HTMLOptionsCollection,'HTMLOptionsCollection');
		native(Option,'Option');
		native(HTMLOptionElement,'HTMLOptionElement');
		native(HTMLOptGroupElement,'HTMLOptGroupElement');
		native(HTMLObjectElement,'HTMLObjectElement');
		native(HTMLOListElement,'HTMLOListElement');
		native(HTMLModElement,'HTMLModElement');
		native(HTMLMeterElement,'HTMLMeterElement');
		native(HTMLMetaElement,'HTMLMetaElement');
		native(HTMLMenuElement,'HTMLMenuElement');
		native(HTMLMarqueeElement,'HTMLMarqueeElement');
		native(HTMLMapElement,'HTMLMapElement');
		native(HTMLLinkElement,'HTMLLinkElement');
		native(HTMLLegendElement,'HTMLLegendElement');
		native(HTMLLabelElement,'HTMLLabelElement');
		native(HTMLLIElement,'HTMLLIElement');
		native(HTMLKeygenElement,'HTMLKeygenElement');
		native(HTMLInputElement,'HTMLInputElement');
		native(Image,'Image');
		native(HTMLImageElement,'HTMLImageElement');
		native(HTMLIFrameElement,'HTMLIFrameElement');
		native(HTMLHtmlElement,'HTMLHtmlElement');
		native(HTMLHeadingElement,'HTMLHeadingElement');
		native(HTMLHeadElement,'HTMLHeadElement');
		native(HTMLHRElement,'HTMLHRElement');
		native(HTMLFrameSetElement,'HTMLFrameSetElement');
		native(HTMLFrameElement,'HTMLFrameElement');
		native(HTMLFormElement,'HTMLFormElement');
		native(HTMLFormControlsCollection,'HTMLFormControlsCollection');
		native(HTMLFontElement,'HTMLFontElement');
		native(HTMLFieldSetElement,'HTMLFieldSetElement');
		native(HTMLEmbedElement,'HTMLEmbedElement');
		native(HTMLElement,'HTMLElement');
		native(HTMLDocument,'HTMLDocument');
		native(HTMLDivElement,'HTMLDivElement');
		native(HTMLDirectoryElement,'HTMLDirectoryElement');
		native(HTMLDialogElement,'HTMLDialogElement');
		native(HTMLDetailsElement,'HTMLDetailsElement');
		native(HTMLDataListElement,'HTMLDataListElement');
		native(HTMLDListElement,'HTMLDListElement');
		native(HTMLContentElement,'HTMLContentElement');
		native(HTMLCollection,'HTMLCollection');
		native(HTMLCanvasElement,'HTMLCanvasElement');
		native(HTMLButtonElement,'HTMLButtonElement');
		native(HTMLBodyElement,'HTMLBodyElement');
		native(HTMLBaseElement,'HTMLBaseElement');
		native(HTMLBRElement,'HTMLBRElement');
		native(HTMLAreaElement,'HTMLAreaElement');
		native(HTMLAppletElement,'HTMLAppletElement');
		native(HTMLAnchorElement,'HTMLAnchorElement');
		native(HTMLAllCollection,'HTMLAllCollection');
		native(FormData,'FormData');
		native(FontFace,'FontFace');
		native(FocusEvent,'FocusEvent');
		native(FileReader,'FileReader');
		native(FileList,'FileList');
		native(FileError,'FileError');
		native(File,'File');
		native(EventTarget,'EventTarget');
		native(EventSource,'EventSource');
		native(Event,'Event');
		native(ErrorEvent,'ErrorEvent');
		native(Element,'Element');
		native(DocumentType,'DocumentType');
		native(DocumentFragment,'DocumentFragment');
		native(Document,'Document');
		native(DataTransferItemList,'DataTransferItemList');
		native(DataTransfer,'DataTransfer');
		native(DOMTokenList,'DOMTokenList');
		native(DOMStringMap,'DOMStringMap');
		native(DOMStringList,'DOMStringList');
		native(DOMSettableTokenList,'DOMSettableTokenList');
		native(DOMParser,'DOMParser');
		native(DOMImplementation,'DOMImplementation');
		native(DOMException,'DOMException');
		native(DOMError,'DOMError');
		native(CustomEvent,'CustomEvent');
		native(CompositionEvent,'CompositionEvent');
		native(Comment,'Comment');
		native(ClipboardEvent,'ClipboardEvent');
		native(ClientRectList,'ClientRectList');
		native(ClientRect,'ClientRect');
		native(CharacterData,'CharacterData');
		native(CanvasRenderingContext2D,'CanvasRenderingContext2D');
		native(CanvasPattern,'CanvasPattern');
		native(CanvasGradient,'CanvasGradient');
		native(CSSViewportRule,'CSSViewportRule');
		native(CSSUnknownRule,'CSSUnknownRule');
		native(CSSSupportsRule,'CSSSupportsRule');
		native(CSSStyleSheet,'CSSStyleSheet');
		native(CSSStyleRule,'CSSStyleRule');
		native(CSSStyleDeclaration,'CSSStyleDeclaration');
		native(CSSRuleList,'CSSRuleList');
		native(CSSRule,'CSSRule');
		native(CSSPageRule,'CSSPageRule');
		native(CSSMediaRule,'CSSMediaRule');
		native(CSSKeyframesRule,'CSSKeyframesRule');
		native(CSSKeyframeRule,'CSSKeyframeRule');
		native(CSSImportRule,'CSSImportRule');
		native(CSSFontFaceRule,'CSSFontFaceRule');
		native(CDATASection,'CDATASection');
		native(Blob,'Blob');
		native(BeforeUnloadEvent,'BeforeUnloadEvent');
		native(BarProp,'BarProp');
		native(AutocompleteErrorEvent,'AutocompleteErrorEvent');
		native(Attr,'Attr');
		native(ApplicationCacheErrorEvent,'ApplicationCacheErrorEvent');
		native(ApplicationCache,'ApplicationCache');
		native(Object.prototype.toString,'Object.prototype.toString');
		native(Object.prototype.toLocaleString,'Object.prototype.toLocaleString');
		native(Object.prototype.valueOf,'Object.prototype.valueOf');
		native(Object.prototype.hasOwnProperty,'Object.prototype.hasOwnProperty');
		native(Object.prototype.isPrototypeOf,'Object.prototype.isPrototypeOf');
		native(Object.prototype.propertyIsEnumerable,'Object.prototype.propertyIsEnumerable');
		native(Object.prototype.__defineGetter__,'Object.prototype.__defineGetter__');
		native(Object.prototype.__lookupGetter__,'Object.prototype.__lookupGetter__');
		native(Object.prototype.__defineSetter__,'Object.prototype.__defineSetter__');
		native(Object.prototype.__lookupSetter__,'Object.prototype.__lookupSetter__');
		native(ApplicationCache.toString,'ApplicationCache.toString');
		native(URL.revokeObjectURL,'URL.revokeObjectURL');
		native(URL.createObjectURL,'URL.createObjectURL');
		native(MediaStreamTrack.getSources,'MediaStreamTrack.getSources');
		native(webkitIDBKeyRange.only,'webkitIDBKeyRange.only');
		native(webkitIDBKeyRange.lowerBound,'webkitIDBKeyRange.lowerBound');
		native(webkitIDBKeyRange.upperBound,'webkitIDBKeyRange.upperBound');
		native(webkitIDBKeyRange.bound,'webkitIDBKeyRange.bound');
		native(MediaSource.isTypeSupported,'MediaSource.isTypeSupported');
		native(Notification.requestPermission,'Notification.requestPermission');
		native(location.replace,'location.replace');
		native(location.assign,'location.assign');
		native(location.reload,'location.reload');
		native(location.toString,'location.toString');
		native(location.valueOf,'location.valueOf');
		native(JSON.parse,'JSON.parse');
		native(JSON.stringify,'JSON.stringify');
		native(Array.isArray,'Array.isArray');
		native(Array.observe,'Array.observe');
		native(Array.unobserve,'Array.unobserve');
		native(Intl.Collator,'Intl.Collator');
		native(Intl.NumberFormat,'Intl.NumberFormat');
		native(Intl.DateTimeFormat,'Intl.DateTimeFormat');
		native(Intl.v8BreakIterator,'Intl.v8BreakIterator');
		native(Date.now,'Date.now');
		native(Object.keys,'Object.keys');
		native(Object.create,'Object.create');
		native(Object.defineProperty,'Object.defineProperty');
		native(Object.defineProperties,'Object.defineProperties');
		native(Object.freeze,'Object.freeze');
		native(Object.getPrototypeOf,'Object.getPrototypeOf');
		native(Object.setPrototypeOf,'Object.setPrototypeOf');
		native(Object.getOwnPropertyDescriptor,'Object.getOwnPropertyDescriptor');
		native(Object.getOwnPropertyNames,'Object.getOwnPropertyNames');
		native(Object.is,'Object.is');
		native(Object.isExtensible,'Object.isExtensible');
		native(Object.isFrozen,'Object.isFrozen');
		native(Object.isSealed,'Object.isSealed');
		native(Object.preventExtensions,'Object.preventExtensions');
		native(Object.seal,'Object.seal');
		native(Object.getOwnPropertySymbols,'Object.getOwnPropertySymbols');
		native(Object.deliverChangeRecords,'Object.deliverChangeRecords');
		native(Object.getNotifier,'Object.getNotifier');
		native(Object.observe,'Object.observe');
		native(Object.unobserve,'Object.unobserve');
		native(String.fromCharCode,'String.fromCharCode');
		native(String.fromCodePoint,'String.fromCodePoint');
		native(String.raw,'String.raw');
		native(ArrayBuffer.isView,'ArrayBuffer.isView');
		native(Symbol.for,'Symbol.for');
		native(Symbol.keyFor,'Symbol.keyFor');
		native(Error.captureStackTrace,'Error.captureStackTrace');
		native(Number.isFinite,'Number.isFinite');
		native(Number.isInteger,'Number.isInteger');
		native(Number.isNaN,'Number.isNaN');
		native(Number.isSafeInteger,'Number.isSafeInteger');
		native(Date.UTC,'Date.UTC');
		native(Date.parse,'Date.parse');
		native(Math.random,'Math.random');
		native(Math.abs,'Math.abs');
		native(Math.acos,'Math.acos');
		native(Math.asin,'Math.asin');
		native(Math.atan,'Math.atan');
		native(Math.ceil,'Math.ceil');
		native(Math.cos,'Math.cos');
		native(Math.exp,'Math.exp');
		native(Math.floor,'Math.floor');
		native(Math.log,'Math.log');
		native(Math.round,'Math.round');
		native(Math.sin,'Math.sin');
		native(Math.sqrt,'Math.sqrt');
		native(Math.tan,'Math.tan');
		native(Math.atan2,'Math.atan2');
		native(Math.pow,'Math.pow');
		native(Math.max,'Math.max');
		native(Math.min,'Math.min');
		native(Math.imul,'Math.imul');
		native(Math.sign,'Math.sign');
		native(Math.trunc,'Math.trunc');
		native(Math.sinh,'Math.sinh');
		native(Math.cosh,'Math.cosh');
		native(Math.tanh,'Math.tanh');
		native(Math.asinh,'Math.asinh');
		native(Math.acosh,'Math.acosh');
		native(Math.atanh,'Math.atanh');
		native(Math.log10,'Math.log10');
		native(Math.log2,'Math.log2');
		native(Math.hypot,'Math.hypot');
		native(Math.fround,'Math.fround');
		native(Math.clz32,'Math.clz32');
		native(Math.cbrt,'Math.cbrt');
		native(Math.log1p,'Math.log1p');
		native(Math.expm1,'Math.expm1');
		native(Promise.defer,'Promise.defer');
		native(Promise.accept,'Promise.accept');
		native(Promise.reject,'Promise.reject');
		native(Promise.all,'Promise.all');
		native(Promise.race,'Promise.race');
		native(Promise.resolve,'Promise.resolve');
		native(Map.prototype.get,'Map.prototype.get');
		native(Map.prototype.set,'Map.prototype.set');
		native(Map.prototype.has,'Map.prototype.has');
		native(Map.prototype.delete,'Map.prototype.delete');
		native(Map.prototype.clear,'Map.prototype.clear');
		native(Map.prototype.forEach,'Map.prototype.forEach');
		native(Map.prototype.entries,'Map.prototype.entries');
		native(Map.prototype.keys,'Map.prototype.keys');
		native(Map.prototype.values,'Map.prototype.values');
		native(Int16Array.prototype.subarray,'Int16Array.prototype.subarray');
		native(Int16Array.prototype.set,'Int16Array.prototype.set');
		native(Int16Array.prototype.entries,'Int16Array.prototype.entries');
		native(Int16Array.prototype.values,'Int16Array.prototype.values');
		native(Int16Array.prototype.keys,'Int16Array.prototype.keys');
		native(Promise.prototype.chain,'Promise.prototype.chain');
		native(Promise.prototype.then,'Promise.prototype.then');
		native(Promise.prototype.catch,'Promise.prototype.catch');
		native(Date.prototype.toString,'Date.prototype.toString');
		native(Date.prototype.toDateString,'Date.prototype.toDateString');
		native(Date.prototype.toTimeString,'Date.prototype.toTimeString');
		native(Date.prototype.toLocaleString,'Date.prototype.toLocaleString');
		native(Date.prototype.toLocaleDateString,'Date.prototype.toLocaleDateString');
		native(Date.prototype.toLocaleTimeString,'Date.prototype.toLocaleTimeString');
		native(Date.prototype.valueOf,'Date.prototype.valueOf');
		native(Date.prototype.getTime,'Date.prototype.getTime');
		native(Date.prototype.getFullYear,'Date.prototype.getFullYear');
		native(Date.prototype.getUTCFullYear,'Date.prototype.getUTCFullYear');
		native(Date.prototype.getMonth,'Date.prototype.getMonth');
		native(Date.prototype.getUTCMonth,'Date.prototype.getUTCMonth');
		native(Date.prototype.getDate,'Date.prototype.getDate');
		native(Date.prototype.getUTCDate,'Date.prototype.getUTCDate');
		native(Date.prototype.getDay,'Date.prototype.getDay');
		native(Date.prototype.getUTCDay,'Date.prototype.getUTCDay');
		native(Date.prototype.getHours,'Date.prototype.getHours');
		native(Date.prototype.getUTCHours,'Date.prototype.getUTCHours');
		native(Date.prototype.getMinutes,'Date.prototype.getMinutes');
		native(Date.prototype.getUTCMinutes,'Date.prototype.getUTCMinutes');
		native(Date.prototype.getSeconds,'Date.prototype.getSeconds');
		native(Date.prototype.getUTCSeconds,'Date.prototype.getUTCSeconds');
		native(Date.prototype.getMilliseconds,'Date.prototype.getMilliseconds');
		native(Date.prototype.getUTCMilliseconds,'Date.prototype.getUTCMilliseconds');
		native(Date.prototype.getTimezoneOffset,'Date.prototype.getTimezoneOffset');
		native(Date.prototype.setTime,'Date.prototype.setTime');
		native(Date.prototype.setMilliseconds,'Date.prototype.setMilliseconds');
		native(Date.prototype.setUTCMilliseconds,'Date.prototype.setUTCMilliseconds');
		native(Date.prototype.setSeconds,'Date.prototype.setSeconds');
		native(Date.prototype.setUTCSeconds,'Date.prototype.setUTCSeconds');
		native(Date.prototype.setMinutes,'Date.prototype.setMinutes');
		native(Date.prototype.setUTCMinutes,'Date.prototype.setUTCMinutes');
		native(Date.prototype.setHours,'Date.prototype.setHours');
		native(Date.prototype.setUTCHours,'Date.prototype.setUTCHours');
		native(Date.prototype.setDate,'Date.prototype.setDate');
		native(Date.prototype.setUTCDate,'Date.prototype.setUTCDate');
		native(Date.prototype.setMonth,'Date.prototype.setMonth');
		native(Date.prototype.setUTCMonth,'Date.prototype.setUTCMonth');
		native(Date.prototype.setFullYear,'Date.prototype.setFullYear');
		native(Date.prototype.setUTCFullYear,'Date.prototype.setUTCFullYear');
		native(Date.prototype.toGMTString,'Date.prototype.toGMTString');
		native(Date.prototype.toUTCString,'Date.prototype.toUTCString');
		native(Date.prototype.getYear,'Date.prototype.getYear');
		native(Date.prototype.setYear,'Date.prototype.setYear');
		native(Date.prototype.toISOString,'Date.prototype.toISOString');
		native(Date.prototype.toJSON,'Date.prototype.toJSON');
		native(Number.prototype.toString,'Number.prototype.toString');
		native(Number.prototype.toLocaleString,'Number.prototype.toLocaleString');
		native(Number.prototype.valueOf,'Number.prototype.valueOf');
		native(Number.prototype.toFixed,'Number.prototype.toFixed');
		native(Number.prototype.toExponential,'Number.prototype.toExponential');
		native(Number.prototype.toPrecision,'Number.prototype.toPrecision');
		native(Uint8Array.prototype.subarray,'Uint8Array.prototype.subarray');
		native(Boolean.prototype.toString,'Boolean.prototype.toString');
		native(Boolean.prototype.valueOf,'Boolean.prototype.valueOf');
		native(Error.prototype.toString,'Error.prototype.toString');
		native(Uint16Array.prototype.subarray,'Uint16Array.prototype.subarray');
		native(Int8Array.prototype.subarray,'Int8Array.prototype.subarray');
		native(WeakMap.prototype.get,'WeakMap.prototype.get');
		native(WeakMap.prototype.set,'WeakMap.prototype.set');
		native(WeakMap.prototype.has,'WeakMap.prototype.has');
		native(WeakMap.prototype.delete,'WeakMap.prototype.delete');
		native(Symbol.prototype.toString,'Symbol.prototype.toString');
		native(Symbol.prototype.valueOf,'Symbol.prototype.valueOf');
		native(chrome.runtime.connect,'chrome.runtime.connect');
		native(chrome.runtime.sendMessage,'chrome.runtime.sendMessage');
		native(chrome.app.getIsInstalled,'chrome.app.getIsInstalled');
		native(chrome.app.getDetails,'chrome.app.getDetails');
		native(chrome.app.getDetailsForFrame,'chrome.app.getDetailsForFrame');
		native(chrome.app.runningState,'chrome.app.runningState');
		native(Float64Array.prototype.subarray,'Float64Array.prototype.subarray');
		native(Uint8ClampedArray.prototype.subarray,'Uint8ClampedArray.prototype.subarray');
		native(Uint32Array.prototype.subarray,'Uint32Array.prototype.subarray');
		native(Int32Array.prototype.subarray,'Int32Array.prototype.subarray');
		native(ArrayBuffer.prototype.slice,'ArrayBuffer.prototype.slice');
		native(DataView.prototype.getInt8,'DataView.prototype.getInt8');
		native(DataView.prototype.setInt8,'DataView.prototype.setInt8');
		native(DataView.prototype.getUint8,'DataView.prototype.getUint8');
		native(DataView.prototype.setUint8,'DataView.prototype.setUint8');
		native(DataView.prototype.getInt16,'DataView.prototype.getInt16');
		native(DataView.prototype.setInt16,'DataView.prototype.setInt16');
		native(DataView.prototype.getUint16,'DataView.prototype.getUint16');
		native(DataView.prototype.setUint16,'DataView.prototype.setUint16');
		native(DataView.prototype.getInt32,'DataView.prototype.getInt32');
		native(DataView.prototype.setInt32,'DataView.prototype.setInt32');
		native(DataView.prototype.getUint32,'DataView.prototype.getUint32');
		native(DataView.prototype.setUint32,'DataView.prototype.setUint32');
		native(DataView.prototype.getFloat32,'DataView.prototype.getFloat32');
		native(DataView.prototype.setFloat32,'DataView.prototype.setFloat32');
		native(DataView.prototype.getFloat64,'DataView.prototype.getFloat64');
		native(DataView.prototype.setFloat64,'DataView.prototype.setFloat64');
		native(String.prototype.valueOf,'String.prototype.valueOf');
		native(String.prototype.toString,'String.prototype.toString');
		native(String.prototype.charAt,'String.prototype.charAt');
		native(String.prototype.charCodeAt,'String.prototype.charCodeAt');
		native(String.prototype.concat,'String.prototype.concat');
		native(String.prototype.indexOf,'String.prototype.indexOf');
		native(String.prototype.lastIndexOf,'String.prototype.lastIndexOf');
		native(String.prototype.localeCompare,'String.prototype.localeCompare');
		native(String.prototype.match,'String.prototype.match');
		native(String.prototype.normalize,'String.prototype.normalize');
		native(String.prototype.replace,'String.prototype.replace');
		native(String.prototype.search,'String.prototype.search');
		native(String.prototype.slice,'String.prototype.slice');
		native(String.prototype.split,'String.prototype.split');
		native(String.prototype.substring,'String.prototype.substring');
		native(String.prototype.substr,'String.prototype.substr');
		native(String.prototype.toLowerCase,'String.prototype.toLowerCase');
		native(String.prototype.toLocaleLowerCase,'String.prototype.toLocaleLowerCase');
		native(String.prototype.toUpperCase,'String.prototype.toUpperCase');
		native(String.prototype.toLocaleUpperCase,'String.prototype.toLocaleUpperCase');
		native(String.prototype.trim,'String.prototype.trim');
		native(String.prototype.trimLeft,'String.prototype.trimLeft');
		native(String.prototype.trimRight,'String.prototype.trimRight');
		native(String.prototype.link,'String.prototype.link');
		native(String.prototype.anchor,'String.prototype.anchor');
		native(String.prototype.fontcolor,'String.prototype.fontcolor');
		native(String.prototype.fontsize,'String.prototype.fontsize');
		native(String.prototype.big,'String.prototype.big');
		native(String.prototype.blink,'String.prototype.blink');
		native(String.prototype.bold,'String.prototype.bold');
		native(String.prototype.fixed,'String.prototype.fixed');
		native(String.prototype.italics,'String.prototype.italics');
		native(String.prototype.small,'String.prototype.small');
		native(String.prototype.strike,'String.prototype.strike');
		native(String.prototype.sub,'String.prototype.sub');
		native(String.prototype.sup,'String.prototype.sup');
		native(String.prototype.codePointAt,'String.prototype.codePointAt');
		native(String.prototype.includes,'String.prototype.includes');
		native(String.prototype.endsWith,'String.prototype.endsWith');
		native(String.prototype.repeat,'String.prototype.repeat');
		native(String.prototype.startsWith,'String.prototype.startsWith');
		native(Function.prototype.toString,'Function.prototype.toString');
		native(Function.prototype.call,'Function.prototype.call');
		native(Function.prototype.apply,'Function.prototype.apply');
		native(Set.prototype.add,'Set.prototype.add');
		native(Set.prototype.has,'Set.prototype.has');
		native(Set.prototype.delete,'Set.prototype.delete');
		native(Set.prototype.clear,'Set.prototype.clear');
		native(Set.prototype.forEach,'Set.prototype.forEach');
		native(Set.prototype.entries,'Set.prototype.entries');
		native(Set.prototype.keys,'Set.prototype.keys');
		native(Array.prototype.push,'Array.prototype.push');
		native(Array.prototype.sort,'Array.prototype.sort');
		native(Array.prototype.splice,'Array.prototype.splice');
		native(Float32Array.prototype.subarray,'Float32Array.prototype.subarray');
		native(Intl.v8BreakIterator.supportedLocalesOf,'Intl.v8BreakIterator.supportedLocalesOf');
		native(Intl.DateTimeFormat.supportedLocalesOf,'Intl.DateTimeFormat.supportedLocalesOf');
		native(Intl.NumberFormat.supportedLocalesOf,'Intl.NumberFormat.supportedLocalesOf');
		native(Intl.Collator.supportedLocalesOf,'Intl.Collator.supportedLocalesOf');
		native(WeakSet.prototype.add,'WeakSet.prototype.add');
		native(WeakSet.prototype.has,'WeakSet.prototype.has');
		native(WeakSet.prototype.delete,'WeakSet.prototype.delete');
		native(Array.prototype.toString,'Array.prototype.toString');
		native(Array.prototype.toLocaleString,'Array.prototype.toLocaleString');
		native(Array.prototype.join,'Array.prototype.join');
		native(Array.prototype.pop,'Array.prototype.pop');
		native(Array.prototype.concat,'Array.prototype.concat');
		native(Array.prototype.reverse,'Array.prototype.reverse');
		native(Array.prototype.shift,'Array.prototype.shift');
		native(Array.prototype.unshift,'Array.prototype.unshift');
		native(Array.prototype.slice,'Array.prototype.slice');
		native(Array.prototype.filter,'Array.prototype.filter');
		native(Array.prototype.forEach,'Array.prototype.forEach');
		native(Array.prototype.some,'Array.prototype.some');
		native(Array.prototype.every,'Array.prototype.every');
		native(Array.prototype.map,'Array.prototype.map');
		native(Array.prototype.indexOf,'Array.prototype.indexOf');
		native(Array.prototype.lastIndexOf,'Array.prototype.lastIndexOf');
		native(Array.prototype.reduce,'Array.prototype.reduce');
		native(Array.prototype.reduceRight,'Array.prototype.reduceRight');
		native(RegExp.prototype.exec,'RegExp.prototype.exec');
		native(RegExp.prototype.test,'RegExp.prototype.test');
		native(RegExp.prototype.toString,'RegExp.prototype.toString');
		native(RegExp.prototype.compile,'RegExp.prototype.compile');
		native(webkitOfflineAudioContext.prototype.startRendering,'webkitOfflineAudioContext.prototype.startRendering');
		native(webkitAudioContext.prototype.createBuffer,'webkitAudioContext.prototype.createBuffer');
		native(webkitAudioContext.prototype.decodeAudioData,'webkitAudioContext.prototype.decodeAudioData');
		native(webkitAudioContext.prototype.createBufferSource,'webkitAudioContext.prototype.createBufferSource');
		native(webkitAudioContext.prototype.createMediaElementSource,'webkitAudioContext.prototype.createMediaElementSource');
		native(webkitAudioContext.prototype.createMediaStreamSource,'webkitAudioContext.prototype.createMediaStreamSource');
		native(webkitAudioContext.prototype.createMediaStreamDestination,'webkitAudioContext.prototype.createMediaStreamDestination');
		native(webkitAudioContext.prototype.createGain,'webkitAudioContext.prototype.createGain');
		native(webkitAudioContext.prototype.createDelay,'webkitAudioContext.prototype.createDelay');
		native(webkitAudioContext.prototype.createBiquadFilter,'webkitAudioContext.prototype.createBiquadFilter');
		native(webkitAudioContext.prototype.createWaveShaper,'webkitAudioContext.prototype.createWaveShaper');
		native(webkitAudioContext.prototype.createPanner,'webkitAudioContext.prototype.createPanner');
		native(webkitAudioContext.prototype.createConvolver,'webkitAudioContext.prototype.createConvolver');
		native(webkitAudioContext.prototype.createDynamicsCompressor,'webkitAudioContext.prototype.createDynamicsCompressor');
		native(webkitAudioContext.prototype.createAnalyser,'webkitAudioContext.prototype.createAnalyser');
		native(webkitAudioContext.prototype.createScriptProcessor,'webkitAudioContext.prototype.createScriptProcessor');
		native(webkitAudioContext.prototype.createStereoPanner,'webkitAudioContext.prototype.createStereoPanner');
		native(webkitAudioContext.prototype.createOscillator,'webkitAudioContext.prototype.createOscillator');
		native(webkitAudioContext.prototype.createPeriodicWave,'webkitAudioContext.prototype.createPeriodicWave');
		native(webkitAudioContext.prototype.createChannelSplitter,'webkitAudioContext.prototype.createChannelSplitter');
		native(webkitAudioContext.prototype.createChannelMerger,'webkitAudioContext.prototype.createChannelMerger');
		native(webkitAudioContext.prototype.suspend,'webkitAudioContext.prototype.suspend');
		native(webkitAudioContext.prototype.resume,'webkitAudioContext.prototype.resume');
		native(webkitSpeechRecognition.prototype.start,'webkitSpeechRecognition.prototype.start');
		native(webkitSpeechRecognition.prototype.stop,'webkitSpeechRecognition.prototype.stop');
		native(webkitSpeechRecognition.prototype.abort,'webkitSpeechRecognition.prototype.abort');
		native(webkitSpeechGrammarList.prototype.item,'webkitSpeechGrammarList.prototype.item');
		native(webkitSpeechGrammarList.prototype.addFromUri,'webkitSpeechGrammarList.prototype.addFromUri');
		native(webkitSpeechGrammarList.prototype.addFromString,'webkitSpeechGrammarList.prototype.addFromString');
		native(webkitRTCPeerConnection.prototype.createOffer,'webkitRTCPeerConnection.prototype.createOffer');
		native(webkitRTCPeerConnection.prototype.createAnswer,'webkitRTCPeerConnection.prototype.createAnswer');
		native(webkitRTCPeerConnection.prototype.setLocalDescription,'webkitRTCPeerConnection.prototype.setLocalDescription');
		native(webkitRTCPeerConnection.prototype.setRemoteDescription,'webkitRTCPeerConnection.prototype.setRemoteDescription');
		native(webkitRTCPeerConnection.prototype.updateIce,'webkitRTCPeerConnection.prototype.updateIce');
		native(webkitRTCPeerConnection.prototype.addIceCandidate,'webkitRTCPeerConnection.prototype.addIceCandidate');
		native(webkitRTCPeerConnection.prototype.getLocalStreams,'webkitRTCPeerConnection.prototype.getLocalStreams');
		native(webkitRTCPeerConnection.prototype.getRemoteStreams,'webkitRTCPeerConnection.prototype.getRemoteStreams');
		native(webkitRTCPeerConnection.prototype.getStreamById,'webkitRTCPeerConnection.prototype.getStreamById');
		native(webkitRTCPeerConnection.prototype.addStream,'webkitRTCPeerConnection.prototype.addStream');
		native(webkitRTCPeerConnection.prototype.removeStream,'webkitRTCPeerConnection.prototype.removeStream');
		native(webkitRTCPeerConnection.prototype.getStats,'webkitRTCPeerConnection.prototype.getStats');
		native(webkitRTCPeerConnection.prototype.createDataChannel,'webkitRTCPeerConnection.prototype.createDataChannel');
		native(webkitRTCPeerConnection.prototype.createDTMFSender,'webkitRTCPeerConnection.prototype.createDTMFSender');
		native(webkitRTCPeerConnection.prototype.close,'webkitRTCPeerConnection.prototype.close');
		native(ServiceWorkerRegistration.prototype.unregister,'ServiceWorkerRegistration.prototype.unregister');
		native(ServiceWorkerContainer.prototype.register,'ServiceWorkerContainer.prototype.register');
		native(ServiceWorkerContainer.prototype.getRegistration,'ServiceWorkerContainer.prototype.getRegistration');
		native(ServiceWorker.prototype.postMessage,'ServiceWorker.prototype.postMessage');
		native(ServiceWorker.prototype.terminate,'ServiceWorker.prototype.terminate');
		native(ScreenOrientation.prototype.lock,'ScreenOrientation.prototype.lock');
		native(ScreenOrientation.prototype.unlock,'ScreenOrientation.prototype.unlock');
		native(Notification.prototype.close,'Notification.prototype.close');
		native(MediaSource.prototype.addSourceBuffer,'MediaSource.prototype.addSourceBuffer');
		native(MediaSource.prototype.removeSourceBuffer,'MediaSource.prototype.removeSourceBuffer');
		native(MediaSource.prototype.endOfStream,'MediaSource.prototype.endOfStream');
		native(webkitMediaStream.prototype.getAudioTracks,'webkitMediaStream.prototype.getAudioTracks');
		native(webkitMediaStream.prototype.getVideoTracks,'webkitMediaStream.prototype.getVideoTracks');
		native(webkitMediaStream.prototype.getTracks,'webkitMediaStream.prototype.getTracks');
		native(webkitMediaStream.prototype.addTrack,'webkitMediaStream.prototype.addTrack');
		native(webkitMediaStream.prototype.removeTrack,'webkitMediaStream.prototype.removeTrack');
		native(webkitMediaStream.prototype.getTrackById,'webkitMediaStream.prototype.getTrackById');
		native(webkitMediaStream.prototype.clone,'webkitMediaStream.prototype.clone');
		native(webkitMediaStream.prototype.stop,'webkitMediaStream.prototype.stop');
		native(webkitIDBTransaction.prototype.objectStore,'webkitIDBTransaction.prototype.objectStore');
		native(webkitIDBTransaction.prototype.abort,'webkitIDBTransaction.prototype.abort');
		native(webkitIDBObjectStore.prototype.put,'webkitIDBObjectStore.prototype.put');
		native(webkitIDBObjectStore.prototype.add,'webkitIDBObjectStore.prototype.add');
		native(webkitIDBObjectStore.prototype.delete,'webkitIDBObjectStore.prototype.delete');
		native(webkitIDBObjectStore.prototype.get,'webkitIDBObjectStore.prototype.get');
		native(webkitIDBObjectStore.prototype.clear,'webkitIDBObjectStore.prototype.clear');
		native(webkitIDBObjectStore.prototype.openCursor,'webkitIDBObjectStore.prototype.openCursor');
		native(webkitIDBObjectStore.prototype.createIndex,'webkitIDBObjectStore.prototype.createIndex');
		native(webkitIDBObjectStore.prototype.index,'webkitIDBObjectStore.prototype.index');
		native(webkitIDBObjectStore.prototype.deleteIndex,'webkitIDBObjectStore.prototype.deleteIndex');
		native(webkitIDBObjectStore.prototype.count,'webkitIDBObjectStore.prototype.count');
		native(webkitIDBIndex.prototype.openCursor,'webkitIDBIndex.prototype.openCursor');
		native(webkitIDBIndex.prototype.openKeyCursor,'webkitIDBIndex.prototype.openKeyCursor');
		native(webkitIDBIndex.prototype.get,'webkitIDBIndex.prototype.get');
		native(webkitIDBIndex.prototype.getKey,'webkitIDBIndex.prototype.getKey');
		native(webkitIDBIndex.prototype.count,'webkitIDBIndex.prototype.count');
		native(webkitIDBFactory.prototype.webkitGetDatabaseNames,'webkitIDBFactory.prototype.webkitGetDatabaseNames');
		native(webkitIDBFactory.prototype.open,'webkitIDBFactory.prototype.open');
		native(webkitIDBFactory.prototype.deleteDatabase,'webkitIDBFactory.prototype.deleteDatabase');
		native(webkitIDBFactory.prototype.cmp,'webkitIDBFactory.prototype.cmp');
		native(webkitIDBDatabase.prototype.createObjectStore,'webkitIDBDatabase.prototype.createObjectStore');
		native(webkitIDBDatabase.prototype.deleteObjectStore,'webkitIDBDatabase.prototype.deleteObjectStore');
		native(webkitIDBDatabase.prototype.transaction,'webkitIDBDatabase.prototype.transaction');
		native(webkitIDBDatabase.prototype.close,'webkitIDBDatabase.prototype.close');
		native(webkitIDBCursor.prototype.update,'webkitIDBCursor.prototype.update');
		native(webkitIDBCursor.prototype.advance,'webkitIDBCursor.prototype.advance');
		native(webkitIDBCursor.prototype.continue,'webkitIDBCursor.prototype.continue');
		native(webkitIDBCursor.prototype.delete,'webkitIDBCursor.prototype.delete');
		native(WebSocket.prototype.close,'WebSocket.prototype.close');
		native(WebSocket.prototype.send,'WebSocket.prototype.send');
		native(TextEncoder.prototype.encode,'TextEncoder.prototype.encode');
		native(TextDecoder.prototype.decode,'TextDecoder.prototype.decode');
		native(MediaStreamTrack.prototype.stop,'MediaStreamTrack.prototype.stop');
		native(MediaStreamTrack.prototype.clone,'MediaStreamTrack.prototype.clone');
		native(DeviceOrientationEvent.prototype.initDeviceOrientationEvent,'DeviceOrientationEvent.prototype.initDeviceOrientationEvent');
		native(DeviceMotionEvent.prototype.initDeviceMotionEvent,'DeviceMotionEvent.prototype.initDeviceMotionEvent');
		native(OscillatorNode.prototype.start,'OscillatorNode.prototype.start');
		native(OscillatorNode.prototype.stop,'OscillatorNode.prototype.stop');
		native(OscillatorNode.prototype.setPeriodicWave,'OscillatorNode.prototype.setPeriodicWave');
		native(BiquadFilterNode.prototype.getFrequencyResponse,'BiquadFilterNode.prototype.getFrequencyResponse');
		native(AudioParam.prototype.setValueAtTime,'AudioParam.prototype.setValueAtTime');
		native(AudioParam.prototype.linearRampToValueAtTime,'AudioParam.prototype.linearRampToValueAtTime');
		native(AudioParam.prototype.exponentialRampToValueAtTime,'AudioParam.prototype.exponentialRampToValueAtTime');
		native(AudioParam.prototype.setTargetAtTime,'AudioParam.prototype.setTargetAtTime');
		native(AudioParam.prototype.setValueCurveAtTime,'AudioParam.prototype.setValueCurveAtTime');
		native(AudioParam.prototype.cancelScheduledValues,'AudioParam.prototype.cancelScheduledValues');
		native(AudioNode.prototype.connect,'AudioNode.prototype.connect');
		native(AudioNode.prototype.disconnect,'AudioNode.prototype.disconnect');
		native(AudioListener.prototype.setPosition,'AudioListener.prototype.setPosition');
		native(AudioListener.prototype.setOrientation,'AudioListener.prototype.setOrientation');
		native(AudioListener.prototype.setVelocity,'AudioListener.prototype.setVelocity');
		native(AudioBufferSourceNode.prototype.start,'AudioBufferSourceNode.prototype.start');
		native(AudioBufferSourceNode.prototype.stop,'AudioBufferSourceNode.prototype.stop');
		native(AudioBuffer.prototype.getChannelData,'AudioBuffer.prototype.getChannelData');
		native(AnalyserNode.prototype.getFloatFrequencyData,'AnalyserNode.prototype.getFloatFrequencyData');
		native(AnalyserNode.prototype.getByteFrequencyData,'AnalyserNode.prototype.getByteFrequencyData');
		native(AnalyserNode.prototype.getFloatTimeDomainData,'AnalyserNode.prototype.getFloatTimeDomainData');
		native(AnalyserNode.prototype.getByteTimeDomainData,'AnalyserNode.prototype.getByteTimeDomainData');
		native(XSLTProcessor.prototype.importStylesheet,'XSLTProcessor.prototype.importStylesheet');
		native(XSLTProcessor.prototype.transformToFragment,'XSLTProcessor.prototype.transformToFragment');
		native(XSLTProcessor.prototype.transformToDocument,'XSLTProcessor.prototype.transformToDocument');
		native(XSLTProcessor.prototype.setParameter,'XSLTProcessor.prototype.setParameter');
		native(XSLTProcessor.prototype.getParameter,'XSLTProcessor.prototype.getParameter');
		native(XSLTProcessor.prototype.removeParameter,'XSLTProcessor.prototype.removeParameter');
		native(XSLTProcessor.prototype.clearParameters,'XSLTProcessor.prototype.clearParameters');
		native(XSLTProcessor.prototype.reset,'XSLTProcessor.prototype.reset');
		native(SVGTransformList.prototype.clear,'SVGTransformList.prototype.clear');
		native(SVGTransformList.prototype.initialize,'SVGTransformList.prototype.initialize');
		native(SVGTransformList.prototype.getItem,'SVGTransformList.prototype.getItem');
		native(SVGTransformList.prototype.insertItemBefore,'SVGTransformList.prototype.insertItemBefore');
		native(SVGTransformList.prototype.replaceItem,'SVGTransformList.prototype.replaceItem');
		native(SVGTransformList.prototype.removeItem,'SVGTransformList.prototype.removeItem');
		native(SVGTransformList.prototype.appendItem,'SVGTransformList.prototype.appendItem');
		native(SVGTransformList.prototype.createSVGTransformFromMatrix,'SVGTransformList.prototype.createSVGTransformFromMatrix');
		native(SVGTransformList.prototype.consolidate,'SVGTransformList.prototype.consolidate');
		native(SVGTransform.prototype.setMatrix,'SVGTransform.prototype.setMatrix');
		native(SVGTransform.prototype.setTranslate,'SVGTransform.prototype.setTranslate');
		native(SVGTransform.prototype.setScale,'SVGTransform.prototype.setScale');
		native(SVGTransform.prototype.setRotate,'SVGTransform.prototype.setRotate');
		native(SVGTransform.prototype.setSkewX,'SVGTransform.prototype.setSkewX');
		native(SVGTransform.prototype.setSkewY,'SVGTransform.prototype.setSkewY');
		native(SVGStringList.prototype.clear,'SVGStringList.prototype.clear');
		native(SVGStringList.prototype.initialize,'SVGStringList.prototype.initialize');
		native(SVGStringList.prototype.getItem,'SVGStringList.prototype.getItem');
		native(SVGStringList.prototype.insertItemBefore,'SVGStringList.prototype.insertItemBefore');
		native(SVGStringList.prototype.replaceItem,'SVGStringList.prototype.replaceItem');
		native(SVGStringList.prototype.removeItem,'SVGStringList.prototype.removeItem');
		native(SVGStringList.prototype.appendItem,'SVGStringList.prototype.appendItem');
		native(SVGPointList.prototype.clear,'SVGPointList.prototype.clear');
		native(SVGPointList.prototype.initialize,'SVGPointList.prototype.initialize');
		native(SVGPointList.prototype.getItem,'SVGPointList.prototype.getItem');
		native(SVGPointList.prototype.insertItemBefore,'SVGPointList.prototype.insertItemBefore');
		native(SVGPointList.prototype.replaceItem,'SVGPointList.prototype.replaceItem');
		native(SVGPointList.prototype.removeItem,'SVGPointList.prototype.removeItem');
		native(SVGPointList.prototype.appendItem,'SVGPointList.prototype.appendItem');
		native(SVGPathSegList.prototype.clear,'SVGPathSegList.prototype.clear');
		native(SVGPathSegList.prototype.initialize,'SVGPathSegList.prototype.initialize');
		native(SVGPathSegList.prototype.getItem,'SVGPathSegList.prototype.getItem');
		native(SVGPathSegList.prototype.insertItemBefore,'SVGPathSegList.prototype.insertItemBefore');
		native(SVGPathSegList.prototype.replaceItem,'SVGPathSegList.prototype.replaceItem');
		native(SVGPathSegList.prototype.removeItem,'SVGPathSegList.prototype.removeItem');
		native(SVGPathSegList.prototype.appendItem,'SVGPathSegList.prototype.appendItem');
		native(SVGNumberList.prototype.clear,'SVGNumberList.prototype.clear');
		native(SVGNumberList.prototype.initialize,'SVGNumberList.prototype.initialize');
		native(SVGNumberList.prototype.getItem,'SVGNumberList.prototype.getItem');
		native(SVGNumberList.prototype.insertItemBefore,'SVGNumberList.prototype.insertItemBefore');
		native(SVGNumberList.prototype.replaceItem,'SVGNumberList.prototype.replaceItem');
		native(SVGNumberList.prototype.removeItem,'SVGNumberList.prototype.removeItem');
		native(SVGNumberList.prototype.appendItem,'SVGNumberList.prototype.appendItem');
		native(SVGLengthList.prototype.clear,'SVGLengthList.prototype.clear');
		native(SVGLengthList.prototype.initialize,'SVGLengthList.prototype.initialize');
		native(SVGLengthList.prototype.getItem,'SVGLengthList.prototype.getItem');
		native(SVGLengthList.prototype.insertItemBefore,'SVGLengthList.prototype.insertItemBefore');
		native(SVGLengthList.prototype.replaceItem,'SVGLengthList.prototype.replaceItem');
		native(SVGLengthList.prototype.removeItem,'SVGLengthList.prototype.removeItem');
		native(SVGLengthList.prototype.appendItem,'SVGLengthList.prototype.appendItem');
		native(SVGLength.prototype.newValueSpecifiedUnits,'SVGLength.prototype.newValueSpecifiedUnits');
		native(SVGLength.prototype.convertToSpecifiedUnits,'SVGLength.prototype.convertToSpecifiedUnits');
		native(TimeRanges.prototype.start,'TimeRanges.prototype.start');
		native(TimeRanges.prototype.end,'TimeRanges.prototype.end');
		native(HTMLVideoElement.prototype.webkitEnterFullscreen,'HTMLVideoElement.prototype.webkitEnterFullscreen');
		native(HTMLVideoElement.prototype.webkitExitFullscreen,'HTMLVideoElement.prototype.webkitExitFullscreen');
		native(HTMLVideoElement.prototype.webkitEnterFullScreen,'HTMLVideoElement.prototype.webkitEnterFullScreen');
		native(HTMLVideoElement.prototype.webkitExitFullScreen,'HTMLVideoElement.prototype.webkitExitFullScreen');
		native(HTMLMediaElement.prototype.load,'HTMLMediaElement.prototype.load');
		native(HTMLMediaElement.prototype.canPlayType,'HTMLMediaElement.prototype.canPlayType');
		native(HTMLMediaElement.prototype.play,'HTMLMediaElement.prototype.play');
		native(HTMLMediaElement.prototype.pause,'HTMLMediaElement.prototype.pause');
		native(HTMLMediaElement.prototype.addTextTrack,'HTMLMediaElement.prototype.addTextTrack');
		native(HTMLMediaElement.prototype.webkitGenerateKeyRequest,'HTMLMediaElement.prototype.webkitGenerateKeyRequest');
		native(HTMLMediaElement.prototype.webkitAddKey,'HTMLMediaElement.prototype.webkitAddKey');
		native(HTMLMediaElement.prototype.webkitCancelKeyRequest,'HTMLMediaElement.prototype.webkitCancelKeyRequest');
		native(XPathResult.prototype.iterateNext,'XPathResult.prototype.iterateNext');
		native(XPathResult.prototype.snapshotItem,'XPathResult.prototype.snapshotItem');
		native(XPathExpression.prototype.evaluate,'XPathExpression.prototype.evaluate');
		native(XPathEvaluator.prototype.createExpression,'XPathEvaluator.prototype.createExpression');
		native(XPathEvaluator.prototype.createNSResolver,'XPathEvaluator.prototype.createNSResolver');
		native(XPathEvaluator.prototype.evaluate,'XPathEvaluator.prototype.evaluate');
		native(XMLSerializer.prototype.serializeToString,'XMLSerializer.prototype.serializeToString');
		native(XMLHttpRequest.prototype.open,'XMLHttpRequest.prototype.open');
		native(XMLHttpRequest.prototype.setRequestHeader,'XMLHttpRequest.prototype.setRequestHeader');
		native(XMLHttpRequest.prototype.send,'XMLHttpRequest.prototype.send');
		native(XMLHttpRequest.prototype.abort,'XMLHttpRequest.prototype.abort');
		native(XMLHttpRequest.prototype.getAllResponseHeaders,'XMLHttpRequest.prototype.getAllResponseHeaders');
		native(XMLHttpRequest.prototype.getResponseHeader,'XMLHttpRequest.prototype.getResponseHeader');
		native(XMLHttpRequest.prototype.overrideMimeType,'XMLHttpRequest.prototype.overrideMimeType');
		native(Worker.prototype.postMessage,'Worker.prototype.postMessage');
		native(Worker.prototype.terminate,'Worker.prototype.terminate');
		native(Window.prototype.toString,'Window.prototype.toString');
		native(Window.prototype.postMessage,'Window.prototype.postMessage');
		native(Window.prototype.close,'Window.prototype.close');
		native(Window.prototype.blur,'Window.prototype.blur');
		native(Window.prototype.focus,'Window.prototype.focus');
		native(Window.prototype.getSelection,'Window.prototype.getSelection');
		native(Window.prototype.print,'Window.prototype.print');
		native(Window.prototype.stop,'Window.prototype.stop');
		native(Window.prototype.open,'Window.prototype.open');
		native(Window.prototype.alert,'Window.prototype.alert');
		native(Window.prototype.confirm,'Window.prototype.confirm');
		native(Window.prototype.prompt,'Window.prototype.prompt');
		native(Window.prototype.find,'Window.prototype.find');
		native(Window.prototype.moveBy,'Window.prototype.moveBy');
		native(Window.prototype.moveTo,'Window.prototype.moveTo');
		native(Window.prototype.resizeBy,'Window.prototype.resizeBy');
		native(Window.prototype.resizeTo,'Window.prototype.resizeTo');
		native(Window.prototype.matchMedia,'Window.prototype.matchMedia');
		native(Window.prototype.getComputedStyle,'Window.prototype.getComputedStyle');
		native(Window.prototype.getMatchedCSSRules,'Window.prototype.getMatchedCSSRules');
		native(Window.prototype.requestAnimationFrame,'Window.prototype.requestAnimationFrame');
		native(Window.prototype.cancelAnimationFrame,'Window.prototype.cancelAnimationFrame');
		native(Window.prototype.webkitRequestAnimationFrame,'Window.prototype.webkitRequestAnimationFrame');
		native(Window.prototype.webkitCancelAnimationFrame,'Window.prototype.webkitCancelAnimationFrame');
		native(Window.prototype.webkitCancelRequestAnimationFrame,'Window.prototype.webkitCancelRequestAnimationFrame');
		native(Window.prototype.captureEvents,'Window.prototype.captureEvents');
		native(Window.prototype.releaseEvents,'Window.prototype.releaseEvents');
		native(Window.prototype.btoa,'Window.prototype.btoa');
		native(Window.prototype.atob,'Window.prototype.atob');
		native(Window.prototype.setTimeout,'Window.prototype.setTimeout');
		native(Window.prototype.clearTimeout,'Window.prototype.clearTimeout');
		native(Window.prototype.setInterval,'Window.prototype.setInterval');
		native(Window.prototype.clearInterval,'Window.prototype.clearInterval');
		native(Window.prototype.scrollBy,'Window.prototype.scrollBy');
		native(Window.prototype.scrollTo,'Window.prototype.scrollTo');
		native(Window.prototype.scroll,'Window.prototype.scroll');
		native(Window.prototype.webkitRequestFileSystem,'Window.prototype.webkitRequestFileSystem');
		native(Window.prototype.webkitResolveLocalFileSystemURL,'Window.prototype.webkitResolveLocalFileSystemURL');
		native(Window.prototype.openDatabase,'Window.prototype.openDatabase');
		native(WebKitCSSMatrix.prototype.setMatrixValue,'WebKitCSSMatrix.prototype.setMatrixValue');
		native(WebKitCSSMatrix.prototype.multiply,'WebKitCSSMatrix.prototype.multiply');
		native(WebKitCSSMatrix.prototype.inverse,'WebKitCSSMatrix.prototype.inverse');
		native(WebKitCSSMatrix.prototype.translate,'WebKitCSSMatrix.prototype.translate');
		native(WebKitCSSMatrix.prototype.scale,'WebKitCSSMatrix.prototype.scale');
		native(WebKitCSSMatrix.prototype.rotate,'WebKitCSSMatrix.prototype.rotate');
		native(WebKitCSSMatrix.prototype.rotateAxisAngle,'WebKitCSSMatrix.prototype.rotateAxisAngle');
		native(WebKitCSSMatrix.prototype.skewX,'WebKitCSSMatrix.prototype.skewX');
		native(WebKitCSSMatrix.prototype.skewY,'WebKitCSSMatrix.prototype.skewY');
		native(WebKitCSSMatrix.prototype.toString,'WebKitCSSMatrix.prototype.toString');
		native(WebGLRenderingContext.prototype.activeTexture,'WebGLRenderingContext.prototype.activeTexture');
		native(WebGLRenderingContext.prototype.attachShader,'WebGLRenderingContext.prototype.attachShader');
		native(WebGLRenderingContext.prototype.bindAttribLocation,'WebGLRenderingContext.prototype.bindAttribLocation');
		native(WebGLRenderingContext.prototype.bindBuffer,'WebGLRenderingContext.prototype.bindBuffer');
		native(WebGLRenderingContext.prototype.bindFramebuffer,'WebGLRenderingContext.prototype.bindFramebuffer');
		native(WebGLRenderingContext.prototype.bindRenderbuffer,'WebGLRenderingContext.prototype.bindRenderbuffer');
		native(WebGLRenderingContext.prototype.bindTexture,'WebGLRenderingContext.prototype.bindTexture');
		native(WebGLRenderingContext.prototype.blendColor,'WebGLRenderingContext.prototype.blendColor');
		native(WebGLRenderingContext.prototype.blendEquation,'WebGLRenderingContext.prototype.blendEquation');
		native(WebGLRenderingContext.prototype.blendEquationSeparate,'WebGLRenderingContext.prototype.blendEquationSeparate');
		native(WebGLRenderingContext.prototype.blendFunc,'WebGLRenderingContext.prototype.blendFunc');
		native(WebGLRenderingContext.prototype.blendFuncSeparate,'WebGLRenderingContext.prototype.blendFuncSeparate');
		native(WebGLRenderingContext.prototype.bufferData,'WebGLRenderingContext.prototype.bufferData');
		native(WebGLRenderingContext.prototype.bufferSubData,'WebGLRenderingContext.prototype.bufferSubData');
		native(WebGLRenderingContext.prototype.checkFramebufferStatus,'WebGLRenderingContext.prototype.checkFramebufferStatus');
		native(WebGLRenderingContext.prototype.clear,'WebGLRenderingContext.prototype.clear');
		native(WebGLRenderingContext.prototype.clearColor,'WebGLRenderingContext.prototype.clearColor');
		native(WebGLRenderingContext.prototype.clearDepth,'WebGLRenderingContext.prototype.clearDepth');
		native(WebGLRenderingContext.prototype.clearStencil,'WebGLRenderingContext.prototype.clearStencil');
		native(WebGLRenderingContext.prototype.colorMask,'WebGLRenderingContext.prototype.colorMask');
		native(WebGLRenderingContext.prototype.compileShader,'WebGLRenderingContext.prototype.compileShader');
		native(WebGLRenderingContext.prototype.compressedTexImage2D,'WebGLRenderingContext.prototype.compressedTexImage2D');
		native(WebGLRenderingContext.prototype.compressedTexSubImage2D,'WebGLRenderingContext.prototype.compressedTexSubImage2D');
		native(WebGLRenderingContext.prototype.copyTexImage2D,'WebGLRenderingContext.prototype.copyTexImage2D');
		native(WebGLRenderingContext.prototype.copyTexSubImage2D,'WebGLRenderingContext.prototype.copyTexSubImage2D');
		native(WebGLRenderingContext.prototype.createBuffer,'WebGLRenderingContext.prototype.createBuffer');
		native(WebGLRenderingContext.prototype.createFramebuffer,'WebGLRenderingContext.prototype.createFramebuffer');
		native(WebGLRenderingContext.prototype.createProgram,'WebGLRenderingContext.prototype.createProgram');
		native(WebGLRenderingContext.prototype.createRenderbuffer,'WebGLRenderingContext.prototype.createRenderbuffer');
		native(WebGLRenderingContext.prototype.createShader,'WebGLRenderingContext.prototype.createShader');
		native(WebGLRenderingContext.prototype.createTexture,'WebGLRenderingContext.prototype.createTexture');
		native(WebGLRenderingContext.prototype.cullFace,'WebGLRenderingContext.prototype.cullFace');
		native(WebGLRenderingContext.prototype.deleteBuffer,'WebGLRenderingContext.prototype.deleteBuffer');
		native(WebGLRenderingContext.prototype.deleteFramebuffer,'WebGLRenderingContext.prototype.deleteFramebuffer');
		native(WebGLRenderingContext.prototype.deleteProgram,'WebGLRenderingContext.prototype.deleteProgram');
		native(WebGLRenderingContext.prototype.deleteRenderbuffer,'WebGLRenderingContext.prototype.deleteRenderbuffer');
		native(WebGLRenderingContext.prototype.deleteShader,'WebGLRenderingContext.prototype.deleteShader');
		native(WebGLRenderingContext.prototype.deleteTexture,'WebGLRenderingContext.prototype.deleteTexture');
		native(WebGLRenderingContext.prototype.depthFunc,'WebGLRenderingContext.prototype.depthFunc');
		native(WebGLRenderingContext.prototype.depthMask,'WebGLRenderingContext.prototype.depthMask');
		native(WebGLRenderingContext.prototype.depthRange,'WebGLRenderingContext.prototype.depthRange');
		native(WebGLRenderingContext.prototype.detachShader,'WebGLRenderingContext.prototype.detachShader');
		native(WebGLRenderingContext.prototype.disable,'WebGLRenderingContext.prototype.disable');
		native(WebGLRenderingContext.prototype.disableVertexAttribArray,'WebGLRenderingContext.prototype.disableVertexAttribArray');
		native(WebGLRenderingContext.prototype.drawArrays,'WebGLRenderingContext.prototype.drawArrays');
		native(WebGLRenderingContext.prototype.drawElements,'WebGLRenderingContext.prototype.drawElements');
		native(WebGLRenderingContext.prototype.enable,'WebGLRenderingContext.prototype.enable');
		native(WebGLRenderingContext.prototype.enableVertexAttribArray,'WebGLRenderingContext.prototype.enableVertexAttribArray');
		native(WebGLRenderingContext.prototype.finish,'WebGLRenderingContext.prototype.finish');
		native(WebGLRenderingContext.prototype.flush,'WebGLRenderingContext.prototype.flush');
		native(WebGLRenderingContext.prototype.framebufferRenderbuffer,'WebGLRenderingContext.prototype.framebufferRenderbuffer');
		native(WebGLRenderingContext.prototype.framebufferTexture2D,'WebGLRenderingContext.prototype.framebufferTexture2D');
		native(WebGLRenderingContext.prototype.frontFace,'WebGLRenderingContext.prototype.frontFace');
		native(WebGLRenderingContext.prototype.generateMipmap,'WebGLRenderingContext.prototype.generateMipmap');
		native(WebGLRenderingContext.prototype.getActiveAttrib,'WebGLRenderingContext.prototype.getActiveAttrib');
		native(WebGLRenderingContext.prototype.getActiveUniform,'WebGLRenderingContext.prototype.getActiveUniform');
		native(WebGLRenderingContext.prototype.getAttachedShaders,'WebGLRenderingContext.prototype.getAttachedShaders');
		native(WebGLRenderingContext.prototype.getAttribLocation,'WebGLRenderingContext.prototype.getAttribLocation');
		native(WebGLRenderingContext.prototype.getBufferParameter,'WebGLRenderingContext.prototype.getBufferParameter');
		native(WebGLRenderingContext.prototype.getContextAttributes,'WebGLRenderingContext.prototype.getContextAttributes');
		native(WebGLRenderingContext.prototype.getError,'WebGLRenderingContext.prototype.getError');
		native(WebGLRenderingContext.prototype.getExtension,'WebGLRenderingContext.prototype.getExtension');
		native(WebGLRenderingContext.prototype.getFramebufferAttachmentParameter,'WebGLRenderingContext.prototype.getFramebufferAttachmentParameter');
		native(WebGLRenderingContext.prototype.getParameter,'WebGLRenderingContext.prototype.getParameter');
		native(WebGLRenderingContext.prototype.getProgramParameter,'WebGLRenderingContext.prototype.getProgramParameter');
		native(WebGLRenderingContext.prototype.getProgramInfoLog,'WebGLRenderingContext.prototype.getProgramInfoLog');
		native(WebGLRenderingContext.prototype.getRenderbufferParameter,'WebGLRenderingContext.prototype.getRenderbufferParameter');
		native(WebGLRenderingContext.prototype.getShaderParameter,'WebGLRenderingContext.prototype.getShaderParameter');
		native(WebGLRenderingContext.prototype.getShaderInfoLog,'WebGLRenderingContext.prototype.getShaderInfoLog');
		native(WebGLRenderingContext.prototype.getShaderPrecisionFormat,'WebGLRenderingContext.prototype.getShaderPrecisionFormat');
		native(WebGLRenderingContext.prototype.getShaderSource,'WebGLRenderingContext.prototype.getShaderSource');
		native(WebGLRenderingContext.prototype.getSupportedExtensions,'WebGLRenderingContext.prototype.getSupportedExtensions');
		native(WebGLRenderingContext.prototype.getTexParameter,'WebGLRenderingContext.prototype.getTexParameter');
		native(WebGLRenderingContext.prototype.getUniform,'WebGLRenderingContext.prototype.getUniform');
		native(WebGLRenderingContext.prototype.getUniformLocation,'WebGLRenderingContext.prototype.getUniformLocation');
		native(WebGLRenderingContext.prototype.getVertexAttrib,'WebGLRenderingContext.prototype.getVertexAttrib');
		native(WebGLRenderingContext.prototype.getVertexAttribOffset,'WebGLRenderingContext.prototype.getVertexAttribOffset');
		native(WebGLRenderingContext.prototype.hint,'WebGLRenderingContext.prototype.hint');
		native(WebGLRenderingContext.prototype.isBuffer,'WebGLRenderingContext.prototype.isBuffer');
		native(WebGLRenderingContext.prototype.isContextLost,'WebGLRenderingContext.prototype.isContextLost');
		native(WebGLRenderingContext.prototype.isEnabled,'WebGLRenderingContext.prototype.isEnabled');
		native(WebGLRenderingContext.prototype.isFramebuffer,'WebGLRenderingContext.prototype.isFramebuffer');
		native(WebGLRenderingContext.prototype.isProgram,'WebGLRenderingContext.prototype.isProgram');
		native(WebGLRenderingContext.prototype.isRenderbuffer,'WebGLRenderingContext.prototype.isRenderbuffer');
		native(WebGLRenderingContext.prototype.isShader,'WebGLRenderingContext.prototype.isShader');
		native(WebGLRenderingContext.prototype.isTexture,'WebGLRenderingContext.prototype.isTexture');
		native(WebGLRenderingContext.prototype.lineWidth,'WebGLRenderingContext.prototype.lineWidth');
		native(WebGLRenderingContext.prototype.linkProgram,'WebGLRenderingContext.prototype.linkProgram');
		native(WebGLRenderingContext.prototype.pixelStorei,'WebGLRenderingContext.prototype.pixelStorei');
		native(WebGLRenderingContext.prototype.polygonOffset,'WebGLRenderingContext.prototype.polygonOffset');
		native(WebGLRenderingContext.prototype.readPixels,'WebGLRenderingContext.prototype.readPixels');
		native(WebGLRenderingContext.prototype.renderbufferStorage,'WebGLRenderingContext.prototype.renderbufferStorage');
		native(WebGLRenderingContext.prototype.sampleCoverage,'WebGLRenderingContext.prototype.sampleCoverage');
		native(WebGLRenderingContext.prototype.scissor,'WebGLRenderingContext.prototype.scissor');
		native(WebGLRenderingContext.prototype.shaderSource,'WebGLRenderingContext.prototype.shaderSource');
		native(WebGLRenderingContext.prototype.stencilFunc,'WebGLRenderingContext.prototype.stencilFunc');
		native(WebGLRenderingContext.prototype.stencilFuncSeparate,'WebGLRenderingContext.prototype.stencilFuncSeparate');
		native(WebGLRenderingContext.prototype.stencilMask,'WebGLRenderingContext.prototype.stencilMask');
		native(WebGLRenderingContext.prototype.stencilMaskSeparate,'WebGLRenderingContext.prototype.stencilMaskSeparate');
		native(WebGLRenderingContext.prototype.stencilOp,'WebGLRenderingContext.prototype.stencilOp');
		native(WebGLRenderingContext.prototype.stencilOpSeparate,'WebGLRenderingContext.prototype.stencilOpSeparate');
		native(WebGLRenderingContext.prototype.texParameterf,'WebGLRenderingContext.prototype.texParameterf');
		native(WebGLRenderingContext.prototype.texParameteri,'WebGLRenderingContext.prototype.texParameteri');
		native(WebGLRenderingContext.prototype.texImage2D,'WebGLRenderingContext.prototype.texImage2D');
		native(WebGLRenderingContext.prototype.texSubImage2D,'WebGLRenderingContext.prototype.texSubImage2D');
		native(WebGLRenderingContext.prototype.uniform1f,'WebGLRenderingContext.prototype.uniform1f');
		native(WebGLRenderingContext.prototype.uniform1fv,'WebGLRenderingContext.prototype.uniform1fv');
		native(WebGLRenderingContext.prototype.uniform1i,'WebGLRenderingContext.prototype.uniform1i');
		native(WebGLRenderingContext.prototype.uniform1iv,'WebGLRenderingContext.prototype.uniform1iv');
		native(WebGLRenderingContext.prototype.uniform2f,'WebGLRenderingContext.prototype.uniform2f');
		native(WebGLRenderingContext.prototype.uniform2fv,'WebGLRenderingContext.prototype.uniform2fv');
		native(WebGLRenderingContext.prototype.uniform2i,'WebGLRenderingContext.prototype.uniform2i');
		native(WebGLRenderingContext.prototype.uniform2iv,'WebGLRenderingContext.prototype.uniform2iv');
		native(WebGLRenderingContext.prototype.uniform3f,'WebGLRenderingContext.prototype.uniform3f');
		native(WebGLRenderingContext.prototype.uniform3fv,'WebGLRenderingContext.prototype.uniform3fv');
		native(WebGLRenderingContext.prototype.uniform3i,'WebGLRenderingContext.prototype.uniform3i');
		native(WebGLRenderingContext.prototype.uniform3iv,'WebGLRenderingContext.prototype.uniform3iv');
		native(WebGLRenderingContext.prototype.uniform4f,'WebGLRenderingContext.prototype.uniform4f');
		native(WebGLRenderingContext.prototype.uniform4fv,'WebGLRenderingContext.prototype.uniform4fv');
		native(WebGLRenderingContext.prototype.uniform4i,'WebGLRenderingContext.prototype.uniform4i');
		native(WebGLRenderingContext.prototype.uniform4iv,'WebGLRenderingContext.prototype.uniform4iv');
		native(WebGLRenderingContext.prototype.uniformMatrix2fv,'WebGLRenderingContext.prototype.uniformMatrix2fv');
		native(WebGLRenderingContext.prototype.uniformMatrix3fv,'WebGLRenderingContext.prototype.uniformMatrix3fv');
		native(WebGLRenderingContext.prototype.uniformMatrix4fv,'WebGLRenderingContext.prototype.uniformMatrix4fv');
		native(WebGLRenderingContext.prototype.useProgram,'WebGLRenderingContext.prototype.useProgram');
		native(WebGLRenderingContext.prototype.validateProgram,'WebGLRenderingContext.prototype.validateProgram');
		native(WebGLRenderingContext.prototype.vertexAttrib1f,'WebGLRenderingContext.prototype.vertexAttrib1f');
		native(WebGLRenderingContext.prototype.vertexAttrib1fv,'WebGLRenderingContext.prototype.vertexAttrib1fv');
		native(WebGLRenderingContext.prototype.vertexAttrib2f,'WebGLRenderingContext.prototype.vertexAttrib2f');
		native(WebGLRenderingContext.prototype.vertexAttrib2fv,'WebGLRenderingContext.prototype.vertexAttrib2fv');
		native(WebGLRenderingContext.prototype.vertexAttrib3f,'WebGLRenderingContext.prototype.vertexAttrib3f');
		native(WebGLRenderingContext.prototype.vertexAttrib3fv,'WebGLRenderingContext.prototype.vertexAttrib3fv');
		native(WebGLRenderingContext.prototype.vertexAttrib4f,'WebGLRenderingContext.prototype.vertexAttrib4f');
		native(WebGLRenderingContext.prototype.vertexAttrib4fv,'WebGLRenderingContext.prototype.vertexAttrib4fv');
		native(WebGLRenderingContext.prototype.vertexAttribPointer,'WebGLRenderingContext.prototype.vertexAttribPointer');
		native(WebGLRenderingContext.prototype.viewport,'WebGLRenderingContext.prototype.viewport');
		native(VTTCue.prototype.getCueAsHTML,'VTTCue.prototype.getCueAsHTML');
		native(URL.prototype.toString,'URL.prototype.toString');
		native(UIEvent.prototype.initUIEvent,'UIEvent.prototype.initUIEvent');
		native(TreeWalker.prototype.parentNode,'TreeWalker.prototype.parentNode');
		native(TreeWalker.prototype.firstChild,'TreeWalker.prototype.firstChild');
		native(TreeWalker.prototype.lastChild,'TreeWalker.prototype.lastChild');
		native(TreeWalker.prototype.previousSibling,'TreeWalker.prototype.previousSibling');
		native(TreeWalker.prototype.nextSibling,'TreeWalker.prototype.nextSibling');
		native(TreeWalker.prototype.previousNode,'TreeWalker.prototype.previousNode');
		native(TreeWalker.prototype.nextNode,'TreeWalker.prototype.nextNode');
		native(TouchList.prototype.item,'TouchList.prototype.item');
		native(TouchEvent.prototype.initTouchEvent,'TouchEvent.prototype.initTouchEvent');
		native(TextTrackList.prototype.item,'TextTrackList.prototype.item');
		native(TextTrackList.prototype.getTrackById,'TextTrackList.prototype.getTrackById');
		native(TextTrackCueList.prototype.item,'TextTrackCueList.prototype.item');
		native(TextTrackCueList.prototype.getCueById,'TextTrackCueList.prototype.getCueById');
		native(TextTrack.prototype.addCue,'TextTrack.prototype.addCue');
		native(TextTrack.prototype.removeCue,'TextTrack.prototype.removeCue');
		native(TextEvent.prototype.initTextEvent,'TextEvent.prototype.initTextEvent');
		native(Text.prototype.splitText,'Text.prototype.splitText');
		native(Text.prototype.getDestinationInsertionPoints,'Text.prototype.getDestinationInsertionPoints');
		native(StyleSheetList.prototype.item,'StyleSheetList.prototype.item');
		native(StorageEvent.prototype.initStorageEvent,'StorageEvent.prototype.initStorageEvent');
		native(Storage.prototype.key,'Storage.prototype.key');
		native(Storage.prototype.getItem,'Storage.prototype.getItem');
		native(Storage.prototype.setItem,'Storage.prototype.setItem');
		native(Storage.prototype.removeItem,'Storage.prototype.removeItem');
		native(Storage.prototype.clear,'Storage.prototype.clear');
		native(ShadowRoot.prototype.cloneNode,'ShadowRoot.prototype.cloneNode');
		native(ShadowRoot.prototype.getSelection,'ShadowRoot.prototype.getSelection');
		native(ShadowRoot.prototype.getElementsByClassName,'ShadowRoot.prototype.getElementsByClassName');
		native(ShadowRoot.prototype.getElementsByTagName,'ShadowRoot.prototype.getElementsByTagName');
		native(ShadowRoot.prototype.getElementsByTagNameNS,'ShadowRoot.prototype.getElementsByTagNameNS');
		native(ShadowRoot.prototype.elementFromPoint,'ShadowRoot.prototype.elementFromPoint');
		native(Selection.prototype.collapse,'Selection.prototype.collapse');
		native(Selection.prototype.collapseToStart,'Selection.prototype.collapseToStart');
		native(Selection.prototype.collapseToEnd,'Selection.prototype.collapseToEnd');
		native(Selection.prototype.extend,'Selection.prototype.extend');
		native(Selection.prototype.selectAllChildren,'Selection.prototype.selectAllChildren');
		native(Selection.prototype.deleteFromDocument,'Selection.prototype.deleteFromDocument');
		native(Selection.prototype.getRangeAt,'Selection.prototype.getRangeAt');
		native(Selection.prototype.addRange,'Selection.prototype.addRange');
		native(Selection.prototype.removeAllRanges,'Selection.prototype.removeAllRanges');
		native(Selection.prototype.containsNode,'Selection.prototype.containsNode');
		native(Selection.prototype.modify,'Selection.prototype.modify');
		native(Selection.prototype.setBaseAndExtent,'Selection.prototype.setBaseAndExtent');
		native(Selection.prototype.setPosition,'Selection.prototype.setPosition');
		native(Selection.prototype.empty,'Selection.prototype.empty');
		native(Selection.prototype.toString,'Selection.prototype.toString');
		native(SVGTextContentElement.prototype.getNumberOfChars,'SVGTextContentElement.prototype.getNumberOfChars');
		native(SVGTextContentElement.prototype.getComputedTextLength,'SVGTextContentElement.prototype.getComputedTextLength');
		native(SVGTextContentElement.prototype.getSubStringLength,'SVGTextContentElement.prototype.getSubStringLength');
		native(SVGTextContentElement.prototype.getStartPositionOfChar,'SVGTextContentElement.prototype.getStartPositionOfChar');
		native(SVGTextContentElement.prototype.getEndPositionOfChar,'SVGTextContentElement.prototype.getEndPositionOfChar');
		native(SVGTextContentElement.prototype.getExtentOfChar,'SVGTextContentElement.prototype.getExtentOfChar');
		native(SVGTextContentElement.prototype.getRotationOfChar,'SVGTextContentElement.prototype.getRotationOfChar');
		native(SVGTextContentElement.prototype.getCharNumAtPosition,'SVGTextContentElement.prototype.getCharNumAtPosition');
		native(SVGTextContentElement.prototype.selectSubString,'SVGTextContentElement.prototype.selectSubString');
		native(SVGSVGElement.prototype.pauseAnimations,'SVGSVGElement.prototype.pauseAnimations');
		native(SVGSVGElement.prototype.unpauseAnimations,'SVGSVGElement.prototype.unpauseAnimations');
		native(SVGSVGElement.prototype.animationsPaused,'SVGSVGElement.prototype.animationsPaused');
		native(SVGSVGElement.prototype.getCurrentTime,'SVGSVGElement.prototype.getCurrentTime');
		native(SVGSVGElement.prototype.setCurrentTime,'SVGSVGElement.prototype.setCurrentTime');
		native(SVGSVGElement.prototype.getIntersectionList,'SVGSVGElement.prototype.getIntersectionList');
		native(SVGSVGElement.prototype.getEnclosureList,'SVGSVGElement.prototype.getEnclosureList');
		native(SVGSVGElement.prototype.checkIntersection,'SVGSVGElement.prototype.checkIntersection');
		native(SVGSVGElement.prototype.checkEnclosure,'SVGSVGElement.prototype.checkEnclosure');
		native(SVGSVGElement.prototype.deselectAll,'SVGSVGElement.prototype.deselectAll');
		native(SVGSVGElement.prototype.getElementById,'SVGSVGElement.prototype.getElementById');
		native(SVGSVGElement.prototype.suspendRedraw,'SVGSVGElement.prototype.suspendRedraw');
		native(SVGSVGElement.prototype.unsuspendRedraw,'SVGSVGElement.prototype.unsuspendRedraw');
		native(SVGSVGElement.prototype.unsuspendRedrawAll,'SVGSVGElement.prototype.unsuspendRedrawAll');
		native(SVGSVGElement.prototype.forceRedraw,'SVGSVGElement.prototype.forceRedraw');
		native(SVGSVGElement.prototype.createSVGNumber,'SVGSVGElement.prototype.createSVGNumber');
		native(SVGSVGElement.prototype.createSVGLength,'SVGSVGElement.prototype.createSVGLength');
		native(SVGSVGElement.prototype.createSVGAngle,'SVGSVGElement.prototype.createSVGAngle');
		native(SVGSVGElement.prototype.createSVGPoint,'SVGSVGElement.prototype.createSVGPoint');
		native(SVGSVGElement.prototype.createSVGMatrix,'SVGSVGElement.prototype.createSVGMatrix');
		native(SVGSVGElement.prototype.createSVGRect,'SVGSVGElement.prototype.createSVGRect');
		native(SVGSVGElement.prototype.createSVGTransform,'SVGSVGElement.prototype.createSVGTransform');
		native(SVGSVGElement.prototype.createSVGTransformFromMatrix,'SVGSVGElement.prototype.createSVGTransformFromMatrix');
		native(SVGPoint.prototype.matrixTransform,'SVGPoint.prototype.matrixTransform');
		native(SVGPatternElement.prototype.hasExtension,'SVGPatternElement.prototype.hasExtension');
		native(SVGPathElement.prototype.getTotalLength,'SVGPathElement.prototype.getTotalLength');
		native(SVGPathElement.prototype.getPointAtLength,'SVGPathElement.prototype.getPointAtLength');
		native(SVGPathElement.prototype.getPathSegAtLength,'SVGPathElement.prototype.getPathSegAtLength');
		native(SVGPathElement.prototype.createSVGPathSegClosePath,'SVGPathElement.prototype.createSVGPathSegClosePath');
		native(SVGPathElement.prototype.createSVGPathSegMovetoAbs,'SVGPathElement.prototype.createSVGPathSegMovetoAbs');
		native(SVGPathElement.prototype.createSVGPathSegMovetoRel,'SVGPathElement.prototype.createSVGPathSegMovetoRel');
		native(SVGPathElement.prototype.createSVGPathSegLinetoAbs,'SVGPathElement.prototype.createSVGPathSegLinetoAbs');
		native(SVGPathElement.prototype.createSVGPathSegLinetoRel,'SVGPathElement.prototype.createSVGPathSegLinetoRel');
		native(SVGPathElement.prototype.createSVGPathSegCurvetoCubicAbs,'SVGPathElement.prototype.createSVGPathSegCurvetoCubicAbs');
		native(SVGPathElement.prototype.createSVGPathSegCurvetoCubicRel,'SVGPathElement.prototype.createSVGPathSegCurvetoCubicRel');
		native(SVGPathElement.prototype.createSVGPathSegCurvetoQuadraticAbs,'SVGPathElement.prototype.createSVGPathSegCurvetoQuadraticAbs');
		native(SVGPathElement.prototype.createSVGPathSegCurvetoQuadraticRel,'SVGPathElement.prototype.createSVGPathSegCurvetoQuadraticRel');
		native(SVGPathElement.prototype.createSVGPathSegArcAbs,'SVGPathElement.prototype.createSVGPathSegArcAbs');
		native(SVGPathElement.prototype.createSVGPathSegArcRel,'SVGPathElement.prototype.createSVGPathSegArcRel');
		native(SVGPathElement.prototype.createSVGPathSegLinetoHorizontalAbs,'SVGPathElement.prototype.createSVGPathSegLinetoHorizontalAbs');
		native(SVGPathElement.prototype.createSVGPathSegLinetoHorizontalRel,'SVGPathElement.prototype.createSVGPathSegLinetoHorizontalRel');
		native(SVGPathElement.prototype.createSVGPathSegLinetoVerticalAbs,'SVGPathElement.prototype.createSVGPathSegLinetoVerticalAbs');
		native(SVGPathElement.prototype.createSVGPathSegLinetoVerticalRel,'SVGPathElement.prototype.createSVGPathSegLinetoVerticalRel');
		native(SVGPathElement.prototype.createSVGPathSegCurvetoCubicSmoothAbs,'SVGPathElement.prototype.createSVGPathSegCurvetoCubicSmoothAbs');
		native(SVGPathElement.prototype.createSVGPathSegCurvetoCubicSmoothRel,'SVGPathElement.prototype.createSVGPathSegCurvetoCubicSmoothRel');
		native(SVGPathElement.prototype.createSVGPathSegCurvetoQuadraticSmoothAbs,'SVGPathElement.prototype.createSVGPathSegCurvetoQuadraticSmoothAbs');
		native(SVGPathElement.prototype.createSVGPathSegCurvetoQuadraticSmoothRel,'SVGPathElement.prototype.createSVGPathSegCurvetoQuadraticSmoothRel');
		native(SVGMatrix.prototype.multiply,'SVGMatrix.prototype.multiply');
		native(SVGMatrix.prototype.inverse,'SVGMatrix.prototype.inverse');
		native(SVGMatrix.prototype.translate,'SVGMatrix.prototype.translate');
		native(SVGMatrix.prototype.scale,'SVGMatrix.prototype.scale');
		native(SVGMatrix.prototype.scaleNonUniform,'SVGMatrix.prototype.scaleNonUniform');
		native(SVGMatrix.prototype.rotate,'SVGMatrix.prototype.rotate');
		native(SVGMatrix.prototype.rotateFromVector,'SVGMatrix.prototype.rotateFromVector');
		native(SVGMatrix.prototype.flipX,'SVGMatrix.prototype.flipX');
		native(SVGMatrix.prototype.flipY,'SVGMatrix.prototype.flipY');
		native(SVGMatrix.prototype.skewX,'SVGMatrix.prototype.skewX');
		native(SVGMatrix.prototype.skewY,'SVGMatrix.prototype.skewY');
		native(SVGMaskElement.prototype.hasExtension,'SVGMaskElement.prototype.hasExtension');
		native(SVGMarkerElement.prototype.setOrientToAuto,'SVGMarkerElement.prototype.setOrientToAuto');
		native(SVGMarkerElement.prototype.setOrientToAngle,'SVGMarkerElement.prototype.setOrientToAngle');
		native(SVGGraphicsElement.prototype.getBBox,'SVGGraphicsElement.prototype.getBBox');
		native(SVGGraphicsElement.prototype.getCTM,'SVGGraphicsElement.prototype.getCTM');
		native(SVGGraphicsElement.prototype.getScreenCTM,'SVGGraphicsElement.prototype.getScreenCTM');
		native(SVGGraphicsElement.prototype.getTransformToElement,'SVGGraphicsElement.prototype.getTransformToElement');
		native(SVGGraphicsElement.prototype.hasExtension,'SVGGraphicsElement.prototype.hasExtension');
		native(SVGGeometryElement.prototype.isPointInFill,'SVGGeometryElement.prototype.isPointInFill');
		native(SVGGeometryElement.prototype.isPointInStroke,'SVGGeometryElement.prototype.isPointInStroke');
		native(SVGFilterElement.prototype.setFilterRes,'SVGFilterElement.prototype.setFilterRes');
		native(SVGFEGaussianBlurElement.prototype.setStdDeviation,'SVGFEGaussianBlurElement.prototype.setStdDeviation');
		native(SVGFEDropShadowElement.prototype.setStdDeviation,'SVGFEDropShadowElement.prototype.setStdDeviation');
		native(SVGCursorElement.prototype.hasExtension,'SVGCursorElement.prototype.hasExtension');
		native(SVGAnimationElement.prototype.getStartTime,'SVGAnimationElement.prototype.getStartTime');
		native(SVGAnimationElement.prototype.getCurrentTime,'SVGAnimationElement.prototype.getCurrentTime');
		native(SVGAnimationElement.prototype.getSimpleDuration,'SVGAnimationElement.prototype.getSimpleDuration');
		native(SVGAnimationElement.prototype.beginElement,'SVGAnimationElement.prototype.beginElement');
		native(SVGAnimationElement.prototype.beginElementAt,'SVGAnimationElement.prototype.beginElementAt');
		native(SVGAnimationElement.prototype.endElement,'SVGAnimationElement.prototype.endElement');
		native(SVGAnimationElement.prototype.endElementAt,'SVGAnimationElement.prototype.endElementAt');
		native(SVGAnimationElement.prototype.hasExtension,'SVGAnimationElement.prototype.hasExtension');
		native(SVGAngle.prototype.newValueSpecifiedUnits,'SVGAngle.prototype.newValueSpecifiedUnits');
		native(SVGAngle.prototype.convertToSpecifiedUnits,'SVGAngle.prototype.convertToSpecifiedUnits');
		native(Range.prototype.setStart,'Range.prototype.setStart');
		native(Range.prototype.setEnd,'Range.prototype.setEnd');
		native(Range.prototype.setStartBefore,'Range.prototype.setStartBefore');
		native(Range.prototype.setStartAfter,'Range.prototype.setStartAfter');
		native(Range.prototype.setEndBefore,'Range.prototype.setEndBefore');
		native(Range.prototype.setEndAfter,'Range.prototype.setEndAfter');
		native(Range.prototype.collapse,'Range.prototype.collapse');
		native(Range.prototype.selectNode,'Range.prototype.selectNode');
		native(Range.prototype.selectNodeContents,'Range.prototype.selectNodeContents');
		native(Range.prototype.compareBoundaryPoints,'Range.prototype.compareBoundaryPoints');
		native(Range.prototype.deleteContents,'Range.prototype.deleteContents');
		native(Range.prototype.extractContents,'Range.prototype.extractContents');
		native(Range.prototype.cloneContents,'Range.prototype.cloneContents');
		native(Range.prototype.insertNode,'Range.prototype.insertNode');
		native(Range.prototype.surroundContents,'Range.prototype.surroundContents');
		native(Range.prototype.cloneRange,'Range.prototype.cloneRange');
		native(Range.prototype.detach,'Range.prototype.detach');
		native(Range.prototype.isPointInRange,'Range.prototype.isPointInRange');
		native(Range.prototype.comparePoint,'Range.prototype.comparePoint');
		native(Range.prototype.intersectsNode,'Range.prototype.intersectsNode');
		native(Range.prototype.getClientRects,'Range.prototype.getClientRects');
		native(Range.prototype.getBoundingClientRect,'Range.prototype.getBoundingClientRect');
		native(Range.prototype.createContextualFragment,'Range.prototype.createContextualFragment');
		native(Range.prototype.compareNode,'Range.prototype.compareNode');
		native(Range.prototype.expand,'Range.prototype.expand');
		native(Range.prototype.toString,'Range.prototype.toString');
		native(Plugin.prototype.item,'Plugin.prototype.item');
		native(Plugin.prototype.namedItem,'Plugin.prototype.namedItem');
		native(PluginArray.prototype.item,'PluginArray.prototype.item');
		native(PluginArray.prototype.namedItem,'PluginArray.prototype.namedItem');
		native(PluginArray.prototype.refresh,'PluginArray.prototype.refresh');
		native(Performance.prototype.getEntries,'Performance.prototype.getEntries');
		native(Performance.prototype.getEntriesByType,'Performance.prototype.getEntriesByType');
		native(Performance.prototype.getEntriesByName,'Performance.prototype.getEntriesByName');
		native(Performance.prototype.webkitClearResourceTimings,'Performance.prototype.webkitClearResourceTimings');
		native(Performance.prototype.webkitSetResourceTimingBufferSize,'Performance.prototype.webkitSetResourceTimingBufferSize');
		native(Performance.prototype.mark,'Performance.prototype.mark');
		native(Performance.prototype.clearMarks,'Performance.prototype.clearMarks');
		native(Performance.prototype.measure,'Performance.prototype.measure');
		native(Performance.prototype.clearMeasures,'Performance.prototype.clearMeasures');
		native(Performance.prototype.now,'Performance.prototype.now');
		native(Path2D.prototype.closePath,'Path2D.prototype.closePath');
		native(Path2D.prototype.moveTo,'Path2D.prototype.moveTo');
		native(Path2D.prototype.lineTo,'Path2D.prototype.lineTo');
		native(Path2D.prototype.quadraticCurveTo,'Path2D.prototype.quadraticCurveTo');
		native(Path2D.prototype.bezierCurveTo,'Path2D.prototype.bezierCurveTo');
		native(Path2D.prototype.arcTo,'Path2D.prototype.arcTo');
		native(Path2D.prototype.rect,'Path2D.prototype.rect');
		native(Path2D.prototype.arc,'Path2D.prototype.arc');
		native(Path2D.prototype.ellipse,'Path2D.prototype.ellipse');
		native(NodeList.prototype.item,'NodeList.prototype.item');
		native(NodeIterator.prototype.nextNode,'NodeIterator.prototype.nextNode');
		native(NodeIterator.prototype.previousNode,'NodeIterator.prototype.previousNode');
		native(NodeIterator.prototype.detach,'NodeIterator.prototype.detach');
		native(NodeFilter.prototype.acceptNode,'NodeFilter.prototype.acceptNode');
		native(Node.prototype.insertBefore,'Node.prototype.insertBefore');
		native(Node.prototype.replaceChild,'Node.prototype.replaceChild');
		native(Node.prototype.removeChild,'Node.prototype.removeChild');
		native(Node.prototype.appendChild,'Node.prototype.appendChild');
		native(Node.prototype.hasChildNodes,'Node.prototype.hasChildNodes');
		native(Node.prototype.cloneNode,'Node.prototype.cloneNode');
		native(Node.prototype.normalize,'Node.prototype.normalize');
		native(Node.prototype.isSameNode,'Node.prototype.isSameNode');
		native(Node.prototype.isEqualNode,'Node.prototype.isEqualNode');
		native(Node.prototype.lookupPrefix,'Node.prototype.lookupPrefix');
		native(Node.prototype.isDefaultNamespace,'Node.prototype.isDefaultNamespace');
		native(Node.prototype.lookupNamespaceURI,'Node.prototype.lookupNamespaceURI');
		native(Node.prototype.compareDocumentPosition,'Node.prototype.compareDocumentPosition');
		native(Node.prototype.contains,'Node.prototype.contains');
		native(Navigator.prototype.javaEnabled,'Navigator.prototype.javaEnabled');
		native(Navigator.prototype.getStorageUpdates,'Navigator.prototype.getStorageUpdates');
		native(Navigator.prototype.getGamepads,'Navigator.prototype.getGamepads');
		native(Navigator.prototype.webkitGetUserMedia,'Navigator.prototype.webkitGetUserMedia');
		native(Navigator.prototype.vibrate,'Navigator.prototype.vibrate');
		native(Navigator.prototype.getBattery,'Navigator.prototype.getBattery');
		native(Navigator.prototype.sendBeacon,'Navigator.prototype.sendBeacon');
		native(Navigator.prototype.registerProtocolHandler,'Navigator.prototype.registerProtocolHandler');
		native(Navigator.prototype.unregisterProtocolHandler,'Navigator.prototype.unregisterProtocolHandler');
		native(NamedNodeMap.prototype.getNamedItem,'NamedNodeMap.prototype.getNamedItem');
		native(NamedNodeMap.prototype.setNamedItem,'NamedNodeMap.prototype.setNamedItem');
		native(NamedNodeMap.prototype.removeNamedItem,'NamedNodeMap.prototype.removeNamedItem');
		native(NamedNodeMap.prototype.item,'NamedNodeMap.prototype.item');
		native(NamedNodeMap.prototype.getNamedItemNS,'NamedNodeMap.prototype.getNamedItemNS');
		native(NamedNodeMap.prototype.setNamedItemNS,'NamedNodeMap.prototype.setNamedItemNS');
		native(NamedNodeMap.prototype.removeNamedItemNS,'NamedNodeMap.prototype.removeNamedItemNS');
		native(MutationObserver.prototype.observe,'MutationObserver.prototype.observe');
		native(MutationObserver.prototype.takeRecords,'MutationObserver.prototype.takeRecords');
		native(MutationObserver.prototype.disconnect,'MutationObserver.prototype.disconnect');
		native(MutationEvent.prototype.initMutationEvent,'MutationEvent.prototype.initMutationEvent');
		native(MouseEvent.prototype.initMouseEvent,'MouseEvent.prototype.initMouseEvent');
		native(MimeTypeArray.prototype.item,'MimeTypeArray.prototype.item');
		native(MimeTypeArray.prototype.namedItem,'MimeTypeArray.prototype.namedItem');
		native(MessagePort.prototype.postMessage,'MessagePort.prototype.postMessage');
		native(MessagePort.prototype.start,'MessagePort.prototype.start');
		native(MessagePort.prototype.close,'MessagePort.prototype.close');
		native(MessageEvent.prototype.initMessageEvent,'MessageEvent.prototype.initMessageEvent');
		native(MediaQueryList.prototype.addListener,'MediaQueryList.prototype.addListener');
		native(MediaQueryList.prototype.removeListener,'MediaQueryList.prototype.removeListener');
		native(MediaList.prototype.item,'MediaList.prototype.item');
		native(MediaList.prototype.deleteMedium,'MediaList.prototype.deleteMedium');
		native(MediaList.prototype.appendMedium,'MediaList.prototype.appendMedium');
		native(KeyboardEvent.prototype.getModifierState,'KeyboardEvent.prototype.getModifierState');
		native(KeyboardEvent.prototype.initKeyboardEvent,'KeyboardEvent.prototype.initKeyboardEvent');
		native(InputMethodContext.prototype.confirmComposition,'InputMethodContext.prototype.confirmComposition');
		native(History.prototype.back,'History.prototype.back');
		native(History.prototype.forward,'History.prototype.forward');
		native(History.prototype.go,'History.prototype.go');
		native(History.prototype.pushState,'History.prototype.pushState');
		native(History.prototype.replaceState,'History.prototype.replaceState');
		native(HashChangeEvent.prototype.initHashChangeEvent,'HashChangeEvent.prototype.initHashChangeEvent');
		native(HTMLTextAreaElement.prototype.checkValidity,'HTMLTextAreaElement.prototype.checkValidity');
		native(HTMLTextAreaElement.prototype.reportValidity,'HTMLTextAreaElement.prototype.reportValidity');
		native(HTMLTextAreaElement.prototype.setCustomValidity,'HTMLTextAreaElement.prototype.setCustomValidity');
		native(HTMLTextAreaElement.prototype.select,'HTMLTextAreaElement.prototype.select');
		native(HTMLTextAreaElement.prototype.setRangeText,'HTMLTextAreaElement.prototype.setRangeText');
		native(HTMLTextAreaElement.prototype.setSelectionRange,'HTMLTextAreaElement.prototype.setSelectionRange');
		native(HTMLTableSectionElement.prototype.insertRow,'HTMLTableSectionElement.prototype.insertRow');
		native(HTMLTableSectionElement.prototype.deleteRow,'HTMLTableSectionElement.prototype.deleteRow');
		native(HTMLTableRowElement.prototype.insertCell,'HTMLTableRowElement.prototype.insertCell');
		native(HTMLTableRowElement.prototype.deleteCell,'HTMLTableRowElement.prototype.deleteCell');
		native(HTMLTableElement.prototype.createTHead,'HTMLTableElement.prototype.createTHead');
		native(HTMLTableElement.prototype.deleteTHead,'HTMLTableElement.prototype.deleteTHead');
		native(HTMLTableElement.prototype.createTFoot,'HTMLTableElement.prototype.createTFoot');
		native(HTMLTableElement.prototype.deleteTFoot,'HTMLTableElement.prototype.deleteTFoot');
		native(HTMLTableElement.prototype.createTBody,'HTMLTableElement.prototype.createTBody');
		native(HTMLTableElement.prototype.createCaption,'HTMLTableElement.prototype.createCaption');
		native(HTMLTableElement.prototype.deleteCaption,'HTMLTableElement.prototype.deleteCaption');
		native(HTMLTableElement.prototype.insertRow,'HTMLTableElement.prototype.insertRow');
		native(HTMLTableElement.prototype.deleteRow,'HTMLTableElement.prototype.deleteRow');
		native(HTMLShadowElement.prototype.getDistributedNodes,'HTMLShadowElement.prototype.getDistributedNodes');
		native(HTMLSelectElement.prototype.item,'HTMLSelectElement.prototype.item');
		native(HTMLSelectElement.prototype.namedItem,'HTMLSelectElement.prototype.namedItem');
		native(HTMLSelectElement.prototype.add,'HTMLSelectElement.prototype.add');
		native(HTMLSelectElement.prototype.remove,'HTMLSelectElement.prototype.remove');
		native(HTMLSelectElement.prototype.checkValidity,'HTMLSelectElement.prototype.checkValidity');
		native(HTMLSelectElement.prototype.reportValidity,'HTMLSelectElement.prototype.reportValidity');
		native(HTMLSelectElement.prototype.setCustomValidity,'HTMLSelectElement.prototype.setCustomValidity');
		native(HTMLOutputElement.prototype.checkValidity,'HTMLOutputElement.prototype.checkValidity');
		native(HTMLOutputElement.prototype.reportValidity,'HTMLOutputElement.prototype.reportValidity');
		native(HTMLOutputElement.prototype.setCustomValidity,'HTMLOutputElement.prototype.setCustomValidity');
		native(HTMLOptionsCollection.prototype.namedItem,'HTMLOptionsCollection.prototype.namedItem');
		native(HTMLOptionsCollection.prototype.add,'HTMLOptionsCollection.prototype.add');
		native(HTMLOptionsCollection.prototype.remove,'HTMLOptionsCollection.prototype.remove');
		native(HTMLObjectElement.prototype.checkValidity,'HTMLObjectElement.prototype.checkValidity');
		native(HTMLObjectElement.prototype.reportValidity,'HTMLObjectElement.prototype.reportValidity');
		native(HTMLObjectElement.prototype.setCustomValidity,'HTMLObjectElement.prototype.setCustomValidity');
		native(HTMLObjectElement.prototype.getSVGDocument,'HTMLObjectElement.prototype.getSVGDocument');
		native(HTMLMarqueeElement.prototype.start,'HTMLMarqueeElement.prototype.start');
		native(HTMLMarqueeElement.prototype.stop,'HTMLMarqueeElement.prototype.stop');
		native(HTMLKeygenElement.prototype.checkValidity,'HTMLKeygenElement.prototype.checkValidity');
		native(HTMLKeygenElement.prototype.reportValidity,'HTMLKeygenElement.prototype.reportValidity');
		native(HTMLKeygenElement.prototype.setCustomValidity,'HTMLKeygenElement.prototype.setCustomValidity');
		native(HTMLInputElement.prototype.stepUp,'HTMLInputElement.prototype.stepUp');
		native(HTMLInputElement.prototype.stepDown,'HTMLInputElement.prototype.stepDown');
		native(HTMLInputElement.prototype.checkValidity,'HTMLInputElement.prototype.checkValidity');
		native(HTMLInputElement.prototype.reportValidity,'HTMLInputElement.prototype.reportValidity');
		native(HTMLInputElement.prototype.setCustomValidity,'HTMLInputElement.prototype.setCustomValidity');
		native(HTMLInputElement.prototype.select,'HTMLInputElement.prototype.select');
		native(HTMLInputElement.prototype.setRangeText,'HTMLInputElement.prototype.setRangeText');
		native(HTMLInputElement.prototype.setSelectionRange,'HTMLInputElement.prototype.setSelectionRange');
		native(HTMLIFrameElement.prototype.getSVGDocument,'HTMLIFrameElement.prototype.getSVGDocument');
		native(HTMLFrameElement.prototype.getSVGDocument,'HTMLFrameElement.prototype.getSVGDocument');
		native(HTMLFormElement.prototype.submit,'HTMLFormElement.prototype.submit');
		native(HTMLFormElement.prototype.reset,'HTMLFormElement.prototype.reset');
		native(HTMLFormElement.prototype.checkValidity,'HTMLFormElement.prototype.checkValidity');
		native(HTMLFormElement.prototype.reportValidity,'HTMLFormElement.prototype.reportValidity');
		native(HTMLFormElement.prototype.requestAutocomplete,'HTMLFormElement.prototype.requestAutocomplete');
		native(HTMLFormControlsCollection.prototype.namedItem,'HTMLFormControlsCollection.prototype.namedItem');
		native(HTMLFieldSetElement.prototype.checkValidity,'HTMLFieldSetElement.prototype.checkValidity');
		native(HTMLFieldSetElement.prototype.reportValidity,'HTMLFieldSetElement.prototype.reportValidity');
		native(HTMLFieldSetElement.prototype.setCustomValidity,'HTMLFieldSetElement.prototype.setCustomValidity');
		native(HTMLEmbedElement.prototype.getSVGDocument,'HTMLEmbedElement.prototype.getSVGDocument');
		native(HTMLElement.prototype.click,'HTMLElement.prototype.click');
		native(HTMLDocument.prototype.open,'HTMLDocument.prototype.open');
		native(HTMLDocument.prototype.close,'HTMLDocument.prototype.close');
		native(HTMLDocument.prototype.write,'HTMLDocument.prototype.write');
		native(HTMLDocument.prototype.writeln,'HTMLDocument.prototype.writeln');
		native(HTMLDocument.prototype.clear,'HTMLDocument.prototype.clear');
		native(HTMLDocument.prototype.captureEvents,'HTMLDocument.prototype.captureEvents');
		native(HTMLDocument.prototype.releaseEvents,'HTMLDocument.prototype.releaseEvents');
		native(HTMLDialogElement.prototype.close,'HTMLDialogElement.prototype.close');
		native(HTMLDialogElement.prototype.show,'HTMLDialogElement.prototype.show');
		native(HTMLDialogElement.prototype.showModal,'HTMLDialogElement.prototype.showModal');
		native(HTMLContentElement.prototype.getDistributedNodes,'HTMLContentElement.prototype.getDistributedNodes');
		native(HTMLCollection.prototype.item,'HTMLCollection.prototype.item');
		native(HTMLCollection.prototype.namedItem,'HTMLCollection.prototype.namedItem');
		native(HTMLCanvasElement.prototype.toDataURL,'HTMLCanvasElement.prototype.toDataURL');
		native(HTMLCanvasElement.prototype.getContext,'HTMLCanvasElement.prototype.getContext');
		native(HTMLButtonElement.prototype.checkValidity,'HTMLButtonElement.prototype.checkValidity');
		native(HTMLButtonElement.prototype.reportValidity,'HTMLButtonElement.prototype.reportValidity');
		native(HTMLButtonElement.prototype.setCustomValidity,'HTMLButtonElement.prototype.setCustomValidity');
		native(HTMLAreaElement.prototype.toString,'HTMLAreaElement.prototype.toString');
		native(HTMLAnchorElement.prototype.toString,'HTMLAnchorElement.prototype.toString');
		native(HTMLAllCollection.prototype.item,'HTMLAllCollection.prototype.item');
		native(HTMLAllCollection.prototype.namedItem,'HTMLAllCollection.prototype.namedItem');
		native(FormData.prototype.append,'FormData.prototype.append');
		native(FontFace.prototype.load,'FontFace.prototype.load');
		native(FileReader.prototype.readAsArrayBuffer,'FileReader.prototype.readAsArrayBuffer');
		native(FileReader.prototype.readAsBinaryString,'FileReader.prototype.readAsBinaryString');
		native(FileReader.prototype.readAsText,'FileReader.prototype.readAsText');
		native(FileReader.prototype.readAsDataURL,'FileReader.prototype.readAsDataURL');
		native(FileReader.prototype.abort,'FileReader.prototype.abort');
		native(FileList.prototype.item,'FileList.prototype.item');
		native(EventTarget.prototype.dispatchEvent,'EventTarget.prototype.dispatchEvent');
		native(EventSource.prototype.close,'EventSource.prototype.close');
		native(Event.prototype.stopPropagation,'Event.prototype.stopPropagation');
		native(Event.prototype.preventDefault,'Event.prototype.preventDefault');
		native(Event.prototype.initEvent,'Event.prototype.initEvent');
		native(Event.prototype.stopImmediatePropagation,'Event.prototype.stopImmediatePropagation');
		native(Element.prototype.getAttribute,'Element.prototype.getAttribute');
		native(Element.prototype.setAttribute,'Element.prototype.setAttribute');
		native(Element.prototype.removeAttribute,'Element.prototype.removeAttribute');
		native(Element.prototype.getAttributeNode,'Element.prototype.getAttributeNode');
		native(Element.prototype.setAttributeNode,'Element.prototype.setAttributeNode');
		native(Element.prototype.removeAttributeNode,'Element.prototype.removeAttributeNode');
		native(Element.prototype.getElementsByTagName,'Element.prototype.getElementsByTagName');
		native(Element.prototype.hasAttributes,'Element.prototype.hasAttributes');
		native(Element.prototype.getAttributeNS,'Element.prototype.getAttributeNS');
		native(Element.prototype.setAttributeNS,'Element.prototype.setAttributeNS');
		native(Element.prototype.removeAttributeNS,'Element.prototype.removeAttributeNS');
		native(Element.prototype.getElementsByTagNameNS,'Element.prototype.getElementsByTagNameNS');
		native(Element.prototype.getAttributeNodeNS,'Element.prototype.getAttributeNodeNS');
		native(Element.prototype.setAttributeNodeNS,'Element.prototype.setAttributeNodeNS');
		native(Element.prototype.hasAttribute,'Element.prototype.hasAttribute');
		native(Element.prototype.hasAttributeNS,'Element.prototype.hasAttributeNS');
		native(Element.prototype.matches,'Element.prototype.matches');
		native(Element.prototype.closest,'Element.prototype.closest');
		native(Element.prototype.focus,'Element.prototype.focus');
		native(Element.prototype.blur,'Element.prototype.blur');
		native(Element.prototype.scrollIntoView,'Element.prototype.scrollIntoView');
		native(Element.prototype.scrollIntoViewIfNeeded,'Element.prototype.scrollIntoViewIfNeeded');
		native(Element.prototype.getElementsByClassName,'Element.prototype.getElementsByClassName');
		native(Element.prototype.insertAdjacentElement,'Element.prototype.insertAdjacentElement');
		native(Element.prototype.insertAdjacentText,'Element.prototype.insertAdjacentText');
		native(Element.prototype.insertAdjacentHTML,'Element.prototype.insertAdjacentHTML');
		native(Element.prototype.webkitMatchesSelector,'Element.prototype.webkitMatchesSelector');
		native(Element.prototype.createShadowRoot,'Element.prototype.createShadowRoot');
		native(Element.prototype.getDestinationInsertionPoints,'Element.prototype.getDestinationInsertionPoints');
		native(Element.prototype.getClientRects,'Element.prototype.getClientRects');
		native(Element.prototype.getBoundingClientRect,'Element.prototype.getBoundingClientRect');
		native(Element.prototype.requestPointerLock,'Element.prototype.requestPointerLock');
		native(Element.prototype.animate,'Element.prototype.animate');
		native(Element.prototype.remove,'Element.prototype.remove');
		native(Element.prototype.webkitRequestFullScreen,'Element.prototype.webkitRequestFullScreen');
		native(Element.prototype.webkitRequestFullscreen,'Element.prototype.webkitRequestFullscreen');
		native(Element.prototype.querySelector,'Element.prototype.querySelector');
		native(Element.prototype.querySelectorAll,'Element.prototype.querySelectorAll');
		native(DocumentType.prototype.remove,'DocumentType.prototype.remove');
		native(DocumentFragment.prototype.getElementById,'DocumentFragment.prototype.getElementById');
		native(DocumentFragment.prototype.querySelector,'DocumentFragment.prototype.querySelector');
		native(DocumentFragment.prototype.querySelectorAll,'DocumentFragment.prototype.querySelectorAll');
		native(Document.prototype.createDocumentFragment,'Document.prototype.createDocumentFragment');
		native(Document.prototype.createTextNode,'Document.prototype.createTextNode');
		native(Document.prototype.createComment,'Document.prototype.createComment');
		native(Document.prototype.createCDATASection,'Document.prototype.createCDATASection');
		native(Document.prototype.createProcessingInstruction,'Document.prototype.createProcessingInstruction');
		native(Document.prototype.createAttribute,'Document.prototype.createAttribute');
		native(Document.prototype.getElementsByTagName,'Document.prototype.getElementsByTagName');
		native(Document.prototype.importNode,'Document.prototype.importNode');
		native(Document.prototype.createAttributeNS,'Document.prototype.createAttributeNS');
		native(Document.prototype.getElementsByTagNameNS,'Document.prototype.getElementsByTagNameNS');
		native(Document.prototype.getElementById,'Document.prototype.getElementById');
		native(Document.prototype.adoptNode,'Document.prototype.adoptNode');
		native(Document.prototype.createEvent,'Document.prototype.createEvent');
		native(Document.prototype.createRange,'Document.prototype.createRange');
		native(Document.prototype.createNodeIterator,'Document.prototype.createNodeIterator');
		native(Document.prototype.createTreeWalker,'Document.prototype.createTreeWalker');
		native(Document.prototype.getOverrideStyle,'Document.prototype.getOverrideStyle');
		native(Document.prototype.execCommand,'Document.prototype.execCommand');
		native(Document.prototype.queryCommandEnabled,'Document.prototype.queryCommandEnabled');
		native(Document.prototype.queryCommandIndeterm,'Document.prototype.queryCommandIndeterm');
		native(Document.prototype.queryCommandState,'Document.prototype.queryCommandState');
		native(Document.prototype.queryCommandSupported,'Document.prototype.queryCommandSupported');
		native(Document.prototype.queryCommandValue,'Document.prototype.queryCommandValue');
		native(Document.prototype.getElementsByName,'Document.prototype.getElementsByName');
		native(Document.prototype.elementFromPoint,'Document.prototype.elementFromPoint');
		native(Document.prototype.caretRangeFromPoint,'Document.prototype.caretRangeFromPoint');
		native(Document.prototype.getSelection,'Document.prototype.getSelection');
		native(Document.prototype.getCSSCanvasContext,'Document.prototype.getCSSCanvasContext');
		native(Document.prototype.getElementsByClassName,'Document.prototype.getElementsByClassName');
		native(Document.prototype.hasFocus,'Document.prototype.hasFocus');
		native(Document.prototype.exitPointerLock,'Document.prototype.exitPointerLock');
		native(Document.prototype.registerElement,'Document.prototype.registerElement');
		native(Document.prototype.createElement,'Document.prototype.createElement');
		native(Document.prototype.createElementNS,'Document.prototype.createElementNS');
		native(Document.prototype.webkitCancelFullScreen,'Document.prototype.webkitCancelFullScreen');
		native(Document.prototype.webkitExitFullscreen,'Document.prototype.webkitExitFullscreen');
		native(Document.prototype.querySelector,'Document.prototype.querySelector');
		native(Document.prototype.querySelectorAll,'Document.prototype.querySelectorAll');
		native(Document.prototype.createExpression,'Document.prototype.createExpression');
		native(Document.prototype.createNSResolver,'Document.prototype.createNSResolver');
		native(Document.prototype.evaluate,'Document.prototype.evaluate');
		native(DataTransferItemList.prototype.add,'DataTransferItemList.prototype.add');
		native(DataTransferItemList.prototype.remove,'DataTransferItemList.prototype.remove');
		native(DataTransferItemList.prototype.clear,'DataTransferItemList.prototype.clear');
		native(DataTransfer.prototype.clearData,'DataTransfer.prototype.clearData');
		native(DataTransfer.prototype.getData,'DataTransfer.prototype.getData');
		native(DataTransfer.prototype.setData,'DataTransfer.prototype.setData');
		native(DataTransfer.prototype.setDragImage,'DataTransfer.prototype.setDragImage');
		native(DOMTokenList.prototype.item,'DOMTokenList.prototype.item');
		native(DOMTokenList.prototype.contains,'DOMTokenList.prototype.contains');
		native(DOMTokenList.prototype.add,'DOMTokenList.prototype.add');
		native(DOMTokenList.prototype.remove,'DOMTokenList.prototype.remove');
		native(DOMTokenList.prototype.toggle,'DOMTokenList.prototype.toggle');
		native(DOMTokenList.prototype.toString,'DOMTokenList.prototype.toString');
		native(DOMStringList.prototype.item,'DOMStringList.prototype.item');
		native(DOMStringList.prototype.contains,'DOMStringList.prototype.contains');
		native(DOMParser.prototype.parseFromString,'DOMParser.prototype.parseFromString');
		native(DOMImplementation.prototype.hasFeature,'DOMImplementation.prototype.hasFeature');
		native(DOMImplementation.prototype.createDocumentType,'DOMImplementation.prototype.createDocumentType');
		native(DOMImplementation.prototype.createDocument,'DOMImplementation.prototype.createDocument');
		native(DOMImplementation.prototype.createHTMLDocument,'DOMImplementation.prototype.createHTMLDocument');
		native(DOMException.prototype.toString,'DOMException.prototype.toString');
		native(CustomEvent.prototype.initCustomEvent,'CustomEvent.prototype.initCustomEvent');
		native(CompositionEvent.prototype.initCompositionEvent,'CompositionEvent.prototype.initCompositionEvent');
		native(ClientRectList.prototype.item,'ClientRectList.prototype.item');
		native(CharacterData.prototype.substringData,'CharacterData.prototype.substringData');
		native(CharacterData.prototype.appendData,'CharacterData.prototype.appendData');
		native(CharacterData.prototype.insertData,'CharacterData.prototype.insertData');
		native(CharacterData.prototype.deleteData,'CharacterData.prototype.deleteData');
		native(CharacterData.prototype.replaceData,'CharacterData.prototype.replaceData');
		native(CharacterData.prototype.remove,'CharacterData.prototype.remove');
		native(CanvasRenderingContext2D.prototype.save,'CanvasRenderingContext2D.prototype.save');
		native(CanvasRenderingContext2D.prototype.restore,'CanvasRenderingContext2D.prototype.restore');
		native(CanvasRenderingContext2D.prototype.scale,'CanvasRenderingContext2D.prototype.scale');
		native(CanvasRenderingContext2D.prototype.rotate,'CanvasRenderingContext2D.prototype.rotate');
		native(CanvasRenderingContext2D.prototype.translate,'CanvasRenderingContext2D.prototype.translate');
		native(CanvasRenderingContext2D.prototype.transform,'CanvasRenderingContext2D.prototype.transform');
		native(CanvasRenderingContext2D.prototype.setTransform,'CanvasRenderingContext2D.prototype.setTransform');
		native(CanvasRenderingContext2D.prototype.resetTransform,'CanvasRenderingContext2D.prototype.resetTransform');
		native(CanvasRenderingContext2D.prototype.createLinearGradient,'CanvasRenderingContext2D.prototype.createLinearGradient');
		native(CanvasRenderingContext2D.prototype.createRadialGradient,'CanvasRenderingContext2D.prototype.createRadialGradient');
		native(CanvasRenderingContext2D.prototype.createPattern,'CanvasRenderingContext2D.prototype.createPattern');
		native(CanvasRenderingContext2D.prototype.clearRect,'CanvasRenderingContext2D.prototype.clearRect');
		native(CanvasRenderingContext2D.prototype.fillRect,'CanvasRenderingContext2D.prototype.fillRect');
		native(CanvasRenderingContext2D.prototype.strokeRect,'CanvasRenderingContext2D.prototype.strokeRect');
		native(CanvasRenderingContext2D.prototype.beginPath,'CanvasRenderingContext2D.prototype.beginPath');
		native(CanvasRenderingContext2D.prototype.fill,'CanvasRenderingContext2D.prototype.fill');
		native(CanvasRenderingContext2D.prototype.stroke,'CanvasRenderingContext2D.prototype.stroke');
		native(CanvasRenderingContext2D.prototype.drawFocusIfNeeded,'CanvasRenderingContext2D.prototype.drawFocusIfNeeded');
		native(CanvasRenderingContext2D.prototype.clip,'CanvasRenderingContext2D.prototype.clip');
		native(CanvasRenderingContext2D.prototype.isPointInPath,'CanvasRenderingContext2D.prototype.isPointInPath');
		native(CanvasRenderingContext2D.prototype.isPointInStroke,'CanvasRenderingContext2D.prototype.isPointInStroke');
		native(CanvasRenderingContext2D.prototype.fillText,'CanvasRenderingContext2D.prototype.fillText');
		native(CanvasRenderingContext2D.prototype.strokeText,'CanvasRenderingContext2D.prototype.strokeText');
		native(CanvasRenderingContext2D.prototype.measureText,'CanvasRenderingContext2D.prototype.measureText');
		native(CanvasRenderingContext2D.prototype.drawImage,'CanvasRenderingContext2D.prototype.drawImage');
		native(CanvasRenderingContext2D.prototype.createImageData,'CanvasRenderingContext2D.prototype.createImageData');
		native(CanvasRenderingContext2D.prototype.getImageData,'CanvasRenderingContext2D.prototype.getImageData');
		native(CanvasRenderingContext2D.prototype.putImageData,'CanvasRenderingContext2D.prototype.putImageData');
		native(CanvasRenderingContext2D.prototype.getContextAttributes,'CanvasRenderingContext2D.prototype.getContextAttributes');
		native(CanvasRenderingContext2D.prototype.setLineDash,'CanvasRenderingContext2D.prototype.setLineDash');
		native(CanvasRenderingContext2D.prototype.getLineDash,'CanvasRenderingContext2D.prototype.getLineDash');
		native(CanvasRenderingContext2D.prototype.closePath,'CanvasRenderingContext2D.prototype.closePath');
		native(CanvasRenderingContext2D.prototype.moveTo,'CanvasRenderingContext2D.prototype.moveTo');
		native(CanvasRenderingContext2D.prototype.lineTo,'CanvasRenderingContext2D.prototype.lineTo');
		native(CanvasRenderingContext2D.prototype.quadraticCurveTo,'CanvasRenderingContext2D.prototype.quadraticCurveTo');
		native(CanvasRenderingContext2D.prototype.bezierCurveTo,'CanvasRenderingContext2D.prototype.bezierCurveTo');
		native(CanvasRenderingContext2D.prototype.arcTo,'CanvasRenderingContext2D.prototype.arcTo');
		native(CanvasRenderingContext2D.prototype.rect,'CanvasRenderingContext2D.prototype.rect');
		native(CanvasRenderingContext2D.prototype.arc,'CanvasRenderingContext2D.prototype.arc');
		native(CanvasRenderingContext2D.prototype.ellipse,'CanvasRenderingContext2D.prototype.ellipse');
		native(CanvasGradient.prototype.addColorStop,'CanvasGradient.prototype.addColorStop');
		native(CSSSupportsRule.prototype.insertRule,'CSSSupportsRule.prototype.insertRule');
		native(CSSSupportsRule.prototype.deleteRule,'CSSSupportsRule.prototype.deleteRule');
		native(CSSStyleSheet.prototype.insertRule,'CSSStyleSheet.prototype.insertRule');
		native(CSSStyleSheet.prototype.deleteRule,'CSSStyleSheet.prototype.deleteRule');
		native(CSSStyleSheet.prototype.addRule,'CSSStyleSheet.prototype.addRule');
		native(CSSStyleSheet.prototype.removeRule,'CSSStyleSheet.prototype.removeRule');
		native(CSSStyleDeclaration.prototype.getPropertyValue,'CSSStyleDeclaration.prototype.getPropertyValue');
		native(CSSStyleDeclaration.prototype.removeProperty,'CSSStyleDeclaration.prototype.removeProperty');
		native(CSSStyleDeclaration.prototype.getPropertyPriority,'CSSStyleDeclaration.prototype.getPropertyPriority');
		native(CSSStyleDeclaration.prototype.setProperty,'CSSStyleDeclaration.prototype.setProperty');
		native(CSSStyleDeclaration.prototype.item,'CSSStyleDeclaration.prototype.item');
		native(CSSRuleList.prototype.item,'CSSRuleList.prototype.item');
		native(CSSMediaRule.prototype.insertRule,'CSSMediaRule.prototype.insertRule');
		native(CSSMediaRule.prototype.deleteRule,'CSSMediaRule.prototype.deleteRule');
		native(CSSKeyframesRule.prototype.insertRule,'CSSKeyframesRule.prototype.insertRule');
		native(CSSKeyframesRule.prototype.appendRule,'CSSKeyframesRule.prototype.appendRule');
		native(CSSKeyframesRule.prototype.deleteRule,'CSSKeyframesRule.prototype.deleteRule');
		native(CSSKeyframesRule.prototype.findRule,'CSSKeyframesRule.prototype.findRule');
		native(Blob.prototype.slice,'Blob.prototype.slice');
		native(ApplicationCache.prototype.update,'ApplicationCache.prototype.update');
		native(ApplicationCache.prototype.swapCache,'ApplicationCache.prototype.swapCache');
		native(ApplicationCache.prototype.abort,'ApplicationCache.prototype.abort');
		native(Intl.Collator.prototype.resolvedOptions,'Intl.Collator.prototype.resolvedOptions');
		native(Intl.NumberFormat.prototype.resolvedOptions,'Intl.NumberFormat.prototype.resolvedOptions');
		native(Intl.DateTimeFormat.prototype.resolvedOptions,'Intl.DateTimeFormat.prototype.resolvedOptions');
		native(Intl.v8BreakIterator.prototype.resolvedOptions,'Intl.v8BreakIterator.prototype.resolvedOptions');
		native(Object.getOwnPropertyDescriptor(Object.prototype,'__proto__').get,'Object.prototype.__proto__ (get)');
		native(Object.getOwnPropertyDescriptor(Object.prototype,'__proto__').set,'Object.prototype.__proto__ (set)');
		native(console.__proto__.constructor,'console.__proto__.constructor');
		native(console.__proto__.__proto__.constructor,'console.__proto__.__proto__.constructor');
		native(console.__proto__.__proto__.clear,'console.__proto__.__proto__.clear');
		native(console.__proto__.__proto__.groupEnd,'console.__proto__.__proto__.groupEnd');
		native(console.__proto__.__proto__.groupCollapsed,'console.__proto__.__proto__.groupCollapsed');
		native(console.__proto__.__proto__.group,'console.__proto__.__proto__.group');
		native(console.__proto__.__proto__.timelineEnd,'console.__proto__.__proto__.timelineEnd');
		native(console.__proto__.__proto__.timeline,'console.__proto__.__proto__.timeline');
		native(console.__proto__.__proto__.timeStamp,'console.__proto__.__proto__.timeStamp');
		native(console.__proto__.__proto__.timeEnd,'console.__proto__.__proto__.timeEnd');
		native(console.__proto__.__proto__.time,'console.__proto__.__proto__.time');
		native(console.__proto__.__proto__.profileEnd,'console.__proto__.__proto__.profileEnd');
		native(console.__proto__.__proto__.profile,'console.__proto__.__proto__.profile');
		native(console.__proto__.__proto__.markTimeline,'console.__proto__.__proto__.markTimeline');
		native(console.__proto__.__proto__.count,'console.__proto__.__proto__.count');
		native(console.__proto__.__proto__.assert,'console.__proto__.__proto__.assert');
		native(console.__proto__.__proto__.trace,'console.__proto__.__proto__.trace');
		native(console.__proto__.__proto__.table,'console.__proto__.__proto__.table');
		native(console.__proto__.__proto__.dirxml,'console.__proto__.__proto__.dirxml');
		native(console.__proto__.__proto__.dir,'console.__proto__.__proto__.dir');
		native(console.__proto__.__proto__.warn,'console.__proto__.__proto__.warn');
		native(console.__proto__.__proto__.log,'console.__proto__.__proto__.log');
		native(console.__proto__.__proto__.info,'console.__proto__.__proto__.info');
		native(console.__proto__.__proto__.error,'console.__proto__.__proto__.error');
		native(console.__proto__.__proto__.debug,'console.__proto__.__proto__.debug');
		native(console.__proto__.__proto__.constructor,'console.__proto__.__proto__.constructor');
		native(console.memory.__proto__.constructor,'console.memory.__proto__.constructor');
		native(XMLHttpRequest.__proto__,'XMLHttpRequest.__proto__');
		native(Object.getOwnPropertyDescriptor(encodeURIComponent,'arguments').get,'encodeURIComponent.arguments (get)');
		native(Object.getOwnPropertyDescriptor(ReferenceError.prototype,'stack').get,'ReferenceError.prototype.stack (get)');
		native(Object.getOwnPropertyDescriptor(ReferenceError.prototype,'stack').set,'ReferenceError.prototype.stack (set)');
		native(Object.getOwnPropertyDescriptor(RegExp,'$9').get,'RegExp.$9 (get)');
		native(Object.getOwnPropertyDescriptor(RegExp,'$8').get,'RegExp.$8 (get)');
		native(Object.getOwnPropertyDescriptor(RegExp,'$7').get,'RegExp.$7 (get)');
		native(Object.getOwnPropertyDescriptor(RegExp,'$6').get,'RegExp.$6 (get)');
		native(Object.getOwnPropertyDescriptor(RegExp,'$5').get,'RegExp.$5 (get)');
		native(Object.getOwnPropertyDescriptor(RegExp,'$4').get,'RegExp.$4 (get)');
		native(Object.getOwnPropertyDescriptor(RegExp,'$3').get,'RegExp.$3 (get)');
		native(Object.getOwnPropertyDescriptor(RegExp,'$2').get,'RegExp.$2 (get)');
		native(Object.getOwnPropertyDescriptor(RegExp,'$1').get,'RegExp.$1 (get)');
		native(Object.getOwnPropertyDescriptor(RegExp,'rightContext').get,'RegExp.rightContext (get)');
		native(Object.getOwnPropertyDescriptor(RegExp,'leftContext').get,'RegExp.leftContext (get)');
		native(Object.getOwnPropertyDescriptor(RegExp,'lastParen').get,'RegExp.lastParen (get)');
		native(Object.getOwnPropertyDescriptor(RegExp,'lastMatch').set,'RegExp.lastMatch (set)');
		native(Object.getOwnPropertyDescriptor(RegExp,'lastMatch').get,'RegExp.lastMatch (get)');
		native(Object.getOwnPropertyDescriptor(RegExp,'multiline').get,'RegExp.multiline (get)');
		native(Object.getOwnPropertyDescriptor(RegExp,'multiline').set,'RegExp.multiline (set)');
		native(Object.getOwnPropertyDescriptor(RegExp,'input').get,'RegExp.input (get)');
		native(Object.getOwnPropertyDescriptor(RegExp,'input').set,'RegExp.input (set)');
		native(document.fonts.__proto__.constructor,'document.fonts.__proto__.constructor');
		native(document.fonts.__proto__.has,'document.fonts.__proto__.has');
		native(document.fonts.__proto__.forEach,'document.fonts.__proto__.forEach');
		native(document.fonts.__proto__.delete,'document.fonts.__proto__.delete');
		native(document.fonts.__proto__.clear,'document.fonts.__proto__.clear');
		native(document.fonts.__proto__.add,'document.fonts.__proto__.add');
		native(document.fonts.__proto__.load,'document.fonts.__proto__.load');
		native(document.fonts.__proto__.check,'document.fonts.__proto__.check');
	};
	
	//
	function init_pred_name(){
		preDefNameList.push(new pair(screen, "screenIns"));
		preDefNameList.push(new pair(console, "ConsoleIns"));
		preDefNameList.push(new pair(console.__proto__.__proto__, "ConsoleProto"));
		preDefNameList.push(new pair(SVGSVGElement.prototype, "SVGSVGElementProto"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.animationsPaused, "SVGSVGElement.animationsPaused"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.checkEnclosure, "SVGSVGElement.checkEnclosure"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.checkIntersection, "SVGSVGElement.checkIntersection"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.createSVGAngle, "SVGSVGElement.createSVGAngle"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.createSVGLength, "SVGSVGElement.createSVGLength"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.createSVGMatrix, "SVGSVGElement.createSVGMatrix"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.createSVGNumber, "SVGSVGElement.createSVGNumber"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.createSVGPoint, "SVGSVGElement.createSVGPoint"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.createSVGRect, "SVGSVGElement.createSVGRect"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.createSVGTransform, "SVGSVGElement.createSVGTransform"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.createSVGTransformFromMatrix, "SVGSVGElement.createSVGTransformFromMatrix"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.deselectAll, "SVGSVGElement.deselectAll"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.forceRedraw, "SVGSVGElement.forceRedraw"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.getCurrentTime, "SVGSVGElement.getCurrentTime"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.getElementById, "SVGSVGElement.getElementById"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.getIntersectionList, "SVGSVGElement.getIntersectionList"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.pauseAnimations, "SVGSVGElement.pauseAnimations"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.setCurrentTime, "SVGSVGElement.setCurrentTime"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.suspendRedraw, "SVGSVGElement.suspendRedraw"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.unpauseAnimations, "SVGSVGElement.unpauseAnimations"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.unsuspendRedraw, "SVGSVGElement.unsuspendRedraw"));
		preDefNameList.push(new pair(SVGSVGElement.prototype.unsuspendRedrawAll, "SVGSVGElement.unsuspendRedrawAll"));

		preDefNameList.push(new pair(SVGElement.prototype, "SVGElementProto"));

		preDefNameList.push(new pair(ClientRectList.prototype, "ClientRectListProto"));
		preDefNameList.push(new pair(ClientRectList.prototype.item, "ClientRectList.item"));
		// ClientRectList, "ClientRectListIns"

		preDefNameList.push(new pair(ClientRect.prototype, "ClientRectProto"));
		// ClientRect, "ClientRectIns"

		preDefNameList.push(new pair(Node, "NodeCons"));
		preDefNameList.push(new pair(Node.prototype, "NodeProto"));
		preDefNameList.push(new pair(Node.prototype.addEventListener, "EventTarget.addEventListener"));
		preDefNameList.push(new pair(Node.prototype.appendChild, "DOMNode.appendChild"));
		preDefNameList.push(new pair(Node.prototype.cloneNode, "DOMNode.cloneNode"));
		preDefNameList.push(new pair(Node.prototype.compareDocumentPosition, "DOMNode.compareDocumentPosition"));
		preDefNameList.push(new pair(Node.prototype.contains, "DOMNode.contains"));
		preDefNameList.push(new pair(Node.prototype.dispatchEvent, "EventTarget.dispatchEvent"));
		preDefNameList.push(new pair(Node.prototype.hasChildNodes, "DOMNode.hasChildNodes"));
		preDefNameList.push(new pair(Node.prototype.insertBefore, "DOMNode.insertBefore"));
		preDefNameList.push(new pair(Node.prototype.isDefaultNamespace, "DOMNode.isDefaultNamespace"));
		preDefNameList.push(new pair(Node.prototype.isEqualNode, "DOMNode.isEqualNode"));
		preDefNameList.push(new pair(Node.prototype.isSameNode, "DOMNode.isSameNode"));
		preDefNameList.push(new pair(Node.prototype.lookupNamespaceURI, "DOMNode.lookupNamespaceURI"));
		preDefNameList.push(new pair(Node.prototype.lookupPrefix, "DOMNode.lookupPrefix"));
		preDefNameList.push(new pair(Node.prototype.normalize, "DOMNode.normalize"));
		preDefNameList.push(new pair(Node.prototype.removeChild, "DOMNode.removeChild"));
		preDefNameList.push(new pair(Node.prototype.removeEventListener, "EventTarget.removeEventListener"));
		preDefNameList.push(new pair(Node.prototype.replaceChild, "DOMNode.replaceChild"));

		preDefNameList.push(new pair(XMLHttpRequest, "XMLHttpRequestCons"));
		preDefNameList.push(new pair(XMLHttpRequest.prototype, "XMLHttpRequestProto"));
		preDefNameList.push(new pair(XMLHttpRequest.prototype.abort, "XMLHttpRequest.abort"));
		preDefNameList.push(new pair(XMLHttpRequest.prototype.getAllResponseHeaders, "XMLHttpRequest.getAllResponseHeaders"));
		preDefNameList.push(new pair(XMLHttpRequest.prototype.getResponseHeader, "XMLHttpRequest.getResponseHeader"));
		preDefNameList.push(new pair(XMLHttpRequest.prototype.open, "XMLHttpRequest.open"));
		preDefNameList.push(new pair(XMLHttpRequest.prototype.overrideMimeType, "XMLHttpRequest.overrideMimeType"));
		preDefNameList.push(new pair(XMLHttpRequest.prototype.send, "XMLHttpRequest.send"));
		preDefNameList.push(new pair(XMLHttpRequest.prototype.setRequestHeader, "XMLHttpRequest.setRequestHeader"));

		preDefNameList.push(new pair(CanvasGradient.prototype, "CanvasGradientProto"));
		preDefNameList.push(new pair(CanvasGradient.prototype.addColorStop, "CanvasGradient.addColorStop"));
		// CanvasGradient, "CanvasGradientIns"

		// CanvasRenderingContext2D, "CanvasRenderingContext2DIns"
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype, "CanvasRenderingContext2DProto"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.arcTo, "CanvasRenderingContext2D.arcTo"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.beginPath, "CanvasRenderingContext2D.beginPath"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.bezierCurveTo, "CanvasRenderingContext2D.bezierCurveTo"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.clearRect, "CanvasRenderingContext2D.clearRect"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.clip, "CanvasRenderingContext2D.clip"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.closePath, "CanvasRenderingContext2D.closePath"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.createImageData, "CanvasRenderingContext2D.createImageData"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.createLinearGradient, "CanvasRenderingContext2D.createLinearGradient"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.createPattern, "CanvasRenderingContext2D.createPattern"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.createRadialGradient, "CanvasRenderingContext2D.createRadialGradient"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.drawImage, "CanvasRenderingContext2D.drawImage"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.fill, "CanvasRenderingContext2D.fill"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.fillRect, "CanvasRenderingContext2D.fillRect"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.fillText, "CanvasRenderingContext2D.fillTest"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.getImageData, "CanvasRenderingContext2D.getImageData"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.isPointInPath, "CanvasRenderingContext2D.isPointInPath"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.lineTo, "CanvasRenderingContext2D.lineTo"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.measureText, "CanvasRenderingContext2D.measureTest"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.moveTo, "CanvasRenderingContext2D.moveTo"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.putImageData, "CanvasRenderingContext2D.putImageData"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.quadraticCurveTo, "CanvasRenderingContext2D.quadraticCurveTo"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.rect, "CanvasRenderingContext2D.rect"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.restore, "CanvasRenderingContext2D.restore"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.rotate, "CanvasRenderingContext2D.rotate"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.save, "CanvasRenderingContext2D.save"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.scale, "CanvasRenderingContext2D.scale"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.setTransform, "CanvasRenderingContext2D.setTransform"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.stroke, "CanvasRenderingContext2D.stroke"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.strokeRect, "CanvasRenderingContext2D.strokeRect"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.strokeText, "CanvasRenderingContext2D.strokeText"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.transform, "CanvasRenderingContext2D.transform"));
		preDefNameList.push(new pair(CanvasRenderingContext2D.prototype.translate, "CanvasRenderingContext2D.translate"));

		preDefNameList.push(new pair(StyleSheetList.prototype, "StyleSheetListProto"));
		preDefNameList.push(new pair(StyleSheetList.prototype.item, "StyleSheetList.item"));
		// StyleSheetList, "StyleSheetListIns"

		preDefNameList.push(new pair(StyleSheet.prototype, "StyleSheetProto"));
		// StyleSheet, "StyleSheetIns"

		preDefNameList.push(new pair(CSSStyleSheet.prototype, "CSSStyleSheetProto"));
		preDefNameList.push(new pair(CSSStyleSheet.prototype.deleteRule, "CSSStyleSheet.deleteRule"));
		preDefNameList.push(new pair(CSSStyleSheet.prototype.insertRule, "CSSStyleSheet.insertRule"));
		// CSSStyleSheet, "CSSStyleSheetIns"

		preDefNameList.push(new pair(TouchEvent, "TouchEventCons"));
		preDefNameList.push(new pair(TouchEvent.prototype, "TouchEventProto"));

		preDefNameList.push(new pair(MessageEvent, "MessageEventCons"));
		preDefNameList.push(new pair(MessageEvent.prototype, "MessageEventProto"));

		preDefNameList.push(new pair(KeyboardEvent, "KeyboardEventCons"));
		preDefNameList.push(new pair(KeyboardEvent.prototype, "KeyboardEventProto"));
		preDefNameList.push(new pair(KeyboardEvent.prototype.initKeyboardEvent, "KeyboardEvent.initKeyboardEvent"));

		preDefNameList.push(new pair(MutationEvent, "MutationEventCons"));
		preDefNameList.push(new pair(MutationEvent.prototype, "MutationEventProto"));
		preDefNameList.push(new pair(MutationEvent.prototype.initMutationEvent, "MutationEvent.initMutationEvent"));

		preDefNameList.push(new pair(UIEvent, "UIEventCons"));
		preDefNameList.push(new pair(UIEvent.prototype, "UIEventProto"));
		preDefNameList.push(new pair(UIEvent.prototype.initUIEvent, "UIEvent.initUIEvent"));

		preDefNameList.push(new pair(MouseEvent, "MouseEventCons"));
		preDefNameList.push(new pair(MouseEvent.prototype, "MouseEventProto"));
		preDefNameList.push(new pair(MouseEvent.prototype.initMouseEvent, "MouseEvent.initMouseEvent"));

		preDefNameList.push(new pair(Event, "EventCons"));
		preDefNameList.push(new pair(Event.prototype, "EventProto"));
		preDefNameList.push(new pair(Event.prototype.initEvent, "Event.initEvent"));
		preDefNameList.push(new pair(Event.prototype.preventDefault, "Event.preventDefault"));
		preDefNameList.push(new pair(Event.prototype.stopImmediatePropagation, "Event.stopImmediatePropagation"));
		preDefNameList.push(new pair(Event.prototype.stopPropagation, "Event.stopPropagation"));

		preDefNameList.push(new pair(DOMStringList, "DOMStringListCons"));
		preDefNameList.push(new pair(DOMStringList.prototype, "DOMStringListProto"));
		preDefNameList.push(new pair(DOMStringList.prototype.contains, "DOMStringList.contains"));
		preDefNameList.push(new pair(DOMStringList.prototype.item, "DOMStringList.item"));

		preDefNameList.push(new pair(ProcessingInstruction, "ProcessingInstructionCons"));
		preDefNameList.push(new pair(ProcessingInstruction.prototype, "ProcessingInstructionProto"));

		preDefNameList.push(new pair(DOMImplementation, "DOMImplementationCons"));
		preDefNameList.push(new pair(DOMImplementation.prototype, "DOMImplementationProto"));
		preDefNameList.push(new pair(DOMImplementation.prototype.createDocument, "DOMImplementation.createDocument"));
		preDefNameList.push(new pair(DOMImplementation.prototype.createDocumentType, "DOMImplementation.createDocumentType"));
		preDefNameList.push(new pair(DOMImplementation.prototype.hasFeature, "DOMImplementation.hasFeature"));

		preDefNameList.push(new pair(DOMException,"DOMExceptionCons"));
		preDefNameList.push(new pair(DOMException.prototype,"DOMExceptionProto"));

		preDefNameList.push(new pair(DocumentFragment,"DocumentFragmentCons"));
		preDefNameList.push(new pair(DocumentFragment.prototype,"DocumentFragmentProto"));

		preDefNameList.push(new pair(Comment,"CommentCons"));
		preDefNameList.push(new pair(Comment.prototype,"CommentProto"));

		preDefNameList.push(new pair(CharacterData,"CharacterDataCons"));
		preDefNameList.push(new pair(CharacterData.prototype,"CharacterDataProto"));
		preDefNameList.push(new pair(CharacterData.prototype.appendData,"DOMCharacterData.appendData"));
		preDefNameList.push(new pair(CharacterData.prototype.deleteData,"DOMCharacterData.deleteData"));
		preDefNameList.push(new pair(CharacterData.prototype.insertData,"DOMCharacterData.insertData"));
		preDefNameList.push(new pair(CharacterData.prototype.replaceData,"DOMCharacterData.replaceData"));
		preDefNameList.push(new pair(CharacterData.prototype.substringData,"DOMCharacterData.substringData"));

		preDefNameList.push(new pair(Text, "TextCons"));
		preDefNameList.push(new pair(Text.prototype, "TextProto"));
		preDefNameList.push(new pair(Text.prototype.splitText, "DOMText.splitText"));

		preDefNameList.push(new pair(CDATASection, "CDATASectionCons"));
		preDefNameList.push(new pair(CDATASection.prototype, "CDATASectionProto"));

		preDefNameList.push(new pair(HTMLUnknownElement, "HTMLUnknownElementCons"));
		preDefNameList.push(new pair(HTMLUnknownElement.prototype, "HTMLUnknownElementProto"));

		preDefNameList.push(new pair(HTMLDataListElement, "HTMLDataListElementCons"));
		preDefNameList.push(new pair(HTMLDataListElement.prototype, "HTMLDataListElementProto"));

		preDefNameList.push(new pair(HTMLCanvasElement, "HTMLCanvasElementCons"));
		preDefNameList.push(new pair(HTMLCanvasElement.prototype, "HTMLCanvasElementProto"));
		preDefNameList.push(new pair(HTMLCanvasElement.prototype.getContext, "HTMLCanvasElement.getContext"));
		preDefNameList.push(new pair(HTMLCanvasElement.prototype.toDataURL, "HTMLCanvasElement.toDataURL"));

		preDefNameList.push(new pair(HTMLUListElement, "HTMLUListElementCons"));
		preDefNameList.push(new pair(HTMLUListElement.prototype, "HTMLUListElementProto"));

		preDefNameList.push(new pair(HTMLTitleElement, "HTMLTitleElementCons"));
		preDefNameList.push(new pair(HTMLTitleElement.prototype, "HTMLTitleElementProto"));

		preDefNameList.push(new pair(HTMLTextAreaElement, "HTMLTextAreaElementCons"));
		preDefNameList.push(new pair(HTMLTextAreaElement.prototype, "HTMLTextAreaElementProto"));
		preDefNameList.push(new pair(HTMLTextAreaElement.prototype.blur, "HTMLTextAreaElement.blur"));
		preDefNameList.push(new pair(HTMLTextAreaElement.prototype.focus, "HTMLTextAreaElement.focus"));
		preDefNameList.push(new pair(HTMLTextAreaElement.prototype.select, "HTMLTextAreaElement.select"));

		preDefNameList.push(new pair(HTMLTableRowElement, "HTMLTableRowElementCons"));
		preDefNameList.push(new pair(HTMLTableRowElement.prototype, "HTMLTableRowElementProto"));
		preDefNameList.push(new pair(HTMLTableRowElement.prototype.deleteCell, "HTMLTableRowElement.deleteCell"));
		preDefNameList.push(new pair(HTMLTableRowElement.prototype.insertCell, "HTMLTableRowElement.insertCell"));

		preDefNameList.push(new pair(HTMLTableSectionElement, "HTMLTableSectionElementCons"));
		preDefNameList.push(new pair(HTMLTableSectionElement.prototype, "HTMLTableSectionElementProto"));
		preDefNameList.push(new pair(HTMLTableSectionElement.prototype.deleteRow, "HTMLTableSectionElement.deleteRow"));
		preDefNameList.push(new pair(HTMLTableSectionElement.prototype.insertRow, "HTMLTableSectionElement.insertRow"));

		preDefNameList.push(new pair(HTMLTableElement, "HTMLTableElementCons"));
		preDefNameList.push(new pair(HTMLTableElement.prototype, "HTMLTableElementProto"));
		preDefNameList.push(new pair(HTMLTableElement.prototype.createCaption, "HTMLTableElement.createCaption"));
		preDefNameList.push(new pair(HTMLTableElement.prototype.createTFoot, "HTMLTableElement.createTFoot"));
		preDefNameList.push(new pair(HTMLTableElement.prototype.createTHead, "HTMLTableElement.createTHead"));
		preDefNameList.push(new pair(HTMLTableElement.prototype.deleteCaption, "HTMLTableElement.deleteCaption"));
		preDefNameList.push(new pair(HTMLTableElement.prototype.deleteRow, "HTMLTableElement.deleteRow"));
		preDefNameList.push(new pair(HTMLTableElement.prototype.deleteTFoot, "HTMLTableElement.deleteTFoot"));
		preDefNameList.push(new pair(HTMLTableElement.prototype.deleteTHead, "HTMLTableElement.deleteTHead"));
		preDefNameList.push(new pair(HTMLTableElement.prototype.insertRow, "HTMLTableElement.insertRow"));

		preDefNameList.push(new pair(HTMLTableColElement, "HTMLTableColElementCons"));
		preDefNameList.push(new pair(HTMLTableColElement.prototype, "HTMLTableColElementProto"));

		preDefNameList.push(new pair(HTMLTableCellElement, "HTMLTableCellElementCons"));
		preDefNameList.push(new pair(HTMLTableCellElement.prototype, "HTMLTableCellElementProto"));

		preDefNameList.push(new pair(HTMLTableCaptionElement, "HTMLTableCaptionElementCons"));
		preDefNameList.push(new pair(HTMLTableCaptionElement.prototype, "HTMLTableCaptionElementProto"));

		preDefNameList.push(new pair(HTMLStyleElement, "HTMLStyleElementCons"));
		preDefNameList.push(new pair(HTMLStyleElement.prototype, "HTMLStyleElementProto"));

		preDefNameList.push(new pair(HTMLSelectElement, "HTMLSelectElementCons"));
		preDefNameList.push(new pair(HTMLSelectElement.prototype, "HTMLSelectElementProto"));
		preDefNameList.push(new pair(HTMLSelectElement.prototype.add, "HTMLSelectElement.add"));
		preDefNameList.push(new pair(HTMLSelectElement.prototype.blur, "HTMLSelectElement.blur"));
		preDefNameList.push(new pair(HTMLSelectElement.prototype.focus, "HTMLSelectElement.focus"));
		preDefNameList.push(new pair(HTMLSelectElement.prototype.remove, "HTMLSelectElement.remove"));

		preDefNameList.push(new pair(HTMLScriptElement, "HTMLScriptElementCons"));
		preDefNameList.push(new pair(HTMLScriptElement.prototype, "HTMLScriptElementProto"));

		preDefNameList.push(new pair(HTMLQuoteElement, "HTMLQuoteElementCons"));
		preDefNameList.push(new pair(HTMLQuoteElement.prototype, "HTMLQuoteElementProto"));

		preDefNameList.push(new pair(HTMLPreElement, "HTMLPreElementCons"));
		preDefNameList.push(new pair(HTMLPreElement.prototype, "HTMLPreElementProto"));

		preDefNameList.push(new pair(HTMLParamElement, "HTMLParamElementCons"));
		preDefNameList.push(new pair(HTMLParamElement.prototype, "HTMLParamElementProto"));

		preDefNameList.push(new pair(HTMLParagraphElement, "HTMLParagraphElementCons"));
		preDefNameList.push(new pair(HTMLParagraphElement.prototype, "HTMLParagraphElementProto"));

		preDefNameList.push(new pair(HTMLOptionElement, "HTMLOptionElementCons"));
		preDefNameList.push(new pair(HTMLOptionElement.prototype, "HTMLOptionElementProto"));

		preDefNameList.push(new pair(HTMLOptGroupElement, "HTMLOptGroupElementCons"));
		preDefNameList.push(new pair(HTMLOptGroupElement.prototype, "HTMLOptGroupElementProto"));

		preDefNameList.push(new pair(HTMLOListElement, "HTMLOListElementCons"));
		preDefNameList.push(new pair(HTMLOListElement.prototype, "HTMLOListElementProto"));

		preDefNameList.push(new pair(HTMLObjectElement, "HTMLObjectElementCons"));
		preDefNameList.push(new pair(HTMLObjectElement.prototype, "HTMLObjectElementProto"));

		preDefNameList.push(new pair(HTMLModElement, "HTMLModElementCons"));
		preDefNameList.push(new pair(HTMLModElement.prototype, "HTMLModElementProto"));

		preDefNameList.push(new pair(HTMLMetaElement, "HTMLMetaElementCons"));
		preDefNameList.push(new pair(HTMLMetaElement.prototype, "HTMLMetaElementProto"));

		preDefNameList.push(new pair(HTMLMenuElement, "HTMLMenuElementCons"));
		preDefNameList.push(new pair(HTMLMenuElement.prototype, "HTMLMenuElementProto"));

		preDefNameList.push(new pair(HTMLMapElement, "HTMLMapElementCons"));
		preDefNameList.push(new pair(HTMLMapElement.prototype, "HTMLMapElementProto"));

		preDefNameList.push(new pair(HTMLLinkElement, "HTMLLinkElementCons"));
		preDefNameList.push(new pair(HTMLLinkElement.prototype, "HTMLLinkElementProto"));

		preDefNameList.push(new pair(HTMLLIElement, "HTMLLIElementCons"));
		preDefNameList.push(new pair(HTMLLIElement.prototype, "HTMLLIElementProto"));

		preDefNameList.push(new pair(HTMLLegendElement, "HTMLLegendElementCons"));
		preDefNameList.push(new pair(HTMLLegendElement.prototype, "HTMLLegendElementProto"));

		preDefNameList.push(new pair(HTMLLabelElement, "HTMLLabelElementCons"));
		preDefNameList.push(new pair(HTMLLabelElement.prototype, "HTMLLabelElementProto"));

		preDefNameList.push(new pair(HTMLInputElement, "HTMLInputElementCons"));
		preDefNameList.push(new pair(HTMLInputElement.prototype, "HTMLInputElementProto"));
		preDefNameList.push(new pair(HTMLInputElement.prototype.blur, "HTMLInputElement.blur"));
		preDefNameList.push(new pair(HTMLInputElement.prototype.click, "HTMLInputElement.click"));
		preDefNameList.push(new pair(HTMLInputElement.prototype.focus, "HTMLInputElement.focus"));
		preDefNameList.push(new pair(HTMLInputElement.prototype.select, "HTMLInputElement.select"));

		preDefNameList.push(new pair(HTMLImageElement, "HTMLImageElementCons"));
		preDefNameList.push(new pair(HTMLImageElement.prototype, "HTMLImageElementProto"));

		preDefNameList.push(new pair(Storage.prototype, "StorageProto"));
		preDefNameList.push(new pair(Storage.prototype.clear, "Storage.clear"));
		preDefNameList.push(new pair(Storage.prototype.getItem, "Storage.getItem"));
		preDefNameList.push(new pair(Storage.prototype.key, "Storage.key"));
		preDefNameList.push(new pair(Storage.prototype.removeItem, "Storage.removeItem"));
		preDefNameList.push(new pair(Storage.prototype.setItem, "Storage.setItem"));

		preDefNameList.push(new pair(MimeType.prototype, "MimeTypeProto"));

		preDefNameList.push(new pair(MimeTypeArray.prototype, "MimeTypeArrayProto"));
		preDefNameList.push(new pair(MimeTypeArray.prototype.item, "MimeTypeArray.item"));
		preDefNameList.push(new pair(MimeTypeArray.prototype.namedItem, "MimeTypeArray.namedItem"));

		preDefNameList.push(new pair(Plugin.prototype, "PluginProto"));
		preDefNameList.push(new pair(Plugin.prototype.item, "Plugin.item"));
		preDefNameList.push(new pair(Plugin.prototype.namedItem, "Plugin.namedItem"));

		preDefNameList.push(new pair(PluginArray.prototype, "PluginArrayProto"));
		preDefNameList.push(new pair(PluginArray.prototype.item, "PluginArray.item"));
		preDefNameList.push(new pair(PluginArray.prototype.namedItem, "PluginArray.namedItem"));
		preDefNameList.push(new pair(PluginArray.prototype.refresh, "PluginArray.refresh"));

		preDefNameList.push(new pair(navigator, "NavigatorIns"));
		preDefNameList.push(new pair(navigator.__proto__, "NavigatorProto"));
		preDefNameList.push(new pair(navigator.mimeTypes, "MimeTypeArrayIns"));
		preDefNameList.push(new pair(navigator.plugins, "PluginArrayIns"));

		preDefNameList.push(new pair(history, "HistoryIns"));

		preDefNameList.push(new pair(HTMLIFrameElement, "HTMLIFrameElementCons"));
		preDefNameList.push(new pair(HTMLIFrameElement.prototype, "HTMLIFrameElementProto"));

		preDefNameList.push(new pair(HTMLHRElement, "HTMLHRElementCons"));
		preDefNameList.push(new pair(HTMLHRElement.prototype, "HTMLHRElementProto"));

		preDefNameList.push(new pair(HTMLHeadingElement, "HTMLHeadingElementCons"));
		preDefNameList.push(new pair(HTMLHeadingElement.prototype, "HTMLHeadingElementProto"));

		preDefNameList.push(new pair(HTMLHeadElement, "HTMLHeadElementCons"));
		preDefNameList.push(new pair(HTMLHeadElement.prototype, "HTMLHeadElementProto"));

		preDefNameList.push(new pair(HTMLFrameSetElement, "HTMLFrameSetElementCons"));
		preDefNameList.push(new pair(HTMLFrameSetElement.prototype, "HTMLFrameSetElementProto"));

		preDefNameList.push(new pair(HTMLFrameElement, "HTMLFrameElementCons"));
		preDefNameList.push(new pair(HTMLFrameElement.prototype, "HTMLFrameElementProto"));

		preDefNameList.push(new pair(HTMLFontElement, "HTMLFontElementCons"));
		preDefNameList.push(new pair(HTMLFontElement.prototype, "HTMLFontElementProto"));

		preDefNameList.push(new pair(HTMLFieldSetElement, "HTMLFieldSetElementCons"));
		preDefNameList.push(new pair(HTMLFieldSetElement.prototype, "HTMLFieldSetElementProto"));

		preDefNameList.push(new pair(HTMLDListElement, "HTMLDListElementCons"));
		preDefNameList.push(new pair(HTMLDListElement.prototype, "HTMLDListElementProto"));

		preDefNameList.push(new pair(HTMLDivElement, "HTMLDivElementCons"));
		preDefNameList.push(new pair(HTMLDivElement.prototype, "HTMLDivElementProto"));

		preDefNameList.push(new pair(HTMLDirectoryElement, "HTMLDirectoryElementCons"));
		preDefNameList.push(new pair(HTMLDirectoryElement.prototype, "HTMLDirectoryElementProto"));

		preDefNameList.push(new pair(HTMLFormElement, "HTMLFormElementCons"));
		preDefNameList.push(new pair(HTMLFormElement.prototype, "HTMLFormElementProto"));
		preDefNameList.push(new pair(HTMLFormElement.prototype.reset, "HTMLFormElement.reset"));
		preDefNameList.push(new pair(HTMLFormElement.prototype.submit, "HTMLFormElement.submit"));

		preDefNameList.push(new pair(HTMLButtonElement, "HTMLButtonElementCons"));
		preDefNameList.push(new pair(HTMLButtonElement.prototype, "HTMLButtonElementProto"));

		preDefNameList.push(new pair(HTMLBRElement, "HTMLBRElementCons"));
		preDefNameList.push(new pair(HTMLBRElement.prototype, "HTMLBRElementProto"));

		preDefNameList.push(new pair(HTMLBaseElement, "HTMLBaseElementCons"));
		preDefNameList.push(new pair(HTMLBaseElement.prototype, "HTMLBaseElementProto"));

		preDefNameList.push(new pair(HTMLAreaElement, "HTMLAreaElementCons"));
		preDefNameList.push(new pair(HTMLAreaElement.prototype, "HTMLAreaElementProto"));

		preDefNameList.push(new pair(HTMLBodyElement, "HTMLBodyElementCons"));
		preDefNameList.push(new pair(HTMLBodyElement.prototype, "HTMLBodyElementProto"));

		preDefNameList.push(new pair(CSSStyleDeclaration.prototype, "CSSStyleDeclarationProto"));
		preDefNameList.push(new pair(CSSStyleDeclaration.prototype.getPropertyPriority, "CSSStyleDeclaration.getPropertyPriority"));
		preDefNameList.push(new pair(CSSStyleDeclaration.prototype.getPropertyValue, "CSSStyleDeclaration.getPropertyValue"));
		preDefNameList.push(new pair(CSSStyleDeclaration.prototype.item, "CSSStyleDeclaration.item"));
		preDefNameList.push(new pair(CSSStyleDeclaration.prototype.removeProperty, "CSSStyleDeclaration.removeProperty"));
		preDefNameList.push(new pair(CSSStyleDeclaration.prototype.setProperty, "CSSStyleDeclaration.setProperty"));

		preDefNameList.push(new pair(HTMLHtmlElement, "HTMLHtmlElementCons"));
		preDefNameList.push(new pair(HTMLHtmlElement.prototype, "HTMLHtmlElementProto"));

		preDefNameList.push(new pair(HTMLCollection, "HTMLCollectionCons"));
		preDefNameList.push(new pair(HTMLCollection.prototype, "HTMLCollectionProto"));
		preDefNameList.push(new pair(HTMLCollection.prototype.item, "HTMLCollection.item"));
		preDefNameList.push(new pair(HTMLCollection.prototype.namedItem, "HTMLCollection.namedItem"));

		preDefNameList.push(new pair(DocumentType, "DocumentTypeCons"));
		preDefNameList.push(new pair(DocumentType.prototype, "DocumentTypeProto"));

		/*** not modeled correctly ***/
		preDefNameList.push(new pair(location.assign, "Location.assign"));
		preDefNameList.push(new pair(location.reload, "Location.reload"));
		preDefNameList.push(new pair(location.replace, "Location.replace"));
		preDefNameList.push(new pair(location.toString, "Location.toString"));
		/***/
		preDefNameList.push(new pair(location, "LocationIns"));

		preDefNameList.push(new pair(Document, "DocumentCons"));
		preDefNameList.push(new pair(Document.prototype, "DocumentProto"));
		preDefNameList.push(new pair(Document.prototype.adoptNode, "DOMDocument.adoptNode"));
		preDefNameList.push(new pair(Document.prototype.createAttribute, "DOMDocument.createAttribute"));
		preDefNameList.push(new pair(Document.prototype.createAttributeNS, "DOMDocument.createAttributeNS"));
		preDefNameList.push(new pair(Document.prototype.createCDATASection, "DOMDocument.createCDATASection"));
		preDefNameList.push(new pair(Document.prototype.createComment, "DOMDocument.createComment"));
		preDefNameList.push(new pair(Document.prototype.createDocumentFragment, "DOMDocument.createDocumentFragment"));
		preDefNameList.push(new pair(Document.prototype.createElement, "DOMDocument.createElement"));
		preDefNameList.push(new pair(Document.prototype.createElementNS, "DOMDocument.createElementNS"));
		preDefNameList.push(new pair(Document.prototype.createProcessingInstruction, "DOMDocument.createProcessingInstruction"));
		preDefNameList.push(new pair(Document.prototype.createTextNode, "DOMDocument.createTextNode"));
		preDefNameList.push(new pair(Document.prototype.getElementById, "DOMDocument.getElementById"));
		preDefNameList.push(new pair(Document.prototype.getElementsByClassName, "DOMDocument.getElementsByClassName"));
		preDefNameList.push(new pair(Document.prototype.getElementsByTagName, "DOMDocument.getElementsByTagName"));
		preDefNameList.push(new pair(Document.prototype.getElementsByTagNameNS, "DOMDocument.getElementsByTagNameNS"));
		preDefNameList.push(new pair(Document.prototype.importNode, "DOMDocument.importNode"));
		preDefNameList.push(new pair(Document.prototype.querySelector, "DOMDocument.querySelector"));
		preDefNameList.push(new pair(Document.prototype.querySelectorAll, "DOMDocument.querySelectorAll"));

		preDefNameList.push(new pair(document, "HTMLDocumentGlobal"));
		preDefNameList.push(new pair(document.styleSheets, "StyleSheetListIns"));
		preDefNameList.push(new pair(document.body, "HTMLBodyElement"));

		preDefNameList.push(new pair(HTMLDocument, "HTMLDocumentCons"));
		preDefNameList.push(new pair(HTMLDocument.prototype, "HTMLDocumentProto"));
		preDefNameList.push(new pair(HTMLDocument.prototype.close, "HTMLDocument.close"));
		preDefNameList.push(new pair(HTMLDocument.prototype.getElementsByName, "HTMLDocument.getElementsByName"));
		preDefNameList.push(new pair(HTMLDocument.prototype.open, "HTMLDocument.open"));
		preDefNameList.push(new pair(HTMLDocument.prototype.write, "HTMLDocument.write"));
		preDefNameList.push(new pair(HTMLDocument.prototype.writeln, "HTMLDocument.writeln"));

		preDefNameList.push(new pair(NamedNodeMap, "NamedNodeMapCons"));
		preDefNameList.push(new pair(NamedNodeMap.prototype, "NamedNodeMapProto"));
		preDefNameList.push(new pair(NamedNodeMap.prototype.getNamedItem, "DOMNamedNodeMap.getNamedItem"));
		preDefNameList.push(new pair(NamedNodeMap.prototype.getNamedItemNS, "DOMNamedNodeMap.getNamedItemNS"));
		preDefNameList.push(new pair(NamedNodeMap.prototype.removeNamedItem, "DOMNamedNodeMap.removeNamedItem"));
		preDefNameList.push(new pair(NamedNodeMap.prototype.removeNamedItemNS, "DOMNamedNodeMap.removeNamedItemNS"));
		preDefNameList.push(new pair(NamedNodeMap.prototype.setNamedItem, "DOMNamedNodeMap.setNamedItem"));
		preDefNameList.push(new pair(NamedNodeMap.prototype.setNamedItemNS, "DOMNamedNodeMap.setNamedItemNS"));

		preDefNameList.push(new pair(NodeList, "NodeListCons"));
		preDefNameList.push(new pair(NodeList.prototype, "NodeListProto"));
		preDefNameList.push(new pair(NodeList.prototype.item, "DOMNodeList.item"));

		preDefNameList.push(new pair(Element, "ElementCons"));
		preDefNameList.push(new pair(Element.prototype, "ElementProto"));
		preDefNameList.push(new pair(Element.prototype.getAttribute, "DOMElement.getAttribute"));
		preDefNameList.push(new pair(Element.prototype.getAttributeNS, "DOMElement.getAttributeNS"));
		preDefNameList.push(new pair(Element.prototype.getAttributeNode, "DOMElement.getAttributeNode"));
		preDefNameList.push(new pair(Element.prototype.getAttributeNodeNS, "DOMElement.getAttributeNodeNS"));
		preDefNameList.push(new pair(Element.prototype.getBoundingClientRect, "DOMElement.getBoundingClientRect"));
		preDefNameList.push(new pair(Element.prototype.getClientRects, "DOMElement.getClientRects"));
		preDefNameList.push(new pair(Element.prototype.getElementsByTagName, "DOMElement.getElementsByTagName"));
		preDefNameList.push(new pair(Element.prototype.getElementsByClassName, "DOMElement.getElementsByClassName"));
		preDefNameList.push(new pair(Element.prototype.getElementsByTagNameNS, "DOMElement.getElementsByTagNameNS"));
		preDefNameList.push(new pair(Element.prototype.hasAttribute, "DOMElement.hasAttribute"));
		preDefNameList.push(new pair(Element.prototype.hasAttributeNS, "DOMElement.hasAttributeNS"));
		preDefNameList.push(new pair(Element.prototype.querySelector, "DOMElement.querySelector"));
		preDefNameList.push(new pair(Element.prototype.querySelectorAll, "DOMElement.querySelectorAll"));
		preDefNameList.push(new pair(Element.prototype.removeAttribute, "DOMElement.removeAttribute"));
		preDefNameList.push(new pair(Element.prototype.removeAttributeNS, "DOMElement.removeAttributeNS"));
		preDefNameList.push(new pair(Element.prototype.removeAttributeNode, "DOMElement.removeAttributeNode"));
		preDefNameList.push(new pair(Element.prototype.scrollIntoView, "DOMElement.scrollIntoView"));
		preDefNameList.push(new pair(Element.prototype.setAttribute, "DOMElement.setAttribute"));
		preDefNameList.push(new pair(Element.prototype.setAttributeNS, "DOMElement.setAttributeNS"));
		preDefNameList.push(new pair(Element.prototype.setAttributeNode, "DOMElement.setAttributeNode"));
		preDefNameList.push(new pair(Element.prototype.setAttributeNodeNS, "DOMElement.setAttributeNodeNS"));
		preDefNameList.push(new pair(Element.prototype.webkitMatchesSelector, "DOMElement.webkitMatchesSelector"));

		preDefNameList.push(new pair(HTMLElement, "HTMLElementCons"));
		preDefNameList.push(new pair(HTMLElement.prototype, "HTMLElementProto"));

		preDefNameList.push(new pair(HTMLAnchorElement, "HTMLAnchorElementCons"));
		preDefNameList.push(new pair(HTMLAnchorElement.prototype, "HTMLAnchorElementProto"));
		preDefNameList.push(new pair(HTMLAnchorElement.prototype.blur, "HTMLAnchorElement.submit"));
		preDefNameList.push(new pair(HTMLAnchorElement.prototype.focus, "HTMLAnchorElement.reset"));

		preDefNameList.push(new pair(Attr, "AttrCons"));
		preDefNameList.push(new pair(Attr.prototype, "AttrProto"));

		preDefNameList.push(new pair(String, "StringConst"));
		preDefNameList.push(new pair(String.prototype, "StringProto"));
		preDefNameList.push(new pair(String.prototype.charAt, "String.prototype.charAt"));
		preDefNameList.push(new pair(String.prototype.charCodeAt, "String.prototype.charCodeAt"));
		preDefNameList.push(new pair(String.prototype.concat, "String.prototype.concat"));
		preDefNameList.push(new pair(String.prototype.indexOf, "String.prototype.indexOf"));
		preDefNameList.push(new pair(String.prototype.lastIndexOf, "String.prototype.lastIndexOf"));
		preDefNameList.push(new pair(String.prototype.localeCompare, "String.prototype.localeCompare"));
		preDefNameList.push(new pair(String.prototype.match, "String.prototype.match"));
		preDefNameList.push(new pair(String.prototype.replace, "String.prototype.replace"));
		preDefNameList.push(new pair(String.prototype.search, "String.prototype.search"));
		preDefNameList.push(new pair(String.prototype.slice, "String.prototype.slice"));
		preDefNameList.push(new pair(String.prototype.split, "String.prototype.split"));
		preDefNameList.push(new pair(String.prototype.substr, "String.prototype.substr"));
		preDefNameList.push(new pair(String.prototype.substring, "String.prototype.substring"));
		preDefNameList.push(new pair(String.prototype.toLocaleLowerCase, "String.prototype.toLocaleLowerCase"));
		preDefNameList.push(new pair(String.prototype.toLocaleUpperCase, "String.prototype.toLocaleUpperCase"));
		preDefNameList.push(new pair(String.prototype.toLowerCase, "String.prototype.toLowerCase"));
		preDefNameList.push(new pair(String.prototype.toString, "String.prototype.toString"));
		preDefNameList.push(new pair(String.prototype.toUpperCase, "String.prototype.toUpperCase"));
		preDefNameList.push(new pair(String.prototype.trim, "String.prototype.trim"));
		preDefNameList.push(new pair(String.prototype.valueOf, "String.prototype.valueOf"));

		preDefNameList.push(new pair(RegExp, "RegExpConst"));
		preDefNameList.push(new pair(RegExp.prototype, "RegExpProto"));
		preDefNameList.push(new pair(RegExp.prototype.exec, "RegExp.prototype.exec"));
		preDefNameList.push(new pair(RegExp.prototype.test, "RegExp.prototype.test"));
		preDefNameList.push(new pair(RegExp.prototype.toString, "RegExp.prototype.toString"));

		preDefNameList.push(new pair(Number, "NumberConst"));
		preDefNameList.push(new pair(Number.prototype, "NumberProto"));
		preDefNameList.push(new pair(Number.prototype.toExponential, "Number.prototype.toExponential"));
		preDefNameList.push(new pair(Number.prototype.toFixed, "Number.prototype.toFixed"));
		preDefNameList.push(new pair(Number.prototype.toLocaleString, "Number.prototype.toLocaleString"));
		preDefNameList.push(new pair(Number.prototype.toPrecision, "Number.prototype.toPrecision"));
		preDefNameList.push(new pair(Number.prototype.toString, "Number.prototype.toString"));
		preDefNameList.push(new pair(Number.prototype.valueOf, "Number.prototype.valueOf"));

		preDefNameList.push(new pair(Math, "MathConst"));
		preDefNameList.push(new pair(Math.abs, "Math.abs"));
		preDefNameList.push(new pair(Math.acos, "Math.acos"));
		preDefNameList.push(new pair(Math.asin, "Math.asin"));
		preDefNameList.push(new pair(Math.atan, "Math.atan"));
		preDefNameList.push(new pair(Math.atan2, "Math.atan2"));
		preDefNameList.push(new pair(Math.ceil, "Math.ceil"));
		preDefNameList.push(new pair(Math.cos, "Math.cos"));
		preDefNameList.push(new pair(Math.exp, "Math.exp"));
		preDefNameList.push(new pair(Math.floor, "Math.floor"));
		preDefNameList.push(new pair(Math.log, "Math.log"));
		preDefNameList.push(new pair(Math.max, "Math.max"));
		preDefNameList.push(new pair(Math.min, "Math.min"));
		preDefNameList.push(new pair(Math.pow, "Math.pow"));
		preDefNameList.push(new pair(Math.random, "Math.random"));
		preDefNameList.push(new pair(Math.round, "Math.round"));
		preDefNameList.push(new pair(Math.sin, "Math.sin"));
		preDefNameList.push(new pair(Math.sqrt, "Math.sqrt"));
		preDefNameList.push(new pair(Math.tan, "Math.tan"));

		preDefNameList.push(new pair(JSON, "JSONConst"));
		preDefNameList.push(new pair(JSON.parse, "JSON.parse"));
		preDefNameList.push(new pair(JSON.stringify, "JSON.stringify"));

		preDefNameList.push(new pair(Date, "DateConst"));
		preDefNameList.push(new pair(Date.UTC, "Date.UTC"));
		preDefNameList.push(new pair(Date.now, "Date.now"));
		preDefNameList.push(new pair(Date.parse, "Date.parse"));
		preDefNameList.push(new pair(Date.prototype, "DateProto"));
		preDefNameList.push(new pair(Date.prototype.getDate, "Date.prototype.getDate"));
		preDefNameList.push(new pair(Date.prototype.getDay, "Date.prototype.getDay"));
		preDefNameList.push(new pair(Date.prototype.getFullYear, "Date.prototype.getFullYear"));
		preDefNameList.push(new pair(Date.prototype.getHours, "Date.prototype.getHours"));
		preDefNameList.push(new pair(Date.prototype.getMilliseconds, "Date.prototype.getMilliseconds"));
		preDefNameList.push(new pair(Date.prototype.getMinutes, "Date.prototype.getMinutes"));
		preDefNameList.push(new pair(Date.prototype.getMonth, "Date.prototype.getMonth"));
		preDefNameList.push(new pair(Date.prototype.getSeconds, "Date.prototype.getSeconds"));
		preDefNameList.push(new pair(Date.prototype.getTime, "Date.prototype.getTime"));
		preDefNameList.push(new pair(Date.prototype.getTimezoneOffset, "Date.prototype.getTimezoneOffset"));
		preDefNameList.push(new pair(Date.prototype.getUTCDate, "Date.prototype.getUTCDate"));
		preDefNameList.push(new pair(Date.prototype.getUTCDay, "Date.prototype.getUTCDay"));
		preDefNameList.push(new pair(Date.prototype.getUTCFullYear, "Date.prototype.getUTCFullYear"));
		preDefNameList.push(new pair(Date.prototype.getUTCHours, "Date.prototype.getUTCHours"));
		preDefNameList.push(new pair(Date.prototype.getUTCMilliseconds, "Date.prototype.getUTCMilliseconds"));
		preDefNameList.push(new pair(Date.prototype.getUTCMinutes, "Date.prototype.getUTCMinutes"));
		preDefNameList.push(new pair(Date.prototype.getUTCMonth, "Date.prototype.getUTCMonth"));
		preDefNameList.push(new pair(Date.prototype.getUTCSeconds, "Date.prototype.getUTCSeconds"));
		preDefNameList.push(new pair(Date.prototype.setDate, "Date.prototype.setDate"));
		preDefNameList.push(new pair(Date.prototype.setFullYear, "Date.prototype.setFullYear"));
		preDefNameList.push(new pair(Date.prototype.setHours, "Date.prototype.setHours"));
		preDefNameList.push(new pair(Date.prototype.setMilliseconds, "Date.prototype.setMilliseconds"));
		preDefNameList.push(new pair(Date.prototype.setMinutes, "Date.prototype.setMinutes"));
		preDefNameList.push(new pair(Date.prototype.setMonth, "Date.prototype.setMonth"));
		preDefNameList.push(new pair(Date.prototype.setSeconds, "Date.prototype.setSeconds"));
		preDefNameList.push(new pair(Date.prototype.setTime, "Date.prototype.setTime"));
		preDefNameList.push(new pair(Date.prototype.setUTCDate, "Date.prototype.setUTCDate"));
		preDefNameList.push(new pair(Date.prototype.setUTCFullYear, "Date.prototype.setUTCFullYear"));
		preDefNameList.push(new pair(Date.prototype.setUTCHours, "Date.prototype.setUTCHours"));
		preDefNameList.push(new pair(Date.prototype.setUTCMilliseconds, "Date.prototype.setUTCMilliseconds"));
		preDefNameList.push(new pair(Date.prototype.setUTCMinutes, "Date.prototype.setUTCMinutes"));
		preDefNameList.push(new pair(Date.prototype.setUTCMonth, "Date.prototype.setUTCMonth"));
		preDefNameList.push(new pair(Date.prototype.setUTCSeconds, "Date.prototype.setUTCSeconds"));
		preDefNameList.push(new pair(Date.prototype.toDateString, "Date.prototype.toDateString"));
		preDefNameList.push(new pair(Date.prototype.toGMTString, "Date.prototype.toUTCString"));
		preDefNameList.push(new pair(Date.prototype.toISOString, "Date.prototype.toISOString"));
		preDefNameList.push(new pair(Date.prototype.toJSON, "Date.prototype.toJSON"));
		preDefNameList.push(new pair(Date.prototype.toLocaleDateString, "Date.prototype.toLocaleDateString"));
		preDefNameList.push(new pair(Date.prototype.toLocaleString, "Date.prototype.toLocaleString"));
		preDefNameList.push(new pair(Date.prototype.toLocaleTimeString, "Date.prototype.toLocaleTimeString"));
		preDefNameList.push(new pair(Date.prototype.toString, "Date.prototype.toString"));
		preDefNameList.push(new pair(Date.prototype.toTimeString, "Date.prototype.toTimeString"));
		preDefNameList.push(new pair(Date.prototype.toUTCString, "Date.prototype.toUTCString"));
		preDefNameList.push(new pair(Date.prototype.valueOf, "Date.prototype.valueOf"));

		preDefNameList.push(new pair(Boolean, "BooleanConst"));
		preDefNameList.push(new pair(Boolean.prototype, "BooleanProto"));
		preDefNameList.push(new pair(Boolean.prototype.toString, "Boolean.prototype.toString"));
		preDefNameList.push(new pair(Boolean.prototype.valueOf, "Boolean.prototype.valueOf"));

		preDefNameList.push(new pair(Array, "ArrayConst"));
		preDefNameList.push(new pair(Array.isArray, "Array.isArray"));
		preDefNameList.push(new pair(Array.prototype, "ArrayProto"));
		preDefNameList.push(new pair(Array.prototype.concat, "Array.prototype.concat"));
		preDefNameList.push(new pair(Array.prototype.every, "Array.prototype.every"));
		preDefNameList.push(new pair(Array.prototype.filter, "Array.prototype.filter"));
		preDefNameList.push(new pair(Array.prototype.forEach, "Array.prototype.forEach"));
		preDefNameList.push(new pair(Array.prototype.indexOf, "Array.prototype.indexOf"));
		preDefNameList.push(new pair(Array.prototype.join, "Array.prototype.join"));
		preDefNameList.push(new pair(Array.prototype.lastIndexOf, "Array.prototype.lastIndexOf"));
		preDefNameList.push(new pair(Array.prototype.map, "Array.prototype.map"));
		preDefNameList.push(new pair(Array.prototype.pop, "Array.prototype.pop"));
		preDefNameList.push(new pair(Array.prototype.push, "Array.prototype.push"));
		preDefNameList.push(new pair(Array.prototype.reduce, "Array.prototype.reduce"));
		preDefNameList.push(new pair(Array.prototype.reduceRight, "Array.prototype.reduce"));
		preDefNameList.push(new pair(Array.prototype.reverse, "Array.prototype.reverse"));
		preDefNameList.push(new pair(Array.prototype.shift, "Array.prototype.shift"));
		preDefNameList.push(new pair(Array.prototype.slice, "Array.prototype.slice"));
		preDefNameList.push(new pair(Array.prototype.some, "Array.prototype.some"));
		preDefNameList.push(new pair(Array.prototype.sort, "Array.prototype.sort"));
		preDefNameList.push(new pair(Array.prototype.splice, "Array.prototype.splice"));
		preDefNameList.push(new pair(Array.prototype.toLocaleString, "Array.prototype.toLocaleString"));
		preDefNameList.push(new pair(Array.prototype.toString, "Array.prototype.toString"));
		preDefNameList.push(new pair(Array.prototype.unshift, "Array.prototype.unshift"));

		preDefNameList.push(new pair(URIError, "URIErrConst"));
		preDefNameList.push(new pair(URIError.prototype, "URIErrProto"));

		preDefNameList.push(new pair(TypeError, "TypeErrConst"));
		preDefNameList.push(new pair(TypeError.prototype, "TypeErrProto"));

		preDefNameList.push(new pair(SyntaxError, "SyntaxErrConst"));
		preDefNameList.push(new pair(SyntaxError.prototype, "SyntaxErrProto"));

		preDefNameList.push(new pair(ReferenceError, "RefErrConst"));
		preDefNameList.push(new pair(ReferenceError.prototype, "RefErrProto"));

		preDefNameList.push(new pair(RangeError, "RangeErrConst"));
		preDefNameList.push(new pair(RangeError.prototype, "RangeErrProto"));

		preDefNameList.push(new pair(EvalError, "EvalErrConst"));
		preDefNameList.push(new pair(EvalError.prototype, "EvalErrProto"));

		preDefNameList.push(new pair(Error, "ErrConst"));
		preDefNameList.push(new pair(Error.prototype, "ErrProto"));

		preDefNameList.push(new pair(Object, "ObjectConst"));
		preDefNameList.push(new pair(Object.create, "Object.create"));
		preDefNameList.push(new pair(Object.defineProperties, "Object.defineProperties"));
		preDefNameList.push(new pair(Object.defineProperty, "Object.defineProperty"));
		preDefNameList.push(new pair(Object.freeze, "Object.freeze"));
		preDefNameList.push(new pair(Object.getOwnPropertyDescriptor, "Object.getOwnPropertyDescriptor"));
		preDefNameList.push(new pair(Object.getOwnPropertyNames, "Object.getOwnPropertyNames"));
		preDefNameList.push(new pair(Object.getPrototypeOf, "Object.getPrototypeOf"));
		preDefNameList.push(new pair(Object.isExtensible, "Object.isExtensible"));
		preDefNameList.push(new pair(Object.isFrozen, "Object.isFrozen"));
		preDefNameList.push(new pair(Object.isSealed, "Object.isSealed"));
		preDefNameList.push(new pair(Object.keys, "Object.keys"));
		preDefNameList.push(new pair(Object.preventExtensions, "Object.preventExtensions"));
		preDefNameList.push(new pair(Object.seal, "Object.seal"));
		
		preDefNameList.push(new pair(Function, "FunctionConst"));
		preDefNameList.push(new pair(Function.prototype, "FunctionProto"));
		preDefNameList.push(new pair(Function.prototype.apply, "Function.prototype.apply"));
		preDefNameList.push(new pair(Function.prototype.bind, "Function.prototype.bind"));
		preDefNameList.push(new pair(Function.prototype.call, "Function.prototype.call"));
		preDefNameList.push(new pair(Function.prototype.toString, "Function.prototype.toString"));

		preDefNameList.push(new pair(Object.prototype, "Object.prototype"));
		preDefNameList.push(new pair(Object.prototype.hasOwnProperty, "Object.prototype.hasOwnProperty"));
		preDefNameList.push(new pair(Object.prototype.isPrototypeOf, "Object.prototype.isPrototypeOf"));
		preDefNameList.push(new pair(Object.prototype.propertyIsEnumerable, "Object.prototype.propertyIsEnumerable"));
		preDefNameList.push(new pair(Object.prototype.toLocaleString, "Object.prototype.toLocaleString"));
		preDefNameList.push(new pair(Object.prototype.toString, "Object.prototype.toString"));
		preDefNameList.push(new pair(Object.prototype.valueOf, "Object.prototype.valueOf"));

		preDefNameList.push(new pair(global, "Global"));
		preDefNameList.push(new pair(global.CanvasGradient, "CanvasGradientIns"));
		preDefNameList.push(new pair(global.CanvasRenderingContext2D, "CanvasRenderingContext2DIns"));
		preDefNameList.push(new pair(global.addEventListener, "window.EventTarget.addEventListener"));
		preDefNameList.push(new pair(global.alert, "DOMWindow.alert"));
		preDefNameList.push(new pair(global.atob, "DOMWindow.atob"));
		preDefNameList.push(new pair(global.blur, "DOMWindow.blur"));
		preDefNameList.push(new pair(global.btoa, "DOMWindow.btoa"));
		preDefNameList.push(new pair(global.clearInterval, "DOMWindow.clearInterval"));
		preDefNameList.push(new pair(global.clearTimeout, "DOMWindow.clearTimeout"));
		preDefNameList.push(new pair(global.close, "DOMWindow.close"));
		preDefNameList.push(new pair(global.confirm, "DOMWindow.confirm"));
		preDefNameList.push(new pair(global.decodeURI, "Global.decodeURI"));
		preDefNameList.push(new pair(global.decodeURIComponent, "Global.decodeURIComponent"));
		preDefNameList.push(new pair(global.dispatchEvent, "window.EventTarget.dispatchEvent"));
		preDefNameList.push(new pair(global.encodeURI, "Global.encodeURI"));
		preDefNameList.push(new pair(global.encodeURIComponent, "Global.encodeURIComponent"));
		preDefNameList.push(new pair(global.escape, "DOMWindow.escape"));
		preDefNameList.push(new pair(global.eval, "Global.eval"));
		preDefNameList.push(new pair(global.focus, "DOMWindow.focus"));
		preDefNameList.push(new pair(global.getComputedStyle, "DOMWindow.getComputedStyle"));
		preDefNameList.push(new pair(global.isFinite, "Global.isFinite"));
		preDefNameList.push(new pair(global.isNaN, "Global.isNaN"));
		preDefNameList.push(new pair(global.localStorage, "StorageIns"));
		preDefNameList.push(new pair(global.moveBy, "DOMWindow.moveBy"));
		preDefNameList.push(new pair(global.moveTo, "DOMWindow.moveTo"));
		preDefNameList.push(new pair(global.open, "DOMWindow.open"));
		preDefNameList.push(new pair(global.parseFloat, "Global.parseFloat"));
		preDefNameList.push(new pair(global.parseInt, "Global.parseInt"));
		preDefNameList.push(new pair(global.postMessage, "DOMWindow.postMessage"));
		preDefNameList.push(new pair(global.print, "DOMWindow.print"));
		preDefNameList.push(new pair(global.prompt, "DOMWindow.prompt"));
		preDefNameList.push(new pair(global.removeEventListener, "window.EventTarget.removeEventListener"));
		preDefNameList.push(new pair(global.resizeBy, "DOMWindow.resizeBy"));
		preDefNameList.push(new pair(global.resizeTo, "DOMWindow.resizeTo"));
		preDefNameList.push(new pair(global.scroll, "DOMWindow.scroll"));
		preDefNameList.push(new pair(global.scrollBy, "DOMWindow.scrollBy"));
		preDefNameList.push(new pair(global.scrollTo, "DOMWindow.scrollTo"));
		preDefNameList.push(new pair(global.sessionStorage, "StorageIns2"));
		preDefNameList.push(new pair(global.setInterval, "DOMWindow.setInterval"));
		preDefNameList.push(new pair(global.setTimeout, "DOMWindow.setTimeout"));
		preDefNameList.push(new pair(global.stop, "DOMWindow.stop"));
		preDefNameList.push(new pair(global.unescape, "DOMWindow.unescape"));
		
		for(var i=0; i<preDefNameList.length; i++){
			var p = preDefNameList[i];
			assignNewLoc(p.fst, p.snd);
		}
	};
	
	// array data type storage
	function array_obj(_value){
		this.value = _value;
	};
	
	var stack = [];
	
	// push an object to queue
	function push_stack(_obj){
		var newprop = bprop + "." + gprop;
		assign(_obj, newprop);
		
		checkPrinted(_obj);
		stack.push(_obj);
	};

	// poll an object from queue
	function poll_stack(){
		var o = stack.pop();
		bprop = get(o);
		return o;
	};
		
	// get a descriptor about the property of a object
	function getDescriptor(_obj, _prop, undefined){
		try{
			var desc = Object.getOwnPropertyDescriptor(_obj, _prop);
			if(typeof desc === 'undefined'){
				var v = getOwnPropValue(_obj, _prop);
                return {'value':v, 'writable':false, 'enumerable':false, 'configurable':false};
			}
		}catch(e){
			return {'value':undefined, 'writable':false, 'enumerable':false, 'configurable':false};
		}
        if(desc.hasOwnProperty('writable')){
            return desc;
        }else{
            try{
                var v = getOwnPropValue(_obj, _prop);
            }catch(e){
                v = undefined;
            }
            return {'value':v, 'writable':true, 'enumerable':desc.enumerable, 'configurable':desc.configurable};
        }
		return desc;
	};

	// get property names of a object 
	function getPropNames(_obj){
		var externProps = nExtList.getProps(_obj);
		return Object.getOwnPropertyNames(_obj).concat(externProps);
	};
	
	// return a fresh location	
	function freshLoc(){
		return loc++;
	};
	
	// string escape
	function escape(str) {
	    var res = str.replace(/\\/g, "\\\\");
	    res = res.replace(/\"/g, "\\\"");
	    return res;
	};
	
	// put a value to a property of an object. if the object is not extensible, then the value is recorded in nExtList.
	function putPropValue(_obj, _prop, _value){
		var trip = new triple(_obj, _prop, _value);
		nExtList.add(trip);
	};

	// get a value of a property of an object. if the object does not have the property, then the value is read from nExtList.	
	function getOwnPropValue(_obj, _prop){
		switch(isInternal(_prop)){
		case true:
			return nExtList.get(_obj,_prop);
		case false:
            if (_prop === "__proto__") return Object.getPrototypeOf(_obj);
			return _obj[_prop];
		default:
			throw "# isInternal function can't return non-boolean value. prop: " + _prop;			
		}
	};
	
	// assign new location to an object
	function assignNewLoc(_obj, _name){
		switch(typeof _obj){
		case 'undefined':
			throw "# can't assign a location to undefined object: " + _name;
		default:
			switch(isNewObj(_obj)){
			case true:
				var f_loc = (typeof _name === 'undefined')? freshLoc() : _name;
				putPropValue(_obj, locF, f_loc);
				break;
			case false:
				if(PREDLOC_DEBUG){
					var pre_loc = getLoc(_obj);
					
					console.log("# trying to reassign predefined location to an object ");
					console.log("	pre: " + pre_loc);
					console.log("	new: " + _name);
				}
				break;
			default:
				throw "# isNewObj function can't return non-boolean value. Object: " + _obj;
			}
		}		
	};
	
	// get a location of an object
	function getLoc(_obj){
		var l = getOwnPropValue(_obj, locF);
		
		switch(isNaN(l)){
		case true:
			return l;
		case false:
			return l * 1;
		}
	};
	
	// check an object is a lexical object or not
	function isLexical(_obj){
		var lexProps = getPropNames(__lexicals);
		for (var j=0; j<lexProps.length; j++) {
			var m = lexProps[j];
	        var scope_obj = __lexicals[m];
	        if(_obj == scope_obj)
				return true;
	    }
		return false;
	};
	
	function isLexicalMap(_obj){
		var lexProps = getPropNames(__lexicals);
		for (var j=0; j<lexProps.length; j++) {
			var m = lexProps[j];
	        var scope_obj = __lexicals[m];
	        if(_obj == scope_obj.map)
				return true;
	    }
		return false;
	};
	
	//check an object is extensible or not
	function isExtensible(_obj){
		return Object.isExtensible(_obj);
	};
	
	// check a object is fresh or not
	function isNewObj(_obj){
		var loc = getLoc(_obj);
		switch(typeof loc){
		case 'undefined':
			return true;
		default:
			return false;
		}
	};

	//
	function isPrinted(_obj){
		var p = getOwnPropValue(_obj, "[[printed]]");
		switch(typeof p){
		case 'undefined':
			return false;
		default:
			return true;
		}
	};
	
	//
	function checkPrinted(_obj){
		putPropValue(_obj, "[[printed]]", true);
	};
	
	// check a property is internal or not
	function isInternal(_prop){
		switch(isNaN(_prop)){
		case true:
			switch(_prop.indexOf(interPS)){
			case 0:
				return true;
			default:
				return false;
			}
		case false:
			return false;
		default:
			throw "# isNaN function can't return non-boolean value. prop: " + _prop;
		}
	};
	
	// check an object is excepted for print or not
	function isExcepted(_obj){
		for(var i=0; i<exceptList.length; i++){
			var o = exceptList[i];
			if(o == _obj)
				return true;
		}
		
		if(_obj instanceof MimeType)
			return true;
		return false;
	};
	
	// check a property is real member or not
	function isRealMember(_prop){
		for(var i=0; i<instMember.length; i++){
			var m = instMember[i];
			if(m == _prop)
				return false;
		}
		if(_prop.indexOf('__temp') == 0)
			return false;
		
		return true;
	};
	
	// attach internal properties to external properties of the object
	function propExtern(_obj, undefined){
		if(isLexicalMap(_obj)){
			return;
		}
		
	    if (typeof _obj == 'function') { // for scope property
			var os = getOwnPropValue(_obj, "_outscope");
	        if (os == 1 || (typeof os) == 'undefined')
	            putPropValue(_obj, interPS + "Scope" + interPE, null);
	        else
	            putPropValue(_obj, interPS + "Scope" + interPE, getOwnPropValue(__lexicals, os));
			
			var span = getOwnPropValue(_obj, spanF);
			if(span)
				putPropValue(_obj, interPS + "span" + interPE, new array_obj(span));
	    }

	    try { // for extensible, proto, class, primitive property
			putPropValue(_obj, interPS + "Extensible" + interPE, isExtensible(_obj));
			putPropValue(_obj, interPS + "Prototype" + interPE, getOwnPropValue(_obj, "__proto__"));
			
			if(typeof _obj.prototype != 'undefined')
				putPropValue(_obj, interPS + "HasInstance" + interPE, null);
			if(typeof getOwnPropValue(_obj, interPS + "Class" + interPE) == 'undefined'){
				if(_obj === JSON) {
					putPropValue(_obj, interPS + "Class" + interPE, "JSON");	
				} else if(_obj === Math) {
					putPropValue(_obj, interPS + "Class" + interPE, "Math");	
				} else if (_obj === Object.prototype) {
					putPropValue(_obj, interPS + "Class" + interPE, "Object");
				} else if (_obj === Function.prototype) {
					putPropValue(_obj, interPS + "Class" + interPE, "Function");
				} else if (_obj === Date.prototype) {
					putPropValue(_obj, interPS + "Class" + interPE, "Date");
					putPropValue(_obj, interPS + "PrimitiveValue" + interPE, NaN);
				} else if (_obj === Array.prototype) {
					putPropValue(_obj, interPS + "Class" + interPE, "Array");
				} else if (_obj === String.prototype) {
					putPropValue(_obj, interPS + "Class" + interPE, "String");
				} else if (_obj === Boolean.prototype) {
					putPropValue(_obj, interPS + "Class" + interPE, "Boolean");
				} else if (_obj === Number.prototype) {
					putPropValue(_obj, interPS + "Class" + interPE, "Number");
				} else if (_obj === RegExp.prototype) {
					putPropValue(_obj, interPS + "Class" + interPE, "RegExp");
				} else if (_obj === Error.prototype) {
					putPropValue(_obj, interPS + "Class" + interPE, "Error");
				} else if (_obj instanceof Number) {
					putPropValue(_obj, interPS + "Class" + interPE, "Number");
					putPropValue(_obj, interPS + "PrimitiveValue" + interPE, _obj.valueOf());
				} else if (_obj instanceof String) {
					putPropValue(_obj, interPS + "Class" + interPE, "String");
					putPropValue(_obj, interPS + "PrimitiveValue" + interPE, _obj.valueOf());
				} else if (_obj instanceof Boolean) {
					putPropValue(_obj, interPS + "Class" + interPE, "Boolean");
					putPropValue(_obj, interPS + "PrimitiveValue" + interPE, _obj.valueOf());
				} else if (_obj instanceof RegExp) {
					putPropValue(_obj, interPS + "Class" + interPE, "RegExp");
				} else if (_obj instanceof Date) {
					putPropValue(_obj, interPS + "Class" + interPE, "Date");
					putPropValue(_obj, interPS + "PrimitiveValue" + interPE, _obj.valueOf());
				} else if (_obj instanceof Error) {
					putPropValue(_obj, interPS + "Class" + interPE, "Error");
				} else if (_obj instanceof Array)
					putPropValue(_obj, interPS + "Class" + interPE, "Array");
					else if (_obj instanceof Function)
						putPropValue(_obj, interPS + "Class" + interPE, "Function");
					else
						putPropValue(_obj, interPS + "Class" + interPE, "Object");
			}
	    } catch (e) {
	        console.log("# can't modified this: " + _obj);
			console.log("# fail extern: " + e);
	    }
	};
	
	
	/* this code is gotten from http://davidwalsh.name/detect-native-function */
	
  	var isNative;
	(function(){
		// Used to resolve the decompiled source of functions
		var fnToString = Function.prototype.toString;
  
		// Used to detect host constructors (Safari > 4; really typed array specific)
		var reHostCtor = /^\[object .+?Constructor\]$/;

		// Compile a regexp using a common native method as a template.
		// We chose `Object#toString` because there's a good chance it is not being mucked with.
		var reNative = RegExp('^' +
		  // Coerce `Object#toString` to a string
		  String(toString)
		  // Escape any special regexp characters
		  .replace(/[.*+?^${}()|[\]\/\\]/g, '\\$&')
		  // Replace mentions of `toString` with `.*?` to keep the template generic.
		  // Replace thing like `for ...` to support environments like Rhino which add extra info
		  // such as method arity.
		  .replace(/toString|(function).*?(?=\\\()| for .+?(?=\\\])/g, '$1.*?') + '$'
		);
  
		isNative = function(value) {
		  var type = typeof value;
	  
	  
		  var ret = (type == 'function'
		    // Use `Function#toString` to bypass the value's own `toString` method
		    // and avoid being faked out.
		    ? reNative.test(fnToString.call(value))
		    // Fallback to a host object check because some environments will represent
		    // things like typed arrays as DOM methods which may not conform to the
		    // normal native pattern.
		    : (value && type == 'object' && reHostCtor.test(toString.call(value))) || false);

			return ret;
		};
	})();
	/*******************************************************************************/
	
	
   /* Initialize routines for SAFE*/
	
	// transform lexical objects to beautiful form
	function initLexicals() {
		var lexProps = getPropNames(__lexicals);
		for (var j=0; j<lexProps.length; j++) {
			var m = lexProps[j];
	        var scope_obj = __lexicals[m];
	        scope_obj.map[interPS + "outer"] = (typeof __lexicals[scope_obj.pfid] == 'undefined')?null:__lexicals[scope_obj.pfid].map;
			scope_obj.map[interPS + "span"] = new array_obj(scope_obj.span);
	    }
	};
	
	// assign events to target object properties
	function initEvents(){
		try{
			for(var i=0; i<__event_info.length; i++){
				var info = __event_info[i];
				var event = info.event;
				var target = info.this;
				var listener = info.listener;
				var capture = (info.useCapture)? "c" : "b";
				var event_name = interPS + "event_" + event + "_" + capture;
				if(target[event_name])
					target[event_name].value.push(listener);
				else
					target[event_name] = new array_obj([ listener ]);
			}
		}catch(e){
			console.log("can't initialize event properties: "+e);
		}
	};

	// check is predefined location or not
	function isPredefined(_obj){
		for(var i=0; i<l.length; i++){
			var trip = l[i];
			var obj = trip.fst;
			var prop = trip.snd;
			var val = trip.thd;
			
			if(_obj === obj && prop === locF){ //predefined location!
				if(isNaN(locF))
					return true;
				else
					return false;
			}
		}
		throw "no matched location!";
	};
	
	function checkPredefinedFunction(){
		var l = nExtList.list;
		var t = [];
		var printed = function(loc){
			for(var i=0; i<t.length; i++){
				var ploc = t[i];
				if(loc === ploc)
					return true;
			}
			return false;
		};
		for(var i=0; i<l.length; i++){
			var trip = l[i];
			var obj = trip.fst;
			var prop = trip.snd;
			var val = trip.thd;
			
			if(prop === locF && isNaN(locF)){ //predefined location!
				if(typeof obj === 'function' && printed(val) === false){ //predefined function!
					console.log("#loc: "+val); // location;
					console.log(obj); //the target function
					console.log("---------"); // borderline
				}
			}
		}
	};
	
	var totprint = [];
	var totreq = [];
	
	function req(_o){
		var isin = false;
		for(var i=0; i < totreq.length; i++){
			var o = totreq[i];
			if(o === _o){
				isin = true;
				break;
			}
		}
		if(isin === false){
			totreq.push(_o);
		}
	};
	
	function print(_o){
		var isin = false;
		for(var i=0; i<totprint.length; i++){
			var o = totprint[i];
			if(_o === o){
				isin = true;
				console.log("#error: samelocation is printed: "+ _o);
				break;
			}
		}
		if(isin === false)
			totprint.push(_o);
	};
	
	function check(){
		var count = 0;
		var unmatched = [];
		for(var i=0; i<totreq.length; i++){
			var r = totreq[i];
			var is = false;
			for(var j=0; j<totprint.length; j++){
				var p = totprint[j];
				if(r === p){
					is = true;
					count++;
					break;
				}
			}
			if(is === false){
				console.log("#missing: "+r);
				unmatched.push(r);
			}
		}
		var ustr = "";
		for(var k=0; k<unmatched.length; k++){
			var u = unmatched[k];
			if(k === 0)
				ustr += u;
			else
				ustr += ", "+u;
		}
		console.log("########################")
		console.log(" reference #loc: " + totreq.length);
		console.log(" printed #loc:   " + totprint.length);
		console.log(" unmatched #loc: " + (totreq.length - count));
		if(unmatched.length > 0)
			console.log(" unmatched locs: " + ustr);
		console.log("########################")
	};
	
	var objnames = [];
	
	function assign(_obj, name){
		objnames.push(new pair(_obj, name));
	};
	
	function get(_obj){
		for(var i=0; i<objnames.length; i++){
			var np = objnames[i];
			var obj = np.fst;
			if(obj === _obj)
				return np.snd;
		}
		return false;
	};
	
	var gprop;
	var bprop;
	assign(window, "window");
	assign(document, "document");
   /* JSED Format */
   /*
    *JSEDObject: JSEDObject
    *{ w }
    *{ w JSEDLocMembers w }
    */	
	function JSEDObject(_obj){
		bprop = "window";
		checkPrinted(_obj);
		this.value = new JSEDLocMembers(_obj);
		this.valueOf = function(){
			var str = "{\n";
			str = str + this.value.valueOf();
			str = str + "\n}";
			return str;
		};
		this.classcheck = function(){
			return this.value.classcheck();
		};
		this.missingcheck = function(){
			this.value.missingcheck();
			check();
		};
	};
	
	
	/*
	 * JSEDLocMembers : List[(String,List[(String,JSEDValue)])]
	 * JSEDLocPair
	 * JSEDLocPair w , w JSEDLocMembers
	 */
	function JSEDLocMembers(_obj){
		var obj = _obj;
		this.value = [];
		this.ttt = [];
		do{
			propExtern(obj);
			this.value.push(new JSEDLocPair(obj));
			this.ttt.push(obj);
			obj = poll_stack();
			
		}while(obj != null);
		
		//this.value.push(new JSEDLocPair(__interval_info));
		//this.value.push(new JSEDLocPair(__timeout_info));
		
		obj = poll_stack();

		while(obj != null){
			curobj = obj;
			propExtern(obj);
			this.value.push(new JSEDLocPair(obj));
			obj = poll_stack();
		}
		
		this.valueOf = function(){
			var str = "";
			for(var i=0; i<this.value.length; i++){
				var val = this.value[i];
				if(i != 0)
					str = str + ",\n";
				str = str + val.valueOf();
			}
			return str;
		};
		this.classcheck = function(){
			var check = 0;
			for(var i=0; i<this.value.length; i++){
				var val = this.value[i];
				var t = this.ttt[i];
				if(val.classcheck())
					check++;
			}
			return (check+2) == this.value.length;
		};
		this.missingcheck = function(){
			for(var i=0; i<this.value.length; i++){
				var val = this.value[i];
				val.missingcheck();
			}
		};
	};
	
	/*
	 * JSEDLocPair: (String, List[(String,JSEDValue)])
	 * JSEDLoc w : w JSEDObjectValue
	 * JSEDLoc w : w JSEDNative
	 */
	function JSEDLocPair(_obj){
		if(isNewObj(_obj))
			assignNewLoc(_obj);
		var loc = getLoc(_obj);
		this.fst = new JSEDLoc(loc);
		this.snd = new JSEDObjectValue(_obj, false);
		this.valueOf = function(){
			return this.fst.valueOf() + ": " + this.snd.valueOf();
		};
		this.classcheck = function(){
			if(this.snd instanceof JSEDNative)
				return true;
			else
				return this.snd.classcheck();
		};
		this.missingcheck = function(){
			print(this.fst.valueOf());
			if((this.snd instanceof JSEDNative) == false)
				this.snd.missingcheck();
		};
	};
	
	/*
	 * JSEDLoc: String
	 *   # JSEDNumber
	 *   # JSEDString
	 */
	function JSEDLoc(_loc){
        this.type = typeof _loc;
		switch(this.type){
			case 'string': this.value = new JSEDString(_loc); break;
			case 'number': this.value = new JSEDNumber(_loc); break;
			default: throw "# non-compatible type for JSEDLoc: " + _loc + "( " + (typeof _loc) + " )";
		}
		this.valueOf = function(){
            var loc = this.value.valueOf();
            if(this.type === "string"){
                loc = loc[0] + "#" + loc.substr(1);
            }else{
                loc = '"' + "#" + loc + '"';
            }
            return loc;
			//return "#" + this.value.valueOf();
		};
	};
	
	/*
	 * JSEDObjectValue: List[(String,JSEDValue)]
	 *   [ w ]
	 *   [ w JSEDValueMembers w ]
	 */
	function JSEDObjectValue(_obj, _descFlag){
		this.value = new JSEDValueMembers(_obj, _descFlag);
		this.valueOf = function(){
			var str = "{\n";
			str = str + this.value.valueOf();
			str = str + "\n}";
			return str;
		};
		this.classcheck = function(){
			return this.value.classcheck();
		};
		this.missingcheck = function(){
			this.value.missingcheck();
		};
	};
	
	/*
	 * JSEDValueMembers: List[(String,JSEDValue)]
	 *   JSEDValuePair w , w JSEDValueMembers
	 */
	function JSEDValueMembers(_obj, _descFlag){
		this.value = [];
		if(typeof _obj === 'function' && isNative(_obj)){
			var funcname = getOwnPropValue(_obj, interPS + "funcname");
			if(typeof funcname === 'undefined'){
                var nativename = get(_obj);
                //native(_obj, nativename);
                //throw "# unknown native name: " + get(_obj);
            }
		}
		var props = getPropNames(_obj);
		for(var i=0; i<props.length; i++){
			var prop = props[i];
			if(_descFlag === false)
				gprop = prop;
			if(isRealMember(prop) && isGlobalExcepted(_obj, prop) == false){
				this.value.push(new JSEDValuePair(_obj, prop, _descFlag));
			}
		}
		this.valueOf = function(){
			var str = "";
			for(var i=0; i<this.value.length; i++){
				var val = this.value[i];
				if(i != 0)
					str = str + ",\n";
				str = str + val.valueOf();
			}
			return str;
		};
		this.classcheck = function(){
			var check = false;
			for(var i=0; i<this.value.length; i++){
				var val = this.value[i];
				check |= val.classcheck();
			}
			return check;
		};
		this.missingcheck = function(){
			for(var i=0; i<this.value.length; i++){
				var val = this.value[i];
				val.missingcheck();
			}
		};
	};
	
	/*
	 * JSEDValuePair: (String, JSEDValue)
	 *   JSEDString w : w JSEDValue
	 */
	function JSEDValuePair(_obj, _prop, _descFlag){
		this.fst = new JSEDString(_prop);
		
		if(isInternal(_prop) || _descFlag){	
			this.snd = new JSEDValue(getDescriptor(_obj, _prop).value, isInternal(_prop), _descFlag);
		}else
			this.snd = new JSEDValue(getDescriptor(_obj, _prop), isInternal(_prop), _descFlag);
		this.valueOf = function(){
			return this.fst.valueOf() + ": " + this.snd.valueOf();
		};
		this.classcheck = function(){
			if(this.fst.classcheck())
				return true;
			else
				return false;
		};
		this.missingcheck = function(){
			this.snd.missingcheck();
		};
	};
	
	
	/*
	 * JSEDValue: JSEDValue
	 * JSEDString
	 * JSEDNumber
	 * JSEDLoc
	 * JSEDArray
	 * JSEDObjectValue
	 * JSEDBoolean
	 * JSEDList
	 * null
	 * undefined
	 */
	function JSEDValue(_value, _isInternal, _descFlag){
		if(_descFlag || _isInternal){
			switch(typeof _value){
			case 'string':
				this.value = new JSEDString(_value);
				break;
			case 'number':
				this.value = new JSEDNumber(_value);
				break;
			case 'boolean':
				this.value = new JSEDBoolean(_value);
				break;
			case 'undefined':
				this.value = new JSEDUndefined();
				break;
			case 'function':
				assignNewLoc(_value);
				
				if(isPrinted(_value) == false){
					push_stack(_value);
				}
				
				this.value = new JSEDLoc(getLoc(_value));
				break;
			case 'object':
				if(_value == null){
					this.value = new JSEDNull();
					break;
				}else if(_value instanceof array_obj){
					this.value = new JSEDArray(_value);
					break;
				}else if(isLexical(_value)){
					_value = _value.map;
				}
				assignNewLoc(_value);
				if(isPrinted(_value) == false){
					push_stack(_value);
				}
				this.value = new JSEDLoc(getLoc(_value));
				break;
            case 'symbol':
                this.value = new JSEDSymbol(_value);
                break;
			default:
				throw "uncaught value type: " + (typeof _value);
			}
		}else{
			this.value = new JSEDObjectValue(_value, true);
		}
		this.valueOf = function(){
			return this.value.valueOf() + "";
		};
		this.missingcheck = function(){
			if(this.value instanceof JSEDLoc)
				req(this.value.valueOf());
			else if(this.value instanceof JSEDObjectValue)
				this.value.missingcheck();
		};
	};
	
	
	function JSEDString(_value){
		this.value = _value;
		this.valueOf = function(){
			return "\"" + escape(this.value) + "\"";
		};
		this.classcheck = function(){
			if(this.value == "[[class" || this.value == "[[outer")
				return true;
			return false;
		};
	};
	
	
	function JSEDNumber(_value){
		this.value = _value;
		this.valueOf = function(){
            if(isNaN(this.value)){
                return '"@NaN"';
            }
            if(this.value === Number.POSITIVE_INFINITY){
                return '"@PosInf"';
            }
            if(this.value === Number.NEGATIVE_INFINITY){
                return '"@NegInf"';
            }
			return this.value + "";
		};
	};
	
	
	function JSEDArray(_value){
		var newlist = [];
		for(var i=0; i<_value.value.length; i++){
			var val = _value.value[i];
			newlist.push(new JSEDValue(val, false, true));
		}
		this.value = newlist;
		this.valueOf = function(){
			var str = "{";
		
			for(var i=0; i<this.value.length; i++){
				var val  = this.value[i];
				if(i != 0)
					str = str + ", ";
				str = str + val.valueOf();
			}
			str = str + "}";
			return str;
		};
	};
	
	
	function JSEDBoolean(_value){
		this.value = _value;
		this.valueOf = function(){
			return this.value;
		};
	};
	
	
	function JSEDSymbol(_value){
		this.value = _value.toString();
		this.valueOf = function(){
            return '""';
			//return "SYM(\"" + this.value + "\")";
		};
	};
	
	
	function JSEDUndefined(){
		this.value = "undefined";
		this.valueOf = function(){
			return '"@undef"';
			//return this.value;
		};
	};
	
	
	function JSEDNull(){
		this.value = "null";
		this.valueOf = function(){
			return this.value;
		};
	};
	
	
	function JSEDNative(){
		this.value = "< Native >";
		this.valueOf = function(){
			return this.value + "";
		};
	};
	
	__JSED.transform = function(_obj, _initFunc, _initLoc){
		__JSED.init();
		
		if(_initFunc)
			_initFunc();
		if(_initLoc && typeof _initLoc == 'number')
			loc = _initLoc;
				
		var o = new JSEDObject(_obj, "window");	
		console.log("#classcheck: "+o.classcheck());
		o.missingcheck();
		return o;
	};
	
	__JSED.stringfy = function(_obj){
		if(_obj instanceof JSEDObject)
			return _obj.valueOf();
		else
			return false;
	};
	
	__JSED.init = function(){
		//init_native_name();
		PREDLOC_DEBUG = true;
		init_pred_name();
		PREDLOC_DEBUG = false;
		initEvents();
		initLexicals();
	};
})();

function __dump(_obj){
	var jsedForm = __JSED.transform(_obj);
	return jsedForm.valueOf();
};
