TEXSRC=	main.tex	ref.bib

main.pdf: $(TEXSRC)
	pdflatex main.tex  && \
	bibtex main && \
	pdflatex main.tex && \
	pdflatex main.tex

.PHONY: clean

clean:
	rm -rf *~ *.aux *.log *.out *.bbl *.blg *.vtc main.pdf 
