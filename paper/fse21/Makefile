#
# Makefile for acmart package
#
# This file is in public domain
#
# $Id: Makefile,v 1.10 2016/04/14 21:55:57 boris Exp $
#

PACKAGE=main


PDF = $(PACKAGE).pdf

all: pdf docclean

pdf:
	pdflatex $(PACKAGE)
	- bibtex $(PACKAGE)
	pdflatex $(PACKAGE)
	pdflatex $(PACKAGE)
	while ( grep -q '^LaTeX Warning: Label(s) may have changed' $(PACKAGE).log) \
	do pdflatex $(PACKAGE); done

docclean:
	$(RM)  *.log *.aux \
	*.cfg *.glo *.idx *.toc \
	*.ilg *.ind *.out *.lof \
	*.lot *.bbl *.blg *.gls *.cut *.hd \
	*.dvi *.ps *.thm *.tgz *.zip *.rpi

clean: docclean

distclean: clean
	$(RM)  *.pdf

#
# Archive for the distribution. Includes typeset documentation
#
archive:  all clean
	COPYFILE_DISABLE=1 tar -C .. -czvf ../$(PACKAGE).tgz --exclude '*~' --exclude '*.tgz' --exclude '*.zip'  --exclude CVS --exclude '.git*' $(PACKAGE); mv ../$(PACKAGE).tgz .

zip:  all clean
	zip -r  $(PACKAGE).zip * -x '*~' -x '*.tgz' -x '*.zip' -x CVS -x 'CVS/*'

documents.zip: all docclean
	zip -r $@ main.pdf ACM-Reference-Format.*

.PHONY: all docclean clean distclean archive zip
.SILENT: pdf
