#
# Makefile for acmart package
#
# This file is in public domain
#
# $Id: Makefile,v 1.10 2016/04/14 21:55:57 boris Exp $
#

PACKAGE=report

all: pdf docclean

pdf:
	pdflatex $(PACKAGE)
	- bibtex $(PACKAGE)
	pdflatex $(PACKAGE)
	pdflatex $(PACKAGE)
	while ( grep -q '^LaTeX Warning: Label(s) may have changed' $(PACKAGE).log) \
	do pdflatex $(PACKAGE); done

%.cls:   %.ins %.dtx
	pdflatex $<

.PRECIOUS:  $(PACKAGE).cfg $(PACKAGE).cls

docclean:
	$(RM)  *.log *.aux \
	*.cfg *.glo *.idx *.toc \
	*.ilg *.ind *.out *.lof \
	*.lot *.bbl *.blg *.gls *.cut *.hd \
	*.dvi *.ps *.thm *.tgz *.zip *.rpi

clean: docclean
	$(RM) $(PACKAGE).pdf

#
# Archive for the distribution. Includes typeset documentation
#
archive:  clean
	COPYFILE_DISABLE=1 tar -C .. -czvf ../$(PACKAGE).tgz --exclude '*~' --exclude '*.tgz' --exclude '*.zip'  --exclude CVS --exclude '.git*' $(PACKAGE); mv ../$(PACKAGE).tgz .

zip:  clean
	zip -r  $(PACKAGE).zip * -x '*~' -x '*.tgz' -x '*.zip' -x CVS -x 'CVS/*'

.PHONY: all pdf clean archive zip
.SILENT: pdf
