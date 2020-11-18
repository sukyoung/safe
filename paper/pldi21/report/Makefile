TEXSRC=	report.tex macros.tex proof.tex

report.pdf: $(TEXSRC)
	pdflatex report.tex

.PHONY: clean

clean:
	rm -rf *~ *.aux *.log *.out *.bbl *.blg *.bak *.vtc report.pdf
