TEXSRC=	chap-1-foreword.tex chap-2-intro.tex chap-3-refman.tex \
				chap-appendix.tex macros.tex manual.tex preamble.tex \
				safe-ast.tex safe-ast2ir.tex safe-cfg-abstract.tex \
				safe-cfg-collecting.tex safe-cfg.tex safe-ir.tex \
				safe-ir2cfg.tex safe-overview.tex safe.tex ref.bib \
				htmldebugger.png

manual.pdf: $(TEXSRC)
	pdflatex manual.tex  && \
	bibtex manual && \
	pdflatex manual.tex && \
	pdflatex manual.tex

.PHONY: clean

clean:
	rm -rf *~ *.aux *.log *.out *.bbl *.blg *.vtc manual.pdf
