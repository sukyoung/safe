We sincerely thank the reviewers for their careful reviews and detailed
suggestions for improvement. We will reflect the suggestions in our revision,
and we answer the outstanding comments and questions from the reviewers as
follows:

### Novelty (Review A, B)

The most important contribution of this paper is to present the __first
formalization of _flexible_ switching between abstract and concrete execution
for JavaScript in a sound way__.  We introduced a _dynamic shortcut_ as a
technique to perform concrete execution with abstract values during static
analysis to enhance analysis performance and precision.  To be aware of when the
actual value of each abstract value is required in the concrete execution, we
converted it to a _sealed value_, which is a symbol that signals the end of the
current dynamic shortcut when the concrete execution tries to access its value.
The most related previous work is Concerto
[[POPL'19](https://dl.acm.org/doi/abs/10.1145/3290356)], a Java static analyzer
with mostly-concrete execution described in the related work section.  Similar
to our dynamic shortcuts, Concerto combines static analysis with extended
concrete execution (mostly-concrete execution or symbolic execution) to improve
analysis precision and performance.  However, Concerto has a critical limitation
that they _syntactically_ divide programs into two different parts for static
analysis and concrete execution.


### Comparison with Concolic Execution (Review B)

We agree with Review B that _concolic execution_ also has similar concept with
dynamic shortcuts because it also leverages concrete execution not for abstract
interpretation but for _symbolic execution_.  As Review B already mentioned,
symbolic execution can be treated as an abstract interpretation with symbolic
expressions and path constraints.  However, we formalized a dynamic shortcut as
a technique to combine concrete execution with general abstract interpretation
not symbolic execution.  Thus, dynamic shortcuts are theoretically applicable
for any type of abstract interpretation including symbolic execution it is more
general definition of concolic execution.  Nevertheless, we agree with concolic
execution is also closely related with out technique.  We will supplement
citations for symbolic and concolic execution and compare them with dynamic
shortcuts.


### Necessity of Formalization (Review B)

The concept of dynamic shortcut is quite simple because it just utilizes
concrete execution with abstract values until their actual values are required.
However, we believe that the formalization of dynamic shortcut is necessary and
the brief formalization idea should be explained in the paper.  Although many
real-world static analyzers aim _soundy_ analysis by sacrificing soundness, it
is still important to prove the _soundness_ and _termination_ of static
analysis.  Thus, we formally defined dynamic shortcuts, found the conditions
when the dynamic shortcut would be sound and terminate, and formally proved
them.  We believe that this formalization could answer an important question:
"Are the soundness and termination of the static analysis with dynamic
shortcuts still guaranteed even though parts of abstract interpretation are
replaced with _concrete execution_?''


### Limitation of Dynamic Shortcuts (Review B)

We agree with Review B that the quality of dynamic shortcuts depends on the
shape of the abstract domain including its abstract values and join (widening)
operator.  Although dynamic shortcuts are theoretically applicable to any
abstract domain, its quality is not always guaranteed.  If an abstract domain
gives less opportunities to utilize concrete execution, the effect of dynamic
shortcuts decrease.  On the other hand, aggressively many dynamic shortcuts
might suffer from massive communication costs.  Although we did not answer about
a more detailed relationship between dynamic shortcuts and the shape of abstract
domain in this paper, we experimentally showed that dynamic shortcuts can
enhance the performance and precision of JavaScript static analysis.  We believe
that to find more detailed relationship between them is one of good future
research direction.


### Extensibility for Other Languages (Review C)

We believe that dynamic shortcuts are applicable for static analysis of other
languages.  Although the abstract domain and sealed execution in other languages
are totally different with those of JavaScript, the general formalization of
dynamic shortcut is not dependent on JavaScript language characteristics.  Thus,
it is possible to define dynamic shortcuts on any other languages and they can
enhance the analysis precision.  However, the precision improvement might not be
remarkable or the analysis might suffer from massive communication costs.  We
believe that to apply dynamic shortcuts for other languages is a convincing
future research direction for dynamic shortcuts.
