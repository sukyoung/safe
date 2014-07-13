#!/usr/bin/perl -w
use strict;
################################################################################
#   Copyright (c) 2012, KAIST.
#   All rights reserved.
#
#   Use is subject to license terms.
#
#   This distribution may include materials developed by third parties.
################################################################################

# http://www.unicode.org/Public/UNIDATA/UnicodeData.txt

my $file = '../../../../../../third_party/unicode/UnicodeData.500.txt';

open IN, "<$file";
open RATS, '>>../parser/Unicode.rats';

my $line;
my @unicodeletter1 = ();
my @unicodeletter2 = ();
my @unicodecombiningmark1 = ();
my @unicodecombiningmark2 = ();
my @unicodedigit1 = ();
my @unicodedigit2 = ();
my @unicodecp1 = ();
my @unicodecp2 = ();

while ( $line = <IN> ) {
  $line =~ /^([0-9A-F]+);([^;]+);([^;]+);/;
  my ($code,$name,$category) = ($1,$2,$3);
  # UnicodeLetter ::=
  #     any character in the Unicode categories "Uppercase Letter (Lu)",
  #     "Lowercase letter (Ll)", "Titlecase letter (Lt)", "Modifier letter
  #     (Lm))", "Other letter (Lo)", or "Letter number (Nl)".
  if ( $category =~ /Lu|Ll|Lt|Lm|Lo|Nl/ ) {
    my $hex = hex $code;
    if ( $name =~ /, First/ ) { # begin (range of characters)
      $line = <IN>;
      $line =~ /^([0-9A-F]+);([^;]+);([^;]+);/;
      my $last = hex $1;
      for (my($i) = $hex; $i <= $last; $i++) {
        if ( $i > 0xFFFF ) {
          push @unicodeletter2, $i;
        } else {
          push @unicodeletter1, $i;
        }
      }
    } # end (range of characters)
    elsif ( $hex > 0xFFFF ) {
      push @unicodeletter2, $hex;
    } else {
      push @unicodeletter1, $hex;
    }
  # UnicodeCombiningMark ::=
  #     any character in the Unicode categories "Non-spacing mark (Mn)" or
  #     "Combining spacing mark (Mc)"
  } elsif ( $category =~ /Mn|Mc/ ) {
    my $hex = hex $code;
    if ( $hex > 0xFFFF ) {
      push @unicodecombiningmark2, $hex;
    } else {
      push @unicodecombiningmark1, $hex;
    }
  # UnicodeDigit ::=
  #     any character in the Unicode categories "Decimal number (Nd)"
  } elsif ( $category =~ /Nd/ ) {
    my $hex = hex $code;
    if ( $hex > 0xFFFF ) {
      push @unicodedigit2, $hex;
    } else {
      push @unicodedigit1, $hex;
    }
  # UnicodeConnectorPunctuation ::=
  #     any character in the Unicode categories "Connector punctuation (Pc)"
  } elsif ( $category =~ /Pc/ ) {
    my $hex = hex $code;
    if ( $hex > 0xFFFF ) {
      push @unicodecp2, $hex;
    } else {
      push @unicodecp1, $hex;
    }
  }
}

sub ranges1 {

  my @codes = sort { $a <=> $b } @_;
  my @output = ();

  while ( @codes ) {
    my $first = shift @codes;
    my $last = $first;
    while ( @codes && $codes[0] == $last + 1 ) {
      $last = shift @codes;
    }
    if ( $first == $last ) {
      push @output, sprintf("\\u%04x", $first);
    } else {
      push @output, sprintf("\\u%04x-\\u%04x", $first, $last);
    }
  }

  return @output;
}

sub front {
  return 0xD7C0 + ($_[0] >> 10);
}

sub back {
  return 0xDC00 | $_[0] & 0x3FF;
}

sub ranges2 {
  my @codes = sort { $a <=> $b } @_;
  my $header = 0;

  while ( @codes ) {
    my $first = shift @codes;
    my $last = $first;
    while ( @codes && $codes[0] == $last + 1 &&
            front($codes[0]) == front($last + 1) ) {
      $last = shift @codes;
    }
    my $firstfront = front($first);
    my $firstback  = back($first);
    my $lastfront  = front($last);
    my $lastback   = back($last);
    if ( $first == $last ) {
      if ( $firstfront == $header ) {
        printf RATS ("\\u%04x", $firstback);
      } else {
        printf RATS ("]\n  / '\\u%04x'[\\u%04x", $firstfront, $firstback);
        $header = $firstfront;
      }
    } else {
      if ( $firstfront == $header ) {
        printf RATS ("\\u%04x-\\u%04x", $firstback, $lastback);
      } else { # $firstfront != $header
        printf RATS ("]\n  / '\\u%04x'[\\u%04x-\\u%04x",
               $firstfront, $firstback, $lastback);
        $header = $firstfront;
      }
    }
  }
  print RATS "];\n";
}

print RATS "/*******************************************************************************\n";
print RATS "    Copyright (c) 2012, KAIST.\n";
print RATS "    All rights reserved.\n\n";
print RATS "    Use is subject to license terms.\n\n";
print RATS "    This distribution may include materials developed by third parties.\n";
print RATS " ******************************************************************************/\n";
print RATS "/*\n";
print RATS " * Definition of JavaScript Unicode characters.\n";
print RATS " *\n * Automatically generated file: Please don't manually edit.\n";
print RATS " */\n";
print RATS "module kr.ac.kaist.jsaf.parser.Unicode;\n";
print RATS "\n";
print RATS "transient String UnicodeLetter = [" . (join '', ranges1(@unicodeletter1));
ranges2(@unicodeletter2);
print RATS "\n";
print RATS "transient String UnicodeCombiningMark = [" . (join '', ranges1(@unicodecombiningmark1));
ranges2(@unicodecombiningmark2);
print RATS "\n";
print RATS "transient String UnicodeDigit = [" . (join '', ranges1(@unicodedigit1));
ranges2(@unicodedigit2);
print RATS "\n";
print RATS "transient String UnicodeConnectorPunctuation = [" . (join '', ranges1(@unicodecp1));
ranges2(@unicodecp2);

close RATS;
