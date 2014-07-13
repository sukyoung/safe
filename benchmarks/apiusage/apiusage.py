#!/usr/bin/python

# Find API usage in benchmark programs

import sys
import os

flist = [
"../sunspider_v0.9.1/3d-cube.js",
"../sunspider_v0.9.1/3d-morph.js",
"../sunspider_v0.9.1/3d-raytrace.js",
"../sunspider_v0.9.1/access-binary-trees.js",
"../sunspider_v0.9.1/access-fannkuch.js",
"../sunspider_v0.9.1/access-nbody.js",
"../sunspider_v0.9.1/access-nsieve.js",
"../sunspider_v0.9.1/bitops-3bit-bits-in-byte.js",
"../sunspider_v0.9.1/bitops-bits-in-byte.js",
"../sunspider_v0.9.1/bitops-bitwise-and.js",
"../sunspider_v0.9.1/bitops-nsieve-bits.js",
"../sunspider_v0.9.1/controlflow-recursive.js",
"../sunspider_v0.9.1/crypto-aes.js",
"../sunspider_v0.9.1/crypto-md5.js",
"../sunspider_v0.9.1/crypto-sha1.js",
"../sunspider_v0.9.1/date-format-tofte.js",
"../sunspider_v0.9.1/date-format-xparb.js",
"../sunspider_v0.9.1/math-cordic.js",
"../sunspider_v0.9.1/math-partial-sums.js",
"../sunspider_v0.9.1/math-spectral-norm.js",
"../sunspider_v0.9.1/regexp-dna.js",
"../sunspider_v0.9.1/string-base64.js",
"../sunspider_v0.9.1/string-fasta.js",
"../sunspider_v0.9.1/string-tagcloud.js",
"../sunspider_v0.9.1/string-unpack-code.js",
"../sunspider_v0.9.1/string-validate-input.js",
"../kraken_v1.1/ai-astar.js",
"../kraken_v1.1/audio-beat-detection.js",
"../kraken_v1.1/audio-dft.js",
"../kraken_v1.1/audio-fft.js",
"../kraken_v1.1/audio-oscillator.js",
"../kraken_v1.1/imaging-darkroom.js",
"../kraken_v1.1/imaging-desaturate.js",
"../kraken_v1.1/imaging-gaussian-blur.js",
"../kraken_v1.1/json-parse-financial.js",
"../kraken_v1.1/json-stringify-tinderbox.js",
"../kraken_v1.1/stanford-crypto-aes.js",
"../kraken_v1.1/stanford-crypto-ccm.js",
"../kraken_v1.1/stanford-crypto-pbkdf2.js",
"../kraken_v1.1/stanford-crypto-sha256-iterative.js",
"../v8_v7/crypto.js",
"../v8_v7/deltablue.js",
"../v8_v7/earley-boyer.js",
"../v8_v7/navier-stokes.js",
"../v8_v7/raytrace.js",
"../v8_v7/regexp.js",
"../v8_v7/richards.js",
"../v8_v7/splay.js",
]

if len(sys.argv) > 1:
		flist = sys.argv[1:]

v8 = "/home/cho/git/v8/out/ia32.release/shell"
sm = "/home/cho/git/mozilla-central-02b26fb307b4/js/src/build-release/js"
rhino = "rhino"

# generate pre.js
os.system("python ./genpre.py > pre.js");

# run benchmarks
for f in flist:
	os.system("echo '>>>' %s" % f)
	#os.system(v8 + " ./pre.js %s" % f)
	#os.system(sm + " -f ./pre.js -f %s" % f)
	os.system(rhino + " -f ./pre.js -f %s" % f)
	#os.system(rhino + " -opt -1 -f ./pre.js -f %s" % f)


